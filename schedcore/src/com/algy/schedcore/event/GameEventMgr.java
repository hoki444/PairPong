package com.algy.schedcore.event;

import java.util.ArrayList;
import java.util.HashMap;

import com.algy.schedcore.BaseItemType;
import com.algy.schedcore.SchedTask;
import com.algy.schedcore.SchedTime;
import com.algy.schedcore.Scheduler;
import com.algy.schedcore.TaskController;


@SuppressWarnings ("rawtypes")
class TypeRecord <T> {
    private HashMap<Class, T> recordMap = new HashMap<Class, T>();
    private Class rootClass;
    
    public TypeRecord (Class rootClass) {
        this.rootClass = rootClass;
    }

    public T put (Class<?> key, T value) {
        return recordMap.put(key, value);
    }

    public T get (Class<?> clazz) {
        for (Class iter = clazz; iter != null && !iter.equals(rootClass); iter = iter.getSuperclass()) {
            T val = recordMap.get(iter);
            if (val != null) return val;
            
        }
        return null;
    }

    public T remove (Class<?> clazz) {
        return recordMap.remove(clazz);
    }
}

@SuppressWarnings ("rawtypes")
class DoubleTypeRecord <T> {
    private HashMap<Class, HashMap<Class, T>> recordMap = new HashMap<Class, HashMap<Class, T>>();
    private Class rootClass;
    
    public DoubleTypeRecord (Class rootClass) {
        this.rootClass = rootClass;
    }

    public T put (Class<?> lhsKey, Class<?> rhsKey, T value) {
        HashMap<Class, T> submap;
        if ((submap = recordMap.get(lhsKey)) == null) {
            submap = new HashMap<Class, T>();
            recordMap.put(lhsKey, submap);
        }
        T result = submap.put(rhsKey, value);
        return result;
    }

    public T get (Class<?> lhsKey, Class<?> rhsKey) {
        T result;
        
        for (Class lhsIter = lhsKey; lhsIter != null && !lhsIter.equals(rootClass); lhsIter = lhsIter.getSuperclass()) {
            HashMap<Class, T> submap = recordMap.get(lhsIter);
            if (submap == null) 
                continue;
            for (Class rhsIter = rhsKey; rhsIter != null && !rhsIter.equals(rootClass); rhsIter = rhsIter.getSuperclass()) {
                if ((result = submap.get(rhsIter)) != null)
                    return result;
            }
        }
        return null;
    }

    public T remove (Class<?> lhsKey, Class<?> rhsKey) {
        HashMap<Class, T> submap;
        if ((submap = recordMap.get(lhsKey)) != null) {
            return submap.remove(rhsKey);
        }
        return null;
    }
}

@SuppressWarnings ("rawtypes")
public class GameEventMgr {
    private Scheduler scheduler;
    private HashMap<Class<? extends GameBroadcast>, ArrayList<GameBroadcastReceiver>> broadcastReceivers = 
            new HashMap<Class<? extends GameBroadcast>, ArrayList<GameBroadcastReceiver>>();
    private HashMap<Class<? extends GameItemEvent>, TypeRecord<GameItemEventReceiver>> itemEventReceivers = 
            new HashMap<Class<? extends GameItemEvent>, TypeRecord<GameItemEventReceiver>>();
    private HashMap<Class<? extends GameInteraction>, DoubleTypeRecord<GameInteractionReceiver>> interactionReceivers =
            new HashMap<Class<? extends GameInteraction>, DoubleTypeRecord<GameInteractionReceiver>>();
    
    public GameEventMgr (Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    private class ItemEventSchedTask 
            <Event extends GameItemEvent, I extends BaseItemType>
            implements SchedTask, SuperItemEventReceiver<I> {
        private final Event event;
        private final I itemType;
        private final GameEventCallback callback;
        public boolean received = false;
        public ItemEventSchedTask(Event event, I itemType, GameEventCallback callback) {
            this.event = event;
            this.itemType = itemType;
            this.callback = callback;
        }
        
        @SuppressWarnings("unchecked")
        private boolean dispatch (GameItemEvent event, Class<? super I> iClass, I i) {
            GameItemEventReceiver recv;
            TypeRecord<GameItemEventReceiver> record = itemEventReceivers.get(event.getClass());
            if (record != null && (recv = record.get(iClass)) != null) {
                recv.onReceive(event, i); 
                return true;
            } else
                return false;

        }

        @Override
        public boolean invokeOnSuper(Class<? super I> superClass) {
            return dispatch(event, superClass, itemType);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onScheduled(SchedTime time) {
            received = dispatch(event, (Class<? super I>) itemType.getClass(), itemType);
        }

        @Override
        public void beginSchedule(TaskController t) {
        }

        @Override
        public void endSchedule(TaskController t) {
            if (callback != null) {
                callback.callback(received);
            }
        }
    }
       

    
    private class InteractionSchedTask 
            <Interaction extends GameInteraction, I1 extends BaseItemType, I2 extends BaseItemType>
            implements SchedTask, SuperInteractionReceiver<I1, I2> {
        private final Interaction event;
        private final I1 itemTypeLHS;
        private final I2 itemTypeRHS;
        private final GameEventCallback callback;
        public boolean received = false;
        public InteractionSchedTask(Interaction event, I1 itemTypeLHS,
                I2 itemTypeRHS, GameEventCallback callback) {
            this.event = event;
            this.itemTypeLHS = itemTypeLHS;
            this.itemTypeRHS = itemTypeRHS;
            this.callback = callback;
        }


        
        @SuppressWarnings("unchecked")
        private boolean dispatch (GameInteraction event, Class<? super I1> i1Class, Class<? super I2> i2Class, I1 i1, I2 i2) {
            GameInteractionReceiver recv;
            DoubleTypeRecord<GameInteractionReceiver> record = interactionReceivers.get(event.getClass());
            if (record != null && (recv = record.get(i1Class, i2Class)) != null) {
                recv.onReceive(event, i1, i2, this);
                return true;
            } else
                return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onScheduled(SchedTime time) {
            received = dispatch(event, 
                                (Class<? super I1>)itemTypeLHS.getClass(), 
                                (Class<? super I2>)itemTypeRHS.getClass(), 
                                itemTypeLHS,
                                itemTypeRHS);
        }

        @Override
        public void beginSchedule(TaskController t) {
        }

        @Override
        public void endSchedule(TaskController t) {
            if (callback != null) {
                callback.callback(received);
            }
        }

        @Override
        public boolean invokeOnSuper( Class<? super I1> superClassLHS, Class<? super I2> superClassRHS) {
            return dispatch(event, superClassLHS, superClassRHS, itemTypeLHS, itemTypeRHS);
        }

    }
    
    
    public 
    <Interaction extends GameInteraction, I1 extends BaseItemType, I2 extends BaseItemType>
    TaskController invokeEvent(long delay, long relativeDeadline, 
                               Interaction event, I1 itemTypeLHS, I2 itemTypeRHS,
                               GameEventCallback callback) {
        return scheduler.scheduleOnce(delay, 
                                      relativeDeadline, 
                                      new InteractionSchedTask<Interaction, I1, I2>(event, itemTypeLHS, itemTypeRHS, callback));
    }

    public 
    <Event extends GameItemEvent, I extends BaseItemType>
    TaskController invokeEvent(long delay, long relativeDeadline, 
                               Event event, I itemType,
                               GameEventCallback callback) {
        return scheduler.scheduleOnce(delay, 
                                      relativeDeadline, 
                                      new ItemEventSchedTask<Event, I>(event, itemType, callback));
    }
    
    public <Event extends GameBroadcast>
    TaskController invokeEvent(long delay, long relativeDeadline,
                               final Event event, final GameEventCallback callback) {
        return scheduler.scheduleOnce(delay, relativeDeadline, 
            new SchedTask() {
                private boolean received = false;

                @SuppressWarnings("unchecked")
                @Override
                public void onScheduled(SchedTime time) {
                    ArrayList<GameBroadcastReceiver> list = broadcastReceivers.get(event.getClass());
                    if (list != null) {
                        for (GameBroadcastReceiver recv : list) {
                            recv.onReceive(event);
                            received = true;
                        }
                    }
                }
                
                @Override
                public void endSchedule(TaskController t) {
                    if (callback != null) {
                        callback.callback(received);
                    }
                }
                
                @Override
                public void beginSchedule(TaskController t) {
                }
        });
    }
    
    

    public <T extends GameBroadcast> 
    void receiveBroadcast(Class<T> eventClass, GameBroadcastReceiver<T> receiver) {
        ArrayList<GameBroadcastReceiver> recvList;
        if ((recvList = broadcastReceivers.get(eventClass)) == null) {
            recvList = new ArrayList<GameBroadcastReceiver>();
            broadcastReceivers.put(eventClass, recvList);
        }
        recvList.add(receiver);
    }

    @SuppressWarnings("unchecked")
    public <T extends GameItemEvent, I extends BaseItemType>
    GameItemEventReceiver<T, I>
    receiveItemEvent(Class<T> eventClass, Class<I> itemTypeClass, GameItemEventReceiver<T, I> receiver) {
        TypeRecord<GameItemEventReceiver> record;
        if ((record = itemEventReceivers.get(eventClass)) == null) {
            record = new TypeRecord<GameItemEventReceiver>(Object.class);
            itemEventReceivers.put(eventClass, record);
        }
        return record.put(itemTypeClass, receiver);
    }

    @SuppressWarnings("unchecked")
    public <T extends GameInteraction, I1 extends BaseItemType, I2 extends BaseItemType>
    GameInteractionReceiver<T, I1, I2>
    receiveInteraction(Class<T> eventClass, Class<I1> itemTypeClassLHS, Class<I2> itemTypeClassRHS,
                 GameInteractionReceiver<T, I1, I2> receiver) {
        DoubleTypeRecord<GameInteractionReceiver> record;
        if ((record = interactionReceivers.get(eventClass)) == null) {
            record = new DoubleTypeRecord<GameInteractionReceiver>(Object.class);
            interactionReceivers.put(eventClass, record);
        }

        return record.put(itemTypeClassLHS, itemTypeClassRHS, receiver);
    }
}