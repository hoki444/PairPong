package com.algy.schedcore;

import java.util.HashMap;
import java.util.HashSet;

import com.algy.schedcore.util.MutableLister;


public class Core implements ICore {
    protected Item<BaseCompServer, ICore> serverItem;
    protected Scheduler scheduler;
    protected ITickGetter tickGetter;
    
    private HashSet<Class<? extends BaseCompServer>> addedServerSig;
    private HashMap<Class<? extends BaseComp>, HashSet<Class<? extends BaseCompServer>>> hookMap;

    public Core (ITickGetter tickGetter) {
        this.serverItem = Item.MakeServerItem();
        this.serverItem.adhereTo(this);

        this.scheduler = new Scheduler();
        this.tickGetter = tickGetter;
        
        this.addedServerSig = new HashSet<Class<? extends BaseCompServer>>();
        this.hookMap = new HashMap<Class<? extends BaseComp>, 
                                   HashSet<Class<? extends BaseCompServer>>>();
    }
    
    public void addItem (Item<BaseComp, ICore> item) {
        item.adhereTo(this);
        for (BaseComp comp : item) {
            hookComp(comp);
            if (comp instanceof ISchedComp) {
                regSchedComp((ISchedComp)comp);
            }
        }
    }

    public void removeItem(Item<BaseComp, ICore> item) {
        item.adhereTo(null);
        for (BaseComp comp : item) {
            unhookComp(comp);
            if (comp instanceof ISchedComp) {
                unregSchedComp((ISchedComp)comp);
            }
        }
    }

    public void addComp(Item<BaseComp, ICore> owner, BaseComp comp) {
        owner.add(comp);
        hookComp(comp);
        if (comp instanceof ISchedComp) {
            regSchedComp((ISchedComp)comp);
        }
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

    public Scheduler sched() {
        return scheduler;
    }

    public <T extends BaseCompServer> boolean addServer(T server) {
        if(serverItem.add(server)) {
            Class<? extends BaseCompServer> cls = (Class<? extends BaseCompServer>)server.getClass();
            if (!addedServerSig.contains(cls)) {
                addedServerSig.add(cls);
                MutableLister<Class<? extends BaseComp>> hookedCompClasses = new MutableLister<Class<? extends BaseComp>>();
                server.hookFilters(hookedCompClasses);
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
                regSchedComp(schedServer);
            }
            return true;
        } else
            return false;
    }
    
    // Internal Aspect
    private void regSchedComp(ISchedComp schedComp) {
        long current = tickGetter.getTickCount();
        long period = schedComp.schedPeriod();
        long offset = schedComp.schedOffset();
        int taskId = this.scheduler.addPeriodic(current, schedComp, period, offset, null);
        
        schedComp.setTaskId(taskId);
    }

    private void unregSchedComp(ISchedComp schedComp) {
        int taskId = schedComp.taskId();
        if (this.scheduler.has(taskId)) {
            this.scheduler.kill(taskId);
        } else 
            throw new KeyError("taskId(" + taskId + ")");
        schedComp.setTaskId(-1);
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