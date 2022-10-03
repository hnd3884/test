package org.apache.axiom.om;

public interface QNameAwareOMDataSource extends OMDataSource
{
    String getLocalName();
    
    String getNamespaceURI();
    
    String getPrefix();
}
