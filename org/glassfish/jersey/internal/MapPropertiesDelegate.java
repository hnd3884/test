package org.glassfish.jersey.internal;

import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public final class MapPropertiesDelegate implements PropertiesDelegate
{
    private final Map<String, Object> store;
    
    public MapPropertiesDelegate() {
        this.store = new HashMap<String, Object>();
    }
    
    public MapPropertiesDelegate(final Map<String, Object> store) {
        this.store = store;
    }
    
    public MapPropertiesDelegate(final PropertiesDelegate that) {
        if (that instanceof MapPropertiesDelegate) {
            this.store = new HashMap<String, Object>(((MapPropertiesDelegate)that).store);
        }
        else {
            this.store = new HashMap<String, Object>();
            for (final String name : that.getPropertyNames()) {
                this.store.put(name, that.getProperty(name));
            }
        }
    }
    
    @Override
    public Object getProperty(final String name) {
        return this.store.get(name);
    }
    
    @Override
    public Collection<String> getPropertyNames() {
        return Collections.unmodifiableCollection((Collection<? extends String>)this.store.keySet());
    }
    
    @Override
    public void setProperty(final String name, final Object value) {
        this.store.put(name, value);
    }
    
    @Override
    public void removeProperty(final String name) {
        this.store.remove(name);
    }
}
