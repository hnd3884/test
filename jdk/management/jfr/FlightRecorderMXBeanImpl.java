package jdk.management.jfr;

import java.util.Arrays;
import java.time.Duration;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import jdk.jfr.RecordingState;
import javax.management.Notification;
import java.util.Collection;
import java.util.ArrayList;
import java.util.function.Predicate;
import javax.management.ListenerNotFoundException;
import jdk.jfr.FlightRecorderListener;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import java.util.function.Consumer;
import javax.management.ObjectName;
import java.util.function.Function;
import java.nio.file.Paths;
import java.util.Iterator;
import java.text.ParseException;
import java.io.Reader;
import java.io.StringReader;
import jdk.jfr.Recording;
import java.util.Objects;
import java.security.AccessControlContext;
import java.security.AccessController;
import jdk.jfr.FlightRecorderPermission;
import java.security.Permission;
import jdk.jfr.internal.management.ManagementSupport;
import jdk.jfr.EventType;
import java.security.PrivilegedAction;
import jdk.jfr.Configuration;
import java.util.Collections;
import java.io.InputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import javax.management.NotificationBroadcasterSupport;
import jdk.jfr.FlightRecorder;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import java.util.List;
import javax.management.NotificationEmitter;
import javax.management.StandardEmitterMBean;

final class FlightRecorderMXBeanImpl extends StandardEmitterMBean implements FlightRecorderMXBean, NotificationEmitter
{
    private static final String ATTRIBUTE_RECORDINGS = "Recordings";
    private static final String OPTION_MAX_SIZE = "maxSize";
    private static final String OPTION_MAX_AGE = "maxAge";
    private static final String OPTION_NAME = "name";
    private static final String OPTION_DISK = "disk";
    private static final String OPTION_DUMP_ON_EXIT = "dumpOnExit";
    private static final String OPTION_DURATION = "duration";
    private static final List<String> OPTIONS;
    private final StreamManager streamHandler;
    private final Map<Long, Object> changes;
    private final AtomicLong sequenceNumber;
    private final List<MXBeanListener> listeners;
    private FlightRecorder recorder;
    
    FlightRecorderMXBeanImpl() {
        super(FlightRecorderMXBean.class, true, new NotificationBroadcasterSupport(createNotificationInfo()));
        this.streamHandler = new StreamManager();
        this.changes = new ConcurrentHashMap<Long, Object>();
        this.sequenceNumber = new AtomicLong();
        this.listeners = new CopyOnWriteArrayList<MXBeanListener>();
    }
    
    @Override
    public void startRecording(final long n) {
        MBeanUtils.checkControl();
        this.getExistingRecording(n).start();
    }
    
    @Override
    public boolean stopRecording(final long n) {
        MBeanUtils.checkControl();
        return this.getExistingRecording(n).stop();
    }
    
    @Override
    public void closeRecording(final long n) {
        MBeanUtils.checkControl();
        this.getExistingRecording(n).close();
    }
    
    @Override
    public long openStream(final long n, final Map<String, String> map) throws IOException {
        MBeanUtils.checkControl();
        if (!FlightRecorder.isInitialized()) {
            throw new IllegalArgumentException("No recording available with id " + n);
        }
        final HashMap hashMap = (map == null) ? new HashMap() : new HashMap((Map<? extends K, ? extends V>)map);
        final Instant timestamp = MBeanUtils.parseTimestamp((String)hashMap.get("startTime"), Instant.MIN);
        final Instant timestamp2 = MBeanUtils.parseTimestamp((String)hashMap.get("endTime"), Instant.MAX);
        final int blockSize = MBeanUtils.parseBlockSize((String)hashMap.get("blockSize"), 50000);
        final InputStream stream = this.getExistingRecording(n).getStream(timestamp, timestamp2);
        if (stream == null) {
            throw new IOException("No recording data available");
        }
        return this.streamHandler.create(stream, blockSize).getId();
    }
    
    @Override
    public void closeStream(final long n) throws IOException {
        MBeanUtils.checkControl();
        this.streamHandler.getStream(n).close();
    }
    
    @Override
    public byte[] readStream(final long n) throws IOException {
        MBeanUtils.checkMonitor();
        return this.streamHandler.getStream(n).read();
    }
    
    @Override
    public List<RecordingInfo> getRecordings() {
        MBeanUtils.checkMonitor();
        if (!FlightRecorder.isInitialized()) {
            return Collections.emptyList();
        }
        return MBeanUtils.transformList(this.getRecorder().getRecordings(), RecordingInfo::new);
    }
    
    @Override
    public List<ConfigurationInfo> getConfigurations() {
        MBeanUtils.checkMonitor();
        return MBeanUtils.transformList(Configuration.getConfigurations(), ConfigurationInfo::new);
    }
    
    @Override
    public List<EventTypeInfo> getEventTypes() {
        MBeanUtils.checkMonitor();
        return MBeanUtils.transformList((List<EventType>)AccessController.doPrivileged((PrivilegedAction<List<T>>)new PrivilegedAction<List<EventType>>() {
            @Override
            public List<EventType> run() {
                return ManagementSupport.getEventTypes();
            }
        }, null, new FlightRecorderPermission("accessFlightRecorder")), EventTypeInfo::new);
    }
    
    @Override
    public Map<String, String> getRecordingSettings(final long n) throws IllegalArgumentException {
        MBeanUtils.checkMonitor();
        return this.getExistingRecording(n).getSettings();
    }
    
    @Override
    public void setRecordingSettings(final long n, final Map<String, String> settings) throws IllegalArgumentException {
        Objects.requireNonNull(settings);
        MBeanUtils.checkControl();
        this.getExistingRecording(n).setSettings(settings);
    }
    
    @Override
    public long newRecording() {
        MBeanUtils.checkControl();
        this.getRecorder();
        return AccessController.doPrivileged((PrivilegedAction<Recording>)new PrivilegedAction<Recording>() {
            @Override
            public Recording run() {
                return new Recording();
            }
        }, null, new FlightRecorderPermission("accessFlightRecorder")).getId();
    }
    
    @Override
    public long takeSnapshot() {
        MBeanUtils.checkControl();
        return this.getRecorder().takeSnapshot().getId();
    }
    
    @Override
    public void setConfiguration(final long n, final String s) throws IllegalArgumentException {
        Objects.requireNonNull(s);
        MBeanUtils.checkControl();
        try {
            this.getExistingRecording(n).setSettings(Configuration.create(new StringReader(s)).getSettings());
        }
        catch (final IOException | ParseException ex) {
            throw new IllegalArgumentException("Could not parse configuration", (Throwable)ex);
        }
    }
    
    @Override
    public void setPredefinedConfiguration(final long n, final String s) throws IllegalArgumentException {
        Objects.requireNonNull(s);
        MBeanUtils.checkControl();
        final Recording existingRecording = this.getExistingRecording(n);
        for (final Configuration configuration : Configuration.getConfigurations()) {
            if (configuration.getName().equals(s)) {
                existingRecording.setSettings(configuration.getSettings());
                return;
            }
        }
        throw new IllegalArgumentException("Could not find configuration with name " + s);
    }
    
    @Override
    public void copyTo(final long n, final String s) throws IOException {
        Objects.requireNonNull(s);
        MBeanUtils.checkControl();
        this.getExistingRecording(n).dump(Paths.get(s, new String[0]));
    }
    
    @Override
    public void setRecordingOptions(final long n, final Map<String, String> map) throws IllegalArgumentException {
        Objects.requireNonNull(map);
        MBeanUtils.checkControl();
        final HashMap hashMap = new HashMap((Map<? extends K, ? extends V>)map);
        for (final Map.Entry entry : hashMap.entrySet()) {
            final Object key = entry.getKey();
            final Object value = entry.getValue();
            if (!(key instanceof String)) {
                throw new IllegalArgumentException("Option key must not be null, or other type than " + String.class);
            }
            if (!FlightRecorderMXBeanImpl.OPTIONS.contains(key)) {
                throw new IllegalArgumentException("Unknown recording option: " + key + ". Valid options are " + FlightRecorderMXBeanImpl.OPTIONS + ".");
            }
            if (value != null && !(value instanceof String)) {
                throw new IllegalArgumentException("Incorrect value for option " + key + ". Values must be of type " + String.class + " .");
            }
        }
        final Recording existingRecording = this.getExistingRecording(n);
        validateOption(hashMap, "dumpOnExit", MBeanUtils::booleanValue);
        validateOption(hashMap, "disk", MBeanUtils::booleanValue);
        validateOption(hashMap, "name", (Function<String, Object>)Function.identity());
        validateOption(hashMap, "maxAge", MBeanUtils::duration);
        validateOption(hashMap, "maxSize", MBeanUtils::size);
        validateOption(hashMap, "duration", MBeanUtils::duration);
        setOption(hashMap, "dumpOnExit", "false", MBeanUtils::booleanValue, b -> recording.setDumpOnExit(b));
        setOption(hashMap, "disk", "true", MBeanUtils::booleanValue, b2 -> recording2.setToDisk(b2));
        setOption(hashMap, "name", String.valueOf(existingRecording.getId()), (Function<String, Object>)Function.identity(), name -> recording3.setName(name));
        setOption(hashMap, "maxAge", null, MBeanUtils::duration, maxAge -> recording4.setMaxAge(maxAge));
        setOption(hashMap, "maxSize", "0", MBeanUtils::size, n2 -> recording5.setMaxSize(n2));
        setOption(hashMap, "duration", null, MBeanUtils::duration, duration -> recording6.setDuration(duration));
    }
    
    @Override
    public Map<String, String> getRecordingOptions(final long n) throws IllegalArgumentException {
        MBeanUtils.checkMonitor();
        final Recording existingRecording = this.getExistingRecording(n);
        final HashMap hashMap = new HashMap(10);
        hashMap.put("dumpOnExit", String.valueOf(existingRecording.getDumpOnExit()));
        hashMap.put("disk", String.valueOf(existingRecording.isToDisk()));
        hashMap.put("name", String.valueOf(existingRecording.getName()));
        hashMap.put("maxAge", ManagementSupport.formatTimespan(existingRecording.getMaxAge(), " "));
        final Long value = existingRecording.getMaxSize();
        hashMap.put("maxSize", String.valueOf((value == null) ? "0" : value.toString()));
        hashMap.put("duration", ManagementSupport.formatTimespan(existingRecording.getDuration(), " "));
        return hashMap;
    }
    
    @Override
    public long cloneRecording(final long n, final boolean b) throws IllegalStateException, SecurityException {
        MBeanUtils.checkControl();
        return this.getRecording(n).copy(b).getId();
    }
    
    @Override
    public ObjectName getObjectName() {
        return MBeanUtils.createObjectName();
    }
    
    private Recording getExistingRecording(final long n) {
        if (FlightRecorder.isInitialized()) {
            final Recording recording = this.getRecording(n);
            if (recording != null) {
                return recording;
            }
        }
        throw new IllegalArgumentException("No recording available with id " + n);
    }
    
    private Recording getRecording(final long n) {
        return this.getRecorder().getRecordings().stream().filter(recording -> recording.getId() == n2).findFirst().orElse(null);
    }
    
    private static <T, U> void setOption(final Map<String, String> map, final String s, final String s2, final Function<String, U> function, final Consumer<U> consumer) {
        if (!map.containsKey(s)) {
            return;
        }
        String s3 = map.get(s);
        if (s3 == null) {
            s3 = s2;
        }
        try {
            consumer.accept(function.apply(s3));
        }
        catch (final IllegalArgumentException ex) {
            throw new IllegalArgumentException("Not a valid value for option '" + s + "'. " + ex.getMessage());
        }
    }
    
    private static <T, U> void validateOption(final Map<String, String> map, final String s, final Function<String, U> function) {
        try {
            final String s2 = map.get(s);
            if (s2 == null) {
                return;
            }
            function.apply(s2);
        }
        catch (final IllegalArgumentException ex) {
            throw new IllegalArgumentException("Not a valid value for option '" + s + "'. " + ex.getMessage());
        }
    }
    
    private FlightRecorder getRecorder() throws SecurityException {
        synchronized (this.streamHandler) {
            if (this.recorder == null) {
                this.recorder = AccessController.doPrivileged((PrivilegedAction<FlightRecorder>)new PrivilegedAction<FlightRecorder>() {
                    @Override
                    public FlightRecorder run() {
                        return FlightRecorder.getFlightRecorder();
                    }
                }, null, new FlightRecorderPermission("accessFlightRecorder"));
            }
            return this.recorder;
        }
    }
    
    private static MBeanNotificationInfo[] createNotificationInfo() {
        return new MBeanNotificationInfo[] { new MBeanNotificationInfo(new String[] { "jmx.attribute.change" }, AttributeChangeNotification.class.getName(), "Notifies if the RecordingState has changed for one of the recordings, for example if a recording starts or stops") };
    }
    
    @Override
    public void addNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) {
        final MXBeanListener mxBeanListener = new MXBeanListener(notificationListener, notificationFilter, o);
        this.listeners.add(mxBeanListener);
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                FlightRecorder.addListener(mxBeanListener);
                return null;
            }
        }, null, new FlightRecorderPermission("accessFlightRecorder"));
        super.addNotificationListener(notificationListener, notificationFilter, o);
    }
    
    @Override
    public void removeNotificationListener(final NotificationListener notificationListener) throws ListenerNotFoundException {
        this.removeListeners(mxBeanListener -> notificationListener2 == mxBeanListener.listener);
        super.removeNotificationListener(notificationListener);
    }
    
    @Override
    public void removeNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws ListenerNotFoundException {
        this.removeListeners(mxBeanListener -> notificationListener2 == mxBeanListener.listener && notificationFilter2 == mxBeanListener.filter && o2 == mxBeanListener.handback);
        super.removeNotificationListener(notificationListener, notificationFilter, o);
    }
    
    private void removeListeners(final Predicate<MXBeanListener> predicate) {
        final ArrayList list = new ArrayList(this.listeners.size());
        for (final MXBeanListener mxBeanListener : this.listeners) {
            if (predicate.test(mxBeanListener)) {
                list.add(mxBeanListener);
                FlightRecorder.removeListener(mxBeanListener);
            }
        }
        this.listeners.removeAll(list);
    }
    
    private Notification createNotication(final Recording recording) {
        try {
            final Long value = recording.getId();
            final Object value2 = this.changes.get(recording.getId());
            final Object attribute = this.getAttribute("Recordings");
            if (recording.getState() != RecordingState.CLOSED) {
                this.changes.put(value, attribute);
            }
            else {
                this.changes.remove(value);
            }
            return new AttributeChangeNotification(this.getObjectName(), this.sequenceNumber.incrementAndGet(), System.currentTimeMillis(), "Recording " + recording.getName() + " is " + recording.getState(), "Recordings", attribute.getClass().getName(), value2, attribute);
        }
        catch (final AttributeNotFoundException | MBeanException | ReflectionException ex) {
            throw new RuntimeException("Could not create notifcation for FlightRecorderMXBean. " + ((Throwable)ex).getMessage(), (Throwable)ex);
        }
    }
    
    static {
        OPTIONS = Arrays.asList("dumpOnExit", "duration", "name", "maxAge", "maxSize", "disk");
    }
    
    final class MXBeanListener implements FlightRecorderListener
    {
        private final NotificationListener listener;
        private final NotificationFilter filter;
        private final Object handback;
        private final AccessControlContext context;
        
        public MXBeanListener(final NotificationListener listener, final NotificationFilter filter, final Object handback) {
            this.context = AccessController.getContext();
            this.listener = listener;
            this.filter = filter;
            this.handback = handback;
        }
        
        @Override
        public void recordingStateChanged(final Recording recording) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    FlightRecorderMXBeanImpl.this.sendNotification(FlightRecorderMXBeanImpl.this.createNotication(recording));
                    return null;
                }
            }, this.context);
        }
    }
}
