package org.apache.axiom.om.impl.common;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNode;

public class OMChildrenLocalNameIterator extends OMChildrenQNameIterator
{
    public OMChildrenLocalNameIterator(final OMNode currentChild, final String localName) {
        super(currentChild, new QName("", localName));
    }
    
    @Override
    public boolean isEqual(final QName searchQName, final QName currentQName) {
        return searchQName.getLocalPart().equals(currentQName.getLocalPart());
    }
}
