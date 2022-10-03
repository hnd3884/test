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

public interface XmlNegativeInteger extends XmlNonPositiveInteger
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_negativeInteger");
    
    public static final class Factory
    {
        public static XmlNegativeInteger newInstance() {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().newInstance(XmlNegativeInteger.type, null);
        }
        
        public static XmlNegativeInteger newInstance(final XmlOptions options) {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().newInstance(XmlNegativeInteger.type, options);
        }
        
        public static XmlNegativeInteger newValue(final Object obj) {
            return (XmlNegativeInteger)XmlNegativeInteger.type.newValue(obj);
        }
        
        public static XmlNegativeInteger parse(final String s) throws XmlException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(s, XmlNegativeInteger.type, null);
        }
        
        public static XmlNegativeInteger parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(s, XmlNegativeInteger.type, options);
        }
        
        public static XmlNegativeInteger parse(final File f) throws XmlException, IOException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(f, XmlNegativeInteger.type, null);
        }
        
        public static XmlNegativeInteger parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(f, XmlNegativeInteger.type, options);
        }
        
        public static XmlNegativeInteger parse(final URL u) throws XmlException, IOException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(u, XmlNegativeInteger.type, null);
        }
        
        public static XmlNegativeInteger parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(u, XmlNegativeInteger.type, options);
        }
        
        public static XmlNegativeInteger parse(final InputStream is) throws XmlException, IOException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(is, XmlNegativeInteger.type, null);
        }
        
        public static XmlNegativeInteger parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(is, XmlNegativeInteger.type, options);
        }
        
        public static XmlNegativeInteger parse(final Reader r) throws XmlException, IOException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(r, XmlNegativeInteger.type, null);
        }
        
        public static XmlNegativeInteger parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(r, XmlNegativeInteger.type, options);
        }
        
        public static XmlNegativeInteger parse(final Node node) throws XmlException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(node, XmlNegativeInteger.type, null);
        }
        
        public static XmlNegativeInteger parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(node, XmlNegativeInteger.type, options);
        }
        
        @Deprecated
        public static XmlNegativeInteger parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(xis, XmlNegativeInteger.type, null);
        }
        
        @Deprecated
        public static XmlNegativeInteger parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(xis, XmlNegativeInteger.type, options);
        }
        
        public static XmlNegativeInteger parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(xsr, XmlNegativeInteger.type, null);
        }
        
        public static XmlNegativeInteger parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlNegativeInteger)XmlBeans.getContextTypeLoader().parse(xsr, XmlNegativeInteger.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNegativeInteger.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNegativeInteger.type, options);
        }
        
        private Factory() {
        }
    }
}
