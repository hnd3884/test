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

public interface XmlHexBinary extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_hexBinary");
    
    @Deprecated
    byte[] byteArrayValue();
    
    @Deprecated
    void set(final byte[] p0);
    
    byte[] getByteArrayValue();
    
    void setByteArrayValue(final byte[] p0);
    
    public static final class Factory
    {
        public static XmlHexBinary newInstance() {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().newInstance(XmlHexBinary.type, null);
        }
        
        public static XmlHexBinary newInstance(final XmlOptions options) {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().newInstance(XmlHexBinary.type, options);
        }
        
        public static XmlHexBinary newValue(final Object obj) {
            return (XmlHexBinary)XmlHexBinary.type.newValue(obj);
        }
        
        public static XmlHexBinary parse(final String s) throws XmlException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(s, XmlHexBinary.type, null);
        }
        
        public static XmlHexBinary parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(s, XmlHexBinary.type, options);
        }
        
        public static XmlHexBinary parse(final File f) throws XmlException, IOException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(f, XmlHexBinary.type, null);
        }
        
        public static XmlHexBinary parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(f, XmlHexBinary.type, options);
        }
        
        public static XmlHexBinary parse(final URL u) throws XmlException, IOException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(u, XmlHexBinary.type, null);
        }
        
        public static XmlHexBinary parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(u, XmlHexBinary.type, options);
        }
        
        public static XmlHexBinary parse(final InputStream is) throws XmlException, IOException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(is, XmlHexBinary.type, null);
        }
        
        public static XmlHexBinary parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(is, XmlHexBinary.type, options);
        }
        
        public static XmlHexBinary parse(final Reader r) throws XmlException, IOException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(r, XmlHexBinary.type, null);
        }
        
        public static XmlHexBinary parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(r, XmlHexBinary.type, options);
        }
        
        public static XmlHexBinary parse(final Node node) throws XmlException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(node, XmlHexBinary.type, null);
        }
        
        public static XmlHexBinary parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(node, XmlHexBinary.type, options);
        }
        
        @Deprecated
        public static XmlHexBinary parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(xis, XmlHexBinary.type, null);
        }
        
        @Deprecated
        public static XmlHexBinary parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(xis, XmlHexBinary.type, options);
        }
        
        public static XmlHexBinary parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(xsr, XmlHexBinary.type, null);
        }
        
        public static XmlHexBinary parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlHexBinary)XmlBeans.getContextTypeLoader().parse(xsr, XmlHexBinary.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlHexBinary.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlHexBinary.type, options);
        }
        
        private Factory() {
        }
    }
}
