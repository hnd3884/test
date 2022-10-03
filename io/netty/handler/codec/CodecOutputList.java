package io.netty.handler.codec;

import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.concurrent.FastThreadLocal;
import java.util.RandomAccess;
import java.util.AbstractList;

final class CodecOutputList extends AbstractList<Object> implements RandomAccess
{
    private static final CodecOutputListRecycler NOOP_RECYCLER;
    private static final FastThreadLocal<CodecOutputLists> CODEC_OUTPUT_LISTS_POOL;
    private final CodecOutputListRecycler recycler;
    private int size;
    private Object[] array;
    private boolean insertSinceRecycled;
    
    static CodecOutputList newInstance() {
        return CodecOutputList.CODEC_OUTPUT_LISTS_POOL.get().getOrCreate();
    }
    
    private CodecOutputList(final CodecOutputListRecycler recycler, final int size) {
        this.recycler = recycler;
        this.array = new Object[size];
    }
    
    @Override
    public Object get(final int index) {
        this.checkIndex(index);
        return this.array[index];
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean add(final Object element) {
        ObjectUtil.checkNotNull(element, "element");
        try {
            this.insert(this.size, element);
        }
        catch (final IndexOutOfBoundsException ignore) {
            this.expandArray();
            this.insert(this.size, element);
        }
        ++this.size;
        return true;
    }
    
    @Override
    public Object set(final int index, final Object element) {
        ObjectUtil.checkNotNull(element, "element");
        this.checkIndex(index);
        final Object old = this.array[index];
        this.insert(index, element);
        return old;
    }
    
    @Override
    public void add(final int index, final Object element) {
        ObjectUtil.checkNotNull(element, "element");
        this.checkIndex(index);
        if (this.size == this.array.length) {
            this.expandArray();
        }
        if (index != this.size) {
            System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
        }
        this.insert(index, element);
        ++this.size;
    }
    
    @Override
    public Object remove(final int index) {
        this.checkIndex(index);
        final Object old = this.array[index];
        final int len = this.size - index - 1;
        if (len > 0) {
            System.arraycopy(this.array, index + 1, this.array, index, len);
        }
        this.array[--this.size] = null;
        return old;
    }
    
    @Override
    public void clear() {
        this.size = 0;
    }
    
    boolean insertSinceRecycled() {
        return this.insertSinceRecycled;
    }
    
    void recycle() {
        for (int i = 0; i < this.size; ++i) {
            this.array[i] = null;
        }
        this.size = 0;
        this.insertSinceRecycled = false;
        this.recycler.recycle(this);
    }
    
    Object getUnsafe(final int index) {
        return this.array[index];
    }
    
    private void checkIndex(final int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("expected: index < (" + this.size + "),but actual is (" + this.size + ")");
        }
    }
    
    private void insert(final int index, final Object element) {
        this.array[index] = element;
        this.insertSinceRecycled = true;
    }
    
    private void expandArray() {
        final int newCapacity = this.array.length << 1;
        if (newCapacity < 0) {
            throw new OutOfMemoryError();
        }
        final Object[] newArray = new Object[newCapacity];
        System.arraycopy(this.array, 0, newArray, 0, this.array.length);
        this.array = newArray;
    }
    
    static {
        NOOP_RECYCLER = new CodecOutputListRecycler() {
            @Override
            public void recycle(final CodecOutputList object) {
            }
        };
        CODEC_OUTPUT_LISTS_POOL = new FastThreadLocal<CodecOutputLists>() {
            @Override
            protected CodecOutputLists initialValue() throws Exception {
                return new CodecOutputLists(16);
            }
        };
    }
    
    private static final class CodecOutputLists implements CodecOutputListRecycler
    {
        private final CodecOutputList[] elements;
        private final int mask;
        private int currentIdx;
        private int count;
        
        CodecOutputLists(final int numElements) {
            this.elements = new CodecOutputList[MathUtil.safeFindNextPositivePowerOfTwo(numElements)];
            for (int i = 0; i < this.elements.length; ++i) {
                this.elements[i] = new CodecOutputList(this, 16, null);
            }
            this.count = this.elements.length;
            this.currentIdx = this.elements.length;
            this.mask = this.elements.length - 1;
        }
        
        public CodecOutputList getOrCreate() {
            if (this.count == 0) {
                return new CodecOutputList(CodecOutputList.NOOP_RECYCLER, 4, null);
            }
            --this.count;
            final int idx = this.currentIdx - 1 & this.mask;
            final CodecOutputList list = this.elements[idx];
            this.currentIdx = idx;
            return list;
        }
        
        @Override
        public void recycle(final CodecOutputList codecOutputList) {
            final int idx = this.currentIdx;
            this.elements[idx] = codecOutputList;
            this.currentIdx = (idx + 1 & this.mask);
            ++this.count;
            assert this.count <= this.elements.length;
        }
    }
    
    private interface CodecOutputListRecycler
    {
        void recycle(final CodecOutputList p0);
    }
}
