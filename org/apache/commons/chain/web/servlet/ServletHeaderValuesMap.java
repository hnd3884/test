package org.apache.commons.chain.web.servlet;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import org.apache.commons.chain.web.MapEntry;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

final class ServletHeaderValuesMap implements Map
{
    private HttpServletRequest request;
    
    public ServletHeaderValuesMap(final HttpServletRequest request) {
        this.request = null;
        this.request = request;
    }
    
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    public boolean containsKey(final Object key) {
        return this.request.getHeader(this.key(key)) != null;
    }
    
    public boolean containsValue(final Object value) {
        if (!(value instanceof String[])) {
            return false;
        }
        final String[] test = (String[])value;
        final Iterator values = this.values().iterator();
        while (values.hasNext()) {
            final String[] actual = values.next();
            if (test.length == actual.length) {
                boolean matched = true;
                for (int i = 0; i < test.length; ++i) {
                    if (!test[i].equals(actual[i])) {
                        matched = false;
                        break;
                    }
                }
                if (matched) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public Set entrySet() {
        final Set set = new HashSet();
        final Enumeration keys = this.request.getHeaderNames();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            set.add(new MapEntry(key, this.request.getHeaders(key), false));
        }
        return set;
    }
    
    public boolean equals(final Object o) {
        return this.request.equals(o);
    }
    
    public Object get(final Object key) {
        final List list = new ArrayList();
        final Enumeration values = this.request.getHeaders(this.key(key));
        while (values.hasMoreElements()) {
            list.add(values.nextElement());
        }
        return list.toArray(new String[list.size()]);
    }
    
    public int hashCode() {
        return this.request.hashCode();
    }
    
    public boolean isEmpty() {
        return this.size() < 1;
    }
    
    public Set keySet() {
        final Set set = new HashSet();
        final Enumeration keys = this.request.getHeaderNames();
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
        final Enumeration keys = this.request.getHeaderNames();
        while (keys.hasMoreElements()) {
            keys.nextElement();
            ++n;
        }
        return n;
    }
    
    public Collection values() {
        final List list = new ArrayList();
        final Enumeration keys = this.request.getHeaderNames();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            final List list2 = new ArrayList();
            final Enumeration values = this.request.getHeaders(key);
            while (values.hasMoreElements()) {
                list2.add(values.nextElement());
            }
            list.add(list2.toArray(new String[list2.size()]));
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
