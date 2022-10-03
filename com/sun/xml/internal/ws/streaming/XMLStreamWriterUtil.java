package com.sun.xml.internal.ws.streaming;

import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.encoding.HasEncoding;
import com.sun.istack.internal.Nullable;
import javax.xml.stream.XMLStreamException;
import java.util.Map;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import java.io.OutputStream;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterUtil
{
    private XMLStreamWriterUtil() {
    }
    
    @Nullable
    public static OutputStream getOutputStream(final XMLStreamWriter writer) throws XMLStreamException {
        Object obj = null;
        final XMLStreamWriter xmlStreamWriter = (writer instanceof XMLStreamWriterFactory.HasEncodingWriter) ? ((XMLStreamWriterFactory.HasEncodingWriter)writer).getWriter() : writer;
        if (xmlStreamWriter instanceof Map) {
            obj = ((Map)xmlStreamWriter).get("sjsxp-outputstream");
        }
        if (obj == null) {
            try {
                obj = writer.getProperty("com.ctc.wstx.outputUnderlyingStream");
            }
            catch (final Exception ex) {}
        }
        if (obj == null) {
            try {
                obj = writer.getProperty("http://java.sun.com/xml/stream/properties/outputstream");
            }
            catch (final Exception ex2) {}
        }
        if (obj != null) {
            writer.writeCharacters("");
            writer.flush();
            return (OutputStream)obj;
        }
        return null;
    }
    
    @Nullable
    public static String getEncoding(final XMLStreamWriter writer) {
        return (writer instanceof HasEncoding) ? ((HasEncoding)writer).getEncoding() : null;
    }
    
    public static String encodeQName(final XMLStreamWriter writer, final QName qname, final PrefixFactory prefixFactory) {
        try {
            final String namespaceURI = qname.getNamespaceURI();
            final String localPart = qname.getLocalPart();
            if (namespaceURI == null || namespaceURI.equals("")) {
                return localPart;
            }
            String prefix = writer.getPrefix(namespaceURI);
            if (prefix == null) {
                prefix = prefixFactory.getPrefix(namespaceURI);
                writer.writeNamespace(prefix, namespaceURI);
            }
            return prefix + ":" + localPart;
        }
        catch (final XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
}
