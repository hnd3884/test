package org.apache.lucene.util;

import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Field;
import java.util.Map;

public final class RamUsageEstimator
{
    public static final long ONE_KB = 1024L;
    public static final long ONE_MB = 1048576L;
    public static final long ONE_GB = 1073741824L;
    public static final int NUM_BYTES_BOOLEAN = 1;
    public static final int NUM_BYTES_BYTE = 1;
    public static final int NUM_BYTES_CHAR = 2;
    public static final int NUM_BYTES_SHORT = 2;
    public static final int NUM_BYTES_INT = 4;
    public static final int NUM_BYTES_FLOAT = 4;
    public static final int NUM_BYTES_LONG = 8;
    public static final int NUM_BYTES_DOUBLE = 8;
    public static final boolean COMPRESSED_REFS_ENABLED;
    public static final int NUM_BYTES_OBJECT_REF;
    public static final int NUM_BYTES_OBJECT_HEADER;
    public static final int NUM_BYTES_ARRAY_HEADER;
    public static final int NUM_BYTES_OBJECT_ALIGNMENT;
    private static final Map<Class<?>, Integer> primitiveSizes;
    static final long LONG_CACHE_MIN_VALUE;
    static final long LONG_CACHE_MAX_VALUE;
    static final int LONG_SIZE;
    static final boolean JVM_IS_HOTSPOT_64BIT;
    static final String MANAGEMENT_FACTORY_CLASS = "java.lang.management.ManagementFactory";
    static final String HOTSPOT_BEAN_CLASS = "com.sun.management.HotSpotDiagnosticMXBean";
    
    private RamUsageEstimator() {
    }
    
    public static long alignObjectSize(long size) {
        size += RamUsageEstimator.NUM_BYTES_OBJECT_ALIGNMENT - 1L;
        return size - size % RamUsageEstimator.NUM_BYTES_OBJECT_ALIGNMENT;
    }
    
    public static long sizeOf(final Long value) {
        if (value >= RamUsageEstimator.LONG_CACHE_MIN_VALUE && value <= RamUsageEstimator.LONG_CACHE_MAX_VALUE) {
            return 0L;
        }
        return RamUsageEstimator.LONG_SIZE;
    }
    
    public static long sizeOf(final byte[] arr) {
        return alignObjectSize(RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + (long)arr.length);
    }
    
    public static long sizeOf(final boolean[] arr) {
        return alignObjectSize(RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + (long)arr.length);
    }
    
    public static long sizeOf(final char[] arr) {
        return alignObjectSize(RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + 2L * arr.length);
    }
    
    public static long sizeOf(final short[] arr) {
        return alignObjectSize(RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + 2L * arr.length);
    }
    
    public static long sizeOf(final int[] arr) {
        return alignObjectSize(RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + 4L * arr.length);
    }
    
    public static long sizeOf(final float[] arr) {
        return alignObjectSize(RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + 4L * arr.length);
    }
    
    public static long sizeOf(final long[] arr) {
        return alignObjectSize(RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + 8L * arr.length);
    }
    
    public static long sizeOf(final double[] arr) {
        return alignObjectSize(RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + 8L * arr.length);
    }
    
    public static long shallowSizeOf(final Object[] arr) {
        return alignObjectSize(RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + RamUsageEstimator.NUM_BYTES_OBJECT_REF * (long)arr.length);
    }
    
    public static long shallowSizeOf(final Object obj) {
        if (obj == null) {
            return 0L;
        }
        final Class<?> clz = obj.getClass();
        if (clz.isArray()) {
            return shallowSizeOfArray(obj);
        }
        return shallowSizeOfInstance(clz);
    }
    
    public static long shallowSizeOfInstance(Class<?> clazz) {
        if (clazz.isArray()) {
            throw new IllegalArgumentException("This method does not work with array classes.");
        }
        if (clazz.isPrimitive()) {
            return RamUsageEstimator.primitiveSizes.get(clazz);
        }
        long size = RamUsageEstimator.NUM_BYTES_OBJECT_HEADER;
        while (clazz != null) {
            final Class<?> target = clazz;
            final Field[] arr$;
            final Field[] fields = arr$ = AccessController.doPrivileged((PrivilegedAction<Field[]>)new PrivilegedAction<Field[]>() {
                @Override
                public Field[] run() {
                    return target.getDeclaredFields();
                }
            });
            for (final Field f : arr$) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    size = adjustForField(size, f);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return alignObjectSize(size);
    }
    
    private static long shallowSizeOfArray(final Object array) {
        long size = RamUsageEstimator.NUM_BYTES_ARRAY_HEADER;
        final int len = Array.getLength(array);
        if (len > 0) {
            final Class<?> arrayElementClazz = array.getClass().getComponentType();
            if (arrayElementClazz.isPrimitive()) {
                size += len * (long)RamUsageEstimator.primitiveSizes.get(arrayElementClazz);
            }
            else {
                size += RamUsageEstimator.NUM_BYTES_OBJECT_REF * (long)len;
            }
        }
        return alignObjectSize(size);
    }
    
    static long adjustForField(final long sizeSoFar, final Field f) {
        final Class<?> type = f.getType();
        final int fsize = type.isPrimitive() ? RamUsageEstimator.primitiveSizes.get(type) : RamUsageEstimator.NUM_BYTES_OBJECT_REF;
        return sizeSoFar + fsize;
    }
    
    public static String humanReadableUnits(final long bytes) {
        return humanReadableUnits(bytes, new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.ROOT)));
    }
    
    public static String humanReadableUnits(final long bytes, final DecimalFormat df) {
        if (bytes / 1073741824L > 0L) {
            return df.format(bytes / 1.07374182E9f) + " GB";
        }
        if (bytes / 1048576L > 0L) {
            return df.format(bytes / 1048576.0f) + " MB";
        }
        if (bytes / 1024L > 0L) {
            return df.format(bytes / 1024.0f) + " KB";
        }
        return bytes + " bytes";
    }
    
    public static long sizeOf(final Accountable[] accountables) {
        long size = shallowSizeOf(accountables);
        for (final Accountable accountable : accountables) {
            if (accountable != null) {
                size += accountable.ramBytesUsed();
            }
        }
        return size;
    }
    
    static {
        (primitiveSizes = new IdentityHashMap<Class<?>, Integer>()).put(Boolean.TYPE, 1);
        RamUsageEstimator.primitiveSizes.put(Byte.TYPE, 1);
        RamUsageEstimator.primitiveSizes.put(Character.TYPE, 2);
        RamUsageEstimator.primitiveSizes.put(Short.TYPE, 2);
        RamUsageEstimator.primitiveSizes.put(Integer.TYPE, 4);
        RamUsageEstimator.primitiveSizes.put(Float.TYPE, 4);
        RamUsageEstimator.primitiveSizes.put(Double.TYPE, 8);
        RamUsageEstimator.primitiveSizes.put(Long.TYPE, 8);
        if (Constants.JRE_IS_64BIT) {
            boolean compressedOops = false;
            int objectAlignment = 8;
            boolean isHotspot = false;
            try {
                final Class<?> beanClazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
                final Object hotSpotBean = Class.forName("java.lang.management.ManagementFactory").getMethod("getPlatformMXBean", Class.class).invoke(null, beanClazz);
                if (hotSpotBean != null) {
                    isHotspot = true;
                    final Method getVMOptionMethod = beanClazz.getMethod("getVMOption", String.class);
                    try {
                        final Object vmOption = getVMOptionMethod.invoke(hotSpotBean, "UseCompressedOops");
                        compressedOops = Boolean.parseBoolean(vmOption.getClass().getMethod("getValue", (Class<?>[])new Class[0]).invoke(vmOption, new Object[0]).toString());
                    }
                    catch (final ReflectiveOperationException | RuntimeException e) {
                        isHotspot = false;
                    }
                    try {
                        final Object vmOption = getVMOptionMethod.invoke(hotSpotBean, "ObjectAlignmentInBytes");
                        objectAlignment = Integer.parseInt(vmOption.getClass().getMethod("getValue", (Class<?>[])new Class[0]).invoke(vmOption, new Object[0]).toString());
                    }
                    catch (final ReflectiveOperationException | RuntimeException e) {
                        isHotspot = false;
                    }
                }
            }
            catch (final ReflectiveOperationException | RuntimeException e2) {
                isHotspot = false;
            }
            JVM_IS_HOTSPOT_64BIT = isHotspot;
            COMPRESSED_REFS_ENABLED = compressedOops;
            NUM_BYTES_OBJECT_ALIGNMENT = objectAlignment;
            NUM_BYTES_OBJECT_REF = (RamUsageEstimator.COMPRESSED_REFS_ENABLED ? 4 : 8);
            NUM_BYTES_OBJECT_HEADER = 8 + RamUsageEstimator.NUM_BYTES_OBJECT_REF;
            NUM_BYTES_ARRAY_HEADER = (int)alignObjectSize(RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 4);
        }
        else {
            JVM_IS_HOTSPOT_64BIT = false;
            COMPRESSED_REFS_ENABLED = false;
            NUM_BYTES_OBJECT_ALIGNMENT = 8;
            NUM_BYTES_OBJECT_REF = 4;
            NUM_BYTES_OBJECT_HEADER = 8;
            NUM_BYTES_ARRAY_HEADER = RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 4;
        }
        long longCacheMinValue;
        for (longCacheMinValue = 0L; longCacheMinValue > Long.MIN_VALUE && Long.valueOf(longCacheMinValue - 1L) == Long.valueOf(longCacheMinValue - 1L); --longCacheMinValue) {}
        long longCacheMaxValue;
        for (longCacheMaxValue = -1L; longCacheMaxValue < Long.MAX_VALUE && Long.valueOf(longCacheMaxValue + 1L) == Long.valueOf(longCacheMaxValue + 1L); ++longCacheMaxValue) {}
        LONG_CACHE_MIN_VALUE = longCacheMinValue;
        LONG_CACHE_MAX_VALUE = longCacheMaxValue;
        LONG_SIZE = (int)shallowSizeOfInstance(Long.class);
    }
}
