package javax.management.monitor;

import javax.management.ObjectName;
import java.util.Iterator;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import javax.management.MBeanNotificationInfo;

public class CounterMonitor extends Monitor implements CounterMonitorMBean
{
    private Number modulus;
    private Number offset;
    private boolean notify;
    private boolean differenceMode;
    private Number initThreshold;
    private static final String[] types;
    private static final MBeanNotificationInfo[] notifsInfo;
    
    public CounterMonitor() {
        this.modulus = CounterMonitor.INTEGER_ZERO;
        this.offset = CounterMonitor.INTEGER_ZERO;
        this.notify = false;
        this.differenceMode = false;
        this.initThreshold = CounterMonitor.INTEGER_ZERO;
    }
    
    @Override
    public synchronized void start() {
        if (this.isActive()) {
            JmxProperties.MONITOR_LOGGER.logp(Level.FINER, CounterMonitor.class.getName(), "start", "the monitor is already active");
            return;
        }
        for (final CounterMonitorObservedObject counterMonitorObservedObject : this.observedObjects) {
            counterMonitorObservedObject.setThreshold(this.initThreshold);
            counterMonitorObservedObject.setModulusExceeded(false);
            counterMonitorObservedObject.setEventAlreadyNotified(false);
            counterMonitorObservedObject.setPreviousScanCounter(null);
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
    
    @Override
    public synchronized Number getThreshold(final ObjectName objectName) {
        final CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)this.getObservedObject(objectName);
        if (counterMonitorObservedObject == null) {
            return null;
        }
        if (this.offset.longValue() > 0L && this.modulus.longValue() > 0L && counterMonitorObservedObject.getThreshold().longValue() > this.modulus.longValue()) {
            return this.initThreshold;
        }
        return counterMonitorObservedObject.getThreshold();
    }
    
    @Override
    public synchronized Number getInitThreshold() {
        return this.initThreshold;
    }
    
    @Override
    public synchronized void setInitThreshold(final Number n) throws IllegalArgumentException {
        if (n == null) {
            throw new IllegalArgumentException("Null threshold");
        }
        if (n.longValue() < 0L) {
            throw new IllegalArgumentException("Negative threshold");
        }
        if (this.initThreshold.equals(n)) {
            return;
        }
        this.initThreshold = n;
        int n2 = 0;
        for (final ObservedObject observedObject : this.observedObjects) {
            this.resetAlreadyNotified(observedObject, n2++, 16);
            final CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)observedObject;
            counterMonitorObservedObject.setThreshold(n);
            counterMonitorObservedObject.setModulusExceeded(false);
            counterMonitorObservedObject.setEventAlreadyNotified(false);
        }
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
    
    @Deprecated
    @Override
    public synchronized Number getThreshold() {
        return this.getThreshold(this.getObservedObject());
    }
    
    @Deprecated
    @Override
    public synchronized void setThreshold(final Number initThreshold) throws IllegalArgumentException {
        this.setInitThreshold(initThreshold);
    }
    
    @Override
    public synchronized Number getOffset() {
        return this.offset;
    }
    
    @Override
    public synchronized void setOffset(final Number offset) throws IllegalArgumentException {
        if (offset == null) {
            throw new IllegalArgumentException("Null offset");
        }
        if (offset.longValue() < 0L) {
            throw new IllegalArgumentException("Negative offset");
        }
        if (this.offset.equals(offset)) {
            return;
        }
        this.offset = offset;
        int n = 0;
        final Iterator<ObservedObject> iterator = this.observedObjects.iterator();
        while (iterator.hasNext()) {
            this.resetAlreadyNotified(iterator.next(), n++, 16);
        }
    }
    
    @Override
    public synchronized Number getModulus() {
        return this.modulus;
    }
    
    @Override
    public synchronized void setModulus(final Number modulus) throws IllegalArgumentException {
        if (modulus == null) {
            throw new IllegalArgumentException("Null modulus");
        }
        if (modulus.longValue() < 0L) {
            throw new IllegalArgumentException("Negative modulus");
        }
        if (this.modulus.equals(modulus)) {
            return;
        }
        this.modulus = modulus;
        int n = 0;
        for (final ObservedObject observedObject : this.observedObjects) {
            this.resetAlreadyNotified(observedObject, n++, 16);
            ((CounterMonitorObservedObject)observedObject).setModulusExceeded(false);
        }
    }
    
    @Override
    public synchronized boolean getNotify() {
        return this.notify;
    }
    
    @Override
    public synchronized void setNotify(final boolean notify) {
        if (this.notify == notify) {
            return;
        }
        this.notify = notify;
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
        for (final CounterMonitorObservedObject counterMonitorObservedObject : this.observedObjects) {
            counterMonitorObservedObject.setThreshold(this.initThreshold);
            counterMonitorObservedObject.setModulusExceeded(false);
            counterMonitorObservedObject.setEventAlreadyNotified(false);
            counterMonitorObservedObject.setPreviousScanCounter(null);
        }
    }
    
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return CounterMonitor.notifsInfo.clone();
    }
    
    private synchronized boolean updateDerivedGauge(final Object o, final CounterMonitorObservedObject counterMonitorObservedObject) {
        boolean b;
        if (this.differenceMode) {
            if (counterMonitorObservedObject.getPreviousScanCounter() != null) {
                this.setDerivedGaugeWithDifference((Number)o, null, counterMonitorObservedObject);
                if (((Number)counterMonitorObservedObject.getDerivedGauge()).longValue() < 0L) {
                    if (this.modulus.longValue() > 0L) {
                        this.setDerivedGaugeWithDifference((Number)o, this.modulus, counterMonitorObservedObject);
                    }
                    counterMonitorObservedObject.setThreshold(this.initThreshold);
                    counterMonitorObservedObject.setEventAlreadyNotified(false);
                }
                b = true;
            }
            else {
                b = false;
            }
            counterMonitorObservedObject.setPreviousScanCounter((Number)o);
        }
        else {
            counterMonitorObservedObject.setDerivedGauge(o);
            b = true;
        }
        return b;
    }
    
    private synchronized MonitorNotification updateNotifications(final CounterMonitorObservedObject counterMonitorObservedObject) {
        MonitorNotification monitorNotification = null;
        if (!counterMonitorObservedObject.getEventAlreadyNotified()) {
            if (((Number)counterMonitorObservedObject.getDerivedGauge()).longValue() >= counterMonitorObservedObject.getThreshold().longValue()) {
                if (this.notify) {
                    monitorNotification = new MonitorNotification("jmx.monitor.counter.threshold", this, 0L, 0L, "", null, null, null, counterMonitorObservedObject.getThreshold());
                }
                if (!this.differenceMode) {
                    counterMonitorObservedObject.setEventAlreadyNotified(true);
                }
            }
        }
        else if (JmxProperties.MONITOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MONITOR_LOGGER.logp(Level.FINER, CounterMonitor.class.getName(), "updateNotifications", "The notification:" + "\n\tNotification observed object = " + counterMonitorObservedObject.getObservedObject() + "\n\tNotification observed attribute = " + this.getObservedAttribute() + "\n\tNotification threshold level = " + counterMonitorObservedObject.getThreshold() + "\n\tNotification derived gauge = " + counterMonitorObservedObject.getDerivedGauge() + "\nhas already been sent");
        }
        return monitorNotification;
    }
    
    private synchronized void updateThreshold(final CounterMonitorObservedObject counterMonitorObservedObject) {
        if (((Number)counterMonitorObservedObject.getDerivedGauge()).longValue() >= counterMonitorObservedObject.getThreshold().longValue()) {
            if (this.offset.longValue() > 0L) {
                long longValue;
                for (longValue = counterMonitorObservedObject.getThreshold().longValue(); ((Number)counterMonitorObservedObject.getDerivedGauge()).longValue() >= longValue; longValue += this.offset.longValue()) {}
                switch (counterMonitorObservedObject.getType()) {
                    case INTEGER: {
                        counterMonitorObservedObject.setThreshold((int)longValue);
                        break;
                    }
                    case BYTE: {
                        counterMonitorObservedObject.setThreshold((byte)longValue);
                        break;
                    }
                    case SHORT: {
                        counterMonitorObservedObject.setThreshold((short)longValue);
                        break;
                    }
                    case LONG: {
                        counterMonitorObservedObject.setThreshold(longValue);
                        break;
                    }
                    default: {
                        JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, CounterMonitor.class.getName(), "updateThreshold", "the threshold type is invalid");
                        break;
                    }
                }
                if (!this.differenceMode && this.modulus.longValue() > 0L && counterMonitorObservedObject.getThreshold().longValue() > this.modulus.longValue()) {
                    counterMonitorObservedObject.setModulusExceeded(true);
                    counterMonitorObservedObject.setDerivedGaugeExceeded((Number)counterMonitorObservedObject.getDerivedGauge());
                }
                counterMonitorObservedObject.setEventAlreadyNotified(false);
            }
            else {
                counterMonitorObservedObject.setModulusExceeded(true);
                counterMonitorObservedObject.setDerivedGaugeExceeded((Number)counterMonitorObservedObject.getDerivedGauge());
            }
        }
    }
    
    private synchronized void setDerivedGaugeWithDifference(final Number n, final Number n2, final CounterMonitorObservedObject counterMonitorObservedObject) {
        long n3 = n.longValue() - counterMonitorObservedObject.getPreviousScanCounter().longValue();
        if (n2 != null) {
            n3 += this.modulus.longValue();
        }
        switch (counterMonitorObservedObject.getType()) {
            case INTEGER: {
                counterMonitorObservedObject.setDerivedGauge((int)n3);
                break;
            }
            case BYTE: {
                counterMonitorObservedObject.setDerivedGauge((byte)n3);
                break;
            }
            case SHORT: {
                counterMonitorObservedObject.setDerivedGauge((short)n3);
                break;
            }
            case LONG: {
                counterMonitorObservedObject.setDerivedGauge(n3);
                break;
            }
            default: {
                JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, CounterMonitor.class.getName(), "setDerivedGaugeWithDifference", "the threshold type is invalid");
                break;
            }
        }
    }
    
    @Override
    ObservedObject createObservedObject(final ObjectName objectName) {
        final CounterMonitorObservedObject counterMonitorObservedObject = new CounterMonitorObservedObject(objectName);
        counterMonitorObservedObject.setThreshold(this.initThreshold);
        counterMonitorObservedObject.setModulusExceeded(false);
        counterMonitorObservedObject.setEventAlreadyNotified(false);
        counterMonitorObservedObject.setPreviousScanCounter(null);
        return counterMonitorObservedObject;
    }
    
    @Override
    synchronized boolean isComparableTypeValid(final ObjectName objectName, final String s, final Comparable<?> comparable) {
        final CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)this.getObservedObject(objectName);
        if (counterMonitorObservedObject == null) {
            return false;
        }
        if (comparable instanceof Integer) {
            counterMonitorObservedObject.setType(NumericalType.INTEGER);
        }
        else if (comparable instanceof Byte) {
            counterMonitorObservedObject.setType(NumericalType.BYTE);
        }
        else if (comparable instanceof Short) {
            counterMonitorObservedObject.setType(NumericalType.SHORT);
        }
        else {
            if (!(comparable instanceof Long)) {
                return false;
            }
            counterMonitorObservedObject.setType(NumericalType.LONG);
        }
        return true;
    }
    
    @Override
    synchronized Comparable<?> getDerivedGaugeFromComparable(final ObjectName objectName, final String s, final Comparable<?> comparable) {
        final CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)this.getObservedObject(objectName);
        if (counterMonitorObservedObject == null) {
            return null;
        }
        if (counterMonitorObservedObject.getModulusExceeded() && ((Number)counterMonitorObservedObject.getDerivedGauge()).longValue() < counterMonitorObservedObject.getDerivedGaugeExceeded().longValue()) {
            counterMonitorObservedObject.setThreshold(this.initThreshold);
            counterMonitorObservedObject.setModulusExceeded(false);
            counterMonitorObservedObject.setEventAlreadyNotified(false);
        }
        counterMonitorObservedObject.setDerivedGaugeValid(this.updateDerivedGauge(comparable, counterMonitorObservedObject));
        return (Comparable)counterMonitorObservedObject.getDerivedGauge();
    }
    
    @Override
    synchronized void onErrorNotification(final MonitorNotification monitorNotification) {
        final CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)this.getObservedObject(monitorNotification.getObservedObject());
        if (counterMonitorObservedObject == null) {
            return;
        }
        counterMonitorObservedObject.setModulusExceeded(false);
        counterMonitorObservedObject.setEventAlreadyNotified(false);
        counterMonitorObservedObject.setPreviousScanCounter(null);
    }
    
    @Override
    synchronized MonitorNotification buildAlarmNotification(final ObjectName objectName, final String s, final Comparable<?> comparable) {
        final CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)this.getObservedObject(objectName);
        if (counterMonitorObservedObject == null) {
            return null;
        }
        MonitorNotification updateNotifications;
        if (counterMonitorObservedObject.getDerivedGaugeValid()) {
            updateNotifications = this.updateNotifications(counterMonitorObservedObject);
            this.updateThreshold(counterMonitorObservedObject);
        }
        else {
            updateNotifications = null;
        }
        return updateNotifications;
    }
    
    @Override
    synchronized boolean isThresholdTypeValid(final ObjectName objectName, final String s, final Comparable<?> comparable) {
        final CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)this.getObservedObject(objectName);
        if (counterMonitorObservedObject == null) {
            return false;
        }
        final Class<? extends Number> classForType = Monitor.classForType(counterMonitorObservedObject.getType());
        return classForType.isInstance(counterMonitorObservedObject.getThreshold()) && Monitor.isValidForType(this.offset, classForType) && Monitor.isValidForType(this.modulus, classForType);
    }
    
    static {
        types = new String[] { "jmx.monitor.error.runtime", "jmx.monitor.error.mbean", "jmx.monitor.error.attribute", "jmx.monitor.error.type", "jmx.monitor.error.threshold", "jmx.monitor.counter.threshold" };
        notifsInfo = new MBeanNotificationInfo[] { new MBeanNotificationInfo(CounterMonitor.types, "javax.management.monitor.MonitorNotification", "Notifications sent by the CounterMonitor MBean") };
    }
    
    static class CounterMonitorObservedObject extends ObservedObject
    {
        private Number threshold;
        private Number previousScanCounter;
        private boolean modulusExceeded;
        private Number derivedGaugeExceeded;
        private boolean derivedGaugeValid;
        private boolean eventAlreadyNotified;
        private NumericalType type;
        
        public CounterMonitorObservedObject(final ObjectName objectName) {
            super(objectName);
        }
        
        public final synchronized Number getThreshold() {
            return this.threshold;
        }
        
        public final synchronized void setThreshold(final Number threshold) {
            this.threshold = threshold;
        }
        
        public final synchronized Number getPreviousScanCounter() {
            return this.previousScanCounter;
        }
        
        public final synchronized void setPreviousScanCounter(final Number previousScanCounter) {
            this.previousScanCounter = previousScanCounter;
        }
        
        public final synchronized boolean getModulusExceeded() {
            return this.modulusExceeded;
        }
        
        public final synchronized void setModulusExceeded(final boolean modulusExceeded) {
            this.modulusExceeded = modulusExceeded;
        }
        
        public final synchronized Number getDerivedGaugeExceeded() {
            return this.derivedGaugeExceeded;
        }
        
        public final synchronized void setDerivedGaugeExceeded(final Number derivedGaugeExceeded) {
            this.derivedGaugeExceeded = derivedGaugeExceeded;
        }
        
        public final synchronized boolean getDerivedGaugeValid() {
            return this.derivedGaugeValid;
        }
        
        public final synchronized void setDerivedGaugeValid(final boolean derivedGaugeValid) {
            this.derivedGaugeValid = derivedGaugeValid;
        }
        
        public final synchronized boolean getEventAlreadyNotified() {
            return this.eventAlreadyNotified;
        }
        
        public final synchronized void setEventAlreadyNotified(final boolean eventAlreadyNotified) {
            this.eventAlreadyNotified = eventAlreadyNotified;
        }
        
        public final synchronized NumericalType getType() {
            return this.type;
        }
        
        public final synchronized void setType(final NumericalType type) {
            this.type = type;
        }
    }
}
