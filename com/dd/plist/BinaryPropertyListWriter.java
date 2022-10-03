package com.dd.plist;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Iterator;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.OutputStream;

public final class BinaryPropertyListWriter
{
    private static final int VERSION_00 = 0;
    private static final int VERSION_10 = 10;
    private static final int VERSION_15 = 15;
    private static final int VERSION_20 = 20;
    private int version;
    private final OutputStream out;
    private long count;
    private final Map<NSObject, Integer> idMap;
    private int idSizeInBytes;
    
    BinaryPropertyListWriter(final OutputStream outStr) throws IOException {
        this.version = 0;
        this.idMap = new LinkedHashMap<NSObject, Integer>();
        this.out = new BufferedOutputStream(outStr);
    }
    
    BinaryPropertyListWriter(final OutputStream outStr, final int version) throws IOException {
        this.version = 0;
        this.idMap = new LinkedHashMap<NSObject, Integer>();
        this.version = version;
        this.out = new BufferedOutputStream(outStr);
    }
    
    private static int getMinimumRequiredVersion(final NSObject root) {
        int minVersion = 0;
        if (root == null) {
            minVersion = 10;
        }
        if (root instanceof NSDictionary) {
            final NSDictionary dict = (NSDictionary)root;
            for (final NSObject o : dict.getHashMap().values()) {
                final int v = getMinimumRequiredVersion(o);
                if (v > minVersion) {
                    minVersion = v;
                }
            }
        }
        else if (root instanceof NSArray) {
            final NSArray array = (NSArray)root;
            for (final NSObject o2 : array.getArray()) {
                final int v2 = getMinimumRequiredVersion(o2);
                if (v2 > minVersion) {
                    minVersion = v2;
                }
            }
        }
        else if (root instanceof NSSet) {
            minVersion = 10;
            final NSSet set = (NSSet)root;
            for (final NSObject o2 : set.allObjects()) {
                final int v2 = getMinimumRequiredVersion(o2);
                if (v2 > minVersion) {
                    minVersion = v2;
                }
            }
        }
        return minVersion;
    }
    
    public static void write(final File file, final NSObject root) throws IOException {
        final OutputStream out = new FileOutputStream(file);
        write(out, root);
        out.close();
    }
    
    public static void write(final OutputStream out, final NSObject root) throws IOException {
        final int minVersion = getMinimumRequiredVersion(root);
        if (minVersion > 0) {
            final String versionString = (minVersion == 10) ? "v1.0" : ((minVersion == 15) ? "v1.5" : ((minVersion == 20) ? "v2.0" : "v0.0"));
            throw new IOException("The given property list structure cannot be saved. The required version of the binary format (" + versionString + ") is not yet supported.");
        }
        final BinaryPropertyListWriter w = new BinaryPropertyListWriter(out, minVersion);
        w.write(root);
    }
    
    public static byte[] writeToArray(final NSObject root) throws IOException {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        write(bout, root);
        return bout.toByteArray();
    }
    
    void write(final NSObject root) throws IOException {
        this.write(new byte[] { 98, 112, 108, 105, 115, 116 });
        switch (this.version) {
            case 0: {
                this.write(new byte[] { 48, 48 });
                break;
            }
            case 10: {
                this.write(new byte[] { 49, 48 });
                break;
            }
            case 15: {
                this.write(new byte[] { 49, 53 });
                break;
            }
            case 20: {
                this.write(new byte[] { 50, 48 });
                break;
            }
        }
        root.assignIDs(this);
        this.idSizeInBytes = computeIdSizeInBytes(this.idMap.size());
        final long[] offsets = new long[this.idMap.size()];
        for (final Map.Entry<NSObject, Integer> entry : this.idMap.entrySet()) {
            final NSObject obj = entry.getKey();
            final int id = entry.getValue();
            offsets[id] = this.count;
            if (obj == null) {
                this.write(0);
            }
            else {
                obj.toBinary(this);
            }
        }
        final long offsetTableOffset = this.count;
        final int offsetSizeInBytes = this.computeOffsetSizeInBytes(this.count);
        for (final long offset : offsets) {
            this.writeBytes(offset, offsetSizeInBytes);
        }
        if (this.version != 15) {
            this.write(new byte[6]);
            this.write(offsetSizeInBytes);
            this.write(this.idSizeInBytes);
            this.writeLong(this.idMap.size());
            this.writeLong(this.idMap.get(root));
            this.writeLong(offsetTableOffset);
        }
        this.out.flush();
    }
    
    void assignID(final NSObject obj) {
        if (!this.idMap.containsKey(obj)) {
            this.idMap.put(obj, this.idMap.size());
        }
    }
    
    int getID(final NSObject obj) {
        return this.idMap.get(obj);
    }
    
    private static int computeIdSizeInBytes(final int numberOfIds) {
        if (numberOfIds < 256) {
            return 1;
        }
        if (numberOfIds < 65536) {
            return 2;
        }
        return 4;
    }
    
    private int computeOffsetSizeInBytes(final long maxOffset) {
        if (maxOffset < 256L) {
            return 1;
        }
        if (maxOffset < 65536L) {
            return 2;
        }
        if (maxOffset < 4294967296L) {
            return 4;
        }
        return 8;
    }
    
    void writeIntHeader(final int kind, final int value) throws IOException {
        assert value >= 0;
        if (value < 15) {
            this.write((kind << 4) + value);
        }
        else if (value < 256) {
            this.write((kind << 4) + 15);
            this.write(16);
            this.writeBytes(value, 1);
        }
        else if (value < 65536) {
            this.write((kind << 4) + 15);
            this.write(17);
            this.writeBytes(value, 2);
        }
        else {
            this.write((kind << 4) + 15);
            this.write(18);
            this.writeBytes(value, 4);
        }
    }
    
    void write(final int b) throws IOException {
        this.out.write(b);
        ++this.count;
    }
    
    void write(final byte[] bytes) throws IOException {
        this.out.write(bytes);
        this.count += bytes.length;
    }
    
    void writeBytes(final long value, final int bytes) throws IOException {
        for (int i = bytes - 1; i >= 0; --i) {
            this.write((int)(value >> 8 * i));
        }
    }
    
    void writeID(final int id) throws IOException {
        this.writeBytes(id, this.idSizeInBytes);
    }
    
    void writeLong(final long value) throws IOException {
        this.writeBytes(value, 8);
    }
    
    void writeDouble(final double value) throws IOException {
        this.writeLong(Double.doubleToRawLongBits(value));
    }
}
