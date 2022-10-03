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

public interface CTAuthors extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTAuthors.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctauthorsb8a7type");
    
    List<String> getAuthorList();
    
    @Deprecated
    String[] getAuthorArray();
    
    String getAuthorArray(final int p0);
    
    List<STXstring> xgetAuthorList();
    
    @Deprecated
    STXstring[] xgetAuthorArray();
    
    STXstring xgetAuthorArray(final int p0);
    
    int sizeOfAuthorArray();
    
    void setAuthorArray(final String[] p0);
    
    void setAuthorArray(final int p0, final String p1);
    
    void xsetAuthorArray(final STXstring[] p0);
    
    void xsetAuthorArray(final int p0, final STXstring p1);
    
    void insertAuthor(final int p0, final String p1);
    
    void addAuthor(final String p0);
    
    STXstring insertNewAuthor(final int p0);
    
    STXstring addNewAuthor();
    
    void removeAuthor(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTAuthors.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTAuthors newInstance() {
            return (CTAuthors)getTypeLoader().newInstance(CTAuthors.type, (XmlOptions)null);
        }
        
        public static CTAuthors newInstance(final XmlOptions xmlOptions) {
            return (CTAuthors)getTypeLoader().newInstance(CTAuthors.type, xmlOptions);
        }
        
        public static CTAuthors parse(final String s) throws XmlException {
            return (CTAuthors)getTypeLoader().parse(s, CTAuthors.type, (XmlOptions)null);
        }
        
        public static CTAuthors parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTAuthors)getTypeLoader().parse(s, CTAuthors.type, xmlOptions);
        }
        
        public static CTAuthors parse(final File file) throws XmlException, IOException {
            return (CTAuthors)getTypeLoader().parse(file, CTAuthors.type, (XmlOptions)null);
        }
        
        public static CTAuthors parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAuthors)getTypeLoader().parse(file, CTAuthors.type, xmlOptions);
        }
        
        public static CTAuthors parse(final URL url) throws XmlException, IOException {
            return (CTAuthors)getTypeLoader().parse(url, CTAuthors.type, (XmlOptions)null);
        }
        
        public static CTAuthors parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAuthors)getTypeLoader().parse(url, CTAuthors.type, xmlOptions);
        }
        
        public static CTAuthors parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTAuthors)getTypeLoader().parse(inputStream, CTAuthors.type, (XmlOptions)null);
        }
        
        public static CTAuthors parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAuthors)getTypeLoader().parse(inputStream, CTAuthors.type, xmlOptions);
        }
        
        public static CTAuthors parse(final Reader reader) throws XmlException, IOException {
            return (CTAuthors)getTypeLoader().parse(reader, CTAuthors.type, (XmlOptions)null);
        }
        
        public static CTAuthors parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAuthors)getTypeLoader().parse(reader, CTAuthors.type, xmlOptions);
        }
        
        public static CTAuthors parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTAuthors)getTypeLoader().parse(xmlStreamReader, CTAuthors.type, (XmlOptions)null);
        }
        
        public static CTAuthors parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTAuthors)getTypeLoader().parse(xmlStreamReader, CTAuthors.type, xmlOptions);
        }
        
        public static CTAuthors parse(final Node node) throws XmlException {
            return (CTAuthors)getTypeLoader().parse(node, CTAuthors.type, (XmlOptions)null);
        }
        
        public static CTAuthors parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTAuthors)getTypeLoader().parse(node, CTAuthors.type, xmlOptions);
        }
        
        @Deprecated
        public static CTAuthors parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTAuthors)getTypeLoader().parse(xmlInputStream, CTAuthors.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTAuthors parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTAuthors)getTypeLoader().parse(xmlInputStream, CTAuthors.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAuthors.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAuthors.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
