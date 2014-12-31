package com.algy.schedcore;



public class TestScheduler {
    public static int Fib (int n) {
        if (n <= 1) {
            return 1;
        } else
            return Fib(n - 1) + Fib(n - 2);
    }

    public static void main (String [] args) throws InterruptedException {
        final Scheduler schd = Scheduler.MilliScheduler();
        
        final TaskController t = schd.schedule(0, 2000, new SchedTask() {
            public void onScheduled(SchedTime time) {
                Fib(40);
                System.out.println("P0:" + System.currentTimeMillis());
            }
            
            public void endSchedule(TaskController t) {
                System.out.println("ETime: " + t.getAverageExecTime());
            }
            
            public void beginSchedule(TaskController t) {
            }
        });
        for (int idx = 0; idx < 1; idx++) {
            schd.schedule(0, 50,
                    new SchedTask() {
                        int num = 0;
                        public void onScheduled(SchedTime time) {
                            num++;
                            System.out.println("P50:" + System.currentTimeMillis());
                            if (num >= 10) {
                                schd.kill(0);
                            }
                        }
                        public void endSchedule(TaskController t) {
                        }
                        
                        public void beginSchedule(TaskController t) {
                        }
            });
        }
        while (true) {
            schd.runOnce();
        }
    }
}
