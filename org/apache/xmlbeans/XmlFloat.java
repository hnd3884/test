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

public interface XmlFloat extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_float");
    
    float getFloatValue();
    
    void setFloatValue(final float p0);
    
    @Deprecated
    float floatValue();
    
    @Deprecated
    void set(final float p0);
    
    public static final class Factory
    {
        public static XmlFloat newInstance() {
            return (XmlFloat)XmlBeans.getContextTypeLoader().newInstance(XmlFloat.type, null);
        }
        
        public static XmlFloat newInstance(final XmlOptions options) {
            return (XmlFloat)XmlBeans.getContextTypeLoader().newInstance(XmlFloat.type, options);
        }
        
        public static XmlFloat newValue(final Object obj) {
            return (XmlFloat)XmlFloat.type.newValue(obj);
        }
        
        public static XmlFloat parse(final String s) throws XmlException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(s, XmlFloat.type, null);
        }
        
        public static XmlFloat parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(s, XmlFloat.type, options);
        }
        
        public static XmlFloat parse(final File f) throws XmlException, IOException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(f, XmlFloat.type, null);
        }
        
        public static XmlFloat parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(f, XmlFloat.type, options);
        }
        
        public static XmlFloat parse(final URL u) throws XmlException, IOException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(u, XmlFloat.type, null);
        }
        
        public static XmlFloat parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(u, XmlFloat.type, options);
        }
        
        public static XmlFloat parse(final InputStream is) throws XmlException, IOException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(is, XmlFloat.type, null);
        }
        
        public static XmlFloat parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(is, XmlFloat.type, options);
        }
        
        public static XmlFloat parse(final Reader r) throws XmlException, IOException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(r, XmlFloat.type, null);
        }
        
        public static XmlFloat parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(r, XmlFloat.type, options);
        }
        
        public static XmlFloat parse(final Node node) throws XmlException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(node, XmlFloat.type, null);
        }
        
        public static XmlFloat parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(node, XmlFloat.type, options);
        }
        
        @Deprecated
        public static XmlFloat parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(xis, XmlFloat.type, null);
        }
        
        @Deprecated
        public static XmlFloat parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(xis, XmlFloat.type, options);
        }
        
        public static XmlFloat parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(xsr, XmlFloat.type, null);
        }
        
        public static XmlFloat parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlFloat)XmlBeans.getContextTypeLoader().parse(xsr, XmlFloat.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlFloat.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlFloat.type, options);
        }
        
        private Factory() {
        }
    }
}
