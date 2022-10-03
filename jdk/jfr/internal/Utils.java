package jdk.jfr.internal;

import java.time.temporal.TemporalAccessor;
import java.time.LocalDateTime;
import jdk.jfr.Recording;
import java.util.Collections;
import java.util.Objects;
import jdk.jfr.internal.settings.StackTraceSetting;
import jdk.jfr.internal.settings.PeriodSetting;
import jdk.jfr.internal.settings.ThresholdSetting;
import jdk.internal.org.objectweb.asm.util.CheckClassAdapter;
import jdk.internal.org.objectweb.asm.ClassReader;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Field;
import jdk.jfr.internal.handlers.EventHandler;
import jdk.jfr.Event;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import jdk.jfr.RecordingState;
import java.lang.reflect.Method;
import java.lang.annotation.Repeatable;
import java.util.Collection;
import java.util.ArrayList;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.time.Duration;
import java.security.Permission;
import jdk.jfr.FlightRecorderPermission;

public final class Utils
{
    private static final String INFINITY = "infinity";
    private static Boolean SAVE_GENERATED;
    public static final String EVENTS_PACKAGE_NAME = "jdk.jfr.events";
    public static final String INSTRUMENT_PACKAGE_NAME = "jdk.jfr.internal.instrument";
    public static final String HANDLERS_PACKAGE_NAME = "jdk.jfr.internal.handlers";
    public static final String REGISTER_EVENT = "registerEvent";
    public static final String ACCESS_FLIGHT_RECORDER = "accessFlightRecorder";
    private static final String LEGACY_EVENT_NAME_PREFIX = "com.oracle.jdk.";
    
    public static void checkAccessFlightRecorder() throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new FlightRecorderPermission("accessFlightRecorder"));
        }
    }
    
    public static void checkRegisterPermission() throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new FlightRecorderPermission("registerEvent"));
        }
    }
    
    private static String formatDataAmount(final String s, final long n) {
        final int n2 = (int)(Math.log((double)Math.abs(n)) / Math.log(1024.0));
        return String.format(s, n / Math.pow(1024.0, n2), "kMGTPE".charAt(n2 - 1));
    }
    
    public static String formatBytesCompact(final long n) {
        if (n < 1024L) {
            return String.valueOf(n);
        }
        return formatDataAmount("%.1f%cB", n);
    }
    
    public static String formatBits(final long n) {
        if (n == 1L || n == -1L) {
            return n + " bit";
        }
        if (n < 1024L && n > -1024L) {
            return n + " bits";
        }
        return formatDataAmount("%.1f %cbit", n);
    }
    
    public static String formatBytes(final long n) {
        if (n == 1L || n == -1L) {
            return n + " byte";
        }
        if (n < 1024L && n > -1024L) {
            return n + " bytes";
        }
        return formatDataAmount("%.1f %cB", n);
    }
    
    public static String formatBytesPerSecond(final long n) {
        if (n < 1024L && n > -1024L) {
            return n + " byte/s";
        }
        return formatDataAmount("%.1f %cB/s", n);
    }
    
    public static String formatBitsPerSecond(final long n) {
        if (n < 1024L && n > -1024L) {
            return n + " bps";
        }
        return formatDataAmount("%.1f %cbps", n);
    }
    
    public static String formatTimespan(final Duration duration, final String s) {
        if (duration == null) {
            return "0";
        }
        long nanos = duration.toNanos();
        TimespanUnit nanoseconds = TimespanUnit.NANOSECONDS;
        final TimespanUnit[] values = TimespanUnit.values();
        for (int length = values.length, i = 0; i < length; ++i) {
            final long amount = (nanoseconds = values[i]).amount;
            if (nanoseconds == TimespanUnit.DAYS || nanos < amount) {
                break;
            }
            if (nanos % amount != 0L) {
                break;
            }
            nanos /= amount;
        }
        return String.format("%d%s%s", nanos, s, nanoseconds.text);
    }
    
    public static long parseTimespanWithInfinity(final String s) {
        if ("infinity".equals(s)) {
            return Long.MAX_VALUE;
        }
        return parseTimespan(s);
    }
    
    public static long parseTimespan(final String s) {
        if (s.endsWith("ns")) {
            return Long.parseLong(s.substring(0, s.length() - 2).trim());
        }
        if (s.endsWith("us")) {
            return TimeUnit.NANOSECONDS.convert(Long.parseLong(s.substring(0, s.length() - 2).trim()), TimeUnit.MICROSECONDS);
        }
        if (s.endsWith("ms")) {
            return TimeUnit.NANOSECONDS.convert(Long.parseLong(s.substring(0, s.length() - 2).trim()), TimeUnit.MILLISECONDS);
        }
        if (s.endsWith("s")) {
            return TimeUnit.NANOSECONDS.convert(Long.parseLong(s.substring(0, s.length() - 1).trim()), TimeUnit.SECONDS);
        }
        if (s.endsWith("m")) {
            return 60L * TimeUnit.NANOSECONDS.convert(Long.parseLong(s.substring(0, s.length() - 1).trim()), TimeUnit.SECONDS);
        }
        if (s.endsWith("h")) {
            return 3600L * TimeUnit.NANOSECONDS.convert(Long.parseLong(s.substring(0, s.length() - 1).trim()), TimeUnit.SECONDS);
        }
        if (s.endsWith("d")) {
            return 86400L * TimeUnit.NANOSECONDS.convert(Long.parseLong(s.substring(0, s.length() - 1).trim()), TimeUnit.SECONDS);
        }
        try {
            Long.parseLong(s);
        }
        catch (final NumberFormatException ex) {
            throw new NumberFormatException("'" + s + "' is not a valid timespan. Shoule be numeric value followed by a unit, i.e. 20 ms. Valid units are ns, us, s, m, h and d.");
        }
        throw new NumberFormatException("Timespan + '" + s + "' is missing unit. Valid units are ns, us, s, m, h and d.");
    }
    
    static List<Annotation> getAnnotations(final Class<?> clazz) {
        final ArrayList list = new ArrayList();
        final Annotation[] annotations = clazz.getAnnotations();
        for (int length = annotations.length, i = 0; i < length; ++i) {
            list.addAll(getAnnotation(annotations[i]));
        }
        return list;
    }
    
    private static List<? extends Annotation> getAnnotation(final Annotation annotation) {
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        final Method valueMethod = getValueMethod(annotationType);
        if (valueMethod != null) {
            final Class<?> returnType = valueMethod.getReturnType();
            if (returnType.isArray()) {
                final Repeatable repeatable = (Repeatable)returnType.getComponentType().getAnnotation(Repeatable.class);
                if (repeatable != null && annotationType == repeatable.value()) {
                    return getAnnotationValues(annotation, valueMethod);
                }
            }
        }
        final ArrayList list = new ArrayList();
        list.add(annotation);
        return list;
    }
    
    static boolean isAfter(final RecordingState recordingState, final RecordingState recordingState2) {
        return recordingState.ordinal() > recordingState2.ordinal();
    }
    
    static boolean isBefore(final RecordingState recordingState, final RecordingState recordingState2) {
        return recordingState.ordinal() < recordingState2.ordinal();
    }
    
    static boolean isState(final RecordingState recordingState, final RecordingState... array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i] == recordingState) {
                return true;
            }
        }
        return false;
    }
    
    private static List<Annotation> getAnnotationValues(final Annotation annotation, final Method method) {
        try {
            return Arrays.asList((Annotation[])method.invoke(annotation, new Object[0]));
        }
        catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            return new ArrayList<Annotation>();
        }
    }
    
    private static Method getValueMethod(final Class<?> clazz) {
        try {
            return clazz.getMethod("value", (Class[])new Class[0]);
        }
        catch (final NoSuchMethodException ex) {
            return null;
        }
    }
    
    public static void touch(final Path path) throws IOException {
        new RandomAccessFile(path.toFile(), "rw").close();
    }
    
    public static Class<?> unboxType(final Class<?> clazz) {
        if (clazz == Integer.class) {
            return Integer.TYPE;
        }
        if (clazz == Long.class) {
            return Long.TYPE;
        }
        if (clazz == Float.class) {
            return Float.TYPE;
        }
        if (clazz == Double.class) {
            return Double.TYPE;
        }
        if (clazz == Byte.class) {
            return Byte.TYPE;
        }
        if (clazz == Short.class) {
            return Short.TYPE;
        }
        if (clazz == Boolean.class) {
            return Boolean.TYPE;
        }
        if (clazz == Character.class) {
            return Character.TYPE;
        }
        return clazz;
    }
    
    static long nanosToTicks(final long n) {
        return (long)(n * JVM.getJVM().getTimeConversionFactor());
    }
    
    static synchronized EventHandler getHandler(final Class<? extends Event> clazz) {
        ensureValidEventSubclass(clazz);
        try {
            final Field declaredField = clazz.getDeclaredField("eventHandler");
            SecuritySupport.setAccessible(declaredField);
            return (EventHandler)declaredField.get(null);
        }
        catch (final NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            throw new InternalError("Could not access event handler");
        }
    }
    
    static synchronized void setHandler(final Class<? extends Event> clazz, final EventHandler eventHandler) {
        ensureValidEventSubclass(clazz);
        try {
            final Field declaredField = clazz.getDeclaredField("eventHandler");
            SecuritySupport.setAccessible(declaredField);
            declaredField.set(null, eventHandler);
        }
        catch (final NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            throw new InternalError("Could not access event handler");
        }
    }
    
    public static Map<String, String> sanitizeNullFreeStringMap(final Map<String, String> map) {
        final HashMap hashMap = new HashMap(map.size());
        for (final Map.Entry entry : map.entrySet()) {
            final String s = (String)entry.getKey();
            if (s == null) {
                throw new NullPointerException("Null key is not allowed in map");
            }
            final String s2 = (String)entry.getValue();
            if (s2 == null) {
                throw new NullPointerException("Null value is not allowed in map");
            }
            hashMap.put(s, s2);
        }
        return hashMap;
    }
    
    public static <T> List<T> sanitizeNullFreeList(final List<T> list, final Class<T> clazz) {
        final ArrayList list2 = new ArrayList(list.size());
        for (final Object next : list) {
            if (next == null) {
                throw new NullPointerException("Null is not an allowed element in list");
            }
            if (next.getClass() != clazz) {
                throw new ClassCastException();
            }
            list2.add(next);
        }
        return list2;
    }
    
    static List<Field> getVisibleEventFields(final Class<?> clazz) {
        ensureValidEventSubclass(clazz);
        final ArrayList list = new ArrayList();
        for (Class<Event> superclass = (Class<Event>)clazz; superclass != Event.class; superclass = superclass.getSuperclass()) {
            for (final Field field : superclass.getDeclaredFields()) {
                if (superclass == clazz || !Modifier.isPrivate(field.getModifiers())) {
                    list.add(field);
                }
            }
        }
        return list;
    }
    
    public static void ensureValidEventSubclass(final Class<?> clazz) {
        if (Event.class.isAssignableFrom(clazz) && Modifier.isAbstract(clazz.getModifiers())) {
            throw new IllegalArgumentException("Abstract event classes are not allowed");
        }
        if (clazz == Event.class || !Event.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Must be a subclass to " + Event.class.getName());
        }
    }
    
    public static void writeGeneratedASM(final String s, final byte[] array) {
        if (Utils.SAVE_GENERATED == null) {
            Utils.SAVE_GENERATED = SecuritySupport.getBooleanProperty("jfr.save.generated.asm");
        }
        if (Utils.SAVE_GENERATED) {
            try {
                try (final FileOutputStream fileOutputStream = new FileOutputStream(s + ".class")) {
                    fileOutputStream.write(array);
                }
                try (final FileWriter fileWriter = new FileWriter(s + ".asm");
                     final PrintWriter printWriter = new PrintWriter(fileWriter)) {
                    CheckClassAdapter.verify(new ClassReader(array), true, printWriter);
                }
                Logger.log(LogTag.JFR_SYSTEM_BYTECODE, LogLevel.INFO, "Instrumented code saved to " + s + ".class and .asm");
            }
            catch (final IOException ex) {
                Logger.log(LogTag.JFR_SYSTEM_BYTECODE, LogLevel.INFO, "Could not save instrumented code, for " + s + ".class and .asm");
            }
        }
    }
    
    public static void ensureInitialized(final Class<? extends Event> clazz) {
        SecuritySupport.ensureClassIsInitialized(clazz);
    }
    
    public static Object makePrimitiveArray(final String s, final List<Object> list) {
        final int size = list.size();
        switch (s) {
            case "int": {
                final int[] array = new int[size];
                for (int i = 0; i < size; ++i) {
                    array[i] = (int)list.get(i);
                }
                return array;
            }
            case "long": {
                final long[] array2 = new long[size];
                for (int j = 0; j < size; ++j) {
                    array2[j] = (long)list.get(j);
                }
                return array2;
            }
            case "float": {
                final float[] array3 = new float[size];
                for (int k = 0; k < size; ++k) {
                    array3[k] = (float)list.get(k);
                }
                return array3;
            }
            case "double": {
                final double[] array4 = new double[size];
                for (int l = 0; l < size; ++l) {
                    array4[l] = (double)list.get(l);
                }
                return array4;
            }
            case "short": {
                final short[] array5 = new short[size];
                for (int n2 = 0; n2 < size; ++n2) {
                    array5[n2] = (short)list.get(n2);
                }
                return array5;
            }
            case "char": {
                final char[] array6 = new char[size];
                for (int n3 = 0; n3 < size; ++n3) {
                    array6[n3] = (char)list.get(n3);
                }
                return array6;
            }
            case "byte": {
                final byte[] array7 = new byte[size];
                for (int n4 = 0; n4 < size; ++n4) {
                    array7[n4] = (byte)list.get(n4);
                }
                return array7;
            }
            case "boolean": {
                final boolean[] array8 = new boolean[size];
                for (int n5 = 0; n5 < size; ++n5) {
                    array8[n5] = (boolean)list.get(n5);
                }
                return array8;
            }
            case "java.lang.String": {
                final String[] array9 = new String[size];
                for (int n6 = 0; n6 < size; ++n6) {
                    array9[n6] = (String)list.get(n6);
                }
                return array9;
            }
            default: {
                return null;
            }
        }
    }
    
    public static boolean isSettingVisible(final Control control, final boolean b) {
        if (control instanceof ThresholdSetting) {
            return !b;
        }
        if (control instanceof PeriodSetting) {
            return b;
        }
        return !(control instanceof StackTraceSetting) || !b;
    }
    
    public static boolean isSettingVisible(final long n, final boolean b) {
        if (ThresholdSetting.isType(n)) {
            return !b;
        }
        if (PeriodSetting.isType(n)) {
            return b;
        }
        return !StackTraceSetting.isType(n) || !b;
    }
    
    public static Type getValidType(Class<?> componentType, final String s) {
        Objects.requireNonNull(componentType, "Null is not a valid type for value descriptor " + s);
        if (componentType.isArray()) {
            componentType = componentType.getComponentType();
            if (componentType != String.class && !componentType.isPrimitive()) {
                throw new IllegalArgumentException("Only arrays of primitives and Strings are allowed");
            }
        }
        final Type knownType = Type.getKnownType(componentType);
        if (knownType == null || knownType == Type.STACK_TRACE) {
            throw new IllegalArgumentException("Only primitive types, java.lang.Thread, java.lang.String and java.lang.Class are allowed for value descriptors. " + componentType.getName());
        }
        return knownType;
    }
    
    public static <T> List<T> smallUnmodifiable(final List<T> list) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        if (list.size() != 0) {
            return Collections.singletonList(list.get(0));
        }
        return Collections.unmodifiableList((List<? extends T>)list);
    }
    
    public static String upgradeLegacyJDKEvent(final String s) {
        if (s.length() <= "com.oracle.jdk.".length()) {
            return s;
        }
        if (s.startsWith("com.oracle.jdk.")) {
            final int lastIndex = s.lastIndexOf(".");
            if (lastIndex == "com.oracle.jdk.".length() - 1) {
                return "jdk." + s.substring(lastIndex + 1);
            }
        }
        return s;
    }
    
    public static String makeFilename(final Recording recording) {
        return "hotspot-pid-" + JVM.getJVM().getPid() + ((recording == null) ? "" : ("-id-" + Long.toString(recording.getId()))) + "-" + Repository.REPO_DATE_FORMAT.format(LocalDateTime.now()) + ".jfr";
    }
    
    private enum TimespanUnit
    {
        NANOSECONDS("ns", 1000L), 
        MICROSECONDS("us", 1000L), 
        MILLISECONDS("ms", 1000L), 
        SECONDS("s", 60L), 
        MINUTES("m", 60L), 
        HOURS("h", 24L), 
        DAYS("d", 7L);
        
        final String text;
        final long amount;
        
        private TimespanUnit(final String text, final long amount) {
            this.text = text;
            this.amount = amount;
        }
    }
}
