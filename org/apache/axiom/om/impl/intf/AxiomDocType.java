package org.apache.axiom.om.impl.intf;

import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.shared.IDocumentTypeDeclaration;
import org.apache.axiom.om.OMDocType;

public interface AxiomDocType extends OMDocType, AxiomCoreLeafNode, IDocumentTypeDeclaration
{
    void buildWithAttachments();
    
    String getRootName();
    
    int getType();
    
    void internalSerialize(final Serializer p0, final OMOutputFormat p1, final boolean p2) throws OutputException;
}
