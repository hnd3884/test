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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTKeyEncryptors extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTKeyEncryptors.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("ctkeyencryptorsa09ctype");
    
    List<CTKeyEncryptor> getKeyEncryptorList();
    
    @Deprecated
    CTKeyEncryptor[] getKeyEncryptorArray();
    
    CTKeyEncryptor getKeyEncryptorArray(final int p0);
    
    int sizeOfKeyEncryptorArray();
    
    void setKeyEncryptorArray(final CTKeyEncryptor[] p0);
    
    void setKeyEncryptorArray(final int p0, final CTKeyEncryptor p1);
    
    CTKeyEncryptor insertNewKeyEncryptor(final int p0);
    
    CTKeyEncryptor addNewKeyEncryptor();
    
    void removeKeyEncryptor(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTKeyEncryptors.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTKeyEncryptors newInstance() {
            return (CTKeyEncryptors)getTypeLoader().newInstance(CTKeyEncryptors.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptors newInstance(final XmlOptions xmlOptions) {
            return (CTKeyEncryptors)getTypeLoader().newInstance(CTKeyEncryptors.type, xmlOptions);
        }
        
        public static CTKeyEncryptors parse(final String s) throws XmlException {
            return (CTKeyEncryptors)getTypeLoader().parse(s, CTKeyEncryptors.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptors parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTKeyEncryptors)getTypeLoader().parse(s, CTKeyEncryptors.type, xmlOptions);
        }
        
        public static CTKeyEncryptors parse(final File file) throws XmlException, IOException {
            return (CTKeyEncryptors)getTypeLoader().parse(file, CTKeyEncryptors.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptors parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTKeyEncryptors)getTypeLoader().parse(file, CTKeyEncryptors.type, xmlOptions);
        }
        
        public static CTKeyEncryptors parse(final URL url) throws XmlException, IOException {
            return (CTKeyEncryptors)getTypeLoader().parse(url, CTKeyEncryptors.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptors parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTKeyEncryptors)getTypeLoader().parse(url, CTKeyEncryptors.type, xmlOptions);
        }
        
        public static CTKeyEncryptors parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTKeyEncryptors)getTypeLoader().parse(inputStream, CTKeyEncryptors.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptors parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTKeyEncryptors)getTypeLoader().parse(inputStream, CTKeyEncryptors.type, xmlOptions);
        }
        
        public static CTKeyEncryptors parse(final Reader reader) throws XmlException, IOException {
            return (CTKeyEncryptors)getTypeLoader().parse(reader, CTKeyEncryptors.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptors parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTKeyEncryptors)getTypeLoader().parse(reader, CTKeyEncryptors.type, xmlOptions);
        }
        
        public static CTKeyEncryptors parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTKeyEncryptors)getTypeLoader().parse(xmlStreamReader, CTKeyEncryptors.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptors parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTKeyEncryptors)getTypeLoader().parse(xmlStreamReader, CTKeyEncryptors.type, xmlOptions);
        }
        
        public static CTKeyEncryptors parse(final Node node) throws XmlException {
            return (CTKeyEncryptors)getTypeLoader().parse(node, CTKeyEncryptors.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptors parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTKeyEncryptors)getTypeLoader().parse(node, CTKeyEncryptors.type, xmlOptions);
        }
        
        @Deprecated
        public static CTKeyEncryptors parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTKeyEncryptors)getTypeLoader().parse(xmlInputStream, CTKeyEncryptors.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTKeyEncryptors parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTKeyEncryptors)getTypeLoader().parse(xmlInputStream, CTKeyEncryptors.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTKeyEncryptors.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTKeyEncryptors.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
