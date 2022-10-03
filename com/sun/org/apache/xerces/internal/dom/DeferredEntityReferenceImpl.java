package com.sun.org.apache.xerces.internal.dom;

public class DeferredEntityReferenceImpl extends EntityReferenceImpl implements DeferredNode
{
    static final long serialVersionUID = 390319091370032223L;
    protected transient int fNodeIndex;
    
    DeferredEntityReferenceImpl(final DeferredDocumentImpl ownerDocument, final int nodeIndex) {
        super(ownerDocument, null);
        this.fNodeIndex = nodeIndex;
        this.needsSyncData(true);
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
        this.baseURI = ownerDocument.getNodeValue(this.fNodeIndex);
    }
    
    @Override
    protected void synchronizeChildren() {
        this.needsSyncChildren(false);
        this.isReadOnly(false);
        final DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument();
        ownerDocument.synchronizeChildren(this, this.fNodeIndex);
        this.setReadOnly(true, true);
    }
}
