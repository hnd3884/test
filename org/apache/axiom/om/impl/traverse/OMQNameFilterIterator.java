package org.apache.axiom.om.impl.traverse;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import java.util.Iterator;
import javax.xml.namespace.QName;

public class OMQNameFilterIterator extends OMFilterIterator
{
    private final QName qname;
    
    public OMQNameFilterIterator(final Iterator parent, final QName qname) {
        super(parent);
        this.qname = qname;
    }
    
    @Override
    protected boolean matches(final OMNode node) {
        return node instanceof OMElement && ((OMElement)node).getQName().equals(this.qname);
    }
}
