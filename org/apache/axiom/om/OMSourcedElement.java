package org.apache.axiom.om;

public interface OMSourcedElement extends OMElement
{
    boolean isExpanded();
    
    OMDataSource getDataSource();
    
    @Deprecated
    OMDataSource setDataSource(final OMDataSource p0);
    
    Object getObject(final Class p0);
}
