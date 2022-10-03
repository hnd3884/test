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

public interface STLineWidth extends STCoordinate32
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STLineWidth.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stlinewidth8313type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STLineWidth newValue(final Object o) {
            return (STLineWidth)STLineWidth.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STLineWidth.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STLineWidth newInstance() {
            return (STLineWidth)getTypeLoader().newInstance(STLineWidth.type, (XmlOptions)null);
        }
        
        public static STLineWidth newInstance(final XmlOptions xmlOptions) {
            return (STLineWidth)getTypeLoader().newInstance(STLineWidth.type, xmlOptions);
        }
        
        public static STLineWidth parse(final String s) throws XmlException {
            return (STLineWidth)getTypeLoader().parse(s, STLineWidth.type, (XmlOptions)null);
        }
        
        public static STLineWidth parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STLineWidth)getTypeLoader().parse(s, STLineWidth.type, xmlOptions);
        }
        
        public static STLineWidth parse(final File file) throws XmlException, IOException {
            return (STLineWidth)getTypeLoader().parse(file, STLineWidth.type, (XmlOptions)null);
        }
        
        public static STLineWidth parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineWidth)getTypeLoader().parse(file, STLineWidth.type, xmlOptions);
        }
        
        public static STLineWidth parse(final URL url) throws XmlException, IOException {
            return (STLineWidth)getTypeLoader().parse(url, STLineWidth.type, (XmlOptions)null);
        }
        
        public static STLineWidth parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineWidth)getTypeLoader().parse(url, STLineWidth.type, xmlOptions);
        }
        
        public static STLineWidth parse(final InputStream inputStream) throws XmlException, IOException {
            return (STLineWidth)getTypeLoader().parse(inputStream, STLineWidth.type, (XmlOptions)null);
        }
        
        public static STLineWidth parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineWidth)getTypeLoader().parse(inputStream, STLineWidth.type, xmlOptions);
        }
        
        public static STLineWidth parse(final Reader reader) throws XmlException, IOException {
            return (STLineWidth)getTypeLoader().parse(reader, STLineWidth.type, (XmlOptions)null);
        }
        
        public static STLineWidth parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineWidth)getTypeLoader().parse(reader, STLineWidth.type, xmlOptions);
        }
        
        public static STLineWidth parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STLineWidth)getTypeLoader().parse(xmlStreamReader, STLineWidth.type, (XmlOptions)null);
        }
        
        public static STLineWidth parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STLineWidth)getTypeLoader().parse(xmlStreamReader, STLineWidth.type, xmlOptions);
        }
        
        public static STLineWidth parse(final Node node) throws XmlException {
            return (STLineWidth)getTypeLoader().parse(node, STLineWidth.type, (XmlOptions)null);
        }
        
        public static STLineWidth parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STLineWidth)getTypeLoader().parse(node, STLineWidth.type, xmlOptions);
        }
        
        @Deprecated
        public static STLineWidth parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STLineWidth)getTypeLoader().parse(xmlInputStream, STLineWidth.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STLineWidth parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STLineWidth)getTypeLoader().parse(xmlInputStream, STLineWidth.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLineWidth.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLineWidth.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
