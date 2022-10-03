package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFaultCode;

public interface SOAP12FactoryEx extends SOAPFactoryEx
{
    SOAPFaultValue createSOAPFaultValue(final SOAPFaultCode p0, final OMXMLParserWrapper p1);
    
    SOAPFaultValue createSOAPFaultValue(final SOAPFaultSubCode p0, final OMXMLParserWrapper p1);
    
    SOAPFaultSubCode createSOAPFaultSubCode(final SOAPFaultCode p0, final OMXMLParserWrapper p1);
    
    SOAPFaultSubCode createSOAPFaultSubCode(final SOAPFaultSubCode p0, final OMXMLParserWrapper p1);
    
    SOAPFaultText createSOAPFaultText(final SOAPFaultReason p0, final OMXMLParserWrapper p1);
    
    SOAPFaultNode createSOAPFaultNode(final SOAPFault p0, final OMXMLParserWrapper p1);
}
