package com.sun.org.apache.xerces.internal.dom;

public final class DeferredAttrNSImpl extends AttrNSImpl implements DeferredNode
{
    static final long serialVersionUID = 6074924934945957154L;
    protected transient int fNodeIndex;
    
    DeferredAttrNSImpl(final DeferredDocumentImpl ownerDocument, final int nodeIndex) {
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
        final DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument();
        this.name = ownerDocument.getNodeName(this.fNodeIndex);
        final int index = this.name.indexOf(58);
        if (index < 0) {
            this.localName = this.name;
        }
        else {
            this.localName = this.name.substring(index + 1);
        }
        final int extra = ownerDocument.getNodeExtra(this.fNodeIndex);
        this.isSpecified((extra & 0x20) != 0x0);
        this.isIdAttribute((extra & 0x200) != 0x0);
        this.namespaceURI = ownerDocument.getNodeURI(this.fNodeIndex);
        final int extraNode = ownerDocument.getLastChild(this.fNodeIndex);
        this.type = ownerDocument.getTypeInfo(extraNode);
    }
    
    @Override
    protected void synchronizeChildren() {
        final DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument();
        ownerDocument.synchronizeChildren(this, this.fNodeIndex);
    }
}
