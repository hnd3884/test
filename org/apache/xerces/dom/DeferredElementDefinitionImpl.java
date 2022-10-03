package org.apache.xerces.dom;

import org.w3c.dom.Node;

public class DeferredElementDefinitionImpl extends ElementDefinitionImpl implements DeferredNode
{
    static final long serialVersionUID = 6703238199538041591L;
    protected transient int fNodeIndex;
    
    DeferredElementDefinitionImpl(final DeferredDocumentImpl deferredDocumentImpl, final int fNodeIndex) {
        super(deferredDocumentImpl, null);
        this.fNodeIndex = fNodeIndex;
        this.needsSyncData(true);
        this.needsSyncChildren(true);
    }
    
    public int getNodeIndex() {
        return this.fNodeIndex;
    }
    
    protected void synchronizeData() {
        this.needsSyncData(false);
        this.name = ((DeferredDocumentImpl)this.ownerDocument).getNodeName(this.fNodeIndex);
    }
    
    protected void synchronizeChildren() {
        final boolean mutationEvents = this.ownerDocument.getMutationEvents();
        this.ownerDocument.setMutationEvents(false);
        this.needsSyncChildren(false);
        final DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument;
        this.attributes = new NamedNodeMapImpl(deferredDocumentImpl);
        for (int i = deferredDocumentImpl.getLastChild(this.fNodeIndex); i != -1; i = deferredDocumentImpl.getPrevSibling(i)) {
            this.attributes.setNamedItem(deferredDocumentImpl.getNodeObject(i));
        }
        deferredDocumentImpl.setMutationEvents(mutationEvents);
    }
}
