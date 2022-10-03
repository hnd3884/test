package org.apache.axiom.om;

public interface OMInformationItem
{
    OMFactory getOMFactory();
    
    OMInformationItem clone(final OMCloneOptions p0);
}
