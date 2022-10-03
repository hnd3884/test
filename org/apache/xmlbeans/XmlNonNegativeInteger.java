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

public interface XmlNonNegativeInteger extends XmlInteger
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_nonNegativeInteger");
    
    public static final class Factory
    {
        public static XmlNonNegativeInteger newInstance() {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().newInstance(XmlNonNegativeInteger.type, null);
        }
        
        public static XmlNonNegativeInteger newInstance(final XmlOptions options) {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().newInstance(XmlNonNegativeInteger.type, options);
        }
        
        public static XmlNonNegativeInteger newValue(final Object obj) {
            return (XmlNonNegativeInteger)XmlNonNegativeInteger.type.newValue(obj);
        }
        
        public static XmlNonNegativeInteger parse(final String s) throws XmlException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(s, XmlNonNegativeInteger.type, null);
        }
        
        public static XmlNonNegativeInteger parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(s, XmlNonNegativeInteger.type, options);
        }
        
        public static XmlNonNegativeInteger parse(final File f) throws XmlException, IOException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(f, XmlNonNegativeInteger.type, null);
        }
        
        public static XmlNonNegativeInteger parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(f, XmlNonNegativeInteger.type, options);
        }
        
        public static XmlNonNegativeInteger parse(final URL u) throws XmlException, IOException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(u, XmlNonNegativeInteger.type, null);
        }
        
        public static XmlNonNegativeInteger parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(u, XmlNonNegativeInteger.type, options);
        }
        
        public static XmlNonNegativeInteger parse(final InputStream is) throws XmlException, IOException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(is, XmlNonNegativeInteger.type, null);
        }
        
        public static XmlNonNegativeInteger parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(is, XmlNonNegativeInteger.type, options);
        }
        
        public static XmlNonNegativeInteger parse(final Reader r) throws XmlException, IOException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(r, XmlNonNegativeInteger.type, null);
        }
        
        public static XmlNonNegativeInteger parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(r, XmlNonNegativeInteger.type, options);
        }
        
        public static XmlNonNegativeInteger parse(final Node node) throws XmlException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(node, XmlNonNegativeInteger.type, null);
        }
        
        public static XmlNonNegativeInteger parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(node, XmlNonNegativeInteger.type, options);
        }
        
        @Deprecated
        public static XmlNonNegativeInteger parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(xis, XmlNonNegativeInteger.type, null);
        }
        
        @Deprecated
        public static XmlNonNegativeInteger parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(xis, XmlNonNegativeInteger.type, options);
        }
        
        public static XmlNonNegativeInteger parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(xsr, XmlNonNegativeInteger.type, null);
        }
        
        public static XmlNonNegativeInteger parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlNonNegativeInteger)XmlBeans.getContextTypeLoader().parse(xsr, XmlNonNegativeInteger.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNonNegativeInteger.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNonNegativeInteger.type, options);
        }
        
        private Factory() {
        }
    }
}
