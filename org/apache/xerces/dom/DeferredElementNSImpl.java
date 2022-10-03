package org.apache.xerces.dom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xs.XSTypeDefinition;

public class DeferredElementNSImpl extends ElementNSImpl implements DeferredNode
{
    static final long serialVersionUID = -5001885145370927385L;
    protected transient int fNodeIndex;
    
    DeferredElementNSImpl(final DeferredDocumentImpl deferredDocumentImpl, final int fNodeIndex) {
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
        final int index = this.name.indexOf(58);
        if (index < 0) {
            this.localName = this.name;
        }
        else {
            this.localName = this.name.substring(index + 1);
        }
        this.namespaceURI = deferredDocumentImpl.getNodeURI(this.fNodeIndex);
        this.type = (XSTypeDefinition)deferredDocumentImpl.getTypeInfo(this.fNodeIndex);
        this.setupDefaultAttributes();
        int i = deferredDocumentImpl.getNodeExtra(this.fNodeIndex);
        if (i != -1) {
            final NamedNodeMap attributes = this.getAttributes();
            int n = 0;
            do {
                final AttrImpl attrImpl = (AttrImpl)deferredDocumentImpl.getNodeObject(i);
                if (!attrImpl.getSpecified() && (n != 0 || (attrImpl.getNamespaceURI() != null && attrImpl.getNamespaceURI() != NamespaceContext.XMLNS_URI && attrImpl.getName().indexOf(58) < 0))) {
                    n = 1;
                    attributes.setNamedItemNS(attrImpl);
                }
                else {
                    attributes.setNamedItem(attrImpl);
                }
                i = deferredDocumentImpl.getPrevSibling(i);
            } while (i != -1);
        }
        deferredDocumentImpl.mutationEvents = mutationEvents;
    }
    
    protected final void synchronizeChildren() {
        ((DeferredDocumentImpl)this.ownerDocument()).synchronizeChildren(this, this.fNodeIndex);
    }
}
