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

public interface XmlUnsignedByte extends XmlUnsignedShort
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_unsignedByte");
    
    short getShortValue();
    
    void setShortValue(final short p0);
    
    @Deprecated
    short shortValue();
    
    @Deprecated
    void set(final short p0);
    
    public static final class Factory
    {
        public static XmlUnsignedByte newInstance() {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().newInstance(XmlUnsignedByte.type, null);
        }
        
        public static XmlUnsignedByte newInstance(final XmlOptions options) {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().newInstance(XmlUnsignedByte.type, options);
        }
        
        public static XmlUnsignedByte newValue(final Object obj) {
            return (XmlUnsignedByte)XmlUnsignedByte.type.newValue(obj);
        }
        
        public static XmlUnsignedByte parse(final String s) throws XmlException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(s, XmlUnsignedByte.type, null);
        }
        
        public static XmlUnsignedByte parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(s, XmlUnsignedByte.type, options);
        }
        
        public static XmlUnsignedByte parse(final File f) throws XmlException, IOException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(f, XmlUnsignedByte.type, null);
        }
        
        public static XmlUnsignedByte parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(f, XmlUnsignedByte.type, options);
        }
        
        public static XmlUnsignedByte parse(final URL u) throws XmlException, IOException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(u, XmlUnsignedByte.type, null);
        }
        
        public static XmlUnsignedByte parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(u, XmlUnsignedByte.type, options);
        }
        
        public static XmlUnsignedByte parse(final InputStream is) throws XmlException, IOException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(is, XmlUnsignedByte.type, null);
        }
        
        public static XmlUnsignedByte parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(is, XmlUnsignedByte.type, options);
        }
        
        public static XmlUnsignedByte parse(final Reader r) throws XmlException, IOException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(r, XmlUnsignedByte.type, null);
        }
        
        public static XmlUnsignedByte parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(r, XmlUnsignedByte.type, options);
        }
        
        public static XmlUnsignedByte parse(final Node node) throws XmlException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(node, XmlUnsignedByte.type, null);
        }
        
        public static XmlUnsignedByte parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(node, XmlUnsignedByte.type, options);
        }
        
        @Deprecated
        public static XmlUnsignedByte parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(xis, XmlUnsignedByte.type, null);
        }
        
        @Deprecated
        public static XmlUnsignedByte parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(xis, XmlUnsignedByte.type, options);
        }
        
        public static XmlUnsignedByte parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(xsr, XmlUnsignedByte.type, null);
        }
        
        public static XmlUnsignedByte parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlUnsignedByte)XmlBeans.getContextTypeLoader().parse(xsr, XmlUnsignedByte.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlUnsignedByte.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlUnsignedByte.type, options);
        }
        
        private Factory() {
        }
    }
}
