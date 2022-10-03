package org.apache.axiom.soap;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMElement;

public interface SOAPEnvelope extends OMElement
{
    SOAPHeader getHeader();
    
    SOAPHeader getOrCreateHeader();
    
    SOAPBody getBody() throws OMException;
    
    SOAPVersion getVersion();
    
    boolean hasFault();
    
    OMNamespace getSOAPBodyFirstElementNS();
    
    String getSOAPBodyFirstElementLocalName();
}
