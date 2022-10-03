package org.tukaani.xz;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XZInputStream extends InputStream
{
    private final ArrayCache arrayCache;
    private final int memoryLimit;
    private InputStream in;
    private SingleXZInputStream xzIn;
    private final boolean verifyCheck;
    private boolean endReached;
    private IOException exception;
    private final byte[] tempBuf;
    
    public XZInputStream(final InputStream inputStream) throws IOException {
        this(inputStream, -1);
    }
    
    public XZInputStream(final InputStream inputStream, final ArrayCache arrayCache) throws IOException {
        this(inputStream, -1, arrayCache);
    }
    
    public XZInputStream(final InputStream inputStream, final int n) throws IOException {
        this(inputStream, n, true);
    }
    
    public XZInputStream(final InputStream inputStream, final int n, final ArrayCache arrayCache) throws IOException {
        this(inputStream, n, true, arrayCache);
    }
    
    public XZInputStream(final InputStream inputStream, final int n, final boolean b) throws IOException {
        this(inputStream, n, b, ArrayCache.getDefaultCache());
    }
    
    public XZInputStream(final InputStream in, final int memoryLimit, final boolean verifyCheck, final ArrayCache arrayCache) throws IOException {
        this.endReached = false;
        this.exception = null;
        this.tempBuf = new byte[1];
        this.arrayCache = arrayCache;
        this.in = in;
        this.memoryLimit = memoryLimit;
        this.verifyCheck = verifyCheck;
        this.xzIn = new SingleXZInputStream(in, memoryLimit, verifyCheck, arrayCache);
    }
    
    @Override
    public int read() throws IOException {
        return (this.read(this.tempBuf, 0, 1) == -1) ? -1 : (this.tempBuf[0] & 0xFF);
    }
    
    @Override
    public int read(final byte[] array, int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i < 0 || n + i > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (i == 0) {
            return 0;
        }
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.endReached) {
            return -1;
        }
        int n2 = 0;
        try {
            while (i > 0) {
                if (this.xzIn == null) {
                    this.prepareNextStream();
                    if (this.endReached) {
                        return (n2 == 0) ? -1 : n2;
                    }
                }
                final int read = this.xzIn.read(array, n, i);
                if (read > 0) {
                    n2 += read;
                    n += read;
                    i -= read;
                }
                else {
                    if (read != -1) {
                        continue;
                    }
                    this.xzIn = null;
                }
            }
        }
        catch (final IOException exception) {
            this.exception = exception;
            if (n2 == 0) {
                throw exception;
            }
        }
        return n2;
    }
    
    private void prepareNextStream() throws IOException {
        final DataInputStream dataInputStream = new DataInputStream(this.in);
        final byte[] array = new byte[12];
        while (dataInputStream.read(array, 0, 1) != -1) {
            dataInputStream.readFully(array, 1, 3);
            if (array[0] != 0 || array[1] != 0 || array[2] != 0 || array[3] != 0) {
                dataInputStream.readFully(array, 4, 8);
                try {
                    this.xzIn = new SingleXZInputStream(this.in, this.memoryLimit, this.verifyCheck, array, this.arrayCache);
                }
                catch (final XZFormatException ex) {
                    throw new CorruptedInputException("Garbage after a valid XZ Stream");
                }
                return;
            }
        }
        this.endReached = true;
    }
    
    @Override
    public int available() throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        return (this.xzIn == null) ? 0 : this.xzIn.available();
    }
    
    @Override
    public void close() throws IOException {
        this.close(true);
    }
    
    public void close(final boolean b) throws IOException {
        if (this.in != null) {
            if (this.xzIn != null) {
                this.xzIn.close(false);
                this.xzIn = null;
            }
            try {
                if (b) {
                    this.in.close();
                }
            }
            finally {
                this.in = null;
            }
        }
    }
}
