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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlQName;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface Qnameconfig extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Qnameconfig.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("qnameconfig463ftype");
    
    QName getName();
    
    XmlQName xgetName();
    
    boolean isSetName();
    
    void setName(final QName p0);
    
    void xsetName(final XmlQName p0);
    
    void unsetName();
    
    String getJavaname();
    
    XmlString xgetJavaname();
    
    boolean isSetJavaname();
    
    void setJavaname(final String p0);
    
    void xsetJavaname(final XmlString p0);
    
    void unsetJavaname();
    
    List getTarget();
    
    Qnametargetlist xgetTarget();
    
    boolean isSetTarget();
    
    void setTarget(final List p0);
    
    void xsetTarget(final Qnametargetlist p0);
    
    void unsetTarget();
    
    public static final class Factory
    {
        public static Qnameconfig newInstance() {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().newInstance(Qnameconfig.type, null);
        }
        
        public static Qnameconfig newInstance(final XmlOptions options) {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().newInstance(Qnameconfig.type, options);
        }
        
        public static Qnameconfig parse(final String xmlAsString) throws XmlException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(xmlAsString, Qnameconfig.type, null);
        }
        
        public static Qnameconfig parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(xmlAsString, Qnameconfig.type, options);
        }
        
        public static Qnameconfig parse(final File file) throws XmlException, IOException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(file, Qnameconfig.type, null);
        }
        
        public static Qnameconfig parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(file, Qnameconfig.type, options);
        }
        
        public static Qnameconfig parse(final URL u) throws XmlException, IOException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(u, Qnameconfig.type, null);
        }
        
        public static Qnameconfig parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(u, Qnameconfig.type, options);
        }
        
        public static Qnameconfig parse(final InputStream is) throws XmlException, IOException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(is, Qnameconfig.type, null);
        }
        
        public static Qnameconfig parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(is, Qnameconfig.type, options);
        }
        
        public static Qnameconfig parse(final Reader r) throws XmlException, IOException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(r, Qnameconfig.type, null);
        }
        
        public static Qnameconfig parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(r, Qnameconfig.type, options);
        }
        
        public static Qnameconfig parse(final XMLStreamReader sr) throws XmlException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(sr, Qnameconfig.type, null);
        }
        
        public static Qnameconfig parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(sr, Qnameconfig.type, options);
        }
        
        public static Qnameconfig parse(final Node node) throws XmlException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(node, Qnameconfig.type, null);
        }
        
        public static Qnameconfig parse(final Node node, final XmlOptions options) throws XmlException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(node, Qnameconfig.type, options);
        }
        
        @Deprecated
        public static Qnameconfig parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(xis, Qnameconfig.type, null);
        }
        
        @Deprecated
        public static Qnameconfig parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (Qnameconfig)XmlBeans.getContextTypeLoader().parse(xis, Qnameconfig.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Qnameconfig.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Qnameconfig.type, options);
        }
        
        private Factory() {
        }
    }
}
