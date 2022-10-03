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

public interface STPositiveCoordinate extends XmlLong
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STPositiveCoordinate.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stpositivecoordinatecbfctype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STPositiveCoordinate newValue(final Object o) {
            return (STPositiveCoordinate)STPositiveCoordinate.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STPositiveCoordinate.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STPositiveCoordinate newInstance() {
            return (STPositiveCoordinate)getTypeLoader().newInstance(STPositiveCoordinate.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate newInstance(final XmlOptions xmlOptions) {
            return (STPositiveCoordinate)getTypeLoader().newInstance(STPositiveCoordinate.type, xmlOptions);
        }
        
        public static STPositiveCoordinate parse(final String s) throws XmlException {
            return (STPositiveCoordinate)getTypeLoader().parse(s, STPositiveCoordinate.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STPositiveCoordinate)getTypeLoader().parse(s, STPositiveCoordinate.type, xmlOptions);
        }
        
        public static STPositiveCoordinate parse(final File file) throws XmlException, IOException {
            return (STPositiveCoordinate)getTypeLoader().parse(file, STPositiveCoordinate.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositiveCoordinate)getTypeLoader().parse(file, STPositiveCoordinate.type, xmlOptions);
        }
        
        public static STPositiveCoordinate parse(final URL url) throws XmlException, IOException {
            return (STPositiveCoordinate)getTypeLoader().parse(url, STPositiveCoordinate.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositiveCoordinate)getTypeLoader().parse(url, STPositiveCoordinate.type, xmlOptions);
        }
        
        public static STPositiveCoordinate parse(final InputStream inputStream) throws XmlException, IOException {
            return (STPositiveCoordinate)getTypeLoader().parse(inputStream, STPositiveCoordinate.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositiveCoordinate)getTypeLoader().parse(inputStream, STPositiveCoordinate.type, xmlOptions);
        }
        
        public static STPositiveCoordinate parse(final Reader reader) throws XmlException, IOException {
            return (STPositiveCoordinate)getTypeLoader().parse(reader, STPositiveCoordinate.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositiveCoordinate)getTypeLoader().parse(reader, STPositiveCoordinate.type, xmlOptions);
        }
        
        public static STPositiveCoordinate parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STPositiveCoordinate)getTypeLoader().parse(xmlStreamReader, STPositiveCoordinate.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STPositiveCoordinate)getTypeLoader().parse(xmlStreamReader, STPositiveCoordinate.type, xmlOptions);
        }
        
        public static STPositiveCoordinate parse(final Node node) throws XmlException {
            return (STPositiveCoordinate)getTypeLoader().parse(node, STPositiveCoordinate.type, (XmlOptions)null);
        }
        
        public static STPositiveCoordinate parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STPositiveCoordinate)getTypeLoader().parse(node, STPositiveCoordinate.type, xmlOptions);
        }
        
        @Deprecated
        public static STPositiveCoordinate parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STPositiveCoordinate)getTypeLoader().parse(xmlInputStream, STPositiveCoordinate.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STPositiveCoordinate parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STPositiveCoordinate)getTypeLoader().parse(xmlInputStream, STPositiveCoordinate.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPositiveCoordinate.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPositiveCoordinate.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
