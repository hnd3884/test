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
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface DownloadedSchemaEntry extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(DownloadedSchemaEntry.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLTOOLS").resolveHandle("downloadedschemaentry1c75type");
    
    String getFilename();
    
    XmlToken xgetFilename();
    
    void setFilename(final String p0);
    
    void xsetFilename(final XmlToken p0);
    
    String getSha1();
    
    XmlToken xgetSha1();
    
    void setSha1(final String p0);
    
    void xsetSha1(final XmlToken p0);
    
    String[] getSchemaLocationArray();
    
    String getSchemaLocationArray(final int p0);
    
    XmlAnyURI[] xgetSchemaLocationArray();
    
    XmlAnyURI xgetSchemaLocationArray(final int p0);
    
    int sizeOfSchemaLocationArray();
    
    void setSchemaLocationArray(final String[] p0);
    
    void setSchemaLocationArray(final int p0, final String p1);
    
    void xsetSchemaLocationArray(final XmlAnyURI[] p0);
    
    void xsetSchemaLocationArray(final int p0, final XmlAnyURI p1);
    
    void insertSchemaLocation(final int p0, final String p1);
    
    void addSchemaLocation(final String p0);
    
    XmlAnyURI insertNewSchemaLocation(final int p0);
    
    XmlAnyURI addNewSchemaLocation();
    
    void removeSchemaLocation(final int p0);
    
    String getNamespace();
    
    XmlAnyURI xgetNamespace();
    
    boolean isSetNamespace();
    
    void setNamespace(final String p0);
    
    void xsetNamespace(final XmlAnyURI p0);
    
    void unsetNamespace();
    
    public static final class Factory
    {
        public static DownloadedSchemaEntry newInstance() {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().newInstance(DownloadedSchemaEntry.type, null);
        }
        
        public static DownloadedSchemaEntry newInstance(final XmlOptions options) {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().newInstance(DownloadedSchemaEntry.type, options);
        }
        
        public static DownloadedSchemaEntry parse(final String xmlAsString) throws XmlException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(xmlAsString, DownloadedSchemaEntry.type, null);
        }
        
        public static DownloadedSchemaEntry parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(xmlAsString, DownloadedSchemaEntry.type, options);
        }
        
        public static DownloadedSchemaEntry parse(final File file) throws XmlException, IOException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(file, DownloadedSchemaEntry.type, null);
        }
        
        public static DownloadedSchemaEntry parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(file, DownloadedSchemaEntry.type, options);
        }
        
        public static DownloadedSchemaEntry parse(final URL u) throws XmlException, IOException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(u, DownloadedSchemaEntry.type, null);
        }
        
        public static DownloadedSchemaEntry parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(u, DownloadedSchemaEntry.type, options);
        }
        
        public static DownloadedSchemaEntry parse(final InputStream is) throws XmlException, IOException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(is, DownloadedSchemaEntry.type, null);
        }
        
        public static DownloadedSchemaEntry parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(is, DownloadedSchemaEntry.type, options);
        }
        
        public static DownloadedSchemaEntry parse(final Reader r) throws XmlException, IOException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(r, DownloadedSchemaEntry.type, null);
        }
        
        public static DownloadedSchemaEntry parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(r, DownloadedSchemaEntry.type, options);
        }
        
        public static DownloadedSchemaEntry parse(final XMLStreamReader sr) throws XmlException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(sr, DownloadedSchemaEntry.type, null);
        }
        
        public static DownloadedSchemaEntry parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(sr, DownloadedSchemaEntry.type, options);
        }
        
        public static DownloadedSchemaEntry parse(final Node node) throws XmlException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(node, DownloadedSchemaEntry.type, null);
        }
        
        public static DownloadedSchemaEntry parse(final Node node, final XmlOptions options) throws XmlException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(node, DownloadedSchemaEntry.type, options);
        }
        
        @Deprecated
        public static DownloadedSchemaEntry parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(xis, DownloadedSchemaEntry.type, null);
        }
        
        @Deprecated
        public static DownloadedSchemaEntry parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (DownloadedSchemaEntry)XmlBeans.getContextTypeLoader().parse(xis, DownloadedSchemaEntry.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, DownloadedSchemaEntry.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, DownloadedSchemaEntry.type, options);
        }
        
        private Factory() {
        }
    }
}
