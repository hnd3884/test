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

public interface STTextPoint extends XmlInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextPoint.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextpoint4284type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextPoint newValue(final Object o) {
            return (STTextPoint)STTextPoint.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextPoint.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextPoint newInstance() {
            return (STTextPoint)getTypeLoader().newInstance(STTextPoint.type, (XmlOptions)null);
        }
        
        public static STTextPoint newInstance(final XmlOptions xmlOptions) {
            return (STTextPoint)getTypeLoader().newInstance(STTextPoint.type, xmlOptions);
        }
        
        public static STTextPoint parse(final String s) throws XmlException {
            return (STTextPoint)getTypeLoader().parse(s, STTextPoint.type, (XmlOptions)null);
        }
        
        public static STTextPoint parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextPoint)getTypeLoader().parse(s, STTextPoint.type, xmlOptions);
        }
        
        public static STTextPoint parse(final File file) throws XmlException, IOException {
            return (STTextPoint)getTypeLoader().parse(file, STTextPoint.type, (XmlOptions)null);
        }
        
        public static STTextPoint parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextPoint)getTypeLoader().parse(file, STTextPoint.type, xmlOptions);
        }
        
        public static STTextPoint parse(final URL url) throws XmlException, IOException {
            return (STTextPoint)getTypeLoader().parse(url, STTextPoint.type, (XmlOptions)null);
        }
        
        public static STTextPoint parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextPoint)getTypeLoader().parse(url, STTextPoint.type, xmlOptions);
        }
        
        public static STTextPoint parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextPoint)getTypeLoader().parse(inputStream, STTextPoint.type, (XmlOptions)null);
        }
        
        public static STTextPoint parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextPoint)getTypeLoader().parse(inputStream, STTextPoint.type, xmlOptions);
        }
        
        public static STTextPoint parse(final Reader reader) throws XmlException, IOException {
            return (STTextPoint)getTypeLoader().parse(reader, STTextPoint.type, (XmlOptions)null);
        }
        
        public static STTextPoint parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextPoint)getTypeLoader().parse(reader, STTextPoint.type, xmlOptions);
        }
        
        public static STTextPoint parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextPoint)getTypeLoader().parse(xmlStreamReader, STTextPoint.type, (XmlOptions)null);
        }
        
        public static STTextPoint parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextPoint)getTypeLoader().parse(xmlStreamReader, STTextPoint.type, xmlOptions);
        }
        
        public static STTextPoint parse(final Node node) throws XmlException {
            return (STTextPoint)getTypeLoader().parse(node, STTextPoint.type, (XmlOptions)null);
        }
        
        public static STTextPoint parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextPoint)getTypeLoader().parse(node, STTextPoint.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextPoint parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextPoint)getTypeLoader().parse(xmlInputStream, STTextPoint.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextPoint parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextPoint)getTypeLoader().parse(xmlInputStream, STTextPoint.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextPoint.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextPoint.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
