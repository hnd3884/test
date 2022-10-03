package org.apache.axiom.soap;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMFactory;

public interface SOAPFactory extends OMFactory
{
    String getSoapVersionURI();
    
    SOAPVersion getSOAPVersion();
    
    SOAPMessage createSOAPMessage();
    
    @Deprecated
    SOAPMessage createSOAPMessage(final OMXMLParserWrapper p0);
    
    SOAPEnvelope createSOAPEnvelope() throws SOAPProcessingException;
    
    SOAPEnvelope createSOAPEnvelope(final OMNamespace p0);
    
    SOAPHeader createSOAPHeader(final SOAPEnvelope p0) throws SOAPProcessingException;
    
    SOAPHeader createSOAPHeader() throws SOAPProcessingException;
    
    SOAPHeaderBlock createSOAPHeaderBlock(final String p0, final OMNamespace p1, final SOAPHeader p2) throws SOAPProcessingException;
    
    SOAPHeaderBlock createSOAPHeaderBlock(final String p0, final OMNamespace p1) throws SOAPProcessingException;
    
    SOAPHeaderBlock createSOAPHeaderBlock(final OMDataSource p0);
    
    SOAPHeaderBlock createSOAPHeaderBlock(final String p0, final OMNamespace p1, final OMDataSource p2) throws SOAPProcessingException;
    
    SOAPFault createSOAPFault(final SOAPBody p0, final Exception p1) throws SOAPProcessingException;
    
    SOAPFault createSOAPFault(final SOAPBody p0) throws SOAPProcessingException;
    
    SOAPFault createSOAPFault() throws SOAPProcessingException;
    
    SOAPBody createSOAPBody(final SOAPEnvelope p0) throws SOAPProcessingException;
    
    SOAPBody createSOAPBody() throws SOAPProcessingException;
    
    SOAPFaultCode createSOAPFaultCode(final SOAPFault p0) throws SOAPProcessingException;
    
    SOAPFaultCode createSOAPFaultCode() throws SOAPProcessingException;
    
    SOAPFaultValue createSOAPFaultValue(final SOAPFaultCode p0) throws SOAPProcessingException;
    
    SOAPFaultValue createSOAPFaultValue() throws SOAPProcessingException;
    
    SOAPFaultValue createSOAPFaultValue(final SOAPFaultSubCode p0) throws SOAPProcessingException;
    
    SOAPFaultSubCode createSOAPFaultSubCode(final SOAPFaultCode p0) throws SOAPProcessingException;
    
    SOAPFaultSubCode createSOAPFaultSubCode() throws SOAPProcessingException;
    
    SOAPFaultSubCode createSOAPFaultSubCode(final SOAPFaultSubCode p0) throws SOAPProcessingException;
    
    SOAPFaultReason createSOAPFaultReason(final SOAPFault p0) throws SOAPProcessingException;
    
    SOAPFaultReason createSOAPFaultReason() throws SOAPProcessingException;
    
    SOAPFaultText createSOAPFaultText(final SOAPFaultReason p0) throws SOAPProcessingException;
    
    SOAPFaultText createSOAPFaultText() throws SOAPProcessingException;
    
    SOAPFaultNode createSOAPFaultNode(final SOAPFault p0) throws SOAPProcessingException;
    
    SOAPFaultNode createSOAPFaultNode() throws SOAPProcessingException;
    
    SOAPFaultRole createSOAPFaultRole(final SOAPFault p0) throws SOAPProcessingException;
    
    SOAPFaultRole createSOAPFaultRole() throws SOAPProcessingException;
    
    SOAPFaultDetail createSOAPFaultDetail(final SOAPFault p0) throws SOAPProcessingException;
    
    SOAPFaultDetail createSOAPFaultDetail() throws SOAPProcessingException;
    
    SOAPEnvelope getDefaultEnvelope() throws SOAPProcessingException;
    
    SOAPMessage createDefaultSOAPMessage();
    
    SOAPEnvelope getDefaultFaultEnvelope() throws SOAPProcessingException;
    
    OMNamespace getNamespace();
}
