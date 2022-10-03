package javax.management;

import java.util.Objects;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import com.sun.jmx.remote.util.ClassLogger;
import java.util.concurrent.Executor;
import java.util.List;

public class NotificationBroadcasterSupport implements NotificationEmitter
{
    private List<ListenerInfo> listenerList;
    private final Executor executor;
    private final MBeanNotificationInfo[] notifInfo;
    private static final Executor defaultExecutor;
    private static final MBeanNotificationInfo[] NO_NOTIFICATION_INFO;
    private static final ClassLogger logger;
    
    public NotificationBroadcasterSupport() {
        this((Executor)null, (MBeanNotificationInfo[])null);
    }
    
    public NotificationBroadcasterSupport(final Executor executor) {
        this(executor, (MBeanNotificationInfo[])null);
    }
    
    public NotificationBroadcasterSupport(final MBeanNotificationInfo... array) {
        this((Executor)null, array);
    }
    
    public NotificationBroadcasterSupport(final Executor executor, final MBeanNotificationInfo... array) {
        this.listenerList = new CopyOnWriteArrayList<ListenerInfo>();
        this.executor = ((executor != null) ? executor : NotificationBroadcasterSupport.defaultExecutor);
        this.notifInfo = ((array == null) ? NotificationBroadcasterSupport.NO_NOTIFICATION_INFO : array.clone());
    }
    
    @Override
    public void addNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) {
        if (notificationListener == null) {
            throw new IllegalArgumentException("Listener can't be null");
        }
        this.listenerList.add(new ListenerInfo(notificationListener, notificationFilter, o));
    }
    
    @Override
    public void removeNotificationListener(final NotificationListener notificationListener) throws ListenerNotFoundException {
        if (!this.listenerList.removeAll(Collections.singleton(new WildcardListenerInfo(notificationListener)))) {
            throw new ListenerNotFoundException("Listener not registered");
        }
    }
    
    @Override
    public void removeNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws ListenerNotFoundException {
        if (!this.listenerList.remove(new ListenerInfo(notificationListener, notificationFilter, o))) {
            throw new ListenerNotFoundException("Listener not registered (with this filter and handback)");
        }
    }
    
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        if (this.notifInfo.length == 0) {
            return this.notifInfo;
        }
        return this.notifInfo.clone();
    }
    
    public void sendNotification(final Notification notification) {
        if (notification == null) {
            return;
        }
        for (final ListenerInfo listenerInfo : this.listenerList) {
            boolean b;
            try {
                b = (listenerInfo.filter == null || listenerInfo.filter.isNotificationEnabled(notification));
            }
            catch (final Exception ex) {
                if (!NotificationBroadcasterSupport.logger.debugOn()) {
                    continue;
                }
                NotificationBroadcasterSupport.logger.debug("sendNotification", ex);
                continue;
            }
            if (b) {
                this.executor.execute(new SendNotifJob(notification, listenerInfo));
            }
        }
    }
    
    protected void handleNotification(final NotificationListener notificationListener, final Notification notification, final Object o) {
        notificationListener.handleNotification(notification, o);
    }
    
    static {
        defaultExecutor = new Executor() {
            @Override
            public void execute(final Runnable runnable) {
                runnable.run();
            }
        };
        NO_NOTIFICATION_INFO = new MBeanNotificationInfo[0];
        logger = new ClassLogger("javax.management", "NotificationBroadcasterSupport");
    }
    
    private static class ListenerInfo
    {
        NotificationListener listener;
        NotificationFilter filter;
        Object handback;
        
        ListenerInfo(final NotificationListener listener, final NotificationFilter filter, final Object handback) {
            this.listener = listener;
            this.filter = filter;
            this.handback = handback;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof ListenerInfo)) {
                return false;
            }
            final ListenerInfo listenerInfo = (ListenerInfo)o;
            if (listenerInfo instanceof WildcardListenerInfo) {
                return listenerInfo.listener == this.listener;
            }
            return listenerInfo.listener == this.listener && listenerInfo.filter == this.filter && listenerInfo.handback == this.handback;
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.listener);
        }
    }
    
    private static class WildcardListenerInfo extends ListenerInfo
    {
        WildcardListenerInfo(final NotificationListener notificationListener) {
            super(notificationListener, null, null);
        }
        
        @Override
        public boolean equals(final Object o) {
            assert !(o instanceof WildcardListenerInfo);
            return o.equals(this);
        }
        
        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
    
    private class SendNotifJob implements Runnable
    {
        private final Notification notif;
        private final ListenerInfo listenerInfo;
        
        public SendNotifJob(final Notification notif, final ListenerInfo listenerInfo) {
            this.notif = notif;
            this.listenerInfo = listenerInfo;
        }
        
        @Override
        public void run() {
            try {
                NotificationBroadcasterSupport.this.handleNotification(this.listenerInfo.listener, this.notif, this.listenerInfo.handback);
            }
            catch (final Exception ex) {
                if (NotificationBroadcasterSupport.logger.debugOn()) {
                    NotificationBroadcasterSupport.logger.debug("SendNotifJob-run", ex);
                }
            }
        }
    }
}
