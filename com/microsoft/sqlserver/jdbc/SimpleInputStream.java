package com.microsoft.sqlserver.jdbc;

import java.io.IOException;
import java.util.logging.Level;

final class SimpleInputStream extends BaseInputStream
{
    private byte[] bSingleByte;
    
    SimpleInputStream(final TDSReader tdsReader, final int payLoadLength, final InputStreamGetterArgs getterArgs, final ServerDTVImpl dtv) throws SQLServerException {
        super(tdsReader, getterArgs.isAdaptive, getterArgs.isStreaming, dtv);
        this.setLoggingInfo(getterArgs.logContext);
        this.payloadLength = payLoadLength;
    }
    
    @Override
    public void close() throws IOException {
        if (null == this.tdsReader) {
            return;
        }
        if (SimpleInputStream.logger.isLoggable(Level.FINER)) {
            SimpleInputStream.logger.finer(this.toString() + "Enter Closing SimpleInputStream.");
        }
        this.skip(this.payloadLength - this.streamPos);
        this.closeHelper();
        if (SimpleInputStream.logger.isLoggable(Level.FINER)) {
            SimpleInputStream.logger.finer(this.toString() + "Exit Closing SimpleInputStream.");
        }
    }
    
    private boolean isEOS() throws IOException {
        assert this.streamPos <= this.payloadLength;
        return this.streamPos == this.payloadLength;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        this.checkClosed();
        if (SimpleInputStream.logger.isLoggable(Level.FINER)) {
            SimpleInputStream.logger.finer(this.toString() + " Skipping :" + n);
        }
        if (n < 0L) {
            return 0L;
        }
        if (this.isEOS()) {
            return 0L;
        }
        int skipAmount;
        if (this.streamPos + n > this.payloadLength) {
            skipAmount = this.payloadLength - this.streamPos;
        }
        else {
            skipAmount = (int)n;
        }
        try {
            this.tdsReader.skip(skipAmount);
        }
        catch (final SQLServerException e) {
            throw new IOException(e.getMessage());
        }
        this.streamPos += skipAmount;
        if (this.isReadLimitSet && this.streamPos - this.markedStreamPos > this.readLimit) {
            this.clearCurrentMark();
        }
        return skipAmount;
    }
    
    @Override
    public int available() throws IOException {
        this.checkClosed();
        assert this.streamPos <= this.payloadLength;
        int available = this.payloadLength - this.streamPos;
        if (this.tdsReader.available() < available) {
            available = this.tdsReader.available();
        }
        return available;
    }
    
    @Override
    public int read() throws IOException {
        this.checkClosed();
        if (null == this.bSingleByte) {
            this.bSingleByte = new byte[1];
        }
        if (this.isEOS()) {
            return -1;
        }
        final int bytesRead = this.read(this.bSingleByte, 0, 1);
        return (0 == bytesRead) ? -1 : (this.bSingleByte[0] & 0xFF);
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        this.checkClosed();
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int offset, final int maxBytes) throws IOException {
        this.checkClosed();
        if (SimpleInputStream.logger.isLoggable(Level.FINER)) {
            SimpleInputStream.logger.finer(this.toString() + " Reading " + maxBytes + " from stream offset " + this.streamPos + " payload length " + this.payloadLength);
        }
        if (offset < 0 || maxBytes < 0 || offset + maxBytes > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (0 == maxBytes) {
            return 0;
        }
        if (this.isEOS()) {
            return -1;
        }
        int readAmount;
        if (this.streamPos + maxBytes > this.payloadLength) {
            readAmount = this.payloadLength - this.streamPos;
        }
        else {
            readAmount = maxBytes;
        }
        try {
            this.tdsReader.readBytes(b, offset, readAmount);
        }
        catch (final SQLServerException e) {
            throw new IOException(e.getMessage());
        }
        this.streamPos += readAmount;
        if (this.isReadLimitSet && this.streamPos - this.markedStreamPos > this.readLimit) {
            this.clearCurrentMark();
        }
        return readAmount;
    }
    
    @Override
    public void mark(final int readLimit) {
        if (null != this.tdsReader && readLimit > 0) {
            this.currentMark = this.tdsReader.mark();
            this.markedStreamPos = this.streamPos;
            this.setReadLimit(readLimit);
        }
    }
    
    @Override
    public void reset() throws IOException {
        this.resetHelper();
        this.streamPos = this.markedStreamPos;
    }
    
    @Override
    final byte[] getBytes() throws SQLServerException {
        assert 0 == this.streamPos;
        final byte[] value = new byte[this.payloadLength];
        try {
            this.read(value);
            this.close();
        }
        catch (final IOException e) {
            SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
        }
        return value;
    }
}
