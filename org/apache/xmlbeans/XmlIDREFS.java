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
import java.util.List;

public interface XmlIDREFS extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_IDREFS");
    
    List getListValue();
    
    List xgetListValue();
    
    void setListValue(final List p0);
    
    @Deprecated
    List listValue();
    
    @Deprecated
    List xlistValue();
    
    @Deprecated
    void set(final List p0);
    
    public static final class Factory
    {
        public static XmlIDREFS newInstance() {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().newInstance(XmlIDREFS.type, null);
        }
        
        public static XmlIDREFS newInstance(final XmlOptions options) {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().newInstance(XmlIDREFS.type, options);
        }
        
        public static XmlIDREFS newValue(final Object obj) {
            return (XmlIDREFS)XmlIDREFS.type.newValue(obj);
        }
        
        public static XmlIDREFS parse(final String s) throws XmlException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(s, XmlIDREFS.type, null);
        }
        
        public static XmlIDREFS parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(s, XmlIDREFS.type, options);
        }
        
        public static XmlIDREFS parse(final File f) throws XmlException, IOException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(f, XmlIDREFS.type, null);
        }
        
        public static XmlIDREFS parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(f, XmlIDREFS.type, options);
        }
        
        public static XmlIDREFS parse(final URL u) throws XmlException, IOException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(u, XmlIDREFS.type, null);
        }
        
        public static XmlIDREFS parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(u, XmlIDREFS.type, options);
        }
        
        public static XmlIDREFS parse(final InputStream is) throws XmlException, IOException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(is, XmlIDREFS.type, null);
        }
        
        public static XmlIDREFS parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(is, XmlIDREFS.type, options);
        }
        
        public static XmlIDREFS parse(final Reader r) throws XmlException, IOException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(r, XmlIDREFS.type, null);
        }
        
        public static XmlIDREFS parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(r, XmlIDREFS.type, options);
        }
        
        public static XmlIDREFS parse(final Node node) throws XmlException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(node, XmlIDREFS.type, null);
        }
        
        public static XmlIDREFS parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(node, XmlIDREFS.type, options);
        }
        
        @Deprecated
        public static XmlIDREFS parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(xis, XmlIDREFS.type, null);
        }
        
        @Deprecated
        public static XmlIDREFS parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(xis, XmlIDREFS.type, options);
        }
        
        public static XmlIDREFS parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(xsr, XmlIDREFS.type, null);
        }
        
        public static XmlIDREFS parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlIDREFS)XmlBeans.getContextTypeLoader().parse(xsr, XmlIDREFS.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlIDREFS.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlIDREFS.type, options);
        }
        
        private Factory() {
        }
    }
}
