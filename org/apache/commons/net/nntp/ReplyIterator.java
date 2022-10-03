package org.apache.commons.net.nntp;

import java.util.NoSuchElementException;
import java.io.IOException;
import java.io.Closeable;
import org.apache.commons.net.io.Util;
import java.io.Reader;
import org.apache.commons.net.io.DotTerminatedMessageReader;
import java.io.BufferedReader;
import java.util.Iterator;

class ReplyIterator implements Iterator<String>, Iterable<String>
{
    private final BufferedReader reader;
    private String line;
    private Exception savedException;
    
    ReplyIterator(final BufferedReader _reader, final boolean addDotReader) throws IOException {
        this.reader = (addDotReader ? new DotTerminatedMessageReader(_reader) : _reader);
        this.line = this.reader.readLine();
        if (this.line == null) {
            Util.closeQuietly(this.reader);
        }
    }
    
    ReplyIterator(final BufferedReader _reader) throws IOException {
        this(_reader, true);
    }
    
    @Override
    public boolean hasNext() {
        if (this.savedException != null) {
            throw new NoSuchElementException(this.savedException.toString());
        }
        return this.line != null;
    }
    
    @Override
    public String next() throws NoSuchElementException {
        if (this.savedException != null) {
            throw new NoSuchElementException(this.savedException.toString());
        }
        final String prev = this.line;
        if (prev == null) {
            throw new NoSuchElementException();
        }
        try {
            this.line = this.reader.readLine();
            if (this.line == null) {
                Util.closeQuietly(this.reader);
            }
        }
        catch (final IOException ex) {
            this.savedException = ex;
            Util.closeQuietly(this.reader);
        }
        return prev;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Iterator<String> iterator() {
        return this;
    }
}
