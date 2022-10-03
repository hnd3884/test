package com.unboundid.util;

import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.io.InputStream;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class AggregateInputStream extends InputStream
{
    private volatile InputStream activeInputStream;
    private final Iterator<InputStream> streamIterator;
    
    public AggregateInputStream(final InputStream... inputStreams) {
        this(StaticUtils.toList(inputStreams));
    }
    
    public AggregateInputStream(final Collection<? extends InputStream> inputStreams) {
        Validator.ensureNotNull(inputStreams);
        final ArrayList<InputStream> streamList = new ArrayList<InputStream>(inputStreams);
        this.streamIterator = streamList.iterator();
        this.activeInputStream = null;
    }
    
    public AggregateInputStream(final File... files) throws IOException {
        this(false, files);
    }
    
    public AggregateInputStream(final boolean ensureBlankLinesBetweenFiles, final File... files) throws IOException {
        Validator.ensureNotNull(files);
        final ArrayList<InputStream> streamList = new ArrayList<InputStream>(2 * files.length);
        IOException ioException = null;
        for (final File f : files) {
            if (ensureBlankLinesBetweenFiles && !streamList.isEmpty()) {
                final ByteStringBuffer buffer = new ByteStringBuffer(4);
                buffer.append(StaticUtils.EOL_BYTES);
                buffer.append(StaticUtils.EOL_BYTES);
                streamList.add(new ByteArrayInputStream(buffer.toByteArray()));
            }
            try {
                streamList.add(new FileInputStream(f));
            }
            catch (final IOException ioe) {
                Debug.debugException(ioe);
                ioException = ioe;
                break;
            }
        }
        if (ioException != null) {
            for (final InputStream s : streamList) {
                if (s != null) {
                    try {
                        s.close();
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                    }
                }
            }
            throw ioException;
        }
        this.streamIterator = streamList.iterator();
        this.activeInputStream = null;
    }
    
    @Override
    public int read() throws IOException {
        while (true) {
            if (this.activeInputStream == null) {
                if (!this.streamIterator.hasNext()) {
                    return -1;
                }
                this.activeInputStream = this.streamIterator.next();
            }
            else {
                final int byteRead = this.activeInputStream.read();
                if (byteRead >= 0) {
                    return byteRead;
                }
                this.activeInputStream.close();
                this.activeInputStream = null;
            }
        }
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        while (true) {
            if (this.activeInputStream == null) {
                if (!this.streamIterator.hasNext()) {
                    return -1;
                }
                this.activeInputStream = this.streamIterator.next();
            }
            else {
                final int bytesRead = this.activeInputStream.read(b, off, len);
                if (bytesRead >= 0) {
                    return bytesRead;
                }
                this.activeInputStream.close();
                this.activeInputStream = null;
            }
        }
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (this.activeInputStream != null) {
            return this.activeInputStream.skip(n);
        }
        if (this.streamIterator.hasNext()) {
            this.activeInputStream = this.streamIterator.next();
            return this.activeInputStream.skip(n);
        }
        return 0L;
    }
    
    @Override
    public int available() throws IOException {
        if (this.activeInputStream != null) {
            return this.activeInputStream.available();
        }
        if (this.streamIterator.hasNext()) {
            this.activeInputStream = this.streamIterator.next();
            return this.activeInputStream.available();
        }
        return 0;
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public void mark(final int readLimit) {
    }
    
    @Override
    public void reset() throws IOException {
        throw new IOException(UtilityMessages.ERR_AGGREGATE_INPUT_STREAM_MARK_NOT_SUPPORTED.get());
    }
    
    @Override
    public void close() throws IOException {
        IOException firstException = null;
        if (this.activeInputStream != null) {
            try {
                this.activeInputStream.close();
            }
            catch (final IOException ioe) {
                Debug.debugException(ioe);
                firstException = ioe;
            }
            this.activeInputStream = null;
        }
        while (this.streamIterator.hasNext()) {
            final InputStream s = this.streamIterator.next();
            try {
                s.close();
            }
            catch (final IOException ioe2) {
                Debug.debugException(ioe2);
                if (firstException != null) {
                    continue;
                }
                firstException = ioe2;
            }
        }
        if (firstException != null) {
            throw firstException;
        }
    }
}
