package org.glassfish.jersey.message.internal;

import java.io.IOException;
import org.glassfish.jersey.internal.guava.Preconditions;
import java.util.logging.Level;
import java.util.Objects;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;
import java.io.OutputStream;

public final class CommittingOutputStream extends OutputStream
{
    private static final Logger LOGGER;
    private static final OutboundMessageContext.StreamProvider NULL_STREAM_PROVIDER;
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    private OutputStream adaptedOutput;
    private OutboundMessageContext.StreamProvider streamProvider;
    private int bufferSize;
    private ByteArrayOutputStream buffer;
    private boolean directWrite;
    private boolean isCommitted;
    private boolean isClosed;
    private static final String STREAM_PROVIDER_NULL;
    private static final String COMMITTING_STREAM_BUFFERING_ILLEGAL_STATE;
    
    public CommittingOutputStream() {
        this.bufferSize = 0;
        this.directWrite = true;
    }
    
    public void setStreamProvider(final OutboundMessageContext.StreamProvider streamProvider) {
        if (this.isClosed) {
            throw new IllegalStateException(LocalizationMessages.OUTPUT_STREAM_CLOSED());
        }
        Objects.nonNull(streamProvider);
        if (this.streamProvider != null) {
            CommittingOutputStream.LOGGER.log(Level.WARNING, LocalizationMessages.COMMITTING_STREAM_ALREADY_INITIALIZED());
        }
        this.streamProvider = streamProvider;
    }
    
    public void enableBuffering(final int bufferSize) {
        Preconditions.checkState(!this.isCommitted && (this.buffer == null || this.buffer.size() == 0), (Object)CommittingOutputStream.COMMITTING_STREAM_BUFFERING_ILLEGAL_STATE);
        this.bufferSize = bufferSize;
        if (bufferSize <= 0) {
            this.directWrite = true;
            this.buffer = null;
        }
        else {
            this.directWrite = false;
            this.buffer = new ByteArrayOutputStream(bufferSize);
        }
    }
    
    void enableBuffering() {
        this.enableBuffering(8192);
    }
    
    public boolean isCommitted() {
        return this.isCommitted;
    }
    
    private void commitStream() throws IOException {
        this.commitStream(-1);
    }
    
    private void commitStream(final int currentSize) throws IOException {
        if (!this.isCommitted) {
            Preconditions.checkState(this.streamProvider != null, (Object)CommittingOutputStream.STREAM_PROVIDER_NULL);
            this.adaptedOutput = this.streamProvider.getOutputStream(currentSize);
            if (this.adaptedOutput == null) {
                this.adaptedOutput = new NullOutputStream();
            }
            this.directWrite = true;
            this.isCommitted = true;
        }
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        if (this.directWrite) {
            this.commitStream();
            this.adaptedOutput.write(b);
        }
        else if (b.length + this.buffer.size() > this.bufferSize) {
            this.flushBuffer(false);
            this.adaptedOutput.write(b);
        }
        else {
            this.buffer.write(b);
        }
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (this.directWrite) {
            this.commitStream();
            this.adaptedOutput.write(b, off, len);
        }
        else if (len + this.buffer.size() > this.bufferSize) {
            this.flushBuffer(false);
            this.adaptedOutput.write(b, off, len);
        }
        else {
            this.buffer.write(b, off, len);
        }
    }
    
    @Override
    public void write(final int b) throws IOException {
        if (this.directWrite) {
            this.commitStream();
            this.adaptedOutput.write(b);
        }
        else if (this.buffer.size() + 1 > this.bufferSize) {
            this.flushBuffer(false);
            this.adaptedOutput.write(b);
        }
        else {
            this.buffer.write(b);
        }
    }
    
    public void commit() throws IOException {
        this.flushBuffer(true);
        this.commitStream();
    }
    
    @Override
    public void close() throws IOException {
        if (this.isClosed) {
            return;
        }
        this.isClosed = true;
        if (this.streamProvider == null) {
            this.streamProvider = CommittingOutputStream.NULL_STREAM_PROVIDER;
        }
        this.commit();
        this.adaptedOutput.close();
    }
    
    public boolean isClosed() {
        return this.isClosed;
    }
    
    @Override
    public void flush() throws IOException {
        if (this.isCommitted()) {
            this.adaptedOutput.flush();
        }
    }
    
    private void flushBuffer(final boolean endOfStream) throws IOException {
        if (!this.directWrite) {
            int currentSize;
            if (endOfStream) {
                currentSize = ((this.buffer == null) ? 0 : this.buffer.size());
            }
            else {
                currentSize = -1;
            }
            this.commitStream(currentSize);
            if (this.buffer != null) {
                this.buffer.writeTo(this.adaptedOutput);
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(CommittingOutputStream.class.getName());
        NULL_STREAM_PROVIDER = (contentLength -> new NullOutputStream());
        STREAM_PROVIDER_NULL = LocalizationMessages.STREAM_PROVIDER_NULL();
        COMMITTING_STREAM_BUFFERING_ILLEGAL_STATE = LocalizationMessages.COMMITTING_STREAM_BUFFERING_ILLEGAL_STATE();
    }
}
