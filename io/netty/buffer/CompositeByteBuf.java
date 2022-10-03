package io.netty.buffer;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import io.netty.util.ReferenceCounted;
import java.util.Arrays;
import io.netty.util.internal.RecyclableArrayList;
import java.nio.channels.ScatteringByteChannel;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import io.netty.util.internal.EmptyArrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCountUtil;
import java.nio.ByteOrder;
import io.netty.util.IllegalReferenceCountException;
import java.util.Collection;
import io.netty.util.internal.ObjectUtil;
import java.util.Iterator;
import java.nio.ByteBuffer;

public class CompositeByteBuf extends AbstractReferenceCountedByteBuf implements Iterable<ByteBuf>
{
    private static final ByteBuffer EMPTY_NIO_BUFFER;
    private static final Iterator<ByteBuf> EMPTY_ITERATOR;
    private final ByteBufAllocator alloc;
    private final boolean direct;
    private final int maxNumComponents;
    private int componentCount;
    private Component[] components;
    private boolean freed;
    static final ByteWrapper<byte[]> BYTE_ARRAY_WRAPPER;
    static final ByteWrapper<ByteBuffer> BYTE_BUFFER_WRAPPER;
    private Component lastAccessed;
    
    private CompositeByteBuf(final ByteBufAllocator alloc, final boolean direct, final int maxNumComponents, final int initSize) {
        super(Integer.MAX_VALUE);
        this.alloc = ObjectUtil.checkNotNull(alloc, "alloc");
        if (maxNumComponents < 1) {
            throw new IllegalArgumentException("maxNumComponents: " + maxNumComponents + " (expected: >= 1)");
        }
        this.direct = direct;
        this.maxNumComponents = maxNumComponents;
        this.components = newCompArray(initSize, maxNumComponents);
    }
    
    public CompositeByteBuf(final ByteBufAllocator alloc, final boolean direct, final int maxNumComponents) {
        this(alloc, direct, maxNumComponents, 0);
    }
    
    public CompositeByteBuf(final ByteBufAllocator alloc, final boolean direct, final int maxNumComponents, final ByteBuf... buffers) {
        this(alloc, direct, maxNumComponents, buffers, 0);
    }
    
    CompositeByteBuf(final ByteBufAllocator alloc, final boolean direct, final int maxNumComponents, final ByteBuf[] buffers, final int offset) {
        this(alloc, direct, maxNumComponents, buffers.length - offset);
        this.addComponents0(false, 0, buffers, offset);
        this.consolidateIfNeeded();
        this.setIndex0(0, this.capacity());
    }
    
    public CompositeByteBuf(final ByteBufAllocator alloc, final boolean direct, final int maxNumComponents, final Iterable<ByteBuf> buffers) {
        this(alloc, direct, maxNumComponents, (buffers instanceof Collection) ? ((Collection)buffers).size() : 0);
        this.addComponents(false, 0, buffers);
        this.setIndex(0, this.capacity());
    }
    
     <T> CompositeByteBuf(final ByteBufAllocator alloc, final boolean direct, final int maxNumComponents, final ByteWrapper<T> wrapper, final T[] buffers, final int offset) {
        this(alloc, direct, maxNumComponents, buffers.length - offset);
        this.addComponents0(false, 0, wrapper, buffers, offset);
        this.consolidateIfNeeded();
        this.setIndex(0, this.capacity());
    }
    
    private static Component[] newCompArray(final int initComponents, final int maxNumComponents) {
        final int capacityGuess = Math.min(16, maxNumComponents);
        return new Component[Math.max(initComponents, capacityGuess)];
    }
    
    CompositeByteBuf(final ByteBufAllocator alloc) {
        super(Integer.MAX_VALUE);
        this.alloc = alloc;
        this.direct = false;
        this.maxNumComponents = 0;
        this.components = null;
    }
    
    public CompositeByteBuf addComponent(final ByteBuf buffer) {
        return this.addComponent(false, buffer);
    }
    
    public CompositeByteBuf addComponents(final ByteBuf... buffers) {
        return this.addComponents(false, buffers);
    }
    
    public CompositeByteBuf addComponents(final Iterable<ByteBuf> buffers) {
        return this.addComponents(false, buffers);
    }
    
    public CompositeByteBuf addComponent(final int cIndex, final ByteBuf buffer) {
        return this.addComponent(false, cIndex, buffer);
    }
    
    public CompositeByteBuf addComponent(final boolean increaseWriterIndex, final ByteBuf buffer) {
        return this.addComponent(increaseWriterIndex, this.componentCount, buffer);
    }
    
    public CompositeByteBuf addComponents(final boolean increaseWriterIndex, final ByteBuf... buffers) {
        ObjectUtil.checkNotNull(buffers, "buffers");
        this.addComponents0(increaseWriterIndex, this.componentCount, buffers, 0);
        this.consolidateIfNeeded();
        return this;
    }
    
    public CompositeByteBuf addComponents(final boolean increaseWriterIndex, final Iterable<ByteBuf> buffers) {
        return this.addComponents(increaseWriterIndex, this.componentCount, buffers);
    }
    
    public CompositeByteBuf addComponent(final boolean increaseWriterIndex, final int cIndex, final ByteBuf buffer) {
        ObjectUtil.checkNotNull(buffer, "buffer");
        this.addComponent0(increaseWriterIndex, cIndex, buffer);
        this.consolidateIfNeeded();
        return this;
    }
    
    private static void checkForOverflow(final int capacity, final int readableBytes) {
        if (capacity + readableBytes < 0) {
            throw new IllegalArgumentException("Can't increase by " + readableBytes + " as capacity(" + capacity + ") would overflow " + Integer.MAX_VALUE);
        }
    }
    
    private int addComponent0(final boolean increaseWriterIndex, final int cIndex, final ByteBuf buffer) {
        assert buffer != null;
        boolean wasAdded = false;
        try {
            this.checkComponentIndex(cIndex);
            final Component c = this.newComponent(ensureAccessible(buffer), 0);
            final int readableBytes = c.length();
            checkForOverflow(this.capacity(), readableBytes);
            this.addComp(cIndex, c);
            wasAdded = true;
            if (readableBytes > 0 && cIndex < this.componentCount - 1) {
                this.updateComponentOffsets(cIndex);
            }
            else if (cIndex > 0) {
                c.reposition(this.components[cIndex - 1].endOffset);
            }
            if (increaseWriterIndex) {
                this.writerIndex += readableBytes;
            }
            return cIndex;
        }
        finally {
            if (!wasAdded) {
                buffer.release();
            }
        }
    }
    
    private static ByteBuf ensureAccessible(final ByteBuf buf) {
        if (CompositeByteBuf.checkAccessible && !buf.isAccessible()) {
            throw new IllegalReferenceCountException(0);
        }
        return buf;
    }
    
    private Component newComponent(final ByteBuf buf, final int offset) {
        final int srcIndex = buf.readerIndex();
        final int len = buf.readableBytes();
        ByteBuf unwrapped = buf;
        int unwrappedIndex = srcIndex;
        while (unwrapped instanceof WrappedByteBuf || unwrapped instanceof SwappedByteBuf) {
            unwrapped = unwrapped.unwrap();
        }
        if (unwrapped instanceof AbstractUnpooledSlicedByteBuf) {
            unwrappedIndex += ((AbstractUnpooledSlicedByteBuf)unwrapped).idx(0);
            unwrapped = unwrapped.unwrap();
        }
        else if (unwrapped instanceof PooledSlicedByteBuf) {
            unwrappedIndex += ((PooledSlicedByteBuf)unwrapped).adjustment;
            unwrapped = unwrapped.unwrap();
        }
        else if (unwrapped instanceof DuplicatedByteBuf || unwrapped instanceof PooledDuplicatedByteBuf) {
            unwrapped = unwrapped.unwrap();
        }
        final ByteBuf slice = (buf.capacity() == len) ? buf : null;
        return new Component(buf.order(ByteOrder.BIG_ENDIAN), srcIndex, unwrapped.order(ByteOrder.BIG_ENDIAN), unwrappedIndex, offset, len, slice);
    }
    
    public CompositeByteBuf addComponents(final int cIndex, final ByteBuf... buffers) {
        ObjectUtil.checkNotNull(buffers, "buffers");
        this.addComponents0(false, cIndex, buffers, 0);
        this.consolidateIfNeeded();
        return this;
    }
    
    private CompositeByteBuf addComponents0(final boolean increaseWriterIndex, final int cIndex, final ByteBuf[] buffers, int arrOffset) {
        final int len = buffers.length;
        final int count = len - arrOffset;
        int readableBytes = 0;
        final int capacity = this.capacity();
        for (int i = arrOffset; i < buffers.length; ++i) {
            final ByteBuf b = buffers[i];
            if (b == null) {
                break;
            }
            readableBytes += b.readableBytes();
            checkForOverflow(capacity, readableBytes);
        }
        int ci = Integer.MAX_VALUE;
        try {
            this.checkComponentIndex(cIndex);
            this.shiftComps(cIndex, count);
            int nextOffset = (cIndex > 0) ? this.components[cIndex - 1].endOffset : 0;
            for (ci = cIndex; arrOffset < len; ++arrOffset, ++ci) {
                final ByteBuf b2 = buffers[arrOffset];
                if (b2 == null) {
                    break;
                }
                final Component c = this.newComponent(ensureAccessible(b2), nextOffset);
                this.components[ci] = c;
                nextOffset = c.endOffset;
            }
            return this;
        }
        finally {
            if (ci < this.componentCount) {
                if (ci < cIndex + count) {
                    this.removeCompRange(ci, cIndex + count);
                    while (arrOffset < len) {
                        ReferenceCountUtil.safeRelease(buffers[arrOffset]);
                        ++arrOffset;
                    }
                }
                this.updateComponentOffsets(ci);
            }
            if (increaseWriterIndex && ci > cIndex && ci <= this.componentCount) {
                this.writerIndex += this.components[ci - 1].endOffset - this.components[cIndex].offset;
            }
        }
    }
    
    private <T> int addComponents0(final boolean increaseWriterIndex, int cIndex, final ByteWrapper<T> wrapper, final T[] buffers, final int offset) {
        this.checkComponentIndex(cIndex);
        for (int i = offset, len = buffers.length; i < len; ++i) {
            final T b = buffers[i];
            if (b == null) {
                break;
            }
            if (!wrapper.isEmpty(b)) {
                cIndex = this.addComponent0(increaseWriterIndex, cIndex, wrapper.wrap(b)) + 1;
                final int size = this.componentCount;
                if (cIndex > size) {
                    cIndex = size;
                }
            }
        }
        return cIndex;
    }
    
    public CompositeByteBuf addComponents(final int cIndex, final Iterable<ByteBuf> buffers) {
        return this.addComponents(false, cIndex, buffers);
    }
    
    public CompositeByteBuf addFlattenedComponents(final boolean increaseWriterIndex, ByteBuf buffer) {
        ObjectUtil.checkNotNull(buffer, "buffer");
        final int ridx = buffer.readerIndex();
        final int widx = buffer.writerIndex();
        if (ridx == widx) {
            buffer.release();
            return this;
        }
        if (!(buffer instanceof CompositeByteBuf)) {
            this.addComponent0(increaseWriterIndex, this.componentCount, buffer);
            this.consolidateIfNeeded();
            return this;
        }
        CompositeByteBuf from;
        if (buffer instanceof WrappedCompositeByteBuf) {
            from = (CompositeByteBuf)buffer.unwrap();
        }
        else {
            from = (CompositeByteBuf)buffer;
        }
        from.checkIndex(ridx, widx - ridx);
        final Component[] fromComponents = from.components;
        final int compCountBefore = this.componentCount;
        final int writerIndexBefore = this.writerIndex;
        try {
            int cidx = from.toComponentIndex0(ridx);
            int newOffset = this.capacity();
            while (true) {
                final Component component = fromComponents[cidx];
                final int compOffset = component.offset;
                final int fromIdx = Math.max(ridx, compOffset);
                final int toIdx = Math.min(widx, component.endOffset);
                final int len = toIdx - fromIdx;
                if (len > 0) {
                    this.addComp(this.componentCount, new Component(component.srcBuf.retain(), component.srcIdx(fromIdx), component.buf, component.idx(fromIdx), newOffset, len, null));
                }
                if (widx == toIdx) {
                    break;
                }
                newOffset += len;
                ++cidx;
            }
            if (increaseWriterIndex) {
                this.writerIndex = writerIndexBefore + (widx - ridx);
            }
            this.consolidateIfNeeded();
            buffer.release();
            buffer = null;
            return this;
        }
        finally {
            if (buffer != null) {
                if (increaseWriterIndex) {
                    this.writerIndex = writerIndexBefore;
                }
                for (int cidx2 = this.componentCount - 1; cidx2 >= compCountBefore; --cidx2) {
                    this.components[cidx2].free();
                    this.removeComp(cidx2);
                }
            }
        }
    }
    
    private CompositeByteBuf addComponents(final boolean increaseIndex, int cIndex, final Iterable<ByteBuf> buffers) {
        if (buffers instanceof ByteBuf) {
            return this.addComponent(increaseIndex, cIndex, (ByteBuf)buffers);
        }
        ObjectUtil.checkNotNull(buffers, "buffers");
        final Iterator<ByteBuf> it = buffers.iterator();
        try {
            this.checkComponentIndex(cIndex);
            while (it.hasNext()) {
                final ByteBuf b = it.next();
                if (b == null) {
                    break;
                }
                cIndex = this.addComponent0(increaseIndex, cIndex, b) + 1;
                cIndex = Math.min(cIndex, this.componentCount);
            }
            while (it.hasNext()) {
                ReferenceCountUtil.safeRelease(it.next());
            }
        }
        finally {
            while (it.hasNext()) {
                ReferenceCountUtil.safeRelease(it.next());
            }
        }
        this.consolidateIfNeeded();
        return this;
    }
    
    private void consolidateIfNeeded() {
        final int size = this.componentCount;
        if (size > this.maxNumComponents) {
            this.consolidate0(0, size);
        }
    }
    
    private void checkComponentIndex(final int cIndex) {
        this.ensureAccessible();
        if (cIndex < 0 || cIndex > this.componentCount) {
            throw new IndexOutOfBoundsException(String.format("cIndex: %d (expected: >= 0 && <= numComponents(%d))", cIndex, this.componentCount));
        }
    }
    
    private void checkComponentIndex(final int cIndex, final int numComponents) {
        this.ensureAccessible();
        if (cIndex < 0 || cIndex + numComponents > this.componentCount) {
            throw new IndexOutOfBoundsException(String.format("cIndex: %d, numComponents: %d (expected: cIndex >= 0 && cIndex + numComponents <= totalNumComponents(%d))", cIndex, numComponents, this.componentCount));
        }
    }
    
    private void updateComponentOffsets(int cIndex) {
        final int size = this.componentCount;
        if (size <= cIndex) {
            return;
        }
        int nextIndex = (cIndex > 0) ? this.components[cIndex - 1].endOffset : 0;
        while (cIndex < size) {
            final Component c = this.components[cIndex];
            c.reposition(nextIndex);
            nextIndex = c.endOffset;
            ++cIndex;
        }
    }
    
    public CompositeByteBuf removeComponent(final int cIndex) {
        this.checkComponentIndex(cIndex);
        final Component comp = this.components[cIndex];
        if (this.lastAccessed == comp) {
            this.lastAccessed = null;
        }
        comp.free();
        this.removeComp(cIndex);
        if (comp.length() > 0) {
            this.updateComponentOffsets(cIndex);
        }
        return this;
    }
    
    public CompositeByteBuf removeComponents(final int cIndex, final int numComponents) {
        this.checkComponentIndex(cIndex, numComponents);
        if (numComponents == 0) {
            return this;
        }
        final int endIndex = cIndex + numComponents;
        boolean needsUpdate = false;
        for (int i = cIndex; i < endIndex; ++i) {
            final Component c = this.components[i];
            if (c.length() > 0) {
                needsUpdate = true;
            }
            if (this.lastAccessed == c) {
                this.lastAccessed = null;
            }
            c.free();
        }
        this.removeCompRange(cIndex, endIndex);
        if (needsUpdate) {
            this.updateComponentOffsets(cIndex);
        }
        return this;
    }
    
    @Override
    public Iterator<ByteBuf> iterator() {
        this.ensureAccessible();
        return (this.componentCount == 0) ? CompositeByteBuf.EMPTY_ITERATOR : new CompositeByteBufIterator();
    }
    
    protected int forEachByteAsc0(int start, final int end, final ByteProcessor processor) throws Exception {
        if (end <= start) {
            return -1;
        }
        int i = this.toComponentIndex0(start);
        int length = end - start;
        while (length > 0) {
            final Component c = this.components[i];
            if (c.offset != c.endOffset) {
                final ByteBuf s = c.buf;
                final int localStart = c.idx(start);
                final int localLength = Math.min(length, c.endOffset - start);
                final int result = (s instanceof AbstractByteBuf) ? ((AbstractByteBuf)s).forEachByteAsc0(localStart, localStart + localLength, processor) : s.forEachByte(localStart, localLength, processor);
                if (result != -1) {
                    return result - c.adjustment;
                }
                start += localLength;
                length -= localLength;
            }
            ++i;
        }
        return -1;
    }
    
    protected int forEachByteDesc0(final int rStart, final int rEnd, final ByteProcessor processor) throws Exception {
        if (rEnd > rStart) {
            return -1;
        }
        int i = this.toComponentIndex0(rStart);
        int length = 1 + rStart - rEnd;
        while (length > 0) {
            final Component c = this.components[i];
            if (c.offset != c.endOffset) {
                final ByteBuf s = c.buf;
                final int localRStart = c.idx(length + rEnd);
                final int localLength = Math.min(length, localRStart);
                final int localIndex = localRStart - localLength;
                final int result = (s instanceof AbstractByteBuf) ? ((AbstractByteBuf)s).forEachByteDesc0(localRStart - 1, localIndex, processor) : s.forEachByteDesc(localIndex, localLength, processor);
                if (result != -1) {
                    return result - c.adjustment;
                }
                length -= localLength;
            }
            --i;
        }
        return -1;
    }
    
    public List<ByteBuf> decompose(final int offset, final int length) {
        this.checkIndex(offset, length);
        if (length == 0) {
            return Collections.emptyList();
        }
        int componentId = this.toComponentIndex0(offset);
        int bytesToSlice = length;
        final Component firstC = this.components[componentId];
        ByteBuf slice = firstC.buf.slice(firstC.idx(offset), Math.min(firstC.endOffset - offset, bytesToSlice));
        bytesToSlice -= slice.readableBytes();
        if (bytesToSlice == 0) {
            return Collections.singletonList(slice);
        }
        final List<ByteBuf> sliceList = new ArrayList<ByteBuf>(this.componentCount - componentId);
        sliceList.add(slice);
        do {
            final Component component = this.components[++componentId];
            slice = component.buf.slice(component.idx(component.offset), Math.min(component.length(), bytesToSlice));
            bytesToSlice -= slice.readableBytes();
            sliceList.add(slice);
        } while (bytesToSlice > 0);
        return sliceList;
    }
    
    @Override
    public boolean isDirect() {
        final int size = this.componentCount;
        if (size == 0) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (!this.components[i].buf.isDirect()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean hasArray() {
        switch (this.componentCount) {
            case 0: {
                return true;
            }
            case 1: {
                return this.components[0].buf.hasArray();
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public byte[] array() {
        switch (this.componentCount) {
            case 0: {
                return EmptyArrays.EMPTY_BYTES;
            }
            case 1: {
                return this.components[0].buf.array();
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
    }
    
    @Override
    public int arrayOffset() {
        switch (this.componentCount) {
            case 0: {
                return 0;
            }
            case 1: {
                final Component c = this.components[0];
                return c.idx(c.buf.arrayOffset());
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
    }
    
    @Override
    public boolean hasMemoryAddress() {
        switch (this.componentCount) {
            case 0: {
                return Unpooled.EMPTY_BUFFER.hasMemoryAddress();
            }
            case 1: {
                return this.components[0].buf.hasMemoryAddress();
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public long memoryAddress() {
        switch (this.componentCount) {
            case 0: {
                return Unpooled.EMPTY_BUFFER.memoryAddress();
            }
            case 1: {
                final Component c = this.components[0];
                return c.buf.memoryAddress() + c.adjustment;
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
    }
    
    @Override
    public int capacity() {
        final int size = this.componentCount;
        return (size > 0) ? this.components[size - 1].endOffset : 0;
    }
    
    @Override
    public CompositeByteBuf capacity(final int newCapacity) {
        this.checkNewCapacity(newCapacity);
        final int size = this.componentCount;
        final int oldCapacity = this.capacity();
        if (newCapacity > oldCapacity) {
            final int paddingLength = newCapacity - oldCapacity;
            final ByteBuf padding = this.allocBuffer(paddingLength).setIndex(0, paddingLength);
            this.addComponent0(false, size, padding);
            if (this.componentCount >= this.maxNumComponents) {
                this.consolidateIfNeeded();
            }
        }
        else if (newCapacity < oldCapacity) {
            this.lastAccessed = null;
            int i = size - 1;
            int bytesToTrim = oldCapacity - newCapacity;
            while (i >= 0) {
                final Component c = this.components[i];
                final int cLength = c.length();
                if (bytesToTrim < cLength) {
                    final Component component = c;
                    component.endOffset -= bytesToTrim;
                    final ByteBuf slice = c.slice;
                    if (slice != null) {
                        c.slice = slice.slice(0, c.length());
                        break;
                    }
                    break;
                }
                else {
                    c.free();
                    bytesToTrim -= cLength;
                    --i;
                }
            }
            this.removeCompRange(i + 1, size);
            if (this.readerIndex() > newCapacity) {
                this.setIndex0(newCapacity, newCapacity);
            }
            else if (this.writerIndex > newCapacity) {
                this.writerIndex = newCapacity;
            }
        }
        return this;
    }
    
    @Override
    public ByteBufAllocator alloc() {
        return this.alloc;
    }
    
    @Override
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }
    
    public int numComponents() {
        return this.componentCount;
    }
    
    public int maxNumComponents() {
        return this.maxNumComponents;
    }
    
    public int toComponentIndex(final int offset) {
        this.checkIndex(offset);
        return this.toComponentIndex0(offset);
    }
    
    private int toComponentIndex0(final int offset) {
        final int size = this.componentCount;
        if (offset == 0) {
            for (int i = 0; i < size; ++i) {
                if (this.components[i].endOffset > 0) {
                    return i;
                }
            }
        }
        if (size <= 2) {
            return (size != 1 && offset >= this.components[0].endOffset) ? 1 : 0;
        }
        int low = 0;
        int high = size;
        while (low <= high) {
            final int mid = low + high >>> 1;
            final Component c = this.components[mid];
            if (offset >= c.endOffset) {
                low = mid + 1;
            }
            else {
                if (offset >= c.offset) {
                    return mid;
                }
                high = mid - 1;
            }
        }
        throw new Error("should not reach here");
    }
    
    public int toByteIndex(final int cIndex) {
        this.checkComponentIndex(cIndex);
        return this.components[cIndex].offset;
    }
    
    @Override
    public byte getByte(final int index) {
        final Component c = this.findComponent(index);
        return c.buf.getByte(c.idx(index));
    }
    
    @Override
    protected byte _getByte(final int index) {
        final Component c = this.findComponent0(index);
        return c.buf.getByte(c.idx(index));
    }
    
    @Override
    protected short _getShort(final int index) {
        final Component c = this.findComponent0(index);
        if (index + 2 <= c.endOffset) {
            return c.buf.getShort(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (short)((this._getByte(index) & 0xFF) << 8 | (this._getByte(index + 1) & 0xFF));
        }
        return (short)((this._getByte(index) & 0xFF) | (this._getByte(index + 1) & 0xFF) << 8);
    }
    
    @Override
    protected short _getShortLE(final int index) {
        final Component c = this.findComponent0(index);
        if (index + 2 <= c.endOffset) {
            return c.buf.getShortLE(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (short)((this._getByte(index) & 0xFF) | (this._getByte(index + 1) & 0xFF) << 8);
        }
        return (short)((this._getByte(index) & 0xFF) << 8 | (this._getByte(index + 1) & 0xFF));
    }
    
    @Override
    protected int _getUnsignedMedium(final int index) {
        final Component c = this.findComponent0(index);
        if (index + 3 <= c.endOffset) {
            return c.buf.getUnsignedMedium(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (this._getShort(index) & 0xFFFF) << 8 | (this._getByte(index + 2) & 0xFF);
        }
        return (this._getShort(index) & 0xFFFF) | (this._getByte(index + 2) & 0xFF) << 16;
    }
    
    @Override
    protected int _getUnsignedMediumLE(final int index) {
        final Component c = this.findComponent0(index);
        if (index + 3 <= c.endOffset) {
            return c.buf.getUnsignedMediumLE(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (this._getShortLE(index) & 0xFFFF) | (this._getByte(index + 2) & 0xFF) << 16;
        }
        return (this._getShortLE(index) & 0xFFFF) << 8 | (this._getByte(index + 2) & 0xFF);
    }
    
    @Override
    protected int _getInt(final int index) {
        final Component c = this.findComponent0(index);
        if (index + 4 <= c.endOffset) {
            return c.buf.getInt(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (this._getShort(index) & 0xFFFF) << 16 | (this._getShort(index + 2) & 0xFFFF);
        }
        return (this._getShort(index) & 0xFFFF) | (this._getShort(index + 2) & 0xFFFF) << 16;
    }
    
    @Override
    protected int _getIntLE(final int index) {
        final Component c = this.findComponent0(index);
        if (index + 4 <= c.endOffset) {
            return c.buf.getIntLE(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (this._getShortLE(index) & 0xFFFF) | (this._getShortLE(index + 2) & 0xFFFF) << 16;
        }
        return (this._getShortLE(index) & 0xFFFF) << 16 | (this._getShortLE(index + 2) & 0xFFFF);
    }
    
    @Override
    protected long _getLong(final int index) {
        final Component c = this.findComponent0(index);
        if (index + 8 <= c.endOffset) {
            return c.buf.getLong(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return ((long)this._getInt(index) & 0xFFFFFFFFL) << 32 | ((long)this._getInt(index + 4) & 0xFFFFFFFFL);
        }
        return ((long)this._getInt(index) & 0xFFFFFFFFL) | ((long)this._getInt(index + 4) & 0xFFFFFFFFL) << 32;
    }
    
    @Override
    protected long _getLongLE(final int index) {
        final Component c = this.findComponent0(index);
        if (index + 8 <= c.endOffset) {
            return c.buf.getLongLE(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return ((long)this._getIntLE(index) & 0xFFFFFFFFL) | ((long)this._getIntLE(index + 4) & 0xFFFFFFFFL) << 32;
        }
        return ((long)this._getIntLE(index) & 0xFFFFFFFFL) << 32 | ((long)this._getIntLE(index + 4) & 0xFFFFFFFFL);
    }
    
    @Override
    public CompositeByteBuf getBytes(int index, final byte[] dst, int dstIndex, int length) {
        this.checkDstIndex(index, length, dstIndex, dst.length);
        if (length == 0) {
            return this;
        }
        int localLength;
        for (int i = this.toComponentIndex0(index); length > 0; length -= localLength, ++i) {
            final Component c = this.components[i];
            localLength = Math.min(length, c.endOffset - index);
            c.buf.getBytes(c.idx(index), dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
        }
        return this;
    }
    
    @Override
    public CompositeByteBuf getBytes(int index, final ByteBuffer dst) {
        final int limit = dst.limit();
        int length = dst.remaining();
        this.checkIndex(index, length);
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0(index);
        try {
            while (length > 0) {
                final Component c = this.components[i];
                final int localLength = Math.min(length, c.endOffset - index);
                dst.limit(dst.position() + localLength);
                c.buf.getBytes(c.idx(index), dst);
                index += localLength;
                length -= localLength;
                ++i;
            }
        }
        finally {
            dst.limit(limit);
        }
        return this;
    }
    
    @Override
    public CompositeByteBuf getBytes(int index, final ByteBuf dst, int dstIndex, int length) {
        this.checkDstIndex(index, length, dstIndex, dst.capacity());
        if (length == 0) {
            return this;
        }
        int localLength;
        for (int i = this.toComponentIndex0(index); length > 0; length -= localLength, ++i) {
            final Component c = this.components[i];
            localLength = Math.min(length, c.endOffset - index);
            c.buf.getBytes(c.idx(index), dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
        }
        return this;
    }
    
    @Override
    public int getBytes(final int index, final GatheringByteChannel out, final int length) throws IOException {
        final int count = this.nioBufferCount();
        if (count == 1) {
            return out.write(this.internalNioBuffer(index, length));
        }
        final long writtenBytes = out.write(this.nioBuffers(index, length));
        if (writtenBytes > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)writtenBytes;
    }
    
    @Override
    public int getBytes(final int index, final FileChannel out, final long position, final int length) throws IOException {
        final int count = this.nioBufferCount();
        if (count == 1) {
            return out.write(this.internalNioBuffer(index, length), position);
        }
        long writtenBytes = 0L;
        for (final ByteBuffer buf : this.nioBuffers(index, length)) {
            writtenBytes += out.write(buf, position + writtenBytes);
        }
        if (writtenBytes > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)writtenBytes;
    }
    
    @Override
    public CompositeByteBuf getBytes(int index, final OutputStream out, int length) throws IOException {
        this.checkIndex(index, length);
        if (length == 0) {
            return this;
        }
        int localLength;
        for (int i = this.toComponentIndex0(index); length > 0; length -= localLength, ++i) {
            final Component c = this.components[i];
            localLength = Math.min(length, c.endOffset - index);
            c.buf.getBytes(c.idx(index), out, localLength);
            index += localLength;
        }
        return this;
    }
    
    @Override
    public CompositeByteBuf setByte(final int index, final int value) {
        final Component c = this.findComponent(index);
        c.buf.setByte(c.idx(index), value);
        return this;
    }
    
    @Override
    protected void _setByte(final int index, final int value) {
        final Component c = this.findComponent0(index);
        c.buf.setByte(c.idx(index), value);
    }
    
    @Override
    public CompositeByteBuf setShort(final int index, final int value) {
        this.checkIndex(index, 2);
        this._setShort(index, value);
        return this;
    }
    
    @Override
    protected void _setShort(final int index, final int value) {
        final Component c = this.findComponent0(index);
        if (index + 2 <= c.endOffset) {
            c.buf.setShort(c.idx(index), value);
        }
        else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setByte(index, (byte)(value >>> 8));
            this._setByte(index + 1, (byte)value);
        }
        else {
            this._setByte(index, (byte)value);
            this._setByte(index + 1, (byte)(value >>> 8));
        }
    }
    
    @Override
    protected void _setShortLE(final int index, final int value) {
        final Component c = this.findComponent0(index);
        if (index + 2 <= c.endOffset) {
            c.buf.setShortLE(c.idx(index), value);
        }
        else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setByte(index, (byte)value);
            this._setByte(index + 1, (byte)(value >>> 8));
        }
        else {
            this._setByte(index, (byte)(value >>> 8));
            this._setByte(index + 1, (byte)value);
        }
    }
    
    @Override
    public CompositeByteBuf setMedium(final int index, final int value) {
        this.checkIndex(index, 3);
        this._setMedium(index, value);
        return this;
    }
    
    @Override
    protected void _setMedium(final int index, final int value) {
        final Component c = this.findComponent0(index);
        if (index + 3 <= c.endOffset) {
            c.buf.setMedium(c.idx(index), value);
        }
        else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setShort(index, (short)(value >> 8));
            this._setByte(index + 2, (byte)value);
        }
        else {
            this._setShort(index, (short)value);
            this._setByte(index + 2, (byte)(value >>> 16));
        }
    }
    
    @Override
    protected void _setMediumLE(final int index, final int value) {
        final Component c = this.findComponent0(index);
        if (index + 3 <= c.endOffset) {
            c.buf.setMediumLE(c.idx(index), value);
        }
        else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setShortLE(index, (short)value);
            this._setByte(index + 2, (byte)(value >>> 16));
        }
        else {
            this._setShortLE(index, (short)(value >> 8));
            this._setByte(index + 2, (byte)value);
        }
    }
    
    @Override
    public CompositeByteBuf setInt(final int index, final int value) {
        this.checkIndex(index, 4);
        this._setInt(index, value);
        return this;
    }
    
    @Override
    protected void _setInt(final int index, final int value) {
        final Component c = this.findComponent0(index);
        if (index + 4 <= c.endOffset) {
            c.buf.setInt(c.idx(index), value);
        }
        else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setShort(index, (short)(value >>> 16));
            this._setShort(index + 2, (short)value);
        }
        else {
            this._setShort(index, (short)value);
            this._setShort(index + 2, (short)(value >>> 16));
        }
    }
    
    @Override
    protected void _setIntLE(final int index, final int value) {
        final Component c = this.findComponent0(index);
        if (index + 4 <= c.endOffset) {
            c.buf.setIntLE(c.idx(index), value);
        }
        else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setShortLE(index, (short)value);
            this._setShortLE(index + 2, (short)(value >>> 16));
        }
        else {
            this._setShortLE(index, (short)(value >>> 16));
            this._setShortLE(index + 2, (short)value);
        }
    }
    
    @Override
    public CompositeByteBuf setLong(final int index, final long value) {
        this.checkIndex(index, 8);
        this._setLong(index, value);
        return this;
    }
    
    @Override
    protected void _setLong(final int index, final long value) {
        final Component c = this.findComponent0(index);
        if (index + 8 <= c.endOffset) {
            c.buf.setLong(c.idx(index), value);
        }
        else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setInt(index, (int)(value >>> 32));
            this._setInt(index + 4, (int)value);
        }
        else {
            this._setInt(index, (int)value);
            this._setInt(index + 4, (int)(value >>> 32));
        }
    }
    
    @Override
    protected void _setLongLE(final int index, final long value) {
        final Component c = this.findComponent0(index);
        if (index + 8 <= c.endOffset) {
            c.buf.setLongLE(c.idx(index), value);
        }
        else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setIntLE(index, (int)value);
            this._setIntLE(index + 4, (int)(value >>> 32));
        }
        else {
            this._setIntLE(index, (int)(value >>> 32));
            this._setIntLE(index + 4, (int)value);
        }
    }
    
    @Override
    public CompositeByteBuf setBytes(int index, final byte[] src, int srcIndex, int length) {
        this.checkSrcIndex(index, length, srcIndex, src.length);
        if (length == 0) {
            return this;
        }
        int localLength;
        for (int i = this.toComponentIndex0(index); length > 0; length -= localLength, ++i) {
            final Component c = this.components[i];
            localLength = Math.min(length, c.endOffset - index);
            c.buf.setBytes(c.idx(index), src, srcIndex, localLength);
            index += localLength;
            srcIndex += localLength;
        }
        return this;
    }
    
    @Override
    public CompositeByteBuf setBytes(int index, final ByteBuffer src) {
        final int limit = src.limit();
        int length = src.remaining();
        this.checkIndex(index, length);
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0(index);
        try {
            while (length > 0) {
                final Component c = this.components[i];
                final int localLength = Math.min(length, c.endOffset - index);
                src.limit(src.position() + localLength);
                c.buf.setBytes(c.idx(index), src);
                index += localLength;
                length -= localLength;
                ++i;
            }
        }
        finally {
            src.limit(limit);
        }
        return this;
    }
    
    @Override
    public CompositeByteBuf setBytes(int index, final ByteBuf src, int srcIndex, int length) {
        this.checkSrcIndex(index, length, srcIndex, src.capacity());
        if (length == 0) {
            return this;
        }
        int localLength;
        for (int i = this.toComponentIndex0(index); length > 0; length -= localLength, ++i) {
            final Component c = this.components[i];
            localLength = Math.min(length, c.endOffset - index);
            c.buf.setBytes(c.idx(index), src, srcIndex, localLength);
            index += localLength;
            srcIndex += localLength;
        }
        return this;
    }
    
    @Override
    public int setBytes(int index, final InputStream in, int length) throws IOException {
        this.checkIndex(index, length);
        if (length == 0) {
            return in.read(EmptyArrays.EMPTY_BYTES);
        }
        int i = this.toComponentIndex0(index);
        int readBytes = 0;
        do {
            final Component c = this.components[i];
            final int localLength = Math.min(length, c.endOffset - index);
            if (localLength == 0) {
                ++i;
            }
            else {
                final int localReadBytes = c.buf.setBytes(c.idx(index), in, localLength);
                if (localReadBytes < 0) {
                    if (readBytes == 0) {
                        return -1;
                    }
                    break;
                }
                else {
                    index += localReadBytes;
                    length -= localReadBytes;
                    readBytes += localReadBytes;
                    if (localReadBytes != localLength) {
                        continue;
                    }
                    ++i;
                }
            }
        } while (length > 0);
        return readBytes;
    }
    
    @Override
    public int setBytes(int index, final ScatteringByteChannel in, int length) throws IOException {
        this.checkIndex(index, length);
        if (length == 0) {
            return in.read(CompositeByteBuf.EMPTY_NIO_BUFFER);
        }
        int i = this.toComponentIndex0(index);
        int readBytes = 0;
        do {
            final Component c = this.components[i];
            final int localLength = Math.min(length, c.endOffset - index);
            if (localLength == 0) {
                ++i;
            }
            else {
                final int localReadBytes = c.buf.setBytes(c.idx(index), in, localLength);
                if (localReadBytes == 0) {
                    break;
                }
                if (localReadBytes < 0) {
                    if (readBytes == 0) {
                        return -1;
                    }
                    break;
                }
                else {
                    index += localReadBytes;
                    length -= localReadBytes;
                    readBytes += localReadBytes;
                    if (localReadBytes != localLength) {
                        continue;
                    }
                    ++i;
                }
            }
        } while (length > 0);
        return readBytes;
    }
    
    @Override
    public int setBytes(int index, final FileChannel in, final long position, int length) throws IOException {
        this.checkIndex(index, length);
        if (length == 0) {
            return in.read(CompositeByteBuf.EMPTY_NIO_BUFFER, position);
        }
        int i = this.toComponentIndex0(index);
        int readBytes = 0;
        do {
            final Component c = this.components[i];
            final int localLength = Math.min(length, c.endOffset - index);
            if (localLength == 0) {
                ++i;
            }
            else {
                final int localReadBytes = c.buf.setBytes(c.idx(index), in, position + readBytes, localLength);
                if (localReadBytes == 0) {
                    break;
                }
                if (localReadBytes < 0) {
                    if (readBytes == 0) {
                        return -1;
                    }
                    break;
                }
                else {
                    index += localReadBytes;
                    length -= localReadBytes;
                    readBytes += localReadBytes;
                    if (localReadBytes != localLength) {
                        continue;
                    }
                    ++i;
                }
            }
        } while (length > 0);
        return readBytes;
    }
    
    @Override
    public ByteBuf copy(final int index, final int length) {
        this.checkIndex(index, length);
        final ByteBuf dst = this.allocBuffer(length);
        if (length != 0) {
            this.copyTo(index, length, this.toComponentIndex0(index), dst);
        }
        return dst;
    }
    
    private void copyTo(int index, int length, final int componentId, final ByteBuf dst) {
        int dstIndex = 0;
        int localLength;
        for (int i = componentId; length > 0; length -= localLength, ++i) {
            final Component c = this.components[i];
            localLength = Math.min(length, c.endOffset - index);
            c.buf.getBytes(c.idx(index), dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
        }
        dst.writerIndex(dst.capacity());
    }
    
    public ByteBuf component(final int cIndex) {
        this.checkComponentIndex(cIndex);
        return this.components[cIndex].duplicate();
    }
    
    public ByteBuf componentAtOffset(final int offset) {
        return this.findComponent(offset).duplicate();
    }
    
    public ByteBuf internalComponent(final int cIndex) {
        this.checkComponentIndex(cIndex);
        return this.components[cIndex].slice();
    }
    
    public ByteBuf internalComponentAtOffset(final int offset) {
        return this.findComponent(offset).slice();
    }
    
    private Component findComponent(final int offset) {
        final Component la = this.lastAccessed;
        if (la != null && offset >= la.offset && offset < la.endOffset) {
            this.ensureAccessible();
            return la;
        }
        this.checkIndex(offset);
        return this.findIt(offset);
    }
    
    private Component findComponent0(final int offset) {
        final Component la = this.lastAccessed;
        if (la != null && offset >= la.offset && offset < la.endOffset) {
            return la;
        }
        return this.findIt(offset);
    }
    
    private Component findIt(final int offset) {
        int low = 0;
        int high = this.componentCount;
        while (low <= high) {
            final int mid = low + high >>> 1;
            final Component c = this.components[mid];
            if (c == null) {
                throw new IllegalStateException("No component found for offset. Composite buffer layout might be outdated, e.g. from a discardReadBytes call.");
            }
            if (offset >= c.endOffset) {
                low = mid + 1;
            }
            else {
                if (offset >= c.offset) {
                    return this.lastAccessed = c;
                }
                high = mid - 1;
            }
        }
        throw new Error("should not reach here");
    }
    
    @Override
    public int nioBufferCount() {
        final int size = this.componentCount;
        switch (size) {
            case 0: {
                return 1;
            }
            case 1: {
                return this.components[0].buf.nioBufferCount();
            }
            default: {
                int count = 0;
                for (int i = 0; i < size; ++i) {
                    count += this.components[i].buf.nioBufferCount();
                }
                return count;
            }
        }
    }
    
    @Override
    public ByteBuffer internalNioBuffer(final int index, final int length) {
        switch (this.componentCount) {
            case 0: {
                return CompositeByteBuf.EMPTY_NIO_BUFFER;
            }
            case 1: {
                return this.components[0].internalNioBuffer(index, length);
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
    }
    
    @Override
    public ByteBuffer nioBuffer(final int index, final int length) {
        this.checkIndex(index, length);
        switch (this.componentCount) {
            case 0: {
                return CompositeByteBuf.EMPTY_NIO_BUFFER;
            }
            case 1: {
                final Component c = this.components[0];
                final ByteBuf buf = c.buf;
                if (buf.nioBufferCount() == 1) {
                    return buf.nioBuffer(c.idx(index), length);
                }
                break;
            }
        }
        final ByteBuffer[] buffers = this.nioBuffers(index, length);
        if (buffers.length == 1) {
            return buffers[0];
        }
        final ByteBuffer merged = ByteBuffer.allocate(length).order(this.order());
        for (final ByteBuffer buf2 : buffers) {
            merged.put(buf2);
        }
        merged.flip();
        return merged;
    }
    
    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        this.checkIndex(index, length);
        if (length == 0) {
            return new ByteBuffer[] { CompositeByteBuf.EMPTY_NIO_BUFFER };
        }
        final RecyclableArrayList buffers = RecyclableArrayList.newInstance(this.componentCount);
        try {
            int localLength;
            for (int i = this.toComponentIndex0(index); length > 0; length -= localLength, ++i) {
                final Component c = this.components[i];
                final ByteBuf s = c.buf;
                localLength = Math.min(length, c.endOffset - index);
                switch (s.nioBufferCount()) {
                    case 0: {
                        throw new UnsupportedOperationException();
                    }
                    case 1: {
                        buffers.add(s.nioBuffer(c.idx(index), localLength));
                        break;
                    }
                    default: {
                        Collections.addAll(buffers, s.nioBuffers(c.idx(index), localLength));
                        break;
                    }
                }
                index += localLength;
            }
            return buffers.toArray(new ByteBuffer[0]);
        }
        finally {
            buffers.recycle();
        }
    }
    
    public CompositeByteBuf consolidate() {
        this.ensureAccessible();
        this.consolidate0(0, this.componentCount);
        return this;
    }
    
    public CompositeByteBuf consolidate(final int cIndex, final int numComponents) {
        this.checkComponentIndex(cIndex, numComponents);
        this.consolidate0(cIndex, numComponents);
        return this;
    }
    
    private void consolidate0(final int cIndex, final int numComponents) {
        if (numComponents <= 1) {
            return;
        }
        final int endCIndex = cIndex + numComponents;
        final int startOffset = (cIndex != 0) ? this.components[cIndex].offset : 0;
        final int capacity = this.components[endCIndex - 1].endOffset - startOffset;
        final ByteBuf consolidated = this.allocBuffer(capacity);
        for (int i = cIndex; i < endCIndex; ++i) {
            this.components[i].transferTo(consolidated);
        }
        this.lastAccessed = null;
        this.removeCompRange(cIndex + 1, endCIndex);
        this.components[cIndex] = this.newComponent(consolidated, 0);
        if (cIndex != 0 || numComponents != this.componentCount) {
            this.updateComponentOffsets(cIndex);
        }
    }
    
    public CompositeByteBuf discardReadComponents() {
        this.ensureAccessible();
        final int readerIndex = this.readerIndex();
        if (readerIndex == 0) {
            return this;
        }
        final int writerIndex = this.writerIndex();
        if (readerIndex == writerIndex && writerIndex == this.capacity()) {
            for (int i = 0, size = this.componentCount; i < size; ++i) {
                this.components[i].free();
            }
            this.lastAccessed = null;
            this.clearComps();
            this.setIndex(0, 0);
            this.adjustMarkers(readerIndex);
            return this;
        }
        int firstComponentId = 0;
        Component c = null;
        for (int size2 = this.componentCount; firstComponentId < size2; ++firstComponentId) {
            c = this.components[firstComponentId];
            if (c.endOffset > readerIndex) {
                break;
            }
            c.free();
        }
        if (firstComponentId == 0) {
            return this;
        }
        final Component la = this.lastAccessed;
        if (la != null && la.endOffset <= readerIndex) {
            this.lastAccessed = null;
        }
        this.removeCompRange(0, firstComponentId);
        final int offset = c.offset;
        this.updateComponentOffsets(0);
        this.setIndex(readerIndex - offset, writerIndex - offset);
        this.adjustMarkers(offset);
        return this;
    }
    
    @Override
    public CompositeByteBuf discardReadBytes() {
        this.ensureAccessible();
        final int readerIndex = this.readerIndex();
        if (readerIndex == 0) {
            return this;
        }
        final int writerIndex = this.writerIndex();
        if (readerIndex == writerIndex && writerIndex == this.capacity()) {
            for (int i = 0, size = this.componentCount; i < size; ++i) {
                this.components[i].free();
            }
            this.lastAccessed = null;
            this.clearComps();
            this.setIndex(0, 0);
            this.adjustMarkers(readerIndex);
            return this;
        }
        int firstComponentId = 0;
        Component c = null;
        for (int size2 = this.componentCount; firstComponentId < size2; ++firstComponentId) {
            c = this.components[firstComponentId];
            if (c.endOffset > readerIndex) {
                break;
            }
            c.free();
        }
        final int trimmedBytes = readerIndex - c.offset;
        c.offset = 0;
        final Component component = c;
        component.endOffset -= readerIndex;
        final Component component2 = c;
        component2.srcAdjustment += readerIndex;
        final Component component3 = c;
        component3.adjustment += readerIndex;
        final ByteBuf slice = c.slice;
        if (slice != null) {
            c.slice = slice.slice(trimmedBytes, c.length());
        }
        final Component la = this.lastAccessed;
        if (la != null && la.endOffset <= readerIndex) {
            this.lastAccessed = null;
        }
        this.removeCompRange(0, firstComponentId);
        this.updateComponentOffsets(0);
        this.setIndex(0, writerIndex - readerIndex);
        this.adjustMarkers(readerIndex);
        return this;
    }
    
    private ByteBuf allocBuffer(final int capacity) {
        return this.direct ? this.alloc().directBuffer(capacity) : this.alloc().heapBuffer(capacity);
    }
    
    @Override
    public String toString() {
        String result = super.toString();
        result = result.substring(0, result.length() - 1);
        return result + ", components=" + this.componentCount + ')';
    }
    
    @Override
    public CompositeByteBuf readerIndex(final int readerIndex) {
        super.readerIndex(readerIndex);
        return this;
    }
    
    @Override
    public CompositeByteBuf writerIndex(final int writerIndex) {
        super.writerIndex(writerIndex);
        return this;
    }
    
    @Override
    public CompositeByteBuf setIndex(final int readerIndex, final int writerIndex) {
        super.setIndex(readerIndex, writerIndex);
        return this;
    }
    
    @Override
    public CompositeByteBuf clear() {
        super.clear();
        return this;
    }
    
    @Override
    public CompositeByteBuf markReaderIndex() {
        super.markReaderIndex();
        return this;
    }
    
    @Override
    public CompositeByteBuf resetReaderIndex() {
        super.resetReaderIndex();
        return this;
    }
    
    @Override
    public CompositeByteBuf markWriterIndex() {
        super.markWriterIndex();
        return this;
    }
    
    @Override
    public CompositeByteBuf resetWriterIndex() {
        super.resetWriterIndex();
        return this;
    }
    
    @Override
    public CompositeByteBuf ensureWritable(final int minWritableBytes) {
        super.ensureWritable(minWritableBytes);
        return this;
    }
    
    @Override
    public CompositeByteBuf getBytes(final int index, final ByteBuf dst) {
        return this.getBytes(index, dst, dst.writableBytes());
    }
    
    @Override
    public CompositeByteBuf getBytes(final int index, final ByteBuf dst, final int length) {
        this.getBytes(index, dst, dst.writerIndex(), length);
        dst.writerIndex(dst.writerIndex() + length);
        return this;
    }
    
    @Override
    public CompositeByteBuf getBytes(final int index, final byte[] dst) {
        return this.getBytes(index, dst, 0, dst.length);
    }
    
    @Override
    public CompositeByteBuf setBoolean(final int index, final boolean value) {
        return this.setByte(index, value ? 1 : 0);
    }
    
    @Override
    public CompositeByteBuf setChar(final int index, final int value) {
        return this.setShort(index, value);
    }
    
    @Override
    public CompositeByteBuf setFloat(final int index, final float value) {
        return this.setInt(index, Float.floatToRawIntBits(value));
    }
    
    @Override
    public CompositeByteBuf setDouble(final int index, final double value) {
        return this.setLong(index, Double.doubleToRawLongBits(value));
    }
    
    @Override
    public CompositeByteBuf setBytes(final int index, final ByteBuf src) {
        super.setBytes(index, src, src.readableBytes());
        return this;
    }
    
    @Override
    public CompositeByteBuf setBytes(final int index, final ByteBuf src, final int length) {
        super.setBytes(index, src, length);
        return this;
    }
    
    @Override
    public CompositeByteBuf setBytes(final int index, final byte[] src) {
        return this.setBytes(index, src, 0, src.length);
    }
    
    @Override
    public CompositeByteBuf setZero(final int index, final int length) {
        super.setZero(index, length);
        return this;
    }
    
    @Override
    public CompositeByteBuf readBytes(final ByteBuf dst) {
        super.readBytes(dst, dst.writableBytes());
        return this;
    }
    
    @Override
    public CompositeByteBuf readBytes(final ByteBuf dst, final int length) {
        super.readBytes(dst, length);
        return this;
    }
    
    @Override
    public CompositeByteBuf readBytes(final ByteBuf dst, final int dstIndex, final int length) {
        super.readBytes(dst, dstIndex, length);
        return this;
    }
    
    @Override
    public CompositeByteBuf readBytes(final byte[] dst) {
        super.readBytes(dst, 0, dst.length);
        return this;
    }
    
    @Override
    public CompositeByteBuf readBytes(final byte[] dst, final int dstIndex, final int length) {
        super.readBytes(dst, dstIndex, length);
        return this;
    }
    
    @Override
    public CompositeByteBuf readBytes(final ByteBuffer dst) {
        super.readBytes(dst);
        return this;
    }
    
    @Override
    public CompositeByteBuf readBytes(final OutputStream out, final int length) throws IOException {
        super.readBytes(out, length);
        return this;
    }
    
    @Override
    public CompositeByteBuf skipBytes(final int length) {
        super.skipBytes(length);
        return this;
    }
    
    @Override
    public CompositeByteBuf writeBoolean(final boolean value) {
        this.writeByte(value ? 1 : 0);
        return this;
    }
    
    @Override
    public CompositeByteBuf writeByte(final int value) {
        this.ensureWritable0(1);
        this._setByte(this.writerIndex++, value);
        return this;
    }
    
    @Override
    public CompositeByteBuf writeShort(final int value) {
        super.writeShort(value);
        return this;
    }
    
    @Override
    public CompositeByteBuf writeMedium(final int value) {
        super.writeMedium(value);
        return this;
    }
    
    @Override
    public CompositeByteBuf writeInt(final int value) {
        super.writeInt(value);
        return this;
    }
    
    @Override
    public CompositeByteBuf writeLong(final long value) {
        super.writeLong(value);
        return this;
    }
    
    @Override
    public CompositeByteBuf writeChar(final int value) {
        super.writeShort(value);
        return this;
    }
    
    @Override
    public CompositeByteBuf writeFloat(final float value) {
        super.writeInt(Float.floatToRawIntBits(value));
        return this;
    }
    
    @Override
    public CompositeByteBuf writeDouble(final double value) {
        super.writeLong(Double.doubleToRawLongBits(value));
        return this;
    }
    
    @Override
    public CompositeByteBuf writeBytes(final ByteBuf src) {
        super.writeBytes(src, src.readableBytes());
        return this;
    }
    
    @Override
    public CompositeByteBuf writeBytes(final ByteBuf src, final int length) {
        super.writeBytes(src, length);
        return this;
    }
    
    @Override
    public CompositeByteBuf writeBytes(final ByteBuf src, final int srcIndex, final int length) {
        super.writeBytes(src, srcIndex, length);
        return this;
    }
    
    @Override
    public CompositeByteBuf writeBytes(final byte[] src) {
        super.writeBytes(src, 0, src.length);
        return this;
    }
    
    @Override
    public CompositeByteBuf writeBytes(final byte[] src, final int srcIndex, final int length) {
        super.writeBytes(src, srcIndex, length);
        return this;
    }
    
    @Override
    public CompositeByteBuf writeBytes(final ByteBuffer src) {
        super.writeBytes(src);
        return this;
    }
    
    @Override
    public CompositeByteBuf writeZero(final int length) {
        super.writeZero(length);
        return this;
    }
    
    @Override
    public CompositeByteBuf retain(final int increment) {
        super.retain(increment);
        return this;
    }
    
    @Override
    public CompositeByteBuf retain() {
        super.retain();
        return this;
    }
    
    @Override
    public CompositeByteBuf touch() {
        return this;
    }
    
    @Override
    public CompositeByteBuf touch(final Object hint) {
        return this;
    }
    
    @Override
    public ByteBuffer[] nioBuffers() {
        return this.nioBuffers(this.readerIndex(), this.readableBytes());
    }
    
    @Override
    public CompositeByteBuf discardSomeReadBytes() {
        return this.discardReadComponents();
    }
    
    @Override
    protected void deallocate() {
        if (this.freed) {
            return;
        }
        this.freed = true;
        for (int i = 0, size = this.componentCount; i < size; ++i) {
            this.components[i].free();
        }
    }
    
    @Override
    boolean isAccessible() {
        return !this.freed;
    }
    
    @Override
    public ByteBuf unwrap() {
        return null;
    }
    
    private void clearComps() {
        this.removeCompRange(0, this.componentCount);
    }
    
    private void removeComp(final int i) {
        this.removeCompRange(i, i + 1);
    }
    
    private void removeCompRange(final int from, final int to) {
        if (from >= to) {
            return;
        }
        final int size = this.componentCount;
        assert from >= 0 && to <= size;
        if (to < size) {
            System.arraycopy(this.components, to, this.components, from, size - to);
        }
        int i;
        int newSize;
        for (newSize = (i = size - to + from); i < size; ++i) {
            this.components[i] = null;
        }
        this.componentCount = newSize;
    }
    
    private void addComp(final int i, final Component c) {
        this.shiftComps(i, 1);
        this.components[i] = c;
    }
    
    private void shiftComps(final int i, final int count) {
        final int size = this.componentCount;
        final int newSize = size + count;
        assert i >= 0 && i <= size && count > 0;
        if (newSize > this.components.length) {
            final int newArrSize = Math.max(size + (size >> 1), newSize);
            Component[] newArr;
            if (i == size) {
                newArr = Arrays.copyOf(this.components, newArrSize, (Class<? extends Component[]>)Component[].class);
            }
            else {
                newArr = new Component[newArrSize];
                if (i > 0) {
                    System.arraycopy(this.components, 0, newArr, 0, i);
                }
                if (i < size) {
                    System.arraycopy(this.components, i, newArr, i + count, size - i);
                }
            }
            this.components = newArr;
        }
        else if (i < size) {
            System.arraycopy(this.components, i, this.components, i + count, size - i);
        }
        this.componentCount = newSize;
    }
    
    static {
        EMPTY_NIO_BUFFER = Unpooled.EMPTY_BUFFER.nioBuffer();
        EMPTY_ITERATOR = Collections.emptyList().iterator();
        BYTE_ARRAY_WRAPPER = new ByteWrapper<byte[]>() {
            @Override
            public ByteBuf wrap(final byte[] bytes) {
                return Unpooled.wrappedBuffer(bytes);
            }
            
            @Override
            public boolean isEmpty(final byte[] bytes) {
                return bytes.length == 0;
            }
        };
        BYTE_BUFFER_WRAPPER = new ByteWrapper<ByteBuffer>() {
            @Override
            public ByteBuf wrap(final ByteBuffer bytes) {
                return Unpooled.wrappedBuffer(bytes);
            }
            
            @Override
            public boolean isEmpty(final ByteBuffer bytes) {
                return !bytes.hasRemaining();
            }
        };
    }
    
    private static final class Component
    {
        final ByteBuf srcBuf;
        final ByteBuf buf;
        int srcAdjustment;
        int adjustment;
        int offset;
        int endOffset;
        private ByteBuf slice;
        
        Component(final ByteBuf srcBuf, final int srcOffset, final ByteBuf buf, final int bufOffset, final int offset, final int len, final ByteBuf slice) {
            this.srcBuf = srcBuf;
            this.srcAdjustment = srcOffset - offset;
            this.buf = buf;
            this.adjustment = bufOffset - offset;
            this.offset = offset;
            this.endOffset = offset + len;
            this.slice = slice;
        }
        
        int srcIdx(final int index) {
            return index + this.srcAdjustment;
        }
        
        int idx(final int index) {
            return index + this.adjustment;
        }
        
        int length() {
            return this.endOffset - this.offset;
        }
        
        void reposition(final int newOffset) {
            final int move = newOffset - this.offset;
            this.endOffset += move;
            this.srcAdjustment -= move;
            this.adjustment -= move;
            this.offset = newOffset;
        }
        
        void transferTo(final ByteBuf dst) {
            dst.writeBytes(this.buf, this.idx(this.offset), this.length());
            this.free();
        }
        
        ByteBuf slice() {
            ByteBuf s = this.slice;
            if (s == null) {
                s = (this.slice = this.srcBuf.slice(this.srcIdx(this.offset), this.length()));
            }
            return s;
        }
        
        ByteBuf duplicate() {
            return this.srcBuf.duplicate();
        }
        
        ByteBuffer internalNioBuffer(final int index, final int length) {
            return this.srcBuf.internalNioBuffer(this.srcIdx(index), length);
        }
        
        void free() {
            this.slice = null;
            this.srcBuf.release();
        }
    }
    
    private final class CompositeByteBufIterator implements Iterator<ByteBuf>
    {
        private final int size;
        private int index;
        
        private CompositeByteBufIterator() {
            this.size = CompositeByteBuf.this.numComponents();
        }
        
        @Override
        public boolean hasNext() {
            return this.size > this.index;
        }
        
        @Override
        public ByteBuf next() {
            if (this.size != CompositeByteBuf.this.numComponents()) {
                throw new ConcurrentModificationException();
            }
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            try {
                return CompositeByteBuf.this.components[this.index++].slice();
            }
            catch (final IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Read-Only");
        }
    }
    
    interface ByteWrapper<T>
    {
        ByteBuf wrap(final T p0);
        
        boolean isEmpty(final T p0);
    }
}
