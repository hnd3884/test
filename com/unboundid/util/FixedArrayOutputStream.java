package com.unboundid.util;

import java.io.IOException;
import java.io.Serializable;
import java.io.OutputStream;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class FixedArrayOutputStream extends OutputStream implements Serializable
{
    private static final long serialVersionUID = 4678108653480347534L;
    private final byte[] array;
    private final int initialPosition;
    private final int length;
    private final int maxPosition;
    private int pos;
    
    public FixedArrayOutputStream(final byte[] array) {
        this(array, 0, array.length);
    }
    
    public FixedArrayOutputStream(final byte[] array, final int pos, final int len) {
        this.array = array;
        this.pos = pos;
        this.initialPosition = pos;
        this.maxPosition = pos + len;
        this.length = len;
        Validator.ensureTrue(pos >= 0, "The position must be greater than or equal to zero.");
        Validator.ensureTrue(len >= 0, "The length must be greater than or equal to zero.");
        Validator.ensureTrue(this.maxPosition <= array.length, "The sum of pos and len must not exceed the array length.");
    }
    
    public byte[] getBackingArray() {
        return this.array;
    }
    
    public int getInitialPosition() {
        return this.initialPosition;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public int getBytesWritten() {
        return this.pos - this.initialPosition;
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void flush() {
    }
    
    @Override
    public void write(final int b) throws IOException {
        if (this.pos >= this.maxPosition) {
            throw new IOException(UtilityMessages.ERR_FIXED_ARRAY_OS_WRITE_BEYOND_END.get());
        }
        this.array[this.pos++] = (byte)b;
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        Validator.ensureTrue(off >= 0, "The provided offset must be greater than or equal to zero.");
        Validator.ensureTrue(len >= 0, "The provided length must be greater than or equal to zero.");
        Validator.ensureTrue(off + len <= b.length, "The sum of off and len must not exceed the array length.");
        if (this.pos + len > this.maxPosition) {
            throw new IOException(UtilityMessages.ERR_FIXED_ARRAY_OS_WRITE_BEYOND_END.get());
        }
        System.arraycopy(b, off, this.array, this.pos, len);
        this.pos += len;
    }
}
