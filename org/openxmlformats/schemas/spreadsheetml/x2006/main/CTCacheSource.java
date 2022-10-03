package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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
import org.apache.xmlbeans.SchemaTypeLoader;
import java.lang.ref.SoftReference;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTCacheSource extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCacheSource.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcachesource00dctype");
    
    CTWorksheetSource getWorksheetSource();
    
    boolean isSetWorksheetSource();
    
    void setWorksheetSource(final CTWorksheetSource p0);
    
    CTWorksheetSource addNewWorksheetSource();
    
    void unsetWorksheetSource();
    
    CTConsolidation getConsolidation();
    
    boolean isSetConsolidation();
    
    void setConsolidation(final CTConsolidation p0);
    
    CTConsolidation addNewConsolidation();
    
    void unsetConsolidation();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    STSourceType.Enum getType();
    
    STSourceType xgetType();
    
    void setType(final STSourceType.Enum p0);
    
    void xsetType(final STSourceType p0);
    
    long getConnectionId();
    
    XmlUnsignedInt xgetConnectionId();
    
    boolean isSetConnectionId();
    
    void setConnectionId(final long p0);
    
    void xsetConnectionId(final XmlUnsignedInt p0);
    
    void unsetConnectionId();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCacheSource.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCacheSource newInstance() {
            return (CTCacheSource)getTypeLoader().newInstance(CTCacheSource.type, (XmlOptions)null);
        }
        
        public static CTCacheSource newInstance(final XmlOptions xmlOptions) {
            return (CTCacheSource)getTypeLoader().newInstance(CTCacheSource.type, xmlOptions);
        }
        
        public static CTCacheSource parse(final String s) throws XmlException {
            return (CTCacheSource)getTypeLoader().parse(s, CTCacheSource.type, (XmlOptions)null);
        }
        
        public static CTCacheSource parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCacheSource)getTypeLoader().parse(s, CTCacheSource.type, xmlOptions);
        }
        
        public static CTCacheSource parse(final File file) throws XmlException, IOException {
            return (CTCacheSource)getTypeLoader().parse(file, CTCacheSource.type, (XmlOptions)null);
        }
        
        public static CTCacheSource parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCacheSource)getTypeLoader().parse(file, CTCacheSource.type, xmlOptions);
        }
        
        public static CTCacheSource parse(final URL url) throws XmlException, IOException {
            return (CTCacheSource)getTypeLoader().parse(url, CTCacheSource.type, (XmlOptions)null);
        }
        
        public static CTCacheSource parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCacheSource)getTypeLoader().parse(url, CTCacheSource.type, xmlOptions);
        }
        
        public static CTCacheSource parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCacheSource)getTypeLoader().parse(inputStream, CTCacheSource.type, (XmlOptions)null);
        }
        
        public static CTCacheSource parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCacheSource)getTypeLoader().parse(inputStream, CTCacheSource.type, xmlOptions);
        }
        
        public static CTCacheSource parse(final Reader reader) throws XmlException, IOException {
            return (CTCacheSource)getTypeLoader().parse(reader, CTCacheSource.type, (XmlOptions)null);
        }
        
        public static CTCacheSource parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCacheSource)getTypeLoader().parse(reader, CTCacheSource.type, xmlOptions);
        }
        
        public static CTCacheSource parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCacheSource)getTypeLoader().parse(xmlStreamReader, CTCacheSource.type, (XmlOptions)null);
        }
        
        public static CTCacheSource parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCacheSource)getTypeLoader().parse(xmlStreamReader, CTCacheSource.type, xmlOptions);
        }
        
        public static CTCacheSource parse(final Node node) throws XmlException {
            return (CTCacheSource)getTypeLoader().parse(node, CTCacheSource.type, (XmlOptions)null);
        }
        
        public static CTCacheSource parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCacheSource)getTypeLoader().parse(node, CTCacheSource.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCacheSource parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCacheSource)getTypeLoader().parse(xmlInputStream, CTCacheSource.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCacheSource parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCacheSource)getTypeLoader().parse(xmlInputStream, CTCacheSource.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCacheSource.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCacheSource.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
