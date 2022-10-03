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
import javax.xml.namespace.QName;

public interface XmlQName extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_QName");
    
    QName getQNameValue();
    
    void setQNameValue(final QName p0);
    
    @Deprecated
    QName qNameValue();
    
    @Deprecated
    void set(final QName p0);
    
    public static final class Factory
    {
        public static XmlQName newInstance() {
            return (XmlQName)XmlBeans.getContextTypeLoader().newInstance(XmlQName.type, null);
        }
        
        public static XmlQName newInstance(final XmlOptions options) {
            return (XmlQName)XmlBeans.getContextTypeLoader().newInstance(XmlQName.type, options);
        }
        
        public static XmlQName newValue(final Object obj) {
            return (XmlQName)XmlQName.type.newValue(obj);
        }
        
        public static XmlQName parse(final String s) throws XmlException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(s, XmlQName.type, null);
        }
        
        public static XmlQName parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(s, XmlQName.type, options);
        }
        
        public static XmlQName parse(final File f) throws XmlException, IOException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(f, XmlQName.type, null);
        }
        
        public static XmlQName parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(f, XmlQName.type, options);
        }
        
        public static XmlQName parse(final URL u) throws XmlException, IOException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(u, XmlQName.type, null);
        }
        
        public static XmlQName parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(u, XmlQName.type, options);
        }
        
        public static XmlQName parse(final InputStream is) throws XmlException, IOException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(is, XmlQName.type, null);
        }
        
        public static XmlQName parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(is, XmlQName.type, options);
        }
        
        public static XmlQName parse(final Reader r) throws XmlException, IOException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(r, XmlQName.type, null);
        }
        
        public static XmlQName parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(r, XmlQName.type, options);
        }
        
        public static XmlQName parse(final Node node) throws XmlException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(node, XmlQName.type, null);
        }
        
        public static XmlQName parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(node, XmlQName.type, options);
        }
        
        @Deprecated
        public static XmlQName parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(xis, XmlQName.type, null);
        }
        
        @Deprecated
        public static XmlQName parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(xis, XmlQName.type, options);
        }
        
        public static XmlQName parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(xsr, XmlQName.type, null);
        }
        
        public static XmlQName parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlQName)XmlBeans.getContextTypeLoader().parse(xsr, XmlQName.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlQName.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlQName.type, options);
        }
        
        private Factory() {
        }
    }
}
