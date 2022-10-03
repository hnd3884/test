package io.netty.util.internal;

import java.nio.Buffer;
import java.lang.reflect.AccessibleObject;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import sun.misc.Unsafe;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import io.netty.util.internal.logging.InternalLogger;

@SuppressJava6Requirement(reason = "Unsafe access is guarded")
final class PlatformDependent0
{
    private static final InternalLogger logger;
    private static final long ADDRESS_FIELD_OFFSET;
    private static final long BYTE_ARRAY_BASE_OFFSET;
    private static final long INT_ARRAY_BASE_OFFSET;
    private static final long INT_ARRAY_INDEX_SCALE;
    private static final long LONG_ARRAY_BASE_OFFSET;
    private static final long LONG_ARRAY_INDEX_SCALE;
    private static final Constructor<?> DIRECT_BUFFER_CONSTRUCTOR;
    private static final Throwable EXPLICIT_NO_UNSAFE_CAUSE;
    private static final Method ALLOCATE_ARRAY_METHOD;
    private static final Method ALIGN_SLICE;
    private static final int JAVA_VERSION;
    private static final boolean IS_ANDROID;
    private static final Throwable UNSAFE_UNAVAILABILITY_CAUSE;
    private static final Object INTERNAL_UNSAFE;
    private static final boolean IS_EXPLICIT_TRY_REFLECTION_SET_ACCESSIBLE;
    private static final boolean RUNNING_IN_NATIVE_IMAGE;
    static final Unsafe UNSAFE;
    static final int HASH_CODE_ASCII_SEED = -1028477387;
    static final int HASH_CODE_C1 = -862048943;
    static final int HASH_CODE_C2 = 461845907;
    private static final long UNSAFE_COPY_THRESHOLD = 1048576L;
    private static final boolean UNALIGNED;
    
    private static boolean unsafeStaticFieldOffsetSupported() {
        return !PlatformDependent0.RUNNING_IN_NATIVE_IMAGE;
    }
    
    static boolean isExplicitNoUnsafe() {
        return PlatformDependent0.EXPLICIT_NO_UNSAFE_CAUSE != null;
    }
    
    private static Throwable explicitNoUnsafeCause0() {
        final boolean noUnsafe = SystemPropertyUtil.getBoolean("io.netty.noUnsafe", false);
        PlatformDependent0.logger.debug("-Dio.netty.noUnsafe: {}", (Object)noUnsafe);
        if (noUnsafe) {
            PlatformDependent0.logger.debug("sun.misc.Unsafe: unavailable (io.netty.noUnsafe)");
            return new UnsupportedOperationException("sun.misc.Unsafe: unavailable (io.netty.noUnsafe)");
        }
        String unsafePropName;
        if (SystemPropertyUtil.contains("io.netty.tryUnsafe")) {
            unsafePropName = "io.netty.tryUnsafe";
        }
        else {
            unsafePropName = "org.jboss.netty.tryUnsafe";
        }
        if (!SystemPropertyUtil.getBoolean(unsafePropName, true)) {
            final String msg = "sun.misc.Unsafe: unavailable (" + unsafePropName + ")";
            PlatformDependent0.logger.debug(msg);
            return new UnsupportedOperationException(msg);
        }
        return null;
    }
    
    static boolean isUnaligned() {
        return PlatformDependent0.UNALIGNED;
    }
    
    static boolean hasUnsafe() {
        return PlatformDependent0.UNSAFE != null;
    }
    
    static Throwable getUnsafeUnavailabilityCause() {
        return PlatformDependent0.UNSAFE_UNAVAILABILITY_CAUSE;
    }
    
    static boolean unalignedAccess() {
        return PlatformDependent0.UNALIGNED;
    }
    
    static void throwException(final Throwable cause) {
        PlatformDependent0.UNSAFE.throwException(ObjectUtil.checkNotNull(cause, "cause"));
    }
    
    static boolean hasDirectBufferNoCleanerConstructor() {
        return PlatformDependent0.DIRECT_BUFFER_CONSTRUCTOR != null;
    }
    
    static ByteBuffer reallocateDirectNoCleaner(final ByteBuffer buffer, final int capacity) {
        return newDirectBuffer(PlatformDependent0.UNSAFE.reallocateMemory(directBufferAddress(buffer), capacity), capacity);
    }
    
    static ByteBuffer allocateDirectNoCleaner(final int capacity) {
        return newDirectBuffer(PlatformDependent0.UNSAFE.allocateMemory(Math.max(1, capacity)), capacity);
    }
    
    static boolean hasAlignSliceMethod() {
        return PlatformDependent0.ALIGN_SLICE != null;
    }
    
    static ByteBuffer alignSlice(final ByteBuffer buffer, final int alignment) {
        try {
            return (ByteBuffer)PlatformDependent0.ALIGN_SLICE.invoke(buffer, alignment);
        }
        catch (final IllegalAccessException e) {
            throw new Error(e);
        }
        catch (final InvocationTargetException e2) {
            throw new Error(e2);
        }
    }
    
    static boolean hasAllocateArrayMethod() {
        return PlatformDependent0.ALLOCATE_ARRAY_METHOD != null;
    }
    
    static byte[] allocateUninitializedArray(final int size) {
        try {
            return (byte[])PlatformDependent0.ALLOCATE_ARRAY_METHOD.invoke(PlatformDependent0.INTERNAL_UNSAFE, Byte.TYPE, size);
        }
        catch (final IllegalAccessException e) {
            throw new Error(e);
        }
        catch (final InvocationTargetException e2) {
            throw new Error(e2);
        }
    }
    
    static ByteBuffer newDirectBuffer(final long address, final int capacity) {
        ObjectUtil.checkPositiveOrZero(capacity, "capacity");
        try {
            return (ByteBuffer)PlatformDependent0.DIRECT_BUFFER_CONSTRUCTOR.newInstance(address, capacity);
        }
        catch (final Throwable cause) {
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw new Error(cause);
        }
    }
    
    static long directBufferAddress(final ByteBuffer buffer) {
        return getLong(buffer, PlatformDependent0.ADDRESS_FIELD_OFFSET);
    }
    
    static long byteArrayBaseOffset() {
        return PlatformDependent0.BYTE_ARRAY_BASE_OFFSET;
    }
    
    static Object getObject(final Object object, final long fieldOffset) {
        return PlatformDependent0.UNSAFE.getObject(object, fieldOffset);
    }
    
    static int getInt(final Object object, final long fieldOffset) {
        return PlatformDependent0.UNSAFE.getInt(object, fieldOffset);
    }
    
    private static long getLong(final Object object, final long fieldOffset) {
        return PlatformDependent0.UNSAFE.getLong(object, fieldOffset);
    }
    
    static long objectFieldOffset(final Field field) {
        return PlatformDependent0.UNSAFE.objectFieldOffset(field);
    }
    
    static byte getByte(final long address) {
        return PlatformDependent0.UNSAFE.getByte(address);
    }
    
    static short getShort(final long address) {
        return PlatformDependent0.UNSAFE.getShort(address);
    }
    
    static int getInt(final long address) {
        return PlatformDependent0.UNSAFE.getInt(address);
    }
    
    static long getLong(final long address) {
        return PlatformDependent0.UNSAFE.getLong(address);
    }
    
    static byte getByte(final byte[] data, final int index) {
        return PlatformDependent0.UNSAFE.getByte(data, PlatformDependent0.BYTE_ARRAY_BASE_OFFSET + index);
    }
    
    static byte getByte(final byte[] data, final long index) {
        return PlatformDependent0.UNSAFE.getByte(data, PlatformDependent0.BYTE_ARRAY_BASE_OFFSET + index);
    }
    
    static short getShort(final byte[] data, final int index) {
        return PlatformDependent0.UNSAFE.getShort(data, PlatformDependent0.BYTE_ARRAY_BASE_OFFSET + index);
    }
    
    static int getInt(final byte[] data, final int index) {
        return PlatformDependent0.UNSAFE.getInt(data, PlatformDependent0.BYTE_ARRAY_BASE_OFFSET + index);
    }
    
    static int getInt(final int[] data, final long index) {
        return PlatformDependent0.UNSAFE.getInt(data, PlatformDependent0.INT_ARRAY_BASE_OFFSET + PlatformDependent0.INT_ARRAY_INDEX_SCALE * index);
    }
    
    static int getIntVolatile(final long address) {
        return PlatformDependent0.UNSAFE.getIntVolatile(null, address);
    }
    
    static void putIntOrdered(final long adddress, final int newValue) {
        PlatformDependent0.UNSAFE.putOrderedInt(null, adddress, newValue);
    }
    
    static long getLong(final byte[] data, final int index) {
        return PlatformDependent0.UNSAFE.getLong(data, PlatformDependent0.BYTE_ARRAY_BASE_OFFSET + index);
    }
    
    static long getLong(final long[] data, final long index) {
        return PlatformDependent0.UNSAFE.getLong(data, PlatformDependent0.LONG_ARRAY_BASE_OFFSET + PlatformDependent0.LONG_ARRAY_INDEX_SCALE * index);
    }
    
    static void putByte(final long address, final byte value) {
        PlatformDependent0.UNSAFE.putByte(address, value);
    }
    
    static void putShort(final long address, final short value) {
        PlatformDependent0.UNSAFE.putShort(address, value);
    }
    
    static void putInt(final long address, final int value) {
        PlatformDependent0.UNSAFE.putInt(address, value);
    }
    
    static void putLong(final long address, final long value) {
        PlatformDependent0.UNSAFE.putLong(address, value);
    }
    
    static void putByte(final byte[] data, final int index, final byte value) {
        PlatformDependent0.UNSAFE.putByte(data, PlatformDependent0.BYTE_ARRAY_BASE_OFFSET + index, value);
    }
    
    static void putByte(final Object data, final long offset, final byte value) {
        PlatformDependent0.UNSAFE.putByte(data, offset, value);
    }
    
    static void putShort(final byte[] data, final int index, final short value) {
        PlatformDependent0.UNSAFE.putShort(data, PlatformDependent0.BYTE_ARRAY_BASE_OFFSET + index, value);
    }
    
    static void putInt(final byte[] data, final int index, final int value) {
        PlatformDependent0.UNSAFE.putInt(data, PlatformDependent0.BYTE_ARRAY_BASE_OFFSET + index, value);
    }
    
    static void putLong(final byte[] data, final int index, final long value) {
        PlatformDependent0.UNSAFE.putLong(data, PlatformDependent0.BYTE_ARRAY_BASE_OFFSET + index, value);
    }
    
    static void putObject(final Object o, final long offset, final Object x) {
        PlatformDependent0.UNSAFE.putObject(o, offset, x);
    }
    
    static void copyMemory(final long srcAddr, final long dstAddr, final long length) {
        if (javaVersion() <= 8) {
            copyMemoryWithSafePointPolling(srcAddr, dstAddr, length);
        }
        else {
            PlatformDependent0.UNSAFE.copyMemory(srcAddr, dstAddr, length);
        }
    }
    
    private static void copyMemoryWithSafePointPolling(long srcAddr, long dstAddr, long length) {
        while (length > 0L) {
            final long size = Math.min(length, 1048576L);
            PlatformDependent0.UNSAFE.copyMemory(srcAddr, dstAddr, size);
            length -= size;
            srcAddr += size;
            dstAddr += size;
        }
    }
    
    static void copyMemory(final Object src, final long srcOffset, final Object dst, final long dstOffset, final long length) {
        if (javaVersion() <= 8) {
            copyMemoryWithSafePointPolling(src, srcOffset, dst, dstOffset, length);
        }
        else {
            PlatformDependent0.UNSAFE.copyMemory(src, srcOffset, dst, dstOffset, length);
        }
    }
    
    private static void copyMemoryWithSafePointPolling(final Object src, long srcOffset, final Object dst, long dstOffset, long length) {
        while (length > 0L) {
            final long size = Math.min(length, 1048576L);
            PlatformDependent0.UNSAFE.copyMemory(src, srcOffset, dst, dstOffset, size);
            length -= size;
            srcOffset += size;
            dstOffset += size;
        }
    }
    
    static void setMemory(final long address, final long bytes, final byte value) {
        PlatformDependent0.UNSAFE.setMemory(address, bytes, value);
    }
    
    static void setMemory(final Object o, final long offset, final long bytes, final byte value) {
        PlatformDependent0.UNSAFE.setMemory(o, offset, bytes, value);
    }
    
    static boolean equals(final byte[] bytes1, final int startPos1, final byte[] bytes2, final int startPos2, final int length) {
        int remainingBytes = length & 0x7;
        final long baseOffset1 = PlatformDependent0.BYTE_ARRAY_BASE_OFFSET + startPos1;
        final long diff = startPos2 - startPos1;
        if (length >= 8) {
            for (long end = baseOffset1 + remainingBytes, i = baseOffset1 - 8L + length; i >= end; i -= 8L) {
                if (PlatformDependent0.UNSAFE.getLong(bytes1, i) != PlatformDependent0.UNSAFE.getLong(bytes2, i + diff)) {
                    return false;
                }
            }
        }
        if (remainingBytes >= 4) {
            remainingBytes -= 4;
            final long pos = baseOffset1 + remainingBytes;
            if (PlatformDependent0.UNSAFE.getInt(bytes1, pos) != PlatformDependent0.UNSAFE.getInt(bytes2, pos + diff)) {
                return false;
            }
        }
        final long baseOffset2 = baseOffset1 + diff;
        if (remainingBytes >= 2) {
            return PlatformDependent0.UNSAFE.getChar(bytes1, baseOffset1) == PlatformDependent0.UNSAFE.getChar(bytes2, baseOffset2) && (remainingBytes == 2 || PlatformDependent0.UNSAFE.getByte(bytes1, baseOffset1 + 2L) == PlatformDependent0.UNSAFE.getByte(bytes2, baseOffset2 + 2L));
        }
        return remainingBytes == 0 || PlatformDependent0.UNSAFE.getByte(bytes1, baseOffset1) == PlatformDependent0.UNSAFE.getByte(bytes2, baseOffset2);
    }
    
    static int equalsConstantTime(final byte[] bytes1, final int startPos1, final byte[] bytes2, final int startPos2, final int length) {
        long result = 0L;
        long remainingBytes = length & 0x7;
        final long baseOffset1 = PlatformDependent0.BYTE_ARRAY_BASE_OFFSET + startPos1;
        final long end = baseOffset1 + remainingBytes;
        final long diff = startPos2 - startPos1;
        for (long i = baseOffset1 - 8L + length; i >= end; i -= 8L) {
            result |= (PlatformDependent0.UNSAFE.getLong(bytes1, i) ^ PlatformDependent0.UNSAFE.getLong(bytes2, i + diff));
        }
        if (remainingBytes >= 4L) {
            result |= (PlatformDependent0.UNSAFE.getInt(bytes1, baseOffset1) ^ PlatformDependent0.UNSAFE.getInt(bytes2, baseOffset1 + diff));
            remainingBytes -= 4L;
        }
        if (remainingBytes >= 2L) {
            final long pos = end - remainingBytes;
            result |= (PlatformDependent0.UNSAFE.getChar(bytes1, pos) ^ PlatformDependent0.UNSAFE.getChar(bytes2, pos + diff));
            remainingBytes -= 2L;
        }
        if (remainingBytes == 1L) {
            final long pos = end - 1L;
            result |= (PlatformDependent0.UNSAFE.getByte(bytes1, pos) ^ PlatformDependent0.UNSAFE.getByte(bytes2, pos + diff));
        }
        return ConstantTimeUtils.equalsConstantTime(result, 0L);
    }
    
    static boolean isZero(final byte[] bytes, final int startPos, final int length) {
        if (length <= 0) {
            return true;
        }
        final long baseOffset = PlatformDependent0.BYTE_ARRAY_BASE_OFFSET + startPos;
        int remainingBytes = length & 0x7;
        for (long end = baseOffset + remainingBytes, i = baseOffset - 8L + length; i >= end; i -= 8L) {
            if (PlatformDependent0.UNSAFE.getLong(bytes, i) != 0L) {
                return false;
            }
        }
        if (remainingBytes >= 4) {
            remainingBytes -= 4;
            if (PlatformDependent0.UNSAFE.getInt(bytes, baseOffset + remainingBytes) != 0) {
                return false;
            }
        }
        if (remainingBytes >= 2) {
            return PlatformDependent0.UNSAFE.getChar(bytes, baseOffset) == '\0' && (remainingBytes == 2 || bytes[startPos + 2] == 0);
        }
        return bytes[startPos] == 0;
    }
    
    static int hashCodeAscii(final byte[] bytes, final int startPos, final int length) {
        int hash = -1028477387;
        long baseOffset = PlatformDependent0.BYTE_ARRAY_BASE_OFFSET + startPos;
        final int remainingBytes = length & 0x7;
        for (long end = baseOffset + remainingBytes, i = baseOffset - 8L + length; i >= end; i -= 8L) {
            hash = hashCodeAsciiCompute(PlatformDependent0.UNSAFE.getLong(bytes, i), hash);
        }
        if (remainingBytes == 0) {
            return hash;
        }
        int hcConst = -862048943;
        if (remainingBytes != 2 & remainingBytes != 4 & remainingBytes != 6) {
            hash = hash * -862048943 + hashCodeAsciiSanitize(PlatformDependent0.UNSAFE.getByte(bytes, baseOffset));
            hcConst = 461845907;
            ++baseOffset;
        }
        if (remainingBytes != 1 & remainingBytes != 4 & remainingBytes != 5) {
            hash = hash * hcConst + hashCodeAsciiSanitize(PlatformDependent0.UNSAFE.getShort(bytes, baseOffset));
            hcConst = ((hcConst == -862048943) ? 461845907 : -862048943);
            baseOffset += 2L;
        }
        if (remainingBytes >= 4) {
            return hash * hcConst + hashCodeAsciiSanitize(PlatformDependent0.UNSAFE.getInt(bytes, baseOffset));
        }
        return hash;
    }
    
    static int hashCodeAsciiCompute(final long value, final int hash) {
        return hash * -862048943 + hashCodeAsciiSanitize((int)value) * 461845907 + (int)((value & 0x1F1F1F1F00000000L) >>> 32);
    }
    
    static int hashCodeAsciiSanitize(final int value) {
        return value & 0x1F1F1F1F;
    }
    
    static int hashCodeAsciiSanitize(final short value) {
        return value & 0x1F1F;
    }
    
    static int hashCodeAsciiSanitize(final byte value) {
        return value & 0x1F;
    }
    
    static ClassLoader getClassLoader(final Class<?> clazz) {
        if (System.getSecurityManager() == null) {
            return clazz.getClassLoader();
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return clazz.getClassLoader();
            }
        });
    }
    
    static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }
    
    static ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() == null) {
            return ClassLoader.getSystemClassLoader();
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return ClassLoader.getSystemClassLoader();
            }
        });
    }
    
    static int addressSize() {
        return PlatformDependent0.UNSAFE.addressSize();
    }
    
    static long allocateMemory(final long size) {
        return PlatformDependent0.UNSAFE.allocateMemory(size);
    }
    
    static void freeMemory(final long address) {
        PlatformDependent0.UNSAFE.freeMemory(address);
    }
    
    static long reallocateMemory(final long address, final long newSize) {
        return PlatformDependent0.UNSAFE.reallocateMemory(address, newSize);
    }
    
    static boolean isAndroid() {
        return PlatformDependent0.IS_ANDROID;
    }
    
    private static boolean isAndroid0() {
        final String vmName = SystemPropertyUtil.get("java.vm.name");
        final boolean isAndroid = "Dalvik".equals(vmName);
        if (isAndroid) {
            PlatformDependent0.logger.debug("Platform: Android");
        }
        return isAndroid;
    }
    
    private static boolean explicitTryReflectionSetAccessible0() {
        return SystemPropertyUtil.getBoolean("io.netty.tryReflectionSetAccessible", javaVersion() < 9);
    }
    
    static boolean isExplicitTryReflectionSetAccessible() {
        return PlatformDependent0.IS_EXPLICIT_TRY_REFLECTION_SET_ACCESSIBLE;
    }
    
    static int javaVersion() {
        return PlatformDependent0.JAVA_VERSION;
    }
    
    private static int javaVersion0() {
        int majorVersion;
        if (isAndroid0()) {
            majorVersion = 6;
        }
        else {
            majorVersion = majorVersionFromJavaSpecificationVersion();
        }
        PlatformDependent0.logger.debug("Java version: {}", (Object)majorVersion);
        return majorVersion;
    }
    
    static int majorVersionFromJavaSpecificationVersion() {
        return majorVersion(SystemPropertyUtil.get("java.specification.version", "1.6"));
    }
    
    static int majorVersion(final String javaSpecVersion) {
        final String[] components = javaSpecVersion.split("\\.");
        final int[] version = new int[components.length];
        for (int i = 0; i < components.length; ++i) {
            version[i] = Integer.parseInt(components[i]);
        }
        if (version[0] != 1) {
            return version[0];
        }
        assert version[1] >= 6;
        return version[1];
    }
    
    private PlatformDependent0() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(PlatformDependent0.class);
        EXPLICIT_NO_UNSAFE_CAUSE = explicitNoUnsafeCause0();
        JAVA_VERSION = javaVersion0();
        IS_ANDROID = isAndroid0();
        IS_EXPLICIT_TRY_REFLECTION_SET_ACCESSIBLE = explicitTryReflectionSetAccessible0();
        RUNNING_IN_NATIVE_IMAGE = SystemPropertyUtil.contains("org.graalvm.nativeimage.imagecode");
        Field addressField = null;
        Method allocateArrayMethod = null;
        Throwable unsafeUnavailabilityCause = null;
        Object internalUnsafe = null;
        ByteBuffer direct;
        Unsafe unsafe;
        if ((unsafeUnavailabilityCause = PlatformDependent0.EXPLICIT_NO_UNSAFE_CAUSE) != null) {
            direct = null;
            addressField = null;
            unsafe = null;
            internalUnsafe = null;
        }
        else {
            direct = ByteBuffer.allocateDirect(1);
            final Object maybeUnsafe = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    try {
                        final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                        final Throwable cause = ReflectionUtil.trySetAccessible(unsafeField, false);
                        if (cause != null) {
                            return cause;
                        }
                        return unsafeField.get(null);
                    }
                    catch (final NoSuchFieldException e) {
                        return e;
                    }
                    catch (final SecurityException e2) {
                        return e2;
                    }
                    catch (final IllegalAccessException e3) {
                        return e3;
                    }
                    catch (final NoClassDefFoundError e4) {
                        return e4;
                    }
                }
            });
            if (maybeUnsafe instanceof Throwable) {
                unsafe = null;
                unsafeUnavailabilityCause = (Throwable)maybeUnsafe;
                if (PlatformDependent0.logger.isTraceEnabled()) {
                    PlatformDependent0.logger.debug("sun.misc.Unsafe.theUnsafe: unavailable", (Throwable)maybeUnsafe);
                }
                else {
                    PlatformDependent0.logger.debug("sun.misc.Unsafe.theUnsafe: unavailable: {}", ((Throwable)maybeUnsafe).getMessage());
                }
            }
            else {
                unsafe = (Unsafe)maybeUnsafe;
                PlatformDependent0.logger.debug("sun.misc.Unsafe.theUnsafe: available");
            }
            if (unsafe != null) {
                final Unsafe finalUnsafe = unsafe;
                final Object maybeException = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        try {
                            finalUnsafe.getClass().getDeclaredMethod("copyMemory", Object.class, Long.TYPE, Object.class, Long.TYPE, Long.TYPE);
                            return null;
                        }
                        catch (final NoSuchMethodException e) {
                            return e;
                        }
                        catch (final SecurityException e2) {
                            return e2;
                        }
                    }
                });
                if (maybeException == null) {
                    PlatformDependent0.logger.debug("sun.misc.Unsafe.copyMemory: available");
                }
                else {
                    unsafe = null;
                    unsafeUnavailabilityCause = (Throwable)maybeException;
                    if (PlatformDependent0.logger.isTraceEnabled()) {
                        PlatformDependent0.logger.debug("sun.misc.Unsafe.copyMemory: unavailable", (Throwable)maybeException);
                    }
                    else {
                        PlatformDependent0.logger.debug("sun.misc.Unsafe.copyMemory: unavailable: {}", ((Throwable)maybeException).getMessage());
                    }
                }
            }
            if (unsafe != null) {
                final Unsafe finalUnsafe = unsafe;
                final Object maybeAddressField = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        try {
                            final Field field = Buffer.class.getDeclaredField("address");
                            final long offset = finalUnsafe.objectFieldOffset(field);
                            final long address = finalUnsafe.getLong(direct, offset);
                            if (address == 0L) {
                                return null;
                            }
                            return field;
                        }
                        catch (final NoSuchFieldException e) {
                            return e;
                        }
                        catch (final SecurityException e2) {
                            return e2;
                        }
                    }
                });
                if (maybeAddressField instanceof Field) {
                    addressField = (Field)maybeAddressField;
                    PlatformDependent0.logger.debug("java.nio.Buffer.address: available");
                }
                else {
                    unsafeUnavailabilityCause = (Throwable)maybeAddressField;
                    if (PlatformDependent0.logger.isTraceEnabled()) {
                        PlatformDependent0.logger.debug("java.nio.Buffer.address: unavailable", (Throwable)maybeAddressField);
                    }
                    else {
                        PlatformDependent0.logger.debug("java.nio.Buffer.address: unavailable: {}", ((Throwable)maybeAddressField).getMessage());
                    }
                    unsafe = null;
                }
            }
            if (unsafe != null) {
                final long byteArrayIndexScale = unsafe.arrayIndexScale(byte[].class);
                if (byteArrayIndexScale != 1L) {
                    PlatformDependent0.logger.debug("unsafe.arrayIndexScale is {} (expected: 1). Not using unsafe.", (Object)byteArrayIndexScale);
                    unsafeUnavailabilityCause = new UnsupportedOperationException("Unexpected unsafe.arrayIndexScale");
                    unsafe = null;
                }
            }
        }
        UNSAFE_UNAVAILABILITY_CAUSE = unsafeUnavailabilityCause;
        if ((UNSAFE = unsafe) == null) {
            ADDRESS_FIELD_OFFSET = -1L;
            BYTE_ARRAY_BASE_OFFSET = -1L;
            LONG_ARRAY_BASE_OFFSET = -1L;
            LONG_ARRAY_INDEX_SCALE = -1L;
            INT_ARRAY_BASE_OFFSET = -1L;
            INT_ARRAY_INDEX_SCALE = -1L;
            UNALIGNED = false;
            DIRECT_BUFFER_CONSTRUCTOR = null;
            ALLOCATE_ARRAY_METHOD = null;
        }
        else {
            long address = -1L;
            Constructor<?> directBufferConstructor;
            try {
                final Object maybeDirectBufferConstructor = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        try {
                            final Constructor<?> constructor = direct.getClass().getDeclaredConstructor(Long.TYPE, Integer.TYPE);
                            final Throwable cause = ReflectionUtil.trySetAccessible(constructor, true);
                            if (cause != null) {
                                return cause;
                            }
                            return constructor;
                        }
                        catch (final NoSuchMethodException e) {
                            return e;
                        }
                        catch (final SecurityException e2) {
                            return e2;
                        }
                    }
                });
                if (maybeDirectBufferConstructor instanceof Constructor) {
                    address = PlatformDependent0.UNSAFE.allocateMemory(1L);
                    try {
                        ((Constructor)maybeDirectBufferConstructor).newInstance(address, 1);
                        directBufferConstructor = (Constructor)maybeDirectBufferConstructor;
                        PlatformDependent0.logger.debug("direct buffer constructor: available");
                    }
                    catch (final InstantiationException e) {
                        directBufferConstructor = null;
                    }
                    catch (final IllegalAccessException e2) {
                        directBufferConstructor = null;
                    }
                    catch (final InvocationTargetException e3) {
                        directBufferConstructor = null;
                    }
                }
                else {
                    if (PlatformDependent0.logger.isTraceEnabled()) {
                        PlatformDependent0.logger.debug("direct buffer constructor: unavailable", (Throwable)maybeDirectBufferConstructor);
                    }
                    else {
                        PlatformDependent0.logger.debug("direct buffer constructor: unavailable: {}", ((Throwable)maybeDirectBufferConstructor).getMessage());
                    }
                    directBufferConstructor = null;
                }
            }
            finally {
                if (address != -1L) {
                    PlatformDependent0.UNSAFE.freeMemory(address);
                }
            }
            DIRECT_BUFFER_CONSTRUCTOR = directBufferConstructor;
            ADDRESS_FIELD_OFFSET = objectFieldOffset(addressField);
            BYTE_ARRAY_BASE_OFFSET = PlatformDependent0.UNSAFE.arrayBaseOffset(byte[].class);
            INT_ARRAY_BASE_OFFSET = PlatformDependent0.UNSAFE.arrayBaseOffset(int[].class);
            INT_ARRAY_INDEX_SCALE = PlatformDependent0.UNSAFE.arrayIndexScale(int[].class);
            LONG_ARRAY_BASE_OFFSET = PlatformDependent0.UNSAFE.arrayBaseOffset(long[].class);
            LONG_ARRAY_INDEX_SCALE = PlatformDependent0.UNSAFE.arrayIndexScale(long[].class);
            final Object maybeUnaligned = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    try {
                        final Class<?> bitsClass = Class.forName("java.nio.Bits", false, PlatformDependent0.getSystemClassLoader());
                        final int version = PlatformDependent0.javaVersion();
                        if (unsafeStaticFieldOffsetSupported() && version >= 9) {
                            final String fieldName = (version >= 11) ? "UNALIGNED" : "unaligned";
                            try {
                                final Field unalignedField = bitsClass.getDeclaredField(fieldName);
                                if (unalignedField.getType() == Boolean.TYPE) {
                                    final long offset = PlatformDependent0.UNSAFE.staticFieldOffset(unalignedField);
                                    final Object object = PlatformDependent0.UNSAFE.staticFieldBase(unalignedField);
                                    return PlatformDependent0.UNSAFE.getBoolean(object, offset);
                                }
                            }
                            catch (final NoSuchFieldException ex) {}
                        }
                        final Method unalignedMethod = bitsClass.getDeclaredMethod("unaligned", (Class<?>[])new Class[0]);
                        final Throwable cause = ReflectionUtil.trySetAccessible(unalignedMethod, true);
                        if (cause != null) {
                            return cause;
                        }
                        return unalignedMethod.invoke(null, new Object[0]);
                    }
                    catch (final NoSuchMethodException e) {
                        return e;
                    }
                    catch (final SecurityException e2) {
                        return e2;
                    }
                    catch (final IllegalAccessException e3) {
                        return e3;
                    }
                    catch (final ClassNotFoundException e4) {
                        return e4;
                    }
                    catch (final InvocationTargetException e5) {
                        return e5;
                    }
                }
            });
            boolean unaligned;
            if (maybeUnaligned instanceof Boolean) {
                unaligned = (boolean)maybeUnaligned;
                PlatformDependent0.logger.debug("java.nio.Bits.unaligned: available, {}", (Object)unaligned);
            }
            else {
                final String arch = SystemPropertyUtil.get("os.arch", "");
                unaligned = arch.matches("^(i[3-6]86|x86(_64)?|x64|amd64)$");
                final Throwable t = (Throwable)maybeUnaligned;
                if (PlatformDependent0.logger.isTraceEnabled()) {
                    PlatformDependent0.logger.debug("java.nio.Bits.unaligned: unavailable, {}", (Object)unaligned, t);
                }
                else {
                    PlatformDependent0.logger.debug("java.nio.Bits.unaligned: unavailable, {}, {}", (Object)unaligned, t.getMessage());
                }
            }
            UNALIGNED = unaligned;
            if (javaVersion() >= 9) {
                Object maybeException2 = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        try {
                            final Class<?> internalUnsafeClass = PlatformDependent0.getClassLoader(PlatformDependent0.class).loadClass("jdk.internal.misc.Unsafe");
                            final Method method = internalUnsafeClass.getDeclaredMethod("getUnsafe", (Class<?>[])new Class[0]);
                            return method.invoke(null, new Object[0]);
                        }
                        catch (final Throwable e) {
                            return e;
                        }
                    }
                });
                if (!(maybeException2 instanceof Throwable)) {
                    final Object finalInternalUnsafe;
                    internalUnsafe = (finalInternalUnsafe = maybeException2);
                    maybeException2 = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                        @Override
                        public Object run() {
                            try {
                                return finalInternalUnsafe.getClass().getDeclaredMethod("allocateUninitializedArray", Class.class, Integer.TYPE);
                            }
                            catch (final NoSuchMethodException e) {
                                return e;
                            }
                            catch (final SecurityException e2) {
                                return e2;
                            }
                        }
                    });
                    if (maybeException2 instanceof Method) {
                        try {
                            final Method m = (Method)maybeException2;
                            final byte[] bytes = (byte[])m.invoke(finalInternalUnsafe, Byte.TYPE, 8);
                            assert bytes.length == 8;
                            allocateArrayMethod = m;
                        }
                        catch (final IllegalAccessException e4) {
                            maybeException2 = e4;
                        }
                        catch (final InvocationTargetException e5) {
                            maybeException2 = e5;
                        }
                    }
                }
                if (maybeException2 instanceof Throwable) {
                    if (PlatformDependent0.logger.isTraceEnabled()) {
                        PlatformDependent0.logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable", (Throwable)maybeException2);
                    }
                    else {
                        PlatformDependent0.logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable: {}", ((Throwable)maybeException2).getMessage());
                    }
                }
                else {
                    PlatformDependent0.logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): available");
                }
            }
            else {
                PlatformDependent0.logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable prior to Java9");
            }
            ALLOCATE_ARRAY_METHOD = allocateArrayMethod;
        }
        if (javaVersion() > 9) {
            ALIGN_SLICE = AccessController.doPrivileged((PrivilegedAction<Method>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    try {
                        return ByteBuffer.class.getDeclaredMethod("alignedSlice", Integer.TYPE);
                    }
                    catch (final Exception e) {
                        return null;
                    }
                }
            });
        }
        else {
            ALIGN_SLICE = null;
        }
        INTERNAL_UNSAFE = internalUnsafe;
        PlatformDependent0.logger.debug("java.nio.DirectByteBuffer.<init>(long, int): {}", (PlatformDependent0.DIRECT_BUFFER_CONSTRUCTOR != null) ? "available" : "unavailable");
    }
}
