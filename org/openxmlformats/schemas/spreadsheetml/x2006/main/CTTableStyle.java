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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTableStyle extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablestylea24ctype");
    
    List<CTTableStyleElement> getTableStyleElementList();
    
    @Deprecated
    CTTableStyleElement[] getTableStyleElementArray();
    
    CTTableStyleElement getTableStyleElementArray(final int p0);
    
    int sizeOfTableStyleElementArray();
    
    void setTableStyleElementArray(final CTTableStyleElement[] p0);
    
    void setTableStyleElementArray(final int p0, final CTTableStyleElement p1);
    
    CTTableStyleElement insertNewTableStyleElement(final int p0);
    
    CTTableStyleElement addNewTableStyleElement();
    
    void removeTableStyleElement(final int p0);
    
    String getName();
    
    XmlString xgetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlString p0);
    
    boolean getPivot();
    
    XmlBoolean xgetPivot();
    
    boolean isSetPivot();
    
    void setPivot(final boolean p0);
    
    void xsetPivot(final XmlBoolean p0);
    
    void unsetPivot();
    
    boolean getTable();
    
    XmlBoolean xgetTable();
    
    boolean isSetTable();
    
    void setTable(final boolean p0);
    
    void xsetTable(final XmlBoolean p0);
    
    void unsetTable();
    
    long getCount();
    
    XmlUnsignedInt xgetCount();
    
    boolean isSetCount();
    
    void setCount(final long p0);
    
    void xsetCount(final XmlUnsignedInt p0);
    
    void unsetCount();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableStyle newInstance() {
            return (CTTableStyle)getTypeLoader().newInstance(CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle newInstance(final XmlOptions xmlOptions) {
            return (CTTableStyle)getTypeLoader().newInstance(CTTableStyle.type, xmlOptions);
        }
        
        public static CTTableStyle parse(final String s) throws XmlException {
            return (CTTableStyle)getTypeLoader().parse(s, CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyle)getTypeLoader().parse(s, CTTableStyle.type, xmlOptions);
        }
        
        public static CTTableStyle parse(final File file) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(file, CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(file, CTTableStyle.type, xmlOptions);
        }
        
        public static CTTableStyle parse(final URL url) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(url, CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(url, CTTableStyle.type, xmlOptions);
        }
        
        public static CTTableStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(inputStream, CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(inputStream, CTTableStyle.type, xmlOptions);
        }
        
        public static CTTableStyle parse(final Reader reader) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(reader, CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyle)getTypeLoader().parse(reader, CTTableStyle.type, xmlOptions);
        }
        
        public static CTTableStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableStyle)getTypeLoader().parse(xmlStreamReader, CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyle)getTypeLoader().parse(xmlStreamReader, CTTableStyle.type, xmlOptions);
        }
        
        public static CTTableStyle parse(final Node node) throws XmlException {
            return (CTTableStyle)getTypeLoader().parse(node, CTTableStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyle)getTypeLoader().parse(node, CTTableStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableStyle)getTypeLoader().parse(xmlInputStream, CTTableStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableStyle)getTypeLoader().parse(xmlInputStream, CTTableStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
