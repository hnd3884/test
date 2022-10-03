package com.sun.xml.internal.ws.api.message;

import java.util.List;
import com.sun.xml.internal.ws.api.WSBinding;
import java.util.Set;
import java.util.Iterator;
import javax.xml.namespace.QName;

public interface MessageHeaders
{
    void understood(final Header p0);
    
    void understood(final QName p0);
    
    void understood(final String p0, final String p1);
    
    Header get(final String p0, final String p1, final boolean p2);
    
    Header get(final QName p0, final boolean p1);
    
    Iterator<Header> getHeaders(final String p0, final String p1, final boolean p2);
    
    Iterator<Header> getHeaders(final String p0, final boolean p1);
    
    Iterator<Header> getHeaders(final QName p0, final boolean p1);
    
    Iterator<Header> getHeaders();
    
    boolean hasHeaders();
    
    boolean add(final Header p0);
    
    Header remove(final QName p0);
    
    Header remove(final String p0, final String p1);
    
    void replace(final Header p0, final Header p1);
    
    boolean addOrReplace(final Header p0);
    
    Set<QName> getUnderstoodHeaders();
    
    Set<QName> getNotUnderstoodHeaders(final Set<String> p0, final Set<QName> p1, final WSBinding p2);
    
    boolean isUnderstood(final Header p0);
    
    boolean isUnderstood(final QName p0);
    
    boolean isUnderstood(final String p0, final String p1);
    
    List<Header> asList();
}
