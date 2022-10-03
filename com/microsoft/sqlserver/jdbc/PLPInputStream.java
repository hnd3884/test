package com.microsoft.sqlserver.jdbc;

import java.io.IOException;

class PLPInputStream extends BaseInputStream
{
    static final long PLP_NULL = -1L;
    static final long UNKNOWN_PLP_LEN = -2L;
    private static final byte[] EMPTY_PLP_BYTES;
    private static final int PLP_EOS = -1;
    private int currentChunkRemain;
    private int markedChunkRemain;
    private int leftOverReadLimit;
    private byte[] oneByteArray;
    
    static final boolean isNull(final TDSReader tdsReader) throws SQLServerException {
        final TDSReaderMark mark = tdsReader.mark();
        try {
            return null == makeTempStream(tdsReader, false, null);
        }
        finally {
            tdsReader.reset(mark);
        }
    }
    
    static final PLPInputStream makeTempStream(final TDSReader tdsReader, final boolean discardValue, final ServerDTVImpl dtv) throws SQLServerException {
        return makeStream(tdsReader, discardValue, discardValue, dtv);
    }
    
    static final PLPInputStream makeStream(final TDSReader tdsReader, final InputStreamGetterArgs getterArgs, final ServerDTVImpl dtv) throws SQLServerException {
        final PLPInputStream is = makeStream(tdsReader, getterArgs.isAdaptive, getterArgs.isStreaming, dtv);
        if (null != is) {
            is.setLoggingInfo(getterArgs.logContext);
        }
        return is;
    }
    
    private static PLPInputStream makeStream(final TDSReader tdsReader, final boolean isAdaptive, final boolean isStreaming, final ServerDTVImpl dtv) throws SQLServerException {
        final long payloadLength = tdsReader.readLong();
        if (-1L == payloadLength) {
            return null;
        }
        return new PLPInputStream(tdsReader, payloadLength, isAdaptive, isStreaming, dtv);
    }
    
    PLPInputStream(final TDSReader tdsReader, final long statedPayloadLength, final boolean isAdaptive, final boolean isStreaming, final ServerDTVImpl dtv) {
        super(tdsReader, isAdaptive, isStreaming, dtv);
        this.leftOverReadLimit = 0;
        this.oneByteArray = new byte[1];
        this.payloadLength = ((-2L != statedPayloadLength) ? ((int)statedPayloadLength) : -1);
        final int n = 0;
        this.markedChunkRemain = n;
        this.currentChunkRemain = n;
    }
    
    @Override
    byte[] getBytes() throws SQLServerException {
        this.readBytesInternal(null, 0, 0);
        byte[] value;
        if (-1 == this.currentChunkRemain) {
            value = PLPInputStream.EMPTY_PLP_BYTES;
        }
        else {
            value = new byte[(-1 != this.payloadLength) ? this.payloadLength : this.currentChunkRemain];
            int bytesRead = 0;
            while (-1 != this.currentChunkRemain) {
                if (value.length == bytesRead) {
                    final byte[] newValue = new byte[bytesRead + this.currentChunkRemain];
                    System.arraycopy(value, 0, newValue, 0, bytesRead);
                    value = newValue;
                }
                bytesRead += this.readBytesInternal(value, bytesRead, this.currentChunkRemain);
            }
        }
        try {
            this.close();
        }
        catch (final IOException e) {
            SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
        }
        return value;
    }
    
    @Override
    public long skip(long n) throws IOException {
        this.checkClosed();
        if (n < 0L) {
            return 0L;
        }
        if (n > 2147483647L) {
            n = 2147483647L;
        }
        final long bytesread = this.readBytes(null, 0, (int)n);
        if (-1L == bytesread) {
            return 0L;
        }
        return bytesread;
    }
    
    @Override
    public int available() throws IOException {
        this.checkClosed();
        try {
            if (0 == this.currentChunkRemain) {
                this.readBytesInternal(null, 0, 0);
            }
            if (-1 == this.currentChunkRemain) {
                return 0;
            }
            int available = this.tdsReader.available();
            if (available > this.currentChunkRemain) {
                available = this.currentChunkRemain;
            }
            return available;
        }
        catch (final SQLServerException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    @Override
    public int read() throws IOException {
        this.checkClosed();
        if (-1 != this.readBytes(this.oneByteArray, 0, 1)) {
            return this.oneByteArray[0] & 0xFF;
        }
        return -1;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        if (null == b) {
            throw new NullPointerException();
        }
        this.checkClosed();
        return this.readBytes(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int offset, final int maxBytes) throws IOException {
        if (null == b) {
            throw new NullPointerException();
        }
        if (offset < 0 || maxBytes < 0 || offset + maxBytes > b.length) {
            throw new IndexOutOfBoundsException();
        }
        this.checkClosed();
        return this.readBytes(b, offset, maxBytes);
    }
    
    int readBytes(final byte[] b, final int offset, final int maxBytes) throws IOException {
        if (0 == maxBytes) {
            return 0;
        }
        try {
            return this.readBytesInternal(b, offset, maxBytes);
        }
        catch (final SQLServerException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    private int readBytesInternal(final byte[] b, final int offset, final int maxBytes) throws SQLServerException {
        if (-1 == this.currentChunkRemain) {
            return -1;
        }
        int bytesRead = 0;
        while (true) {
            if (0 == this.currentChunkRemain) {
                this.currentChunkRemain = (int)this.tdsReader.readUnsignedInt();
                assert this.currentChunkRemain >= 0;
                if (0 == this.currentChunkRemain) {
                    this.currentChunkRemain = -1;
                    break;
                }
            }
            if (bytesRead == maxBytes) {
                break;
            }
            int bytesToRead = maxBytes - bytesRead;
            if (bytesToRead > this.currentChunkRemain) {
                bytesToRead = this.currentChunkRemain;
            }
            if (null == b) {
                this.tdsReader.skip(bytesToRead);
            }
            else {
                this.tdsReader.readBytes(b, offset + bytesRead, bytesToRead);
            }
            bytesRead += bytesToRead;
            this.currentChunkRemain -= bytesToRead;
        }
        if (bytesRead > 0) {
            if (this.isReadLimitSet && this.leftOverReadLimit > 0) {
                this.leftOverReadLimit -= bytesRead;
                if (this.leftOverReadLimit < 0) {
                    this.clearCurrentMark();
                }
            }
            return bytesRead;
        }
        if (-1 == this.currentChunkRemain) {
            return -1;
        }
        return 0;
    }
    
    @Override
    public void mark(final int readLimit) {
        if (null != this.tdsReader && readLimit > 0) {
            this.currentMark = this.tdsReader.mark();
            this.markedChunkRemain = this.currentChunkRemain;
            this.setReadLimit(this.leftOverReadLimit = readLimit);
        }
    }
    
    @Override
    public void close() throws IOException {
        if (null == this.tdsReader) {
            return;
        }
        while (this.skip(this.tdsReader.getConnection().getTDSPacketSize()) != 0L) {}
        this.closeHelper();
    }
    
    @Override
    public void reset() throws IOException {
        this.resetHelper();
        this.leftOverReadLimit = this.readLimit;
        this.currentChunkRemain = this.markedChunkRemain;
    }
    
    static {
        EMPTY_PLP_BYTES = new byte[0];
    }
}
