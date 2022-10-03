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
import org.apache.xmlbeans.XmlObject;

public interface FootnotesDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(FootnotesDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("footnotes8773doctype");
    
    CTFootnotes getFootnotes();
    
    void setFootnotes(final CTFootnotes p0);
    
    CTFootnotes addNewFootnotes();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(FootnotesDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static FootnotesDocument newInstance() {
            return (FootnotesDocument)getTypeLoader().newInstance(FootnotesDocument.type, (XmlOptions)null);
        }
        
        public static FootnotesDocument newInstance(final XmlOptions xmlOptions) {
            return (FootnotesDocument)getTypeLoader().newInstance(FootnotesDocument.type, xmlOptions);
        }
        
        public static FootnotesDocument parse(final String s) throws XmlException {
            return (FootnotesDocument)getTypeLoader().parse(s, FootnotesDocument.type, (XmlOptions)null);
        }
        
        public static FootnotesDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (FootnotesDocument)getTypeLoader().parse(s, FootnotesDocument.type, xmlOptions);
        }
        
        public static FootnotesDocument parse(final File file) throws XmlException, IOException {
            return (FootnotesDocument)getTypeLoader().parse(file, FootnotesDocument.type, (XmlOptions)null);
        }
        
        public static FootnotesDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (FootnotesDocument)getTypeLoader().parse(file, FootnotesDocument.type, xmlOptions);
        }
        
        public static FootnotesDocument parse(final URL url) throws XmlException, IOException {
            return (FootnotesDocument)getTypeLoader().parse(url, FootnotesDocument.type, (XmlOptions)null);
        }
        
        public static FootnotesDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (FootnotesDocument)getTypeLoader().parse(url, FootnotesDocument.type, xmlOptions);
        }
        
        public static FootnotesDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (FootnotesDocument)getTypeLoader().parse(inputStream, FootnotesDocument.type, (XmlOptions)null);
        }
        
        public static FootnotesDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (FootnotesDocument)getTypeLoader().parse(inputStream, FootnotesDocument.type, xmlOptions);
        }
        
        public static FootnotesDocument parse(final Reader reader) throws XmlException, IOException {
            return (FootnotesDocument)getTypeLoader().parse(reader, FootnotesDocument.type, (XmlOptions)null);
        }
        
        public static FootnotesDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (FootnotesDocument)getTypeLoader().parse(reader, FootnotesDocument.type, xmlOptions);
        }
        
        public static FootnotesDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (FootnotesDocument)getTypeLoader().parse(xmlStreamReader, FootnotesDocument.type, (XmlOptions)null);
        }
        
        public static FootnotesDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (FootnotesDocument)getTypeLoader().parse(xmlStreamReader, FootnotesDocument.type, xmlOptions);
        }
        
        public static FootnotesDocument parse(final Node node) throws XmlException {
            return (FootnotesDocument)getTypeLoader().parse(node, FootnotesDocument.type, (XmlOptions)null);
        }
        
        public static FootnotesDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (FootnotesDocument)getTypeLoader().parse(node, FootnotesDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static FootnotesDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (FootnotesDocument)getTypeLoader().parse(xmlInputStream, FootnotesDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static FootnotesDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (FootnotesDocument)getTypeLoader().parse(xmlInputStream, FootnotesDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, FootnotesDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, FootnotesDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
