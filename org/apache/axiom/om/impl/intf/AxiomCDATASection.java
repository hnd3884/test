package org.apache.axiom.om.impl.intf;

import org.apache.axiom.core.CoreCDATASection;

public interface AxiomCDATASection extends CoreCDATASection, AxiomText, AxiomCoreParentNode
{
    int getType();
}
