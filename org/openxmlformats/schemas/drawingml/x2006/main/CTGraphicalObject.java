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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTGraphicalObject extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGraphicalObject.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgraphicalobject1ce3type");
    
    CTGraphicalObjectData getGraphicData();
    
    void setGraphicData(final CTGraphicalObjectData p0);
    
    CTGraphicalObjectData addNewGraphicData();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGraphicalObject.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGraphicalObject newInstance() {
            return (CTGraphicalObject)getTypeLoader().newInstance(CTGraphicalObject.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObject newInstance(final XmlOptions xmlOptions) {
            return (CTGraphicalObject)getTypeLoader().newInstance(CTGraphicalObject.type, xmlOptions);
        }
        
        public static CTGraphicalObject parse(final String s) throws XmlException {
            return (CTGraphicalObject)getTypeLoader().parse(s, CTGraphicalObject.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObject parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGraphicalObject)getTypeLoader().parse(s, CTGraphicalObject.type, xmlOptions);
        }
        
        public static CTGraphicalObject parse(final File file) throws XmlException, IOException {
            return (CTGraphicalObject)getTypeLoader().parse(file, CTGraphicalObject.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObject parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObject)getTypeLoader().parse(file, CTGraphicalObject.type, xmlOptions);
        }
        
        public static CTGraphicalObject parse(final URL url) throws XmlException, IOException {
            return (CTGraphicalObject)getTypeLoader().parse(url, CTGraphicalObject.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObject parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObject)getTypeLoader().parse(url, CTGraphicalObject.type, xmlOptions);
        }
        
        public static CTGraphicalObject parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGraphicalObject)getTypeLoader().parse(inputStream, CTGraphicalObject.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObject parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObject)getTypeLoader().parse(inputStream, CTGraphicalObject.type, xmlOptions);
        }
        
        public static CTGraphicalObject parse(final Reader reader) throws XmlException, IOException {
            return (CTGraphicalObject)getTypeLoader().parse(reader, CTGraphicalObject.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObject parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObject)getTypeLoader().parse(reader, CTGraphicalObject.type, xmlOptions);
        }
        
        public static CTGraphicalObject parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGraphicalObject)getTypeLoader().parse(xmlStreamReader, CTGraphicalObject.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObject parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGraphicalObject)getTypeLoader().parse(xmlStreamReader, CTGraphicalObject.type, xmlOptions);
        }
        
        public static CTGraphicalObject parse(final Node node) throws XmlException {
            return (CTGraphicalObject)getTypeLoader().parse(node, CTGraphicalObject.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObject parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGraphicalObject)getTypeLoader().parse(node, CTGraphicalObject.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGraphicalObject parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGraphicalObject)getTypeLoader().parse(xmlInputStream, CTGraphicalObject.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGraphicalObject parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGraphicalObject)getTypeLoader().parse(xmlInputStream, CTGraphicalObject.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGraphicalObject.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGraphicalObject.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
