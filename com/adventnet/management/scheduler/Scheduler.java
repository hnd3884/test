package com.adventnet.management.scheduler;

import java.util.Enumeration;
import java.util.Date;
import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

public class Scheduler extends Thread
{
    private static int indexCount;
    private static Vector allSchedulerVector;
    private int MAX_THREADS;
    private int index;
    private String descriptor;
    int idleThreads;
    int activeThreads;
    private static int DEFAULT_MAX_THREADS;
    static int TOTAL_THREADS;
    static boolean STOP_ALL;
    boolean STOP_THIS;
    int NUM_THREADS_STOPPED;
    private Vector runnables;
    private Vector times;
    private Vector workers;
    Vector ready_tasks;
    private static Hashtable schedulers;
    private static Vector threadNames;
    private static Hashtable maxThreads;
    private static String confFile;
    private static boolean readConfFile;
    static boolean SUSPEND_ALL;
    private static int STOP_TIME_OUT;
    private boolean timeReverted;
    private long oldWrongTime;
    private long diffTime;
    private long scheduleAdjuster;
    private boolean adjForReversion;
    private int runnableControlCount;
    private long waitTime;
    
    public static Vector getSchedulerList() {
        return Scheduler.allSchedulerVector;
    }
    
    public int getMaxThreads() {
        return this.MAX_THREADS;
    }
    
    public static int getDefaultMaxThreads() {
        return Scheduler.DEFAULT_MAX_THREADS;
    }
    
    public static void setDefaultMaxThreads(final int default_MAX_THREADS) {
        if (default_MAX_THREADS >= 0 && default_MAX_THREADS < 100) {
            Scheduler.DEFAULT_MAX_THREADS = default_MAX_THREADS;
        }
    }
    
    public boolean setMaxThreads(final int n) {
        if (this.isAlive()) {
            return false;
        }
        if (n >= 0 && n < 100) {
            this.MAX_THREADS = n;
            this.idleThreads = n;
            return true;
        }
        return false;
    }
    
    public static int getTotalThreads() {
        return Scheduler.TOTAL_THREADS;
    }
    
    public static String getConfFile() {
        return Scheduler.confFile;
    }
    
    public static void setConfFile(final String confFile) {
        Scheduler.confFile = confFile;
    }
    
    private static InputStream openFile(final File file) throws IOException {
        InputStream resourceAsStream;
        if (System.getProperty("JavaWebStart") != null) {
            System.out.println("Java Web Start mode in Scheduler: " + file);
            resourceAsStream = Scheduler.class.getClassLoader().getResourceAsStream(file.getName());
        }
        else {
            resourceAsStream = new FileInputStream(file);
        }
        return resourceAsStream;
    }
    
    private static synchronized void readTheConfFile() {
        if (Scheduler.readConfFile) {
            return;
        }
        try {
            final File file = new File(Scheduler.confFile);
            if (!file.exists()) {
                Scheduler.readConfFile = true;
                return;
            }
            String line;
            while ((line = new BufferedReader(new InputStreamReader(openFile(file))).readLine()) != null) {
                if (line.trim().equals("")) {
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;
                }
                final StringTokenizer stringTokenizer = new StringTokenizer(line);
                if (stringTokenizer.countTokens() == 2) {
                    final String nextToken = stringTokenizer.nextToken();
                    int int1 = -1;
                    try {
                        int1 = Integer.parseInt(stringTokenizer.nextToken());
                    }
                    catch (final NumberFormatException ex) {}
                    if (int1 < 0 || int1 > 100) {
                        System.err.println(" Invalid line in the conf file " + Scheduler.confFile + " :" + line);
                    }
                    else {
                        Scheduler.maxThreads.put(nextToken, new Integer(int1));
                    }
                }
                else {
                    System.err.println(" Invalid line in the conf file " + Scheduler.confFile + " :" + line);
                }
            }
        }
        catch (final IOException ex2) {
            System.err.println("Scheduler  File read Error:" + Scheduler.confFile + ": " + ex2);
        }
        catch (final SecurityException ex3) {}
        Scheduler.readConfFile = true;
    }
    
    public static Scheduler createScheduler(final String s) {
        return createScheduler(s, -1);
    }
    
    public static Scheduler createScheduler(final String s, final int n) {
        synchronized (Scheduler.schedulers) {
            if (s == null) {
                return null;
            }
            final Scheduler scheduler = getScheduler(s);
            if (scheduler != null) {
                return scheduler;
            }
            final int noOfThreadsSpecified = getNoOfThreadsSpecified(s);
            Scheduler scheduler2;
            if (n <= 0 || n > 100) {
                if (noOfThreadsSpecified != -1) {
                    scheduler2 = new Scheduler(s, noOfThreadsSpecified);
                }
                else {
                    scheduler2 = new Scheduler(s);
                }
            }
            else {
                scheduler2 = new Scheduler(s, n);
            }
            System.out.println("Instantiated " + scheduler2.getName() + " scheduler with " + scheduler2.MAX_THREADS + " threads ");
            return scheduler2;
        }
    }
    
    public static int getNoOfThreadsSpecified(final String s) {
        readTheConfFile();
        final Integer n = Scheduler.maxThreads.get(s);
        if (n != null) {
            return n;
        }
        return -1;
    }
    
    public static Scheduler getScheduler(final String s) {
        if (s == null) {
            return null;
        }
        return Scheduler.schedulers.get(s);
    }
    
    protected Scheduler(final String descriptor, final int max_THREADS) {
        super(descriptor);
        this.MAX_THREADS = 4;
        this.index = 0;
        this.descriptor = "";
        this.idleThreads = 4;
        this.activeThreads = 0;
        this.STOP_THIS = false;
        this.NUM_THREADS_STOPPED = 0;
        this.runnables = new Vector();
        this.times = new Vector();
        this.workers = new Vector();
        this.ready_tasks = new Vector();
        this.timeReverted = false;
        this.oldWrongTime = 0L;
        this.diffTime = 0L;
        this.scheduleAdjuster = 0L;
        this.adjForReversion = true;
        this.runnableControlCount = 2000;
        this.waitTime = 10L;
        this.descriptor = descriptor;
        this.index = getNextIndex();
        this.MAX_THREADS = max_THREADS;
        this.idleThreads = this.MAX_THREADS;
        Scheduler.schedulers.put(descriptor, this);
        addToSchedulerVector(descriptor, this);
    }
    
    protected Scheduler(final String s) {
        this(s, Scheduler.DEFAULT_MAX_THREADS);
    }
    
    private static synchronized int getNextIndex() {
        return ++Scheduler.indexCount;
    }
    
    public synchronized void scheduleTask(final Runnable runnable, final Date date) {
        long time = 0L;
        if (date != null) {
            time = date.getTime();
        }
        this.scheduleTask(runnable, time);
    }
    
    public synchronized void scheduleTask(final Runnable runnable, long currentTimeMillis) {
        if (currentTimeMillis <= 0L) {
            currentTimeMillis = System.currentTimeMillis();
        }
        if (this.timeReverted) {
            if (System.currentTimeMillis() < this.scheduleAdjuster) {
                if (currentTimeMillis >= this.oldWrongTime) {
                    currentTimeMillis -= this.diffTime;
                }
            }
            else {
                this.timeReverted = false;
            }
        }
        for (int i = 0; i < this.times.size(); ++i) {
            if ((long)this.times.elementAt(i) > currentTimeMillis) {
                this.times.insertElementAt(new Long(currentTimeMillis), i);
                this.runnables.insertElementAt(runnable, i);
                return;
            }
        }
        this.times.addElement(new Long(currentTimeMillis));
        this.runnables.addElement(runnable);
        this.notifyAll();
    }
    
    public synchronized void removeTask(final Runnable runnable) {
        this.ready_tasks.removeElement(runnable);
        if (runnable == null) {
            return;
        }
        for (int i = 0; i < this.runnables.size(); ++i) {
            final Runnable runnable2 = this.runnables.elementAt(i);
            if (runnable.equals(runnable2)) {
                this.runnables.removeElement(runnable2);
                this.times.removeElementAt(i);
                --i;
            }
        }
    }
    
    public static boolean isSuspended() {
        return Scheduler.SUSPEND_ALL;
    }
    
    public static boolean suspendAll() {
        return Scheduler.SUSPEND_ALL = true;
    }
    
    public static boolean resumeAll() {
        Scheduler.SUSPEND_ALL = false;
        final Enumeration elements = Scheduler.schedulers.elements();
        while (elements.hasMoreElements()) {
            wakeUpScheduler((Scheduler)elements.nextElement());
        }
        return true;
    }
    
    public static boolean stopAll() {
        synchronized (Scheduler.schedulers) {
            if (Scheduler.STOP_ALL) {
                return false;
            }
            int n = 0;
            Scheduler.STOP_ALL = true;
            resumeAll();
            final int total_THREADS = Scheduler.TOTAL_THREADS;
            while (Scheduler.TOTAL_THREADS > 0) {
                if (n >= Scheduler.STOP_TIME_OUT) {
                    System.err.println("Schedulers did not stop properly: " + (total_THREADS - Scheduler.TOTAL_THREADS) + " threads stopped out of " + total_THREADS);
                    System.err.println("The remaining " + Scheduler.TOTAL_THREADS + " threads did not stop in " + Scheduler.STOP_TIME_OUT + " seconds ");
                    return false;
                }
                try {
                    Thread.sleep(1000L);
                    ++n;
                }
                catch (final Exception ex) {
                    System.out.println("Exception while stopping the Schedulers" + ex.getMessage());
                }
            }
            System.out.println(total_THREADS - Scheduler.TOTAL_THREADS + " of the " + total_THREADS + " active threads in the control " + " of  the schedulers stopped");
            Scheduler.TOTAL_THREADS = 0;
            return true;
        }
    }
    
    private static void wakeUpScheduler(final Scheduler scheduler) {
        scheduler.wakeUp();
        final Vector workers = scheduler.workers;
        for (int i = 0; i < workers.size(); ++i) {
            ((WorkerThread)workers.elementAt(i)).wakeUp();
        }
    }
    
    public boolean stopThis() {
        int n = 0;
        this.STOP_THIS = true;
        wakeUpScheduler(this);
        while (this.NUM_THREADS_STOPPED < this.MAX_THREADS) {
            if (n >= Scheduler.STOP_TIME_OUT) {
                System.err.println("Scheduler:" + this.getName() + " did not stop properly: " + this.NUM_THREADS_STOPPED + " threads stopped out of " + this.MAX_THREADS);
                System.err.println("The remaining " + (this.MAX_THREADS - this.NUM_THREADS_STOPPED) + " threads of scheduler:" + this.getName() + "did not stop in " + Scheduler.STOP_TIME_OUT + " seconds ");
                return false;
            }
            try {
                Thread.sleep(1000L);
                ++n;
            }
            catch (final Exception ex) {
                System.out.println("Exception while stopping the Scheduler " + this.getName() + "  " + ex.getMessage());
            }
        }
        System.out.println(this.NUM_THREADS_STOPPED + "out of " + this.MAX_THREADS + " active threads stopped in " + " Scheduler:" + this.getName());
        return true;
    }
    
    public boolean cleanUp() {
        if (this.isAlive()) {
            return false;
        }
        synchronized (Scheduler.schedulers) {
            this.times.removeAllElements();
            this.runnables.removeAllElements();
            this.ready_tasks.removeAllElements();
            this.workers.removeAllElements();
            Scheduler.schedulers.remove(this.getName());
            return true;
        }
    }
    
    public static void setStopTimeout(final int stop_TIME_OUT) {
        Scheduler.STOP_TIME_OUT = stop_TIME_OUT;
    }
    
    protected synchronized Runnable getTheWork() {
        while (this.times.size() == 0) {
            try {
                this.wait(10L);
                if (Scheduler.STOP_ALL || this.STOP_THIS) {
                    return null;
                }
                continue;
            }
            catch (final InterruptedException ex) {}
        }
        final long longValue = this.times.firstElement();
        if (longValue > System.currentTimeMillis()) {
            return null;
        }
        final Runnable runnable = this.runnables.firstElement();
        this.runnables.removeElement(runnable);
        this.times.removeElement(new Long(longValue));
        return runnable;
    }
    
    public void setAdjustForTimeReversion(final boolean adjForReversion) {
        this.adjForReversion = adjForReversion;
    }
    
    public boolean getAdjustForTimeReversion() {
        return this.adjForReversion;
    }
    
    public void run() {
        synchronized (Scheduler.schedulers) {
            Scheduler.STOP_ALL = false;
            this.STOP_THIS = false;
        }
        this.startWorkers();
        if (this.MAX_THREADS == 0) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        while (!Scheduler.STOP_ALL && !this.STOP_THIS) {
            final long currentTimeMillis2 = System.currentTimeMillis();
            if (currentTimeMillis - currentTimeMillis2 > 120000L && this.adjForReversion) {
                this.diffTime = currentTimeMillis - currentTimeMillis2;
                this.scheduleAdjuster = currentTimeMillis2 + 1000L;
                this.timeReverted = true;
                this.oldWrongTime = currentTimeMillis;
                this.adjustForTimeReversion(this.diffTime);
            }
            currentTimeMillis = currentTimeMillis2;
            if (Scheduler.SUSPEND_ALL) {
                while (Scheduler.SUSPEND_ALL) {
                    try {
                        if (this.waitAndIntimateStopping(10L)) {
                            break;
                        }
                        continue;
                    }
                    catch (final InterruptedException ex) {}
                }
            }
            try {
                while (this.ready_tasks.size() > this.runnableControlCount) {
                    this.waitAndIntimateStopping(this.waitTime);
                }
                final Runnable theWork = this.getTheWork();
                if (theWork == null) {
                    try {
                        this.waitAndIntimateStopping(10L);
                    }
                    catch (final InterruptedException ex2) {}
                }
                else {
                    this.startTask(theWork);
                }
            }
            catch (final Exception ex3) {
                System.err.println("Exception scheduling task in scheduler:" + this.getName() + " " + ex3);
                ex3.printStackTrace();
            }
        }
    }
    
    protected synchronized void adjustForTimeReversion(final long n) {
        synchronized (this.times) {
            for (int i = 0; i < this.times.size(); ++i) {
                this.times.setElementAt(new Long((long)this.times.elementAt(i) - n), i);
            }
        }
    }
    
    protected synchronized boolean waitAndIntimateStopping(final long n) throws InterruptedException {
        try {
            this.wait(n);
            if (Scheduler.STOP_ALL || this.STOP_THIS) {
                return true;
            }
        }
        catch (final InterruptedException ex) {}
        return false;
    }
    
    private synchronized void wakeUp() {
        this.notifyAll();
    }
    
    private synchronized void startTask(final Runnable runnable) {
        this.ready_tasks.addElement(runnable);
        for (int i = 0; i < this.workers.size(); ++i) {
            ((WorkerThread)this.workers.elementAt(i)).wakeUp();
        }
    }
    
    synchronized Runnable getNextTask() {
        if (this.ready_tasks.size() == 0) {
            return null;
        }
        final Runnable runnable = this.ready_tasks.firstElement();
        this.ready_tasks.removeElement(runnable);
        return runnable;
    }
    
    private synchronized void startWorkers() {
        if (Scheduler.STOP_ALL || this.STOP_THIS) {
            return;
        }
        this.workers = new Vector();
        for (int i = 0; i < this.MAX_THREADS; ++i) {
            ++Scheduler.TOTAL_THREADS;
            final WorkerThread workerThread = new WorkerThread(this, this.getName() + "-" + (i + 1));
            this.workers.addElement(workerThread);
            workerThread.start();
        }
    }
    
    public void deregisterThisScheduler(final String s) {
        if (s == null) {
            return;
        }
        Scheduler.schedulers.remove(s).stopThis();
    }
    
    private static void addToSchedulerVector(final String s, final Scheduler scheduler) {
        Scheduler.threadNames.addElement(s);
        Scheduler.allSchedulerVector.addElement(scheduler);
    }
    
    public static Vector getAllSchedulerNames() {
        return Scheduler.threadNames;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public String getDescriptor() {
        return this.descriptor;
    }
    
    public int getNumTasks() {
        return this.runnables.size();
    }
    
    public int getNumThreads() {
        return this.MAX_THREADS;
    }
    
    public int getActiveThreads() {
        return this.activeThreads;
    }
    
    public int getIdleThreads() {
        return this.idleThreads;
    }
    
    public int getRunnableControlCount() {
        return this.runnableControlCount;
    }
    
    public void setRunnableControlCount(final int runnableControlCount) {
        this.runnableControlCount = runnableControlCount;
    }
    
    public long getWaitTime() {
        return this.waitTime;
    }
    
    public void setWaitTime(final long waitTime) {
        this.waitTime = waitTime;
    }
    
    static {
        Scheduler.indexCount = 0;
        Scheduler.allSchedulerVector = new Vector();
        Scheduler.DEFAULT_MAX_THREADS = 4;
        Scheduler.TOTAL_THREADS = 0;
        Scheduler.STOP_ALL = false;
        Scheduler.schedulers = new Hashtable(15);
        Scheduler.threadNames = new Vector(15);
        Scheduler.maxThreads = new Hashtable(15);
        Scheduler.confFile = null;
        try {
            Scheduler.confFile = "conf/threads.conf";
        }
        catch (final Throwable t) {
            Scheduler.confFile = "";
        }
        Scheduler.readConfFile = false;
        Scheduler.SUSPEND_ALL = false;
        Scheduler.STOP_TIME_OUT = 15;
    }
}
