package org.apache.axiom.om.impl.common;

import org.apache.axiom.om.OMElement;
import java.util.Iterator;
import org.apache.axiom.om.impl.traverse.OMChildrenIterator;
import org.apache.axiom.om.OMNode;
import javax.xml.namespace.QName;
import org.apache.axiom.om.impl.traverse.OMFilterIterator;

public class OMChildrenQNameIterator extends OMFilterIterator
{
    private final QName givenQName;
    
    public OMChildrenQNameIterator(final OMNode currentChild, final QName givenQName) {
        super((Iterator)new OMChildrenIterator(currentChild));
        this.givenQName = givenQName;
    }
    
    public boolean isEqual(final QName searchQName, final QName currentQName) {
        return searchQName.equals(currentQName);
    }
    
    protected boolean matches(final OMNode node) {
        if (node instanceof OMElement) {
            final QName thisQName = ((OMElement)node).getQName();
            return this.givenQName == null || this.isEqual(this.givenQName, thisQName);
        }
        return false;
    }
}
