package org.apache.commons.collections4.multimap;

import java.util.Collections;
import java.util.ListIterator;
import org.apache.commons.collections4.ListUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.ListValuedMap;

public abstract class AbstractListValuedMap<K, V> extends AbstractMultiValuedMap<K, V> implements ListValuedMap<K, V>
{
    protected AbstractListValuedMap() {
    }
    
    protected AbstractListValuedMap(final Map<K, ? extends List<V>> map) {
        super((Map<K, ? extends Collection<Object>>)map);
    }
    
    @Override
    protected Map<K, List<V>> getMap() {
        return (Map<K, List<V>>)super.getMap();
    }
    
    @Override
    protected abstract List<V> createCollection();
    
    @Override
    public List<V> get(final K key) {
        return this.wrappedCollection(key);
    }
    
    @Override
    List<V> wrappedCollection(final K key) {
        return new WrappedList(key);
    }
    
    @Override
    public List<V> remove(final Object key) {
        return ListUtils.emptyIfNull(this.getMap().remove(key));
    }
    
    private class WrappedList extends WrappedCollection implements List<V>
    {
        public WrappedList(final K key) {
            AbstractListValuedMap.this.super(key);
        }
        
        @Override
        protected List<V> getMapping() {
            return AbstractListValuedMap.this.getMap().get(this.key);
        }
        
        @Override
        public void add(final int index, final V value) {
            List<V> list = this.getMapping();
            if (list == null) {
                list = AbstractListValuedMap.this.createCollection();
                AbstractListValuedMap.this.getMap().put((K)this.key, list);
            }
            list.add(index, value);
        }
        
        @Override
        public boolean addAll(final int index, final Collection<? extends V> c) {
            List<V> list = this.getMapping();
            if (list == null) {
                list = AbstractListValuedMap.this.createCollection();
                final boolean changed = list.addAll(index, c);
                if (changed) {
                    AbstractListValuedMap.this.getMap().put((K)this.key, list);
                }
                return changed;
            }
            return list.addAll(index, c);
        }
        
        @Override
        public V get(final int index) {
            final List<V> list = ListUtils.emptyIfNull(this.getMapping());
            return list.get(index);
        }
        
        @Override
        public int indexOf(final Object o) {
            final List<V> list = ListUtils.emptyIfNull(this.getMapping());
            return list.indexOf(o);
        }
        
        @Override
        public int lastIndexOf(final Object o) {
            final List<V> list = ListUtils.emptyIfNull(this.getMapping());
            return list.lastIndexOf(o);
        }
        
        @Override
        public ListIterator<V> listIterator() {
            return new ValuesListIterator((K)this.key);
        }
        
        @Override
        public ListIterator<V> listIterator(final int index) {
            return new ValuesListIterator((K)this.key, index);
        }
        
        @Override
        public V remove(final int index) {
            final List<V> list = ListUtils.emptyIfNull(this.getMapping());
            final V value = list.remove(index);
            if (list.isEmpty()) {
                AbstractListValuedMap.this.remove(this.key);
            }
            return value;
        }
        
        @Override
        public V set(final int index, final V value) {
            final List<V> list = ListUtils.emptyIfNull(this.getMapping());
            return list.set(index, value);
        }
        
        @Override
        public List<V> subList(final int fromIndex, final int toIndex) {
            final List<V> list = ListUtils.emptyIfNull(this.getMapping());
            return list.subList(fromIndex, toIndex);
        }
        
        @Override
        public boolean equals(final Object other) {
            final List<V> list = this.getMapping();
            if (list == null) {
                return Collections.emptyList().equals(other);
            }
            if (!(other instanceof List)) {
                return false;
            }
            final List<?> otherList = (List<?>)other;
            return ListUtils.isEqualList(list, otherList);
        }
        
        @Override
        public int hashCode() {
            final List<V> list = this.getMapping();
            return ListUtils.hashCodeForList(list);
        }
    }
    
    private class ValuesListIterator implements ListIterator<V>
    {
        private final K key;
        private List<V> values;
        private ListIterator<V> iterator;
        
        public ValuesListIterator(final K key) {
            this.key = key;
            this.values = ListUtils.emptyIfNull(AbstractListValuedMap.this.getMap().get(key));
            this.iterator = this.values.listIterator();
        }
        
        public ValuesListIterator(final K key, final int index) {
            this.key = key;
            this.values = ListUtils.emptyIfNull(AbstractListValuedMap.this.getMap().get(key));
            this.iterator = this.values.listIterator(index);
        }
        
        @Override
        public void add(final V value) {
            if (AbstractListValuedMap.this.getMap().get(this.key) == null) {
                final List<V> list = AbstractListValuedMap.this.createCollection();
                AbstractListValuedMap.this.getMap().put(this.key, list);
                this.values = list;
                this.iterator = list.listIterator();
            }
            this.iterator.add(value);
        }
        
        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        @Override
        public boolean hasPrevious() {
            return this.iterator.hasPrevious();
        }
        
        @Override
        public V next() {
            return this.iterator.next();
        }
        
        @Override
        public int nextIndex() {
            return this.iterator.nextIndex();
        }
        
        @Override
        public V previous() {
            return this.iterator.previous();
        }
        
        @Override
        public int previousIndex() {
            return this.iterator.previousIndex();
        }
        
        @Override
        public void remove() {
            this.iterator.remove();
            if (this.values.isEmpty()) {
                AbstractListValuedMap.this.getMap().remove(this.key);
            }
        }
        
        @Override
        public void set(final V value) {
            this.iterator.set(value);
        }
    }
}
