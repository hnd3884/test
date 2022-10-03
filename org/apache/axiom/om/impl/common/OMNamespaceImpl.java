package org.apache.axiom.om.impl.common;

import org.apache.axiom.om.OMNamespace;

public class OMNamespaceImpl implements OMNamespace
{
    private final String prefix;
    private final String uri;
    
    public OMNamespaceImpl(final String uri, final String prefix) {
        if (uri == null) {
            throw new IllegalArgumentException("Namespace URI may not be null");
        }
        this.uri = uri;
        this.prefix = prefix;
    }
    
    public boolean equals(final String uri, final String prefix) {
        if (this.uri.equals(uri)) {
            if (this.prefix == null) {
                if (prefix != null) {
                    return false;
                }
            }
            else if (!this.prefix.equals(prefix)) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof OMNamespace)) {
            return false;
        }
        final OMNamespace other = (OMNamespace)obj;
        final String otherPrefix = other.getPrefix();
        if (this.uri.equals(other.getNamespaceURI())) {
            if (this.prefix == null) {
                if (otherPrefix != null) {
                    return false;
                }
            }
            else if (!this.prefix.equals(otherPrefix)) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public String getName() {
        return this.uri;
    }
    
    public String getNamespaceURI() {
        return this.uri;
    }
    
    @Override
    public int hashCode() {
        return this.uri.hashCode() ^ ((this.prefix != null) ? this.prefix.hashCode() : 0);
    }
}
