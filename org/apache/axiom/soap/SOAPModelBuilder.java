package org.apache.axiom.soap;

import org.apache.axiom.om.OMXMLParserWrapper;

public interface SOAPModelBuilder extends OMXMLParserWrapper
{
    SOAPEnvelope getSOAPEnvelope();
    
    SOAPMessage getSOAPMessage();
}
