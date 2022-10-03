package org.apache.lucene.store;

import java.util.Iterator;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.io.EOFException;
import org.apache.lucene.util.WeakIdentityMap;
import java.nio.ByteBuffer;

abstract class ByteBufferIndexInput extends IndexInput implements RandomAccessInput
{
    protected final BufferCleaner cleaner;
    protected final long length;
    protected final long chunkSizeMask;
    protected final int chunkSizePower;
    protected ByteBuffer[] buffers;
    protected int curBufIndex;
    protected ByteBuffer curBuf;
    protected boolean isClone;
    protected final WeakIdentityMap<ByteBufferIndexInput, Boolean> clones;
    
    public static ByteBufferIndexInput newInstance(final String resourceDescription, final ByteBuffer[] buffers, final long length, final int chunkSizePower, final BufferCleaner cleaner, final boolean trackClones) {
        final WeakIdentityMap<ByteBufferIndexInput, Boolean> clones = trackClones ? WeakIdentityMap.newConcurrentHashMap() : null;
        if (buffers.length == 1) {
            return new SingleBufferImpl(resourceDescription, buffers[0], length, chunkSizePower, cleaner, clones);
        }
        return new MultiBufferImpl(resourceDescription, buffers, 0, length, chunkSizePower, cleaner, clones);
    }
    
    ByteBufferIndexInput(final String resourceDescription, final ByteBuffer[] buffers, final long length, final int chunkSizePower, final BufferCleaner cleaner, final WeakIdentityMap<ByteBufferIndexInput, Boolean> clones) {
        super(resourceDescription);
        this.curBufIndex = -1;
        this.isClone = false;
        this.buffers = buffers;
        this.length = length;
        this.chunkSizePower = chunkSizePower;
        this.chunkSizeMask = (1L << chunkSizePower) - 1L;
        this.clones = clones;
        this.cleaner = cleaner;
        assert chunkSizePower >= 0 && chunkSizePower <= 30;
        assert length >>> chunkSizePower < 2147483647L;
    }
    
    @Override
    public final byte readByte() throws IOException {
        try {
            return this.curBuf.get();
        }
        catch (final BufferUnderflowException e) {
            do {
                ++this.curBufIndex;
                if (this.curBufIndex >= this.buffers.length) {
                    throw new EOFException("read past EOF: " + this);
                }
                (this.curBuf = this.buffers[this.curBufIndex]).position(0);
            } while (!this.curBuf.hasRemaining());
            return this.curBuf.get();
        }
        catch (final NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }
    
    @Override
    public final void readBytes(final byte[] b, int offset, int len) throws IOException {
        try {
            this.curBuf.get(b, offset, len);
        }
        catch (final BufferUnderflowException e) {
            for (int curAvail = this.curBuf.remaining(); len > curAvail; curAvail = this.curBuf.remaining()) {
                this.curBuf.get(b, offset, curAvail);
                len -= curAvail;
                offset += curAvail;
                ++this.curBufIndex;
                if (this.curBufIndex >= this.buffers.length) {
                    throw new EOFException("read past EOF: " + this);
                }
                (this.curBuf = this.buffers[this.curBufIndex]).position(0);
            }
            this.curBuf.get(b, offset, len);
        }
        catch (final NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }
    
    @Override
    public final short readShort() throws IOException {
        try {
            return this.curBuf.getShort();
        }
        catch (final BufferUnderflowException e) {
            return super.readShort();
        }
        catch (final NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }
    
    @Override
    public final int readInt() throws IOException {
        try {
            return this.curBuf.getInt();
        }
        catch (final BufferUnderflowException e) {
            return super.readInt();
        }
        catch (final NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }
    
    @Override
    public final long readLong() throws IOException {
        try {
            return this.curBuf.getLong();
        }
        catch (final BufferUnderflowException e) {
            return super.readLong();
        }
        catch (final NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }
    
    @Override
    public long getFilePointer() {
        try {
            return ((long)this.curBufIndex << this.chunkSizePower) + this.curBuf.position();
        }
        catch (final NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }
    
    @Override
    public void seek(final long pos) throws IOException {
        final int bi = (int)(pos >> this.chunkSizePower);
        try {
            if (bi == this.curBufIndex) {
                this.curBuf.position((int)(pos & this.chunkSizeMask));
            }
            else {
                final ByteBuffer b = this.buffers[bi];
                b.position((int)(pos & this.chunkSizeMask));
                this.curBufIndex = bi;
                this.curBuf = b;
            }
        }
        catch (final ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new EOFException("seek past EOF: " + this);
        }
        catch (final NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }
    
    @Override
    public byte readByte(final long pos) throws IOException {
        try {
            final int bi = (int)(pos >> this.chunkSizePower);
            return this.buffers[bi].get((int)(pos & this.chunkSizeMask));
        }
        catch (final IndexOutOfBoundsException ioobe) {
            throw new EOFException("seek past EOF: " + this);
        }
        catch (final NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }
    
    private void setPos(final long pos, final int bi) throws IOException {
        try {
            final ByteBuffer b = this.buffers[bi];
            b.position((int)(pos & this.chunkSizeMask));
            this.curBufIndex = bi;
            this.curBuf = b;
        }
        catch (final ArrayIndexOutOfBoundsException | IllegalArgumentException aioobe) {
            throw new EOFException("seek past EOF: " + this);
        }
        catch (final NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }
    
    @Override
    public short readShort(final long pos) throws IOException {
        final int bi = (int)(pos >> this.chunkSizePower);
        try {
            return this.buffers[bi].getShort((int)(pos & this.chunkSizeMask));
        }
        catch (final IndexOutOfBoundsException ioobe) {
            this.setPos(pos, bi);
            return this.readShort();
        }
        catch (final NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }
    
    @Override
    public int readInt(final long pos) throws IOException {
        final int bi = (int)(pos >> this.chunkSizePower);
        try {
            return this.buffers[bi].getInt((int)(pos & this.chunkSizeMask));
        }
        catch (final IndexOutOfBoundsException ioobe) {
            this.setPos(pos, bi);
            return this.readInt();
        }
        catch (final NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }
    
    @Override
    public long readLong(final long pos) throws IOException {
        final int bi = (int)(pos >> this.chunkSizePower);
        try {
            return this.buffers[bi].getLong((int)(pos & this.chunkSizeMask));
        }
        catch (final IndexOutOfBoundsException ioobe) {
            this.setPos(pos, bi);
            return this.readLong();
        }
        catch (final NullPointerException npe) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
    }
    
    @Override
    public final long length() {
        return this.length;
    }
    
    @Override
    public final ByteBufferIndexInput clone() {
        final ByteBufferIndexInput clone = this.buildSlice((String)null, 0L, this.length);
        try {
            clone.seek(this.getFilePointer());
        }
        catch (final IOException ioe) {
            throw new AssertionError((Object)ioe);
        }
        return clone;
    }
    
    @Override
    public final ByteBufferIndexInput slice(final String sliceDescription, final long offset, final long length) {
        if (offset < 0L || length < 0L || offset + length > this.length) {
            throw new IllegalArgumentException("slice() " + sliceDescription + " out of bounds: offset=" + offset + ",length=" + length + ",fileLength=" + this.length + ": " + this);
        }
        return this.buildSlice(sliceDescription, offset, length);
    }
    
    protected ByteBufferIndexInput buildSlice(final String sliceDescription, final long offset, final long length) {
        if (this.buffers == null) {
            throw new AlreadyClosedException("Already closed: " + this);
        }
        final ByteBuffer[] newBuffers = this.buildSlice(this.buffers, offset, length);
        final int ofs = (int)(offset & this.chunkSizeMask);
        final ByteBufferIndexInput clone = this.newCloneInstance(this.getFullSliceDescription(sliceDescription), newBuffers, ofs, length);
        clone.isClone = true;
        if (this.clones != null) {
            this.clones.put(clone, Boolean.TRUE);
        }
        return clone;
    }
    
    protected ByteBufferIndexInput newCloneInstance(final String newResourceDescription, final ByteBuffer[] newBuffers, final int offset, final long length) {
        if (newBuffers.length == 1) {
            newBuffers[0].position(offset);
            return new SingleBufferImpl(newResourceDescription, newBuffers[0].slice(), length, this.chunkSizePower, this.cleaner, this.clones);
        }
        return new MultiBufferImpl(newResourceDescription, newBuffers, offset, length, this.chunkSizePower, this.cleaner, this.clones);
    }
    
    private ByteBuffer[] buildSlice(final ByteBuffer[] buffers, final long offset, final long length) {
        final long sliceEnd = offset + length;
        final int startIndex = (int)(offset >>> this.chunkSizePower);
        final int endIndex = (int)(sliceEnd >>> this.chunkSizePower);
        final ByteBuffer[] slices = new ByteBuffer[endIndex - startIndex + 1];
        for (int i = 0; i < slices.length; ++i) {
            slices[i] = buffers[startIndex + i].duplicate();
        }
        slices[slices.length - 1].limit((int)(sliceEnd & this.chunkSizeMask));
        return slices;
    }
    
    @Override
    public final void close() throws IOException {
        try {
            if (this.buffers == null) {
                return;
            }
            final ByteBuffer[] bufs = this.buffers;
            this.unsetBuffers();
            if (this.clones != null) {
                this.clones.remove(this);
            }
            if (this.isClone) {
                return;
            }
            if (this.clones != null) {
                final Iterator<ByteBufferIndexInput> it = this.clones.keyIterator();
                while (it.hasNext()) {
                    final ByteBufferIndexInput clone = it.next();
                    assert clone.isClone;
                    clone.unsetBuffers();
                }
                this.clones.clear();
            }
            for (final ByteBuffer b : bufs) {
                this.freeBuffer(b);
            }
        }
        finally {
            this.unsetBuffers();
        }
    }
    
    private void unsetBuffers() {
        this.buffers = null;
        this.curBuf = null;
        this.curBufIndex = 0;
    }
    
    private void freeBuffer(final ByteBuffer b) throws IOException {
        if (this.cleaner != null) {
            this.cleaner.freeBuffer(this, b);
        }
    }
    
    static final class SingleBufferImpl extends ByteBufferIndexInput
    {
        SingleBufferImpl(final String resourceDescription, final ByteBuffer buffer, final long length, final int chunkSizePower, final BufferCleaner cleaner, final WeakIdentityMap<ByteBufferIndexInput, Boolean> clones) {
            super(resourceDescription, new ByteBuffer[] { buffer }, length, chunkSizePower, cleaner, clones);
            this.curBufIndex = 0;
            (this.curBuf = buffer).position(0);
        }
        
        @Override
        public void seek(final long pos) throws IOException {
            try {
                this.curBuf.position((int)pos);
            }
            catch (final IllegalArgumentException e) {
                if (pos < 0L) {
                    throw new IllegalArgumentException("Seeking to negative position: " + this, e);
                }
                throw new EOFException("seek past EOF: " + this);
            }
            catch (final NullPointerException npe) {
                throw new AlreadyClosedException("Already closed: " + this);
            }
        }
        
        @Override
        public long getFilePointer() {
            try {
                return this.curBuf.position();
            }
            catch (final NullPointerException npe) {
                throw new AlreadyClosedException("Already closed: " + this);
            }
        }
        
        @Override
        public byte readByte(final long pos) throws IOException {
            try {
                return this.curBuf.get((int)pos);
            }
            catch (final IllegalArgumentException e) {
                if (pos < 0L) {
                    throw new IllegalArgumentException("Seeking to negative position: " + this, e);
                }
                throw new EOFException("seek past EOF: " + this);
            }
            catch (final NullPointerException npe) {
                throw new AlreadyClosedException("Already closed: " + this);
            }
        }
        
        @Override
        public short readShort(final long pos) throws IOException {
            try {
                return this.curBuf.getShort((int)pos);
            }
            catch (final IllegalArgumentException e) {
                if (pos < 0L) {
                    throw new IllegalArgumentException("Seeking to negative position: " + this, e);
                }
                throw new EOFException("seek past EOF: " + this);
            }
            catch (final NullPointerException npe) {
                throw new AlreadyClosedException("Already closed: " + this);
            }
        }
        
        @Override
        public int readInt(final long pos) throws IOException {
            try {
                return this.curBuf.getInt((int)pos);
            }
            catch (final IllegalArgumentException e) {
                if (pos < 0L) {
                    throw new IllegalArgumentException("Seeking to negative position: " + this, e);
                }
                throw new EOFException("seek past EOF: " + this);
            }
            catch (final NullPointerException npe) {
                throw new AlreadyClosedException("Already closed: " + this);
            }
        }
        
        @Override
        public long readLong(final long pos) throws IOException {
            try {
                return this.curBuf.getLong((int)pos);
            }
            catch (final IllegalArgumentException e) {
                if (pos < 0L) {
                    throw new IllegalArgumentException("Seeking to negative position: " + this, e);
                }
                throw new EOFException("seek past EOF: " + this);
            }
            catch (final NullPointerException npe) {
                throw new AlreadyClosedException("Already closed: " + this);
            }
        }
    }
    
    static final class MultiBufferImpl extends ByteBufferIndexInput
    {
        private final int offset;
        
        MultiBufferImpl(final String resourceDescription, final ByteBuffer[] buffers, final int offset, final long length, final int chunkSizePower, final BufferCleaner cleaner, final WeakIdentityMap<ByteBufferIndexInput, Boolean> clones) {
            super(resourceDescription, buffers, length, chunkSizePower, cleaner, clones);
            this.offset = offset;
            try {
                this.seek(0L);
            }
            catch (final IOException ioe) {
                throw new AssertionError((Object)ioe);
            }
        }
        
        @Override
        public void seek(final long pos) throws IOException {
            assert pos >= 0L;
            super.seek(pos + this.offset);
        }
        
        @Override
        public long getFilePointer() {
            return super.getFilePointer() - this.offset;
        }
        
        @Override
        public byte readByte(final long pos) throws IOException {
            return super.readByte(pos + this.offset);
        }
        
        @Override
        public short readShort(final long pos) throws IOException {
            return super.readShort(pos + this.offset);
        }
        
        @Override
        public int readInt(final long pos) throws IOException {
            return super.readInt(pos + this.offset);
        }
        
        @Override
        public long readLong(final long pos) throws IOException {
            return super.readLong(pos + this.offset);
        }
        
        @Override
        protected ByteBufferIndexInput buildSlice(final String sliceDescription, final long ofs, final long length) {
            return super.buildSlice(sliceDescription, this.offset + ofs, length);
        }
    }
    
    interface BufferCleaner
    {
        void freeBuffer(final ByteBufferIndexInput p0, final ByteBuffer p1) throws IOException;
    }
}
