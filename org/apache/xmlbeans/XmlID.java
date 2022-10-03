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

public interface XmlID extends XmlNCName
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_ID");
    
    public static final class Factory
    {
        public static XmlID newInstance() {
            return (XmlID)XmlBeans.getContextTypeLoader().newInstance(XmlID.type, null);
        }
        
        public static XmlID newInstance(final XmlOptions options) {
            return (XmlID)XmlBeans.getContextTypeLoader().newInstance(XmlID.type, options);
        }
        
        public static XmlID newValue(final Object obj) {
            return (XmlID)XmlID.type.newValue(obj);
        }
        
        public static XmlID parse(final String s) throws XmlException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(s, XmlID.type, null);
        }
        
        public static XmlID parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(s, XmlID.type, options);
        }
        
        public static XmlID parse(final File f) throws XmlException, IOException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(f, XmlID.type, null);
        }
        
        public static XmlID parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(f, XmlID.type, options);
        }
        
        public static XmlID parse(final URL u) throws XmlException, IOException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(u, XmlID.type, null);
        }
        
        public static XmlID parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(u, XmlID.type, options);
        }
        
        public static XmlID parse(final InputStream is) throws XmlException, IOException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(is, XmlID.type, null);
        }
        
        public static XmlID parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(is, XmlID.type, options);
        }
        
        public static XmlID parse(final Reader r) throws XmlException, IOException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(r, XmlID.type, null);
        }
        
        public static XmlID parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(r, XmlID.type, options);
        }
        
        public static XmlID parse(final Node node) throws XmlException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(node, XmlID.type, null);
        }
        
        public static XmlID parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(node, XmlID.type, options);
        }
        
        @Deprecated
        public static XmlID parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(xis, XmlID.type, null);
        }
        
        @Deprecated
        public static XmlID parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(xis, XmlID.type, options);
        }
        
        public static XmlID parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(xsr, XmlID.type, null);
        }
        
        public static XmlID parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlID)XmlBeans.getContextTypeLoader().parse(xsr, XmlID.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlID.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlID.type, options);
        }
        
        private Factory() {
        }
    }
}
