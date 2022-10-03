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

public interface XmlDouble extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_double");
    
    double getDoubleValue();
    
    void setDoubleValue(final double p0);
    
    @Deprecated
    double doubleValue();
    
    @Deprecated
    void set(final double p0);
    
    public static final class Factory
    {
        public static XmlDouble newInstance() {
            return (XmlDouble)XmlBeans.getContextTypeLoader().newInstance(XmlDouble.type, null);
        }
        
        public static XmlDouble newInstance(final XmlOptions options) {
            return (XmlDouble)XmlBeans.getContextTypeLoader().newInstance(XmlDouble.type, options);
        }
        
        public static XmlDouble newValue(final Object obj) {
            return (XmlDouble)XmlDouble.type.newValue(obj);
        }
        
        public static XmlDouble parse(final String s) throws XmlException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(s, XmlDouble.type, null);
        }
        
        public static XmlDouble parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(s, XmlDouble.type, options);
        }
        
        public static XmlDouble parse(final File f) throws XmlException, IOException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(f, XmlDouble.type, null);
        }
        
        public static XmlDouble parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(f, XmlDouble.type, options);
        }
        
        public static XmlDouble parse(final URL u) throws XmlException, IOException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(u, XmlDouble.type, null);
        }
        
        public static XmlDouble parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(u, XmlDouble.type, options);
        }
        
        public static XmlDouble parse(final InputStream is) throws XmlException, IOException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(is, XmlDouble.type, null);
        }
        
        public static XmlDouble parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(is, XmlDouble.type, options);
        }
        
        public static XmlDouble parse(final Reader r) throws XmlException, IOException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(r, XmlDouble.type, null);
        }
        
        public static XmlDouble parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(r, XmlDouble.type, options);
        }
        
        public static XmlDouble parse(final Node node) throws XmlException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(node, XmlDouble.type, null);
        }
        
        public static XmlDouble parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(node, XmlDouble.type, options);
        }
        
        @Deprecated
        public static XmlDouble parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(xis, XmlDouble.type, null);
        }
        
        @Deprecated
        public static XmlDouble parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(xis, XmlDouble.type, options);
        }
        
        public static XmlDouble parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(xsr, XmlDouble.type, null);
        }
        
        public static XmlDouble parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlDouble)XmlBeans.getContextTypeLoader().parse(xsr, XmlDouble.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlDouble.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlDouble.type, options);
        }
        
        private Factory() {
        }
    }
}
