package org.apache.axiom.om.impl.intf;

import org.apache.axiom.core.CoreCharacterDataNode;

public interface AxiomCharacterDataNode extends CoreCharacterDataNode, AxiomText, AxiomCoreLeafNode
{
    int getType();
}
