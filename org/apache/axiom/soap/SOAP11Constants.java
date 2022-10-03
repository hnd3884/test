package org.apache.axiom.soap;

import javax.xml.namespace.QName;

public interface SOAP11Constants extends SOAPConstants
{
    public static final String SOAP_ENVELOPE_NAMESPACE_URI = "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String SOAP_ENCODING_NAMESPACE_URI = "http://schemas.xmlsoap.org/soap/encoding/";
    public static final String ATTR_ACTOR = "actor";
    public static final String SOAP_FAULT_CODE_LOCAL_NAME = "faultcode";
    public static final String SOAP_FAULT_STRING_LOCAL_NAME = "faultstring";
    public static final String SOAP_FAULT_ACTOR_LOCAL_NAME = "faultactor";
    public static final String SOAP_FAULT_DETAIL_LOCAL_NAME = "detail";
    public static final String SOAP_11_CONTENT_TYPE = "text/xml";
    public static final String FAULT_CODE_SENDER = "Client";
    public static final String FAULT_CODE_RECEIVER = "Server";
    public static final String SOAP_ACTOR_NEXT = "http://schemas.xmlsoap.org/soap/actor/next";
    public static final QName QNAME_ACTOR = new QName("http://schemas.xmlsoap.org/soap/envelope/", "actor");
    public static final QName QNAME_MU_FAULTCODE = new QName("http://schemas.xmlsoap.org/soap/envelope/", "MustUnderstand");
    public static final QName QNAME_SENDER_FAULTCODE = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Client");
    public static final QName QNAME_RECEIVER_FAULTCODE = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server");
    public static final QName QNAME_FAULT_REASON = new QName("faultstring");
    public static final QName QNAME_FAULT_CODE = new QName("faultcode");
    public static final QName QNAME_FAULT_DETAIL = new QName("detail");
    public static final QName QNAME_FAULT_ROLE = new QName("faultactor");
}
