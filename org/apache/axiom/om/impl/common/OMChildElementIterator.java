package org.apache.axiom.om.impl.common;

import java.util.Iterator;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.traverse.OMChildrenIterator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.traverse.OMFilterIterator;

public class OMChildElementIterator extends OMFilterIterator
{
    public OMChildElementIterator(final OMElement currentChild) {
        super((Iterator)new OMChildrenIterator((OMNode)currentChild));
    }
    
    protected boolean matches(final OMNode node) {
        return node instanceof OMElement;
    }
}
