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

public interface CTSdtDocPart extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSdtDocPart.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsdtdocpartcea0type");
    
    CTString getDocPartGallery();
    
    boolean isSetDocPartGallery();
    
    void setDocPartGallery(final CTString p0);
    
    CTString addNewDocPartGallery();
    
    void unsetDocPartGallery();
    
    CTString getDocPartCategory();
    
    boolean isSetDocPartCategory();
    
    void setDocPartCategory(final CTString p0);
    
    CTString addNewDocPartCategory();
    
    void unsetDocPartCategory();
    
    CTOnOff getDocPartUnique();
    
    boolean isSetDocPartUnique();
    
    void setDocPartUnique(final CTOnOff p0);
    
    CTOnOff addNewDocPartUnique();
    
    void unsetDocPartUnique();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSdtDocPart.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSdtDocPart newInstance() {
            return (CTSdtDocPart)getTypeLoader().newInstance(CTSdtDocPart.type, (XmlOptions)null);
        }
        
        public static CTSdtDocPart newInstance(final XmlOptions xmlOptions) {
            return (CTSdtDocPart)getTypeLoader().newInstance(CTSdtDocPart.type, xmlOptions);
        }
        
        public static CTSdtDocPart parse(final String s) throws XmlException {
            return (CTSdtDocPart)getTypeLoader().parse(s, CTSdtDocPart.type, (XmlOptions)null);
        }
        
        public static CTSdtDocPart parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtDocPart)getTypeLoader().parse(s, CTSdtDocPart.type, xmlOptions);
        }
        
        public static CTSdtDocPart parse(final File file) throws XmlException, IOException {
            return (CTSdtDocPart)getTypeLoader().parse(file, CTSdtDocPart.type, (XmlOptions)null);
        }
        
        public static CTSdtDocPart parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtDocPart)getTypeLoader().parse(file, CTSdtDocPart.type, xmlOptions);
        }
        
        public static CTSdtDocPart parse(final URL url) throws XmlException, IOException {
            return (CTSdtDocPart)getTypeLoader().parse(url, CTSdtDocPart.type, (XmlOptions)null);
        }
        
        public static CTSdtDocPart parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtDocPart)getTypeLoader().parse(url, CTSdtDocPart.type, xmlOptions);
        }
        
        public static CTSdtDocPart parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSdtDocPart)getTypeLoader().parse(inputStream, CTSdtDocPart.type, (XmlOptions)null);
        }
        
        public static CTSdtDocPart parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtDocPart)getTypeLoader().parse(inputStream, CTSdtDocPart.type, xmlOptions);
        }
        
        public static CTSdtDocPart parse(final Reader reader) throws XmlException, IOException {
            return (CTSdtDocPart)getTypeLoader().parse(reader, CTSdtDocPart.type, (XmlOptions)null);
        }
        
        public static CTSdtDocPart parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtDocPart)getTypeLoader().parse(reader, CTSdtDocPart.type, xmlOptions);
        }
        
        public static CTSdtDocPart parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSdtDocPart)getTypeLoader().parse(xmlStreamReader, CTSdtDocPart.type, (XmlOptions)null);
        }
        
        public static CTSdtDocPart parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtDocPart)getTypeLoader().parse(xmlStreamReader, CTSdtDocPart.type, xmlOptions);
        }
        
        public static CTSdtDocPart parse(final Node node) throws XmlException {
            return (CTSdtDocPart)getTypeLoader().parse(node, CTSdtDocPart.type, (XmlOptions)null);
        }
        
        public static CTSdtDocPart parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtDocPart)getTypeLoader().parse(node, CTSdtDocPart.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSdtDocPart parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSdtDocPart)getTypeLoader().parse(xmlInputStream, CTSdtDocPart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSdtDocPart parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSdtDocPart)getTypeLoader().parse(xmlInputStream, CTSdtDocPart.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSdtDocPart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSdtDocPart.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
