package org.apache.axiom.om;

public interface OMDocType extends OMNode
{
    String getRootName();
    
    String getPublicId();
    
    String getSystemId();
    
    String getInternalSubset();
}
