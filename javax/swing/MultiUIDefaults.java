package javax.swing;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Enumeration;
import java.util.Locale;

class MultiUIDefaults extends UIDefaults
{
    private UIDefaults[] tables;
    
    public MultiUIDefaults(final UIDefaults[] tables) {
        this.tables = tables;
    }
    
    public MultiUIDefaults() {
        this.tables = new UIDefaults[0];
    }
    
    @Override
    public Object get(final Object o) {
        final Object value = super.get(o);
        if (value != null) {
            return value;
        }
        for (final UIDefaults uiDefaults : this.tables) {
            final Object o2 = (uiDefaults != null) ? uiDefaults.get(o) : null;
            if (o2 != null) {
                return o2;
            }
        }
        return null;
    }
    
    @Override
    public Object get(final Object o, final Locale locale) {
        final Object value = super.get(o, locale);
        if (value != null) {
            return value;
        }
        for (final UIDefaults uiDefaults : this.tables) {
            final Object o2 = (uiDefaults != null) ? uiDefaults.get(o, locale) : null;
            if (o2 != null) {
                return o2;
            }
        }
        return null;
    }
    
    @Override
    public int size() {
        return this.entrySet().size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public Enumeration<Object> keys() {
        return new MultiUIDefaultsEnumerator(MultiUIDefaultsEnumerator.Type.KEYS, this.entrySet());
    }
    
    @Override
    public Enumeration<Object> elements() {
        return new MultiUIDefaultsEnumerator(MultiUIDefaultsEnumerator.Type.ELEMENTS, this.entrySet());
    }
    
    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        final HashSet set = new HashSet();
        for (int i = this.tables.length - 1; i >= 0; --i) {
            if (this.tables[i] != null) {
                set.addAll(this.tables[i].entrySet());
            }
        }
        set.addAll(super.entrySet());
        return set;
    }
    
    @Override
    protected void getUIError(final String s) {
        if (this.tables.length > 0) {
            this.tables[0].getUIError(s);
        }
        else {
            super.getUIError(s);
        }
    }
    
    @Override
    public Object remove(final Object o) {
        Object o2 = null;
        for (int i = this.tables.length - 1; i >= 0; --i) {
            if (this.tables[i] != null) {
                final Object remove = ((Hashtable<K, Object>)this.tables[i]).remove(o);
                if (remove != null) {
                    o2 = remove;
                }
            }
        }
        final Object remove2 = super.remove(o);
        if (remove2 != null) {
            o2 = remove2;
        }
        return o2;
    }
    
    @Override
    public void clear() {
        super.clear();
        for (final UIDefaults uiDefaults : this.tables) {
            if (uiDefaults != null) {
                uiDefaults.clear();
            }
        }
    }
    
    @Override
    public synchronized String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("{");
        final Enumeration<Object> keys = this.keys();
        while (keys.hasMoreElements()) {
            final Object nextElement = keys.nextElement();
            sb.append(nextElement + "=" + this.get(nextElement) + ", ");
        }
        final int length = sb.length();
        if (length > 1) {
            sb.delete(length - 2, length);
        }
        sb.append("}");
        return sb.toString();
    }
    
    private static class MultiUIDefaultsEnumerator implements Enumeration<Object>
    {
        private Iterator<Map.Entry<Object, Object>> iterator;
        private Type type;
        
        MultiUIDefaultsEnumerator(final Type type, final Set<Map.Entry<Object, Object>> set) {
            this.type = type;
            this.iterator = set.iterator();
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.iterator.hasNext();
        }
        
        @Override
        public Object nextElement() {
            switch (this.type) {
                case KEYS: {
                    return this.iterator.next().getKey();
                }
                case ELEMENTS: {
                    return this.iterator.next().getValue();
                }
                default: {
                    return null;
                }
            }
        }
        
        public enum Type
        {
            KEYS, 
            ELEMENTS;
        }
    }
}
