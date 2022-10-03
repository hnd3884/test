package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.om.impl.intf.AxiomDocument;

public interface AxiomSOAPMessage extends AxiomDocument, SOAPMessage
{
    Class<? extends CoreNode> coreGetNodeClass();
    
    OMFactory getOMFactory();
    
     <T> void initAncillaryData(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
    
    void initSOAPFactory(final SOAPFactory p0);
    
    void internalSerialize(final Serializer p0, final OMOutputFormat p1, final boolean p2, final boolean p3) throws OutputException;
}
