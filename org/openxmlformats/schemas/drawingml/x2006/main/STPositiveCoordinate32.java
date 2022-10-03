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

public interface STPositiveCoordinate32 extends STCoordinate32
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STPositiveCoordinate32.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stpositivecoordinate321b9btype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STPositiveCoordinate32 newValue(final Object o) {
            return (STPositiveCoordinate32)STPositiveCoordinate32.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STPositiveCoordinate32.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STPositiveCoordinate32 newInstance() {
            return (STPositiveCoordinate32)getTypeLoader().newInstance(STPositiveCoordinate32.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate32 newInstance(final XmlOptions xmlOptions) {
            return (STPositiveCoordinate32)getTypeLoader().newInstance(STPositiveCoordinate32.type, xmlOptions);
        }
        
        public static STPositiveCoordinate32 parse(final String s) throws XmlException {
            return (STPositiveCoordinate32)getTypeLoader().parse(s, STPositiveCoordinate32.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate32 parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STPositiveCoordinate32)getTypeLoader().parse(s, STPositiveCoordinate32.type, xmlOptions);
        }
        
        public static STPositiveCoordinate32 parse(final File file) throws XmlException, IOException {
            return (STPositiveCoordinate32)getTypeLoader().parse(file, STPositiveCoordinate32.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate32 parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositiveCoordinate32)getTypeLoader().parse(file, STPositiveCoordinate32.type, xmlOptions);
        }
        
        public static STPositiveCoordinate32 parse(final URL url) throws XmlException, IOException {
            return (STPositiveCoordinate32)getTypeLoader().parse(url, STPositiveCoordinate32.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate32 parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositiveCoordinate32)getTypeLoader().parse(url, STPositiveCoordinate32.type, xmlOptions);
        }
        
        public static STPositiveCoordinate32 parse(final InputStream inputStream) throws XmlException, IOException {
            return (STPositiveCoordinate32)getTypeLoader().parse(inputStream, STPositiveCoordinate32.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate32 parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositiveCoordinate32)getTypeLoader().parse(inputStream, STPositiveCoordinate32.type, xmlOptions);
        }
        
        public static STPositiveCoordinate32 parse(final Reader reader) throws XmlException, IOException {
            return (STPositiveCoordinate32)getTypeLoader().parse(reader, STPositiveCoordinate32.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate32 parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositiveCoordinate32)getTypeLoader().parse(reader, STPositiveCoordinate32.type, xmlOptions);
        }
        
        public static STPositiveCoordinate32 parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STPositiveCoordinate32)getTypeLoader().parse(xmlStreamReader, STPositiveCoordinate32.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate32 parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STPositiveCoordinate32)getTypeLoader().parse(xmlStreamReader, STPositiveCoordinate32.type, xmlOptions);
        }
        
        public static STPositiveCoordinate32 parse(final Node node) throws XmlException {
            return (STPositiveCoordinate32)getTypeLoader().parse(node, STPositiveCoordinate32.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate32 parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STPositiveCoordinate32)getTypeLoader().parse(node, STPositiveCoordinate32.type, xmlOptions);
        }
        
        @Deprecated
        public static STPositiveCoordinate32 parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STPositiveCoordinate32)getTypeLoader().parse(xmlInputStream, STPositiveCoordinate32.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STPositiveCoordinate32 parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STPositiveCoordinate32)getTypeLoader().parse(xmlInputStream, STPositiveCoordinate32.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPositiveCoordinate32.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPositiveCoordinate32.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
