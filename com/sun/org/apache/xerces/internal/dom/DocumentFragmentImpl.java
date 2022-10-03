package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.DocumentFragment;

public class DocumentFragmentImpl extends ParentNode implements DocumentFragment
{
    static final long serialVersionUID = -7596449967279236746L;
    
    public DocumentFragmentImpl(final CoreDocumentImpl ownerDoc) {
        super(ownerDoc);
    }
    
    public DocumentFragmentImpl() {
    }
    
    @Override
    public short getNodeType() {
        return 11;
    }
    
    @Override
    public String getNodeName() {
        return "#document-fragment";
    }
    
    @Override
    public void normalize() {
        if (this.isNormalized()) {
            return;
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        ChildNode next;
        for (ChildNode kid = this.firstChild; kid != null; kid = next) {
            next = kid.nextSibling;
            if (kid.getNodeType() == 3) {
                if (next != null && next.getNodeType() == 3) {
                    ((Text)kid).appendData(next.getNodeValue());
                    this.removeChild(next);
                    next = kid;
                }
                else if (kid.getNodeValue() == null || kid.getNodeValue().length() == 0) {
                    this.removeChild(kid);
                }
            }
            kid.normalize();
        }
        this.isNormalized(true);
    }
}
