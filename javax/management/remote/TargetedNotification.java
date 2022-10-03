package javax.management.remote;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import javax.management.Notification;
import java.io.Serializable;

public class TargetedNotification implements Serializable
{
    private static final long serialVersionUID = 7676132089779300926L;
    private Notification notif;
    private Integer id;
    
    public TargetedNotification(final Notification notif, final Integer id) {
        validate(notif, id);
        this.notif = notif;
        this.id = id;
    }
    
    public Notification getNotification() {
        return this.notif;
    }
    
    public Integer getListenerID() {
        return this.id;
    }
    
    @Override
    public String toString() {
        return "{" + this.notif + ", " + this.id + "}";
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            validate(this.notif, this.id);
        }
        catch (final IllegalArgumentException ex) {
            throw new InvalidObjectException(ex.getMessage());
        }
    }
    
    private static void validate(final Notification notification, final Integer n) throws IllegalArgumentException {
        if (notification == null) {
            throw new IllegalArgumentException("Invalid notification: null");
        }
        if (n == null) {
            throw new IllegalArgumentException("Invalid listener ID: null");
        }
    }
}
