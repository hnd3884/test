package org.apache.xmlbeans.impl.xb.xsdschema;

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
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface AppinfoDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(AppinfoDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("appinfo2ea6doctype");
    
    Appinfo getAppinfo();
    
    void setAppinfo(final Appinfo p0);
    
    Appinfo addNewAppinfo();
    
    public interface Appinfo extends XmlObject
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Appinfo.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("appinfo650belemtype");
        
        String getSource();
        
        XmlAnyURI xgetSource();
        
        boolean isSetSource();
        
        void setSource(final String p0);
        
        void xsetSource(final XmlAnyURI p0);
        
        void unsetSource();
        
        public static final class Factory
        {
            public static Appinfo newInstance() {
                return (Appinfo)XmlBeans.getContextTypeLoader().newInstance(Appinfo.type, null);
            }
            
            public static Appinfo newInstance(final XmlOptions options) {
                return (Appinfo)XmlBeans.getContextTypeLoader().newInstance(Appinfo.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static AppinfoDocument newInstance() {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().newInstance(AppinfoDocument.type, null);
        }
        
        public static AppinfoDocument newInstance(final XmlOptions options) {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().newInstance(AppinfoDocument.type, options);
        }
        
        public static AppinfoDocument parse(final String xmlAsString) throws XmlException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, AppinfoDocument.type, null);
        }
        
        public static AppinfoDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, AppinfoDocument.type, options);
        }
        
        public static AppinfoDocument parse(final File file) throws XmlException, IOException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(file, AppinfoDocument.type, null);
        }
        
        public static AppinfoDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(file, AppinfoDocument.type, options);
        }
        
        public static AppinfoDocument parse(final URL u) throws XmlException, IOException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(u, AppinfoDocument.type, null);
        }
        
        public static AppinfoDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(u, AppinfoDocument.type, options);
        }
        
        public static AppinfoDocument parse(final InputStream is) throws XmlException, IOException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(is, AppinfoDocument.type, null);
        }
        
        public static AppinfoDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(is, AppinfoDocument.type, options);
        }
        
        public static AppinfoDocument parse(final Reader r) throws XmlException, IOException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(r, AppinfoDocument.type, null);
        }
        
        public static AppinfoDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(r, AppinfoDocument.type, options);
        }
        
        public static AppinfoDocument parse(final XMLStreamReader sr) throws XmlException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(sr, AppinfoDocument.type, null);
        }
        
        public static AppinfoDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(sr, AppinfoDocument.type, options);
        }
        
        public static AppinfoDocument parse(final Node node) throws XmlException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(node, AppinfoDocument.type, null);
        }
        
        public static AppinfoDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(node, AppinfoDocument.type, options);
        }
        
        @Deprecated
        public static AppinfoDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(xis, AppinfoDocument.type, null);
        }
        
        @Deprecated
        public static AppinfoDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (AppinfoDocument)XmlBeans.getContextTypeLoader().parse(xis, AppinfoDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AppinfoDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AppinfoDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
