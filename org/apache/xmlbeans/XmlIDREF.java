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

public interface XmlIDREF extends XmlNCName
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_IDREF");
    
    public static final class Factory
    {
        public static XmlIDREF newInstance() {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().newInstance(XmlIDREF.type, null);
        }
        
        public static XmlIDREF newInstance(final XmlOptions options) {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().newInstance(XmlIDREF.type, options);
        }
        
        public static XmlIDREF newValue(final Object obj) {
            return (XmlIDREF)XmlIDREF.type.newValue(obj);
        }
        
        public static XmlIDREF parse(final String s) throws XmlException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(s, XmlIDREF.type, null);
        }
        
        public static XmlIDREF parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(s, XmlIDREF.type, options);
        }
        
        public static XmlIDREF parse(final File f) throws XmlException, IOException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(f, XmlIDREF.type, null);
        }
        
        public static XmlIDREF parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(f, XmlIDREF.type, options);
        }
        
        public static XmlIDREF parse(final URL u) throws XmlException, IOException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(u, XmlIDREF.type, null);
        }
        
        public static XmlIDREF parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(u, XmlIDREF.type, options);
        }
        
        public static XmlIDREF parse(final InputStream is) throws XmlException, IOException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(is, XmlIDREF.type, null);
        }
        
        public static XmlIDREF parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(is, XmlIDREF.type, options);
        }
        
        public static XmlIDREF parse(final Reader r) throws XmlException, IOException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(r, XmlIDREF.type, null);
        }
        
        public static XmlIDREF parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(r, XmlIDREF.type, options);
        }
        
        public static XmlIDREF parse(final Node node) throws XmlException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(node, XmlIDREF.type, null);
        }
        
        public static XmlIDREF parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(node, XmlIDREF.type, options);
        }
        
        @Deprecated
        public static XmlIDREF parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(xis, XmlIDREF.type, null);
        }
        
        @Deprecated
        public static XmlIDREF parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(xis, XmlIDREF.type, options);
        }
        
        public static XmlIDREF parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(xsr, XmlIDREF.type, null);
        }
        
        public static XmlIDREF parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlIDREF)XmlBeans.getContextTypeLoader().parse(xsr, XmlIDREF.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlIDREF.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlIDREF.type, options);
        }
        
        private Factory() {
        }
    }
}
