package com.sun.xml.internal.ws.wsdl.parser;

import javax.xml.namespace.QName;

interface MIMEConstants
{
    public static final String NS_WSDL_MIME = "http://schemas.xmlsoap.org/wsdl/mime/";
    public static final QName QNAME_CONTENT = new QName("http://schemas.xmlsoap.org/wsdl/mime/", "content");
    public static final QName QNAME_MULTIPART_RELATED = new QName("http://schemas.xmlsoap.org/wsdl/mime/", "multipartRelated");
    public static final QName QNAME_PART = new QName("http://schemas.xmlsoap.org/wsdl/mime/", "part");
    public static final QName QNAME_MIME_XML = new QName("http://schemas.xmlsoap.org/wsdl/mime/", "mimeXml");
}
