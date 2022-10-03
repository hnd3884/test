package com.unboundid.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import com.unboundid.asn1.ASN1OctetString;
import java.util.Arrays;
import java.io.Serializable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ByteStringBuffer implements Serializable, Appendable
{
    private static final int DEFAULT_INITIAL_CAPACITY = 20;
    private static final byte[] FALSE_VALUE_BYTES;
    private static final byte[] TRUE_VALUE_BYTES;
    private static final ThreadLocal<byte[]> TEMP_NUMBER_BUFFER;
    private static final long serialVersionUID = 2899392249591230998L;
    private byte[] array;
    private int capacity;
    private int endPos;
    
    public ByteStringBuffer() {
        this(20);
    }
    
    public ByteStringBuffer(final int initialCapacity) {
        this.array = new byte[initialCapacity];
        this.capacity = initialCapacity;
        this.endPos = 0;
    }
    
    public ByteStringBuffer append(final boolean b) {
        if (b) {
            return this.append(ByteStringBuffer.TRUE_VALUE_BYTES, 0, 4);
        }
        return this.append(ByteStringBuffer.FALSE_VALUE_BYTES, 0, 5);
    }
    
    public ByteStringBuffer append(final byte b) {
        this.ensureCapacity(this.endPos + 1);
        this.array[this.endPos++] = b;
        return this;
    }
    
    public ByteStringBuffer append(final byte[] b) throws NullPointerException {
        if (b == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_ARRAY_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        return this.append(b, 0, b.length);
    }
    
    public ByteStringBuffer append(final byte[] b, final int off, final int len) throws NullPointerException, IndexOutOfBoundsException {
        if (b == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_ARRAY_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        if (off < 0 || len < 0 || off + len > b.length) {
            String message;
            if (off < 0) {
                message = UtilityMessages.ERR_BS_BUFFER_OFFSET_NEGATIVE.get(off);
            }
            else if (len < 0) {
                message = UtilityMessages.ERR_BS_BUFFER_LENGTH_NEGATIVE.get(len);
            }
            else {
                message = UtilityMessages.ERR_BS_BUFFER_OFFSET_PLUS_LENGTH_TOO_LARGE.get(off, len, b.length);
            }
            final IndexOutOfBoundsException e2 = new IndexOutOfBoundsException(message);
            Debug.debugCodingError(e2);
            throw e2;
        }
        if (len > 0) {
            this.ensureCapacity(this.endPos + len);
            System.arraycopy(b, off, this.array, this.endPos, len);
            this.endPos += len;
        }
        return this;
    }
    
    public ByteStringBuffer append(final ByteString b) throws NullPointerException {
        if (b == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_BYTE_STRING_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        b.appendValueTo(this);
        return this;
    }
    
    public ByteStringBuffer append(final ByteStringBuffer buffer) throws NullPointerException {
        if (buffer == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_BUFFER_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        return this.append(buffer.array, 0, buffer.endPos);
    }
    
    @Override
    public ByteStringBuffer append(final char c) {
        final byte b = (byte)(c & '\u007f');
        if (b == c) {
            this.ensureCapacity(this.endPos + 1);
            this.array[this.endPos++] = b;
        }
        else {
            this.append((CharSequence)String.valueOf(c));
        }
        return this;
    }
    
    public ByteStringBuffer append(final char[] c) throws NullPointerException {
        if (c == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_ARRAY_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        return this.append(c, 0, c.length);
    }
    
    public ByteStringBuffer append(final char[] c, final int off, final int len) throws NullPointerException, IndexOutOfBoundsException {
        if (c == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_ARRAY_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        if (off < 0 || len < 0 || off + len > c.length) {
            String message;
            if (off < 0) {
                message = UtilityMessages.ERR_BS_BUFFER_OFFSET_NEGATIVE.get(off);
            }
            else if (len < 0) {
                message = UtilityMessages.ERR_BS_BUFFER_LENGTH_NEGATIVE.get(len);
            }
            else {
                message = UtilityMessages.ERR_BS_BUFFER_OFFSET_PLUS_LENGTH_TOO_LARGE.get(off, len, c.length);
            }
            final IndexOutOfBoundsException e2 = new IndexOutOfBoundsException(message);
            Debug.debugCodingError(e2);
            throw e2;
        }
        if (len > 0) {
            this.ensureCapacity(this.endPos + len);
            for (int pos = off, i = 0; i < len; ++i, ++pos) {
                final byte b = (byte)(c[pos] & '\u007f');
                if (b != c[pos]) {
                    final String remainingString = String.valueOf(c, pos, off + len - pos);
                    final byte[] remainingBytes = StaticUtils.getBytes(remainingString);
                    return this.append(remainingBytes);
                }
                this.array[this.endPos++] = b;
            }
        }
        return this;
    }
    
    @Override
    public ByteStringBuffer append(final CharSequence s) throws NullPointerException {
        final String str = s.toString();
        return this.append((CharSequence)str, 0, str.length());
    }
    
    @Override
    public ByteStringBuffer append(final CharSequence s, final int start, final int end) throws NullPointerException, IndexOutOfBoundsException {
        if (s == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_CHAR_SEQUENCE_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        final String string = s.toString();
        final int stringLength = string.length();
        if (start < 0) {
            throw new IndexOutOfBoundsException(UtilityMessages.ERR_BS_BUFFER_START_NEGATIVE.get(start));
        }
        if (start > end) {
            throw new IndexOutOfBoundsException(UtilityMessages.ERR_BS_BUFFER_START_BEYOND_END.get(start, end));
        }
        if (start > stringLength) {
            throw new IndexOutOfBoundsException(UtilityMessages.ERR_BS_BUFFER_START_BEYOND_LENGTH.get(start, stringLength));
        }
        if (end > stringLength) {
            throw new IndexOutOfBoundsException(UtilityMessages.ERR_BS_BUFFER_END_BEYOND_LENGTH.get(start, stringLength));
        }
        if (start < end) {
            this.ensureCapacity(this.endPos + (end - start));
            for (int pos = start; pos < end; ++pos) {
                final char c = string.charAt(pos);
                if (c > '\u007f') {
                    final String remainingString = string.substring(pos, end);
                    final byte[] remainingBytes = StaticUtils.getBytes(remainingString);
                    return this.append(remainingBytes);
                }
                this.array[this.endPos++] = (byte)(c & '\u007f');
            }
        }
        return this;
    }
    
    public ByteStringBuffer append(final int i) {
        final int length = getBytes(i);
        return this.append(ByteStringBuffer.TEMP_NUMBER_BUFFER.get(), 0, length);
    }
    
    public ByteStringBuffer append(final long l) {
        final int length = getBytes(l);
        return this.append(ByteStringBuffer.TEMP_NUMBER_BUFFER.get(), 0, length);
    }
    
    public ByteStringBuffer insert(final int pos, final boolean b) throws IndexOutOfBoundsException {
        if (b) {
            return this.insert(pos, ByteStringBuffer.TRUE_VALUE_BYTES, 0, 4);
        }
        return this.insert(pos, ByteStringBuffer.FALSE_VALUE_BYTES, 0, 5);
    }
    
    public ByteStringBuffer insert(final int pos, final byte b) throws IndexOutOfBoundsException {
        if (pos < 0 || pos > this.endPos) {
            String message;
            if (pos < 0) {
                message = UtilityMessages.ERR_BS_BUFFER_POS_NEGATIVE.get(pos);
            }
            else {
                message = UtilityMessages.ERR_BS_BUFFER_POS_TOO_LARGE.get(pos, this.endPos);
            }
            final IndexOutOfBoundsException e = new IndexOutOfBoundsException(message);
            Debug.debugCodingError(e);
            throw e;
        }
        if (pos == this.endPos) {
            return this.append(b);
        }
        this.ensureCapacity(this.endPos + 1);
        System.arraycopy(this.array, pos, this.array, pos + 1, this.endPos - pos);
        this.array[pos] = b;
        ++this.endPos;
        return this;
    }
    
    public ByteStringBuffer insert(final int pos, final byte[] b) throws NullPointerException, IndexOutOfBoundsException {
        if (b == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_ARRAY_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        return this.insert(pos, b, 0, b.length);
    }
    
    public ByteStringBuffer insert(final int pos, final byte[] b, final int off, final int len) throws NullPointerException, IndexOutOfBoundsException {
        if (b == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_ARRAY_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        if (pos < 0 || pos > this.endPos || off < 0 || len < 0 || off + len > b.length) {
            String message;
            if (pos < 0) {
                message = UtilityMessages.ERR_BS_BUFFER_POS_NEGATIVE.get(pos);
            }
            else if (pos > this.endPos) {
                message = UtilityMessages.ERR_BS_BUFFER_POS_TOO_LARGE.get(pos, this.endPos);
            }
            else if (off < 0) {
                message = UtilityMessages.ERR_BS_BUFFER_OFFSET_NEGATIVE.get(off);
            }
            else if (len < 0) {
                message = UtilityMessages.ERR_BS_BUFFER_LENGTH_NEGATIVE.get(len);
            }
            else {
                message = UtilityMessages.ERR_BS_BUFFER_OFFSET_PLUS_LENGTH_TOO_LARGE.get(off, len, b.length);
            }
            final IndexOutOfBoundsException e2 = new IndexOutOfBoundsException(message);
            Debug.debugCodingError(e2);
            throw e2;
        }
        if (len == 0) {
            return this;
        }
        if (pos == this.endPos) {
            return this.append(b, off, len);
        }
        this.ensureCapacity(this.endPos + len);
        System.arraycopy(this.array, pos, this.array, pos + len, this.endPos - pos);
        System.arraycopy(b, off, this.array, pos, len);
        this.endPos += len;
        return this;
    }
    
    public ByteStringBuffer insert(final int pos, final ByteString b) throws NullPointerException, IndexOutOfBoundsException {
        if (b == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_BYTE_STRING_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        return this.insert(pos, b.getValue());
    }
    
    public ByteStringBuffer insert(final int pos, final ByteStringBuffer buffer) throws NullPointerException, IndexOutOfBoundsException {
        if (buffer == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_BUFFER_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        return this.insert(pos, buffer.array, 0, buffer.endPos);
    }
    
    public ByteStringBuffer insert(final int pos, final char c) throws IndexOutOfBoundsException {
        if (pos < 0 || pos > this.endPos) {
            String message;
            if (pos < 0) {
                message = UtilityMessages.ERR_BS_BUFFER_POS_NEGATIVE.get(pos);
            }
            else {
                message = UtilityMessages.ERR_BS_BUFFER_POS_TOO_LARGE.get(pos, this.endPos);
            }
            final IndexOutOfBoundsException e = new IndexOutOfBoundsException(message);
            Debug.debugCodingError(e);
            throw e;
        }
        if (pos == this.endPos) {
            return this.append(c);
        }
        final byte b = (byte)(c & '\u007f');
        if (b == c) {
            this.ensureCapacity(this.endPos + 1);
            System.arraycopy(this.array, pos, this.array, pos + 1, this.endPos - pos);
            this.array[pos] = b;
            ++this.endPos;
        }
        else {
            this.insert(pos, String.valueOf(c));
        }
        return this;
    }
    
    public ByteStringBuffer insert(final int pos, final char[] c) throws NullPointerException, IndexOutOfBoundsException {
        if (c == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_ARRAY_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        return this.insert(pos, new String(c, 0, c.length));
    }
    
    public ByteStringBuffer insert(final int pos, final char[] c, final int off, final int len) throws NullPointerException, IndexOutOfBoundsException {
        if (c == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_ARRAY_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        return this.insert(pos, new String(c, off, len));
    }
    
    public ByteStringBuffer insert(final int pos, final CharSequence s) throws NullPointerException, IndexOutOfBoundsException {
        if (s == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_CHAR_SEQUENCE_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        if (pos < 0 || pos > this.endPos) {
            String message;
            if (pos < 0) {
                message = UtilityMessages.ERR_BS_BUFFER_POS_NEGATIVE.get(pos);
            }
            else {
                message = UtilityMessages.ERR_BS_BUFFER_POS_TOO_LARGE.get(pos, this.endPos);
            }
            final IndexOutOfBoundsException e2 = new IndexOutOfBoundsException(message);
            Debug.debugCodingError(e2);
            throw e2;
        }
        if (pos == this.endPos) {
            return this.append(s);
        }
        return this.insert(pos, StaticUtils.getBytes(s.toString()));
    }
    
    public ByteStringBuffer insert(final int pos, final int i) throws IndexOutOfBoundsException {
        final int length = getBytes(i);
        return this.insert(pos, ByteStringBuffer.TEMP_NUMBER_BUFFER.get(), 0, length);
    }
    
    public ByteStringBuffer insert(final int pos, final long l) throws IndexOutOfBoundsException {
        final int length = getBytes(l);
        return this.insert(pos, ByteStringBuffer.TEMP_NUMBER_BUFFER.get(), 0, length);
    }
    
    public ByteStringBuffer delete(final int len) throws IndexOutOfBoundsException {
        return this.delete(0, len);
    }
    
    public ByteStringBuffer delete(final int off, final int len) throws IndexOutOfBoundsException {
        if (off < 0) {
            throw new IndexOutOfBoundsException(UtilityMessages.ERR_BS_BUFFER_OFFSET_NEGATIVE.get(off));
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException(UtilityMessages.ERR_BS_BUFFER_LENGTH_NEGATIVE.get(len));
        }
        if (off + len > this.endPos) {
            throw new IndexOutOfBoundsException(UtilityMessages.ERR_BS_BUFFER_OFFSET_PLUS_LENGTH_TOO_LARGE.get(off, len, this.endPos));
        }
        if (len == 0) {
            return this;
        }
        if (off == 0) {
            if (len == this.endPos) {
                this.endPos = 0;
                return this;
            }
            final int newEndPos = this.endPos - len;
            System.arraycopy(this.array, len, this.array, 0, newEndPos);
            this.endPos = newEndPos;
            return this;
        }
        else {
            if (off + len == this.endPos) {
                this.endPos = off;
                return this;
            }
            final int bytesToCopy = this.endPos - (off + len);
            System.arraycopy(this.array, off + len, this.array, off, bytesToCopy);
            this.endPos -= len;
            return this;
        }
    }
    
    public ByteStringBuffer set(final boolean b) {
        if (b) {
            return this.set(ByteStringBuffer.TRUE_VALUE_BYTES, 0, 4);
        }
        return this.set(ByteStringBuffer.FALSE_VALUE_BYTES, 0, 5);
    }
    
    public ByteStringBuffer set(final byte b) {
        this.endPos = 0;
        return this.append(b);
    }
    
    public ByteStringBuffer set(final byte[] b) throws NullPointerException {
        if (b == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_ARRAY_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        this.endPos = 0;
        return this.append(b, 0, b.length);
    }
    
    public ByteStringBuffer set(final byte[] b, final int off, final int len) throws NullPointerException, IndexOutOfBoundsException {
        if (b == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_ARRAY_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        if (off < 0 || len < 0 || off + len > b.length) {
            String message;
            if (off < 0) {
                message = UtilityMessages.ERR_BS_BUFFER_OFFSET_NEGATIVE.get(off);
            }
            else if (len < 0) {
                message = UtilityMessages.ERR_BS_BUFFER_LENGTH_NEGATIVE.get(len);
            }
            else {
                message = UtilityMessages.ERR_BS_BUFFER_OFFSET_PLUS_LENGTH_TOO_LARGE.get(off, len, b.length);
            }
            final IndexOutOfBoundsException e2 = new IndexOutOfBoundsException(message);
            Debug.debugCodingError(e2);
            throw e2;
        }
        this.endPos = 0;
        return this.append(b, off, len);
    }
    
    public ByteStringBuffer set(final ByteString b) throws NullPointerException {
        if (b == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_BYTE_STRING_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        this.endPos = 0;
        b.appendValueTo(this);
        return this;
    }
    
    public ByteStringBuffer set(final ByteStringBuffer buffer) throws NullPointerException {
        if (buffer == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_BUFFER_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        this.endPos = 0;
        return this.append(buffer.array, 0, buffer.endPos);
    }
    
    public ByteStringBuffer set(final char c) {
        this.endPos = 0;
        return this.append(c);
    }
    
    public ByteStringBuffer set(final char[] c) throws NullPointerException {
        if (c == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_ARRAY_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        this.endPos = 0;
        return this.append(c, 0, c.length);
    }
    
    public ByteStringBuffer set(final char[] c, final int off, final int len) throws NullPointerException, IndexOutOfBoundsException {
        if (c == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_ARRAY_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        if (off < 0 || len < 0 || off + len > c.length) {
            String message;
            if (off < 0) {
                message = UtilityMessages.ERR_BS_BUFFER_OFFSET_NEGATIVE.get(off);
            }
            else if (len < 0) {
                message = UtilityMessages.ERR_BS_BUFFER_LENGTH_NEGATIVE.get(len);
            }
            else {
                message = UtilityMessages.ERR_BS_BUFFER_OFFSET_PLUS_LENGTH_TOO_LARGE.get(off, len, c.length);
            }
            final IndexOutOfBoundsException e2 = new IndexOutOfBoundsException(message);
            Debug.debugCodingError(e2);
            throw e2;
        }
        this.endPos = 0;
        return this.append(c, off, len);
    }
    
    public ByteStringBuffer set(final CharSequence s) throws NullPointerException {
        if (s == null) {
            final NullPointerException e = new NullPointerException(UtilityMessages.ERR_BS_BUFFER_CHAR_SEQUENCE_NULL.get());
            Debug.debugCodingError(e);
            throw e;
        }
        this.endPos = 0;
        return this.append(s);
    }
    
    public ByteStringBuffer set(final int i) {
        final int length = getBytes(i);
        return this.set(ByteStringBuffer.TEMP_NUMBER_BUFFER.get(), 0, length);
    }
    
    public ByteStringBuffer set(final long l) {
        final int length = getBytes(l);
        return this.set(ByteStringBuffer.TEMP_NUMBER_BUFFER.get(), 0, length);
    }
    
    public ByteStringBuffer clear() {
        this.endPos = 0;
        return this;
    }
    
    public ByteStringBuffer clear(final boolean zero) {
        this.endPos = 0;
        if (zero) {
            Arrays.fill(this.array, (byte)0);
        }
        return this;
    }
    
    public byte[] getBackingArray() {
        return this.array;
    }
    
    public boolean isEmpty() {
        return this.endPos == 0;
    }
    
    public int length() {
        return this.endPos;
    }
    
    public void setLength(final int length) throws IndexOutOfBoundsException {
        if (length < 0) {
            final IndexOutOfBoundsException e = new IndexOutOfBoundsException(UtilityMessages.ERR_BS_BUFFER_LENGTH_NEGATIVE.get(length));
            Debug.debugCodingError(e);
            throw e;
        }
        if (length > this.endPos) {
            this.ensureCapacity(length);
            Arrays.fill(this.array, this.endPos, length, (byte)0);
            this.endPos = length;
        }
        else {
            this.endPos = length;
        }
    }
    
    public int capacity() {
        return this.capacity;
    }
    
    public void ensureCapacity(final int minimumCapacity) {
        if (this.capacity < minimumCapacity) {
            final int newCapacity = Math.max(minimumCapacity, 2 * this.capacity + 2);
            final byte[] newArray = new byte[newCapacity];
            System.arraycopy(this.array, 0, newArray, 0, this.capacity);
            this.array = newArray;
            this.capacity = newCapacity;
        }
    }
    
    public void setCapacity(final int capacity) throws IndexOutOfBoundsException {
        if (capacity < 0) {
            final IndexOutOfBoundsException e = new IndexOutOfBoundsException(UtilityMessages.ERR_BS_BUFFER_CAPACITY_NEGATIVE.get(capacity));
            Debug.debugCodingError(e);
            throw e;
        }
        if (this.capacity == capacity) {
            return;
        }
        if (this.capacity < capacity) {
            final byte[] newArray = new byte[capacity];
            System.arraycopy(this.array, 0, newArray, 0, this.capacity);
            this.array = newArray;
            this.capacity = capacity;
        }
        else {
            final byte[] newArray = new byte[capacity];
            System.arraycopy(this.array, 0, newArray, 0, capacity);
            this.array = newArray;
            this.endPos = Math.min(this.endPos, capacity);
            this.capacity = capacity;
        }
    }
    
    public ByteStringBuffer trimToSize() {
        if (this.endPos != this.capacity) {
            final byte[] newArray = new byte[this.endPos];
            System.arraycopy(this.array, 0, newArray, 0, this.endPos);
            this.array = newArray;
            this.capacity = this.endPos;
        }
        return this;
    }
    
    public byte[] toByteArray() {
        final byte[] newArray = new byte[this.endPos];
        System.arraycopy(this.array, 0, newArray, 0, this.endPos);
        return newArray;
    }
    
    public ByteString toByteString() {
        return new ASN1OctetString(this.toByteArray());
    }
    
    public InputStream asInputStream() {
        return new ByteArrayInputStream(this.array, 0, this.endPos);
    }
    
    public void write(final OutputStream outputStream) throws IOException {
        outputStream.write(this.array, 0, this.endPos);
    }
    
    private static int getBytes(final long l) {
        byte[] b = ByteStringBuffer.TEMP_NUMBER_BUFFER.get();
        if (b == null) {
            b = new byte[20];
            ByteStringBuffer.TEMP_NUMBER_BUFFER.set(b);
        }
        if (l == Long.MIN_VALUE) {
            b[0] = 45;
            b[1] = 57;
            b[3] = (b[2] = 50);
            b[5] = (b[4] = 51);
            b[6] = 55;
            b[7] = 50;
            b[8] = 48;
            b[9] = 51;
            b[10] = 54;
            b[11] = 56;
            b[12] = 53;
            b[13] = 52;
            b[15] = (b[14] = 55);
            b[16] = 53;
            b[17] = 56;
            b[18] = 48;
            b[19] = 56;
            return 20;
        }
        if (l == 0L) {
            b[0] = 48;
            return 1;
        }
        int pos = 0;
        long v = l;
        if (l < 0L) {
            b[0] = 45;
            pos = 1;
            v = Math.abs(l);
        }
        long divisor;
        if (v <= 9L) {
            divisor = 1L;
        }
        else if (v <= 99L) {
            divisor = 10L;
        }
        else if (v <= 999L) {
            divisor = 100L;
        }
        else if (v <= 9999L) {
            divisor = 1000L;
        }
        else if (v <= 99999L) {
            divisor = 10000L;
        }
        else if (v <= 999999L) {
            divisor = 100000L;
        }
        else if (v <= 9999999L) {
            divisor = 1000000L;
        }
        else if (v <= 99999999L) {
            divisor = 10000000L;
        }
        else if (v <= 999999999L) {
            divisor = 100000000L;
        }
        else if (v <= 9999999999L) {
            divisor = 1000000000L;
        }
        else if (v <= 99999999999L) {
            divisor = 10000000000L;
        }
        else if (v <= 999999999999L) {
            divisor = 100000000000L;
        }
        else if (v <= 9999999999999L) {
            divisor = 1000000000000L;
        }
        else if (v <= 99999999999999L) {
            divisor = 10000000000000L;
        }
        else if (v <= 999999999999999L) {
            divisor = 100000000000000L;
        }
        else if (v <= 9999999999999999L) {
            divisor = 1000000000000000L;
        }
        else if (v <= 99999999999999999L) {
            divisor = 10000000000000000L;
        }
        else if (v <= 999999999999999999L) {
            divisor = 100000000000000000L;
        }
        else {
            divisor = 1000000000000000000L;
        }
        while (true) {
            final long digit = v / divisor;
            switch ((int)digit) {
                case 0: {
                    b[pos++] = 48;
                    break;
                }
                case 1: {
                    b[pos++] = 49;
                    break;
                }
                case 2: {
                    b[pos++] = 50;
                    break;
                }
                case 3: {
                    b[pos++] = 51;
                    break;
                }
                case 4: {
                    b[pos++] = 52;
                    break;
                }
                case 5: {
                    b[pos++] = 53;
                    break;
                }
                case 6: {
                    b[pos++] = 54;
                    break;
                }
                case 7: {
                    b[pos++] = 55;
                    break;
                }
                case 8: {
                    b[pos++] = 56;
                    break;
                }
                case 9: {
                    b[pos++] = 57;
                    break;
                }
            }
            if (divisor == 1L) {
                return pos;
            }
            v -= divisor * digit;
            if (v == 0L) {
                break;
            }
            divisor /= 10L;
        }
        while (divisor > 1L) {
            b[pos++] = 48;
            divisor /= 10L;
        }
        return pos;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        for (int i = 0; i < this.endPos; ++i) {
            hashCode += this.array[i];
        }
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof ByteStringBuffer)) {
            return false;
        }
        final ByteStringBuffer b = (ByteStringBuffer)o;
        if (this.endPos != b.endPos) {
            return false;
        }
        for (int i = 0; i < this.endPos; ++i) {
            if (this.array[i] != b.array[i]) {
                return false;
            }
        }
        return true;
    }
    
    public ByteStringBuffer duplicate() {
        final ByteStringBuffer newBuffer = new ByteStringBuffer(this.endPos);
        return newBuffer.append(this);
    }
    
    @Override
    public String toString() {
        return StaticUtils.toUTF8String(this.array, 0, this.endPos);
    }
    
    static {
        FALSE_VALUE_BYTES = StaticUtils.getBytes("false");
        TRUE_VALUE_BYTES = StaticUtils.getBytes("true");
        TEMP_NUMBER_BUFFER = new ThreadLocal<byte[]>();
    }
}
