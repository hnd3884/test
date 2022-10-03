package javax.imageio.stream;

import javax.imageio.IIOException;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Stack;

public abstract class ImageInputStreamImpl implements ImageInputStream
{
    private Stack markByteStack;
    private Stack markBitStack;
    private boolean isClosed;
    private static final int BYTE_BUF_LENGTH = 8192;
    byte[] byteBuf;
    protected ByteOrder byteOrder;
    protected long streamPos;
    protected int bitOffset;
    protected long flushedPos;
    
    public ImageInputStreamImpl() {
        this.markByteStack = new Stack();
        this.markBitStack = new Stack();
        this.isClosed = false;
        this.byteBuf = new byte[8192];
        this.byteOrder = ByteOrder.BIG_ENDIAN;
        this.flushedPos = 0L;
    }
    
    protected final void checkClosed() throws IOException {
        if (this.isClosed) {
            throw new IOException("closed");
        }
    }
    
    @Override
    public void setByteOrder(final ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }
    
    @Override
    public ByteOrder getByteOrder() {
        return this.byteOrder;
    }
    
    @Override
    public abstract int read() throws IOException;
    
    @Override
    public int read(final byte[] array) throws IOException {
        return this.read(array, 0, array.length);
    }
    
    @Override
    public abstract int read(final byte[] p0, final int p1, final int p2) throws IOException;
    
    @Override
    public void readBytes(final IIOByteBuffer iioByteBuffer, int read) throws IOException {
        if (read < 0) {
            throw new IndexOutOfBoundsException("len < 0!");
        }
        if (iioByteBuffer == null) {
            throw new NullPointerException("buf == null!");
        }
        final byte[] data = new byte[read];
        read = this.read(data, 0, read);
        iioByteBuffer.setData(data);
        iioByteBuffer.setOffset(0);
        iioByteBuffer.setLength(read);
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        final int read = this.read();
        if (read < 0) {
            throw new EOFException();
        }
        return read != 0;
    }
    
    @Override
    public byte readByte() throws IOException {
        final int read = this.read();
        if (read < 0) {
            throw new EOFException();
        }
        return (byte)read;
    }
    
    @Override
    public int readUnsignedByte() throws IOException {
        final int read = this.read();
        if (read < 0) {
            throw new EOFException();
        }
        return read;
    }
    
    @Override
    public short readShort() throws IOException {
        if (this.read(this.byteBuf, 0, 2) != 2) {
            throw new EOFException();
        }
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            return (short)((this.byteBuf[0] & 0xFF) << 8 | (this.byteBuf[1] & 0xFF) << 0);
        }
        return (short)((this.byteBuf[1] & 0xFF) << 8 | (this.byteBuf[0] & 0xFF) << 0);
    }
    
    @Override
    public int readUnsignedShort() throws IOException {
        return this.readShort() & 0xFFFF;
    }
    
    @Override
    public char readChar() throws IOException {
        return (char)this.readShort();
    }
    
    @Override
    public int readInt() throws IOException {
        if (this.read(this.byteBuf, 0, 4) != 4) {
            throw new EOFException();
        }
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            return (this.byteBuf[0] & 0xFF) << 24 | (this.byteBuf[1] & 0xFF) << 16 | (this.byteBuf[2] & 0xFF) << 8 | (this.byteBuf[3] & 0xFF) << 0;
        }
        return (this.byteBuf[3] & 0xFF) << 24 | (this.byteBuf[2] & 0xFF) << 16 | (this.byteBuf[1] & 0xFF) << 8 | (this.byteBuf[0] & 0xFF) << 0;
    }
    
    @Override
    public long readUnsignedInt() throws IOException {
        return (long)this.readInt() & 0xFFFFFFFFL;
    }
    
    @Override
    public long readLong() throws IOException {
        final int int1 = this.readInt();
        final int int2 = this.readInt();
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            return ((long)int1 << 32) + ((long)int2 & 0xFFFFFFFFL);
        }
        return ((long)int2 << 32) + ((long)int1 & 0xFFFFFFFFL);
    }
    
    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }
    
    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }
    
    @Override
    public String readLine() throws IOException {
        final StringBuffer sb = new StringBuffer();
        int read = -1;
        int i = 0;
        while (i == 0) {
            switch (read = this.read()) {
                case -1:
                case 10: {
                    i = 1;
                    continue;
                }
                case 13: {
                    i = 1;
                    final long streamPosition = this.getStreamPosition();
                    if (this.read() != 10) {
                        this.seek(streamPosition);
                        continue;
                    }
                    continue;
                }
                default: {
                    sb.append((char)read);
                    continue;
                }
            }
        }
        if (read == -1 && sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }
    
    @Override
    public String readUTF() throws IOException {
        this.bitOffset = 0;
        final ByteOrder byteOrder = this.getByteOrder();
        this.setByteOrder(ByteOrder.BIG_ENDIAN);
        String utf;
        try {
            utf = DataInputStream.readUTF(this);
        }
        catch (final IOException ex) {
            this.setByteOrder(byteOrder);
            throw ex;
        }
        this.setByteOrder(byteOrder);
        return utf;
    }
    
    @Override
    public void readFully(final byte[] array, int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i > array.length || n + i < 0) {
            throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > b.length!");
        }
        while (i > 0) {
            final int read = this.read(array, n, i);
            if (read == -1) {
                throw new EOFException();
            }
            n += read;
            i -= read;
        }
    }
    
    @Override
    public void readFully(final byte[] array) throws IOException {
        this.readFully(array, 0, array.length);
    }
    
    @Override
    public void readFully(final short[] array, int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i > array.length || n + i < 0) {
            throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > s.length!");
        }
        while (i > 0) {
            final int min = Math.min(i, this.byteBuf.length / 2);
            this.readFully(this.byteBuf, 0, min * 2);
            this.toShorts(this.byteBuf, array, n, min);
            n += min;
            i -= min;
        }
    }
    
    @Override
    public void readFully(final char[] array, int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i > array.length || n + i < 0) {
            throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length!");
        }
        while (i > 0) {
            final int min = Math.min(i, this.byteBuf.length / 2);
            this.readFully(this.byteBuf, 0, min * 2);
            this.toChars(this.byteBuf, array, n, min);
            n += min;
            i -= min;
        }
    }
    
    @Override
    public void readFully(final int[] array, int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i > array.length || n + i < 0) {
            throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > i.length!");
        }
        while (i > 0) {
            final int min = Math.min(i, this.byteBuf.length / 4);
            this.readFully(this.byteBuf, 0, min * 4);
            this.toInts(this.byteBuf, array, n, min);
            n += min;
            i -= min;
        }
    }
    
    @Override
    public void readFully(final long[] array, int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i > array.length || n + i < 0) {
            throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > l.length!");
        }
        while (i > 0) {
            final int min = Math.min(i, this.byteBuf.length / 8);
            this.readFully(this.byteBuf, 0, min * 8);
            this.toLongs(this.byteBuf, array, n, min);
            n += min;
            i -= min;
        }
    }
    
    @Override
    public void readFully(final float[] array, int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i > array.length || n + i < 0) {
            throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > f.length!");
        }
        while (i > 0) {
            final int min = Math.min(i, this.byteBuf.length / 4);
            this.readFully(this.byteBuf, 0, min * 4);
            this.toFloats(this.byteBuf, array, n, min);
            n += min;
            i -= min;
        }
    }
    
    @Override
    public void readFully(final double[] array, int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i > array.length || n + i < 0) {
            throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > d.length!");
        }
        while (i > 0) {
            final int min = Math.min(i, this.byteBuf.length / 8);
            this.readFully(this.byteBuf, 0, min * 8);
            this.toDoubles(this.byteBuf, array, n, min);
            n += min;
            i -= min;
        }
    }
    
    private void toShorts(final byte[] array, final short[] array2, final int n, final int n2) {
        int n3 = 0;
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < n2; ++i) {
                array2[n + i] = (short)(array[n3] << 8 | (array[n3 + 1] & 0xFF));
                n3 += 2;
            }
        }
        else {
            for (int j = 0; j < n2; ++j) {
                array2[n + j] = (short)(array[n3 + 1] << 8 | (array[n3] & 0xFF));
                n3 += 2;
            }
        }
    }
    
    private void toChars(final byte[] array, final char[] array2, final int n, final int n2) {
        int n3 = 0;
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < n2; ++i) {
                array2[n + i] = (char)(array[n3] << 8 | (array[n3 + 1] & 0xFF));
                n3 += 2;
            }
        }
        else {
            for (int j = 0; j < n2; ++j) {
                array2[n + j] = (char)(array[n3 + 1] << 8 | (array[n3] & 0xFF));
                n3 += 2;
            }
        }
    }
    
    private void toInts(final byte[] array, final int[] array2, final int n, final int n2) {
        int n3 = 0;
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < n2; ++i) {
                array2[n + i] = (array[n3] << 24 | (array[n3 + 1] & 0xFF) << 16 | (array[n3 + 2] & 0xFF) << 8 | (array[n3 + 3] & 0xFF));
                n3 += 4;
            }
        }
        else {
            for (int j = 0; j < n2; ++j) {
                array2[n + j] = (array[n3 + 3] << 24 | (array[n3 + 2] & 0xFF) << 16 | (array[n3 + 1] & 0xFF) << 8 | (array[n3] & 0xFF));
                n3 += 4;
            }
        }
    }
    
    private void toLongs(final byte[] array, final long[] array2, final int n, final int n2) {
        int n3 = 0;
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < n2; ++i) {
                array2[n + i] = ((long)(array[n3] << 24 | (array[n3 + 1] & 0xFF) << 16 | (array[n3 + 2] & 0xFF) << 8 | (array[n3 + 3] & 0xFF)) << 32 | ((long)(array[n3 + 4] << 24 | (array[n3 + 5] & 0xFF) << 16 | (array[n3 + 6] & 0xFF) << 8 | (array[n3 + 7] & 0xFF)) & 0xFFFFFFFFL));
                n3 += 8;
            }
        }
        else {
            for (int j = 0; j < n2; ++j) {
                array2[n + j] = ((long)(array[n3 + 7] << 24 | (array[n3 + 6] & 0xFF) << 16 | (array[n3 + 5] & 0xFF) << 8 | (array[n3 + 4] & 0xFF)) << 32 | ((long)(array[n3 + 3] << 24 | (array[n3 + 2] & 0xFF) << 16 | (array[n3 + 1] & 0xFF) << 8 | (array[n3] & 0xFF)) & 0xFFFFFFFFL));
                n3 += 8;
            }
        }
    }
    
    private void toFloats(final byte[] array, final float[] array2, final int n, final int n2) {
        int n3 = 0;
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < n2; ++i) {
                array2[n + i] = Float.intBitsToFloat(array[n3] << 24 | (array[n3 + 1] & 0xFF) << 16 | (array[n3 + 2] & 0xFF) << 8 | (array[n3 + 3] & 0xFF));
                n3 += 4;
            }
        }
        else {
            for (int j = 0; j < n2; ++j) {
                array2[n + j] = Float.intBitsToFloat(array[n3 + 3] << 24 | (array[n3 + 2] & 0xFF) << 16 | (array[n3 + 1] & 0xFF) << 8 | (array[n3 + 0] & 0xFF));
                n3 += 4;
            }
        }
    }
    
    private void toDoubles(final byte[] array, final double[] array2, final int n, final int n2) {
        int n3 = 0;
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < n2; ++i) {
                array2[n + i] = Double.longBitsToDouble((long)(array[n3] << 24 | (array[n3 + 1] & 0xFF) << 16 | (array[n3 + 2] & 0xFF) << 8 | (array[n3 + 3] & 0xFF)) << 32 | ((long)(array[n3 + 4] << 24 | (array[n3 + 5] & 0xFF) << 16 | (array[n3 + 6] & 0xFF) << 8 | (array[n3 + 7] & 0xFF)) & 0xFFFFFFFFL));
                n3 += 8;
            }
        }
        else {
            for (int j = 0; j < n2; ++j) {
                array2[n + j] = Double.longBitsToDouble((long)(array[n3 + 7] << 24 | (array[n3 + 6] & 0xFF) << 16 | (array[n3 + 5] & 0xFF) << 8 | (array[n3 + 4] & 0xFF)) << 32 | ((long)(array[n3 + 3] << 24 | (array[n3 + 2] & 0xFF) << 16 | (array[n3 + 1] & 0xFF) << 8 | (array[n3] & 0xFF)) & 0xFFFFFFFFL));
                n3 += 8;
            }
        }
    }
    
    @Override
    public long getStreamPosition() throws IOException {
        this.checkClosed();
        return this.streamPos;
    }
    
    @Override
    public int getBitOffset() throws IOException {
        this.checkClosed();
        return this.bitOffset;
    }
    
    @Override
    public void setBitOffset(final int bitOffset) throws IOException {
        this.checkClosed();
        if (bitOffset < 0 || bitOffset > 7) {
            throw new IllegalArgumentException("bitOffset must be betwwen 0 and 7!");
        }
        this.bitOffset = bitOffset;
    }
    
    @Override
    public int readBit() throws IOException {
        this.checkClosed();
        final int bitOffset = this.bitOffset + 1 & 0x7;
        int read = this.read();
        if (read == -1) {
            throw new EOFException();
        }
        if (bitOffset != 0) {
            this.seek(this.getStreamPosition() - 1L);
            read >>= 8 - bitOffset;
        }
        this.bitOffset = bitOffset;
        return read & 0x1;
    }
    
    @Override
    public long readBits(final int n) throws IOException {
        this.checkClosed();
        if (n < 0 || n > 64) {
            throw new IllegalArgumentException();
        }
        if (n == 0) {
            return 0L;
        }
        int i = n + this.bitOffset;
        final int bitOffset = this.bitOffset + n & 0x7;
        long n2 = 0L;
        while (i > 0) {
            final int read = this.read();
            if (read == -1) {
                throw new EOFException();
            }
            n2 = (n2 << 8 | (long)read);
            i -= 8;
        }
        if (bitOffset != 0) {
            this.seek(this.getStreamPosition() - 1L);
        }
        this.bitOffset = bitOffset;
        return n2 >>> -i & -1L >>> 64 - n;
    }
    
    @Override
    public long length() {
        return -1L;
    }
    
    @Override
    public int skipBytes(final int n) throws IOException {
        final long streamPosition = this.getStreamPosition();
        this.seek(streamPosition + n);
        return (int)(this.getStreamPosition() - streamPosition);
    }
    
    @Override
    public long skipBytes(final long n) throws IOException {
        final long streamPosition = this.getStreamPosition();
        this.seek(streamPosition + n);
        return this.getStreamPosition() - streamPosition;
    }
    
    @Override
    public void seek(final long streamPos) throws IOException {
        this.checkClosed();
        if (streamPos < this.flushedPos) {
            throw new IndexOutOfBoundsException("pos < flushedPos!");
        }
        this.streamPos = streamPos;
        this.bitOffset = 0;
    }
    
    @Override
    public void mark() {
        try {
            this.markByteStack.push(this.getStreamPosition());
            this.markBitStack.push(this.getBitOffset());
        }
        catch (final IOException ex) {}
    }
    
    @Override
    public void reset() throws IOException {
        if (this.markByteStack.empty()) {
            return;
        }
        final long longValue = this.markByteStack.pop();
        if (longValue < this.flushedPos) {
            throw new IIOException("Previous marked position has been discarded!");
        }
        this.seek(longValue);
        this.setBitOffset(this.markBitStack.pop());
    }
    
    @Override
    public void flushBefore(final long flushedPos) throws IOException {
        this.checkClosed();
        if (flushedPos < this.flushedPos) {
            throw new IndexOutOfBoundsException("pos < flushedPos!");
        }
        if (flushedPos > this.getStreamPosition()) {
            throw new IndexOutOfBoundsException("pos > getStreamPosition()!");
        }
        this.flushedPos = flushedPos;
    }
    
    @Override
    public void flush() throws IOException {
        this.flushBefore(this.getStreamPosition());
    }
    
    @Override
    public long getFlushedPosition() {
        return this.flushedPos;
    }
    
    @Override
    public boolean isCached() {
        return false;
    }
    
    @Override
    public boolean isCachedMemory() {
        return false;
    }
    
    @Override
    public boolean isCachedFile() {
        return false;
    }
    
    @Override
    public void close() throws IOException {
        this.checkClosed();
        this.isClosed = true;
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (!this.isClosed) {
            try {
                this.close();
            }
            catch (final IOException ex) {}
        }
        super.finalize();
    }
}
