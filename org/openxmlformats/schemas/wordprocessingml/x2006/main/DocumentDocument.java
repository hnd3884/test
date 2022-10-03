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

public interface DocumentDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(DocumentDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("document2bd9doctype");
    
    CTDocument1 getDocument();
    
    void setDocument(final CTDocument1 p0);
    
    CTDocument1 addNewDocument();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(DocumentDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static DocumentDocument newInstance() {
            return (DocumentDocument)getTypeLoader().newInstance(DocumentDocument.type, (XmlOptions)null);
        }
        
        public static DocumentDocument newInstance(final XmlOptions xmlOptions) {
            return (DocumentDocument)getTypeLoader().newInstance(DocumentDocument.type, xmlOptions);
        }
        
        public static DocumentDocument parse(final String s) throws XmlException {
            return (DocumentDocument)getTypeLoader().parse(s, DocumentDocument.type, (XmlOptions)null);
        }
        
        public static DocumentDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (DocumentDocument)getTypeLoader().parse(s, DocumentDocument.type, xmlOptions);
        }
        
        public static DocumentDocument parse(final File file) throws XmlException, IOException {
            return (DocumentDocument)getTypeLoader().parse(file, DocumentDocument.type, (XmlOptions)null);
        }
        
        public static DocumentDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DocumentDocument)getTypeLoader().parse(file, DocumentDocument.type, xmlOptions);
        }
        
        public static DocumentDocument parse(final URL url) throws XmlException, IOException {
            return (DocumentDocument)getTypeLoader().parse(url, DocumentDocument.type, (XmlOptions)null);
        }
        
        public static DocumentDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DocumentDocument)getTypeLoader().parse(url, DocumentDocument.type, xmlOptions);
        }
        
        public static DocumentDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (DocumentDocument)getTypeLoader().parse(inputStream, DocumentDocument.type, (XmlOptions)null);
        }
        
        public static DocumentDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DocumentDocument)getTypeLoader().parse(inputStream, DocumentDocument.type, xmlOptions);
        }
        
        public static DocumentDocument parse(final Reader reader) throws XmlException, IOException {
            return (DocumentDocument)getTypeLoader().parse(reader, DocumentDocument.type, (XmlOptions)null);
        }
        
        public static DocumentDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DocumentDocument)getTypeLoader().parse(reader, DocumentDocument.type, xmlOptions);
        }
        
        public static DocumentDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (DocumentDocument)getTypeLoader().parse(xmlStreamReader, DocumentDocument.type, (XmlOptions)null);
        }
        
        public static DocumentDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (DocumentDocument)getTypeLoader().parse(xmlStreamReader, DocumentDocument.type, xmlOptions);
        }
        
        public static DocumentDocument parse(final Node node) throws XmlException {
            return (DocumentDocument)getTypeLoader().parse(node, DocumentDocument.type, (XmlOptions)null);
        }
        
        public static DocumentDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (DocumentDocument)getTypeLoader().parse(node, DocumentDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static DocumentDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (DocumentDocument)getTypeLoader().parse(xmlInputStream, DocumentDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static DocumentDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (DocumentDocument)getTypeLoader().parse(xmlInputStream, DocumentDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, DocumentDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, DocumentDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
