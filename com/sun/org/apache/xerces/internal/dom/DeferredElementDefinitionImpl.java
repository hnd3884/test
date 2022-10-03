package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;

public class DeferredElementDefinitionImpl extends ElementDefinitionImpl implements DeferredNode
{
    static final long serialVersionUID = 6703238199538041591L;
    protected transient int fNodeIndex;
    
    DeferredElementDefinitionImpl(final DeferredDocumentImpl ownerDocument, final int nodeIndex) {
        super(ownerDocument, null);
        this.fNodeIndex = nodeIndex;
        this.needsSyncData(true);
        this.needsSyncChildren(true);
    }
    
    @Override
    public int getNodeIndex() {
        return this.fNodeIndex;
    }
    
    @Override
    protected void synchronizeData() {
        this.needsSyncData(false);
        final DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument;
        this.name = ownerDocument.getNodeName(this.fNodeIndex);
    }
    
    @Override
    protected void synchronizeChildren() {
        final boolean orig = this.ownerDocument.getMutationEvents();
        this.ownerDocument.setMutationEvents(false);
        this.needsSyncChildren(false);
        final DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument;
        this.attributes = new NamedNodeMapImpl(ownerDocument);
        for (int nodeIndex = ownerDocument.getLastChild(this.fNodeIndex); nodeIndex != -1; nodeIndex = ownerDocument.getPrevSibling(nodeIndex)) {
            final Node attr = ownerDocument.getNodeObject(nodeIndex);
            this.attributes.setNamedItem(attr);
        }
        ownerDocument.setMutationEvents(orig);
    }
}
