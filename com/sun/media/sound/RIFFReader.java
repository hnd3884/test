package com.sun.media.sound;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class RIFFReader extends InputStream
{
    private final RIFFReader root;
    private long filepointer;
    private final String fourcc;
    private String riff_type;
    private long ckSize;
    private InputStream stream;
    private long avail;
    private RIFFReader lastiterator;
    
    public RIFFReader(final InputStream stream) throws IOException {
        this.filepointer = 0L;
        this.riff_type = null;
        this.ckSize = 2147483647L;
        this.avail = 2147483647L;
        this.lastiterator = null;
        if (stream instanceof RIFFReader) {
            this.root = ((RIFFReader)stream).root;
        }
        else {
            this.root = this;
        }
        this.stream = stream;
        int i;
        do {
            i = this.read();
            if (i == -1) {
                this.fourcc = "";
                this.riff_type = null;
                this.avail = 0L;
                return;
            }
        } while (i == 0);
        final byte[] array = new byte[4];
        array[0] = (byte)i;
        this.readFully(array, 1, 3);
        this.fourcc = new String(array, "ascii");
        this.ckSize = this.readUnsignedInt();
        this.avail = this.ckSize;
        if (this.getFormat().equals("RIFF") || this.getFormat().equals("LIST")) {
            if (this.avail > 2147483647L) {
                throw new RIFFInvalidDataException("Chunk size too big");
            }
            final byte[] array2 = new byte[4];
            this.readFully(array2);
            this.riff_type = new String(array2, "ascii");
        }
    }
    
    public long getFilePointer() throws IOException {
        return this.root.filepointer;
    }
    
    public boolean hasNextChunk() throws IOException {
        if (this.lastiterator != null) {
            this.lastiterator.finish();
        }
        return this.avail != 0L;
    }
    
    public RIFFReader nextChunk() throws IOException {
        if (this.lastiterator != null) {
            this.lastiterator.finish();
        }
        if (this.avail == 0L) {
            return null;
        }
        return this.lastiterator = new RIFFReader(this);
    }
    
    public String getFormat() {
        return this.fourcc;
    }
    
    public String getType() {
        return this.riff_type;
    }
    
    public long getSize() {
        return this.ckSize;
    }
    
    @Override
    public int read() throws IOException {
        if (this.avail == 0L) {
            return -1;
        }
        final int read = this.stream.read();
        if (read == -1) {
            this.avail = 0L;
            return -1;
        }
        --this.avail;
        ++this.filepointer;
        return read;
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        if (this.avail == 0L) {
            return -1;
        }
        if (n2 > this.avail) {
            final int read = this.stream.read(array, n, (int)this.avail);
            if (read != -1) {
                this.filepointer += read;
            }
            this.avail = 0L;
            return read;
        }
        final int read2 = this.stream.read(array, n, n2);
        if (read2 == -1) {
            this.avail = 0L;
            return -1;
        }
        this.avail -= read2;
        this.filepointer += read2;
        return read2;
    }
    
    public final void readFully(final byte[] array) throws IOException {
        this.readFully(array, 0, array.length);
    }
    
    public final void readFully(final byte[] array, int n, int i) throws IOException {
        if (i < 0) {
            throw new IndexOutOfBoundsException();
        }
        while (i > 0) {
            final int read = this.read(array, n, i);
            if (read < 0) {
                throw new EOFException();
            }
            if (read == 0) {
                Thread.yield();
            }
            n += read;
            i -= read;
        }
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (n <= 0L || this.avail == 0L) {
            return 0L;
        }
        long min;
        long min2;
        for (min = Math.min(n, this.avail); min > 0L; min -= min2, this.avail -= min2, this.filepointer += min2) {
            min2 = Math.min(this.stream.skip(min), min);
            if (min2 == 0L) {
                Thread.yield();
                if (this.stream.read() == -1) {
                    this.avail = 0L;
                    break;
                }
                min2 = 1L;
            }
        }
        return n - min;
    }
    
    @Override
    public int available() {
        return (int)this.avail;
    }
    
    public void finish() throws IOException {
        if (this.avail != 0L) {
            this.skip(this.avail);
        }
    }
    
    public String readString(final int n) throws IOException {
        byte[] array;
        try {
            array = new byte[n];
        }
        catch (final OutOfMemoryError outOfMemoryError) {
            throw new IOException("Length too big", outOfMemoryError);
        }
        this.readFully(array);
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == 0) {
                return new String(array, 0, i, "ascii");
            }
        }
        return new String(array, "ascii");
    }
    
    public byte readByte() throws IOException {
        final int read = this.read();
        if (read < 0) {
            throw new EOFException();
        }
        return (byte)read;
    }
    
    public short readShort() throws IOException {
        final int read = this.read();
        final int read2 = this.read();
        if (read < 0) {
            throw new EOFException();
        }
        if (read2 < 0) {
            throw new EOFException();
        }
        return (short)(read | read2 << 8);
    }
    
    public int readInt() throws IOException {
        final int read = this.read();
        final int read2 = this.read();
        final int read3 = this.read();
        final int read4 = this.read();
        if (read < 0) {
            throw new EOFException();
        }
        if (read2 < 0) {
            throw new EOFException();
        }
        if (read3 < 0) {
            throw new EOFException();
        }
        if (read4 < 0) {
            throw new EOFException();
        }
        return read + (read2 << 8) | read3 << 16 | read4 << 24;
    }
    
    public long readLong() throws IOException {
        final long n = this.read();
        final long n2 = this.read();
        final long n3 = this.read();
        final long n4 = this.read();
        final long n5 = this.read();
        final long n6 = this.read();
        final long n7 = this.read();
        final long n8 = this.read();
        if (n < 0L) {
            throw new EOFException();
        }
        if (n2 < 0L) {
            throw new EOFException();
        }
        if (n3 < 0L) {
            throw new EOFException();
        }
        if (n4 < 0L) {
            throw new EOFException();
        }
        if (n5 < 0L) {
            throw new EOFException();
        }
        if (n6 < 0L) {
            throw new EOFException();
        }
        if (n7 < 0L) {
            throw new EOFException();
        }
        if (n8 < 0L) {
            throw new EOFException();
        }
        return n | n2 << 8 | n3 << 16 | n4 << 24 | n5 << 32 | n6 << 40 | n7 << 48 | n8 << 56;
    }
    
    public int readUnsignedByte() throws IOException {
        final int read = this.read();
        if (read < 0) {
            throw new EOFException();
        }
        return read;
    }
    
    public int readUnsignedShort() throws IOException {
        final int read = this.read();
        final int read2 = this.read();
        if (read < 0) {
            throw new EOFException();
        }
        if (read2 < 0) {
            throw new EOFException();
        }
        return read | read2 << 8;
    }
    
    public long readUnsignedInt() throws IOException {
        final long n = this.read();
        final long n2 = this.read();
        final long n3 = this.read();
        final long n4 = this.read();
        if (n < 0L) {
            throw new EOFException();
        }
        if (n2 < 0L) {
            throw new EOFException();
        }
        if (n3 < 0L) {
            throw new EOFException();
        }
        if (n4 < 0L) {
            throw new EOFException();
        }
        return n + (n2 << 8) | n3 << 16 | n4 << 24;
    }
    
    @Override
    public void close() throws IOException {
        this.finish();
        if (this == this.root) {
            this.stream.close();
        }
        this.stream = null;
    }
}
