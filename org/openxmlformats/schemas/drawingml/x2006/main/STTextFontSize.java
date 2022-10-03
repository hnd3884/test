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

public interface STTextFontSize extends XmlInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextFontSize.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextfontsizeb3a8type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextFontSize newValue(final Object o) {
            return (STTextFontSize)STTextFontSize.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextFontSize.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextFontSize newInstance() {
            return (STTextFontSize)getTypeLoader().newInstance(STTextFontSize.type, (XmlOptions)null);
        }
        
        public static STTextFontSize newInstance(final XmlOptions xmlOptions) {
            return (STTextFontSize)getTypeLoader().newInstance(STTextFontSize.type, xmlOptions);
        }
        
        public static STTextFontSize parse(final String s) throws XmlException {
            return (STTextFontSize)getTypeLoader().parse(s, STTextFontSize.type, (XmlOptions)null);
        }
        
        public static STTextFontSize parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextFontSize)getTypeLoader().parse(s, STTextFontSize.type, xmlOptions);
        }
        
        public static STTextFontSize parse(final File file) throws XmlException, IOException {
            return (STTextFontSize)getTypeLoader().parse(file, STTextFontSize.type, (XmlOptions)null);
        }
        
        public static STTextFontSize parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextFontSize)getTypeLoader().parse(file, STTextFontSize.type, xmlOptions);
        }
        
        public static STTextFontSize parse(final URL url) throws XmlException, IOException {
            return (STTextFontSize)getTypeLoader().parse(url, STTextFontSize.type, (XmlOptions)null);
        }
        
        public static STTextFontSize parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextFontSize)getTypeLoader().parse(url, STTextFontSize.type, xmlOptions);
        }
        
        public static STTextFontSize parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextFontSize)getTypeLoader().parse(inputStream, STTextFontSize.type, (XmlOptions)null);
        }
        
        public static STTextFontSize parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextFontSize)getTypeLoader().parse(inputStream, STTextFontSize.type, xmlOptions);
        }
        
        public static STTextFontSize parse(final Reader reader) throws XmlException, IOException {
            return (STTextFontSize)getTypeLoader().parse(reader, STTextFontSize.type, (XmlOptions)null);
        }
        
        public static STTextFontSize parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextFontSize)getTypeLoader().parse(reader, STTextFontSize.type, xmlOptions);
        }
        
        public static STTextFontSize parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextFontSize)getTypeLoader().parse(xmlStreamReader, STTextFontSize.type, (XmlOptions)null);
        }
        
        public static STTextFontSize parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextFontSize)getTypeLoader().parse(xmlStreamReader, STTextFontSize.type, xmlOptions);
        }
        
        public static STTextFontSize parse(final Node node) throws XmlException {
            return (STTextFontSize)getTypeLoader().parse(node, STTextFontSize.type, (XmlOptions)null);
        }
        
        public static STTextFontSize parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextFontSize)getTypeLoader().parse(node, STTextFontSize.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextFontSize parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextFontSize)getTypeLoader().parse(xmlInputStream, STTextFontSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextFontSize parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextFontSize)getTypeLoader().parse(xmlInputStream, STTextFontSize.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextFontSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextFontSize.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
