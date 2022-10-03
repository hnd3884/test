package org.tanukisoftware.wrapper;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

public class WrapperSimpleApp implements WrapperListener, Runnable
{
    private static WrapperPrintStream m_outInfo;
    private static WrapperPrintStream m_outError;
    private static WrapperPrintStream m_outDebug;
    private Method m_mainMethod;
    private String[] m_appArgs;
    private boolean m_mainStarted;
    private boolean m_mainComplete;
    private Integer m_mainExitCode;
    private boolean m_ignoreMainExceptions;
    private boolean m_startComplete;
    private boolean m_initFailed;
    private String m_initError;
    private boolean m_initShowUsage;
    private Throwable m_initException;
    
    protected WrapperSimpleApp(final String[] args) {
        final Class wmClass = WrapperManager.class;
        this.m_mainMethod = null;
        boolean streamsSet = false;
        if ("true".equals(System.getProperty("wrapper.use_sun_encoding"))) {
            final String sunStdoutEncoding = System.getProperty("sun.stdout.encoding");
            if (sunStdoutEncoding != null && !sunStdoutEncoding.equals(System.getProperty("file.encoding"))) {
                try {
                    WrapperSimpleApp.m_outInfo = new WrapperPrintStream(System.out, false, sunStdoutEncoding, "WrapperSimpleApp: ");
                    WrapperSimpleApp.m_outError = new WrapperPrintStream(System.out, false, sunStdoutEncoding, "WrapperSimpleApp Error: ");
                    WrapperSimpleApp.m_outDebug = new WrapperPrintStream(System.out, false, sunStdoutEncoding, "WrapperSimpleApp Debug: ");
                    streamsSet = true;
                }
                catch (final UnsupportedEncodingException e) {
                    System.out.println(WrapperManager.getRes().getString("Failed to set the encoding '{0}' when creating a WrapperPrintStream.\n Make sure the value of sun.stdout.encoding is correct.", sunStdoutEncoding));
                }
            }
        }
        if (!streamsSet) {
            WrapperSimpleApp.m_outInfo = new WrapperPrintStream(System.out, "WrapperSimpleApp: ");
            WrapperSimpleApp.m_outError = new WrapperPrintStream(System.out, "WrapperSimpleApp Error: ");
            WrapperSimpleApp.m_outDebug = new WrapperPrintStream(System.out, "WrapperSimpleApp Debug: ");
        }
        String[] appArgs;
        if (args.length < 1) {
            this.m_initFailed = true;
            this.m_initError = WrapperManager.getRes().getString("Not enough arguments.  Minimum {0} required.", "1");
            this.m_initShowUsage = true;
            appArgs = new String[0];
        }
        else {
            final String[] ar = args[0].split("/");
            String mainClassString;
            String mainMethodString;
            if (ar.length > 1) {
                mainClassString = ar[0];
                mainMethodString = ar[1];
            }
            else {
                mainClassString = args[0];
                mainMethodString = "main";
            }
            Class mainClass;
            try {
                mainClass = Class.forName(mainClassString);
            }
            catch (final ClassNotFoundException e2) {
                this.m_initFailed = true;
                this.m_initError = WrapperManager.getRes().getString("Unable to locate the class {0} : {1}", mainClassString, e2);
                this.m_initShowUsage = true;
                mainClass = null;
            }
            catch (final ExceptionInInitializerError e3) {
                this.m_initFailed = true;
                this.m_initError = WrapperManager.getRes().getString("Class {0} found but could not be initialized due to:", mainClassString);
                this.m_initException = e3;
                mainClass = null;
            }
            catch (final LinkageError e4) {
                this.m_initFailed = true;
                this.m_initError = WrapperManager.getRes().getString("Class {0} found but could not be initialized: {1}", mainClassString, e4);
                mainClass = null;
            }
            if (!this.m_initFailed) {
                try {
                    this.m_mainMethod = mainClass.getMethod(mainMethodString, String[].class);
                }
                catch (final NoSuchMethodException e5) {
                    try {
                        this.m_mainMethod = mainClass.getMethod(mainMethodString, (Class[])new Class[0]);
                    }
                    catch (final NoSuchMethodException ex) {}
                    if (this.m_mainMethod == null) {
                        this.m_initFailed = true;
                        this.m_initError = WrapperManager.getRes().getString("Unable to locate a public static {2} method in class {0} : {1}", mainClassString, e5, mainMethodString);
                    }
                }
                catch (final SecurityException e6) {
                    this.m_initFailed = true;
                    this.m_initError = WrapperManager.getRes().getString("Unable to locate a public static {2} method in class {0} : {1}", mainClassString, e6, mainMethodString);
                }
                if (!this.m_initFailed) {
                    final int modifiers = this.m_mainMethod.getModifiers();
                    if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) {
                        this.m_initFailed = true;
                        this.m_initError = WrapperManager.getRes().getString("The {1} method in class {0} must be declared public and static.", mainClassString, mainMethodString);
                    }
                }
            }
            appArgs = new String[args.length - 1];
            System.arraycopy(args, 1, appArgs, 0, appArgs.length);
        }
        WrapperManager.start(this, appArgs);
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
                    WrapperSimpleApp.m_outDebug.println(WrapperManager.getRes().getString("invoking main method"));
                    try {
                        this.m_mainMethod.invoke(null, this.m_appArgs);
                    }
                    catch (final IllegalArgumentException iae) {
                        this.m_mainMethod.invoke(null, new Object[0]);
                    }
                    finally {
                        Thread.currentThread().setPriority(10);
                    }
                    if (WrapperManager.isDebugEnabled()) {
                        WrapperSimpleApp.m_outDebug.println(WrapperManager.getRes().getString("main method completed"));
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
        WrapperSimpleApp.m_outInfo.println();
        WrapperSimpleApp.m_outError.println(WrapperManager.getRes().getString("Encountered an error running main:"));
        t.printStackTrace(WrapperSimpleApp.m_outError);
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
                WrapperSimpleApp.m_outError.println(this.m_initError);
            }
            if (this.m_initException != null) {
                this.m_initException.printStackTrace(WrapperSimpleApp.m_outError);
            }
            if (this.m_initShowUsage) {
                this.showUsage();
            }
            return new Integer(1);
        }
        final boolean waitForStartMain = WrapperSystemPropertyUtil.getBooleanProperty(WrapperSimpleApp.class.getName() + ".waitForStartMain", false);
        this.m_ignoreMainExceptions = WrapperSystemPropertyUtil.getBooleanProperty(WrapperSimpleApp.class.getName() + ".ignoreMainExceptions", false);
        int maxStartMainWait = WrapperSystemPropertyUtil.getIntProperty(WrapperSimpleApp.class.getName() + ".maxStartMainWait", 2);
        maxStartMainWait = Math.max(1, maxStartMainWait);
        int maxLoops;
        if (waitForStartMain) {
            maxLoops = Integer.MAX_VALUE;
            if (WrapperManager.isDebugEnabled()) {
                WrapperSimpleApp.m_outDebug.println(WrapperManager.getRes().getString("start(args) Will wait indefinitely for the main method to complete."));
            }
        }
        else {
            maxLoops = maxStartMainWait;
            if (WrapperManager.isDebugEnabled()) {
                WrapperSimpleApp.m_outDebug.println(WrapperManager.getRes().getString("start(args) Will wait up to {0} seconds for the main method to complete.", new Integer(maxLoops)));
            }
        }
        final Thread mainThread = new Thread(this, "WrapperSimpleAppMain");
        synchronized (this) {
            this.m_appArgs = args;
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
                WrapperSimpleApp.m_outDebug.println(WrapperManager.getRes().getString("start(args) end.  Main Completed={0}, exitCode={1}", new Boolean(this.m_mainComplete), this.m_mainExitCode));
            }
            return this.m_mainExitCode;
        }
    }
    
    public int stop(final int exitCode) {
        if (WrapperManager.isDebugEnabled()) {
            WrapperSimpleApp.m_outDebug.println(WrapperManager.getRes().getString("stop({0})", new Integer(exitCode)));
        }
        return exitCode;
    }
    
    public void controlEvent(final int event) {
        if (event == 202 && (WrapperManager.isLaunchedAsService() || WrapperManager.isIgnoreUserLogoffs())) {
            WrapperSimpleApp.m_outInfo.println(WrapperManager.getRes().getString("User logged out.  Ignored."));
        }
        else {
            if (WrapperManager.isDebugEnabled()) {
                WrapperSimpleApp.m_outDebug.println(WrapperManager.getRes().getString("controlEvent({0}) Stopping", new Integer(event)));
            }
            WrapperManager.stop(0);
        }
    }
    
    protected void showUsage() {
        System.out.println();
        System.out.println(WrapperManager.getRes().getString("WrapperSimpleApp Usage:"));
        System.out.println(WrapperManager.getRes().getString("  java org.tanukisoftware.wrapper.WrapperSimpleApp {app_class{/app_method}} [app_arguments]"));
        System.out.println();
        System.out.println(WrapperManager.getRes().getString("Where:"));
        System.out.println(WrapperManager.getRes().getString("  app_class:      The fully qualified class name of the application to run."));
        System.out.println(WrapperManager.getRes().getString("  app_arguments:  The arguments that would normally be passed to the"));
        System.out.println(WrapperManager.getRes().getString("                  application."));
    }
    
    public static void main(final String[] args) {
        new WrapperSimpleApp(args);
    }
}
