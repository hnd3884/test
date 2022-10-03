package org.apache.axiom.om.impl.intf;

import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.core.CoreEntityReference;
import org.apache.axiom.om.OMEntityReference;

public interface AxiomEntityReference extends OMEntityReference, AxiomCoreLeafNode, CoreEntityReference
{
    void buildWithAttachments();
    
    String getName();
    
    String getReplacementText();
    
    int getType();
    
    void internalSerialize(final Serializer p0, final OMOutputFormat p1, final boolean p2) throws OutputException;
}
