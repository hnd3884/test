package com.microsoft.schemas.office.visio.x2012.main;

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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface StyleSheetsType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(StyleSheetsType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stylesheetstypeb706type");
    
    List<StyleSheetType> getStyleSheetList();
    
    @Deprecated
    StyleSheetType[] getStyleSheetArray();
    
    StyleSheetType getStyleSheetArray(final int p0);
    
    int sizeOfStyleSheetArray();
    
    void setStyleSheetArray(final StyleSheetType[] p0);
    
    void setStyleSheetArray(final int p0, final StyleSheetType p1);
    
    StyleSheetType insertNewStyleSheet(final int p0);
    
    StyleSheetType addNewStyleSheet();
    
    void removeStyleSheet(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(StyleSheetsType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static StyleSheetsType newInstance() {
            return (StyleSheetsType)getTypeLoader().newInstance(StyleSheetsType.type, (XmlOptions)null);
        }
        
        public static StyleSheetsType newInstance(final XmlOptions xmlOptions) {
            return (StyleSheetsType)getTypeLoader().newInstance(StyleSheetsType.type, xmlOptions);
        }
        
        public static StyleSheetsType parse(final String s) throws XmlException {
            return (StyleSheetsType)getTypeLoader().parse(s, StyleSheetsType.type, (XmlOptions)null);
        }
        
        public static StyleSheetsType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (StyleSheetsType)getTypeLoader().parse(s, StyleSheetsType.type, xmlOptions);
        }
        
        public static StyleSheetsType parse(final File file) throws XmlException, IOException {
            return (StyleSheetsType)getTypeLoader().parse(file, StyleSheetsType.type, (XmlOptions)null);
        }
        
        public static StyleSheetsType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StyleSheetsType)getTypeLoader().parse(file, StyleSheetsType.type, xmlOptions);
        }
        
        public static StyleSheetsType parse(final URL url) throws XmlException, IOException {
            return (StyleSheetsType)getTypeLoader().parse(url, StyleSheetsType.type, (XmlOptions)null);
        }
        
        public static StyleSheetsType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StyleSheetsType)getTypeLoader().parse(url, StyleSheetsType.type, xmlOptions);
        }
        
        public static StyleSheetsType parse(final InputStream inputStream) throws XmlException, IOException {
            return (StyleSheetsType)getTypeLoader().parse(inputStream, StyleSheetsType.type, (XmlOptions)null);
        }
        
        public static StyleSheetsType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StyleSheetsType)getTypeLoader().parse(inputStream, StyleSheetsType.type, xmlOptions);
        }
        
        public static StyleSheetsType parse(final Reader reader) throws XmlException, IOException {
            return (StyleSheetsType)getTypeLoader().parse(reader, StyleSheetsType.type, (XmlOptions)null);
        }
        
        public static StyleSheetsType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StyleSheetsType)getTypeLoader().parse(reader, StyleSheetsType.type, xmlOptions);
        }
        
        public static StyleSheetsType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (StyleSheetsType)getTypeLoader().parse(xmlStreamReader, StyleSheetsType.type, (XmlOptions)null);
        }
        
        public static StyleSheetsType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (StyleSheetsType)getTypeLoader().parse(xmlStreamReader, StyleSheetsType.type, xmlOptions);
        }
        
        public static StyleSheetsType parse(final Node node) throws XmlException {
            return (StyleSheetsType)getTypeLoader().parse(node, StyleSheetsType.type, (XmlOptions)null);
        }
        
        public static StyleSheetsType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (StyleSheetsType)getTypeLoader().parse(node, StyleSheetsType.type, xmlOptions);
        }
        
        @Deprecated
        public static StyleSheetsType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (StyleSheetsType)getTypeLoader().parse(xmlInputStream, StyleSheetsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static StyleSheetsType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (StyleSheetsType)getTypeLoader().parse(xmlInputStream, StyleSheetsType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, StyleSheetsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, StyleSheetsType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
