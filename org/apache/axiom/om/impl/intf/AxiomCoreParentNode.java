package org.apache.axiom.om.impl.intf;

import org.apache.axiom.core.CoreParentNode;

public interface AxiomCoreParentNode extends CoreParentNode, AxiomSerializable
{
    boolean isComplete();
}
