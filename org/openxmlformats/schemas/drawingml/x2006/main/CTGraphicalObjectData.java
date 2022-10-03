package org.openxmlformats.schemas.drawingml.x2006.main;

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
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTGraphicalObjectData extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGraphicalObjectData.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgraphicalobjectdata66adtype");
    
    String getUri();
    
    XmlToken xgetUri();
    
    boolean isSetUri();
    
    void setUri(final String p0);
    
    void xsetUri(final XmlToken p0);
    
    void unsetUri();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGraphicalObjectData.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGraphicalObjectData newInstance() {
            return (CTGraphicalObjectData)getTypeLoader().newInstance(CTGraphicalObjectData.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectData newInstance(final XmlOptions xmlOptions) {
            return (CTGraphicalObjectData)getTypeLoader().newInstance(CTGraphicalObjectData.type, xmlOptions);
        }
        
        public static CTGraphicalObjectData parse(final String s) throws XmlException {
            return (CTGraphicalObjectData)getTypeLoader().parse(s, CTGraphicalObjectData.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectData parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGraphicalObjectData)getTypeLoader().parse(s, CTGraphicalObjectData.type, xmlOptions);
        }
        
        public static CTGraphicalObjectData parse(final File file) throws XmlException, IOException {
            return (CTGraphicalObjectData)getTypeLoader().parse(file, CTGraphicalObjectData.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectData parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectData)getTypeLoader().parse(file, CTGraphicalObjectData.type, xmlOptions);
        }
        
        public static CTGraphicalObjectData parse(final URL url) throws XmlException, IOException {
            return (CTGraphicalObjectData)getTypeLoader().parse(url, CTGraphicalObjectData.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectData parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectData)getTypeLoader().parse(url, CTGraphicalObjectData.type, xmlOptions);
        }
        
        public static CTGraphicalObjectData parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGraphicalObjectData)getTypeLoader().parse(inputStream, CTGraphicalObjectData.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectData parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectData)getTypeLoader().parse(inputStream, CTGraphicalObjectData.type, xmlOptions);
        }
        
        public static CTGraphicalObjectData parse(final Reader reader) throws XmlException, IOException {
            return (CTGraphicalObjectData)getTypeLoader().parse(reader, CTGraphicalObjectData.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectData parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectData)getTypeLoader().parse(reader, CTGraphicalObjectData.type, xmlOptions);
        }
        
        public static CTGraphicalObjectData parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGraphicalObjectData)getTypeLoader().parse(xmlStreamReader, CTGraphicalObjectData.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectData parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGraphicalObjectData)getTypeLoader().parse(xmlStreamReader, CTGraphicalObjectData.type, xmlOptions);
        }
        
        public static CTGraphicalObjectData parse(final Node node) throws XmlException {
            return (CTGraphicalObjectData)getTypeLoader().parse(node, CTGraphicalObjectData.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectData parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGraphicalObjectData)getTypeLoader().parse(node, CTGraphicalObjectData.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGraphicalObjectData parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGraphicalObjectData)getTypeLoader().parse(xmlInputStream, CTGraphicalObjectData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGraphicalObjectData parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGraphicalObjectData)getTypeLoader().parse(xmlInputStream, CTGraphicalObjectData.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGraphicalObjectData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGraphicalObjectData.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
