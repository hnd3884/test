package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;

public class DeferredDocumentTypeImpl extends DocumentTypeImpl implements DeferredNode
{
    static final long serialVersionUID = -2172579663227313509L;
    protected transient int fNodeIndex;
    
    DeferredDocumentTypeImpl(final DeferredDocumentImpl ownerDocument, final int nodeIndex) {
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
        this.publicID = ownerDocument.getNodeValue(this.fNodeIndex);
        this.systemID = ownerDocument.getNodeURI(this.fNodeIndex);
        final int extraDataIndex = ownerDocument.getNodeExtra(this.fNodeIndex);
        this.internalSubset = ownerDocument.getNodeValue(extraDataIndex);
    }
    
    @Override
    protected void synchronizeChildren() {
        final boolean orig = this.ownerDocument().getMutationEvents();
        this.ownerDocument().setMutationEvents(false);
        this.needsSyncChildren(false);
        final DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument;
        this.entities = new NamedNodeMapImpl(this);
        this.notations = new NamedNodeMapImpl(this);
        this.elements = new NamedNodeMapImpl(this);
        DeferredNode last = null;
        for (int index = ownerDocument.getLastChild(this.fNodeIndex); index != -1; index = ownerDocument.getPrevSibling(index)) {
            final DeferredNode node = ownerDocument.getNodeObject(index);
            final int type = node.getNodeType();
            switch (type) {
                case 6: {
                    this.entities.setNamedItem(node);
                    continue;
                }
                case 12: {
                    this.notations.setNamedItem(node);
                    continue;
                }
                case 21: {
                    this.elements.setNamedItem(node);
                    continue;
                }
                case 1: {
                    if (((DocumentImpl)this.getOwnerDocument()).allowGrammarAccess) {
                        this.insertBefore(node, last);
                        last = node;
                        continue;
                    }
                    break;
                }
            }
            System.out.println("DeferredDocumentTypeImpl#synchronizeInfo: node.getNodeType() = " + node.getNodeType() + ", class = " + node.getClass().getName());
        }
        this.ownerDocument().setMutationEvents(orig);
        this.setReadOnly(true, false);
    }
}
