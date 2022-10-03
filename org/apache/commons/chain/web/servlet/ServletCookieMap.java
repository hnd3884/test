package org.apache.commons.chain.web.servlet;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.chain.web.MapEntry;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

final class ServletCookieMap implements Map
{
    private HttpServletRequest request;
    
    public ServletCookieMap(final HttpServletRequest request) {
        this.request = null;
        this.request = request;
    }
    
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    public boolean containsKey(final Object key) {
        return this.get(key) != null;
    }
    
    public boolean containsValue(final Object value) {
        final Cookie[] cookies = this.request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                if (cookies[i].equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Set entrySet() {
        final Set set = new HashSet();
        final Cookie[] cookies = this.request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                set.add(new MapEntry(cookies[i].getName(), cookies[i], false));
            }
        }
        return set;
    }
    
    public boolean equals(final Object o) {
        return this.request.equals(o);
    }
    
    public Object get(final Object key) {
        final Cookie[] cookies = this.request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                if (cookies[i].getName().equals(this.key(key))) {
                    return cookies[i];
                }
            }
        }
        return null;
    }
    
    public int hashCode() {
        return this.request.hashCode();
    }
    
    public boolean isEmpty() {
        return this.size() < 1;
    }
    
    public Set keySet() {
        final Set set = new HashSet();
        final Cookie[] cookies = this.request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                set.add(cookies[i].getName());
            }
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
        final Cookie[] cookies = this.request.getCookies();
        return (cookies == null) ? 0 : cookies.length;
    }
    
    public Collection values() {
        final List list = new ArrayList(this.size());
        final Cookie[] cookies = this.request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                list.add(cookies[i]);
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
}
