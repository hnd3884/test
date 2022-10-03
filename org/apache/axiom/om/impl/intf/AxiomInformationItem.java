package org.apache.axiom.om.impl.intf;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.OMInformationItem;

public interface AxiomInformationItem extends OMInformationItem, CoreNode
{
    OMMetaFactory getMetaFactory();
    
    OMInformationItem clone(final OMCloneOptions p0);
    
    OMFactory getOMFactory();
}
