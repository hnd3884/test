package com.sun.org.apache.xerces.internal.dom;

public class DeferredCommentImpl extends CommentImpl implements DeferredNode
{
    static final long serialVersionUID = 6498796371083589338L;
    protected transient int fNodeIndex;
    
    DeferredCommentImpl(final DeferredDocumentImpl ownerDocument, final int nodeIndex) {
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
        this.data = ownerDocument.getNodeValueString(this.fNodeIndex);
    }
}
