package com.sun.media.sound;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public final class RIFFWriter extends OutputStream
{
    private int chunktype;
    private RandomAccessWriter raf;
    private final long chunksizepointer;
    private final long startpointer;
    private RIFFWriter childchunk;
    private boolean open;
    private boolean writeoverride;
    
    public RIFFWriter(final String s, final String s2) throws IOException {
        this(new RandomAccessFileWriter(s), s2, 0);
    }
    
    public RIFFWriter(final File file, final String s) throws IOException {
        this(new RandomAccessFileWriter(file), s, 0);
    }
    
    public RIFFWriter(final OutputStream outputStream, final String s) throws IOException {
        this(new RandomAccessByteWriter(outputStream), s, 0);
    }
    
    private RIFFWriter(final RandomAccessWriter raf, final String s, final int chunktype) throws IOException {
        this.chunktype = 0;
        this.childchunk = null;
        this.open = true;
        this.writeoverride = false;
        if (chunktype == 0 && raf.length() != 0L) {
            raf.setLength(0L);
        }
        this.raf = raf;
        if (raf.getPointer() % 2L != 0L) {
            raf.write(0);
        }
        if (chunktype == 0) {
            raf.write("RIFF".getBytes("ascii"));
        }
        else if (chunktype == 1) {
            raf.write("LIST".getBytes("ascii"));
        }
        else {
            raf.write((s + "    ").substring(0, 4).getBytes("ascii"));
        }
        this.chunksizepointer = raf.getPointer();
        this.chunktype = 2;
        this.writeUnsignedInt(0L);
        this.chunktype = chunktype;
        this.startpointer = raf.getPointer();
        if (chunktype != 2) {
            raf.write((s + "    ").substring(0, 4).getBytes("ascii"));
        }
    }
    
    public void seek(final long n) throws IOException {
        this.raf.seek(n);
    }
    
    public long getFilePointer() throws IOException {
        return this.raf.getPointer();
    }
    
    public void setWriteOverride(final boolean writeoverride) {
        this.writeoverride = writeoverride;
    }
    
    public boolean getWriteOverride() {
        return this.writeoverride;
    }
    
    @Override
    public void close() throws IOException {
        if (!this.open) {
            return;
        }
        if (this.childchunk != null) {
            this.childchunk.close();
            this.childchunk = null;
        }
        final int chunktype = this.chunktype;
        final long pointer = this.raf.getPointer();
        this.raf.seek(this.chunksizepointer);
        this.chunktype = 2;
        this.writeUnsignedInt(pointer - this.startpointer);
        if (chunktype == 0) {
            this.raf.close();
        }
        else {
            this.raf.seek(pointer);
        }
        this.open = false;
        this.raf = null;
    }
    
    @Override
    public void write(final int n) throws IOException {
        if (!this.writeoverride) {
            if (this.chunktype != 2) {
                throw new IllegalArgumentException("Only chunks can write bytes!");
            }
            if (this.childchunk != null) {
                this.childchunk.close();
                this.childchunk = null;
            }
        }
        this.raf.write(n);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        if (!this.writeoverride) {
            if (this.chunktype != 2) {
                throw new IllegalArgumentException("Only chunks can write bytes!");
            }
            if (this.childchunk != null) {
                this.childchunk.close();
                this.childchunk = null;
            }
        }
        this.raf.write(array, n, n2);
    }
    
    public RIFFWriter writeList(final String s) throws IOException {
        if (this.chunktype == 2) {
            throw new IllegalArgumentException("Only LIST and RIFF can write lists!");
        }
        if (this.childchunk != null) {
            this.childchunk.close();
            this.childchunk = null;
        }
        return this.childchunk = new RIFFWriter(this.raf, s, 1);
    }
    
    public RIFFWriter writeChunk(final String s) throws IOException {
        if (this.chunktype == 2) {
            throw new IllegalArgumentException("Only LIST and RIFF can write chunks!");
        }
        if (this.childchunk != null) {
            this.childchunk.close();
            this.childchunk = null;
        }
        return this.childchunk = new RIFFWriter(this.raf, s, 2);
    }
    
    public void writeString(final String s) throws IOException {
        this.write(s.getBytes());
    }
    
    public void writeString(final String s, final int n) throws IOException {
        final byte[] bytes = s.getBytes();
        if (bytes.length > n) {
            this.write(bytes, 0, n);
        }
        else {
            this.write(bytes);
            for (int i = bytes.length; i < n; ++i) {
                this.write(0);
            }
        }
    }
    
    public void writeByte(final int n) throws IOException {
        this.write(n);
    }
    
    public void writeShort(final short n) throws IOException {
        this.write(n >>> 0 & 0xFF);
        this.write(n >>> 8 & 0xFF);
    }
    
    public void writeInt(final int n) throws IOException {
        this.write(n >>> 0 & 0xFF);
        this.write(n >>> 8 & 0xFF);
        this.write(n >>> 16 & 0xFF);
        this.write(n >>> 24 & 0xFF);
    }
    
    public void writeLong(final long n) throws IOException {
        this.write((int)(n >>> 0) & 0xFF);
        this.write((int)(n >>> 8) & 0xFF);
        this.write((int)(n >>> 16) & 0xFF);
        this.write((int)(n >>> 24) & 0xFF);
        this.write((int)(n >>> 32) & 0xFF);
        this.write((int)(n >>> 40) & 0xFF);
        this.write((int)(n >>> 48) & 0xFF);
        this.write((int)(n >>> 56) & 0xFF);
    }
    
    public void writeUnsignedByte(final int n) throws IOException {
        this.writeByte((byte)n);
    }
    
    public void writeUnsignedShort(final int n) throws IOException {
        this.writeShort((short)n);
    }
    
    public void writeUnsignedInt(final long n) throws IOException {
        this.writeInt((int)n);
    }
    
    private static class RandomAccessFileWriter implements RandomAccessWriter
    {
        RandomAccessFile raf;
        
        RandomAccessFileWriter(final File file) throws FileNotFoundException {
            this.raf = new RandomAccessFile(file, "rw");
        }
        
        RandomAccessFileWriter(final String s) throws FileNotFoundException {
            this.raf = new RandomAccessFile(s, "rw");
        }
        
        @Override
        public void seek(final long n) throws IOException {
            this.raf.seek(n);
        }
        
        @Override
        public long getPointer() throws IOException {
            return this.raf.getFilePointer();
        }
        
        @Override
        public void close() throws IOException {
            this.raf.close();
        }
        
        @Override
        public void write(final int n) throws IOException {
            this.raf.write(n);
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            this.raf.write(array, n, n2);
        }
        
        @Override
        public void write(final byte[] array) throws IOException {
            this.raf.write(array);
        }
        
        @Override
        public long length() throws IOException {
            return this.raf.length();
        }
        
        @Override
        public void setLength(final long length) throws IOException {
            this.raf.setLength(length);
        }
    }
    
    private static class RandomAccessByteWriter implements RandomAccessWriter
    {
        byte[] buff;
        int length;
        int pos;
        byte[] s;
        final OutputStream stream;
        
        RandomAccessByteWriter(final OutputStream stream) {
            this.buff = new byte[32];
            this.length = 0;
            this.pos = 0;
            this.stream = stream;
        }
        
        @Override
        public void seek(final long n) throws IOException {
            this.pos = (int)n;
        }
        
        @Override
        public long getPointer() throws IOException {
            return this.pos;
        }
        
        @Override
        public void close() throws IOException {
            this.stream.write(this.buff, 0, this.length);
            this.stream.close();
        }
        
        @Override
        public void write(final int n) throws IOException {
            if (this.s == null) {
                this.s = new byte[1];
            }
            this.s[0] = (byte)n;
            this.write(this.s, 0, 1);
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            final int n3 = this.pos + n2;
            if (n3 > this.length) {
                this.setLength(n3);
            }
            for (int n4 = n + n2, i = n; i < n4; ++i) {
                this.buff[this.pos++] = array[i];
            }
        }
        
        @Override
        public void write(final byte[] array) throws IOException {
            this.write(array, 0, array.length);
        }
        
        @Override
        public long length() throws IOException {
            return this.length;
        }
        
        @Override
        public void setLength(final long n) throws IOException {
            this.length = (int)n;
            if (this.length > this.buff.length) {
                final byte[] buff = new byte[Math.max(this.buff.length << 1, this.length)];
                System.arraycopy(this.buff, 0, buff, 0, this.buff.length);
                this.buff = buff;
            }
        }
    }
    
    private interface RandomAccessWriter
    {
        void seek(final long p0) throws IOException;
        
        long getPointer() throws IOException;
        
        void close() throws IOException;
        
        void write(final int p0) throws IOException;
        
        void write(final byte[] p0, final int p1, final int p2) throws IOException;
        
        void write(final byte[] p0) throws IOException;
        
        long length() throws IOException;
        
        void setLength(final long p0) throws IOException;
    }
}
