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

public interface CTMergeCells extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTMergeCells.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctmergecells1242type");
    
    List<CTMergeCell> getMergeCellList();
    
    @Deprecated
    CTMergeCell[] getMergeCellArray();
    
    CTMergeCell getMergeCellArray(final int p0);
    
    int sizeOfMergeCellArray();
    
    void setMergeCellArray(final CTMergeCell[] p0);
    
    void setMergeCellArray(final int p0, final CTMergeCell p1);
    
    CTMergeCell insertNewMergeCell(final int p0);
    
    CTMergeCell addNewMergeCell();
    
    void removeMergeCell(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTMergeCells.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTMergeCells newInstance() {
            return (CTMergeCells)getTypeLoader().newInstance(CTMergeCells.type, (XmlOptions)null);
        }
        
        public static CTMergeCells newInstance(final XmlOptions xmlOptions) {
            return (CTMergeCells)getTypeLoader().newInstance(CTMergeCells.type, xmlOptions);
        }
        
        public static CTMergeCells parse(final String s) throws XmlException {
            return (CTMergeCells)getTypeLoader().parse(s, CTMergeCells.type, (XmlOptions)null);
        }
        
        public static CTMergeCells parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTMergeCells)getTypeLoader().parse(s, CTMergeCells.type, xmlOptions);
        }
        
        public static CTMergeCells parse(final File file) throws XmlException, IOException {
            return (CTMergeCells)getTypeLoader().parse(file, CTMergeCells.type, (XmlOptions)null);
        }
        
        public static CTMergeCells parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMergeCells)getTypeLoader().parse(file, CTMergeCells.type, xmlOptions);
        }
        
        public static CTMergeCells parse(final URL url) throws XmlException, IOException {
            return (CTMergeCells)getTypeLoader().parse(url, CTMergeCells.type, (XmlOptions)null);
        }
        
        public static CTMergeCells parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMergeCells)getTypeLoader().parse(url, CTMergeCells.type, xmlOptions);
        }
        
        public static CTMergeCells parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTMergeCells)getTypeLoader().parse(inputStream, CTMergeCells.type, (XmlOptions)null);
        }
        
        public static CTMergeCells parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMergeCells)getTypeLoader().parse(inputStream, CTMergeCells.type, xmlOptions);
        }
        
        public static CTMergeCells parse(final Reader reader) throws XmlException, IOException {
            return (CTMergeCells)getTypeLoader().parse(reader, CTMergeCells.type, (XmlOptions)null);
        }
        
        public static CTMergeCells parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMergeCells)getTypeLoader().parse(reader, CTMergeCells.type, xmlOptions);
        }
        
        public static CTMergeCells parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTMergeCells)getTypeLoader().parse(xmlStreamReader, CTMergeCells.type, (XmlOptions)null);
        }
        
        public static CTMergeCells parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTMergeCells)getTypeLoader().parse(xmlStreamReader, CTMergeCells.type, xmlOptions);
        }
        
        public static CTMergeCells parse(final Node node) throws XmlException {
            return (CTMergeCells)getTypeLoader().parse(node, CTMergeCells.type, (XmlOptions)null);
        }
        
        public static CTMergeCells parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTMergeCells)getTypeLoader().parse(node, CTMergeCells.type, xmlOptions);
        }
        
        @Deprecated
        public static CTMergeCells parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTMergeCells)getTypeLoader().parse(xmlInputStream, CTMergeCells.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTMergeCells parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTMergeCells)getTypeLoader().parse(xmlInputStream, CTMergeCells.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMergeCells.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMergeCells.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
