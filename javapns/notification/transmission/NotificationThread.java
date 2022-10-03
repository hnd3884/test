package javapns.notification.transmission;

import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.notification.PushedNotification;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Devices;
import java.util.Vector;
import javapns.notification.PayloadPerDevice;
import javapns.devices.Device;
import java.util.List;
import javapns.notification.Payload;
import javapns.notification.PushedNotifications;
import javapns.notification.PushNotificationManager;
import javapns.notification.AppleNotificationServer;

public class NotificationThread implements Runnable, PushQueue
{
    private static final int DEFAULT_MAXNOTIFICATIONSPERCONNECTION = 200;
    private final Thread thread;
    private final AppleNotificationServer server;
    private final PushNotificationManager notificationManager;
    private final PushedNotifications notifications;
    private boolean started;
    private int maxNotificationsPerConnection;
    private long sleepBetweenNotifications;
    private NotificationProgressListener listener;
    private int threadNumber;
    private int nextMessageIdentifier;
    private MODE mode;
    private boolean busy;
    private Payload payload;
    private List<Device> devices;
    private List<PayloadPerDevice> messages;
    private Exception exception;
    
    public NotificationThread(final NotificationThreads threads, final PushNotificationManager notificationManager, final AppleNotificationServer server, final Payload payload, final Object devices) {
        this.notifications = new PushedNotifications();
        this.started = false;
        this.maxNotificationsPerConnection = 200;
        this.sleepBetweenNotifications = 0L;
        this.threadNumber = 1;
        this.nextMessageIdentifier = 1;
        this.mode = MODE.LIST;
        this.busy = false;
        this.messages = new Vector<PayloadPerDevice>();
        this.thread = new Thread(threads, this, "JavaPNS" + ((threads != null) ? " grouped" : " standalone") + " notification thread in LIST mode");
        this.notificationManager = ((notificationManager == null) ? new PushNotificationManager() : notificationManager);
        this.server = server;
        this.payload = payload;
        this.devices = Devices.asDevices(devices);
        this.notifications.setMaxRetained(this.devices.size());
    }
    
    public NotificationThread(final NotificationThreads threads, final PushNotificationManager notificationManager, final AppleNotificationServer server, final Object messages) {
        this.notifications = new PushedNotifications();
        this.started = false;
        this.maxNotificationsPerConnection = 200;
        this.sleepBetweenNotifications = 0L;
        this.threadNumber = 1;
        this.nextMessageIdentifier = 1;
        this.mode = MODE.LIST;
        this.busy = false;
        this.messages = new Vector<PayloadPerDevice>();
        this.thread = new Thread(threads, this, "JavaPNS" + ((threads != null) ? " grouped" : " standalone") + " notification thread in LIST mode");
        this.notificationManager = ((notificationManager == null) ? new PushNotificationManager() : notificationManager);
        this.server = server;
        this.messages = Devices.asPayloadsPerDevices(messages);
        this.notifications.setMaxRetained(this.messages.size());
    }
    
    public NotificationThread(final PushNotificationManager notificationManager, final AppleNotificationServer server, final Payload payload, final Object devices) {
        this(null, notificationManager, server, payload, devices);
    }
    
    public NotificationThread(final PushNotificationManager notificationManager, final AppleNotificationServer server, final Object messages) {
        this(null, notificationManager, server, messages);
    }
    
    public NotificationThread(final NotificationThreads threads, final PushNotificationManager notificationManager, final AppleNotificationServer server) {
        this.notifications = new PushedNotifications();
        this.started = false;
        this.maxNotificationsPerConnection = 200;
        this.sleepBetweenNotifications = 0L;
        this.threadNumber = 1;
        this.nextMessageIdentifier = 1;
        this.mode = MODE.LIST;
        this.busy = false;
        this.messages = new Vector<PayloadPerDevice>();
        this.thread = new Thread(threads, this, "JavaPNS" + ((threads != null) ? " grouped" : " standalone") + " notification thread in QUEUE mode");
        this.notificationManager = ((notificationManager == null) ? new PushNotificationManager() : notificationManager);
        this.server = server;
        this.mode = MODE.QUEUE;
        this.thread.setDaemon(true);
    }
    
    public NotificationThread(final PushNotificationManager notificationManager, final AppleNotificationServer server) {
        this(null, notificationManager, server);
    }
    
    public NotificationThread(final AppleNotificationServer server) {
        this(null, new PushNotificationManager(), server);
    }
    
    @Override
    public synchronized NotificationThread start() {
        if (this.started) {
            return this;
        }
        this.started = true;
        try {
            this.thread.start();
        }
        catch (final IllegalStateException ex) {}
        return this;
    }
    
    @Override
    public void run() {
        switch (this.mode) {
            case LIST: {
                this.runList();
                break;
            }
            case QUEUE: {
                this.runQueue();
                break;
            }
        }
    }
    
    private void runList() {
        if (this.listener != null) {
            this.listener.eventThreadStarted(this);
        }
        this.busy = true;
        try {
            final int total = this.size();
            this.notificationManager.initializeConnection(this.server);
            for (int i = 0; i < total; ++i) {
                Device device;
                Payload payload;
                if (this.devices != null) {
                    device = this.devices.get(i);
                    payload = this.payload;
                }
                else {
                    final PayloadPerDevice message = this.messages.get(i);
                    device = message.getDevice();
                    payload = message.getPayload();
                }
                final int message2 = this.newMessageIdentifier();
                final PushedNotification notification = this.notificationManager.sendNotification(device, payload, false, message2);
                this.notifications.add(notification);
                try {
                    if (this.sleepBetweenNotifications > 0L) {
                        Thread.sleep(this.sleepBetweenNotifications);
                    }
                }
                catch (final InterruptedException ex) {}
                if (i != 0 && i % this.maxNotificationsPerConnection == 0) {
                    if (this.listener != null) {
                        this.listener.eventConnectionRestarted(this);
                    }
                    this.notificationManager.restartConnection(this.server);
                }
            }
            this.notificationManager.stopConnection();
        }
        catch (final KeystoreException | CommunicationException e) {
            this.exception = e;
            if (this.listener != null) {
                this.listener.eventCriticalException(this, e);
            }
        }
        this.busy = false;
        if (this.listener != null) {
            this.listener.eventThreadFinished(this);
        }
        if (this.thread.getThreadGroup() instanceof NotificationThreads) {
            ((NotificationThreads)this.thread.getThreadGroup()).threadFinished(this);
        }
    }
    
    private void runQueue() {
        if (this.listener != null) {
            this.listener.eventThreadStarted(this);
        }
        try {
            this.notificationManager.initializeConnection(this.server);
            int notificationsPushed = 0;
            while (this.mode == MODE.QUEUE) {
                while (!this.messages.isEmpty()) {
                    this.busy = true;
                    final PayloadPerDevice message = this.messages.get(0);
                    this.messages.remove(message);
                    ++notificationsPushed;
                    final int messageId = this.newMessageIdentifier();
                    final PushedNotification notification = this.notificationManager.sendNotification(message.getDevice(), message.getPayload(), false, messageId);
                    this.notifications.add(notification);
                    try {
                        if (this.sleepBetweenNotifications > 0L) {
                            Thread.sleep(this.sleepBetweenNotifications);
                        }
                    }
                    catch (final InterruptedException ex) {}
                    if (notificationsPushed != 0 && notificationsPushed % this.maxNotificationsPerConnection == 0) {
                        if (this.listener != null) {
                            this.listener.eventConnectionRestarted(this);
                        }
                        this.notificationManager.restartConnection(this.server);
                    }
                    this.busy = false;
                }
                try {
                    Thread.sleep(10000L);
                }
                catch (final Exception ex2) {}
            }
            this.notificationManager.stopConnection();
        }
        catch (final KeystoreException | CommunicationException e) {
            this.exception = e;
            if (this.listener != null) {
                this.listener.eventCriticalException(this, e);
            }
        }
        if (this.listener != null) {
            this.listener.eventThreadFinished(this);
        }
        if (this.thread.getThreadGroup() instanceof NotificationThreads) {
            ((NotificationThreads)this.thread.getThreadGroup()).threadFinished(this);
        }
    }
    
    @Override
    public PushQueue add(final Payload payload, final String token) throws InvalidDeviceTokenFormatException {
        return this.add(new PayloadPerDevice(payload, token));
    }
    
    @Override
    public PushQueue add(final Payload payload, final Device device) {
        return this.add(new PayloadPerDevice(payload, device));
    }
    
    @Override
    public PushQueue add(final PayloadPerDevice message) {
        if (this.mode != MODE.QUEUE) {
            return this;
        }
        try {
            this.messages.add(message);
            this.thread.interrupt();
        }
        catch (final Exception ex) {}
        return this;
    }
    
    public int getMaxNotificationsPerConnection() {
        return this.maxNotificationsPerConnection;
    }
    
    public void setMaxNotificationsPerConnection(final int maxNotificationsPerConnection) {
        this.maxNotificationsPerConnection = maxNotificationsPerConnection;
    }
    
    public long getSleepBetweenNotifications() {
        return this.sleepBetweenNotifications;
    }
    
    public void setSleepBetweenNotifications(final long milliseconds) {
        this.sleepBetweenNotifications = milliseconds;
    }
    
    public List<Device> getDevices() {
        return this.devices;
    }
    
    void setDevices(final List<Device> devices) {
        this.devices = devices;
    }
    
    private int size() {
        return (this.devices != null) ? this.devices.size() : this.messages.size();
    }
    
    public NotificationProgressListener getListener() {
        return this.listener;
    }
    
    public void setListener(final NotificationProgressListener listener) {
        this.listener = listener;
    }
    
    public int getThreadNumber() {
        return this.threadNumber;
    }
    
    void setThreadNumber(final int threadNumber) {
        this.threadNumber = threadNumber;
    }
    
    private int newMessageIdentifier() {
        return this.threadNumber << 24 | this.nextMessageIdentifier++;
    }
    
    public int getFirstMessageIdentifier() {
        return this.threadNumber << 24 | 0x1;
    }
    
    public int getLastMessageIdentifier() {
        return this.threadNumber << 24 | this.size();
    }
    
    @Override
    public PushedNotifications getPushedNotifications() {
        return this.notifications;
    }
    
    @Override
    public void clearPushedNotifications() {
        this.notifications.clear();
    }
    
    public PushedNotifications getFailedNotifications() {
        return this.getPushedNotifications().getFailedNotifications();
    }
    
    public PushedNotifications getSuccessfulNotifications() {
        return this.getPushedNotifications().getSuccessfulNotifications();
    }
    
    public List<PayloadPerDevice> getMessages() {
        return this.messages;
    }
    
    void setMessages(final List<PayloadPerDevice> messages) {
        this.messages = messages;
    }
    
    public boolean isBusy() {
        return this.busy;
    }
    
    public Exception getCriticalException() {
        return this.exception;
    }
    
    @Override
    public List<Exception> getCriticalExceptions() {
        final List<Exception> exceptions = new Vector<Exception>((this.exception != null) ? 1 : 0);
        if (this.exception != null) {
            exceptions.add(this.exception);
        }
        return exceptions;
    }
    
    public enum MODE
    {
        LIST, 
        QUEUE;
    }
}
