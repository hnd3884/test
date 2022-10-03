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

public interface CTEmbeddedFontDataId extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTEmbeddedFontDataId.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctembeddedfontdataid7d67type");
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTEmbeddedFontDataId.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTEmbeddedFontDataId newInstance() {
            return (CTEmbeddedFontDataId)getTypeLoader().newInstance(CTEmbeddedFontDataId.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontDataId newInstance(final XmlOptions xmlOptions) {
            return (CTEmbeddedFontDataId)getTypeLoader().newInstance(CTEmbeddedFontDataId.type, xmlOptions);
        }
        
        public static CTEmbeddedFontDataId parse(final String s) throws XmlException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(s, CTEmbeddedFontDataId.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontDataId parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(s, CTEmbeddedFontDataId.type, xmlOptions);
        }
        
        public static CTEmbeddedFontDataId parse(final File file) throws XmlException, IOException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(file, CTEmbeddedFontDataId.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontDataId parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(file, CTEmbeddedFontDataId.type, xmlOptions);
        }
        
        public static CTEmbeddedFontDataId parse(final URL url) throws XmlException, IOException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(url, CTEmbeddedFontDataId.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontDataId parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(url, CTEmbeddedFontDataId.type, xmlOptions);
        }
        
        public static CTEmbeddedFontDataId parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(inputStream, CTEmbeddedFontDataId.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontDataId parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(inputStream, CTEmbeddedFontDataId.type, xmlOptions);
        }
        
        public static CTEmbeddedFontDataId parse(final Reader reader) throws XmlException, IOException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(reader, CTEmbeddedFontDataId.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontDataId parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(reader, CTEmbeddedFontDataId.type, xmlOptions);
        }
        
        public static CTEmbeddedFontDataId parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(xmlStreamReader, CTEmbeddedFontDataId.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontDataId parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(xmlStreamReader, CTEmbeddedFontDataId.type, xmlOptions);
        }
        
        public static CTEmbeddedFontDataId parse(final Node node) throws XmlException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(node, CTEmbeddedFontDataId.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontDataId parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(node, CTEmbeddedFontDataId.type, xmlOptions);
        }
        
        @Deprecated
        public static CTEmbeddedFontDataId parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(xmlInputStream, CTEmbeddedFontDataId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTEmbeddedFontDataId parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTEmbeddedFontDataId)getTypeLoader().parse(xmlInputStream, CTEmbeddedFontDataId.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEmbeddedFontDataId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEmbeddedFontDataId.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
