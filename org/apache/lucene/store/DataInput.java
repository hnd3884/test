package org.apache.lucene.store;

import java.util.TreeSet;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import org.apache.lucene.util.BitUtil;
import java.io.IOException;

public abstract class DataInput implements Cloneable
{
    private static final int SKIP_BUFFER_SIZE = 1024;
    private byte[] skipBuffer;
    
    public abstract byte readByte() throws IOException;
    
    public abstract void readBytes(final byte[] p0, final int p1, final int p2) throws IOException;
    
    public void readBytes(final byte[] b, final int offset, final int len, final boolean useBuffer) throws IOException {
        this.readBytes(b, offset, len);
    }
    
    public short readShort() throws IOException {
        return (short)((this.readByte() & 0xFF) << 8 | (this.readByte() & 0xFF));
    }
    
    public int readInt() throws IOException {
        return (this.readByte() & 0xFF) << 24 | (this.readByte() & 0xFF) << 16 | (this.readByte() & 0xFF) << 8 | (this.readByte() & 0xFF);
    }
    
    public int readVInt() throws IOException {
        byte b = this.readByte();
        if (b >= 0) {
            return b;
        }
        int i = b & 0x7F;
        b = this.readByte();
        i |= (b & 0x7F) << 7;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= (b & 0x7F) << 14;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= (b & 0x7F) << 21;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= (b & 0xF) << 28;
        if ((b & 0xF0) == 0x0) {
            return i;
        }
        throw new IOException("Invalid vInt detected (too many bits)");
    }
    
    public int readZInt() throws IOException {
        return BitUtil.zigZagDecode(this.readVInt());
    }
    
    public long readLong() throws IOException {
        return (long)this.readInt() << 32 | ((long)this.readInt() & 0xFFFFFFFFL);
    }
    
    public long readVLong() throws IOException {
        return this.readVLong(false);
    }
    
    private long readVLong(final boolean allowNegative) throws IOException {
        byte b = this.readByte();
        if (b >= 0) {
            return b;
        }
        long i = (long)b & 0x7FL;
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 7;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 14;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 21;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 28;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 35;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 42;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 49;
        if (b >= 0) {
            return i;
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 56;
        if (b >= 0) {
            return i;
        }
        if (!allowNegative) {
            throw new IOException("Invalid vLong detected (negative values disallowed)");
        }
        b = this.readByte();
        i |= ((long)b & 0x7FL) << 63;
        if (b == 0 || b == 1) {
            return i;
        }
        throw new IOException("Invalid vLong detected (more than 64 bits)");
    }
    
    public long readZLong() throws IOException {
        return BitUtil.zigZagDecode(this.readVLong(true));
    }
    
    public String readString() throws IOException {
        final int length = this.readVInt();
        final byte[] bytes = new byte[length];
        this.readBytes(bytes, 0, length);
        return new String(bytes, 0, length, StandardCharsets.UTF_8);
    }
    
    public DataInput clone() {
        try {
            return (DataInput)super.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new Error("This cannot happen: Failing to clone DataInput");
        }
    }
    
    @Deprecated
    public Map<String, String> readStringStringMap() throws IOException {
        final Map<String, String> map = new HashMap<String, String>();
        for (int count = this.readInt(), i = 0; i < count; ++i) {
            final String key = this.readString();
            final String val = this.readString();
            map.put(key, val);
        }
        return map;
    }
    
    public Map<String, String> readMapOfStrings() throws IOException {
        final int count = this.readVInt();
        if (count == 0) {
            return Collections.emptyMap();
        }
        if (count == 1) {
            return Collections.singletonMap(this.readString(), this.readString());
        }
        final Map<String, String> map = (Map<String, String>)((count > 10) ? new HashMap<Object, Object>() : new TreeMap<Object, Object>());
        for (int i = 0; i < count; ++i) {
            final String key = this.readString();
            final String val = this.readString();
            map.put(key, val);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)map);
    }
    
    @Deprecated
    public Set<String> readStringSet() throws IOException {
        final Set<String> set = new HashSet<String>();
        for (int count = this.readInt(), i = 0; i < count; ++i) {
            set.add(this.readString());
        }
        return set;
    }
    
    public Set<String> readSetOfStrings() throws IOException {
        final int count = this.readVInt();
        if (count == 0) {
            return Collections.emptySet();
        }
        if (count == 1) {
            return Collections.singleton(this.readString());
        }
        final Set<String> set = (Set<String>)((count > 10) ? new HashSet<Object>() : new TreeSet<Object>());
        for (int i = 0; i < count; ++i) {
            set.add(this.readString());
        }
        return Collections.unmodifiableSet((Set<? extends String>)set);
    }
    
    public void skipBytes(final long numBytes) throws IOException {
        if (numBytes < 0L) {
            throw new IllegalArgumentException("numBytes must be >= 0, got " + numBytes);
        }
        if (this.skipBuffer == null) {
            this.skipBuffer = new byte[1024];
        }
        assert this.skipBuffer.length == 1024;
        int step;
        for (long skipped = 0L; skipped < numBytes; skipped += step) {
            step = (int)Math.min(1024L, numBytes - skipped);
            this.readBytes(this.skipBuffer, 0, step, false);
        }
    }
}
