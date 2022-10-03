package sun.util.resources;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Collection;
import sun.util.ResourceBundleEnumeration;
import java.util.Enumeration;
import java.util.Set;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class OpenListResourceBundle extends ResourceBundle
{
    private volatile Map<String, Object> lookup;
    private volatile Set<String> keyset;
    
    protected OpenListResourceBundle() {
        this.lookup = null;
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
        final ResourceBundle parent = this.parent;
        return new ResourceBundleEnumeration(this.handleKeySet(), (parent != null) ? parent.getKeys() : null);
    }
    
    @Override
    protected Set<String> handleKeySet() {
        this.loadLookupTablesIfNecessary();
        return this.lookup.keySet();
    }
    
    @Override
    public Set<String> keySet() {
        if (this.keyset != null) {
            return this.keyset;
        }
        final Set<Object> set = (Set<Object>)this.createSet();
        set.addAll(this.handleKeySet());
        if (this.parent != null) {
            set.addAll(this.parent.keySet());
        }
        synchronized (this) {
            if (this.keyset == null) {
                this.keyset = (Set<String>)set;
            }
        }
        return this.keyset;
    }
    
    protected abstract Object[][] getContents();
    
    void loadLookupTablesIfNecessary() {
        if (this.lookup == null) {
            this.loadLookup();
        }
    }
    
    private void loadLookup() {
        final Object[][] contents = this.getContents();
        final Map<Object, Object> map = (Map<Object, Object>)this.createMap(contents.length);
        for (int i = 0; i < contents.length; ++i) {
            final String s = (String)contents[i][0];
            final Object o = contents[i][1];
            if (s == null || o == null) {
                throw new NullPointerException();
            }
            map.put(s, o);
        }
        synchronized (this) {
            if (this.lookup == null) {
                this.lookup = (Map<String, Object>)map;
            }
        }
    }
    
    protected <K, V> Map<K, V> createMap(final int n) {
        return new HashMap<K, V>(n);
    }
    
    protected <E> Set<E> createSet() {
        return new HashSet<E>();
    }
}
