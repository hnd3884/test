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

public interface CTDxfs extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDxfs.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdxfsb26atype");
    
    List<CTDxf> getDxfList();
    
    @Deprecated
    CTDxf[] getDxfArray();
    
    CTDxf getDxfArray(final int p0);
    
    int sizeOfDxfArray();
    
    void setDxfArray(final CTDxf[] p0);
    
    void setDxfArray(final int p0, final CTDxf p1);
    
    CTDxf insertNewDxf(final int p0);
    
    CTDxf addNewDxf();
    
    void removeDxf(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDxfs.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDxfs newInstance() {
            return (CTDxfs)getTypeLoader().newInstance(CTDxfs.type, (XmlOptions)null);
        }
        
        public static CTDxfs newInstance(final XmlOptions xmlOptions) {
            return (CTDxfs)getTypeLoader().newInstance(CTDxfs.type, xmlOptions);
        }
        
        public static CTDxfs parse(final String s) throws XmlException {
            return (CTDxfs)getTypeLoader().parse(s, CTDxfs.type, (XmlOptions)null);
        }
        
        public static CTDxfs parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDxfs)getTypeLoader().parse(s, CTDxfs.type, xmlOptions);
        }
        
        public static CTDxfs parse(final File file) throws XmlException, IOException {
            return (CTDxfs)getTypeLoader().parse(file, CTDxfs.type, (XmlOptions)null);
        }
        
        public static CTDxfs parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDxfs)getTypeLoader().parse(file, CTDxfs.type, xmlOptions);
        }
        
        public static CTDxfs parse(final URL url) throws XmlException, IOException {
            return (CTDxfs)getTypeLoader().parse(url, CTDxfs.type, (XmlOptions)null);
        }
        
        public static CTDxfs parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDxfs)getTypeLoader().parse(url, CTDxfs.type, xmlOptions);
        }
        
        public static CTDxfs parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDxfs)getTypeLoader().parse(inputStream, CTDxfs.type, (XmlOptions)null);
        }
        
        public static CTDxfs parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDxfs)getTypeLoader().parse(inputStream, CTDxfs.type, xmlOptions);
        }
        
        public static CTDxfs parse(final Reader reader) throws XmlException, IOException {
            return (CTDxfs)getTypeLoader().parse(reader, CTDxfs.type, (XmlOptions)null);
        }
        
        public static CTDxfs parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDxfs)getTypeLoader().parse(reader, CTDxfs.type, xmlOptions);
        }
        
        public static CTDxfs parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDxfs)getTypeLoader().parse(xmlStreamReader, CTDxfs.type, (XmlOptions)null);
        }
        
        public static CTDxfs parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDxfs)getTypeLoader().parse(xmlStreamReader, CTDxfs.type, xmlOptions);
        }
        
        public static CTDxfs parse(final Node node) throws XmlException {
            return (CTDxfs)getTypeLoader().parse(node, CTDxfs.type, (XmlOptions)null);
        }
        
        public static CTDxfs parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDxfs)getTypeLoader().parse(node, CTDxfs.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDxfs parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDxfs)getTypeLoader().parse(xmlInputStream, CTDxfs.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDxfs parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDxfs)getTypeLoader().parse(xmlInputStream, CTDxfs.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDxfs.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDxfs.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
