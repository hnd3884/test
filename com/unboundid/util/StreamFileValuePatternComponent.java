package com.unboundid.util;

import com.unboundid.ldap.sdk.LDAPRuntimeException;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicLong;

final class StreamFileValuePatternComponent extends ValuePatternComponent
{
    private static final long serialVersionUID = -4557045230341165225L;
    private final AtomicLong nextReadPosition;
    private final AtomicReference<StreamFileValuePatternReaderThread> threadRef;
    private final File file;
    private final LinkedBlockingQueue<String> lineQueue;
    private final long maxOfferBlockTimeMillis;
    
    StreamFileValuePatternComponent(final String path) throws IOException {
        this(path, 10000, 60000L);
    }
    
    StreamFileValuePatternComponent(final String path, final int queueSize, final long maxOfferBlockTimeMillis) throws IOException {
        Validator.ensureNotNull(path);
        Validator.ensureTrue(queueSize > 0);
        Validator.ensureTrue(maxOfferBlockTimeMillis > 0L);
        this.maxOfferBlockTimeMillis = maxOfferBlockTimeMillis;
        this.file = new File(path);
        if (!this.file.exists()) {
            throw new IOException(UtilityMessages.ERR_STREAM_FILE_VALUE_PATTERN_PATH_MISSING.get(this.file.getAbsolutePath()));
        }
        if (!this.file.isFile()) {
            throw new IOException(UtilityMessages.ERR_STREAM_FILE_VALUE_PATTERN_PATH_NOT_FILE.get(this.file.getAbsolutePath()));
        }
        if (this.file.length() <= 0L) {
            throw new IOException(UtilityMessages.ERR_STREAM_FILE_VALUE_PATTERN_FILE_EMPTY.get(this.file.getAbsolutePath()));
        }
        this.lineQueue = new LinkedBlockingQueue<String>(queueSize);
        this.nextReadPosition = new AtomicLong(0L);
        this.threadRef = new AtomicReference<StreamFileValuePatternReaderThread>();
        final StreamFileValuePatternReaderThread readerThread = new StreamFileValuePatternReaderThread(this.file, this.lineQueue, maxOfferBlockTimeMillis, this.nextReadPosition, this.threadRef);
        this.threadRef.set(readerThread);
        readerThread.start();
    }
    
    @Override
    void append(final StringBuilder buffer) {
        String line = this.lineQueue.poll();
        if (line != null) {
            buffer.append(line);
            return;
        }
        Label_0022: {
            break Label_0022;
            try {
                do {
                    synchronized (this) {
                        StreamFileValuePatternReaderThread readerThread = this.threadRef.get();
                        if (readerThread == null) {
                            readerThread = new StreamFileValuePatternReaderThread(this.file, this.lineQueue, this.maxOfferBlockTimeMillis, this.nextReadPosition, this.threadRef);
                            this.threadRef.set(readerThread);
                            readerThread.start();
                        }
                    }
                    line = this.lineQueue.poll(10L, TimeUnit.MILLISECONDS);
                } while (line == null);
                buffer.append(line);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPRuntimeException(new LDAPException(ResultCode.LOCAL_ERROR, UtilityMessages.ERR_STREAM_FILE_VALUE_PATTERN_ERROR_GETTING_NEXT_VALUE.get(this.file.getAbsolutePath(), StaticUtils.getExceptionMessage(e)), e));
            }
        }
    }
    
    @Override
    boolean supportsBackReference() {
        return true;
    }
}
