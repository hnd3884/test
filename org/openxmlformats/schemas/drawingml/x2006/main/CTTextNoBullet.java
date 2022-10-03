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

public interface CTTextNoBullet extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextNoBullet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextnobulleta08btype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextNoBullet.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextNoBullet newInstance() {
            return (CTTextNoBullet)getTypeLoader().newInstance(CTTextNoBullet.type, (XmlOptions)null);
        }
        
        public static CTTextNoBullet newInstance(final XmlOptions xmlOptions) {
            return (CTTextNoBullet)getTypeLoader().newInstance(CTTextNoBullet.type, xmlOptions);
        }
        
        public static CTTextNoBullet parse(final String s) throws XmlException {
            return (CTTextNoBullet)getTypeLoader().parse(s, CTTextNoBullet.type, (XmlOptions)null);
        }
        
        public static CTTextNoBullet parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextNoBullet)getTypeLoader().parse(s, CTTextNoBullet.type, xmlOptions);
        }
        
        public static CTTextNoBullet parse(final File file) throws XmlException, IOException {
            return (CTTextNoBullet)getTypeLoader().parse(file, CTTextNoBullet.type, (XmlOptions)null);
        }
        
        public static CTTextNoBullet parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextNoBullet)getTypeLoader().parse(file, CTTextNoBullet.type, xmlOptions);
        }
        
        public static CTTextNoBullet parse(final URL url) throws XmlException, IOException {
            return (CTTextNoBullet)getTypeLoader().parse(url, CTTextNoBullet.type, (XmlOptions)null);
        }
        
        public static CTTextNoBullet parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextNoBullet)getTypeLoader().parse(url, CTTextNoBullet.type, xmlOptions);
        }
        
        public static CTTextNoBullet parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextNoBullet)getTypeLoader().parse(inputStream, CTTextNoBullet.type, (XmlOptions)null);
        }
        
        public static CTTextNoBullet parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextNoBullet)getTypeLoader().parse(inputStream, CTTextNoBullet.type, xmlOptions);
        }
        
        public static CTTextNoBullet parse(final Reader reader) throws XmlException, IOException {
            return (CTTextNoBullet)getTypeLoader().parse(reader, CTTextNoBullet.type, (XmlOptions)null);
        }
        
        public static CTTextNoBullet parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextNoBullet)getTypeLoader().parse(reader, CTTextNoBullet.type, xmlOptions);
        }
        
        public static CTTextNoBullet parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextNoBullet)getTypeLoader().parse(xmlStreamReader, CTTextNoBullet.type, (XmlOptions)null);
        }
        
        public static CTTextNoBullet parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextNoBullet)getTypeLoader().parse(xmlStreamReader, CTTextNoBullet.type, xmlOptions);
        }
        
        public static CTTextNoBullet parse(final Node node) throws XmlException {
            return (CTTextNoBullet)getTypeLoader().parse(node, CTTextNoBullet.type, (XmlOptions)null);
        }
        
        public static CTTextNoBullet parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextNoBullet)getTypeLoader().parse(node, CTTextNoBullet.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextNoBullet parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextNoBullet)getTypeLoader().parse(xmlInputStream, CTTextNoBullet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextNoBullet parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextNoBullet)getTypeLoader().parse(xmlInputStream, CTTextNoBullet.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextNoBullet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextNoBullet.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
