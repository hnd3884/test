package org.apache.xerces.dom;

public class DeferredEntityImpl extends EntityImpl implements DeferredNode
{
    static final long serialVersionUID = 4760180431078941638L;
    protected transient int fNodeIndex;
    
    DeferredEntityImpl(final DeferredDocumentImpl deferredDocumentImpl, final int fNodeIndex) {
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
        final DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument;
        this.name = deferredDocumentImpl.getNodeName(this.fNodeIndex);
        this.publicId = deferredDocumentImpl.getNodeValue(this.fNodeIndex);
        this.systemId = deferredDocumentImpl.getNodeURI(this.fNodeIndex);
        final int nodeExtra = deferredDocumentImpl.getNodeExtra(this.fNodeIndex);
        deferredDocumentImpl.getNodeType(nodeExtra);
        this.notationName = deferredDocumentImpl.getNodeName(nodeExtra);
        this.version = deferredDocumentImpl.getNodeValue(nodeExtra);
        this.encoding = deferredDocumentImpl.getNodeURI(nodeExtra);
        final int nodeExtra2 = deferredDocumentImpl.getNodeExtra(nodeExtra);
        this.baseURI = deferredDocumentImpl.getNodeName(nodeExtra2);
        this.inputEncoding = deferredDocumentImpl.getNodeValue(nodeExtra2);
    }
    
    protected void synchronizeChildren() {
        this.needsSyncChildren(false);
        this.isReadOnly(false);
        ((DeferredDocumentImpl)this.ownerDocument()).synchronizeChildren(this, this.fNodeIndex);
        this.setReadOnly(true, true);
    }
}
