package org.apache.xmlbeans;

import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;

public interface XmlUnsignedLong extends XmlNonNegativeInteger
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_unsignedLong");
    
    public static final class Factory
    {
        public static XmlUnsignedLong newInstance() {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().newInstance(XmlUnsignedLong.type, null);
        }
        
        public static XmlUnsignedLong newInstance(final XmlOptions options) {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().newInstance(XmlUnsignedLong.type, options);
        }
        
        public static XmlUnsignedLong newValue(final Object obj) {
            return (XmlUnsignedLong)XmlUnsignedLong.type.newValue(obj);
        }
        
        public static XmlUnsignedLong parse(final String s) throws XmlException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(s, XmlUnsignedLong.type, null);
        }
        
        public static XmlUnsignedLong parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(s, XmlUnsignedLong.type, options);
        }
        
        public static XmlUnsignedLong parse(final File f) throws XmlException, IOException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(f, XmlUnsignedLong.type, null);
        }
        
        public static XmlUnsignedLong parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(f, XmlUnsignedLong.type, options);
        }
        
        public static XmlUnsignedLong parse(final URL u) throws XmlException, IOException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(u, XmlUnsignedLong.type, null);
        }
        
        public static XmlUnsignedLong parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(u, XmlUnsignedLong.type, options);
        }
        
        public static XmlUnsignedLong parse(final InputStream is) throws XmlException, IOException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(is, XmlUnsignedLong.type, null);
        }
        
        public static XmlUnsignedLong parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(is, XmlUnsignedLong.type, options);
        }
        
        public static XmlUnsignedLong parse(final Reader r) throws XmlException, IOException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(r, XmlUnsignedLong.type, null);
        }
        
        public static XmlUnsignedLong parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(r, XmlUnsignedLong.type, options);
        }
        
        public static XmlUnsignedLong parse(final Node node) throws XmlException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(node, XmlUnsignedLong.type, null);
        }
        
        public static XmlUnsignedLong parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(node, XmlUnsignedLong.type, options);
        }
        
        @Deprecated
        public static XmlUnsignedLong parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(xis, XmlUnsignedLong.type, null);
        }
        
        @Deprecated
        public static XmlUnsignedLong parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(xis, XmlUnsignedLong.type, options);
        }
        
        public static XmlUnsignedLong parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(xsr, XmlUnsignedLong.type, null);
        }
        
        public static XmlUnsignedLong parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlUnsignedLong)XmlBeans.getContextTypeLoader().parse(xsr, XmlUnsignedLong.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlUnsignedLong.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlUnsignedLong.type, options);
        }
        
        private Factory() {
        }
    }
}
