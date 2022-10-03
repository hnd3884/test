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

public interface XmlPositiveInteger extends XmlNonNegativeInteger
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_positiveInteger");
    
    public static final class Factory
    {
        public static XmlPositiveInteger newInstance() {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().newInstance(XmlPositiveInteger.type, null);
        }
        
        public static XmlPositiveInteger newInstance(final XmlOptions options) {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().newInstance(XmlPositiveInteger.type, options);
        }
        
        public static XmlPositiveInteger newValue(final Object obj) {
            return (XmlPositiveInteger)XmlPositiveInteger.type.newValue(obj);
        }
        
        public static XmlPositiveInteger parse(final String s) throws XmlException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(s, XmlPositiveInteger.type, null);
        }
        
        public static XmlPositiveInteger parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(s, XmlPositiveInteger.type, options);
        }
        
        public static XmlPositiveInteger parse(final File f) throws XmlException, IOException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(f, XmlPositiveInteger.type, null);
        }
        
        public static XmlPositiveInteger parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(f, XmlPositiveInteger.type, options);
        }
        
        public static XmlPositiveInteger parse(final URL u) throws XmlException, IOException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(u, XmlPositiveInteger.type, null);
        }
        
        public static XmlPositiveInteger parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(u, XmlPositiveInteger.type, options);
        }
        
        public static XmlPositiveInteger parse(final InputStream is) throws XmlException, IOException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(is, XmlPositiveInteger.type, null);
        }
        
        public static XmlPositiveInteger parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(is, XmlPositiveInteger.type, options);
        }
        
        public static XmlPositiveInteger parse(final Reader r) throws XmlException, IOException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(r, XmlPositiveInteger.type, null);
        }
        
        public static XmlPositiveInteger parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(r, XmlPositiveInteger.type, options);
        }
        
        public static XmlPositiveInteger parse(final Node node) throws XmlException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(node, XmlPositiveInteger.type, null);
        }
        
        public static XmlPositiveInteger parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(node, XmlPositiveInteger.type, options);
        }
        
        @Deprecated
        public static XmlPositiveInteger parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(xis, XmlPositiveInteger.type, null);
        }
        
        @Deprecated
        public static XmlPositiveInteger parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(xis, XmlPositiveInteger.type, options);
        }
        
        public static XmlPositiveInteger parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(xsr, XmlPositiveInteger.type, null);
        }
        
        public static XmlPositiveInteger parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlPositiveInteger)XmlBeans.getContextTypeLoader().parse(xsr, XmlPositiveInteger.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlPositiveInteger.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlPositiveInteger.type, options);
        }
        
        private Factory() {
        }
    }
}
