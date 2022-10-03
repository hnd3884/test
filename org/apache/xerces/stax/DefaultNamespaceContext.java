package org.apache.xerces.stax;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public final class DefaultNamespaceContext implements NamespaceContext
{
    private static final DefaultNamespaceContext DEFAULT_NAMESPACE_CONTEXT_INSTANCE;
    
    private DefaultNamespaceContext() {
    }
    
    public static DefaultNamespaceContext getInstance() {
        return DefaultNamespaceContext.DEFAULT_NAMESPACE_CONTEXT_INSTANCE;
    }
    
    public String getNamespaceURI(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("Prefix cannot be null.");
        }
        if ("xml".equals(s)) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if ("xmlns".equals(s)) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return "";
    }
    
    public String getPrefix(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("Namespace URI cannot be null.");
        }
        if ("http://www.w3.org/XML/1998/namespace".equals(s)) {
            return "xml";
        }
        if ("http://www.w3.org/2000/xmlns/".equals(s)) {
            return "xmlns";
        }
        return null;
    }
    
    public Iterator getPrefixes(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("Namespace URI cannot be null.");
        }
        if ("http://www.w3.org/XML/1998/namespace".equals(s)) {
            return new Iterator() {
                boolean more = true;
                
                public boolean hasNext() {
                    return this.more;
                }
                
                public Object next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.more = false;
                    return "xml";
                }
                
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        if ("http://www.w3.org/2000/xmlns/".equals(s)) {
            return new Iterator() {
                boolean more = true;
                
                public boolean hasNext() {
                    return this.more;
                }
                
                public Object next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.more = false;
                    return "xmlns";
                }
                
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return Collections.EMPTY_LIST.iterator();
    }
    
    static {
        DEFAULT_NAMESPACE_CONTEXT_INSTANCE = new DefaultNamespaceContext();
    }
}
