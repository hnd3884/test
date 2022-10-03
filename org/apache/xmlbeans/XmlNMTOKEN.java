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

public interface XmlNMTOKEN extends XmlToken
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_NMTOKEN");
    
    public static final class Factory
    {
        public static XmlNMTOKEN newInstance() {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().newInstance(XmlNMTOKEN.type, null);
        }
        
        public static XmlNMTOKEN newInstance(final XmlOptions options) {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().newInstance(XmlNMTOKEN.type, options);
        }
        
        public static XmlNMTOKEN newValue(final Object obj) {
            return (XmlNMTOKEN)XmlNMTOKEN.type.newValue(obj);
        }
        
        public static XmlNMTOKEN parse(final String s) throws XmlException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(s, XmlNMTOKEN.type, null);
        }
        
        public static XmlNMTOKEN parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(s, XmlNMTOKEN.type, options);
        }
        
        public static XmlNMTOKEN parse(final File f) throws XmlException, IOException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(f, XmlNMTOKEN.type, null);
        }
        
        public static XmlNMTOKEN parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(f, XmlNMTOKEN.type, options);
        }
        
        public static XmlNMTOKEN parse(final URL u) throws XmlException, IOException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(u, XmlNMTOKEN.type, null);
        }
        
        public static XmlNMTOKEN parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(u, XmlNMTOKEN.type, options);
        }
        
        public static XmlNMTOKEN parse(final InputStream is) throws XmlException, IOException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(is, XmlNMTOKEN.type, null);
        }
        
        public static XmlNMTOKEN parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(is, XmlNMTOKEN.type, options);
        }
        
        public static XmlNMTOKEN parse(final Reader r) throws XmlException, IOException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(r, XmlNMTOKEN.type, null);
        }
        
        public static XmlNMTOKEN parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(r, XmlNMTOKEN.type, options);
        }
        
        public static XmlNMTOKEN parse(final Node node) throws XmlException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(node, XmlNMTOKEN.type, null);
        }
        
        public static XmlNMTOKEN parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(node, XmlNMTOKEN.type, options);
        }
        
        @Deprecated
        public static XmlNMTOKEN parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(xis, XmlNMTOKEN.type, null);
        }
        
        @Deprecated
        public static XmlNMTOKEN parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(xis, XmlNMTOKEN.type, options);
        }
        
        public static XmlNMTOKEN parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(xsr, XmlNMTOKEN.type, null);
        }
        
        public static XmlNMTOKEN parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlNMTOKEN)XmlBeans.getContextTypeLoader().parse(xsr, XmlNMTOKEN.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNMTOKEN.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNMTOKEN.type, options);
        }
        
        private Factory() {
        }
    }
}
