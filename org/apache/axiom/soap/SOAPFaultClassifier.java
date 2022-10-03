package org.apache.axiom.soap;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;

public interface SOAPFaultClassifier extends OMElement
{
    void setValue(final SOAPFaultValue p0) throws SOAPProcessingException;
    
    SOAPFaultValue getValue();
    
    void setValue(final QName p0);
    
    QName getValueAsQName();
    
    void setSubCode(final SOAPFaultSubCode p0) throws SOAPProcessingException;
    
    SOAPFaultSubCode getSubCode();
}
