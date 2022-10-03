package sun.java2d.pipe;

import sun.misc.Unsafe;

public class RenderBuffer
{
    protected static final long SIZEOF_BYTE = 1L;
    protected static final long SIZEOF_SHORT = 2L;
    protected static final long SIZEOF_INT = 4L;
    protected static final long SIZEOF_FLOAT = 4L;
    protected static final long SIZEOF_LONG = 8L;
    protected static final long SIZEOF_DOUBLE = 8L;
    private static final int COPY_FROM_ARRAY_THRESHOLD = 6;
    protected final Unsafe unsafe;
    protected final long baseAddress;
    protected final long endAddress;
    protected long curAddress;
    protected final int capacity;
    
    protected RenderBuffer(final int capacity) {
        this.unsafe = Unsafe.getUnsafe();
        final long allocateMemory = this.unsafe.allocateMemory(capacity);
        this.baseAddress = allocateMemory;
        this.curAddress = allocateMemory;
        this.endAddress = this.baseAddress + capacity;
        this.capacity = capacity;
    }
    
    public static RenderBuffer allocate(final int n) {
        return new RenderBuffer(n);
    }
    
    public final long getAddress() {
        return this.baseAddress;
    }
    
    public final int capacity() {
        return this.capacity;
    }
    
    public final int remaining() {
        return (int)(this.endAddress - this.curAddress);
    }
    
    public final int position() {
        return (int)(this.curAddress - this.baseAddress);
    }
    
    public final void position(final long n) {
        this.curAddress = this.baseAddress + n;
    }
    
    public final void clear() {
        this.curAddress = this.baseAddress;
    }
    
    public final RenderBuffer skip(final long n) {
        this.curAddress += n;
        return this;
    }
    
    public final RenderBuffer putByte(final byte b) {
        this.unsafe.putByte(this.curAddress, b);
        ++this.curAddress;
        return this;
    }
    
    public RenderBuffer put(final byte[] array) {
        return this.put(array, 0, array.length);
    }
    
    public RenderBuffer put(final byte[] array, final int n, final int n2) {
        if (n2 > 6) {
            final long n3 = n * 1L + Unsafe.ARRAY_BYTE_BASE_OFFSET;
            final long n4 = n2 * 1L;
            this.unsafe.copyMemory(array, n3, null, this.curAddress, n4);
            this.position(this.position() + n4);
        }
        else {
            for (int n5 = n + n2, i = n; i < n5; ++i) {
                this.putByte(array[i]);
            }
        }
        return this;
    }
    
    public final RenderBuffer putShort(final short n) {
        this.unsafe.putShort(this.curAddress, n);
        this.curAddress += 2L;
        return this;
    }
    
    public RenderBuffer put(final short[] array) {
        return this.put(array, 0, array.length);
    }
    
    public RenderBuffer put(final short[] array, final int n, final int n2) {
        if (n2 > 6) {
            final long n3 = n * 2L + Unsafe.ARRAY_SHORT_BASE_OFFSET;
            final long n4 = n2 * 2L;
            this.unsafe.copyMemory(array, n3, null, this.curAddress, n4);
            this.position(this.position() + n4);
        }
        else {
            for (int n5 = n + n2, i = n; i < n5; ++i) {
                this.putShort(array[i]);
            }
        }
        return this;
    }
    
    public final RenderBuffer putInt(final int n, final int n2) {
        this.unsafe.putInt(this.baseAddress + n, n2);
        return this;
    }
    
    public final RenderBuffer putInt(final int n) {
        this.unsafe.putInt(this.curAddress, n);
        this.curAddress += 4L;
        return this;
    }
    
    public RenderBuffer put(final int[] array) {
        return this.put(array, 0, array.length);
    }
    
    public RenderBuffer put(final int[] array, final int n, final int n2) {
        if (n2 > 6) {
            final long n3 = n * 4L + Unsafe.ARRAY_INT_BASE_OFFSET;
            final long n4 = n2 * 4L;
            this.unsafe.copyMemory(array, n3, null, this.curAddress, n4);
            this.position(this.position() + n4);
        }
        else {
            for (int n5 = n + n2, i = n; i < n5; ++i) {
                this.putInt(array[i]);
            }
        }
        return this;
    }
    
    public final RenderBuffer putFloat(final float n) {
        this.unsafe.putFloat(this.curAddress, n);
        this.curAddress += 4L;
        return this;
    }
    
    public RenderBuffer put(final float[] array) {
        return this.put(array, 0, array.length);
    }
    
    public RenderBuffer put(final float[] array, final int n, final int n2) {
        if (n2 > 6) {
            final long n3 = n * 4L + Unsafe.ARRAY_FLOAT_BASE_OFFSET;
            final long n4 = n2 * 4L;
            this.unsafe.copyMemory(array, n3, null, this.curAddress, n4);
            this.position(this.position() + n4);
        }
        else {
            for (int n5 = n + n2, i = n; i < n5; ++i) {
                this.putFloat(array[i]);
            }
        }
        return this;
    }
    
    public final RenderBuffer putLong(final long n) {
        this.unsafe.putLong(this.curAddress, n);
        this.curAddress += 8L;
        return this;
    }
    
    public RenderBuffer put(final long[] array) {
        return this.put(array, 0, array.length);
    }
    
    public RenderBuffer put(final long[] array, final int n, final int n2) {
        if (n2 > 6) {
            final long n3 = n * 8L + Unsafe.ARRAY_LONG_BASE_OFFSET;
            final long n4 = n2 * 8L;
            this.unsafe.copyMemory(array, n3, null, this.curAddress, n4);
            this.position(this.position() + n4);
        }
        else {
            for (int n5 = n + n2, i = n; i < n5; ++i) {
                this.putLong(array[i]);
            }
        }
        return this;
    }
    
    public final RenderBuffer putDouble(final double n) {
        this.unsafe.putDouble(this.curAddress, n);
        this.curAddress += 8L;
        return this;
    }
}
