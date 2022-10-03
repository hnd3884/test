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

public interface XmlENTITIES extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_ENTITIES");
    
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
        public static XmlENTITIES newInstance() {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().newInstance(XmlENTITIES.type, null);
        }
        
        public static XmlENTITIES newInstance(final XmlOptions options) {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().newInstance(XmlENTITIES.type, options);
        }
        
        public static XmlENTITIES newValue(final Object obj) {
            return (XmlENTITIES)XmlENTITIES.type.newValue(obj);
        }
        
        public static XmlENTITIES parse(final String s) throws XmlException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(s, XmlENTITIES.type, null);
        }
        
        public static XmlENTITIES parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(s, XmlENTITIES.type, options);
        }
        
        public static XmlENTITIES parse(final File f) throws XmlException, IOException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(f, XmlENTITIES.type, null);
        }
        
        public static XmlENTITIES parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(f, XmlENTITIES.type, options);
        }
        
        public static XmlENTITIES parse(final URL u) throws XmlException, IOException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(u, XmlENTITIES.type, null);
        }
        
        public static XmlENTITIES parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(u, XmlENTITIES.type, options);
        }
        
        public static XmlENTITIES parse(final InputStream is) throws XmlException, IOException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(is, XmlENTITIES.type, null);
        }
        
        public static XmlENTITIES parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(is, XmlENTITIES.type, options);
        }
        
        public static XmlENTITIES parse(final Reader r) throws XmlException, IOException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(r, XmlENTITIES.type, null);
        }
        
        public static XmlENTITIES parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(r, XmlENTITIES.type, options);
        }
        
        public static XmlENTITIES parse(final Node node) throws XmlException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(node, XmlENTITIES.type, null);
        }
        
        public static XmlENTITIES parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(node, XmlENTITIES.type, options);
        }
        
        @Deprecated
        public static XmlENTITIES parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(xis, XmlENTITIES.type, null);
        }
        
        @Deprecated
        public static XmlENTITIES parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(xis, XmlENTITIES.type, options);
        }
        
        public static XmlENTITIES parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(xsr, XmlENTITIES.type, null);
        }
        
        public static XmlENTITIES parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlENTITIES)XmlBeans.getContextTypeLoader().parse(xsr, XmlENTITIES.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlENTITIES.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlENTITIES.type, options);
        }
        
        private Factory() {
        }
    }
}
