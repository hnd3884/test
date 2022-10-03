package org.apache.axiom.om;

import javax.xml.namespace.QName;

public interface OMNamedInformationItem extends OMInformationItem
{
    String getLocalName();
    
    void setLocalName(final String p0);
    
    OMNamespace getNamespace();
    
    void setNamespace(final OMNamespace p0, final boolean p1);
    
    QName getQName();
    
    String getPrefix();
    
    String getNamespaceURI();
    
    boolean hasName(final QName p0);
}
