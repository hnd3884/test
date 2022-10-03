package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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
import java.util.Calendar;
import org.apache.xmlbeans.SchemaType;

public interface CTMoveBookmark extends CTBookmark
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTMoveBookmark.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctmovebookmarkf7a1type");
    
    String getAuthor();
    
    STString xgetAuthor();
    
    void setAuthor(final String p0);
    
    void xsetAuthor(final STString p0);
    
    Calendar getDate();
    
    STDateTime xgetDate();
    
    void setDate(final Calendar p0);
    
    void xsetDate(final STDateTime p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTMoveBookmark.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTMoveBookmark newInstance() {
            return (CTMoveBookmark)getTypeLoader().newInstance(CTMoveBookmark.type, (XmlOptions)null);
        }
        
        public static CTMoveBookmark newInstance(final XmlOptions xmlOptions) {
            return (CTMoveBookmark)getTypeLoader().newInstance(CTMoveBookmark.type, xmlOptions);
        }
        
        public static CTMoveBookmark parse(final String s) throws XmlException {
            return (CTMoveBookmark)getTypeLoader().parse(s, CTMoveBookmark.type, (XmlOptions)null);
        }
        
        public static CTMoveBookmark parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTMoveBookmark)getTypeLoader().parse(s, CTMoveBookmark.type, xmlOptions);
        }
        
        public static CTMoveBookmark parse(final File file) throws XmlException, IOException {
            return (CTMoveBookmark)getTypeLoader().parse(file, CTMoveBookmark.type, (XmlOptions)null);
        }
        
        public static CTMoveBookmark parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMoveBookmark)getTypeLoader().parse(file, CTMoveBookmark.type, xmlOptions);
        }
        
        public static CTMoveBookmark parse(final URL url) throws XmlException, IOException {
            return (CTMoveBookmark)getTypeLoader().parse(url, CTMoveBookmark.type, (XmlOptions)null);
        }
        
        public static CTMoveBookmark parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMoveBookmark)getTypeLoader().parse(url, CTMoveBookmark.type, xmlOptions);
        }
        
        public static CTMoveBookmark parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTMoveBookmark)getTypeLoader().parse(inputStream, CTMoveBookmark.type, (XmlOptions)null);
        }
        
        public static CTMoveBookmark parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMoveBookmark)getTypeLoader().parse(inputStream, CTMoveBookmark.type, xmlOptions);
        }
        
        public static CTMoveBookmark parse(final Reader reader) throws XmlException, IOException {
            return (CTMoveBookmark)getTypeLoader().parse(reader, CTMoveBookmark.type, (XmlOptions)null);
        }
        
        public static CTMoveBookmark parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMoveBookmark)getTypeLoader().parse(reader, CTMoveBookmark.type, xmlOptions);
        }
        
        public static CTMoveBookmark parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTMoveBookmark)getTypeLoader().parse(xmlStreamReader, CTMoveBookmark.type, (XmlOptions)null);
        }
        
        public static CTMoveBookmark parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTMoveBookmark)getTypeLoader().parse(xmlStreamReader, CTMoveBookmark.type, xmlOptions);
        }
        
        public static CTMoveBookmark parse(final Node node) throws XmlException {
            return (CTMoveBookmark)getTypeLoader().parse(node, CTMoveBookmark.type, (XmlOptions)null);
        }
        
        public static CTMoveBookmark parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTMoveBookmark)getTypeLoader().parse(node, CTMoveBookmark.type, xmlOptions);
        }
        
        @Deprecated
        public static CTMoveBookmark parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTMoveBookmark)getTypeLoader().parse(xmlInputStream, CTMoveBookmark.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTMoveBookmark parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTMoveBookmark)getTypeLoader().parse(xmlInputStream, CTMoveBookmark.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMoveBookmark.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMoveBookmark.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
