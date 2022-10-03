package jdk.jfr.internal.consumer;

import java.io.EOFException;
import java.io.IOException;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.io.DataInput;

public final class RecordingInput implements DataInput, AutoCloseable
{
    public static final byte STRING_ENCODING_NULL = 0;
    public static final byte STRING_ENCODING_EMPTY_STRING = 1;
    public static final byte STRING_ENCODING_CONSTANT_POOL = 2;
    public static final byte STRING_ENCODING_UTF8_BYTE_ARRAY = 3;
    public static final byte STRING_ENCODING_CHAR_ARRAY = 4;
    public static final byte STRING_ENCODING_LATIN1_BYTE_ARRAY = 5;
    private static final int DEFAULT_BLOCK_SIZE = 16777216;
    private static final Charset UTF8;
    private static final Charset LATIN1;
    private final RandomAccessFile file;
    private final long size;
    private Block currentBlock;
    private Block previousBlock;
    private long position;
    private final int blockSize;
    
    private RecordingInput(final File file, final int blockSize) throws IOException {
        this.currentBlock = new Block();
        this.previousBlock = new Block();
        this.size = file.length();
        this.blockSize = blockSize;
        this.file = new RandomAccessFile(file, "r");
        if (this.size < 8L) {
            throw new IOException("Not a valid Flight Recorder file. File length is only " + this.size + " bytes.");
        }
    }
    
    public RecordingInput(final File file) throws IOException {
        this(file, 16777216);
    }
    
    @Override
    public final byte readByte() throws IOException {
        if (!this.currentBlock.contains(this.position)) {
            this.position(this.position);
        }
        return this.currentBlock.get(this.position++);
    }
    
    @Override
    public final void readFully(final byte[] array, final int n, final int n2) throws IOException {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.readByte();
        }
    }
    
    @Override
    public final void readFully(final byte[] array) throws IOException {
        this.readFully(array, 0, array.length);
    }
    
    public final short readRawShort() throws IOException {
        return (short)((this.readByte() & 0xFF) + (this.readByte() << 8));
    }
    
    @Override
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readRawLong());
    }
    
    @Override
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readRawInt());
    }
    
    public final int readRawInt() throws IOException {
        return (this.readByte() & 0xFF) + ((this.readByte() & 0xFF) << 8) + ((this.readByte() & 0xFF) << 16) + (this.readByte() << 24);
    }
    
    public final long readRawLong() throws IOException {
        return ((long)this.readByte() & 0xFFL) + (((long)this.readByte() & 0xFFL) << 8) + (((long)this.readByte() & 0xFFL) << 16) + (((long)this.readByte() & 0xFFL) << 24) + (((long)this.readByte() & 0xFFL) << 32) + (((long)this.readByte() & 0xFFL) << 40) + (((long)this.readByte() & 0xFFL) << 48) + ((long)this.readByte() << 56);
    }
    
    public final long position() throws IOException {
        return this.position;
    }
    
    public final void position(final long position) throws IOException {
        if (!this.currentBlock.contains(position)) {
            if (!this.previousBlock.contains(position)) {
                if (position > this.size()) {
                    throw new EOFException("Trying to read at " + position + ", but file is only " + this.size() + " bytes.");
                }
                final long trimToFileSize = this.trimToFileSize(this.calculateBlockStart(position));
                this.file.seek(trimToFileSize);
                this.previousBlock.read(this.file, (int)Math.min(this.size() - trimToFileSize, this.blockSize));
            }
            final Block currentBlock = this.currentBlock;
            this.currentBlock = this.previousBlock;
            this.previousBlock = currentBlock;
        }
        this.position = position;
    }
    
    private final long trimToFileSize(final long n) throws IOException {
        return Math.min(this.size(), Math.max(0L, n));
    }
    
    private final long calculateBlockStart(final long n) {
        if (this.currentBlock.contains(n - this.blockSize)) {
            return this.currentBlock.blockPosition + this.currentBlock.bytes.length;
        }
        if (this.currentBlock.contains(n + this.blockSize)) {
            return this.currentBlock.blockPosition - this.blockSize;
        }
        return n - this.blockSize / 2;
    }
    
    public final long size() throws IOException {
        return this.size;
    }
    
    @Override
    public final void close() throws IOException {
        this.file.close();
    }
    
    @Override
    public final int skipBytes(final int n) throws IOException {
        final long position = this.position();
        this.position(position + n);
        return (int)(this.position() - position);
    }
    
    @Override
    public final boolean readBoolean() throws IOException {
        return this.readByte() != 0;
    }
    
    @Override
    public int readUnsignedByte() throws IOException {
        return this.readByte() & 0xFF;
    }
    
    @Override
    public int readUnsignedShort() throws IOException {
        return this.readShort() & 0xFFFF;
    }
    
    @Override
    public final String readLine() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String readUTF() throws IOException {
        return this.readEncodedString(this.readByte());
    }
    
    public String readEncodedString(final byte b) throws IOException {
        if (b == 0) {
            return null;
        }
        if (b == 1) {
            return "";
        }
        final int int1 = this.readInt();
        if (b == 4) {
            final char[] array = new char[int1];
            for (int i = 0; i < int1; ++i) {
                array[i] = this.readChar();
            }
            return new String(array);
        }
        final byte[] array2 = new byte[int1];
        this.readFully(array2);
        if (b == 3) {
            return new String(array2, RecordingInput.UTF8);
        }
        if (b == 5) {
            return new String(array2, RecordingInput.LATIN1);
        }
        throw new IOException("Unknown string encoding " + b);
    }
    
    @Override
    public char readChar() throws IOException {
        return (char)this.readLong();
    }
    
    @Override
    public short readShort() throws IOException {
        return (short)this.readLong();
    }
    
    @Override
    public int readInt() throws IOException {
        return (int)this.readLong();
    }
    
    @Override
    public long readLong() throws IOException {
        final byte byte1 = this.readByte();
        final long n = (long)byte1 & 0x7FL;
        if (byte1 >= 0) {
            return n;
        }
        final byte byte2 = this.readByte();
        final long n2 = n + (((long)byte2 & 0x7FL) << 7);
        if (byte2 >= 0) {
            return n2;
        }
        final byte byte3 = this.readByte();
        final long n3 = n2 + (((long)byte3 & 0x7FL) << 14);
        if (byte3 >= 0) {
            return n3;
        }
        final byte byte4 = this.readByte();
        final long n4 = n3 + (((long)byte4 & 0x7FL) << 21);
        if (byte4 >= 0) {
            return n4;
        }
        final byte byte5 = this.readByte();
        final long n5 = n4 + (((long)byte5 & 0x7FL) << 28);
        if (byte5 >= 0) {
            return n5;
        }
        final byte byte6 = this.readByte();
        final long n6 = n5 + (((long)byte6 & 0x7FL) << 35);
        if (byte6 >= 0) {
            return n6;
        }
        final byte byte7 = this.readByte();
        final long n7 = n6 + (((long)byte7 & 0x7FL) << 42);
        if (byte7 >= 0) {
            return n7;
        }
        final byte byte8 = this.readByte();
        final long n8 = n7 + (((long)byte8 & 0x7FL) << 49);
        if (byte8 >= 0) {
            return n8;
        }
        return n8 + ((long)(this.readByte() & 0xFF) << 56);
    }
    
    static {
        UTF8 = Charset.forName("UTF-8");
        LATIN1 = Charset.forName("ISO-8859-1");
    }
    
    private static final class Block
    {
        private byte[] bytes;
        private long blockPosition;
        
        private Block() {
            this.bytes = new byte[0];
        }
        
        boolean contains(final long n) {
            return n >= this.blockPosition && n < this.blockPosition + this.bytes.length;
        }
        
        public void read(final RandomAccessFile randomAccessFile, final int n) throws IOException {
            this.blockPosition = randomAccessFile.getFilePointer();
            if (n != this.bytes.length) {
                this.bytes = new byte[n];
            }
            randomAccessFile.readFully(this.bytes);
        }
        
        public byte get(final long n) {
            return this.bytes[(int)(n - this.blockPosition)];
        }
    }
}
