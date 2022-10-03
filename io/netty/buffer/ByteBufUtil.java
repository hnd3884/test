package io.netty.buffer;

import io.netty.util.internal.ObjectPool;
import java.util.Locale;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.nio.charset.CoderResult;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteOrder;
import io.netty.util.internal.StringUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.ByteProcessor;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.logging.InternalLogger;

public final class ByteBufUtil
{
    private static final InternalLogger logger;
    private static final FastThreadLocal<byte[]> BYTE_ARRAYS;
    private static final byte WRITE_UTF_UNKNOWN = 63;
    private static final int MAX_CHAR_BUFFER_SIZE;
    private static final int THREAD_LOCAL_BUFFER_SIZE;
    private static final int MAX_BYTES_PER_CHAR_UTF8;
    static final int WRITE_CHUNK_SIZE = 8192;
    static final ByteBufAllocator DEFAULT_ALLOCATOR;
    static final int MAX_TL_ARRAY_LEN = 1024;
    private static final ByteProcessor FIND_NON_ASCII;
    
    static byte[] threadLocalTempArray(final int minLength) {
        return (minLength <= 1024) ? ByteBufUtil.BYTE_ARRAYS.get() : PlatformDependent.allocateUninitializedArray(minLength);
    }
    
    public static boolean isAccessible(final ByteBuf buffer) {
        return buffer.isAccessible();
    }
    
    public static ByteBuf ensureAccessible(final ByteBuf buffer) {
        if (!buffer.isAccessible()) {
            throw new IllegalReferenceCountException(buffer.refCnt());
        }
        return buffer;
    }
    
    public static String hexDump(final ByteBuf buffer) {
        return hexDump(buffer, buffer.readerIndex(), buffer.readableBytes());
    }
    
    public static String hexDump(final ByteBuf buffer, final int fromIndex, final int length) {
        return hexDump(buffer, fromIndex, length);
    }
    
    public static String hexDump(final byte[] array) {
        return hexDump(array, 0, array.length);
    }
    
    public static String hexDump(final byte[] array, final int fromIndex, final int length) {
        return hexDump(array, fromIndex, length);
    }
    
    public static byte decodeHexByte(final CharSequence s, final int pos) {
        return StringUtil.decodeHexByte(s, pos);
    }
    
    public static byte[] decodeHexDump(final CharSequence hexDump) {
        return StringUtil.decodeHexDump(hexDump, 0, hexDump.length());
    }
    
    public static byte[] decodeHexDump(final CharSequence hexDump, final int fromIndex, final int length) {
        return StringUtil.decodeHexDump(hexDump, fromIndex, length);
    }
    
    public static boolean ensureWritableSuccess(final int ensureWritableResult) {
        return ensureWritableResult == 0 || ensureWritableResult == 2;
    }
    
    public static int hashCode(final ByteBuf buffer) {
        final int aLen = buffer.readableBytes();
        final int intCount = aLen >>> 2;
        final int byteCount = aLen & 0x3;
        int hashCode = 1;
        int arrayIndex = buffer.readerIndex();
        if (buffer.order() == ByteOrder.BIG_ENDIAN) {
            for (int i = intCount; i > 0; --i) {
                hashCode = 31 * hashCode + buffer.getInt(arrayIndex);
                arrayIndex += 4;
            }
        }
        else {
            for (int i = intCount; i > 0; --i) {
                hashCode = 31 * hashCode + swapInt(buffer.getInt(arrayIndex));
                arrayIndex += 4;
            }
        }
        for (int i = byteCount; i > 0; --i) {
            hashCode = 31 * hashCode + buffer.getByte(arrayIndex++);
        }
        if (hashCode == 0) {
            hashCode = 1;
        }
        return hashCode;
    }
    
    public static int indexOf(final ByteBuf needle, final ByteBuf haystack) {
        if (haystack == null || needle == null) {
            return -1;
        }
        if (needle.readableBytes() > haystack.readableBytes()) {
            return -1;
        }
        final int n = haystack.readableBytes();
        final int m = needle.readableBytes();
        if (m == 0) {
            return 0;
        }
        if (m == 1) {
            return firstIndexOf((AbstractByteBuf)haystack, haystack.readerIndex(), haystack.writerIndex(), needle.getByte(needle.readerIndex()));
        }
        int j = 0;
        final int aStartIndex = needle.readerIndex();
        final int bStartIndex = haystack.readerIndex();
        final long suffixes = maxSuf(needle, m, aStartIndex, true);
        final long prefixes = maxSuf(needle, m, aStartIndex, false);
        final int ell = Math.max((int)(suffixes >> 32), (int)(prefixes >> 32));
        int per = Math.max((int)suffixes, (int)prefixes);
        final int length = Math.min(m - per, ell + 1);
        if (equals(needle, aStartIndex, needle, aStartIndex + per, length)) {
            int memory = -1;
            while (j <= n - m) {
                int i;
                for (i = Math.max(ell, memory) + 1; i < m && needle.getByte(i + aStartIndex) == haystack.getByte(i + j + bStartIndex); ++i) {}
                if (i > n) {
                    return -1;
                }
                if (i >= m) {
                    for (i = ell; i > memory && needle.getByte(i + aStartIndex) == haystack.getByte(i + j + bStartIndex); --i) {}
                    if (i <= memory) {
                        return j;
                    }
                    j += per;
                    memory = m - per - 1;
                }
                else {
                    j += i - ell;
                    memory = -1;
                }
            }
        }
        else {
            per = Math.max(ell + 1, m - ell - 1) + 1;
            while (j <= n - m) {
                int i;
                for (i = ell + 1; i < m && needle.getByte(i + aStartIndex) == haystack.getByte(i + j + bStartIndex); ++i) {}
                if (i > n) {
                    return -1;
                }
                if (i >= m) {
                    for (i = ell; i >= 0 && needle.getByte(i + aStartIndex) == haystack.getByte(i + j + bStartIndex); --i) {}
                    if (i < 0) {
                        return j;
                    }
                    j += per;
                }
                else {
                    j += i - ell;
                }
            }
        }
        return -1;
    }
    
    private static long maxSuf(final ByteBuf x, final int m, final int start, final boolean isSuffix) {
        int p = 1;
        int ms = -1;
        int j = start;
        int k = 1;
        while (j + k < m) {
            final byte a = x.getByte(j + k);
            final byte b = x.getByte(ms + k);
            final boolean suffix = isSuffix ? (a < b) : (a > b);
            if (suffix) {
                j += k;
                k = 1;
                p = j - ms;
            }
            else if (a == b) {
                if (k != p) {
                    ++k;
                }
                else {
                    j += p;
                    k = 1;
                }
            }
            else {
                ms = j;
                j = ms + 1;
                p = (k = 1);
            }
        }
        return ((long)ms << 32) + p;
    }
    
    public static boolean equals(final ByteBuf a, int aStartIndex, final ByteBuf b, int bStartIndex, final int length) {
        ObjectUtil.checkNotNull(a, "a");
        ObjectUtil.checkNotNull(b, "b");
        ObjectUtil.checkPositiveOrZero(aStartIndex, "aStartIndex");
        ObjectUtil.checkPositiveOrZero(bStartIndex, "bStartIndex");
        ObjectUtil.checkPositiveOrZero(length, "length");
        if (a.writerIndex() - length < aStartIndex || b.writerIndex() - length < bStartIndex) {
            return false;
        }
        final int longCount = length >>> 3;
        final int byteCount = length & 0x7;
        if (a.order() == b.order()) {
            for (int i = longCount; i > 0; --i) {
                if (a.getLong(aStartIndex) != b.getLong(bStartIndex)) {
                    return false;
                }
                aStartIndex += 8;
                bStartIndex += 8;
            }
        }
        else {
            for (int i = longCount; i > 0; --i) {
                if (a.getLong(aStartIndex) != swapLong(b.getLong(bStartIndex))) {
                    return false;
                }
                aStartIndex += 8;
                bStartIndex += 8;
            }
        }
        for (int i = byteCount; i > 0; --i) {
            if (a.getByte(aStartIndex) != b.getByte(bStartIndex)) {
                return false;
            }
            ++aStartIndex;
            ++bStartIndex;
        }
        return true;
    }
    
    public static boolean equals(final ByteBuf bufferA, final ByteBuf bufferB) {
        if (bufferA == bufferB) {
            return true;
        }
        final int aLen = bufferA.readableBytes();
        return aLen == bufferB.readableBytes() && equals(bufferA, bufferA.readerIndex(), bufferB, bufferB.readerIndex(), aLen);
    }
    
    public static int compare(final ByteBuf bufferA, final ByteBuf bufferB) {
        if (bufferA == bufferB) {
            return 0;
        }
        final int aLen = bufferA.readableBytes();
        final int bLen = bufferB.readableBytes();
        final int minLength = Math.min(aLen, bLen);
        final int uintCount = minLength >>> 2;
        final int byteCount = minLength & 0x3;
        int aIndex = bufferA.readerIndex();
        int bIndex = bufferB.readerIndex();
        if (uintCount > 0) {
            final boolean bufferAIsBigEndian = bufferA.order() == ByteOrder.BIG_ENDIAN;
            final int uintCountIncrement = uintCount << 2;
            long res;
            if (bufferA.order() == bufferB.order()) {
                res = (bufferAIsBigEndian ? compareUintBigEndian(bufferA, bufferB, aIndex, bIndex, uintCountIncrement) : compareUintLittleEndian(bufferA, bufferB, aIndex, bIndex, uintCountIncrement));
            }
            else {
                res = (bufferAIsBigEndian ? compareUintBigEndianA(bufferA, bufferB, aIndex, bIndex, uintCountIncrement) : compareUintBigEndianB(bufferA, bufferB, aIndex, bIndex, uintCountIncrement));
            }
            if (res != 0L) {
                return (int)Math.min(2147483647L, Math.max(-2147483648L, res));
            }
            aIndex += uintCountIncrement;
            bIndex += uintCountIncrement;
        }
        for (int aEnd = aIndex + byteCount; aIndex < aEnd; ++aIndex, ++bIndex) {
            final int comp = bufferA.getUnsignedByte(aIndex) - bufferB.getUnsignedByte(bIndex);
            if (comp != 0) {
                return comp;
            }
        }
        return aLen - bLen;
    }
    
    private static long compareUintBigEndian(final ByteBuf bufferA, final ByteBuf bufferB, int aIndex, int bIndex, final int uintCountIncrement) {
        for (int aEnd = aIndex + uintCountIncrement; aIndex < aEnd; aIndex += 4, bIndex += 4) {
            final long comp = bufferA.getUnsignedInt(aIndex) - bufferB.getUnsignedInt(bIndex);
            if (comp != 0L) {
                return comp;
            }
        }
        return 0L;
    }
    
    private static long compareUintLittleEndian(final ByteBuf bufferA, final ByteBuf bufferB, int aIndex, int bIndex, final int uintCountIncrement) {
        for (int aEnd = aIndex + uintCountIncrement; aIndex < aEnd; aIndex += 4, bIndex += 4) {
            final long comp = bufferA.getUnsignedIntLE(aIndex) - bufferB.getUnsignedIntLE(bIndex);
            if (comp != 0L) {
                return comp;
            }
        }
        return 0L;
    }
    
    private static long compareUintBigEndianA(final ByteBuf bufferA, final ByteBuf bufferB, int aIndex, int bIndex, final int uintCountIncrement) {
        for (int aEnd = aIndex + uintCountIncrement; aIndex < aEnd; aIndex += 4, bIndex += 4) {
            final long comp = bufferA.getUnsignedInt(aIndex) - bufferB.getUnsignedIntLE(bIndex);
            if (comp != 0L) {
                return comp;
            }
        }
        return 0L;
    }
    
    private static long compareUintBigEndianB(final ByteBuf bufferA, final ByteBuf bufferB, int aIndex, int bIndex, final int uintCountIncrement) {
        for (int aEnd = aIndex + uintCountIncrement; aIndex < aEnd; aIndex += 4, bIndex += 4) {
            final long comp = bufferA.getUnsignedIntLE(aIndex) - bufferB.getUnsignedInt(bIndex);
            if (comp != 0L) {
                return comp;
            }
        }
        return 0L;
    }
    
    private static int unrolledFirstIndexOf(final AbstractByteBuf buffer, final int fromIndex, final int byteCount, final byte value) {
        assert byteCount > 0 && byteCount < 8;
        if (buffer._getByte(fromIndex) == value) {
            return fromIndex;
        }
        if (byteCount == 1) {
            return -1;
        }
        if (buffer._getByte(fromIndex + 1) == value) {
            return fromIndex + 1;
        }
        if (byteCount == 2) {
            return -1;
        }
        if (buffer._getByte(fromIndex + 2) == value) {
            return fromIndex + 2;
        }
        if (byteCount == 3) {
            return -1;
        }
        if (buffer._getByte(fromIndex + 3) == value) {
            return fromIndex + 3;
        }
        if (byteCount == 4) {
            return -1;
        }
        if (buffer._getByte(fromIndex + 4) == value) {
            return fromIndex + 4;
        }
        if (byteCount == 5) {
            return -1;
        }
        if (buffer._getByte(fromIndex + 5) == value) {
            return fromIndex + 5;
        }
        if (byteCount == 6) {
            return -1;
        }
        if (buffer._getByte(fromIndex + 6) == value) {
            return fromIndex + 6;
        }
        return -1;
    }
    
    static int firstIndexOf(final AbstractByteBuf buffer, int fromIndex, final int toIndex, final byte value) {
        fromIndex = Math.max(fromIndex, 0);
        if (fromIndex >= toIndex || buffer.capacity() == 0) {
            return -1;
        }
        final int length = toIndex - fromIndex;
        buffer.checkIndex(fromIndex, length);
        if (!PlatformDependent.isUnaligned()) {
            return linearFirstIndexOf(buffer, fromIndex, toIndex, value);
        }
        assert PlatformDependent.isUnaligned();
        int offset = fromIndex;
        final int byteCount = length & 0x7;
        if (byteCount > 0) {
            final int index = unrolledFirstIndexOf(buffer, fromIndex, byteCount, value);
            if (index != -1) {
                return index;
            }
            offset += byteCount;
            if (offset == toIndex) {
                return -1;
            }
        }
        final int longCount = length >>> 3;
        final ByteOrder nativeOrder = ByteOrder.nativeOrder();
        final boolean isNative = nativeOrder == buffer.order();
        final boolean useLE = nativeOrder == ByteOrder.LITTLE_ENDIAN;
        final long pattern = compilePattern(value);
        for (int i = 0; i < longCount; ++i) {
            final long word = useLE ? buffer._getLongLE(offset) : buffer._getLong(offset);
            final int index2 = firstAnyPattern(word, pattern, isNative);
            if (index2 < 8) {
                return offset + index2;
            }
            offset += 8;
        }
        return -1;
    }
    
    private static int linearFirstIndexOf(final AbstractByteBuf buffer, final int fromIndex, final int toIndex, final byte value) {
        for (int i = fromIndex; i < toIndex; ++i) {
            if (buffer._getByte(i) == value) {
                return i;
            }
        }
        return -1;
    }
    
    public static int indexOf(final ByteBuf buffer, final int fromIndex, final int toIndex, final byte value) {
        return buffer.indexOf(fromIndex, toIndex, value);
    }
    
    public static short swapShort(final short value) {
        return Short.reverseBytes(value);
    }
    
    public static int swapMedium(final int value) {
        int swapped = (value << 16 & 0xFF0000) | (value & 0xFF00) | (value >>> 16 & 0xFF);
        if ((swapped & 0x800000) != 0x0) {
            swapped |= 0xFF000000;
        }
        return swapped;
    }
    
    public static int swapInt(final int value) {
        return Integer.reverseBytes(value);
    }
    
    public static long swapLong(final long value) {
        return Long.reverseBytes(value);
    }
    
    public static ByteBuf writeShortBE(final ByteBuf buf, final int shortValue) {
        return (buf.order() == ByteOrder.BIG_ENDIAN) ? buf.writeShort(shortValue) : buf.writeShort(swapShort((short)shortValue));
    }
    
    public static ByteBuf setShortBE(final ByteBuf buf, final int index, final int shortValue) {
        return (buf.order() == ByteOrder.BIG_ENDIAN) ? buf.setShort(index, shortValue) : buf.setShort(index, swapShort((short)shortValue));
    }
    
    public static ByteBuf writeMediumBE(final ByteBuf buf, final int mediumValue) {
        return (buf.order() == ByteOrder.BIG_ENDIAN) ? buf.writeMedium(mediumValue) : buf.writeMedium(swapMedium(mediumValue));
    }
    
    public static ByteBuf readBytes(final ByteBufAllocator alloc, final ByteBuf buffer, final int length) {
        boolean release = true;
        final ByteBuf dst = alloc.buffer(length);
        try {
            buffer.readBytes(dst);
            release = false;
            return dst;
        }
        finally {
            if (release) {
                dst.release();
            }
        }
    }
    
    static int lastIndexOf(final AbstractByteBuf buffer, int fromIndex, final int toIndex, final byte value) {
        assert fromIndex > toIndex;
        final int capacity = buffer.capacity();
        fromIndex = Math.min(fromIndex, capacity);
        if (fromIndex < 0 || capacity == 0) {
            return -1;
        }
        buffer.checkIndex(toIndex, fromIndex - toIndex);
        for (int i = fromIndex - 1; i >= toIndex; --i) {
            if (buffer._getByte(i) == value) {
                return i;
            }
        }
        return -1;
    }
    
    private static CharSequence checkCharSequenceBounds(final CharSequence seq, final int start, final int end) {
        if (MathUtil.isOutOfBounds(start, end - start, seq.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= end (" + end + ") <= seq.length(" + seq.length() + ')');
        }
        return seq;
    }
    
    public static ByteBuf writeUtf8(final ByteBufAllocator alloc, final CharSequence seq) {
        final ByteBuf buf = alloc.buffer(utf8MaxBytes(seq));
        writeUtf8(buf, seq);
        return buf;
    }
    
    public static int writeUtf8(final ByteBuf buf, final CharSequence seq) {
        final int seqLength = seq.length();
        return reserveAndWriteUtf8Seq(buf, seq, 0, seqLength, utf8MaxBytes(seqLength));
    }
    
    public static int writeUtf8(final ByteBuf buf, final CharSequence seq, final int start, final int end) {
        checkCharSequenceBounds(seq, start, end);
        return reserveAndWriteUtf8Seq(buf, seq, start, end, utf8MaxBytes(end - start));
    }
    
    public static int reserveAndWriteUtf8(final ByteBuf buf, final CharSequence seq, final int reserveBytes) {
        return reserveAndWriteUtf8Seq(buf, seq, 0, seq.length(), reserveBytes);
    }
    
    public static int reserveAndWriteUtf8(final ByteBuf buf, final CharSequence seq, final int start, final int end, final int reserveBytes) {
        return reserveAndWriteUtf8Seq(buf, checkCharSequenceBounds(seq, start, end), start, end, reserveBytes);
    }
    
    private static int reserveAndWriteUtf8Seq(ByteBuf buf, final CharSequence seq, final int start, final int end, final int reserveBytes) {
        while (true) {
            if (buf instanceof WrappedCompositeByteBuf) {
                buf = buf.unwrap();
            }
            else {
                if (buf instanceof AbstractByteBuf) {
                    final AbstractByteBuf byteBuf = (AbstractByteBuf)buf;
                    byteBuf.ensureWritable0(reserveBytes);
                    final int written = writeUtf8(byteBuf, byteBuf.writerIndex, reserveBytes, seq, start, end);
                    final AbstractByteBuf abstractByteBuf = byteBuf;
                    abstractByteBuf.writerIndex += written;
                    return written;
                }
                if (!(buf instanceof WrappedByteBuf)) {
                    final byte[] bytes = seq.subSequence(start, end).toString().getBytes(CharsetUtil.UTF_8);
                    buf.writeBytes(bytes);
                    return bytes.length;
                }
                buf = buf.unwrap();
            }
        }
    }
    
    static int writeUtf8(final AbstractByteBuf buffer, final int writerIndex, final int reservedBytes, final CharSequence seq, final int len) {
        return writeUtf8(buffer, writerIndex, reservedBytes, seq, 0, len);
    }
    
    static int writeUtf8(final AbstractByteBuf buffer, final int writerIndex, final int reservedBytes, final CharSequence seq, final int start, final int end) {
        if (seq instanceof AsciiString) {
            writeAsciiString(buffer, writerIndex, (AsciiString)seq, start, end);
            return end - start;
        }
        if (PlatformDependent.hasUnsafe()) {
            if (buffer.hasArray()) {
                return unsafeWriteUtf8(buffer.array(), PlatformDependent.byteArrayBaseOffset(), buffer.arrayOffset() + writerIndex, seq, start, end);
            }
            if (buffer.hasMemoryAddress()) {
                return unsafeWriteUtf8(null, buffer.memoryAddress(), writerIndex, seq, start, end);
            }
        }
        else {
            if (buffer.hasArray()) {
                return safeArrayWriteUtf8(buffer.array(), buffer.arrayOffset() + writerIndex, seq, start, end);
            }
            if (buffer.isDirect()) {
                assert buffer.nioBufferCount() == 1;
                final ByteBuffer internalDirectBuffer = buffer.internalNioBuffer(writerIndex, reservedBytes);
                final int bufferPosition = internalDirectBuffer.position();
                return safeDirectWriteUtf8(internalDirectBuffer, bufferPosition, seq, start, end);
            }
        }
        return safeWriteUtf8(buffer, writerIndex, seq, start, end);
    }
    
    static void writeAsciiString(final AbstractByteBuf buffer, final int writerIndex, final AsciiString seq, final int start, final int end) {
        final int begin = seq.arrayOffset() + start;
        final int length = end - start;
        if (PlatformDependent.hasUnsafe()) {
            if (buffer.hasArray()) {
                PlatformDependent.copyMemory(seq.array(), begin, buffer.array(), buffer.arrayOffset() + writerIndex, length);
                return;
            }
            if (buffer.hasMemoryAddress()) {
                PlatformDependent.copyMemory(seq.array(), begin, buffer.memoryAddress() + writerIndex, length);
                return;
            }
        }
        if (buffer.hasArray()) {
            System.arraycopy(seq.array(), begin, buffer.array(), buffer.arrayOffset() + writerIndex, length);
            return;
        }
        buffer.setBytes(writerIndex, seq.array(), begin, length);
    }
    
    private static int safeDirectWriteUtf8(final ByteBuffer buffer, int writerIndex, final CharSequence seq, final int start, final int end) {
        assert !(seq instanceof AsciiString);
        final int oldWriterIndex = writerIndex;
        for (int i = start; i < end; ++i) {
            final char c = seq.charAt(i);
            if (c < '\u0080') {
                buffer.put(writerIndex++, (byte)c);
            }
            else if (c < '\u0800') {
                buffer.put(writerIndex++, (byte)(0xC0 | c >> 6));
                buffer.put(writerIndex++, (byte)(0x80 | (c & '?')));
            }
            else if (StringUtil.isSurrogate(c)) {
                if (!Character.isHighSurrogate(c)) {
                    buffer.put(writerIndex++, (byte)63);
                }
                else {
                    if (++i == end) {
                        buffer.put(writerIndex++, (byte)63);
                        break;
                    }
                    final char c2 = seq.charAt(i);
                    if (!Character.isLowSurrogate(c2)) {
                        buffer.put(writerIndex++, (byte)63);
                        buffer.put(writerIndex++, (byte)(Character.isHighSurrogate(c2) ? 63 : ((byte)c2)));
                    }
                    else {
                        final int codePoint = Character.toCodePoint(c, c2);
                        buffer.put(writerIndex++, (byte)(0xF0 | codePoint >> 18));
                        buffer.put(writerIndex++, (byte)(0x80 | (codePoint >> 12 & 0x3F)));
                        buffer.put(writerIndex++, (byte)(0x80 | (codePoint >> 6 & 0x3F)));
                        buffer.put(writerIndex++, (byte)(0x80 | (codePoint & 0x3F)));
                    }
                }
            }
            else {
                buffer.put(writerIndex++, (byte)(0xE0 | c >> 12));
                buffer.put(writerIndex++, (byte)(0x80 | (c >> 6 & 0x3F)));
                buffer.put(writerIndex++, (byte)(0x80 | (c & '?')));
            }
        }
        return writerIndex - oldWriterIndex;
    }
    
    private static int safeWriteUtf8(final AbstractByteBuf buffer, int writerIndex, final CharSequence seq, final int start, final int end) {
        assert !(seq instanceof AsciiString);
        final int oldWriterIndex = writerIndex;
        for (int i = start; i < end; ++i) {
            final char c = seq.charAt(i);
            if (c < '\u0080') {
                buffer._setByte(writerIndex++, (byte)c);
            }
            else if (c < '\u0800') {
                buffer._setByte(writerIndex++, (byte)(0xC0 | c >> 6));
                buffer._setByte(writerIndex++, (byte)(0x80 | (c & '?')));
            }
            else if (StringUtil.isSurrogate(c)) {
                if (!Character.isHighSurrogate(c)) {
                    buffer._setByte(writerIndex++, 63);
                }
                else {
                    if (++i == end) {
                        buffer._setByte(writerIndex++, 63);
                        break;
                    }
                    final char c2 = seq.charAt(i);
                    if (!Character.isLowSurrogate(c2)) {
                        buffer._setByte(writerIndex++, 63);
                        buffer._setByte(writerIndex++, Character.isHighSurrogate(c2) ? '?' : c2);
                    }
                    else {
                        final int codePoint = Character.toCodePoint(c, c2);
                        buffer._setByte(writerIndex++, (byte)(0xF0 | codePoint >> 18));
                        buffer._setByte(writerIndex++, (byte)(0x80 | (codePoint >> 12 & 0x3F)));
                        buffer._setByte(writerIndex++, (byte)(0x80 | (codePoint >> 6 & 0x3F)));
                        buffer._setByte(writerIndex++, (byte)(0x80 | (codePoint & 0x3F)));
                    }
                }
            }
            else {
                buffer._setByte(writerIndex++, (byte)(0xE0 | c >> 12));
                buffer._setByte(writerIndex++, (byte)(0x80 | (c >> 6 & 0x3F)));
                buffer._setByte(writerIndex++, (byte)(0x80 | (c & '?')));
            }
        }
        return writerIndex - oldWriterIndex;
    }
    
    private static int safeArrayWriteUtf8(final byte[] buffer, int writerIndex, final CharSequence seq, final int start, final int end) {
        final int oldWriterIndex = writerIndex;
        for (int i = start; i < end; ++i) {
            final char c = seq.charAt(i);
            if (c < '\u0080') {
                buffer[writerIndex++] = (byte)c;
            }
            else if (c < '\u0800') {
                buffer[writerIndex++] = (byte)(0xC0 | c >> 6);
                buffer[writerIndex++] = (byte)(0x80 | (c & '?'));
            }
            else if (StringUtil.isSurrogate(c)) {
                if (!Character.isHighSurrogate(c)) {
                    buffer[writerIndex++] = 63;
                }
                else {
                    if (++i == end) {
                        buffer[writerIndex++] = 63;
                        break;
                    }
                    final char c2 = seq.charAt(i);
                    if (!Character.isLowSurrogate(c2)) {
                        buffer[writerIndex++] = 63;
                        buffer[writerIndex++] = (byte)(Character.isHighSurrogate(c2) ? '?' : c2);
                    }
                    else {
                        final int codePoint = Character.toCodePoint(c, c2);
                        buffer[writerIndex++] = (byte)(0xF0 | codePoint >> 18);
                        buffer[writerIndex++] = (byte)(0x80 | (codePoint >> 12 & 0x3F));
                        buffer[writerIndex++] = (byte)(0x80 | (codePoint >> 6 & 0x3F));
                        buffer[writerIndex++] = (byte)(0x80 | (codePoint & 0x3F));
                    }
                }
            }
            else {
                buffer[writerIndex++] = (byte)(0xE0 | c >> 12);
                buffer[writerIndex++] = (byte)(0x80 | (c >> 6 & 0x3F));
                buffer[writerIndex++] = (byte)(0x80 | (c & '?'));
            }
        }
        return writerIndex - oldWriterIndex;
    }
    
    private static int unsafeWriteUtf8(final byte[] buffer, final long memoryOffset, final int writerIndex, final CharSequence seq, final int start, final int end) {
        assert !(seq instanceof AsciiString);
        final long oldWriterOffset;
        long writerOffset = oldWriterOffset = memoryOffset + writerIndex;
        for (int i = start; i < end; ++i) {
            final char c = seq.charAt(i);
            if (c < '\u0080') {
                PlatformDependent.putByte(buffer, writerOffset++, (byte)c);
            }
            else if (c < '\u0800') {
                PlatformDependent.putByte(buffer, writerOffset++, (byte)(0xC0 | c >> 6));
                PlatformDependent.putByte(buffer, writerOffset++, (byte)(0x80 | (c & '?')));
            }
            else if (StringUtil.isSurrogate(c)) {
                if (!Character.isHighSurrogate(c)) {
                    PlatformDependent.putByte(buffer, writerOffset++, (byte)63);
                }
                else {
                    if (++i == end) {
                        PlatformDependent.putByte(buffer, writerOffset++, (byte)63);
                        break;
                    }
                    final char c2 = seq.charAt(i);
                    if (!Character.isLowSurrogate(c2)) {
                        PlatformDependent.putByte(buffer, writerOffset++, (byte)63);
                        PlatformDependent.putByte(buffer, writerOffset++, (byte)(Character.isHighSurrogate(c2) ? '?' : c2));
                    }
                    else {
                        final int codePoint = Character.toCodePoint(c, c2);
                        PlatformDependent.putByte(buffer, writerOffset++, (byte)(0xF0 | codePoint >> 18));
                        PlatformDependent.putByte(buffer, writerOffset++, (byte)(0x80 | (codePoint >> 12 & 0x3F)));
                        PlatformDependent.putByte(buffer, writerOffset++, (byte)(0x80 | (codePoint >> 6 & 0x3F)));
                        PlatformDependent.putByte(buffer, writerOffset++, (byte)(0x80 | (codePoint & 0x3F)));
                    }
                }
            }
            else {
                PlatformDependent.putByte(buffer, writerOffset++, (byte)(0xE0 | c >> 12));
                PlatformDependent.putByte(buffer, writerOffset++, (byte)(0x80 | (c >> 6 & 0x3F)));
                PlatformDependent.putByte(buffer, writerOffset++, (byte)(0x80 | (c & '?')));
            }
        }
        return (int)(writerOffset - oldWriterOffset);
    }
    
    public static int utf8MaxBytes(final int seqLength) {
        return seqLength * ByteBufUtil.MAX_BYTES_PER_CHAR_UTF8;
    }
    
    public static int utf8MaxBytes(final CharSequence seq) {
        return utf8MaxBytes(seq.length());
    }
    
    public static int utf8Bytes(final CharSequence seq) {
        return utf8ByteCount(seq, 0, seq.length());
    }
    
    public static int utf8Bytes(final CharSequence seq, final int start, final int end) {
        return utf8ByteCount(checkCharSequenceBounds(seq, start, end), start, end);
    }
    
    private static int utf8ByteCount(final CharSequence seq, final int start, final int end) {
        if (seq instanceof AsciiString) {
            return end - start;
        }
        int i;
        for (i = start; i < end && seq.charAt(i) < '\u0080'; ++i) {}
        return (i < end) ? (i - start + utf8BytesNonAscii(seq, i, end)) : (i - start);
    }
    
    private static int utf8BytesNonAscii(final CharSequence seq, final int start, final int end) {
        int encodedLength = 0;
        for (int i = start; i < end; ++i) {
            final char c = seq.charAt(i);
            if (c < '\u0800') {
                encodedLength += ('\u007f' - c >>> 31) + 1;
            }
            else if (StringUtil.isSurrogate(c)) {
                if (!Character.isHighSurrogate(c)) {
                    ++encodedLength;
                }
                else {
                    if (++i == end) {
                        ++encodedLength;
                        break;
                    }
                    if (!Character.isLowSurrogate(seq.charAt(i))) {
                        encodedLength += 2;
                    }
                    else {
                        encodedLength += 4;
                    }
                }
            }
            else {
                encodedLength += 3;
            }
        }
        return encodedLength;
    }
    
    public static ByteBuf writeAscii(final ByteBufAllocator alloc, final CharSequence seq) {
        final ByteBuf buf = alloc.buffer(seq.length());
        writeAscii(buf, seq);
        return buf;
    }
    
    public static int writeAscii(ByteBuf buf, final CharSequence seq) {
        while (true) {
            if (buf instanceof WrappedCompositeByteBuf) {
                buf = buf.unwrap();
            }
            else {
                if (buf instanceof AbstractByteBuf) {
                    final int len = seq.length();
                    final AbstractByteBuf byteBuf = (AbstractByteBuf)buf;
                    byteBuf.ensureWritable0(len);
                    if (seq instanceof AsciiString) {
                        writeAsciiString(byteBuf, byteBuf.writerIndex, (AsciiString)seq, 0, len);
                    }
                    else {
                        final int written = writeAscii(byteBuf, byteBuf.writerIndex, seq, len);
                        assert written == len;
                    }
                    final AbstractByteBuf abstractByteBuf = byteBuf;
                    abstractByteBuf.writerIndex += len;
                    return len;
                }
                if (!(buf instanceof WrappedByteBuf)) {
                    final byte[] bytes = seq.toString().getBytes(CharsetUtil.US_ASCII);
                    buf.writeBytes(bytes);
                    return bytes.length;
                }
                buf = buf.unwrap();
            }
        }
    }
    
    static int writeAscii(final AbstractByteBuf buffer, int writerIndex, final CharSequence seq, final int len) {
        for (int i = 0; i < len; ++i) {
            buffer._setByte(writerIndex++, AsciiString.c2b(seq.charAt(i)));
        }
        return len;
    }
    
    public static ByteBuf encodeString(final ByteBufAllocator alloc, final CharBuffer src, final Charset charset) {
        return encodeString0(alloc, false, src, charset, 0);
    }
    
    public static ByteBuf encodeString(final ByteBufAllocator alloc, final CharBuffer src, final Charset charset, final int extraCapacity) {
        return encodeString0(alloc, false, src, charset, extraCapacity);
    }
    
    static ByteBuf encodeString0(final ByteBufAllocator alloc, final boolean enforceHeap, final CharBuffer src, final Charset charset, final int extraCapacity) {
        final CharsetEncoder encoder = CharsetUtil.encoder(charset);
        final int length = (int)(src.remaining() * (double)encoder.maxBytesPerChar()) + extraCapacity;
        boolean release = true;
        ByteBuf dst;
        if (enforceHeap) {
            dst = alloc.heapBuffer(length);
        }
        else {
            dst = alloc.buffer(length);
        }
        try {
            final ByteBuffer dstBuf = dst.internalNioBuffer(dst.readerIndex(), length);
            final int pos = dstBuf.position();
            CoderResult cr = encoder.encode(src, dstBuf, true);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
            cr = encoder.flush(dstBuf);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
            dst.writerIndex(dst.writerIndex() + dstBuf.position() - pos);
            release = false;
            return dst;
        }
        catch (final CharacterCodingException x) {
            throw new IllegalStateException(x);
        }
        finally {
            if (release) {
                dst.release();
            }
        }
    }
    
    static String decodeString(final ByteBuf src, final int readerIndex, final int len, final Charset charset) {
        if (len == 0) {
            return "";
        }
        byte[] array;
        int offset;
        if (src.hasArray()) {
            array = src.array();
            offset = src.arrayOffset() + readerIndex;
        }
        else {
            array = threadLocalTempArray(len);
            offset = 0;
            src.getBytes(readerIndex, array, 0, len);
        }
        if (CharsetUtil.US_ASCII.equals(charset)) {
            return new String(array, 0, offset, len);
        }
        return new String(array, offset, len, charset);
    }
    
    public static ByteBuf threadLocalDirectBuffer() {
        if (ByteBufUtil.THREAD_LOCAL_BUFFER_SIZE <= 0) {
            return null;
        }
        if (PlatformDependent.hasUnsafe()) {
            return ThreadLocalUnsafeDirectByteBuf.newInstance();
        }
        return ThreadLocalDirectByteBuf.newInstance();
    }
    
    public static byte[] getBytes(final ByteBuf buf) {
        return getBytes(buf, buf.readerIndex(), buf.readableBytes());
    }
    
    public static byte[] getBytes(final ByteBuf buf, final int start, final int length) {
        return getBytes(buf, start, length, true);
    }
    
    public static byte[] getBytes(final ByteBuf buf, final int start, final int length, final boolean copy) {
        final int capacity = buf.capacity();
        if (MathUtil.isOutOfBounds(start, length, capacity)) {
            throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= buf.capacity(" + capacity + ')');
        }
        if (!buf.hasArray()) {
            final byte[] bytes = PlatformDependent.allocateUninitializedArray(length);
            buf.getBytes(start, bytes);
            return bytes;
        }
        final int baseOffset = buf.arrayOffset() + start;
        final byte[] bytes2 = buf.array();
        if (copy || baseOffset != 0 || length != bytes2.length) {
            return Arrays.copyOfRange(bytes2, baseOffset, baseOffset + length);
        }
        return bytes2;
    }
    
    public static void copy(final AsciiString src, final ByteBuf dst) {
        copy(src, 0, dst, src.length());
    }
    
    public static void copy(final AsciiString src, final int srcIdx, final ByteBuf dst, final int dstIdx, final int length) {
        if (MathUtil.isOutOfBounds(srcIdx, length, src.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + src.length() + ')');
        }
        ObjectUtil.checkNotNull(dst, "dst").setBytes(dstIdx, src.array(), srcIdx + src.arrayOffset(), length);
    }
    
    public static void copy(final AsciiString src, final int srcIdx, final ByteBuf dst, final int length) {
        if (MathUtil.isOutOfBounds(srcIdx, length, src.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + src.length() + ')');
        }
        ObjectUtil.checkNotNull(dst, "dst").writeBytes(src.array(), srcIdx + src.arrayOffset(), length);
    }
    
    public static String prettyHexDump(final ByteBuf buffer) {
        return prettyHexDump(buffer, buffer.readerIndex(), buffer.readableBytes());
    }
    
    public static String prettyHexDump(final ByteBuf buffer, final int offset, final int length) {
        return prettyHexDump(buffer, offset, length);
    }
    
    public static void appendPrettyHexDump(final StringBuilder dump, final ByteBuf buf) {
        appendPrettyHexDump(dump, buf, buf.readerIndex(), buf.readableBytes());
    }
    
    public static void appendPrettyHexDump(final StringBuilder dump, final ByteBuf buf, final int offset, final int length) {
        appendPrettyHexDump(dump, buf, offset, length);
    }
    
    public static boolean isText(final ByteBuf buf, final Charset charset) {
        return isText(buf, buf.readerIndex(), buf.readableBytes(), charset);
    }
    
    public static boolean isText(final ByteBuf buf, final int index, final int length, final Charset charset) {
        ObjectUtil.checkNotNull(buf, "buf");
        ObjectUtil.checkNotNull(charset, "charset");
        final int maxIndex = buf.readerIndex() + buf.readableBytes();
        if (index < 0 || length < 0 || index > maxIndex - length) {
            throw new IndexOutOfBoundsException("index: " + index + " length: " + length);
        }
        if (charset.equals(CharsetUtil.UTF_8)) {
            return isUtf8(buf, index, length);
        }
        if (charset.equals(CharsetUtil.US_ASCII)) {
            return isAscii(buf, index, length);
        }
        final CharsetDecoder decoder = CharsetUtil.decoder(charset, CodingErrorAction.REPORT, CodingErrorAction.REPORT);
        try {
            if (buf.nioBufferCount() == 1) {
                decoder.decode(buf.nioBuffer(index, length));
            }
            else {
                final ByteBuf heapBuffer = buf.alloc().heapBuffer(length);
                try {
                    heapBuffer.writeBytes(buf, index, length);
                    decoder.decode(heapBuffer.internalNioBuffer(heapBuffer.readerIndex(), length));
                }
                finally {
                    heapBuffer.release();
                }
            }
            return true;
        }
        catch (final CharacterCodingException ignore) {
            return false;
        }
    }
    
    private static boolean isAscii(final ByteBuf buf, final int index, final int length) {
        return buf.forEachByte(index, length, ByteBufUtil.FIND_NON_ASCII) == -1;
    }
    
    private static boolean isUtf8(final ByteBuf buf, int index, final int length) {
        final int endIndex = index + length;
        while (index < endIndex) {
            final byte b1 = buf.getByte(index++);
            if ((b1 & 0x80) == 0x0) {
                continue;
            }
            if ((b1 & 0xE0) == 0xC0) {
                if (index >= endIndex) {
                    return false;
                }
                final byte b2 = buf.getByte(index++);
                if ((b2 & 0xC0) != 0x80) {
                    return false;
                }
                if ((b1 & 0xFF) < 194) {
                    return false;
                }
                continue;
            }
            else if ((b1 & 0xF0) == 0xE0) {
                if (index > endIndex - 2) {
                    return false;
                }
                final byte b2 = buf.getByte(index++);
                final byte b3 = buf.getByte(index++);
                if ((b2 & 0xC0) != 0x80 || (b3 & 0xC0) != 0x80) {
                    return false;
                }
                if ((b1 & 0xF) == 0x0 && (b2 & 0xFF) < 160) {
                    return false;
                }
                if ((b1 & 0xF) == 0xD && (b2 & 0xFF) > 159) {
                    return false;
                }
                continue;
            }
            else {
                if ((b1 & 0xF8) != 0xF0) {
                    return false;
                }
                if (index > endIndex - 3) {
                    return false;
                }
                final byte b2 = buf.getByte(index++);
                final byte b3 = buf.getByte(index++);
                final byte b4 = buf.getByte(index++);
                if ((b2 & 0xC0) != 0x80 || (b3 & 0xC0) != 0x80 || (b4 & 0xC0) != 0x80) {
                    return false;
                }
                if ((b1 & 0xFF) > 244 || ((b1 & 0xFF) == 0xF0 && (b2 & 0xFF) < 144) || ((b1 & 0xFF) == 0xF4 && (b2 & 0xFF) > 143)) {
                    return false;
                }
                continue;
            }
        }
        return true;
    }
    
    static void readBytes(final ByteBufAllocator allocator, final ByteBuffer buffer, final int position, final int length, final OutputStream out) throws IOException {
        if (buffer.hasArray()) {
            out.write(buffer.array(), position + buffer.arrayOffset(), length);
        }
        else {
            final int chunkLen = Math.min(length, 8192);
            buffer.clear().position(position);
            if (length <= 1024 || !allocator.isDirectBufferPooled()) {
                getBytes(buffer, threadLocalTempArray(chunkLen), 0, chunkLen, out, length);
            }
            else {
                final ByteBuf tmpBuf = allocator.heapBuffer(chunkLen);
                try {
                    final byte[] tmp = tmpBuf.array();
                    final int offset = tmpBuf.arrayOffset();
                    getBytes(buffer, tmp, offset, chunkLen, out, length);
                }
                finally {
                    tmpBuf.release();
                }
            }
        }
    }
    
    private static void getBytes(final ByteBuffer inBuffer, final byte[] in, final int inOffset, final int inLen, final OutputStream out, int outLen) throws IOException {
        do {
            final int len = Math.min(inLen, outLen);
            inBuffer.get(in, inOffset, len);
            out.write(in, inOffset, len);
            outLen -= len;
        } while (outLen > 0);
    }
    
    private ByteBufUtil() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(ByteBufUtil.class);
        BYTE_ARRAYS = new FastThreadLocal<byte[]>() {
            @Override
            protected byte[] initialValue() throws Exception {
                return PlatformDependent.allocateUninitializedArray(1024);
            }
        };
        MAX_BYTES_PER_CHAR_UTF8 = (int)CharsetUtil.encoder(CharsetUtil.UTF_8).maxBytesPerChar();
        String allocType = SystemPropertyUtil.get("io.netty.allocator.type", PlatformDependent.isAndroid() ? "unpooled" : "pooled");
        allocType = allocType.toLowerCase(Locale.US).trim();
        ByteBufAllocator alloc;
        if ("unpooled".equals(allocType)) {
            alloc = UnpooledByteBufAllocator.DEFAULT;
            ByteBufUtil.logger.debug("-Dio.netty.allocator.type: {}", allocType);
        }
        else if ("pooled".equals(allocType)) {
            alloc = PooledByteBufAllocator.DEFAULT;
            ByteBufUtil.logger.debug("-Dio.netty.allocator.type: {}", allocType);
        }
        else {
            alloc = PooledByteBufAllocator.DEFAULT;
            ByteBufUtil.logger.debug("-Dio.netty.allocator.type: pooled (unknown: {})", allocType);
        }
        DEFAULT_ALLOCATOR = alloc;
        THREAD_LOCAL_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.threadLocalDirectBufferSize", 0);
        ByteBufUtil.logger.debug("-Dio.netty.threadLocalDirectBufferSize: {}", (Object)ByteBufUtil.THREAD_LOCAL_BUFFER_SIZE);
        MAX_CHAR_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.maxThreadLocalCharBufferSize", 16384);
        ByteBufUtil.logger.debug("-Dio.netty.maxThreadLocalCharBufferSize: {}", (Object)ByteBufUtil.MAX_CHAR_BUFFER_SIZE);
        FIND_NON_ASCII = new ByteProcessor() {
            @Override
            public boolean process(final byte value) {
                return value >= 0;
            }
        };
    }
    
    private static final class SWARByteSearch
    {
        private static long compilePattern(final byte byteToFind) {
            return ((long)byteToFind & 0xFFL) * 72340172838076673L;
        }
        
        private static int firstAnyPattern(final long word, final long pattern, final boolean leading) {
            final long input = word ^ pattern;
            long tmp = (input & 0x7F7F7F7F7F7F7F7FL) + 9187201950435737471L;
            tmp = ~(tmp | input | 0x7F7F7F7F7F7F7F7FL);
            final int binaryPosition = leading ? Long.numberOfLeadingZeros(tmp) : Long.numberOfTrailingZeros(tmp);
            return binaryPosition >>> 3;
        }
    }
    
    private static final class HexUtil
    {
        private static final char[] BYTE2CHAR;
        private static final char[] HEXDUMP_TABLE;
        private static final String[] HEXPADDING;
        private static final String[] HEXDUMP_ROWPREFIXES;
        private static final String[] BYTE2HEX;
        private static final String[] BYTEPADDING;
        
        private static String hexDump(final ByteBuf buffer, final int fromIndex, final int length) {
            ObjectUtil.checkPositiveOrZero(length, "length");
            if (length == 0) {
                return "";
            }
            final int endIndex = fromIndex + length;
            final char[] buf = new char[length << 1];
            for (int srcIdx = fromIndex, dstIdx = 0; srcIdx < endIndex; ++srcIdx, dstIdx += 2) {
                System.arraycopy(HexUtil.HEXDUMP_TABLE, buffer.getUnsignedByte(srcIdx) << 1, buf, dstIdx, 2);
            }
            return new String(buf);
        }
        
        private static String hexDump(final byte[] array, final int fromIndex, final int length) {
            ObjectUtil.checkPositiveOrZero(length, "length");
            if (length == 0) {
                return "";
            }
            final int endIndex = fromIndex + length;
            final char[] buf = new char[length << 1];
            for (int srcIdx = fromIndex, dstIdx = 0; srcIdx < endIndex; ++srcIdx, dstIdx += 2) {
                System.arraycopy(HexUtil.HEXDUMP_TABLE, (array[srcIdx] & 0xFF) << 1, buf, dstIdx, 2);
            }
            return new String(buf);
        }
        
        private static String prettyHexDump(final ByteBuf buffer, final int offset, final int length) {
            if (length == 0) {
                return "";
            }
            final int rows = length / 16 + (((length & 0xF) != 0x0) ? 1 : 0) + 4;
            final StringBuilder buf = new StringBuilder(rows * 80);
            appendPrettyHexDump(buf, buffer, offset, length);
            return buf.toString();
        }
        
        private static void appendPrettyHexDump(final StringBuilder dump, final ByteBuf buf, final int offset, final int length) {
            if (MathUtil.isOutOfBounds(offset, length, buf.capacity())) {
                throw new IndexOutOfBoundsException("expected: 0 <= offset(" + offset + ") <= offset + length(" + length + ") <= buf.capacity(" + buf.capacity() + ')');
            }
            if (length == 0) {
                return;
            }
            dump.append("         +-------------------------------------------------+" + StringUtil.NEWLINE + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |" + StringUtil.NEWLINE + "+--------+-------------------------------------------------+----------------+");
            final int fullRows = length >>> 4;
            final int remainder = length & 0xF;
            for (int row = 0; row < fullRows; ++row) {
                final int rowStartIndex = (row << 4) + offset;
                appendHexDumpRowPrefix(dump, row, rowStartIndex);
                final int rowEndIndex = rowStartIndex + 16;
                for (int j = rowStartIndex; j < rowEndIndex; ++j) {
                    dump.append(HexUtil.BYTE2HEX[buf.getUnsignedByte(j)]);
                }
                dump.append(" |");
                for (int j = rowStartIndex; j < rowEndIndex; ++j) {
                    dump.append(HexUtil.BYTE2CHAR[buf.getUnsignedByte(j)]);
                }
                dump.append('|');
            }
            if (remainder != 0) {
                final int rowStartIndex2 = (fullRows << 4) + offset;
                appendHexDumpRowPrefix(dump, fullRows, rowStartIndex2);
                final int rowEndIndex2 = rowStartIndex2 + remainder;
                for (int i = rowStartIndex2; i < rowEndIndex2; ++i) {
                    dump.append(HexUtil.BYTE2HEX[buf.getUnsignedByte(i)]);
                }
                dump.append(HexUtil.HEXPADDING[remainder]);
                dump.append(" |");
                for (int i = rowStartIndex2; i < rowEndIndex2; ++i) {
                    dump.append(HexUtil.BYTE2CHAR[buf.getUnsignedByte(i)]);
                }
                dump.append(HexUtil.BYTEPADDING[remainder]);
                dump.append('|');
            }
            dump.append(StringUtil.NEWLINE + "+--------+-------------------------------------------------+----------------+");
        }
        
        private static void appendHexDumpRowPrefix(final StringBuilder dump, final int row, final int rowStartIndex) {
            if (row < HexUtil.HEXDUMP_ROWPREFIXES.length) {
                dump.append(HexUtil.HEXDUMP_ROWPREFIXES[row]);
            }
            else {
                dump.append(StringUtil.NEWLINE);
                dump.append(Long.toHexString(((long)rowStartIndex & 0xFFFFFFFFL) | 0x100000000L));
                dump.setCharAt(dump.length() - 9, '|');
                dump.append('|');
            }
        }
        
        static {
            BYTE2CHAR = new char[256];
            HEXDUMP_TABLE = new char[1024];
            HEXPADDING = new String[16];
            HEXDUMP_ROWPREFIXES = new String[4096];
            BYTE2HEX = new String[256];
            BYTEPADDING = new String[16];
            final char[] DIGITS = "0123456789abcdef".toCharArray();
            for (int i = 0; i < 256; ++i) {
                HexUtil.HEXDUMP_TABLE[i << 1] = DIGITS[i >>> 4 & 0xF];
                HexUtil.HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i & 0xF];
            }
            for (int i = 0; i < HexUtil.HEXPADDING.length; ++i) {
                final int padding = HexUtil.HEXPADDING.length - i;
                final StringBuilder buf = new StringBuilder(padding * 3);
                for (int j = 0; j < padding; ++j) {
                    buf.append("   ");
                }
                HexUtil.HEXPADDING[i] = buf.toString();
            }
            for (int i = 0; i < HexUtil.HEXDUMP_ROWPREFIXES.length; ++i) {
                final StringBuilder buf2 = new StringBuilder(12);
                buf2.append(StringUtil.NEWLINE);
                buf2.append(Long.toHexString(((long)(i << 4) & 0xFFFFFFFFL) | 0x100000000L));
                buf2.setCharAt(buf2.length() - 9, '|');
                buf2.append('|');
                HexUtil.HEXDUMP_ROWPREFIXES[i] = buf2.toString();
            }
            for (int i = 0; i < HexUtil.BYTE2HEX.length; ++i) {
                HexUtil.BYTE2HEX[i] = ' ' + StringUtil.byteToHexStringPadded(i);
            }
            for (int i = 0; i < HexUtil.BYTEPADDING.length; ++i) {
                final int padding = HexUtil.BYTEPADDING.length - i;
                final StringBuilder buf = new StringBuilder(padding);
                for (int j = 0; j < padding; ++j) {
                    buf.append(' ');
                }
                HexUtil.BYTEPADDING[i] = buf.toString();
            }
            for (int i = 0; i < HexUtil.BYTE2CHAR.length; ++i) {
                if (i <= 31 || i >= 127) {
                    HexUtil.BYTE2CHAR[i] = '.';
                }
                else {
                    HexUtil.BYTE2CHAR[i] = (char)i;
                }
            }
        }
    }
    
    static final class ThreadLocalUnsafeDirectByteBuf extends UnpooledUnsafeDirectByteBuf
    {
        private static final ObjectPool<ThreadLocalUnsafeDirectByteBuf> RECYCLER;
        private final ObjectPool.Handle<ThreadLocalUnsafeDirectByteBuf> handle;
        
        static ThreadLocalUnsafeDirectByteBuf newInstance() {
            final ThreadLocalUnsafeDirectByteBuf buf = ThreadLocalUnsafeDirectByteBuf.RECYCLER.get();
            buf.resetRefCnt();
            return buf;
        }
        
        private ThreadLocalUnsafeDirectByteBuf(final ObjectPool.Handle<ThreadLocalUnsafeDirectByteBuf> handle) {
            super(UnpooledByteBufAllocator.DEFAULT, 256, Integer.MAX_VALUE);
            this.handle = handle;
        }
        
        @Override
        protected void deallocate() {
            if (this.capacity() > ByteBufUtil.THREAD_LOCAL_BUFFER_SIZE) {
                super.deallocate();
            }
            else {
                this.clear();
                this.handle.recycle(this);
            }
        }
        
        static {
            RECYCLER = ObjectPool.newPool((ObjectPool.ObjectCreator<ThreadLocalUnsafeDirectByteBuf>)new ObjectPool.ObjectCreator<ThreadLocalUnsafeDirectByteBuf>() {
                @Override
                public ThreadLocalUnsafeDirectByteBuf newObject(final ObjectPool.Handle<ThreadLocalUnsafeDirectByteBuf> handle) {
                    return new ThreadLocalUnsafeDirectByteBuf((ObjectPool.Handle)handle);
                }
            });
        }
    }
    
    static final class ThreadLocalDirectByteBuf extends UnpooledDirectByteBuf
    {
        private static final ObjectPool<ThreadLocalDirectByteBuf> RECYCLER;
        private final ObjectPool.Handle<ThreadLocalDirectByteBuf> handle;
        
        static ThreadLocalDirectByteBuf newInstance() {
            final ThreadLocalDirectByteBuf buf = ThreadLocalDirectByteBuf.RECYCLER.get();
            buf.resetRefCnt();
            return buf;
        }
        
        private ThreadLocalDirectByteBuf(final ObjectPool.Handle<ThreadLocalDirectByteBuf> handle) {
            super(UnpooledByteBufAllocator.DEFAULT, 256, Integer.MAX_VALUE);
            this.handle = handle;
        }
        
        @Override
        protected void deallocate() {
            if (this.capacity() > ByteBufUtil.THREAD_LOCAL_BUFFER_SIZE) {
                super.deallocate();
            }
            else {
                this.clear();
                this.handle.recycle(this);
            }
        }
        
        static {
            RECYCLER = ObjectPool.newPool((ObjectPool.ObjectCreator<ThreadLocalDirectByteBuf>)new ObjectPool.ObjectCreator<ThreadLocalDirectByteBuf>() {
                @Override
                public ThreadLocalDirectByteBuf newObject(final ObjectPool.Handle<ThreadLocalDirectByteBuf> handle) {
                    return new ThreadLocalDirectByteBuf((ObjectPool.Handle)handle);
                }
            });
        }
    }
}
