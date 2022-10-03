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

public interface CTCellStyleXfs extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCellStyleXfs.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcellstylexfsa81ftype");
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCellStyleXfs.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCellStyleXfs newInstance() {
            return (CTCellStyleXfs)getTypeLoader().newInstance(CTCellStyleXfs.type, (XmlOptions)null);
        }
        
        public static CTCellStyleXfs newInstance(final XmlOptions xmlOptions) {
            return (CTCellStyleXfs)getTypeLoader().newInstance(CTCellStyleXfs.type, xmlOptions);
        }
        
        public static CTCellStyleXfs parse(final String s) throws XmlException {
            return (CTCellStyleXfs)getTypeLoader().parse(s, CTCellStyleXfs.type, (XmlOptions)null);
        }
        
        public static CTCellStyleXfs parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCellStyleXfs)getTypeLoader().parse(s, CTCellStyleXfs.type, xmlOptions);
        }
        
        public static CTCellStyleXfs parse(final File file) throws XmlException, IOException {
            return (CTCellStyleXfs)getTypeLoader().parse(file, CTCellStyleXfs.type, (XmlOptions)null);
        }
        
        public static CTCellStyleXfs parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellStyleXfs)getTypeLoader().parse(file, CTCellStyleXfs.type, xmlOptions);
        }
        
        public static CTCellStyleXfs parse(final URL url) throws XmlException, IOException {
            return (CTCellStyleXfs)getTypeLoader().parse(url, CTCellStyleXfs.type, (XmlOptions)null);
        }
        
        public static CTCellStyleXfs parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellStyleXfs)getTypeLoader().parse(url, CTCellStyleXfs.type, xmlOptions);
        }
        
        public static CTCellStyleXfs parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCellStyleXfs)getTypeLoader().parse(inputStream, CTCellStyleXfs.type, (XmlOptions)null);
        }
        
        public static CTCellStyleXfs parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellStyleXfs)getTypeLoader().parse(inputStream, CTCellStyleXfs.type, xmlOptions);
        }
        
        public static CTCellStyleXfs parse(final Reader reader) throws XmlException, IOException {
            return (CTCellStyleXfs)getTypeLoader().parse(reader, CTCellStyleXfs.type, (XmlOptions)null);
        }
        
        public static CTCellStyleXfs parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellStyleXfs)getTypeLoader().parse(reader, CTCellStyleXfs.type, xmlOptions);
        }
        
        public static CTCellStyleXfs parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCellStyleXfs)getTypeLoader().parse(xmlStreamReader, CTCellStyleXfs.type, (XmlOptions)null);
        }
        
        public static CTCellStyleXfs parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCellStyleXfs)getTypeLoader().parse(xmlStreamReader, CTCellStyleXfs.type, xmlOptions);
        }
        
        public static CTCellStyleXfs parse(final Node node) throws XmlException {
            return (CTCellStyleXfs)getTypeLoader().parse(node, CTCellStyleXfs.type, (XmlOptions)null);
        }
        
        public static CTCellStyleXfs parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCellStyleXfs)getTypeLoader().parse(node, CTCellStyleXfs.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCellStyleXfs parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCellStyleXfs)getTypeLoader().parse(xmlInputStream, CTCellStyleXfs.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCellStyleXfs parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCellStyleXfs)getTypeLoader().parse(xmlInputStream, CTCellStyleXfs.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCellStyleXfs.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCellStyleXfs.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
