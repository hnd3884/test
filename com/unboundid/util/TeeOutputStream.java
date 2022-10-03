package com.unboundid.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.OutputStream;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class TeeOutputStream extends OutputStream
{
    private final List<OutputStream> streams;
    
    public TeeOutputStream(final OutputStream... targetStreams) {
        if (targetStreams == null) {
            this.streams = Collections.emptyList();
        }
        else {
            this.streams = Collections.unmodifiableList((List<? extends OutputStream>)new ArrayList<OutputStream>(Arrays.asList(targetStreams)));
        }
    }
    
    public TeeOutputStream(final Collection<? extends OutputStream> targetStreams) {
        if (targetStreams == null) {
            this.streams = Collections.emptyList();
        }
        else {
            this.streams = Collections.unmodifiableList((List<? extends OutputStream>)new ArrayList<OutputStream>(targetStreams));
        }
    }
    
    @Override
    public void write(final int b) throws IOException {
        for (final OutputStream s : this.streams) {
            s.write(b);
        }
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        for (final OutputStream s : this.streams) {
            s.write(b);
        }
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        for (final OutputStream s : this.streams) {
            s.write(b, off, len);
        }
    }
    
    @Override
    public void flush() throws IOException {
        for (final OutputStream s : this.streams) {
            s.flush();
        }
    }
    
    @Override
    public void close() throws IOException {
        IOException exceptionToThrow = null;
        for (final OutputStream s : this.streams) {
            try {
                s.close();
            }
            catch (final IOException ioe) {
                Debug.debugException(ioe);
                if (exceptionToThrow != null) {
                    continue;
                }
                exceptionToThrow = ioe;
            }
        }
        if (exceptionToThrow != null) {
            throw exceptionToThrow;
        }
    }
}
