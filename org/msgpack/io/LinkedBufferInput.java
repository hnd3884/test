package org.msgpack.io;

import java.util.Iterator;
import java.io.IOException;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

public class LinkedBufferInput extends AbstractInput
{
    LinkedList<ByteBuffer> link;
    int writable;
    private int nextAdvance;
    private byte[] tmpBuffer;
    private ByteBuffer tmpByteBuffer;
    private final int bufferSize;
    
    public LinkedBufferInput(final int bufferSize) {
        this.link = new LinkedList<ByteBuffer>();
        this.writable = -1;
        this.tmpBuffer = new byte[8];
        this.tmpByteBuffer = ByteBuffer.wrap(this.tmpBuffer);
        this.bufferSize = bufferSize;
    }
    
    @Override
    public int read(final byte[] b, int off, int len) throws EOFException {
        if (this.link.isEmpty()) {
            return 0;
        }
        final int olen = len;
        while (true) {
            final ByteBuffer bb = this.link.peekFirst();
            if (len < bb.remaining()) {
                bb.get(b, off, len);
                this.incrReadByteCount(len);
                return olen;
            }
            final int rem = bb.remaining();
            bb.get(b, off, rem);
            this.incrReadByteCount(rem);
            len -= rem;
            off += rem;
            if (!this.removeFirstLink(bb)) {
                return olen - len;
            }
        }
    }
    
    @Override
    public boolean tryRefer(final BufferReferer ref, final int len) throws IOException {
        final ByteBuffer bb = this.link.peekFirst();
        if (bb == null) {
            throw new EndOfBufferException();
        }
        if (bb.remaining() < len) {
            return false;
        }
        boolean success = false;
        final int pos = bb.position();
        final int lim = bb.limit();
        try {
            bb.limit(pos + len);
            ref.refer(bb, true);
            this.incrReadByteCount(len);
            success = true;
        }
        finally {
            bb.limit(lim);
            if (success) {
                bb.position(pos + len);
            }
            else {
                bb.position(pos);
            }
            if (bb.remaining() == 0) {
                this.removeFirstLink(bb);
            }
        }
        return true;
    }
    
    @Override
    public byte readByte() throws EOFException {
        final ByteBuffer bb = this.link.peekFirst();
        if (bb == null || bb.remaining() == 0) {
            throw new EndOfBufferException();
        }
        final byte result = bb.get();
        this.incrReadOneByteCount();
        if (bb.remaining() == 0) {
            this.removeFirstLink(bb);
        }
        return result;
    }
    
    @Override
    public void advance() {
        if (this.link.isEmpty()) {
            return;
        }
        int len = this.nextAdvance;
        ByteBuffer bb;
        do {
            bb = this.link.peekFirst();
            if (len < bb.remaining()) {
                bb.position(bb.position() + len);
                break;
            }
            len -= bb.remaining();
            bb.position(bb.position() + bb.remaining());
        } while (this.removeFirstLink(bb));
        this.incrReadByteCount(this.nextAdvance);
        this.nextAdvance = 0;
    }
    
    private boolean removeFirstLink(final ByteBuffer first) {
        if (this.link.size() != 1) {
            this.link.removeFirst();
            return true;
        }
        if (this.writable >= 0) {
            first.position(0);
            first.limit(0);
            this.writable = first.capacity();
            return false;
        }
        this.link.removeFirst();
        return false;
    }
    
    private void requireMore(int n) throws EOFException {
        int off = 0;
        for (final ByteBuffer bb : this.link) {
            if (n <= bb.remaining()) {
                final int pos = bb.position();
                bb.get(this.tmpBuffer, off, n);
                bb.position(pos);
                return;
            }
            final int rem = bb.remaining();
            final int pos2 = bb.position();
            bb.get(this.tmpBuffer, off, rem);
            bb.position(pos2);
            n -= rem;
            off += rem;
        }
        throw new EndOfBufferException();
    }
    
    private ByteBuffer require(final int n) throws EOFException {
        final ByteBuffer bb = this.link.peekFirst();
        if (bb == null) {
            throw new EndOfBufferException();
        }
        if (n <= bb.remaining()) {
            this.nextAdvance = n;
            return bb;
        }
        this.requireMore(n);
        this.nextAdvance = n;
        return this.tmpByteBuffer;
    }
    
    @Override
    public byte getByte() throws EOFException {
        final ByteBuffer bb = this.require(1);
        return bb.get(bb.position());
    }
    
    @Override
    public short getShort() throws EOFException {
        final ByteBuffer bb = this.require(2);
        return bb.getShort(bb.position());
    }
    
    @Override
    public int getInt() throws EOFException {
        final ByteBuffer bb = this.require(4);
        return bb.getInt(bb.position());
    }
    
    @Override
    public long getLong() throws EOFException {
        final ByteBuffer bb = this.require(8);
        return bb.getLong(bb.position());
    }
    
    @Override
    public float getFloat() throws EOFException {
        final ByteBuffer bb = this.require(4);
        return bb.getFloat(bb.position());
    }
    
    @Override
    public double getDouble() throws EOFException {
        final ByteBuffer bb = this.require(8);
        return bb.getDouble(bb.position());
    }
    
    public void feed(final byte[] b) {
        this.feed(b, 0, b.length, false);
    }
    
    public void feed(final byte[] b, final boolean reference) {
        this.feed(b, 0, b.length, reference);
    }
    
    public void feed(final byte[] b, final int off, final int len) {
        this.feed(b, off, len, false);
    }
    
    public void feed(final byte[] b, int off, int len, final boolean reference) {
        if (reference) {
            if (this.writable > 0 && this.link.peekLast().remaining() == 0) {
                this.link.add(this.link.size() - 1, ByteBuffer.wrap(b, off, len));
                return;
            }
            this.link.addLast(ByteBuffer.wrap(b, off, len));
            this.writable = -1;
        }
        else {
            final ByteBuffer bb = this.link.peekLast();
            if (len <= this.writable) {
                final int pos = bb.position();
                bb.position(bb.limit());
                bb.limit(bb.limit() + len);
                bb.put(b, off, len);
                bb.position(pos);
                this.writable = bb.capacity() - bb.limit();
                return;
            }
            if (this.writable > 0) {
                final int pos = bb.position();
                bb.position(bb.limit());
                bb.limit(bb.limit() + this.writable);
                bb.put(b, off, this.writable);
                bb.position(pos);
                off += this.writable;
                len -= this.writable;
                this.writable = 0;
            }
            final int sz = Math.max(len, this.bufferSize);
            final ByteBuffer nb = ByteBuffer.allocate(sz);
            nb.put(b, off, len);
            nb.limit(len);
            nb.position(0);
            this.link.addLast(nb);
            this.writable = sz - len;
        }
    }
    
    public void feed(final ByteBuffer b) {
        this.feed(b, false);
    }
    
    public void feed(final ByteBuffer buf, final boolean reference) {
        if (reference) {
            if (this.writable > 0 && this.link.peekLast().remaining() == 0) {
                this.link.add(this.link.size() - 1, buf);
                return;
            }
            this.link.addLast(buf);
            this.writable = -1;
        }
        else {
            int rem = buf.remaining();
            final ByteBuffer bb = this.link.peekLast();
            if (rem <= this.writable) {
                final int pos = bb.position();
                bb.position(bb.limit());
                bb.limit(bb.limit() + rem);
                bb.put(buf);
                bb.position(pos);
                this.writable = bb.capacity() - bb.limit();
                return;
            }
            if (this.writable > 0) {
                final int pos = bb.position();
                bb.position(bb.limit());
                bb.limit(bb.limit() + this.writable);
                buf.limit(this.writable);
                bb.put(buf);
                bb.position(pos);
                rem -= this.writable;
                buf.limit(buf.limit() + rem);
                this.writable = 0;
            }
            final int sz = Math.max(rem, this.bufferSize);
            final ByteBuffer nb = ByteBuffer.allocate(sz);
            nb.put(buf);
            nb.limit(rem);
            nb.position(0);
            this.link.addLast(nb);
            this.writable = sz - rem;
        }
    }
    
    public void clear() {
        if (this.writable >= 0) {
            final ByteBuffer bb = this.link.getLast();
            this.link.clear();
            bb.position(0);
            bb.limit(0);
            this.link.addLast(bb);
            this.writable = bb.capacity();
        }
        else {
            this.link.clear();
            this.writable = -1;
        }
    }
    
    public void copyReferencedBuffer() {
        if (this.link.isEmpty()) {
            return;
        }
        int size = 0;
        for (final ByteBuffer bb : this.link) {
            size += bb.remaining();
        }
        if (size == 0) {
            return;
        }
        if (this.writable >= 0) {
            final ByteBuffer last = this.link.removeLast();
            final byte[] copy = new byte[size - last.remaining()];
            int off = 0;
            for (final ByteBuffer bb2 : this.link) {
                final int len = bb2.remaining();
                bb2.get(copy, off, len);
                off += len;
            }
            this.link.clear();
            this.link.add(ByteBuffer.wrap(copy));
            this.link.add(last);
        }
        else {
            final byte[] copy2 = new byte[size];
            int off2 = 0;
            for (final ByteBuffer bb3 : this.link) {
                final int len2 = bb3.remaining();
                bb3.get(copy2, off2, len2);
                off2 += len2;
            }
            this.link.clear();
            this.link.add(ByteBuffer.wrap(copy2));
            this.writable = 0;
        }
    }
    
    public int getSize() {
        int size = 0;
        for (final ByteBuffer bb : this.link) {
            size += bb.remaining();
        }
        return size;
    }
    
    @Override
    public void close() {
    }
}
