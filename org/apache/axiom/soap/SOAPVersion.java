package org.apache.axiom.soap;

import org.apache.axiom.mime.MediaType;
import javax.xml.namespace.QName;

public interface SOAPVersion
{
    public static final SOAPVersion SOAP11 = new SOAP11Version();
    public static final SOAPVersion SOAP12 = new SOAP12Version();
    
    String getEnvelopeURI();
    
    String getEncodingURI();
    
    QName getRoleAttributeQName();
    
    String getNextRoleURI();
    
    QName getMustUnderstandFaultCode();
    
    QName getSenderFaultCode();
    
    QName getReceiverFaultCode();
    
    QName getFaultReasonQName();
    
    QName getFaultCodeQName();
    
    QName getFaultDetailQName();
    
    QName getFaultRoleQName();
    
    MediaType getMediaType();
}
