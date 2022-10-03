package io.netty.util.internal;

import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import java.util.Collection;
import java.util.ArrayList;

public final class RecyclableArrayList extends ArrayList<Object>
{
    private static final long serialVersionUID = -8605125654176467947L;
    private static final int DEFAULT_INITIAL_CAPACITY = 8;
    private static final ObjectPool<RecyclableArrayList> RECYCLER;
    private boolean insertSinceRecycled;
    private final ObjectPool.Handle<RecyclableArrayList> handle;
    
    public static RecyclableArrayList newInstance() {
        return newInstance(8);
    }
    
    public static RecyclableArrayList newInstance(final int minCapacity) {
        final RecyclableArrayList ret = RecyclableArrayList.RECYCLER.get();
        ret.ensureCapacity(minCapacity);
        return ret;
    }
    
    private RecyclableArrayList(final ObjectPool.Handle<RecyclableArrayList> handle) {
        this(handle, 8);
    }
    
    private RecyclableArrayList(final ObjectPool.Handle<RecyclableArrayList> handle, final int initialCapacity) {
        super(initialCapacity);
        this.handle = handle;
    }
    
    @Override
    public boolean addAll(final Collection<?> c) {
        checkNullElements(c);
        return super.addAll(c) && (this.insertSinceRecycled = true);
    }
    
    @Override
    public boolean addAll(final int index, final Collection<?> c) {
        checkNullElements(c);
        return super.addAll(index, c) && (this.insertSinceRecycled = true);
    }
    
    private static void checkNullElements(final Collection<?> c) {
        if (c instanceof RandomAccess && c instanceof List) {
            final List<?> list = (List)c;
            for (int size = list.size(), i = 0; i < size; ++i) {
                if (list.get(i) == null) {
                    throw new IllegalArgumentException("c contains null values");
                }
            }
        }
        else {
            for (final Object element : c) {
                if (element == null) {
                    throw new IllegalArgumentException("c contains null values");
                }
            }
        }
    }
    
    @Override
    public boolean add(final Object element) {
        return super.add(ObjectUtil.checkNotNull(element, "element")) && (this.insertSinceRecycled = true);
    }
    
    @Override
    public void add(final int index, final Object element) {
        super.add(index, ObjectUtil.checkNotNull(element, "element"));
        this.insertSinceRecycled = true;
    }
    
    @Override
    public Object set(final int index, final Object element) {
        final Object old = super.set(index, ObjectUtil.checkNotNull(element, "element"));
        this.insertSinceRecycled = true;
        return old;
    }
    
    public boolean insertSinceRecycled() {
        return this.insertSinceRecycled;
    }
    
    public boolean recycle() {
        this.clear();
        this.insertSinceRecycled = false;
        this.handle.recycle(this);
        return true;
    }
    
    static {
        RECYCLER = ObjectPool.newPool((ObjectPool.ObjectCreator<RecyclableArrayList>)new ObjectPool.ObjectCreator<RecyclableArrayList>() {
            @Override
            public RecyclableArrayList newObject(final ObjectPool.Handle<RecyclableArrayList> handle) {
                return new RecyclableArrayList(handle, null);
            }
        });
    }
}
