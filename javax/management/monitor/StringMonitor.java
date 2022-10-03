package javax.management.monitor;

import javax.management.ObjectName;
import java.util.Iterator;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import javax.management.MBeanNotificationInfo;

public class StringMonitor extends Monitor implements StringMonitorMBean
{
    private String stringToCompare;
    private boolean notifyMatch;
    private boolean notifyDiffer;
    private static final String[] types;
    private static final MBeanNotificationInfo[] notifsInfo;
    private static final int MATCHING = 0;
    private static final int DIFFERING = 1;
    private static final int MATCHING_OR_DIFFERING = 2;
    
    public StringMonitor() {
        this.stringToCompare = "";
        this.notifyMatch = false;
        this.notifyDiffer = false;
    }
    
    @Override
    public synchronized void start() {
        if (this.isActive()) {
            JmxProperties.MONITOR_LOGGER.logp(Level.FINER, StringMonitor.class.getName(), "start", "the monitor is already active");
            return;
        }
        final Iterator<ObservedObject> iterator = this.observedObjects.iterator();
        while (iterator.hasNext()) {
            ((StringMonitorObservedObject)iterator.next()).setStatus(2);
        }
        this.doStart();
    }
    
    @Override
    public synchronized void stop() {
        this.doStop();
    }
    
    @Override
    public synchronized String getDerivedGauge(final ObjectName objectName) {
        return (String)super.getDerivedGauge(objectName);
    }
    
    @Override
    public synchronized long getDerivedGaugeTimeStamp(final ObjectName objectName) {
        return super.getDerivedGaugeTimeStamp(objectName);
    }
    
    @Deprecated
    @Override
    public synchronized String getDerivedGauge() {
        if (this.observedObjects.isEmpty()) {
            return null;
        }
        return (String)this.observedObjects.get(0).getDerivedGauge();
    }
    
    @Deprecated
    @Override
    public synchronized long getDerivedGaugeTimeStamp() {
        if (this.observedObjects.isEmpty()) {
            return 0L;
        }
        return this.observedObjects.get(0).getDerivedGaugeTimeStamp();
    }
    
    @Override
    public synchronized String getStringToCompare() {
        return this.stringToCompare;
    }
    
    @Override
    public synchronized void setStringToCompare(final String stringToCompare) throws IllegalArgumentException {
        if (stringToCompare == null) {
            throw new IllegalArgumentException("Null string to compare");
        }
        if (this.stringToCompare.equals(stringToCompare)) {
            return;
        }
        this.stringToCompare = stringToCompare;
        final Iterator<ObservedObject> iterator = this.observedObjects.iterator();
        while (iterator.hasNext()) {
            ((StringMonitorObservedObject)iterator.next()).setStatus(2);
        }
    }
    
    @Override
    public synchronized boolean getNotifyMatch() {
        return this.notifyMatch;
    }
    
    @Override
    public synchronized void setNotifyMatch(final boolean notifyMatch) {
        if (this.notifyMatch == notifyMatch) {
            return;
        }
        this.notifyMatch = notifyMatch;
    }
    
    @Override
    public synchronized boolean getNotifyDiffer() {
        return this.notifyDiffer;
    }
    
    @Override
    public synchronized void setNotifyDiffer(final boolean notifyDiffer) {
        if (this.notifyDiffer == notifyDiffer) {
            return;
        }
        this.notifyDiffer = notifyDiffer;
    }
    
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return StringMonitor.notifsInfo.clone();
    }
    
    @Override
    ObservedObject createObservedObject(final ObjectName objectName) {
        final StringMonitorObservedObject stringMonitorObservedObject = new StringMonitorObservedObject(objectName);
        stringMonitorObservedObject.setStatus(2);
        return stringMonitorObservedObject;
    }
    
    @Override
    synchronized boolean isComparableTypeValid(final ObjectName objectName, final String s, final Comparable<?> comparable) {
        return comparable instanceof String;
    }
    
    @Override
    synchronized void onErrorNotification(final MonitorNotification monitorNotification) {
        final StringMonitorObservedObject stringMonitorObservedObject = (StringMonitorObservedObject)this.getObservedObject(monitorNotification.getObservedObject());
        if (stringMonitorObservedObject == null) {
            return;
        }
        stringMonitorObservedObject.setStatus(2);
    }
    
    @Override
    synchronized MonitorNotification buildAlarmNotification(final ObjectName objectName, final String s, final Comparable<?> comparable) {
        String s2 = null;
        String s3 = null;
        Object o = null;
        final StringMonitorObservedObject stringMonitorObservedObject = (StringMonitorObservedObject)this.getObservedObject(objectName);
        if (stringMonitorObservedObject == null) {
            return null;
        }
        if (stringMonitorObservedObject.getStatus() == 2) {
            if (stringMonitorObservedObject.getDerivedGauge().equals(this.stringToCompare)) {
                if (this.notifyMatch) {
                    s2 = "jmx.monitor.string.matches";
                    s3 = "";
                    o = this.stringToCompare;
                }
                stringMonitorObservedObject.setStatus(1);
            }
            else {
                if (this.notifyDiffer) {
                    s2 = "jmx.monitor.string.differs";
                    s3 = "";
                    o = this.stringToCompare;
                }
                stringMonitorObservedObject.setStatus(0);
            }
        }
        else if (stringMonitorObservedObject.getStatus() == 0) {
            if (stringMonitorObservedObject.getDerivedGauge().equals(this.stringToCompare)) {
                if (this.notifyMatch) {
                    s2 = "jmx.monitor.string.matches";
                    s3 = "";
                    o = this.stringToCompare;
                }
                stringMonitorObservedObject.setStatus(1);
            }
        }
        else if (stringMonitorObservedObject.getStatus() == 1 && !stringMonitorObservedObject.getDerivedGauge().equals(this.stringToCompare)) {
            if (this.notifyDiffer) {
                s2 = "jmx.monitor.string.differs";
                s3 = "";
                o = this.stringToCompare;
            }
            stringMonitorObservedObject.setStatus(0);
        }
        return new MonitorNotification(s2, this, 0L, 0L, s3, null, null, null, o);
    }
    
    static {
        types = new String[] { "jmx.monitor.error.runtime", "jmx.monitor.error.mbean", "jmx.monitor.error.attribute", "jmx.monitor.error.type", "jmx.monitor.string.matches", "jmx.monitor.string.differs" };
        notifsInfo = new MBeanNotificationInfo[] { new MBeanNotificationInfo(StringMonitor.types, "javax.management.monitor.MonitorNotification", "Notifications sent by the StringMonitor MBean") };
    }
    
    static class StringMonitorObservedObject extends ObservedObject
    {
        private int status;
        
        public StringMonitorObservedObject(final ObjectName objectName) {
            super(objectName);
        }
        
        public final synchronized int getStatus() {
            return this.status;
        }
        
        public final synchronized void setStatus(final int status) {
            this.status = status;
        }
    }
}
