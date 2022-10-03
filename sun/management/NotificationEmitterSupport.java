package sun.management;

import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.ListenerNotFoundException;
import java.util.Collection;
import java.util.ArrayList;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import java.util.Collections;
import java.util.List;
import javax.management.NotificationEmitter;

abstract class NotificationEmitterSupport implements NotificationEmitter
{
    private Object listenerLock;
    private List<ListenerInfo> listenerList;
    
    protected NotificationEmitterSupport() {
        this.listenerLock = new Object();
        this.listenerList = Collections.emptyList();
    }
    
    @Override
    public void addNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) {
        if (notificationListener == null) {
            throw new IllegalArgumentException("Listener can't be null");
        }
        synchronized (this.listenerLock) {
            final ArrayList listenerList = new ArrayList(this.listenerList.size() + 1);
            listenerList.addAll(this.listenerList);
            listenerList.add(new ListenerInfo(notificationListener, notificationFilter, o));
            this.listenerList = listenerList;
        }
    }
    
    @Override
    public void removeNotificationListener(final NotificationListener notificationListener) throws ListenerNotFoundException {
        synchronized (this.listenerLock) {
            final ArrayList listenerList = new ArrayList((Collection<? extends E>)this.listenerList);
            for (int i = listenerList.size() - 1; i >= 0; --i) {
                if (((ListenerInfo)listenerList.get(i)).listener == notificationListener) {
                    listenerList.remove(i);
                }
            }
            if (listenerList.size() == this.listenerList.size()) {
                throw new ListenerNotFoundException("Listener not registered");
            }
            this.listenerList = listenerList;
        }
    }
    
    @Override
    public void removeNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws ListenerNotFoundException {
        boolean b = false;
        synchronized (this.listenerLock) {
            final ArrayList listenerList = new ArrayList((Collection<? extends E>)this.listenerList);
            for (int size = listenerList.size(), i = 0; i < size; ++i) {
                final ListenerInfo listenerInfo = (ListenerInfo)listenerList.get(i);
                if (listenerInfo.listener == notificationListener) {
                    b = true;
                    if (listenerInfo.filter == notificationFilter && listenerInfo.handback == o) {
                        listenerList.remove(i);
                        this.listenerList = listenerList;
                        return;
                    }
                }
            }
        }
        if (b) {
            throw new ListenerNotFoundException("Listener not registered with this filter and handback");
        }
        throw new ListenerNotFoundException("Listener not registered");
    }
    
    void sendNotification(final Notification notification) {
        if (notification == null) {
            return;
        }
        final List<ListenerInfo> listenerList;
        synchronized (this.listenerLock) {
            listenerList = this.listenerList;
        }
        for (int size = listenerList.size(), i = 0; i < size; ++i) {
            final ListenerInfo listenerInfo = listenerList.get(i);
            if (listenerInfo.filter != null) {
                if (!listenerInfo.filter.isNotificationEnabled(notification)) {
                    continue;
                }
            }
            try {
                listenerInfo.listener.handleNotification(notification, listenerInfo.handback);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                throw new AssertionError((Object)"Error in invoking listener");
            }
        }
    }
    
    boolean hasListeners() {
        synchronized (this.listenerLock) {
            return !this.listenerList.isEmpty();
        }
    }
    
    @Override
    public abstract MBeanNotificationInfo[] getNotificationInfo();
    
    private class ListenerInfo
    {
        public NotificationListener listener;
        NotificationFilter filter;
        Object handback;
        
        public ListenerInfo(final NotificationListener listener, final NotificationFilter filter, final Object handback) {
            this.listener = listener;
            this.filter = filter;
            this.handback = handback;
        }
    }
}
