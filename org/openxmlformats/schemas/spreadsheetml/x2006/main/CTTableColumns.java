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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTableColumns extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableColumns.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablecolumnsebb8type");
    
    List<CTTableColumn> getTableColumnList();
    
    @Deprecated
    CTTableColumn[] getTableColumnArray();
    
    CTTableColumn getTableColumnArray(final int p0);
    
    int sizeOfTableColumnArray();
    
    void setTableColumnArray(final CTTableColumn[] p0);
    
    void setTableColumnArray(final int p0, final CTTableColumn p1);
    
    CTTableColumn insertNewTableColumn(final int p0);
    
    CTTableColumn addNewTableColumn();
    
    void removeTableColumn(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableColumns.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableColumns newInstance() {
            return (CTTableColumns)getTypeLoader().newInstance(CTTableColumns.type, (XmlOptions)null);
        }
        
        public static CTTableColumns newInstance(final XmlOptions xmlOptions) {
            return (CTTableColumns)getTypeLoader().newInstance(CTTableColumns.type, xmlOptions);
        }
        
        public static CTTableColumns parse(final String s) throws XmlException {
            return (CTTableColumns)getTypeLoader().parse(s, CTTableColumns.type, (XmlOptions)null);
        }
        
        public static CTTableColumns parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableColumns)getTypeLoader().parse(s, CTTableColumns.type, xmlOptions);
        }
        
        public static CTTableColumns parse(final File file) throws XmlException, IOException {
            return (CTTableColumns)getTypeLoader().parse(file, CTTableColumns.type, (XmlOptions)null);
        }
        
        public static CTTableColumns parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableColumns)getTypeLoader().parse(file, CTTableColumns.type, xmlOptions);
        }
        
        public static CTTableColumns parse(final URL url) throws XmlException, IOException {
            return (CTTableColumns)getTypeLoader().parse(url, CTTableColumns.type, (XmlOptions)null);
        }
        
        public static CTTableColumns parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableColumns)getTypeLoader().parse(url, CTTableColumns.type, xmlOptions);
        }
        
        public static CTTableColumns parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableColumns)getTypeLoader().parse(inputStream, CTTableColumns.type, (XmlOptions)null);
        }
        
        public static CTTableColumns parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableColumns)getTypeLoader().parse(inputStream, CTTableColumns.type, xmlOptions);
        }
        
        public static CTTableColumns parse(final Reader reader) throws XmlException, IOException {
            return (CTTableColumns)getTypeLoader().parse(reader, CTTableColumns.type, (XmlOptions)null);
        }
        
        public static CTTableColumns parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableColumns)getTypeLoader().parse(reader, CTTableColumns.type, xmlOptions);
        }
        
        public static CTTableColumns parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableColumns)getTypeLoader().parse(xmlStreamReader, CTTableColumns.type, (XmlOptions)null);
        }
        
        public static CTTableColumns parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableColumns)getTypeLoader().parse(xmlStreamReader, CTTableColumns.type, xmlOptions);
        }
        
        public static CTTableColumns parse(final Node node) throws XmlException {
            return (CTTableColumns)getTypeLoader().parse(node, CTTableColumns.type, (XmlOptions)null);
        }
        
        public static CTTableColumns parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableColumns)getTypeLoader().parse(node, CTTableColumns.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableColumns parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableColumns)getTypeLoader().parse(xmlInputStream, CTTableColumns.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableColumns parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableColumns)getTypeLoader().parse(xmlInputStream, CTTableColumns.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableColumns.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableColumns.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
