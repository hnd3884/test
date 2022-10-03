package org.tanukisoftware.wrapper;

import java.lang.reflect.Modifier;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

public class WrapperStartStopApp implements WrapperListener, Runnable
{
    private static WrapperPrintStream m_outInfo;
    private static WrapperPrintStream m_outError;
    private static WrapperPrintStream m_outDebug;
    private Method m_startMainMethod;
    private String[] m_startMainArgs;
    private Method m_stopMainMethod;
    private boolean m_stopWait;
    private String[] m_stopMainArgs;
    private boolean m_mainStarted;
    private boolean m_mainComplete;
    private Integer m_mainExitCode;
    private boolean m_ignoreMainExceptions;
    private boolean m_startComplete;
    private boolean m_initFailed;
    private String m_initError;
    private boolean m_initShowUsage;
    private Throwable m_initException;
    private final String PASSTHROUGH_MODE_IGNORE = "ignore";
    private final String PASSTHROUGH_MODE_BOTH = "both";
    private final String PASSTHROUGH_MODE_START = "start";
    private final String PASSTHROUGH_MODE_STOP = "stop";
    
    protected WrapperStartStopApp(final String[] args) {
        final Class wmClass = WrapperManager.class;
        boolean streamsSet = false;
        if ("true".equals(System.getProperty("wrapper.use_sun_encoding"))) {
            final String sunStdoutEncoding = System.getProperty("sun.stdout.encoding");
            if (sunStdoutEncoding != null && !sunStdoutEncoding.equals(System.getProperty("file.encoding"))) {
                try {
                    WrapperStartStopApp.m_outInfo = new WrapperPrintStream(System.out, false, sunStdoutEncoding, "WrapperStartStopApp: ");
                    WrapperStartStopApp.m_outError = new WrapperPrintStream(System.out, false, sunStdoutEncoding, "WrapperStartStopApp Error: ");
                    WrapperStartStopApp.m_outDebug = new WrapperPrintStream(System.out, false, sunStdoutEncoding, "WrapperStartStopApp Debug: ");
                    streamsSet = true;
                }
                catch (final UnsupportedEncodingException e) {
                    System.out.println(WrapperManager.getRes().getString("Failed to set the encoding '{0}' when creating a WrapperPrintStream.\n Make sure the value of sun.stdout.encoding is correct.", sunStdoutEncoding));
                }
            }
        }
        if (!streamsSet) {
            WrapperStartStopApp.m_outInfo = new WrapperPrintStream(System.out, "WrapperStartStopApp: ");
            WrapperStartStopApp.m_outError = new WrapperPrintStream(System.out, "WrapperStartStopApp Error: ");
            WrapperStartStopApp.m_outDebug = new WrapperPrintStream(System.out, "WrapperStartStopApp Debug: ");
        }
        String[] startArgs;
        if (args.length < 5) {
            this.m_initFailed = true;
            this.m_initError = WrapperManager.getRes().getString("Not enough arguments.  Minimum {0} required.", "5");
            this.m_initShowUsage = true;
            startArgs = new String[0];
        }
        else {
            this.m_startMainMethod = this.getMainMethod(args[0]);
            int argCount = this.getArgCount(args, 1);
            if (argCount < 0) {
                startArgs = new String[0];
            }
            else {
                startArgs = this.getArgs(args, 1, argCount);
                if (startArgs == null) {
                    startArgs = new String[0];
                }
                else {
                    final int stopArgBase = 2 + startArgs.length;
                    if (args.length < stopArgBase + 3) {
                        this.m_initFailed = true;
                        this.m_initError = WrapperManager.getRes().getString("Not enough arguments. Minimum 3 after start arguments.");
                        this.m_initShowUsage = true;
                    }
                    else {
                        this.m_stopMainMethod = this.getMainMethod(args[stopArgBase]);
                        if (args[stopArgBase + 1].equalsIgnoreCase("true")) {
                            this.m_stopWait = true;
                        }
                        else if (args[stopArgBase + 1].equalsIgnoreCase("false")) {
                            this.m_stopWait = false;
                        }
                        else {
                            this.m_initFailed = true;
                            this.m_initError = WrapperManager.getRes().getString("The stop_wait argument must be either true or false.");
                            this.m_initShowUsage = true;
                        }
                        if (!this.m_initFailed) {
                            argCount = this.getArgCount(args, stopArgBase + 2);
                            if (argCount >= 0) {
                                this.m_stopMainArgs = this.getArgs(args, stopArgBase + 2, argCount);
                                if (this.m_stopMainArgs != null) {
                                    final int expectedSize = stopArgBase + 2 + argCount + 1;
                                    if (expectedSize < args.length) {
                                        final String passthroughMode = WrapperSystemPropertyUtil.getStringProperty(WrapperStartStopApp.class.getName() + ".passthroughMode", "ignore");
                                        if (passthroughMode.equalsIgnoreCase("both")) {
                                            startArgs = this.addPassthroughParams(startArgs, args, expectedSize);
                                            this.m_stopMainArgs = this.addPassthroughParams(this.m_stopMainArgs, args, expectedSize);
                                        }
                                        else if (passthroughMode.equalsIgnoreCase("start")) {
                                            startArgs = this.addPassthroughParams(startArgs, args, expectedSize);
                                        }
                                        else if (passthroughMode.equalsIgnoreCase("stop")) {
                                            this.m_stopMainArgs = this.addPassthroughParams(this.m_stopMainArgs, args, expectedSize);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        WrapperManager.start(this, startArgs);
    }
    
    protected WrapperStartStopApp(final Method startMainMethod, final Method stopMainMethod, final boolean stopWait, final String[] stopMainArgs) {
        this.m_startMainMethod = startMainMethod;
        this.m_stopMainMethod = stopMainMethod;
        this.m_stopWait = stopWait;
        this.m_stopMainArgs = stopMainArgs;
    }
    
    public void run() {
        synchronized (this) {
            this.m_mainStarted = true;
            this.notifyAll();
        }
        Throwable t = null;
        try {
            while (true) {
                if (WrapperManager.isDebugEnabled()) {
                    WrapperStartStopApp.m_outDebug.println(WrapperManager.getRes().getString("invoking start main method"));
                    try {
                        this.m_startMainMethod.invoke(null, this.m_startMainArgs);
                    }
                    catch (final IllegalArgumentException iae) {
                        this.m_startMainMethod.invoke(null, new Object[0]);
                    }
                    finally {
                        Thread.currentThread().setPriority(10);
                    }
                    if (WrapperManager.isDebugEnabled()) {
                        WrapperStartStopApp.m_outDebug.println(WrapperManager.getRes().getString("start main method completed"));
                    }
                    synchronized (this) {
                        this.m_mainComplete = true;
                        this.notifyAll();
                    }
                    return;
                }
                continue;
            }
        }
        catch (final IllegalAccessException e) {
            t = e;
        }
        catch (final IllegalArgumentException e2) {
            t = e2;
        }
        catch (final InvocationTargetException e3) {
            t = e3.getTargetException();
            if (t == null) {
                t = e3;
            }
        }
        WrapperStartStopApp.m_outInfo.println();
        WrapperStartStopApp.m_outError.println(WrapperManager.getRes().getString("Encountered an error running start main: {0}", t));
        t.printStackTrace(WrapperStartStopApp.m_outError);
        synchronized (this) {
            if (this.m_ignoreMainExceptions) {
                if (!this.m_startComplete) {
                    this.m_mainComplete = true;
                    this.notifyAll();
                }
                return;
            }
            if (this.m_startComplete) {
                WrapperManager.stop(1);
                return;
            }
            this.m_mainComplete = true;
            this.m_mainExitCode = new Integer(1);
            this.notifyAll();
        }
    }
    
    public Integer start(final String[] args) {
        if (this.m_initFailed) {
            if (this.m_initError != null) {
                WrapperStartStopApp.m_outError.println(this.m_initError);
            }
            if (this.m_initException != null) {
                this.m_initException.printStackTrace(WrapperStartStopApp.m_outError);
            }
            if (this.m_initShowUsage) {
                this.showUsage();
            }
            return new Integer(1);
        }
        final boolean waitForStartMain = WrapperSystemPropertyUtil.getBooleanProperty(WrapperStartStopApp.class.getName() + ".waitForStartMain", false);
        this.m_ignoreMainExceptions = WrapperSystemPropertyUtil.getBooleanProperty(WrapperStartStopApp.class.getName() + ".ignoreMainExceptions", false);
        int maxStartMainWait = WrapperSystemPropertyUtil.getIntProperty(WrapperStartStopApp.class.getName() + ".maxStartMainWait", 2);
        maxStartMainWait = Math.max(1, maxStartMainWait);
        int maxLoops;
        if (waitForStartMain) {
            maxLoops = Integer.MAX_VALUE;
            if (WrapperManager.isDebugEnabled()) {
                WrapperStartStopApp.m_outDebug.println(WrapperManager.getRes().getString("start(args) Will wait indefinitely for the main method to complete."));
            }
        }
        else {
            maxLoops = maxStartMainWait;
            if (WrapperManager.isDebugEnabled()) {
                WrapperStartStopApp.m_outDebug.println(WrapperManager.getRes().getString("start(args) Will wait up to {0} seconds for the main method to complete.", new Integer(maxLoops)));
            }
        }
        final Thread mainThread = new Thread(this, "WrapperStartStopAppMain");
        synchronized (this) {
            this.m_startMainArgs = args;
            mainThread.start();
            Thread.currentThread().setPriority(10);
            while (!this.m_mainStarted) {
                try {
                    this.wait(1000L);
                }
                catch (final InterruptedException e) {}
            }
            for (int loops = 0; loops < maxLoops && !this.m_mainComplete; ++loops) {
                try {
                    this.wait(1000L);
                }
                catch (final InterruptedException ex) {}
                if (!this.m_mainComplete) {
                    WrapperManager.signalStarting(5000);
                }
            }
            this.m_startComplete = true;
            if (WrapperManager.isDebugEnabled()) {
                WrapperStartStopApp.m_outDebug.println(WrapperManager.getRes().getString("start(args) end.  Main Completed={0}, exitCode={1}", new Boolean(this.m_mainComplete), this.m_mainExitCode));
            }
            return this.m_mainExitCode;
        }
    }
    
    public int stop(final int exitCode) {
        if (WrapperManager.isDebugEnabled()) {
            WrapperStartStopApp.m_outDebug.println(WrapperManager.getRes().getString("stop({0})", new Integer(exitCode)));
        }
        Throwable t = null;
        try {
            if (WrapperManager.isDebugEnabled()) {
                WrapperStartStopApp.m_outDebug.println(WrapperManager.getRes().getString("invoking stop main method"));
            }
            try {
                this.m_stopMainMethod.invoke(null, this.m_stopMainArgs);
            }
            catch (final IllegalArgumentException iae) {
                this.m_stopMainMethod.invoke(null, new Object[0]);
            }
            if (WrapperManager.isDebugEnabled()) {
                WrapperStartStopApp.m_outDebug.println(WrapperManager.getRes().getString("stop main method completed"));
            }
            if (this.m_stopWait) {
                int systemThreadCount = WrapperSystemPropertyUtil.getIntProperty(WrapperStartStopApp.class.getName() + ".systemThreadCount", 1);
                systemThreadCount = Math.max(0, systemThreadCount);
                int threadCnt;
                while ((threadCnt = this.getNonDaemonThreadCount()) > systemThreadCount) {
                    if (WrapperManager.isDebugEnabled()) {
                        WrapperStartStopApp.m_outDebug.println(WrapperManager.getRes().getString("stopping.  Waiting for {0} threads to complete.", new Integer(threadCnt - systemThreadCount)));
                    }
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (final InterruptedException e) {}
                }
            }
            return exitCode;
        }
        catch (final IllegalAccessException e2) {
            t = e2;
        }
        catch (final IllegalArgumentException e3) {
            t = e3;
        }
        catch (final InvocationTargetException e4) {
            t = e4;
        }
        WrapperStartStopApp.m_outError.println(WrapperManager.getRes().getString("Encountered an error running stop main: {0}", t));
        t.printStackTrace(WrapperStartStopApp.m_outError);
        return 1;
    }
    
    public void controlEvent(final int event) {
        if (event == 202 && (WrapperManager.isLaunchedAsService() || WrapperManager.isIgnoreUserLogoffs())) {
            WrapperStartStopApp.m_outInfo.println(WrapperManager.getRes().getString("User logged out.  Ignored."));
        }
        else {
            if (WrapperManager.isDebugEnabled()) {
                WrapperStartStopApp.m_outDebug.println(WrapperManager.getRes().getString("controlEvent({0}) Stopping", new Integer(event)));
            }
            WrapperManager.stop(0);
        }
    }
    
    private int getNonDaemonThreadCount() {
        ThreadGroup topGroup;
        for (topGroup = Thread.currentThread().getThreadGroup(); topGroup.getParent() != null; topGroup = topGroup.getParent()) {}
        final Thread[] threads = new Thread[topGroup.activeCount() * 2];
        topGroup.enumerate(threads, true);
        int liveCount = 0;
        for (int i = 0; i < threads.length; ++i) {
            if (threads[i] != null && threads[i].isAlive() && Thread.currentThread() != threads[i] && !threads[i].isDaemon()) {
                ++liveCount;
            }
        }
        return liveCount;
    }
    
    private Method getMainMethod(String className) {
        String methodName = "main";
        final String[] arr = className.split("/");
        if (arr.length > 1) {
            className = arr[0];
            methodName = arr[1];
        }
        Class mainClass;
        try {
            mainClass = Class.forName(className);
        }
        catch (final ClassNotFoundException e) {
            this.m_initFailed = true;
            this.m_initError = WrapperManager.getRes().getString("Unable to locate the class {0}: {1}", className, e);
            this.m_initShowUsage = true;
            return null;
        }
        catch (final ExceptionInInitializerError e2) {
            this.m_initFailed = true;
            this.m_initError = WrapperManager.getRes().getString("Class {0} found but could not be initialized due to:", className);
            this.m_initException = e2;
            return null;
        }
        catch (final LinkageError e3) {
            this.m_initFailed = true;
            this.m_initError = WrapperManager.getRes().getString("Class {0} found but could not be initialized: {1}", className, e3);
            return null;
        }
        Method mainMethod = null;
        try {
            mainMethod = mainClass.getMethod(methodName, String[].class);
        }
        catch (final NoSuchMethodException e4) {
            try {
                mainMethod = mainClass.getMethod(methodName, (Class[])new Class[0]);
            }
            catch (final NoSuchMethodException ex) {}
            if (mainMethod == null) {
                this.m_initFailed = true;
                this.m_initError = WrapperManager.getRes().getString("Unable to locate a public static {2} method in class {0}: {1}", className, e4, methodName);
                return null;
            }
        }
        catch (final SecurityException e5) {
            this.m_initFailed = true;
            this.m_initError = WrapperManager.getRes().getString("Unable to locate a public static {2} method in class {0}: {1}", className, e5, methodName);
            return null;
        }
        final int modifiers = mainMethod.getModifiers();
        if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) {
            this.m_initFailed = true;
            this.m_initError = WrapperManager.getRes().getString("The {1} method in class {0} must be declared public and static.", className, methodName);
            return null;
        }
        return mainMethod;
    }
    
    private String[] getArgs(final String[] args, final int argBase, final int argCount) {
        if (argCount < 0) {
            this.m_initFailed = true;
            this.m_initError = WrapperManager.getRes().getString("Illegal argument count: {0}", args[argBase]);
            this.m_initShowUsage = true;
            return null;
        }
        if (args.length < argBase + 1 + argCount) {
            this.m_initFailed = true;
            this.m_initError = WrapperManager.getRes().getString("Not enough arguments.  Argument count of {0} was specified.", new Integer(argCount));
            this.m_initShowUsage = true;
            return null;
        }
        final String[] mainArgs = new String[argCount];
        System.arraycopy(args, argBase + 1, mainArgs, 0, argCount);
        return mainArgs;
    }
    
    private int getArgCount(final String[] args, final int argBase) {
        int argCount;
        try {
            argCount = Integer.parseInt(args[argBase]);
        }
        catch (final NumberFormatException e) {
            this.m_initFailed = true;
            this.m_initError = WrapperManager.getRes().getString("Illegal argument count: {0}", args[argBase]);
            this.m_initShowUsage = true;
            return -1;
        }
        return argCount;
    }
    
    private String[] addPassthroughParams(final String[] source, final String[] extra, final int position) {
        if (extra != null && extra.length != 0 && position < extra.length) {
            final int numberExtraElements = extra.length - position;
            int sizeSource = 0;
            if (source != null) {
                sizeSource = source.length;
            }
            final String[] result = new String[sizeSource + numberExtraElements];
            for (int i = 0; i < sizeSource; ++i) {
                result[i] = source[i];
            }
            for (int i = 0; i < numberExtraElements; ++i) {
                result[sizeSource + i] = extra[position + i];
            }
            return result;
        }
        if (source == null) {
            return new String[0];
        }
        return source;
    }
    
    protected void showUsage() {
        System.out.println();
        System.out.println(WrapperManager.getRes().getString("WrapperStartStopApp Usage:"));
        System.out.println(WrapperManager.getRes().getString("  java org.tanukisoftware.wrapper.WrapperStartStopApp {start_class{/start_method}} {start_arg_count} [start_arguments] {stop_class{/stop_method}} {stop_wait} {stop_arg_count} [stop_arguments]"));
        System.out.println();
        System.out.println(WrapperManager.getRes().getString("Where:"));
        System.out.println(WrapperManager.getRes().getString("  start_class:     The fully qualified class name to run to start the "));
        System.out.println(WrapperManager.getRes().getString("                   application."));
        System.out.println(WrapperManager.getRes().getString("  start_arg_count: The number of arguments to be passed to the start class''s "));
        System.out.println(WrapperManager.getRes().getString("                   main method."));
        System.out.println(WrapperManager.getRes().getString("  start_arguments: The arguments that would normally be passed to the start "));
        System.out.println(WrapperManager.getRes().getString("                   class application."));
        System.out.println(WrapperManager.getRes().getString("  stop_class:      The fully qualified class name to run to stop the "));
        System.out.println(WrapperManager.getRes().getString("                   application."));
        System.out.println(WrapperManager.getRes().getString("  stop_wait:       When stopping, should the Wrapper wait for all threads to "));
        System.out.println(WrapperManager.getRes().getString("                   complete before exiting (true/false)."));
        System.out.println(WrapperManager.getRes().getString("  stop_arg_count:  The number of arguments to be passed to the stop class''s "));
        System.out.println(WrapperManager.getRes().getString("                   main method."));
        System.out.println(WrapperManager.getRes().getString("  stop_arguments:  The arguments that would normally be passed to the stop "));
        System.out.println(WrapperManager.getRes().getString("                   class application."));
    }
    
    public static void main(final String[] args) {
        new WrapperStartStopApp(args);
    }
}
