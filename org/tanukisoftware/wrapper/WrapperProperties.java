package org.tanukisoftware.wrapper;

import java.util.Collection;
import java.util.Map;
import java.util.Collections;
import java.util.Set;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class WrapperProperties extends Properties
{
    private static final long serialVersionUID = 1991422118345246456L;
    boolean m_locked;
    
    WrapperProperties() {
        this.m_locked = false;
    }
    
    public void lock() {
        this.m_locked = true;
    }
    
    public void load(final InputStream inStream) throws IOException {
        if (this.m_locked) {
            throw new IllegalStateException(WrapperManager.getRes().getString("Read Only"));
        }
        super.load(inStream);
    }
    
    public Object setProperty(final String key, final String value) {
        if (this.m_locked) {
            throw new IllegalStateException(WrapperManager.getRes().getString("Read Only"));
        }
        return super.setProperty(key, value);
    }
    
    public void clear() {
        if (this.m_locked) {
            throw new IllegalStateException(WrapperManager.getRes().getString("Read Only"));
        }
        super.clear();
    }
    
    public Set entrySet() {
        if (this.m_locked) {
            return Collections.unmodifiableSet((Set<?>)super.entrySet());
        }
        return super.entrySet();
    }
    
    public Set keySet() {
        if (this.m_locked) {
            return Collections.unmodifiableSet(super.keySet());
        }
        return super.keySet();
    }
    
    public Object put(final Object key, final Object value) {
        if (this.m_locked) {
            throw new IllegalStateException(WrapperManager.getRes().getString("Read Only"));
        }
        return super.put(key, value);
    }
    
    public void putAll(final Map map) {
        if (this.m_locked) {
            throw new IllegalStateException(WrapperManager.getRes().getString("Read Only"));
        }
        super.putAll(map);
    }
    
    public Object remove(final Object key) {
        if (this.m_locked) {
            throw new IllegalStateException(WrapperManager.getRes().getString("Read Only"));
        }
        return super.remove(key);
    }
    
    public Collection values() {
        if (this.m_locked) {
            return Collections.unmodifiableCollection(super.values());
        }
        return super.values();
    }
}
