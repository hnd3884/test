package sun.util.resources;

import java.util.NoSuchElementException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.AbstractSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.ResourceBundle;

public abstract class ParallelListResourceBundle extends ResourceBundle
{
    private volatile ConcurrentMap<String, Object> lookup;
    private volatile Set<String> keyset;
    private final AtomicMarkableReference<Object[][]> parallelContents;
    
    protected ParallelListResourceBundle() {
        this.parallelContents = new AtomicMarkableReference<Object[][]>(null, false);
    }
    
    protected abstract Object[][] getContents();
    
    ResourceBundle getParent() {
        return this.parent;
    }
    
    public void setParallelContents(final OpenListResourceBundle openListResourceBundle) {
        if (openListResourceBundle == null) {
            this.parallelContents.compareAndSet(null, null, false, true);
        }
        else {
            this.parallelContents.compareAndSet(null, openListResourceBundle.getContents(), false, false);
        }
    }
    
    boolean areParallelContentsComplete() {
        if (this.parallelContents.isMarked()) {
            return true;
        }
        final boolean[] array = { false };
        return this.parallelContents.get(array) != null || array[0];
    }
    
    @Override
    protected Object handleGetObject(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        this.loadLookupTablesIfNecessary();
        return this.lookup.get(s);
    }
    
    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(this.keySet());
    }
    
    @Override
    public boolean containsKey(final String s) {
        return this.keySet().contains(s);
    }
    
    @Override
    protected Set<String> handleKeySet() {
        this.loadLookupTablesIfNecessary();
        return this.lookup.keySet();
    }
    
    @Override
    public Set<String> keySet() {
        Set<String> keyset;
        while ((keyset = this.keyset) == null) {
            final KeySet keyset2 = new KeySet((Set)this.handleKeySet(), this.parent);
            synchronized (this) {
                if (this.keyset != null) {
                    continue;
                }
                this.keyset = keyset2;
            }
        }
        return keyset;
    }
    
    synchronized void resetKeySet() {
        this.keyset = null;
    }
    
    void loadLookupTablesIfNecessary() {
        ConcurrentMap<String, Object> lookup = this.lookup;
        if (lookup == null) {
            lookup = new ConcurrentHashMap<String, Object>();
            for (final Object[] array : this.getContents()) {
                lookup.put((String)array[0], array[1]);
            }
        }
        final Object[][] array2 = this.parallelContents.getReference();
        if (array2 != null) {
            for (final Object[] array4 : array2) {
                lookup.putIfAbsent((String)array4[0], array4[1]);
            }
            this.parallelContents.set(null, true);
        }
        if (this.lookup == null) {
            synchronized (this) {
                if (this.lookup == null) {
                    this.lookup = lookup;
                }
            }
        }
    }
    
    private static class KeySet extends AbstractSet<String>
    {
        private final Set<String> set;
        private final ResourceBundle parent;
        
        private KeySet(final Set<String> set, final ResourceBundle parent) {
            this.set = set;
            this.parent = parent;
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.set.contains(o) || (this.parent != null && this.parent.containsKey((String)o));
        }
        
        @Override
        public Iterator<String> iterator() {
            if (this.parent == null) {
                return this.set.iterator();
            }
            return new Iterator<String>() {
                private Iterator<String> itr = KeySet.this.set.iterator();
                private boolean usingParent;
                
                @Override
                public boolean hasNext() {
                    if (this.itr.hasNext()) {
                        return true;
                    }
                    if (!this.usingParent) {
                        final HashSet set = new HashSet((Collection<? extends E>)KeySet.this.parent.keySet());
                        set.removeAll(KeySet.this.set);
                        this.itr = set.iterator();
                        this.usingParent = true;
                    }
                    return this.itr.hasNext();
                }
                
                @Override
                public String next() {
                    if (this.hasNext()) {
                        return this.itr.next();
                    }
                    throw new NoSuchElementException();
                }
                
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        
        @Override
        public int size() {
            if (this.parent == null) {
                return this.set.size();
            }
            final HashSet set = new HashSet((Collection<? extends E>)this.set);
            set.addAll(this.parent.keySet());
            return set.size();
        }
    }
}
