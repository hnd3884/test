package org.apache.xerces.dom;

public class DeferredEntityReferenceImpl extends EntityReferenceImpl implements DeferredNode
{
    static final long serialVersionUID = 390319091370032223L;
    protected transient int fNodeIndex;
    
    DeferredEntityReferenceImpl(final DeferredDocumentImpl deferredDocumentImpl, final int fNodeIndex) {
        super(deferredDocumentImpl, null);
        this.fNodeIndex = fNodeIndex;
        this.needsSyncData(true);
    }
    
    public int getNodeIndex() {
        return this.fNodeIndex;
    }
    
    protected void synchronizeData() {
        this.needsSyncData(false);
        final DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument;
        this.name = deferredDocumentImpl.getNodeName(this.fNodeIndex);
        this.baseURI = deferredDocumentImpl.getNodeValue(this.fNodeIndex);
    }
    
    protected void synchronizeChildren() {
        this.needsSyncChildren(false);
        this.isReadOnly(false);
        ((DeferredDocumentImpl)this.ownerDocument()).synchronizeChildren(this, this.fNodeIndex);
        this.setReadOnly(true, true);
    }
}
