package org.glassfish.jersey.internal.guava;

import java.util.AbstractCollection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.function.Supplier;
import java.util.Collection;
import java.util.Map;

public final class Multimaps
{
    private Multimaps() {
    }
    
    public static <K, V> ListMultimap<K, V> newListMultimap(final Map<K, Collection<V>> map, final Supplier<? extends List<V>> factory) {
        return new CustomListMultimap<K, V>(map, factory);
    }
    
    static boolean equalsImpl(final Multimap<?, ?> multimap, final Object object) {
        if (object == multimap) {
            return true;
        }
        if (object instanceof Multimap) {
            final Multimap<?, ?> that = (Multimap<?, ?>)object;
            return multimap.asMap().equals(that.asMap());
        }
        return false;
    }
    
    private static class CustomListMultimap<K, V> extends AbstractListMultimap<K, V>
    {
        private static final long serialVersionUID = 0L;
        transient Supplier<? extends List<V>> factory;
        
        CustomListMultimap(final Map<K, Collection<V>> map, final Supplier<? extends List<V>> factory) {
            super(map);
            this.factory = Preconditions.checkNotNull(factory);
        }
        
        protected List<V> createCollection() {
            return (List)this.factory.get();
        }
        
        private void writeObject(final ObjectOutputStream stream) throws IOException {
            stream.defaultWriteObject();
            stream.writeObject(this.factory);
            stream.writeObject(this.backingMap());
        }
        
        private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            this.factory = (Supplier)stream.readObject();
            final Map<K, Collection<V>> map = (Map<K, Collection<V>>)stream.readObject();
            this.setMap(map);
        }
    }
    
    abstract static class Entries<K, V> extends AbstractCollection<Map.Entry<K, V>>
    {
        abstract Multimap<K, V> multimap();
        
        @Override
        public int size() {
            return this.multimap().size();
        }
        
        @Override
        public boolean contains(final Object o) {
            if (o instanceof Map.Entry) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
                return this.multimap().containsEntry(entry.getKey(), entry.getValue());
            }
            return false;
        }
        
        @Override
        public boolean remove(final Object o) {
            if (o instanceof Map.Entry) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
                return this.multimap().remove(entry.getKey(), entry.getValue());
            }
            return false;
        }
        
        @Override
        public void clear() {
            this.multimap().clear();
        }
    }
}
