package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.soap.SOAPFactory;

public interface SOAPFactoryEx extends SOAPFactory, OMFactoryEx
{
    SOAPMessage createSOAPMessage(final OMXMLParserWrapper p0);
    
    SOAPEnvelope createSOAPEnvelope(final SOAPMessage p0, final OMXMLParserWrapper p1);
    
    SOAPHeader createSOAPHeader(final SOAPEnvelope p0, final OMXMLParserWrapper p1);
    
    SOAPHeaderBlock createSOAPHeaderBlock(final String p0, final SOAPHeader p1, final OMXMLParserWrapper p2) throws SOAPProcessingException;
    
    SOAPFault createSOAPFault(final SOAPBody p0, final OMXMLParserWrapper p1);
    
    SOAPBody createSOAPBody(final SOAPEnvelope p0, final OMXMLParserWrapper p1);
    
    SOAPFaultCode createSOAPFaultCode(final SOAPFault p0, final OMXMLParserWrapper p1);
    
    SOAPFaultReason createSOAPFaultReason(final SOAPFault p0, final OMXMLParserWrapper p1);
    
    SOAPFaultRole createSOAPFaultRole(final SOAPFault p0, final OMXMLParserWrapper p1);
    
    SOAPFaultDetail createSOAPFaultDetail(final SOAPFault p0, final OMXMLParserWrapper p1);
}
