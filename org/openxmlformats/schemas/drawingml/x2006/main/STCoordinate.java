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
import org.apache.xmlbeans.XmlLong;

public interface STCoordinate extends XmlLong
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCoordinate.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcoordinatefae3type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCoordinate newValue(final Object o) {
            return (STCoordinate)STCoordinate.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCoordinate.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCoordinate newInstance() {
            return (STCoordinate)getTypeLoader().newInstance(STCoordinate.type, (XmlOptions)null);
        }
        
        public static STCoordinate newInstance(final XmlOptions xmlOptions) {
            return (STCoordinate)getTypeLoader().newInstance(STCoordinate.type, xmlOptions);
        }
        
        public static STCoordinate parse(final String s) throws XmlException {
            return (STCoordinate)getTypeLoader().parse(s, STCoordinate.type, (XmlOptions)null);
        }
        
        public static STCoordinate parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCoordinate)getTypeLoader().parse(s, STCoordinate.type, xmlOptions);
        }
        
        public static STCoordinate parse(final File file) throws XmlException, IOException {
            return (STCoordinate)getTypeLoader().parse(file, STCoordinate.type, (XmlOptions)null);
        }
        
        public static STCoordinate parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCoordinate)getTypeLoader().parse(file, STCoordinate.type, xmlOptions);
        }
        
        public static STCoordinate parse(final URL url) throws XmlException, IOException {
            return (STCoordinate)getTypeLoader().parse(url, STCoordinate.type, (XmlOptions)null);
        }
        
        public static STCoordinate parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCoordinate)getTypeLoader().parse(url, STCoordinate.type, xmlOptions);
        }
        
        public static STCoordinate parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCoordinate)getTypeLoader().parse(inputStream, STCoordinate.type, (XmlOptions)null);
        }
        
        public static STCoordinate parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCoordinate)getTypeLoader().parse(inputStream, STCoordinate.type, xmlOptions);
        }
        
        public static STCoordinate parse(final Reader reader) throws XmlException, IOException {
            return (STCoordinate)getTypeLoader().parse(reader, STCoordinate.type, (XmlOptions)null);
        }
        
        public static STCoordinate parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCoordinate)getTypeLoader().parse(reader, STCoordinate.type, xmlOptions);
        }
        
        public static STCoordinate parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCoordinate)getTypeLoader().parse(xmlStreamReader, STCoordinate.type, (XmlOptions)null);
        }
        
        public static STCoordinate parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCoordinate)getTypeLoader().parse(xmlStreamReader, STCoordinate.type, xmlOptions);
        }
        
        public static STCoordinate parse(final Node node) throws XmlException {
            return (STCoordinate)getTypeLoader().parse(node, STCoordinate.type, (XmlOptions)null);
        }
        
        public static STCoordinate parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCoordinate)getTypeLoader().parse(node, STCoordinate.type, xmlOptions);
        }
        
        @Deprecated
        public static STCoordinate parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCoordinate)getTypeLoader().parse(xmlInputStream, STCoordinate.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCoordinate parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCoordinate)getTypeLoader().parse(xmlInputStream, STCoordinate.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCoordinate.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCoordinate.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
