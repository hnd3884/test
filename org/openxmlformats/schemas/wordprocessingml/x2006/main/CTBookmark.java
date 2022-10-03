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
import org.apache.xmlbeans.SchemaType;

public interface CTBookmark extends CTBookmarkRange
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBookmark.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbookmarkd672type");
    
    String getName();
    
    STString xgetName();
    
    void setName(final String p0);
    
    void xsetName(final STString p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBookmark.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBookmark newInstance() {
            return (CTBookmark)getTypeLoader().newInstance(CTBookmark.type, (XmlOptions)null);
        }
        
        public static CTBookmark newInstance(final XmlOptions xmlOptions) {
            return (CTBookmark)getTypeLoader().newInstance(CTBookmark.type, xmlOptions);
        }
        
        public static CTBookmark parse(final String s) throws XmlException {
            return (CTBookmark)getTypeLoader().parse(s, CTBookmark.type, (XmlOptions)null);
        }
        
        public static CTBookmark parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBookmark)getTypeLoader().parse(s, CTBookmark.type, xmlOptions);
        }
        
        public static CTBookmark parse(final File file) throws XmlException, IOException {
            return (CTBookmark)getTypeLoader().parse(file, CTBookmark.type, (XmlOptions)null);
        }
        
        public static CTBookmark parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookmark)getTypeLoader().parse(file, CTBookmark.type, xmlOptions);
        }
        
        public static CTBookmark parse(final URL url) throws XmlException, IOException {
            return (CTBookmark)getTypeLoader().parse(url, CTBookmark.type, (XmlOptions)null);
        }
        
        public static CTBookmark parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookmark)getTypeLoader().parse(url, CTBookmark.type, xmlOptions);
        }
        
        public static CTBookmark parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBookmark)getTypeLoader().parse(inputStream, CTBookmark.type, (XmlOptions)null);
        }
        
        public static CTBookmark parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookmark)getTypeLoader().parse(inputStream, CTBookmark.type, xmlOptions);
        }
        
        public static CTBookmark parse(final Reader reader) throws XmlException, IOException {
            return (CTBookmark)getTypeLoader().parse(reader, CTBookmark.type, (XmlOptions)null);
        }
        
        public static CTBookmark parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookmark)getTypeLoader().parse(reader, CTBookmark.type, xmlOptions);
        }
        
        public static CTBookmark parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBookmark)getTypeLoader().parse(xmlStreamReader, CTBookmark.type, (XmlOptions)null);
        }
        
        public static CTBookmark parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBookmark)getTypeLoader().parse(xmlStreamReader, CTBookmark.type, xmlOptions);
        }
        
        public static CTBookmark parse(final Node node) throws XmlException {
            return (CTBookmark)getTypeLoader().parse(node, CTBookmark.type, (XmlOptions)null);
        }
        
        public static CTBookmark parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBookmark)getTypeLoader().parse(node, CTBookmark.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBookmark parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBookmark)getTypeLoader().parse(xmlInputStream, CTBookmark.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBookmark parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBookmark)getTypeLoader().parse(xmlInputStream, CTBookmark.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBookmark.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBookmark.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
