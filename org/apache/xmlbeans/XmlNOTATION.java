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

public interface XmlNOTATION extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_NOTATION");
    
    public static final class Factory
    {
        public static XmlNOTATION newInstance() {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().newInstance(XmlNOTATION.type, null);
        }
        
        public static XmlNOTATION newInstance(final XmlOptions options) {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().newInstance(XmlNOTATION.type, options);
        }
        
        public static XmlNOTATION newValue(final Object obj) {
            return (XmlNOTATION)XmlNOTATION.type.newValue(obj);
        }
        
        public static XmlNOTATION parse(final String s) throws XmlException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(s, XmlNOTATION.type, null);
        }
        
        public static XmlNOTATION parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(s, XmlNOTATION.type, options);
        }
        
        public static XmlNOTATION parse(final File f) throws XmlException, IOException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(f, XmlNOTATION.type, null);
        }
        
        public static XmlNOTATION parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(f, XmlNOTATION.type, options);
        }
        
        public static XmlNOTATION parse(final URL u) throws XmlException, IOException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(u, XmlNOTATION.type, null);
        }
        
        public static XmlNOTATION parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(u, XmlNOTATION.type, options);
        }
        
        public static XmlNOTATION parse(final InputStream is) throws XmlException, IOException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(is, XmlNOTATION.type, null);
        }
        
        public static XmlNOTATION parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(is, XmlNOTATION.type, options);
        }
        
        public static XmlNOTATION parse(final Reader r) throws XmlException, IOException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(r, XmlNOTATION.type, null);
        }
        
        public static XmlNOTATION parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(r, XmlNOTATION.type, options);
        }
        
        public static XmlNOTATION parse(final Node node) throws XmlException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(node, XmlNOTATION.type, null);
        }
        
        public static XmlNOTATION parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(node, XmlNOTATION.type, options);
        }
        
        @Deprecated
        public static XmlNOTATION parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(xis, XmlNOTATION.type, null);
        }
        
        @Deprecated
        public static XmlNOTATION parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(xis, XmlNOTATION.type, options);
        }
        
        public static XmlNOTATION parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(xsr, XmlNOTATION.type, null);
        }
        
        public static XmlNOTATION parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlNOTATION)XmlBeans.getContextTypeLoader().parse(xsr, XmlNOTATION.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNOTATION.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNOTATION.type, options);
        }
        
        private Factory() {
        }
    }
}
