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
import javax.portlet.PortletSession;
import java.util.Map;

final class PortletSessionScopeMap implements Map
{
    private PortletSession session;
    private PortletRequest request;
    
    public PortletSessionScopeMap(final PortletRequest request) {
        this.session = null;
        this.request = null;
        this.request = request;
        this.sessionExists();
    }
    
    public void clear() {
        if (this.sessionExists()) {
            final Iterator keys = this.keySet().iterator();
            while (keys.hasNext()) {
                this.session.removeAttribute((String)keys.next());
            }
        }
    }
    
    public boolean containsKey(final Object key) {
        return this.sessionExists() && this.session.getAttribute(this.key(key)) != null;
    }
    
    public boolean containsValue(final Object value) {
        if (value == null || !this.sessionExists()) {
            return false;
        }
        final Enumeration keys = this.session.getAttributeNames(2);
        while (keys.hasMoreElements()) {
            final Object next = this.session.getAttribute((String)keys.nextElement());
            if (value.equals(next)) {
                return true;
            }
        }
        return false;
    }
    
    public Set entrySet() {
        final Set set = new HashSet();
        if (this.sessionExists()) {
            final Enumeration keys = this.session.getAttributeNames(2);
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                set.add(new MapEntry(key, this.session.getAttribute(key), true));
            }
        }
        return set;
    }
    
    public boolean equals(final Object o) {
        return this.sessionExists() && this.session.equals(o);
    }
    
    public Object get(final Object key) {
        if (this.sessionExists()) {
            return this.session.getAttribute(this.key(key));
        }
        return null;
    }
    
    public int hashCode() {
        if (this.sessionExists()) {
            return this.session.hashCode();
        }
        return 0;
    }
    
    public boolean isEmpty() {
        return !this.sessionExists() || !this.session.getAttributeNames().hasMoreElements();
    }
    
    public Set keySet() {
        final Set set = new HashSet();
        if (this.sessionExists()) {
            final Enumeration keys = this.session.getAttributeNames(2);
            while (keys.hasMoreElements()) {
                set.add(keys.nextElement());
            }
        }
        return set;
    }
    
    public Object put(final Object key, final Object value) {
        if (value == null) {
            return this.remove(key);
        }
        if (this.session == null) {
            this.session = this.request.getPortletSession();
            this.request = null;
        }
        final String skey = this.key(key);
        final Object previous = this.session.getAttribute(skey);
        this.session.setAttribute(skey, value);
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
        if (this.sessionExists()) {
            final String skey = this.key(key);
            final Object previous = this.session.getAttribute(skey);
            this.session.removeAttribute(skey);
            return previous;
        }
        return null;
    }
    
    public int size() {
        int n = 0;
        if (this.sessionExists()) {
            final Enumeration keys = this.session.getAttributeNames(2);
            while (keys.hasMoreElements()) {
                keys.nextElement();
                ++n;
            }
        }
        return n;
    }
    
    public Collection values() {
        final List list = new ArrayList();
        if (this.sessionExists()) {
            final Enumeration keys = this.session.getAttributeNames(2);
            while (keys.hasMoreElements()) {
                list.add(this.session.getAttribute((String)keys.nextElement()));
            }
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
    
    private boolean sessionExists() {
        if (this.session == null) {
            this.session = this.request.getPortletSession(false);
            if (this.session != null) {
                this.request = null;
            }
        }
        return this.session != null;
    }
}
