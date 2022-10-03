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

public interface CTDocument1 extends CTDocumentBase
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDocument1.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdocument64adtype");
    
    CTBody getBody();
    
    boolean isSetBody();
    
    void setBody(final CTBody p0);
    
    CTBody addNewBody();
    
    void unsetBody();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDocument1.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDocument1 newInstance() {
            return (CTDocument1)getTypeLoader().newInstance(CTDocument1.type, (XmlOptions)null);
        }
        
        public static CTDocument1 newInstance(final XmlOptions xmlOptions) {
            return (CTDocument1)getTypeLoader().newInstance(CTDocument1.type, xmlOptions);
        }
        
        public static CTDocument1 parse(final String s) throws XmlException {
            return (CTDocument1)getTypeLoader().parse(s, CTDocument1.type, (XmlOptions)null);
        }
        
        public static CTDocument1 parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDocument1)getTypeLoader().parse(s, CTDocument1.type, xmlOptions);
        }
        
        public static CTDocument1 parse(final File file) throws XmlException, IOException {
            return (CTDocument1)getTypeLoader().parse(file, CTDocument1.type, (XmlOptions)null);
        }
        
        public static CTDocument1 parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocument1)getTypeLoader().parse(file, CTDocument1.type, xmlOptions);
        }
        
        public static CTDocument1 parse(final URL url) throws XmlException, IOException {
            return (CTDocument1)getTypeLoader().parse(url, CTDocument1.type, (XmlOptions)null);
        }
        
        public static CTDocument1 parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocument1)getTypeLoader().parse(url, CTDocument1.type, xmlOptions);
        }
        
        public static CTDocument1 parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDocument1)getTypeLoader().parse(inputStream, CTDocument1.type, (XmlOptions)null);
        }
        
        public static CTDocument1 parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocument1)getTypeLoader().parse(inputStream, CTDocument1.type, xmlOptions);
        }
        
        public static CTDocument1 parse(final Reader reader) throws XmlException, IOException {
            return (CTDocument1)getTypeLoader().parse(reader, CTDocument1.type, (XmlOptions)null);
        }
        
        public static CTDocument1 parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocument1)getTypeLoader().parse(reader, CTDocument1.type, xmlOptions);
        }
        
        public static CTDocument1 parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDocument1)getTypeLoader().parse(xmlStreamReader, CTDocument1.type, (XmlOptions)null);
        }
        
        public static CTDocument1 parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDocument1)getTypeLoader().parse(xmlStreamReader, CTDocument1.type, xmlOptions);
        }
        
        public static CTDocument1 parse(final Node node) throws XmlException {
            return (CTDocument1)getTypeLoader().parse(node, CTDocument1.type, (XmlOptions)null);
        }
        
        public static CTDocument1 parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDocument1)getTypeLoader().parse(node, CTDocument1.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDocument1 parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDocument1)getTypeLoader().parse(xmlInputStream, CTDocument1.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDocument1 parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDocument1)getTypeLoader().parse(xmlInputStream, CTDocument1.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDocument1.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDocument1.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
