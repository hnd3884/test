package com.microsoft.schemas.office.visio.x2012.main;

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

public interface VisioDocumentDocument1 extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(VisioDocumentDocument1.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("visiodocumentd431doctype");
    
    VisioDocumentType getVisioDocument();
    
    void setVisioDocument(final VisioDocumentType p0);
    
    VisioDocumentType addNewVisioDocument();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(VisioDocumentDocument1.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static VisioDocumentDocument1 newInstance() {
            return (VisioDocumentDocument1)getTypeLoader().newInstance(VisioDocumentDocument1.type, (XmlOptions)null);
        }
        
        public static VisioDocumentDocument1 newInstance(final XmlOptions xmlOptions) {
            return (VisioDocumentDocument1)getTypeLoader().newInstance(VisioDocumentDocument1.type, xmlOptions);
        }
        
        public static VisioDocumentDocument1 parse(final String s) throws XmlException {
            return (VisioDocumentDocument1)getTypeLoader().parse(s, VisioDocumentDocument1.type, (XmlOptions)null);
        }
        
        public static VisioDocumentDocument1 parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (VisioDocumentDocument1)getTypeLoader().parse(s, VisioDocumentDocument1.type, xmlOptions);
        }
        
        public static VisioDocumentDocument1 parse(final File file) throws XmlException, IOException {
            return (VisioDocumentDocument1)getTypeLoader().parse(file, VisioDocumentDocument1.type, (XmlOptions)null);
        }
        
        public static VisioDocumentDocument1 parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (VisioDocumentDocument1)getTypeLoader().parse(file, VisioDocumentDocument1.type, xmlOptions);
        }
        
        public static VisioDocumentDocument1 parse(final URL url) throws XmlException, IOException {
            return (VisioDocumentDocument1)getTypeLoader().parse(url, VisioDocumentDocument1.type, (XmlOptions)null);
        }
        
        public static VisioDocumentDocument1 parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (VisioDocumentDocument1)getTypeLoader().parse(url, VisioDocumentDocument1.type, xmlOptions);
        }
        
        public static VisioDocumentDocument1 parse(final InputStream inputStream) throws XmlException, IOException {
            return (VisioDocumentDocument1)getTypeLoader().parse(inputStream, VisioDocumentDocument1.type, (XmlOptions)null);
        }
        
        public static VisioDocumentDocument1 parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (VisioDocumentDocument1)getTypeLoader().parse(inputStream, VisioDocumentDocument1.type, xmlOptions);
        }
        
        public static VisioDocumentDocument1 parse(final Reader reader) throws XmlException, IOException {
            return (VisioDocumentDocument1)getTypeLoader().parse(reader, VisioDocumentDocument1.type, (XmlOptions)null);
        }
        
        public static VisioDocumentDocument1 parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (VisioDocumentDocument1)getTypeLoader().parse(reader, VisioDocumentDocument1.type, xmlOptions);
        }
        
        public static VisioDocumentDocument1 parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (VisioDocumentDocument1)getTypeLoader().parse(xmlStreamReader, VisioDocumentDocument1.type, (XmlOptions)null);
        }
        
        public static VisioDocumentDocument1 parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (VisioDocumentDocument1)getTypeLoader().parse(xmlStreamReader, VisioDocumentDocument1.type, xmlOptions);
        }
        
        public static VisioDocumentDocument1 parse(final Node node) throws XmlException {
            return (VisioDocumentDocument1)getTypeLoader().parse(node, VisioDocumentDocument1.type, (XmlOptions)null);
        }
        
        public static VisioDocumentDocument1 parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (VisioDocumentDocument1)getTypeLoader().parse(node, VisioDocumentDocument1.type, xmlOptions);
        }
        
        @Deprecated
        public static VisioDocumentDocument1 parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (VisioDocumentDocument1)getTypeLoader().parse(xmlInputStream, VisioDocumentDocument1.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static VisioDocumentDocument1 parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (VisioDocumentDocument1)getTypeLoader().parse(xmlInputStream, VisioDocumentDocument1.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, VisioDocumentDocument1.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, VisioDocumentDocument1.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
