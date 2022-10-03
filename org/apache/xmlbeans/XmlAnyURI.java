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

public interface XmlAnyURI extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_anyURI");
    
    public static final class Factory
    {
        public static XmlAnyURI newInstance() {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().newInstance(XmlAnyURI.type, null);
        }
        
        public static XmlAnyURI newInstance(final XmlOptions options) {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().newInstance(XmlAnyURI.type, options);
        }
        
        public static XmlAnyURI newValue(final Object obj) {
            return (XmlAnyURI)XmlAnyURI.type.newValue(obj);
        }
        
        public static XmlAnyURI parse(final String s) throws XmlException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(s, XmlAnyURI.type, null);
        }
        
        public static XmlAnyURI parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(s, XmlAnyURI.type, options);
        }
        
        public static XmlAnyURI parse(final File f) throws XmlException, IOException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(f, XmlAnyURI.type, null);
        }
        
        public static XmlAnyURI parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(f, XmlAnyURI.type, options);
        }
        
        public static XmlAnyURI parse(final URL u) throws XmlException, IOException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(u, XmlAnyURI.type, null);
        }
        
        public static XmlAnyURI parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(u, XmlAnyURI.type, options);
        }
        
        public static XmlAnyURI parse(final InputStream is) throws XmlException, IOException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(is, XmlAnyURI.type, null);
        }
        
        public static XmlAnyURI parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(is, XmlAnyURI.type, options);
        }
        
        public static XmlAnyURI parse(final Reader r) throws XmlException, IOException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(r, XmlAnyURI.type, null);
        }
        
        public static XmlAnyURI parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(r, XmlAnyURI.type, options);
        }
        
        public static XmlAnyURI parse(final Node node) throws XmlException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(node, XmlAnyURI.type, null);
        }
        
        public static XmlAnyURI parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(node, XmlAnyURI.type, options);
        }
        
        @Deprecated
        public static XmlAnyURI parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(xis, XmlAnyURI.type, null);
        }
        
        @Deprecated
        public static XmlAnyURI parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(xis, XmlAnyURI.type, options);
        }
        
        public static XmlAnyURI parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(xsr, XmlAnyURI.type, null);
        }
        
        public static XmlAnyURI parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlAnyURI)XmlBeans.getContextTypeLoader().parse(xsr, XmlAnyURI.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlAnyURI.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlAnyURI.type, options);
        }
        
        private Factory() {
        }
    }
}
