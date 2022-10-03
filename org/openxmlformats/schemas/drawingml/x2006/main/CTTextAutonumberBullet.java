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

public interface CTTextAutonumberBullet extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextAutonumberBullet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextautonumberbulletd602type");
    
    STTextAutonumberScheme.Enum getType();
    
    STTextAutonumberScheme xgetType();
    
    void setType(final STTextAutonumberScheme.Enum p0);
    
    void xsetType(final STTextAutonumberScheme p0);
    
    int getStartAt();
    
    STTextBulletStartAtNum xgetStartAt();
    
    boolean isSetStartAt();
    
    void setStartAt(final int p0);
    
    void xsetStartAt(final STTextBulletStartAtNum p0);
    
    void unsetStartAt();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextAutonumberBullet.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextAutonumberBullet newInstance() {
            return (CTTextAutonumberBullet)getTypeLoader().newInstance(CTTextAutonumberBullet.type, (XmlOptions)null);
        }
        
        public static CTTextAutonumberBullet newInstance(final XmlOptions xmlOptions) {
            return (CTTextAutonumberBullet)getTypeLoader().newInstance(CTTextAutonumberBullet.type, xmlOptions);
        }
        
        public static CTTextAutonumberBullet parse(final String s) throws XmlException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(s, CTTextAutonumberBullet.type, (XmlOptions)null);
        }
        
        public static CTTextAutonumberBullet parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(s, CTTextAutonumberBullet.type, xmlOptions);
        }
        
        public static CTTextAutonumberBullet parse(final File file) throws XmlException, IOException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(file, CTTextAutonumberBullet.type, (XmlOptions)null);
        }
        
        public static CTTextAutonumberBullet parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(file, CTTextAutonumberBullet.type, xmlOptions);
        }
        
        public static CTTextAutonumberBullet parse(final URL url) throws XmlException, IOException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(url, CTTextAutonumberBullet.type, (XmlOptions)null);
        }
        
        public static CTTextAutonumberBullet parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(url, CTTextAutonumberBullet.type, xmlOptions);
        }
        
        public static CTTextAutonumberBullet parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(inputStream, CTTextAutonumberBullet.type, (XmlOptions)null);
        }
        
        public static CTTextAutonumberBullet parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(inputStream, CTTextAutonumberBullet.type, xmlOptions);
        }
        
        public static CTTextAutonumberBullet parse(final Reader reader) throws XmlException, IOException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(reader, CTTextAutonumberBullet.type, (XmlOptions)null);
        }
        
        public static CTTextAutonumberBullet parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(reader, CTTextAutonumberBullet.type, xmlOptions);
        }
        
        public static CTTextAutonumberBullet parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(xmlStreamReader, CTTextAutonumberBullet.type, (XmlOptions)null);
        }
        
        public static CTTextAutonumberBullet parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(xmlStreamReader, CTTextAutonumberBullet.type, xmlOptions);
        }
        
        public static CTTextAutonumberBullet parse(final Node node) throws XmlException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(node, CTTextAutonumberBullet.type, (XmlOptions)null);
        }
        
        public static CTTextAutonumberBullet parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(node, CTTextAutonumberBullet.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextAutonumberBullet parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(xmlInputStream, CTTextAutonumberBullet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextAutonumberBullet parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextAutonumberBullet)getTypeLoader().parse(xmlInputStream, CTTextAutonumberBullet.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextAutonumberBullet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextAutonumberBullet.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
