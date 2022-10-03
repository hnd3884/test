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

public interface XmlToken extends XmlNormalizedString
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_token");
    
    public static final class Factory
    {
        public static XmlToken newInstance() {
            return (XmlToken)XmlBeans.getContextTypeLoader().newInstance(XmlToken.type, null);
        }
        
        public static XmlToken newInstance(final XmlOptions options) {
            return (XmlToken)XmlBeans.getContextTypeLoader().newInstance(XmlToken.type, options);
        }
        
        public static XmlToken newValue(final Object obj) {
            return (XmlToken)XmlToken.type.newValue(obj);
        }
        
        public static XmlToken parse(final String s) throws XmlException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(s, XmlToken.type, null);
        }
        
        public static XmlToken parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(s, XmlToken.type, options);
        }
        
        public static XmlToken parse(final File f) throws XmlException, IOException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(f, XmlToken.type, null);
        }
        
        public static XmlToken parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(f, XmlToken.type, options);
        }
        
        public static XmlToken parse(final URL u) throws XmlException, IOException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(u, XmlToken.type, null);
        }
        
        public static XmlToken parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(u, XmlToken.type, options);
        }
        
        public static XmlToken parse(final InputStream is) throws XmlException, IOException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(is, XmlToken.type, null);
        }
        
        public static XmlToken parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(is, XmlToken.type, options);
        }
        
        public static XmlToken parse(final Reader r) throws XmlException, IOException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(r, XmlToken.type, null);
        }
        
        public static XmlToken parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(r, XmlToken.type, options);
        }
        
        public static XmlToken parse(final Node node) throws XmlException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(node, XmlToken.type, null);
        }
        
        public static XmlToken parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(node, XmlToken.type, options);
        }
        
        @Deprecated
        public static XmlToken parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(xis, XmlToken.type, null);
        }
        
        @Deprecated
        public static XmlToken parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(xis, XmlToken.type, options);
        }
        
        public static XmlToken parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(xsr, XmlToken.type, null);
        }
        
        public static XmlToken parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlToken)XmlBeans.getContextTypeLoader().parse(xsr, XmlToken.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlToken.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlToken.type, options);
        }
        
        private Factory() {
        }
    }
}
