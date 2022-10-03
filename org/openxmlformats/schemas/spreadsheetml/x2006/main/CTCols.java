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

public interface CTCols extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCols.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcols627ctype");
    
    List<CTCol> getColList();
    
    @Deprecated
    CTCol[] getColArray();
    
    CTCol getColArray(final int p0);
    
    int sizeOfColArray();
    
    void setColArray(final CTCol[] p0);
    
    void setColArray(final int p0, final CTCol p1);
    
    CTCol insertNewCol(final int p0);
    
    CTCol addNewCol();
    
    void removeCol(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCols.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCols newInstance() {
            return (CTCols)getTypeLoader().newInstance(CTCols.type, (XmlOptions)null);
        }
        
        public static CTCols newInstance(final XmlOptions xmlOptions) {
            return (CTCols)getTypeLoader().newInstance(CTCols.type, xmlOptions);
        }
        
        public static CTCols parse(final String s) throws XmlException {
            return (CTCols)getTypeLoader().parse(s, CTCols.type, (XmlOptions)null);
        }
        
        public static CTCols parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCols)getTypeLoader().parse(s, CTCols.type, xmlOptions);
        }
        
        public static CTCols parse(final File file) throws XmlException, IOException {
            return (CTCols)getTypeLoader().parse(file, CTCols.type, (XmlOptions)null);
        }
        
        public static CTCols parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCols)getTypeLoader().parse(file, CTCols.type, xmlOptions);
        }
        
        public static CTCols parse(final URL url) throws XmlException, IOException {
            return (CTCols)getTypeLoader().parse(url, CTCols.type, (XmlOptions)null);
        }
        
        public static CTCols parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCols)getTypeLoader().parse(url, CTCols.type, xmlOptions);
        }
        
        public static CTCols parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCols)getTypeLoader().parse(inputStream, CTCols.type, (XmlOptions)null);
        }
        
        public static CTCols parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCols)getTypeLoader().parse(inputStream, CTCols.type, xmlOptions);
        }
        
        public static CTCols parse(final Reader reader) throws XmlException, IOException {
            return (CTCols)getTypeLoader().parse(reader, CTCols.type, (XmlOptions)null);
        }
        
        public static CTCols parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCols)getTypeLoader().parse(reader, CTCols.type, xmlOptions);
        }
        
        public static CTCols parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCols)getTypeLoader().parse(xmlStreamReader, CTCols.type, (XmlOptions)null);
        }
        
        public static CTCols parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCols)getTypeLoader().parse(xmlStreamReader, CTCols.type, xmlOptions);
        }
        
        public static CTCols parse(final Node node) throws XmlException {
            return (CTCols)getTypeLoader().parse(node, CTCols.type, (XmlOptions)null);
        }
        
        public static CTCols parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCols)getTypeLoader().parse(node, CTCols.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCols parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCols)getTypeLoader().parse(xmlInputStream, CTCols.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCols parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCols)getTypeLoader().parse(xmlInputStream, CTCols.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCols.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCols.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
