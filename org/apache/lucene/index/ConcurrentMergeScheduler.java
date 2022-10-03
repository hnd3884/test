package org.apache.lucene.index;

import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.ThreadInterruptedException;
import java.util.Iterator;
import java.io.IOException;
import org.apache.lucene.util.IOUtils;
import java.util.Locale;
import org.apache.lucene.util.CollectionUtil;
import java.util.ArrayList;
import java.util.List;

public class ConcurrentMergeScheduler extends MergeScheduler
{
    public static final int AUTO_DETECT_MERGES_AND_THREADS = -1;
    public static final String DEFAULT_CPU_CORE_COUNT_PROPERTY = "lucene.cms.override_core_count";
    public static final String DEFAULT_SPINS_PROPERTY = "lucene.cms.override_spins";
    protected final List<MergeThread> mergeThreads;
    private int maxThreadCount;
    private int maxMergeCount;
    protected int mergeThreadCount;
    private static final double MIN_MERGE_MB_PER_SEC = 5.0;
    private static final double MAX_MERGE_MB_PER_SEC = 10240.0;
    private static final double START_MB_PER_SEC = 20.0;
    private static final double MIN_BIG_MERGE_MB = 50.0;
    protected double targetMBPerSec;
    private boolean doAutoIOThrottle;
    private double forceMergeMBPerSec;
    private boolean suppressExceptions;
    
    public ConcurrentMergeScheduler() {
        this.mergeThreads = new ArrayList<MergeThread>();
        this.maxThreadCount = -1;
        this.maxMergeCount = -1;
        this.targetMBPerSec = 20.0;
        this.doAutoIOThrottle = true;
        this.forceMergeMBPerSec = Double.POSITIVE_INFINITY;
    }
    
    public synchronized void setMaxMergesAndThreads(final int maxMergeCount, final int maxThreadCount) {
        if (maxMergeCount == -1 && maxThreadCount == -1) {
            this.maxMergeCount = -1;
            this.maxThreadCount = -1;
        }
        else {
            if (maxMergeCount == -1) {
                throw new IllegalArgumentException("both maxMergeCount and maxThreadCount must be AUTO_DETECT_MERGES_AND_THREADS");
            }
            if (maxThreadCount == -1) {
                throw new IllegalArgumentException("both maxMergeCount and maxThreadCount must be AUTO_DETECT_MERGES_AND_THREADS");
            }
            if (maxThreadCount < 1) {
                throw new IllegalArgumentException("maxThreadCount should be at least 1");
            }
            if (maxMergeCount < 1) {
                throw new IllegalArgumentException("maxMergeCount should be at least 1");
            }
            if (maxThreadCount > maxMergeCount) {
                throw new IllegalArgumentException("maxThreadCount should be <= maxMergeCount (= " + maxMergeCount + ")");
            }
            this.maxThreadCount = maxThreadCount;
            this.maxMergeCount = maxMergeCount;
        }
    }
    
    public synchronized void setDefaultMaxMergesAndThreads(final boolean spins) {
        if (spins) {
            this.maxThreadCount = 1;
            this.maxMergeCount = 6;
        }
        else {
            int coreCount = Runtime.getRuntime().availableProcessors();
            try {
                final String value = System.getProperty("lucene.cms.override_core_count");
                if (value != null) {
                    coreCount = Integer.parseInt(value);
                }
            }
            catch (final Throwable t) {}
            this.maxThreadCount = Math.max(1, Math.min(4, coreCount / 2));
            this.maxMergeCount = this.maxThreadCount + 5;
        }
    }
    
    public synchronized void setForceMergeMBPerSec(final double v) {
        this.forceMergeMBPerSec = v;
        this.updateMergeThreads();
    }
    
    public synchronized double getForceMergeMBPerSec() {
        return this.forceMergeMBPerSec;
    }
    
    public synchronized void enableAutoIOThrottle() {
        this.doAutoIOThrottle = true;
        this.targetMBPerSec = 20.0;
        this.updateMergeThreads();
    }
    
    public synchronized void disableAutoIOThrottle() {
        this.doAutoIOThrottle = false;
        this.updateMergeThreads();
    }
    
    public synchronized boolean getAutoIOThrottle() {
        return this.doAutoIOThrottle;
    }
    
    public synchronized double getIORateLimitMBPerSec() {
        if (this.doAutoIOThrottle) {
            return this.targetMBPerSec;
        }
        return Double.POSITIVE_INFINITY;
    }
    
    public synchronized int getMaxThreadCount() {
        return this.maxThreadCount;
    }
    
    public synchronized int getMaxMergeCount() {
        return this.maxMergeCount;
    }
    
    synchronized void removeMergeThread() {
        final Thread currentThread = Thread.currentThread();
        for (int i = 0; i < this.mergeThreads.size(); ++i) {
            if (this.mergeThreads.get(i) == currentThread) {
                this.mergeThreads.remove(i);
                return;
            }
        }
        assert false : "merge thread " + currentThread + " was not found";
    }
    
    protected synchronized void updateMergeThreads() {
        final List<MergeThread> activeMerges = new ArrayList<MergeThread>();
        int threadIdx = 0;
        while (threadIdx < this.mergeThreads.size()) {
            final MergeThread mergeThread = this.mergeThreads.get(threadIdx);
            if (!mergeThread.isAlive()) {
                this.mergeThreads.remove(threadIdx);
            }
            else {
                activeMerges.add(mergeThread);
                ++threadIdx;
            }
        }
        CollectionUtil.timSort(activeMerges);
        final int activeMergeCount = activeMerges.size();
        int bigMergeCount = 0;
        for (threadIdx = activeMergeCount - 1; threadIdx >= 0; --threadIdx) {
            final MergeThread mergeThread2 = activeMerges.get(threadIdx);
            if (mergeThread2.merge.estimatedMergeBytes > 5.24288E7) {
                bigMergeCount = 1 + threadIdx;
                break;
            }
        }
        final long now = System.nanoTime();
        StringBuilder message;
        if (this.verbose()) {
            message = new StringBuilder();
            message.append(String.format(Locale.ROOT, "updateMergeThreads ioThrottle=%s targetMBPerSec=%.1f MB/sec", this.doAutoIOThrottle, this.targetMBPerSec));
        }
        else {
            message = null;
        }
        for (threadIdx = 0; threadIdx < activeMergeCount; ++threadIdx) {
            final MergeThread mergeThread3 = activeMerges.get(threadIdx);
            final MergePolicy.OneMerge merge = mergeThread3.merge;
            final boolean doPause = threadIdx < bigMergeCount - this.maxThreadCount;
            double newMBPerSec;
            if (doPause) {
                newMBPerSec = 0.0;
            }
            else if (merge.maxNumSegments != -1) {
                newMBPerSec = this.forceMergeMBPerSec;
            }
            else if (!this.doAutoIOThrottle) {
                newMBPerSec = Double.POSITIVE_INFINITY;
            }
            else if (merge.estimatedMergeBytes < 5.24288E7) {
                newMBPerSec = Double.POSITIVE_INFINITY;
            }
            else {
                newMBPerSec = this.targetMBPerSec;
            }
            final double curMBPerSec = merge.rateLimiter.getMBPerSec();
            if (this.verbose()) {
                long mergeStartNS = merge.mergeStartNS;
                if (mergeStartNS == -1L) {
                    mergeStartNS = now;
                }
                message.append('\n');
                message.append(String.format(Locale.ROOT, "merge thread %s estSize=%.1f MB (written=%.1f MB) runTime=%.1fs (stopped=%.1fs, paused=%.1fs) rate=%s\n", mergeThread3.getName(), bytesToMB(merge.estimatedMergeBytes), bytesToMB(merge.rateLimiter.totalBytesWritten), nsToSec(now - mergeStartNS), nsToSec(merge.rateLimiter.getTotalStoppedNS()), nsToSec(merge.rateLimiter.getTotalPausedNS()), rateToString(merge.rateLimiter.getMBPerSec())));
                if (newMBPerSec != curMBPerSec) {
                    if (newMBPerSec == 0.0) {
                        message.append("  now stop");
                    }
                    else if (curMBPerSec == 0.0) {
                        if (newMBPerSec == Double.POSITIVE_INFINITY) {
                            message.append("  now resume");
                        }
                        else {
                            message.append(String.format(Locale.ROOT, "  now resume to %.1f MB/sec", newMBPerSec));
                        }
                    }
                    else {
                        message.append(String.format(Locale.ROOT, "  now change from %.1f MB/sec to %.1f MB/sec", curMBPerSec, newMBPerSec));
                    }
                }
                else if (curMBPerSec == 0.0) {
                    message.append("  leave stopped");
                }
                else {
                    message.append(String.format(Locale.ROOT, "  leave running at %.1f MB/sec", curMBPerSec));
                }
            }
            merge.rateLimiter.setMBPerSec(newMBPerSec);
        }
        if (this.verbose()) {
            this.message(message.toString());
        }
    }
    
    private synchronized void initDynamicDefaults(final IndexWriter writer) throws IOException {
        if (this.maxThreadCount == -1) {
            boolean spins = IOUtils.spins(writer.getDirectory());
            try {
                final String value = System.getProperty("lucene.cms.override_spins");
                if (value != null) {
                    spins = Boolean.parseBoolean(value);
                }
            }
            catch (final Throwable t) {}
            this.setDefaultMaxMergesAndThreads(spins);
            if (this.verbose()) {
                this.message("initDynamicDefaults spins=" + spins + " maxThreadCount=" + this.maxThreadCount + " maxMergeCount=" + this.maxMergeCount);
            }
        }
    }
    
    private static String rateToString(final double mbPerSec) {
        if (mbPerSec == 0.0) {
            return "stopped";
        }
        if (mbPerSec == Double.POSITIVE_INFINITY) {
            return "unlimited";
        }
        return String.format(Locale.ROOT, "%.1f MB/sec", mbPerSec);
    }
    
    @Override
    public void close() {
        this.sync();
    }
    
    public void sync() {
        boolean interrupted = false;
        try {
            while (true) {
                MergeThread toSync = null;
                synchronized (this) {
                    for (final MergeThread t : this.mergeThreads) {
                        if (t.isAlive() && t != Thread.currentThread()) {
                            toSync = t;
                            break;
                        }
                    }
                }
                if (toSync == null) {
                    break;
                }
                try {
                    toSync.join();
                }
                catch (final InterruptedException ie) {
                    interrupted = true;
                }
            }
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public synchronized int mergeThreadCount() {
        final Thread currentThread = Thread.currentThread();
        int count = 0;
        for (final MergeThread mergeThread : this.mergeThreads) {
            if (currentThread != mergeThread && mergeThread.isAlive() && !mergeThread.merge.rateLimiter.getAbort()) {
                ++count;
            }
        }
        return count;
    }
    
    @Override
    public synchronized void merge(final IndexWriter writer, final MergeTrigger trigger, final boolean newMergesFound) throws IOException {
        assert !Thread.holdsLock(writer);
        this.initDynamicDefaults(writer);
        if (trigger == MergeTrigger.CLOSING) {
            this.targetMBPerSec = 10240.0;
            this.updateMergeThreads();
        }
        if (this.verbose()) {
            this.message("now merge");
            this.message("  index: " + writer.segString());
        }
        while (this.maybeStall(writer)) {
            final MergePolicy.OneMerge merge = writer.getNextMerge();
            if (merge == null) {
                if (this.verbose()) {
                    this.message("  no more merges pending; now return");
                }
                return;
            }
            this.updateIOThrottle(merge);
            boolean success = false;
            try {
                if (this.verbose()) {
                    this.message("  consider merge " + writer.segString(merge.segments));
                }
                final MergeThread merger = this.getMergeThread(writer, merge);
                this.mergeThreads.add(merger);
                if (this.verbose()) {
                    this.message("    launch new thread [" + merger.getName() + "]");
                }
                merger.start();
                this.updateMergeThreads();
                success = true;
            }
            finally {
                if (!success) {
                    writer.mergeFinish(merge);
                }
            }
        }
    }
    
    protected synchronized boolean maybeStall(final IndexWriter writer) {
        long startStallTime = 0L;
        while (writer.hasPendingMerges() && this.mergeThreadCount() >= this.maxMergeCount) {
            if (this.mergeThreads.contains(Thread.currentThread())) {
                return false;
            }
            if (this.verbose() && startStallTime == 0L) {
                this.message("    too many merges; stalling...");
            }
            startStallTime = System.currentTimeMillis();
            this.doStall();
        }
        if (this.verbose() && startStallTime != 0L) {
            this.message("  stalled for " + (System.currentTimeMillis() - startStallTime) + " msec");
        }
        return true;
    }
    
    protected synchronized void doStall() {
        try {
            this.wait(250L);
        }
        catch (final InterruptedException ie) {
            throw new ThreadInterruptedException(ie);
        }
    }
    
    protected void doMerge(final IndexWriter writer, final MergePolicy.OneMerge merge) throws IOException {
        writer.merge(merge);
    }
    
    protected synchronized MergeThread getMergeThread(final IndexWriter writer, final MergePolicy.OneMerge merge) throws IOException {
        final MergeThread thread = new MergeThread(writer, merge);
        thread.setDaemon(true);
        thread.setName("Lucene Merge Thread #" + this.mergeThreadCount++);
        return thread;
    }
    
    protected void handleMergeException(final Directory dir, final Throwable exc) {
        throw new MergePolicy.MergeException(exc, dir);
    }
    
    void setSuppressExceptions() {
        if (this.verbose()) {
            this.message("will suppress merge exceptions");
        }
        this.suppressExceptions = true;
    }
    
    void clearSuppressExceptions() {
        if (this.verbose()) {
            this.message("will not suppress merge exceptions");
        }
        this.suppressExceptions = false;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName() + ": ");
        sb.append("maxThreadCount=").append(this.maxThreadCount).append(", ");
        sb.append("maxMergeCount=").append(this.maxMergeCount).append(", ");
        sb.append("ioThrottle=").append(this.doAutoIOThrottle);
        return sb.toString();
    }
    
    private boolean isBacklog(final long now, final MergePolicy.OneMerge merge) {
        final double mergeMB = bytesToMB(merge.estimatedMergeBytes);
        for (final MergeThread mergeThread : this.mergeThreads) {
            final long mergeStartNS = mergeThread.merge.mergeStartNS;
            if (mergeThread.isAlive() && mergeThread.merge != merge && mergeStartNS != -1L && mergeThread.merge.estimatedMergeBytes >= 5.24288E7 && nsToSec(now - mergeStartNS) > 3.0) {
                final double otherMergeMB = bytesToMB(mergeThread.merge.estimatedMergeBytes);
                final double ratio = otherMergeMB / mergeMB;
                if (ratio > 0.3 && ratio < 3.0) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    private synchronized void updateIOThrottle(final MergePolicy.OneMerge newMerge) throws IOException {
        if (!this.doAutoIOThrottle) {
            return;
        }
        final double mergeMB = bytesToMB(newMerge.estimatedMergeBytes);
        if (mergeMB < 50.0) {
            return;
        }
        final long now = System.nanoTime();
        final boolean newBacklog = this.isBacklog(now, newMerge);
        boolean curBacklog = false;
        if (!newBacklog) {
            if (this.mergeThreads.size() > this.maxThreadCount) {
                curBacklog = true;
            }
            else {
                for (final MergeThread mergeThread : this.mergeThreads) {
                    if (this.isBacklog(now, mergeThread.merge)) {
                        curBacklog = true;
                        break;
                    }
                }
            }
        }
        final double curMBPerSec = this.targetMBPerSec;
        if (newBacklog) {
            this.targetMBPerSec *= 1.2;
            if (this.targetMBPerSec > 10240.0) {
                this.targetMBPerSec = 10240.0;
            }
            if (this.verbose()) {
                if (curMBPerSec == this.targetMBPerSec) {
                    this.message(String.format(Locale.ROOT, "io throttle: new merge backlog; leave IO rate at ceiling %.1f MB/sec", this.targetMBPerSec));
                }
                else {
                    this.message(String.format(Locale.ROOT, "io throttle: new merge backlog; increase IO rate to %.1f MB/sec", this.targetMBPerSec));
                }
            }
        }
        else if (curBacklog) {
            if (this.verbose()) {
                this.message(String.format(Locale.ROOT, "io throttle: current merge backlog; leave IO rate at %.1f MB/sec", this.targetMBPerSec));
            }
        }
        else {
            this.targetMBPerSec /= 1.1;
            if (this.targetMBPerSec < 5.0) {
                this.targetMBPerSec = 5.0;
            }
            if (this.verbose()) {
                if (curMBPerSec == this.targetMBPerSec) {
                    this.message(String.format(Locale.ROOT, "io throttle: no merge backlog; leave IO rate at floor %.1f MB/sec", this.targetMBPerSec));
                }
                else {
                    this.message(String.format(Locale.ROOT, "io throttle: no merge backlog; decrease IO rate to %.1f MB/sec", this.targetMBPerSec));
                }
            }
        }
        double rate;
        if (newMerge.maxNumSegments != -1) {
            rate = this.forceMergeMBPerSec;
        }
        else {
            rate = this.targetMBPerSec;
        }
        newMerge.rateLimiter.setMBPerSec(rate);
        this.targetMBPerSecChanged();
    }
    
    protected void targetMBPerSecChanged() {
    }
    
    private static double nsToSec(final long ns) {
        return ns / 1.0E9;
    }
    
    private static double bytesToMB(final long bytes) {
        return bytes / 1024.0 / 1024.0;
    }
    
    protected class MergeThread extends Thread implements Comparable<MergeThread>
    {
        final IndexWriter writer;
        final MergePolicy.OneMerge merge;
        
        public MergeThread(final IndexWriter writer, final MergePolicy.OneMerge merge) {
            this.writer = writer;
            this.merge = merge;
        }
        
        @Override
        public int compareTo(final MergeThread other) {
            return Long.compare(other.merge.estimatedMergeBytes, this.merge.estimatedMergeBytes);
        }
        
        @Override
        public void run() {
            try {
                if (ConcurrentMergeScheduler.this.verbose()) {
                    ConcurrentMergeScheduler.this.message("  merge thread: start");
                }
                ConcurrentMergeScheduler.this.doMerge(this.writer, this.merge);
                if (ConcurrentMergeScheduler.this.verbose()) {
                    ConcurrentMergeScheduler.this.message("  merge thread: done");
                }
                try {
                    ConcurrentMergeScheduler.this.merge(this.writer, MergeTrigger.MERGE_FINISHED, true);
                }
                catch (final AlreadyClosedException ace) {}
                catch (final IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
            catch (final Throwable exc) {
                if (!(exc instanceof MergePolicy.MergeAbortedException)) {
                    if (!ConcurrentMergeScheduler.this.suppressExceptions) {
                        ConcurrentMergeScheduler.this.handleMergeException(this.writer.getDirectory(), exc);
                    }
                }
                synchronized (ConcurrentMergeScheduler.this) {
                    ConcurrentMergeScheduler.this.removeMergeThread();
                    ConcurrentMergeScheduler.this.updateMergeThreads();
                    ConcurrentMergeScheduler.this.notifyAll();
                }
            }
            finally {
                synchronized (ConcurrentMergeScheduler.this) {
                    ConcurrentMergeScheduler.this.removeMergeThread();
                    ConcurrentMergeScheduler.this.updateMergeThreads();
                    ConcurrentMergeScheduler.this.notifyAll();
                }
            }
        }
    }
}
