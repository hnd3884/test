package org.apache.commons.chain.web.servlet;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import org.apache.commons.chain.web.MapEntry;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import javax.servlet.ServletContext;
import java.util.Map;

final class ServletInitParamMap implements Map
{
    private ServletContext context;
    
    public ServletInitParamMap(final ServletContext context) {
        this.context = null;
        this.context = context;
    }
    
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    public boolean containsKey(final Object key) {
        return this.context.getInitParameter(this.key(key)) != null;
    }
    
    public boolean containsValue(final Object value) {
        final Iterator values = this.values().iterator();
        while (values.hasNext()) {
            if (value.equals(values.next())) {
                return true;
            }
        }
        return false;
    }
    
    public Set entrySet() {
        final Set set = new HashSet();
        final Enumeration keys = this.context.getInitParameterNames();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            set.add(new MapEntry(key, this.context.getInitParameter(key), false));
        }
        return set;
    }
    
    public boolean equals(final Object o) {
        return this.context.equals(o);
    }
    
    public Object get(final Object key) {
        return this.context.getInitParameter(this.key(key));
    }
    
    public int hashCode() {
        return this.context.hashCode();
    }
    
    public boolean isEmpty() {
        return this.size() < 1;
    }
    
    public Set keySet() {
        final Set set = new HashSet();
        final Enumeration keys = this.context.getInitParameterNames();
        while (keys.hasMoreElements()) {
            set.add(keys.nextElement());
        }
        return set;
    }
    
    public Object put(final Object key, final Object value) {
        throw new UnsupportedOperationException();
    }
    
    public void putAll(final Map map) {
        throw new UnsupportedOperationException();
    }
    
    public Object remove(final Object key) {
        throw new UnsupportedOperationException();
    }
    
    public int size() {
        int n = 0;
        final Enumeration keys = this.context.getInitParameterNames();
        while (keys.hasMoreElements()) {
            keys.nextElement();
            ++n;
        }
        return n;
    }
    
    public Collection values() {
        final List list = new ArrayList();
        final Enumeration keys = this.context.getInitParameterNames();
        while (keys.hasMoreElements()) {
            list.add(this.context.getInitParameter((String)keys.nextElement()));
        }
        return list;
    }
    
    private String key(final Object key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (key instanceof String) {
            return (String)key;
        }
        return key.toString();
    }
}
