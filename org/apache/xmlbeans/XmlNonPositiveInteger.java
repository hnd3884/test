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

public interface XmlNonPositiveInteger extends XmlInteger
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_nonPositiveInteger");
    
    public static final class Factory
    {
        public static XmlNonPositiveInteger newInstance() {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().newInstance(XmlNonPositiveInteger.type, null);
        }
        
        public static XmlNonPositiveInteger newInstance(final XmlOptions options) {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().newInstance(XmlNonPositiveInteger.type, options);
        }
        
        public static XmlNonPositiveInteger newValue(final Object obj) {
            return (XmlNonPositiveInteger)XmlNonPositiveInteger.type.newValue(obj);
        }
        
        public static XmlNonPositiveInteger parse(final String s) throws XmlException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(s, XmlNonPositiveInteger.type, null);
        }
        
        public static XmlNonPositiveInteger parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(s, XmlNonPositiveInteger.type, options);
        }
        
        public static XmlNonPositiveInteger parse(final File f) throws XmlException, IOException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(f, XmlNonPositiveInteger.type, null);
        }
        
        public static XmlNonPositiveInteger parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(f, XmlNonPositiveInteger.type, options);
        }
        
        public static XmlNonPositiveInteger parse(final URL u) throws XmlException, IOException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(u, XmlNonPositiveInteger.type, null);
        }
        
        public static XmlNonPositiveInteger parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(u, XmlNonPositiveInteger.type, options);
        }
        
        public static XmlNonPositiveInteger parse(final InputStream is) throws XmlException, IOException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(is, XmlNonPositiveInteger.type, null);
        }
        
        public static XmlNonPositiveInteger parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(is, XmlNonPositiveInteger.type, options);
        }
        
        public static XmlNonPositiveInteger parse(final Reader r) throws XmlException, IOException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(r, XmlNonPositiveInteger.type, null);
        }
        
        public static XmlNonPositiveInteger parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(r, XmlNonPositiveInteger.type, options);
        }
        
        public static XmlNonPositiveInteger parse(final Node node) throws XmlException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(node, XmlNonPositiveInteger.type, null);
        }
        
        public static XmlNonPositiveInteger parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(node, XmlNonPositiveInteger.type, options);
        }
        
        @Deprecated
        public static XmlNonPositiveInteger parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(xis, XmlNonPositiveInteger.type, null);
        }
        
        @Deprecated
        public static XmlNonPositiveInteger parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(xis, XmlNonPositiveInteger.type, options);
        }
        
        public static XmlNonPositiveInteger parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(xsr, XmlNonPositiveInteger.type, null);
        }
        
        public static XmlNonPositiveInteger parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlNonPositiveInteger)XmlBeans.getContextTypeLoader().parse(xsr, XmlNonPositiveInteger.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNonPositiveInteger.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNonPositiveInteger.type, options);
        }
        
        private Factory() {
        }
    }
}
