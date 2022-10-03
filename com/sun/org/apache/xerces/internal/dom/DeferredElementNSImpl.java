package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public class DeferredElementNSImpl extends ElementNSImpl implements DeferredNode
{
    static final long serialVersionUID = -5001885145370927385L;
    protected transient int fNodeIndex;
    
    DeferredElementNSImpl(final DeferredDocumentImpl ownerDoc, final int nodeIndex) {
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
        final int index = this.name.indexOf(58);
        if (index < 0) {
            this.localName = this.name;
        }
        else {
            this.localName = this.name.substring(index + 1);
        }
        this.namespaceURI = ownerDocument.getNodeURI(this.fNodeIndex);
        this.type = (XSTypeDefinition)ownerDocument.getTypeInfo(this.fNodeIndex);
        this.setupDefaultAttributes();
        int attrIndex = ownerDocument.getNodeExtra(this.fNodeIndex);
        if (attrIndex != -1) {
            final NamedNodeMap attrs = this.getAttributes();
            boolean seenSchemaDefault = false;
            do {
                final AttrImpl attr = (AttrImpl)ownerDocument.getNodeObject(attrIndex);
                if (!attr.getSpecified() && (seenSchemaDefault || (attr.getNamespaceURI() != null && attr.getNamespaceURI() != NamespaceContext.XMLNS_URI && attr.getName().indexOf(58) < 0))) {
                    seenSchemaDefault = true;
                    attrs.setNamedItemNS(attr);
                }
                else {
                    attrs.setNamedItem(attr);
                }
                attrIndex = ownerDocument.getPrevSibling(attrIndex);
            } while (attrIndex != -1);
        }
        ownerDocument.mutationEvents = orig;
    }
    
    @Override
    protected final void synchronizeChildren() {
        final DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument();
        ownerDocument.synchronizeChildren(this, this.fNodeIndex);
    }
}
