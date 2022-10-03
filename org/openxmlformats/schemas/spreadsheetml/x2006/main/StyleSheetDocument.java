package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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
import org.apache.xmlbeans.XmlObject;

public interface StyleSheetDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(StyleSheetDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stylesheet5d8bdoctype");
    
    CTStylesheet getStyleSheet();
    
    void setStyleSheet(final CTStylesheet p0);
    
    CTStylesheet addNewStyleSheet();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(StyleSheetDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static StyleSheetDocument newInstance() {
            return (StyleSheetDocument)getTypeLoader().newInstance(StyleSheetDocument.type, (XmlOptions)null);
        }
        
        public static StyleSheetDocument newInstance(final XmlOptions xmlOptions) {
            return (StyleSheetDocument)getTypeLoader().newInstance(StyleSheetDocument.type, xmlOptions);
        }
        
        public static StyleSheetDocument parse(final String s) throws XmlException {
            return (StyleSheetDocument)getTypeLoader().parse(s, StyleSheetDocument.type, (XmlOptions)null);
        }
        
        public static StyleSheetDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (StyleSheetDocument)getTypeLoader().parse(s, StyleSheetDocument.type, xmlOptions);
        }
        
        public static StyleSheetDocument parse(final File file) throws XmlException, IOException {
            return (StyleSheetDocument)getTypeLoader().parse(file, StyleSheetDocument.type, (XmlOptions)null);
        }
        
        public static StyleSheetDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StyleSheetDocument)getTypeLoader().parse(file, StyleSheetDocument.type, xmlOptions);
        }
        
        public static StyleSheetDocument parse(final URL url) throws XmlException, IOException {
            return (StyleSheetDocument)getTypeLoader().parse(url, StyleSheetDocument.type, (XmlOptions)null);
        }
        
        public static StyleSheetDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StyleSheetDocument)getTypeLoader().parse(url, StyleSheetDocument.type, xmlOptions);
        }
        
        public static StyleSheetDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (StyleSheetDocument)getTypeLoader().parse(inputStream, StyleSheetDocument.type, (XmlOptions)null);
        }
        
        public static StyleSheetDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StyleSheetDocument)getTypeLoader().parse(inputStream, StyleSheetDocument.type, xmlOptions);
        }
        
        public static StyleSheetDocument parse(final Reader reader) throws XmlException, IOException {
            return (StyleSheetDocument)getTypeLoader().parse(reader, StyleSheetDocument.type, (XmlOptions)null);
        }
        
        public static StyleSheetDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StyleSheetDocument)getTypeLoader().parse(reader, StyleSheetDocument.type, xmlOptions);
        }
        
        public static StyleSheetDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (StyleSheetDocument)getTypeLoader().parse(xmlStreamReader, StyleSheetDocument.type, (XmlOptions)null);
        }
        
        public static StyleSheetDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (StyleSheetDocument)getTypeLoader().parse(xmlStreamReader, StyleSheetDocument.type, xmlOptions);
        }
        
        public static StyleSheetDocument parse(final Node node) throws XmlException {
            return (StyleSheetDocument)getTypeLoader().parse(node, StyleSheetDocument.type, (XmlOptions)null);
        }
        
        public static StyleSheetDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (StyleSheetDocument)getTypeLoader().parse(node, StyleSheetDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static StyleSheetDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (StyleSheetDocument)getTypeLoader().parse(xmlInputStream, StyleSheetDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static StyleSheetDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (StyleSheetDocument)getTypeLoader().parse(xmlInputStream, StyleSheetDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, StyleSheetDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, StyleSheetDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
