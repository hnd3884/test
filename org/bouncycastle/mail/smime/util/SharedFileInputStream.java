package org.bouncycastle.mail.smime.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.io.File;
import javax.mail.internet.SharedInputStream;
import java.io.FilterInputStream;

public class SharedFileInputStream extends FilterInputStream implements SharedInputStream
{
    private final SharedFileInputStream _parent;
    private final File _file;
    private final long _start;
    private final long _length;
    private long _position;
    private long _markedPosition;
    private List _subStreams;
    
    public SharedFileInputStream(final String s) throws IOException {
        this(new File(s));
    }
    
    public SharedFileInputStream(final File file) throws IOException {
        this(file, 0L, file.length());
    }
    
    private SharedFileInputStream(final File file, final long start, final long length) throws IOException {
        super(new BufferedInputStream(new FileInputStream(file)));
        this._subStreams = new LinkedList();
        this._parent = null;
        this._file = file;
        this._start = start;
        this._length = length;
        this.in.skip(start);
    }
    
    private SharedFileInputStream(final SharedFileInputStream parent, final long start, final long length) throws IOException {
        super(new BufferedInputStream(new FileInputStream(parent._file)));
        this._subStreams = new LinkedList();
        this._parent = parent;
        this._file = parent._file;
        this._start = start;
        this._length = length;
        this.in.skip(start);
    }
    
    public long getPosition() {
        return this._position;
    }
    
    public InputStream newStream(final long n, final long n2) {
        try {
            SharedFileInputStream sharedFileInputStream;
            if (n2 < 0L) {
                if (this._length > 0L) {
                    sharedFileInputStream = new SharedFileInputStream(this, this._start + n, this._length - n);
                }
                else if (this._length == 0L) {
                    sharedFileInputStream = new SharedFileInputStream(this, this._start + n, 0L);
                }
                else {
                    sharedFileInputStream = new SharedFileInputStream(this, this._start + n, -1L);
                }
            }
            else {
                sharedFileInputStream = new SharedFileInputStream(this, this._start + n, n2 - n);
            }
            this._subStreams.add(sharedFileInputStream);
            return sharedFileInputStream;
        }
        catch (final IOException ex) {
            throw new IllegalStateException("unable to create shared stream: " + ex);
        }
    }
    
    @Override
    public int read(final byte[] array) throws IOException {
        return this.read(array, 0, array.length);
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        int i = 0;
        if (n2 == 0) {
            return 0;
        }
        while (i < n2) {
            final int read = this.read();
            if (read < 0) {
                break;
            }
            array[n + i] = (byte)read;
            ++i;
        }
        if (i == 0) {
            return -1;
        }
        return i;
    }
    
    @Override
    public int read() throws IOException {
        if (this._position == this._length) {
            return -1;
        }
        ++this._position;
        return this.in.read();
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        long n2;
        for (n2 = 0L; n2 != n && this.read() >= 0; ++n2) {}
        return n2;
    }
    
    @Override
    public void mark(final int n) {
        this._markedPosition = this._position;
        this.in.mark(n);
    }
    
    @Override
    public void reset() throws IOException {
        this._position = this._markedPosition;
        this.in.reset();
    }
    
    public SharedFileInputStream getRoot() {
        if (this._parent != null) {
            return this._parent.getRoot();
        }
        return this;
    }
    
    public void dispose() throws IOException {
        final Iterator iterator = this._subStreams.iterator();
        while (iterator.hasNext()) {
            try {
                ((SharedFileInputStream)iterator.next()).dispose();
            }
            catch (final IOException ex) {}
        }
        this.in.close();
    }
}
