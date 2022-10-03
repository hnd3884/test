package org.apache.xmlbeans.impl.xb.substwsdl;

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
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface TImport extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(TImport.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLTOOLS").resolveHandle("timport22datype");
    
    String getNamespace();
    
    XmlAnyURI xgetNamespace();
    
    void setNamespace(final String p0);
    
    void xsetNamespace(final XmlAnyURI p0);
    
    String getLocation();
    
    XmlAnyURI xgetLocation();
    
    void setLocation(final String p0);
    
    void xsetLocation(final XmlAnyURI p0);
    
    public static final class Factory
    {
        public static TImport newInstance() {
            return (TImport)XmlBeans.getContextTypeLoader().newInstance(TImport.type, null);
        }
        
        public static TImport newInstance(final XmlOptions options) {
            return (TImport)XmlBeans.getContextTypeLoader().newInstance(TImport.type, options);
        }
        
        public static TImport parse(final String xmlAsString) throws XmlException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(xmlAsString, TImport.type, null);
        }
        
        public static TImport parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(xmlAsString, TImport.type, options);
        }
        
        public static TImport parse(final File file) throws XmlException, IOException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(file, TImport.type, null);
        }
        
        public static TImport parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(file, TImport.type, options);
        }
        
        public static TImport parse(final URL u) throws XmlException, IOException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(u, TImport.type, null);
        }
        
        public static TImport parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(u, TImport.type, options);
        }
        
        public static TImport parse(final InputStream is) throws XmlException, IOException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(is, TImport.type, null);
        }
        
        public static TImport parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(is, TImport.type, options);
        }
        
        public static TImport parse(final Reader r) throws XmlException, IOException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(r, TImport.type, null);
        }
        
        public static TImport parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(r, TImport.type, options);
        }
        
        public static TImport parse(final XMLStreamReader sr) throws XmlException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(sr, TImport.type, null);
        }
        
        public static TImport parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(sr, TImport.type, options);
        }
        
        public static TImport parse(final Node node) throws XmlException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(node, TImport.type, null);
        }
        
        public static TImport parse(final Node node, final XmlOptions options) throws XmlException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(node, TImport.type, options);
        }
        
        @Deprecated
        public static TImport parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(xis, TImport.type, null);
        }
        
        @Deprecated
        public static TImport parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (TImport)XmlBeans.getContextTypeLoader().parse(xis, TImport.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TImport.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TImport.type, options);
        }
        
        private Factory() {
        }
    }
}
