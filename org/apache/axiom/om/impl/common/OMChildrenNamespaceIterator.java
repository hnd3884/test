package org.apache.axiom.om.impl.common;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNode;

public class OMChildrenNamespaceIterator extends OMChildrenQNameIterator
{
    public OMChildrenNamespaceIterator(final OMNode currentChild, final String uri) {
        super(currentChild, new QName(uri, "dummyName"));
    }
    
    @Override
    public boolean isEqual(final QName searchQName, final QName currentQName) {
        return searchQName.getNamespaceURI().equals(currentQName.getNamespaceURI());
    }
}
