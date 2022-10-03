package com.sun.org.apache.xml.internal.dtm.ref;

public final class ExtendedType
{
    private int nodetype;
    private String namespace;
    private String localName;
    private int hash;
    
    public ExtendedType(final int nodetype, final String namespace, final String localName) {
        this.nodetype = nodetype;
        this.namespace = namespace;
        this.localName = localName;
        this.hash = nodetype + namespace.hashCode() + localName.hashCode();
    }
    
    public ExtendedType(final int nodetype, final String namespace, final String localName, final int hash) {
        this.nodetype = nodetype;
        this.namespace = namespace;
        this.localName = localName;
        this.hash = hash;
    }
    
    protected void redefine(final int nodetype, final String namespace, final String localName) {
        this.nodetype = nodetype;
        this.namespace = namespace;
        this.localName = localName;
        this.hash = nodetype + namespace.hashCode() + localName.hashCode();
    }
    
    protected void redefine(final int nodetype, final String namespace, final String localName, final int hash) {
        this.nodetype = nodetype;
        this.namespace = namespace;
        this.localName = localName;
        this.hash = hash;
    }
    
    @Override
    public int hashCode() {
        return this.hash;
    }
    
    public boolean equals(final ExtendedType other) {
        try {
            return other.nodetype == this.nodetype && other.localName.equals(this.localName) && other.namespace.equals(this.namespace);
        }
        catch (final NullPointerException e) {
            return false;
        }
    }
    
    public int getNodeType() {
        return this.nodetype;
    }
    
    public String getLocalName() {
        return this.localName;
    }
    
    public String getNamespace() {
        return this.namespace;
    }
}
