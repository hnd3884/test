package javapns.notification;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class PushedNotifications extends Vector<PushedNotification> implements List<PushedNotification>
{
    private static final long serialVersionUID = 1418782231076330494L;
    private int maxRetained;
    
    public PushedNotifications() {
        this.maxRetained = 1000;
    }
    
    public PushedNotifications(final int capacity) {
        super(capacity);
        this.maxRetained = 1000;
    }
    
    private PushedNotifications(final PushedNotifications parent) {
        this.maxRetained = 1000;
        this.maxRetained = parent.getMaxRetained();
    }
    
    public PushedNotifications getSuccessfulNotifications() {
        final PushedNotifications filteredList = new PushedNotifications(this);
        for (final PushedNotification notification : this) {
            if (notification.isSuccessful()) {
                filteredList.add(notification);
            }
        }
        return filteredList;
    }
    
    public PushedNotifications getFailedNotifications() {
        final PushedNotifications filteredList = new PushedNotifications(this);
        for (final PushedNotification notification : this) {
            if (!notification.isSuccessful()) {
                filteredList.add(notification);
            }
        }
        return filteredList;
    }
    
    @Override
    public synchronized boolean add(final PushedNotification notification) {
        this.prepareAdd(1);
        return super.add(notification);
    }
    
    @Override
    public synchronized void addElement(final PushedNotification notification) {
        this.prepareAdd(1);
        super.addElement(notification);
    }
    
    @Override
    public synchronized boolean addAll(final Collection<? extends PushedNotification> notifications) {
        this.prepareAdd(notifications.size());
        return super.addAll(notifications);
    }
    
    private void prepareAdd(final int n) {
        final int size = this.size();
        if (size + n > this.maxRetained) {
            for (int i = 0; i < n; ++i) {
                this.remove(0);
            }
        }
    }
    
    private int getMaxRetained() {
        return this.maxRetained;
    }
    
    public void setMaxRetained(final int maxRetained) {
        this.maxRetained = maxRetained;
    }
}
