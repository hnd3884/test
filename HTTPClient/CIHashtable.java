package HTTPClient;

import java.util.Enumeration;
import java.util.Hashtable;

class CIHashtable extends Hashtable
{
    public CIHashtable(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    public CIHashtable(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public CIHashtable() {
    }
    
    public Object get(final String key) {
        return super.get(new CIString(key));
    }
    
    public Object put(final String key, final Object value) {
        return super.put(new CIString(key), value);
    }
    
    public boolean containsKey(final String key) {
        return super.containsKey(new CIString(key));
    }
    
    public Object remove(final String key) {
        return super.remove(new CIString(key));
    }
    
    public Enumeration keys() {
        return new CIHashtableEnumeration(super.keys());
    }
}
