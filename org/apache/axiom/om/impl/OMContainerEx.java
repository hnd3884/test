package org.apache.axiom.om.impl;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMContainer;

public interface OMContainerEx extends OMContainer
{
    void setComplete(final boolean p0);
    
    void discarded();
    
    void addChild(final OMNode p0, final boolean p1);
}
