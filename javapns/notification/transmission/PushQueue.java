package javapns.notification.transmission;

import javapns.notification.PushedNotifications;
import java.util.List;
import javapns.notification.PayloadPerDevice;
import javapns.devices.Device;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.notification.Payload;

public interface PushQueue
{
    PushQueue add(final Payload p0, final String p1) throws InvalidDeviceTokenFormatException;
    
    PushQueue add(final Payload p0, final Device p1);
    
    PushQueue add(final PayloadPerDevice p0);
    
    PushQueue start();
    
    List<Exception> getCriticalExceptions();
    
    PushedNotifications getPushedNotifications();
    
    void clearPushedNotifications();
}
