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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTAutoFilter extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTAutoFilter.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctautofiltera8d0type");
    
    List<CTFilterColumn> getFilterColumnList();
    
    @Deprecated
    CTFilterColumn[] getFilterColumnArray();
    
    CTFilterColumn getFilterColumnArray(final int p0);
    
    int sizeOfFilterColumnArray();
    
    void setFilterColumnArray(final CTFilterColumn[] p0);
    
    void setFilterColumnArray(final int p0, final CTFilterColumn p1);
    
    CTFilterColumn insertNewFilterColumn(final int p0);
    
    CTFilterColumn addNewFilterColumn();
    
    void removeFilterColumn(final int p0);
    
    CTSortState getSortState();
    
    boolean isSetSortState();
    
    void setSortState(final CTSortState p0);
    
    CTSortState addNewSortState();
    
    void unsetSortState();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    String getRef();
    
    STRef xgetRef();
    
    boolean isSetRef();
    
    void setRef(final String p0);
    
    void xsetRef(final STRef p0);
    
    void unsetRef();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTAutoFilter.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTAutoFilter newInstance() {
            return (CTAutoFilter)getTypeLoader().newInstance(CTAutoFilter.type, (XmlOptions)null);
        }
        
        public static CTAutoFilter newInstance(final XmlOptions xmlOptions) {
            return (CTAutoFilter)getTypeLoader().newInstance(CTAutoFilter.type, xmlOptions);
        }
        
        public static CTAutoFilter parse(final String s) throws XmlException {
            return (CTAutoFilter)getTypeLoader().parse(s, CTAutoFilter.type, (XmlOptions)null);
        }
        
        public static CTAutoFilter parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTAutoFilter)getTypeLoader().parse(s, CTAutoFilter.type, xmlOptions);
        }
        
        public static CTAutoFilter parse(final File file) throws XmlException, IOException {
            return (CTAutoFilter)getTypeLoader().parse(file, CTAutoFilter.type, (XmlOptions)null);
        }
        
        public static CTAutoFilter parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAutoFilter)getTypeLoader().parse(file, CTAutoFilter.type, xmlOptions);
        }
        
        public static CTAutoFilter parse(final URL url) throws XmlException, IOException {
            return (CTAutoFilter)getTypeLoader().parse(url, CTAutoFilter.type, (XmlOptions)null);
        }
        
        public static CTAutoFilter parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAutoFilter)getTypeLoader().parse(url, CTAutoFilter.type, xmlOptions);
        }
        
        public static CTAutoFilter parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTAutoFilter)getTypeLoader().parse(inputStream, CTAutoFilter.type, (XmlOptions)null);
        }
        
        public static CTAutoFilter parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAutoFilter)getTypeLoader().parse(inputStream, CTAutoFilter.type, xmlOptions);
        }
        
        public static CTAutoFilter parse(final Reader reader) throws XmlException, IOException {
            return (CTAutoFilter)getTypeLoader().parse(reader, CTAutoFilter.type, (XmlOptions)null);
        }
        
        public static CTAutoFilter parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAutoFilter)getTypeLoader().parse(reader, CTAutoFilter.type, xmlOptions);
        }
        
        public static CTAutoFilter parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTAutoFilter)getTypeLoader().parse(xmlStreamReader, CTAutoFilter.type, (XmlOptions)null);
        }
        
        public static CTAutoFilter parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTAutoFilter)getTypeLoader().parse(xmlStreamReader, CTAutoFilter.type, xmlOptions);
        }
        
        public static CTAutoFilter parse(final Node node) throws XmlException {
            return (CTAutoFilter)getTypeLoader().parse(node, CTAutoFilter.type, (XmlOptions)null);
        }
        
        public static CTAutoFilter parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTAutoFilter)getTypeLoader().parse(node, CTAutoFilter.type, xmlOptions);
        }
        
        @Deprecated
        public static CTAutoFilter parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTAutoFilter)getTypeLoader().parse(xmlInputStream, CTAutoFilter.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTAutoFilter parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTAutoFilter)getTypeLoader().parse(xmlInputStream, CTAutoFilter.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAutoFilter.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAutoFilter.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
