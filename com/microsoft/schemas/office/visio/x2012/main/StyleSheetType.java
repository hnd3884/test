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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;

public interface StyleSheetType extends SheetType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(StyleSheetType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stylesheettypeebcbtype");
    
    long getID();
    
    XmlUnsignedInt xgetID();
    
    void setID(final long p0);
    
    void xsetID(final XmlUnsignedInt p0);
    
    String getName();
    
    XmlString xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlString p0);
    
    void unsetName();
    
    String getNameU();
    
    XmlString xgetNameU();
    
    boolean isSetNameU();
    
    void setNameU(final String p0);
    
    void xsetNameU(final XmlString p0);
    
    void unsetNameU();
    
    boolean getIsCustomName();
    
    XmlBoolean xgetIsCustomName();
    
    boolean isSetIsCustomName();
    
    void setIsCustomName(final boolean p0);
    
    void xsetIsCustomName(final XmlBoolean p0);
    
    void unsetIsCustomName();
    
    boolean getIsCustomNameU();
    
    XmlBoolean xgetIsCustomNameU();
    
    boolean isSetIsCustomNameU();
    
    void setIsCustomNameU(final boolean p0);
    
    void xsetIsCustomNameU(final XmlBoolean p0);
    
    void unsetIsCustomNameU();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(StyleSheetType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static StyleSheetType newInstance() {
            return (StyleSheetType)getTypeLoader().newInstance(StyleSheetType.type, (XmlOptions)null);
        }
        
        public static StyleSheetType newInstance(final XmlOptions xmlOptions) {
            return (StyleSheetType)getTypeLoader().newInstance(StyleSheetType.type, xmlOptions);
        }
        
        public static StyleSheetType parse(final String s) throws XmlException {
            return (StyleSheetType)getTypeLoader().parse(s, StyleSheetType.type, (XmlOptions)null);
        }
        
        public static StyleSheetType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (StyleSheetType)getTypeLoader().parse(s, StyleSheetType.type, xmlOptions);
        }
        
        public static StyleSheetType parse(final File file) throws XmlException, IOException {
            return (StyleSheetType)getTypeLoader().parse(file, StyleSheetType.type, (XmlOptions)null);
        }
        
        public static StyleSheetType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StyleSheetType)getTypeLoader().parse(file, StyleSheetType.type, xmlOptions);
        }
        
        public static StyleSheetType parse(final URL url) throws XmlException, IOException {
            return (StyleSheetType)getTypeLoader().parse(url, StyleSheetType.type, (XmlOptions)null);
        }
        
        public static StyleSheetType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StyleSheetType)getTypeLoader().parse(url, StyleSheetType.type, xmlOptions);
        }
        
        public static StyleSheetType parse(final InputStream inputStream) throws XmlException, IOException {
            return (StyleSheetType)getTypeLoader().parse(inputStream, StyleSheetType.type, (XmlOptions)null);
        }
        
        public static StyleSheetType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StyleSheetType)getTypeLoader().parse(inputStream, StyleSheetType.type, xmlOptions);
        }
        
        public static StyleSheetType parse(final Reader reader) throws XmlException, IOException {
            return (StyleSheetType)getTypeLoader().parse(reader, StyleSheetType.type, (XmlOptions)null);
        }
        
        public static StyleSheetType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (StyleSheetType)getTypeLoader().parse(reader, StyleSheetType.type, xmlOptions);
        }
        
        public static StyleSheetType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (StyleSheetType)getTypeLoader().parse(xmlStreamReader, StyleSheetType.type, (XmlOptions)null);
        }
        
        public static StyleSheetType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (StyleSheetType)getTypeLoader().parse(xmlStreamReader, StyleSheetType.type, xmlOptions);
        }
        
        public static StyleSheetType parse(final Node node) throws XmlException {
            return (StyleSheetType)getTypeLoader().parse(node, StyleSheetType.type, (XmlOptions)null);
        }
        
        public static StyleSheetType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (StyleSheetType)getTypeLoader().parse(node, StyleSheetType.type, xmlOptions);
        }
        
        @Deprecated
        public static StyleSheetType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (StyleSheetType)getTypeLoader().parse(xmlInputStream, StyleSheetType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static StyleSheetType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (StyleSheetType)getTypeLoader().parse(xmlInputStream, StyleSheetType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, StyleSheetType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, StyleSheetType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
