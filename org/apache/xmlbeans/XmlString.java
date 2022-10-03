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

public interface XmlString extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_string");
    
    public static final class Factory
    {
        public static XmlString newInstance() {
            return (XmlString)XmlBeans.getContextTypeLoader().newInstance(XmlString.type, null);
        }
        
        public static XmlString newInstance(final XmlOptions options) {
            return (XmlString)XmlBeans.getContextTypeLoader().newInstance(XmlString.type, options);
        }
        
        public static XmlString newValue(final Object obj) {
            return (XmlString)XmlString.type.newValue(obj);
        }
        
        public static XmlString parse(final String s) throws XmlException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(s, XmlString.type, null);
        }
        
        public static XmlString parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(s, XmlString.type, options);
        }
        
        public static XmlString parse(final File f) throws XmlException, IOException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(f, XmlString.type, null);
        }
        
        public static XmlString parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(f, XmlString.type, options);
        }
        
        public static XmlString parse(final URL u) throws XmlException, IOException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(u, XmlString.type, null);
        }
        
        public static XmlString parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(u, XmlString.type, options);
        }
        
        public static XmlString parse(final InputStream is) throws XmlException, IOException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(is, XmlString.type, null);
        }
        
        public static XmlString parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(is, XmlString.type, options);
        }
        
        public static XmlString parse(final Reader r) throws XmlException, IOException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(r, XmlString.type, null);
        }
        
        public static XmlString parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(r, XmlString.type, options);
        }
        
        public static XmlString parse(final Node node) throws XmlException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(node, XmlString.type, null);
        }
        
        public static XmlString parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(node, XmlString.type, options);
        }
        
        @Deprecated
        public static XmlString parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(xis, XmlString.type, null);
        }
        
        @Deprecated
        public static XmlString parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(xis, XmlString.type, options);
        }
        
        public static XmlString parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(xsr, XmlString.type, null);
        }
        
        public static XmlString parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlString)XmlBeans.getContextTypeLoader().parse(xsr, XmlString.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlString.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlString.type, options);
        }
        
        private Factory() {
        }
    }
}
