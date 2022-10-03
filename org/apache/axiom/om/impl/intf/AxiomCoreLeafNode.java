package org.apache.axiom.om.impl.intf;

import org.apache.axiom.om.OMXMLParserWrapper;

public interface AxiomCoreLeafNode extends AxiomLeafNode
{
    void build();
    
    OMXMLParserWrapper getBuilder();
    
    boolean isComplete();
}
