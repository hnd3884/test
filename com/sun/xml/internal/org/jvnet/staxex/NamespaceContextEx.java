package com.sun.xml.internal.org.jvnet.staxex;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public interface NamespaceContextEx extends NamespaceContext, Iterable<Binding>
{
    Iterator<Binding> iterator();
    
    public interface Binding
    {
        String getPrefix();
        
        String getNamespaceURI();
    }
}
