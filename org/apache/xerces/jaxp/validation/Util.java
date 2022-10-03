package org.apache.xerces.jaxp.validation;

import org.xml.sax.SAXParseException;
import org.apache.xerces.xni.parser.XMLParseException;
import org.xml.sax.SAXException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import javax.xml.transform.stream.StreamSource;

final class Util
{
    public static final XMLInputSource toXMLInputSource(final StreamSource streamSource) {
        if (streamSource.getReader() != null) {
            return new XMLInputSource(streamSource.getPublicId(), streamSource.getSystemId(), streamSource.getSystemId(), streamSource.getReader(), null);
        }
        if (streamSource.getInputStream() != null) {
            return new XMLInputSource(streamSource.getPublicId(), streamSource.getSystemId(), streamSource.getSystemId(), streamSource.getInputStream(), null);
        }
        return new XMLInputSource(streamSource.getPublicId(), streamSource.getSystemId(), streamSource.getSystemId());
    }
    
    public static SAXException toSAXException(final XNIException ex) {
        if (ex instanceof XMLParseException) {
            return toSAXParseException((XMLParseException)ex);
        }
        if (ex.getException() instanceof SAXException) {
            return (SAXException)ex.getException();
        }
        return new SAXException(ex.getMessage(), ex.getException());
    }
    
    public static SAXParseException toSAXParseException(final XMLParseException ex) {
        if (ex.getException() instanceof SAXParseException) {
            return (SAXParseException)ex.getException();
        }
        return new SAXParseException(ex.getMessage(), ex.getPublicId(), ex.getExpandedSystemId(), ex.getLineNumber(), ex.getColumnNumber(), ex.getException());
    }
}
