package javax.script;

import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class SimpleBindings implements Bindings
{
    private Map<String, Object> map;
    
    public SimpleBindings(final Map<String, Object> map) {
        if (map == null) {
            throw new NullPointerException();
        }
        this.map = map;
    }
    
    public SimpleBindings() {
        this(new HashMap<String, Object>());
    }
    
    @Override
    public Object put(final String s, final Object o) {
        this.checkKey(s);
        return this.map.put(s, o);
    }
    
    @Override
    public void putAll(final Map<? extends String, ?> map) {
        if (map == null) {
            throw new NullPointerException("toMerge map is null");
        }
        for (final Map.Entry entry : map.entrySet()) {
            final String s = (String)entry.getKey();
            this.checkKey(s);
            this.put(s, entry.getValue());
        }
    }
    
    @Override
    public void clear() {
        this.map.clear();
    }
    
    @Override
    public boolean containsKey(final Object o) {
        this.checkKey(o);
        return this.map.containsKey(o);
    }
    
    @Override
    public boolean containsValue(final Object o) {
        return this.map.containsValue(o);
    }
    
    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return this.map.entrySet();
    }
    
    @Override
    public Object get(final Object o) {
        this.checkKey(o);
        return this.map.get(o);
    }
    
    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    @Override
    public Set<String> keySet() {
        return this.map.keySet();
    }
    
    @Override
    public Object remove(final Object o) {
        this.checkKey(o);
        return this.map.remove(o);
    }
    
    @Override
    public int size() {
        return this.map.size();
    }
    
    @Override
    public Collection<Object> values() {
        return this.map.values();
    }
    
    private void checkKey(final Object o) {
        if (o == null) {
            throw new NullPointerException("key can not be null");
        }
        if (!(o instanceof String)) {
            throw new ClassCastException("key should be a String");
        }
        if (o.equals("")) {
            throw new IllegalArgumentException("key can not be empty");
        }
    }
}
