package org.apache.axiom.soap;

public interface SOAPConstants
{
    public static final String SOAP_DEFAULT_NAMESPACE_PREFIX = "soapenv";
    public static final String SOAPENVELOPE_LOCAL_NAME = "Envelope";
    public static final String HEADER_LOCAL_NAME = "Header";
    public static final String BODY_LOCAL_NAME = "Body";
    public static final String BODY_NAMESPACE_PREFIX = "soapenv";
    public static final String BODY_FAULT_LOCAL_NAME = "Fault";
    public static final String ATTR_MUSTUNDERSTAND = "mustUnderstand";
    public static final String ATTR_MUSTUNDERSTAND_TRUE = "true";
    public static final String ATTR_MUSTUNDERSTAND_FALSE = "false";
    public static final String ATTR_MUSTUNDERSTAND_0 = "0";
    public static final String ATTR_MUSTUNDERSTAND_1 = "1";
    public static final String SOAPFAULT_LOCAL_NAME = "Fault";
    public static final String SOAPFAULT_DETAIL_LOCAL_NAME = "detail";
    public static final String SOAP_FAULT_DETAIL_EXCEPTION_ENTRY = "Exception";
    public static final String FAULT_CODE_VERSION_MISMATCH = "VersionMismatch";
    public static final String FAULT_CODE_MUST_UNDERSTAND = "MustUnderstand";
    public static final String FAULT_CODE_DATA_ENCODING_UNKNOWN = "DataEncodingUnknown";
    public static final String FAULT_CODE_SENDER = "";
    public static final String FAULT_CODE_RECEIVER = "";
    public static final String SOAPBODY_FIRST_CHILD_ELEMENT_QNAME = "org.apache.axiom.SOAPBodyFirstChildElementQName";
}
