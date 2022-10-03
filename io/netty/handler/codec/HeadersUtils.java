package io.netty.handler.codec;

import io.netty.util.internal.ObjectUtil;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.util.AbstractList;
import java.util.List;

public final class HeadersUtils
{
    private HeadersUtils() {
    }
    
    public static <K, V> List<String> getAllAsString(final Headers<K, V, ?> headers, final K name) {
        final List<V> allNames = headers.getAll(name);
        return new AbstractList<String>() {
            @Override
            public String get(final int index) {
                final V value = allNames.get(index);
                return (value != null) ? value.toString() : null;
            }
            
            @Override
            public int size() {
                return allNames.size();
            }
        };
    }
    
    public static <K, V> String getAsString(final Headers<K, V, ?> headers, final K name) {
        final V orig = headers.get(name);
        return (orig != null) ? orig.toString() : null;
    }
    
    public static Iterator<Map.Entry<String, String>> iteratorAsString(final Iterable<Map.Entry<CharSequence, CharSequence>> headers) {
        return new StringEntryIterator(headers.iterator());
    }
    
    public static <K, V> String toString(final Class<?> headersClass, final Iterator<Map.Entry<K, V>> headersIt, final int size) {
        final String simpleName = headersClass.getSimpleName();
        if (size == 0) {
            return simpleName + "[]";
        }
        final StringBuilder sb = new StringBuilder(simpleName.length() + 2 + size * 20).append(simpleName).append('[');
        while (headersIt.hasNext()) {
            final Map.Entry<?, ?> header = headersIt.next();
            sb.append(header.getKey()).append(": ").append(header.getValue()).append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.append(']').toString();
    }
    
    public static Set<String> namesAsString(final Headers<CharSequence, CharSequence, ?> headers) {
        return new CharSequenceDelegatingStringSet(headers.names());
    }
    
    private static final class StringEntryIterator implements Iterator<Map.Entry<String, String>>
    {
        private final Iterator<Map.Entry<CharSequence, CharSequence>> iter;
        
        StringEntryIterator(final Iterator<Map.Entry<CharSequence, CharSequence>> iter) {
            this.iter = iter;
        }
        
        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }
        
        @Override
        public Map.Entry<String, String> next() {
            return new StringEntry(this.iter.next());
        }
        
        @Override
        public void remove() {
            this.iter.remove();
        }
    }
    
    private static final class StringEntry implements Map.Entry<String, String>
    {
        private final Map.Entry<CharSequence, CharSequence> entry;
        private String name;
        private String value;
        
        StringEntry(final Map.Entry<CharSequence, CharSequence> entry) {
            this.entry = entry;
        }
        
        @Override
        public String getKey() {
            if (this.name == null) {
                this.name = this.entry.getKey().toString();
            }
            return this.name;
        }
        
        @Override
        public String getValue() {
            if (this.value == null && this.entry.getValue() != null) {
                this.value = this.entry.getValue().toString();
            }
            return this.value;
        }
        
        @Override
        public String setValue(final String value) {
            final String old = this.getValue();
            this.entry.setValue(value);
            return old;
        }
        
        @Override
        public String toString() {
            return this.entry.toString();
        }
    }
    
    private static final class StringIterator<T> implements Iterator<String>
    {
        private final Iterator<T> iter;
        
        StringIterator(final Iterator<T> iter) {
            this.iter = iter;
        }
        
        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }
        
        @Override
        public String next() {
            final T next = this.iter.next();
            return (next != null) ? next.toString() : null;
        }
        
        @Override
        public void remove() {
            this.iter.remove();
        }
    }
    
    private static final class CharSequenceDelegatingStringSet extends DelegatingStringSet<CharSequence>
    {
        CharSequenceDelegatingStringSet(final Set<CharSequence> allNames) {
            super(allNames);
        }
        
        @Override
        public boolean add(final String e) {
            return this.allNames.add((T)e);
        }
        
        @Override
        public boolean addAll(final Collection<? extends String> c) {
            return this.allNames.addAll((Collection<? extends T>)c);
        }
    }
    
    private abstract static class DelegatingStringSet<T> extends AbstractCollection<String> implements Set<String>
    {
        protected final Set<T> allNames;
        
        DelegatingStringSet(final Set<T> allNames) {
            this.allNames = ObjectUtil.checkNotNull(allNames, "allNames");
        }
        
        @Override
        public int size() {
            return this.allNames.size();
        }
        
        @Override
        public boolean isEmpty() {
            return this.allNames.isEmpty();
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.allNames.contains(o.toString());
        }
        
        @Override
        public Iterator<String> iterator() {
            return new StringIterator<Object>(this.allNames.iterator());
        }
        
        @Override
        public boolean remove(final Object o) {
            return this.allNames.remove(o);
        }
        
        @Override
        public void clear() {
            this.allNames.clear();
        }
    }
}
