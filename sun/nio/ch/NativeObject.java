package sun.nio.ch;

import java.nio.ByteOrder;
import sun.misc.Unsafe;

class NativeObject
{
    protected static final Unsafe unsafe;
    protected long allocationAddress;
    private final long address;
    private static ByteOrder byteOrder;
    private static int pageSize;
    
    NativeObject(final long n) {
        this.allocationAddress = n;
        this.address = n;
    }
    
    NativeObject(final long allocationAddress, final long n) {
        this.allocationAddress = allocationAddress;
        this.address = allocationAddress + n;
    }
    
    protected NativeObject(final int n, final boolean b) {
        if (!b) {
            this.allocationAddress = NativeObject.unsafe.allocateMemory(n);
            this.address = this.allocationAddress;
        }
        else {
            final int pageSize = pageSize();
            final long allocateMemory = NativeObject.unsafe.allocateMemory(n + pageSize);
            this.allocationAddress = allocateMemory;
            this.address = allocateMemory + pageSize - (allocateMemory & (long)(pageSize - 1));
        }
    }
    
    long address() {
        return this.address;
    }
    
    long allocationAddress() {
        return this.allocationAddress;
    }
    
    NativeObject subObject(final int n) {
        return new NativeObject(n + this.address);
    }
    
    NativeObject getObject(final int n) {
        long long1 = 0L;
        switch (addressSize()) {
            case 8: {
                long1 = NativeObject.unsafe.getLong(n + this.address);
                break;
            }
            case 4: {
                long1 = (NativeObject.unsafe.getInt(n + this.address) & -1);
                break;
            }
            default: {
                throw new InternalError("Address size not supported");
            }
        }
        return new NativeObject(long1);
    }
    
    void putObject(final int n, final NativeObject nativeObject) {
        switch (addressSize()) {
            case 8: {
                this.putLong(n, nativeObject.address);
                break;
            }
            case 4: {
                this.putInt(n, (int)(nativeObject.address & -1L));
                break;
            }
            default: {
                throw new InternalError("Address size not supported");
            }
        }
    }
    
    final byte getByte(final int n) {
        return NativeObject.unsafe.getByte(n + this.address);
    }
    
    final void putByte(final int n, final byte b) {
        NativeObject.unsafe.putByte(n + this.address, b);
    }
    
    final short getShort(final int n) {
        return NativeObject.unsafe.getShort(n + this.address);
    }
    
    final void putShort(final int n, final short n2) {
        NativeObject.unsafe.putShort(n + this.address, n2);
    }
    
    final char getChar(final int n) {
        return NativeObject.unsafe.getChar(n + this.address);
    }
    
    final void putChar(final int n, final char c) {
        NativeObject.unsafe.putChar(n + this.address, c);
    }
    
    final int getInt(final int n) {
        return NativeObject.unsafe.getInt(n + this.address);
    }
    
    final void putInt(final int n, final int n2) {
        NativeObject.unsafe.putInt(n + this.address, n2);
    }
    
    final long getLong(final int n) {
        return NativeObject.unsafe.getLong(n + this.address);
    }
    
    final void putLong(final int n, final long n2) {
        NativeObject.unsafe.putLong(n + this.address, n2);
    }
    
    final float getFloat(final int n) {
        return NativeObject.unsafe.getFloat(n + this.address);
    }
    
    final void putFloat(final int n, final float n2) {
        NativeObject.unsafe.putFloat(n + this.address, n2);
    }
    
    final double getDouble(final int n) {
        return NativeObject.unsafe.getDouble(n + this.address);
    }
    
    final void putDouble(final int n, final double n2) {
        NativeObject.unsafe.putDouble(n + this.address, n2);
    }
    
    static int addressSize() {
        return NativeObject.unsafe.addressSize();
    }
    
    static ByteOrder byteOrder() {
        if (NativeObject.byteOrder != null) {
            return NativeObject.byteOrder;
        }
        final long allocateMemory = NativeObject.unsafe.allocateMemory(8L);
        try {
            NativeObject.unsafe.putLong(allocateMemory, 72623859790382856L);
            switch (NativeObject.unsafe.getByte(allocateMemory)) {
                case 1: {
                    NativeObject.byteOrder = ByteOrder.BIG_ENDIAN;
                    break;
                }
                case 8: {
                    NativeObject.byteOrder = ByteOrder.LITTLE_ENDIAN;
                    break;
                }
                default: {
                    assert false;
                    break;
                }
            }
        }
        finally {
            NativeObject.unsafe.freeMemory(allocateMemory);
        }
        return NativeObject.byteOrder;
    }
    
    static int pageSize() {
        if (NativeObject.pageSize == -1) {
            NativeObject.pageSize = NativeObject.unsafe.pageSize();
        }
        return NativeObject.pageSize;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
        NativeObject.byteOrder = null;
        NativeObject.pageSize = -1;
    }
}
