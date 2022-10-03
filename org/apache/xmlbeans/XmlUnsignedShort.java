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

public interface XmlUnsignedShort extends XmlUnsignedInt
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_unsignedShort");
    
    int getIntValue();
    
    void setIntValue(final int p0);
    
    @Deprecated
    int intValue();
    
    @Deprecated
    void set(final int p0);
    
    public static final class Factory
    {
        public static XmlUnsignedShort newInstance() {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().newInstance(XmlUnsignedShort.type, null);
        }
        
        public static XmlUnsignedShort newInstance(final XmlOptions options) {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().newInstance(XmlUnsignedShort.type, options);
        }
        
        public static XmlUnsignedShort newValue(final Object obj) {
            return (XmlUnsignedShort)XmlUnsignedShort.type.newValue(obj);
        }
        
        public static XmlUnsignedShort parse(final String s) throws XmlException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(s, XmlUnsignedShort.type, null);
        }
        
        public static XmlUnsignedShort parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(s, XmlUnsignedShort.type, options);
        }
        
        public static XmlUnsignedShort parse(final File f) throws XmlException, IOException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(f, XmlUnsignedShort.type, null);
        }
        
        public static XmlUnsignedShort parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(f, XmlUnsignedShort.type, options);
        }
        
        public static XmlUnsignedShort parse(final URL u) throws XmlException, IOException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(u, XmlUnsignedShort.type, null);
        }
        
        public static XmlUnsignedShort parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(u, XmlUnsignedShort.type, options);
        }
        
        public static XmlUnsignedShort parse(final InputStream is) throws XmlException, IOException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(is, XmlUnsignedShort.type, null);
        }
        
        public static XmlUnsignedShort parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(is, XmlUnsignedShort.type, options);
        }
        
        public static XmlUnsignedShort parse(final Reader r) throws XmlException, IOException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(r, XmlUnsignedShort.type, null);
        }
        
        public static XmlUnsignedShort parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(r, XmlUnsignedShort.type, options);
        }
        
        public static XmlUnsignedShort parse(final Node node) throws XmlException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(node, XmlUnsignedShort.type, null);
        }
        
        public static XmlUnsignedShort parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(node, XmlUnsignedShort.type, options);
        }
        
        @Deprecated
        public static XmlUnsignedShort parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(xis, XmlUnsignedShort.type, null);
        }
        
        @Deprecated
        public static XmlUnsignedShort parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(xis, XmlUnsignedShort.type, options);
        }
        
        public static XmlUnsignedShort parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(xsr, XmlUnsignedShort.type, null);
        }
        
        public static XmlUnsignedShort parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlUnsignedShort)XmlBeans.getContextTypeLoader().parse(xsr, XmlUnsignedShort.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlUnsignedShort.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlUnsignedShort.type, options);
        }
        
        private Factory() {
        }
    }
}
