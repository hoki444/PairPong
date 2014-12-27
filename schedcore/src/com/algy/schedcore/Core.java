package com.algy.schedcore;

import java.util.HashMap;
import java.util.HashSet;

import com.algy.schedcore.Scheduler.Task;
import com.algy.schedcore.util.ArrayLister;


public class Core implements ICore {
    protected Item<BaseCompServer, ICore> serverItem;
    protected Scheduler scheduler;
    protected TickGetter tickGetter;
    
    private HashSet<Class<? extends BaseCompServer>> addedServerSig;
    private HashMap<Class<? extends BaseComp>, HashSet<Class<? extends BaseCompServer>>> hookMap;


    public Core (Scheduler scheduler) { 
        this.serverItem = Item.MakeServerItem();
        this.serverItem.adhereTo(this);

        this.scheduler = scheduler;
        
        this.addedServerSig = new HashSet<Class<? extends BaseCompServer>>();
        this.hookMap = new HashMap<Class<? extends BaseComp>, 
                                   HashSet<Class<? extends BaseCompServer>>>();
    }

    public void addItemAll(Iterable<Item<BaseComp, ICore>> itemIterable) {
        for (Item<BaseComp, ICore> item : itemIterable) {
            _addItem(item, false);
        }
        for (Item<BaseComp, ICore> item : itemIterable) {
            for (BaseComp comp : item)
                comp.onItemAdded();
        }
    }

    public void addItem (Item<BaseComp, ICore> item, boolean suspend) {
        _addItem(item, suspend);
        for (BaseComp comp : item)
            comp.onItemAdded();
    }
    
    public void addItem (Item<BaseComp, ICore> item) {
        _addItem(item, false);
        for (BaseComp comp : item)
            comp.onItemAdded();
    }
    
    private void _addItem (Item<BaseComp, ICore> item, boolean suspend ) {
        item.adhereTo(this);
        for (BaseComp comp : item) {
            hookComp(comp);
            if (comp instanceof ISchedComp) {
                regSchedComp((ISchedComp)comp, suspend);
            }
        }
    }

    @Override
    public void removeItem(Item<BaseComp, ICore> item) {
        item.adhereTo(null);
        for (BaseComp comp : item) {
            unhookComp(comp);
            if (comp instanceof ISchedComp) {
                unregSchedComp((ISchedComp)comp);
            }
        }        
        for (BaseComp comp : item) {
            comp.onItemRemoved();
        }

    }
    
    
    public void suspendItem (Item<BaseComp, ICore> item) {
        for (BaseComp comp : item) {
            if (comp instanceof BaseSchedComp) {
                ((BaseSchedComp)comp).getTask().suspend();
            }
        }
    }
    
    public void resumeItem (Item<BaseComp, ICore> item) {
        for (BaseComp comp : item) {
            if (comp instanceof BaseSchedComp) {
                ((BaseSchedComp)comp).getTask().resume();
            }
        }
    }
    
    public boolean resumeComp (BaseSchedComp comp) {
        return comp.getTask().resume();
    }
    
    
    public void addComp(Item<BaseComp, ICore> owner, BaseComp comp, boolean suspend) {
        owner.add(comp);
        hookComp(comp);
        if (comp instanceof ISchedComp) {
            regSchedComp((ISchedComp)comp, suspend);
        }
    }

    public void addComp(Item<BaseComp, ICore> owner, BaseComp comp) {
        addComp (owner, comp, false);
    }

    public void removeComp(BaseComp comp) {
        Item<BaseComp, ICore> owner = comp.owner();
        unhookComp(comp);
        if (owner.remove(comp.getClass()) != null) {
            if (comp instanceof ISchedComp) {
                unregSchedComp((ISchedComp)comp);
            }
        }
    }

    public Item<BaseCompServer, ICore> getServerItem() {
        return this.serverItem;
    }
    
    public Iterable<BaseCompServer> servers () {
        return serverItem;
    }

    public <T extends BaseCompServer> T server(Class<T> cls) {
        return serverItem.as(cls);
    }

    public Iterable<BaseCompServer> removeServer(Class<? extends BaseCompServer> serverClass) {
        Iterable<BaseCompServer> result = serverItem.remove(serverClass);
        for (BaseCompServer server : result) {
            if (server instanceof BaseSchedServer) {
                BaseSchedServer schedServer = (BaseSchedServer)server;
                unregSchedComp(schedServer);
            }
        }
        return result;
    }

    public Scheduler scheduler() {
        return scheduler;
    }

    public <T extends BaseCompServer> boolean addServer(T server, boolean suspend) {
        if(serverItem.add(server)) {
            Class<? extends BaseCompServer> cls = (Class<? extends BaseCompServer>)server.getClass();
            if (!addedServerSig.contains(cls)) {
                addedServerSig.add(cls);
                ArrayLister<Class<? extends BaseComp>> hookedCompClasses = new ArrayLister<Class<? extends BaseComp>>();
                server.listCompSignatures(hookedCompClasses);
                for (Class<? extends BaseComp> hookCompClass : hookedCompClasses) {
                    HashSet<Class<? extends BaseCompServer>> hookSet;
                    if ((hookSet = hookMap.get(hookCompClass)) == null)  {
                        hookSet = new HashSet<Class<? extends BaseCompServer>>();
                        hookMap.put(hookCompClass, hookSet);
                    }
                    hookSet.add(cls);
                }
            } 
            if (server instanceof BaseSchedServer) {
                BaseSchedServer schedServer = (BaseSchedServer)server;
                regSchedComp(schedServer, suspend);
            }
            return true;
        } else
            return false;
    }

    public <T extends BaseCompServer> boolean addServer(T server) {
        return addServer (server, false);
    }
    
    // Internal Aspect
    private void regSchedComp(ISchedComp schedComp, boolean suspend) {
        long period = schedComp.schedPeriod();
        long offset = schedComp.schedOffset();
        Task task = this.scheduler.addPeriodic(schedComp, period, offset, null);

        if (suspend)
            task.suspend();
        
        schedComp.setTask(task);
    }

    private void unregSchedComp(ISchedComp schedComp) {
        Task task = schedComp.getTask();
        if (task.active())
            task.kill();
        schedComp.setTask(null);
    }

    @SuppressWarnings("unchecked")
    private void hookWork (BaseComp comp, boolean isAdd) {
        Class<? extends BaseComp> compClass = (Class<? extends BaseComp>)comp.getClass();
        while (compClass != null) {
            HashSet<Class<? extends BaseCompServer>> hookSet;
            if ((hookSet = hookMap.get(compClass)) != null)  {
                for (Class<? extends BaseCompServer> serverSig : hookSet) {
                    if (serverItem.has(serverSig)) {
                        if (isAdd)
                            serverItem.as(serverSig).hookAddComp(comp);
                        else
                            serverItem.as(serverSig).hookRemoveComp(comp);
                    }
                }
            }
            if (compClass.equals(BaseComp.class))
                break;
            compClass = (Class<? extends BaseComp>) compClass.getSuperclass();
        }
    }

    private void hookComp (BaseComp comp) {
        hookWork(comp, true);
    }

    private void unhookComp (BaseComp comp) {
        hookWork(comp, false);
    }
}