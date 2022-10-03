package org.apache.axiom.om.impl.intf;

import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.om.impl.OMNodeEx;

public interface AxiomChildNode extends OMNodeEx, CoreChildNode, AxiomSerializable
{
    OMNode detach();
    
    OMNode getNextOMSibling();
    
    OMContainer getParent();
    
    OMNode getPreviousOMSibling();
    
    void insertSiblingAfter(final OMNode p0) throws OMException;
    
    void insertSiblingBefore(final OMNode p0) throws OMException;
}
