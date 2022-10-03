package org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing;

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

public interface STWrapDistance extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STWrapDistance.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stwrapdistanceea50type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STWrapDistance newValue(final Object o) {
            return (STWrapDistance)STWrapDistance.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STWrapDistance.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STWrapDistance newInstance() {
            return (STWrapDistance)getTypeLoader().newInstance(STWrapDistance.type, (XmlOptions)null);
        }
        
        public static STWrapDistance newInstance(final XmlOptions xmlOptions) {
            return (STWrapDistance)getTypeLoader().newInstance(STWrapDistance.type, xmlOptions);
        }
        
        public static STWrapDistance parse(final String s) throws XmlException {
            return (STWrapDistance)getTypeLoader().parse(s, STWrapDistance.type, (XmlOptions)null);
        }
        
        public static STWrapDistance parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STWrapDistance)getTypeLoader().parse(s, STWrapDistance.type, xmlOptions);
        }
        
        public static STWrapDistance parse(final File file) throws XmlException, IOException {
            return (STWrapDistance)getTypeLoader().parse(file, STWrapDistance.type, (XmlOptions)null);
        }
        
        public static STWrapDistance parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STWrapDistance)getTypeLoader().parse(file, STWrapDistance.type, xmlOptions);
        }
        
        public static STWrapDistance parse(final URL url) throws XmlException, IOException {
            return (STWrapDistance)getTypeLoader().parse(url, STWrapDistance.type, (XmlOptions)null);
        }
        
        public static STWrapDistance parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STWrapDistance)getTypeLoader().parse(url, STWrapDistance.type, xmlOptions);
        }
        
        public static STWrapDistance parse(final InputStream inputStream) throws XmlException, IOException {
            return (STWrapDistance)getTypeLoader().parse(inputStream, STWrapDistance.type, (XmlOptions)null);
        }
        
        public static STWrapDistance parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STWrapDistance)getTypeLoader().parse(inputStream, STWrapDistance.type, xmlOptions);
        }
        
        public static STWrapDistance parse(final Reader reader) throws XmlException, IOException {
            return (STWrapDistance)getTypeLoader().parse(reader, STWrapDistance.type, (XmlOptions)null);
        }
        
        public static STWrapDistance parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STWrapDistance)getTypeLoader().parse(reader, STWrapDistance.type, xmlOptions);
        }
        
        public static STWrapDistance parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STWrapDistance)getTypeLoader().parse(xmlStreamReader, STWrapDistance.type, (XmlOptions)null);
        }
        
        public static STWrapDistance parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STWrapDistance)getTypeLoader().parse(xmlStreamReader, STWrapDistance.type, xmlOptions);
        }
        
        public static STWrapDistance parse(final Node node) throws XmlException {
            return (STWrapDistance)getTypeLoader().parse(node, STWrapDistance.type, (XmlOptions)null);
        }
        
        public static STWrapDistance parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STWrapDistance)getTypeLoader().parse(node, STWrapDistance.type, xmlOptions);
        }
        
        @Deprecated
        public static STWrapDistance parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STWrapDistance)getTypeLoader().parse(xmlInputStream, STWrapDistance.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STWrapDistance parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STWrapDistance)getTypeLoader().parse(xmlInputStream, STWrapDistance.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STWrapDistance.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STWrapDistance.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
