package org.apache.axiom.soap;

import org.apache.axiom.om.OMDocument;

public interface SOAPMessage extends OMDocument
{
    SOAPEnvelope getSOAPEnvelope() throws SOAPProcessingException;
    
    void setSOAPEnvelope(final SOAPEnvelope p0);
}
