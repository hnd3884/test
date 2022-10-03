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

public interface STTextIndentLevelType extends XmlInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextIndentLevelType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextindentleveltypeaf86type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextIndentLevelType newValue(final Object o) {
            return (STTextIndentLevelType)STTextIndentLevelType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextIndentLevelType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextIndentLevelType newInstance() {
            return (STTextIndentLevelType)getTypeLoader().newInstance(STTextIndentLevelType.type, (XmlOptions)null);
        }
        
        public static STTextIndentLevelType newInstance(final XmlOptions xmlOptions) {
            return (STTextIndentLevelType)getTypeLoader().newInstance(STTextIndentLevelType.type, xmlOptions);
        }
        
        public static STTextIndentLevelType parse(final String s) throws XmlException {
            return (STTextIndentLevelType)getTypeLoader().parse(s, STTextIndentLevelType.type, (XmlOptions)null);
        }
        
        public static STTextIndentLevelType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextIndentLevelType)getTypeLoader().parse(s, STTextIndentLevelType.type, xmlOptions);
        }
        
        public static STTextIndentLevelType parse(final File file) throws XmlException, IOException {
            return (STTextIndentLevelType)getTypeLoader().parse(file, STTextIndentLevelType.type, (XmlOptions)null);
        }
        
        public static STTextIndentLevelType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextIndentLevelType)getTypeLoader().parse(file, STTextIndentLevelType.type, xmlOptions);
        }
        
        public static STTextIndentLevelType parse(final URL url) throws XmlException, IOException {
            return (STTextIndentLevelType)getTypeLoader().parse(url, STTextIndentLevelType.type, (XmlOptions)null);
        }
        
        public static STTextIndentLevelType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextIndentLevelType)getTypeLoader().parse(url, STTextIndentLevelType.type, xmlOptions);
        }
        
        public static STTextIndentLevelType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextIndentLevelType)getTypeLoader().parse(inputStream, STTextIndentLevelType.type, (XmlOptions)null);
        }
        
        public static STTextIndentLevelType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextIndentLevelType)getTypeLoader().parse(inputStream, STTextIndentLevelType.type, xmlOptions);
        }
        
        public static STTextIndentLevelType parse(final Reader reader) throws XmlException, IOException {
            return (STTextIndentLevelType)getTypeLoader().parse(reader, STTextIndentLevelType.type, (XmlOptions)null);
        }
        
        public static STTextIndentLevelType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextIndentLevelType)getTypeLoader().parse(reader, STTextIndentLevelType.type, xmlOptions);
        }
        
        public static STTextIndentLevelType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextIndentLevelType)getTypeLoader().parse(xmlStreamReader, STTextIndentLevelType.type, (XmlOptions)null);
        }
        
        public static STTextIndentLevelType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextIndentLevelType)getTypeLoader().parse(xmlStreamReader, STTextIndentLevelType.type, xmlOptions);
        }
        
        public static STTextIndentLevelType parse(final Node node) throws XmlException {
            return (STTextIndentLevelType)getTypeLoader().parse(node, STTextIndentLevelType.type, (XmlOptions)null);
        }
        
        public static STTextIndentLevelType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextIndentLevelType)getTypeLoader().parse(node, STTextIndentLevelType.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextIndentLevelType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextIndentLevelType)getTypeLoader().parse(xmlInputStream, STTextIndentLevelType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextIndentLevelType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextIndentLevelType)getTypeLoader().parse(xmlInputStream, STTextIndentLevelType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextIndentLevelType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextIndentLevelType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
