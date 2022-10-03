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

public interface CTCellXfs extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCellXfs.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcellxfs1322type");
    
    List<CTXf> getXfList();
    
    @Deprecated
    CTXf[] getXfArray();
    
    CTXf getXfArray(final int p0);
    
    int sizeOfXfArray();
    
    void setXfArray(final CTXf[] p0);
    
    void setXfArray(final int p0, final CTXf p1);
    
    CTXf insertNewXf(final int p0);
    
    CTXf addNewXf();
    
    void removeXf(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCellXfs.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCellXfs newInstance() {
            return (CTCellXfs)getTypeLoader().newInstance(CTCellXfs.type, (XmlOptions)null);
        }
        
        public static CTCellXfs newInstance(final XmlOptions xmlOptions) {
            return (CTCellXfs)getTypeLoader().newInstance(CTCellXfs.type, xmlOptions);
        }
        
        public static CTCellXfs parse(final String s) throws XmlException {
            return (CTCellXfs)getTypeLoader().parse(s, CTCellXfs.type, (XmlOptions)null);
        }
        
        public static CTCellXfs parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCellXfs)getTypeLoader().parse(s, CTCellXfs.type, xmlOptions);
        }
        
        public static CTCellXfs parse(final File file) throws XmlException, IOException {
            return (CTCellXfs)getTypeLoader().parse(file, CTCellXfs.type, (XmlOptions)null);
        }
        
        public static CTCellXfs parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellXfs)getTypeLoader().parse(file, CTCellXfs.type, xmlOptions);
        }
        
        public static CTCellXfs parse(final URL url) throws XmlException, IOException {
            return (CTCellXfs)getTypeLoader().parse(url, CTCellXfs.type, (XmlOptions)null);
        }
        
        public static CTCellXfs parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellXfs)getTypeLoader().parse(url, CTCellXfs.type, xmlOptions);
        }
        
        public static CTCellXfs parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCellXfs)getTypeLoader().parse(inputStream, CTCellXfs.type, (XmlOptions)null);
        }
        
        public static CTCellXfs parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellXfs)getTypeLoader().parse(inputStream, CTCellXfs.type, xmlOptions);
        }
        
        public static CTCellXfs parse(final Reader reader) throws XmlException, IOException {
            return (CTCellXfs)getTypeLoader().parse(reader, CTCellXfs.type, (XmlOptions)null);
        }
        
        public static CTCellXfs parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellXfs)getTypeLoader().parse(reader, CTCellXfs.type, xmlOptions);
        }
        
        public static CTCellXfs parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCellXfs)getTypeLoader().parse(xmlStreamReader, CTCellXfs.type, (XmlOptions)null);
        }
        
        public static CTCellXfs parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCellXfs)getTypeLoader().parse(xmlStreamReader, CTCellXfs.type, xmlOptions);
        }
        
        public static CTCellXfs parse(final Node node) throws XmlException {
            return (CTCellXfs)getTypeLoader().parse(node, CTCellXfs.type, (XmlOptions)null);
        }
        
        public static CTCellXfs parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCellXfs)getTypeLoader().parse(node, CTCellXfs.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCellXfs parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCellXfs)getTypeLoader().parse(xmlInputStream, CTCellXfs.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCellXfs parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCellXfs)getTypeLoader().parse(xmlInputStream, CTCellXfs.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCellXfs.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCellXfs.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
