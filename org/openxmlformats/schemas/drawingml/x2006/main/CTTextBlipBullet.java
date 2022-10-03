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

public interface CTTextBlipBullet extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextBlipBullet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextblipbullet853btype");
    
    CTBlip getBlip();
    
    void setBlip(final CTBlip p0);
    
    CTBlip addNewBlip();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextBlipBullet.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextBlipBullet newInstance() {
            return (CTTextBlipBullet)getTypeLoader().newInstance(CTTextBlipBullet.type, (XmlOptions)null);
        }
        
        public static CTTextBlipBullet newInstance(final XmlOptions xmlOptions) {
            return (CTTextBlipBullet)getTypeLoader().newInstance(CTTextBlipBullet.type, xmlOptions);
        }
        
        public static CTTextBlipBullet parse(final String s) throws XmlException {
            return (CTTextBlipBullet)getTypeLoader().parse(s, CTTextBlipBullet.type, (XmlOptions)null);
        }
        
        public static CTTextBlipBullet parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBlipBullet)getTypeLoader().parse(s, CTTextBlipBullet.type, xmlOptions);
        }
        
        public static CTTextBlipBullet parse(final File file) throws XmlException, IOException {
            return (CTTextBlipBullet)getTypeLoader().parse(file, CTTextBlipBullet.type, (XmlOptions)null);
        }
        
        public static CTTextBlipBullet parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBlipBullet)getTypeLoader().parse(file, CTTextBlipBullet.type, xmlOptions);
        }
        
        public static CTTextBlipBullet parse(final URL url) throws XmlException, IOException {
            return (CTTextBlipBullet)getTypeLoader().parse(url, CTTextBlipBullet.type, (XmlOptions)null);
        }
        
        public static CTTextBlipBullet parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBlipBullet)getTypeLoader().parse(url, CTTextBlipBullet.type, xmlOptions);
        }
        
        public static CTTextBlipBullet parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextBlipBullet)getTypeLoader().parse(inputStream, CTTextBlipBullet.type, (XmlOptions)null);
        }
        
        public static CTTextBlipBullet parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBlipBullet)getTypeLoader().parse(inputStream, CTTextBlipBullet.type, xmlOptions);
        }
        
        public static CTTextBlipBullet parse(final Reader reader) throws XmlException, IOException {
            return (CTTextBlipBullet)getTypeLoader().parse(reader, CTTextBlipBullet.type, (XmlOptions)null);
        }
        
        public static CTTextBlipBullet parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBlipBullet)getTypeLoader().parse(reader, CTTextBlipBullet.type, xmlOptions);
        }
        
        public static CTTextBlipBullet parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextBlipBullet)getTypeLoader().parse(xmlStreamReader, CTTextBlipBullet.type, (XmlOptions)null);
        }
        
        public static CTTextBlipBullet parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBlipBullet)getTypeLoader().parse(xmlStreamReader, CTTextBlipBullet.type, xmlOptions);
        }
        
        public static CTTextBlipBullet parse(final Node node) throws XmlException {
            return (CTTextBlipBullet)getTypeLoader().parse(node, CTTextBlipBullet.type, (XmlOptions)null);
        }
        
        public static CTTextBlipBullet parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBlipBullet)getTypeLoader().parse(node, CTTextBlipBullet.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextBlipBullet parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextBlipBullet)getTypeLoader().parse(xmlInputStream, CTTextBlipBullet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextBlipBullet parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextBlipBullet)getTypeLoader().parse(xmlInputStream, CTTextBlipBullet.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextBlipBullet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextBlipBullet.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
