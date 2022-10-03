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

public interface XmlLong extends XmlInteger
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_long");
    
    long getLongValue();
    
    void setLongValue(final long p0);
    
    @Deprecated
    long longValue();
    
    @Deprecated
    void set(final long p0);
    
    public static final class Factory
    {
        public static XmlLong newInstance() {
            return (XmlLong)XmlBeans.getContextTypeLoader().newInstance(XmlLong.type, null);
        }
        
        public static XmlLong newInstance(final XmlOptions options) {
            return (XmlLong)XmlBeans.getContextTypeLoader().newInstance(XmlLong.type, options);
        }
        
        public static XmlLong newValue(final Object obj) {
            return (XmlLong)XmlLong.type.newValue(obj);
        }
        
        public static XmlLong parse(final String s) throws XmlException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(s, XmlLong.type, null);
        }
        
        public static XmlLong parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(s, XmlLong.type, options);
        }
        
        public static XmlLong parse(final File f) throws XmlException, IOException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(f, XmlLong.type, null);
        }
        
        public static XmlLong parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(f, XmlLong.type, options);
        }
        
        public static XmlLong parse(final URL u) throws XmlException, IOException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(u, XmlLong.type, null);
        }
        
        public static XmlLong parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(u, XmlLong.type, options);
        }
        
        public static XmlLong parse(final InputStream is) throws XmlException, IOException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(is, XmlLong.type, null);
        }
        
        public static XmlLong parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(is, XmlLong.type, options);
        }
        
        public static XmlLong parse(final Reader r) throws XmlException, IOException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(r, XmlLong.type, null);
        }
        
        public static XmlLong parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(r, XmlLong.type, options);
        }
        
        public static XmlLong parse(final Node node) throws XmlException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(node, XmlLong.type, null);
        }
        
        public static XmlLong parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(node, XmlLong.type, options);
        }
        
        @Deprecated
        public static XmlLong parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(xis, XmlLong.type, null);
        }
        
        @Deprecated
        public static XmlLong parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(xis, XmlLong.type, options);
        }
        
        public static XmlLong parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(xsr, XmlLong.type, null);
        }
        
        public static XmlLong parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlLong)XmlBeans.getContextTypeLoader().parse(xsr, XmlLong.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlLong.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlLong.type, options);
        }
        
        private Factory() {
        }
    }
}
