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

public interface XmlAnySimpleType extends XmlObject
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_anySimpleType");
    
    @Deprecated
    String stringValue();
    
    @Deprecated
    void set(final String p0);
    
    String getStringValue();
    
    void setStringValue(final String p0);
    
    public static final class Factory
    {
        public static XmlAnySimpleType newInstance() {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().newInstance(XmlAnySimpleType.type, null);
        }
        
        public static XmlAnySimpleType newInstance(final XmlOptions options) {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().newInstance(XmlAnySimpleType.type, options);
        }
        
        public static XmlAnySimpleType newValue(final Object obj) {
            return XmlAnySimpleType.type.newValue(obj);
        }
        
        public static XmlAnySimpleType parse(final String s) throws XmlException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(s, XmlAnySimpleType.type, null);
        }
        
        public static XmlAnySimpleType parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(s, XmlAnySimpleType.type, options);
        }
        
        public static XmlAnySimpleType parse(final File f) throws XmlException, IOException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(f, XmlAnySimpleType.type, null);
        }
        
        public static XmlAnySimpleType parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(f, XmlAnySimpleType.type, options);
        }
        
        public static XmlAnySimpleType parse(final URL u) throws XmlException, IOException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(u, XmlAnySimpleType.type, null);
        }
        
        public static XmlAnySimpleType parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(u, XmlAnySimpleType.type, options);
        }
        
        public static XmlAnySimpleType parse(final InputStream is) throws XmlException, IOException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(is, XmlAnySimpleType.type, null);
        }
        
        public static XmlAnySimpleType parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(is, XmlAnySimpleType.type, options);
        }
        
        public static XmlAnySimpleType parse(final Reader r) throws XmlException, IOException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(r, XmlAnySimpleType.type, null);
        }
        
        public static XmlAnySimpleType parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(r, XmlAnySimpleType.type, options);
        }
        
        public static XmlAnySimpleType parse(final Node node) throws XmlException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(node, XmlAnySimpleType.type, null);
        }
        
        public static XmlAnySimpleType parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(node, XmlAnySimpleType.type, options);
        }
        
        @Deprecated
        public static XmlAnySimpleType parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(xis, XmlAnySimpleType.type, null);
        }
        
        @Deprecated
        public static XmlAnySimpleType parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(xis, XmlAnySimpleType.type, options);
        }
        
        public static XmlAnySimpleType parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(xsr, XmlAnySimpleType.type, null);
        }
        
        public static XmlAnySimpleType parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlAnySimpleType)XmlBeans.getContextTypeLoader().parse(xsr, XmlAnySimpleType.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlAnySimpleType.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlAnySimpleType.type, options);
        }
        
        private Factory() {
        }
    }
}
