package org.apache.xerces.dom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ElementDefinitionImpl extends ParentNode
{
    static final long serialVersionUID = -8373890672670022714L;
    protected String name;
    protected NamedNodeMapImpl attributes;
    
    public ElementDefinitionImpl(final CoreDocumentImpl coreDocumentImpl, final String name) {
        super(coreDocumentImpl);
        this.name = name;
        this.attributes = new NamedNodeMapImpl(coreDocumentImpl);
    }
    
    public short getNodeType() {
        return 21;
    }
    
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }
    
    public Node cloneNode(final boolean b) {
        final ElementDefinitionImpl elementDefinitionImpl = (ElementDefinitionImpl)super.cloneNode(b);
        elementDefinitionImpl.attributes = this.attributes.cloneMap(elementDefinitionImpl);
        return elementDefinitionImpl;
    }
    
    public NamedNodeMap getAttributes() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.attributes;
    }
}
