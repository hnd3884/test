package org.apache.xerces.dom;

public class DeferredNotationImpl extends NotationImpl implements DeferredNode
{
    static final long serialVersionUID = 5705337172887990848L;
    protected transient int fNodeIndex;
    
    DeferredNotationImpl(final DeferredDocumentImpl deferredDocumentImpl, final int fNodeIndex) {
        super(deferredDocumentImpl, null);
        this.fNodeIndex = fNodeIndex;
        this.needsSyncData(true);
    }
    
    public int getNodeIndex() {
        return this.fNodeIndex;
    }
    
    protected void synchronizeData() {
        this.needsSyncData(false);
        final DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument();
        this.name = deferredDocumentImpl.getNodeName(this.fNodeIndex);
        deferredDocumentImpl.getNodeType(this.fNodeIndex);
        this.publicId = deferredDocumentImpl.getNodeValue(this.fNodeIndex);
        this.systemId = deferredDocumentImpl.getNodeURI(this.fNodeIndex);
        final int nodeExtra = deferredDocumentImpl.getNodeExtra(this.fNodeIndex);
        deferredDocumentImpl.getNodeType(nodeExtra);
        this.baseURI = deferredDocumentImpl.getNodeName(nodeExtra);
    }
}
