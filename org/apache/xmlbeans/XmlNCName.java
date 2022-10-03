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

public interface XmlNCName extends XmlName
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_NCName");
    
    public static final class Factory
    {
        public static XmlNCName newInstance() {
            return (XmlNCName)XmlBeans.getContextTypeLoader().newInstance(XmlNCName.type, null);
        }
        
        public static XmlNCName newInstance(final XmlOptions options) {
            return (XmlNCName)XmlBeans.getContextTypeLoader().newInstance(XmlNCName.type, options);
        }
        
        public static XmlNCName newValue(final Object obj) {
            return (XmlNCName)XmlNCName.type.newValue(obj);
        }
        
        public static XmlNCName parse(final String s) throws XmlException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(s, XmlNCName.type, null);
        }
        
        public static XmlNCName parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(s, XmlNCName.type, options);
        }
        
        public static XmlNCName parse(final File f) throws XmlException, IOException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(f, XmlNCName.type, null);
        }
        
        public static XmlNCName parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(f, XmlNCName.type, options);
        }
        
        public static XmlNCName parse(final URL u) throws XmlException, IOException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(u, XmlNCName.type, null);
        }
        
        public static XmlNCName parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(u, XmlNCName.type, options);
        }
        
        public static XmlNCName parse(final InputStream is) throws XmlException, IOException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(is, XmlNCName.type, null);
        }
        
        public static XmlNCName parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(is, XmlNCName.type, options);
        }
        
        public static XmlNCName parse(final Reader r) throws XmlException, IOException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(r, XmlNCName.type, null);
        }
        
        public static XmlNCName parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(r, XmlNCName.type, options);
        }
        
        public static XmlNCName parse(final Node node) throws XmlException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(node, XmlNCName.type, null);
        }
        
        public static XmlNCName parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(node, XmlNCName.type, options);
        }
        
        @Deprecated
        public static XmlNCName parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(xis, XmlNCName.type, null);
        }
        
        @Deprecated
        public static XmlNCName parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(xis, XmlNCName.type, options);
        }
        
        public static XmlNCName parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(xsr, XmlNCName.type, null);
        }
        
        public static XmlNCName parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlNCName)XmlBeans.getContextTypeLoader().parse(xsr, XmlNCName.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNCName.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNCName.type, options);
        }
        
        private Factory() {
        }
    }
}
