package org.apache.axiom.soap;

import org.apache.axiom.mime.MediaType;
import javax.xml.namespace.QName;

public class SOAP11Version implements SOAPVersion, SOAP11Constants
{
    @Deprecated
    public static SOAP11Version getSingleton() {
        return (SOAP11Version)SOAPVersion.SOAP11;
    }
    
    SOAP11Version() {
    }
    
    public String getEnvelopeURI() {
        return "http://schemas.xmlsoap.org/soap/envelope/";
    }
    
    public String getEncodingURI() {
        return "http://schemas.xmlsoap.org/soap/encoding/";
    }
    
    public QName getRoleAttributeQName() {
        return SOAP11Version.QNAME_ACTOR;
    }
    
    public String getNextRoleURI() {
        return "http://schemas.xmlsoap.org/soap/actor/next";
    }
    
    public QName getMustUnderstandFaultCode() {
        return SOAP11Version.QNAME_MU_FAULTCODE;
    }
    
    public QName getSenderFaultCode() {
        return SOAP11Version.QNAME_SENDER_FAULTCODE;
    }
    
    public QName getReceiverFaultCode() {
        return SOAP11Version.QNAME_RECEIVER_FAULTCODE;
    }
    
    public QName getFaultReasonQName() {
        return SOAP11Version.QNAME_FAULT_REASON;
    }
    
    public QName getFaultCodeQName() {
        return SOAP11Version.QNAME_FAULT_CODE;
    }
    
    public QName getFaultDetailQName() {
        return SOAP11Version.QNAME_FAULT_DETAIL;
    }
    
    public QName getFaultRoleQName() {
        return SOAP11Version.QNAME_FAULT_ROLE;
    }
    
    public MediaType getMediaType() {
        return MediaType.TEXT_XML;
    }
}
