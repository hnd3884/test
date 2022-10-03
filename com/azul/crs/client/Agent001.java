package com.azul.crs.client;

import java.io.IOException;
import com.azul.crs.client.util.DnsDetect;
import com.azul.crs.shared.models.Payload;
import java.lang.management.ManagementFactory;
import sun.launcher.LauncherHelper;
import java.util.List;
import java.util.Collection;
import com.azul.crs.shared.models.VMEvent;
import java.util.ArrayList;
import java.util.Map;
import com.azul.crs.shared.Utils;
import com.azul.crs.util.logging.Logger;
import com.azul.crs.client.service.FirstCallMonitor;
import com.azul.crs.client.service.ClassLoadMonitor;
import com.azul.crs.client.service.JFRMonitor;
import com.azul.crs.client.service.GCLogMonitor;
import com.azul.crs.client.service.HeartbeatService;

class Agent001
{
    private static final int CRS_DISABLED = -1;
    private static final int CRS_NOT_YET_DECIDED = 0;
    private static final int CRS_LAUNCHER_DETECTED = 1;
    private static final int CRS_ENABLED_FORCED = 2;
    private static final int CRS_ENABLED_MAIN_KNOWN = 3;
    private static HeartbeatService heartbeatService;
    private static GCLogMonitor gclogMonitor;
    private static JFRMonitor jfrMonitor;
    private static ClassLoadMonitor classLoadMonitor;
    private static FirstCallMonitor firstCallMonitor;
    private static Client client;
    private static volatile int useCRS;
    private static Thread startThread;
    private static volatile boolean launcherDetected;
    private static final int FLUSH_THREAD_DEFAULT_PERIOD_MS = 1000;
    private static final int FLUSH_THREAD_FORCE_DEFAULT_PERIOD_MS = 1800000;
    private static final long DEFAULT_SHUTDOWN_DELAY = 120000L;
    private static final Object flushThreadLock;
    private static boolean flushThreadStop;
    private static volatile Thread flushThread;
    private static int forceFlushTimeout;
    private static long delayShutdown;
    private static final int DRAIN_NATIVE_QUEUE_AND_STOP = -101;
    private static final int DRAIN_NATIVE_QUEUE = -100;
    private static final int EVENT_USE_CRS = -99;
    private static final int EVENT_TO_JAVA_CALL = -98;
    private static final int EVENT_CLASS_LOAD = 0;
    private static final int EVENT_FIRST_CALL = 1;
    private static final Logger logger;
    private static boolean connectionEstablished;
    
    private static void addShutdownHook(final long startTime) {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                final long shutdownDeadline = Utils.nextTimeCount(Agent001.delayShutdown);
                Client.setVMShutdownInitiated(shutdownDeadline);
                final long shutdownStartTime = Utils.currentTimeCount();
                try {
                    if (Agent001.delayShutdown > 0L) {
                        Agent001.logger.trace("checking if startup is complete and waiting for it to finish (%d ms)", Agent001.delayShutdown);
                        Agent001.startThread.join();
                        if (Agent001.useCRS == -1) {
                            return;
                        }
                        Agent001.logger.debug("drain native queue", new Object[0]);
                        Agent001.flushThreadStop = true;
                        synchronized (Agent001.flushThreadLock) {
                            Agent001.flushThreadLock.notifyAll();
                        }
                        try {
                            Agent001.flushThread.join();
                        }
                        catch (final InterruptedException ex) {}
                        Agent001.heartbeatService.stop(shutdownDeadline);
                        Agent001.jfrMonitor.stop(shutdownDeadline);
                        Agent001.gclogMonitor.stop(shutdownDeadline);
                        Agent001.classLoadMonitor.stop(shutdownDeadline);
                        Agent001.firstCallMonitor.stop(shutdownDeadline);
                        final Map perfMonData = PerformanceMetrics.logPreShutdown(Utils.elapsedTimeMillis(shutdownStartTime));
                        postVMShutdown(Agent001.client, perfMonData);
                        Agent001.client.shutdown(shutdownDeadline);
                    }
                    if (Agent001.client != null) {
                        PerformanceMetrics.logShutdown(Utils.elapsedTimeMillis(shutdownStartTime));
                        PerformanceMetrics.report();
                        Agent001.logger.info("Agent terminated: vmId=%s, runningTime=%d", Agent001.client.getVmId(), Utils.elapsedTimeMillis(startTime));
                    }
                    else {
                        Agent001.logger.info("Agent shut down during startup. Data is discarded", new Object[0]);
                    }
                }
                catch (final InterruptedException e) {
                    Agent001.logger.error("Agent failed to process shutdown during startup. Data is discarded", new Object[0]);
                }
            }
        }));
    }
    
    private static void postVMStart(final long startTime, final String mainMethod) throws Exception {
        final Map<String, Object> inventory = new Inventory().populate().mainMethod(mainMethod).toMap();
        Agent001.logger.trace("Post VM start to CRS service", new Object[0]);
        Agent001.client.postVMStart(inventory, startTime);
    }
    
    private static void sendMainMethodName(final String mainMethod) {
        final Map<String, Object> inventory = new Inventory().mainMethod(mainMethod).toMap();
        Agent001.client.patchInventory(inventory);
    }
    
    private static void sendNetworkInformation() {
        final Map<String, Object> inventory = new Inventory().networkInformation().toMap();
        Agent001.client.patchInventory(inventory);
    }
    
    private static void postVMShutdown(final Client client, final Map perfMonData) {
        Agent001.logger.trace("Post VM shutdown to CRS service", new Object[0]);
        final List<VMEvent> trailingEvents = new ArrayList<VMEvent>();
        trailingEvents.add(new VMEvent<Map>().eventType(VMEvent.Type.VM_PERFORMANCE_METRICS).randomEventId().eventTime(System.currentTimeMillis()).eventPayload(perfMonData));
        client.postVMShutdown(trailingEvents);
    }
    
    public static void startAgent(final String args) {
        Options.read(args);
        PerformanceMetrics.init();
        if ("force".equals(Options.useCRS.get()) || !Options.useCRS.isSet()) {
            stateEvent(2, null);
        }
        if (Options.forceSyncTimeout.isSet()) {
            Agent001.forceFlushTimeout = Options.forceSyncTimeout.getInt() * 1000;
        }
        if (Options.delayShutdown.isSet()) {
            Agent001.delayShutdown = Options.delayShutdown.getLong();
        }
        if (Options.noDelayShutdown.isSet()) {
            Agent001.delayShutdown = 0L;
        }
        System.setProperty("com.azul.crs.instance.options.delayShutdown", Long.toString(Agent001.delayShutdown));
    }
    
    private static synchronized void stateEvent(final int state, final String mainMethod) {
        switch (state) {
            case 2: {
                assert Agent001.useCRS != -1;
                if (Agent001.useCRS == 0 || Agent001.useCRS == 1) {
                    Agent001.useCRS = 2;
                    activateAgent("");
                    break;
                }
                break;
            }
            case 1: {
                if (Agent001.useCRS == 0) {
                    Agent001.useCRS = 1;
                }
                final Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Class<?> applicationClass;
                        do {
                            applicationClass = LauncherHelper.getApplicationClass();
                            try {
                                Thread.sleep(10L);
                            }
                            catch (final InterruptedException e) {
                                return;
                            }
                        } while (applicationClass == null);
                        stateEvent(3, applicationClass.getName().replace('.', '/') + ".main");
                    }
                });
                t.setDaemon(true);
                t.start();
                break;
            }
            case 3: {
                assert mainMethod != null;
                if (Agent001.useCRS == 0 || Agent001.useCRS == 1) {
                    if (mainMethod.startsWith("com/sun/tools")) {
                        stateEvent(-1, null);
                        break;
                    }
                    Agent001.useCRS = 3;
                    activateAgent(mainMethod);
                    break;
                }
                else {
                    if (Agent001.useCRS != 2) {
                        break;
                    }
                    assert Agent001.startThread != null;
                    Agent001.useCRS = 3;
                    final Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Agent001.startThread.join();
                            }
                            catch (final InterruptedException e) {
                                return;
                            }
                            if (Agent001.useCRS != -1) {
                                sendMainMethodName(mainMethod);
                            }
                        }
                    });
                    t.setDaemon(false);
                    t.start();
                    break;
                }
                break;
            }
            case -1: {
                Agent001.useCRS = -1;
                setNativeEventFilter(-99, false);
                break;
            }
        }
    }
    
    private static void activateAgent(final String mainMethod) {
        final long startTime = System.currentTimeMillis();
        final long startTimeStamp = Utils.currentTimeCount();
        if (Agent001.logger.isEnabled(Logger.Level.DEBUG)) {
            Agent001.logger.debug("CRS agent started. VM uptime %dms", ManagementFactory.getRuntimeMXBean().getUptime());
        }
        (Agent001.startThread = new Thread(new Runnable() {
            @Override
            public void run() {
                addShutdownHook(startTimeStamp);
                try {
                    Agent001.client = new Client(getClientProps(), new Client.ClientListener() {
                        @Override
                        public void authenticated() {
                            if (Agent001.client.getVmId() != null) {
                                Agent001.logger.info("Agent authenticated: vmId=%s", Agent001.client.getVmId());
                                if (Agent001.logger.isEnabled(Logger.Level.DEBUG)) {
                                    Agent001.logger.debug(" VM uptime %dms", ManagementFactory.getRuntimeMXBean().getUptime());
                                }
                                if (!Agent001.connectionEstablished) {
                                    Agent001.client.connectionEstablished();
                                }
                                Agent001.connectionEstablished = true;
                            }
                            else {
                                disableCRS("Backend malfunction, invalid vmId received", null);
                            }
                        }
                        
                        @Override
                        public void syncFailed(final Result<Payload> reason) {
                            Agent001.logger.error("Data synchronization to the CRS cloud has failed: %s", reason.toString());
                        }
                    });
                    Agent001.jfrMonitor = JFRMonitor.getInstance(Agent001.client, Options.lifetimejfr.get());
                    Agent001.jfrMonitor.start();
                    postVMStart(startTime, mainMethod);
                    Agent001.heartbeatService = HeartbeatService.getInstance(Agent001.client);
                    Agent001.gclogMonitor = GCLogMonitor.getInstance(Agent001.client, startTime);
                    Agent001.classLoadMonitor = ClassLoadMonitor.getInstance(Agent001.client);
                    Agent001.firstCallMonitor = FirstCallMonitor.getInstance(Agent001.client);
                    Agent001.client.startup();
                    Agent001.heartbeatService.start();
                    Agent001.gclogMonitor.start();
                    Agent001.classLoadMonitor.start();
                    Agent001.firstCallMonitor.start();
                    Agent001.flushThread = new Thread(Agent001::flushThreadMain);
                    Agent001.flushThread.setDaemon(true);
                    Agent001.flushThread.setName("CRSEventFlush");
                    Agent001.flushThread.start();
                    sendNetworkInformation();
                }
                catch (final Exception e) {
                    disableCRS("CRS failed to start: %s", e);
                }
            }
        })).setDaemon(Agent001.delayShutdown == 0L);
        Agent001.startThread.setName("CRSStartThread");
        Agent001.startThread.start();
    }
    
    private static void disableCRS(final String cause, final Exception ex) {
        if (Agent001.client != null) {
            Agent001.client.cancel();
        }
        Agent001.useCRS = -1;
        Agent001.logger.error(cause, ex);
        if (ex.getCause() != null) {
            Agent001.logger.trace("caused by: %s", ex.getCause());
        }
    }
    
    private static Map<Client.ClientProp, Object> getClientProps() throws CRSException {
        final Map<Client.ClientProp, Object> clientProps = Options.getClientProps();
        final boolean hasEndpointConfig = clientProps.get(Client.ClientProp.API_URL) != null;
        final boolean hasMailboxConfig = clientProps.get(Client.ClientProp.API_MAILBOX) != null;
        Label_0198: {
            if (hasEndpointConfig) {
                if (hasMailboxConfig) {
                    break Label_0198;
                }
            }
            try {
                final DnsDetect detector = new DnsDetect(Options.stackRecordId.get());
                Logger.getLogger(ConnectionManager.class).info("querying DNS record%s", (detector.getRecordNamePostfix().length() > 0) ? (" (postfix " + detector.getRecordNamePostfix() + ")") : "");
                if (!hasEndpointConfig) {
                    clientProps.put(Client.ClientProp.API_URL, "https://" + detector.queryEndpoint());
                }
                if (!hasMailboxConfig) {
                    clientProps.put(Client.ClientProp.API_MAILBOX, detector.queryMailbox());
                }
            }
            catch (final IOException ex) {
                throw new CRSException(-1, "DNS query error and not enough configuration supplied", ex);
            }
        }
        clientProps.put(Client.ClientProp.VM_SHUTDOWN_DELAY, Agent001.delayShutdown);
        return clientProps;
    }
    
    static void flushThreadMain() {
        long previousForceFlushTime = Utils.currentTimeCount();
        while (true) {
            synchronized (Agent001.flushThreadLock) {
                try {
                    Agent001.flushThreadLock.wait(1000L);
                }
                catch (final InterruptedException e) {
                    break;
                }
            }
            if (Agent001.flushThreadStop) {
                break;
            }
            final boolean forceFlush = Utils.elapsedTimeMillis(previousForceFlushTime) >= Agent001.forceFlushTimeout;
            if (forceFlush) {
                previousForceFlushTime = Utils.currentTimeCount();
            }
            setNativeEventFilter(-100, forceFlush);
        }
        setNativeEventFilter(-101, true);
    }
    
    public static void notifyToJavaCall(final String name) {
        if (Agent001.useCRS != 0 && Agent001.useCRS != 2) {
            setNativeEventFilter(-98, false);
            return;
        }
        if (name.startsWith("sun/launcher/LauncherHelper.checkAndLoadMain")) {
            Agent001.launcherDetected = true;
            stateEvent(1, null);
        }
        else if (!Agent001.launcherDetected) {
            if (name.startsWith("java/lang/Thread") || name.startsWith("sun/launcher") || name.startsWith("java/") || name.startsWith("javax/") || name.startsWith("sun/") || name.startsWith("com/sun/") || name.startsWith("com/fasterxml") || name.startsWith("org/jcp") || name.startsWith("com/azul/crs") || name.startsWith("jdk/jfr")) {
                return;
            }
            stateEvent(3, name);
        }
    }
    
    public static void notifyFirstCall(final int classId, final String name) {
        Agent001.firstCallMonitor.notifyMethodFirstCalled(classId, name);
    }
    
    public static void notifyClassLoad(final String className, final byte[] hash, final int classId, final int loaderId, final String source) {
        Agent001.classLoadMonitor.notifyClassLoad(className, hash, classId, loaderId, source);
    }
    
    @Deprecated
    private static native void setNativeEventFilter(final int p0, final boolean p1);
    
    static {
        Agent001.useCRS = 0;
        flushThreadLock = new Object();
        Agent001.forceFlushTimeout = 1800000;
        Agent001.delayShutdown = 120000L;
        logger = Logger.getLogger(Agent001.class);
    }
}
