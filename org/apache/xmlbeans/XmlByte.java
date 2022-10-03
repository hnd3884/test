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

public interface XmlByte extends XmlShort
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_byte");
    
    @Deprecated
    byte byteValue();
    
    @Deprecated
    void set(final byte p0);
    
    byte getByteValue();
    
    void setByteValue(final byte p0);
    
    public static final class Factory
    {
        public static XmlByte newInstance() {
            return (XmlByte)XmlBeans.getContextTypeLoader().newInstance(XmlByte.type, null);
        }
        
        public static XmlByte newInstance(final XmlOptions options) {
            return (XmlByte)XmlBeans.getContextTypeLoader().newInstance(XmlByte.type, options);
        }
        
        public static XmlByte newValue(final Object obj) {
            return (XmlByte)XmlByte.type.newValue(obj);
        }
        
        public static XmlByte parse(final String s) throws XmlException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(s, XmlByte.type, null);
        }
        
        public static XmlByte parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(s, XmlByte.type, options);
        }
        
        public static XmlByte parse(final File f) throws XmlException, IOException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(f, XmlByte.type, null);
        }
        
        public static XmlByte parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(f, XmlByte.type, options);
        }
        
        public static XmlByte parse(final URL u) throws XmlException, IOException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(u, XmlByte.type, null);
        }
        
        public static XmlByte parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(u, XmlByte.type, options);
        }
        
        public static XmlByte parse(final InputStream is) throws XmlException, IOException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(is, XmlByte.type, null);
        }
        
        public static XmlByte parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(is, XmlByte.type, options);
        }
        
        public static XmlByte parse(final Reader r) throws XmlException, IOException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(r, XmlByte.type, null);
        }
        
        public static XmlByte parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(r, XmlByte.type, options);
        }
        
        public static XmlByte parse(final Node node) throws XmlException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(node, XmlByte.type, null);
        }
        
        public static XmlByte parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(node, XmlByte.type, options);
        }
        
        @Deprecated
        public static XmlByte parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(xis, XmlByte.type, null);
        }
        
        @Deprecated
        public static XmlByte parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(xis, XmlByte.type, options);
        }
        
        public static XmlByte parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(xsr, XmlByte.type, null);
        }
        
        public static XmlByte parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlByte)XmlBeans.getContextTypeLoader().parse(xsr, XmlByte.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlByte.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlByte.type, options);
        }
        
        private Factory() {
        }
    }
}
