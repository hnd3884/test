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

public interface CTPatternFillProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPatternFillProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpatternfillproperties3637type");
    
    CTColor getFgClr();
    
    boolean isSetFgClr();
    
    void setFgClr(final CTColor p0);
    
    CTColor addNewFgClr();
    
    void unsetFgClr();
    
    CTColor getBgClr();
    
    boolean isSetBgClr();
    
    void setBgClr(final CTColor p0);
    
    CTColor addNewBgClr();
    
    void unsetBgClr();
    
    STPresetPatternVal.Enum getPrst();
    
    STPresetPatternVal xgetPrst();
    
    boolean isSetPrst();
    
    void setPrst(final STPresetPatternVal.Enum p0);
    
    void xsetPrst(final STPresetPatternVal p0);
    
    void unsetPrst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPatternFillProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPatternFillProperties newInstance() {
            return (CTPatternFillProperties)getTypeLoader().newInstance(CTPatternFillProperties.type, (XmlOptions)null);
        }
        
        public static CTPatternFillProperties newInstance(final XmlOptions xmlOptions) {
            return (CTPatternFillProperties)getTypeLoader().newInstance(CTPatternFillProperties.type, xmlOptions);
        }
        
        public static CTPatternFillProperties parse(final String s) throws XmlException {
            return (CTPatternFillProperties)getTypeLoader().parse(s, CTPatternFillProperties.type, (XmlOptions)null);
        }
        
        public static CTPatternFillProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPatternFillProperties)getTypeLoader().parse(s, CTPatternFillProperties.type, xmlOptions);
        }
        
        public static CTPatternFillProperties parse(final File file) throws XmlException, IOException {
            return (CTPatternFillProperties)getTypeLoader().parse(file, CTPatternFillProperties.type, (XmlOptions)null);
        }
        
        public static CTPatternFillProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPatternFillProperties)getTypeLoader().parse(file, CTPatternFillProperties.type, xmlOptions);
        }
        
        public static CTPatternFillProperties parse(final URL url) throws XmlException, IOException {
            return (CTPatternFillProperties)getTypeLoader().parse(url, CTPatternFillProperties.type, (XmlOptions)null);
        }
        
        public static CTPatternFillProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPatternFillProperties)getTypeLoader().parse(url, CTPatternFillProperties.type, xmlOptions);
        }
        
        public static CTPatternFillProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPatternFillProperties)getTypeLoader().parse(inputStream, CTPatternFillProperties.type, (XmlOptions)null);
        }
        
        public static CTPatternFillProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPatternFillProperties)getTypeLoader().parse(inputStream, CTPatternFillProperties.type, xmlOptions);
        }
        
        public static CTPatternFillProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTPatternFillProperties)getTypeLoader().parse(reader, CTPatternFillProperties.type, (XmlOptions)null);
        }
        
        public static CTPatternFillProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPatternFillProperties)getTypeLoader().parse(reader, CTPatternFillProperties.type, xmlOptions);
        }
        
        public static CTPatternFillProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPatternFillProperties)getTypeLoader().parse(xmlStreamReader, CTPatternFillProperties.type, (XmlOptions)null);
        }
        
        public static CTPatternFillProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPatternFillProperties)getTypeLoader().parse(xmlStreamReader, CTPatternFillProperties.type, xmlOptions);
        }
        
        public static CTPatternFillProperties parse(final Node node) throws XmlException {
            return (CTPatternFillProperties)getTypeLoader().parse(node, CTPatternFillProperties.type, (XmlOptions)null);
        }
        
        public static CTPatternFillProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPatternFillProperties)getTypeLoader().parse(node, CTPatternFillProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPatternFillProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPatternFillProperties)getTypeLoader().parse(xmlInputStream, CTPatternFillProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPatternFillProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPatternFillProperties)getTypeLoader().parse(xmlInputStream, CTPatternFillProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPatternFillProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPatternFillProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
