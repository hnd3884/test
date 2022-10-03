package javax.management.monitor;

import javax.management.ObjectName;
import java.util.Iterator;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import javax.management.MBeanNotificationInfo;

public class GaugeMonitor extends Monitor implements GaugeMonitorMBean
{
    private Number highThreshold;
    private Number lowThreshold;
    private boolean notifyHigh;
    private boolean notifyLow;
    private boolean differenceMode;
    private static final String[] types;
    private static final MBeanNotificationInfo[] notifsInfo;
    private static final int RISING = 0;
    private static final int FALLING = 1;
    private static final int RISING_OR_FALLING = 2;
    
    public GaugeMonitor() {
        this.highThreshold = GaugeMonitor.INTEGER_ZERO;
        this.lowThreshold = GaugeMonitor.INTEGER_ZERO;
        this.notifyHigh = false;
        this.notifyLow = false;
        this.differenceMode = false;
    }
    
    @Override
    public synchronized void start() {
        if (this.isActive()) {
            JmxProperties.MONITOR_LOGGER.logp(Level.FINER, GaugeMonitor.class.getName(), "start", "the monitor is already active");
            return;
        }
        for (final GaugeMonitorObservedObject gaugeMonitorObservedObject : this.observedObjects) {
            gaugeMonitorObservedObject.setStatus(2);
            gaugeMonitorObservedObject.setPreviousScanGauge(null);
        }
        this.doStart();
    }
    
    @Override
    public synchronized void stop() {
        this.doStop();
    }
    
    @Override
    public synchronized Number getDerivedGauge(final ObjectName objectName) {
        return (Number)super.getDerivedGauge(objectName);
    }
    
    @Override
    public synchronized long getDerivedGaugeTimeStamp(final ObjectName objectName) {
        return super.getDerivedGaugeTimeStamp(objectName);
    }
    
    @Deprecated
    @Override
    public synchronized Number getDerivedGauge() {
        if (this.observedObjects.isEmpty()) {
            return null;
        }
        return (Number)this.observedObjects.get(0).getDerivedGauge();
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
    public synchronized Number getHighThreshold() {
        return this.highThreshold;
    }
    
    @Override
    public synchronized Number getLowThreshold() {
        return this.lowThreshold;
    }
    
    @Override
    public synchronized void setThresholds(final Number highThreshold, final Number lowThreshold) throws IllegalArgumentException {
        if (highThreshold == null || lowThreshold == null) {
            throw new IllegalArgumentException("Null threshold value");
        }
        if (highThreshold.getClass() != lowThreshold.getClass()) {
            throw new IllegalArgumentException("Different type threshold values");
        }
        if (this.isFirstStrictlyGreaterThanLast(lowThreshold, highThreshold, highThreshold.getClass().getName())) {
            throw new IllegalArgumentException("High threshold less than low threshold");
        }
        if (this.highThreshold.equals(highThreshold) && this.lowThreshold.equals(lowThreshold)) {
            return;
        }
        this.highThreshold = highThreshold;
        this.lowThreshold = lowThreshold;
        int n = 0;
        for (final ObservedObject observedObject : this.observedObjects) {
            this.resetAlreadyNotified(observedObject, n++, 16);
            ((GaugeMonitorObservedObject)observedObject).setStatus(2);
        }
    }
    
    @Override
    public synchronized boolean getNotifyHigh() {
        return this.notifyHigh;
    }
    
    @Override
    public synchronized void setNotifyHigh(final boolean notifyHigh) {
        if (this.notifyHigh == notifyHigh) {
            return;
        }
        this.notifyHigh = notifyHigh;
    }
    
    @Override
    public synchronized boolean getNotifyLow() {
        return this.notifyLow;
    }
    
    @Override
    public synchronized void setNotifyLow(final boolean notifyLow) {
        if (this.notifyLow == notifyLow) {
            return;
        }
        this.notifyLow = notifyLow;
    }
    
    @Override
    public synchronized boolean getDifferenceMode() {
        return this.differenceMode;
    }
    
    @Override
    public synchronized void setDifferenceMode(final boolean differenceMode) {
        if (this.differenceMode == differenceMode) {
            return;
        }
        this.differenceMode = differenceMode;
        for (final GaugeMonitorObservedObject gaugeMonitorObservedObject : this.observedObjects) {
            gaugeMonitorObservedObject.setStatus(2);
            gaugeMonitorObservedObject.setPreviousScanGauge(null);
        }
    }
    
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return GaugeMonitor.notifsInfo.clone();
    }
    
    private synchronized boolean updateDerivedGauge(final Object o, final GaugeMonitorObservedObject gaugeMonitorObservedObject) {
        boolean b;
        if (this.differenceMode) {
            if (gaugeMonitorObservedObject.getPreviousScanGauge() != null) {
                this.setDerivedGaugeWithDifference((Number)o, gaugeMonitorObservedObject);
                b = true;
            }
            else {
                b = false;
            }
            gaugeMonitorObservedObject.setPreviousScanGauge((Number)o);
        }
        else {
            gaugeMonitorObservedObject.setDerivedGauge(o);
            b = true;
        }
        return b;
    }
    
    private synchronized MonitorNotification updateNotifications(final GaugeMonitorObservedObject gaugeMonitorObservedObject) {
        MonitorNotification monitorNotification = null;
        if (gaugeMonitorObservedObject.getStatus() == 2) {
            if (this.isFirstGreaterThanLast((Number)gaugeMonitorObservedObject.getDerivedGauge(), this.highThreshold, gaugeMonitorObservedObject.getType())) {
                if (this.notifyHigh) {
                    monitorNotification = new MonitorNotification("jmx.monitor.gauge.high", this, 0L, 0L, "", null, null, null, this.highThreshold);
                }
                gaugeMonitorObservedObject.setStatus(1);
            }
            else if (this.isFirstGreaterThanLast(this.lowThreshold, (Number)gaugeMonitorObservedObject.getDerivedGauge(), gaugeMonitorObservedObject.getType())) {
                if (this.notifyLow) {
                    monitorNotification = new MonitorNotification("jmx.monitor.gauge.low", this, 0L, 0L, "", null, null, null, this.lowThreshold);
                }
                gaugeMonitorObservedObject.setStatus(0);
            }
        }
        else if (gaugeMonitorObservedObject.getStatus() == 0) {
            if (this.isFirstGreaterThanLast((Number)gaugeMonitorObservedObject.getDerivedGauge(), this.highThreshold, gaugeMonitorObservedObject.getType())) {
                if (this.notifyHigh) {
                    monitorNotification = new MonitorNotification("jmx.monitor.gauge.high", this, 0L, 0L, "", null, null, null, this.highThreshold);
                }
                gaugeMonitorObservedObject.setStatus(1);
            }
        }
        else if (gaugeMonitorObservedObject.getStatus() == 1 && this.isFirstGreaterThanLast(this.lowThreshold, (Number)gaugeMonitorObservedObject.getDerivedGauge(), gaugeMonitorObservedObject.getType())) {
            if (this.notifyLow) {
                monitorNotification = new MonitorNotification("jmx.monitor.gauge.low", this, 0L, 0L, "", null, null, null, this.lowThreshold);
            }
            gaugeMonitorObservedObject.setStatus(0);
        }
        return monitorNotification;
    }
    
    private synchronized void setDerivedGaugeWithDifference(final Number n, final GaugeMonitorObservedObject gaugeMonitorObservedObject) {
        final Number previousScanGauge = gaugeMonitorObservedObject.getPreviousScanGauge();
        Number derivedGauge = null;
        switch (gaugeMonitorObservedObject.getType()) {
            case INTEGER: {
                derivedGauge = (int)n - (int)previousScanGauge;
                break;
            }
            case BYTE: {
                derivedGauge = (byte)((byte)n - (byte)previousScanGauge);
                break;
            }
            case SHORT: {
                derivedGauge = (short)((short)n - (short)previousScanGauge);
                break;
            }
            case LONG: {
                derivedGauge = (long)n - (long)previousScanGauge;
                break;
            }
            case FLOAT: {
                derivedGauge = (float)n - (float)previousScanGauge;
                break;
            }
            case DOUBLE: {
                derivedGauge = (double)n - (double)previousScanGauge;
                break;
            }
            default: {
                JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, GaugeMonitor.class.getName(), "setDerivedGaugeWithDifference", "the threshold type is invalid");
                return;
            }
        }
        gaugeMonitorObservedObject.setDerivedGauge(derivedGauge);
    }
    
    private boolean isFirstGreaterThanLast(final Number n, final Number n2, final NumericalType numericalType) {
        switch (numericalType) {
            case INTEGER:
            case BYTE:
            case SHORT:
            case LONG: {
                return n.longValue() >= n2.longValue();
            }
            case FLOAT:
            case DOUBLE: {
                return n.doubleValue() >= n2.doubleValue();
            }
            default: {
                JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, GaugeMonitor.class.getName(), "isFirstGreaterThanLast", "the threshold type is invalid");
                return false;
            }
        }
    }
    
    private boolean isFirstStrictlyGreaterThanLast(final Number n, final Number n2, final String s) {
        if (s.equals("java.lang.Integer") || s.equals("java.lang.Byte") || s.equals("java.lang.Short") || s.equals("java.lang.Long")) {
            return n.longValue() > n2.longValue();
        }
        if (s.equals("java.lang.Float") || s.equals("java.lang.Double")) {
            return n.doubleValue() > n2.doubleValue();
        }
        JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, GaugeMonitor.class.getName(), "isFirstStrictlyGreaterThanLast", "the threshold type is invalid");
        return false;
    }
    
    @Override
    ObservedObject createObservedObject(final ObjectName objectName) {
        final GaugeMonitorObservedObject gaugeMonitorObservedObject = new GaugeMonitorObservedObject(objectName);
        gaugeMonitorObservedObject.setStatus(2);
        gaugeMonitorObservedObject.setPreviousScanGauge(null);
        return gaugeMonitorObservedObject;
    }
    
    @Override
    synchronized boolean isComparableTypeValid(final ObjectName objectName, final String s, final Comparable<?> comparable) {
        final GaugeMonitorObservedObject gaugeMonitorObservedObject = (GaugeMonitorObservedObject)this.getObservedObject(objectName);
        if (gaugeMonitorObservedObject == null) {
            return false;
        }
        if (comparable instanceof Integer) {
            gaugeMonitorObservedObject.setType(NumericalType.INTEGER);
        }
        else if (comparable instanceof Byte) {
            gaugeMonitorObservedObject.setType(NumericalType.BYTE);
        }
        else if (comparable instanceof Short) {
            gaugeMonitorObservedObject.setType(NumericalType.SHORT);
        }
        else if (comparable instanceof Long) {
            gaugeMonitorObservedObject.setType(NumericalType.LONG);
        }
        else if (comparable instanceof Float) {
            gaugeMonitorObservedObject.setType(NumericalType.FLOAT);
        }
        else {
            if (!(comparable instanceof Double)) {
                return false;
            }
            gaugeMonitorObservedObject.setType(NumericalType.DOUBLE);
        }
        return true;
    }
    
    @Override
    synchronized Comparable<?> getDerivedGaugeFromComparable(final ObjectName objectName, final String s, final Comparable<?> comparable) {
        final GaugeMonitorObservedObject gaugeMonitorObservedObject = (GaugeMonitorObservedObject)this.getObservedObject(objectName);
        if (gaugeMonitorObservedObject == null) {
            return null;
        }
        gaugeMonitorObservedObject.setDerivedGaugeValid(this.updateDerivedGauge(comparable, gaugeMonitorObservedObject));
        return (Comparable)gaugeMonitorObservedObject.getDerivedGauge();
    }
    
    @Override
    synchronized void onErrorNotification(final MonitorNotification monitorNotification) {
        final GaugeMonitorObservedObject gaugeMonitorObservedObject = (GaugeMonitorObservedObject)this.getObservedObject(monitorNotification.getObservedObject());
        if (gaugeMonitorObservedObject == null) {
            return;
        }
        gaugeMonitorObservedObject.setStatus(2);
        gaugeMonitorObservedObject.setPreviousScanGauge(null);
    }
    
    @Override
    synchronized MonitorNotification buildAlarmNotification(final ObjectName objectName, final String s, final Comparable<?> comparable) {
        final GaugeMonitorObservedObject gaugeMonitorObservedObject = (GaugeMonitorObservedObject)this.getObservedObject(objectName);
        if (gaugeMonitorObservedObject == null) {
            return null;
        }
        MonitorNotification updateNotifications;
        if (gaugeMonitorObservedObject.getDerivedGaugeValid()) {
            updateNotifications = this.updateNotifications(gaugeMonitorObservedObject);
        }
        else {
            updateNotifications = null;
        }
        return updateNotifications;
    }
    
    @Override
    synchronized boolean isThresholdTypeValid(final ObjectName objectName, final String s, final Comparable<?> comparable) {
        final GaugeMonitorObservedObject gaugeMonitorObservedObject = (GaugeMonitorObservedObject)this.getObservedObject(objectName);
        if (gaugeMonitorObservedObject == null) {
            return false;
        }
        final Class<? extends Number> classForType = Monitor.classForType(gaugeMonitorObservedObject.getType());
        return Monitor.isValidForType(this.highThreshold, classForType) && Monitor.isValidForType(this.lowThreshold, classForType);
    }
    
    static {
        types = new String[] { "jmx.monitor.error.runtime", "jmx.monitor.error.mbean", "jmx.monitor.error.attribute", "jmx.monitor.error.type", "jmx.monitor.error.threshold", "jmx.monitor.gauge.high", "jmx.monitor.gauge.low" };
        notifsInfo = new MBeanNotificationInfo[] { new MBeanNotificationInfo(GaugeMonitor.types, "javax.management.monitor.MonitorNotification", "Notifications sent by the GaugeMonitor MBean") };
    }
    
    static class GaugeMonitorObservedObject extends ObservedObject
    {
        private boolean derivedGaugeValid;
        private NumericalType type;
        private Number previousScanGauge;
        private int status;
        
        public GaugeMonitorObservedObject(final ObjectName objectName) {
            super(objectName);
        }
        
        public final synchronized boolean getDerivedGaugeValid() {
            return this.derivedGaugeValid;
        }
        
        public final synchronized void setDerivedGaugeValid(final boolean derivedGaugeValid) {
            this.derivedGaugeValid = derivedGaugeValid;
        }
        
        public final synchronized NumericalType getType() {
            return this.type;
        }
        
        public final synchronized void setType(final NumericalType type) {
            this.type = type;
        }
        
        public final synchronized Number getPreviousScanGauge() {
            return this.previousScanGauge;
        }
        
        public final synchronized void setPreviousScanGauge(final Number previousScanGauge) {
            this.previousScanGauge = previousScanGauge;
        }
        
        public final synchronized int getStatus() {
            return this.status;
        }
        
        public final synchronized void setStatus(final int status) {
            this.status = status;
        }
    }
}
