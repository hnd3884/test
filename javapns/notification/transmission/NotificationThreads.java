package javapns.notification.transmission;

import javapns.notification.PushedNotification;
import javapns.notification.PushedNotifications;
import java.util.Iterator;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PayloadPerDevice;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javapns.notification.PushNotificationManager;
import java.util.Collection;
import java.util.Vector;
import javapns.devices.Device;
import javapns.notification.Payload;
import javapns.notification.AppleNotificationServer;
import java.util.List;

public class NotificationThreads extends ThreadGroup implements PushQueue
{
    private static final long DEFAULT_DELAY_BETWEEN_THREADS = 500L;
    private final Object finishPoint;
    private List<NotificationThread> threads;
    private NotificationProgressListener listener;
    private boolean started;
    private int threadsRunning;
    private int nextThread;
    private long delayBetweenThreads;
    
    public NotificationThreads(final AppleNotificationServer server, final Payload payload, final List<Device> devices, final int numberOfThreads) {
        super("javapns notification threads (" + numberOfThreads + " threads)");
        this.finishPoint = new Object();
        this.threads = new Vector<NotificationThread>();
        this.started = false;
        this.threadsRunning = 0;
        this.nextThread = 0;
        this.delayBetweenThreads = 500L;
        this.threads.addAll(makeGroups(devices, numberOfThreads).stream().map(deviceGroup -> {
            new NotificationThread(this, new PushNotificationManager(), server, payload, deviceGroup);
            return;
        }).collect((Collector<? super Object, ?, Collection<? extends NotificationThread>>)Collectors.toList()));
    }
    
    public NotificationThreads(final AppleNotificationServer server, final List<PayloadPerDevice> messages, final int numberOfThreads) {
        super("javapns notification threads (" + numberOfThreads + " threads)");
        this.finishPoint = new Object();
        this.threads = new Vector<NotificationThread>();
        this.started = false;
        this.threadsRunning = 0;
        this.nextThread = 0;
        this.delayBetweenThreads = 500L;
        this.threads.addAll(makeGroups(messages, numberOfThreads).stream().map(deviceGroup -> {
            new NotificationThread(this, new PushNotificationManager(), server, deviceGroup);
            return;
        }).collect((Collector<? super Object, ?, Collection<? extends NotificationThread>>)Collectors.toList()));
    }
    
    public NotificationThreads(final Object keystore, final String password, final boolean production, final Payload payload, final List<Device> devices, final int numberOfThreads) throws Exception {
        this(new AppleNotificationServerBasicImpl(keystore, password, production), payload, devices, numberOfThreads);
    }
    
    private NotificationThreads(final AppleNotificationServer server, final Payload payload, final List<Device> devices, final List<NotificationThread> threads) {
        super("javapns notification threads (" + threads.size() + " threads)");
        this.finishPoint = new Object();
        this.threads = new Vector<NotificationThread>();
        this.started = false;
        this.threadsRunning = 0;
        this.nextThread = 0;
        this.delayBetweenThreads = 500L;
        this.threads = threads;
        final List<List<?>> groups = makeGroups(devices, threads.size());
        for (int i = 0; i < groups.size(); ++i) {
            threads.get(i).setDevices(groups.get(i));
        }
    }
    
    public NotificationThreads(final Object keystore, final String password, final boolean production, final Payload payload, final List<Device> devices, final List<NotificationThread> threads) throws Exception {
        this(new AppleNotificationServerBasicImpl(keystore, password, production), payload, devices, threads);
    }
    
    private NotificationThreads(final AppleNotificationServer server, final Payload payload, final List<NotificationThread> threads) {
        super("javapns notification threads (" + threads.size() + " threads)");
        this.finishPoint = new Object();
        this.threads = new Vector<NotificationThread>();
        this.started = false;
        this.threadsRunning = 0;
        this.nextThread = 0;
        this.delayBetweenThreads = 500L;
        this.threads = threads;
    }
    
    public NotificationThreads(final Object keystore, final String password, final boolean production, final Payload payload, final List<NotificationThread> threads) throws Exception {
        this(new AppleNotificationServerBasicImpl(keystore, password, production), payload, threads);
    }
    
    public NotificationThreads(final AppleNotificationServer server, final int numberOfThreads) {
        super("javapns notification thread pool (" + numberOfThreads + " threads)");
        this.finishPoint = new Object();
        this.threads = new Vector<NotificationThread>();
        this.started = false;
        this.threadsRunning = 0;
        this.nextThread = 0;
        this.delayBetweenThreads = 500L;
        for (int i = 0; i < numberOfThreads; ++i) {
            this.threads.add(new NotificationThread(this, new PushNotificationManager(), server));
        }
    }
    
    private static List<List<?>> makeGroups(final List<?> objects, final int threads) {
        final List<List<?>> groups = new Vector<List<?>>(threads);
        final int total = objects.size();
        int devicesPerThread = total / threads;
        if (total % threads > 0) {
            ++devicesPerThread;
        }
        for (int i = 0; i < threads; ++i) {
            final int firstObject = i * devicesPerThread;
            if (firstObject >= total) {
                break;
            }
            int lastObject = firstObject + devicesPerThread - 1;
            if (lastObject >= total) {
                lastObject = total - 1;
            }
            ++lastObject;
            final List threadObjects = objects.subList(firstObject, lastObject);
            groups.add(threadObjects);
        }
        return groups;
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
        this.start();
        final NotificationThread targetThread = this.getNextAvailableThread();
        targetThread.add(message);
        return targetThread;
    }
    
    private NotificationThread getNextAvailableThread() {
        for (int i = 0; i < this.threads.size(); ++i) {
            final NotificationThread thread = this.getNextThread();
            final boolean busy = thread.isBusy();
            if (!busy) {
                return thread;
            }
        }
        return this.getNextThread();
    }
    
    private synchronized NotificationThread getNextThread() {
        if (this.nextThread >= this.threads.size()) {
            this.nextThread = 0;
        }
        return this.threads.get(this.nextThread++);
    }
    
    @Override
    public synchronized NotificationThreads start() {
        if (this.started) {
            return this;
        }
        this.started = true;
        if (this.threadsRunning > 0) {
            throw new IllegalStateException("NotificationThreads already started (" + this.threadsRunning + " still running)");
        }
        this.assignThreadsNumbers();
        for (final NotificationThread thread : this.threads) {
            ++this.threadsRunning;
            thread.start();
            try {
                Thread.sleep(this.delayBetweenThreads);
            }
            catch (final InterruptedException ex) {}
        }
        if (this.listener != null) {
            this.listener.eventAllThreadsStarted(this);
        }
        return this;
    }
    
    public void setMaxNotificationsPerConnection(final int notifications) {
        for (final NotificationThread thread : this.threads) {
            thread.setMaxNotificationsPerConnection(notifications);
        }
    }
    
    public void setSleepBetweenNotifications(final long milliseconds) {
        for (final NotificationThread thread : this.threads) {
            thread.setSleepBetweenNotifications(milliseconds);
        }
    }
    
    public List<NotificationThread> getThreads() {
        return this.threads;
    }
    
    public NotificationProgressListener getListener() {
        return this.listener;
    }
    
    public void setListener(final NotificationProgressListener listener) {
        this.listener = listener;
        for (final NotificationThread thread : this.threads) {
            thread.setListener(listener);
        }
    }
    
    synchronized void threadFinished(final NotificationThread notificationThread) {
        --this.threadsRunning;
        if (this.threadsRunning == 0) {
            if (this.listener != null) {
                this.listener.eventAllThreadsFinished(this);
            }
            try {
                synchronized (this.finishPoint) {
                    this.finishPoint.notifyAll();
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    public void waitForAllThreads() throws InterruptedException {
        try {
            synchronized (this.finishPoint) {
                this.finishPoint.wait();
            }
        }
        catch (final IllegalMonitorStateException ex) {}
    }
    
    public void waitForAllThreads(final boolean throwCriticalExceptions) throws Exception {
        this.waitForAllThreads();
        if (throwCriticalExceptions) {
            final List<Exception> exceptions = this.getCriticalExceptions();
            if (exceptions.size() > 0) {
                throw exceptions.get(0);
            }
        }
    }
    
    private void assignThreadsNumbers() {
        int t = 1;
        for (final NotificationThread thread : this.threads) {
            thread.setThreadNumber(t++);
        }
    }
    
    @Override
    public PushedNotifications getPushedNotifications() {
        int capacity = 0;
        for (final NotificationThread thread : this.threads) {
            capacity += thread.getPushedNotifications().size();
        }
        final PushedNotifications all = new PushedNotifications(capacity);
        all.setMaxRetained(capacity);
        for (final NotificationThread thread2 : this.threads) {
            all.addAll(thread2.getPushedNotifications());
        }
        return all;
    }
    
    @Override
    public void clearPushedNotifications() {
        for (final NotificationThread thread : this.threads) {
            thread.clearPushedNotifications();
        }
    }
    
    public PushedNotifications getFailedNotifications() {
        return this.getPushedNotifications().getFailedNotifications();
    }
    
    public PushedNotifications getSuccessfulNotifications() {
        return this.getPushedNotifications().getSuccessfulNotifications();
    }
    
    @Override
    public List<Exception> getCriticalExceptions() {
        final List<Exception> exceptions = new Vector<Exception>();
        for (final NotificationThread thread : this.threads) {
            final Exception exception = thread.getCriticalException();
            if (exception != null) {
                exceptions.add(exception);
            }
        }
        return exceptions;
    }
    
    public long getDelayBetweenThreads() {
        return this.delayBetweenThreads;
    }
    
    public void setDelayBetweenThreads(final long delayBetweenThreads) {
        this.delayBetweenThreads = delayBetweenThreads;
    }
}
