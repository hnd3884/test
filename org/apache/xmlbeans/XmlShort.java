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

public interface XmlShort extends XmlInt
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_short");
    
    short getShortValue();
    
    void setShortValue(final short p0);
    
    @Deprecated
    short shortValue();
    
    @Deprecated
    void set(final short p0);
    
    public static final class Factory
    {
        public static XmlShort newInstance() {
            return (XmlShort)XmlBeans.getContextTypeLoader().newInstance(XmlShort.type, null);
        }
        
        public static XmlShort newInstance(final XmlOptions options) {
            return (XmlShort)XmlBeans.getContextTypeLoader().newInstance(XmlShort.type, options);
        }
        
        public static XmlShort newValue(final Object obj) {
            return (XmlShort)XmlShort.type.newValue(obj);
        }
        
        public static XmlShort parse(final String s) throws XmlException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(s, XmlShort.type, null);
        }
        
        public static XmlShort parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(s, XmlShort.type, options);
        }
        
        public static XmlShort parse(final File f) throws XmlException, IOException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(f, XmlShort.type, null);
        }
        
        public static XmlShort parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(f, XmlShort.type, options);
        }
        
        public static XmlShort parse(final URL u) throws XmlException, IOException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(u, XmlShort.type, null);
        }
        
        public static XmlShort parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(u, XmlShort.type, options);
        }
        
        public static XmlShort parse(final InputStream is) throws XmlException, IOException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(is, XmlShort.type, null);
        }
        
        public static XmlShort parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(is, XmlShort.type, options);
        }
        
        public static XmlShort parse(final Reader r) throws XmlException, IOException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(r, XmlShort.type, null);
        }
        
        public static XmlShort parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(r, XmlShort.type, options);
        }
        
        public static XmlShort parse(final Node node) throws XmlException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(node, XmlShort.type, null);
        }
        
        public static XmlShort parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(node, XmlShort.type, options);
        }
        
        @Deprecated
        public static XmlShort parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(xis, XmlShort.type, null);
        }
        
        @Deprecated
        public static XmlShort parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(xis, XmlShort.type, options);
        }
        
        public static XmlShort parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(xsr, XmlShort.type, null);
        }
        
        public static XmlShort parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlShort)XmlBeans.getContextTypeLoader().parse(xsr, XmlShort.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlShort.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlShort.type, options);
        }
        
        private Factory() {
        }
    }
}
