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

public interface XmlInt extends XmlLong
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_int");
    
    int getIntValue();
    
    void setIntValue(final int p0);
    
    @Deprecated
    int intValue();
    
    @Deprecated
    void set(final int p0);
    
    public static final class Factory
    {
        public static XmlInt newInstance() {
            return (XmlInt)XmlBeans.getContextTypeLoader().newInstance(XmlInt.type, null);
        }
        
        public static XmlInt newInstance(final XmlOptions options) {
            return (XmlInt)XmlBeans.getContextTypeLoader().newInstance(XmlInt.type, options);
        }
        
        public static XmlInt newValue(final Object obj) {
            return (XmlInt)XmlInt.type.newValue(obj);
        }
        
        public static XmlInt parse(final String s) throws XmlException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(s, XmlInt.type, null);
        }
        
        public static XmlInt parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(s, XmlInt.type, options);
        }
        
        public static XmlInt parse(final File f) throws XmlException, IOException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(f, XmlInt.type, null);
        }
        
        public static XmlInt parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(f, XmlInt.type, options);
        }
        
        public static XmlInt parse(final URL u) throws XmlException, IOException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(u, XmlInt.type, null);
        }
        
        public static XmlInt parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(u, XmlInt.type, options);
        }
        
        public static XmlInt parse(final InputStream is) throws XmlException, IOException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(is, XmlInt.type, null);
        }
        
        public static XmlInt parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(is, XmlInt.type, options);
        }
        
        public static XmlInt parse(final Reader r) throws XmlException, IOException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(r, XmlInt.type, null);
        }
        
        public static XmlInt parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(r, XmlInt.type, options);
        }
        
        public static XmlInt parse(final Node node) throws XmlException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(node, XmlInt.type, null);
        }
        
        public static XmlInt parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(node, XmlInt.type, options);
        }
        
        @Deprecated
        public static XmlInt parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(xis, XmlInt.type, null);
        }
        
        @Deprecated
        public static XmlInt parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(xis, XmlInt.type, options);
        }
        
        public static XmlInt parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(xsr, XmlInt.type, null);
        }
        
        public static XmlInt parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlInt)XmlBeans.getContextTypeLoader().parse(xsr, XmlInt.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlInt.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlInt.type, options);
        }
        
        private Factory() {
        }
    }
}
