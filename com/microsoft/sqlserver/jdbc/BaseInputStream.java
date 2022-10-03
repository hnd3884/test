package com.microsoft.sqlserver.jdbc;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.InputStream;

abstract class BaseInputStream extends InputStream
{
    final boolean isAdaptive;
    final boolean isStreaming;
    int payloadLength;
    private static final AtomicInteger lastLoggingID;
    static final Logger logger;
    private String traceID;
    int streamPos;
    int markedStreamPos;
    TDSReaderMark currentMark;
    private ServerDTVImpl dtv;
    TDSReader tdsReader;
    int readLimit;
    boolean isReadLimitSet;
    
    abstract byte[] getBytes() throws SQLServerException;
    
    private static int nextLoggingID() {
        return BaseInputStream.lastLoggingID.incrementAndGet();
    }
    
    @Override
    public final String toString() {
        if (this.traceID == null) {
            this.traceID = this.getClass().getName() + "ID:" + nextLoggingID();
        }
        return this.traceID;
    }
    
    final void setLoggingInfo(final String info) {
        if (BaseInputStream.logger.isLoggable(Level.FINER)) {
            BaseInputStream.logger.finer(this.toString());
        }
    }
    
    BaseInputStream(final TDSReader tdsReader, final boolean isAdaptive, final boolean isStreaming, final ServerDTVImpl dtv) {
        this.streamPos = 0;
        this.markedStreamPos = 0;
        this.readLimit = 0;
        this.isReadLimitSet = false;
        this.tdsReader = tdsReader;
        this.isAdaptive = isAdaptive;
        this.isStreaming = isStreaming;
        if (isAdaptive) {
            this.clearCurrentMark();
        }
        else {
            this.currentMark = tdsReader.mark();
        }
        this.dtv = dtv;
    }
    
    final void clearCurrentMark() {
        this.currentMark = null;
        this.isReadLimitSet = false;
        if (this.isAdaptive && this.isStreaming) {
            this.tdsReader.stream();
        }
    }
    
    void closeHelper() throws IOException {
        if (this.isAdaptive && null != this.dtv) {
            if (BaseInputStream.logger.isLoggable(Level.FINER)) {
                BaseInputStream.logger.finer(this.toString() + " closing the adaptive stream.");
            }
            this.dtv.setPositionAfterStreamed(this.tdsReader);
        }
        this.currentMark = null;
        this.tdsReader = null;
        this.dtv = null;
    }
    
    final void checkClosed() throws IOException {
        if (null == this.tdsReader) {
            throw new IOException(SQLServerException.getErrString("R_streamIsClosed"));
        }
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    void setReadLimit(final int readLimit) {
        if (this.isAdaptive && readLimit > 0) {
            this.readLimit = readLimit;
            this.isReadLimitSet = true;
        }
    }
    
    void resetHelper() throws IOException {
        this.checkClosed();
        if (null == this.currentMark) {
            throw new IOException(SQLServerException.getErrString("R_streamWasNotMarkedBefore"));
        }
        this.tdsReader.reset(this.currentMark);
    }
    
    static {
        lastLoggingID = new AtomicInteger(0);
        logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.InputStream");
    }
}
