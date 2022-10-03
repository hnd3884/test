package javapns;

import javapns.feedback.AppleFeedbackServer;
import java.util.Collection;
import javapns.feedback.AppleFeedbackServerBasicImpl;
import javapns.feedback.FeedbackServiceManager;
import java.util.Vector;
import javapns.notification.PayloadPerDevice;
import javapns.notification.transmission.NotificationThread;
import javapns.notification.transmission.PushQueue;
import javapns.notification.transmission.NotificationThreads;
import java.util.Iterator;
import java.util.List;
import javapns.notification.AppleNotificationServer;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.notification.PushedNotification;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.devices.Device;
import javapns.devices.Devices;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.NewsstandNotificationPayload;
import javapns.communication.exceptions.KeystoreException;
import javapns.communication.exceptions.CommunicationException;
import javapns.notification.Payload;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotifications;

public class Push
{
    private Push() {
    }
    
    public static PushedNotifications alert(final String message, final Object keystore, final String password, final boolean production, final Object devices) throws CommunicationException, KeystoreException {
        return sendPayload(PushNotificationPayload.alert(message), keystore, password, production, devices);
    }
    
    public static PushedNotifications badge(final int badge, final Object keystore, final String password, final boolean production, final Object devices) throws CommunicationException, KeystoreException {
        return sendPayload(PushNotificationPayload.badge(badge), keystore, password, production, devices);
    }
    
    public static PushedNotifications sound(final String sound, final Object keystore, final String password, final boolean production, final Object devices) throws CommunicationException, KeystoreException {
        return sendPayload(PushNotificationPayload.sound(sound), keystore, password, production, devices);
    }
    
    public static PushedNotifications combined(final String message, final int badge, final String sound, final Object keystore, final String password, final boolean production, final Object devices) throws CommunicationException, KeystoreException {
        return sendPayload(PushNotificationPayload.combined(message, badge, sound), keystore, password, production, devices);
    }
    
    public static PushedNotifications contentAvailable(final Object keystore, final String password, final boolean production, final Object devices) throws CommunicationException, KeystoreException {
        return sendPayload(NewsstandNotificationPayload.contentAvailable(), keystore, password, production, devices);
    }
    
    public static PushedNotifications test(final Object keystore, final String password, final boolean production, final Object devices) throws CommunicationException, KeystoreException {
        return sendPayload(PushNotificationPayload.test(), keystore, password, production, devices);
    }
    
    public static PushedNotifications payload(final Payload payload, final Object keystore, final String password, final boolean production, final Object devices) throws CommunicationException, KeystoreException {
        return sendPayload(payload, keystore, password, production, devices);
    }
    
    private static PushedNotifications sendPayload(final Payload payload, final Object keystore, final String password, final boolean production, final Object devices) throws CommunicationException, KeystoreException {
        final PushedNotifications notifications = new PushedNotifications();
        if (payload == null) {
            return notifications;
        }
        final PushNotificationManager pushManager = new PushNotificationManager();
        try {
            final AppleNotificationServer server = new AppleNotificationServerBasicImpl(keystore, password, production);
            pushManager.initializeConnection(server);
            final List<Device> deviceList = Devices.asDevices(devices);
            notifications.setMaxRetained(deviceList.size());
            for (final Device device : deviceList) {
                try {
                    BasicDevice.validateTokenFormat(device.getToken());
                    final PushedNotification notification = pushManager.sendNotification(device, payload, false);
                    notifications.add(notification);
                }
                catch (final InvalidDeviceTokenFormatException e) {
                    notifications.add(new PushedNotification(device, payload, e));
                }
            }
        }
        finally {
            try {
                pushManager.stopConnection();
            }
            catch (final Exception ex) {}
        }
        return notifications;
    }
    
    public static PushedNotifications payload(final Payload payload, final Object keystore, final String password, final boolean production, final int numberOfThreads, final Object devices) throws Exception {
        if (numberOfThreads <= 0) {
            return sendPayload(payload, keystore, password, production, devices);
        }
        final AppleNotificationServer server = new AppleNotificationServerBasicImpl(keystore, password, production);
        final List<Device> deviceList = Devices.asDevices(devices);
        final NotificationThreads threads = new NotificationThreads(server, payload, deviceList, numberOfThreads);
        threads.start();
        try {
            threads.waitForAllThreads(true);
        }
        catch (final InterruptedException ex) {}
        return threads.getPushedNotifications();
    }
    
    public static PushQueue queue(final Object keystore, final String password, final boolean production, final int numberOfThreads) throws KeystoreException {
        final AppleNotificationServer server = new AppleNotificationServerBasicImpl(keystore, password, production);
        final PushQueue queue = (numberOfThreads <= 1) ? new NotificationThread(server) : new NotificationThreads(server, numberOfThreads);
        return queue;
    }
    
    public static PushedNotifications payloads(final Object keystore, final String password, final boolean production, final Object payloadDevicePairs) throws CommunicationException, KeystoreException {
        return sendPayloads(keystore, password, production, payloadDevicePairs);
    }
    
    public static PushedNotifications payloads(final Object keystore, final String password, final boolean production, final int numberOfThreads, final Object payloadDevicePairs) throws Exception {
        if (numberOfThreads <= 0) {
            return sendPayloads(keystore, password, production, payloadDevicePairs);
        }
        final AppleNotificationServer server = new AppleNotificationServerBasicImpl(keystore, password, production);
        final List<PayloadPerDevice> payloadPerDevicePairs = Devices.asPayloadsPerDevices(payloadDevicePairs);
        final NotificationThreads threads = new NotificationThreads(server, payloadPerDevicePairs, numberOfThreads);
        threads.start();
        try {
            threads.waitForAllThreads(true);
        }
        catch (final InterruptedException ex) {}
        return threads.getPushedNotifications();
    }
    
    private static PushedNotifications sendPayloads(final Object keystore, final String password, final boolean production, final Object payloadDevicePairs) throws CommunicationException, KeystoreException {
        final PushedNotifications notifications = new PushedNotifications();
        if (payloadDevicePairs == null) {
            return notifications;
        }
        final PushNotificationManager pushManager = new PushNotificationManager();
        try {
            final AppleNotificationServer server = new AppleNotificationServerBasicImpl(keystore, password, production);
            pushManager.initializeConnection(server);
            final List<PayloadPerDevice> pairs = Devices.asPayloadsPerDevices(payloadDevicePairs);
            notifications.setMaxRetained(pairs.size());
            for (final PayloadPerDevice ppd : pairs) {
                final Device device = ppd.getDevice();
                final Payload payload = ppd.getPayload();
                try {
                    final PushedNotification notification = pushManager.sendNotification(device, payload, false);
                    notifications.add(notification);
                }
                catch (final Exception e) {
                    notifications.add(new PushedNotification(device, payload, e));
                }
            }
        }
        finally {
            try {
                pushManager.stopConnection();
            }
            catch (final Exception ex) {}
        }
        return notifications;
    }
    
    public static List<Device> feedback(final Object keystore, final String password, final boolean production) throws CommunicationException, KeystoreException {
        final List<Device> devices = new Vector<Device>();
        final FeedbackServiceManager feedbackManager = new FeedbackServiceManager();
        final AppleFeedbackServer server = new AppleFeedbackServerBasicImpl(keystore, password, production);
        devices.addAll(feedbackManager.getDevices(server));
        return devices;
    }
}
