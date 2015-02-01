package com.algy.schedcore.frontend;

import com.algy.schedcore.Scheduler;

class UpdaterLongjump extends RuntimeException {
}

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
            try {
                while (!stop) {
                    scheduler.runOnce();
                }
            } catch (UpdaterLongjump e) {}
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
    	    if (Thread.currentThread().getId() != thread.getId()) {
    	        thread.interrupt();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
    	    } 
            thread = null;
    	}
    }
}