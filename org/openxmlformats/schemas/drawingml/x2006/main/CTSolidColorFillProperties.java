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

public interface CTSolidColorFillProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSolidColorFillProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsolidcolorfillproperties9cc9type");
    
    CTScRgbColor getScrgbClr();
    
    boolean isSetScrgbClr();
    
    void setScrgbClr(final CTScRgbColor p0);
    
    CTScRgbColor addNewScrgbClr();
    
    void unsetScrgbClr();
    
    CTSRgbColor getSrgbClr();
    
    boolean isSetSrgbClr();
    
    void setSrgbClr(final CTSRgbColor p0);
    
    CTSRgbColor addNewSrgbClr();
    
    void unsetSrgbClr();
    
    CTHslColor getHslClr();
    
    boolean isSetHslClr();
    
    void setHslClr(final CTHslColor p0);
    
    CTHslColor addNewHslClr();
    
    void unsetHslClr();
    
    CTSystemColor getSysClr();
    
    boolean isSetSysClr();
    
    void setSysClr(final CTSystemColor p0);
    
    CTSystemColor addNewSysClr();
    
    void unsetSysClr();
    
    CTSchemeColor getSchemeClr();
    
    boolean isSetSchemeClr();
    
    void setSchemeClr(final CTSchemeColor p0);
    
    CTSchemeColor addNewSchemeClr();
    
    void unsetSchemeClr();
    
    CTPresetColor getPrstClr();
    
    boolean isSetPrstClr();
    
    void setPrstClr(final CTPresetColor p0);
    
    CTPresetColor addNewPrstClr();
    
    void unsetPrstClr();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSolidColorFillProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSolidColorFillProperties newInstance() {
            return (CTSolidColorFillProperties)getTypeLoader().newInstance(CTSolidColorFillProperties.type, (XmlOptions)null);
        }
        
        public static CTSolidColorFillProperties newInstance(final XmlOptions xmlOptions) {
            return (CTSolidColorFillProperties)getTypeLoader().newInstance(CTSolidColorFillProperties.type, xmlOptions);
        }
        
        public static CTSolidColorFillProperties parse(final String s) throws XmlException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(s, CTSolidColorFillProperties.type, (XmlOptions)null);
        }
        
        public static CTSolidColorFillProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(s, CTSolidColorFillProperties.type, xmlOptions);
        }
        
        public static CTSolidColorFillProperties parse(final File file) throws XmlException, IOException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(file, CTSolidColorFillProperties.type, (XmlOptions)null);
        }
        
        public static CTSolidColorFillProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(file, CTSolidColorFillProperties.type, xmlOptions);
        }
        
        public static CTSolidColorFillProperties parse(final URL url) throws XmlException, IOException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(url, CTSolidColorFillProperties.type, (XmlOptions)null);
        }
        
        public static CTSolidColorFillProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(url, CTSolidColorFillProperties.type, xmlOptions);
        }
        
        public static CTSolidColorFillProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(inputStream, CTSolidColorFillProperties.type, (XmlOptions)null);
        }
        
        public static CTSolidColorFillProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(inputStream, CTSolidColorFillProperties.type, xmlOptions);
        }
        
        public static CTSolidColorFillProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(reader, CTSolidColorFillProperties.type, (XmlOptions)null);
        }
        
        public static CTSolidColorFillProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(reader, CTSolidColorFillProperties.type, xmlOptions);
        }
        
        public static CTSolidColorFillProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(xmlStreamReader, CTSolidColorFillProperties.type, (XmlOptions)null);
        }
        
        public static CTSolidColorFillProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(xmlStreamReader, CTSolidColorFillProperties.type, xmlOptions);
        }
        
        public static CTSolidColorFillProperties parse(final Node node) throws XmlException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(node, CTSolidColorFillProperties.type, (XmlOptions)null);
        }
        
        public static CTSolidColorFillProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(node, CTSolidColorFillProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSolidColorFillProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(xmlInputStream, CTSolidColorFillProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSolidColorFillProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSolidColorFillProperties)getTypeLoader().parse(xmlInputStream, CTSolidColorFillProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSolidColorFillProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSolidColorFillProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
