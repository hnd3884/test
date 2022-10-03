package com.sun.org.apache.xerces.internal.dom;

public class DeferredNotationImpl extends NotationImpl implements DeferredNode
{
    static final long serialVersionUID = 5705337172887990848L;
    protected transient int fNodeIndex;
    
    DeferredNotationImpl(final DeferredDocumentImpl ownerDocument, final int nodeIndex) {
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
        final DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)this.ownerDocument();
        this.name = ownerDocument.getNodeName(this.fNodeIndex);
        ownerDocument.getNodeType(this.fNodeIndex);
        this.publicId = ownerDocument.getNodeValue(this.fNodeIndex);
        this.systemId = ownerDocument.getNodeURI(this.fNodeIndex);
        final int extraDataIndex = ownerDocument.getNodeExtra(this.fNodeIndex);
        ownerDocument.getNodeType(extraDataIndex);
        this.baseURI = ownerDocument.getNodeName(extraDataIndex);
    }
}
