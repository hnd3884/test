package com.sun.org.apache.xerces.internal.dom;

public class DeferredEntityImpl extends EntityImpl implements DeferredNode
{
    static final long serialVersionUID = 4760180431078941638L;
    protected transient int fNodeIndex;
    
    DeferredEntityImpl(final DeferredDocumentImpl ownerDocument, final int nodeIndex) {
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
        this.publicId = ownerDocument.getNodeValue(this.fNodeIndex);
        this.systemId = ownerDocument.getNodeURI(this.fNodeIndex);
        final int extraDataIndex = ownerDocument.getNodeExtra(this.fNodeIndex);
        ownerDocument.getNodeType(extraDataIndex);
        this.notationName = ownerDocument.getNodeName(extraDataIndex);
        this.version = ownerDocument.getNodeValue(extraDataIndex);
        this.encoding = ownerDocument.getNodeURI(extraDataIndex);
        final int extraIndex2 = ownerDocument.getNodeExtra(extraDataIndex);
        this.baseURI = ownerDocument.getNodeName(extraIndex2);
        this.inputEncoding = ownerDocument.getNodeValue(extraIndex2);
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
