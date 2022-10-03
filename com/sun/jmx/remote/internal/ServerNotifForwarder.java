package com.sun.jmx.remote.internal;

import javax.management.NotificationBroadcaster;
import java.security.AccessControlContext;
import java.security.Permission;
import javax.management.MBeanPermission;
import javax.management.ObjectInstance;
import javax.security.auth.Subject;
import javax.management.Notification;
import java.util.Iterator;
import java.util.List;
import javax.management.MBeanServerNotification;
import java.util.ArrayList;
import javax.management.MBeanServerDelegate;
import javax.management.remote.TargetedNotification;
import javax.management.remote.NotificationResult;
import javax.management.ListenerNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import javax.management.MalformedObjectNameException;
import java.io.IOException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import javax.management.InstanceNotFoundException;
import java.security.PrivilegedExceptionAction;
import javax.management.NotificationFilter;
import com.sun.jmx.remote.util.EnvHelp;
import java.util.HashMap;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.security.NotificationAccessController;
import java.util.Set;
import javax.management.ObjectName;
import java.util.Map;
import javax.management.MBeanServer;

public class ServerNotifForwarder
{
    private final NotifForwarderBufferFilter bufferFilter;
    private MBeanServer mbeanServer;
    private final String connectionId;
    private final long connectionTimeout;
    private static int listenerCounter;
    private static final int[] listenerCounterLock;
    private NotificationBuffer notifBuffer;
    private final Map<ObjectName, Set<IdAndFilter>> listenerMap;
    private boolean terminated;
    private final int[] terminationLock;
    static final String broadcasterClass;
    private final boolean checkNotificationEmission;
    private final NotificationAccessController notificationAccessController;
    private static final ClassLogger logger;
    
    public ServerNotifForwarder(final MBeanServer mbeanServer, final Map<String, ?> map, final NotificationBuffer notifBuffer, final String connectionId) {
        this.bufferFilter = new NotifForwarderBufferFilter();
        this.listenerMap = new HashMap<ObjectName, Set<IdAndFilter>>();
        this.terminated = false;
        this.terminationLock = new int[0];
        this.mbeanServer = mbeanServer;
        this.notifBuffer = notifBuffer;
        this.connectionId = connectionId;
        this.connectionTimeout = EnvHelp.getServerConnectionTimeout(map);
        this.checkNotificationEmission = EnvHelp.computeBooleanFromString((String)map.get("jmx.remote.x.check.notification.emission"));
        this.notificationAccessController = EnvHelp.getNotificationAccessController(map);
    }
    
    public Integer addNotificationListener(final ObjectName objectName, final NotificationFilter notificationFilter) throws InstanceNotFoundException, IOException {
        if (ServerNotifForwarder.logger.traceOn()) {
            ServerNotifForwarder.logger.trace("addNotificationListener", "Add a listener at " + objectName);
        }
        this.checkState();
        this.checkMBeanPermission(objectName, "addNotificationListener");
        if (this.notificationAccessController != null) {
            this.notificationAccessController.addNotificationListener(this.connectionId, objectName, this.getSubject());
        }
        try {
            if (!AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)new PrivilegedExceptionAction<Boolean>() {
                @Override
                public Boolean run() throws InstanceNotFoundException {
                    return ServerNotifForwarder.this.mbeanServer.isInstanceOf(objectName, ServerNotifForwarder.broadcasterClass);
                }
            })) {
                throw new IllegalArgumentException("The specified MBean [" + objectName + "] is not a NotificationBroadcaster object.");
            }
        }
        catch (final PrivilegedActionException ex) {
            throw (InstanceNotFoundException)extractException(ex);
        }
        final Integer listenerID = this.getListenerID();
        ObjectName instance = objectName;
        Label_0214: {
            if (objectName.getDomain() != null) {
                if (!objectName.getDomain().equals("")) {
                    break Label_0214;
                }
            }
            try {
                instance = ObjectName.getInstance(this.mbeanServer.getDefaultDomain(), objectName.getKeyPropertyList());
            }
            catch (final MalformedObjectNameException ex2) {
                final IOException ex3 = new IOException(ex2.getMessage());
                ex3.initCause(ex2);
                throw ex3;
            }
        }
        synchronized (this.listenerMap) {
            final IdAndFilter idAndFilter = new IdAndFilter(listenerID, notificationFilter);
            Set<IdAndFilter> singleton = this.listenerMap.get(instance);
            if (singleton == null) {
                singleton = Collections.singleton(idAndFilter);
            }
            else {
                if (singleton.size() == 1) {
                    singleton = new HashSet<IdAndFilter>(singleton);
                }
                singleton.add(idAndFilter);
            }
            this.listenerMap.put(instance, singleton);
        }
        return listenerID;
    }
    
    public void removeNotificationListener(final ObjectName objectName, final Integer[] array) throws Exception {
        if (ServerNotifForwarder.logger.traceOn()) {
            ServerNotifForwarder.logger.trace("removeNotificationListener", "Remove some listeners from " + objectName);
        }
        this.checkState();
        this.checkMBeanPermission(objectName, "removeNotificationListener");
        if (this.notificationAccessController != null) {
            this.notificationAccessController.removeNotificationListener(this.connectionId, objectName, this.getSubject());
        }
        Exception ex = null;
        for (int i = 0; i < array.length; ++i) {
            try {
                this.removeNotificationListener(objectName, array[i]);
            }
            catch (final Exception ex2) {
                if (ex != null) {
                    ex = ex2;
                }
            }
        }
        if (ex != null) {
            throw ex;
        }
    }
    
    public void removeNotificationListener(final ObjectName objectName, final Integer n) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
        if (ServerNotifForwarder.logger.traceOn()) {
            ServerNotifForwarder.logger.trace("removeNotificationListener", "Remove the listener " + n + " from " + objectName);
        }
        this.checkState();
        if (objectName != null && !objectName.isPattern() && !this.mbeanServer.isRegistered(objectName)) {
            throw new InstanceNotFoundException("The MBean " + objectName + " is not registered.");
        }
        synchronized (this.listenerMap) {
            final Set set = this.listenerMap.get(objectName);
            final IdAndFilter idAndFilter = new IdAndFilter(n, null);
            if (set == null || !set.contains(idAndFilter)) {
                throw new ListenerNotFoundException("Listener not found");
            }
            if (set.size() == 1) {
                this.listenerMap.remove(objectName);
            }
            else {
                set.remove(idAndFilter);
            }
        }
    }
    
    public NotificationResult fetchNotifs(final long n, final long n2, final int n3) {
        if (ServerNotifForwarder.logger.traceOn()) {
            ServerNotifForwarder.logger.trace("fetchNotifs", "Fetching notifications, the startSequenceNumber is " + n + ", the timeout is " + n2 + ", the maxNotifications is " + n3);
        }
        final long min = Math.min(this.connectionTimeout, n2);
        NotificationResult fetchNotifications;
        try {
            fetchNotifications = this.notifBuffer.fetchNotifications(this.bufferFilter, n, min, n3);
            this.snoopOnUnregister(fetchNotifications);
        }
        catch (final InterruptedException ex) {
            fetchNotifications = new NotificationResult(0L, 0L, new TargetedNotification[0]);
        }
        if (ServerNotifForwarder.logger.traceOn()) {
            ServerNotifForwarder.logger.trace("fetchNotifs", "Forwarding the notifs: " + fetchNotifications);
        }
        return fetchNotifications;
    }
    
    private void snoopOnUnregister(final NotificationResult notificationResult) {
        List list = null;
        synchronized (this.listenerMap) {
            final Set set = this.listenerMap.get(MBeanServerDelegate.DELEGATE_NAME);
            if (set == null || set.isEmpty()) {
                return;
            }
            list = new ArrayList(set);
        }
        for (final TargetedNotification targetedNotification : notificationResult.getTargetedNotifications()) {
            final Integer listenerID = targetedNotification.getListenerID();
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                if (((IdAndFilter)iterator.next()).id == listenerID) {
                    final Notification notification = targetedNotification.getNotification();
                    if (!(notification instanceof MBeanServerNotification) || !notification.getType().equals("JMX.mbean.unregistered")) {
                        continue;
                    }
                    final ObjectName mBeanName = ((MBeanServerNotification)notification).getMBeanName();
                    synchronized (this.listenerMap) {
                        this.listenerMap.remove(mBeanName);
                    }
                }
            }
        }
    }
    
    public void terminate() {
        if (ServerNotifForwarder.logger.traceOn()) {
            ServerNotifForwarder.logger.trace("terminate", "Be called.");
        }
        synchronized (this.terminationLock) {
            if (this.terminated) {
                return;
            }
            this.terminated = true;
            synchronized (this.listenerMap) {
                this.listenerMap.clear();
            }
        }
        if (ServerNotifForwarder.logger.traceOn()) {
            ServerNotifForwarder.logger.trace("terminate", "Terminated.");
        }
    }
    
    private Subject getSubject() {
        return Subject.getSubject(AccessController.getContext());
    }
    
    private void checkState() throws IOException {
        synchronized (this.terminationLock) {
            if (this.terminated) {
                throw new IOException("The connection has been terminated.");
            }
        }
    }
    
    private Integer getListenerID() {
        synchronized (ServerNotifForwarder.listenerCounterLock) {
            return ServerNotifForwarder.listenerCounter++;
        }
    }
    
    public final void checkMBeanPermission(final ObjectName objectName, final String s) throws InstanceNotFoundException, SecurityException {
        checkMBeanPermission(this.mbeanServer, objectName, s);
    }
    
    static void checkMBeanPermission(final MBeanServer mBeanServer, final ObjectName objectName, final String s) throws InstanceNotFoundException, SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            final AccessControlContext context = AccessController.getContext();
            ObjectInstance objectInstance;
            try {
                objectInstance = AccessController.doPrivileged((PrivilegedExceptionAction<ObjectInstance>)new PrivilegedExceptionAction<ObjectInstance>() {
                    @Override
                    public ObjectInstance run() throws InstanceNotFoundException {
                        return mBeanServer.getObjectInstance(objectName);
                    }
                });
            }
            catch (final PrivilegedActionException ex) {
                throw (InstanceNotFoundException)extractException(ex);
            }
            securityManager.checkPermission(new MBeanPermission(objectInstance.getClassName(), null, objectName, s), context);
        }
    }
    
    private boolean allowNotificationEmission(final ObjectName objectName, final TargetedNotification targetedNotification) {
        try {
            if (this.checkNotificationEmission) {
                this.checkMBeanPermission(objectName, "addNotificationListener");
            }
            if (this.notificationAccessController != null) {
                this.notificationAccessController.fetchNotification(this.connectionId, objectName, targetedNotification.getNotification(), this.getSubject());
            }
            return true;
        }
        catch (final SecurityException ex) {
            if (ServerNotifForwarder.logger.debugOn()) {
                ServerNotifForwarder.logger.debug("fetchNotifs", "Notification " + targetedNotification.getNotification() + " not forwarded: the caller didn't have the required access rights");
            }
            return false;
        }
        catch (final Exception ex2) {
            if (ServerNotifForwarder.logger.debugOn()) {
                ServerNotifForwarder.logger.debug("fetchNotifs", "Notification " + targetedNotification.getNotification() + " not forwarded: got an unexpected exception: " + ex2);
            }
            return false;
        }
    }
    
    private static Exception extractException(Exception exception) {
        while (exception instanceof PrivilegedActionException) {
            exception = ((PrivilegedActionException)exception).getException();
        }
        return exception;
    }
    
    static {
        ServerNotifForwarder.listenerCounter = 0;
        listenerCounterLock = new int[0];
        broadcasterClass = NotificationBroadcaster.class.getName();
        logger = new ClassLogger("javax.management.remote.misc", "ServerNotifForwarder");
    }
    
    final class NotifForwarderBufferFilter implements NotificationBufferFilter
    {
        @Override
        public void apply(final List<TargetedNotification> list, final ObjectName objectName, final Notification notification) {
            final IdAndFilter[] array;
            synchronized (ServerNotifForwarder.this.listenerMap) {
                final Set set = ServerNotifForwarder.this.listenerMap.get(objectName);
                if (set == null) {
                    ServerNotifForwarder.logger.debug("bufferFilter", "no listeners for this name");
                    return;
                }
                array = new IdAndFilter[set.size()];
                set.toArray(array);
            }
            for (final IdAndFilter idAndFilter : array) {
                final NotificationFilter filter = idAndFilter.getFilter();
                if (filter == null || filter.isNotificationEnabled(notification)) {
                    ServerNotifForwarder.logger.debug("bufferFilter", "filter matches");
                    final TargetedNotification targetedNotification = new TargetedNotification(notification, idAndFilter.getId());
                    if (ServerNotifForwarder.this.allowNotificationEmission(objectName, targetedNotification)) {
                        list.add(targetedNotification);
                    }
                }
            }
        }
    }
    
    private static class IdAndFilter
    {
        private Integer id;
        private NotificationFilter filter;
        
        IdAndFilter(final Integer id, final NotificationFilter filter) {
            this.id = id;
            this.filter = filter;
        }
        
        Integer getId() {
            return this.id;
        }
        
        NotificationFilter getFilter() {
            return this.filter;
        }
        
        @Override
        public int hashCode() {
            return this.id.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof IdAndFilter && ((IdAndFilter)o).getId().equals(this.getId());
        }
    }
}
