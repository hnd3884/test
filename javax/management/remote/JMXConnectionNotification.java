package javax.management.remote;

import javax.management.Notification;

public class JMXConnectionNotification extends Notification
{
    private static final long serialVersionUID = -2331308725952627538L;
    public static final String OPENED = "jmx.remote.connection.opened";
    public static final String CLOSED = "jmx.remote.connection.closed";
    public static final String FAILED = "jmx.remote.connection.failed";
    public static final String NOTIFS_LOST = "jmx.remote.connection.notifs.lost";
    private final String connectionId;
    
    public JMXConnectionNotification(final String s, final Object o, final String connectionId, final long n, final String s2, final Object userData) {
        super((String)nonNull(s), nonNull(o), Math.max(0L, n), System.currentTimeMillis(), s2);
        if (s == null || o == null || connectionId == null) {
            throw new NullPointerException("Illegal null argument");
        }
        if (n < 0L) {
            throw new IllegalArgumentException("Negative sequence number");
        }
        this.connectionId = connectionId;
        this.setUserData(userData);
    }
    
    private static Object nonNull(final Object o) {
        if (o == null) {
            return "";
        }
        return o;
    }
    
    public String getConnectionId() {
        return this.connectionId;
    }
}
