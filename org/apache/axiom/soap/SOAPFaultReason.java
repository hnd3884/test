package org.apache.axiom.soap;

import java.util.List;
import org.apache.axiom.om.OMElement;

public interface SOAPFaultReason extends OMElement
{
    void addSOAPText(final SOAPFaultText p0) throws SOAPProcessingException;
    
    SOAPFaultText getFirstSOAPText();
    
    List getAllSoapTexts();
    
    SOAPFaultText getSOAPFaultText(final String p0);
}
