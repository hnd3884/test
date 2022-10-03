package org.apache.poi.xssf.streaming;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;

class Zip64Impl
{
    private static final long PK0102 = 33639248L;
    private static final long PK0304 = 67324752L;
    private static final long PK0506 = 101010256L;
    private static final long PK0708 = 134695760L;
    private static final int VERSION_20 = 20;
    private static final int VERSION_45 = 45;
    private static final int DATA_DESCRIPTOR_USED = 8;
    private static final int ZIP64_FIELD = 1;
    private static final long MAX32 = 4294967295L;
    private final OutputStream out;
    private int written;
    
    Zip64Impl(final OutputStream out) {
        this.written = 0;
        this.out = out;
    }
    
    int writeLFH(final Entry entry) throws IOException {
        this.written = 0;
        this.writeInt(67324752L);
        this.writeShort(45);
        this.writeShort(8);
        this.writeShort(8);
        this.writeInt(0L);
        this.writeInt(entry.crc);
        this.writeInt(0L);
        this.writeInt(0L);
        this.writeShort(entry.filename.length());
        this.writeShort(0);
        final byte[] filenameBytes = entry.filename.getBytes(StandardCharsets.US_ASCII);
        this.out.write(filenameBytes);
        return this.written + filenameBytes.length;
    }
    
    int writeDAT(final Entry entry) throws IOException {
        this.written = 0;
        this.writeInt(134695760L);
        this.writeInt(entry.crc);
        this.writeLong(entry.compressedSize);
        this.writeLong(entry.size);
        return this.written;
    }
    
    int writeCEN(final Entry entry) throws IOException {
        this.written = 0;
        final boolean useZip64 = entry.size > 4294967295L;
        this.writeInt(33639248L);
        this.writeShort(45);
        this.writeShort(useZip64 ? 45 : 20);
        this.writeShort(8);
        this.writeShort(8);
        this.writeInt(0L);
        this.writeInt(entry.crc);
        this.writeInt(entry.compressedSize);
        this.writeInt(useZip64 ? 4294967295L : entry.size);
        this.writeShort(entry.filename.length());
        this.writeShort(useZip64 ? 12 : 0);
        this.writeShort(0);
        this.writeShort(0);
        this.writeShort(0);
        this.writeInt(0L);
        this.writeInt(entry.offset);
        final byte[] filenameBytes = entry.filename.getBytes(StandardCharsets.US_ASCII);
        this.out.write(filenameBytes);
        if (useZip64) {
            this.writeShort(1);
            this.writeShort(8);
            this.writeLong(entry.size);
        }
        return this.written + filenameBytes.length;
    }
    
    int writeEND(final int entriesCount, final int offset, final int length) throws IOException {
        this.written = 0;
        this.writeInt(101010256L);
        this.writeShort(0);
        this.writeShort(0);
        this.writeShort(entriesCount);
        this.writeShort(entriesCount);
        this.writeInt(length);
        this.writeInt(offset);
        this.writeShort(0);
        return this.written;
    }
    
    private void writeShort(final int v) throws IOException {
        final OutputStream out = this.out;
        out.write(v >>> 0 & 0xFF);
        out.write(v >>> 8 & 0xFF);
        this.written += 2;
    }
    
    private void writeInt(final long v) throws IOException {
        final OutputStream out = this.out;
        out.write((int)(v >>> 0 & 0xFFL));
        out.write((int)(v >>> 8 & 0xFFL));
        out.write((int)(v >>> 16 & 0xFFL));
        out.write((int)(v >>> 24 & 0xFFL));
        this.written += 4;
    }
    
    private void writeLong(final long v) throws IOException {
        final OutputStream out = this.out;
        out.write((int)(v >>> 0 & 0xFFL));
        out.write((int)(v >>> 8 & 0xFFL));
        out.write((int)(v >>> 16 & 0xFFL));
        out.write((int)(v >>> 24 & 0xFFL));
        out.write((int)(v >>> 32 & 0xFFL));
        out.write((int)(v >>> 40 & 0xFFL));
        out.write((int)(v >>> 48 & 0xFFL));
        out.write((int)(v >>> 56 & 0xFFL));
        this.written += 8;
    }
    
    static class Entry
    {
        final String filename;
        long crc;
        long size;
        int compressedSize;
        int offset;
        
        Entry(final String filename) {
            this.filename = filename;
        }
    }
}
