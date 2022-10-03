package org.apache.xerces.dom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DeferredElementImpl extends ElementImpl implements DeferredNode
{
    static final long serialVersionUID = -7670981133940934842L;
    protected transient int fNodeIndex;
    
    DeferredElementImpl(final DeferredDocumentImpl deferredDocumentImpl, final int fNodeIndex) {
        super(deferredDocumentImpl, null);
        this.fNodeIndex = fNodeIndex;
        this.needsSyncChildren(true);
    }
    
    public final int getNodeIndex() {
        return this.fNodeIndex;
    }
    
    protected final void synchronizeData() {
        this.needsSyncData(false);
        final DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument;
        final boolean mutationEvents = deferredDocumentImpl.mutationEvents;
        deferredDocumentImpl.mutationEvents = false;
        this.name = deferredDocumentImpl.getNodeName(this.fNodeIndex);
        this.setupDefaultAttributes();
        int i = deferredDocumentImpl.getNodeExtra(this.fNodeIndex);
        if (i != -1) {
            final NamedNodeMap attributes = this.getAttributes();
            do {
                attributes.setNamedItem(deferredDocumentImpl.getNodeObject(i));
                i = deferredDocumentImpl.getPrevSibling(i);
            } while (i != -1);
        }
        deferredDocumentImpl.mutationEvents = mutationEvents;
    }
    
    protected final void synchronizeChildren() {
        ((DeferredDocumentImpl)this.ownerDocument()).synchronizeChildren(this, this.fNodeIndex);
    }
}
