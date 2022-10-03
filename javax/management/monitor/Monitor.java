package javax.management.monitor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.security.PrivilegedAction;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.util.WeakHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import javax.management.Notification;
import com.sun.jmx.mbeanserver.Introspector;
import java.io.IOException;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.InstanceNotFoundException;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.IntrospectionException;
import javax.management.MBeanServerConnection;
import java.security.AccessController;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import javax.management.ObjectName;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.management.MBeanServer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.security.AccessControlContext;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.MBeanRegistration;
import javax.management.NotificationBroadcasterSupport;

public abstract class Monitor extends NotificationBroadcasterSupport implements MonitorMBean, MBeanRegistration
{
    private String observedAttribute;
    private long granularityPeriod;
    private boolean isActive;
    private final AtomicLong sequenceNumber;
    private boolean isComplexTypeAttribute;
    private String firstAttribute;
    private final List<String> remainingAttributes;
    private static final AccessControlContext noPermissionsACC;
    private volatile AccessControlContext acc;
    private static final ScheduledExecutorService scheduler;
    private static final Map<ThreadPoolExecutor, Void> executors;
    private static final Object executorsLock;
    private static final int maximumPoolSize;
    private Future<?> monitorFuture;
    private final SchedulerTask schedulerTask;
    private ScheduledFuture<?> schedulerFuture;
    protected static final int capacityIncrement = 16;
    protected int elementCount;
    @Deprecated
    protected int alreadyNotified;
    protected int[] alreadyNotifieds;
    protected MBeanServer server;
    protected static final int RESET_FLAGS_ALREADY_NOTIFIED = 0;
    protected static final int OBSERVED_OBJECT_ERROR_NOTIFIED = 1;
    protected static final int OBSERVED_ATTRIBUTE_ERROR_NOTIFIED = 2;
    protected static final int OBSERVED_ATTRIBUTE_TYPE_ERROR_NOTIFIED = 4;
    protected static final int RUNTIME_ERROR_NOTIFIED = 8;
    @Deprecated
    protected String dbgTag;
    final List<ObservedObject> observedObjects;
    static final int THRESHOLD_ERROR_NOTIFIED = 16;
    static final Integer INTEGER_ZERO;
    
    public Monitor() {
        this.granularityPeriod = 10000L;
        this.isActive = false;
        this.sequenceNumber = new AtomicLong();
        this.isComplexTypeAttribute = false;
        this.remainingAttributes = new CopyOnWriteArrayList<String>();
        this.acc = Monitor.noPermissionsACC;
        this.schedulerTask = new SchedulerTask();
        this.elementCount = 0;
        this.alreadyNotified = 0;
        this.alreadyNotifieds = new int[16];
        this.dbgTag = Monitor.class.getName();
        this.observedObjects = new CopyOnWriteArrayList<ObservedObject>();
    }
    
    @Override
    public ObjectName preRegister(final MBeanServer server, final ObjectName objectName) throws Exception {
        JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "preRegister(MBeanServer, ObjectName)", "initialize the reference on the MBean server");
        this.server = server;
        return objectName;
    }
    
    @Override
    public void postRegister(final Boolean b) {
    }
    
    @Override
    public void preDeregister() throws Exception {
        JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "preDeregister()", "stop the monitor");
        this.stop();
    }
    
    @Override
    public void postDeregister() {
    }
    
    @Override
    public abstract void start();
    
    @Override
    public abstract void stop();
    
    @Deprecated
    @Override
    public synchronized ObjectName getObservedObject() {
        if (this.observedObjects.isEmpty()) {
            return null;
        }
        return this.observedObjects.get(0).getObservedObject();
    }
    
    @Deprecated
    @Override
    public synchronized void setObservedObject(final ObjectName objectName) throws IllegalArgumentException {
        if (objectName == null) {
            throw new IllegalArgumentException("Null observed object");
        }
        if (this.observedObjects.size() == 1 && this.containsObservedObject(objectName)) {
            return;
        }
        this.observedObjects.clear();
        this.addObservedObject(objectName);
    }
    
    @Override
    public synchronized void addObservedObject(final ObjectName objectName) throws IllegalArgumentException {
        if (objectName == null) {
            throw new IllegalArgumentException("Null observed object");
        }
        if (this.containsObservedObject(objectName)) {
            return;
        }
        final ObservedObject observedObject = this.createObservedObject(objectName);
        observedObject.setAlreadyNotified(0);
        observedObject.setDerivedGauge(Monitor.INTEGER_ZERO);
        observedObject.setDerivedGaugeTimeStamp(System.currentTimeMillis());
        this.observedObjects.add(observedObject);
        this.createAlreadyNotified();
    }
    
    @Override
    public synchronized void removeObservedObject(final ObjectName objectName) {
        if (objectName == null) {
            return;
        }
        final ObservedObject observedObject = this.getObservedObject(objectName);
        if (observedObject != null) {
            this.observedObjects.remove(observedObject);
            this.createAlreadyNotified();
        }
    }
    
    @Override
    public synchronized boolean containsObservedObject(final ObjectName objectName) {
        return this.getObservedObject(objectName) != null;
    }
    
    @Override
    public synchronized ObjectName[] getObservedObjects() {
        final ObjectName[] array = new ObjectName[this.observedObjects.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.observedObjects.get(i).getObservedObject();
        }
        return array;
    }
    
    @Override
    public synchronized String getObservedAttribute() {
        return this.observedAttribute;
    }
    
    @Override
    public void setObservedAttribute(final String observedAttribute) throws IllegalArgumentException {
        if (observedAttribute == null) {
            throw new IllegalArgumentException("Null observed attribute");
        }
        synchronized (this) {
            if (this.observedAttribute != null && this.observedAttribute.equals(observedAttribute)) {
                return;
            }
            this.observedAttribute = observedAttribute;
            this.cleanupIsComplexTypeAttribute();
            int n = 0;
            final Iterator<ObservedObject> iterator = this.observedObjects.iterator();
            while (iterator.hasNext()) {
                this.resetAlreadyNotified(iterator.next(), n++, 6);
            }
        }
    }
    
    @Override
    public synchronized long getGranularityPeriod() {
        return this.granularityPeriod;
    }
    
    @Override
    public synchronized void setGranularityPeriod(final long granularityPeriod) throws IllegalArgumentException {
        if (granularityPeriod <= 0L) {
            throw new IllegalArgumentException("Nonpositive granularity period");
        }
        if (this.granularityPeriod == granularityPeriod) {
            return;
        }
        this.granularityPeriod = granularityPeriod;
        if (this.isActive()) {
            this.cleanupFutures();
            this.schedulerFuture = Monitor.scheduler.schedule(this.schedulerTask, granularityPeriod, TimeUnit.MILLISECONDS);
        }
    }
    
    @Override
    public synchronized boolean isActive() {
        return this.isActive;
    }
    
    void doStart() {
        JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStart()", "start the monitor");
        synchronized (this) {
            if (this.isActive()) {
                JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStart()", "the monitor is already active");
                return;
            }
            this.isActive = true;
            this.cleanupIsComplexTypeAttribute();
            this.acc = AccessController.getContext();
            this.cleanupFutures();
            this.schedulerTask.setMonitorTask(new MonitorTask());
            this.schedulerFuture = Monitor.scheduler.schedule(this.schedulerTask, this.getGranularityPeriod(), TimeUnit.MILLISECONDS);
        }
    }
    
    void doStop() {
        JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStop()", "stop the monitor");
        synchronized (this) {
            if (!this.isActive()) {
                JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStop()", "the monitor is not active");
                return;
            }
            this.isActive = false;
            this.cleanupFutures();
            this.acc = Monitor.noPermissionsACC;
            this.cleanupIsComplexTypeAttribute();
        }
    }
    
    synchronized Object getDerivedGauge(final ObjectName objectName) {
        final ObservedObject observedObject = this.getObservedObject(objectName);
        return (observedObject == null) ? null : observedObject.getDerivedGauge();
    }
    
    synchronized long getDerivedGaugeTimeStamp(final ObjectName objectName) {
        final ObservedObject observedObject = this.getObservedObject(objectName);
        return (observedObject == null) ? 0L : observedObject.getDerivedGaugeTimeStamp();
    }
    
    Object getAttribute(final MBeanServerConnection mBeanServerConnection, final ObjectName objectName, final String s) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
        final boolean b;
        synchronized (this) {
            if (!this.isActive()) {
                throw new IllegalArgumentException("The monitor has been stopped");
            }
            if (!s.equals(this.getObservedAttribute())) {
                throw new IllegalArgumentException("The observed attribute has been changed");
            }
            b = (this.firstAttribute == null && s.indexOf(46) != -1);
        }
        MBeanInfo mBeanInfo = null;
        Label_0113: {
            if (b) {
                try {
                    mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);
                    break Label_0113;
                }
                catch (final IntrospectionException ex) {
                    throw new IllegalArgumentException(ex);
                }
            }
            mBeanInfo = null;
        }
        final String firstAttribute;
        synchronized (this) {
            if (!this.isActive()) {
                throw new IllegalArgumentException("The monitor has been stopped");
            }
            if (!s.equals(this.getObservedAttribute())) {
                throw new IllegalArgumentException("The observed attribute has been changed");
            }
            if (this.firstAttribute == null) {
                if (s.indexOf(46) != -1) {
                    final MBeanAttributeInfo[] attributes = mBeanInfo.getAttributes();
                    for (int length = attributes.length, i = 0; i < length; ++i) {
                        if (s.equals(attributes[i].getName())) {
                            this.firstAttribute = s;
                            break;
                        }
                    }
                    if (this.firstAttribute == null) {
                        final String[] split = s.split("\\.", -1);
                        this.firstAttribute = split[0];
                        for (int j = 1; j < split.length; ++j) {
                            this.remainingAttributes.add(split[j]);
                        }
                        this.isComplexTypeAttribute = true;
                    }
                }
                else {
                    this.firstAttribute = s;
                }
            }
            firstAttribute = this.firstAttribute;
        }
        return mBeanServerConnection.getAttribute(objectName, firstAttribute);
    }
    
    Comparable<?> getComparableFromAttribute(final ObjectName objectName, final String s, final Object o) throws AttributeNotFoundException {
        if (this.isComplexTypeAttribute) {
            Object elementFromComplex = o;
            final Iterator<String> iterator = this.remainingAttributes.iterator();
            while (iterator.hasNext()) {
                elementFromComplex = Introspector.elementFromComplex(elementFromComplex, iterator.next());
            }
            return (Comparable<?>)elementFromComplex;
        }
        return (Comparable)o;
    }
    
    boolean isComparableTypeValid(final ObjectName objectName, final String s, final Comparable<?> comparable) {
        return true;
    }
    
    String buildErrorNotification(final ObjectName objectName, final String s, final Comparable<?> comparable) {
        return null;
    }
    
    void onErrorNotification(final MonitorNotification monitorNotification) {
    }
    
    Comparable<?> getDerivedGaugeFromComparable(final ObjectName objectName, final String s, final Comparable<?> comparable) {
        return comparable;
    }
    
    MonitorNotification buildAlarmNotification(final ObjectName objectName, final String s, final Comparable<?> comparable) {
        return null;
    }
    
    boolean isThresholdTypeValid(final ObjectName objectName, final String s, final Comparable<?> comparable) {
        return true;
    }
    
    static Class<? extends Number> classForType(final NumericalType numericalType) {
        switch (numericalType) {
            case BYTE: {
                return Byte.class;
            }
            case SHORT: {
                return Short.class;
            }
            case INTEGER: {
                return Integer.class;
            }
            case LONG: {
                return Long.class;
            }
            case FLOAT: {
                return Float.class;
            }
            case DOUBLE: {
                return Double.class;
            }
            default: {
                throw new IllegalArgumentException("Unsupported numerical type");
            }
        }
    }
    
    static boolean isValidForType(final Object o, final Class<? extends Number> clazz) {
        return o == Monitor.INTEGER_ZERO || clazz.isInstance(o);
    }
    
    synchronized ObservedObject getObservedObject(final ObjectName objectName) {
        for (final ObservedObject observedObject : this.observedObjects) {
            if (observedObject.getObservedObject().equals(objectName)) {
                return observedObject;
            }
        }
        return null;
    }
    
    ObservedObject createObservedObject(final ObjectName objectName) {
        return new ObservedObject(objectName);
    }
    
    synchronized void createAlreadyNotified() {
        this.elementCount = this.observedObjects.size();
        this.alreadyNotifieds = new int[this.elementCount];
        for (int i = 0; i < this.elementCount; ++i) {
            this.alreadyNotifieds[i] = this.observedObjects.get(i).getAlreadyNotified();
        }
        this.updateDeprecatedAlreadyNotified();
    }
    
    synchronized void updateDeprecatedAlreadyNotified() {
        if (this.elementCount > 0) {
            this.alreadyNotified = this.alreadyNotifieds[0];
        }
        else {
            this.alreadyNotified = 0;
        }
    }
    
    synchronized void updateAlreadyNotified(final ObservedObject observedObject, final int n) {
        this.alreadyNotifieds[n] = observedObject.getAlreadyNotified();
        if (n == 0) {
            this.updateDeprecatedAlreadyNotified();
        }
    }
    
    synchronized boolean isAlreadyNotified(final ObservedObject observedObject, final int n) {
        return (observedObject.getAlreadyNotified() & n) != 0x0;
    }
    
    synchronized void setAlreadyNotified(final ObservedObject observedObject, final int n, final int n2, final int[] array) {
        final int computeAlreadyNotifiedIndex = this.computeAlreadyNotifiedIndex(observedObject, n, array);
        if (computeAlreadyNotifiedIndex == -1) {
            return;
        }
        observedObject.setAlreadyNotified(observedObject.getAlreadyNotified() | n2);
        this.updateAlreadyNotified(observedObject, computeAlreadyNotifiedIndex);
    }
    
    synchronized void resetAlreadyNotified(final ObservedObject observedObject, final int n, final int n2) {
        observedObject.setAlreadyNotified(observedObject.getAlreadyNotified() & ~n2);
        this.updateAlreadyNotified(observedObject, n);
    }
    
    synchronized void resetAllAlreadyNotified(final ObservedObject observedObject, final int n, final int[] array) {
        if (this.computeAlreadyNotifiedIndex(observedObject, n, array) == -1) {
            return;
        }
        observedObject.setAlreadyNotified(0);
        this.updateAlreadyNotified(observedObject, n);
    }
    
    synchronized int computeAlreadyNotifiedIndex(final ObservedObject observedObject, final int n, final int[] array) {
        if (array == this.alreadyNotifieds) {
            return n;
        }
        return this.observedObjects.indexOf(observedObject);
    }
    
    private void sendNotification(final String s, final long n, final String s2, final Object o, final Object o2, final ObjectName objectName, final boolean b) {
        if (!this.isActive()) {
            return;
        }
        if (JmxProperties.MONITOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "sendNotification", "send notification: \n\tNotification observed object = " + objectName + "\n\tNotification observed attribute = " + this.observedAttribute + "\n\tNotification derived gauge = " + o);
        }
        final MonitorNotification monitorNotification = new MonitorNotification(s, this, this.sequenceNumber.getAndIncrement(), n, s2, objectName, this.observedAttribute, o, o2);
        if (b) {
            this.onErrorNotification(monitorNotification);
        }
        this.sendNotification(monitorNotification);
    }
    
    private void monitor(final ObservedObject observedObject, final int n, final int[] array) {
        String s = null;
        String buildErrorNotification = null;
        Comparable<?> derivedGaugeFromComparable = null;
        final Object o = null;
        Comparable<?> comparableFromAttribute = null;
        MonitorNotification buildAlarmNotification = null;
        if (!this.isActive()) {
            return;
        }
        final ObjectName observedObject2;
        final String observedAttribute;
        synchronized (this) {
            observedObject2 = observedObject.getObservedObject();
            observedAttribute = this.getObservedAttribute();
            if (observedObject2 == null || observedAttribute == null) {
                return;
            }
        }
        Object attribute = null;
        try {
            attribute = this.getAttribute(this.server, observedObject2, observedAttribute);
            if (attribute == null) {
                if (this.isAlreadyNotified(observedObject, 4)) {
                    return;
                }
                s = "jmx.monitor.error.type";
                this.setAlreadyNotified(observedObject, n, 4, array);
                buildErrorNotification = "The observed attribute value is null.";
                JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", buildErrorNotification);
            }
        }
        catch (final NullPointerException ex) {
            if (this.isAlreadyNotified(observedObject, 8)) {
                return;
            }
            s = "jmx.monitor.error.runtime";
            this.setAlreadyNotified(observedObject, n, 8, array);
            buildErrorNotification = "The monitor must be registered in the MBean server or an MBeanServerConnection must be explicitly supplied.";
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", buildErrorNotification);
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", ex.toString());
        }
        catch (final InstanceNotFoundException ex2) {
            if (this.isAlreadyNotified(observedObject, 1)) {
                return;
            }
            s = "jmx.monitor.error.mbean";
            this.setAlreadyNotified(observedObject, n, 1, array);
            buildErrorNotification = "The observed object must be accessible in the MBeanServerConnection.";
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", buildErrorNotification);
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", ex2.toString());
        }
        catch (final AttributeNotFoundException ex3) {
            if (this.isAlreadyNotified(observedObject, 2)) {
                return;
            }
            s = "jmx.monitor.error.attribute";
            this.setAlreadyNotified(observedObject, n, 2, array);
            buildErrorNotification = "The observed attribute must be accessible in the observed object.";
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", buildErrorNotification);
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", ex3.toString());
        }
        catch (final MBeanException ex4) {
            if (this.isAlreadyNotified(observedObject, 8)) {
                return;
            }
            s = "jmx.monitor.error.runtime";
            this.setAlreadyNotified(observedObject, n, 8, array);
            buildErrorNotification = ((ex4.getMessage() == null) ? "" : ex4.getMessage());
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", buildErrorNotification);
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", ex4.toString());
        }
        catch (final ReflectionException ex5) {
            if (this.isAlreadyNotified(observedObject, 8)) {
                return;
            }
            s = "jmx.monitor.error.runtime";
            this.setAlreadyNotified(observedObject, n, 8, array);
            buildErrorNotification = ((ex5.getMessage() == null) ? "" : ex5.getMessage());
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", buildErrorNotification);
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", ex5.toString());
        }
        catch (final IOException ex6) {
            if (this.isAlreadyNotified(observedObject, 8)) {
                return;
            }
            s = "jmx.monitor.error.runtime";
            this.setAlreadyNotified(observedObject, n, 8, array);
            buildErrorNotification = ((ex6.getMessage() == null) ? "" : ex6.getMessage());
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", buildErrorNotification);
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", ex6.toString());
        }
        catch (final RuntimeException ex7) {
            if (this.isAlreadyNotified(observedObject, 8)) {
                return;
            }
            s = "jmx.monitor.error.runtime";
            this.setAlreadyNotified(observedObject, n, 8, array);
            buildErrorNotification = ((ex7.getMessage() == null) ? "" : ex7.getMessage());
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", buildErrorNotification);
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", ex7.toString());
        }
        synchronized (this) {
            if (!this.isActive()) {
                return;
            }
            if (!observedAttribute.equals(this.getObservedAttribute())) {
                return;
            }
            if (buildErrorNotification == null) {
                try {
                    comparableFromAttribute = this.getComparableFromAttribute(observedObject2, observedAttribute, attribute);
                }
                catch (final ClassCastException ex8) {
                    if (this.isAlreadyNotified(observedObject, 4)) {
                        return;
                    }
                    s = "jmx.monitor.error.type";
                    this.setAlreadyNotified(observedObject, n, 4, array);
                    buildErrorNotification = "The observed attribute value does not implement the Comparable interface.";
                    JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", buildErrorNotification);
                    JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", ex8.toString());
                }
                catch (final AttributeNotFoundException ex9) {
                    if (this.isAlreadyNotified(observedObject, 2)) {
                        return;
                    }
                    s = "jmx.monitor.error.attribute";
                    this.setAlreadyNotified(observedObject, n, 2, array);
                    buildErrorNotification = "The observed attribute must be accessible in the observed object.";
                    JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", buildErrorNotification);
                    JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", ex9.toString());
                }
                catch (final RuntimeException ex10) {
                    if (this.isAlreadyNotified(observedObject, 8)) {
                        return;
                    }
                    s = "jmx.monitor.error.runtime";
                    this.setAlreadyNotified(observedObject, n, 8, array);
                    buildErrorNotification = ((ex10.getMessage() == null) ? "" : ex10.getMessage());
                    JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", buildErrorNotification);
                    JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", ex10.toString());
                }
            }
            if (buildErrorNotification == null && !this.isComparableTypeValid(observedObject2, observedAttribute, comparableFromAttribute)) {
                if (this.isAlreadyNotified(observedObject, 4)) {
                    return;
                }
                s = "jmx.monitor.error.type";
                this.setAlreadyNotified(observedObject, n, 4, array);
                buildErrorNotification = "The observed attribute type is not valid.";
                JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", buildErrorNotification);
            }
            if (buildErrorNotification == null && !this.isThresholdTypeValid(observedObject2, observedAttribute, comparableFromAttribute)) {
                if (this.isAlreadyNotified(observedObject, 16)) {
                    return;
                }
                s = "jmx.monitor.error.threshold";
                this.setAlreadyNotified(observedObject, n, 16, array);
                buildErrorNotification = "The threshold type is not valid.";
                JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", buildErrorNotification);
            }
            if (buildErrorNotification == null) {
                buildErrorNotification = this.buildErrorNotification(observedObject2, observedAttribute, comparableFromAttribute);
                if (buildErrorNotification != null) {
                    if (this.isAlreadyNotified(observedObject, 8)) {
                        return;
                    }
                    s = "jmx.monitor.error.runtime";
                    this.setAlreadyNotified(observedObject, n, 8, array);
                    JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", buildErrorNotification);
                }
            }
            if (buildErrorNotification == null) {
                this.resetAllAlreadyNotified(observedObject, n, array);
                derivedGaugeFromComparable = this.getDerivedGaugeFromComparable(observedObject2, observedAttribute, comparableFromAttribute);
                observedObject.setDerivedGauge(derivedGaugeFromComparable);
                observedObject.setDerivedGaugeTimeStamp(System.currentTimeMillis());
                buildAlarmNotification = this.buildAlarmNotification(observedObject2, observedAttribute, derivedGaugeFromComparable);
            }
        }
        if (buildErrorNotification != null) {
            this.sendNotification(s, System.currentTimeMillis(), buildErrorNotification, derivedGaugeFromComparable, o, observedObject2, true);
        }
        if (buildAlarmNotification != null && buildAlarmNotification.getType() != null) {
            this.sendNotification(buildAlarmNotification.getType(), System.currentTimeMillis(), buildAlarmNotification.getMessage(), derivedGaugeFromComparable, buildAlarmNotification.getTrigger(), observedObject2, false);
        }
    }
    
    private synchronized void cleanupFutures() {
        if (this.schedulerFuture != null) {
            this.schedulerFuture.cancel(false);
            this.schedulerFuture = null;
        }
        if (this.monitorFuture != null) {
            this.monitorFuture.cancel(false);
            this.monitorFuture = null;
        }
    }
    
    private synchronized void cleanupIsComplexTypeAttribute() {
        this.firstAttribute = null;
        this.remainingAttributes.clear();
        this.isComplexTypeAttribute = false;
    }
    
    static {
        noPermissionsACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, null) });
        scheduler = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("Scheduler"));
        executors = new WeakHashMap<ThreadPoolExecutor, Void>();
        executorsLock = new Object();
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.x.monitor.maximum.pool.size"));
        if (s == null || s.trim().length() == 0) {
            maximumPoolSize = 10;
        }
        else {
            int int1;
            try {
                int1 = Integer.parseInt(s);
            }
            catch (final NumberFormatException ex) {
                if (JmxProperties.MONITOR_LOGGER.isLoggable(Level.FINER)) {
                    JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "<static initializer>", "Wrong value for jmx.x.monitor.maximum.pool.size system property", ex);
                    JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "<static initializer>", "jmx.x.monitor.maximum.pool.size defaults to 10");
                }
                int1 = 10;
            }
            if (int1 < 1) {
                maximumPoolSize = 1;
            }
            else {
                maximumPoolSize = int1;
            }
        }
        INTEGER_ZERO = 0;
    }
    
    static class ObservedObject
    {
        private final ObjectName observedObject;
        private int alreadyNotified;
        private Object derivedGauge;
        private long derivedGaugeTimeStamp;
        
        public ObservedObject(final ObjectName observedObject) {
            this.observedObject = observedObject;
        }
        
        public final ObjectName getObservedObject() {
            return this.observedObject;
        }
        
        public final synchronized int getAlreadyNotified() {
            return this.alreadyNotified;
        }
        
        public final synchronized void setAlreadyNotified(final int alreadyNotified) {
            this.alreadyNotified = alreadyNotified;
        }
        
        public final synchronized Object getDerivedGauge() {
            return this.derivedGauge;
        }
        
        public final synchronized void setDerivedGauge(final Object derivedGauge) {
            this.derivedGauge = derivedGauge;
        }
        
        public final synchronized long getDerivedGaugeTimeStamp() {
            return this.derivedGaugeTimeStamp;
        }
        
        public final synchronized void setDerivedGaugeTimeStamp(final long derivedGaugeTimeStamp) {
            this.derivedGaugeTimeStamp = derivedGaugeTimeStamp;
        }
    }
    
    enum NumericalType
    {
        BYTE, 
        SHORT, 
        INTEGER, 
        LONG, 
        FLOAT, 
        DOUBLE;
    }
    
    private class SchedulerTask implements Runnable
    {
        private MonitorTask task;
        
        public SchedulerTask() {
        }
        
        public void setMonitorTask(final MonitorTask task) {
            this.task = task;
        }
        
        @Override
        public void run() {
            synchronized (Monitor.this) {
                Monitor.this.monitorFuture = this.task.submit();
            }
        }
    }
    
    private class MonitorTask implements Runnable
    {
        private ThreadPoolExecutor executor;
        
        public MonitorTask() {
            final SecurityManager securityManager = System.getSecurityManager();
            final ThreadGroup threadGroup = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
            synchronized (Monitor.executorsLock) {
                for (final ThreadPoolExecutor executor : Monitor.executors.keySet()) {
                    if (((DaemonThreadFactory)executor.getThreadFactory()).getThreadGroup() == threadGroup) {
                        this.executor = executor;
                        break;
                    }
                }
                if (this.executor == null) {
                    (this.executor = new ThreadPoolExecutor(Monitor.maximumPoolSize, Monitor.maximumPoolSize, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new DaemonThreadFactory("ThreadGroup<" + threadGroup.getName() + "> Executor", threadGroup))).allowCoreThreadTimeOut(true);
                    Monitor.executors.put(this.executor, null);
                }
            }
        }
        
        public Future<?> submit() {
            return this.executor.submit(this);
        }
        
        @Override
        public void run() {
            final ScheduledFuture access$400;
            final AccessControlContext access$401;
            synchronized (Monitor.this) {
                access$400 = Monitor.this.schedulerFuture;
                access$401 = Monitor.this.acc;
            }
            final PrivilegedAction<Void> privilegedAction = new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    if (Monitor.this.isActive()) {
                        final int[] alreadyNotifieds = Monitor.this.alreadyNotifieds;
                        int n = 0;
                        for (final ObservedObject observedObject : Monitor.this.observedObjects) {
                            if (Monitor.this.isActive()) {
                                Monitor.this.monitor(observedObject, n++, alreadyNotifieds);
                            }
                        }
                    }
                    return null;
                }
            };
            if (access$401 == null) {
                throw new SecurityException("AccessControlContext cannot be null");
            }
            AccessController.doPrivileged((PrivilegedAction<Object>)privilegedAction, access$401);
            synchronized (Monitor.this) {
                if (Monitor.this.isActive() && Monitor.this.schedulerFuture == access$400) {
                    Monitor.this.monitorFuture = null;
                    Monitor.this.schedulerFuture = Monitor.scheduler.schedule(Monitor.this.schedulerTask, Monitor.this.getGranularityPeriod(), TimeUnit.MILLISECONDS);
                }
            }
        }
    }
    
    private static class DaemonThreadFactory implements ThreadFactory
    {
        final ThreadGroup group;
        final AtomicInteger threadNumber;
        final String namePrefix;
        static final String nameSuffix = "]";
        
        public DaemonThreadFactory(final String s) {
            this.threadNumber = new AtomicInteger(1);
            final SecurityManager securityManager = System.getSecurityManager();
            this.group = ((securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup());
            this.namePrefix = "JMX Monitor " + s + " Pool [Thread-";
        }
        
        public DaemonThreadFactory(final String s, final ThreadGroup group) {
            this.threadNumber = new AtomicInteger(1);
            this.group = group;
            this.namePrefix = "JMX Monitor " + s + " Pool [Thread-";
        }
        
        public ThreadGroup getThreadGroup() {
            return this.group;
        }
        
        @Override
        public Thread newThread(final Runnable runnable) {
            final Thread thread = new Thread(this.group, runnable, this.namePrefix + this.threadNumber.getAndIncrement() + "]", 0L);
            thread.setDaemon(true);
            if (thread.getPriority() != 5) {
                thread.setPriority(5);
            }
            return thread;
        }
    }
}
