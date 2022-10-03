package javax.management;

import java.util.Iterator;
import java.util.Vector;
import java.util.List;

public class NotificationFilterSupport implements NotificationFilter
{
    private static final long serialVersionUID = 6579080007561786969L;
    private List<String> enabledTypes;
    
    public NotificationFilterSupport() {
        this.enabledTypes = new Vector<String>();
    }
    
    @Override
    public synchronized boolean isNotificationEnabled(final Notification notification) {
        final String type = notification.getType();
        if (type == null) {
            return false;
        }
        try {
            final Iterator<String> iterator = this.enabledTypes.iterator();
            while (iterator.hasNext()) {
                if (type.startsWith(iterator.next())) {
                    return true;
                }
            }
        }
        catch (final NullPointerException ex) {
            return false;
        }
        return false;
    }
    
    public synchronized void enableType(final String s) throws IllegalArgumentException {
        if (s == null) {
            throw new IllegalArgumentException("The prefix cannot be null.");
        }
        if (!this.enabledTypes.contains(s)) {
            this.enabledTypes.add(s);
        }
    }
    
    public synchronized void disableType(final String s) {
        this.enabledTypes.remove(s);
    }
    
    public synchronized void disableAllTypes() {
        this.enabledTypes.clear();
    }
    
    public synchronized Vector<String> getEnabledTypes() {
        return (Vector)this.enabledTypes;
    }
}
