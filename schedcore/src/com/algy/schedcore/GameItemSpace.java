package com.algy.schedcore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.algy.schedcore.event.GameEventMgr;
import com.algy.schedcore.util.ArrayLister;
import com.algy.schedcore.util.LinkedListCell;
import com.algy.schedcore.util.LinkedListCellHelper;


public class GameItemSpace implements Iterable<GameItem> {
    private CompMgrSpace compMgrSpace;
    private Scheduler scheduler;
    private GameEventMgr eventMgr;
    
    private HashSet<Class<? extends BaseCompMgr>> addedServerSig;
    private HashMap<Class<? extends BaseComp>, HashSet<Class<? extends BaseCompMgr>>> hookMap;

    private HashMap<Class<? extends BaseItemType>, LinkedListCell> itemTypeMap = 
        new HashMap<Class<? extends BaseItemType>, LinkedListCell>();


    public GameItemSpace (Scheduler scheduler) { 
        this.compMgrSpace = new CompMgrSpace(this);
        this.compMgrSpace.attachTo(this);
        

        this.scheduler = scheduler;
        
        this.addedServerSig = new HashSet<Class<? extends BaseCompMgr>>();
        this.hookMap = new HashMap<Class<? extends BaseComp>, 
                                   HashSet<Class<? extends BaseCompMgr>>>();
    }
    
    public void addItems (Iterable<GameItem> items) {
        for (GameItem item : items) {
            addItem(item);
        }
        
    }

    public void addItem (GameItem item, boolean suspend) {
        _addItem(item, suspend);
        for (BaseComp comp : item)
            comp.onItemAdded();
    }
    
    public void addItem (GameItem item) {
        _addItem(item, false);
        for (BaseComp comp : item)
            comp.onItemAdded();
    }
    
    private void _addItem (GameItem item, boolean suspend) {
        hookItem(item);
        item.attachTo(this);
        for (BaseComp comp : item) {
            hookComp(comp);
            if (comp instanceof StaticSchedMixin) {
                regSchedComp((StaticSchedMixin)comp, suspend);
            }
        }
    }

    public void removeItem(GameItem gameItem) {
        unhookItem(gameItem);

        gameItem.attachTo(null);
        for (BaseComp comp : gameItem) {
            unhookComp(comp);
            if (comp instanceof StaticSchedMixin) {
                unregSchedComp((StaticSchedMixin)comp);
            }
        }        
        for (BaseComp comp : gameItem) {
            comp.onItemRemoved();
        }

        gameItem.removeAll();
    }
    
    
    public void suspendItem (GameItem item) {
        for (BaseComp comp : item) {
            if (comp instanceof BaseSchedComp) {
                ((BaseSchedComp)comp).getTask().suspend();
            }
        }
    }
    
    public void resumeItem (GameItem item) {
        for (BaseComp comp : item) {
            if (comp instanceof BaseSchedComp) {
                ((BaseSchedComp)comp).getTask().resume();
            }
        }
    }
    
    public boolean resumeComp (BaseSchedComp comp) {
        return comp.getTask().resume();
    }
    
    
    public void addComp(GameItem owner, BaseComp comp, boolean suspend) {
        owner.add(comp);
        hookComp(comp);
        if (comp instanceof StaticSchedMixin) {
            regSchedComp((StaticSchedMixin)comp, suspend);
        }
    }

    public void addComp(GameItem owner, BaseComp comp) {
        addComp (owner, comp, false);
    }

    public void removeComp(BaseComp comp) {
        GameItem owner = comp.owner();
        unhookComp(comp);
        if (owner.remove(comp.getClass()) != null) {
            if (comp instanceof StaticSchedMixin) {
                unregSchedComp((StaticSchedMixin)comp);
            }
        }
    }

    public CompMgrSpace  getCompMgrSpace() {
        return compMgrSpace;
    }
    
    public GameEventMgr getGameEventMgr() {
        return eventMgr;
    }

    public <T extends BaseCompMgr> T compMgr(Class<T> cls) {
        return compMgrSpace.as(cls);
    }

    public Iterable<BaseCompMgr> removeCompMgr(Class<? extends BaseCompMgr> compMgrClass) {
        Iterable<BaseCompMgr> result = compMgrSpace.remove(compMgrClass);
        for (BaseCompMgr compMgr : result) {
            if (compMgr instanceof BaseSchedMgr) {
                BaseSchedMgr schedServer = (BaseSchedMgr)compMgr;
                unregSchedComp(schedServer);
            }
        }
        return result;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public <T extends BaseCompMgr> boolean addCompMgr(T compMgr, boolean suspend) {
        if(compMgrSpace.add(compMgr)) {
            Class<? extends BaseCompMgr> cls = (Class<? extends BaseCompMgr>)compMgr.getClass();
            if (!addedServerSig.contains(cls)) {
                addedServerSig.add(cls);
                ArrayLister<Class<? extends BaseComp>> hookedCompClasses = new ArrayLister<Class<? extends BaseComp>>();
                compMgr.listCompSignatures(hookedCompClasses);
                for (Class<? extends BaseComp> hookCompClass : hookedCompClasses) {
                    HashSet<Class<? extends BaseCompMgr>> hookSet;
                    if ((hookSet = hookMap.get(hookCompClass)) == null)  {
                        hookSet = new HashSet<Class<? extends BaseCompMgr>>();
                        hookMap.put(hookCompClass, hookSet);
                    }
                    hookSet.add(cls);
                }
            } 
            if (compMgr instanceof BaseSchedMgr) {
                BaseSchedMgr schedServer = (BaseSchedMgr)compMgr;
                regSchedComp(schedServer, suspend);
            }
            return true;
        } else
            return false;
    }

    public <T extends BaseCompMgr> boolean addCompMgr(T compMgr) {
        return addCompMgr(compMgr, false);
    }
    
    public <T extends BaseCompMgr> T getCompMgr(Class<T> clazz) {
        return compMgrSpace.as(clazz);
    }

    public void clearAll() {
        for (Iterator<GameItem> p = iterator(); p.hasNext(); ) {
            p.remove();
        }
        removeCompMgr(BaseCompMgr.class);
    }
    
    public GameItem firstItemWithType (Class<? extends BaseItemType> itemTypeClass) {
        LinkedListCell header = itemTypeMap.get(itemTypeClass);
        if (header == null)
            return null;
        return (GameItem)header.getNext();
    }

    public Iterable<GameItem> getItemWithType (final Class<? extends BaseItemType> itemTypeClass) {
        return new Iterable<GameItem>() {
            @Override
            public Iterator<GameItem> iterator() {
                return new Iterator<GameItem>() {
                    private GameItem pointer = null;
                    {
                        LinkedListCell header = itemTypeMap.get(itemTypeClass);
                        if (header != null)
                            pointer = (GameItem)header.getNext();
                    }

                    @Override
                    public boolean hasNext() {
                        return pointer != null;
                    }

                    @Override
                    public GameItem next() {
                        GameItem result = pointer;
                        pointer = (GameItem)pointer.getNext();
                        return result;
                    }

                    @Override
                    public void remove() {
                        GameItem nextPointer = (GameItem)pointer.getNext();
                        removeItem(pointer);
                        pointer = nextPointer;
                    }
                };
            }
        };
    }
    
    @Override
    public Iterator<GameItem> iterator() {
        return new Iterator<GameItem>() {
            private GameItem pointer = null;
            private Iterator<LinkedListCell> headerIter = itemTypeMap.values().iterator();
            
            {
                prepare();
            }

            @Override
            public boolean hasNext() {
                return pointer != null;
            }
            
            private void prepare () {
                while (headerIter.hasNext() && pointer == null) {
                    pointer = (GameItem)(headerIter.next().getNext());
                }
            }

            @Override
            public GameItem next() {
                GameItem result = pointer;
                pointer = (GameItem)pointer.getNext();
                prepare();
                return result;
            }

            @Override
            public void remove() {
                GameItem nextPointer = (GameItem)pointer.getNext();
                removeItem(pointer);
                pointer = nextPointer;
                prepare();
            }
        };
    }
    
    // Internal Aspect
    private void regSchedComp(StaticSchedMixin schedComp, boolean suspend) {
        long period = schedComp.schedPeriod();
        long delay = schedComp.schedDelay();

        TaskController task;
        if (schedComp.isPeriodic()) {
            task = scheduler.schedule(delay, period, schedComp);
        } else {
            task = scheduler.scheduleOnce(delay, period, schedComp);
        }
        
        task = scheduler.schedule(delay, period, schedComp);

        if (suspend)
            task.suspend();
        
        schedComp.setTask(task);
    }

    private void unregSchedComp(StaticSchedMixin schedComp) {
        TaskController task = schedComp.getTask();
        if (task.active())
            task.kill();
        schedComp.setTask(null);
    }

    @SuppressWarnings("unchecked")
    private void hookWork (BaseComp comp, boolean isAdd) {
        Class<? extends BaseComp> compClass = (Class<? extends BaseComp>)comp.getClass();
        while (compClass != null) {
            HashSet<Class<? extends BaseCompMgr>> hookSet;
            if ((hookSet = hookMap.get(compClass)) != null)  {
                for (Class<? extends BaseCompMgr> compMgrSig : hookSet) {
                    if (compMgrSpace.has(compMgrSig)) {
                        if (isAdd)
                            compMgrSpace.as(compMgrSig).hookAddComp(comp);
                        else
                            compMgrSpace.as(compMgrSig).hookRemoveComp(comp);
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

    void updateTypeMap (GameItem gameItem, BaseItemType oldType) {
        if (oldType != null) {
            unhookItem(gameItem);
        }
        BaseItemType curType = gameItem.getItemType();
        LinkedListCell header = itemTypeMap.get(curType.getClass());
        if (header == null) {
            System.out.println(curType);
            header = LinkedListCellHelper.simpleCell();
            itemTypeMap.put(curType.getClass(), header);
        }
        LinkedListCellHelper.pushNext(header, gameItem);
    }

    private void hookItem (GameItem gameItem) {
        updateTypeMap(gameItem, null);
    }

    private void unhookItem (GameItem gameItem) {
        LinkedListCellHelper.popDest(gameItem);
    }
}