package com.algy.schedcore.frontend;

import com.algy.schedcore.Scheduler;

class SchedulerUpdater {
    /**
     * 
     */
    private final Scheduler scheduler;

    /**
     * @param scene
     */
    public SchedulerUpdater(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    private boolean stop = false;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (!stop)
                scheduler.runOnce();
        }
    };
    private Thread thread;
    
    public synchronized void start () {
        stop = false;
        thread = new Thread(runnable);
        thread.start();
    }
    public synchronized void stop () {
    	if (!stop) {
            stop = true;
            thread.interrupt();
            /*
            // this stub cause deadlock :(
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            */
            thread = null;
    	}
    }
}