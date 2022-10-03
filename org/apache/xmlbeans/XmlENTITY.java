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

public interface XmlENTITY extends XmlNCName
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_ENTITY");
    
    public static final class Factory
    {
        public static XmlENTITY newInstance() {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().newInstance(XmlENTITY.type, null);
        }
        
        public static XmlENTITY newInstance(final XmlOptions options) {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().newInstance(XmlENTITY.type, options);
        }
        
        public static XmlENTITY newValue(final Object obj) {
            return (XmlENTITY)XmlENTITY.type.newValue(obj);
        }
        
        public static XmlENTITY parse(final String s) throws XmlException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(s, XmlENTITY.type, null);
        }
        
        public static XmlENTITY parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(s, XmlENTITY.type, options);
        }
        
        public static XmlENTITY parse(final File f) throws XmlException, IOException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(f, XmlENTITY.type, null);
        }
        
        public static XmlENTITY parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(f, XmlENTITY.type, options);
        }
        
        public static XmlENTITY parse(final URL u) throws XmlException, IOException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(u, XmlENTITY.type, null);
        }
        
        public static XmlENTITY parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(u, XmlENTITY.type, options);
        }
        
        public static XmlENTITY parse(final InputStream is) throws XmlException, IOException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(is, XmlENTITY.type, null);
        }
        
        public static XmlENTITY parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(is, XmlENTITY.type, options);
        }
        
        public static XmlENTITY parse(final Reader r) throws XmlException, IOException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(r, XmlENTITY.type, null);
        }
        
        public static XmlENTITY parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(r, XmlENTITY.type, options);
        }
        
        public static XmlENTITY parse(final Node node) throws XmlException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(node, XmlENTITY.type, null);
        }
        
        public static XmlENTITY parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(node, XmlENTITY.type, options);
        }
        
        @Deprecated
        public static XmlENTITY parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(xis, XmlENTITY.type, null);
        }
        
        @Deprecated
        public static XmlENTITY parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(xis, XmlENTITY.type, options);
        }
        
        public static XmlENTITY parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(xsr, XmlENTITY.type, null);
        }
        
        public static XmlENTITY parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlENTITY)XmlBeans.getContextTypeLoader().parse(xsr, XmlENTITY.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlENTITY.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlENTITY.type, options);
        }
        
        private Factory() {
        }
    }
}
