package org.apache.xerces.dom;

public class DeferredTextImpl extends TextImpl implements DeferredNode
{
    static final long serialVersionUID = 2310613872100393425L;
    protected transient int fNodeIndex;
    
    DeferredTextImpl(final DeferredDocumentImpl deferredDocumentImpl, final int fNodeIndex) {
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
        this.data = deferredDocumentImpl.getNodeValueString(this.fNodeIndex);
        this.isIgnorableWhitespace(deferredDocumentImpl.getNodeExtra(this.fNodeIndex) == 1);
    }
}
