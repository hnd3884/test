package sun.net.www.http;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.PrintStream;

public class ChunkedOutputStream extends PrintStream
{
    static final int DEFAULT_CHUNK_SIZE = 4096;
    private static final byte[] CRLF;
    private static final int CRLF_SIZE;
    private static final byte[] FOOTER;
    private static final int FOOTER_SIZE;
    private static final byte[] EMPTY_CHUNK_HEADER;
    private static final int EMPTY_CHUNK_HEADER_SIZE;
    private byte[] buf;
    private int size;
    private int count;
    private int spaceInCurrentChunk;
    private PrintStream out;
    private int preferredChunkDataSize;
    private int preferedHeaderSize;
    private int preferredChunkGrossSize;
    private byte[] completeHeader;
    
    private static int getHeaderSize(final int n) {
        return Integer.toHexString(n).length() + ChunkedOutputStream.CRLF_SIZE;
    }
    
    private static byte[] getHeader(final int n) {
        try {
            final byte[] bytes = Integer.toHexString(n).getBytes("US-ASCII");
            final byte[] array = new byte[getHeaderSize(n)];
            for (int i = 0; i < bytes.length; ++i) {
                array[i] = bytes[i];
            }
            array[bytes.length] = ChunkedOutputStream.CRLF[0];
            array[bytes.length + 1] = ChunkedOutputStream.CRLF[1];
            return array;
        }
        catch (final UnsupportedEncodingException ex) {
            throw new InternalError(ex.getMessage(), ex);
        }
    }
    
    public ChunkedOutputStream(final PrintStream printStream) {
        this(printStream, 4096);
    }
    
    public ChunkedOutputStream(final PrintStream out, int preferredChunkDataSize) {
        super(out);
        this.out = out;
        if (preferredChunkDataSize <= 0) {
            preferredChunkDataSize = 4096;
        }
        if (preferredChunkDataSize > 0) {
            int n = preferredChunkDataSize - getHeaderSize(preferredChunkDataSize) - ChunkedOutputStream.FOOTER_SIZE;
            if (getHeaderSize(n + 1) < getHeaderSize(preferredChunkDataSize)) {
                ++n;
            }
            preferredChunkDataSize = n;
        }
        if (preferredChunkDataSize > 0) {
            this.preferredChunkDataSize = preferredChunkDataSize;
        }
        else {
            this.preferredChunkDataSize = 4096 - getHeaderSize(4096) - ChunkedOutputStream.FOOTER_SIZE;
        }
        this.preferedHeaderSize = getHeaderSize(this.preferredChunkDataSize);
        this.preferredChunkGrossSize = this.preferedHeaderSize + this.preferredChunkDataSize + ChunkedOutputStream.FOOTER_SIZE;
        this.completeHeader = getHeader(this.preferredChunkDataSize);
        this.buf = new byte[this.preferredChunkGrossSize];
        this.reset();
    }
    
    private void flush(final boolean b) {
        if (this.spaceInCurrentChunk == 0) {
            this.out.write(this.buf, 0, this.preferredChunkGrossSize);
            this.out.flush();
            this.reset();
        }
        else if (b) {
            if (this.size > 0) {
                final int n = this.preferedHeaderSize - getHeaderSize(this.size);
                System.arraycopy(getHeader(this.size), 0, this.buf, n, getHeaderSize(this.size));
                this.buf[this.count++] = ChunkedOutputStream.FOOTER[0];
                this.buf[this.count++] = ChunkedOutputStream.FOOTER[1];
                this.out.write(this.buf, n, this.count - n);
            }
            else {
                this.out.write(ChunkedOutputStream.EMPTY_CHUNK_HEADER, 0, ChunkedOutputStream.EMPTY_CHUNK_HEADER_SIZE);
            }
            this.out.flush();
            this.reset();
        }
    }
    
    @Override
    public boolean checkError() {
        return this.out.checkError();
    }
    
    private void ensureOpen() {
        if (this.out == null) {
            this.setError();
        }
    }
    
    @Override
    public synchronized void write(final byte[] array, final int n, final int n2) {
        this.ensureOpen();
        if (n < 0 || n > array.length || n2 < 0 || n + n2 > array.length || n + n2 < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (n2 == 0) {
            return;
        }
        int i = n2;
        int n3 = n;
        do {
            if (i >= this.spaceInCurrentChunk) {
                for (int j = 0; j < this.completeHeader.length; ++j) {
                    this.buf[j] = this.completeHeader[j];
                }
                System.arraycopy(array, n3, this.buf, this.count, this.spaceInCurrentChunk);
                n3 += this.spaceInCurrentChunk;
                i -= this.spaceInCurrentChunk;
                this.count += this.spaceInCurrentChunk;
                this.buf[this.count++] = ChunkedOutputStream.FOOTER[0];
                this.buf[this.count++] = ChunkedOutputStream.FOOTER[1];
                this.spaceInCurrentChunk = 0;
                this.flush(false);
                if (this.checkError()) {
                    break;
                }
                continue;
            }
            else {
                System.arraycopy(array, n3, this.buf, this.count, i);
                this.count += i;
                this.size += i;
                this.spaceInCurrentChunk -= i;
                i = 0;
            }
        } while (i > 0);
    }
    
    @Override
    public synchronized void write(final int n) {
        this.write(new byte[] { (byte)n }, 0, 1);
    }
    
    public synchronized void reset() {
        this.count = this.preferedHeaderSize;
        this.size = 0;
        this.spaceInCurrentChunk = this.preferredChunkDataSize;
    }
    
    public int size() {
        return this.size;
    }
    
    @Override
    public synchronized void close() {
        this.ensureOpen();
        if (this.size > 0) {
            this.flush(true);
        }
        this.flush(true);
        this.out = null;
    }
    
    @Override
    public synchronized void flush() {
        this.ensureOpen();
        if (this.size > 0) {
            this.flush(true);
        }
    }
    
    static {
        CRLF = new byte[] { 13, 10 };
        CRLF_SIZE = ChunkedOutputStream.CRLF.length;
        FOOTER = ChunkedOutputStream.CRLF;
        FOOTER_SIZE = ChunkedOutputStream.CRLF_SIZE;
        EMPTY_CHUNK_HEADER = getHeader(0);
        EMPTY_CHUNK_HEADER_SIZE = getHeaderSize(0);
    }
}
