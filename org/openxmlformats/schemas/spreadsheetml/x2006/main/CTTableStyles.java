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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTableStyles extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableStyles.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablestyles872ftype");
    
    List<CTTableStyle> getTableStyleList();
    
    @Deprecated
    CTTableStyle[] getTableStyleArray();
    
    CTTableStyle getTableStyleArray(final int p0);
    
    int sizeOfTableStyleArray();
    
    void setTableStyleArray(final CTTableStyle[] p0);
    
    void setTableStyleArray(final int p0, final CTTableStyle p1);
    
    CTTableStyle insertNewTableStyle(final int p0);
    
    CTTableStyle addNewTableStyle();
    
    void removeTableStyle(final int p0);
    
    long getCount();
    
    XmlUnsignedInt xgetCount();
    
    boolean isSetCount();
    
    void setCount(final long p0);
    
    void xsetCount(final XmlUnsignedInt p0);
    
    void unsetCount();
    
    String getDefaultTableStyle();
    
    XmlString xgetDefaultTableStyle();
    
    boolean isSetDefaultTableStyle();
    
    void setDefaultTableStyle(final String p0);
    
    void xsetDefaultTableStyle(final XmlString p0);
    
    void unsetDefaultTableStyle();
    
    String getDefaultPivotStyle();
    
    XmlString xgetDefaultPivotStyle();
    
    boolean isSetDefaultPivotStyle();
    
    void setDefaultPivotStyle(final String p0);
    
    void xsetDefaultPivotStyle(final XmlString p0);
    
    void unsetDefaultPivotStyle();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableStyles.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableStyles newInstance() {
            return (CTTableStyles)getTypeLoader().newInstance(CTTableStyles.type, (XmlOptions)null);
        }
        
        public static CTTableStyles newInstance(final XmlOptions xmlOptions) {
            return (CTTableStyles)getTypeLoader().newInstance(CTTableStyles.type, xmlOptions);
        }
        
        public static CTTableStyles parse(final String s) throws XmlException {
            return (CTTableStyles)getTypeLoader().parse(s, CTTableStyles.type, (XmlOptions)null);
        }
        
        public static CTTableStyles parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyles)getTypeLoader().parse(s, CTTableStyles.type, xmlOptions);
        }
        
        public static CTTableStyles parse(final File file) throws XmlException, IOException {
            return (CTTableStyles)getTypeLoader().parse(file, CTTableStyles.type, (XmlOptions)null);
        }
        
        public static CTTableStyles parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyles)getTypeLoader().parse(file, CTTableStyles.type, xmlOptions);
        }
        
        public static CTTableStyles parse(final URL url) throws XmlException, IOException {
            return (CTTableStyles)getTypeLoader().parse(url, CTTableStyles.type, (XmlOptions)null);
        }
        
        public static CTTableStyles parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyles)getTypeLoader().parse(url, CTTableStyles.type, xmlOptions);
        }
        
        public static CTTableStyles parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableStyles)getTypeLoader().parse(inputStream, CTTableStyles.type, (XmlOptions)null);
        }
        
        public static CTTableStyles parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyles)getTypeLoader().parse(inputStream, CTTableStyles.type, xmlOptions);
        }
        
        public static CTTableStyles parse(final Reader reader) throws XmlException, IOException {
            return (CTTableStyles)getTypeLoader().parse(reader, CTTableStyles.type, (XmlOptions)null);
        }
        
        public static CTTableStyles parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyles)getTypeLoader().parse(reader, CTTableStyles.type, xmlOptions);
        }
        
        public static CTTableStyles parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableStyles)getTypeLoader().parse(xmlStreamReader, CTTableStyles.type, (XmlOptions)null);
        }
        
        public static CTTableStyles parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyles)getTypeLoader().parse(xmlStreamReader, CTTableStyles.type, xmlOptions);
        }
        
        public static CTTableStyles parse(final Node node) throws XmlException {
            return (CTTableStyles)getTypeLoader().parse(node, CTTableStyles.type, (XmlOptions)null);
        }
        
        public static CTTableStyles parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyles)getTypeLoader().parse(node, CTTableStyles.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableStyles parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableStyles)getTypeLoader().parse(xmlInputStream, CTTableStyles.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableStyles parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableStyles)getTypeLoader().parse(xmlInputStream, CTTableStyles.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyles.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyles.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
