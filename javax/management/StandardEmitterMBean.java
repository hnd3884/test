package javax.management;

public class StandardEmitterMBean extends StandardMBean implements NotificationEmitter
{
    private static final MBeanNotificationInfo[] NO_NOTIFICATION_INFO;
    private final NotificationEmitter emitter;
    private final MBeanNotificationInfo[] notificationInfo;
    
    public <T> StandardEmitterMBean(final T t, final Class<T> clazz, final NotificationEmitter notificationEmitter) {
        this(t, clazz, false, notificationEmitter);
    }
    
    public <T> StandardEmitterMBean(final T t, final Class<T> clazz, final boolean b, final NotificationEmitter emitter) {
        super(t, clazz, b);
        if (emitter == null) {
            throw new IllegalArgumentException("Null emitter");
        }
        this.emitter = emitter;
        final MBeanNotificationInfo[] notificationInfo = emitter.getNotificationInfo();
        if (notificationInfo == null || notificationInfo.length == 0) {
            this.notificationInfo = StandardEmitterMBean.NO_NOTIFICATION_INFO;
        }
        else {
            this.notificationInfo = notificationInfo.clone();
        }
    }
    
    protected StandardEmitterMBean(final Class<?> clazz, final NotificationEmitter notificationEmitter) {
        this(clazz, false, notificationEmitter);
    }
    
    protected StandardEmitterMBean(final Class<?> clazz, final boolean b, final NotificationEmitter emitter) {
        super(clazz, b);
        if (emitter == null) {
            throw new IllegalArgumentException("Null emitter");
        }
        this.emitter = emitter;
        final MBeanNotificationInfo[] notificationInfo = emitter.getNotificationInfo();
        if (notificationInfo == null || notificationInfo.length == 0) {
            this.notificationInfo = StandardEmitterMBean.NO_NOTIFICATION_INFO;
        }
        else {
            this.notificationInfo = notificationInfo.clone();
        }
    }
    
    @Override
    public void removeNotificationListener(final NotificationListener notificationListener) throws ListenerNotFoundException {
        this.emitter.removeNotificationListener(notificationListener);
    }
    
    @Override
    public void removeNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws ListenerNotFoundException {
        this.emitter.removeNotificationListener(notificationListener, notificationFilter, o);
    }
    
    @Override
    public void addNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) {
        this.emitter.addNotificationListener(notificationListener, notificationFilter, o);
    }
    
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        if (this.notificationInfo == null) {
            return StandardEmitterMBean.NO_NOTIFICATION_INFO;
        }
        if (this.notificationInfo.length == 0) {
            return this.notificationInfo;
        }
        return this.notificationInfo.clone();
    }
    
    public void sendNotification(final Notification notification) {
        if (this.emitter instanceof NotificationBroadcasterSupport) {
            ((NotificationBroadcasterSupport)this.emitter).sendNotification(notification);
            return;
        }
        throw new ClassCastException("Cannot sendNotification when emitter is not an instance of NotificationBroadcasterSupport: " + this.emitter.getClass().getName());
    }
    
    @Override
    MBeanNotificationInfo[] getNotifications(final MBeanInfo mBeanInfo) {
        return this.getNotificationInfo();
    }
    
    static {
        NO_NOTIFICATION_INFO = new MBeanNotificationInfo[0];
    }
}
