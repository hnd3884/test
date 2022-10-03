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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlUnsignedInt;

public interface STSlideId extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STSlideId.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stslideida0b3type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STSlideId newValue(final Object o) {
            return (STSlideId)STSlideId.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STSlideId.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STSlideId newInstance() {
            return (STSlideId)getTypeLoader().newInstance(STSlideId.type, (XmlOptions)null);
        }
        
        public static STSlideId newInstance(final XmlOptions xmlOptions) {
            return (STSlideId)getTypeLoader().newInstance(STSlideId.type, xmlOptions);
        }
        
        public static STSlideId parse(final String s) throws XmlException {
            return (STSlideId)getTypeLoader().parse(s, STSlideId.type, (XmlOptions)null);
        }
        
        public static STSlideId parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STSlideId)getTypeLoader().parse(s, STSlideId.type, xmlOptions);
        }
        
        public static STSlideId parse(final File file) throws XmlException, IOException {
            return (STSlideId)getTypeLoader().parse(file, STSlideId.type, (XmlOptions)null);
        }
        
        public static STSlideId parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSlideId)getTypeLoader().parse(file, STSlideId.type, xmlOptions);
        }
        
        public static STSlideId parse(final URL url) throws XmlException, IOException {
            return (STSlideId)getTypeLoader().parse(url, STSlideId.type, (XmlOptions)null);
        }
        
        public static STSlideId parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSlideId)getTypeLoader().parse(url, STSlideId.type, xmlOptions);
        }
        
        public static STSlideId parse(final InputStream inputStream) throws XmlException, IOException {
            return (STSlideId)getTypeLoader().parse(inputStream, STSlideId.type, (XmlOptions)null);
        }
        
        public static STSlideId parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSlideId)getTypeLoader().parse(inputStream, STSlideId.type, xmlOptions);
        }
        
        public static STSlideId parse(final Reader reader) throws XmlException, IOException {
            return (STSlideId)getTypeLoader().parse(reader, STSlideId.type, (XmlOptions)null);
        }
        
        public static STSlideId parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSlideId)getTypeLoader().parse(reader, STSlideId.type, xmlOptions);
        }
        
        public static STSlideId parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STSlideId)getTypeLoader().parse(xmlStreamReader, STSlideId.type, (XmlOptions)null);
        }
        
        public static STSlideId parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STSlideId)getTypeLoader().parse(xmlStreamReader, STSlideId.type, xmlOptions);
        }
        
        public static STSlideId parse(final Node node) throws XmlException {
            return (STSlideId)getTypeLoader().parse(node, STSlideId.type, (XmlOptions)null);
        }
        
        public static STSlideId parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STSlideId)getTypeLoader().parse(node, STSlideId.type, xmlOptions);
        }
        
        @Deprecated
        public static STSlideId parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STSlideId)getTypeLoader().parse(xmlInputStream, STSlideId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STSlideId parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STSlideId)getTypeLoader().parse(xmlInputStream, STSlideId.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSlideId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSlideId.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
