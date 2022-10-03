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
import org.apache.xmlbeans.XmlQName;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface Usertypeconfig extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Usertypeconfig.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("usertypeconfig7bbatype");
    
    String getStaticHandler();
    
    XmlString xgetStaticHandler();
    
    void setStaticHandler(final String p0);
    
    void xsetStaticHandler(final XmlString p0);
    
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
    
    public static final class Factory
    {
        public static Usertypeconfig newInstance() {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().newInstance(Usertypeconfig.type, null);
        }
        
        public static Usertypeconfig newInstance(final XmlOptions options) {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().newInstance(Usertypeconfig.type, options);
        }
        
        public static Usertypeconfig parse(final String xmlAsString) throws XmlException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(xmlAsString, Usertypeconfig.type, null);
        }
        
        public static Usertypeconfig parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(xmlAsString, Usertypeconfig.type, options);
        }
        
        public static Usertypeconfig parse(final File file) throws XmlException, IOException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(file, Usertypeconfig.type, null);
        }
        
        public static Usertypeconfig parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(file, Usertypeconfig.type, options);
        }
        
        public static Usertypeconfig parse(final URL u) throws XmlException, IOException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(u, Usertypeconfig.type, null);
        }
        
        public static Usertypeconfig parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(u, Usertypeconfig.type, options);
        }
        
        public static Usertypeconfig parse(final InputStream is) throws XmlException, IOException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(is, Usertypeconfig.type, null);
        }
        
        public static Usertypeconfig parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(is, Usertypeconfig.type, options);
        }
        
        public static Usertypeconfig parse(final Reader r) throws XmlException, IOException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(r, Usertypeconfig.type, null);
        }
        
        public static Usertypeconfig parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(r, Usertypeconfig.type, options);
        }
        
        public static Usertypeconfig parse(final XMLStreamReader sr) throws XmlException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(sr, Usertypeconfig.type, null);
        }
        
        public static Usertypeconfig parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(sr, Usertypeconfig.type, options);
        }
        
        public static Usertypeconfig parse(final Node node) throws XmlException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(node, Usertypeconfig.type, null);
        }
        
        public static Usertypeconfig parse(final Node node, final XmlOptions options) throws XmlException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(node, Usertypeconfig.type, options);
        }
        
        @Deprecated
        public static Usertypeconfig parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(xis, Usertypeconfig.type, null);
        }
        
        @Deprecated
        public static Usertypeconfig parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (Usertypeconfig)XmlBeans.getContextTypeLoader().parse(xis, Usertypeconfig.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Usertypeconfig.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Usertypeconfig.type, options);
        }
        
        private Factory() {
        }
    }
}
