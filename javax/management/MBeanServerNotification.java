package javax.management;

public class MBeanServerNotification extends Notification
{
    private static final long serialVersionUID = 2876477500475969677L;
    public static final String REGISTRATION_NOTIFICATION = "JMX.mbean.registered";
    public static final String UNREGISTRATION_NOTIFICATION = "JMX.mbean.unregistered";
    private final ObjectName objectName;
    
    public MBeanServerNotification(final String s, final Object o, final long n, final ObjectName objectName) {
        super(s, o, n);
        this.objectName = objectName;
    }
    
    public ObjectName getMBeanName() {
        return this.objectName;
    }
    
    @Override
    public String toString() {
        return super.toString() + "[mbeanName=" + this.objectName + "]";
    }
}
