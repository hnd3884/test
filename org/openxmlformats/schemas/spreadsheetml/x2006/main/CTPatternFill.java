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

public interface CTPatternFill extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPatternFill.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpatternfill7452type");
    
    CTColor getFgColor();
    
    boolean isSetFgColor();
    
    void setFgColor(final CTColor p0);
    
    CTColor addNewFgColor();
    
    void unsetFgColor();
    
    CTColor getBgColor();
    
    boolean isSetBgColor();
    
    void setBgColor(final CTColor p0);
    
    CTColor addNewBgColor();
    
    void unsetBgColor();
    
    STPatternType.Enum getPatternType();
    
    STPatternType xgetPatternType();
    
    boolean isSetPatternType();
    
    void setPatternType(final STPatternType.Enum p0);
    
    void xsetPatternType(final STPatternType p0);
    
    void unsetPatternType();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPatternFill.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPatternFill newInstance() {
            return (CTPatternFill)getTypeLoader().newInstance(CTPatternFill.type, (XmlOptions)null);
        }
        
        public static CTPatternFill newInstance(final XmlOptions xmlOptions) {
            return (CTPatternFill)getTypeLoader().newInstance(CTPatternFill.type, xmlOptions);
        }
        
        public static CTPatternFill parse(final String s) throws XmlException {
            return (CTPatternFill)getTypeLoader().parse(s, CTPatternFill.type, (XmlOptions)null);
        }
        
        public static CTPatternFill parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPatternFill)getTypeLoader().parse(s, CTPatternFill.type, xmlOptions);
        }
        
        public static CTPatternFill parse(final File file) throws XmlException, IOException {
            return (CTPatternFill)getTypeLoader().parse(file, CTPatternFill.type, (XmlOptions)null);
        }
        
        public static CTPatternFill parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPatternFill)getTypeLoader().parse(file, CTPatternFill.type, xmlOptions);
        }
        
        public static CTPatternFill parse(final URL url) throws XmlException, IOException {
            return (CTPatternFill)getTypeLoader().parse(url, CTPatternFill.type, (XmlOptions)null);
        }
        
        public static CTPatternFill parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPatternFill)getTypeLoader().parse(url, CTPatternFill.type, xmlOptions);
        }
        
        public static CTPatternFill parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPatternFill)getTypeLoader().parse(inputStream, CTPatternFill.type, (XmlOptions)null);
        }
        
        public static CTPatternFill parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPatternFill)getTypeLoader().parse(inputStream, CTPatternFill.type, xmlOptions);
        }
        
        public static CTPatternFill parse(final Reader reader) throws XmlException, IOException {
            return (CTPatternFill)getTypeLoader().parse(reader, CTPatternFill.type, (XmlOptions)null);
        }
        
        public static CTPatternFill parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPatternFill)getTypeLoader().parse(reader, CTPatternFill.type, xmlOptions);
        }
        
        public static CTPatternFill parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPatternFill)getTypeLoader().parse(xmlStreamReader, CTPatternFill.type, (XmlOptions)null);
        }
        
        public static CTPatternFill parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPatternFill)getTypeLoader().parse(xmlStreamReader, CTPatternFill.type, xmlOptions);
        }
        
        public static CTPatternFill parse(final Node node) throws XmlException {
            return (CTPatternFill)getTypeLoader().parse(node, CTPatternFill.type, (XmlOptions)null);
        }
        
        public static CTPatternFill parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPatternFill)getTypeLoader().parse(node, CTPatternFill.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPatternFill parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPatternFill)getTypeLoader().parse(xmlInputStream, CTPatternFill.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPatternFill parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPatternFill)getTypeLoader().parse(xmlInputStream, CTPatternFill.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPatternFill.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPatternFill.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
