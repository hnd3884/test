package org.tanukisoftware.wrapper;

import org.tanukisoftware.wrapper.event.WrapperTickEvent;
import java.util.HashMap;
import java.net.SocketTimeoutException;
import org.tanukisoftware.wrapper.event.WrapperSecondInvocationEvent;
import org.tanukisoftware.wrapper.event.WrapperLogFileChangedEvent;
import org.tanukisoftware.wrapper.event.WrapperServiceResumeEvent;
import org.tanukisoftware.wrapper.event.WrapperServicePauseEvent;
import org.tanukisoftware.wrapper.event.WrapperServiceActionEvent;
import org.tanukisoftware.wrapper.event.WrapperServiceControlEvent;
import java.io.DataInputStream;
import org.tanukisoftware.wrapper.event.WrapperPingEvent;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.net.SocketException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.tanukisoftware.wrapper.event.WrapperControlEvent;
import java.security.AccessControlException;
import java.security.AllPermission;
import java.util.Iterator;
import org.tanukisoftware.wrapper.security.WrapperEventPermission;
import org.tanukisoftware.wrapper.event.WrapperEventListener;
import org.tanukisoftware.wrapper.security.WrapperServicePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import org.tanukisoftware.wrapper.security.WrapperPermission;
import java.security.Permission;
import org.tanukisoftware.wrapper.security.WrapperUserEventPermission;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.tanukisoftware.wrapper.event.WrapperEvent;
import org.tanukisoftware.wrapper.jmx.WrapperManagerTesting;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.List;
import java.io.File;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.io.PrintStream;

public final class WrapperManager implements Runnable
{
    private static final String WRAPPER_CONNECTION_THREAD_NAME = "Wrapper-Connection";
    private static final int DEFAULT_PORT = 15003;
    private static final int DEFAULT_CPU_TIMEOUT = 10000;
    private static final int TICK_MS = 100;
    private static final int TIMER_FAST_THRESHOLD = 1728000;
    private static final int TIMER_SLOW_THRESHOLD = 1728000;
    private static final int BACKEND_TYPE_UNKNOWN = 0;
    private static final int BACKEND_TYPE_SOCKET_V4 = 1;
    private static final int BACKEND_TYPE_SOCKET_V6 = 2;
    private static final int BACKEND_TYPE_PIPE = 4;
    private static final byte WRAPPER_MSG_START = 100;
    private static final byte WRAPPER_MSG_STOP = 101;
    private static final byte WRAPPER_MSG_RESTART = 102;
    private static final byte WRAPPER_MSG_PING = 103;
    private static final byte WRAPPER_MSG_STOP_PENDING = 104;
    private static final byte WRAPPER_MSG_START_PENDING = 105;
    private static final byte WRAPPER_MSG_STARTED = 106;
    private static final byte WRAPPER_MSG_STOPPED = 107;
    private static final byte WRAPPER_MSG_KEY = 110;
    private static final byte WRAPPER_MSG_BADKEY = 111;
    private static final byte WRAPPER_MSG_LOW_LOG_LEVEL = 112;
    private static final byte WRAPPER_MSG_PING_TIMEOUT = 113;
    private static final byte WRAPPER_MSG_SERVICE_CONTROL_CODE = 114;
    private static final byte WRAPPER_MSG_PROPERTIES = 115;
    private static final byte WRAPPER_MSG_LOG = 116;
    private static final byte WRAPPER_MSG_CHILD_LAUNCH = -124;
    private static final byte WRAPPER_MSG_CHILD_TERM = -123;
    private static final byte WRAPPER_MSG_LOGFILE = -122;
    private static final byte WRAPPER_MSG_CHECK_DEADLOCK = -121;
    private static final byte WRAPPER_MSG_DEADLOCK = -120;
    private static final byte WRAPPER_MSG_APPEAR_ORPHAN = -119;
    private static final byte WRAPPER_MSG_PAUSE = -118;
    private static final byte WRAPPER_MSG_RESUME = -117;
    private static final byte WRAPPER_MSG_GC = -116;
    private static final byte WRAPPER_MSG_FIRE_USER_EVENT = -115;
    private static final byte WRAPPER_MSG_SECOND_INVOCATION_EVENT = -114;
    public static final int WRAPPER_CTRL_C_EVENT = 200;
    public static final int WRAPPER_CTRL_CLOSE_EVENT = 201;
    public static final int WRAPPER_CTRL_LOGOFF_EVENT = 202;
    public static final int WRAPPER_CTRL_SHUTDOWN_EVENT = 203;
    public static final int WRAPPER_CTRL_TERM_EVENT = 204;
    public static final int WRAPPER_CTRL_HUP_EVENT = 205;
    public static final int WRAPPER_CTRL_USR1_EVENT = 206;
    public static final int WRAPPER_CTRL_USR2_EVENT = 207;
    public static final int WRAPPER_LOG_LEVEL_DEBUG = 1;
    public static final int WRAPPER_LOG_LEVEL_INFO = 2;
    public static final int WRAPPER_LOG_LEVEL_STATUS = 3;
    public static final int WRAPPER_LOG_LEVEL_WARN = 4;
    public static final int WRAPPER_LOG_LEVEL_ERROR = 5;
    public static final int WRAPPER_LOG_LEVEL_FATAL = 6;
    public static final int WRAPPER_LOG_LEVEL_ADVICE = 7;
    public static final int WRAPPER_LOG_LEVEL_NOTICE = 8;
    public static final int SERVICE_CONTROL_CODE_START = 65536;
    public static final int SERVICE_CONTROL_CODE_STOP = 1;
    public static final int SERVICE_CONTROL_CODE_PAUSE = 2;
    public static final int SERVICE_CONTROL_CODE_CONTINUE = 3;
    public static final int SERVICE_CONTROL_CODE_INTERROGATE = 4;
    public static final int SERVICE_CONTROL_CODE_SHUTDOWN = 5;
    public static final int SERVICE_CONTROL_CODE_POWEREVENT_QUERYSUSPEND = 3328;
    public static final int SERVICE_CONTROL_CODE_POWEREVENT_QUERYSUSPENDFAILED = 3330;
    public static final int SERVICE_CONTROL_CODE_POWEREVENT_SUSPEND = 3332;
    public static final int SERVICE_CONTROL_CODE_POWEREVENT_RESUMECRITICAL = 3334;
    public static final int SERVICE_CONTROL_CODE_POWEREVENT_RESUMESUSPEND = 3335;
    public static final int SERVICE_CONTROL_CODE_POWEREVENT_BATTERYLOW = 3337;
    public static final int SERVICE_CONTROL_CODE_POWEREVENT_POWERSTATUSCHANGE = 3338;
    public static final int SERVICE_CONTROL_CODE_POWEREVENT_OEMEVENT = 3339;
    public static final int SERVICE_CONTROL_CODE_POWEREVENT_RESUMEAUTOMATIC = 3346;
    private static PrintStream m_out;
    private static PrintStream m_err;
    private static WrapperPrintStream m_outInfo;
    private static WrapperPrintStream m_outError;
    private static WrapperPrintStream m_outDebug;
    private static String m_os;
    private static String m_arch;
    private static boolean m_windows;
    private static boolean m_macosx;
    private static boolean m_aix;
    private static boolean m_zos;
    private static boolean m_securityManagerChecked;
    private static boolean m_disposed;
    private static boolean m_starting;
    private static boolean m_started;
    private static WrapperManager m_instance;
    private static Thread m_hook;
    private static boolean m_hookTriggered;
    private static boolean m_hookRemoveFailed;
    private static boolean m_shutdownJVMComplete;
    private static Map m_shutdownLockMap;
    private static int m_shutdownLocks;
    private static int m_runningExecs;
    private static String[] m_args;
    private static int m_backendType;
    private static boolean m_backendConnected;
    private static OutputStream m_backendOS;
    private static InputStream m_backendIS;
    private static int m_port;
    private static int m_jvmPort;
    private static int m_jvmPortMin;
    private static int m_jvmPortMax;
    private static String m_wrapperPortAddress;
    private static String m_key;
    private static int m_soTimeout;
    private static long m_cpuTimeout;
    private static int m_startedTicks;
    private static int m_lowLogLevel;
    private static boolean m_ignoreSignals;
    private static boolean m_detachStarted;
    private static Thread m_commRunner;
    private static boolean m_commRunnerStarted;
    private static Thread m_eventRunner;
    private static int m_eventRunnerTicks;
    private static Thread m_startupRunner;
    private static boolean m_useSystemTime;
    private static int m_timerFastThreshold;
    private static int m_timerSlowThreshold;
    private static boolean m_disableTests;
    private static boolean m_listenerForceStop;
    private static int m_jvmBits;
    private static volatile int m_ticks;
    private static WrapperListener m_listener;
    private static int m_lastPingTicks;
    private static Socket m_backendSocket;
    private static boolean m_appearHung;
    private static int m_slowSeconds;
    private static boolean m_ignoreUserLogoffs;
    private static boolean m_service;
    private static boolean m_debug;
    private static boolean m_logFinalizer;
    private static int m_jvmId;
    private static boolean m_stoppingInit;
    private static boolean m_stopping;
    private static Thread m_stoppingThread;
    private static boolean m_stopped;
    private static String m_pendingStopMessage;
    private static int m_exitCode;
    private static boolean m_libraryOK;
    private static boolean m_libraryLoaded;
    private static boolean m_libraryVersionOk;
    private static boolean m_wrapperVersionOk;
    private static byte[] m_commandBuffer;
    private static File m_logFile;
    private static WrapperProperties m_properties;
    private static List m_wrapperEventListenerMaskList;
    private static WrapperEventListenerMask[] m_wrapperEventListenerMasks;
    private static boolean m_produceCoreEvents;
    private static WrapperResources m_res;
    private static boolean m_professionalEdition;
    private static boolean m_standardEdition;
    private static char PROPERTY_SEPARATOR;
    private static byte[] m_backendReadBuffer;
    static /* synthetic */ Class class$org$tanukisoftware$wrapper$WrapperManager;
    
    public static WrapperResources getRes() {
        return WrapperManager.m_res;
    }
    
    private static void logPackageInfo(final Class clazz) {
        if (WrapperManager.m_debug) {
            final Package pkg = WrapperManager.class.getPackage();
            if (pkg == null) {
                WrapperManager.m_outDebug.println(getRes().getString("{0} package not found.", clazz.getName()));
            }
            else {
                WrapperManager.m_outDebug.println(getRes().getString("{0} package information:", clazz.getName()));
                WrapperManager.m_outDebug.println(getRes().getString("  Implementation Title: {0}", pkg.getImplementationTitle()));
                WrapperManager.m_outDebug.println(getRes().getString("  Implementation Vendor: {0}", pkg.getImplementationVendor()));
                WrapperManager.m_outDebug.println(getRes().getString("  Implementation Version: {0}", pkg.getImplementationVersion()));
                WrapperManager.m_outDebug.println(getRes().getString("  Is Sealed?: {0}", pkg.isSealed() ? getRes().getString("True") : getRes().getString("False")));
            }
            final ProtectionDomain proDom = clazz.getProtectionDomain();
            WrapperManager.m_outDebug.println(getRes().getString("{0} protection domain:", clazz.getName()));
            final CodeSource codeSource = proDom.getCodeSource();
            final URL jarLocation = codeSource.getLocation();
            WrapperManager.m_outDebug.println(getRes().getString("  Location: {0}", jarLocation));
            try {
                final InputStream is = jarLocation.openStream();
                try {
                    int jarSize = 0;
                    final MessageDigest md = MessageDigest.getInstance("MD5");
                    int data;
                    while ((data = is.read()) >= 0) {
                        ++jarSize;
                        md.update((byte)(data & 0xFF));
                    }
                    WrapperManager.m_outDebug.println(getRes().getString("    Size: {0}", new Integer(jarSize)));
                    final byte[] bytes = md.digest();
                    final StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < bytes.length; ++i) {
                        final String val = Integer.toString(bytes[i] & 0xFF, 16).toLowerCase();
                        if (val.length() == 1) {
                            sb.append("0");
                        }
                        sb.append(val);
                    }
                    WrapperManager.m_outDebug.println(getRes().getString("    MD5: {0}", sb));
                }
                finally {
                    is.close();
                }
            }
            catch (final NoSuchAlgorithmException e) {
                WrapperManager.m_outDebug.println(getRes().getString("    Unable to calculate MD5: {0}", e));
            }
            catch (final IOException e2) {
                WrapperManager.m_outDebug.println(getRes().getString("    Unable to access location: {0}", e2));
            }
        }
    }
    
    private static void privilegedClassInit() {
        WrapperManager.m_out = System.out;
        WrapperManager.m_err = System.err;
        boolean streamsSet = false;
        if ("true".equals(System.getProperty("wrapper.use_sun_encoding"))) {
            final String sunStdoutEncoding = System.getProperty("sun.stdout.encoding");
            if (sunStdoutEncoding != null && !sunStdoutEncoding.equals(System.getProperty("file.encoding"))) {
                try {
                    WrapperManager.m_outInfo = new WrapperPrintStream(WrapperManager.m_out, false, sunStdoutEncoding, "WrapperManager: ");
                    WrapperManager.m_outError = new WrapperPrintStream(WrapperManager.m_out, false, sunStdoutEncoding, "WrapperManager Error: ");
                    WrapperManager.m_outDebug = new WrapperPrintStream(WrapperManager.m_out, false, sunStdoutEncoding, "WrapperManager Debug: ");
                    streamsSet = true;
                }
                catch (final UnsupportedEncodingException e) {
                    System.out.println("Failed to set the encoding '" + sunStdoutEncoding + "' when creating a WrapperPrintStream.\n Make sure the value of sun.stdout.encoding is correct.");
                }
            }
        }
        if (!streamsSet) {
            WrapperManager.m_outInfo = new WrapperPrintStream(WrapperManager.m_out, "WrapperManager: ");
            WrapperManager.m_outError = new WrapperPrintStream(WrapperManager.m_out, "WrapperManager Error: ");
            WrapperManager.m_outDebug = new WrapperPrintStream(WrapperManager.m_out, "WrapperManager Debug: ");
        }
        (WrapperManager.m_properties = new WrapperProperties()).lock();
        WrapperManager.m_res = new WrapperResources();
        checkSecurityManager();
        WrapperManager.m_debug = WrapperSystemPropertyUtil.getBooleanProperty("wrapper.debug", false);
        if (WrapperManager.m_debug) {
            WrapperManager.m_outDebug.println("WrapperManager class initialized by thread: " + Thread.currentThread().getName() + "   Using classloader: " + WrapperManager.class.getClassLoader().toString());
        }
        WrapperManager.m_outInfo.println("Initializing...");
        WrapperManager.m_key = System.getProperty("wrapper.key");
        WrapperManager.m_logFinalizer = WrapperSystemPropertyUtil.getBooleanProperty("wrapper.logFinalizers", false);
        WrapperManager.m_jvmId = WrapperSystemPropertyUtil.getIntProperty("wrapper.jvmid", 1);
        if (WrapperManager.m_debug) {
            WrapperManager.m_outDebug.println("JVM #" + WrapperManager.m_jvmId);
        }
        WrapperManager.m_jvmBits = WrapperSystemPropertyUtil.getIntProperty("sun.arch.data.model", -1);
        if (WrapperManager.m_jvmBits == -1) {
            WrapperManager.m_jvmBits = WrapperSystemPropertyUtil.getIntProperty("com.ibm.vm.bitmode", -1);
        }
        if (WrapperManager.m_debug) {
            if (WrapperManager.m_jvmBits > 0) {
                WrapperManager.m_outDebug.println("Running a " + WrapperManager.m_jvmBits + "-bit JVM.");
            }
            else {
                WrapperManager.m_outDebug.println("The bit depth of this JVM could not be determined.");
            }
        }
        logPackageInfo(WrapperManager.class);
        WrapperManager.m_detachStarted = WrapperSystemPropertyUtil.getBooleanProperty("wrapper.detachStarted", false);
        WrapperManager.m_ticks = 2147483447;
        WrapperManager.m_useSystemTime = WrapperSystemPropertyUtil.getBooleanProperty("wrapper.use_system_time", false);
        WrapperManager.m_timerFastThreshold = WrapperSystemPropertyUtil.getIntProperty("wrapper.timer_fast_threshold", 1728000) * 1000 / 100;
        WrapperManager.m_timerSlowThreshold = WrapperSystemPropertyUtil.getIntProperty("wrapper.timer_slow_threshold", 1728000) * 1000 / 100;
        WrapperManager.m_disableTests = WrapperSystemPropertyUtil.getBooleanProperty("wrapper.disable_tests", false);
        final boolean disableShutdownHook = WrapperSystemPropertyUtil.getBooleanProperty("wrapper.disable_shutdown_hook", false);
        WrapperManager.m_listenerForceStop = WrapperSystemPropertyUtil.getBooleanProperty("wrapper.listener.force_stop", false);
        WrapperManager.m_soTimeout = WrapperSystemPropertyUtil.getIntProperty("wrapper.backend.so_timeout", -1) * 1000;
        if (!disableShutdownHook) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println("Registering shutdown hook");
            }
            WrapperManager.m_hook = new Thread("Wrapper-Shutdown-Hook") {
                public void run() {
                    WrapperManager.m_hookTriggered = true;
                    if (WrapperManager.m_debug) {
                        WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("ShutdownHook started"));
                    }
                    WrapperManager.m_startupRunner = null;
                    WrapperManager.stop(0);
                    if (WrapperManager.m_debug) {
                        WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("ShutdownHook complete"));
                    }
                    if (WrapperManager.m_debug) {
                        WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("WrapperManager stopped due to {0}", WrapperManager.getRes().getString("Shutdown Hook")));
                    }
                    WrapperManager.m_stopped = true;
                }
            };
            Runtime.getRuntime().addShutdownHook(WrapperManager.m_hook);
        }
        WrapperManager.m_backendType = 0;
        WrapperManager.m_port = 0;
        WrapperManager.m_jvmPort = 0;
        WrapperManager.m_jvmPortMin = 0;
        WrapperManager.m_jvmPortMax = 0;
        if (WrapperManager.m_key == null) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println("Not using the Wrapper.  (key not specified)");
            }
            WrapperManager.m_service = false;
            WrapperManager.m_cpuTimeout = 31557600000L;
        }
        else {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Using the Wrapper"));
            }
            if (WrapperSystemPropertyUtil.getBooleanProperty("wrapper.disable_console_input", false)) {
                System.setIn(new WrapperInputStream());
            }
            final String backendType = WrapperSystemPropertyUtil.getStringProperty("wrapper.backend", "SOCKET");
            if (backendType.equalsIgnoreCase("PIPE")) {
                WrapperManager.m_backendType = 4;
            }
            else {
                if (backendType.equalsIgnoreCase("SOCKET")) {
                    WrapperManager.m_backendType = 1;
                }
                else {
                    WrapperManager.m_backendType = 2;
                }
                if ((WrapperManager.m_wrapperPortAddress = System.getProperty("wrapper.port.address")) == null) {
                    if (WrapperManager.m_backendType == 1) {
                        WrapperManager.m_wrapperPortAddress = "127.0.0.1";
                    }
                    else {
                        WrapperManager.m_wrapperPortAddress = "::1";
                    }
                }
                final String sPort;
                if ((sPort = System.getProperty("wrapper.port")) == null) {
                    final String msg = "The 'wrapper.port' system property was not set.";
                    WrapperManager.m_outError.println(msg);
                    throw new ExceptionInInitializerError(msg);
                }
                try {
                    WrapperManager.m_port = Integer.parseInt(sPort);
                }
                catch (final NumberFormatException e2) {
                    final String msg2 = "'" + sPort + "' is not a valid value for 'wrapper.port'.";
                    WrapperManager.m_outError.println(msg2);
                    throw new ExceptionInInitializerError(msg2);
                }
                WrapperManager.m_jvmPort = WrapperSystemPropertyUtil.getIntProperty("wrapper.jvm.port", -1);
                WrapperManager.m_jvmPortMin = WrapperSystemPropertyUtil.getIntProperty("wrapper.jvm.port.min", 31000);
                WrapperManager.m_jvmPortMax = WrapperSystemPropertyUtil.getIntProperty("wrapper.jvm.port.max", 31999);
            }
            WrapperManager.m_ignoreSignals = WrapperSystemPropertyUtil.getBooleanProperty("wrapper.ignore_signals", false);
            WrapperManager.m_service = WrapperSystemPropertyUtil.getBooleanProperty("wrapper.service", false);
            final String sCPUTimeout = System.getProperty("wrapper.cpu.timeout");
            if (sCPUTimeout == null) {
                WrapperManager.m_cpuTimeout = 10000L;
            }
            else {
                try {
                    WrapperManager.m_cpuTimeout = Integer.parseInt(sCPUTimeout) * 1000L;
                }
                catch (final NumberFormatException e2) {
                    final String msg2 = "'" + sCPUTimeout + "' is not a valid value for 'wrapper.cpu.timeout'.";
                    WrapperManager.m_outError.println(msg2);
                    throw new ExceptionInInitializerError(msg2);
                }
            }
        }
        if (WrapperSystemPropertyUtil.getBooleanProperty(WrapperManager.class.getName() + ".mbean", true)) {
            registerMBean(new org.tanukisoftware.wrapper.jmx.WrapperManager(), "org.tanukisoftware.wrapper:type=WrapperManager");
        }
        if (WrapperSystemPropertyUtil.getBooleanProperty(WrapperManager.class.getName() + ".mbean.testing", false)) {
            registerMBean(new WrapperManagerTesting(), "org.tanukisoftware.wrapper:type=WrapperManagerTesting");
        }
        initializeNativeLibrary();
        if (isNativeLibraryOk()) {
            try {
                System.setProperty("wrapper.java.pid", Integer.toString(nativeGetJavaPID()));
            }
            catch (final Throwable e3) {
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(getRes().getString("Call to nativeGetJavaPID() failed: {0}", e3));
                }
            }
        }
        WrapperManager.m_eventRunnerTicks = getTicks();
        (WrapperManager.m_eventRunner = new Thread("Wrapper-Control-Event-Monitor") {
            public void run() {
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("Control event monitor thread started."));
                }
                try {
                    final WrapperTickEventImpl tickEvent = new WrapperTickEventImpl();
                    int lastTickOffset = 0;
                    boolean first = true;
                    boolean stoppingLogged = false;
                    while (!WrapperManager.m_stopped) {
                        int offsetDiff;
                        if (!WrapperManager.m_useSystemTime) {
                            final int sysTicks = getSystemTicks();
                            WrapperManager.m_ticks++;
                            final int tickOffset = sysTicks - WrapperManager.m_ticks;
                            offsetDiff = tickOffset - lastTickOffset;
                            if (first) {
                                first = false;
                            }
                            else if (offsetDiff > WrapperManager.m_timerSlowThreshold) {
                                WrapperManager.m_outInfo.println(WrapperManager.getRes().getString("The timer fell behind the system clock by {0} ms.", new Integer(offsetDiff * 100)));
                            }
                            else if (offsetDiff < -WrapperManager.m_timerFastThreshold) {
                                WrapperManager.m_outInfo.println(WrapperManager.getRes().getString("The system clock fell behind the timer by {0} ms.", new Integer(-1 * offsetDiff * 100)));
                            }
                            lastTickOffset = tickOffset;
                        }
                        else {
                            offsetDiff = 0;
                        }
                        final int nowTicks = getTicks();
                        final long age = getTickAge(WrapperManager.m_eventRunnerTicks, nowTicks);
                        if (WrapperManager.m_cpuTimeout > 0L && age > WrapperManager.m_cpuTimeout) {
                            WrapperManager.m_outInfo.println(WrapperManager.getRes().getString("JVM Process has not received any CPU time for {0} seconds.  Extending timeouts.", new Long(age / 1000L)));
                            WrapperManager.m_lastPingTicks = nowTicks;
                        }
                        WrapperManager.m_eventRunnerTicks = nowTicks;
                        if (WrapperManager.m_produceCoreEvents) {
                            tickEvent.m_ticks = nowTicks;
                            tickEvent.m_tickOffset = offsetDiff;
                            fireWrapperEvent(tickEvent);
                        }
                        if (WrapperManager.isNativeLibraryOk()) {
                            if (!WrapperManager.isShuttingDown()) {
                                int event = 0;
                                do {
                                    event = nativeGetControlEvent();
                                    if (event != 0) {
                                        controlEvent(event);
                                    }
                                } while (event != 0);
                            }
                            else if (!stoppingLogged) {
                                stoppingLogged = true;
                                if (WrapperManager.m_debug) {
                                    WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("Stopped checking for control events."));
                                }
                            }
                        }
                        try {
                            Thread.sleep(100L);
                        }
                        catch (final InterruptedException ex) {}
                    }
                }
                finally {
                    if (WrapperManager.m_debug) {
                        WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("Control event monitor thread stopped."));
                    }
                }
            }
        }).setDaemon(true);
        WrapperManager.m_eventRunner.start();
        String fullVersion = System.getProperty("java.fullversion");
        final String vendor = System.getProperty("java.vm.vendor", "");
        final String os = System.getProperty("os.name", "").toLowerCase();
        if (fullVersion == null) {
            fullVersion = System.getProperty("java.runtime.version") + " " + System.getProperty("java.vm.name");
        }
        if (WrapperManager.m_debug) {
            WrapperManager.m_outDebug.println(getRes().getString("Java PID       : {0}", Integer.toString(getJavaPID())));
            WrapperManager.m_outDebug.println(getRes().getString("Java Version   : {0}", fullVersion));
            WrapperManager.m_outDebug.println(getRes().getString("Java VM Vendor : {0}", vendor));
            WrapperManager.m_outDebug.println(getRes().getString("OS Name        : {0}", System.getProperty("os.name", "")));
            WrapperManager.m_outDebug.println(getRes().getString("OS Arch        : {0}", System.getProperty("os.arch", "")));
            WrapperManager.m_outDebug.println();
        }
        (WrapperManager.m_startupRunner = new Thread("Wrapper-Startup-Runner") {
            public void run() {
                Thread.currentThread().setPriority(10);
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("Startup runner thread started."));
                }
                try {
                    while (WrapperManager.m_startupRunner != null) {
                        try {
                            Thread.sleep(100L);
                        }
                        catch (final InterruptedException e) {}
                    }
                }
                finally {
                    if (WrapperManager.m_debug) {
                        WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("Startup runner thread stopped."));
                    }
                }
            }
        }).setDaemon(false);
        WrapperManager.m_startupRunner.start();
        WrapperManager.m_instance = new WrapperManager();
    }
    
    protected void finalize() throws Throwable {
        try {
            if (isLoggingFinalizers()) {
                System.out.println("WrapperManager.finalize");
            }
        }
        finally {
            super.finalize();
        }
    }
    
    private static native void nativeInit(final boolean p0);
    
    private static native String nativeGetLibraryVersion();
    
    private static native int nativeGetJavaPID();
    
    private static native boolean nativeIsProfessionalEdition();
    
    private static native boolean nativeIsStandardEdition();
    
    private static native int nativeGetControlEvent();
    
    private static native int nativeRedirectPipes();
    
    private static native void nativeRequestThreadDump();
    
    private static native void accessViolationInner();
    
    private static native void nativeSetConsoleTitle(final String p0);
    
    private static native WrapperUser nativeGetUser(final boolean p0);
    
    private static native WrapperUser nativeGetInteractiveUser(final boolean p0);
    
    private static native WrapperWin32Service[] nativeListServices();
    
    private static native WrapperWin32Service nativeSendServiceControlCode(final String p0, final int p1);
    
    private static native WrapperProcess nativeExec(final String[] p0, final String p1, final WrapperProcessConfig p2, final boolean p3);
    
    private static native String nativeWrapperGetEnv(final String p0) throws NullPointerException;
    
    private static native WrapperResources nativeLoadWrapperResources(final String p0, final String p1, final boolean p2);
    
    private static native boolean nativeCheckDeadLocks();
    
    private static native int nativeGetPortStatus(final int p0, final String p1, final int p2);
    
    public static native int nativeGetDpiScale();
    
    private static int getSystemTicks() {
        return (int)(System.currentTimeMillis() / 100L);
    }
    
    private static int getTicks() {
        if (WrapperManager.m_useSystemTime) {
            return getSystemTicks();
        }
        return WrapperManager.m_ticks;
    }
    
    private static long getTickAge(final int start, final int end) {
        return (end - start) * 100L;
    }
    
    private static String loadNativeLibrary(final String name, final String file) {
        try {
            checkOldLibraryOnAix(file);
            System.loadLibrary(name);
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("  Attempt to load native library with name: {0}  Result: {1}", file, getRes().getString("Success!")));
            }
            return null;
        }
        catch (final UnsatisfiedLinkError e) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("  Attempt to load native library with name: {0}  Result: {1}", file, e.getMessage()));
            }
            String error = e.getMessage();
            if (error == null) {
                error = e.toString();
            }
            return error;
        }
        catch (final Throwable e2) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("  Attempt to load native library with name: {0}  Result: {1}", file, e2.getMessage()));
            }
            final String error = e2.toString();
            return error;
        }
    }
    
    private static void registerMBean(final Object mbean, final String name) {
        Class classManagementFactory;
        Class classMBeanServer;
        Class classObjectName;
        try {
            classManagementFactory = Class.forName("java.lang.management.ManagementFactory");
            classMBeanServer = Class.forName("javax.management.MBeanServer");
            classObjectName = Class.forName("javax.management.ObjectName");
        }
        catch (final ClassNotFoundException e) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Registering MBeans not supported by current JVM: {0}", name));
            }
            return;
        }
        try {
            final Method methodGetPlatformMBeanServer = classManagementFactory.getMethod("getPlatformMBeanServer", (Class[])null);
            final Constructor constructorObjectName = classObjectName.getConstructor(String.class);
            final Method methodRegisterMBean = classMBeanServer.getMethod("registerMBean", Object.class, classObjectName);
            final Object mbs = methodGetPlatformMBeanServer.invoke(null, (Object[])null);
            final Object oName = constructorObjectName.newInstance(name);
            methodRegisterMBean.invoke(mbs, mbean, oName);
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Registered MBean with Platform MBean Server: {0}", name));
            }
        }
        catch (final Throwable t) {
            if (t instanceof ClassNotFoundException) {
                WrapperManager.m_outError.println("Using MBean requires at least a JVM version 1.5.");
            }
            WrapperManager.m_outError.println("Unable to register the " + name + " MBean.");
            t.printStackTrace(WrapperManager.m_outError);
        }
    }
    
    private static File locateFileOnPath(final String file, final String path) {
        final String pathSep = System.getProperty("path.separator");
        final StringTokenizer st = new StringTokenizer(path, pathSep);
        while (st.hasMoreTokens()) {
            final File libFile = new File(new File(st.nextToken()), file);
            if (libFile.exists()) {
                return libFile;
            }
        }
        return null;
    }
    
    private static String generateDetailedNativeLibraryBaseName(final String baseName, final int jvmBits) {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.startsWith("windows")) {
            os = "windows";
            WrapperManager.m_windows = true;
        }
        else if (os.equals("sunos")) {
            os = "solaris";
        }
        else if (os.equals("hp-ux") || os.equals("hp-ux64")) {
            os = "hpux";
        }
        else if (os.equals("mac os x")) {
            os = "macosx";
            WrapperManager.m_macosx = true;
        }
        else if (os.equals("unix_sv")) {
            os = "unixware";
        }
        else if (os.equals("os/400")) {
            os = "os400";
        }
        else if (os.equals("z/os")) {
            os = "zos";
            WrapperManager.m_zos = true;
        }
        else if (os.indexOf("aix") > -1) {
            WrapperManager.m_aix = true;
        }
        WrapperManager.m_os = os;
        String arch;
        if (WrapperManager.m_macosx) {
            arch = "universal";
        }
        else {
            arch = System.getProperty("os.arch", "").toLowerCase();
            if (arch.equals("amd64") || arch.equals("athlon") || arch.equals("x86_64") || arch.equals("i686") || arch.equals("i586") || arch.equals("i486") || arch.equals("i386")) {
                arch = "x86";
            }
            else if (arch.startsWith("ia32") || arch.startsWith("ia64")) {
                arch = "ia";
            }
            else if (arch.startsWith("sparc")) {
                arch = "sparc";
            }
            else if (arch.startsWith("ppc64le")) {
                arch = "ppcle";
            }
            else if (arch.equals("power") || arch.equals("powerpc") || arch.equals("ppc64")) {
                if (WrapperManager.m_aix) {
                    arch = "ppc";
                }
                else {
                    arch = "ppcbe";
                }
            }
            else if (arch.startsWith("pa_risc") || arch.startsWith("pa-risc")) {
                arch = "parisc";
            }
            else if (arch.startsWith("arm")) {
                arch = System.getProperty("wrapper.arch");
            }
            else if (arch.equals("s390") || arch.equals("s390x")) {
                arch = "390";
            }
        }
        WrapperManager.m_arch = arch;
        return baseName + "-" + os + "-" + arch + "-" + jvmBits;
    }
    
    private static String mapSharedLibraryName(final String name) {
        String result = System.mapLibraryName(name);
        if (isAIX() && result.endsWith(".a")) {
            result = result.substring(0, result.length() - 2).concat(".so");
        }
        return result;
    }
    
    private static void checkOldLibraryOnAix(String libName) {
        if (isAIX()) {
            if (libName.endsWith(".so")) {
                libName = libName.substring(0, libName.length() - 3).concat(".a");
            }
            final String pathSep = System.getProperty("path.separator");
            final String[] libPaths = System.getProperty("java.library.path").split(pathSep);
            for (int j = 0; j < libPaths.length; ++j) {
                final File libFile = locateFileOnPath(libName, libPaths[j]);
                if (libFile != null) {
                    WrapperManager.m_outInfo.println(getRes().getString("WARNING - {0} was found in {1}.", libName, libPaths[j]));
                    WrapperManager.m_outInfo.println(getRes().getString("          Recent Wrapper''s native libraries have a ''.so'' suffix."));
                    WrapperManager.m_outInfo.println(getRes().getString("          Depending on the version of Java that is used, {0}", libName));
                    WrapperManager.m_outInfo.println(getRes().getString("          may be loaded instead of a more recent library."));
                    WrapperManager.m_outInfo.println(getRes().getString("          Please remove {0} and make sure that the latest version", libName));
                    WrapperManager.m_outInfo.println(getRes().getString("          of the Wrapper''s native library is in the lib folder."));
                }
            }
        }
    }
    
    private static void initializeNativeLibrary() {
        String baseName = System.getProperty("wrapper.native_library");
        if (baseName == null) {
            WrapperManager.m_outInfo.println("WARNING - The wrapper.native_library system property was not");
            WrapperManager.m_outInfo.println("          set. Using the default value, 'wrapper'.");
            baseName = "wrapper";
        }
        final String[] detailedNames = new String[4];
        if (WrapperManager.m_jvmBits > 0) {
            detailedNames[0] = generateDetailedNativeLibraryBaseName(baseName, WrapperManager.m_jvmBits);
        }
        else {
            detailedNames[0] = generateDetailedNativeLibraryBaseName(baseName, 32);
            detailedNames[1] = generateDetailedNativeLibraryBaseName(baseName, 64);
        }
        final String file = mapSharedLibraryName(baseName);
        final String[] detailedFiles = new String[detailedNames.length];
        for (int i = 0; i < detailedNames.length; ++i) {
            if (detailedNames[i] != null) {
                detailedFiles[i] = mapSharedLibraryName(detailedNames[i]);
            }
        }
        final String[] detailedErrors = new String[detailedNames.length];
        String baseError = null;
        if (WrapperManager.m_debug) {
            WrapperManager.m_outDebug.println("Load native library.  There are multiple possible file names and the first to be found will be used.  Errors loading non-existing files is normal and is only a problem if they all fail.");
        }
        WrapperManager.m_libraryOK = false;
        for (int j = 0; j < detailedNames.length; ++j) {
            if (detailedNames[j] != null) {
                detailedErrors[j] = loadNativeLibrary(detailedNames[j], detailedFiles[j]);
                if (detailedErrors[j] == null) {
                    WrapperManager.m_libraryOK = true;
                    break;
                }
            }
        }
        if (!WrapperManager.m_libraryOK && (baseError = loadNativeLibrary(baseName, file)) == null) {
            WrapperManager.m_libraryOK = true;
        }
        if (WrapperManager.m_libraryOK) {
            WrapperManager.m_libraryLoaded = true;
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println("  Successfully loaded native library.");
            }
            try {
                WrapperManager.m_professionalEdition = nativeIsProfessionalEdition();
            }
            catch (final Throwable e) {
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println("Call to nativeIsProfessionalEdition() failed: " + e);
                }
                WrapperManager.m_professionalEdition = false;
            }
            try {
                WrapperManager.m_standardEdition = nativeIsStandardEdition();
            }
            catch (final Throwable e) {
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println("Call to nativeIsStandardEdition() failed: " + e);
                }
                WrapperManager.m_standardEdition = false;
            }
            WrapperManager.m_res = loadWrapperResourcesInner(System.getProperty("wrapper.lang.domain") + "jni", WrapperSystemPropertyUtil.getStringProperty("wrapper.lang.folder", "../lang"), true);
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Loaded localized resources."));
            }
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Calling native initialization method."));
            }
            nativeInit(WrapperManager.m_debug);
            if (WrapperManager.m_stoppingInit) {
                WrapperManager.m_libraryOK = false;
            }
        }
        else {
            WrapperManager.m_libraryLoaded = false;
            final String libPath = System.getProperty("java.library.path");
            WrapperManager.m_outInfo.println();
            if (libPath.equals("")) {
                WrapperManager.m_outInfo.println("ERROR - Unable to load the Wrapper's native library because the");
                WrapperManager.m_outInfo.println("        java.library.path was set to ''.  Please see the");
                WrapperManager.m_outInfo.println("        documentation for the wrapper.java.library.path");
                WrapperManager.m_outInfo.println("        configuration property.");
            }
            else {
                String error = null;
                File libFile = null;
                for (int k = 0; k < detailedNames.length; ++k) {
                    if (detailedFiles[k] != null) {
                        libFile = locateFileOnPath(detailedFiles[k], libPath);
                        if (libFile != null) {
                            error = detailedErrors[k];
                            break;
                        }
                    }
                }
                if (libFile == null) {
                    libFile = locateFileOnPath(file, libPath);
                    if (libFile != null) {
                        error = baseError;
                    }
                }
                if (libFile == null) {
                    WrapperManager.m_outInfo.println("ERROR - Unable to load the Wrapper's native library because none of the");
                    WrapperManager.m_outInfo.println("        following files:");
                    for (int k = 0; k < detailedNames.length; ++k) {
                        if (detailedFiles[k] != null) {
                            WrapperManager.m_outInfo.println("          " + detailedFiles[k]);
                        }
                    }
                    WrapperManager.m_outInfo.println("          " + file);
                    WrapperManager.m_outInfo.println("        could be located on the following java.library.path:");
                    final String pathSep = System.getProperty("path.separator");
                    final StringTokenizer st = new StringTokenizer(libPath, pathSep);
                    while (st.hasMoreTokens()) {
                        final File pathElement = new File(st.nextToken());
                        WrapperManager.m_outInfo.println("          " + pathElement.getAbsolutePath());
                    }
                    WrapperManager.m_outInfo.println("        Please see the documentation for the wrapper.java.library.path");
                    WrapperManager.m_outInfo.println("        configuration property.");
                }
                else {
                    WrapperManager.m_outInfo.println("ERROR - Unable to load the Wrapper's native library '" + libFile.getName() + "'.");
                    WrapperManager.m_outInfo.println("        The file is located on the path at the following location but");
                    WrapperManager.m_outInfo.println("        could not be loaded:");
                    WrapperManager.m_outInfo.println("          " + libFile.getAbsolutePath());
                    WrapperManager.m_outInfo.println("        Please verify that the file is both readable and executable by the");
                    WrapperManager.m_outInfo.println("        current user and that the file has not been corrupted in any way.");
                    WrapperManager.m_outInfo.println("        One common cause of this problem is running a 32-bit version");
                    WrapperManager.m_outInfo.println("        of the Wrapper with a 64-bit version of Java, or vica versa.");
                    if (WrapperManager.m_jvmBits > 0) {
                        WrapperManager.m_outInfo.println("        This is a " + WrapperManager.m_jvmBits + "-bit JVM.");
                    }
                    else {
                        WrapperManager.m_outInfo.println("        The bit depth of this JVM could not be determined.");
                    }
                    WrapperManager.m_outInfo.println("        Reported cause:");
                    WrapperManager.m_outInfo.println("          " + error);
                }
            }
            WrapperManager.m_outInfo.println();
        }
    }
    
    private static void verifyWrapperVersion() {
        if (!isControlledByNativeWrapper()) {
            return;
        }
        String wrapperVersion = System.getProperty("wrapper.version");
        if (wrapperVersion == null) {
            wrapperVersion = getRes().getString("unknown");
        }
        if (wrapperVersion.endsWith("-pro")) {
            wrapperVersion = wrapperVersion.substring(0, wrapperVersion.length() - 4);
        }
        else if (wrapperVersion.endsWith("-st")) {
            wrapperVersion = wrapperVersion.substring(0, wrapperVersion.length() - 3);
        }
        if (!WrapperInfo.getVersion().equals(wrapperVersion)) {
            WrapperManager.m_outInfo.println(getRes().getString("ERROR - The Wrapper jar file currently in use is version \"{0}\"\n        while the version of the Wrapper which launched this JVM is\n        \"{1}\"", WrapperInfo.getVersion(), wrapperVersion));
            WrapperManager.m_outInfo.println();
            WrapperManager.m_wrapperVersionOk = false;
        }
        else {
            WrapperManager.m_wrapperVersionOk = true;
        }
    }
    
    private static void verifyNativeLibraryVersion() {
        String jniVersion;
        try {
            jniVersion = nativeGetLibraryVersion();
        }
        catch (final Throwable e) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Call to nativeGetLibraryVersion() failed: {0}", e));
            }
            jniVersion = getRes().getString("unknown");
        }
        String wrapperVersion = System.getProperty("wrapper.version");
        if (wrapperVersion == null) {
            wrapperVersion = getRes().getString("unknown");
        }
        if (!wrapperVersion.equals(jniVersion)) {
            WrapperManager.m_outInfo.println(getRes().getString("ERROR - The version of the Wrapper which launched this JVM is\n        \"{0}\" while the version of the native library\n        is \"{1}\".", wrapperVersion, jniVersion));
            WrapperManager.m_outInfo.println();
            WrapperManager.m_libraryVersionOk = false;
        }
        else {
            WrapperManager.m_libraryVersionOk = true;
        }
    }
    
    private static void checkTmpDir() {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        if (WrapperManager.m_debug) {
            WrapperManager.m_outDebug.println(getRes().getString("Java temporary directory: {0}", tmpDir));
        }
        final boolean tmpDirCheck = getProperties().getProperty("wrapper.java.tmpdir.check", "TRUE").equalsIgnoreCase("TRUE");
        if (!tmpDirCheck) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Validation of temporary directory disabled."));
            }
            return;
        }
        final boolean tmpDirRequired = getProperties().getProperty("wrapper.java.tmpdir.required", "FALSE").equalsIgnoreCase("TRUE");
        final boolean tmpDirWarnSilently = getProperties().getProperty("wrapper.java.tmpdir.warn_silently", "TRUE").equalsIgnoreCase("TRUE");
        Exception ex = null;
        try {
            tmpDir = tmpDir.getCanonicalFile();
            final File tempFile = new File(tmpDir, "wrapper-" + System.currentTimeMillis() + "-" + getJavaPID());
            if (tempFile.createNewFile()) {
                if (!tempFile.delete()) {
                    WrapperManager.m_outError.println("Unable to delete temporary file: " + tempFile);
                }
            }
            else if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println("Unable to create temporary file: " + tempFile);
            }
        }
        catch (final IOException e) {
            ex = e;
        }
        catch (final SecurityException e2) {
            ex = e2;
        }
        if (ex != null) {
            if (tmpDirRequired) {
                WrapperManager.m_outError.println(getRes().getString("Unable to write to the configured Java temporary directory: {0} : {1}", tmpDir, ex.toString()));
                WrapperManager.m_outError.println(getRes().getString("Shutting down."));
                System.exit(1);
            }
            else {
                if (tmpDirWarnSilently) {
                    if (WrapperManager.m_debug) {
                        WrapperManager.m_outDebug.println(getRes().getString("Unable to write to the configured Java temporary directory: {0} : {1}", tmpDir, ex.toString()));
                    }
                }
                else {
                    WrapperManager.m_outInfo.println(getRes().getString("Unable to write to the configured Java temporary directory: {0} : {1}", tmpDir, ex.toString()));
                }
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(getRes().getString("  The lack of a temp directory could lead to problems with features that store temporary data, including remote jar class loading."));
                    WrapperManager.m_outDebug.println(getRes().getString("  The Java temporary directory can be redefined with the java.io.tmpdir system property."));
                }
            }
        }
    }
    
    private static WrapperResources loadWrapperResourcesInner(final String domain, String folder, final boolean makeActive) {
        if (isStandardEdition() && isNativeLibraryOk()) {
            if (folder == null) {
                folder = WrapperSystemPropertyUtil.getStringProperty("wrapper.lang.folder", "../lang");
            }
            return nativeLoadWrapperResources(domain, folder, makeActive);
        }
        return new WrapperResources();
    }
    
    public static WrapperResources loadWrapperResources(final String domain, final String folder) {
        return loadWrapperResourcesInner(domain, folder, false);
    }
    
    public static String getVersion() {
        return WrapperInfo.getVersion();
    }
    
    public static String getBuildTime() {
        return WrapperInfo.getBuildTime();
    }
    
    public static int getJVMId() {
        return WrapperManager.m_jvmId;
    }
    
    private static String[] parseCommandLine(final String cmdLine) {
        final ArrayList argList = new ArrayList();
        final StringBuffer arg = new StringBuffer();
        boolean quoteMode = false;
        boolean escapeNextCharIfQuote = false;
        final char[] c = cmdLine.toCharArray();
        for (int i = 0; i < cmdLine.length(); ++i) {
            if (c[i] == '\\' && !escapeNextCharIfQuote) {
                escapeNextCharIfQuote = true;
            }
            else if (Character.isWhitespace(c[i]) && !quoteMode) {
                if (arg.length() > 0) {
                    argList.add(arg.toString());
                    arg.setLength(0);
                }
            }
            else if (c[i] == '\"') {
                if (!escapeNextCharIfQuote) {
                    quoteMode = !quoteMode;
                    escapeNextCharIfQuote = false;
                }
                else {
                    escapeNextCharIfQuote = false;
                    arg.append(c[i]);
                }
            }
            else if (c[i] == '\\') {
                if (escapeNextCharIfQuote) {
                    escapeNextCharIfQuote = false;
                }
                arg.append('\\');
            }
            else {
                if (escapeNextCharIfQuote) {
                    arg.append('\\');
                    escapeNextCharIfQuote = false;
                }
                arg.append(c[i]);
            }
        }
        if (arg.length() > 0) {
            argList.add(arg.toString());
        }
        final String[] args = new String[argList.size()];
        argList.toArray(args);
        return args;
    }
    
    public static WrapperProcess exec(final String command) throws SecurityException, IOException, NullPointerException, IllegalArgumentException, WrapperJNIError, WrapperLicenseError, UnsatisfiedLinkError {
        final WrapperProcess proc = exec(command, new WrapperProcessConfig());
        return proc;
    }
    
    public static WrapperProcess exec(final String command, final WrapperProcessConfig config) throws SecurityException, IOException, NullPointerException, IllegalArgumentException, WrapperJNIError, WrapperLicenseError, UnsatisfiedLinkError {
        if (command == null || command.length() == 0) {
            throw new IllegalArgumentException(getRes().getString("No command specified"));
        }
        return exec(null, command, config);
    }
    
    public static WrapperProcess exec(final String[] cmdArray) throws SecurityException, IOException, NullPointerException, IndexOutOfBoundsException, IllegalArgumentException, WrapperJNIError, UnsatisfiedLinkError, WrapperLicenseError {
        final WrapperProcess proc = exec(cmdArray, new WrapperProcessConfig());
        return proc;
    }
    
    public static WrapperProcess exec(final String[] cmdArray, final WrapperProcessConfig config) throws SecurityException, IOException, NullPointerException, IndexOutOfBoundsException, IllegalArgumentException, WrapperJNIError, WrapperLicenseError, UnsatisfiedLinkError {
        return exec(cmdArray, null, config);
    }
    
    private static WrapperProcess exec(String[] cmdArray, final String cmdLine, final WrapperProcessConfig config) throws SecurityException, IOException, NullPointerException, IndexOutOfBoundsException, IllegalArgumentException, WrapperJNIError, WrapperLicenseError, UnsatisfiedLinkError {
        assertProfessionalEdition();
        if (cmdArray == null && cmdLine == null) {
            throw new NullPointerException(getRes().getString("No command specified"));
        }
        if (cmdArray != null && cmdArray.length == 0) {
            throw new IndexOutOfBoundsException(getRes().getString("cmdArray is empty"));
        }
        if (cmdArray == null && cmdLine != null) {
            cmdArray = parseCommandLine(cmdLine);
        }
        if (config == null) {
            throw new NullPointerException(getRes().getString("config is null"));
        }
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkExec(cmdArray[0]);
        }
        Class class$;
        Class class$org$tanukisoftware$wrapper$WrapperManager;
        if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
            class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
        }
        else {
            class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
        }
        final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
        synchronized (class$) {
            ++WrapperManager.m_runningExecs;
        }
        try {
            if (isNativeLibraryOk()) {
                for (int i = 0; i < cmdArray.length; ++i) {
                    if (cmdArray[i] == null) {
                        throw new NullPointerException(getRes().getString("cmdarray[{0}]: Invalid element (isNull).", new Integer(i)));
                    }
                }
                if (!WrapperManager.m_windows && !new File(cmdArray[0]).exists()) {
                    boolean found = false;
                    final String path = nativeWrapperGetEnv("PATH");
                    if (path != null) {
                        final String[] paths = path.split(File.pathSeparator);
                        for (int j = 0; j < paths.length; ++j) {
                            final File file = new File(paths[j] + File.separator + cmdArray[0]);
                            if (file.exists()) {
                                cmdArray[0] = file.getPath();
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        throw new IOException(getRes().getString("''{0}'' not found.", cmdArray[0]));
                    }
                }
                if (WrapperManager.m_debug) {
                    for (int k = 0; k < cmdArray.length; ++k) {
                        WrapperManager.m_outDebug.println("args[" + k + "] = " + cmdArray[k]);
                    }
                }
                return nativeExec(cmdArray, cmdLine, config.setEnvironment(config.getEnvironment()), WrapperSystemPropertyUtil.getBooleanProperty("wrapper.child.allowCWDOnSpawn", false));
            }
            if (WrapperManager.m_stopped) {
                throw new WrapperJNIError("Wrapper native library shutting down.");
            }
            throw new WrapperJNIError("Wrapper native library not loaded.");
        }
        finally {
            Class class$2;
            Class class$org$tanukisoftware$wrapper$WrapperManager2;
            if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
                class$org$tanukisoftware$wrapper$WrapperManager2 = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$2 = class$("org.tanukisoftware.wrapper.WrapperManager")));
            }
            else {
                class$2 = (class$org$tanukisoftware$wrapper$WrapperManager2 = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
            }
            final Class clazz2 = class$org$tanukisoftware$wrapper$WrapperManager2;
            synchronized (class$2) {
                --WrapperManager.m_runningExecs;
                if (WrapperManager.m_runningExecs <= 0) {
                    WrapperManager.class.notifyAll();
                }
            }
        }
    }
    
    public static boolean isNativeLibraryOk() {
        return WrapperManager.m_libraryOK && !WrapperManager.m_stopped;
    }
    
    static void assertProfessionalEdition() throws WrapperLicenseError {
        if (!WrapperManager.m_libraryOK) {
            throw new WrapperLicenseError(getRes().getString("Requires that the Professional Edition native library be loaded.  Please check for errors earlier in the log."));
        }
        if (WrapperManager.m_stopped) {
            throw new WrapperLicenseError(getRes().getString("Requires that the Professional Edition native library be loaded, but it has already been unloaded as part of the shutdown process."));
        }
        if (!isProfessionalEdition()) {
            throw new WrapperLicenseError(getRes().getString("Requires the Professional Edition."));
        }
    }
    
    public static String getOS() {
        return WrapperManager.m_os;
    }
    
    public static String getArch() {
        return WrapperManager.m_arch;
    }
    
    public static boolean isWindows() {
        return WrapperManager.m_windows;
    }
    
    public static boolean isMacOSX() {
        return WrapperManager.m_macosx;
    }
    
    public static boolean isAIX() {
        return WrapperManager.m_aix;
    }
    
    private static boolean isZOS() {
        return WrapperManager.m_zos;
    }
    
    public static boolean isProfessionalEdition() {
        return WrapperManager.m_professionalEdition;
    }
    
    public static boolean isStandardEdition() {
        return WrapperManager.m_standardEdition;
    }
    
    public static void fireUserEvent(final int eventNr) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperUserEventPermission("fireUserEvent", String.valueOf(eventNr)));
        }
        if (eventNr <= 0 || eventNr > 32767) {
            throw new IllegalArgumentException(getRes().getString("The user-event number must be in the range of 1-32767."));
        }
        assertProfessionalEdition();
        sendCommand((byte)(-115), String.valueOf(eventNr));
    }
    
    public static void setConsoleTitle(final String title) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("setConsoleTitle"));
        }
        if (isNativeLibraryOk()) {
            nativeSetConsoleTitle(title);
        }
    }
    
    public static WrapperUser getUser(final boolean groups) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("getUser"));
        }
        WrapperUser user = null;
        if (isNativeLibraryOk()) {
            user = nativeGetUser(groups);
        }
        return user;
    }
    
    public static WrapperUser getInteractiveUser(final boolean groups) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("getInteractiveUser"));
        }
        WrapperUser user = null;
        if (isNativeLibraryOk()) {
            user = nativeGetInteractiveUser(groups);
        }
        return user;
    }
    
    public static Properties getProperties() {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("getProperties"));
        }
        return WrapperManager.m_properties;
    }
    
    public static int getWrapperPID() {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("getWrapperPID"));
        }
        return WrapperSystemPropertyUtil.getIntProperty("wrapper.pid", 0);
    }
    
    public static int getJavaPID() {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("getJavaPID"));
        }
        return WrapperSystemPropertyUtil.getIntProperty("wrapper.java.pid", 0);
    }
    
    public static void requestThreadDump() {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("requestThreadDump"));
        }
        if (isNativeLibraryOk()) {
            nativeRequestThreadDump();
        }
        else {
            WrapperManager.m_outInfo.println(getRes().getString("  wrapper library not loaded."));
        }
    }
    
    public static void appearHung() {
        if (WrapperManager.m_disableTests) {
            throw new IllegalStateException(getRes().getString("Test methods have been disabled."));
        }
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("test.appearHung"));
        }
        WrapperManager.m_outInfo.println(getRes().getString("WARNING: Making JVM appear to be hung..."));
        WrapperManager.m_appearHung = true;
    }
    
    public static void appearSlow(final int slowSeconds) {
        if (WrapperManager.m_disableTests) {
            throw new IllegalStateException(getRes().getString("Test methods have been disabled."));
        }
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("test.appearSlow"));
        }
        if (slowSeconds > 0) {
            WrapperManager.m_outInfo.println(getRes().getString("WARNING: Making JVM appear to be slow using a delay of {0} seconds...", new Integer(slowSeconds)));
            WrapperManager.m_slowSeconds = slowSeconds;
        }
        else if (WrapperManager.m_slowSeconds > 0) {
            WrapperManager.m_outInfo.println(getRes().getString("Resetting the JVM delayed response to normal..."));
            WrapperManager.m_slowSeconds = 0;
        }
    }
    
    public static void appearOrphan() {
    }
    
    public static void accessViolation() {
        if (WrapperManager.m_disableTests) {
            throw new IllegalStateException(getRes().getString("Test methods have been disabled."));
        }
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("test.accessViolation"));
        }
        WrapperManager.m_outInfo.println(getRes().getString("WARNING: Attempting to cause an access violation..."));
        try {
            final Class c = Class.forName("java.lang.String");
            final Method m = c.getDeclaredMethod(null, (Class[])null);
        }
        catch (final NoSuchMethodException ex) {}
        catch (final Exception ex2) {
            if (!(ex2 instanceof NoSuchFieldException)) {
                ex2.printStackTrace(WrapperManager.m_outError);
            }
        }
        WrapperManager.m_outInfo.println(getRes().getString("  Attempt to cause access violation failed.  JVM is still alive."));
    }
    
    public static void accessViolationNative() {
        if (WrapperManager.m_disableTests) {
            throw new IllegalStateException(getRes().getString("Test methods have been disabled."));
        }
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("test.accessViolationNative"));
        }
        WrapperManager.m_outInfo.println(getRes().getString("WARNING: Attempting to cause an access violation..."));
        if (isNativeLibraryOk()) {
            accessViolationInner();
            WrapperManager.m_outInfo.println(getRes().getString("  Attempt to cause access violation failed.  JVM is still alive."));
        }
        else {
            WrapperManager.m_outInfo.println(getRes().getString("  wrapper library not loaded."));
        }
    }
    
    public static boolean isControlledByNativeWrapper() {
        return WrapperManager.m_key != null;
    }
    
    public static boolean isLaunchedAsService() {
        return WrapperManager.m_service;
    }
    
    public static boolean isIgnoreUserLogoffs() {
        return WrapperManager.m_ignoreUserLogoffs;
    }
    
    public static boolean isDebugEnabled() {
        return WrapperManager.m_debug;
    }
    
    static boolean isLoggingFinalizers() {
        return WrapperManager.m_logFinalizer;
    }
    
    public static void start(final WrapperListener listener, final String[] args) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                privilegedStart(listener, args);
                return null;
            }
        });
    }
    
    private static void privilegedStart(final WrapperListener listener, String[] args) {
        checkSecurityManager();
        if (args == null) {
            args = new String[0];
        }
        if (WrapperManager.m_debug) {
            final StringBuffer sb = new StringBuffer();
            sb.append("args[");
            for (int i = 0; i < args.length; ++i) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append("\"");
                sb.append(args[i]);
                sb.append("\"");
            }
            sb.append("]");
            WrapperManager.m_outDebug.println(getRes().getString("{0} called by thread: {1}", "WrapperManager.start(a " + listener.getClass().getName() + ", " + sb.toString() + ")", Thread.currentThread().getName()));
        }
        Class class$;
        Class class$org$tanukisoftware$wrapper$WrapperManager;
        if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
            class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
        }
        else {
            class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
        }
        final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
        synchronized (class$) {
            if (WrapperManager.m_disposed) {
                throw new IllegalStateException(getRes().getString("WrapperManager has already been disposed."));
            }
            if (WrapperManager.m_listener != null) {
                throw new IllegalStateException(getRes().getString("WrapperManager has already been started with a WrapperListener."));
            }
            if (listener == null) {
                throw new IllegalStateException(getRes().getString("A WrapperListener must be specified."));
            }
            WrapperManager.m_listener = listener;
            WrapperManager.m_args = args;
            if (WrapperManager.m_debug) {
                final Thread thisThread = Thread.currentThread();
                WrapperManager.m_outDebug.println(getRes().getString("Initial thread: {0} Priority: {1}", thisThread.getName(), new Integer(thisThread.getPriority())));
            }
            startRunner();
            if (!isControlledByNativeWrapper()) {
                startInner(true);
            }
        }
    }
    
    public static boolean isShuttingDown() {
        return WrapperManager.m_stopping;
    }
    
    public static void requestShutdownLock() throws WrapperShuttingDownException {
        Class class$;
        Class class$org$tanukisoftware$wrapper$WrapperManager;
        if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
            class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
        }
        else {
            class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
        }
        final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
        synchronized (class$) {
            if (WrapperManager.m_stopping) {
                throw new WrapperShuttingDownException();
            }
            final Thread thisThread = Thread.currentThread();
            ShutdownLock lock = WrapperManager.m_shutdownLockMap.get(thisThread);
            if (lock == null) {
                lock = new ShutdownLock(thisThread);
                WrapperManager.m_shutdownLockMap.put(thisThread, lock);
            }
            lock.m_count++;
            ++WrapperManager.m_shutdownLocks;
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("{0} called by thread: {1} (New thread lock count: {2}, total lock count: {3})", "WrapperManager.requestShutdownLock()", thisThread.getName(), new Integer(lock.m_count), new Integer(WrapperManager.m_shutdownLocks)));
            }
        }
    }
    
    public static void releaseShutdownLock() throws IllegalStateException {
        Class class$;
        Class class$org$tanukisoftware$wrapper$WrapperManager;
        if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
            class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
        }
        else {
            class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
        }
        final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
        synchronized (class$) {
            final Thread thisThread = Thread.currentThread();
            final ShutdownLock lock = WrapperManager.m_shutdownLockMap.get(thisThread);
            if (lock == null) {
                throw new IllegalStateException(getRes().getString("requestShutdownLock was not called from this thread."));
            }
            lock.m_count--;
            --WrapperManager.m_shutdownLocks;
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("{0} called by thread: {1} (New thread lock count: {2}, total lock count: {3})", "WrapperManager.releaseShutdownLock()", thisThread.getName(), new Integer(lock.m_count), new Integer(WrapperManager.m_shutdownLocks)));
            }
            if (lock.m_count <= 0) {
                WrapperManager.m_shutdownLockMap.remove(thisThread);
            }
            WrapperManager.class.notify();
        }
    }
    
    private static void waitForShutdownLocks() {
        Class class$;
        Class class$org$tanukisoftware$wrapper$WrapperManager;
        if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
            class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
        }
        else {
            class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
        }
        final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
        synchronized (class$) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("wait for {0} shutdown locks to be released.", new Integer(WrapperManager.m_shutdownLocks)));
            }
            while (WrapperManager.m_shutdownLocks > 0) {
                try {
                    WrapperManager.class.wait(5000L);
                }
                catch (final InterruptedException ex) {}
                if (WrapperManager.m_shutdownLocks > 0) {
                    WrapperManager.m_outInfo.println(getRes().getString("Waiting for {0} shutdown locks to be released...", new Integer(WrapperManager.m_shutdownLocks)));
                }
            }
        }
    }
    
    public static void restart() throws SecurityException {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("restart"));
        }
        WrapperManager.m_stoppingInit = true;
        if (WrapperManager.m_debug) {
            WrapperManager.m_outDebug.println(getRes().getString("{0} called by thread: {1}", "WrapperManager.restart()", Thread.currentThread().getName()));
        }
        restartInner();
    }
    
    public static void restartAndReturn() throws SecurityException {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("restart"));
        }
        WrapperManager.m_stoppingInit = true;
        Class class$;
        Class class$org$tanukisoftware$wrapper$WrapperManager;
        if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
            class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
        }
        else {
            class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
        }
        final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
        synchronized (class$) {
            if (WrapperManager.m_stopping) {
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(getRes().getString("{0} called by thread: {1} (already stopping)", "WrapperManager.restartAndReturn()", Thread.currentThread().getName()));
                }
                return;
            }
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("{0} called by thread: {1}", "WrapperManager.restartAndReturn()", Thread.currentThread().getName()));
            }
        }
        final Thread restarter = new Thread("Wrapper-Restarter") {
            public void run() {
                restartInner();
            }
        };
        restarter.setDaemon(false);
        restarter.start();
    }
    
    private static void restartInner() {
        Class class$;
        Class class$org$tanukisoftware$wrapper$WrapperManager;
        if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
            class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
        }
        else {
            class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
        }
        final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
        final boolean stopping;
        synchronized (class$) {
            stopping = WrapperManager.m_stopping;
            if (!stopping) {
                WrapperManager.m_stopping = true;
            }
        }
        if (!stopping) {
            sendCommand((byte)102, "restart");
        }
        try {
            Thread.sleep(1000L);
        }
        catch (final InterruptedException ex) {}
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                privilegedStopInner(0);
                return null;
            }
        });
    }
    
    public static void stop(final int exitCode) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("stop"));
        }
        WrapperManager.m_stoppingInit = true;
        if (WrapperManager.m_debug) {
            WrapperManager.m_outDebug.println(getRes().getString("{0} called by thread: {1}", "WrapperManager.stop(" + exitCode + ")", Thread.currentThread().getName()));
        }
        stopCommon(exitCode, 1000);
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                privilegedStopInner(exitCode);
                return null;
            }
        });
    }
    
    public static void stopAndReturn(final int exitCode) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("stop"));
        }
        WrapperManager.m_stoppingInit = true;
        Class class$;
        Class class$org$tanukisoftware$wrapper$WrapperManager;
        if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
            class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
        }
        else {
            class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
        }
        final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
        synchronized (class$) {
            if (WrapperManager.m_stopping) {
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(getRes().getString("{0} called by thread: {1} (already stopping)", "WrapperManager.stopAndReturn(" + exitCode + ")", Thread.currentThread().getName()));
                }
                return;
            }
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("{0} called by thread: {1}", "WrapperManager.stopAndReturn(" + exitCode + ")", Thread.currentThread().getName()));
            }
        }
        final Thread stopper = new Thread("Wrapper-Stopper") {
            public void run() {
                stopCommon(exitCode, 1000);
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    public Object run() {
                        privilegedStopInner(exitCode);
                        return null;
                    }
                });
            }
        };
        stopper.setDaemon(false);
        stopper.start();
    }
    
    public static void stopImmediate(final int exitCode) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("stopImmediate"));
        }
        if (WrapperManager.m_debug) {
            WrapperManager.m_outDebug.println(getRes().getString("{0} called by thread: {1}", "WrapperManager.stopImmediate(" + exitCode + ")", Thread.currentThread().getName()));
        }
        stopCommon(exitCode, 250);
        signalStopped(exitCode);
        if (WrapperManager.m_debug) {
            WrapperManager.m_outDebug.println(getRes().getString("WrapperManager stopped due to {0}", getRes().getString("Halt")));
        }
        WrapperManager.m_stopped = true;
        Runtime.getRuntime().halt(exitCode);
    }
    
    public static void signalStarting(final int waitHint) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("signalStarting"));
        }
        sendCommand((byte)105, Integer.toString(waitHint));
    }
    
    public static void signalStopping(final int waitHint) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("signalStopping"));
        }
        WrapperManager.m_stopping = true;
        sendCommand((byte)104, Integer.toString(waitHint));
    }
    
    public static void signalStopped(final int exitCode) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("signalStopped"));
        }
        WrapperManager.m_stopping = true;
        sendCommand((byte)107, Integer.toString(exitCode));
        try {
            Thread.sleep(250L);
        }
        catch (final InterruptedException ex) {}
    }
    
    public static boolean hasShutdownHookBeenTriggered() {
        return WrapperManager.m_hookTriggered;
    }
    
    public static void log(final int logLevel, final String message) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("log"));
        }
        if (logLevel < 1 || logLevel > 8) {
            throw new IllegalArgumentException(getRes().getString("The specified logLevel is not valid."));
        }
        if (message == null) {
            throw new IllegalArgumentException(getRes().getString("The message parameter can not be null."));
        }
        if (WrapperManager.m_lowLogLevel <= logLevel) {
            sendCommand((byte)(116 + logLevel), message);
        }
    }
    
    public static WrapperWin32Service[] listServices() throws SecurityException {
        final SecurityManager sm = System.getSecurityManager();
        if (sm == null) {
            throw new SecurityException(getRes().getString("A SecurityManager has not yet been set."));
        }
        sm.checkPermission(new WrapperPermission("listServices"));
        if (isNativeLibraryOk()) {
            return nativeListServices();
        }
        return null;
    }
    
    public static WrapperWin32Service sendServiceControlCode(final String serviceName, final int controlCode) throws WrapperServiceException, SecurityException {
        final SecurityManager sm = System.getSecurityManager();
        if (sm == null) {
            throw new SecurityException(getRes().getString("A SecurityManager has not yet been set."));
        }
        switch (controlCode) {
            case 65536: {
                final String action = WrapperServicePermission.ACTION_START;
                break;
            }
            case 1: {
                final String action = WrapperServicePermission.ACTION_STOP;
                break;
            }
            case 2: {
                final String action = WrapperServicePermission.ACTION_PAUSE;
                break;
            }
            case 3: {
                final String action = WrapperServicePermission.ACTION_CONTINUE;
                break;
            }
            case 4: {
                final String action = WrapperServicePermission.ACTION_INTERROGATE;
                break;
            }
            default: {
                if (controlCode >= 128 && controlCode <= 255) {
                    final String action = WrapperServicePermission.ACTION_USER_CODE;
                    break;
                }
                throw new IllegalArgumentException(getRes().getString("The specified controlCode is invalid."));
            }
        }
        final String actions;
        sm.checkPermission(new WrapperServicePermission(serviceName, actions));
        WrapperWin32Service service = null;
        if (isNativeLibraryOk()) {
            service = nativeSendServiceControlCode(serviceName, controlCode);
        }
        return service;
    }
    
    public static void addWrapperEventListener(final WrapperEventListener listener, final long mask) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            final StringBuffer sb = new StringBuffer();
            boolean first = true;
            if ((mask & 0x1L) != 0x0L) {
                first = false;
                sb.append(WrapperEventPermission.EVENT_TYPE_SERVICE);
            }
            if ((mask & 0x2L) != 0x0L) {
                if (first) {
                    first = false;
                }
                else {
                    sb.append(",");
                }
                sb.append(WrapperEventPermission.EVENT_TYPE_CONTROL);
            }
            if ((mask & 0x8L) != 0x0L) {
                first = false;
                sb.append(WrapperEventPermission.EVENT_TYPE_REMOTE_CONTROL);
            }
            if ((mask & 0xF000000000000000L) != 0x0L) {
                if (first) {
                    first = false;
                }
                else {
                    sb.append(",");
                }
                sb.append(WrapperEventPermission.EVENT_TYPE_CORE);
            }
            sm.checkPermission(new WrapperEventPermission(sb.toString()));
        }
        Class class$;
        Class class$org$tanukisoftware$wrapper$WrapperManager;
        if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
            class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
        }
        else {
            class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
        }
        final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
        synchronized (class$) {
            final WrapperEventListenerMask listenerMask = new WrapperEventListenerMask();
            listenerMask.m_listener = listener;
            listenerMask.m_mask = mask;
            WrapperManager.m_wrapperEventListenerMaskList.add(listenerMask);
            WrapperManager.m_wrapperEventListenerMasks = null;
        }
        updateWrapperEventListenerFlags();
    }
    
    public static void removeWrapperEventListener(final WrapperEventListener listener) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new WrapperPermission("removeWrapperEventListener"));
        }
        Class class$;
        Class class$org$tanukisoftware$wrapper$WrapperManager;
        if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
            class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
        }
        else {
            class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
        }
        final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
        synchronized (class$) {
            final Iterator iter = WrapperManager.m_wrapperEventListenerMaskList.iterator();
            while (iter.hasNext()) {
                final WrapperEventListenerMask listenerMask = iter.next();
                if (listenerMask.m_listener == listener) {
                    iter.remove();
                    WrapperManager.m_wrapperEventListenerMasks = null;
                    break;
                }
            }
        }
        updateWrapperEventListenerFlags();
    }
    
    public static File getWrapperLogFile() {
        final File logFile = WrapperManager.m_logFile;
        if (logFile == null) {
            throw new IllegalStateException(getRes().getString("Not yet initialized."));
        }
        return logFile;
    }
    
    private WrapperManager() {
    }
    
    private static void checkSecurityManager() {
        if (WrapperManager.m_securityManagerChecked) {
            return;
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Detected a SecurityManager: {0} ", securityManager.getClass().getName()));
            }
            try {
                securityManager.checkPermission(new AllPermission());
            }
            catch (final SecurityException e) {
                WrapperManager.m_outDebug.println();
                WrapperManager.m_outDebug.println(getRes().getString("WARNING - Detected that a SecurityManager has been installed but the "));
                WrapperManager.m_outDebug.println(getRes().getString("          wrapper.jar has not been granted the java.security.AllPermission"));
                WrapperManager.m_outDebug.println(getRes().getString("          permission.  This will most likely result in SecurityExceptions"));
                WrapperManager.m_outDebug.println(getRes().getString("          being thrown by the Wrapper."));
                WrapperManager.m_outDebug.println();
            }
            WrapperManager.m_securityManagerChecked = true;
        }
    }
    
    private static WrapperEventListenerMask[] getWrapperEventListenerMasks() {
        WrapperEventListenerMask[] listenerMasks = WrapperManager.m_wrapperEventListenerMasks;
        if (listenerMasks == null) {
            Class class$;
            Class class$org$tanukisoftware$wrapper$WrapperManager;
            if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
                class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
            }
            else {
                class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
            }
            final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
            synchronized (class$) {
                if (listenerMasks == null) {
                    listenerMasks = new WrapperEventListenerMask[WrapperManager.m_wrapperEventListenerMaskList.size()];
                    WrapperManager.m_wrapperEventListenerMaskList.toArray(listenerMasks);
                    WrapperManager.m_wrapperEventListenerMasks = listenerMasks;
                }
            }
        }
        return listenerMasks;
    }
    
    private static void updateWrapperEventListenerFlags() {
        boolean core = false;
        final WrapperEventListenerMask[] listenerMasks = getWrapperEventListenerMasks();
        for (int i = 0; i < listenerMasks.length; ++i) {
            final long mask = listenerMasks[i].m_mask;
            core |= ((mask & 0xF000000000000000L) != 0x0L);
        }
        WrapperManager.m_produceCoreEvents = core;
    }
    
    private static void fireWrapperEvent(final WrapperEvent event) {
        final long eventMask = event.getFlags();
        final WrapperEventListenerMask[] listenerMasks = getWrapperEventListenerMasks();
        for (int i = 0; i < listenerMasks.length; ++i) {
            final long listenerMask = listenerMasks[i].m_mask;
            if ((listenerMask & eventMask) != 0x0L) {
                final WrapperEventListener listener = listenerMasks[i].m_listener;
                try {
                    listener.fired(event);
                }
                catch (final Throwable t) {
                    WrapperManager.m_outError.println(getRes().getString("Encountered an uncaught exception while notifying WrapperEventListener of an event:"));
                    t.printStackTrace(WrapperManager.m_outError);
                }
            }
        }
    }
    
    private static void stopCommon(final int exitCode, final int delay) {
        Class class$;
        Class class$org$tanukisoftware$wrapper$WrapperManager;
        if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
            class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
        }
        else {
            class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
        }
        final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
        final boolean stopping;
        synchronized (class$) {
            stopping = WrapperManager.m_stopping;
            if (!stopping) {
                WrapperManager.m_stopping = true;
            }
        }
        if (!stopping) {
            sendCommand((byte)101, Integer.toString(exitCode));
            if (delay > 0) {
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(getRes().getString("Pausing for {0}ms to allow a clean shutdown...", new Integer(delay)));
                }
                try {
                    Thread.sleep(delay);
                }
                catch (final InterruptedException ex) {}
            }
        }
    }
    
    private static void dispose() {
        Class class$;
        Class class$org$tanukisoftware$wrapper$WrapperManager;
        if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
            class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
        }
        else {
            class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
        }
        final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
        synchronized (class$) {
            WrapperManager.m_disposed = true;
            closeBackend();
            try {
                Thread.sleep(500L);
            }
            catch (final InterruptedException ex) {}
        }
    }
    
    private static void startCompleted() {
        WrapperManager.m_startedTicks = getTicks();
        WrapperManager.m_startupRunner = null;
        checkSecurityManager();
        signalStarted();
        WrapperManager.class.notifyAll();
    }
    
    private static void startInner(final boolean block) {
        WrapperManager.m_starting = true;
        checkTmpDir();
        if (WrapperManager.m_listener == null) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("No WrapperListener has been set.  Nothing to start."));
            }
            startCompleted();
        }
        else {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("calling WrapperListener.start()"));
            }
            final Integer[] resultF = { null };
            final Throwable[] tF = { null };
            final Thread startRunner = new Thread("WrapperListener_start_runner") {
                public void run() {
                    if (WrapperManager.m_debug) {
                        WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("WrapperListener.start runner thread started."));
                    }
                    final Thread thisThread = Thread.currentThread();
                    thisThread.setPriority(5);
                    while (true) {
                        if (WrapperManager.m_debug) {
                            WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("Application start main thread: {0} Priority: {1}", thisThread.getName(), new Integer(thisThread.getPriority())));
                            try {
                                resultF[0] = WrapperManager.m_listener.start(WrapperManager.m_args);
                            }
                            catch (final Throwable t) {
                                tF[0] = t;
                            }
                            finally {
                                thisThread.setPriority(10);
                                if (tF[0] != null) {
                                    WrapperManager.m_outError.println(WrapperManager.getRes().getString("Error in WrapperListener.start callback.  {0}", tF[0]));
                                    tF[0].printStackTrace(WrapperManager.m_outError);
                                    privilegedStopInner(1);
                                    return;
                                }
                                if (WrapperManager.m_debug) {
                                    WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("returned from WrapperListener.start()"));
                                }
                                if (resultF[0] != null) {
                                    final int exitCode = resultF[0];
                                    if (WrapperManager.m_debug) {
                                        WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("WrapperListener.start() returned an exit code of {0}.", new Integer(exitCode)));
                                    }
                                    WrapperManager.stop(exitCode);
                                    return;
                                }
                                Class class$;
                                Class class$org$tanukisoftware$wrapper$WrapperManager;
                                if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
                                    class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = WrapperManager.class$("org.tanukisoftware.wrapper.WrapperManager")));
                                }
                                else {
                                    class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
                                }
                                final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
                                synchronized (class$) {
                                    startCompleted();
                                }
                                if (WrapperManager.m_debug) {
                                    WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("WrapperListener.start runner thread stopped."));
                                }
                            }
                            return;
                        }
                        continue;
                    }
                }
            };
            startRunner.setDaemon(false);
            startRunner.start();
            if (block) {
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(getRes().getString("Waiting for WrapperListener.start runner thread to complete."));
                }
                while (startRunner != null && startRunner.isAlive()) {
                    try {
                        WrapperManager.class.wait();
                    }
                    catch (final InterruptedException e) {}
                }
            }
        }
    }
    
    private static void shutdownJVM(final int exitCode) {
        if (WrapperManager.m_debug) {
            WrapperManager.m_outDebug.println(getRes().getString("shutdownJVM({0}) Thread: {1}", new Integer(exitCode), Thread.currentThread().getName()));
        }
        waitForShutdownLocks();
        signalStopped(exitCode);
        dispose();
        WrapperManager.m_shutdownJVMComplete = true;
        if (Thread.currentThread() != WrapperManager.m_hook) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("calling System.exit({0})", new Integer(exitCode)));
            }
            safeSystemExit(exitCode);
        }
    }
    
    private static void safeSystemExit(final int exitCode) {
        if (WrapperManager.m_debug) {
            WrapperManager.m_outDebug.println(getRes().getString("WrapperManager stopped due to {0}", getRes().getString("System Exit")));
        }
        WrapperManager.m_stopped = true;
        try {
            System.exit(exitCode);
        }
        catch (final IllegalThreadStateException e) {
            WrapperManager.m_outError.println(getRes().getString("Attempted System.exit({0}) call failed: {1}", new Integer(exitCode), e.toString()));
            WrapperManager.m_outError.println(getRes().getString("   Trying Runtime.halt({0})", new Integer(exitCode)));
            Runtime.getRuntime().halt(exitCode);
        }
    }
    
    private static void privilegedStopInner(final int exitCode) {
        Class class$;
        Class class$org$tanukisoftware$wrapper$WrapperManager;
        if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
            class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
        }
        else {
            class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
        }
        final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
        boolean block;
        synchronized (class$) {
            WrapperManager.m_stopping = true;
            if (WrapperManager.m_stoppingThread == null) {
                WrapperManager.m_stoppingThread = Thread.currentThread();
                block = false;
            }
            else {
                if (Thread.currentThread() == WrapperManager.m_stoppingThread) {
                    throw new IllegalStateException(getRes().getString("WrapperManager.stop() can not be called recursively."));
                }
                block = true;
            }
        }
        if (block) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Thread, {0}, waiting for the JVM to exit.", Thread.currentThread().getName()));
                if (Thread.currentThread() == WrapperManager.m_hook && !WrapperManager.m_hookRemoveFailed) {
                    WrapperManager.m_outDebug.println(getRes().getString("System.exit appears to have been called from within the\n  WrapperListener.stop() method.  If possible the application\n  should be modified to avoid this behavior.\n  To avoid a deadlock, this thread will only wait 5 seconds\n  for the application to shutdown.  This may result in the\n  application failing to shutdown completely before the JVM\n  exits.  Removing the offending System.exit call will\n  resolve this."));
                }
            }
            int loops = 0;
            final int wait = 50;
            while (true) {
                try {
                    Thread.sleep(wait);
                }
                catch (final InterruptedException ex) {}
                if (Thread.currentThread() == WrapperManager.m_hook && (WrapperManager.m_shutdownJVMComplete || loops > 5000 / wait)) {
                    break;
                }
                ++loops;
            }
            if (!WrapperManager.m_shutdownJVMComplete && WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Thread, {0}, continuing after 5 seconds.", Thread.currentThread().getName()));
            }
            signalStopped(WrapperManager.m_exitCode);
            return;
        }
        if (WrapperManager.m_debug) {
            WrapperManager.m_outDebug.println(getRes().getString("Thread, {0}, handling the shutdown process.", Thread.currentThread().getName()));
        }
        WrapperManager.m_exitCode = exitCode;
        if (Thread.currentThread() != WrapperManager.m_hook && !WrapperManager.m_hookTriggered && WrapperManager.m_hook != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(WrapperManager.m_hook);
            }
            catch (final AccessControlException e) {
                WrapperManager.m_outError.println(getRes().getString("Unable to remove the Wrapper''s shutdownhook: {0}", e));
                WrapperManager.m_hookRemoveFailed = true;
            }
        }
        int code = exitCode;
        if ((WrapperManager.m_listenerForceStop && WrapperManager.m_starting) || WrapperManager.m_started) {
            if (WrapperManager.m_listener == null) {
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(getRes().getString("No WrapperListener has been set.  Nothing to stop."));
                }
            }
            else {
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(getRes().getString("calling listener.stop()"));
                }
                if (Thread.currentThread().isDaemon()) {
                    final Integer[] codeF = { new Integer(code) };
                    Thread stopRunner = new Thread("WrapperListener_stop_runner") {
                        public void run() {
                            if (WrapperManager.m_debug) {
                                WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("WrapperListener.stop runner thread started."));
                            }
                            final Thread thisThread = Thread.currentThread();
                            thisThread.setPriority(5);
                            while (true) {
                                if (WrapperManager.m_debug) {
                                    WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("Application stop main thread: {0} Priority: {1}", thisThread.getName(), new Integer(thisThread.getPriority())));
                                    try {
                                        codeF[0] = new Integer(WrapperManager.m_listener.stop(codeF[0]));
                                    }
                                    catch (final Throwable t) {
                                        WrapperManager.m_outError.println(WrapperManager.getRes().getString("Error in WrapperListener.stop callback."));
                                        t.printStackTrace(WrapperManager.m_outError);
                                    }
                                    finally {
                                        if (WrapperManager.m_debug) {
                                            WrapperManager.m_outDebug.println(WrapperManager.getRes().getString("WrapperListener.stop runner thread stopped."));
                                        }
                                    }
                                    return;
                                }
                                continue;
                            }
                        }
                    };
                    stopRunner.setDaemon(false);
                    stopRunner.start();
                    if (WrapperManager.m_debug) {
                        WrapperManager.m_outDebug.println(getRes().getString("Waiting for WrapperListener.stop runner thread to complete."));
                    }
                    while (stopRunner != null && stopRunner.isAlive()) {
                        try {
                            stopRunner.join();
                            stopRunner = null;
                        }
                        catch (final InterruptedException e2) {}
                    }
                    code = codeF[0];
                }
                else {
                    try {
                        code = WrapperManager.m_listener.stop(code);
                    }
                    catch (final Throwable t) {
                        WrapperManager.m_outError.println(getRes().getString("Error in WrapperListener.stop callback."));
                        t.printStackTrace(WrapperManager.m_outError);
                    }
                }
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(getRes().getString("returned from listener.stop() -> {0}", new Integer(code)));
                }
            }
        }
        shutdownJVM(code);
    }
    
    private static void signalStarted() {
        sendCommand((byte)106, "");
        WrapperManager.m_started = true;
    }
    
    private static void controlEvent(final int event) {
        String eventName = null;
        boolean ignore = false;
        switch (event) {
            case 200: {
                eventName = "WRAPPER_CTRL_C_EVENT";
                ignore = WrapperManager.m_ignoreSignals;
                break;
            }
            case 201: {
                eventName = "WRAPPER_CTRL_CLOSE_EVENT";
                ignore = WrapperManager.m_ignoreSignals;
                break;
            }
            case 202: {
                eventName = "WRAPPER_CTRL_LOGOFF_EVENT";
                ignore = false;
                break;
            }
            case 203: {
                eventName = "WRAPPER_CTRL_SHUTDOWN_EVENT";
                ignore = false;
                break;
            }
            case 204: {
                eventName = "WRAPPER_CTRL_TERM_EVENT";
                ignore = WrapperManager.m_ignoreSignals;
                break;
            }
            case 205: {
                eventName = "WRAPPER_CTRL_HUP_EVENT";
                ignore = WrapperManager.m_ignoreSignals;
                break;
            }
            case 206: {
                eventName = "WRAPPER_CTRL_USR1_EVENT";
                ignore = WrapperManager.m_ignoreSignals;
                break;
            }
            case 207: {
                eventName = "WRAPPER_CTRL_USR2_EVENT";
                ignore = WrapperManager.m_ignoreSignals;
                break;
            }
            default: {
                eventName = getRes().getString("Unexpected event: {0}", new Integer(event));
                ignore = false;
                break;
            }
        }
        final WrapperControlEvent controlEvent = new WrapperControlEvent(event, eventName);
        if (ignore) {
            controlEvent.consume();
        }
        fireWrapperEvent(controlEvent);
        if (ignore) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Ignoring control event({0})", eventName));
            }
        }
        else if (controlEvent.isConsumed()) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Control event({0}) was consumed by user listener.", eventName));
            }
        }
        else {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Processing control event({0})", eventName));
            }
            if (WrapperManager.m_listener != null) {
                try {
                    WrapperManager.m_listener.controlEvent(event);
                }
                catch (final Throwable t) {
                    WrapperManager.m_outError.println(getRes().getString("Error in WrapperListener.controlEvent callback."));
                    t.printStackTrace(WrapperManager.m_outError);
                }
            }
            else {
                stop(0);
            }
        }
    }
    
    private static void readProperties(final String rawProps) {
        final WrapperProperties properties = new WrapperProperties();
        final int len = rawProps.length();
        int first = 0;
        while (first < len) {
            final StringBuffer sb = new StringBuffer();
            boolean foundEnd = false;
            do {
                final int pos = rawProps.indexOf(WrapperManager.PROPERTY_SEPARATOR, first);
                if (pos >= 0) {
                    if (pos > 0) {
                        sb.append(rawProps.substring(first, pos));
                    }
                    if (pos < len - 1) {
                        if (rawProps.charAt(pos + 1) == WrapperManager.PROPERTY_SEPARATOR) {
                            sb.append(WrapperManager.PROPERTY_SEPARATOR);
                            first = pos + 2;
                        }
                        else {
                            foundEnd = true;
                            first = pos + 1;
                        }
                    }
                    else {
                        foundEnd = true;
                        first = pos + 1;
                    }
                }
                else {
                    sb.append(rawProps.substring(first));
                    foundEnd = true;
                    first = len;
                }
            } while (!foundEnd);
            final String property = sb.toString();
            final int pos2 = property.indexOf(61);
            if (pos2 > 0) {
                final String key = property.substring(0, pos2);
                String value;
                if (pos2 < property.length() - 1) {
                    value = property.substring(pos2 + 1);
                }
                else {
                    value = "";
                }
                properties.setProperty(key, value);
                if (!key.equals("wrapper.ignore_user_logoffs")) {
                    continue;
                }
                WrapperManager.m_ignoreUserLogoffs = value.equalsIgnoreCase("true");
            }
        }
        properties.lock();
        WrapperManager.m_properties = properties;
    }
    
    private static synchronized void openBackendSocket() {
        if (WrapperManager.m_debug) {
            WrapperManager.m_outDebug.println(getRes().getString("Open socket to Wrapper...{0}", Thread.currentThread().getName()));
        }
        InetAddress iNetAddress;
        try {
            iNetAddress = InetAddress.getByName(WrapperManager.m_wrapperPortAddress);
        }
        catch (final UnknownHostException e) {
            WrapperManager.m_outError.println(getRes().getString("Unable to resolve localhost name: {0}", e));
            WrapperManager.m_outError.println(getRes().getString("Exiting JVM..."));
            stop(1);
            return;
        }
        boolean connected = false;
        int tryPort;
        boolean fixedPort;
        if (WrapperManager.m_jvmPort >= 0) {
            tryPort = WrapperManager.m_jvmPort;
            fixedPort = true;
        }
        else {
            tryPort = WrapperManager.m_jvmPortMin;
            fixedPort = false;
        }
        SocketException causeException = null;
        do {
            int portStatus;
            if (isNativeLibraryOk()) {
                try {
                    portStatus = nativeGetPortStatus(tryPort, WrapperManager.m_wrapperPortAddress, (WrapperManager.m_backendType == 2) ? 1 : 0);
                }
                catch (final UnsatisfiedLinkError e2) {
                    WrapperManager.m_outError.println(getRes().getString("Unable to precheck status of port {0} due to: {1}", new Integer(tryPort), e2.toString()));
                    portStatus = -1;
                }
            }
            else {
                portStatus = -1;
            }
            Label_0466: {
                if (portStatus <= 0) {
                    try {
                        WrapperManager.m_backendSocket = new Socket(iNetAddress, WrapperManager.m_port, iNetAddress, tryPort);
                        if (WrapperManager.m_debug) {
                            WrapperManager.m_outDebug.println(getRes().getString("Opened Socket from {0} to {1}", new Integer(tryPort), new Integer(WrapperManager.m_port)));
                        }
                        connected = true;
                        break;
                    }
                    catch (final SocketException e3) {
                        final String eMessage = e3.getMessage();
                        if (e3 instanceof ConnectException) {
                            WrapperManager.m_outError.println(getRes().getString("Failed to connect to the Wrapper at port {0}. Cause: {1}", new Integer(WrapperManager.m_port), e3));
                            WrapperManager.m_outError.println("Exiting JVM...");
                            stopImmediate(1);
                            WrapperManager.m_backendSocket = null;
                            return;
                        }
                        if (e3 instanceof BindException || (eMessage != null && (eMessage.indexOf("errno: 48") >= 0 || eMessage.indexOf("Address already in use") >= 0)) || eMessage.indexOf("Unrecognized Windows Sockets error: 0: JVM_Bind") >= 0) {
                            if (WrapperManager.m_debug) {
                                WrapperManager.m_outDebug.println(getRes().getString("Unable to open socket to Wrapper from port {0}, already in use.", new Integer(tryPort)));
                            }
                            if (causeException == null) {
                                causeException = e3;
                            }
                            break Label_0466;
                        }
                        WrapperManager.m_outError.println(getRes().getString("Unexpected exception opening backend socket: {0}", e3));
                        WrapperManager.m_backendSocket = null;
                        return;
                    }
                    catch (final IOException e4) {
                        WrapperManager.m_outError.println(getRes().getString("Unable to open backend socket: {0}", e4));
                        WrapperManager.m_backendSocket = null;
                        return;
                    }
                }
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(getRes().getString("Unable to open socket to Wrapper from port {0}, already in use. ({1})", new Integer(tryPort), Integer.toString(portStatus)));
                }
            }
            if (fixedPort) {
                tryPort = WrapperManager.m_jvmPortMin;
                fixedPort = false;
            }
            else {
                ++tryPort;
            }
        } while (tryPort <= WrapperManager.m_jvmPortMax);
        if (connected) {
            if (WrapperManager.m_jvmPort >= 0 && WrapperManager.m_jvmPort != tryPort) {
                WrapperManager.m_outInfo.println(getRes().getString("Port {0} already in use, using port {1} instead.", new Integer(WrapperManager.m_jvmPort), new Integer(tryPort)));
            }
        }
        else {
            if (WrapperManager.m_jvmPortMax > WrapperManager.m_jvmPortMin) {
                WrapperManager.m_outError.println(getRes().getString("Failed to connect to the Wrapper at port {0} by binding to any ports in the range {1} to {2}.  Cause: {3}", new Integer(WrapperManager.m_port), new Integer(WrapperManager.m_jvmPortMin), new Integer(WrapperManager.m_jvmPortMax), causeException));
            }
            else {
                WrapperManager.m_outError.println(getRes().getString("Failed to connect to the Wrapper at port {0} by binding to port {1}.  Cause: {2}", new Integer(WrapperManager.m_port), new Integer(WrapperManager.m_jvmPortMin), causeException));
            }
            WrapperManager.m_outError.println(getRes().getString("Exiting JVM..."));
            stopImmediate(1);
        }
        try {
            WrapperManager.m_backendSocket.setTcpNoDelay(true);
            if (WrapperManager.m_soTimeout >= 0) {
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(getRes().getString("Setting backend socket SO_TIMEOUT to {0}ms from {1}ms.", new Integer(WrapperManager.m_soTimeout), new Integer(WrapperManager.m_backendSocket.getSoTimeout())));
                }
                WrapperManager.m_backendSocket.setSoTimeout(WrapperManager.m_soTimeout);
            }
            WrapperManager.m_backendOS = WrapperManager.m_backendSocket.getOutputStream();
            WrapperManager.m_backendIS = WrapperManager.m_backendSocket.getInputStream();
        }
        catch (final IOException e5) {
            WrapperManager.m_outError.println(e5);
            closeBackend();
            return;
        }
        WrapperManager.m_backendConnected = true;
    }
    
    private static synchronized void openBackendPipe() {
        String s;
        if (isWindows()) {
            s = "\\\\.\\pipe\\wrapper-" + getWrapperPID() + "-" + getJVMId();
        }
        else {
            s = "/tmp/wrapper-" + getWrapperPID() + "-" + getJVMId();
        }
        try {
            WrapperManager.m_backendIS = new FileInputStream(new File(s + "-out"));
            WrapperManager.m_backendOS = new FileOutputStream(new File(s + "-in"));
        }
        catch (final IOException e) {
            WrapperManager.m_outInfo.println("write error " + e);
            e.printStackTrace();
            closeBackend();
            return;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            closeBackend();
            return;
        }
        WrapperManager.m_backendConnected = true;
    }
    
    private static synchronized void openBackend() {
        WrapperManager.m_backendConnected = false;
        if (WrapperManager.m_backendType == 4) {
            openBackendPipe();
        }
        else {
            openBackendSocket();
        }
        if (!WrapperManager.m_backendConnected) {
            WrapperManager.m_outError.println(getRes().getString("The backend could not be initialized.  Restart to resync with the Wrapper."));
            restart();
            return;
        }
        sendCommand((byte)110, WrapperManager.m_key);
        if (WrapperManager.m_pendingStopMessage != null) {
            WrapperManager.m_outDebug.println(getRes().getString("Resend pending packet {0} : {1}", getPacketCodeName((byte)101), WrapperManager.m_pendingStopMessage));
            sendCommand((byte)101, WrapperManager.m_pendingStopMessage);
            WrapperManager.m_pendingStopMessage = null;
        }
    }
    
    private static synchronized void closeBackend() {
        if (WrapperManager.m_backendConnected) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Closing backend connection."));
            }
            final long start = System.currentTimeMillis();
            while (WrapperManager.m_runningExecs > 0) {
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(getRes().getString("Waiting for {0} threads to finish launching child processes...", new Integer(WrapperManager.m_runningExecs)));
                }
                try {
                    WrapperManager.class.wait(1000L);
                }
                catch (final InterruptedException ex) {}
                if (System.currentTimeMillis() - start > 30000L) {
                    WrapperManager.m_outError.println(getRes().getString("Timed out waiting for {0} threads to finish launching child processes.", new Integer(WrapperManager.m_runningExecs)));
                    break;
                }
            }
            WrapperManager.m_backendConnected = false;
            final Thread commRunner = WrapperManager.m_commRunner;
            if (commRunner != null) {
                try {
                    commRunner.interrupt();
                }
                catch (final SecurityException e) {
                    WrapperManager.m_outError.println(getRes().getString("Failed to interrupt communications thread: {0}", e.getMessage()));
                }
            }
        }
        if (WrapperManager.m_backendOS != null) {
            try {
                WrapperManager.m_backendOS.close();
            }
            catch (final IOException e2) {
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(getRes().getString("Unable to close backend output stream: {0}", e2.toString()));
                }
            }
            WrapperManager.m_backendOS = null;
        }
        if (WrapperManager.m_backendIS != null) {
            WrapperManager.m_backendIS = null;
        }
        if (WrapperManager.m_backendSocket != null) {
            try {
                WrapperManager.m_backendSocket.close();
            }
            catch (final IOException e2) {
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(getRes().getString("Unable to close backend socket: {0}", e2.toString()));
                }
            }
            WrapperManager.m_backendSocket = null;
        }
    }
    
    private static String getPacketCodeName(final byte code) {
        String name = null;
        switch (code) {
            case 100: {
                name = "START";
                break;
            }
            case 101: {
                name = "STOP";
                break;
            }
            case 102: {
                name = "RESTART";
                break;
            }
            case 103: {
                name = "PING";
                break;
            }
            case 104: {
                name = "STOP_PENDING";
                break;
            }
            case 105: {
                name = "START_PENDING";
                break;
            }
            case 106: {
                name = "STARTED";
                break;
            }
            case 107: {
                name = "STOPPED";
                break;
            }
            case 110: {
                name = "KEY";
                break;
            }
            case 111: {
                name = "BADKEY";
                break;
            }
            case 112: {
                name = "LOW_LOG_LEVEL";
                break;
            }
            case 113: {
                name = "PING_TIMEOUT";
                break;
            }
            case 114: {
                name = "SERVICE_CONTROL_CODE";
                break;
            }
            case 115: {
                name = "PROPERTIES";
                break;
            }
            case 117: {
                name = "LOG(DEBUG)";
                break;
            }
            case 118: {
                name = "LOG(INFO)";
                break;
            }
            case 119: {
                name = "LOG(STATUS)";
                break;
            }
            case 120: {
                name = "LOG(WARN)";
                break;
            }
            case 121: {
                name = "LOG(ERROR)";
                break;
            }
            case 122: {
                name = "LOG(FATAL)";
                break;
            }
            case 123: {
                name = "LOG(ADVICE)";
                break;
            }
            case 124: {
                name = "LOG(NOTICE)";
                break;
            }
            case -124: {
                name = "CHILD_LAUNCH";
                break;
            }
            case -123: {
                name = "CHILD_TERM";
                break;
            }
            case -122: {
                name = "LOGFILE";
                break;
            }
            case -121: {
                name = "CHECK_DEADLOCK";
                break;
            }
            case -120: {
                name = "DEADLOCK";
                break;
            }
            case -119: {
                name = "APPEAR_ORPHAN";
                break;
            }
            case -118: {
                name = "PAUSE";
                break;
            }
            case -117: {
                name = "RESUME";
                break;
            }
            case -116: {
                name = "GC";
                break;
            }
            case -115: {
                name = "FIRE_USER_EVENT";
                break;
            }
            case -114: {
                name = "SECOND_INVOCATION_EVENT";
                break;
            }
            default: {
                name = "UNKNOWN(" + code + ")";
                break;
            }
        }
        return name;
    }
    
    private static synchronized void sendCommand(final byte code, final String message) {
        if (WrapperManager.m_debug) {
            if (code != 103 || !message.startsWith("silent")) {
                if (!WrapperManager.m_backendConnected) {
                    WrapperManager.m_outDebug.println(getRes().getString("Backend not connected, not sending packet {0} : {1}", getPacketCodeName(code), message));
                    if (code == 101) {
                        WrapperManager.m_pendingStopMessage = message;
                    }
                }
                else {
                    WrapperManager.m_outDebug.println(getRes().getString("Send a packet {0} : {1}", getPacketCodeName(code), message));
                }
            }
        }
        boolean sentCommand = false;
        if (!WrapperManager.m_appearHung) {
            if (code == 105 || code == 106) {
                WrapperManager.m_lastPingTicks = getTicks();
            }
            if (WrapperManager.m_backendConnected) {
                try {
                    final byte[] messageBytes = message.getBytes();
                    if (WrapperManager.m_commandBuffer.length < messageBytes.length + 2) {
                        WrapperManager.m_commandBuffer = new byte[messageBytes.length + 2];
                    }
                    WrapperManager.m_commandBuffer[0] = code;
                    System.arraycopy(messageBytes, 0, WrapperManager.m_commandBuffer, 1, messageBytes.length);
                    final int len = messageBytes.length + 2;
                    WrapperManager.m_commandBuffer[len - 1] = 0;
                    WrapperManager.m_backendOS.write(WrapperManager.m_commandBuffer, 0, len);
                    WrapperManager.m_backendOS.flush();
                    sentCommand = true;
                }
                catch (final IOException e) {
                    WrapperManager.m_outError.println(e);
                    e.printStackTrace(WrapperManager.m_outError);
                    closeBackend();
                }
            }
        }
        if (!sentCommand) {
            switch (code) {
                case -124: {
                    WrapperManager.m_outError.println(getRes().getString("Failed to notify the Wrapper process that child with PID={0} was launched.  The Wrapper will not be able to make sure it is terminated when the Java process exits.", message));
                    break;
                }
                case -123: {
                    if (WrapperManager.m_debug) {
                        WrapperManager.m_outDebug.println(getRes().getString("Failed to notify the Wrapper process that child with PID={0} completed.  The Wrapper will recover on its own.", message));
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    private static void handleBackend() {
        final WrapperPingEvent pingEvent = new WrapperPingEvent();
        try {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("handleBackend()"));
            }
            final DataInputStream is = new DataInputStream(WrapperManager.m_backendIS);
            while (!WrapperManager.m_disposed) {
                try {
                    final byte code = is.readByte();
                    int i = 0;
                    byte b;
                    do {
                        b = is.readByte();
                        if (b != 0) {
                            if (i >= WrapperManager.m_backendReadBuffer.length) {
                                final byte[] tmp = WrapperManager.m_backendReadBuffer;
                                System.arraycopy(tmp, 0, WrapperManager.m_backendReadBuffer = new byte[tmp.length + 256], 0, tmp.length);
                            }
                            WrapperManager.m_backendReadBuffer[i] = b;
                            ++i;
                        }
                    } while (b != 0);
                    String msg;
                    if (!isZOS()) {
                        msg = new String(WrapperManager.m_backendReadBuffer, 0, i, "UTF-8");
                    }
                    else {
                        msg = new String(WrapperManager.m_backendReadBuffer, 0, i);
                    }
                    if (WrapperManager.m_appearHung) {
                        continue;
                    }
                    if (WrapperManager.m_debug) {
                        String logMsg;
                        if (code == 115) {
                            logMsg = getRes().getString("(Property Values, Size={0})", Integer.toString(i));
                        }
                        else {
                            logMsg = msg;
                        }
                        if (code != 103 || !msg.startsWith("silent")) {
                            WrapperManager.m_outDebug.println(getRes().getString("Received a packet {0} : {1}", getPacketCodeName(code), logMsg));
                        }
                    }
                    if (WrapperManager.m_slowSeconds > 0) {
                        if (WrapperManager.m_debug) {
                            WrapperManager.m_outDebug.println(getRes().getString("  Delay packet processing by {0} seconds.", new Integer(WrapperManager.m_slowSeconds)));
                        }
                        try {
                            Thread.sleep(WrapperManager.m_slowSeconds * 1000);
                        }
                        catch (final InterruptedException ex) {}
                    }
                    switch (code) {
                        case 100: {
                            if (!WrapperManager.m_stoppingInit) {
                                startInner(false);
                                continue;
                            }
                            if (WrapperManager.m_debug) {
                                WrapperManager.m_outDebug.println(getRes().getString("Java stop initiated.  Skipping application startup."));
                                continue;
                            }
                            continue;
                        }
                        case 101: {
                            if (!WrapperManager.m_stopping) {
                                privilegedStopInner(0);
                                continue;
                            }
                            continue;
                        }
                        case 103: {
                            WrapperManager.m_lastPingTicks = getTicks();
                            sendCommand((byte)103, msg);
                            if (WrapperManager.m_produceCoreEvents) {
                                fireWrapperEvent(pingEvent);
                                continue;
                            }
                            continue;
                        }
                        case -121: {
                            final boolean deadLocked = checkDeadlocks();
                            if (deadLocked) {
                                sendCommand((byte)(-120), "deadLock");
                                continue;
                            }
                            continue;
                        }
                        case 111: {
                            WrapperManager.m_outError.println(getRes().getString("Authorization key rejected by Wrapper."));
                            WrapperManager.m_outError.println(getRes().getString("Exiting JVM..."));
                            closeBackend();
                            privilegedStopInner(1);
                            continue;
                        }
                        case 112: {
                            try {
                                WrapperManager.m_lowLogLevel = Integer.parseInt(msg);
                                WrapperManager.m_debug = (WrapperManager.m_lowLogLevel <= 1);
                                if (!WrapperManager.m_debug) {
                                    continue;
                                }
                                WrapperManager.m_outDebug.println(getRes().getString("LowLogLevel from Wrapper is {0}", new Integer(WrapperManager.m_lowLogLevel)));
                            }
                            catch (final NumberFormatException e) {
                                WrapperManager.m_outError.println(getRes().getString("Encountered an Illegal LowLogLevel from the Wrapper: {0}", msg));
                            }
                            continue;
                        }
                        case 113: {
                            continue;
                        }
                        case 114: {
                            try {
                                final int serviceControlCode = Integer.parseInt(msg);
                                if (WrapperManager.m_debug) {
                                    WrapperManager.m_outDebug.println(getRes().getString("ServiceControlCode from Wrapper with code {0}", new Integer(serviceControlCode)));
                                }
                                final WrapperServiceControlEvent event = new WrapperServiceControlEvent(serviceControlCode);
                                fireWrapperEvent(event);
                            }
                            catch (final NumberFormatException e) {
                                WrapperManager.m_outError.println(getRes().getString("Encountered an Illegal ServiceControlCode from the Wrapper: {0}", msg));
                            }
                            continue;
                        }
                        case -118: {
                            try {
                                final int actionSourceCode = Integer.parseInt(msg);
                                if (WrapperManager.m_debug) {
                                    WrapperManager.m_outDebug.println(getRes().getString("Pause from Wrapper with action source: {0}", WrapperServiceActionEvent.getSourceCodeName(actionSourceCode)));
                                }
                                final WrapperServicePauseEvent event2 = new WrapperServicePauseEvent(actionSourceCode);
                                fireWrapperEvent(event2);
                            }
                            catch (final NumberFormatException e) {
                                WrapperManager.m_outError.println(getRes().getString("Encountered an Illegal action source code from the Wrapper: {0}", msg));
                            }
                            continue;
                        }
                        case -117: {
                            try {
                                final int actionSourceCode = Integer.parseInt(msg);
                                if (WrapperManager.m_debug) {
                                    WrapperManager.m_outDebug.println(getRes().getString("Resume from Wrapper with action source: {0}", WrapperServiceActionEvent.getSourceCodeName(actionSourceCode)));
                                }
                                final WrapperServiceResumeEvent event3 = new WrapperServiceResumeEvent(actionSourceCode);
                                fireWrapperEvent(event3);
                            }
                            catch (final NumberFormatException e) {
                                WrapperManager.m_outError.println(getRes().getString("Encountered an Illegal action source code from the Wrapper: {0}", msg));
                            }
                            continue;
                        }
                        case -116: {
                            try {
                                final int actionSourceCode = Integer.parseInt(msg);
                                if (WrapperManager.m_debug) {
                                    WrapperManager.m_outDebug.println(getRes().getString("Garbage Collection request from Wrapper with action source: {0}", WrapperServiceActionEvent.getSourceCodeName(actionSourceCode)));
                                }
                                System.gc();
                            }
                            catch (final NumberFormatException e) {
                                WrapperManager.m_outError.println(getRes().getString("Encountered an Illegal action source code from the Wrapper: {0}", msg));
                            }
                            continue;
                        }
                        case 115: {
                            readProperties(msg);
                            continue;
                        }
                        case -122: {
                            WrapperManager.m_logFile = new File(msg);
                            final WrapperLogFileChangedEvent event4 = new WrapperLogFileChangedEvent(WrapperManager.m_logFile);
                            fireWrapperEvent(event4);
                            continue;
                        }
                        case -114: {
                            final WrapperSecondInvocationEvent secondInvocationEvent = new WrapperSecondInvocationEvent();
                            fireWrapperEvent(secondInvocationEvent);
                            sendCommand((byte)(-114), "");
                            continue;
                        }
                        default: {
                            WrapperManager.m_outInfo.println(getRes().getString("Wrapper code received an unknown packet type: {0}", new Integer(code)));
                            continue;
                        }
                    }
                }
                catch (final SocketTimeoutException e2) {
                    if (!WrapperManager.m_debug) {
                        continue;
                    }
                    WrapperManager.m_outDebug.println(getRes().getString("Backend socket timed out.  Attempting to continue.  (SO_TIMEOUT={0}ms.)", new Integer(WrapperManager.m_backendSocket.getSoTimeout())));
                }
            }
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Backend handler loop completed.  Disposed: {0}", WrapperManager.m_disposed ? "True" : "False"));
            }
        }
        catch (final SocketException e3) {
            if (WrapperManager.m_debug) {
                if (WrapperManager.m_backendSocket == null) {
                    WrapperManager.m_outDebug.println(getRes().getString("Closed backend socket (Normal): {0}", e3));
                }
                else {
                    WrapperManager.m_outDebug.println(getRes().getString("Closed backend socket: {0}", e3));
                }
            }
        }
        catch (final IOException e4) {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Closed backend (Normal): {0}", e4));
            }
        }
    }
    
    private static void startRunner() {
        if (isControlledByNativeWrapper()) {
            if (WrapperManager.m_commRunner == null) {
                (WrapperManager.m_commRunner = new Thread(WrapperManager.m_instance, "Wrapper-Connection")).setDaemon(true);
                WrapperManager.m_commRunner.start();
            }
            Class class$;
            Class class$org$tanukisoftware$wrapper$WrapperManager;
            if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
                class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
            }
            else {
                class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
            }
            final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
            synchronized (class$) {
                while (!WrapperManager.m_commRunnerStarted) {
                    try {
                        WrapperManager.class.wait(100L);
                    }
                    catch (final InterruptedException e) {}
                }
                return;
            }
        }
        Class class$2;
        Class class$org$tanukisoftware$wrapper$WrapperManager2;
        if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
            class$org$tanukisoftware$wrapper$WrapperManager2 = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$2 = class$("org.tanukisoftware.wrapper.WrapperManager")));
        }
        else {
            class$2 = (class$org$tanukisoftware$wrapper$WrapperManager2 = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
        }
        final Class clazz2 = class$org$tanukisoftware$wrapper$WrapperManager2;
        synchronized (class$2) {
            WrapperManager.m_commRunnerStarted = true;
            WrapperManager.class.notifyAll();
        }
    }
    
    public void run() {
        if (Thread.currentThread() != WrapperManager.m_commRunner) {
            throw new IllegalStateException(getRes().getString("Only the communications runner thread is allowed to call this method."));
        }
        if (WrapperManager.m_debug) {
            WrapperManager.m_outDebug.println(getRes().getString("Communications runner thread started."));
        }
        Thread.currentThread().setPriority(10);
        WrapperManager.m_lastPingTicks = getTicks();
        try {
            openBackend();
            if (!WrapperManager.m_commRunnerStarted) {
                Class class$;
                Class class$org$tanukisoftware$wrapper$WrapperManager;
                if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
                    class$org$tanukisoftware$wrapper$WrapperManager = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$ = class$("org.tanukisoftware.wrapper.WrapperManager")));
                }
                else {
                    class$ = (class$org$tanukisoftware$wrapper$WrapperManager = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
                }
                final Class clazz = class$org$tanukisoftware$wrapper$WrapperManager;
                synchronized (class$) {
                    WrapperManager.m_commRunnerStarted = true;
                    WrapperManager.class.notifyAll();
                }
            }
            verifyWrapperVersion();
            if (WrapperManager.m_libraryLoaded) {
                verifyNativeLibraryVersion();
            }
            if (!WrapperManager.m_wrapperVersionOk || !WrapperManager.m_libraryVersionOk || !WrapperManager.m_libraryLoaded) {
                stop(1);
            }
            if (WrapperManager.m_backendSocket != null || WrapperManager.m_backendConnected) {
                handleBackend();
            }
            else {
                try {
                    Thread.sleep(100L);
                }
                catch (final InterruptedException ex) {}
            }
        }
        catch (final ThreadDeath td) {
            WrapperManager.m_outError.println(getRes().getString("Server daemon killed"));
        }
        catch (final Throwable t) {
            if (!isShuttingDown()) {
                WrapperManager.m_outError.println(getRes().getString("Server daemon died!"));
                t.printStackTrace(WrapperManager.m_outError);
            }
            else if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Server daemon died!"));
                t.printStackTrace(WrapperManager.m_outDebug);
            }
        }
        finally {
            if (WrapperManager.m_debug) {
                WrapperManager.m_outDebug.println(getRes().getString("Returned from backend handler."));
            }
            closeBackend();
            if (!isShuttingDown()) {
                if (WrapperManager.m_detachStarted && WrapperManager.m_started) {
                    WrapperManager.m_outInfo.println(getRes().getString("The backend was closed as expected."));
                    if (isNativeLibraryOk()) {
                        nativeRedirectPipes();
                    }
                    else {
                        WrapperManager.m_outError.println(getRes().getString("Failed to redirect stdout and stderr before the Wrapper exits.\nOutput from the JVM may block.\nPlease make sure the native library has been properly initialized."));
                    }
                }
                else {
                    WrapperManager.m_outError.println(getRes().getString("The backend was closed unexpectedly.  Restart to resync with the Wrapper."));
                    restart();
                }
            }
        }
        Class class$2;
        Class class$org$tanukisoftware$wrapper$WrapperManager2;
        if (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager == null) {
            class$org$tanukisoftware$wrapper$WrapperManager2 = (WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager = (class$2 = class$("org.tanukisoftware.wrapper.WrapperManager")));
        }
        else {
            class$2 = (class$org$tanukisoftware$wrapper$WrapperManager2 = WrapperManager.class$org$tanukisoftware$wrapper$WrapperManager);
        }
        final Class clazz2 = class$org$tanukisoftware$wrapper$WrapperManager2;
        synchronized (class$2) {
            if (!WrapperManager.m_commRunnerStarted) {
                WrapperManager.m_commRunnerStarted = true;
                WrapperManager.class.notifyAll();
            }
        }
        if (WrapperManager.m_debug) {
            WrapperManager.m_outDebug.println(getRes().getString("Server daemon shut down"));
        }
    }
    
    private static boolean checkDeadlocks() throws WrapperLicenseError {
        if (isStandardEdition()) {
            boolean result = false;
            if (isNativeLibraryOk()) {
                result = nativeCheckDeadLocks();
            }
            else {
                if (WrapperManager.m_debug) {
                    WrapperManager.m_outDebug.println(getRes().getString("Deadlock check skipped.  Native call unavailable."));
                }
                result = false;
            }
            return result;
        }
        return false;
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (final ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
    
    static {
        WrapperManager.m_windows = false;
        WrapperManager.m_macosx = false;
        WrapperManager.m_aix = false;
        WrapperManager.m_zos = false;
        WrapperManager.m_securityManagerChecked = false;
        WrapperManager.m_disposed = false;
        WrapperManager.m_starting = false;
        WrapperManager.m_started = false;
        WrapperManager.m_instance = null;
        WrapperManager.m_hook = null;
        WrapperManager.m_hookTriggered = false;
        WrapperManager.m_hookRemoveFailed = false;
        WrapperManager.m_shutdownJVMComplete = false;
        WrapperManager.m_shutdownLockMap = new HashMap();
        WrapperManager.m_shutdownLocks = 0;
        WrapperManager.m_runningExecs = 0;
        WrapperManager.m_backendType = 0;
        WrapperManager.m_backendConnected = false;
        WrapperManager.m_backendOS = null;
        WrapperManager.m_backendIS = null;
        WrapperManager.m_port = 15003;
        WrapperManager.m_wrapperPortAddress = null;
        WrapperManager.m_soTimeout = -1;
        WrapperManager.m_cpuTimeout = 10000L;
        WrapperManager.m_lowLogLevel = 9;
        WrapperManager.m_ignoreSignals = false;
        WrapperManager.m_detachStarted = false;
        WrapperManager.m_commRunnerStarted = false;
        WrapperManager.m_appearHung = false;
        WrapperManager.m_slowSeconds = 0;
        WrapperManager.m_ignoreUserLogoffs = false;
        WrapperManager.m_service = false;
        WrapperManager.m_debug = false;
        WrapperManager.m_logFinalizer = false;
        WrapperManager.m_jvmId = 0;
        WrapperManager.m_stoppingInit = false;
        WrapperManager.m_stopping = false;
        WrapperManager.m_stopped = false;
        WrapperManager.m_pendingStopMessage = null;
        WrapperManager.m_libraryOK = false;
        WrapperManager.m_libraryLoaded = false;
        WrapperManager.m_libraryVersionOk = false;
        WrapperManager.m_wrapperVersionOk = false;
        WrapperManager.m_commandBuffer = new byte[512];
        WrapperManager.m_logFile = null;
        WrapperManager.m_wrapperEventListenerMaskList = new ArrayList();
        WrapperManager.m_wrapperEventListenerMasks = null;
        WrapperManager.m_produceCoreEvents = false;
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                privilegedClassInit();
                return null;
            }
        });
        WrapperManager.PROPERTY_SEPARATOR = '\t';
        WrapperManager.m_backendReadBuffer = new byte[256];
    }
    
    private static class ShutdownLock
    {
        private final Thread m_thread;
        private int m_count;
        
        private ShutdownLock(final Thread thread) {
            this.m_thread = thread;
        }
    }
    
    private static class WrapperEventListenerMask
    {
        private WrapperEventListener m_listener;
        private long m_mask;
    }
    
    private static class WrapperTickEventImpl extends WrapperTickEvent
    {
        private int m_ticks;
        private int m_tickOffset;
        
        public int getTicks() {
            return this.m_ticks;
        }
        
        public int getTickOffset() {
            return this.m_tickOffset;
        }
    }
    
    private static class WrapperInputStream extends InputStream
    {
        public int read() throws IOException {
            WrapperManager.m_outInfo.println(WrapperManager.getRes().getString("WARNING - System.in has been disabled by the wrapper.disable_console_input property.  Calls will block indefinitely."));
            while (true) {
                synchronized (this) {
                    try {
                        this.wait();
                    }
                    catch (final InterruptedException ex) {}
                }
            }
        }
    }
}
