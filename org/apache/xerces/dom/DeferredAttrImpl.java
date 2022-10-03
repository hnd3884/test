package org.apache.xerces.dom;

public final class DeferredAttrImpl extends AttrImpl implements DeferredNode
{
    static final long serialVersionUID = 6903232312469148636L;
    protected transient int fNodeIndex;
    
    DeferredAttrImpl(final DeferredDocumentImpl deferredDocumentImpl, final int fNodeIndex) {
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
        final DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument();
        this.name = deferredDocumentImpl.getNodeName(this.fNodeIndex);
        final int nodeExtra = deferredDocumentImpl.getNodeExtra(this.fNodeIndex);
        this.isSpecified((nodeExtra & 0x20) != 0x0);
        this.isIdAttribute((nodeExtra & 0x200) != 0x0);
        this.type = deferredDocumentImpl.getTypeInfo(deferredDocumentImpl.getLastChild(this.fNodeIndex));
    }
    
    protected void synchronizeChildren() {
        ((DeferredDocumentImpl)this.ownerDocument()).synchronizeChildren(this, this.fNodeIndex);
    }
}
