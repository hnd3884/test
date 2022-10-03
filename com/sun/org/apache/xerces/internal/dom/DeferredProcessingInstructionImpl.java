package com.sun.org.apache.xerces.internal.dom;

public class DeferredProcessingInstructionImpl extends ProcessingInstructionImpl implements DeferredNode
{
    static final long serialVersionUID = -4643577954293565388L;
    protected transient int fNodeIndex;
    
    DeferredProcessingInstructionImpl(final DeferredDocumentImpl ownerDocument, final int nodeIndex) {
        super(ownerDocument, null, null);
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
        this.target = ownerDocument.getNodeName(this.fNodeIndex);
        this.data = ownerDocument.getNodeValueString(this.fNodeIndex);
    }
}
