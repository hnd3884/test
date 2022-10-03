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
import org.apache.xmlbeans.XmlAnySimpleType;

public interface STHexColor extends XmlAnySimpleType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STHexColor.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sthexcolor55d0type");
    
    Object getObjectValue();
    
    void setObjectValue(final Object p0);
    
    @Deprecated
    Object objectValue();
    
    @Deprecated
    void objectSet(final Object p0);
    
    SchemaType instanceType();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STHexColor newValue(final Object o) {
            return (STHexColor)STHexColor.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STHexColor.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STHexColor newInstance() {
            return (STHexColor)getTypeLoader().newInstance(STHexColor.type, (XmlOptions)null);
        }
        
        public static STHexColor newInstance(final XmlOptions xmlOptions) {
            return (STHexColor)getTypeLoader().newInstance(STHexColor.type, xmlOptions);
        }
        
        public static STHexColor parse(final String s) throws XmlException {
            return (STHexColor)getTypeLoader().parse(s, STHexColor.type, (XmlOptions)null);
        }
        
        public static STHexColor parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STHexColor)getTypeLoader().parse(s, STHexColor.type, xmlOptions);
        }
        
        public static STHexColor parse(final File file) throws XmlException, IOException {
            return (STHexColor)getTypeLoader().parse(file, STHexColor.type, (XmlOptions)null);
        }
        
        public static STHexColor parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHexColor)getTypeLoader().parse(file, STHexColor.type, xmlOptions);
        }
        
        public static STHexColor parse(final URL url) throws XmlException, IOException {
            return (STHexColor)getTypeLoader().parse(url, STHexColor.type, (XmlOptions)null);
        }
        
        public static STHexColor parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHexColor)getTypeLoader().parse(url, STHexColor.type, xmlOptions);
        }
        
        public static STHexColor parse(final InputStream inputStream) throws XmlException, IOException {
            return (STHexColor)getTypeLoader().parse(inputStream, STHexColor.type, (XmlOptions)null);
        }
        
        public static STHexColor parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHexColor)getTypeLoader().parse(inputStream, STHexColor.type, xmlOptions);
        }
        
        public static STHexColor parse(final Reader reader) throws XmlException, IOException {
            return (STHexColor)getTypeLoader().parse(reader, STHexColor.type, (XmlOptions)null);
        }
        
        public static STHexColor parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHexColor)getTypeLoader().parse(reader, STHexColor.type, xmlOptions);
        }
        
        public static STHexColor parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STHexColor)getTypeLoader().parse(xmlStreamReader, STHexColor.type, (XmlOptions)null);
        }
        
        public static STHexColor parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STHexColor)getTypeLoader().parse(xmlStreamReader, STHexColor.type, xmlOptions);
        }
        
        public static STHexColor parse(final Node node) throws XmlException {
            return (STHexColor)getTypeLoader().parse(node, STHexColor.type, (XmlOptions)null);
        }
        
        public static STHexColor parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STHexColor)getTypeLoader().parse(node, STHexColor.type, xmlOptions);
        }
        
        @Deprecated
        public static STHexColor parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STHexColor)getTypeLoader().parse(xmlInputStream, STHexColor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STHexColor parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STHexColor)getTypeLoader().parse(xmlInputStream, STHexColor.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHexColor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHexColor.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
