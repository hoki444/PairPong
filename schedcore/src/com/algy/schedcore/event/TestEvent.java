package com.algy.schedcore.event;

import com.algy.schedcore.BaseItemType;
import com.algy.schedcore.SchedTask;
import com.algy.schedcore.SchedTime;
import com.algy.schedcore.Scheduler;
import com.algy.schedcore.TaskController;

class IrtnBattle extends GameInteraction {
}

class Dog extends BaseItemType {
    @Override
    public BaseItemType duplicate() {
        return null;
    }
}

class ShibeDoge extends Dog {
    @Override
    public BaseItemType duplicate() {
        return new ShibeDoge();
    }
}

class MainTask implements SchedTask {
    private Scheduler scheduler;
    private GameEventMgr eventMgr;
    
    public MainTask (Scheduler scheduler , GameEventMgr eventMgr) {
        this.scheduler = scheduler;
        this.eventMgr = eventMgr;
    }
    
    @Override
    public void onScheduled(SchedTime time) {
        eventMgr.invokeEvent(100, 0, new IrtnBattle(), new Dog(), new ShibeDoge(), new GameEventCallback() {
            @Override
            public void callback(boolean received) {
                System.out.println("Received: " + received);
            }
        });
    }

    @Override
    public void beginSchedule(TaskController t) {
    }

    @Override
    public void endSchedule(TaskController t) {
    }
}

public class TestEvent {
    public static void main (String [] args ) {
        Scheduler scheduler = Scheduler.MilliScheduler();
        GameEventMgr mgr = new GameEventMgr(scheduler);
        
        mgr.receiveInteraction(IrtnBattle.class, Dog.class, Dog.class, 
                new GameInteractionReceiver<IrtnBattle, Dog, Dog>() {
                    @Override
                    public void onReceive(IrtnBattle event, Dog lhs, Dog rhs,
                            SuperInteractionReceiver<Dog, Dog> superReceiver) {
                        System.out.println("General");
                    }
                });
        mgr.receiveInteraction(IrtnBattle.class, Dog.class, ShibeDoge.class, 
                new GameInteractionReceiver<IrtnBattle, Dog, ShibeDoge>() {
                    @Override
                    public void onReceive(IrtnBattle event, Dog lhs, ShibeDoge rhs,
                            SuperInteractionReceiver<Dog, ShibeDoge> superReceiver) {
                        System.out.println("Specific");
                    }
                });

        scheduler.scheduleOnce(0, 0, new MainTask(scheduler, mgr));
        while (true) {
            scheduler.runOnce();
        }
    }

}
