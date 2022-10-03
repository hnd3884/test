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

public interface XmlUnsignedInt extends XmlUnsignedLong
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_unsignedInt");
    
    long getLongValue();
    
    void setLongValue(final long p0);
    
    @Deprecated
    long longValue();
    
    @Deprecated
    void set(final long p0);
    
    public static final class Factory
    {
        public static XmlUnsignedInt newInstance() {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().newInstance(XmlUnsignedInt.type, null);
        }
        
        public static XmlUnsignedInt newInstance(final XmlOptions options) {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().newInstance(XmlUnsignedInt.type, options);
        }
        
        public static XmlUnsignedInt newValue(final Object obj) {
            return (XmlUnsignedInt)XmlUnsignedInt.type.newValue(obj);
        }
        
        public static XmlUnsignedInt parse(final String s) throws XmlException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(s, XmlUnsignedInt.type, null);
        }
        
        public static XmlUnsignedInt parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(s, XmlUnsignedInt.type, options);
        }
        
        public static XmlUnsignedInt parse(final File f) throws XmlException, IOException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(f, XmlUnsignedInt.type, null);
        }
        
        public static XmlUnsignedInt parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(f, XmlUnsignedInt.type, options);
        }
        
        public static XmlUnsignedInt parse(final URL u) throws XmlException, IOException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(u, XmlUnsignedInt.type, null);
        }
        
        public static XmlUnsignedInt parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(u, XmlUnsignedInt.type, options);
        }
        
        public static XmlUnsignedInt parse(final InputStream is) throws XmlException, IOException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(is, XmlUnsignedInt.type, null);
        }
        
        public static XmlUnsignedInt parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(is, XmlUnsignedInt.type, options);
        }
        
        public static XmlUnsignedInt parse(final Reader r) throws XmlException, IOException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(r, XmlUnsignedInt.type, null);
        }
        
        public static XmlUnsignedInt parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(r, XmlUnsignedInt.type, options);
        }
        
        public static XmlUnsignedInt parse(final Node node) throws XmlException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(node, XmlUnsignedInt.type, null);
        }
        
        public static XmlUnsignedInt parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(node, XmlUnsignedInt.type, options);
        }
        
        @Deprecated
        public static XmlUnsignedInt parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(xis, XmlUnsignedInt.type, null);
        }
        
        @Deprecated
        public static XmlUnsignedInt parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(xis, XmlUnsignedInt.type, options);
        }
        
        public static XmlUnsignedInt parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(xsr, XmlUnsignedInt.type, null);
        }
        
        public static XmlUnsignedInt parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlUnsignedInt)XmlBeans.getContextTypeLoader().parse(xsr, XmlUnsignedInt.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlUnsignedInt.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlUnsignedInt.type, options);
        }
        
        private Factory() {
        }
    }
}
