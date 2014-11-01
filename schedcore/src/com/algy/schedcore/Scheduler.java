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
            long curReltime, long curDeadline, long lastExecutedTime,
            long futureReltime, boolean first, JobInfo prev, JobInfo next) {
        super();
        this.status = status;
        this.ti = ti;
        this.pushedTime = pushedTime;
        this.curReltime = curReltime;
        this.curDeadline = curDeadline;
        this.lastExecutedTime = lastExecutedTime;
        this.futureReltime = futureReltime;
        this.first = first;
        this.prev = prev;
        this.next = next;
    }
    public TaskInfo ti;
    public long pushedTime;
    public long curReltime; 
    public long curDeadline;
    public long futureReltime; // used in Waiting status
    public long lastExecutedTime; 
    public long lastSuspendedTime;
    public boolean first;
    public JobInfo prev, next;
    
    public static JobInfo firstWaitingJob(TaskInfo ti, long futureReltime) {
        return new JobInfo(JobStatus.Waiting, ti, -1, -1, -1, -1, futureReltime, true, null, null);
    }
    
    public static JobInfo firstPendingJob(TaskInfo ti, long current) {
        return new JobInfo(JobStatus.Pending, ti, current, current, current + ti.period, -1, -1, true, null, null);
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
        // if (offset <= 0 && currentJobPeriod > period) { // RM
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
    
    public boolean suspend (int taskid) {
        if (!has(taskid))
            return false;
        suspendJob(infoBitmap.get(taskid));
        return true;
    }
    
    public boolean resume (int taskid) {
        if (!has(taskid))
            return false;
        resumeJob(infoBitmap.get(taskid));
        return true;
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
    // Aspect from backend
    public Object runOnce(ITickGetter tickGetter) {
        //// RM(Rate Monotonic) scheduling
        // EDF(Earlist Deadline First) scheduling

        long current = tickGetter.getTickCount();
        releaseJobs(current);
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
                realdelta = current - execJob.lastExecutedTime;
            }

            execJob.lastExecutedTime = current;
            
            // NOTE: this.currentJob can become null or other job 
            //       after schedule it (it means the current job is now rescheduled)
            execJob.ti.task.schedule(new SchedTime(period,
                                                   execJob.curDeadline,
                                                   realdelta, 
                                                   execJob.first));
            current = tickGetter.getTickCount();
            if (current > execJob.curDeadline) {
                System.out.println("Task " + execJob.ti.taskId + " missed deadline. " + (current - execJob.curDeadline));
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
    
    private void suspendJob(JobInfo ji) {
        /*
         * Pending -
         *    ff
         *    
         *    
         * 
         * 
         * 
         * 
         */
        if (ji.status == JobStatus.Suspended)
            return;
        removeFromQueue (ji);
        ji.status = JobStatus.Suspended;
    }

    private void resumeJob(JobInfo ji) {
        if (ji.status != JobStatus.Suspended)
            return;
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