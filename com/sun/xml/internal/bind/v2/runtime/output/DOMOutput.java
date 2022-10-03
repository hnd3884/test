package com.sun.xml.internal.bind.v2.runtime.output;

import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import org.w3c.dom.Node;
import com.sun.xml.internal.bind.v2.runtime.AssociationMap;

public final class DOMOutput extends SAXOutput
{
    private final AssociationMap assoc;
    
    public DOMOutput(final Node node, final AssociationMap assoc) {
        super(new SAX2DOMEx(node));
        this.assoc = assoc;
        assert assoc != null;
    }
    
    private SAX2DOMEx getBuilder() {
        return (SAX2DOMEx)this.out;
    }
    
    @Override
    public void endStartTag() throws SAXException {
        super.endStartTag();
        final Object op = this.nsContext.getCurrent().getOuterPeer();
        if (op != null) {
            this.assoc.addOuter(this.getBuilder().getCurrentElement(), op);
        }
        final Object ip = this.nsContext.getCurrent().getInnerPeer();
        if (ip != null) {
            this.assoc.addInner(this.getBuilder().getCurrentElement(), ip);
        }
    }
}
