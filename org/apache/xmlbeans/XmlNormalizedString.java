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

public interface XmlNormalizedString extends XmlString
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_normalizedString");
    
    public static final class Factory
    {
        public static XmlNormalizedString newInstance() {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().newInstance(XmlNormalizedString.type, null);
        }
        
        public static XmlNormalizedString newInstance(final XmlOptions options) {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().newInstance(XmlNormalizedString.type, options);
        }
        
        public static XmlNormalizedString newValue(final Object obj) {
            return (XmlNormalizedString)XmlNormalizedString.type.newValue(obj);
        }
        
        public static XmlNormalizedString parse(final String s) throws XmlException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(s, XmlNormalizedString.type, null);
        }
        
        public static XmlNormalizedString parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(s, XmlNormalizedString.type, options);
        }
        
        public static XmlNormalizedString parse(final File f) throws XmlException, IOException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(f, XmlNormalizedString.type, null);
        }
        
        public static XmlNormalizedString parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(f, XmlNormalizedString.type, options);
        }
        
        public static XmlNormalizedString parse(final URL u) throws XmlException, IOException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(u, XmlNormalizedString.type, null);
        }
        
        public static XmlNormalizedString parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(u, XmlNormalizedString.type, options);
        }
        
        public static XmlNormalizedString parse(final InputStream is) throws XmlException, IOException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(is, XmlNormalizedString.type, null);
        }
        
        public static XmlNormalizedString parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(is, XmlNormalizedString.type, options);
        }
        
        public static XmlNormalizedString parse(final Reader r) throws XmlException, IOException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(r, XmlNormalizedString.type, null);
        }
        
        public static XmlNormalizedString parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(r, XmlNormalizedString.type, options);
        }
        
        public static XmlNormalizedString parse(final Node node) throws XmlException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(node, XmlNormalizedString.type, null);
        }
        
        public static XmlNormalizedString parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(node, XmlNormalizedString.type, options);
        }
        
        @Deprecated
        public static XmlNormalizedString parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(xis, XmlNormalizedString.type, null);
        }
        
        @Deprecated
        public static XmlNormalizedString parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(xis, XmlNormalizedString.type, options);
        }
        
        public static XmlNormalizedString parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(xsr, XmlNormalizedString.type, null);
        }
        
        public static XmlNormalizedString parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlNormalizedString)XmlBeans.getContextTypeLoader().parse(xsr, XmlNormalizedString.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNormalizedString.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNormalizedString.type, options);
        }
        
        private Factory() {
        }
    }
}
