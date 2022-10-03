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
import java.math.BigDecimal;

public interface XmlDecimal extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_decimal");
    
    BigDecimal getBigDecimalValue();
    
    void setBigDecimalValue(final BigDecimal p0);
    
    @Deprecated
    BigDecimal bigDecimalValue();
    
    @Deprecated
    void set(final BigDecimal p0);
    
    public static final class Factory
    {
        public static XmlDecimal newInstance() {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().newInstance(XmlDecimal.type, null);
        }
        
        public static XmlDecimal newInstance(final XmlOptions options) {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().newInstance(XmlDecimal.type, options);
        }
        
        public static XmlDecimal newValue(final Object obj) {
            return (XmlDecimal)XmlDecimal.type.newValue(obj);
        }
        
        public static XmlDecimal parse(final String s) throws XmlException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(s, XmlDecimal.type, null);
        }
        
        public static XmlDecimal parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(s, XmlDecimal.type, options);
        }
        
        public static XmlDecimal parse(final File f) throws XmlException, IOException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(f, XmlDecimal.type, null);
        }
        
        public static XmlDecimal parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(f, XmlDecimal.type, options);
        }
        
        public static XmlDecimal parse(final URL u) throws XmlException, IOException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(u, XmlDecimal.type, null);
        }
        
        public static XmlDecimal parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(u, XmlDecimal.type, options);
        }
        
        public static XmlDecimal parse(final InputStream is) throws XmlException, IOException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(is, XmlDecimal.type, null);
        }
        
        public static XmlDecimal parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(is, XmlDecimal.type, options);
        }
        
        public static XmlDecimal parse(final Reader r) throws XmlException, IOException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(r, XmlDecimal.type, null);
        }
        
        public static XmlDecimal parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(r, XmlDecimal.type, options);
        }
        
        public static XmlDecimal parse(final Node node) throws XmlException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(node, XmlDecimal.type, null);
        }
        
        public static XmlDecimal parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(node, XmlDecimal.type, options);
        }
        
        @Deprecated
        public static XmlDecimal parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(xis, XmlDecimal.type, null);
        }
        
        @Deprecated
        public static XmlDecimal parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(xis, XmlDecimal.type, options);
        }
        
        public static XmlDecimal parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(xsr, XmlDecimal.type, null);
        }
        
        public static XmlDecimal parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlDecimal)XmlBeans.getContextTypeLoader().parse(xsr, XmlDecimal.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlDecimal.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlDecimal.type, options);
        }
        
        private Factory() {
        }
    }
}
