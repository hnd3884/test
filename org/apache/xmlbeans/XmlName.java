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

public interface XmlName extends XmlToken
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_Name");
    
    public static final class Factory
    {
        public static XmlName newInstance() {
            return (XmlName)XmlBeans.getContextTypeLoader().newInstance(XmlName.type, null);
        }
        
        public static XmlName newInstance(final XmlOptions options) {
            return (XmlName)XmlBeans.getContextTypeLoader().newInstance(XmlName.type, options);
        }
        
        public static XmlName newValue(final Object obj) {
            return (XmlName)XmlName.type.newValue(obj);
        }
        
        public static XmlName parse(final String s) throws XmlException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(s, XmlName.type, null);
        }
        
        public static XmlName parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(s, XmlName.type, options);
        }
        
        public static XmlName parse(final File f) throws XmlException, IOException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(f, XmlName.type, null);
        }
        
        public static XmlName parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(f, XmlName.type, options);
        }
        
        public static XmlName parse(final URL u) throws XmlException, IOException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(u, XmlName.type, null);
        }
        
        public static XmlName parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(u, XmlName.type, options);
        }
        
        public static XmlName parse(final InputStream is) throws XmlException, IOException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(is, XmlName.type, null);
        }
        
        public static XmlName parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(is, XmlName.type, options);
        }
        
        public static XmlName parse(final Reader r) throws XmlException, IOException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(r, XmlName.type, null);
        }
        
        public static XmlName parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(r, XmlName.type, options);
        }
        
        public static XmlName parse(final Node node) throws XmlException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(node, XmlName.type, null);
        }
        
        public static XmlName parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(node, XmlName.type, options);
        }
        
        @Deprecated
        public static XmlName parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(xis, XmlName.type, null);
        }
        
        @Deprecated
        public static XmlName parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(xis, XmlName.type, options);
        }
        
        public static XmlName parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(xsr, XmlName.type, null);
        }
        
        public static XmlName parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlName)XmlBeans.getContextTypeLoader().parse(xsr, XmlName.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlName.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlName.type, options);
        }
        
        private Factory() {
        }
    }
}
