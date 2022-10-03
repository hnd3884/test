package org.apache.commons.collections4.map;

import org.apache.commons.collections4.iterators.AbstractUntypedIteratorDecorator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntryDecorator;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import java.lang.reflect.Array;
import java.util.Iterator;
import org.apache.commons.collections4.set.AbstractSetDecorator;
import java.util.Set;
import java.util.Map;

abstract class AbstractInputCheckedMapDecorator<K, V> extends AbstractMapDecorator<K, V>
{
    protected AbstractInputCheckedMapDecorator() {
    }
    
    protected AbstractInputCheckedMapDecorator(final Map<K, V> map) {
        super(map);
    }
    
    protected abstract V checkSetValue(final V p0);
    
    protected boolean isSetValueChecking() {
        return true;
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.isSetValueChecking()) {
            return new EntrySet(this.map.entrySet(), this);
        }
        return this.map.entrySet();
    }
    
    private class EntrySet extends AbstractSetDecorator<Map.Entry<K, V>>
    {
        private static final long serialVersionUID = 4354731610923110264L;
        private final AbstractInputCheckedMapDecorator<K, V> parent;
        
        protected EntrySet(final Set<Map.Entry<K, V>> set, final AbstractInputCheckedMapDecorator<K, V> parent) {
            super(set);
            this.parent = parent;
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return (Iterator<Map.Entry<K, V>>)new EntrySetIterator(this.decorated().iterator(), this.parent);
        }
        
        @Override
        public Object[] toArray() {
            final Object[] array = this.decorated().toArray();
            for (int i = 0; i < array.length; ++i) {
                array[i] = new MapEntry((Map.Entry<K, V>)array[i], this.parent);
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
                result[i] = new MapEntry((Map.Entry<K, V>)result[i], this.parent);
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
    }
    
    private class EntrySetIterator extends AbstractIteratorDecorator<Map.Entry<K, V>>
    {
        private final AbstractInputCheckedMapDecorator<K, V> parent;
        
        protected EntrySetIterator(final Iterator<Map.Entry<K, V>> iterator, final AbstractInputCheckedMapDecorator<K, V> parent) {
            super(iterator);
            this.parent = parent;
        }
        
        @Override
        public Map.Entry<K, V> next() {
            final Map.Entry<K, V> entry = (Map.Entry<K, V>)((AbstractUntypedIteratorDecorator<E, O>)this).getIterator().next();
            return new MapEntry(entry, this.parent);
        }
    }
    
    private class MapEntry extends AbstractMapEntryDecorator<K, V>
    {
        private final AbstractInputCheckedMapDecorator<K, V> parent;
        
        protected MapEntry(final Map.Entry<K, V> entry, final AbstractInputCheckedMapDecorator<K, V> parent) {
            super(entry);
            this.parent = parent;
        }
        
        @Override
        public V setValue(V value) {
            value = this.parent.checkSetValue(value);
            return this.getMapEntry().setValue(value);
        }
    }
}
