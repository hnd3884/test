package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;
import java.nio.ByteBuffer;

final class DataHead
{
    volatile Chunk head;
    volatile Chunk tail;
    DataFile dataFile;
    private final MIMEPart part;
    boolean readOnce;
    volatile long inMemory;
    private Throwable consumedAt;
    
    DataHead(final MIMEPart part) {
        this.part = part;
    }
    
    void addBody(final ByteBuffer buf) {
        synchronized (this) {
            this.inMemory += buf.limit();
        }
        if (this.tail != null) {
            this.tail = this.tail.createNext(this, buf);
        }
        else {
            final Chunk chunk = new Chunk(new MemoryData(buf, this.part.msg.config));
            this.tail = chunk;
            this.head = chunk;
        }
    }
    
    void doneParsing() {
    }
    
    void moveTo(final File f) {
        if (this.dataFile != null) {
            this.dataFile.renameTo(f);
        }
        else {
            try {
                final OutputStream os = new FileOutputStream(f);
                try {
                    final InputStream in = this.readOnce();
                    final byte[] buf = new byte[8192];
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        os.write(buf, 0, len);
                    }
                }
                finally {
                    if (os != null) {
                        os.close();
                    }
                }
            }
            catch (final IOException ioe) {
                throw new MIMEParsingException(ioe);
            }
        }
    }
    
    void close() {
        final Chunk chunk = null;
        this.tail = chunk;
        this.head = chunk;
        if (this.dataFile != null) {
            this.dataFile.close();
        }
    }
    
    public InputStream read() {
        if (this.readOnce) {
            throw new IllegalStateException("readOnce() is called before, read() cannot be called later.");
        }
        while (this.tail == null) {
            if (!this.part.msg.makeProgress()) {
                throw new IllegalStateException("No such MIME Part: " + this.part);
            }
        }
        if (this.head == null) {
            throw new IllegalStateException("Already read. Probably readOnce() is called before.");
        }
        return new ReadMultiStream();
    }
    
    private boolean unconsumed() {
        if (this.consumedAt != null) {
            final AssertionError error = new AssertionError((Object)"readOnce() is already called before. See the nested exception from where it's called.");
            error.initCause(this.consumedAt);
            throw error;
        }
        this.consumedAt = new Exception().fillInStackTrace();
        return true;
    }
    
    public InputStream readOnce() {
        assert this.unconsumed();
        if (this.readOnce) {
            throw new IllegalStateException("readOnce() is called before. It can only be called once.");
        }
        this.readOnce = true;
        while (this.tail == null) {
            if (!this.part.msg.makeProgress() && this.tail == null) {
                throw new IllegalStateException("No such Part: " + this.part);
            }
        }
        final InputStream in = new ReadOnceStream();
        this.head = null;
        return in;
    }
    
    class ReadMultiStream extends InputStream
    {
        Chunk current;
        int offset;
        int len;
        byte[] buf;
        boolean closed;
        
        public ReadMultiStream() {
            this.current = DataHead.this.head;
            this.len = this.current.data.size();
            this.buf = this.current.data.read();
        }
        
        @Override
        public int read(final byte[] b, final int off, int sz) throws IOException {
            if (!this.fetch()) {
                return -1;
            }
            sz = Math.min(sz, this.len - this.offset);
            System.arraycopy(this.buf, this.offset, b, off, sz);
            this.offset += sz;
            return sz;
        }
        
        @Override
        public int read() throws IOException {
            if (!this.fetch()) {
                return -1;
            }
            return this.buf[this.offset++] & 0xFF;
        }
        
        void adjustInMemoryUsage() {
        }
        
        private boolean fetch() throws IOException {
            if (this.closed) {
                throw new IOException("Stream already closed");
            }
            if (this.current == null) {
                return false;
            }
            while (this.offset == this.len) {
                while (!DataHead.this.part.parsed && this.current.next == null) {
                    DataHead.this.part.msg.makeProgress();
                }
                this.current = this.current.next;
                if (this.current == null) {
                    return false;
                }
                this.adjustInMemoryUsage();
                this.offset = 0;
                this.buf = this.current.data.read();
                this.len = this.current.data.size();
            }
            return true;
        }
        
        @Override
        public void close() throws IOException {
            super.close();
            this.current = null;
            this.closed = true;
        }
    }
    
    final class ReadOnceStream extends ReadMultiStream
    {
        @Override
        void adjustInMemoryUsage() {
            synchronized (DataHead.this) {
                final DataHead this$0 = DataHead.this;
                this$0.inMemory -= this.current.data.size();
            }
        }
    }
}
