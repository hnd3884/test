package com.sun.xml.internal.ws.encoding.soap;

import javax.xml.namespace.QName;

public class SOAP12Constants
{
    public static final String URI_ENVELOPE = "http://www.w3.org/2003/05/soap-envelope";
    public static final String URI_ENCODING = "http://schemas.xmlsoap.org/soap/encoding/";
    public static final String URI_HTTP = "http://www.w3.org/2003/05/soap/bindings/HTTP/";
    public static final String URI_SOAP_RPC = "http://www.w3.org/2002/06/soap-rpc";
    public static final QName QNAME_SOAP_RPC;
    public static final QName QNAME_SOAP_RESULT;
    public static final QName QNAME_SOAP_ENVELOPE;
    public static final QName QNAME_SOAP_BODY;
    public static final QName QNAME_SOAP_HEADER;
    public static final QName QNAME_ENVELOPE_ENCODINGSTYLE;
    public static final QName QNAME_SOAP_FAULT;
    public static final QName QNAME_MUSTUNDERSTAND;
    public static final QName QNAME_ROLE;
    public static final QName QNAME_NOT_UNDERSTOOD;
    public static final QName QNAME_FAULT_CODE;
    public static final QName QNAME_FAULT_SUBCODE;
    public static final QName QNAME_FAULT_VALUE;
    public static final QName QNAME_FAULT_REASON;
    public static final QName QNAME_FAULT_NODE;
    public static final QName QNAME_FAULT_ROLE;
    public static final QName QNAME_FAULT_DETAIL;
    public static final QName QNAME_FAULT_REASON_TEXT;
    public static final QName QNAME_UPGRADE;
    public static final QName QNAME_UPGRADE_SUPPORTED_ENVELOPE;
    public static final QName FAULT_CODE_MUST_UNDERSTAND;
    public static final QName FAULT_CODE_MISUNDERSTOOD;
    public static final QName FAULT_CODE_VERSION_MISMATCH;
    public static final QName FAULT_CODE_DATA_ENCODING_UNKNOWN;
    public static final QName FAULT_CODE_PROCEDURE_NOT_PRESENT;
    public static final QName FAULT_CODE_BAD_ARGUMENTS;
    
    static {
        QNAME_SOAP_RPC = new QName("http://www.w3.org/2002/06/soap-rpc", "rpc");
        QNAME_SOAP_RESULT = new QName("http://www.w3.org/2002/06/soap-rpc", "result");
        QNAME_SOAP_ENVELOPE = new QName("http://www.w3.org/2003/05/soap-envelope", "Envelope");
        QNAME_SOAP_BODY = new QName("http://www.w3.org/2003/05/soap-envelope", "Body");
        QNAME_SOAP_HEADER = new QName("http://www.w3.org/2003/05/soap-envelope", "Header");
        QNAME_ENVELOPE_ENCODINGSTYLE = new QName("http://www.w3.org/2003/05/soap-envelope", "encodingStyle");
        QNAME_SOAP_FAULT = new QName("http://www.w3.org/2003/05/soap-envelope", "Fault");
        QNAME_MUSTUNDERSTAND = new QName("http://www.w3.org/2003/05/soap-envelope", "mustUnderstand");
        QNAME_ROLE = new QName("http://www.w3.org/2003/05/soap-envelope", "role");
        QNAME_NOT_UNDERSTOOD = new QName("http://www.w3.org/2003/05/soap-envelope", "NotUnderstood");
        QNAME_FAULT_CODE = new QName("http://www.w3.org/2003/05/soap-envelope", "Code");
        QNAME_FAULT_SUBCODE = new QName("http://www.w3.org/2003/05/soap-envelope", "Subcode");
        QNAME_FAULT_VALUE = new QName("http://www.w3.org/2003/05/soap-envelope", "Value");
        QNAME_FAULT_REASON = new QName("http://www.w3.org/2003/05/soap-envelope", "Reason");
        QNAME_FAULT_NODE = new QName("http://www.w3.org/2003/05/soap-envelope", "Node");
        QNAME_FAULT_ROLE = new QName("http://www.w3.org/2003/05/soap-envelope", "Role");
        QNAME_FAULT_DETAIL = new QName("http://www.w3.org/2003/05/soap-envelope", "Detail");
        QNAME_FAULT_REASON_TEXT = new QName("http://www.w3.org/2003/05/soap-envelope", "Text");
        QNAME_UPGRADE = new QName("http://www.w3.org/2003/05/soap-envelope", "Upgrade");
        QNAME_UPGRADE_SUPPORTED_ENVELOPE = new QName("http://www.w3.org/2003/05/soap-envelope", "SupportedEnvelope");
        FAULT_CODE_MUST_UNDERSTAND = new QName("http://www.w3.org/2003/05/soap-envelope", "MustUnderstand");
        FAULT_CODE_MISUNDERSTOOD = new QName("http://www.w3.org/2003/05/soap-envelope", "Misunderstood");
        FAULT_CODE_VERSION_MISMATCH = new QName("http://www.w3.org/2003/05/soap-envelope", "VersionMismatch");
        FAULT_CODE_DATA_ENCODING_UNKNOWN = new QName("http://www.w3.org/2003/05/soap-envelope", "DataEncodingUnknown");
        FAULT_CODE_PROCEDURE_NOT_PRESENT = new QName("http://www.w3.org/2003/05/soap-envelope", "ProcedureNotPresent");
        FAULT_CODE_BAD_ARGUMENTS = new QName("http://www.w3.org/2003/05/soap-envelope", "BadArguments");
    }
}
