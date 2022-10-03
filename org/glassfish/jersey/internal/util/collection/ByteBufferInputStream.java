package org.glassfish.jersey.internal.util.collection;

import java.util.Iterator;
import java.io.IOException;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.BlockingQueue;
import java.nio.ByteBuffer;

public final class ByteBufferInputStream extends NonBlockingInputStream
{
    private static final ByteBuffer EOF;
    private boolean eof;
    private ByteBuffer current;
    private final BlockingQueue<ByteBuffer> buffers;
    private final AtomicReference<Object> queueStatus;
    private final AtomicBoolean closed;
    
    public ByteBufferInputStream() {
        this.eof = false;
        this.queueStatus = new AtomicReference<Object>(null);
        this.closed = new AtomicBoolean(false);
        this.buffers = DataStructures.createLinkedTransferQueue();
        this.current = null;
    }
    
    private boolean fetchChunk(final boolean block) throws InterruptedException {
        if (this.eof) {
            return false;
        }
        while (true) {
            while (!this.closed.get()) {
                this.current = (block ? this.buffers.take() : this.buffers.poll());
                if (this.current == null || this.current == ByteBufferInputStream.EOF || this.current.hasRemaining()) {
                    this.eof = (this.current == ByteBufferInputStream.EOF);
                    return !this.eof;
                }
            }
            this.current = ByteBufferInputStream.EOF;
            continue;
        }
    }
    
    private void checkNotClosed() throws IOException {
        if (this.closed.get()) {
            throw new IOException(LocalizationMessages.INPUT_STREAM_CLOSED());
        }
    }
    
    private void checkThrowable() throws IOException {
        final Object o = this.queueStatus.get();
        if (o != null && o != ByteBufferInputStream.EOF && this.queueStatus.compareAndSet(o, ByteBufferInputStream.EOF)) {
            try {
                throw new IOException((Throwable)o);
            }
            finally {
                this.close();
            }
        }
    }
    
    @Override
    public int available() throws IOException {
        if (this.eof || this.closed.get()) {
            this.checkThrowable();
            return 0;
        }
        int available = 0;
        if (this.current != null && this.current.hasRemaining()) {
            available = this.current.remaining();
        }
        for (final ByteBuffer buffer : this.buffers) {
            if (buffer == ByteBufferInputStream.EOF) {
                break;
            }
            available += buffer.remaining();
        }
        this.checkThrowable();
        return this.closed.get() ? 0 : available;
    }
    
    @Override
    public int read() throws IOException {
        return this.tryRead(true);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.tryRead(b, off, len, true);
    }
    
    @Override
    public int tryRead() throws IOException {
        return this.tryRead(false);
    }
    
    @Override
    public int tryRead(final byte[] b) throws IOException {
        return this.tryRead(b, 0, b.length);
    }
    
    @Override
    public int tryRead(final byte[] b, final int off, final int len) throws IOException {
        return this.tryRead(b, off, len, false);
    }
    
    @Override
    public void close() throws IOException {
        if (this.closed.compareAndSet(false, true)) {
            this.closeQueue();
            this.buffers.clear();
        }
        this.checkThrowable();
    }
    
    public boolean put(final ByteBuffer src) throws InterruptedException {
        if (this.queueStatus.get() == null) {
            this.buffers.put(src);
            return true;
        }
        return false;
    }
    
    public void closeQueue() {
        if (this.queueStatus.compareAndSet(null, ByteBufferInputStream.EOF)) {
            try {
                this.buffers.put(ByteBufferInputStream.EOF);
            }
            catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public void closeQueue(final Throwable throwable) {
        if (this.queueStatus.compareAndSet(null, throwable)) {
            try {
                this.buffers.put(ByteBufferInputStream.EOF);
            }
            catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private int tryRead(final byte[] b, final int off, final int len, final boolean block) throws IOException {
        this.checkThrowable();
        this.checkNotClosed();
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        if (this.eof) {
            return -1;
        }
        int i = 0;
        while (i < len) {
            if (this.current != null && this.current.hasRemaining()) {
                final int available = this.current.remaining();
                if (available >= len - i) {
                    this.current.get(b, off + i, len - i);
                    return len;
                }
                this.current.get(b, off + i, available);
                i += available;
            }
            else {
                try {
                    if (!this.fetchChunk(block) || this.current == null) {
                        break;
                    }
                    continue;
                }
                catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    if (block) {
                        throw new IOException(e);
                    }
                    continue;
                }
            }
        }
        return (i == 0 && this.eof) ? -1 : i;
    }
    
    private int tryRead(final boolean block) throws IOException {
        this.checkThrowable();
        this.checkNotClosed();
        if (this.eof) {
            return -1;
        }
        if (this.current != null && this.current.hasRemaining()) {
            return this.current.get() & 0xFF;
        }
        try {
            if (this.fetchChunk(block) && this.current != null) {
                return this.current.get() & 0xFF;
            }
            if (block) {
                return -1;
            }
        }
        catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            if (block) {
                throw new IOException(e);
            }
        }
        return this.eof ? -1 : Integer.MIN_VALUE;
    }
    
    static {
        EOF = ByteBuffer.wrap(new byte[0]);
    }
}
