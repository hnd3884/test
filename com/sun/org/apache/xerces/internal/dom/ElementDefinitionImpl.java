package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ElementDefinitionImpl extends ParentNode
{
    static final long serialVersionUID = -8373890672670022714L;
    protected String name;
    protected NamedNodeMapImpl attributes;
    
    public ElementDefinitionImpl(final CoreDocumentImpl ownerDocument, final String name) {
        super(ownerDocument);
        this.name = name;
        this.attributes = new NamedNodeMapImpl(ownerDocument);
    }
    
    @Override
    public short getNodeType() {
        return 21;
    }
    
    @Override
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }
    
    @Override
    public Node cloneNode(final boolean deep) {
        final ElementDefinitionImpl newnode = (ElementDefinitionImpl)super.cloneNode(deep);
        newnode.attributes = this.attributes.cloneMap(newnode);
        return newnode;
    }
    
    @Override
    public NamedNodeMap getAttributes() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.attributes;
    }
}
