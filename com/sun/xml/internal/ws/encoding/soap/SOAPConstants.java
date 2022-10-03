package com.sun.xml.internal.ws.encoding.soap;

import javax.xml.namespace.QName;

public class SOAPConstants
{
    public static final String URI_ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String URI_HTTP = "http://schemas.xmlsoap.org/soap/http";
    public static final String URI_ENCODING = "http://schemas.xmlsoap.org/soap/encoding/";
    public static final String NS_WSDL_SOAP = "http://schemas.xmlsoap.org/wsdl/soap/";
    public static final QName QNAME_ENVELOPE_ENCODINGSTYLE;
    public static final QName QNAME_SOAP_ENVELOPE;
    public static final QName QNAME_SOAP_HEADER;
    public static final QName QNAME_MUSTUNDERSTAND;
    public static final QName QNAME_ROLE;
    public static final QName QNAME_SOAP_BODY;
    public static final QName QNAME_SOAP_FAULT;
    public static final QName QNAME_SOAP_FAULT_CODE;
    public static final QName QNAME_SOAP_FAULT_STRING;
    public static final QName QNAME_SOAP_FAULT_ACTOR;
    public static final QName QNAME_SOAP_FAULT_DETAIL;
    public static final QName FAULT_CODE_MUST_UNDERSTAND;
    public static final QName FAULT_CODE_VERSION_MISMATCH;
    public static final QName FAULT_CODE_DATA_ENCODING_UNKNOWN;
    public static final QName FAULT_CODE_PROCEDURE_NOT_PRESENT;
    public static final QName FAULT_CODE_BAD_ARGUMENTS;
    
    static {
        QNAME_ENVELOPE_ENCODINGSTYLE = new QName("http://schemas.xmlsoap.org/soap/envelope/", "encodingStyle");
        QNAME_SOAP_ENVELOPE = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Envelope");
        QNAME_SOAP_HEADER = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Header");
        QNAME_MUSTUNDERSTAND = new QName("http://schemas.xmlsoap.org/soap/envelope/", "mustUnderstand");
        QNAME_ROLE = new QName("http://schemas.xmlsoap.org/soap/envelope/", "actor");
        QNAME_SOAP_BODY = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body");
        QNAME_SOAP_FAULT = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Fault");
        QNAME_SOAP_FAULT_CODE = new QName("", "faultcode");
        QNAME_SOAP_FAULT_STRING = new QName("", "faultstring");
        QNAME_SOAP_FAULT_ACTOR = new QName("", "faultactor");
        QNAME_SOAP_FAULT_DETAIL = new QName("", "detail");
        FAULT_CODE_MUST_UNDERSTAND = new QName("http://schemas.xmlsoap.org/soap/envelope/", "MustUnderstand");
        FAULT_CODE_VERSION_MISMATCH = new QName("http://schemas.xmlsoap.org/soap/envelope/", "VersionMismatch");
        FAULT_CODE_DATA_ENCODING_UNKNOWN = new QName("http://schemas.xmlsoap.org/soap/envelope/", "DataEncodingUnknown");
        FAULT_CODE_PROCEDURE_NOT_PRESENT = new QName("http://schemas.xmlsoap.org/soap/envelope/", "ProcedureNotPresent");
        FAULT_CODE_BAD_ARGUMENTS = new QName("http://schemas.xmlsoap.org/soap/envelope/", "BadArguments");
    }
}
