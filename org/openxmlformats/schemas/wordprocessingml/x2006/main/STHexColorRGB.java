package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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
import org.apache.xmlbeans.XmlHexBinary;

public interface STHexColorRGB extends XmlHexBinary
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STHexColorRGB.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sthexcolorrgbd59dtype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STHexColorRGB newValue(final Object o) {
            return (STHexColorRGB)STHexColorRGB.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STHexColorRGB.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STHexColorRGB newInstance() {
            return (STHexColorRGB)getTypeLoader().newInstance(STHexColorRGB.type, (XmlOptions)null);
        }
        
        public static STHexColorRGB newInstance(final XmlOptions xmlOptions) {
            return (STHexColorRGB)getTypeLoader().newInstance(STHexColorRGB.type, xmlOptions);
        }
        
        public static STHexColorRGB parse(final String s) throws XmlException {
            return (STHexColorRGB)getTypeLoader().parse(s, STHexColorRGB.type, (XmlOptions)null);
        }
        
        public static STHexColorRGB parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STHexColorRGB)getTypeLoader().parse(s, STHexColorRGB.type, xmlOptions);
        }
        
        public static STHexColorRGB parse(final File file) throws XmlException, IOException {
            return (STHexColorRGB)getTypeLoader().parse(file, STHexColorRGB.type, (XmlOptions)null);
        }
        
        public static STHexColorRGB parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHexColorRGB)getTypeLoader().parse(file, STHexColorRGB.type, xmlOptions);
        }
        
        public static STHexColorRGB parse(final URL url) throws XmlException, IOException {
            return (STHexColorRGB)getTypeLoader().parse(url, STHexColorRGB.type, (XmlOptions)null);
        }
        
        public static STHexColorRGB parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHexColorRGB)getTypeLoader().parse(url, STHexColorRGB.type, xmlOptions);
        }
        
        public static STHexColorRGB parse(final InputStream inputStream) throws XmlException, IOException {
            return (STHexColorRGB)getTypeLoader().parse(inputStream, STHexColorRGB.type, (XmlOptions)null);
        }
        
        public static STHexColorRGB parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHexColorRGB)getTypeLoader().parse(inputStream, STHexColorRGB.type, xmlOptions);
        }
        
        public static STHexColorRGB parse(final Reader reader) throws XmlException, IOException {
            return (STHexColorRGB)getTypeLoader().parse(reader, STHexColorRGB.type, (XmlOptions)null);
        }
        
        public static STHexColorRGB parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHexColorRGB)getTypeLoader().parse(reader, STHexColorRGB.type, xmlOptions);
        }
        
        public static STHexColorRGB parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STHexColorRGB)getTypeLoader().parse(xmlStreamReader, STHexColorRGB.type, (XmlOptions)null);
        }
        
        public static STHexColorRGB parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STHexColorRGB)getTypeLoader().parse(xmlStreamReader, STHexColorRGB.type, xmlOptions);
        }
        
        public static STHexColorRGB parse(final Node node) throws XmlException {
            return (STHexColorRGB)getTypeLoader().parse(node, STHexColorRGB.type, (XmlOptions)null);
        }
        
        public static STHexColorRGB parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STHexColorRGB)getTypeLoader().parse(node, STHexColorRGB.type, xmlOptions);
        }
        
        @Deprecated
        public static STHexColorRGB parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STHexColorRGB)getTypeLoader().parse(xmlInputStream, STHexColorRGB.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STHexColorRGB parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STHexColorRGB)getTypeLoader().parse(xmlInputStream, STHexColorRGB.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHexColorRGB.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHexColorRGB.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
