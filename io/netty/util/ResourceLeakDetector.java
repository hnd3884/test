package io.netty.util;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.lang.ref.WeakReference;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import io.netty.util.internal.PlatformDependent;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.atomic.AtomicReference;
import java.lang.ref.ReferenceQueue;
import java.util.Set;
import io.netty.util.internal.logging.InternalLogger;

public class ResourceLeakDetector<T>
{
    private static final String PROP_LEVEL_OLD = "io.netty.leakDetectionLevel";
    private static final String PROP_LEVEL = "io.netty.leakDetection.level";
    private static final Level DEFAULT_LEVEL;
    private static final String PROP_TARGET_RECORDS = "io.netty.leakDetection.targetRecords";
    private static final int DEFAULT_TARGET_RECORDS = 4;
    private static final String PROP_SAMPLING_INTERVAL = "io.netty.leakDetection.samplingInterval";
    private static final int DEFAULT_SAMPLING_INTERVAL = 128;
    private static final int TARGET_RECORDS;
    static final int SAMPLING_INTERVAL;
    private static Level level;
    private static final InternalLogger logger;
    private final Set<DefaultResourceLeak<?>> allLeaks;
    private final ReferenceQueue<Object> refQueue;
    private final Set<String> reportedLeaks;
    private final String resourceType;
    private final int samplingInterval;
    private static final AtomicReference<String[]> excludedMethods;
    
    @Deprecated
    public static void setEnabled(final boolean enabled) {
        setLevel(enabled ? Level.SIMPLE : Level.DISABLED);
    }
    
    public static boolean isEnabled() {
        return getLevel().ordinal() > Level.DISABLED.ordinal();
    }
    
    public static void setLevel(final Level level) {
        ResourceLeakDetector.level = ObjectUtil.checkNotNull(level, "level");
    }
    
    public static Level getLevel() {
        return ResourceLeakDetector.level;
    }
    
    @Deprecated
    public ResourceLeakDetector(final Class<?> resourceType) {
        this(StringUtil.simpleClassName(resourceType));
    }
    
    @Deprecated
    public ResourceLeakDetector(final String resourceType) {
        this(resourceType, 128, Long.MAX_VALUE);
    }
    
    @Deprecated
    public ResourceLeakDetector(final Class<?> resourceType, final int samplingInterval, final long maxActive) {
        this(resourceType, samplingInterval);
    }
    
    public ResourceLeakDetector(final Class<?> resourceType, final int samplingInterval) {
        this(StringUtil.simpleClassName(resourceType), samplingInterval, Long.MAX_VALUE);
    }
    
    @Deprecated
    public ResourceLeakDetector(final String resourceType, final int samplingInterval, final long maxActive) {
        this.allLeaks = Collections.newSetFromMap(new ConcurrentHashMap<DefaultResourceLeak<?>, Boolean>());
        this.refQueue = new ReferenceQueue<Object>();
        this.reportedLeaks = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        this.resourceType = ObjectUtil.checkNotNull(resourceType, "resourceType");
        this.samplingInterval = samplingInterval;
    }
    
    @Deprecated
    public final ResourceLeak open(final T obj) {
        return this.track0(obj);
    }
    
    public final ResourceLeakTracker<T> track(final T obj) {
        return this.track0(obj);
    }
    
    private DefaultResourceLeak track0(final T obj) {
        final Level level = ResourceLeakDetector.level;
        if (level == Level.DISABLED) {
            return null;
        }
        if (level.ordinal() >= Level.PARANOID.ordinal()) {
            this.reportLeak();
            return new DefaultResourceLeak(obj, this.refQueue, this.allLeaks);
        }
        if (PlatformDependent.threadLocalRandom().nextInt(this.samplingInterval) == 0) {
            this.reportLeak();
            return new DefaultResourceLeak(obj, this.refQueue, this.allLeaks);
        }
        return null;
    }
    
    private void clearRefQueue() {
        while (true) {
            final DefaultResourceLeak ref = (DefaultResourceLeak)this.refQueue.poll();
            if (ref == null) {
                break;
            }
            ref.dispose();
        }
    }
    
    protected boolean needReport() {
        return ResourceLeakDetector.logger.isErrorEnabled();
    }
    
    private void reportLeak() {
        if (!this.needReport()) {
            this.clearRefQueue();
            return;
        }
        while (true) {
            final DefaultResourceLeak ref = (DefaultResourceLeak)this.refQueue.poll();
            if (ref == null) {
                break;
            }
            if (!ref.dispose()) {
                continue;
            }
            final String records = ref.toString();
            if (!this.reportedLeaks.add(records)) {
                continue;
            }
            if (records.isEmpty()) {
                this.reportUntracedLeak(this.resourceType);
            }
            else {
                this.reportTracedLeak(this.resourceType, records);
            }
        }
    }
    
    protected void reportTracedLeak(final String resourceType, final String records) {
        ResourceLeakDetector.logger.error("LEAK: {}.release() was not called before it's garbage-collected. See https://netty.io/wiki/reference-counted-objects.html for more information.{}", resourceType, records);
    }
    
    protected void reportUntracedLeak(final String resourceType) {
        ResourceLeakDetector.logger.error("LEAK: {}.release() was not called before it's garbage-collected. Enable advanced leak reporting to find out where the leak occurred. To enable advanced leak reporting, specify the JVM option '-D{}={}' or call {}.setLevel() See https://netty.io/wiki/reference-counted-objects.html for more information.", resourceType, "io.netty.leakDetection.level", Level.ADVANCED.name().toLowerCase(), StringUtil.simpleClassName(this));
    }
    
    @Deprecated
    protected void reportInstancesLeak(final String resourceType) {
    }
    
    public static void addExclusions(final Class clz, final String... methodNames) {
        final Set<String> nameSet = new HashSet<String>(Arrays.asList(methodNames));
        for (final Method method : clz.getDeclaredMethods()) {
            if (nameSet.remove(method.getName()) && nameSet.isEmpty()) {
                break;
            }
        }
        if (!nameSet.isEmpty()) {
            throw new IllegalArgumentException("Can't find '" + nameSet + "' in " + clz.getName());
        }
        String[] oldMethods;
        String[] newMethods;
        do {
            oldMethods = ResourceLeakDetector.excludedMethods.get();
            newMethods = Arrays.copyOf(oldMethods, oldMethods.length + 2 * methodNames.length);
            for (int i = 0; i < methodNames.length; ++i) {
                newMethods[oldMethods.length + i * 2] = clz.getName();
                newMethods[oldMethods.length + i * 2 + 1] = methodNames[i];
            }
        } while (!ResourceLeakDetector.excludedMethods.compareAndSet(oldMethods, newMethods));
    }
    
    static {
        DEFAULT_LEVEL = Level.SIMPLE;
        logger = InternalLoggerFactory.getInstance(ResourceLeakDetector.class);
        boolean disabled;
        if (SystemPropertyUtil.get("io.netty.noResourceLeakDetection") != null) {
            disabled = SystemPropertyUtil.getBoolean("io.netty.noResourceLeakDetection", false);
            ResourceLeakDetector.logger.debug("-Dio.netty.noResourceLeakDetection: {}", (Object)disabled);
            ResourceLeakDetector.logger.warn("-Dio.netty.noResourceLeakDetection is deprecated. Use '-D{}={}' instead.", "io.netty.leakDetection.level", ResourceLeakDetector.DEFAULT_LEVEL.name().toLowerCase());
        }
        else {
            disabled = false;
        }
        final Level defaultLevel = disabled ? Level.DISABLED : ResourceLeakDetector.DEFAULT_LEVEL;
        String levelStr = SystemPropertyUtil.get("io.netty.leakDetectionLevel", defaultLevel.name());
        levelStr = SystemPropertyUtil.get("io.netty.leakDetection.level", levelStr);
        final Level level = Level.parseLevel(levelStr);
        TARGET_RECORDS = SystemPropertyUtil.getInt("io.netty.leakDetection.targetRecords", 4);
        SAMPLING_INTERVAL = SystemPropertyUtil.getInt("io.netty.leakDetection.samplingInterval", 128);
        ResourceLeakDetector.level = level;
        if (ResourceLeakDetector.logger.isDebugEnabled()) {
            ResourceLeakDetector.logger.debug("-D{}: {}", "io.netty.leakDetection.level", level.name().toLowerCase());
            ResourceLeakDetector.logger.debug("-D{}: {}", "io.netty.leakDetection.targetRecords", ResourceLeakDetector.TARGET_RECORDS);
        }
        excludedMethods = new AtomicReference<String[]>(EmptyArrays.EMPTY_STRINGS);
    }
    
    public enum Level
    {
        DISABLED, 
        SIMPLE, 
        ADVANCED, 
        PARANOID;
        
        static Level parseLevel(final String levelStr) {
            final String trimmedLevelStr = levelStr.trim();
            for (final Level l : values()) {
                if (trimmedLevelStr.equalsIgnoreCase(l.name()) || trimmedLevelStr.equals(String.valueOf(l.ordinal()))) {
                    return l;
                }
            }
            return ResourceLeakDetector.DEFAULT_LEVEL;
        }
    }
    
    private static final class DefaultResourceLeak<T> extends WeakReference<Object> implements ResourceLeakTracker<T>, ResourceLeak
    {
        private static final AtomicReferenceFieldUpdater<DefaultResourceLeak<?>, TraceRecord> headUpdater;
        private static final AtomicIntegerFieldUpdater<DefaultResourceLeak<?>> droppedRecordsUpdater;
        private volatile TraceRecord head;
        private volatile int droppedRecords;
        private final Set<DefaultResourceLeak<?>> allLeaks;
        private final int trackedHash;
        
        DefaultResourceLeak(final Object referent, final ReferenceQueue<Object> refQueue, final Set<DefaultResourceLeak<?>> allLeaks) {
            super(referent, refQueue);
            assert referent != null;
            this.trackedHash = System.identityHashCode(referent);
            allLeaks.add(this);
            DefaultResourceLeak.headUpdater.set(this, new TraceRecord(TraceRecord.BOTTOM));
            this.allLeaks = allLeaks;
        }
        
        @Override
        public void record() {
            this.record0(null);
        }
        
        @Override
        public void record(final Object hint) {
            this.record0(hint);
        }
        
        private void record0(final Object hint) {
            if (ResourceLeakDetector.TARGET_RECORDS > 0) {
                TraceRecord oldHead;
                TraceRecord prevHead;
                while ((prevHead = (oldHead = DefaultResourceLeak.headUpdater.get(this))) != null) {
                    final int numElements = oldHead.pos + 1;
                    boolean dropped;
                    if (numElements >= ResourceLeakDetector.TARGET_RECORDS) {
                        final int backOffFactor = Math.min(numElements - ResourceLeakDetector.TARGET_RECORDS, 30);
                        if (dropped = (PlatformDependent.threadLocalRandom().nextInt(1 << backOffFactor) != 0)) {
                            prevHead = oldHead.next;
                        }
                    }
                    else {
                        dropped = false;
                    }
                    final TraceRecord newHead = (hint != null) ? new TraceRecord(prevHead, hint) : new TraceRecord(prevHead);
                    if (DefaultResourceLeak.headUpdater.compareAndSet(this, oldHead, newHead)) {
                        if (dropped) {
                            DefaultResourceLeak.droppedRecordsUpdater.incrementAndGet(this);
                        }
                    }
                }
            }
        }
        
        boolean dispose() {
            this.clear();
            return this.allLeaks.remove(this);
        }
        
        @Override
        public boolean close() {
            if (this.allLeaks.remove(this)) {
                this.clear();
                DefaultResourceLeak.headUpdater.set(this, null);
                return true;
            }
            return false;
        }
        
        @Override
        public boolean close(final T trackedObject) {
            assert this.trackedHash == System.identityHashCode(trackedObject);
            try {
                return this.close();
            }
            finally {
                reachabilityFence0(trackedObject);
            }
        }
        
        private static void reachabilityFence0(final Object ref) {
            if (ref != null) {
                synchronized (ref) {}
            }
        }
        
        @Override
        public String toString() {
            TraceRecord oldHead = DefaultResourceLeak.headUpdater.getAndSet(this, null);
            if (oldHead == null) {
                return "";
            }
            final int dropped = DefaultResourceLeak.droppedRecordsUpdater.get(this);
            int duped = 0;
            final int present = oldHead.pos + 1;
            final StringBuilder buf = new StringBuilder(present * 2048).append(StringUtil.NEWLINE);
            buf.append("Recent access records: ").append(StringUtil.NEWLINE);
            int i = 1;
            final Set<String> seen = new HashSet<String>(present);
            while (oldHead != TraceRecord.BOTTOM) {
                final String s = oldHead.toString();
                if (seen.add(s)) {
                    if (oldHead.next == TraceRecord.BOTTOM) {
                        buf.append("Created at:").append(StringUtil.NEWLINE).append(s);
                    }
                    else {
                        buf.append('#').append(i++).append(':').append(StringUtil.NEWLINE).append(s);
                    }
                }
                else {
                    ++duped;
                }
                oldHead = oldHead.next;
            }
            if (duped > 0) {
                buf.append(": ").append(duped).append(" leak records were discarded because they were duplicates").append(StringUtil.NEWLINE);
            }
            if (dropped > 0) {
                buf.append(": ").append(dropped).append(" leak records were discarded because the leak record count is targeted to ").append(ResourceLeakDetector.TARGET_RECORDS).append(". Use system property ").append("io.netty.leakDetection.targetRecords").append(" to increase the limit.").append(StringUtil.NEWLINE);
            }
            buf.setLength(buf.length() - StringUtil.NEWLINE.length());
            return buf.toString();
        }
        
        static {
            headUpdater = AtomicReferenceFieldUpdater.newUpdater(DefaultResourceLeak.class, TraceRecord.class, "head");
            droppedRecordsUpdater = AtomicIntegerFieldUpdater.newUpdater(DefaultResourceLeak.class, "droppedRecords");
        }
    }
    
    private static class TraceRecord extends Throwable
    {
        private static final long serialVersionUID = 6065153674892850720L;
        private static final TraceRecord BOTTOM;
        private final String hintString;
        private final TraceRecord next;
        private final int pos;
        
        TraceRecord(final TraceRecord next, final Object hint) {
            this.hintString = ((hint instanceof ResourceLeakHint) ? ((ResourceLeakHint)hint).toHintString() : hint.toString());
            this.next = next;
            this.pos = next.pos + 1;
        }
        
        TraceRecord(final TraceRecord next) {
            this.hintString = null;
            this.next = next;
            this.pos = next.pos + 1;
        }
        
        private TraceRecord() {
            this.hintString = null;
            this.next = null;
            this.pos = -1;
        }
        
        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder(2048);
            if (this.hintString != null) {
                buf.append("\tHint: ").append(this.hintString).append(StringUtil.NEWLINE);
            }
            final StackTraceElement[] array = this.getStackTrace();
            int i = 3;
        Label_0045:
            while (i < array.length) {
                final StackTraceElement element = array[i];
                final String[] exclusions = ResourceLeakDetector.excludedMethods.get();
                while (true) {
                    for (int k = 0; k < exclusions.length; k += 2) {
                        if (exclusions[k].equals(element.getClassName()) && exclusions[k + 1].equals(element.getMethodName())) {
                            ++i;
                            continue Label_0045;
                        }
                    }
                    buf.append('\t');
                    buf.append(element.toString());
                    buf.append(StringUtil.NEWLINE);
                    continue;
                }
            }
            return buf.toString();
        }
        
        static {
            BOTTOM = new TraceRecord() {
                private static final long serialVersionUID = 7396077602074694571L;
                
                @Override
                public Throwable fillInStackTrace() {
                    return this;
                }
            };
        }
    }
}
