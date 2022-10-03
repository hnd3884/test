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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTNonVisualPictureProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNonVisualPictureProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnonvisualpicturepropertiesee3ftype");
    
    CTPictureLocking getPicLocks();
    
    boolean isSetPicLocks();
    
    void setPicLocks(final CTPictureLocking p0);
    
    CTPictureLocking addNewPicLocks();
    
    void unsetPicLocks();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    boolean getPreferRelativeResize();
    
    XmlBoolean xgetPreferRelativeResize();
    
    boolean isSetPreferRelativeResize();
    
    void setPreferRelativeResize(final boolean p0);
    
    void xsetPreferRelativeResize(final XmlBoolean p0);
    
    void unsetPreferRelativeResize();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNonVisualPictureProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNonVisualPictureProperties newInstance() {
            return (CTNonVisualPictureProperties)getTypeLoader().newInstance(CTNonVisualPictureProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualPictureProperties newInstance(final XmlOptions xmlOptions) {
            return (CTNonVisualPictureProperties)getTypeLoader().newInstance(CTNonVisualPictureProperties.type, xmlOptions);
        }
        
        public static CTNonVisualPictureProperties parse(final String s) throws XmlException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(s, CTNonVisualPictureProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualPictureProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(s, CTNonVisualPictureProperties.type, xmlOptions);
        }
        
        public static CTNonVisualPictureProperties parse(final File file) throws XmlException, IOException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(file, CTNonVisualPictureProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualPictureProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(file, CTNonVisualPictureProperties.type, xmlOptions);
        }
        
        public static CTNonVisualPictureProperties parse(final URL url) throws XmlException, IOException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(url, CTNonVisualPictureProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualPictureProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(url, CTNonVisualPictureProperties.type, xmlOptions);
        }
        
        public static CTNonVisualPictureProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(inputStream, CTNonVisualPictureProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualPictureProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(inputStream, CTNonVisualPictureProperties.type, xmlOptions);
        }
        
        public static CTNonVisualPictureProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(reader, CTNonVisualPictureProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualPictureProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(reader, CTNonVisualPictureProperties.type, xmlOptions);
        }
        
        public static CTNonVisualPictureProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(xmlStreamReader, CTNonVisualPictureProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualPictureProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(xmlStreamReader, CTNonVisualPictureProperties.type, xmlOptions);
        }
        
        public static CTNonVisualPictureProperties parse(final Node node) throws XmlException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(node, CTNonVisualPictureProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualPictureProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(node, CTNonVisualPictureProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNonVisualPictureProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(xmlInputStream, CTNonVisualPictureProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNonVisualPictureProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNonVisualPictureProperties)getTypeLoader().parse(xmlInputStream, CTNonVisualPictureProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNonVisualPictureProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNonVisualPictureProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
