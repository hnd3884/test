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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface Nsconfig extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Nsconfig.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("nsconfigaebatype");
    
    String getPackage();
    
    XmlString xgetPackage();
    
    boolean isSetPackage();
    
    void setPackage(final String p0);
    
    void xsetPackage(final XmlString p0);
    
    void unsetPackage();
    
    String getPrefix();
    
    XmlString xgetPrefix();
    
    boolean isSetPrefix();
    
    void setPrefix(final String p0);
    
    void xsetPrefix(final XmlString p0);
    
    void unsetPrefix();
    
    String getSuffix();
    
    XmlString xgetSuffix();
    
    boolean isSetSuffix();
    
    void setSuffix(final String p0);
    
    void xsetSuffix(final XmlString p0);
    
    void unsetSuffix();
    
    Object getUri();
    
    NamespaceList xgetUri();
    
    boolean isSetUri();
    
    void setUri(final Object p0);
    
    void xsetUri(final NamespaceList p0);
    
    void unsetUri();
    
    List getUriprefix();
    
    NamespacePrefixList xgetUriprefix();
    
    boolean isSetUriprefix();
    
    void setUriprefix(final List p0);
    
    void xsetUriprefix(final NamespacePrefixList p0);
    
    void unsetUriprefix();
    
    public static final class Factory
    {
        public static Nsconfig newInstance() {
            return (Nsconfig)XmlBeans.getContextTypeLoader().newInstance(Nsconfig.type, null);
        }
        
        public static Nsconfig newInstance(final XmlOptions options) {
            return (Nsconfig)XmlBeans.getContextTypeLoader().newInstance(Nsconfig.type, options);
        }
        
        public static Nsconfig parse(final String xmlAsString) throws XmlException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(xmlAsString, Nsconfig.type, null);
        }
        
        public static Nsconfig parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(xmlAsString, Nsconfig.type, options);
        }
        
        public static Nsconfig parse(final File file) throws XmlException, IOException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(file, Nsconfig.type, null);
        }
        
        public static Nsconfig parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(file, Nsconfig.type, options);
        }
        
        public static Nsconfig parse(final URL u) throws XmlException, IOException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(u, Nsconfig.type, null);
        }
        
        public static Nsconfig parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(u, Nsconfig.type, options);
        }
        
        public static Nsconfig parse(final InputStream is) throws XmlException, IOException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(is, Nsconfig.type, null);
        }
        
        public static Nsconfig parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(is, Nsconfig.type, options);
        }
        
        public static Nsconfig parse(final Reader r) throws XmlException, IOException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(r, Nsconfig.type, null);
        }
        
        public static Nsconfig parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(r, Nsconfig.type, options);
        }
        
        public static Nsconfig parse(final XMLStreamReader sr) throws XmlException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(sr, Nsconfig.type, null);
        }
        
        public static Nsconfig parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(sr, Nsconfig.type, options);
        }
        
        public static Nsconfig parse(final Node node) throws XmlException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(node, Nsconfig.type, null);
        }
        
        public static Nsconfig parse(final Node node, final XmlOptions options) throws XmlException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(node, Nsconfig.type, options);
        }
        
        @Deprecated
        public static Nsconfig parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(xis, Nsconfig.type, null);
        }
        
        @Deprecated
        public static Nsconfig parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (Nsconfig)XmlBeans.getContextTypeLoader().parse(xis, Nsconfig.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Nsconfig.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Nsconfig.type, options);
        }
        
        private Factory() {
        }
    }
}
