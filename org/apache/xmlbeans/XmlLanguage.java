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

public interface XmlLanguage extends XmlToken
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_language");
    
    public static final class Factory
    {
        public static XmlLanguage newInstance() {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().newInstance(XmlLanguage.type, null);
        }
        
        public static XmlLanguage newInstance(final XmlOptions options) {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().newInstance(XmlLanguage.type, options);
        }
        
        public static XmlLanguage newValue(final Object obj) {
            return (XmlLanguage)XmlLanguage.type.newValue(obj);
        }
        
        public static XmlLanguage parse(final String s) throws XmlException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(s, XmlLanguage.type, null);
        }
        
        public static XmlLanguage parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(s, XmlLanguage.type, options);
        }
        
        public static XmlLanguage parse(final File f) throws XmlException, IOException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(f, XmlLanguage.type, null);
        }
        
        public static XmlLanguage parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(f, XmlLanguage.type, options);
        }
        
        public static XmlLanguage parse(final URL u) throws XmlException, IOException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(u, XmlLanguage.type, null);
        }
        
        public static XmlLanguage parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(u, XmlLanguage.type, options);
        }
        
        public static XmlLanguage parse(final InputStream is) throws XmlException, IOException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(is, XmlLanguage.type, null);
        }
        
        public static XmlLanguage parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(is, XmlLanguage.type, options);
        }
        
        public static XmlLanguage parse(final Reader r) throws XmlException, IOException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(r, XmlLanguage.type, null);
        }
        
        public static XmlLanguage parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(r, XmlLanguage.type, options);
        }
        
        public static XmlLanguage parse(final Node node) throws XmlException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(node, XmlLanguage.type, null);
        }
        
        public static XmlLanguage parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(node, XmlLanguage.type, options);
        }
        
        @Deprecated
        public static XmlLanguage parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(xis, XmlLanguage.type, null);
        }
        
        @Deprecated
        public static XmlLanguage parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(xis, XmlLanguage.type, options);
        }
        
        public static XmlLanguage parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(xsr, XmlLanguage.type, null);
        }
        
        public static XmlLanguage parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlLanguage)XmlBeans.getContextTypeLoader().parse(xsr, XmlLanguage.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlLanguage.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlLanguage.type, options);
        }
        
        private Factory() {
        }
    }
}
