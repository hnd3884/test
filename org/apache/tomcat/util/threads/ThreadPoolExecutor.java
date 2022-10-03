package org.apache.tomcat.util.threads;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.ConcurrentModificationException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.security.Permission;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tomcat.util.res.StringManager;
import java.util.concurrent.AbstractExecutorService;

public class ThreadPoolExecutor extends AbstractExecutorService
{
    protected static final StringManager sm;
    private final AtomicInteger ctl;
    private static final int COUNT_BITS = 29;
    private static final int COUNT_MASK = 536870911;
    private static final int RUNNING = -536870912;
    private static final int SHUTDOWN = 0;
    private static final int STOP = 536870912;
    private static final int TIDYING = 1073741824;
    private static final int TERMINATED = 1610612736;
    private final BlockingQueue<Runnable> workQueue;
    private final ReentrantLock mainLock;
    private final HashSet<Worker> workers;
    private final Condition termination;
    private int largestPoolSize;
    private long completedTaskCount;
    private final AtomicInteger submittedCount;
    private final AtomicLong lastContextStoppedTime;
    private final AtomicLong lastTimeThreadKilledItself;
    private volatile long threadRenewalDelay;
    private volatile ThreadFactory threadFactory;
    private volatile RejectedExecutionHandler handler;
    private volatile long keepAliveTime;
    private volatile boolean allowCoreThreadTimeOut;
    private volatile int corePoolSize;
    private volatile int maximumPoolSize;
    private static final RejectedExecutionHandler defaultHandler;
    private static final RuntimePermission shutdownPerm;
    private static final boolean ONLY_ONE = true;
    
    private static int workerCountOf(final int c) {
        return c & 0x1FFFFFFF;
    }
    
    private static int ctlOf(final int rs, final int wc) {
        return rs | wc;
    }
    
    private static boolean runStateLessThan(final int c, final int s) {
        return c < s;
    }
    
    private static boolean runStateAtLeast(final int c, final int s) {
        return c >= s;
    }
    
    private static boolean isRunning(final int c) {
        return c < 0;
    }
    
    private boolean compareAndIncrementWorkerCount(final int expect) {
        return this.ctl.compareAndSet(expect, expect + 1);
    }
    
    private boolean compareAndDecrementWorkerCount(final int expect) {
        return this.ctl.compareAndSet(expect, expect - 1);
    }
    
    private void decrementWorkerCount() {
        this.ctl.addAndGet(-1);
    }
    
    private void advanceRunState(final int targetState) {
        int c;
        do {
            c = this.ctl.get();
        } while (!runStateAtLeast(c, targetState) && !this.ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c))));
    }
    
    final void tryTerminate() {
        while (true) {
            final int c = this.ctl.get();
            if (isRunning(c) || runStateAtLeast(c, 1073741824) || (runStateLessThan(c, 536870912) && !this.workQueue.isEmpty())) {
                return;
            }
            if (workerCountOf(c) != 0) {
                this.interruptIdleWorkers(true);
                return;
            }
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                if (this.ctl.compareAndSet(c, ctlOf(1073741824, 0))) {
                    try {
                        this.terminated();
                    }
                    finally {
                        this.ctl.set(ctlOf(1610612736, 0));
                        this.termination.signalAll();
                    }
                    return;
                }
                continue;
            }
            finally {
                mainLock.unlock();
            }
        }
    }
    
    private void checkShutdownAccess() {
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(ThreadPoolExecutor.shutdownPerm);
            for (final Worker w : this.workers) {
                security.checkAccess(w.thread);
            }
        }
    }
    
    private void interruptWorkers() {
        for (final Worker w : this.workers) {
            w.interruptIfStarted();
        }
    }
    
    private void interruptIdleWorkers(final boolean onlyOne) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (final Worker w : this.workers) {
                final Thread t = w.thread;
                if (!t.isInterrupted() && w.tryLock()) {
                    try {
                        t.interrupt();
                    }
                    catch (final SecurityException ex) {}
                    finally {
                        w.unlock();
                    }
                }
                if (onlyOne) {
                    break;
                }
            }
        }
        finally {
            mainLock.unlock();
        }
    }
    
    private void interruptIdleWorkers() {
        this.interruptIdleWorkers(false);
    }
    
    final void reject(final Runnable command) {
        this.handler.rejectedExecution(command, this);
    }
    
    void onShutdown() {
    }
    
    private List<Runnable> drainQueue() {
        final BlockingQueue<Runnable> q = this.workQueue;
        final ArrayList<Runnable> taskList = new ArrayList<Runnable>();
        q.drainTo(taskList);
        if (!q.isEmpty()) {
            for (final Runnable r : q.toArray(new Runnable[0])) {
                if (q.remove(r)) {
                    taskList.add(r);
                }
            }
        }
        return taskList;
    }
    
    private boolean addWorker(final Runnable firstTask, final boolean core) {
        int c = this.ctl.get();
    Label_0008:
        while (!runStateAtLeast(c, 0) || (!runStateAtLeast(c, 536870912) && firstTask == null && !this.workQueue.isEmpty())) {
            while (workerCountOf(c) < ((core ? this.corePoolSize : this.maximumPoolSize) & 0x1FFFFFFF)) {
                if (this.compareAndIncrementWorkerCount(c)) {
                    boolean workerStarted = false;
                    boolean workerAdded = false;
                    Worker w = null;
                    try {
                        w = new Worker(firstTask);
                        final Thread t = w.thread;
                        if (t != null) {
                            final ReentrantLock mainLock = this.mainLock;
                            mainLock.lock();
                            try {
                                final int c2 = this.ctl.get();
                                if (isRunning(c2) || (runStateLessThan(c2, 536870912) && firstTask == null)) {
                                    if (t.getState() != Thread.State.NEW) {
                                        throw new IllegalThreadStateException();
                                    }
                                    this.workers.add(w);
                                    workerAdded = true;
                                    final int s = this.workers.size();
                                    if (s > this.largestPoolSize) {
                                        this.largestPoolSize = s;
                                    }
                                }
                            }
                            finally {
                                mainLock.unlock();
                            }
                            if (workerAdded) {
                                t.start();
                                workerStarted = true;
                            }
                        }
                    }
                    finally {
                        if (!workerStarted) {
                            this.addWorkerFailed(w);
                        }
                    }
                    return workerStarted;
                }
                c = this.ctl.get();
                if (runStateAtLeast(c, 0)) {
                    continue Label_0008;
                }
            }
            return false;
        }
        return false;
    }
    
    private void addWorkerFailed(final Worker w) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if (w != null) {
                this.workers.remove(w);
            }
            this.decrementWorkerCount();
            this.tryTerminate();
        }
        finally {
            mainLock.unlock();
        }
    }
    
    private void processWorkerExit(final Worker w, final boolean completedAbruptly) {
        if (completedAbruptly) {
            this.decrementWorkerCount();
        }
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            this.completedTaskCount += w.completedTasks;
            this.workers.remove(w);
        }
        finally {
            mainLock.unlock();
        }
        this.tryTerminate();
        final int c = this.ctl.get();
        if (runStateLessThan(c, 536870912)) {
            if (!completedAbruptly) {
                int min = this.allowCoreThreadTimeOut ? 0 : this.corePoolSize;
                if (min == 0 && !this.workQueue.isEmpty()) {
                    min = 1;
                }
                if (workerCountOf(c) >= min && this.workQueue.isEmpty()) {
                    return;
                }
            }
            this.addWorker(null, false);
        }
    }
    
    private Runnable getTask() {
        boolean timedOut = false;
        while (true) {
            final int c = this.ctl.get();
            if (runStateAtLeast(c, 0) && (runStateAtLeast(c, 536870912) || this.workQueue.isEmpty())) {
                this.decrementWorkerCount();
                return null;
            }
            final int wc = workerCountOf(c);
            final boolean timed = this.allowCoreThreadTimeOut || wc > this.corePoolSize;
            if ((wc > this.maximumPoolSize || (timed && timedOut)) && (wc > 1 || this.workQueue.isEmpty())) {
                if (this.compareAndDecrementWorkerCount(c)) {
                    return null;
                }
                continue;
            }
            else {
                try {
                    final Runnable r = timed ? this.workQueue.poll(this.keepAliveTime, TimeUnit.NANOSECONDS) : this.workQueue.take();
                    if (r != null) {
                        return r;
                    }
                    timedOut = true;
                }
                catch (final InterruptedException retry) {
                    timedOut = false;
                }
            }
        }
    }
    
    final void runWorker(final Worker w) {
        final Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock();
        boolean completedAbruptly = true;
        try {
            while (task != null || (task = this.getTask()) != null) {
                w.lock();
                if ((runStateAtLeast(this.ctl.get(), 536870912) || (Thread.interrupted() && runStateAtLeast(this.ctl.get(), 536870912))) && !wt.isInterrupted()) {
                    wt.interrupt();
                }
                try {
                    this.beforeExecute(wt, task);
                    try {
                        task.run();
                        this.afterExecute(task, null);
                    }
                    catch (final Throwable ex) {
                        this.afterExecute(task, ex);
                        throw ex;
                    }
                }
                finally {
                    task = null;
                    ++w.completedTasks;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        }
        finally {
            this.processWorkerExit(w, completedAbruptly);
        }
    }
    
    public ThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), ThreadPoolExecutor.defaultHandler);
    }
    
    public ThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, ThreadPoolExecutor.defaultHandler);
    }
    
    public ThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), handler);
    }
    
    public ThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
        this.ctl = new AtomicInteger(ctlOf(-536870912, 0));
        this.mainLock = new ReentrantLock();
        this.workers = new HashSet<Worker>();
        this.termination = this.mainLock.newCondition();
        this.submittedCount = new AtomicInteger(0);
        this.lastContextStoppedTime = new AtomicLong(0L);
        this.lastTimeThreadKilledItself = new AtomicLong(0L);
        this.threadRenewalDelay = 1000L;
        if (corePoolSize < 0 || maximumPoolSize <= 0 || maximumPoolSize < corePoolSize || keepAliveTime < 0L) {
            throw new IllegalArgumentException();
        }
        if (workQueue == null || threadFactory == null || handler == null) {
            throw new NullPointerException();
        }
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }
    
    @Override
    public void execute(final Runnable command) {
        this.execute(command, 0L, TimeUnit.MILLISECONDS);
    }
    
    @Deprecated
    public void execute(final Runnable command, final long timeout, final TimeUnit unit) {
        this.submittedCount.incrementAndGet();
        try {
            this.executeInternal(command);
        }
        catch (final RejectedExecutionException rx) {
            if (!(this.getQueue() instanceof TaskQueue)) {
                this.submittedCount.decrementAndGet();
                throw rx;
            }
            final TaskQueue queue = (TaskQueue)this.getQueue();
            try {
                if (!queue.force(command, timeout, unit)) {
                    this.submittedCount.decrementAndGet();
                    throw new RejectedExecutionException(ThreadPoolExecutor.sm.getString("threadPoolExecutor.queueFull"));
                }
            }
            catch (final InterruptedException x) {
                this.submittedCount.decrementAndGet();
                throw new RejectedExecutionException(x);
            }
        }
    }
    
    private void executeInternal(final Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }
        int c = this.ctl.get();
        if (workerCountOf(c) < this.corePoolSize) {
            if (this.addWorker(command, true)) {
                return;
            }
            c = this.ctl.get();
        }
        if (isRunning(c) && this.workQueue.offer(command)) {
            final int recheck = this.ctl.get();
            if (!isRunning(recheck) && this.remove(command)) {
                this.reject(command);
            }
            else if (workerCountOf(recheck) == 0) {
                this.addWorker(null, false);
            }
        }
        else if (!this.addWorker(command, false)) {
            this.reject(command);
        }
    }
    
    @Override
    public void shutdown() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            this.checkShutdownAccess();
            this.advanceRunState(0);
            this.interruptIdleWorkers();
            this.onShutdown();
        }
        finally {
            mainLock.unlock();
        }
        this.tryTerminate();
    }
    
    @Override
    public List<Runnable> shutdownNow() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        List<Runnable> tasks;
        try {
            this.checkShutdownAccess();
            this.advanceRunState(536870912);
            this.interruptWorkers();
            tasks = this.drainQueue();
        }
        finally {
            mainLock.unlock();
        }
        this.tryTerminate();
        return tasks;
    }
    
    @Override
    public boolean isShutdown() {
        return runStateAtLeast(this.ctl.get(), 0);
    }
    
    boolean isStopped() {
        return runStateAtLeast(this.ctl.get(), 536870912);
    }
    
    public boolean isTerminating() {
        final int c = this.ctl.get();
        return runStateAtLeast(c, 0) && runStateLessThan(c, 1610612736);
    }
    
    @Override
    public boolean isTerminated() {
        return runStateAtLeast(this.ctl.get(), 1610612736);
    }
    
    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            while (runStateLessThan(this.ctl.get(), 1610612736)) {
                if (nanos <= 0L) {
                    return false;
                }
                nanos = this.termination.awaitNanos(nanos);
            }
            return true;
        }
        finally {
            mainLock.unlock();
        }
    }
    
    public void setThreadFactory(final ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException();
        }
        this.threadFactory = threadFactory;
    }
    
    public ThreadFactory getThreadFactory() {
        return this.threadFactory;
    }
    
    public void setRejectedExecutionHandler(final RejectedExecutionHandler handler) {
        if (handler == null) {
            throw new NullPointerException();
        }
        this.handler = handler;
    }
    
    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return this.handler;
    }
    
    public void setCorePoolSize(final int corePoolSize) {
        if (corePoolSize < 0 || this.maximumPoolSize < corePoolSize) {
            throw new IllegalArgumentException();
        }
        final int delta = corePoolSize - this.corePoolSize;
        if (workerCountOf(this.ctl.get()) > (this.corePoolSize = corePoolSize)) {
            this.interruptIdleWorkers();
        }
        else if (delta > 0) {
            int k = Math.min(delta, this.workQueue.size());
            while (k-- > 0 && this.addWorker(null, true) && !this.workQueue.isEmpty()) {}
        }
    }
    
    public int getCorePoolSize() {
        return this.corePoolSize;
    }
    
    public boolean prestartCoreThread() {
        return workerCountOf(this.ctl.get()) < this.corePoolSize && this.addWorker(null, true);
    }
    
    void ensurePrestart() {
        final int wc = workerCountOf(this.ctl.get());
        if (wc < this.corePoolSize) {
            this.addWorker(null, true);
        }
        else if (wc == 0) {
            this.addWorker(null, false);
        }
    }
    
    public int prestartAllCoreThreads() {
        int n = 0;
        while (this.addWorker(null, true)) {
            ++n;
        }
        return n;
    }
    
    public boolean allowsCoreThreadTimeOut() {
        return this.allowCoreThreadTimeOut;
    }
    
    public void allowCoreThreadTimeOut(final boolean value) {
        if (value && this.keepAliveTime <= 0L) {
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        }
        if (value != this.allowCoreThreadTimeOut && (this.allowCoreThreadTimeOut = value)) {
            this.interruptIdleWorkers();
        }
    }
    
    public void setMaximumPoolSize(final int maximumPoolSize) {
        if (maximumPoolSize <= 0 || maximumPoolSize < this.corePoolSize) {
            throw new IllegalArgumentException();
        }
        if (workerCountOf(this.ctl.get()) > (this.maximumPoolSize = maximumPoolSize)) {
            this.interruptIdleWorkers();
        }
    }
    
    public int getMaximumPoolSize() {
        return this.maximumPoolSize;
    }
    
    public void setKeepAliveTime(final long time, final TimeUnit unit) {
        if (time < 0L) {
            throw new IllegalArgumentException();
        }
        if (time == 0L && this.allowsCoreThreadTimeOut()) {
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        }
        final long keepAliveTime = unit.toNanos(time);
        final long delta = keepAliveTime - this.keepAliveTime;
        this.keepAliveTime = keepAliveTime;
        if (delta < 0L) {
            this.interruptIdleWorkers();
        }
    }
    
    public long getKeepAliveTime(final TimeUnit unit) {
        return unit.convert(this.keepAliveTime, TimeUnit.NANOSECONDS);
    }
    
    public long getThreadRenewalDelay() {
        return this.threadRenewalDelay;
    }
    
    public void setThreadRenewalDelay(final long threadRenewalDelay) {
        this.threadRenewalDelay = threadRenewalDelay;
    }
    
    public BlockingQueue<Runnable> getQueue() {
        return this.workQueue;
    }
    
    public boolean remove(final Runnable task) {
        final boolean removed = this.workQueue.remove(task);
        this.tryTerminate();
        return removed;
    }
    
    public void purge() {
        final BlockingQueue<Runnable> q = this.workQueue;
        try {
            final Iterator<Runnable> it = q.iterator();
            while (it.hasNext()) {
                final Runnable r = it.next();
                if (r instanceof Future && ((Future)r).isCancelled()) {
                    it.remove();
                }
            }
        }
        catch (final ConcurrentModificationException fallThrough) {
            for (final Object r2 : q.toArray()) {
                if (r2 instanceof Future && ((Future)r2).isCancelled()) {
                    q.remove(r2);
                }
            }
        }
        this.tryTerminate();
    }
    
    public void contextStopping() {
        this.lastContextStoppedTime.set(System.currentTimeMillis());
        final int savedCorePoolSize = this.getCorePoolSize();
        final TaskQueue taskQueue = (this.getQueue() instanceof TaskQueue) ? ((TaskQueue)this.getQueue()) : null;
        if (taskQueue != null) {
            taskQueue.setForcedRemainingCapacity(0);
        }
        this.setCorePoolSize(0);
        if (taskQueue != null) {
            taskQueue.resetForcedRemainingCapacity();
        }
        this.setCorePoolSize(savedCorePoolSize);
    }
    
    public int getPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return runStateAtLeast(this.ctl.get(), 1073741824) ? 0 : this.workers.size();
        }
        finally {
            mainLock.unlock();
        }
    }
    
    public int getActiveCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int n = 0;
            for (final Worker w : this.workers) {
                if (w.isLocked()) {
                    ++n;
                }
            }
            return n;
        }
        finally {
            mainLock.unlock();
        }
    }
    
    public int getLargestPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return this.largestPoolSize;
        }
        finally {
            mainLock.unlock();
        }
    }
    
    public long getTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = this.completedTaskCount;
            for (final Worker w : this.workers) {
                n += w.completedTasks;
                if (w.isLocked()) {
                    ++n;
                }
            }
            return n + this.workQueue.size();
        }
        finally {
            mainLock.unlock();
        }
    }
    
    public long getCompletedTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = this.completedTaskCount;
            for (final Worker w : this.workers) {
                n += w.completedTasks;
            }
            return n;
        }
        finally {
            mainLock.unlock();
        }
    }
    
    public int getSubmittedCount() {
        return this.submittedCount.get();
    }
    
    @Override
    public String toString() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        long ncompleted;
        int nactive;
        int nworkers;
        try {
            ncompleted = this.completedTaskCount;
            nactive = 0;
            nworkers = this.workers.size();
            for (final Worker w : this.workers) {
                ncompleted += w.completedTasks;
                if (w.isLocked()) {
                    ++nactive;
                }
            }
        }
        finally {
            mainLock.unlock();
        }
        final int c = this.ctl.get();
        final String runState = isRunning(c) ? "Running" : (runStateAtLeast(c, 1610612736) ? "Terminated" : "Shutting down");
        return super.toString() + "[" + runState + ", pool size = " + nworkers + ", active threads = " + nactive + ", queued tasks = " + this.workQueue.size() + ", completed tasks = " + ncompleted + "]";
    }
    
    protected void beforeExecute(final Thread t, final Runnable r) {
    }
    
    protected void afterExecute(final Runnable r, final Throwable t) {
        if (!(t instanceof StopPooledThreadException)) {
            this.submittedCount.decrementAndGet();
        }
        if (t == null) {
            this.stopCurrentThreadIfNeeded();
        }
    }
    
    protected void stopCurrentThreadIfNeeded() {
        if (this.currentThreadShouldBeStopped()) {
            final long lastTime = this.lastTimeThreadKilledItself.longValue();
            if (lastTime + this.threadRenewalDelay < System.currentTimeMillis() && this.lastTimeThreadKilledItself.compareAndSet(lastTime, System.currentTimeMillis() + 1L)) {
                final String msg = ThreadPoolExecutor.sm.getString("threadPoolExecutor.threadStoppedToAvoidPotentialLeak", Thread.currentThread().getName());
                throw new StopPooledThreadException(msg);
            }
        }
    }
    
    protected boolean currentThreadShouldBeStopped() {
        if (this.threadRenewalDelay >= 0L && Thread.currentThread() instanceof TaskThread) {
            final TaskThread currentTaskThread = (TaskThread)Thread.currentThread();
            if (currentTaskThread.getCreationTime() < this.lastContextStoppedTime.longValue()) {
                return true;
            }
        }
        return false;
    }
    
    protected void terminated() {
    }
    
    static {
        sm = StringManager.getManager(ThreadPoolExecutor.class);
        defaultHandler = new RejectPolicy();
        shutdownPerm = new RuntimePermission("modifyThread");
    }
    
    private final class Worker extends AbstractQueuedSynchronizer implements Runnable
    {
        private static final long serialVersionUID = 6138294804551838833L;
        final Thread thread;
        Runnable firstTask;
        volatile long completedTasks;
        
        Worker(final Runnable firstTask) {
            this.setState(-1);
            this.firstTask = firstTask;
            this.thread = ThreadPoolExecutor.this.getThreadFactory().newThread(this);
        }
        
        @Override
        public void run() {
            ThreadPoolExecutor.this.runWorker(this);
        }
        
        @Override
        protected boolean isHeldExclusively() {
            return this.getState() != 0;
        }
        
        @Override
        protected boolean tryAcquire(final int unused) {
            if (this.compareAndSetState(0, 1)) {
                this.setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }
        
        @Override
        protected boolean tryRelease(final int unused) {
            this.setExclusiveOwnerThread(null);
            this.setState(0);
            return true;
        }
        
        public void lock() {
            this.acquire(1);
        }
        
        public boolean tryLock() {
            return this.tryAcquire(1);
        }
        
        public void unlock() {
            this.release(1);
        }
        
        public boolean isLocked() {
            return this.isHeldExclusively();
        }
        
        void interruptIfStarted() {
            final Thread t;
            if (this.getState() >= 0 && (t = this.thread) != null && !t.isInterrupted()) {
                try {
                    t.interrupt();
                }
                catch (final SecurityException ex) {}
            }
        }
    }
    
    public static class CallerRunsPolicy implements RejectedExecutionHandler
    {
        @Override
        public void rejectedExecution(final Runnable r, final ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                r.run();
            }
        }
    }
    
    public static class AbortPolicy implements RejectedExecutionHandler
    {
        @Override
        public void rejectedExecution(final Runnable r, final ThreadPoolExecutor e) {
            throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + e.toString());
        }
    }
    
    public static class DiscardPolicy implements RejectedExecutionHandler
    {
        @Override
        public void rejectedExecution(final Runnable r, final ThreadPoolExecutor e) {
        }
    }
    
    public static class DiscardOldestPolicy implements RejectedExecutionHandler
    {
        @Override
        public void rejectedExecution(final Runnable r, final ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                e.getQueue().poll();
                e.execute(r);
            }
        }
    }
    
    private static class RejectPolicy implements RejectedExecutionHandler
    {
        @Override
        public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {
            throw new RejectedExecutionException();
        }
    }
    
    public interface RejectedExecutionHandler
    {
        void rejectedExecution(final Runnable p0, final ThreadPoolExecutor p1);
    }
}
