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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTextCharBullet extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextCharBullet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextcharbullet3c20type");
    
    String getChar();
    
    XmlString xgetChar();
    
    void setChar(final String p0);
    
    void xsetChar(final XmlString p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextCharBullet.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextCharBullet newInstance() {
            return (CTTextCharBullet)getTypeLoader().newInstance(CTTextCharBullet.type, (XmlOptions)null);
        }
        
        public static CTTextCharBullet newInstance(final XmlOptions xmlOptions) {
            return (CTTextCharBullet)getTypeLoader().newInstance(CTTextCharBullet.type, xmlOptions);
        }
        
        public static CTTextCharBullet parse(final String s) throws XmlException {
            return (CTTextCharBullet)getTypeLoader().parse(s, CTTextCharBullet.type, (XmlOptions)null);
        }
        
        public static CTTextCharBullet parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextCharBullet)getTypeLoader().parse(s, CTTextCharBullet.type, xmlOptions);
        }
        
        public static CTTextCharBullet parse(final File file) throws XmlException, IOException {
            return (CTTextCharBullet)getTypeLoader().parse(file, CTTextCharBullet.type, (XmlOptions)null);
        }
        
        public static CTTextCharBullet parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextCharBullet)getTypeLoader().parse(file, CTTextCharBullet.type, xmlOptions);
        }
        
        public static CTTextCharBullet parse(final URL url) throws XmlException, IOException {
            return (CTTextCharBullet)getTypeLoader().parse(url, CTTextCharBullet.type, (XmlOptions)null);
        }
        
        public static CTTextCharBullet parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextCharBullet)getTypeLoader().parse(url, CTTextCharBullet.type, xmlOptions);
        }
        
        public static CTTextCharBullet parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextCharBullet)getTypeLoader().parse(inputStream, CTTextCharBullet.type, (XmlOptions)null);
        }
        
        public static CTTextCharBullet parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextCharBullet)getTypeLoader().parse(inputStream, CTTextCharBullet.type, xmlOptions);
        }
        
        public static CTTextCharBullet parse(final Reader reader) throws XmlException, IOException {
            return (CTTextCharBullet)getTypeLoader().parse(reader, CTTextCharBullet.type, (XmlOptions)null);
        }
        
        public static CTTextCharBullet parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextCharBullet)getTypeLoader().parse(reader, CTTextCharBullet.type, xmlOptions);
        }
        
        public static CTTextCharBullet parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextCharBullet)getTypeLoader().parse(xmlStreamReader, CTTextCharBullet.type, (XmlOptions)null);
        }
        
        public static CTTextCharBullet parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextCharBullet)getTypeLoader().parse(xmlStreamReader, CTTextCharBullet.type, xmlOptions);
        }
        
        public static CTTextCharBullet parse(final Node node) throws XmlException {
            return (CTTextCharBullet)getTypeLoader().parse(node, CTTextCharBullet.type, (XmlOptions)null);
        }
        
        public static CTTextCharBullet parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextCharBullet)getTypeLoader().parse(node, CTTextCharBullet.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextCharBullet parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextCharBullet)getTypeLoader().parse(xmlInputStream, CTTextCharBullet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextCharBullet parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextCharBullet)getTypeLoader().parse(xmlInputStream, CTTextCharBullet.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextCharBullet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextCharBullet.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
