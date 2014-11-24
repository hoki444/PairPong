package com.algy.schedcore;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.algy.schedcore.Scheduler.JobStatus;
import com.algy.schedcore.util.IntegerBitmap;

class TaskInfo {
    public TaskInfo(int taskId, ISchedTask task, long period, long firstReltime, Object returned) {
        super();
        this.taskId = taskId;
        this.task = task;
        this.period = period;
        this.firstReltime = firstReltime;
        this.returned = returned;
    }
    public int taskId;
    public ISchedTask task;
    public long period;
    public long firstReltime;
    public Object returned = null;
}


class JobInfo {
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
    public long suspendedTime; 
    public JobStatus prevStatus;

    /*
     * fields for forming double linked list with jobinfos having the same status
     */
    public JobInfo prev, next;


    /*
     * informative fields for statistics
     */
    public long lastStartTime; 
    public long lastExecutionTime; 
    public long ewmaExecutionTime; // alpha = 0.2
    public long pushedTime;
    
    
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

    private TreeMap<Long, JobInfo> pendings; 
    private TreeMap<Long, JobInfo> waitings; // futureReltime -> JobInfo
    private ArrayList<JobInfo> tblTask;
    
    private IntegerBitmap <JobInfo> infoBitmap;
    private JobInfo currentJob;
    private long currentJobPeriod;
    private long currentJobDeadline;

    public Scheduler() {
        this.pendings = new TreeMap<Long, JobInfo>();
        this.waitings = new TreeMap<Long, JobInfo>();
        this.infoBitmap = new IntegerBitmap<JobInfo>();
    }

    // Aspect from frontend
    public int addPeriodic(long current, ISchedTask task, long period, long offset, Object returned) {
        TaskInfo ti;
        JobInfo ji;
        
        ti = new TaskInfo(-1, task, period, current + offset, returned);
        if (offset <= 0 && currentJobDeadline > current + period) { // EDF
            // pend it
            ji = JobInfo.firstPendingJob(ti, current);
            queueToPendings(ji, current, current + offset, current + offset + period);
        } else {
            ji = JobInfo.firstWaitingJob(ti, current + offset);
            evictToWaitings(ji, current + offset);
        }
        task.beginSchedule();
        
        int id = infoBitmap.add(ji);
        ti.taskId = id;
        return id;
    }
    
    public JobStatus status(int taskid) {
        if (!has(taskid))
            return null;
        return infoBitmap.get(taskid).status;
    }
    
    public boolean suspend (int taskid, ITickGetter tickGetter) {
        if (!has(taskid))
            return false;
        suspendJob(infoBitmap.get(taskid), tickGetter.getTickCount());
        return true;
    }
    
    public boolean resume (int taskid, ITickGetter tickGetter) {
        if (!has(taskid))
            return false;
        recoverFromSuspension(infoBitmap.get(taskid), tickGetter.getTickCount());
        return true;
    }
    
    public boolean suspended (int taskid) {
        if (!has(taskid))
            return false;
        return infoBitmap.get(taskid).status == JobStatus.Suspended;
    }

    public boolean kill(int taskId) {
        if (!infoBitmap.has(taskId)) 
            return false;

        JobInfo oldJob = infoBitmap.remove(taskId); 
        removeFromQueue(oldJob);
        oldJob.ti.task.endSchedule();
        
        return true;
    }
    
    public boolean has (int taskId) {
        return infoBitmap.has(taskId);
    }
    
    static int vv = 0;
    
    private static SchedTime schedTimeLocal = new SchedTime(0, 0, 0, true);

    // Aspect from backend
    public Object runOnce(ITickGetter tickGetter) {
        // EDF(Earlist Deadline First) scheduling

        long startTime = tickGetter.getTickCount();
        releaseJobs(startTime);
        if (this.currentJob == null) {
            JobInfo ji = scheduleFromPendings();
            if (ji != null)
                setToCurrentJob(ji);
        }

        if (this.currentJob != null) {
            JobInfo execJob = this.currentJob;
            long period = execJob.ti.period;
            long realdelta;
            if (execJob.first) {
                execJob.first = false;
                realdelta = -1;
            } else {
                realdelta = startTime - execJob.lastStartTime;
            }

            execJob.lastStartTime = startTime;

            // This is for preventing garbage
            schedTimeLocal.first =  execJob.first;
            schedTimeLocal.realDelta = realdelta;
            schedTimeLocal.deadline = execJob.curDeadline;
            schedTimeLocal.delta = period;
            // NOTE: this.currentJob can become null or other job 
            //       after schedule it (it means the current job is now rescheduled)
            execJob.ti.task.schedule(schedTimeLocal);

            long finishTime = tickGetter.getTickCount();
            long executionTime = finishTime - startTime;
            execJob.lastExecutionTime = executionTime;
            if (execJob.first) {
                execJob.ewmaExecutionTime = executionTime;
                execJob.first = false;
            } else {
                /*
                 * exponential weighted moving average for execution time.
                 * alpha = 0.2
                 * alpha * newExec + (1 - alpha) * oldExec
                 *        1 * newExec + 4 * oldExec
                 * =  ------------------------------------
                 *                     5
                 */
                long oldExecutionTime = execJob.ewmaExecutionTime;
                execJob.ewmaExecutionTime = (4 * oldExecutionTime + executionTime) / 5;
            }
            if (finishTime > execJob.curDeadline) {
                System.out.println("Task " + execJob.ti.taskId + " missed deadline. " + 
                                   (finishTime - execJob.curDeadline) +
                                   " avgExecTime. " + execJob.ewmaExecutionTime);
            }
            if (this.currentJob != null && this.currentJob == execJob) {
                evictCurrentJob(execJob.curReltime + period);
            } else {
                // In this case, the "current" job we knew is assumed to be already queued to "pendings", 
                // evicted to "waitings", be suspended, or even be killed. So we don't care about it.
            }

            return execJob.ti.returned;
        } else {
            // IDLE
            return null;
        }
    }

    // Functions for internal usage
    private void evictCurrentJob(long futureReltime) {
        evictToWaitings(currentJob, futureReltime);
        currentJob = null;
    }

    private void releaseJobs(long current) {
        /*
         * Release waiting jobs into "pendings", each expected release time of which is less or equal than current time,
         */
        for (Entry<Long, JobInfo> entry : this.waitings.entrySet()) {
            long futureReltime = entry.getKey();
            if (futureReltime > current) {
                break;
            }

            JobInfo ji = entry.getValue(), next = null;
            long period = ji.ti.period;


            while (ji != null) {
                assert ji.futureReltime == futureReltime;
                assert ji.status == JobStatus.Waiting;

                next = ji.next;
                queueToPendings(ji, current, futureReltime, futureReltime + period);
                ji = next;
            }
        }
        while (!this.waitings.isEmpty() && this.waitings.firstKey() <= current) {
            this.waitings.pollFirstEntry();
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
        int count = 0;
        for (Entry<Long, JobInfo> entrySet : this.waitings.entrySet()) {
            for (JobInfo ji = entrySet.getValue(); ji != null; ji = ji.next) {
                // invariant check
                if (ji.prev != null)  {
                    if (ji.prev.next != ji) throw new RuntimeException("LINK BROKEN!");
                } else {
                    if (ji != entrySet.getValue()) throw new RuntimeException("LINK BROKEN!");
                }
                count++;
            }
        }
        return count;
    }
    private int countPendings() {
        int count = 0;
        for (Entry<Long, JobInfo> entrySet : this.pendings.entrySet()) {
            for (JobInfo ji = entrySet.getValue(); ji != null; ji = ji.next) {
                // invariant check
                if (ji.prev != null)  {
                    if (ji.prev.next != ji) throw new RuntimeException("LINK BROKEN!");
                } else {
                    if (ji != entrySet.getValue()) throw new RuntimeException("LINK BROKEN!");
                }
                count++;
            }
        }
        return count;
    }
    
    private JobInfo scheduleFromPendings() {
        if (!this.pendings.isEmpty()) {
            Entry<Long, JobInfo> entry = this.pendings.firstEntry();
            long key = entry.getKey();
            JobInfo ji = entry.getValue();
            if (ji.next != null) {
                ji.next.prev = null;
                this.pendings.put(key, ji.next);
            } else {
                this.pendings.remove(key);
            }
            ji.next = null;
            
            return ji;
        } else {
            return null;
        }
    }

    private void setToCurrentJob(JobInfo ji) {
        ji.next = null;
        ji.prev = null;
        this.currentJob = ji;
        this.currentJobPeriod = ji.ti.period;
        this.currentJobDeadline = ji.curDeadline;
    }

    private void queueToPendings(JobInfo ji, long pushedTime, long curReltime, long curDeadline) {
        //JobInfo pendingHead = this.pendings.get(ji.ti.period); // RM
        JobInfo pendingHead = this.pendings.get(curDeadline);
        ji.status = JobStatus.Pending;
        ji.pushedTime = pushedTime;
        ji.curReltime = curReltime;
        ji.curDeadline = curDeadline;
        if (pendingHead != null) {
            ji.next = pendingHead;
            pendingHead.prev = ji;
        } else {
            ji.next = null;
        }
        ji.prev = null;
        this.pendings.put(curDeadline, ji);
    }

    private void evictToWaitings(JobInfo ji, long futureReltime) {
        JobInfo waitingHead = this.waitings.get(futureReltime);
        ji.futureReltime = futureReltime;
        ji.status = JobStatus.Waiting;
        if (waitingHead != null) {
            ji.next = waitingHead;
            waitingHead.prev = ji;
        } else {
            ji.next = null;
        }
        ji.prev = null;
        this.waitings.put(futureReltime, ji);
    }
    
    private void suspendJob(JobInfo ji, long current) {
        if (ji.status == JobStatus.Suspended) 
            return;
        removeFromQueue (ji);
        ji.prevStatus = ji.status;
        ji.suspendedTime = current;
        ji.status = JobStatus.Suspended;
    }

    private void recoverFromSuspension(JobInfo ji, long current) {
        if (ji.status != JobStatus.Suspended) 
            return;
        long delta = current - ji.suspendedTime;
        switch (ji.prevStatus) {
        case Pending:
            queueToPendings(ji, current, ji.curReltime + delta, ji.curDeadline + delta);
            break;
        case Running: // This scheduler don't preempt job for suspending it. 
                      // In other words, when try to suspend job which is running now, 
                      // scheduler don't suspend it in the middle of job and waits for end of job.
            evictToWaitings(ji, ji.curReltime + ji.ti.period + delta);
            break;
        case Waiting:
            evictToWaitings(ji, ji.futureReltime + delta);
            break;
        case Suspended:
            throw new RuntimeException("NON REACHABLE");
        }
    }
    
    private void removeFromQueue(JobInfo oldJob) {
        if (oldJob.prev != null) {
            if (oldJob.next != null)
                oldJob.next.prev = oldJob.prev;
            oldJob.prev.next = oldJob.next;
        } else {
            switch (oldJob.status) {
            case Pending:
                if (oldJob.next != null) {
                    oldJob.next.prev = null;
                    this.pendings.put(oldJob.curDeadline, oldJob.next);
                } else {
                    this.pendings.remove(oldJob.curDeadline);
                }

                break;
            case Running:
                this.currentJob = null;
                break;
            case Waiting:
                if (oldJob.next != null) {
                    oldJob.next.prev = null;
                    this.waitings.put(oldJob.futureReltime, oldJob.next);
                } else {
                    this.waitings.remove(oldJob.futureReltime);
                }
                break;
            case Suspended:
                break;
            }
        }
    }
}