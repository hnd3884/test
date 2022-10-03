package org.apache.axiom.om.impl.intf;

import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.core.CoreComment;
import org.apache.axiom.om.OMComment;

public interface AxiomComment extends OMComment, AxiomLeafNode, CoreComment, AxiomCoreParentNode
{
    void buildWithAttachments();
    
    int getType();
    
    String getValue();
    
    void internalSerialize(final Serializer p0, final OMOutputFormat p1, final boolean p2) throws OutputException;
    
    void setValue(final String p0);
}
