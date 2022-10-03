package sun.management.counter.perf;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;

class Prologue
{
    private static final byte PERFDATA_BIG_ENDIAN = 0;
    private static final byte PERFDATA_LITTLE_ENDIAN = 1;
    private static final int PERFDATA_MAGIC = -889274176;
    private ByteBuffer header;
    private int magic;
    
    Prologue(final ByteBuffer byteBuffer) {
        (this.header = byteBuffer.duplicate()).order(ByteOrder.BIG_ENDIAN);
        this.header.position(0);
        this.magic = this.header.getInt();
        if (this.magic != -889274176) {
            throw new InstrumentationException("Bad Magic: " + Integer.toHexString(this.getMagic()));
        }
        this.header.order(this.getByteOrder());
        final int majorVersion = this.getMajorVersion();
        final int minorVersion = this.getMinorVersion();
        if (majorVersion < 2) {
            throw new InstrumentationException("Unsupported version: " + majorVersion + "." + minorVersion);
        }
        this.header.limit(32);
    }
    
    public int getMagic() {
        return this.magic;
    }
    
    public int getMajorVersion() {
        this.header.position(5);
        return this.header.get();
    }
    
    public int getMinorVersion() {
        this.header.position(6);
        return this.header.get();
    }
    
    public ByteOrder getByteOrder() {
        this.header.position(4);
        if (this.header.get() == 0) {
            return ByteOrder.BIG_ENDIAN;
        }
        return ByteOrder.LITTLE_ENDIAN;
    }
    
    public int getEntryOffset() {
        this.header.position(24);
        return this.header.getInt();
    }
    
    public int getUsed() {
        this.header.position(8);
        return this.header.getInt();
    }
    
    public int getOverflow() {
        this.header.position(12);
        return this.header.getInt();
    }
    
    public long getModificationTimeStamp() {
        this.header.position(16);
        return this.header.getLong();
    }
    
    public int getNumEntries() {
        this.header.position(28);
        return this.header.getInt();
    }
    
    public boolean isAccessible() {
        this.header.position(7);
        return this.header.get() != 0;
    }
    
    private class PrologueFieldOffset
    {
        private static final int SIZEOF_BYTE = 1;
        private static final int SIZEOF_INT = 4;
        private static final int SIZEOF_LONG = 8;
        private static final int MAGIC_SIZE = 4;
        private static final int BYTE_ORDER_SIZE = 1;
        private static final int MAJOR_SIZE = 1;
        private static final int MINOR_SIZE = 1;
        private static final int ACCESSIBLE_SIZE = 1;
        private static final int USED_SIZE = 4;
        private static final int OVERFLOW_SIZE = 4;
        private static final int MOD_TIMESTAMP_SIZE = 8;
        private static final int ENTRY_OFFSET_SIZE = 4;
        private static final int NUM_ENTRIES_SIZE = 4;
        static final int MAGIC = 0;
        static final int BYTE_ORDER = 4;
        static final int MAJOR_VERSION = 5;
        static final int MINOR_VERSION = 6;
        static final int ACCESSIBLE = 7;
        static final int USED = 8;
        static final int OVERFLOW = 12;
        static final int MOD_TIMESTAMP = 16;
        static final int ENTRY_OFFSET = 24;
        static final int NUM_ENTRIES = 28;
        static final int PROLOGUE_2_0_SIZE = 32;
    }
}
