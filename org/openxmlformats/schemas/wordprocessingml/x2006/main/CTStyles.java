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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTStyles extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTStyles.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctstyles8506type");
    
    CTDocDefaults getDocDefaults();
    
    boolean isSetDocDefaults();
    
    void setDocDefaults(final CTDocDefaults p0);
    
    CTDocDefaults addNewDocDefaults();
    
    void unsetDocDefaults();
    
    CTLatentStyles getLatentStyles();
    
    boolean isSetLatentStyles();
    
    void setLatentStyles(final CTLatentStyles p0);
    
    CTLatentStyles addNewLatentStyles();
    
    void unsetLatentStyles();
    
    List<CTStyle> getStyleList();
    
    @Deprecated
    CTStyle[] getStyleArray();
    
    CTStyle getStyleArray(final int p0);
    
    int sizeOfStyleArray();
    
    void setStyleArray(final CTStyle[] p0);
    
    void setStyleArray(final int p0, final CTStyle p1);
    
    CTStyle insertNewStyle(final int p0);
    
    CTStyle addNewStyle();
    
    void removeStyle(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTStyles.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTStyles newInstance() {
            return (CTStyles)getTypeLoader().newInstance(CTStyles.type, (XmlOptions)null);
        }
        
        public static CTStyles newInstance(final XmlOptions xmlOptions) {
            return (CTStyles)getTypeLoader().newInstance(CTStyles.type, xmlOptions);
        }
        
        public static CTStyles parse(final String s) throws XmlException {
            return (CTStyles)getTypeLoader().parse(s, CTStyles.type, (XmlOptions)null);
        }
        
        public static CTStyles parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTStyles)getTypeLoader().parse(s, CTStyles.type, xmlOptions);
        }
        
        public static CTStyles parse(final File file) throws XmlException, IOException {
            return (CTStyles)getTypeLoader().parse(file, CTStyles.type, (XmlOptions)null);
        }
        
        public static CTStyles parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyles)getTypeLoader().parse(file, CTStyles.type, xmlOptions);
        }
        
        public static CTStyles parse(final URL url) throws XmlException, IOException {
            return (CTStyles)getTypeLoader().parse(url, CTStyles.type, (XmlOptions)null);
        }
        
        public static CTStyles parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyles)getTypeLoader().parse(url, CTStyles.type, xmlOptions);
        }
        
        public static CTStyles parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTStyles)getTypeLoader().parse(inputStream, CTStyles.type, (XmlOptions)null);
        }
        
        public static CTStyles parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyles)getTypeLoader().parse(inputStream, CTStyles.type, xmlOptions);
        }
        
        public static CTStyles parse(final Reader reader) throws XmlException, IOException {
            return (CTStyles)getTypeLoader().parse(reader, CTStyles.type, (XmlOptions)null);
        }
        
        public static CTStyles parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyles)getTypeLoader().parse(reader, CTStyles.type, xmlOptions);
        }
        
        public static CTStyles parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTStyles)getTypeLoader().parse(xmlStreamReader, CTStyles.type, (XmlOptions)null);
        }
        
        public static CTStyles parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTStyles)getTypeLoader().parse(xmlStreamReader, CTStyles.type, xmlOptions);
        }
        
        public static CTStyles parse(final Node node) throws XmlException {
            return (CTStyles)getTypeLoader().parse(node, CTStyles.type, (XmlOptions)null);
        }
        
        public static CTStyles parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTStyles)getTypeLoader().parse(node, CTStyles.type, xmlOptions);
        }
        
        @Deprecated
        public static CTStyles parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTStyles)getTypeLoader().parse(xmlInputStream, CTStyles.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTStyles parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTStyles)getTypeLoader().parse(xmlInputStream, CTStyles.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStyles.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStyles.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
