package org.glassfish.jersey.internal.util.collection;

import java.lang.reflect.Constructor;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MultivaluedHashMap;

public class MultivaluedStringMap extends MultivaluedHashMap<String, String>
{
    static final long serialVersionUID = -6052320403766368902L;
    
    public MultivaluedStringMap(final MultivaluedMap<? extends String, ? extends String> map) {
        super((MultivaluedMap)map);
    }
    
    public MultivaluedStringMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    public MultivaluedStringMap(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public MultivaluedStringMap() {
    }
    
    protected void addFirstNull(final List<String> values) {
        values.add("");
    }
    
    protected void addNull(final List<String> values) {
        values.add(0, "");
    }
    
    public final <A> A getFirst(final String key, final Class<A> type) {
        final String value = (String)this.getFirst((Object)key);
        if (value == null) {
            return null;
        }
        Constructor<A> c = null;
        try {
            c = type.getConstructor(String.class);
        }
        catch (final Exception ex) {
            throw new IllegalArgumentException(type.getName() + " has no String constructor", ex);
        }
        A retVal = null;
        try {
            retVal = c.newInstance(value);
        }
        catch (final Exception ex2) {}
        return retVal;
    }
    
    public final <A> A getFirst(final String key, final A defaultValue) {
        final String value = (String)this.getFirst((Object)key);
        if (value == null) {
            return defaultValue;
        }
        final Class<A> type = (Class<A>)defaultValue.getClass();
        Constructor<A> c = null;
        try {
            c = type.getConstructor(String.class);
        }
        catch (final Exception ex) {
            throw new IllegalArgumentException(type.getName() + " has no String constructor", ex);
        }
        A retVal = defaultValue;
        try {
            retVal = c.newInstance(value);
        }
        catch (final Exception ex2) {}
        return retVal;
    }
}
