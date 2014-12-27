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

    public static JobInfo firstWaitingJob(TaskInfo ti, long futureReltime) {
        return new JobInfo(JobStatus.Waiting, ti, -1, -1, -1, futureReltime, true, null, null);
    }
    
    public static JobInfo firstPendingJob(TaskInfo ti, long current) {
        return new JobInfo(JobStatus.Pending, ti, current, current, current + ti.period, -1, true, null, null);
    }
}

class HeapNode {
    public JobInfo content;
    public HeapNode left = null, right = null, parent = null, linePrev = null, lineNext = null;
    
    public HeapNode (JobInfo content) {
        this.content = content;
    }
    
    public void clear() {
        left = null; 
        right = null; 
        parent = null; 
        linePrev = null;
        lineNext = null;
    }
}

class BinheapQueue {
    public int size = 0;
    public HeapNode root = null;
    public HeapNode rightmostLeaf = null;
    public boolean isPendingQueue;
    
    public BinheapQueue (boolean isPendingQueue) {
        this.isPendingQueue = isPendingQueue;
    }

    public boolean isEmpty () {
        return root == null;
    }
    
    public JobInfo peek () {
        return root.content;
    }
    
    public JobInfo shallowPop () {
        if (root != null) {
            JobInfo ji = root.content;
            JobInfo ji_next = ji.next;
            if (ji_next != null) {
                if (ji_next.next != null)
                    ji_next.next.prev = ji;
                ji.next = ji_next.next;
                ji_next.prev = null;
                ji_next.next = null;
                return ji_next;
            } else {
                return deepPop();
            }
        } else {
            return null;
        }
    }

    public JobInfo deepPop () {
        return remove(root.content);
    }

    public void push (JobInfo ji) {
        HeapNode heapNode = ji.heapNode;
        heapNode.clear();

        if (size == 0) {
            root = rightmostLeaf = heapNode;
            size++;
        } else { 
            HeapNode anchor = null;
            boolean appendToLeft = true;
            boolean newLine = false;

            if (rightmostLeaf.parent == null) {
                newLine = true;
                anchor = rightmostLeaf;
            } else if (rightmostLeaf.parent.left == rightmostLeaf) {
                appendToLeft = false;
                anchor = rightmostLeaf.parent;
            } else if (rightmostLeaf.parent.lineNext == null) {
                newLine = true;
                HeapNode p = root;
                while (p.left != null) {
                    p = p.left;
                }
                anchor = p;
            } else {
                anchor = rightmostLeaf.parent.lineNext;
            }

            if (appendToLeft) {
                // append to left
                anchor.left = heapNode;
            } else {
                // append to right
                anchor.right = heapNode;
            }
            heapNode.parent = anchor;

            if (newLine) {
                rightmostLeaf.lineNext = null;
                heapNode.linePrev = null; 
            } else {
                rightmostLeaf.lineNext = heapNode;
                heapNode.linePrev = rightmostLeaf;
            }
            rightmostLeaf = heapNode;
            size++;
            upheap(heapNode);
        }
    }

    public JobInfo remove(JobInfo ji) {
        HeapNode heapNode = ji.heapNode;
        if (size == 0) {
            return null;
        } 

        if (rightmostLeaf == root) {
            if (root == heapNode) {
                JobInfo result = root.content;
                root.clear();
                root = rightmostLeaf = null;
                size--;
                return result;
            } else
                return null;
        } 

        swap(heapNode, rightmostLeaf);
        if (rightmostLeaf.parent != null) {
            if (rightmostLeaf.parent.left == rightmostLeaf)
                rightmostLeaf.parent.left = null;
            else
                rightmostLeaf.parent.right = null;
        }

        HeapNode newRightmost;
        if (rightmostLeaf.linePrev != null) {
            rightmostLeaf.linePrev.lineNext = null;
            newRightmost = rightmostLeaf.linePrev;
        } else {
            HeapNode p = root;
            while (p.right != null)
                p = p.right;
            newRightmost = p;
        }
        JobInfo result = rightmostLeaf.content;
        rightmostLeaf.clear();
        rightmostLeaf = newRightmost;
        size--;

        if (newRightmost == null)
            root = null;
        else if (heapNode.parent != null && cmp(heapNode.parent, heapNode) > 0)
            upheap(heapNode);
        else
            downheap(heapNode);
        return result;
    }
    
    private void upheap(HeapNode node) {
        HeapNode deadEnd = node.parent;
        boolean mergable = false;
        while (deadEnd != null) {
            int test = cmp(node, deadEnd);
            if (test > 0) {
                break;
            } else if (test == 0) {
                mergable = true;
                break;
            }
            deadEnd = deadEnd.parent;
        }

        if (mergable) {
            JobInfo ji = node.content;
            JobInfo deadEndJi = deadEnd.content;
            ji.prev = deadEndJi;
            ji.next = deadEndJi.next;
            if (deadEndJi.next != null)
                deadEndJi.next.prev = ji;
            deadEndJi.next = ji;
            remove(ji);
        } else {
            while (node.parent != deadEnd) {
                swap(node, node.parent);
                node = node.parent;
            }
        }

    }

    private void downheap(HeapNode node) {
        while (node.left != null) {
            HeapNode cand = node;
            if (cmp(cand, node.left) > 0)
                cand = node.left;
            if (node.right != null && cmp(cand, node.right) > 0)
                cand = node.right;
            if (node != cand) {
                swap(node, cand);
                node = cand;
            } else
                break;
        }
    }

    private void swap(HeapNode lhs, HeapNode rhs) {
        lhs.content.heapNode = rhs;
        rhs.content.heapNode = lhs;

        JobInfo tmp = lhs.content;
        lhs.content = rhs.content;
        rhs.content = tmp;
    }
    
    private int cmp (HeapNode lhs, HeapNode rhs) {
        long a, b;
        if (isPendingQueue) { // EDF
            a = lhs.content.curDeadline; 
            b = rhs.content.curDeadline;
        } else { // Early FutureReltime First
            a = lhs.content.futureReltime;
            b = rhs.content.futureReltime;
        }

        if (a < b)
            return -1;
        else if (a > b)
            return 1;
        else
            return 0;
    }
    
    public int count () {
        Assert(_inv(root));
        return _count(root);
    }
    
    private boolean _inv (HeapNode heapNode) {
        if (heapNode == null)
            return true;
        else if (_inv(heapNode.left) && _inv(heapNode.right)) {
            return heapNode.parent == null || cmp(heapNode.parent, heapNode) <= 0;
        } else
            return false;
    }
    
    private static int _count (HeapNode heapNode) {
        int result = 0;
        boolean hasChild = false;
        
        if (heapNode == null)
            return 0;
        
        if (heapNode.left != null) {
            result += _count(heapNode.left);
            Assert(heapNode.left.parent == heapNode);
            hasChild = true;
                    
        }
        if (heapNode.right != null) {
            result += _count(heapNode.right);
            Assert(heapNode.right.parent == heapNode);
            hasChild = true;
        }
        
        if (heapNode.lineNext != null) {
            Assert (heapNode.lineNext.linePrev == heapNode);
        }
        if (heapNode.linePrev != null) {
            Assert (heapNode.linePrev.lineNext == heapNode);
        }
        
        for (JobInfo ji = heapNode.content; ji != null; ji = ji.next) {
            if (heapNode.content == ji)
                Assert (ji.prev == null);
            
            if (ji.prev != null) {
                Assert(ji.prev.next == ji);
            }
            if (ji.next != null) {
                Assert(ji.next.prev == ji);
            }
            result++;
        }
        return result;
    }
    private static void Assert (boolean test) {
        if (!test) {
            throw new RuntimeException("Invariant BROKEN!");
        }
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
    
    public static interface Task {
        public int getTaskId();
        public TaskType getTaskType();
        public JobStatus getStatus();

        public boolean active();
        public boolean suspended();

        public void kill();
        public boolean suspend();
        public boolean resume();
        
        public boolean aperiodicDone ();

        public Object repr();
    }
    class TaskImpl implements Task {
        public int taskImplId;
        public JobInfo ji;
        public boolean active = true;
        public boolean aperiodicDoneCache = false;
        public Object reprObjCache;
        public TaskType typeCache;
        public int periodCache;
        
        public void invalidate() {
            aperiodicDoneCache = ji.aperiodicDone;
            reprObjCache = ji.ti.reprObj;
            typeCache = ji.ti.type;

            active = false;
            ji = null;
        }

        @Override
        public int getTaskId() {
            assertActiveStatus("getTaskId()");
            return ji.ti.taskId;
        }


        @Override
        public TaskType getTaskType() {
            if (active)
                return ji.ti.type;
            else
                return typeCache;
        }

        @Override
        public JobStatus getStatus() {
            assertActiveStatus("getStatus()");
            return ji.status;
        }

        @Override
        public boolean active() {
            return active;
        }

        @Override
        public void kill() {
            assertActiveStatus("kill()");
            Scheduler.this.kill(getTaskId());
        }

        @Override
        public boolean suspended() {
            assertActiveStatus("suspended()");
            return Scheduler.this.suspended(getTaskId());
        }

        @Override
        public boolean suspend() {
            assertActiveStatus("suspend()");
            return Scheduler.this.suspend(getTaskId());
        }

        @Override
        public boolean resume() {
            assertActiveStatus("resume()");
            return Scheduler.this.resume(getTaskId());
        }

        @Override
        public Object repr() {
            if (active)
                return ji.ti.reprObj;
            else
                return reprObjCache;
        }

        @Override
        public boolean aperiodicDone() {
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
    }

    private TickGetter timer;
    
    private BinheapQueue pendings = new BinheapQueue(true);
    private BinheapQueue waitings = new BinheapQueue(false); // futureReltime
    
    private IntegerBitmap <JobInfo> infoBitmap;
    
    
    private JobInfo currentJob;
    private long currentJobPeriod;
    private long currentJobDeadline;
    
    public Scheduler(TickGetter timer) {
        this.timer = timer;
        this.infoBitmap = new IntegerBitmap<JobInfo>();
    }

    public static Scheduler MilliScheduler() {
        return new Scheduler(TickGetter.systemTickGetter);
    }
    public static Scheduler NanoScheduler() {
        return new Scheduler(TickGetter.systemNanoTickGetter);
    }

    // Aspect from frontend
    public Task addPeriodic(SchedTask task, long period, long offset, Object reprObj) {
        return addTask(TaskType.Periodic, task, period, offset, reprObj);
    }
    

    public Task addAperiodic(SchedTask task, long relDeadline, long offset, Object reprObj) {
        return addTask(TaskType.Aperiodic, task, relDeadline, offset, reprObj);
    }
    
    private Task addTask(TaskType type, SchedTask task, long period, long offset, Object reprObj) {
        TaskInfo ti;
        JobInfo ji;

        long current = timer.getTickCount();
        ti = new TaskInfo(type, -1, task, period, current + offset, reprObj);
        if (offset <= 0 && currentJobDeadline > current + period) { // EDF
            // pend it
            ji = JobInfo.firstPendingJob(ti, current);
            queueToPendings(ji, current, current + offset, current + offset + period);
        } else {
            ji = JobInfo.firstWaitingJob(ti, current + offset);
            evictToWaitings(ji, current + offset);
        }
        int id = infoBitmap.add(ji);
        task.beginSchedule();
        ti.taskId = id;
        
        TaskImpl taskImpl = issueTaskImpl(ji);
        ji.taskImpl = taskImpl;
        return taskImpl;
    }
    
    public JobStatus status(int taskid) {
        if (!has(taskid))
            return null;
        return infoBitmap.get(taskid).status;
    }
    
    public boolean suspend (int taskid) {
        if (!has(taskid))
            return false;
        suspendJob(infoBitmap.get(taskid), timer.getTickCount());
        return true;
    }
    
    public boolean resume (int taskid) {
        if (!has(taskid))
            return false;
        recoverFromSuspension(infoBitmap.get(taskid), timer.getTickCount());
        return true;
    }
    
    public boolean suspended (int taskid) {
        if (!has(taskid))
            return false;
        return infoBitmap.get(taskid).status == JobStatus.Suspended;
    }
    
    public void killAll () {
        for (JobInfo ji : infoBitmap) {
            kill(ji.ti.taskId);
        }
    }

    public boolean kill(int taskId) {
        if (!infoBitmap.has(taskId)) 
            return false;

        JobInfo oldJob = infoBitmap.remove(taskId); 
        removeFromQueue(oldJob);
        invalidateTaskImpl(oldJob.taskImpl);
        oldJob.ti.task.endSchedule();
        
        return true;
    }
    
    public boolean has (int taskId) {
        return infoBitmap.has(taskId);
    }
    
    static int vv = 0;
    
    private static SchedTime schedTimeLocal = new SchedTime(0, 0, 0, true);

    // Aspect from backend
    public Object runOnce() {
        // EDF(Earlist Deadline First) scheduling
        
        //long st, ed;
        // st = System.nanoTime();

        long startTime = timer.getTickCount();
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
            schedTimeLocal.realDeltaTime = realdelta;
            schedTimeLocal.deadline = execJob.curDeadline;
            schedTimeLocal.deltaTime = period;
            // NOTE: this.currentJob can become null or other job 
            //       after schedule it (it means the current job is now rescheduled)
//            long mst, med;
//            mst = System.nanoTime();
            execJob.ti.task.onScheduled(schedTimeLocal);
//            med = System.nanoTime();

            // printQueueStat();

            long finishTime = timer.getTickCount();
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
            if (this.currentJob != null && this.currentJob == execJob) {
                evictJobAfterExec(execJob, execJob.curReltime + period, finishTime);
                currentJob = null;
            } else {
                // In this case, the "current" job we once knew is assumed to be already queued to "pendings", 
                // evicted to "waitings", be suspended, or even be killed. So we don't care about it.
            }
            /*
            ed = System.nanoTime();
            
            System.out.println("Scheduling overhead: " + (ed - st - (med - mst)) / 1000.0 + " us");
            */
            

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
    private void invalidateTaskImpl(TaskImpl taskImpl) {
        taskImpl.invalidate();
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
            // FIXME: kill?
            ji.aperiodicDone = true;
            kill(ji.ti.taskId);
            break;
        }
    }

    private void releaseJobs(long current) {
        /*
         * Release waiting jobs into "pendings", each expected release time of which is less or equal than current time,
         */
        while (!waitings.isEmpty()) {
            if (waitings.peek().futureReltime <= current) {
                JobInfo ji = waitings.shallowPop();
                queueToPendings(ji, current, ji.futureReltime, ji.futureReltime + ji.ti.period);
            } else
                break;
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
        return waitings.count();
    }

    private int countPendings() {
        return pendings.count();
    }
    
    private JobInfo scheduleFromPendings() {
        return pendings.shallowPop();
    }

    private void setToCurrentJob(JobInfo ji) {
        ji.status = JobStatus.Running;
        ji.next = null;
        ji.prev = null;
        this.currentJob = ji;
        this.currentJobPeriod = ji.ti.period;
        this.currentJobDeadline = ji.curDeadline;
    }

    private void queueToPendings(JobInfo ji, long pushedTime, long curReltime, long curDeadline) {
        ji.status = JobStatus.Pending;
        ji.pushedTime = pushedTime;
        ji.curReltime = curReltime;
        ji.curDeadline = curDeadline;
        pendings.push(ji);
    }

    private void evictToWaitings(JobInfo ji, long futureReltime) {
        ji.futureReltime = futureReltime;
        ji.status = JobStatus.Waiting;
        waitings.push(ji);
    }
    
    private void suspendJob(JobInfo ji, long current) {
        if (ji.status == JobStatus.Suspended) 
            return;
        removeFromQueue (ji);
        ji.prevStatus = ji.status;
        ji.timeAtSuspension = current;
        ji.status = JobStatus.Suspended;
    }

    private void recoverFromSuspension(JobInfo ji, long current) {
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
    
    private void removeFromQueue(JobInfo oldJob) {
        if (oldJob.prev != null) {
            if (oldJob.next != null)
                oldJob.next.prev = oldJob.prev;
            oldJob.prev.next = oldJob.next;

            oldJob.prev = null;
            oldJob.next = null;
        } else {
            switch (oldJob.status) {
            case Pending:
                pendings.remove(oldJob);
                if (oldJob.next != null) {
                    oldJob.next.prev = null;
                    pendings.push(oldJob.next);
                    oldJob.next = null;
                } 
                break;
            case Running:
                this.currentJob = null;
                break;
            case Waiting:
                waitings.remove(oldJob);
                if (oldJob.next != null) {
                    oldJob.next.prev = null;
                    waitings.push(oldJob.next);
                    oldJob.next = null;
                } 
                break;
            case Suspended:
                break;
            }
        }
    }
}