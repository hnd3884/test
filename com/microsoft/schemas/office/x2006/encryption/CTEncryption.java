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

public interface CTEncryption extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTEncryption.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("ctencryption365ftype");
    
    CTKeyData getKeyData();
    
    void setKeyData(final CTKeyData p0);
    
    CTKeyData addNewKeyData();
    
    CTDataIntegrity getDataIntegrity();
    
    void setDataIntegrity(final CTDataIntegrity p0);
    
    CTDataIntegrity addNewDataIntegrity();
    
    CTKeyEncryptors getKeyEncryptors();
    
    void setKeyEncryptors(final CTKeyEncryptors p0);
    
    CTKeyEncryptors addNewKeyEncryptors();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTEncryption.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTEncryption newInstance() {
            return (CTEncryption)getTypeLoader().newInstance(CTEncryption.type, (XmlOptions)null);
        }
        
        public static CTEncryption newInstance(final XmlOptions xmlOptions) {
            return (CTEncryption)getTypeLoader().newInstance(CTEncryption.type, xmlOptions);
        }
        
        public static CTEncryption parse(final String s) throws XmlException {
            return (CTEncryption)getTypeLoader().parse(s, CTEncryption.type, (XmlOptions)null);
        }
        
        public static CTEncryption parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTEncryption)getTypeLoader().parse(s, CTEncryption.type, xmlOptions);
        }
        
        public static CTEncryption parse(final File file) throws XmlException, IOException {
            return (CTEncryption)getTypeLoader().parse(file, CTEncryption.type, (XmlOptions)null);
        }
        
        public static CTEncryption parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEncryption)getTypeLoader().parse(file, CTEncryption.type, xmlOptions);
        }
        
        public static CTEncryption parse(final URL url) throws XmlException, IOException {
            return (CTEncryption)getTypeLoader().parse(url, CTEncryption.type, (XmlOptions)null);
        }
        
        public static CTEncryption parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEncryption)getTypeLoader().parse(url, CTEncryption.type, xmlOptions);
        }
        
        public static CTEncryption parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTEncryption)getTypeLoader().parse(inputStream, CTEncryption.type, (XmlOptions)null);
        }
        
        public static CTEncryption parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEncryption)getTypeLoader().parse(inputStream, CTEncryption.type, xmlOptions);
        }
        
        public static CTEncryption parse(final Reader reader) throws XmlException, IOException {
            return (CTEncryption)getTypeLoader().parse(reader, CTEncryption.type, (XmlOptions)null);
        }
        
        public static CTEncryption parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEncryption)getTypeLoader().parse(reader, CTEncryption.type, xmlOptions);
        }
        
        public static CTEncryption parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTEncryption)getTypeLoader().parse(xmlStreamReader, CTEncryption.type, (XmlOptions)null);
        }
        
        public static CTEncryption parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTEncryption)getTypeLoader().parse(xmlStreamReader, CTEncryption.type, xmlOptions);
        }
        
        public static CTEncryption parse(final Node node) throws XmlException {
            return (CTEncryption)getTypeLoader().parse(node, CTEncryption.type, (XmlOptions)null);
        }
        
        public static CTEncryption parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTEncryption)getTypeLoader().parse(node, CTEncryption.type, xmlOptions);
        }
        
        @Deprecated
        public static CTEncryption parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTEncryption)getTypeLoader().parse(xmlInputStream, CTEncryption.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTEncryption parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTEncryption)getTypeLoader().parse(xmlInputStream, CTEncryption.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEncryption.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEncryption.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
