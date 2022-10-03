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
import org.apache.xmlbeans.XmlObject;

public interface CTBaseStyles extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBaseStyles.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbasestyles122etype");
    
    CTColorScheme getClrScheme();
    
    void setClrScheme(final CTColorScheme p0);
    
    CTColorScheme addNewClrScheme();
    
    CTFontScheme getFontScheme();
    
    void setFontScheme(final CTFontScheme p0);
    
    CTFontScheme addNewFontScheme();
    
    CTStyleMatrix getFmtScheme();
    
    void setFmtScheme(final CTStyleMatrix p0);
    
    CTStyleMatrix addNewFmtScheme();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBaseStyles.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBaseStyles newInstance() {
            return (CTBaseStyles)getTypeLoader().newInstance(CTBaseStyles.type, (XmlOptions)null);
        }
        
        public static CTBaseStyles newInstance(final XmlOptions xmlOptions) {
            return (CTBaseStyles)getTypeLoader().newInstance(CTBaseStyles.type, xmlOptions);
        }
        
        public static CTBaseStyles parse(final String s) throws XmlException {
            return (CTBaseStyles)getTypeLoader().parse(s, CTBaseStyles.type, (XmlOptions)null);
        }
        
        public static CTBaseStyles parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBaseStyles)getTypeLoader().parse(s, CTBaseStyles.type, xmlOptions);
        }
        
        public static CTBaseStyles parse(final File file) throws XmlException, IOException {
            return (CTBaseStyles)getTypeLoader().parse(file, CTBaseStyles.type, (XmlOptions)null);
        }
        
        public static CTBaseStyles parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBaseStyles)getTypeLoader().parse(file, CTBaseStyles.type, xmlOptions);
        }
        
        public static CTBaseStyles parse(final URL url) throws XmlException, IOException {
            return (CTBaseStyles)getTypeLoader().parse(url, CTBaseStyles.type, (XmlOptions)null);
        }
        
        public static CTBaseStyles parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBaseStyles)getTypeLoader().parse(url, CTBaseStyles.type, xmlOptions);
        }
        
        public static CTBaseStyles parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBaseStyles)getTypeLoader().parse(inputStream, CTBaseStyles.type, (XmlOptions)null);
        }
        
        public static CTBaseStyles parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBaseStyles)getTypeLoader().parse(inputStream, CTBaseStyles.type, xmlOptions);
        }
        
        public static CTBaseStyles parse(final Reader reader) throws XmlException, IOException {
            return (CTBaseStyles)getTypeLoader().parse(reader, CTBaseStyles.type, (XmlOptions)null);
        }
        
        public static CTBaseStyles parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBaseStyles)getTypeLoader().parse(reader, CTBaseStyles.type, xmlOptions);
        }
        
        public static CTBaseStyles parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBaseStyles)getTypeLoader().parse(xmlStreamReader, CTBaseStyles.type, (XmlOptions)null);
        }
        
        public static CTBaseStyles parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBaseStyles)getTypeLoader().parse(xmlStreamReader, CTBaseStyles.type, xmlOptions);
        }
        
        public static CTBaseStyles parse(final Node node) throws XmlException {
            return (CTBaseStyles)getTypeLoader().parse(node, CTBaseStyles.type, (XmlOptions)null);
        }
        
        public static CTBaseStyles parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBaseStyles)getTypeLoader().parse(node, CTBaseStyles.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBaseStyles parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBaseStyles)getTypeLoader().parse(xmlInputStream, CTBaseStyles.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBaseStyles parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBaseStyles)getTypeLoader().parse(xmlInputStream, CTBaseStyles.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBaseStyles.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBaseStyles.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
