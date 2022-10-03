package io.netty.util.internal;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscUnboundedAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpscUnboundedArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscChunkedAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpscChunkedArrayQueue;
import java.security.AccessController;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import io.netty.util.CharsetUtil;
import java.io.FileInputStream;
import java.security.PrivilegedAction;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.nio.ByteOrder;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.regex.Matcher;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.Deque;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.SpscLinkedAtomicQueue;
import io.netty.util.internal.shaded.org.jctools.queues.SpscLinkedQueue;
import java.util.Queue;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Set;
import java.io.File;
import java.util.regex.Pattern;
import io.netty.util.internal.logging.InternalLogger;

public final class PlatformDependent
{
    private static final InternalLogger logger;
    private static final Pattern MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN;
    private static final boolean MAYBE_SUPER_USER;
    private static final boolean CAN_ENABLE_TCP_NODELAY_BY_DEFAULT;
    private static final Throwable UNSAFE_UNAVAILABILITY_CAUSE;
    private static final boolean DIRECT_BUFFER_PREFERRED;
    private static final long MAX_DIRECT_MEMORY;
    private static final int MPSC_CHUNK_SIZE = 1024;
    private static final int MIN_MAX_MPSC_CAPACITY = 2048;
    private static final int MAX_ALLOWED_MPSC_CAPACITY = 1073741824;
    private static final long BYTE_ARRAY_BASE_OFFSET;
    private static final File TMPDIR;
    private static final int BIT_MODE;
    private static final String NORMALIZED_ARCH;
    private static final String NORMALIZED_OS;
    private static final String[] ALLOWED_LINUX_OS_CLASSIFIERS;
    private static final Set<String> LINUX_OS_CLASSIFIERS;
    private static final boolean IS_WINDOWS;
    private static final boolean IS_OSX;
    private static final boolean IS_J9_JVM;
    private static final boolean IS_IVKVM_DOT_NET;
    private static final int ADDRESS_SIZE;
    private static final boolean USE_DIRECT_BUFFER_NO_CLEANER;
    private static final AtomicLong DIRECT_MEMORY_COUNTER;
    private static final long DIRECT_MEMORY_LIMIT;
    private static final ThreadLocalRandomProvider RANDOM_PROVIDER;
    private static final Cleaner CLEANER;
    private static final int UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD;
    private static final String[] OS_RELEASE_FILES;
    private static final String LINUX_ID_PREFIX = "ID=";
    private static final String LINUX_ID_LIKE_PREFIX = "ID_LIKE=";
    public static final boolean BIG_ENDIAN_NATIVE_ORDER;
    private static final Cleaner NOOP;
    
    public static long byteArrayBaseOffset() {
        return PlatformDependent.BYTE_ARRAY_BASE_OFFSET;
    }
    
    public static boolean hasDirectBufferNoCleanerConstructor() {
        return PlatformDependent0.hasDirectBufferNoCleanerConstructor();
    }
    
    public static byte[] allocateUninitializedArray(final int size) {
        return (PlatformDependent.UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD < 0 || PlatformDependent.UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD > size) ? new byte[size] : PlatformDependent0.allocateUninitializedArray(size);
    }
    
    public static boolean isAndroid() {
        return PlatformDependent0.isAndroid();
    }
    
    public static boolean isWindows() {
        return PlatformDependent.IS_WINDOWS;
    }
    
    public static boolean isOsx() {
        return PlatformDependent.IS_OSX;
    }
    
    public static boolean maybeSuperUser() {
        return PlatformDependent.MAYBE_SUPER_USER;
    }
    
    public static int javaVersion() {
        return PlatformDependent0.javaVersion();
    }
    
    public static boolean canEnableTcpNoDelayByDefault() {
        return PlatformDependent.CAN_ENABLE_TCP_NODELAY_BY_DEFAULT;
    }
    
    public static boolean hasUnsafe() {
        return PlatformDependent.UNSAFE_UNAVAILABILITY_CAUSE == null;
    }
    
    public static Throwable getUnsafeUnavailabilityCause() {
        return PlatformDependent.UNSAFE_UNAVAILABILITY_CAUSE;
    }
    
    public static boolean isUnaligned() {
        return PlatformDependent0.isUnaligned();
    }
    
    public static boolean directBufferPreferred() {
        return PlatformDependent.DIRECT_BUFFER_PREFERRED;
    }
    
    public static long maxDirectMemory() {
        return PlatformDependent.DIRECT_MEMORY_LIMIT;
    }
    
    public static long usedDirectMemory() {
        return (PlatformDependent.DIRECT_MEMORY_COUNTER != null) ? PlatformDependent.DIRECT_MEMORY_COUNTER.get() : -1L;
    }
    
    public static File tmpdir() {
        return PlatformDependent.TMPDIR;
    }
    
    public static int bitMode() {
        return PlatformDependent.BIT_MODE;
    }
    
    public static int addressSize() {
        return PlatformDependent.ADDRESS_SIZE;
    }
    
    public static long allocateMemory(final long size) {
        return PlatformDependent0.allocateMemory(size);
    }
    
    public static void freeMemory(final long address) {
        PlatformDependent0.freeMemory(address);
    }
    
    public static long reallocateMemory(final long address, final long newSize) {
        return PlatformDependent0.reallocateMemory(address, newSize);
    }
    
    public static void throwException(final Throwable t) {
        if (hasUnsafe()) {
            PlatformDependent0.throwException(t);
        }
        else {
            throwException0(t);
        }
    }
    
    private static <E extends Throwable> void throwException0(final Throwable t) throws E, Throwable {
        throw t;
    }
    
    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<K, V>();
    }
    
    public static LongCounter newLongCounter() {
        if (javaVersion() >= 8) {
            return new LongAdderCounter();
        }
        return new AtomicLongCounter();
    }
    
    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(final int initialCapacity) {
        return new ConcurrentHashMap<K, V>(initialCapacity);
    }
    
    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(final int initialCapacity, final float loadFactor) {
        return new ConcurrentHashMap<K, V>(initialCapacity, loadFactor);
    }
    
    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        return new ConcurrentHashMap<K, V>(initialCapacity, loadFactor, concurrencyLevel);
    }
    
    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(final Map<? extends K, ? extends V> map) {
        return new ConcurrentHashMap<K, V>(map);
    }
    
    public static void freeDirectBuffer(final ByteBuffer buffer) {
        PlatformDependent.CLEANER.freeDirectBuffer(buffer);
    }
    
    public static long directBufferAddress(final ByteBuffer buffer) {
        return PlatformDependent0.directBufferAddress(buffer);
    }
    
    public static ByteBuffer directBuffer(final long memoryAddress, final int size) {
        if (PlatformDependent0.hasDirectBufferNoCleanerConstructor()) {
            return PlatformDependent0.newDirectBuffer(memoryAddress, size);
        }
        throw new UnsupportedOperationException("sun.misc.Unsafe or java.nio.DirectByteBuffer.<init>(long, int) not available");
    }
    
    public static Object getObject(final Object object, final long fieldOffset) {
        return PlatformDependent0.getObject(object, fieldOffset);
    }
    
    public static int getInt(final Object object, final long fieldOffset) {
        return PlatformDependent0.getInt(object, fieldOffset);
    }
    
    public static int getIntVolatile(final long address) {
        return PlatformDependent0.getIntVolatile(address);
    }
    
    public static void putIntOrdered(final long adddress, final int newValue) {
        PlatformDependent0.putIntOrdered(adddress, newValue);
    }
    
    public static byte getByte(final long address) {
        return PlatformDependent0.getByte(address);
    }
    
    public static short getShort(final long address) {
        return PlatformDependent0.getShort(address);
    }
    
    public static int getInt(final long address) {
        return PlatformDependent0.getInt(address);
    }
    
    public static long getLong(final long address) {
        return PlatformDependent0.getLong(address);
    }
    
    public static byte getByte(final byte[] data, final int index) {
        return PlatformDependent0.getByte(data, index);
    }
    
    public static byte getByte(final byte[] data, final long index) {
        return PlatformDependent0.getByte(data, index);
    }
    
    public static short getShort(final byte[] data, final int index) {
        return PlatformDependent0.getShort(data, index);
    }
    
    public static int getInt(final byte[] data, final int index) {
        return PlatformDependent0.getInt(data, index);
    }
    
    public static int getInt(final int[] data, final long index) {
        return PlatformDependent0.getInt(data, index);
    }
    
    public static long getLong(final byte[] data, final int index) {
        return PlatformDependent0.getLong(data, index);
    }
    
    public static long getLong(final long[] data, final long index) {
        return PlatformDependent0.getLong(data, index);
    }
    
    private static long getLongSafe(final byte[] bytes, final int offset) {
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            return (long)bytes[offset] << 56 | ((long)bytes[offset + 1] & 0xFFL) << 48 | ((long)bytes[offset + 2] & 0xFFL) << 40 | ((long)bytes[offset + 3] & 0xFFL) << 32 | ((long)bytes[offset + 4] & 0xFFL) << 24 | ((long)bytes[offset + 5] & 0xFFL) << 16 | ((long)bytes[offset + 6] & 0xFFL) << 8 | ((long)bytes[offset + 7] & 0xFFL);
        }
        return ((long)bytes[offset] & 0xFFL) | ((long)bytes[offset + 1] & 0xFFL) << 8 | ((long)bytes[offset + 2] & 0xFFL) << 16 | ((long)bytes[offset + 3] & 0xFFL) << 24 | ((long)bytes[offset + 4] & 0xFFL) << 32 | ((long)bytes[offset + 5] & 0xFFL) << 40 | ((long)bytes[offset + 6] & 0xFFL) << 48 | (long)bytes[offset + 7] << 56;
    }
    
    private static int getIntSafe(final byte[] bytes, final int offset) {
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            return bytes[offset] << 24 | (bytes[offset + 1] & 0xFF) << 16 | (bytes[offset + 2] & 0xFF) << 8 | (bytes[offset + 3] & 0xFF);
        }
        return (bytes[offset] & 0xFF) | (bytes[offset + 1] & 0xFF) << 8 | (bytes[offset + 2] & 0xFF) << 16 | bytes[offset + 3] << 24;
    }
    
    private static short getShortSafe(final byte[] bytes, final int offset) {
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            return (short)(bytes[offset] << 8 | (bytes[offset + 1] & 0xFF));
        }
        return (short)((bytes[offset] & 0xFF) | bytes[offset + 1] << 8);
    }
    
    private static int hashCodeAsciiCompute(final CharSequence value, final int offset, final int hash) {
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            return hash * -862048943 + hashCodeAsciiSanitizeInt(value, offset + 4) * 461845907 + hashCodeAsciiSanitizeInt(value, offset);
        }
        return hash * -862048943 + hashCodeAsciiSanitizeInt(value, offset) * 461845907 + hashCodeAsciiSanitizeInt(value, offset + 4);
    }
    
    private static int hashCodeAsciiSanitizeInt(final CharSequence value, final int offset) {
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            return (value.charAt(offset + 3) & '\u001f') | (value.charAt(offset + 2) & '\u001f') << 8 | (value.charAt(offset + 1) & '\u001f') << 16 | (value.charAt(offset) & '\u001f') << 24;
        }
        return (value.charAt(offset + 3) & '\u001f') << 24 | (value.charAt(offset + 2) & '\u001f') << 16 | (value.charAt(offset + 1) & '\u001f') << 8 | (value.charAt(offset) & '\u001f');
    }
    
    private static int hashCodeAsciiSanitizeShort(final CharSequence value, final int offset) {
        if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
            return (value.charAt(offset + 1) & '\u001f') | (value.charAt(offset) & '\u001f') << 8;
        }
        return (value.charAt(offset + 1) & '\u001f') << 8 | (value.charAt(offset) & '\u001f');
    }
    
    private static int hashCodeAsciiSanitizeByte(final char value) {
        return value & '\u001f';
    }
    
    public static void putByte(final long address, final byte value) {
        PlatformDependent0.putByte(address, value);
    }
    
    public static void putShort(final long address, final short value) {
        PlatformDependent0.putShort(address, value);
    }
    
    public static void putInt(final long address, final int value) {
        PlatformDependent0.putInt(address, value);
    }
    
    public static void putLong(final long address, final long value) {
        PlatformDependent0.putLong(address, value);
    }
    
    public static void putByte(final byte[] data, final int index, final byte value) {
        PlatformDependent0.putByte(data, index, value);
    }
    
    public static void putByte(final Object data, final long offset, final byte value) {
        PlatformDependent0.putByte(data, offset, value);
    }
    
    public static void putShort(final byte[] data, final int index, final short value) {
        PlatformDependent0.putShort(data, index, value);
    }
    
    public static void putInt(final byte[] data, final int index, final int value) {
        PlatformDependent0.putInt(data, index, value);
    }
    
    public static void putLong(final byte[] data, final int index, final long value) {
        PlatformDependent0.putLong(data, index, value);
    }
    
    public static void putObject(final Object o, final long offset, final Object x) {
        PlatformDependent0.putObject(o, offset, x);
    }
    
    public static long objectFieldOffset(final Field field) {
        return PlatformDependent0.objectFieldOffset(field);
    }
    
    public static void copyMemory(final long srcAddr, final long dstAddr, final long length) {
        PlatformDependent0.copyMemory(srcAddr, dstAddr, length);
    }
    
    public static void copyMemory(final byte[] src, final int srcIndex, final long dstAddr, final long length) {
        PlatformDependent0.copyMemory(src, PlatformDependent.BYTE_ARRAY_BASE_OFFSET + srcIndex, null, dstAddr, length);
    }
    
    public static void copyMemory(final byte[] src, final int srcIndex, final byte[] dst, final int dstIndex, final long length) {
        PlatformDependent0.copyMemory(src, PlatformDependent.BYTE_ARRAY_BASE_OFFSET + srcIndex, dst, PlatformDependent.BYTE_ARRAY_BASE_OFFSET + dstIndex, length);
    }
    
    public static void copyMemory(final long srcAddr, final byte[] dst, final int dstIndex, final long length) {
        PlatformDependent0.copyMemory(null, srcAddr, dst, PlatformDependent.BYTE_ARRAY_BASE_OFFSET + dstIndex, length);
    }
    
    public static void setMemory(final byte[] dst, final int dstIndex, final long bytes, final byte value) {
        PlatformDependent0.setMemory(dst, PlatformDependent.BYTE_ARRAY_BASE_OFFSET + dstIndex, bytes, value);
    }
    
    public static void setMemory(final long address, final long bytes, final byte value) {
        PlatformDependent0.setMemory(address, bytes, value);
    }
    
    public static ByteBuffer allocateDirectNoCleaner(final int capacity) {
        assert PlatformDependent.USE_DIRECT_BUFFER_NO_CLEANER;
        incrementMemoryCounter(capacity);
        try {
            return PlatformDependent0.allocateDirectNoCleaner(capacity);
        }
        catch (final Throwable e) {
            decrementMemoryCounter(capacity);
            throwException(e);
            return null;
        }
    }
    
    public static ByteBuffer reallocateDirectNoCleaner(final ByteBuffer buffer, final int capacity) {
        assert PlatformDependent.USE_DIRECT_BUFFER_NO_CLEANER;
        final int len = capacity - buffer.capacity();
        incrementMemoryCounter(len);
        try {
            return PlatformDependent0.reallocateDirectNoCleaner(buffer, capacity);
        }
        catch (final Throwable e) {
            decrementMemoryCounter(len);
            throwException(e);
            return null;
        }
    }
    
    public static void freeDirectNoCleaner(final ByteBuffer buffer) {
        assert PlatformDependent.USE_DIRECT_BUFFER_NO_CLEANER;
        final int capacity = buffer.capacity();
        PlatformDependent0.freeMemory(PlatformDependent0.directBufferAddress(buffer));
        decrementMemoryCounter(capacity);
    }
    
    public static boolean hasAlignDirectByteBuffer() {
        return hasUnsafe() || PlatformDependent0.hasAlignSliceMethod();
    }
    
    public static ByteBuffer alignDirectBuffer(final ByteBuffer buffer, final int alignment) {
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("Cannot get aligned slice of non-direct byte buffer.");
        }
        if (PlatformDependent0.hasAlignSliceMethod()) {
            return PlatformDependent0.alignSlice(buffer, alignment);
        }
        if (hasUnsafe()) {
            final long address = directBufferAddress(buffer);
            final long aligned = align(address, alignment);
            buffer.position((int)(aligned - address));
            return buffer.slice();
        }
        throw new UnsupportedOperationException("Cannot align direct buffer. Needs either Unsafe or ByteBuffer.alignSlice method available.");
    }
    
    public static long align(final long value, final int alignment) {
        return Pow2.align(value, alignment);
    }
    
    private static void incrementMemoryCounter(final int capacity) {
        if (PlatformDependent.DIRECT_MEMORY_COUNTER != null) {
            final long newUsedMemory = PlatformDependent.DIRECT_MEMORY_COUNTER.addAndGet(capacity);
            if (newUsedMemory > PlatformDependent.DIRECT_MEMORY_LIMIT) {
                PlatformDependent.DIRECT_MEMORY_COUNTER.addAndGet(-capacity);
                throw new OutOfDirectMemoryError("failed to allocate " + capacity + " byte(s) of direct memory (used: " + (newUsedMemory - capacity) + ", max: " + PlatformDependent.DIRECT_MEMORY_LIMIT + ')');
            }
        }
    }
    
    private static void decrementMemoryCounter(final int capacity) {
        if (PlatformDependent.DIRECT_MEMORY_COUNTER != null) {
            final long usedMemory = PlatformDependent.DIRECT_MEMORY_COUNTER.addAndGet(-capacity);
            assert usedMemory >= 0L;
        }
    }
    
    public static boolean useDirectBufferNoCleaner() {
        return PlatformDependent.USE_DIRECT_BUFFER_NO_CLEANER;
    }
    
    public static boolean equals(final byte[] bytes1, final int startPos1, final byte[] bytes2, final int startPos2, final int length) {
        return (!hasUnsafe() || !PlatformDependent0.unalignedAccess()) ? equalsSafe(bytes1, startPos1, bytes2, startPos2, length) : PlatformDependent0.equals(bytes1, startPos1, bytes2, startPos2, length);
    }
    
    public static boolean isZero(final byte[] bytes, final int startPos, final int length) {
        return (!hasUnsafe() || !PlatformDependent0.unalignedAccess()) ? isZeroSafe(bytes, startPos, length) : PlatformDependent0.isZero(bytes, startPos, length);
    }
    
    public static int equalsConstantTime(final byte[] bytes1, final int startPos1, final byte[] bytes2, final int startPos2, final int length) {
        return (!hasUnsafe() || !PlatformDependent0.unalignedAccess()) ? ConstantTimeUtils.equalsConstantTime(bytes1, startPos1, bytes2, startPos2, length) : PlatformDependent0.equalsConstantTime(bytes1, startPos1, bytes2, startPos2, length);
    }
    
    public static int hashCodeAscii(final byte[] bytes, final int startPos, final int length) {
        return (!hasUnsafe() || !PlatformDependent0.unalignedAccess()) ? hashCodeAsciiSafe(bytes, startPos, length) : PlatformDependent0.hashCodeAscii(bytes, startPos, length);
    }
    
    public static int hashCodeAscii(final CharSequence bytes) {
        final int length = bytes.length();
        final int remainingBytes = length & 0x7;
        int hash = -1028477387;
        if (length >= 32) {
            for (int i = length - 8; i >= remainingBytes; i -= 8) {
                hash = hashCodeAsciiCompute(bytes, i, hash);
            }
        }
        else if (length >= 8) {
            hash = hashCodeAsciiCompute(bytes, length - 8, hash);
            if (length >= 16) {
                hash = hashCodeAsciiCompute(bytes, length - 16, hash);
                if (length >= 24) {
                    hash = hashCodeAsciiCompute(bytes, length - 24, hash);
                }
            }
        }
        if (remainingBytes == 0) {
            return hash;
        }
        int offset = 0;
        if (remainingBytes != 2 & remainingBytes != 4 & remainingBytes != 6) {
            hash = hash * -862048943 + hashCodeAsciiSanitizeByte(bytes.charAt(0));
            offset = 1;
        }
        if (remainingBytes != 1 & remainingBytes != 4 & remainingBytes != 5) {
            hash = hash * ((offset == 0) ? -862048943 : 461845907) + PlatformDependent0.hashCodeAsciiSanitize(hashCodeAsciiSanitizeShort(bytes, offset));
            offset += 2;
        }
        if (remainingBytes >= 4) {
            return hash * ((offset == 0 | offset == 3) ? -862048943 : 461845907) + hashCodeAsciiSanitizeInt(bytes, offset);
        }
        return hash;
    }
    
    public static <T> Queue<T> newMpscQueue() {
        return Mpsc.newMpscQueue();
    }
    
    public static <T> Queue<T> newMpscQueue(final int maxCapacity) {
        return Mpsc.newMpscQueue(maxCapacity);
    }
    
    public static <T> Queue<T> newSpscQueue() {
        return (Queue<T>)(hasUnsafe() ? new SpscLinkedQueue<T>() : new SpscLinkedAtomicQueue<T>());
    }
    
    public static <T> Queue<T> newFixedMpscQueue(final int capacity) {
        return (Queue<T>)(hasUnsafe() ? new MpscArrayQueue<Object>(capacity) : new MpscAtomicArrayQueue<Object>(capacity));
    }
    
    public static ClassLoader getClassLoader(final Class<?> clazz) {
        return PlatformDependent0.getClassLoader(clazz);
    }
    
    public static ClassLoader getContextClassLoader() {
        return PlatformDependent0.getContextClassLoader();
    }
    
    public static ClassLoader getSystemClassLoader() {
        return PlatformDependent0.getSystemClassLoader();
    }
    
    @SuppressJava6Requirement(reason = "Usage guarded by java version check")
    public static <C> Deque<C> newConcurrentDeque() {
        if (javaVersion() < 7) {
            return new LinkedBlockingDeque<C>();
        }
        return new ConcurrentLinkedDeque<C>();
    }
    
    public static Random threadLocalRandom() {
        return PlatformDependent.RANDOM_PROVIDER.current();
    }
    
    private static boolean isWindows0() {
        final boolean windows = "windows".equals(PlatformDependent.NORMALIZED_OS);
        if (windows) {
            PlatformDependent.logger.debug("Platform: Windows");
        }
        return windows;
    }
    
    private static boolean isOsx0() {
        final boolean osx = "osx".equals(PlatformDependent.NORMALIZED_OS);
        if (osx) {
            PlatformDependent.logger.debug("Platform: MacOS");
        }
        return osx;
    }
    
    private static boolean maybeSuperUser0() {
        final String username = SystemPropertyUtil.get("user.name");
        if (isWindows()) {
            return "Administrator".equals(username);
        }
        return "root".equals(username) || "toor".equals(username);
    }
    
    private static Throwable unsafeUnavailabilityCause0() {
        if (isAndroid()) {
            PlatformDependent.logger.debug("sun.misc.Unsafe: unavailable (Android)");
            return new UnsupportedOperationException("sun.misc.Unsafe: unavailable (Android)");
        }
        if (isIkvmDotNet()) {
            PlatformDependent.logger.debug("sun.misc.Unsafe: unavailable (IKVM.NET)");
            return new UnsupportedOperationException("sun.misc.Unsafe: unavailable (IKVM.NET)");
        }
        final Throwable cause = PlatformDependent0.getUnsafeUnavailabilityCause();
        if (cause != null) {
            return cause;
        }
        try {
            final boolean hasUnsafe = PlatformDependent0.hasUnsafe();
            PlatformDependent.logger.debug("sun.misc.Unsafe: {}", hasUnsafe ? "available" : "unavailable");
            return hasUnsafe ? null : PlatformDependent0.getUnsafeUnavailabilityCause();
        }
        catch (final Throwable t) {
            PlatformDependent.logger.trace("Could not determine if Unsafe is available", t);
            return new UnsupportedOperationException("Could not determine if Unsafe is available", t);
        }
    }
    
    public static boolean isJ9Jvm() {
        return PlatformDependent.IS_J9_JVM;
    }
    
    private static boolean isJ9Jvm0() {
        final String vmName = SystemPropertyUtil.get("java.vm.name", "").toLowerCase();
        return vmName.startsWith("ibm j9") || vmName.startsWith("eclipse openj9");
    }
    
    public static boolean isIkvmDotNet() {
        return PlatformDependent.IS_IVKVM_DOT_NET;
    }
    
    private static boolean isIkvmDotNet0() {
        final String vmName = SystemPropertyUtil.get("java.vm.name", "").toUpperCase(Locale.US);
        return vmName.equals("IKVM.NET");
    }
    
    private static long maxDirectMemory0() {
        long maxDirectMemory = 0L;
        ClassLoader systemClassLoader = null;
        try {
            systemClassLoader = getSystemClassLoader();
            final String vmName = SystemPropertyUtil.get("java.vm.name", "").toLowerCase();
            if (!vmName.startsWith("ibm j9") && !vmName.startsWith("eclipse openj9")) {
                final Class<?> vmClass = Class.forName("sun.misc.VM", true, systemClassLoader);
                final Method m = vmClass.getDeclaredMethod("maxDirectMemory", (Class<?>[])new Class[0]);
                maxDirectMemory = ((Number)m.invoke(null, new Object[0])).longValue();
            }
        }
        catch (final Throwable t) {}
        if (maxDirectMemory > 0L) {
            return maxDirectMemory;
        }
        try {
            final Class<?> mgmtFactoryClass = Class.forName("java.lang.management.ManagementFactory", true, systemClassLoader);
            final Class<?> runtimeClass = Class.forName("java.lang.management.RuntimeMXBean", true, systemClassLoader);
            final Object runtime = mgmtFactoryClass.getDeclaredMethod("getRuntimeMXBean", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
            final List<String> vmArgs = (List<String>)runtimeClass.getDeclaredMethod("getInputArguments", (Class<?>[])new Class[0]).invoke(runtime, new Object[0]);
            int i = vmArgs.size() - 1;
        Label_0320:
            while (i >= 0) {
                final Matcher j = PlatformDependent.MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN.matcher(vmArgs.get(i));
                if (!j.matches()) {
                    --i;
                }
                else {
                    maxDirectMemory = Long.parseLong(j.group(1));
                    switch (j.group(2).charAt(0)) {
                        case 'K':
                        case 'k': {
                            maxDirectMemory *= 1024L;
                            break Label_0320;
                        }
                        case 'M':
                        case 'm': {
                            maxDirectMemory *= 1048576L;
                            break Label_0320;
                        }
                        case 'G':
                        case 'g': {
                            maxDirectMemory *= 1073741824L;
                            break Label_0320;
                        }
                        default: {
                            break Label_0320;
                        }
                    }
                }
            }
        }
        catch (final Throwable t2) {}
        if (maxDirectMemory <= 0L) {
            maxDirectMemory = Runtime.getRuntime().maxMemory();
            PlatformDependent.logger.debug("maxDirectMemory: {} bytes (maybe)", (Object)maxDirectMemory);
        }
        else {
            PlatformDependent.logger.debug("maxDirectMemory: {} bytes", (Object)maxDirectMemory);
        }
        return maxDirectMemory;
    }
    
    private static File tmpdir0() {
        try {
            File f = toDirectory(SystemPropertyUtil.get("io.netty.tmpdir"));
            if (f != null) {
                PlatformDependent.logger.debug("-Dio.netty.tmpdir: {}", f);
                return f;
            }
            f = toDirectory(SystemPropertyUtil.get("java.io.tmpdir"));
            if (f != null) {
                PlatformDependent.logger.debug("-Dio.netty.tmpdir: {} (java.io.tmpdir)", f);
                return f;
            }
            if (isWindows()) {
                f = toDirectory(System.getenv("TEMP"));
                if (f != null) {
                    PlatformDependent.logger.debug("-Dio.netty.tmpdir: {} (%TEMP%)", f);
                    return f;
                }
                final String userprofile = System.getenv("USERPROFILE");
                if (userprofile != null) {
                    f = toDirectory(userprofile + "\\AppData\\Local\\Temp");
                    if (f != null) {
                        PlatformDependent.logger.debug("-Dio.netty.tmpdir: {} (%USERPROFILE%\\AppData\\Local\\Temp)", f);
                        return f;
                    }
                    f = toDirectory(userprofile + "\\Local Settings\\Temp");
                    if (f != null) {
                        PlatformDependent.logger.debug("-Dio.netty.tmpdir: {} (%USERPROFILE%\\Local Settings\\Temp)", f);
                        return f;
                    }
                }
            }
            else {
                f = toDirectory(System.getenv("TMPDIR"));
                if (f != null) {
                    PlatformDependent.logger.debug("-Dio.netty.tmpdir: {} ($TMPDIR)", f);
                    return f;
                }
            }
        }
        catch (final Throwable t) {}
        File f;
        if (isWindows()) {
            f = new File("C:\\Windows\\Temp");
        }
        else {
            f = new File("/tmp");
        }
        PlatformDependent.logger.warn("Failed to get the temporary directory; falling back to: {}", f);
        return f;
    }
    
    private static File toDirectory(final String path) {
        if (path == null) {
            return null;
        }
        final File f = new File(path);
        f.mkdirs();
        if (!f.isDirectory()) {
            return null;
        }
        try {
            return f.getAbsoluteFile();
        }
        catch (final Exception ignored) {
            return f;
        }
    }
    
    private static int bitMode0() {
        int bitMode = SystemPropertyUtil.getInt("io.netty.bitMode", 0);
        if (bitMode > 0) {
            PlatformDependent.logger.debug("-Dio.netty.bitMode: {}", (Object)bitMode);
            return bitMode;
        }
        bitMode = SystemPropertyUtil.getInt("sun.arch.data.model", 0);
        if (bitMode > 0) {
            PlatformDependent.logger.debug("-Dio.netty.bitMode: {} (sun.arch.data.model)", (Object)bitMode);
            return bitMode;
        }
        bitMode = SystemPropertyUtil.getInt("com.ibm.vm.bitmode", 0);
        if (bitMode > 0) {
            PlatformDependent.logger.debug("-Dio.netty.bitMode: {} (com.ibm.vm.bitmode)", (Object)bitMode);
            return bitMode;
        }
        final String arch = SystemPropertyUtil.get("os.arch", "").toLowerCase(Locale.US).trim();
        if ("amd64".equals(arch) || "x86_64".equals(arch)) {
            bitMode = 64;
        }
        else if ("i386".equals(arch) || "i486".equals(arch) || "i586".equals(arch) || "i686".equals(arch)) {
            bitMode = 32;
        }
        if (bitMode > 0) {
            PlatformDependent.logger.debug("-Dio.netty.bitMode: {} (os.arch: {})", (Object)bitMode, arch);
        }
        final String vm = SystemPropertyUtil.get("java.vm.name", "").toLowerCase(Locale.US);
        final Pattern bitPattern = Pattern.compile("([1-9][0-9]+)-?bit");
        final Matcher m = bitPattern.matcher(vm);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 64;
    }
    
    private static int addressSize0() {
        if (!hasUnsafe()) {
            return -1;
        }
        return PlatformDependent0.addressSize();
    }
    
    private static long byteArrayBaseOffset0() {
        if (!hasUnsafe()) {
            return -1L;
        }
        return PlatformDependent0.byteArrayBaseOffset();
    }
    
    private static boolean equalsSafe(final byte[] bytes1, int startPos1, final byte[] bytes2, int startPos2, final int length) {
        for (int end = startPos1 + length; startPos1 < end; ++startPos1, ++startPos2) {
            if (bytes1[startPos1] != bytes2[startPos2]) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isZeroSafe(final byte[] bytes, int startPos, final int length) {
        for (int end = startPos + length; startPos < end; ++startPos) {
            if (bytes[startPos] != 0) {
                return false;
            }
        }
        return true;
    }
    
    static int hashCodeAsciiSafe(final byte[] bytes, final int startPos, final int length) {
        int hash = -1028477387;
        final int remainingBytes = length & 0x7;
        for (int end = startPos + remainingBytes, i = startPos - 8 + length; i >= end; i -= 8) {
            hash = PlatformDependent0.hashCodeAsciiCompute(getLongSafe(bytes, i), hash);
        }
        switch (remainingBytes) {
            case 7: {
                return ((hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(bytes[startPos])) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(getShortSafe(bytes, startPos + 1))) * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(getIntSafe(bytes, startPos + 3));
            }
            case 6: {
                return (hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(getShortSafe(bytes, startPos))) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(getIntSafe(bytes, startPos + 2));
            }
            case 5: {
                return (hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(bytes[startPos])) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(getIntSafe(bytes, startPos + 1));
            }
            case 4: {
                return hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(getIntSafe(bytes, startPos));
            }
            case 3: {
                return (hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(bytes[startPos])) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(getShortSafe(bytes, startPos + 1));
            }
            case 2: {
                return hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(getShortSafe(bytes, startPos));
            }
            case 1: {
                return hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(bytes[startPos]);
            }
            default: {
                return hash;
            }
        }
    }
    
    public static String normalizedArch() {
        return PlatformDependent.NORMALIZED_ARCH;
    }
    
    public static String normalizedOs() {
        return PlatformDependent.NORMALIZED_OS;
    }
    
    public static Set<String> normalizedLinuxClassifiers() {
        return PlatformDependent.LINUX_OS_CLASSIFIERS;
    }
    
    @SuppressJava6Requirement(reason = "Guarded by version check")
    public static File createTempFile(final String prefix, final String suffix, final File directory) throws IOException {
        if (javaVersion() >= 7) {
            if (directory == null) {
                return Files.createTempFile(prefix, suffix, (FileAttribute<?>[])new FileAttribute[0]).toFile();
            }
            return Files.createTempFile(directory.toPath(), prefix, suffix, (FileAttribute<?>[])new FileAttribute[0]).toFile();
        }
        else {
            if (directory == null) {
                return File.createTempFile(prefix, suffix);
            }
            final File file = File.createTempFile(prefix, suffix, directory);
            file.setReadable(false, false);
            file.setReadable(true, true);
            return file;
        }
    }
    
    private static void addClassifier(final Set<String> allowed, final Set<String> dest, final String... maybeClassifiers) {
        for (final String id : maybeClassifiers) {
            if (allowed.contains(id)) {
                dest.add(id);
            }
        }
    }
    
    private static String normalizeOsReleaseVariableValue(final String value) {
        return value.trim().replaceAll("[\"']", "");
    }
    
    private static String normalize(final String value) {
        return value.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");
    }
    
    private static String normalizeArch(String value) {
        value = normalize(value);
        if (value.matches("^(x8664|amd64|ia32e|em64t|x64)$")) {
            return "x86_64";
        }
        if (value.matches("^(x8632|x86|i[3-6]86|ia32|x32)$")) {
            return "x86_32";
        }
        if (value.matches("^(ia64|itanium64)$")) {
            return "itanium_64";
        }
        if (value.matches("^(sparc|sparc32)$")) {
            return "sparc_32";
        }
        if (value.matches("^(sparcv9|sparc64)$")) {
            return "sparc_64";
        }
        if (value.matches("^(arm|arm32)$")) {
            return "arm_32";
        }
        if ("aarch64".equals(value)) {
            return "aarch_64";
        }
        if (value.matches("^(ppc|ppc32)$")) {
            return "ppc_32";
        }
        if ("ppc64".equals(value)) {
            return "ppc_64";
        }
        if ("ppc64le".equals(value)) {
            return "ppcle_64";
        }
        if ("s390".equals(value)) {
            return "s390_32";
        }
        if ("s390x".equals(value)) {
            return "s390_64";
        }
        return "unknown";
    }
    
    private static String normalizeOs(String value) {
        value = normalize(value);
        if (value.startsWith("aix")) {
            return "aix";
        }
        if (value.startsWith("hpux")) {
            return "hpux";
        }
        if (value.startsWith("os400") && (value.length() <= 5 || !Character.isDigit(value.charAt(5)))) {
            return "os400";
        }
        if (value.startsWith("linux")) {
            return "linux";
        }
        if (value.startsWith("macosx") || value.startsWith("osx") || value.startsWith("darwin")) {
            return "osx";
        }
        if (value.startsWith("freebsd")) {
            return "freebsd";
        }
        if (value.startsWith("openbsd")) {
            return "openbsd";
        }
        if (value.startsWith("netbsd")) {
            return "netbsd";
        }
        if (value.startsWith("solaris") || value.startsWith("sunos")) {
            return "sunos";
        }
        if (value.startsWith("windows")) {
            return "windows";
        }
        return "unknown";
    }
    
    private PlatformDependent() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(PlatformDependent.class);
        MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN = Pattern.compile("\\s*-XX:MaxDirectMemorySize\\s*=\\s*([0-9]+)\\s*([kKmMgG]?)\\s*$");
        CAN_ENABLE_TCP_NODELAY_BY_DEFAULT = !isAndroid();
        UNSAFE_UNAVAILABILITY_CAUSE = unsafeUnavailabilityCause0();
        MAX_DIRECT_MEMORY = maxDirectMemory0();
        BYTE_ARRAY_BASE_OFFSET = byteArrayBaseOffset0();
        TMPDIR = tmpdir0();
        BIT_MODE = bitMode0();
        NORMALIZED_ARCH = normalizeArch(SystemPropertyUtil.get("os.arch", ""));
        NORMALIZED_OS = normalizeOs(SystemPropertyUtil.get("os.name", ""));
        ALLOWED_LINUX_OS_CLASSIFIERS = new String[] { "fedora", "suse", "arch" };
        IS_WINDOWS = isWindows0();
        IS_OSX = isOsx0();
        IS_J9_JVM = isJ9Jvm0();
        IS_IVKVM_DOT_NET = isIkvmDotNet0();
        ADDRESS_SIZE = addressSize0();
        OS_RELEASE_FILES = new String[] { "/etc/os-release", "/usr/lib/os-release" };
        BIG_ENDIAN_NATIVE_ORDER = (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN);
        NOOP = new Cleaner() {
            @Override
            public void freeDirectBuffer(final ByteBuffer buffer) {
            }
        };
        if (javaVersion() >= 7) {
            RANDOM_PROVIDER = new ThreadLocalRandomProvider() {
                @SuppressJava6Requirement(reason = "Usage guarded by java version check")
                @Override
                public Random current() {
                    return ThreadLocalRandom.current();
                }
            };
        }
        else {
            RANDOM_PROVIDER = new ThreadLocalRandomProvider() {
                @Override
                public Random current() {
                    return io.netty.util.internal.ThreadLocalRandom.current();
                }
            };
        }
        long maxDirectMemory = SystemPropertyUtil.getLong("io.netty.maxDirectMemory", -1L);
        if (maxDirectMemory == 0L || !hasUnsafe() || !PlatformDependent0.hasDirectBufferNoCleanerConstructor()) {
            USE_DIRECT_BUFFER_NO_CLEANER = false;
            DIRECT_MEMORY_COUNTER = null;
        }
        else {
            USE_DIRECT_BUFFER_NO_CLEANER = true;
            if (maxDirectMemory < 0L) {
                maxDirectMemory = PlatformDependent.MAX_DIRECT_MEMORY;
                if (maxDirectMemory <= 0L) {
                    DIRECT_MEMORY_COUNTER = null;
                }
                else {
                    DIRECT_MEMORY_COUNTER = new AtomicLong();
                }
            }
            else {
                DIRECT_MEMORY_COUNTER = new AtomicLong();
            }
        }
        PlatformDependent.logger.debug("-Dio.netty.maxDirectMemory: {} bytes", (Object)maxDirectMemory);
        DIRECT_MEMORY_LIMIT = ((maxDirectMemory >= 1L) ? maxDirectMemory : PlatformDependent.MAX_DIRECT_MEMORY);
        final int tryAllocateUninitializedArray = SystemPropertyUtil.getInt("io.netty.uninitializedArrayAllocationThreshold", 1024);
        UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD = ((javaVersion() >= 9 && PlatformDependent0.hasAllocateArrayMethod()) ? tryAllocateUninitializedArray : -1);
        PlatformDependent.logger.debug("-Dio.netty.uninitializedArrayAllocationThreshold: {}", (Object)PlatformDependent.UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD);
        MAYBE_SUPER_USER = maybeSuperUser0();
        if (!isAndroid()) {
            if (javaVersion() >= 9) {
                CLEANER = (CleanerJava9.isSupported() ? new CleanerJava9() : PlatformDependent.NOOP);
            }
            else {
                CLEANER = (CleanerJava6.isSupported() ? new CleanerJava6() : PlatformDependent.NOOP);
            }
        }
        else {
            CLEANER = PlatformDependent.NOOP;
        }
        DIRECT_BUFFER_PREFERRED = (PlatformDependent.CLEANER != PlatformDependent.NOOP && !SystemPropertyUtil.getBoolean("io.netty.noPreferDirect", false));
        if (PlatformDependent.logger.isDebugEnabled()) {
            PlatformDependent.logger.debug("-Dio.netty.noPreferDirect: {}", (Object)!PlatformDependent.DIRECT_BUFFER_PREFERRED);
        }
        if (PlatformDependent.CLEANER == PlatformDependent.NOOP && !PlatformDependent0.isExplicitNoUnsafe()) {
            PlatformDependent.logger.info("Your platform does not provide complete low-level API for accessing direct buffers reliably. Unless explicitly requested, heap buffer will always be preferred to avoid potential system instability.");
        }
        final Set<String> allowedClassifiers = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList(PlatformDependent.ALLOWED_LINUX_OS_CLASSIFIERS)));
        final Set<String> availableClassifiers = new LinkedHashSet<String>();
        for (final String osReleaseFileName : PlatformDependent.OS_RELEASE_FILES) {
            final File file = new File(osReleaseFileName);
            final boolean found = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
                final /* synthetic */ Set val$allowedClassifiers = allowedClassifiers;
                final /* synthetic */ Set val$availableClassifiers = availableClassifiers;
                
                @Override
                public Boolean run() {
                    try {
                        if (file.exists()) {
                            BufferedReader reader = null;
                            try {
                                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), CharsetUtil.UTF_8));
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if (line.startsWith("ID=")) {
                                        final String id = normalizeOsReleaseVariableValue(line.substring("ID=".length()));
                                        addClassifier(this.val$allowedClassifiers, this.val$availableClassifiers, new String[] { id });
                                    }
                                    else {
                                        if (!line.startsWith("ID_LIKE=")) {
                                            continue;
                                        }
                                        line = normalizeOsReleaseVariableValue(line.substring("ID_LIKE=".length()));
                                        addClassifier(this.val$allowedClassifiers, this.val$availableClassifiers, line.split("[ ]+"));
                                    }
                                }
                            }
                            catch (final SecurityException e) {
                                PlatformDependent.logger.debug("Unable to read {}", osReleaseFileName, e);
                            }
                            catch (final IOException e2) {
                                PlatformDependent.logger.debug("Error while reading content of {}", osReleaseFileName, e2);
                            }
                            finally {
                                if (reader != null) {
                                    try {
                                        reader.close();
                                    }
                                    catch (final IOException ex) {}
                                }
                            }
                            return true;
                        }
                    }
                    catch (final SecurityException e3) {
                        PlatformDependent.logger.debug("Unable to check if {} exists", osReleaseFileName, e3);
                    }
                    return false;
                }
            });
            if (found) {
                break;
            }
        }
        LINUX_OS_CLASSIFIERS = Collections.unmodifiableSet((Set<? extends String>)availableClassifiers);
    }
    
    private static final class Mpsc
    {
        private static final boolean USE_MPSC_CHUNKED_ARRAY_QUEUE;
        
        static <T> Queue<T> newMpscQueue(final int maxCapacity) {
            final int capacity = Math.max(Math.min(maxCapacity, 1073741824), 2048);
            return (Queue<T>)(Mpsc.USE_MPSC_CHUNKED_ARRAY_QUEUE ? new MpscChunkedArrayQueue<Object>(1024, capacity) : new MpscChunkedAtomicArrayQueue<Object>(1024, capacity));
        }
        
        static <T> Queue<T> newMpscQueue() {
            return (Queue<T>)(Mpsc.USE_MPSC_CHUNKED_ARRAY_QUEUE ? new MpscUnboundedArrayQueue<Object>(1024) : new MpscUnboundedAtomicArrayQueue<Object>(1024));
        }
        
        static {
            Object unsafe = null;
            if (PlatformDependent.hasUnsafe()) {
                unsafe = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        return UnsafeAccess.UNSAFE;
                    }
                });
            }
            if (unsafe == null) {
                PlatformDependent.logger.debug("org.jctools-core.MpscChunkedArrayQueue: unavailable");
                USE_MPSC_CHUNKED_ARRAY_QUEUE = false;
            }
            else {
                PlatformDependent.logger.debug("org.jctools-core.MpscChunkedArrayQueue: available");
                USE_MPSC_CHUNKED_ARRAY_QUEUE = true;
            }
        }
    }
    
    private static final class AtomicLongCounter extends AtomicLong implements LongCounter
    {
        private static final long serialVersionUID = 4074772784610639305L;
        
        @Override
        public void add(final long delta) {
            this.addAndGet(delta);
        }
        
        @Override
        public void increment() {
            this.incrementAndGet();
        }
        
        @Override
        public void decrement() {
            this.decrementAndGet();
        }
        
        @Override
        public long value() {
            return this.get();
        }
    }
    
    private interface ThreadLocalRandomProvider
    {
        Random current();
    }
}
