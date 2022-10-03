package org.apache.commons.collections4.multiset;

import org.apache.commons.collections4.set.UnmodifiableSet;
import java.util.Set;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableMultiSet<E> extends AbstractMultiSetDecorator<E> implements Unmodifiable
{
    private static final long serialVersionUID = 20150611L;
    
    public static <E> MultiSet<E> unmodifiableMultiSet(final MultiSet<? extends E> multiset) {
        if (multiset instanceof Unmodifiable) {
            final MultiSet<E> tmpMultiSet = (MultiSet<E>)multiset;
            return tmpMultiSet;
        }
        return new UnmodifiableMultiSet<E>(multiset);
    }
    
    private UnmodifiableMultiSet(final MultiSet<? extends E> multiset) {
        super(multiset);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.decorated());
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setCollection((Collection<E>)in.readObject());
    }
    
    @Override
    public Iterator<E> iterator() {
        return UnmodifiableIterator.unmodifiableIterator((Iterator<? extends E>)this.decorated().iterator());
    }
    
    @Override
    public boolean add(final E object) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean remove(final Object object) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAll(final Collection<?> coll) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean retainAll(final Collection<?> coll) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int setCount(final E object, final int count) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int add(final E object, final int count) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int remove(final Object object, final int count) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Set<E> uniqueSet() {
        final Set<E> set = this.decorated().uniqueSet();
        return UnmodifiableSet.unmodifiableSet((Set<? extends E>)set);
    }
    
    @Override
    public Set<MultiSet.Entry<E>> entrySet() {
        final Set<MultiSet.Entry<E>> set = this.decorated().entrySet();
        return UnmodifiableSet.unmodifiableSet((Set<? extends MultiSet.Entry<E>>)set);
    }
}
