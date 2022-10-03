package org.apache.xmlbeans.impl.xb.xmlconfig;

import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlBeans;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;

public interface Qnametargetlist extends XmlAnySimpleType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Qnametargetlist.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("qnametargetlist16actype");
    
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
        public static Qnametargetlist newValue(final Object obj) {
            return (Qnametargetlist)Qnametargetlist.type.newValue(obj);
        }
        
        public static Qnametargetlist newInstance() {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().newInstance(Qnametargetlist.type, null);
        }
        
        public static Qnametargetlist newInstance(final XmlOptions options) {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().newInstance(Qnametargetlist.type, options);
        }
        
        public static Qnametargetlist parse(final String xmlAsString) throws XmlException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(xmlAsString, Qnametargetlist.type, null);
        }
        
        public static Qnametargetlist parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(xmlAsString, Qnametargetlist.type, options);
        }
        
        public static Qnametargetlist parse(final File file) throws XmlException, IOException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(file, Qnametargetlist.type, null);
        }
        
        public static Qnametargetlist parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(file, Qnametargetlist.type, options);
        }
        
        public static Qnametargetlist parse(final URL u) throws XmlException, IOException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(u, Qnametargetlist.type, null);
        }
        
        public static Qnametargetlist parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(u, Qnametargetlist.type, options);
        }
        
        public static Qnametargetlist parse(final InputStream is) throws XmlException, IOException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(is, Qnametargetlist.type, null);
        }
        
        public static Qnametargetlist parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(is, Qnametargetlist.type, options);
        }
        
        public static Qnametargetlist parse(final Reader r) throws XmlException, IOException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(r, Qnametargetlist.type, null);
        }
        
        public static Qnametargetlist parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(r, Qnametargetlist.type, options);
        }
        
        public static Qnametargetlist parse(final XMLStreamReader sr) throws XmlException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(sr, Qnametargetlist.type, null);
        }
        
        public static Qnametargetlist parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(sr, Qnametargetlist.type, options);
        }
        
        public static Qnametargetlist parse(final Node node) throws XmlException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(node, Qnametargetlist.type, null);
        }
        
        public static Qnametargetlist parse(final Node node, final XmlOptions options) throws XmlException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(node, Qnametargetlist.type, options);
        }
        
        @Deprecated
        public static Qnametargetlist parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(xis, Qnametargetlist.type, null);
        }
        
        @Deprecated
        public static Qnametargetlist parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (Qnametargetlist)XmlBeans.getContextTypeLoader().parse(xis, Qnametargetlist.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Qnametargetlist.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Qnametargetlist.type, options);
        }
        
        private Factory() {
        }
    }
}
