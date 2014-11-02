package com.algy.schedcore.util;

import java.util.HashSet;

import com.badlogic.gdx.files.FileHandle;

public class FileObserver {
    public static interface Notification {
        public void fileModified(FileHandle file, int index);
        public void fileCreated(FileHandle file, int index);
        public void fileRemoved(FileHandle file, int index);
    }
    
    private HashSet<Notification> observers = new HashSet<Notification>();
    private FileHandle [] fileList;
    private long [] fileMTimes;
    private boolean [] fileExistences;
    private boolean observing = false;
    private long observingPeriod;
    private Thread runningThread = null;

    private Object observerLock = new Object();
    private Object observingStateLock = new Object();

    public FileObserver (long observingPeriod) {
        this.observingPeriod = observingPeriod;
    }
    
    public void register (Notification observer) {
        synchronized (observerLock) {
            this.observers.add(observer);
        }
    }

    public void unregister (Notification observer) {
        synchronized (observerLock) {
            this.observers.remove(observer);
        }
    }

    private void notifyCreated (int index) {
        synchronized (observerLock) {
            for (Notification dest : observers) {
                dest.fileCreated(fileList[index], index);
            }
        }
    }
    private void notifyRemoved (int index) {
        synchronized (observerLock) {
            for (Notification dest : observers) {
                dest.fileRemoved(fileList[index], index);
            }
        }
    }

    private void notifyModified (int index) {
        synchronized (observerLock) {
            for (Notification dest : observers) {
                dest.fileModified(fileList[index], index);
            }
        }
    }
    
    private class Watcher implements Runnable {
        boolean first = true;
        @Override
        public void run() {
            while (true) {
                if (!observing) break;
                if (first) {
                    for (int idx = 0;  idx < fileList.length; idx++) {
                        if (fileList[idx].exists()) {
                            fileExistences[idx] = true;
                            fileMTimes[idx] = fileList[idx].lastModified();
                        } else 
                            fileExistences[idx] = false;
                    }
                    for (int idx = 0;  idx < fileList.length; idx++) {
                        if (fileExistences[idx]) {
                            notifyCreated(idx);
                        } else 
                            notifyRemoved(idx);
                    }
                    first = false;
                } else {
                    for (int idx = 0;  idx < fileList.length; idx++) {
                        boolean oldExistence = fileExistences[idx];
                        boolean curExistence = fileList[idx].exists();
                        fileExistences[idx] = curExistence;
                        if (!oldExistence && curExistence)
                            notifyCreated(idx);
                        else if (oldExistence && !curExistence)
                            notifyRemoved(idx);
                        else if (oldExistence && curExistence) {
                            long oldMTime = fileMTimes[idx];
                            long curMTime = fileList[idx].lastModified();
                            
                            if (curMTime > oldMTime) {
                                fileMTimes[idx] = curMTime;
                                notifyModified(idx);
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(observingPeriod);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
    

    public void start (FileHandle [] fileList) {
        synchronized (observingStateLock) {
            if (isObserving()) 
                return;
            observing = true;
            this.fileList = fileList;
            this.fileMTimes = new long [this.fileList.length];
            this.fileExistences = new boolean [this.fileList.length];
            runningThread = new Thread(new Watcher());
            runningThread.start();
        }
    }
    
    public void end () {
        synchronized (observingStateLock) {
            if (!isObserving()) 
                return;

            runningThread.interrupt();
            try {
                runningThread.join();
            } catch (InterruptedException e) { }
            runningThread = null;
            fileList = null;
            fileExistences = null;
            fileMTimes = null;
            observing = false;
        }
    }
    
    public void join () throws InterruptedException {
        runningThread.join();
    }
    
    public boolean isObserving () {
        return observing;
    }
}
