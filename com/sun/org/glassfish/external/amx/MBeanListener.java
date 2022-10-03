package com.sun.org.glassfish.external.amx;

import java.util.concurrent.CountDownLatch;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import java.util.Iterator;
import java.util.Set;
import javax.management.QueryExp;
import javax.management.NotificationFilter;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import com.sun.org.glassfish.external.arc.Stability;
import com.sun.org.glassfish.external.arc.Taxonomy;
import javax.management.NotificationListener;

@Taxonomy(stability = Stability.UNCOMMITTED)
public class MBeanListener<T extends Callback> implements NotificationListener
{
    private final String mJMXDomain;
    private final String mType;
    private final String mName;
    private final ObjectName mObjectName;
    private final MBeanServerConnection mMBeanServer;
    private final T mCallback;
    
    private static void debug(final Object o) {
        System.out.println("" + o);
    }
    
    @Override
    public String toString() {
        return "MBeanListener: ObjectName=" + this.mObjectName + ", type=" + this.mType + ", name=" + this.mName;
    }
    
    public String getType() {
        return this.mType;
    }
    
    public String getName() {
        return this.mName;
    }
    
    public MBeanServerConnection getMBeanServer() {
        return this.mMBeanServer;
    }
    
    public T getCallback() {
        return this.mCallback;
    }
    
    public MBeanListener(final MBeanServerConnection server, final ObjectName objectName, final T callback) {
        this.mMBeanServer = server;
        this.mObjectName = objectName;
        this.mJMXDomain = null;
        this.mType = null;
        this.mName = null;
        this.mCallback = callback;
    }
    
    public MBeanListener(final MBeanServerConnection server, final String domain, final String type, final T callback) {
        this(server, domain, type, null, callback);
    }
    
    public MBeanListener(final MBeanServerConnection server, final String domain, final String type, final String name, final T callback) {
        this.mMBeanServer = server;
        this.mJMXDomain = domain;
        this.mType = type;
        this.mName = name;
        this.mObjectName = null;
        this.mCallback = callback;
    }
    
    private boolean isRegistered(final MBeanServerConnection conn, final ObjectName objectName) {
        try {
            return conn.isRegistered(objectName);
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void startListening() {
        try {
            this.mMBeanServer.addNotificationListener(AMXUtil.getMBeanServerDelegateObjectName(), this, null, this);
        }
        catch (final Exception e) {
            throw new RuntimeException("Can't add NotificationListener", e);
        }
        if (this.mObjectName != null) {
            if (this.isRegistered(this.mMBeanServer, this.mObjectName)) {
                this.mCallback.mbeanRegistered(this.mObjectName, this);
            }
        }
        else {
            String props = "type=" + this.mType;
            if (this.mName != null) {
                props = props + "," + "name" + this.mName;
            }
            final ObjectName pattern = AMXUtil.newObjectName(this.mJMXDomain + ":" + props);
            try {
                final Set<ObjectName> matched = this.mMBeanServer.queryNames(pattern, null);
                for (final ObjectName objectName : matched) {
                    this.mCallback.mbeanRegistered(objectName, this);
                }
            }
            catch (final Exception e2) {
                throw new RuntimeException(e2);
            }
        }
    }
    
    public void stopListening() {
        try {
            this.mMBeanServer.removeNotificationListener(AMXUtil.getMBeanServerDelegateObjectName(), this);
        }
        catch (final Exception e) {
            throw new RuntimeException("Can't remove NotificationListener " + this, e);
        }
    }
    
    @Override
    public void handleNotification(final Notification notifIn, final Object handback) {
        if (notifIn instanceof MBeanServerNotification) {
            final MBeanServerNotification notif = (MBeanServerNotification)notifIn;
            final ObjectName objectName = notif.getMBeanName();
            boolean match = false;
            if (this.mObjectName != null && this.mObjectName.equals(objectName)) {
                match = true;
            }
            else if (objectName.getDomain().equals(this.mJMXDomain) && this.mType != null && this.mType.equals(objectName.getKeyProperty("type"))) {
                final String mbeanName = objectName.getKeyProperty("name");
                if (this.mName != null && this.mName.equals(mbeanName)) {
                    match = true;
                }
            }
            if (match) {
                final String notifType = notif.getType();
                if ("JMX.mbean.registered".equals(notifType)) {
                    this.mCallback.mbeanRegistered(objectName, this);
                }
                else if ("JMX.mbean.unregistered".equals(notifType)) {
                    this.mCallback.mbeanUnregistered(objectName, this);
                }
            }
        }
    }
    
    public static class CallbackImpl implements Callback
    {
        private volatile ObjectName mRegistered;
        private volatile ObjectName mUnregistered;
        private final boolean mStopAtFirst;
        protected final CountDownLatch mLatch;
        
        public CallbackImpl() {
            this(true);
        }
        
        public CallbackImpl(final boolean stopAtFirst) {
            this.mRegistered = null;
            this.mUnregistered = null;
            this.mLatch = new CountDownLatch(1);
            this.mStopAtFirst = stopAtFirst;
        }
        
        public ObjectName getRegistered() {
            return this.mRegistered;
        }
        
        public ObjectName getUnregistered() {
            return this.mUnregistered;
        }
        
        public void await() {
            try {
                this.mLatch.await();
            }
            catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public void mbeanRegistered(final ObjectName objectName, final MBeanListener listener) {
            this.mRegistered = objectName;
            if (this.mStopAtFirst) {
                listener.stopListening();
            }
        }
        
        @Override
        public void mbeanUnregistered(final ObjectName objectName, final MBeanListener listener) {
            this.mUnregistered = objectName;
            if (this.mStopAtFirst) {
                listener.stopListening();
            }
        }
    }
    
    public interface Callback
    {
        void mbeanRegistered(final ObjectName p0, final MBeanListener p1);
        
        void mbeanUnregistered(final ObjectName p0, final MBeanListener p1);
    }
}
