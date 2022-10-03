package org.apache.commons.collections4.map;

import org.apache.commons.collections4.iterators.AbstractUntypedIteratorDecorator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntryDecorator;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import org.apache.commons.collections4.Unmodifiable;
import java.util.Map;
import org.apache.commons.collections4.set.AbstractSetDecorator;

public final class UnmodifiableEntrySet<K, V> extends AbstractSetDecorator<Map.Entry<K, V>> implements Unmodifiable
{
    private static final long serialVersionUID = 1678353579659253473L;
    
    public static <K, V> Set<Map.Entry<K, V>> unmodifiableEntrySet(final Set<Map.Entry<K, V>> set) {
        if (set instanceof Unmodifiable) {
            return set;
        }
        return (Set<Map.Entry<K, V>>)new UnmodifiableEntrySet((Set<Map.Entry<Object, Object>>)set);
    }
    
    private UnmodifiableEntrySet(final Set<Map.Entry<K, V>> set) {
        super(set);
    }
    
    @Override
    public boolean add(final Map.Entry<K, V> object) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(final Collection<? extends Map.Entry<K, V>> coll) {
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
    public Iterator<Map.Entry<K, V>> iterator() {
        return (Iterator<Map.Entry<K, V>>)new UnmodifiableEntrySetIterator(this.decorated().iterator());
    }
    
    @Override
    public Object[] toArray() {
        final Object[] array = this.decorated().toArray();
        for (int i = 0; i < array.length; ++i) {
            array[i] = new UnmodifiableEntry((Map.Entry<K, V>)array[i]);
        }
        return array;
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        Object[] result = array;
        if (array.length > 0) {
            result = (Object[])Array.newInstance(array.getClass().getComponentType(), 0);
        }
        result = this.decorated().toArray(result);
        for (int i = 0; i < result.length; ++i) {
            result[i] = new UnmodifiableEntry((Map.Entry<K, V>)result[i]);
        }
        if (result.length > array.length) {
            return (T[])result;
        }
        System.arraycopy(result, 0, array, 0, result.length);
        if (array.length > result.length) {
            array[result.length] = null;
        }
        return array;
    }
    
    private class UnmodifiableEntrySetIterator extends AbstractIteratorDecorator<Map.Entry<K, V>>
    {
        protected UnmodifiableEntrySetIterator(final Iterator<Map.Entry<K, V>> iterator) {
            super(iterator);
        }
        
        @Override
        public Map.Entry<K, V> next() {
            return new UnmodifiableEntry((Map.Entry<K, V>)((AbstractUntypedIteratorDecorator<E, O>)this).getIterator().next());
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private class UnmodifiableEntry extends AbstractMapEntryDecorator<K, V>
    {
        protected UnmodifiableEntry(final Map.Entry<K, V> entry) {
            super(entry);
        }
        
        @Override
        public V setValue(final V obj) {
            throw new UnsupportedOperationException();
        }
    }
}
