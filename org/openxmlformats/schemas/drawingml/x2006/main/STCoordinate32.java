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
import org.apache.xmlbeans.XmlInt;

public interface STCoordinate32 extends XmlInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCoordinate32.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcoordinate322cc2type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCoordinate32 newValue(final Object o) {
            return (STCoordinate32)STCoordinate32.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCoordinate32.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCoordinate32 newInstance() {
            return (STCoordinate32)getTypeLoader().newInstance(STCoordinate32.type, (XmlOptions)null);
        }
        
        public static STCoordinate32 newInstance(final XmlOptions xmlOptions) {
            return (STCoordinate32)getTypeLoader().newInstance(STCoordinate32.type, xmlOptions);
        }
        
        public static STCoordinate32 parse(final String s) throws XmlException {
            return (STCoordinate32)getTypeLoader().parse(s, STCoordinate32.type, (XmlOptions)null);
        }
        
        public static STCoordinate32 parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCoordinate32)getTypeLoader().parse(s, STCoordinate32.type, xmlOptions);
        }
        
        public static STCoordinate32 parse(final File file) throws XmlException, IOException {
            return (STCoordinate32)getTypeLoader().parse(file, STCoordinate32.type, (XmlOptions)null);
        }
        
        public static STCoordinate32 parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCoordinate32)getTypeLoader().parse(file, STCoordinate32.type, xmlOptions);
        }
        
        public static STCoordinate32 parse(final URL url) throws XmlException, IOException {
            return (STCoordinate32)getTypeLoader().parse(url, STCoordinate32.type, (XmlOptions)null);
        }
        
        public static STCoordinate32 parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCoordinate32)getTypeLoader().parse(url, STCoordinate32.type, xmlOptions);
        }
        
        public static STCoordinate32 parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCoordinate32)getTypeLoader().parse(inputStream, STCoordinate32.type, (XmlOptions)null);
        }
        
        public static STCoordinate32 parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCoordinate32)getTypeLoader().parse(inputStream, STCoordinate32.type, xmlOptions);
        }
        
        public static STCoordinate32 parse(final Reader reader) throws XmlException, IOException {
            return (STCoordinate32)getTypeLoader().parse(reader, STCoordinate32.type, (XmlOptions)null);
        }
        
        public static STCoordinate32 parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCoordinate32)getTypeLoader().parse(reader, STCoordinate32.type, xmlOptions);
        }
        
        public static STCoordinate32 parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCoordinate32)getTypeLoader().parse(xmlStreamReader, STCoordinate32.type, (XmlOptions)null);
        }
        
        public static STCoordinate32 parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCoordinate32)getTypeLoader().parse(xmlStreamReader, STCoordinate32.type, xmlOptions);
        }
        
        public static STCoordinate32 parse(final Node node) throws XmlException {
            return (STCoordinate32)getTypeLoader().parse(node, STCoordinate32.type, (XmlOptions)null);
        }
        
        public static STCoordinate32 parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCoordinate32)getTypeLoader().parse(node, STCoordinate32.type, xmlOptions);
        }
        
        @Deprecated
        public static STCoordinate32 parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCoordinate32)getTypeLoader().parse(xmlInputStream, STCoordinate32.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCoordinate32 parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCoordinate32)getTypeLoader().parse(xmlInputStream, STCoordinate32.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCoordinate32.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCoordinate32.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
