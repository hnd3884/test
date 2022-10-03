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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTLocation extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLocation.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlocationc23etype");
    
    String getRef();
    
    STRef xgetRef();
    
    void setRef(final String p0);
    
    void xsetRef(final STRef p0);
    
    long getFirstHeaderRow();
    
    XmlUnsignedInt xgetFirstHeaderRow();
    
    void setFirstHeaderRow(final long p0);
    
    void xsetFirstHeaderRow(final XmlUnsignedInt p0);
    
    long getFirstDataRow();
    
    XmlUnsignedInt xgetFirstDataRow();
    
    void setFirstDataRow(final long p0);
    
    void xsetFirstDataRow(final XmlUnsignedInt p0);
    
    long getFirstDataCol();
    
    XmlUnsignedInt xgetFirstDataCol();
    
    void setFirstDataCol(final long p0);
    
    void xsetFirstDataCol(final XmlUnsignedInt p0);
    
    long getRowPageCount();
    
    XmlUnsignedInt xgetRowPageCount();
    
    boolean isSetRowPageCount();
    
    void setRowPageCount(final long p0);
    
    void xsetRowPageCount(final XmlUnsignedInt p0);
    
    void unsetRowPageCount();
    
    long getColPageCount();
    
    XmlUnsignedInt xgetColPageCount();
    
    boolean isSetColPageCount();
    
    void setColPageCount(final long p0);
    
    void xsetColPageCount(final XmlUnsignedInt p0);
    
    void unsetColPageCount();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLocation.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLocation newInstance() {
            return (CTLocation)getTypeLoader().newInstance(CTLocation.type, (XmlOptions)null);
        }
        
        public static CTLocation newInstance(final XmlOptions xmlOptions) {
            return (CTLocation)getTypeLoader().newInstance(CTLocation.type, xmlOptions);
        }
        
        public static CTLocation parse(final String s) throws XmlException {
            return (CTLocation)getTypeLoader().parse(s, CTLocation.type, (XmlOptions)null);
        }
        
        public static CTLocation parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLocation)getTypeLoader().parse(s, CTLocation.type, xmlOptions);
        }
        
        public static CTLocation parse(final File file) throws XmlException, IOException {
            return (CTLocation)getTypeLoader().parse(file, CTLocation.type, (XmlOptions)null);
        }
        
        public static CTLocation parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLocation)getTypeLoader().parse(file, CTLocation.type, xmlOptions);
        }
        
        public static CTLocation parse(final URL url) throws XmlException, IOException {
            return (CTLocation)getTypeLoader().parse(url, CTLocation.type, (XmlOptions)null);
        }
        
        public static CTLocation parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLocation)getTypeLoader().parse(url, CTLocation.type, xmlOptions);
        }
        
        public static CTLocation parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLocation)getTypeLoader().parse(inputStream, CTLocation.type, (XmlOptions)null);
        }
        
        public static CTLocation parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLocation)getTypeLoader().parse(inputStream, CTLocation.type, xmlOptions);
        }
        
        public static CTLocation parse(final Reader reader) throws XmlException, IOException {
            return (CTLocation)getTypeLoader().parse(reader, CTLocation.type, (XmlOptions)null);
        }
        
        public static CTLocation parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLocation)getTypeLoader().parse(reader, CTLocation.type, xmlOptions);
        }
        
        public static CTLocation parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLocation)getTypeLoader().parse(xmlStreamReader, CTLocation.type, (XmlOptions)null);
        }
        
        public static CTLocation parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLocation)getTypeLoader().parse(xmlStreamReader, CTLocation.type, xmlOptions);
        }
        
        public static CTLocation parse(final Node node) throws XmlException {
            return (CTLocation)getTypeLoader().parse(node, CTLocation.type, (XmlOptions)null);
        }
        
        public static CTLocation parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLocation)getTypeLoader().parse(node, CTLocation.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLocation parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLocation)getTypeLoader().parse(xmlInputStream, CTLocation.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLocation parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLocation)getTypeLoader().parse(xmlInputStream, CTLocation.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLocation.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLocation.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
