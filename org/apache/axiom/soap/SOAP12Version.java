package org.apache.axiom.soap;

import org.apache.axiom.mime.MediaType;
import javax.xml.namespace.QName;

public class SOAP12Version implements SOAPVersion, SOAP12Constants
{
    @Deprecated
    public static SOAP12Version getSingleton() {
        return (SOAP12Version)SOAPVersion.SOAP12;
    }
    
    SOAP12Version() {
    }
    
    public String getEnvelopeURI() {
        return "http://www.w3.org/2003/05/soap-envelope";
    }
    
    public String getEncodingURI() {
        return "http://www.w3.org/2003/05/soap-encoding";
    }
    
    public QName getRoleAttributeQName() {
        return SOAP12Version.QNAME_ROLE;
    }
    
    public String getNextRoleURI() {
        return "http://www.w3.org/2003/05/soap-envelope/role/next";
    }
    
    public QName getMustUnderstandFaultCode() {
        return SOAP12Version.QNAME_MU_FAULTCODE;
    }
    
    public QName getSenderFaultCode() {
        return SOAP12Version.QNAME_SENDER_FAULTCODE;
    }
    
    public QName getReceiverFaultCode() {
        return SOAP12Version.QNAME_RECEIVER_FAULTCODE;
    }
    
    public QName getFaultReasonQName() {
        return SOAP12Version.QNAME_FAULT_REASON;
    }
    
    public QName getFaultCodeQName() {
        return SOAP12Version.QNAME_FAULT_CODE;
    }
    
    public QName getFaultDetailQName() {
        return SOAP12Version.QNAME_FAULT_DETAIL;
    }
    
    public QName getFaultRoleQName() {
        return SOAP12Version.QNAME_FAULT_ROLE;
    }
    
    public MediaType getMediaType() {
        return MediaType.APPLICATION_SOAP_XML;
    }
}
