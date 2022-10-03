package javax.management.remote;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class NotificationResult implements Serializable
{
    private static final long serialVersionUID = 1191800228721395279L;
    private long earliestSequenceNumber;
    private long nextSequenceNumber;
    private TargetedNotification[] targetedNotifications;
    
    public NotificationResult(final long earliestSequenceNumber, final long nextSequenceNumber, final TargetedNotification[] array) {
        validate(array, earliestSequenceNumber, nextSequenceNumber);
        this.earliestSequenceNumber = earliestSequenceNumber;
        this.nextSequenceNumber = nextSequenceNumber;
        this.targetedNotifications = ((array.length == 0) ? array : array.clone());
    }
    
    public long getEarliestSequenceNumber() {
        return this.earliestSequenceNumber;
    }
    
    public long getNextSequenceNumber() {
        return this.nextSequenceNumber;
    }
    
    public TargetedNotification[] getTargetedNotifications() {
        return (this.targetedNotifications.length == 0) ? this.targetedNotifications : this.targetedNotifications.clone();
    }
    
    @Override
    public String toString() {
        return "NotificationResult: earliest=" + this.getEarliestSequenceNumber() + "; next=" + this.getNextSequenceNumber() + "; nnotifs=" + this.getTargetedNotifications().length;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            validate(this.targetedNotifications, this.earliestSequenceNumber, this.nextSequenceNumber);
            this.targetedNotifications = ((this.targetedNotifications.length == 0) ? this.targetedNotifications : this.targetedNotifications.clone());
        }
        catch (final IllegalArgumentException ex) {
            throw new InvalidObjectException(ex.getMessage());
        }
    }
    
    private static void validate(final TargetedNotification[] array, final long n, final long n2) throws IllegalArgumentException {
        if (array == null) {
            throw new IllegalArgumentException("Notifications null");
        }
        if (n < 0L || n2 < 0L) {
            throw new IllegalArgumentException("Bad sequence numbers");
        }
    }
}
