package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DeferredElementImpl extends ElementImpl implements DeferredNode
{
    static final long serialVersionUID = -7670981133940934842L;
    protected transient int fNodeIndex;
    
    DeferredElementImpl(final DeferredDocumentImpl ownerDoc, final int nodeIndex) {
        super(ownerDoc, null);
        this.fNodeIndex = nodeIndex;
        this.needsSyncChildren(true);
    }
    
    @Override
    public final int getNodeIndex() {
        return this.fNodeIndex;
    }
    
    @Override
    protected final void synchronizeData() {
        this.needsSyncData(false);
        final DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument;
        final boolean orig = ownerDocument.mutationEvents;
        ownerDocument.mutationEvents = false;
        this.name = ownerDocument.getNodeName(this.fNodeIndex);
        this.setupDefaultAttributes();
        int index = ownerDocument.getNodeExtra(this.fNodeIndex);
        if (index != -1) {
            final NamedNodeMap attrs = this.getAttributes();
            do {
                final NodeImpl attr = (NodeImpl)ownerDocument.getNodeObject(index);
                attrs.setNamedItem(attr);
                index = ownerDocument.getPrevSibling(index);
            } while (index != -1);
        }
        ownerDocument.mutationEvents = orig;
    }
    
    @Override
    protected final void synchronizeChildren() {
        final DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument();
        ownerDocument.synchronizeChildren(this, this.fNodeIndex);
    }
}
