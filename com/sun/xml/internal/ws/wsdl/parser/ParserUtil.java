package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public class ParserUtil
{
    public static String getAttribute(final XMLStreamReader reader, final String name) {
        return reader.getAttributeValue(null, name);
    }
    
    public static String getAttribute(final XMLStreamReader reader, final String nsUri, final String name) {
        return reader.getAttributeValue(nsUri, name);
    }
    
    public static String getAttribute(final XMLStreamReader reader, final QName name) {
        return reader.getAttributeValue(name.getNamespaceURI(), name.getLocalPart());
    }
    
    public static QName getQName(final XMLStreamReader reader, final String tag) {
        final String localName = XmlUtil.getLocalPart(tag);
        final String pfix = XmlUtil.getPrefix(tag);
        final String uri = reader.getNamespaceURI(fixNull(pfix));
        return new QName(uri, localName);
    }
    
    public static String getMandatoryNonEmptyAttribute(final XMLStreamReader reader, final String name) {
        final String value = reader.getAttributeValue(null, name);
        if (value == null) {
            failWithLocalName("client.missing.attribute", reader, name);
        }
        else if (value.equals("")) {
            failWithLocalName("client.invalidAttributeValue", reader, name);
        }
        return value;
    }
    
    public static void failWithFullName(final String key, final XMLStreamReader reader) {
    }
    
    public static void failWithLocalName(final String key, final XMLStreamReader reader) {
    }
    
    public static void failWithLocalName(final String key, final XMLStreamReader reader, final String arg) {
    }
    
    @NotNull
    private static String fixNull(@Nullable final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
}
