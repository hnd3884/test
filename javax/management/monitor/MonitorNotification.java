package javax.management.monitor;

import javax.management.ObjectName;
import javax.management.Notification;

public class MonitorNotification extends Notification
{
    public static final String OBSERVED_OBJECT_ERROR = "jmx.monitor.error.mbean";
    public static final String OBSERVED_ATTRIBUTE_ERROR = "jmx.monitor.error.attribute";
    public static final String OBSERVED_ATTRIBUTE_TYPE_ERROR = "jmx.monitor.error.type";
    public static final String THRESHOLD_ERROR = "jmx.monitor.error.threshold";
    public static final String RUNTIME_ERROR = "jmx.monitor.error.runtime";
    public static final String THRESHOLD_VALUE_EXCEEDED = "jmx.monitor.counter.threshold";
    public static final String THRESHOLD_HIGH_VALUE_EXCEEDED = "jmx.monitor.gauge.high";
    public static final String THRESHOLD_LOW_VALUE_EXCEEDED = "jmx.monitor.gauge.low";
    public static final String STRING_TO_COMPARE_VALUE_MATCHED = "jmx.monitor.string.matches";
    public static final String STRING_TO_COMPARE_VALUE_DIFFERED = "jmx.monitor.string.differs";
    private static final long serialVersionUID = -4608189663661929204L;
    private ObjectName observedObject;
    private String observedAttribute;
    private Object derivedGauge;
    private Object trigger;
    
    MonitorNotification(final String s, final Object o, final long n, final long n2, final String s2, final ObjectName observedObject, final String observedAttribute, final Object derivedGauge, final Object trigger) {
        super(s, o, n, n2, s2);
        this.observedObject = null;
        this.observedAttribute = null;
        this.derivedGauge = null;
        this.trigger = null;
        this.observedObject = observedObject;
        this.observedAttribute = observedAttribute;
        this.derivedGauge = derivedGauge;
        this.trigger = trigger;
    }
    
    public ObjectName getObservedObject() {
        return this.observedObject;
    }
    
    public String getObservedAttribute() {
        return this.observedAttribute;
    }
    
    public Object getDerivedGauge() {
        return this.derivedGauge;
    }
    
    public Object getTrigger() {
        return this.trigger;
    }
}
