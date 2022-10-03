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
import org.apache.xmlbeans.XmlObject;

public interface StylesDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(StylesDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("styles2732doctype");
    
    CTStyles getStyles();
    
    void setStyles(final CTStyles p0);
    
    CTStyles addNewStyles();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(StylesDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static StylesDocument newInstance() {
            return (StylesDocument)getTypeLoader().newInstance(StylesDocument.type, (XmlOptions)null);
        }
        
        public static StylesDocument newInstance(final XmlOptions xmlOptions) {
            return (StylesDocument)getTypeLoader().newInstance(StylesDocument.type, xmlOptions);
        }
        
        public static StylesDocument parse(final String s) throws XmlException {
            return (StylesDocument)getTypeLoader().parse(s, StylesDocument.type, (XmlOptions)null);
        }
        
        public static StylesDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (StylesDocument)getTypeLoader().parse(s, StylesDocument.type, xmlOptions);
        }
        
        public static StylesDocument parse(final File file) throws XmlException, IOException {
            return (StylesDocument)getTypeLoader().parse(file, StylesDocument.type, (XmlOptions)null);
        }
        
        public static StylesDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StylesDocument)getTypeLoader().parse(file, StylesDocument.type, xmlOptions);
        }
        
        public static StylesDocument parse(final URL url) throws XmlException, IOException {
            return (StylesDocument)getTypeLoader().parse(url, StylesDocument.type, (XmlOptions)null);
        }
        
        public static StylesDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StylesDocument)getTypeLoader().parse(url, StylesDocument.type, xmlOptions);
        }
        
        public static StylesDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (StylesDocument)getTypeLoader().parse(inputStream, StylesDocument.type, (XmlOptions)null);
        }
        
        public static StylesDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StylesDocument)getTypeLoader().parse(inputStream, StylesDocument.type, xmlOptions);
        }
        
        public static StylesDocument parse(final Reader reader) throws XmlException, IOException {
            return (StylesDocument)getTypeLoader().parse(reader, StylesDocument.type, (XmlOptions)null);
        }
        
        public static StylesDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StylesDocument)getTypeLoader().parse(reader, StylesDocument.type, xmlOptions);
        }
        
        public static StylesDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (StylesDocument)getTypeLoader().parse(xmlStreamReader, StylesDocument.type, (XmlOptions)null);
        }
        
        public static StylesDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (StylesDocument)getTypeLoader().parse(xmlStreamReader, StylesDocument.type, xmlOptions);
        }
        
        public static StylesDocument parse(final Node node) throws XmlException {
            return (StylesDocument)getTypeLoader().parse(node, StylesDocument.type, (XmlOptions)null);
        }
        
        public static StylesDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (StylesDocument)getTypeLoader().parse(node, StylesDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static StylesDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (StylesDocument)getTypeLoader().parse(xmlInputStream, StylesDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static StylesDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (StylesDocument)getTypeLoader().parse(xmlInputStream, StylesDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, StylesDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, StylesDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
