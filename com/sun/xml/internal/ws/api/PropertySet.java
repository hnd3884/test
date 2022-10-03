package com.sun.xml.internal.ws.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import com.oracle.webservices.internal.api.message.BasePropertySet;

public abstract class PropertySet extends BasePropertySet
{
    @Deprecated
    protected static PropertyMap parse(final Class clazz) {
        final BasePropertySet.PropertyMap pm = BasePropertySet.parse(clazz);
        final PropertyMap map = new PropertyMap();
        map.putAll(pm);
        return map;
    }
    
    @Override
    public Object get(final Object key) {
        final Accessor sp = ((HashMap<K, Accessor>)this.getPropertyMap()).get(key);
        if (sp != null) {
            return sp.get(this);
        }
        throw new IllegalArgumentException("Undefined property " + key);
    }
    
    @Override
    public Object put(final String key, final Object value) {
        final Accessor sp = ((HashMap<K, Accessor>)this.getPropertyMap()).get(key);
        if (sp != null) {
            final Object old = sp.get(this);
            sp.set(this, value);
            return old;
        }
        throw new IllegalArgumentException("Undefined property " + key);
    }
    
    @Override
    public boolean supports(final Object key) {
        return this.getPropertyMap().containsKey(key);
    }
    
    @Override
    public Object remove(final Object key) {
        final Accessor sp = ((HashMap<K, Accessor>)this.getPropertyMap()).get(key);
        if (sp != null) {
            final Object old = sp.get(this);
            sp.set(this, null);
            return old;
        }
        throw new IllegalArgumentException("Undefined property " + key);
    }
    
    @Override
    protected void createEntrySet(final Set<Map.Entry<String, Object>> core) {
        for (final Map.Entry<String, Accessor> e : this.getPropertyMap().entrySet()) {
            core.add(new Map.Entry<String, Object>() {
                @Override
                public String getKey() {
                    return e.getKey();
                }
                
                @Override
                public Object getValue() {
                    return e.getValue().get(PropertySet.this);
                }
                
                @Override
                public Object setValue(final Object value) {
                    final Accessor acc = e.getValue();
                    final Object old = acc.get(PropertySet.this);
                    acc.set(PropertySet.this, value);
                    return old;
                }
            });
        }
    }
    
    @Override
    protected abstract PropertyMap getPropertyMap();
    
    protected static class PropertyMap extends BasePropertySet.PropertyMap
    {
    }
}
