package com.sun.jmx.remote.internal;

import java.rmi.UnmarshalException;
import java.io.NotSerializableException;
import javax.management.Notification;
import javax.management.remote.TargetedNotification;
import javax.management.MBeanServerNotification;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.ArrayList;
import javax.security.auth.Subject;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ListenerNotFoundException;
import javax.management.InstanceNotFoundException;
import java.io.IOException;
import javax.management.remote.NotificationResult;
import java.security.AccessController;
import com.sun.jmx.remote.util.EnvHelp;
import java.util.HashMap;
import com.sun.jmx.remote.util.ClassLogger;
import java.util.Map;
import java.util.concurrent.Executor;
import java.security.AccessControlContext;

public abstract class ClientNotifForwarder
{
    private final AccessControlContext acc;
    private static int threadId;
    private final ClassLoader defaultClassLoader;
    private final Executor executor;
    private final Map<Integer, ClientListenerInfo> infoList;
    private long clientSequenceNumber;
    private final int maxNotifications;
    private final long timeout;
    private Integer mbeanRemovedNotifID;
    private Thread currentFetchThread;
    private static final int STARTING = 0;
    private static final int STARTED = 1;
    private static final int STOPPING = 2;
    private static final int STOPPED = 3;
    private static final int TERMINATED = 4;
    private int state;
    private boolean beingReconnected;
    private static final ClassLogger logger;
    
    public ClientNotifForwarder(final Map map) {
        this(null, map);
    }
    
    public ClientNotifForwarder(final ClassLoader defaultClassLoader, final Map<String, ?> map) {
        this.infoList = new HashMap<Integer, ClientListenerInfo>();
        this.clientSequenceNumber = -1L;
        this.mbeanRemovedNotifID = null;
        this.state = 3;
        this.beingReconnected = false;
        this.maxNotifications = EnvHelp.getMaxFetchNotifNumber(map);
        this.timeout = EnvHelp.getFetchTimeout(map);
        Executor executor = (Executor)map.get("jmx.remote.x.fetch.notifications.executor");
        if (executor == null) {
            executor = new LinearExecutor();
        }
        else if (ClientNotifForwarder.logger.traceOn()) {
            ClientNotifForwarder.logger.trace("ClientNotifForwarder", "executor is " + executor);
        }
        this.defaultClassLoader = defaultClassLoader;
        this.executor = executor;
        this.acc = AccessController.getContext();
    }
    
    protected abstract NotificationResult fetchNotifs(final long p0, final int p1, final long p2) throws IOException, ClassNotFoundException;
    
    protected abstract Integer addListenerForMBeanRemovedNotif() throws IOException, InstanceNotFoundException;
    
    protected abstract void removeListenerForMBeanRemovedNotif(final Integer p0) throws IOException, InstanceNotFoundException, ListenerNotFoundException;
    
    protected abstract void lostNotifs(final String p0, final long p1);
    
    public synchronized void addNotificationListener(final Integer n, final ObjectName objectName, final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o, final Subject subject) throws IOException, InstanceNotFoundException {
        if (ClientNotifForwarder.logger.traceOn()) {
            ClientNotifForwarder.logger.trace("addNotificationListener", "Add the listener " + notificationListener + " at " + objectName);
        }
        this.infoList.put(n, new ClientListenerInfo(n, objectName, notificationListener, notificationFilter, o, subject));
        this.init(false);
    }
    
    public synchronized Integer[] removeNotificationListener(final ObjectName objectName, final NotificationListener notificationListener) throws ListenerNotFoundException, IOException {
        this.beforeRemove();
        if (ClientNotifForwarder.logger.traceOn()) {
            ClientNotifForwarder.logger.trace("removeNotificationListener", "Remove the listener " + notificationListener + " from " + objectName);
        }
        final ArrayList list = new ArrayList();
        final ArrayList list2 = new ArrayList((Collection<? extends E>)this.infoList.values());
        for (int i = list2.size() - 1; i >= 0; --i) {
            final ClientListenerInfo clientListenerInfo = (ClientListenerInfo)list2.get(i);
            if (clientListenerInfo.sameAs(objectName, notificationListener)) {
                list.add(clientListenerInfo.getListenerID());
                this.infoList.remove(clientListenerInfo.getListenerID());
            }
        }
        if (list.isEmpty()) {
            throw new ListenerNotFoundException("Listener not found");
        }
        return (Integer[])list.toArray(new Integer[0]);
    }
    
    public synchronized Integer removeNotificationListener(final ObjectName objectName, final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws ListenerNotFoundException, IOException {
        if (ClientNotifForwarder.logger.traceOn()) {
            ClientNotifForwarder.logger.trace("removeNotificationListener", "Remove the listener " + notificationListener + " from " + objectName);
        }
        this.beforeRemove();
        Object listenerID = null;
        final ArrayList list = new ArrayList((Collection<? extends E>)this.infoList.values());
        for (int i = list.size() - 1; i >= 0; --i) {
            final ClientListenerInfo clientListenerInfo = (ClientListenerInfo)list.get(i);
            if (clientListenerInfo.sameAs(objectName, notificationListener, notificationFilter, o)) {
                listenerID = clientListenerInfo.getListenerID();
                this.infoList.remove(listenerID);
                break;
            }
        }
        if (listenerID == null) {
            throw new ListenerNotFoundException("Listener not found");
        }
        return (Integer)listenerID;
    }
    
    public synchronized Integer[] removeNotificationListener(final ObjectName objectName) {
        if (ClientNotifForwarder.logger.traceOn()) {
            ClientNotifForwarder.logger.trace("removeNotificationListener", "Remove all listeners registered at " + objectName);
        }
        final ArrayList list = new ArrayList();
        final ArrayList list2 = new ArrayList((Collection<? extends E>)this.infoList.values());
        for (int i = list2.size() - 1; i >= 0; --i) {
            final ClientListenerInfo clientListenerInfo = (ClientListenerInfo)list2.get(i);
            if (clientListenerInfo.sameAs(objectName)) {
                list.add(clientListenerInfo.getListenerID());
                this.infoList.remove(clientListenerInfo.getListenerID());
            }
        }
        return (Integer[])list.toArray(new Integer[0]);
    }
    
    public synchronized ClientListenerInfo[] preReconnection() throws IOException {
        if (this.state == 4 || this.beingReconnected) {
            throw new IOException("Illegal state.");
        }
        final ClientListenerInfo[] array = this.infoList.values().toArray(new ClientListenerInfo[0]);
        this.beingReconnected = true;
        this.infoList.clear();
        return array;
    }
    
    public synchronized void postReconnection(final ClientListenerInfo[] array) throws IOException {
        if (this.state == 4) {
            return;
        }
        while (this.state == 2) {
            try {
                this.wait();
                continue;
            }
            catch (final InterruptedException ex) {
                final IOException ex2 = new IOException(ex.toString());
                EnvHelp.initCause(ex2, ex);
                throw ex2;
            }
            break;
        }
        final boolean traceOn = ClientNotifForwarder.logger.traceOn();
        for (int length = array.length, i = 0; i < length; ++i) {
            if (traceOn) {
                ClientNotifForwarder.logger.trace("addNotificationListeners", "Add a listener at " + array[i].getListenerID());
            }
            this.infoList.put(array[i].getListenerID(), array[i]);
        }
        this.beingReconnected = false;
        this.notifyAll();
        if (this.currentFetchThread != Thread.currentThread() && this.state != 0) {
            if (this.state != 1) {
                while (this.state == 2) {
                    try {
                        this.wait();
                        continue;
                    }
                    catch (final InterruptedException ex3) {
                        final IOException ex4 = new IOException(ex3.toString());
                        EnvHelp.initCause(ex4, ex3);
                        throw ex4;
                    }
                    break;
                }
                if (array.length > 0) {
                    this.init(true);
                    return;
                }
                if (this.infoList.size() > 0) {
                    this.init(false);
                }
                return;
            }
        }
        try {
            this.mbeanRemovedNotifID = this.addListenerForMBeanRemovedNotif();
        }
        catch (final Exception ex5) {
            if (ClientNotifForwarder.logger.traceOn()) {
                ClientNotifForwarder.logger.trace("init", "Failed to register a listener to the mbean server: the client will not do clean when an MBean is unregistered", ex5);
            }
        }
    }
    
    public synchronized void terminate() {
        if (this.state == 4) {
            return;
        }
        if (ClientNotifForwarder.logger.traceOn()) {
            ClientNotifForwarder.logger.trace("terminate", "Terminating...");
        }
        if (this.state == 1) {
            this.infoList.clear();
        }
        this.setState(4);
    }
    
    private synchronized void setState(final int state) {
        if (this.state == 4) {
            return;
        }
        this.state = state;
        this.notifyAll();
    }
    
    private synchronized void init(final boolean b) throws IOException {
        switch (this.state) {
            case 1: {
                return;
            }
            case 0: {
                return;
            }
            case 4: {
                throw new IOException("The ClientNotifForwarder has been terminated.");
            }
            case 2: {
                if (this.beingReconnected) {
                    return;
                }
                while (this.state == 2) {
                    try {
                        this.wait();
                        continue;
                    }
                    catch (final InterruptedException ex) {
                        final IOException ex2 = new IOException(ex.toString());
                        EnvHelp.initCause(ex2, ex);
                        throw ex2;
                    }
                    break;
                }
                this.init(b);
                return;
            }
            case 3: {
                if (this.beingReconnected) {
                    return;
                }
                if (ClientNotifForwarder.logger.traceOn()) {
                    ClientNotifForwarder.logger.trace("init", "Initializing...");
                }
                if (!b) {
                    try {
                        final NotificationResult fetchNotifs = this.fetchNotifs(-1L, 0, 0L);
                        if (this.state != 3) {
                            return;
                        }
                        this.clientSequenceNumber = fetchNotifs.getNextSequenceNumber();
                    }
                    catch (final ClassNotFoundException ex3) {
                        ClientNotifForwarder.logger.warning("init", "Impossible exception: " + ex3);
                        ClientNotifForwarder.logger.debug("init", ex3);
                    }
                }
                try {
                    this.mbeanRemovedNotifID = this.addListenerForMBeanRemovedNotif();
                }
                catch (final Exception ex4) {
                    if (ClientNotifForwarder.logger.traceOn()) {
                        ClientNotifForwarder.logger.trace("init", "Failed to register a listener to the mbean server: the client will not do clean when an MBean is unregistered", ex4);
                    }
                }
                this.setState(0);
                this.executor.execute(new NotifFetcher());
                return;
            }
            default: {
                throw new IOException("Unknown state.");
            }
        }
    }
    
    private synchronized void beforeRemove() throws IOException {
        while (this.beingReconnected) {
            if (this.state == 4) {
                throw new IOException("Terminated.");
            }
            try {
                this.wait();
                continue;
            }
            catch (final InterruptedException ex) {
                final IOException ex2 = new IOException(ex.toString());
                EnvHelp.initCause(ex2, ex);
                throw ex2;
            }
            break;
        }
        if (this.state == 4) {
            throw new IOException("Terminated.");
        }
    }
    
    static {
        logger = new ClassLogger("javax.management.remote.misc", "ClientNotifForwarder");
    }
    
    private static class LinearExecutor implements Executor
    {
        private Runnable command;
        private Thread thread;
        
        @Override
        public synchronized void execute(final Runnable command) {
            if (this.command != null) {
                throw new IllegalArgumentException("More than one command");
            }
            this.command = command;
            if (this.thread == null) {
                (this.thread = new Thread() {
                    @Override
                    public void run() {
                        while (true) {
                            final Runnable access$000;
                            synchronized (LinearExecutor.this) {
                                if (LinearExecutor.this.command == null) {
                                    LinearExecutor.this.thread = null;
                                    return;
                                }
                                access$000 = LinearExecutor.this.command;
                                LinearExecutor.this.command = null;
                            }
                            access$000.run();
                        }
                    }
                }).setDaemon(true);
                this.thread.setName("ClientNotifForwarder-" + ++ClientNotifForwarder.threadId);
                this.thread.start();
            }
        }
    }
    
    private class NotifFetcher implements Runnable
    {
        private volatile boolean alreadyLogged;
        
        private NotifFetcher() {
            this.alreadyLogged = false;
        }
        
        private void logOnce(final String s, final SecurityException ex) {
            if (this.alreadyLogged) {
                return;
            }
            ClientNotifForwarder.logger.config("setContextClassLoader", s);
            if (ex != null) {
                ClientNotifForwarder.logger.fine("setContextClassLoader", ex);
            }
            this.alreadyLogged = true;
        }
        
        private final ClassLoader setContextClassLoader(final ClassLoader classLoader) {
            final AccessControlContext access$500 = ClientNotifForwarder.this.acc;
            if (access$500 == null) {
                this.logOnce("AccessControlContext must not be null.", null);
                throw new SecurityException("AccessControlContext must not be null");
            }
            return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                    try {
                        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                        if (classLoader == contextClassLoader) {
                            return contextClassLoader;
                        }
                        Thread.currentThread().setContextClassLoader(classLoader);
                        return contextClassLoader;
                    }
                    catch (final SecurityException ex) {
                        NotifFetcher.this.logOnce("Permission to set ContextClassLoader missing. Notifications will not be dispatched. Please check your Java policy configuration: " + ex, ex);
                        throw ex;
                    }
                }
            }, access$500);
        }
        
        @Override
        public void run() {
            ClassLoader setContextClassLoader;
            if (ClientNotifForwarder.this.defaultClassLoader != null) {
                setContextClassLoader = this.setContextClassLoader(ClientNotifForwarder.this.defaultClassLoader);
            }
            else {
                setContextClassLoader = null;
            }
            try {
                this.doRun();
            }
            finally {
                if (ClientNotifForwarder.this.defaultClassLoader != null) {
                    this.setContextClassLoader(setContextClassLoader);
                }
            }
        }
        
        private void doRun() {
            synchronized (ClientNotifForwarder.this) {
                ClientNotifForwarder.this.currentFetchThread = Thread.currentThread();
                if (ClientNotifForwarder.this.state == 0) {
                    ClientNotifForwarder.this.setState(1);
                }
            }
            NotificationResult fetchNotifs = null;
            if (!this.shouldStop() && (fetchNotifs = this.fetchNotifs()) != null) {
                final TargetedNotification[] targetedNotifications = fetchNotifs.getTargetedNotifications();
                final int length = targetedNotifications.length;
                long n = 0L;
                final HashMap hashMap;
                final Integer access$1200;
                synchronized (ClientNotifForwarder.this) {
                    if (ClientNotifForwarder.this.clientSequenceNumber >= 0L) {
                        n = fetchNotifs.getEarliestSequenceNumber() - ClientNotifForwarder.this.clientSequenceNumber;
                    }
                    ClientNotifForwarder.this.clientSequenceNumber = fetchNotifs.getNextSequenceNumber();
                    hashMap = new HashMap();
                    for (final TargetedNotification targetedNotification : targetedNotifications) {
                        final Integer listenerID = targetedNotification.getListenerID();
                        if (!listenerID.equals(ClientNotifForwarder.this.mbeanRemovedNotifID)) {
                            final ClientListenerInfo clientListenerInfo = ClientNotifForwarder.this.infoList.get(listenerID);
                            if (clientListenerInfo != null) {
                                hashMap.put(listenerID, clientListenerInfo);
                            }
                        }
                        else {
                            final Notification notification = targetedNotification.getNotification();
                            if (notification instanceof MBeanServerNotification && notification.getType().equals("JMX.mbean.unregistered")) {
                                ClientNotifForwarder.this.removeNotificationListener(((MBeanServerNotification)notification).getMBeanName());
                            }
                        }
                    }
                    access$1200 = ClientNotifForwarder.this.mbeanRemovedNotifID;
                }
                if (n > 0L) {
                    final String string = "May have lost up to " + n + " notification" + ((n == 1L) ? "" : "s");
                    ClientNotifForwarder.this.lostNotifs(string, n);
                    ClientNotifForwarder.logger.trace("NotifFetcher.run", string);
                }
                for (int j = 0; j < length; ++j) {
                    this.dispatchNotification(targetedNotifications[j], access$1200, hashMap);
                }
            }
            synchronized (ClientNotifForwarder.this) {
                ClientNotifForwarder.this.currentFetchThread = null;
            }
            if (fetchNotifs == null && ClientNotifForwarder.logger.traceOn()) {
                ClientNotifForwarder.logger.trace("NotifFetcher-run", "Recieved null object as notifs, stops fetching because the notification server is terminated.");
            }
            if (fetchNotifs == null || this.shouldStop()) {
                ClientNotifForwarder.this.setState(3);
                try {
                    ClientNotifForwarder.this.removeListenerForMBeanRemovedNotif(ClientNotifForwarder.this.mbeanRemovedNotifID);
                }
                catch (final Exception ex) {
                    if (ClientNotifForwarder.logger.traceOn()) {
                        ClientNotifForwarder.logger.trace("NotifFetcher-run", "removeListenerForMBeanRemovedNotif", ex);
                    }
                }
            }
            else {
                ClientNotifForwarder.this.executor.execute(this);
            }
        }
        
        void dispatchNotification(final TargetedNotification targetedNotification, final Integer n, final Map<Integer, ClientListenerInfo> map) {
            final Notification notification = targetedNotification.getNotification();
            final Integer listenerID = targetedNotification.getListenerID();
            if (listenerID.equals(n)) {
                return;
            }
            final ClientListenerInfo clientListenerInfo = map.get(listenerID);
            if (clientListenerInfo == null) {
                ClientNotifForwarder.logger.trace("NotifFetcher.dispatch", "Listener ID not in map");
                return;
            }
            final NotificationListener listener = clientListenerInfo.getListener();
            final Object handback = clientListenerInfo.getHandback();
            try {
                listener.handleNotification(notification, handback);
            }
            catch (final RuntimeException ex) {
                ClientNotifForwarder.logger.trace("NotifFetcher-run", "Failed to forward a notification to a listener", ex);
            }
        }
        
        private NotificationResult fetchNotifs() {
            try {
                final NotificationResult fetchNotifs = ClientNotifForwarder.this.fetchNotifs(ClientNotifForwarder.this.clientSequenceNumber, ClientNotifForwarder.this.maxNotifications, ClientNotifForwarder.this.timeout);
                if (ClientNotifForwarder.logger.traceOn()) {
                    ClientNotifForwarder.logger.trace("NotifFetcher-run", "Got notifications from the server: " + fetchNotifs);
                }
                return fetchNotifs;
            }
            catch (final ClassNotFoundException | NotSerializableException | UnmarshalException ex) {
                ClientNotifForwarder.logger.trace("NotifFetcher.fetchNotifs", (Throwable)ex);
                return this.fetchOneNotif();
            }
            catch (final IOException ex2) {
                if (!this.shouldStop()) {
                    ClientNotifForwarder.logger.error("NotifFetcher-run", "Failed to fetch notification, stopping thread. Error is: " + ex2, ex2);
                    ClientNotifForwarder.logger.debug("NotifFetcher-run", ex2);
                }
                return null;
            }
        }
        
        private NotificationResult fetchOneNotif() {
            final ClientNotifForwarder this$0 = ClientNotifForwarder.this;
            long n = ClientNotifForwarder.this.clientSequenceNumber;
            int n2 = 0;
            NotificationResult fetchNotifs = null;
            long earliestSequenceNumber = -1L;
            while (fetchNotifs == null && !this.shouldStop()) {
                NotificationResult fetchNotifs2;
                try {
                    fetchNotifs2 = this$0.fetchNotifs(n, 0, 0L);
                }
                catch (final ClassNotFoundException ex) {
                    ClientNotifForwarder.logger.warning("NotifFetcher.fetchOneNotif", "Impossible exception: " + ex);
                    ClientNotifForwarder.logger.debug("NotifFetcher.fetchOneNotif", ex);
                    return null;
                }
                catch (final IOException ex2) {
                    if (!this.shouldStop()) {
                        ClientNotifForwarder.logger.trace("NotifFetcher.fetchOneNotif", ex2);
                    }
                    return null;
                }
                if (this.shouldStop() || fetchNotifs2 == null) {
                    return null;
                }
                n = fetchNotifs2.getNextSequenceNumber();
                if (earliestSequenceNumber < 0L) {
                    earliestSequenceNumber = fetchNotifs2.getEarliestSequenceNumber();
                }
                try {
                    fetchNotifs = this$0.fetchNotifs(n, 1, 0L);
                }
                catch (final ClassNotFoundException | NotSerializableException | UnmarshalException ex3) {
                    ClientNotifForwarder.logger.warning("NotifFetcher.fetchOneNotif", "Failed to deserialize a notification: " + ((Throwable)ex3).toString());
                    if (ClientNotifForwarder.logger.traceOn()) {
                        ClientNotifForwarder.logger.trace("NotifFetcher.fetchOneNotif", "Failed to deserialize a notification.", (Throwable)ex3);
                    }
                    ++n2;
                    ++n;
                }
                catch (final Exception ex4) {
                    if (!this.shouldStop()) {
                        ClientNotifForwarder.logger.trace("NotifFetcher.fetchOneNotif", ex4);
                    }
                    return null;
                }
            }
            if (n2 > 0) {
                ClientNotifForwarder.this.lostNotifs("Dropped " + n2 + " notification" + ((n2 == 1) ? "" : "s") + " because classes were missing locally or incompatible", n2);
                if (fetchNotifs != null) {
                    fetchNotifs = new NotificationResult(earliestSequenceNumber, fetchNotifs.getNextSequenceNumber(), fetchNotifs.getTargetedNotifications());
                }
            }
            return fetchNotifs;
        }
        
        private boolean shouldStop() {
            synchronized (ClientNotifForwarder.this) {
                if (ClientNotifForwarder.this.state != 1) {
                    return true;
                }
                if (ClientNotifForwarder.this.infoList.size() == 0) {
                    ClientNotifForwarder.this.setState(2);
                    return true;
                }
                return false;
            }
        }
    }
}
