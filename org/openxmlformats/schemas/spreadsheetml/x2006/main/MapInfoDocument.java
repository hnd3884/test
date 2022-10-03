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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface MapInfoDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(MapInfoDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("mapinfo5715doctype");
    
    CTMapInfo getMapInfo();
    
    void setMapInfo(final CTMapInfo p0);
    
    CTMapInfo addNewMapInfo();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(MapInfoDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static MapInfoDocument newInstance() {
            return (MapInfoDocument)getTypeLoader().newInstance(MapInfoDocument.type, (XmlOptions)null);
        }
        
        public static MapInfoDocument newInstance(final XmlOptions xmlOptions) {
            return (MapInfoDocument)getTypeLoader().newInstance(MapInfoDocument.type, xmlOptions);
        }
        
        public static MapInfoDocument parse(final String s) throws XmlException {
            return (MapInfoDocument)getTypeLoader().parse(s, MapInfoDocument.type, (XmlOptions)null);
        }
        
        public static MapInfoDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (MapInfoDocument)getTypeLoader().parse(s, MapInfoDocument.type, xmlOptions);
        }
        
        public static MapInfoDocument parse(final File file) throws XmlException, IOException {
            return (MapInfoDocument)getTypeLoader().parse(file, MapInfoDocument.type, (XmlOptions)null);
        }
        
        public static MapInfoDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MapInfoDocument)getTypeLoader().parse(file, MapInfoDocument.type, xmlOptions);
        }
        
        public static MapInfoDocument parse(final URL url) throws XmlException, IOException {
            return (MapInfoDocument)getTypeLoader().parse(url, MapInfoDocument.type, (XmlOptions)null);
        }
        
        public static MapInfoDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MapInfoDocument)getTypeLoader().parse(url, MapInfoDocument.type, xmlOptions);
        }
        
        public static MapInfoDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (MapInfoDocument)getTypeLoader().parse(inputStream, MapInfoDocument.type, (XmlOptions)null);
        }
        
        public static MapInfoDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MapInfoDocument)getTypeLoader().parse(inputStream, MapInfoDocument.type, xmlOptions);
        }
        
        public static MapInfoDocument parse(final Reader reader) throws XmlException, IOException {
            return (MapInfoDocument)getTypeLoader().parse(reader, MapInfoDocument.type, (XmlOptions)null);
        }
        
        public static MapInfoDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MapInfoDocument)getTypeLoader().parse(reader, MapInfoDocument.type, xmlOptions);
        }
        
        public static MapInfoDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (MapInfoDocument)getTypeLoader().parse(xmlStreamReader, MapInfoDocument.type, (XmlOptions)null);
        }
        
        public static MapInfoDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (MapInfoDocument)getTypeLoader().parse(xmlStreamReader, MapInfoDocument.type, xmlOptions);
        }
        
        public static MapInfoDocument parse(final Node node) throws XmlException {
            return (MapInfoDocument)getTypeLoader().parse(node, MapInfoDocument.type, (XmlOptions)null);
        }
        
        public static MapInfoDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (MapInfoDocument)getTypeLoader().parse(node, MapInfoDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static MapInfoDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (MapInfoDocument)getTypeLoader().parse(xmlInputStream, MapInfoDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static MapInfoDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (MapInfoDocument)getTypeLoader().parse(xmlInputStream, MapInfoDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, MapInfoDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, MapInfoDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
