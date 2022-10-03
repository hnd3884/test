package org.apache.xmlbeans.impl.xb.xsdownload;

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
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface DownloadedSchemasDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(DownloadedSchemasDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLTOOLS").resolveHandle("downloadedschemas2dd7doctype");
    
    DownloadedSchemas getDownloadedSchemas();
    
    void setDownloadedSchemas(final DownloadedSchemas p0);
    
    DownloadedSchemas addNewDownloadedSchemas();
    
    public interface DownloadedSchemas extends XmlObject
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(DownloadedSchemas.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLTOOLS").resolveHandle("downloadedschemasb3efelemtype");
        
        DownloadedSchemaEntry[] getEntryArray();
        
        DownloadedSchemaEntry getEntryArray(final int p0);
        
        int sizeOfEntryArray();
        
        void setEntryArray(final DownloadedSchemaEntry[] p0);
        
        void setEntryArray(final int p0, final DownloadedSchemaEntry p1);
        
        DownloadedSchemaEntry insertNewEntry(final int p0);
        
        DownloadedSchemaEntry addNewEntry();
        
        void removeEntry(final int p0);
        
        String getDefaultDirectory();
        
        XmlToken xgetDefaultDirectory();
        
        boolean isSetDefaultDirectory();
        
        void setDefaultDirectory(final String p0);
        
        void xsetDefaultDirectory(final XmlToken p0);
        
        void unsetDefaultDirectory();
        
        public static final class Factory
        {
            public static DownloadedSchemas newInstance() {
                return (DownloadedSchemas)XmlBeans.getContextTypeLoader().newInstance(DownloadedSchemas.type, null);
            }
            
            public static DownloadedSchemas newInstance(final XmlOptions options) {
                return (DownloadedSchemas)XmlBeans.getContextTypeLoader().newInstance(DownloadedSchemas.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static DownloadedSchemasDocument newInstance() {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().newInstance(DownloadedSchemasDocument.type, null);
        }
        
        public static DownloadedSchemasDocument newInstance(final XmlOptions options) {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().newInstance(DownloadedSchemasDocument.type, options);
        }
        
        public static DownloadedSchemasDocument parse(final String xmlAsString) throws XmlException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, DownloadedSchemasDocument.type, null);
        }
        
        public static DownloadedSchemasDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, DownloadedSchemasDocument.type, options);
        }
        
        public static DownloadedSchemasDocument parse(final File file) throws XmlException, IOException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(file, DownloadedSchemasDocument.type, null);
        }
        
        public static DownloadedSchemasDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(file, DownloadedSchemasDocument.type, options);
        }
        
        public static DownloadedSchemasDocument parse(final URL u) throws XmlException, IOException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(u, DownloadedSchemasDocument.type, null);
        }
        
        public static DownloadedSchemasDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(u, DownloadedSchemasDocument.type, options);
        }
        
        public static DownloadedSchemasDocument parse(final InputStream is) throws XmlException, IOException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(is, DownloadedSchemasDocument.type, null);
        }
        
        public static DownloadedSchemasDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(is, DownloadedSchemasDocument.type, options);
        }
        
        public static DownloadedSchemasDocument parse(final Reader r) throws XmlException, IOException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(r, DownloadedSchemasDocument.type, null);
        }
        
        public static DownloadedSchemasDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(r, DownloadedSchemasDocument.type, options);
        }
        
        public static DownloadedSchemasDocument parse(final XMLStreamReader sr) throws XmlException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(sr, DownloadedSchemasDocument.type, null);
        }
        
        public static DownloadedSchemasDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(sr, DownloadedSchemasDocument.type, options);
        }
        
        public static DownloadedSchemasDocument parse(final Node node) throws XmlException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(node, DownloadedSchemasDocument.type, null);
        }
        
        public static DownloadedSchemasDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(node, DownloadedSchemasDocument.type, options);
        }
        
        @Deprecated
        public static DownloadedSchemasDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(xis, DownloadedSchemasDocument.type, null);
        }
        
        @Deprecated
        public static DownloadedSchemasDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (DownloadedSchemasDocument)XmlBeans.getContextTypeLoader().parse(xis, DownloadedSchemasDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, DownloadedSchemasDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, DownloadedSchemasDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
