package com.sun.jmx.remote.internal;

import javax.management.QueryEval;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationFilterSupport;
import java.security.PrivilegedActionException;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import javax.management.MBeanServerDelegate;
import java.util.List;
import javax.management.InstanceNotFoundException;
import java.util.ArrayList;
import javax.management.remote.TargetedNotification;
import javax.management.remote.NotificationResult;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import java.util.HashSet;
import java.util.Iterator;
import com.sun.jmx.remote.util.EnvHelp;
import java.util.Collections;
import java.util.Map;
import javax.management.ObjectName;
import java.util.Set;
import com.sun.jmx.remote.util.ClassLogger;
import javax.management.NotificationFilter;
import javax.management.QueryExp;
import javax.management.NotificationListener;
import java.util.Collection;
import javax.management.MBeanServer;
import java.util.HashMap;

public class ArrayNotificationBuffer implements NotificationBuffer
{
    private boolean disposed;
    private static final Object globalLock;
    private static final HashMap<MBeanServer, ArrayNotificationBuffer> mbsToBuffer;
    private final Collection<ShareBuffer> sharers;
    private final NotificationListener bufferListener;
    private static final QueryExp broadcasterQuery;
    private static final NotificationFilter creationFilter;
    private final NotificationListener creationListener;
    private static final ClassLogger logger;
    private final MBeanServer mBeanServer;
    private final ArrayQueue<NamedNotification> queue;
    private int queueSize;
    private long earliestSequenceNumber;
    private long nextSequenceNumber;
    private Set<ObjectName> createdDuringQuery;
    static final String broadcasterClass;
    
    public static NotificationBuffer getNotificationBuffer(final MBeanServer mBeanServer, Map<String, ?> emptyMap) {
        if (emptyMap == null) {
            emptyMap = Collections.emptyMap();
        }
        final int notifBufferSize = EnvHelp.getNotifBufferSize(emptyMap);
        ArrayNotificationBuffer arrayNotificationBuffer;
        final boolean b;
        final ShareBuffer shareBuffer;
        synchronized (ArrayNotificationBuffer.globalLock) {
            arrayNotificationBuffer = ArrayNotificationBuffer.mbsToBuffer.get(mBeanServer);
            b = (arrayNotificationBuffer == null);
            if (b) {
                arrayNotificationBuffer = new ArrayNotificationBuffer(mBeanServer, notifBufferSize);
                ArrayNotificationBuffer.mbsToBuffer.put(mBeanServer, arrayNotificationBuffer);
            }
            final ArrayNotificationBuffer arrayNotificationBuffer2 = arrayNotificationBuffer;
            arrayNotificationBuffer2.getClass();
            shareBuffer = arrayNotificationBuffer2.new ShareBuffer(notifBufferSize);
        }
        if (b) {
            arrayNotificationBuffer.createListeners();
        }
        return shareBuffer;
    }
    
    static void removeNotificationBuffer(final MBeanServer mBeanServer) {
        synchronized (ArrayNotificationBuffer.globalLock) {
            ArrayNotificationBuffer.mbsToBuffer.remove(mBeanServer);
        }
    }
    
    void addSharer(final ShareBuffer shareBuffer) {
        synchronized (ArrayNotificationBuffer.globalLock) {
            synchronized (this) {
                if (shareBuffer.getSize() > this.queueSize) {
                    this.resize(shareBuffer.getSize());
                }
            }
            this.sharers.add(shareBuffer);
        }
    }
    
    private void removeSharer(final ShareBuffer shareBuffer) {
        final boolean empty;
        synchronized (ArrayNotificationBuffer.globalLock) {
            this.sharers.remove(shareBuffer);
            empty = this.sharers.isEmpty();
            if (empty) {
                removeNotificationBuffer(this.mBeanServer);
            }
            else {
                int n = 0;
                final Iterator<ShareBuffer> iterator = this.sharers.iterator();
                while (iterator.hasNext()) {
                    final int size = iterator.next().getSize();
                    if (size > n) {
                        n = size;
                    }
                }
                if (n < this.queueSize) {
                    this.resize(n);
                }
            }
        }
        if (empty) {
            synchronized (this) {
                this.disposed = true;
                this.notifyAll();
            }
            this.destroyListeners();
        }
    }
    
    private synchronized void resize(final int queueSize) {
        if (queueSize == this.queueSize) {
            return;
        }
        while (this.queue.size() > queueSize) {
            this.dropNotification();
        }
        this.queue.resize(queueSize);
        this.queueSize = queueSize;
    }
    
    private ArrayNotificationBuffer(final MBeanServer mBeanServer, final int queueSize) {
        this.disposed = false;
        this.sharers = new HashSet<ShareBuffer>(1);
        this.bufferListener = new BufferListener();
        this.creationListener = new NotificationListener() {
            @Override
            public void handleNotification(final Notification notification, final Object o) {
                ArrayNotificationBuffer.logger.debug("creationListener", "handleNotification called");
                ArrayNotificationBuffer.this.createdNotification((MBeanServerNotification)notification);
            }
        };
        if (ArrayNotificationBuffer.logger.traceOn()) {
            ArrayNotificationBuffer.logger.trace("Constructor", "queueSize=" + queueSize);
        }
        if (mBeanServer == null || queueSize < 1) {
            throw new IllegalArgumentException("Bad args");
        }
        this.mBeanServer = mBeanServer;
        this.queueSize = queueSize;
        this.queue = new ArrayQueue<NamedNotification>(queueSize);
        this.earliestSequenceNumber = System.currentTimeMillis();
        this.nextSequenceNumber = this.earliestSequenceNumber;
        ArrayNotificationBuffer.logger.trace("Constructor", "ends");
    }
    
    private synchronized boolean isDisposed() {
        return this.disposed;
    }
    
    @Override
    public void dispose() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public NotificationResult fetchNotifications(final NotificationBufferFilter notificationBufferFilter, final long n, final long n2, int n3) throws InterruptedException {
        ArrayNotificationBuffer.logger.trace("fetchNotifications", "starts");
        if (n < 0L || this.isDisposed()) {
            synchronized (this) {
                return new NotificationResult(this.earliestSequenceNumber(), this.nextSequenceNumber(), new TargetedNotification[0]);
            }
        }
        if (notificationBufferFilter == null || n < 0L || n2 < 0L || n3 < 0) {
            ArrayNotificationBuffer.logger.trace("fetchNotifications", "Bad args");
            throw new IllegalArgumentException("Bad args to fetch");
        }
        if (ArrayNotificationBuffer.logger.debugOn()) {
            ArrayNotificationBuffer.logger.trace("fetchNotifications", "filter=" + notificationBufferFilter + "; startSeq=" + n + "; timeout=" + n2 + "; max=" + n3);
        }
        if (n > this.nextSequenceNumber()) {
            final String string = "Start sequence number too big: " + n + " > " + this.nextSequenceNumber();
            ArrayNotificationBuffer.logger.trace("fetchNotifications", string);
            throw new IllegalArgumentException(string);
        }
        long n4 = System.currentTimeMillis() + n2;
        if (n4 < 0L) {
            n4 = Long.MAX_VALUE;
        }
        if (ArrayNotificationBuffer.logger.debugOn()) {
            ArrayNotificationBuffer.logger.debug("fetchNotifications", "endTime=" + n4);
        }
        long n5 = -1L;
        long n6 = n;
        final ArrayList list = new ArrayList();
        while (true) {
            ArrayNotificationBuffer.logger.debug("fetchNotifications", "main loop starts");
            NamedNotification notification;
            synchronized (this) {
                if (n5 < 0L) {
                    n5 = this.earliestSequenceNumber();
                    if (ArrayNotificationBuffer.logger.debugOn()) {
                        ArrayNotificationBuffer.logger.debug("fetchNotifications", "earliestSeq=" + n5);
                    }
                    if (n6 < n5) {
                        n6 = n5;
                        ArrayNotificationBuffer.logger.debug("fetchNotifications", "nextSeq=earliestSeq");
                    }
                }
                else {
                    n5 = this.earliestSequenceNumber();
                }
                if (n6 < n5) {
                    ArrayNotificationBuffer.logger.trace("fetchNotifications", "nextSeq=" + n6 + " < earliestSeq=" + n5 + " so may have lost notifs");
                    break;
                }
                if (n6 < this.nextSequenceNumber()) {
                    notification = this.notificationAt(n6);
                    if (!(notificationBufferFilter instanceof ServerNotifForwarder.NotifForwarderBufferFilter)) {
                        try {
                            ServerNotifForwarder.checkMBeanPermission(this.mBeanServer, notification.getObjectName(), "addNotificationListener");
                        }
                        catch (final InstanceNotFoundException | SecurityException ex) {
                            if (ArrayNotificationBuffer.logger.debugOn()) {
                                ArrayNotificationBuffer.logger.debug("fetchNotifications", "candidate: " + notification + " skipped. exception " + ex);
                            }
                            ++n6;
                            continue;
                        }
                    }
                    if (ArrayNotificationBuffer.logger.debugOn()) {
                        ArrayNotificationBuffer.logger.debug("fetchNotifications", "candidate: " + notification);
                        ArrayNotificationBuffer.logger.debug("fetchNotifications", "nextSeq now " + n6);
                    }
                }
                else {
                    if (list.size() > 0) {
                        ArrayNotificationBuffer.logger.debug("fetchNotifications", "no more notifs but have some so don't wait");
                        break;
                    }
                    final long n7 = n4 - System.currentTimeMillis();
                    if (n7 <= 0L) {
                        ArrayNotificationBuffer.logger.debug("fetchNotifications", "timeout");
                        break;
                    }
                    if (this.isDisposed()) {
                        if (ArrayNotificationBuffer.logger.debugOn()) {
                            ArrayNotificationBuffer.logger.debug("fetchNotifications", "dispose callled, no wait");
                        }
                        return new NotificationResult(this.earliestSequenceNumber(), this.nextSequenceNumber(), new TargetedNotification[0]);
                    }
                    if (ArrayNotificationBuffer.logger.debugOn()) {
                        ArrayNotificationBuffer.logger.debug("fetchNotifications", "wait(" + n7 + ")");
                    }
                    this.wait(n7);
                    continue;
                }
            }
            final ObjectName objectName = notification.getObjectName();
            final Notification notification2 = notification.getNotification();
            final ArrayList<TargetedNotification> list2 = new ArrayList<TargetedNotification>();
            ArrayNotificationBuffer.logger.debug("fetchNotifications", "applying filter to candidate");
            notificationBufferFilter.apply(list2, objectName, notification2);
            if (list2.size() > 0) {
                if (n3 <= 0) {
                    ArrayNotificationBuffer.logger.debug("fetchNotifications", "reached maxNotifications");
                    break;
                }
                --n3;
                if (ArrayNotificationBuffer.logger.debugOn()) {
                    ArrayNotificationBuffer.logger.debug("fetchNotifications", "add: " + list2);
                }
                list.addAll(list2);
            }
            ++n6;
        }
        final TargetedNotification[] array = new TargetedNotification[list.size()];
        list.toArray(array);
        final NotificationResult notificationResult = new NotificationResult(n5, n6, array);
        if (ArrayNotificationBuffer.logger.debugOn()) {
            ArrayNotificationBuffer.logger.debug("fetchNotifications", notificationResult.toString());
        }
        ArrayNotificationBuffer.logger.trace("fetchNotifications", "ends");
        return notificationResult;
    }
    
    synchronized long earliestSequenceNumber() {
        return this.earliestSequenceNumber;
    }
    
    synchronized long nextSequenceNumber() {
        return this.nextSequenceNumber;
    }
    
    synchronized void addNotification(final NamedNotification namedNotification) {
        if (ArrayNotificationBuffer.logger.traceOn()) {
            ArrayNotificationBuffer.logger.trace("addNotification", namedNotification.toString());
        }
        while (this.queue.size() >= this.queueSize) {
            this.dropNotification();
            if (ArrayNotificationBuffer.logger.debugOn()) {
                ArrayNotificationBuffer.logger.debug("addNotification", "dropped oldest notif, earliestSeq=" + this.earliestSequenceNumber);
            }
        }
        this.queue.add(namedNotification);
        ++this.nextSequenceNumber;
        if (ArrayNotificationBuffer.logger.debugOn()) {
            ArrayNotificationBuffer.logger.debug("addNotification", "nextSeq=" + this.nextSequenceNumber);
        }
        this.notifyAll();
    }
    
    private void dropNotification() {
        this.queue.remove(0);
        ++this.earliestSequenceNumber;
    }
    
    synchronized NamedNotification notificationAt(final long n) {
        final long n2 = n - this.earliestSequenceNumber;
        if (n2 < 0L || n2 > 2147483647L) {
            final String string = "Bad sequence number: " + n + " (earliest " + this.earliestSequenceNumber + ")";
            ArrayNotificationBuffer.logger.trace("notificationAt", string);
            throw new IllegalArgumentException(string);
        }
        return this.queue.get((int)n2);
    }
    
    private void createListeners() {
        ArrayNotificationBuffer.logger.debug("createListeners", "starts");
        synchronized (this) {
            this.createdDuringQuery = new HashSet<ObjectName>();
        }
        try {
            this.addNotificationListener(MBeanServerDelegate.DELEGATE_NAME, this.creationListener, ArrayNotificationBuffer.creationFilter, null);
            ArrayNotificationBuffer.logger.debug("createListeners", "added creationListener");
        }
        catch (final Exception ex) {
            final IllegalArgumentException ex2 = new IllegalArgumentException("Can't add listener to MBean server delegate: " + ex);
            EnvHelp.initCause(ex2, ex);
            ArrayNotificationBuffer.logger.fine("createListeners", "Can't add listener to MBean server delegate: " + ex);
            ArrayNotificationBuffer.logger.debug("createListeners", ex);
            throw ex2;
        }
        final HashSet set = new HashSet((Collection<? extends E>)this.queryNames(null, ArrayNotificationBuffer.broadcasterQuery));
        synchronized (this) {
            set.addAll(this.createdDuringQuery);
            this.createdDuringQuery = null;
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            this.addBufferListener((ObjectName)iterator.next());
        }
        ArrayNotificationBuffer.logger.debug("createListeners", "ends");
    }
    
    private void addBufferListener(final ObjectName objectName) {
        this.checkNoLocks();
        if (ArrayNotificationBuffer.logger.debugOn()) {
            ArrayNotificationBuffer.logger.debug("addBufferListener", objectName.toString());
        }
        try {
            this.addNotificationListener(objectName, this.bufferListener, null, objectName);
        }
        catch (final Exception ex) {
            ArrayNotificationBuffer.logger.trace("addBufferListener", ex);
        }
    }
    
    private void removeBufferListener(final ObjectName objectName) {
        this.checkNoLocks();
        if (ArrayNotificationBuffer.logger.debugOn()) {
            ArrayNotificationBuffer.logger.debug("removeBufferListener", objectName.toString());
        }
        try {
            this.removeNotificationListener(objectName, this.bufferListener);
        }
        catch (final Exception ex) {
            ArrayNotificationBuffer.logger.trace("removeBufferListener", ex);
        }
    }
    
    private void addNotificationListener(final ObjectName objectName, final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws Exception {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws InstanceNotFoundException {
                    ArrayNotificationBuffer.this.mBeanServer.addNotificationListener(objectName, notificationListener, notificationFilter, o);
                    return null;
                }
            });
        }
        catch (final Exception ex) {
            throw extractException(ex);
        }
    }
    
    private void removeNotificationListener(final ObjectName objectName, final NotificationListener notificationListener) throws Exception {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    ArrayNotificationBuffer.this.mBeanServer.removeNotificationListener(objectName, notificationListener);
                    return null;
                }
            });
        }
        catch (final Exception ex) {
            throw extractException(ex);
        }
    }
    
    private Set<ObjectName> queryNames(final ObjectName objectName, final QueryExp queryExp) {
        final PrivilegedAction<Set<ObjectName>> privilegedAction = new PrivilegedAction<Set<ObjectName>>() {
            @Override
            public Set<ObjectName> run() {
                return ArrayNotificationBuffer.this.mBeanServer.queryNames(objectName, queryExp);
            }
        };
        try {
            return AccessController.doPrivileged((PrivilegedAction<Set<ObjectName>>)privilegedAction);
        }
        catch (final RuntimeException ex) {
            ArrayNotificationBuffer.logger.fine("queryNames", "Failed to query names: " + ex);
            ArrayNotificationBuffer.logger.debug("queryNames", ex);
            throw ex;
        }
    }
    
    private static boolean isInstanceOf(final MBeanServer mBeanServer, final ObjectName objectName, final String s) {
        final PrivilegedExceptionAction<Boolean> privilegedExceptionAction = new PrivilegedExceptionAction<Boolean>() {
            @Override
            public Boolean run() throws InstanceNotFoundException {
                return mBeanServer.isInstanceOf(objectName, s);
            }
        };
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)privilegedExceptionAction);
        }
        catch (final Exception ex) {
            ArrayNotificationBuffer.logger.fine("isInstanceOf", "failed: " + ex);
            ArrayNotificationBuffer.logger.debug("isInstanceOf", ex);
            return false;
        }
    }
    
    private void createdNotification(final MBeanServerNotification mBeanServerNotification) {
        if (!mBeanServerNotification.getType().equals("JMX.mbean.registered")) {
            ArrayNotificationBuffer.logger.warning("createNotification", "bad type: " + mBeanServerNotification.getType());
            return;
        }
        final ObjectName mBeanName = mBeanServerNotification.getMBeanName();
        if (ArrayNotificationBuffer.logger.debugOn()) {
            ArrayNotificationBuffer.logger.debug("createdNotification", "for: " + mBeanName);
        }
        synchronized (this) {
            if (this.createdDuringQuery != null) {
                this.createdDuringQuery.add(mBeanName);
                return;
            }
        }
        if (isInstanceOf(this.mBeanServer, mBeanName, ArrayNotificationBuffer.broadcasterClass)) {
            this.addBufferListener(mBeanName);
            if (this.isDisposed()) {
                this.removeBufferListener(mBeanName);
            }
        }
    }
    
    private void destroyListeners() {
        this.checkNoLocks();
        ArrayNotificationBuffer.logger.debug("destroyListeners", "starts");
        try {
            this.removeNotificationListener(MBeanServerDelegate.DELEGATE_NAME, this.creationListener);
        }
        catch (final Exception ex) {
            ArrayNotificationBuffer.logger.warning("remove listener from MBeanServer delegate", ex);
        }
        for (final ObjectName objectName : this.queryNames(null, ArrayNotificationBuffer.broadcasterQuery)) {
            if (ArrayNotificationBuffer.logger.debugOn()) {
                ArrayNotificationBuffer.logger.debug("destroyListeners", "remove listener from " + objectName);
            }
            this.removeBufferListener(objectName);
        }
        ArrayNotificationBuffer.logger.debug("destroyListeners", "ends");
    }
    
    private void checkNoLocks() {
        if (Thread.holdsLock(this) || Thread.holdsLock(ArrayNotificationBuffer.globalLock)) {
            ArrayNotificationBuffer.logger.warning("checkNoLocks", "lock protocol violation");
        }
    }
    
    private static Exception extractException(Exception exception) {
        while (exception instanceof PrivilegedActionException) {
            exception = ((PrivilegedActionException)exception).getException();
        }
        return exception;
    }
    
    static {
        globalLock = new Object();
        mbsToBuffer = new HashMap<MBeanServer, ArrayNotificationBuffer>(1);
        broadcasterQuery = new BroadcasterQuery();
        final NotificationFilterSupport creationFilter2 = new NotificationFilterSupport();
        creationFilter2.enableType("JMX.mbean.registered");
        creationFilter = creationFilter2;
        logger = new ClassLogger("javax.management.remote.misc", "ArrayNotificationBuffer");
        broadcasterClass = NotificationBroadcaster.class.getName();
    }
    
    private class ShareBuffer implements NotificationBuffer
    {
        private final int size;
        
        ShareBuffer(final int size) {
            this.size = size;
            ArrayNotificationBuffer.this.addSharer(this);
        }
        
        @Override
        public NotificationResult fetchNotifications(final NotificationBufferFilter notificationBufferFilter, final long n, final long n2, final int n3) throws InterruptedException {
            return ArrayNotificationBuffer.this.fetchNotifications(notificationBufferFilter, n, n2, n3);
        }
        
        @Override
        public void dispose() {
            ArrayNotificationBuffer.this.removeSharer(this);
        }
        
        int getSize() {
            return this.size;
        }
    }
    
    private static class NamedNotification
    {
        private final ObjectName sender;
        private final Notification notification;
        
        NamedNotification(final ObjectName sender, final Notification notification) {
            this.sender = sender;
            this.notification = notification;
        }
        
        ObjectName getObjectName() {
            return this.sender;
        }
        
        Notification getNotification() {
            return this.notification;
        }
        
        @Override
        public String toString() {
            return "NamedNotification(" + this.sender + ", " + this.notification + ")";
        }
    }
    
    private class BufferListener implements NotificationListener
    {
        @Override
        public void handleNotification(final Notification notification, final Object o) {
            if (ArrayNotificationBuffer.logger.debugOn()) {
                ArrayNotificationBuffer.logger.debug("BufferListener.handleNotification", "notif=" + notification + "; handback=" + o);
            }
            ArrayNotificationBuffer.this.addNotification(new NamedNotification((ObjectName)o, notification));
        }
    }
    
    private static class BroadcasterQuery extends QueryEval implements QueryExp
    {
        private static final long serialVersionUID = 7378487660587592048L;
        
        @Override
        public boolean apply(final ObjectName objectName) {
            return isInstanceOf(QueryEval.getMBeanServer(), objectName, ArrayNotificationBuffer.broadcasterClass);
        }
    }
}
