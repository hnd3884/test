package org.apache.axiom.soap;

import javax.xml.namespace.QName;

public interface SOAP12Constants extends SOAPConstants
{
    public static final String SOAP_ENVELOPE_NAMESPACE_URI = "http://www.w3.org/2003/05/soap-envelope";
    public static final String SOAP_ENCODING_NAMESPACE_URI = "http://www.w3.org/2003/05/soap-encoding";
    public static final String SOAP_ROLE = "role";
    public static final String SOAP_RELAY = "relay";
    public static final String SOAP_FAULT_CODE_LOCAL_NAME = "Code";
    public static final String SOAP_FAULT_SUB_CODE_LOCAL_NAME = "Subcode";
    public static final String SOAP_FAULT_VALUE_LOCAL_NAME = "Value";
    public static final String SOAP_FAULT_VALUE_VERSION_MISMATCH = "VersionMismatch";
    public static final String SOAP_FAULT_VALUE_MUST_UNDERSTAND = "MustUnderstand";
    public static final String SOAP_FAULT_VALUE_DATA_ENCODING_UKNOWN = "DataEncodingUnknown";
    public static final String SOAP_FAULT_VALUE_SENDER = "Sender";
    public static final String SOAP_FAULT_VALUE_RECEIVER = "Receiver";
    public static final String SOAP_FAULT_REASON_LOCAL_NAME = "Reason";
    public static final String SOAP_FAULT_TEXT_LOCAL_NAME = "Text";
    public static final String SOAP_FAULT_TEXT_LANG_ATTR_LOCAL_NAME = "lang";
    public static final String SOAP_FAULT_TEXT_LANG_ATTR_NS_URI = "http://www.w3.org/XML/1998/namespace";
    public static final String SOAP_FAULT_TEXT_LANG_ATTR_NS_PREFIX = "xml";
    public static final String SOAP_FAULT_NODE_LOCAL_NAME = "Node";
    public static final String SOAP_FAULT_DETAIL_LOCAL_NAME = "Detail";
    public static final String SOAP_FAULT_ROLE_LOCAL_NAME = "Role";
    public static final String SOAP_12_CONTENT_TYPE = "application/soap+xml";
    public static final String FAULT_CODE_SENDER = "Sender";
    public static final String FAULT_CODE_RECEIVER = "Receiver";
    public static final String SOAP_ROLE_NEXT = "http://www.w3.org/2003/05/soap-envelope/role/next";
    public static final String SOAP_ROLE_NONE = "http://www.w3.org/2003/05/soap-envelope/role/none";
    public static final String SOAP_ROLE_ULTIMATE_RECEIVER = "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver";
    public static final QName QNAME_ROLE = new QName("http://www.w3.org/2003/05/soap-envelope", "role");
    public static final QName QNAME_RELAY = new QName("http://www.w3.org/2003/05/soap-envelope", "relay");
    public static final QName QNAME_MU_FAULTCODE = new QName("http://www.w3.org/2003/05/soap-envelope", "MustUnderstand");
    public static final QName QNAME_SENDER_FAULTCODE = new QName("http://www.w3.org/2003/05/soap-envelope", "Sender");
    public static final QName QNAME_RECEIVER_FAULTCODE = new QName("http://www.w3.org/2003/05/soap-envelope", "Receiver");
    public static final QName QNAME_FAULT_REASON = new QName("http://www.w3.org/2003/05/soap-envelope", "Reason");
    public static final QName QNAME_FAULT_CODE = new QName("http://www.w3.org/2003/05/soap-envelope", "Code");
    public static final QName QNAME_FAULT_NODE = new QName("http://www.w3.org/2003/05/soap-envelope", "Node");
    public static final QName QNAME_FAULT_DETAIL = new QName("http://www.w3.org/2003/05/soap-envelope", "Detail");
    public static final QName QNAME_FAULT_ROLE = new QName("http://www.w3.org/2003/05/soap-envelope", "Role");
    public static final QName QNAME_FAULT_VALUE = new QName("http://www.w3.org/2003/05/soap-envelope", "Value");
    public static final QName QNAME_FAULT_SUBCODE = new QName("http://www.w3.org/2003/05/soap-envelope", "Subcode");
    public static final QName QNAME_FAULT_TEXT = new QName("http://www.w3.org/2003/05/soap-envelope", "Text");
}
