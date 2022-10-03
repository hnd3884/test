package com.microsoft.schemas.office.x2006.encryption;

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

public interface EncryptionDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(EncryptionDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("encryptione8b3doctype");
    
    CTEncryption getEncryption();
    
    void setEncryption(final CTEncryption p0);
    
    CTEncryption addNewEncryption();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(EncryptionDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static EncryptionDocument newInstance() {
            return (EncryptionDocument)getTypeLoader().newInstance(EncryptionDocument.type, (XmlOptions)null);
        }
        
        public static EncryptionDocument newInstance(final XmlOptions xmlOptions) {
            return (EncryptionDocument)getTypeLoader().newInstance(EncryptionDocument.type, xmlOptions);
        }
        
        public static EncryptionDocument parse(final String s) throws XmlException {
            return (EncryptionDocument)getTypeLoader().parse(s, EncryptionDocument.type, (XmlOptions)null);
        }
        
        public static EncryptionDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (EncryptionDocument)getTypeLoader().parse(s, EncryptionDocument.type, xmlOptions);
        }
        
        public static EncryptionDocument parse(final File file) throws XmlException, IOException {
            return (EncryptionDocument)getTypeLoader().parse(file, EncryptionDocument.type, (XmlOptions)null);
        }
        
        public static EncryptionDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (EncryptionDocument)getTypeLoader().parse(file, EncryptionDocument.type, xmlOptions);
        }
        
        public static EncryptionDocument parse(final URL url) throws XmlException, IOException {
            return (EncryptionDocument)getTypeLoader().parse(url, EncryptionDocument.type, (XmlOptions)null);
        }
        
        public static EncryptionDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (EncryptionDocument)getTypeLoader().parse(url, EncryptionDocument.type, xmlOptions);
        }
        
        public static EncryptionDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (EncryptionDocument)getTypeLoader().parse(inputStream, EncryptionDocument.type, (XmlOptions)null);
        }
        
        public static EncryptionDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (EncryptionDocument)getTypeLoader().parse(inputStream, EncryptionDocument.type, xmlOptions);
        }
        
        public static EncryptionDocument parse(final Reader reader) throws XmlException, IOException {
            return (EncryptionDocument)getTypeLoader().parse(reader, EncryptionDocument.type, (XmlOptions)null);
        }
        
        public static EncryptionDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (EncryptionDocument)getTypeLoader().parse(reader, EncryptionDocument.type, xmlOptions);
        }
        
        public static EncryptionDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (EncryptionDocument)getTypeLoader().parse(xmlStreamReader, EncryptionDocument.type, (XmlOptions)null);
        }
        
        public static EncryptionDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (EncryptionDocument)getTypeLoader().parse(xmlStreamReader, EncryptionDocument.type, xmlOptions);
        }
        
        public static EncryptionDocument parse(final Node node) throws XmlException {
            return (EncryptionDocument)getTypeLoader().parse(node, EncryptionDocument.type, (XmlOptions)null);
        }
        
        public static EncryptionDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (EncryptionDocument)getTypeLoader().parse(node, EncryptionDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static EncryptionDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (EncryptionDocument)getTypeLoader().parse(xmlInputStream, EncryptionDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static EncryptionDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (EncryptionDocument)getTypeLoader().parse(xmlInputStream, EncryptionDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, EncryptionDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, EncryptionDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
