package com.algy.schedcore;

import com.algy.schedcore.Scheduler.JobStatus;
import com.algy.schedcore.Scheduler.TaskType;
import com.algy.schedcore.util.IntegerBitmap;

class TaskInfo {
    public TaskInfo(TaskType type, int taskId, SchedTask task, long period, long firstReltime, Object reprObj) {
        super();
        this.type = type;
        this.taskId = taskId;
        this.task = task;
        this.period = period;
        this.firstReltime = firstReltime;
        this.reprObj = reprObj;
    }
    public TaskType type;
    public int taskId;
    public SchedTask task;
    public long period; // it means "relative deadline" in the case of aperidic task
    public long firstReltime;
    public Object reprObj = null;
    public SchedTaskConfig config;
}

class JobInfo {
    public Scheduler.TaskImpl taskImpl;
    public JobStatus status;
    public JobInfo(JobStatus status, TaskInfo ti, long pushedTime,
            long curReltime, long curDeadline,
            long futureReltime, boolean first, JobInfo prev, JobInfo next) {
        super();
        this.status = status;
        this.ti = ti;
        
        this.curReltime = curReltime;
        this.curDeadline = curDeadline;
        this.first = first;
        
        this.futureReltime = futureReltime;
        this.prev = prev;
        this.next = next;

        // informative fields for statistics
        this.pushedTime = pushedTime;
    }

    public TaskInfo ti;
    /*
     * fields that are valid when status is Pending or Running
     */
    public long curReltime; 
    public long curDeadline;
    /*
     * fields that are valid when status is Waiting
     */
    public long futureReltime; 
    public long lastSuspendedTime;

    /* first execution? */
    public boolean first;

    /*
     * fields that are valid when status is Suspended or Sleeping
     */
    public long timeAtSuspension; 
    public JobStatus prevStatus;

    /*
     * fields for forming double linked list with jobinfos having the same status
     */
    public JobInfo prev, next;

    /*
     * If this jobinfo is respresented as an element in binary heap, 
     * these fields describes connected left child, right child and parent, respectively.
     * 
     * Only valid for "head node"
     */
    public HeapNode heapNode = new HeapNode(this);

    /*
     * informative fields for statistics
     */
    public long lastStartTime; 
    public long lastExecutionTime; 
    public long ewmaExecutionTime; // alpha = 0.5
    public long pushedTime;
    
    
    /*
     * only valid for aperiodic task
     */
    public boolean aperiodicDone = false;
    
    public JobInfo popNext() {
        JobInfo ji_next = this.next;
        if (ji_next != null) {
            if (ji_next.next != null)
                ji_next.next.prev = this;
            this.next = ji_next.next;
            ji_next.prev = null;
            ji_next.next = null;
            return ji_next;
        } else {
            return null;
        }
    }

    public boolean popThis () {
        if (this.prev != null) {
            if (this.next != null) {
                this.next.prev = this.prev;
            }
            this.prev.next = this.next;
            this.prev = null;
            this.next = null;
            return true;
        }
        return false;
    }

    
    public void pushNext (JobInfo ji) {
        ji.prev = this;
        ji.next = this.next;
        if (this.next != null)
            this.next.prev = ji;
        this.next = ji;
    }

    public static JobInfo firstWaitingJob(TaskInfo ti, long futureReltime) {
        return new JobInfo(JobStatus.Waiting, ti, -1, -1, -1, futureReltime, true, null, null);
    }
    
    public static JobInfo firstPendingJob(TaskInfo ti, long current) {
        return new JobInfo(JobStatus.Pending, ti, current, current, current + ti.period, -1, true, null, null);
    }
}

@SuppressWarnings("unused")
public final class Scheduler {
    public static enum JobStatus {
        Running, Pending, Waiting, Suspended
    }
    public static enum TaskType {
        Periodic, Aperiodic
    }
    
    class TaskImpl implements TaskController {
        public int taskImplId;
        public JobInfo ji;
        public boolean active = true;
        public boolean aperiodicDoneCache = false;
        public Object reprObjCache;
        public TaskType typeCache;
        public int periodCache;
        
        public synchronized void invalidate() {
            if (active) {
                active = false;
                aperiodicDoneCache = ji.aperiodicDone;
                reprObjCache = ji.ti.reprObj;
                typeCache = ji.ti.type;

                ji = null;
            }
        }

        @Override
        public synchronized int getTaskId() {
            assertActiveStatus("getTaskId()");
            return ji.ti.taskId;
        }


        @Override
        public synchronized TaskType getTaskType() {
            if (active)
                return ji.ti.type;
            else
                return typeCache;
        }

        @Override
        public synchronized JobStatus getStatus() {
            assertActiveStatus("getStatus()");
            return ji.status;
        }

        @Override
        public synchronized boolean active() {
            return active;
        }

        @Override
        public synchronized void kill() {
            assertActiveStatus("kill()");
            Scheduler.this.kill(getTaskId());
        }

        @Override
        public synchronized boolean suspended() {
            assertActiveStatus("suspended()");
            return Scheduler.this.suspended(getTaskId());
        }

        @Override
        public synchronized boolean suspend() {
            assertActiveStatus("suspend()");
            return Scheduler.this.suspend(getTaskId());
        }

        @Override
        public synchronized boolean resume() {
            assertActiveStatus("resume()");
            return Scheduler.this.resume(getTaskId());
        }

        @Override
        public synchronized Object repr() {
            if (active)
                return ji.ti.reprObj;
            else
                return reprObjCache;
        }

        @Override
        public synchronized boolean aperiodicDone() {
            if (active)
                return ji.aperiodicDone;
            else
                return aperiodicDoneCache;
        }
        
        private void assertActiveStatus (String v) {
            if (!active) {
                throw new SchedcoreRuntimeError("Tried to execute " + v + ", but the corresponding task is inactive");
            }
        }

        @Override
        public synchronized void setRepr(Object reprObj) {
            assertActiveStatus("setRepr()");
            ji.ti.reprObj = reprObj;
        }

        @Override
        public synchronized float getAverageExecTime() {
            assertActiveStatus("getAverageExecTime()");
            return ji.ewmaExecutionTime;
        }
    }

    private TickGetter timer;
    
    private BinheapQueue pendings = new BinheapQueue(true);
    private BinheapQueue waitings = new BinheapQueue(false); // futureReltime
    
    private IntegerBitmap <JobInfo> infoBitmap;
    
    private JobInfo currentJob;
    private long currentJobPeriod;
    private long currentJobDeadline;
    
    private Object currentJobLock = new Object();
    private Object pendingsLock = new Object();
    private Object waitingsLock = new Object();
    private Object suspendingsLock = new Object();
    
    private JobInfo sentinelJI = new JobInfo(null, null, 0, 0, 0, 0, true, null, null);
    public Scheduler(TickGetter timer) {
        this.timer = timer;
        this.infoBitmap = new IntegerBitmap<JobInfo>(sentinelJI);
    }

    public static Scheduler MilliScheduler() {
        return new Scheduler(TickGetter.systemTickGetter);
    }

    public static Scheduler MicroScheduler() {
        return new Scheduler(TickGetter.systemMicroTickGetter);
    }

    public static Scheduler NanoScheduler() {
        return new Scheduler(TickGetter.systemNanoTickGetter);
    }

    // Aspect from frontend
    public TaskController schedule(long delay, long period, SchedTask task) {
        return schedule(delay, period, task, null);
    }

    public TaskController scheduleOnce(long delay, long relativeDeadline, SchedTask task) {
        return scheduleOnce(delay, relativeDeadline, task, null);
    }
    
    public TaskController schedule(long delay, long period, SchedTask task, SchedTaskConfig config) {
        return addTask(TaskType.Periodic, task, period, delay, config);
    }
    
    public TaskController scheduleOnce(long delay, long relDeadline, SchedTask task, SchedTaskConfig config) {
        return addTask(TaskType.Aperiodic, task, relDeadline, delay, config);
    }
    
    private TaskController addTask(TaskType type, SchedTask task, long period, long offset, SchedTaskConfig config) {
        TaskInfo ti;
        JobInfo ji;

        long current = timer.getTime();
        ti = new TaskInfo(type, -1, task, period, current + offset, null);
        ti.config = config;

        ji = JobInfo.firstWaitingJob(ti, current + offset);
        evictToWaitings(ji, current + offset);

        int id = infoBitmap.add(ji);
        ti.taskId = id;
        
        TaskImpl taskImpl = issueTaskImpl(ji);
        ji.taskImpl = taskImpl;

        task.beginSchedule(taskImpl);
        return taskImpl;
    }
    
    public JobStatus status(int taskid) {
        JobInfo ji; 
        ji = infoBitmap.get(taskid);
        if (ji != null) {
            return ji.status;
        } else {
            return null;
        }
    }
    
    public boolean suspend (int taskid) {
        JobInfo ji; 
        ji = infoBitmap.get(taskid);
        if (ji == null)
            return false;
        suspendJob(ji, timer.getTime());
        return true;
    }
    
    public boolean resume (int taskid) {
        JobInfo ji = infoBitmap.get(taskid);
        if (ji == null)
            return false;
        recoverFromSuspension(ji, timer.getTime());
        return true;
    }
    
    public boolean suspended (int taskid) {
        JobInfo ji = infoBitmap.get(taskid);
        synchronized (suspendingsLock) {
            return ji.status == JobStatus.Suspended;
        }
    }
    
    public void killAll () {
        for (JobInfo ji : infoBitmap) {
            kill(ji.ti.taskId);
        }
    }

    public void kill(int taskId) {
        JobInfo oldJob;
        oldJob = infoBitmap.remove(taskId); 
        if (oldJob != null) {
            removeFromQueueAndCleanUp(oldJob);
            infoBitmap.releaseId(taskId);
        }
    }
    
    private void removeFromQueueAndCleanUp(JobInfo oldJob) {
        removeFromQueue(oldJob);
        oldJob.ti.task.endSchedule(oldJob.taskImpl);
        oldJob.taskImpl.invalidate();
    }
    
    
    public boolean hasId (int taskId) {
        return infoBitmap.has(taskId);
    }
    
    private SchedTime schedTimeLocal = new SchedTime(0, 0, 0, true);

    // Aspect from backend
    public Object runOnce() {
        // EDF(Earlist Deadline First) scheduling
        
        RuntimeException error = null;
        RuntimeException error2 = null;
        //long st, ed;
        // st = System.nanoTime();
        long startTime = timer.getTime();
        releaseJobs(startTime);

        JobInfo execJob = scheduleFromPendings();
        if (execJob != null) {
            setToCurrentJob(execJob);

            long period = execJob.ti.period;
            long realdelta;
            if (execJob.first) {
                execJob.first = false;
                realdelta = -1;
            } else {
                realdelta = startTime - execJob.lastStartTime;
            }

            execJob.lastStartTime = startTime;

            // The fact schedTimeLocal is a field of instance and not created per runOnce() call.
            // It can be helpful for preventing the scheduler from making too many garbages
            schedTimeLocal.first =  execJob.first;
            schedTimeLocal.realDeltaTime = realdelta;
            schedTimeLocal.deadline = execJob.curDeadline;
            schedTimeLocal.deltaTime = period;

//            long mst, med;
//            mst = System.nanoTime();
            try {
                execJob.ti.task.onScheduled(schedTimeLocal);
            } catch (RuntimeException e) {
                error = e;
            }
//            med = System.nanoTime();

            // printQueueStat();

            long finishTime = timer.getTime();
            long executionTime = finishTime - startTime;
            execJob.lastExecutionTime = executionTime;
            if (execJob.first) {
                execJob.ewmaExecutionTime = executionTime;
                execJob.first = false;
            } else {
                /*
                 * exponential weighted moving average for execution time.
                 * alpha = 0.5
                 * alpha * newExec + (1 - alpha) * oldExec
                 *        1 * newExec + 1 * oldExec
                 * =  ------------------------------------
                 *                     2
                 */
                long oldExecutionTime = execJob.ewmaExecutionTime;
                execJob.ewmaExecutionTime = (oldExecutionTime + executionTime) / 2;
            }
            if (finishTime > execJob.curDeadline) {
                System.out.println("Task " + execJob.ti.taskId + " missed deadline. " + 
                                   (finishTime - execJob.curDeadline) +
                                   " avgExecTime. " + execJob.ewmaExecutionTime);
            }
            synchronized (currentJobLock) {
                if (this.currentJob != null) {
                    try {
                        evictJobAfterExec(execJob, execJob.curReltime + period, finishTime);
                    } catch (RuntimeException e) {
                        error2 = e;
                    }
                    currentJob = null;
                } else {
                    // In this case, the "current" job we once knew is suspected 
                    // being suspended or killed. 
                    // So we don't care about it.
                }
            }
            /*
            ed = System.nanoTime();
            
            System.out.println("Scheduling overhead: " + (ed - st - (med - mst)) / 1000.0 + " us");
            */
            
            if (error != null)
                throw error;
            if (error2 != null)
                throw error2;

            return execJob.ti.reprObj;
        } else {
            // IDLE
            return null;
        }
    }

    // Functions for internal usage
    private TaskImpl issueTaskImpl(JobInfo ji) {
        TaskImpl task = new TaskImpl();
        task.active = true;
        task.ji = ji;
        return task;
    }


    private void evictJobAfterExec(JobInfo ji, long futureReltime, long finishTime) {
        switch (ji.ti.type) {
        case Periodic:
            if (finishTime >= 0 && finishTime >= futureReltime) {
                queueToPendings(ji, finishTime, futureReltime, futureReltime + ji.ti.period);
            } else {
                evictToWaitings(ji, futureReltime);
            }
            break;
        case Aperiodic:
            ji.aperiodicDone = true;
            removeFromQueueAndCleanUp(ji);
            break;
        }
    }

    private void releaseJobs(long current) {
        /*
         * Release waiting jobs into "pendings", each expected release time of which is less or equal than current time,
         */
        synchronized (waitingsLock) {
            while (!waitings.isEmpty()) {
                if (waitings.peek().futureReltime <= current) {
                    JobInfo ji = waitings.shallowPop();
                    queueToPendings(ji, current, ji.futureReltime, ji.futureReltime + ji.ti.period);
                } else
                    break;
            }
        }
    }

    
    private int printQueueStat() {
        int pendingCount, waitingCount, currentCount;
        currentCount = (currentJob == null)? 0 : 1;
        pendingCount = countPendings();
        waitingCount = countWaitings();
        System.out.println("Running?: " + currentCount);
        System.out.println("Pendings: " + pendingCount);
        System.out.println("Waitings: " + waitingCount);
        System.out.println("==");
        return currentCount + pendingCount + waitingCount;

    }

    private int countWaitings() {
        return waitings.dbgcount();
    }

    private int countPendings() {
        return pendings.dbgcount();
    }
    
    private JobInfo scheduleFromPendings() {
        synchronized (pendingsLock) {
            return pendings.shallowPop();
        }
    }

    private void setToCurrentJob(JobInfo ji) {
        ji.next = null;
        ji.prev = null;
        synchronized (currentJobLock) {
            this.currentJobPeriod = ji.ti.period;
            this.currentJobDeadline = ji.curDeadline;
            this.currentJob = ji;

            ji.status = JobStatus.Running;
        }
    }

    private void queueToPendings(JobInfo ji, long pushedTime, long curReltime, long curDeadline) {
        ji.pushedTime = pushedTime;
        ji.curReltime = curReltime;
        ji.curDeadline = curDeadline;
        synchronized (pendingsLock) {
            pendings.push(ji);

            ji.status = JobStatus.Pending;
        }
    }

    private void evictToWaitings(JobInfo ji, long futureReltime) {
        ji.futureReltime = futureReltime;
        synchronized (waitingsLock) {
            waitings.push(ji);

            ji.status = JobStatus.Waiting;
        }
    }
    
    private void suspendJob(JobInfo ji, long current) {
        synchronized (suspendingsLock) {
            if (ji.status == JobStatus.Suspended) 
                return;
            removeFromQueue (ji);
            ji.prevStatus = ji.status;
            ji.timeAtSuspension = current;
            ji.status = JobStatus.Suspended;
        }
    }

    private void recoverFromSuspension(JobInfo ji, long current) {
        synchronized (suspendingsLock) {
            if (ji.status != JobStatus.Suspended) 
                return;
            long delta = current - ji.timeAtSuspension;
            switch (ji.prevStatus) {
            case Pending:
                queueToPendings(ji, current, ji.curReltime + delta, ji.curDeadline + delta);
                break;
            case Running: // This scheduler don't preempt job for suspending it. 
                          // In other words, when try to suspend job which is running now, 
                          // scheduler don't suspend it in the middle of job and waits for end of job.
                evictJobAfterExec(ji, ji.curReltime + ji.ti.period + delta, -1);
                break;
            case Waiting:
                evictToWaitings(ji, ji.futureReltime + delta);
                break;
            case Suspended:
                throw new RuntimeException("NON REACHABLE");
            }
        }
    }
    
    private void removeFromQueue(JobInfo oldJob) {
        while (true) {
            switch (oldJob.status) {
            case Pending:
                removeFromPendings(oldJob);
                break;
            case Waiting:
                removeFromWaitings(oldJob);
                break;
            case Running:
                synchronized (currentJobLock) {
                    if (this.currentJob != null) {
                        this.currentJob = null;
                    } else {
                        continue;
                    }
                }
                break;
            case Suspended:
                break;
            }
            break;
        }
    }

    private void removeFromPendings (JobInfo oldJob) {
        synchronized (pendingsLock) {
            if (oldJob.popThis())
                return;

            pendings.remove(oldJob);
            if (oldJob.next != null) {
                oldJob.next.prev = null;
                pendings.push(oldJob.next);
                oldJob.next = null;
            } 
        }
    }
    private void removeFromWaitings (JobInfo oldJob) {
        synchronized (waitingsLock) {
            if (oldJob.popThis())
                return;

            waitings.remove(oldJob);
            if (oldJob.next != null) {
                oldJob.next.prev = null;
                waitings.push(oldJob.next);
                oldJob.next = null;
            } 
        }
    }
}