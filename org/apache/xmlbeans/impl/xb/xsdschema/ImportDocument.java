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

public interface ImportDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ImportDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("import99fedoctype");
    
    Import getImport();
    
    void setImport(final Import p0);
    
    Import addNewImport();
    
    public interface Import extends Annotated
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Import.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("importe2ffelemtype");
        
        String getNamespace();
        
        XmlAnyURI xgetNamespace();
        
        boolean isSetNamespace();
        
        void setNamespace(final String p0);
        
        void xsetNamespace(final XmlAnyURI p0);
        
        void unsetNamespace();
        
        String getSchemaLocation();
        
        XmlAnyURI xgetSchemaLocation();
        
        boolean isSetSchemaLocation();
        
        void setSchemaLocation(final String p0);
        
        void xsetSchemaLocation(final XmlAnyURI p0);
        
        void unsetSchemaLocation();
        
        public static final class Factory
        {
            public static Import newInstance() {
                return (Import)XmlBeans.getContextTypeLoader().newInstance(Import.type, null);
            }
            
            public static Import newInstance(final XmlOptions options) {
                return (Import)XmlBeans.getContextTypeLoader().newInstance(Import.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static ImportDocument newInstance() {
            return (ImportDocument)XmlBeans.getContextTypeLoader().newInstance(ImportDocument.type, null);
        }
        
        public static ImportDocument newInstance(final XmlOptions options) {
            return (ImportDocument)XmlBeans.getContextTypeLoader().newInstance(ImportDocument.type, options);
        }
        
        public static ImportDocument parse(final String xmlAsString) throws XmlException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, ImportDocument.type, null);
        }
        
        public static ImportDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, ImportDocument.type, options);
        }
        
        public static ImportDocument parse(final File file) throws XmlException, IOException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(file, ImportDocument.type, null);
        }
        
        public static ImportDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(file, ImportDocument.type, options);
        }
        
        public static ImportDocument parse(final URL u) throws XmlException, IOException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(u, ImportDocument.type, null);
        }
        
        public static ImportDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(u, ImportDocument.type, options);
        }
        
        public static ImportDocument parse(final InputStream is) throws XmlException, IOException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(is, ImportDocument.type, null);
        }
        
        public static ImportDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(is, ImportDocument.type, options);
        }
        
        public static ImportDocument parse(final Reader r) throws XmlException, IOException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(r, ImportDocument.type, null);
        }
        
        public static ImportDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(r, ImportDocument.type, options);
        }
        
        public static ImportDocument parse(final XMLStreamReader sr) throws XmlException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(sr, ImportDocument.type, null);
        }
        
        public static ImportDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(sr, ImportDocument.type, options);
        }
        
        public static ImportDocument parse(final Node node) throws XmlException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(node, ImportDocument.type, null);
        }
        
        public static ImportDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(node, ImportDocument.type, options);
        }
        
        @Deprecated
        public static ImportDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(xis, ImportDocument.type, null);
        }
        
        @Deprecated
        public static ImportDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (ImportDocument)XmlBeans.getContextTypeLoader().parse(xis, ImportDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ImportDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ImportDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
