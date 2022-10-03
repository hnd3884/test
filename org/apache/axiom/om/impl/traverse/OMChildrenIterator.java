package org.apache.axiom.om.impl.traverse;

import org.apache.axiom.om.OMNode;

public class OMChildrenIterator extends OMAbstractIterator
{
    public OMChildrenIterator(final OMNode currentChild) {
        super(currentChild);
    }
    
    @Override
    protected OMNode getNextNode(final OMNode currentNode) {
        return currentNode.getNextOMSibling();
    }
}
