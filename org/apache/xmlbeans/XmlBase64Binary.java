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

public interface XmlBase64Binary extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_base64Binary");
    
    @Deprecated
    byte[] byteArrayValue();
    
    @Deprecated
    void set(final byte[] p0);
    
    byte[] getByteArrayValue();
    
    void setByteArrayValue(final byte[] p0);
    
    public static final class Factory
    {
        public static XmlBase64Binary newInstance() {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().newInstance(XmlBase64Binary.type, null);
        }
        
        public static XmlBase64Binary newInstance(final XmlOptions options) {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().newInstance(XmlBase64Binary.type, options);
        }
        
        public static XmlBase64Binary newValue(final Object obj) {
            return (XmlBase64Binary)XmlBase64Binary.type.newValue(obj);
        }
        
        public static XmlBase64Binary parse(final String s) throws XmlException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(s, XmlBase64Binary.type, null);
        }
        
        public static XmlBase64Binary parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(s, XmlBase64Binary.type, options);
        }
        
        public static XmlBase64Binary parse(final File f) throws XmlException, IOException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(f, XmlBase64Binary.type, null);
        }
        
        public static XmlBase64Binary parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(f, XmlBase64Binary.type, options);
        }
        
        public static XmlBase64Binary parse(final URL u) throws XmlException, IOException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(u, XmlBase64Binary.type, null);
        }
        
        public static XmlBase64Binary parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(u, XmlBase64Binary.type, options);
        }
        
        public static XmlBase64Binary parse(final InputStream is) throws XmlException, IOException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(is, XmlBase64Binary.type, null);
        }
        
        public static XmlBase64Binary parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(is, XmlBase64Binary.type, options);
        }
        
        public static XmlBase64Binary parse(final Reader r) throws XmlException, IOException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(r, XmlBase64Binary.type, null);
        }
        
        public static XmlBase64Binary parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(r, XmlBase64Binary.type, options);
        }
        
        public static XmlBase64Binary parse(final Node node) throws XmlException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(node, XmlBase64Binary.type, null);
        }
        
        public static XmlBase64Binary parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(node, XmlBase64Binary.type, options);
        }
        
        @Deprecated
        public static XmlBase64Binary parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(xis, XmlBase64Binary.type, null);
        }
        
        @Deprecated
        public static XmlBase64Binary parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(xis, XmlBase64Binary.type, options);
        }
        
        public static XmlBase64Binary parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(xsr, XmlBase64Binary.type, null);
        }
        
        public static XmlBase64Binary parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlBase64Binary)XmlBeans.getContextTypeLoader().parse(xsr, XmlBase64Binary.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlBase64Binary.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlBase64Binary.type, options);
        }
        
        private Factory() {
        }
    }
}
