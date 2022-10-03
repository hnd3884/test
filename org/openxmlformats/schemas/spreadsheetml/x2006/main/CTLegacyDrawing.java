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
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTLegacyDrawing extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLegacyDrawing.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlegacydrawing49f4type");
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLegacyDrawing.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLegacyDrawing newInstance() {
            return (CTLegacyDrawing)getTypeLoader().newInstance(CTLegacyDrawing.type, (XmlOptions)null);
        }
        
        public static CTLegacyDrawing newInstance(final XmlOptions xmlOptions) {
            return (CTLegacyDrawing)getTypeLoader().newInstance(CTLegacyDrawing.type, xmlOptions);
        }
        
        public static CTLegacyDrawing parse(final String s) throws XmlException {
            return (CTLegacyDrawing)getTypeLoader().parse(s, CTLegacyDrawing.type, (XmlOptions)null);
        }
        
        public static CTLegacyDrawing parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLegacyDrawing)getTypeLoader().parse(s, CTLegacyDrawing.type, xmlOptions);
        }
        
        public static CTLegacyDrawing parse(final File file) throws XmlException, IOException {
            return (CTLegacyDrawing)getTypeLoader().parse(file, CTLegacyDrawing.type, (XmlOptions)null);
        }
        
        public static CTLegacyDrawing parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegacyDrawing)getTypeLoader().parse(file, CTLegacyDrawing.type, xmlOptions);
        }
        
        public static CTLegacyDrawing parse(final URL url) throws XmlException, IOException {
            return (CTLegacyDrawing)getTypeLoader().parse(url, CTLegacyDrawing.type, (XmlOptions)null);
        }
        
        public static CTLegacyDrawing parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegacyDrawing)getTypeLoader().parse(url, CTLegacyDrawing.type, xmlOptions);
        }
        
        public static CTLegacyDrawing parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLegacyDrawing)getTypeLoader().parse(inputStream, CTLegacyDrawing.type, (XmlOptions)null);
        }
        
        public static CTLegacyDrawing parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegacyDrawing)getTypeLoader().parse(inputStream, CTLegacyDrawing.type, xmlOptions);
        }
        
        public static CTLegacyDrawing parse(final Reader reader) throws XmlException, IOException {
            return (CTLegacyDrawing)getTypeLoader().parse(reader, CTLegacyDrawing.type, (XmlOptions)null);
        }
        
        public static CTLegacyDrawing parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegacyDrawing)getTypeLoader().parse(reader, CTLegacyDrawing.type, xmlOptions);
        }
        
        public static CTLegacyDrawing parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLegacyDrawing)getTypeLoader().parse(xmlStreamReader, CTLegacyDrawing.type, (XmlOptions)null);
        }
        
        public static CTLegacyDrawing parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLegacyDrawing)getTypeLoader().parse(xmlStreamReader, CTLegacyDrawing.type, xmlOptions);
        }
        
        public static CTLegacyDrawing parse(final Node node) throws XmlException {
            return (CTLegacyDrawing)getTypeLoader().parse(node, CTLegacyDrawing.type, (XmlOptions)null);
        }
        
        public static CTLegacyDrawing parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLegacyDrawing)getTypeLoader().parse(node, CTLegacyDrawing.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLegacyDrawing parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLegacyDrawing)getTypeLoader().parse(xmlInputStream, CTLegacyDrawing.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLegacyDrawing parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLegacyDrawing)getTypeLoader().parse(xmlInputStream, CTLegacyDrawing.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLegacyDrawing.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLegacyDrawing.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
