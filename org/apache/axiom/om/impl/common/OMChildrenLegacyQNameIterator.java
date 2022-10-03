package org.apache.axiom.om.impl.common;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNode;

public class OMChildrenLegacyQNameIterator extends OMChildrenQNameIterator
{
    public OMChildrenLegacyQNameIterator(final OMNode currentChild, final QName qName) {
        super(currentChild, qName);
    }
    
    @Override
    public boolean isEqual(final QName searchQName, final QName currentQName) {
        final String localPart = searchQName.getLocalPart();
        final boolean localNameMatch = localPart == null || localPart.equals("") || (currentQName != null && currentQName.getLocalPart().equals(localPart));
        final String namespaceURI = searchQName.getNamespaceURI();
        final boolean namespaceURIMatch = namespaceURI.equals("") || (currentQName != null && currentQName.getNamespaceURI().equals(namespaceURI));
        return localNameMatch && namespaceURIMatch;
    }
}
