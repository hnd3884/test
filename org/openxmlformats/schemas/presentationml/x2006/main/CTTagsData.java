package org.openxmlformats.schemas.presentationml.x2006.main;

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

public interface CTTagsData extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTagsData.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttagsdatac662type");
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTagsData.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTagsData newInstance() {
            return (CTTagsData)getTypeLoader().newInstance(CTTagsData.type, (XmlOptions)null);
        }
        
        public static CTTagsData newInstance(final XmlOptions xmlOptions) {
            return (CTTagsData)getTypeLoader().newInstance(CTTagsData.type, xmlOptions);
        }
        
        public static CTTagsData parse(final String s) throws XmlException {
            return (CTTagsData)getTypeLoader().parse(s, CTTagsData.type, (XmlOptions)null);
        }
        
        public static CTTagsData parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTagsData)getTypeLoader().parse(s, CTTagsData.type, xmlOptions);
        }
        
        public static CTTagsData parse(final File file) throws XmlException, IOException {
            return (CTTagsData)getTypeLoader().parse(file, CTTagsData.type, (XmlOptions)null);
        }
        
        public static CTTagsData parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTagsData)getTypeLoader().parse(file, CTTagsData.type, xmlOptions);
        }
        
        public static CTTagsData parse(final URL url) throws XmlException, IOException {
            return (CTTagsData)getTypeLoader().parse(url, CTTagsData.type, (XmlOptions)null);
        }
        
        public static CTTagsData parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTagsData)getTypeLoader().parse(url, CTTagsData.type, xmlOptions);
        }
        
        public static CTTagsData parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTagsData)getTypeLoader().parse(inputStream, CTTagsData.type, (XmlOptions)null);
        }
        
        public static CTTagsData parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTagsData)getTypeLoader().parse(inputStream, CTTagsData.type, xmlOptions);
        }
        
        public static CTTagsData parse(final Reader reader) throws XmlException, IOException {
            return (CTTagsData)getTypeLoader().parse(reader, CTTagsData.type, (XmlOptions)null);
        }
        
        public static CTTagsData parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTagsData)getTypeLoader().parse(reader, CTTagsData.type, xmlOptions);
        }
        
        public static CTTagsData parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTagsData)getTypeLoader().parse(xmlStreamReader, CTTagsData.type, (XmlOptions)null);
        }
        
        public static CTTagsData parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTagsData)getTypeLoader().parse(xmlStreamReader, CTTagsData.type, xmlOptions);
        }
        
        public static CTTagsData parse(final Node node) throws XmlException {
            return (CTTagsData)getTypeLoader().parse(node, CTTagsData.type, (XmlOptions)null);
        }
        
        public static CTTagsData parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTagsData)getTypeLoader().parse(node, CTTagsData.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTagsData parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTagsData)getTypeLoader().parse(xmlInputStream, CTTagsData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTagsData parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTagsData)getTypeLoader().parse(xmlInputStream, CTTagsData.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTagsData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTagsData.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
