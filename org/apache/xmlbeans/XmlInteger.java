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
import java.math.BigInteger;

public interface XmlInteger extends XmlDecimal
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_integer");
    
    BigInteger getBigIntegerValue();
    
    void setBigIntegerValue(final BigInteger p0);
    
    @Deprecated
    BigInteger bigIntegerValue();
    
    @Deprecated
    void set(final BigInteger p0);
    
    public static final class Factory
    {
        public static XmlInteger newInstance() {
            return (XmlInteger)XmlBeans.getContextTypeLoader().newInstance(XmlInteger.type, null);
        }
        
        public static XmlInteger newInstance(final XmlOptions options) {
            return (XmlInteger)XmlBeans.getContextTypeLoader().newInstance(XmlInteger.type, options);
        }
        
        public static XmlInteger newValue(final Object obj) {
            return (XmlInteger)XmlInteger.type.newValue(obj);
        }
        
        public static XmlInteger parse(final String s) throws XmlException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(s, XmlInteger.type, null);
        }
        
        public static XmlInteger parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(s, XmlInteger.type, options);
        }
        
        public static XmlInteger parse(final File f) throws XmlException, IOException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(f, XmlInteger.type, null);
        }
        
        public static XmlInteger parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(f, XmlInteger.type, options);
        }
        
        public static XmlInteger parse(final URL u) throws XmlException, IOException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(u, XmlInteger.type, null);
        }
        
        public static XmlInteger parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(u, XmlInteger.type, options);
        }
        
        public static XmlInteger parse(final InputStream is) throws XmlException, IOException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(is, XmlInteger.type, null);
        }
        
        public static XmlInteger parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(is, XmlInteger.type, options);
        }
        
        public static XmlInteger parse(final Reader r) throws XmlException, IOException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(r, XmlInteger.type, null);
        }
        
        public static XmlInteger parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(r, XmlInteger.type, options);
        }
        
        public static XmlInteger parse(final Node node) throws XmlException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(node, XmlInteger.type, null);
        }
        
        public static XmlInteger parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(node, XmlInteger.type, options);
        }
        
        @Deprecated
        public static XmlInteger parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(xis, XmlInteger.type, null);
        }
        
        @Deprecated
        public static XmlInteger parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(xis, XmlInteger.type, options);
        }
        
        public static XmlInteger parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(xsr, XmlInteger.type, null);
        }
        
        public static XmlInteger parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlInteger)XmlBeans.getContextTypeLoader().parse(xsr, XmlInteger.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlInteger.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlInteger.type, options);
        }
        
        private Factory() {
        }
    }
}
