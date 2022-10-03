package org.apache.commons.collections4.multiset;

import java.util.AbstractSet;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.Transformer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.collections4.MultiSet;
import java.util.AbstractCollection;

public abstract class AbstractMultiSet<E> extends AbstractCollection<E> implements MultiSet<E>
{
    private transient Set<E> uniqueSet;
    private transient Set<Entry<E>> entrySet;
    
    protected AbstractMultiSet() {
    }
    
    @Override
    public int size() {
        int totalSize = 0;
        for (final Entry<E> entry : this.entrySet()) {
            totalSize += entry.getCount();
        }
        return totalSize;
    }
    
    @Override
    public int getCount(final Object object) {
        for (final Entry<E> entry : this.entrySet()) {
            final E element = entry.getElement();
            if (element == object || (element != null && element.equals(object))) {
                return entry.getCount();
            }
        }
        return 0;
    }
    
    @Override
    public int setCount(final E object, final int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count must not be negative.");
        }
        final int oldCount = this.getCount(object);
        if (oldCount < count) {
            this.add(object, count - oldCount);
        }
        else {
            this.remove(object, oldCount - count);
        }
        return oldCount;
    }
    
    @Override
    public boolean contains(final Object object) {
        return this.getCount(object) > 0;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new MultiSetIterator<E>(this);
    }
    
    @Override
    public boolean add(final E object) {
        this.add(object, 1);
        return true;
    }
    
    @Override
    public int add(final E object, final int occurrences) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear() {
        final Iterator<Entry<E>> it = this.entrySet().iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }
    
    @Override
    public boolean remove(final Object object) {
        return this.remove(object, 1) != 0;
    }
    
    @Override
    public int remove(final Object object, final int occurrences) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAll(final Collection<?> coll) {
        boolean result = false;
        for (final Object obj : coll) {
            final boolean changed = this.remove(obj, this.getCount(obj)) != 0;
            result = (result || changed);
        }
        return result;
    }
    
    @Override
    public Set<E> uniqueSet() {
        if (this.uniqueSet == null) {
            this.uniqueSet = this.createUniqueSet();
        }
        return this.uniqueSet;
    }
    
    protected Set<E> createUniqueSet() {
        return new UniqueSet<E>(this);
    }
    
    protected Iterator<E> createUniqueSetIterator() {
        final Transformer<Entry<E>, E> transformer = new Transformer<Entry<E>, E>() {
            @Override
            public E transform(final Entry<E> entry) {
                return entry.getElement();
            }
        };
        return IteratorUtils.transformedIterator((Iterator<?>)this.entrySet().iterator(), (Transformer<? super Object, ? extends E>)transformer);
    }
    
    @Override
    public Set<Entry<E>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = this.createEntrySet();
        }
        return this.entrySet;
    }
    
    protected Set<Entry<E>> createEntrySet() {
        return (Set<Entry<E>>)new EntrySet((AbstractMultiSet<Object>)this);
    }
    
    protected abstract int uniqueElements();
    
    protected abstract Iterator<Entry<E>> createEntrySetIterator();
    
    protected void doWriteObject(final ObjectOutputStream out) throws IOException {
        out.writeInt(this.entrySet().size());
        for (final Entry<E> entry : this.entrySet()) {
            out.writeObject(entry.getElement());
            out.writeInt(entry.getCount());
        }
    }
    
    protected void doReadObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        for (int entrySize = in.readInt(), i = 0; i < entrySize; ++i) {
            final E obj = (E)in.readObject();
            final int count = in.readInt();
            this.setCount(obj, count);
        }
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof MultiSet)) {
            return false;
        }
        final MultiSet<?> other = (MultiSet<?>)object;
        if (other.size() != this.size()) {
            return false;
        }
        for (final Entry<E> entry : this.entrySet()) {
            if (other.getCount(entry.getElement()) != this.getCount(entry.getElement())) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return this.entrySet().hashCode();
    }
    
    @Override
    public String toString() {
        return this.entrySet().toString();
    }
    
    private static class MultiSetIterator<E> implements Iterator<E>
    {
        private final AbstractMultiSet<E> parent;
        private final Iterator<Entry<E>> entryIterator;
        private Entry<E> current;
        private int itemCount;
        private boolean canRemove;
        
        public MultiSetIterator(final AbstractMultiSet<E> parent) {
            this.parent = parent;
            this.entryIterator = parent.entrySet().iterator();
            this.current = null;
            this.canRemove = false;
        }
        
        @Override
        public boolean hasNext() {
            return this.itemCount > 0 || this.entryIterator.hasNext();
        }
        
        @Override
        public E next() {
            if (this.itemCount == 0) {
                this.current = this.entryIterator.next();
                this.itemCount = this.current.getCount();
            }
            this.canRemove = true;
            --this.itemCount;
            return this.current.getElement();
        }
        
        @Override
        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException();
            }
            final int count = this.current.getCount();
            if (count > 1) {
                this.parent.remove(this.current.getElement());
            }
            else {
                this.entryIterator.remove();
            }
            this.canRemove = false;
        }
    }
    
    protected static class UniqueSet<E> extends AbstractSet<E>
    {
        protected final AbstractMultiSet<E> parent;
        
        protected UniqueSet(final AbstractMultiSet<E> parent) {
            this.parent = parent;
        }
        
        @Override
        public Iterator<E> iterator() {
            return this.parent.createUniqueSetIterator();
        }
        
        @Override
        public boolean contains(final Object key) {
            return this.parent.contains(key);
        }
        
        @Override
        public boolean containsAll(final Collection<?> coll) {
            return this.parent.containsAll(coll);
        }
        
        @Override
        public boolean remove(final Object key) {
            return this.parent.remove(key, this.parent.getCount(key)) != 0;
        }
        
        @Override
        public int size() {
            return this.parent.uniqueElements();
        }
        
        @Override
        public void clear() {
            this.parent.clear();
        }
    }
    
    protected static class EntrySet<E> extends AbstractSet<Entry<E>>
    {
        private final AbstractMultiSet<E> parent;
        
        protected EntrySet(final AbstractMultiSet<E> parent) {
            this.parent = parent;
        }
        
        @Override
        public int size() {
            return this.parent.uniqueElements();
        }
        
        @Override
        public Iterator<Entry<E>> iterator() {
            return this.parent.createEntrySetIterator();
        }
        
        @Override
        public boolean contains(final Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            final Entry<?> entry = (Entry<?>)obj;
            final Object element = entry.getElement();
            return this.parent.getCount(element) == entry.getCount();
        }
        
        @Override
        public boolean remove(final Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            final Entry<?> entry = (Entry<?>)obj;
            final Object element = entry.getElement();
            if (this.parent.contains(element)) {
                final int count = this.parent.getCount(element);
                if (entry.getCount() == count) {
                    this.parent.remove(element, count);
                    return true;
                }
            }
            return false;
        }
    }
    
    protected abstract static class AbstractEntry<E> implements Entry<E>
    {
        @Override
        public boolean equals(final Object object) {
            if (object instanceof Entry) {
                final Entry<?> other = (Entry<?>)object;
                final E element = this.getElement();
                final Object otherElement = other.getElement();
                return this.getCount() == other.getCount() && (element == otherElement || (element != null && element.equals(otherElement)));
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            final E element = this.getElement();
            return ((element == null) ? 0 : element.hashCode()) ^ this.getCount();
        }
        
        @Override
        public String toString() {
            return String.format("%s:%d", this.getElement(), this.getCount());
        }
    }
}
