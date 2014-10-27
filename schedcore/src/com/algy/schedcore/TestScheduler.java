package com.algy.schedcore;

public class TestScheduler {
    public static void main (String [] args) throws InterruptedException {
        Scheduler schd = new Scheduler();
        
        schd.addPeriodic(System.currentTimeMillis(), new ISchedTask() {
            public void schedule(SchedTime time) {
                System.out.println("P40:" + System.currentTimeMillis());
            }
            
            public void endSchedule() {
            }
            
            public void beginSchedule() {
            }
        }, 40, 0, "P40");
        schd.addPeriodic(System.currentTimeMillis(), 
                new ISchedTask() {
                    public void schedule(SchedTime time) {
                        System.out.println("P50 :" + System.currentTimeMillis());
                    }
                    public void endSchedule() {
                    }
                    
                    public void beginSchedule() {
                    }
        }, 50, 0, "P50");
        while (true) {
            schd.runOnce(new ITickGetter() {
                public long getTickCount() {
                    return System.currentTimeMillis();
                }
            });
            Thread.sleep(3);
        }
    }
}
