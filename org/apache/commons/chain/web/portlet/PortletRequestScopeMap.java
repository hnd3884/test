package org.apache.commons.chain.web.portlet;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.chain.web.MapEntry;
import java.util.HashSet;
import java.util.Set;
import java.util.Enumeration;
import java.util.Iterator;
import javax.portlet.PortletRequest;
import java.util.Map;

final class PortletRequestScopeMap implements Map
{
    private PortletRequest request;
    
    public PortletRequestScopeMap(final PortletRequest request) {
        this.request = null;
        this.request = request;
    }
    
    public void clear() {
        final Iterator keys = this.keySet().iterator();
        while (keys.hasNext()) {
            this.request.removeAttribute((String)keys.next());
        }
    }
    
    public boolean containsKey(final Object key) {
        return this.request.getAttribute(this.key(key)) != null;
    }
    
    public boolean containsValue(final Object value) {
        if (value == null) {
            return false;
        }
        final Enumeration keys = this.request.getAttributeNames();
        while (keys.hasMoreElements()) {
            final Object next = this.request.getAttribute((String)keys.nextElement());
            if (value.equals(next)) {
                return true;
            }
        }
        return false;
    }
    
    public Set entrySet() {
        final Set set = new HashSet();
        final Enumeration keys = this.request.getAttributeNames();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            set.add(new MapEntry(key, this.request.getAttribute(key), true));
        }
        return set;
    }
    
    public boolean equals(final Object o) {
        return this.request.equals(o);
    }
    
    public Object get(final Object key) {
        return this.request.getAttribute(this.key(key));
    }
    
    public int hashCode() {
        return this.request.hashCode();
    }
    
    public boolean isEmpty() {
        return this.size() < 1;
    }
    
    public Set keySet() {
        final Set set = new HashSet();
        final Enumeration keys = this.request.getAttributeNames();
        while (keys.hasMoreElements()) {
            set.add(keys.nextElement());
        }
        return set;
    }
    
    public Object put(final Object key, final Object value) {
        if (value == null) {
            return this.remove(key);
        }
        final String skey = this.key(key);
        final Object previous = this.request.getAttribute(skey);
        this.request.setAttribute(skey, value);
        return previous;
    }
    
    public void putAll(final Map map) {
        final Iterator entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            final Entry entry = entries.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    public Object remove(final Object key) {
        final String skey = this.key(key);
        final Object previous = this.request.getAttribute(skey);
        this.request.removeAttribute(skey);
        return previous;
    }
    
    public int size() {
        int n = 0;
        final Enumeration keys = this.request.getAttributeNames();
        while (keys.hasMoreElements()) {
            keys.nextElement();
            ++n;
        }
        return n;
    }
    
    public Collection values() {
        final List list = new ArrayList();
        final Enumeration keys = this.request.getAttributeNames();
        while (keys.hasMoreElements()) {
            list.add(this.request.getAttribute((String)keys.nextElement()));
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
