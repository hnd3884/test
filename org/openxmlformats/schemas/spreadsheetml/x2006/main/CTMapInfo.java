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
import org.apache.xmlbeans.XmlString;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTMapInfo extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTMapInfo.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctmapinfo1a09type");
    
    List<CTSchema> getSchemaList();
    
    @Deprecated
    CTSchema[] getSchemaArray();
    
    CTSchema getSchemaArray(final int p0);
    
    int sizeOfSchemaArray();
    
    void setSchemaArray(final CTSchema[] p0);
    
    void setSchemaArray(final int p0, final CTSchema p1);
    
    CTSchema insertNewSchema(final int p0);
    
    CTSchema addNewSchema();
    
    void removeSchema(final int p0);
    
    List<CTMap> getMapList();
    
    @Deprecated
    CTMap[] getMapArray();
    
    CTMap getMapArray(final int p0);
    
    int sizeOfMapArray();
    
    void setMapArray(final CTMap[] p0);
    
    void setMapArray(final int p0, final CTMap p1);
    
    CTMap insertNewMap(final int p0);
    
    CTMap addNewMap();
    
    void removeMap(final int p0);
    
    String getSelectionNamespaces();
    
    XmlString xgetSelectionNamespaces();
    
    void setSelectionNamespaces(final String p0);
    
    void xsetSelectionNamespaces(final XmlString p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTMapInfo.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTMapInfo newInstance() {
            return (CTMapInfo)getTypeLoader().newInstance(CTMapInfo.type, (XmlOptions)null);
        }
        
        public static CTMapInfo newInstance(final XmlOptions xmlOptions) {
            return (CTMapInfo)getTypeLoader().newInstance(CTMapInfo.type, xmlOptions);
        }
        
        public static CTMapInfo parse(final String s) throws XmlException {
            return (CTMapInfo)getTypeLoader().parse(s, CTMapInfo.type, (XmlOptions)null);
        }
        
        public static CTMapInfo parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTMapInfo)getTypeLoader().parse(s, CTMapInfo.type, xmlOptions);
        }
        
        public static CTMapInfo parse(final File file) throws XmlException, IOException {
            return (CTMapInfo)getTypeLoader().parse(file, CTMapInfo.type, (XmlOptions)null);
        }
        
        public static CTMapInfo parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMapInfo)getTypeLoader().parse(file, CTMapInfo.type, xmlOptions);
        }
        
        public static CTMapInfo parse(final URL url) throws XmlException, IOException {
            return (CTMapInfo)getTypeLoader().parse(url, CTMapInfo.type, (XmlOptions)null);
        }
        
        public static CTMapInfo parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMapInfo)getTypeLoader().parse(url, CTMapInfo.type, xmlOptions);
        }
        
        public static CTMapInfo parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTMapInfo)getTypeLoader().parse(inputStream, CTMapInfo.type, (XmlOptions)null);
        }
        
        public static CTMapInfo parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMapInfo)getTypeLoader().parse(inputStream, CTMapInfo.type, xmlOptions);
        }
        
        public static CTMapInfo parse(final Reader reader) throws XmlException, IOException {
            return (CTMapInfo)getTypeLoader().parse(reader, CTMapInfo.type, (XmlOptions)null);
        }
        
        public static CTMapInfo parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMapInfo)getTypeLoader().parse(reader, CTMapInfo.type, xmlOptions);
        }
        
        public static CTMapInfo parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTMapInfo)getTypeLoader().parse(xmlStreamReader, CTMapInfo.type, (XmlOptions)null);
        }
        
        public static CTMapInfo parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTMapInfo)getTypeLoader().parse(xmlStreamReader, CTMapInfo.type, xmlOptions);
        }
        
        public static CTMapInfo parse(final Node node) throws XmlException {
            return (CTMapInfo)getTypeLoader().parse(node, CTMapInfo.type, (XmlOptions)null);
        }
        
        public static CTMapInfo parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTMapInfo)getTypeLoader().parse(node, CTMapInfo.type, xmlOptions);
        }
        
        @Deprecated
        public static CTMapInfo parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTMapInfo)getTypeLoader().parse(xmlInputStream, CTMapInfo.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTMapInfo parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTMapInfo)getTypeLoader().parse(xmlInputStream, CTMapInfo.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMapInfo.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMapInfo.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
