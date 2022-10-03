package org.tanukisoftware.wrapper;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.io.IOException;
import java.util.jar.JarFile;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

public class WrapperJarApp implements WrapperListener, Runnable
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
    
    protected WrapperJarApp(final String[] args) {
        final Class wmClass = WrapperManager.class;
        boolean streamsSet = false;
        if ("true".equals(System.getProperty("wrapper.use_sun_encoding"))) {
            final String sunStdoutEncoding = System.getProperty("sun.stdout.encoding");
            if (sunStdoutEncoding != null && !sunStdoutEncoding.equals(System.getProperty("file.encoding"))) {
                try {
                    WrapperJarApp.m_outInfo = new WrapperPrintStream(System.out, false, sunStdoutEncoding, "WrapperJarApp: ");
                    WrapperJarApp.m_outError = new WrapperPrintStream(System.out, false, sunStdoutEncoding, "WrapperJarApp Error: ");
                    WrapperJarApp.m_outDebug = new WrapperPrintStream(System.out, false, sunStdoutEncoding, "WrapperJarApp Debug: ");
                    streamsSet = true;
                }
                catch (final UnsupportedEncodingException e) {
                    System.out.println(WrapperManager.getRes().getString("Failed to set the encoding '{0}' when creating a WrapperPrintStream.\n Make sure the value of sun.stdout.encoding is correct.", sunStdoutEncoding));
                }
            }
        }
        if (!streamsSet) {
            WrapperJarApp.m_outInfo = new WrapperPrintStream(System.out, "WrapperJarApp: ");
            WrapperJarApp.m_outError = new WrapperPrintStream(System.out, "WrapperJarApp Error: ");
            WrapperJarApp.m_outDebug = new WrapperPrintStream(System.out, "WrapperJarApp Debug: ");
        }
        String[] appArgs;
        if (args.length < 1) {
            this.m_initFailed = true;
            this.m_initError = WrapperManager.getRes().getString("Not enough arguments.  Minimum {0} required.", "1");
            this.m_initShowUsage = true;
            appArgs = new String[0];
        }
        else {
            final File file = new File(args[0]);
            if (!file.exists()) {
                this.m_initFailed = true;
                this.m_initError = WrapperManager.getRes().getString("Unable to locate the jar file {0}", args[0]);
                this.m_initShowUsage = true;
            }
            else {
                final File parent = file.getParentFile();
                JarFile jarFile;
                try {
                    jarFile = new JarFile(file);
                }
                catch (final IOException e2) {
                    this.m_initFailed = true;
                    this.m_initError = WrapperManager.getRes().getString("Unable to open the jar file {0} : {1}", args[0], e2);
                    jarFile = null;
                }
                if (!this.m_initFailed) {
                    Manifest manifest;
                    try {
                        manifest = jarFile.getManifest();
                    }
                    catch (final IOException e3) {
                        this.m_initFailed = true;
                        this.m_initError = WrapperManager.getRes().getString("Unable to access the jar''s manifest file {0} : {1}", args[0], e3);
                        manifest = null;
                    }
                    if (!this.m_initFailed) {
                        final Attributes attributes = manifest.getMainAttributes();
                        final String mainClassName = attributes.getValue("Main-Class");
                        if (mainClassName == null) {
                            this.m_initFailed = true;
                            this.m_initError = WrapperManager.getRes().getString("The Main-Class was not specified correctly in the jar file''s manifest file.  Please make sure all required meta information is being set.");
                        }
                        else {
                            final String classPath = attributes.getValue("Class-Path");
                            if (WrapperManager.isDebugEnabled()) {
                                WrapperJarApp.m_outDebug.println("Jar Main-Class: " + mainClassName);
                            }
                            URL[] classURLs;
                            if (classPath != null && !classPath.equals("")) {
                                if (WrapperManager.isDebugEnabled()) {
                                    WrapperJarApp.m_outDebug.println(WrapperManager.getRes().getString("Jar Classpath: {0}", classPath));
                                }
                                final StringTokenizer st = new StringTokenizer(classPath, " \n\r");
                                classURLs = new URL[st.countTokens() + 1];
                                try {
                                    classURLs[0] = new URL("file:" + file.getAbsolutePath());
                                }
                                catch (final MalformedURLException e4) {
                                    this.m_initFailed = true;
                                    this.m_initError = WrapperManager.getRes().getString("Unable to add jar to classpath: {0}", e4);
                                }
                                if (!this.m_initFailed) {
                                    if (WrapperManager.isDebugEnabled()) {
                                        WrapperJarApp.m_outDebug.println(WrapperManager.getRes().getString("    Classpath[0]=") + classURLs[0]);
                                    }
                                    int i = 1;
                                    while (st.hasMoreTokens()) {
                                        final String classEntry = st.nextToken();
                                        try {
                                            classURLs[i] = new URL("file:" + new File(parent, classEntry).getAbsolutePath());
                                        }
                                        catch (final MalformedURLException e5) {
                                            this.m_initFailed = true;
                                            this.m_initError = WrapperManager.getRes().getString("Malformed classpath in the jar''s manifest file {0} : {1}", args[0], e5);
                                        }
                                        if (!this.m_initFailed && WrapperManager.isDebugEnabled()) {
                                            WrapperJarApp.m_outDebug.println(WrapperManager.getRes().getString("    Classpath[{0}]=", new Integer(i)) + classURLs[i]);
                                        }
                                        ++i;
                                    }
                                }
                            }
                            else {
                                if (WrapperManager.isDebugEnabled()) {
                                    WrapperJarApp.m_outDebug.println(WrapperManager.getRes().getString("Jar Classpath: Not specified."));
                                }
                                classURLs = new URL[] { null };
                                try {
                                    classURLs[0] = new URL("file:" + file.getAbsolutePath());
                                }
                                catch (final MalformedURLException e6) {
                                    this.m_initFailed = true;
                                    this.m_initError = WrapperManager.getRes().getString("Unable to add jar to classpath: {0}", e6);
                                }
                                if (!this.m_initFailed && WrapperManager.isDebugEnabled()) {
                                    WrapperJarApp.m_outDebug.println(WrapperManager.getRes().getString("    Classpath[0]=") + classURLs[0]);
                                }
                            }
                            if (!this.m_initFailed) {
                                final URLClassLoader cl = URLClassLoader.newInstance(classURLs, this.getClass().getClassLoader());
                                Class mainClass;
                                try {
                                    mainClass = Class.forName(mainClassName, true, cl);
                                }
                                catch (final ClassNotFoundException e7) {
                                    this.m_initFailed = true;
                                    this.m_initError = WrapperManager.getRes().getString("Unable to locate the class {0} : {1}", mainClassName, e7);
                                    mainClass = null;
                                }
                                catch (final ExceptionInInitializerError e8) {
                                    this.m_initFailed = true;
                                    this.m_initError = WrapperManager.getRes().getString("Class {0} found but could not be initialized due to:", mainClassName);
                                    this.m_initException = e8;
                                    mainClass = null;
                                }
                                catch (final LinkageError e9) {
                                    this.m_initFailed = true;
                                    this.m_initError = WrapperManager.getRes().getString("Class {0} found but could not be initialized: {1}", mainClassName, e9);
                                    mainClass = null;
                                }
                                if (!this.m_initFailed) {
                                    try {
                                        this.m_mainMethod = mainClass.getMethod("main", String[].class);
                                    }
                                    catch (final NoSuchMethodException e10) {
                                        this.m_initFailed = true;
                                        this.m_initError = WrapperManager.getRes().getString("Unable to locate a public static main method in class {0} : {1}", args[0], e10);
                                    }
                                    catch (final SecurityException e11) {
                                        this.m_initFailed = true;
                                        this.m_initError = WrapperManager.getRes().getString("Unable to locate a public static main method in class {0} : {1}", args[0], e11);
                                    }
                                    if (!this.m_initFailed) {
                                        final int modifiers = this.m_mainMethod.getModifiers();
                                        if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) {
                                            this.m_initFailed = true;
                                            this.m_initError = WrapperManager.getRes().getString("The main method in class {0} must be declared public and static.", args[0]);
                                        }
                                    }
                                }
                            }
                        }
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
            if (WrapperManager.isDebugEnabled()) {
                WrapperJarApp.m_outDebug.println(WrapperManager.getRes().getString("invoking main method"));
            }
            try {
                this.m_mainMethod.invoke(null, this.m_appArgs);
            }
            finally {
                Thread.currentThread().setPriority(10);
            }
            if (WrapperManager.isDebugEnabled()) {
                WrapperJarApp.m_outDebug.println(WrapperManager.getRes().getString("main method completed"));
            }
            synchronized (this) {
                this.m_mainComplete = true;
                this.notifyAll();
            }
            return;
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
        WrapperJarApp.m_outInfo.println();
        WrapperJarApp.m_outError.println(WrapperManager.getRes().getString("Encountered an error running main:"));
        t.printStackTrace(WrapperJarApp.m_outError);
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
                WrapperJarApp.m_outError.println(this.m_initError);
            }
            if (this.m_initException != null) {
                this.m_initException.printStackTrace(WrapperJarApp.m_outError);
            }
            if (this.m_initShowUsage) {
                this.showUsage();
            }
            return new Integer(1);
        }
        final boolean waitForStartMain = WrapperSystemPropertyUtil.getBooleanProperty(WrapperJarApp.class.getName() + ".waitForStartMain", false);
        this.m_ignoreMainExceptions = WrapperSystemPropertyUtil.getBooleanProperty(WrapperJarApp.class.getName() + ".ignoreMainExceptions", false);
        int maxStartMainWait = WrapperSystemPropertyUtil.getIntProperty(WrapperJarApp.class.getName() + ".maxStartMainWait", 2);
        maxStartMainWait = Math.max(1, maxStartMainWait);
        int maxLoops;
        if (waitForStartMain) {
            maxLoops = Integer.MAX_VALUE;
            if (WrapperManager.isDebugEnabled()) {
                WrapperJarApp.m_outDebug.println(WrapperManager.getRes().getString("start(args) Will wait indefinitely for the main method to complete."));
            }
        }
        else {
            maxLoops = maxStartMainWait;
            if (WrapperManager.isDebugEnabled()) {
                WrapperJarApp.m_outDebug.println(WrapperManager.getRes().getString("start(args) Will wait up to {0} seconds for the main method to complete.", new Integer(maxLoops)));
            }
        }
        final Thread mainThread = new Thread(this, "WrapperJarAppMain");
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
                WrapperJarApp.m_outDebug.println(WrapperManager.getRes().getString("start(args) end.  Main Completed={0}, exitCode={1}", new Boolean(this.m_mainComplete), this.m_mainExitCode));
            }
            return this.m_mainExitCode;
        }
    }
    
    public int stop(final int exitCode) {
        if (WrapperManager.isDebugEnabled()) {
            WrapperJarApp.m_outDebug.println("stop(" + exitCode + ")");
        }
        return exitCode;
    }
    
    public void controlEvent(final int event) {
        if (event == 202 && (WrapperManager.isLaunchedAsService() || WrapperManager.isIgnoreUserLogoffs())) {
            WrapperJarApp.m_outInfo.println(WrapperManager.getRes().getString("User logged out.  Ignored."));
        }
        else {
            if (WrapperManager.isDebugEnabled()) {
                WrapperJarApp.m_outDebug.println(WrapperManager.getRes().getString("controlEvent({0}) Stopping", new Integer(event)));
            }
            WrapperManager.stop(0);
        }
    }
    
    protected void showUsage() {
        System.out.println();
        System.out.println(WrapperManager.getRes().getString("WrapperJarApp Usage:"));
        System.out.println(WrapperManager.getRes().getString("  java org.tanukisoftware.wrapper.WrapperJarApp {jar_file} [app_arguments]"));
        System.out.println();
        System.out.println(WrapperManager.getRes().getString("Where:"));
        System.out.println(WrapperManager.getRes().getString("  jar_file:       The jar file to run."));
        System.out.println(WrapperManager.getRes().getString("  app_arguments:  The arguments that would normally be passed to the"));
        System.out.println(WrapperManager.getRes().getString("                  application."));
    }
    
    public static void main(final String[] args) {
        new WrapperJarApp(args);
    }
}
