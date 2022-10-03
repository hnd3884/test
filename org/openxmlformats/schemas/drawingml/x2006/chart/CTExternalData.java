package org.openxmlformats.schemas.drawingml.x2006.chart;

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
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTExternalData extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTExternalData.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctexternaldata2e07type");
    
    CTBoolean getAutoUpdate();
    
    boolean isSetAutoUpdate();
    
    void setAutoUpdate(final CTBoolean p0);
    
    CTBoolean addNewAutoUpdate();
    
    void unsetAutoUpdate();
    
    String getId();
    
    STRelationshipId xgetId();
    
    void setId(final String p0);
    
    void xsetId(final STRelationshipId p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTExternalData.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTExternalData newInstance() {
            return (CTExternalData)getTypeLoader().newInstance(CTExternalData.type, (XmlOptions)null);
        }
        
        public static CTExternalData newInstance(final XmlOptions xmlOptions) {
            return (CTExternalData)getTypeLoader().newInstance(CTExternalData.type, xmlOptions);
        }
        
        public static CTExternalData parse(final String s) throws XmlException {
            return (CTExternalData)getTypeLoader().parse(s, CTExternalData.type, (XmlOptions)null);
        }
        
        public static CTExternalData parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalData)getTypeLoader().parse(s, CTExternalData.type, xmlOptions);
        }
        
        public static CTExternalData parse(final File file) throws XmlException, IOException {
            return (CTExternalData)getTypeLoader().parse(file, CTExternalData.type, (XmlOptions)null);
        }
        
        public static CTExternalData parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalData)getTypeLoader().parse(file, CTExternalData.type, xmlOptions);
        }
        
        public static CTExternalData parse(final URL url) throws XmlException, IOException {
            return (CTExternalData)getTypeLoader().parse(url, CTExternalData.type, (XmlOptions)null);
        }
        
        public static CTExternalData parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalData)getTypeLoader().parse(url, CTExternalData.type, xmlOptions);
        }
        
        public static CTExternalData parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTExternalData)getTypeLoader().parse(inputStream, CTExternalData.type, (XmlOptions)null);
        }
        
        public static CTExternalData parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalData)getTypeLoader().parse(inputStream, CTExternalData.type, xmlOptions);
        }
        
        public static CTExternalData parse(final Reader reader) throws XmlException, IOException {
            return (CTExternalData)getTypeLoader().parse(reader, CTExternalData.type, (XmlOptions)null);
        }
        
        public static CTExternalData parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalData)getTypeLoader().parse(reader, CTExternalData.type, xmlOptions);
        }
        
        public static CTExternalData parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTExternalData)getTypeLoader().parse(xmlStreamReader, CTExternalData.type, (XmlOptions)null);
        }
        
        public static CTExternalData parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalData)getTypeLoader().parse(xmlStreamReader, CTExternalData.type, xmlOptions);
        }
        
        public static CTExternalData parse(final Node node) throws XmlException {
            return (CTExternalData)getTypeLoader().parse(node, CTExternalData.type, (XmlOptions)null);
        }
        
        public static CTExternalData parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalData)getTypeLoader().parse(node, CTExternalData.type, xmlOptions);
        }
        
        @Deprecated
        public static CTExternalData parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTExternalData)getTypeLoader().parse(xmlInputStream, CTExternalData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTExternalData parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTExternalData)getTypeLoader().parse(xmlInputStream, CTExternalData.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalData.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
