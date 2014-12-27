package com.algy.schedcore;

import com.algy.schedcore.Scheduler.Task;


public class TestScheduler {
    public static int Fib (int n) {
        if (n <= 1) {
            return 1;
        } else
            return Fib(n - 1) + Fib(n - 2);
    }

    public static void main (String [] args) throws InterruptedException {
        final Scheduler schd = Scheduler.MilliScheduler();
        
        final Task t = schd.addPeriodic(new SchedTask() {
            public void onScheduled(SchedTime time) {
                Fib(40);
                System.out.println("P0:" + System.currentTimeMillis());
            }
            
            public void endSchedule() {
            }
            
            public void beginSchedule() {
            }
        }, 2000, 0, "P90");
        t.suspend();
        for (int idx = 0; idx < 1; idx++) {
            schd.addPeriodic(
                    new SchedTask() {
                        int num = 0;
                        public void onScheduled(SchedTime time) {
                            num++;
                            System.out.println("P50:" + System.currentTimeMillis());
                        }
                        public void endSchedule() {
                        }
                        
                        public void beginSchedule() {
                        }
            }, 50, 0, "P50");
        }
        while (true) {
            schd.runOnce();
        }
    }
}
