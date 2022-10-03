package org.apache.xerces.dom;

import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.DocumentFragment;

public class DocumentFragmentImpl extends ParentNode implements DocumentFragment
{
    static final long serialVersionUID = -7596449967279236746L;
    
    public DocumentFragmentImpl(final CoreDocumentImpl coreDocumentImpl) {
        super(coreDocumentImpl);
    }
    
    public DocumentFragmentImpl() {
    }
    
    public short getNodeType() {
        return 11;
    }
    
    public String getNodeName() {
        return "#document-fragment";
    }
    
    public void normalize() {
        if (this.isNormalized()) {
            return;
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        Object nextSibling;
        for (Object firstChild = this.firstChild; firstChild != null; firstChild = nextSibling) {
            nextSibling = ((ChildNode)firstChild).nextSibling;
            if (((NodeImpl)firstChild).getNodeType() == 3) {
                if (nextSibling != null && ((NodeImpl)nextSibling).getNodeType() == 3) {
                    ((Text)firstChild).appendData(((NodeImpl)nextSibling).getNodeValue());
                    this.removeChild((Node)nextSibling);
                    nextSibling = firstChild;
                }
                else if (((NodeImpl)firstChild).getNodeValue() == null || ((NodeImpl)firstChild).getNodeValue().length() == 0) {
                    this.removeChild((Node)firstChild);
                }
            }
            ((ChildNode)firstChild).normalize();
        }
        this.isNormalized(true);
    }
}
