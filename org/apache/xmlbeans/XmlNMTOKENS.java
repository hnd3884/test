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

public interface XmlNMTOKENS extends XmlAnySimpleType
{
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_NMTOKENS");
    
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
        public static XmlNMTOKENS newInstance() {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().newInstance(XmlNMTOKENS.type, null);
        }
        
        public static XmlNMTOKENS newInstance(final XmlOptions options) {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().newInstance(XmlNMTOKENS.type, options);
        }
        
        public static XmlNMTOKENS newValue(final Object obj) {
            return (XmlNMTOKENS)XmlNMTOKENS.type.newValue(obj);
        }
        
        public static XmlNMTOKENS parse(final String s) throws XmlException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(s, XmlNMTOKENS.type, null);
        }
        
        public static XmlNMTOKENS parse(final String s, final XmlOptions options) throws XmlException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(s, XmlNMTOKENS.type, options);
        }
        
        public static XmlNMTOKENS parse(final File f) throws XmlException, IOException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(f, XmlNMTOKENS.type, null);
        }
        
        public static XmlNMTOKENS parse(final File f, final XmlOptions options) throws XmlException, IOException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(f, XmlNMTOKENS.type, options);
        }
        
        public static XmlNMTOKENS parse(final URL u) throws XmlException, IOException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(u, XmlNMTOKENS.type, null);
        }
        
        public static XmlNMTOKENS parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(u, XmlNMTOKENS.type, options);
        }
        
        public static XmlNMTOKENS parse(final InputStream is) throws XmlException, IOException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(is, XmlNMTOKENS.type, null);
        }
        
        public static XmlNMTOKENS parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(is, XmlNMTOKENS.type, options);
        }
        
        public static XmlNMTOKENS parse(final Reader r) throws XmlException, IOException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(r, XmlNMTOKENS.type, null);
        }
        
        public static XmlNMTOKENS parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(r, XmlNMTOKENS.type, options);
        }
        
        public static XmlNMTOKENS parse(final Node node) throws XmlException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(node, XmlNMTOKENS.type, null);
        }
        
        public static XmlNMTOKENS parse(final Node node, final XmlOptions options) throws XmlException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(node, XmlNMTOKENS.type, options);
        }
        
        @Deprecated
        public static XmlNMTOKENS parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(xis, XmlNMTOKENS.type, null);
        }
        
        @Deprecated
        public static XmlNMTOKENS parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(xis, XmlNMTOKENS.type, options);
        }
        
        public static XmlNMTOKENS parse(final XMLStreamReader xsr) throws XmlException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(xsr, XmlNMTOKENS.type, null);
        }
        
        public static XmlNMTOKENS parse(final XMLStreamReader xsr, final XmlOptions options) throws XmlException {
            return (XmlNMTOKENS)XmlBeans.getContextTypeLoader().parse(xsr, XmlNMTOKENS.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNMTOKENS.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, XmlNMTOKENS.type, options);
        }
        
        private Factory() {
        }
    }
}
