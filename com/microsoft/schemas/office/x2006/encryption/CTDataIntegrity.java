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
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTDataIntegrity extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDataIntegrity.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("ctdataintegrity6eb5type");
    
    byte[] getEncryptedHmacKey();
    
    XmlBase64Binary xgetEncryptedHmacKey();
    
    void setEncryptedHmacKey(final byte[] p0);
    
    void xsetEncryptedHmacKey(final XmlBase64Binary p0);
    
    byte[] getEncryptedHmacValue();
    
    XmlBase64Binary xgetEncryptedHmacValue();
    
    void setEncryptedHmacValue(final byte[] p0);
    
    void xsetEncryptedHmacValue(final XmlBase64Binary p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDataIntegrity.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDataIntegrity newInstance() {
            return (CTDataIntegrity)getTypeLoader().newInstance(CTDataIntegrity.type, (XmlOptions)null);
        }
        
        public static CTDataIntegrity newInstance(final XmlOptions xmlOptions) {
            return (CTDataIntegrity)getTypeLoader().newInstance(CTDataIntegrity.type, xmlOptions);
        }
        
        public static CTDataIntegrity parse(final String s) throws XmlException {
            return (CTDataIntegrity)getTypeLoader().parse(s, CTDataIntegrity.type, (XmlOptions)null);
        }
        
        public static CTDataIntegrity parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataIntegrity)getTypeLoader().parse(s, CTDataIntegrity.type, xmlOptions);
        }
        
        public static CTDataIntegrity parse(final File file) throws XmlException, IOException {
            return (CTDataIntegrity)getTypeLoader().parse(file, CTDataIntegrity.type, (XmlOptions)null);
        }
        
        public static CTDataIntegrity parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataIntegrity)getTypeLoader().parse(file, CTDataIntegrity.type, xmlOptions);
        }
        
        public static CTDataIntegrity parse(final URL url) throws XmlException, IOException {
            return (CTDataIntegrity)getTypeLoader().parse(url, CTDataIntegrity.type, (XmlOptions)null);
        }
        
        public static CTDataIntegrity parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataIntegrity)getTypeLoader().parse(url, CTDataIntegrity.type, xmlOptions);
        }
        
        public static CTDataIntegrity parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDataIntegrity)getTypeLoader().parse(inputStream, CTDataIntegrity.type, (XmlOptions)null);
        }
        
        public static CTDataIntegrity parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataIntegrity)getTypeLoader().parse(inputStream, CTDataIntegrity.type, xmlOptions);
        }
        
        public static CTDataIntegrity parse(final Reader reader) throws XmlException, IOException {
            return (CTDataIntegrity)getTypeLoader().parse(reader, CTDataIntegrity.type, (XmlOptions)null);
        }
        
        public static CTDataIntegrity parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataIntegrity)getTypeLoader().parse(reader, CTDataIntegrity.type, xmlOptions);
        }
        
        public static CTDataIntegrity parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDataIntegrity)getTypeLoader().parse(xmlStreamReader, CTDataIntegrity.type, (XmlOptions)null);
        }
        
        public static CTDataIntegrity parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataIntegrity)getTypeLoader().parse(xmlStreamReader, CTDataIntegrity.type, xmlOptions);
        }
        
        public static CTDataIntegrity parse(final Node node) throws XmlException {
            return (CTDataIntegrity)getTypeLoader().parse(node, CTDataIntegrity.type, (XmlOptions)null);
        }
        
        public static CTDataIntegrity parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataIntegrity)getTypeLoader().parse(node, CTDataIntegrity.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDataIntegrity parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDataIntegrity)getTypeLoader().parse(xmlInputStream, CTDataIntegrity.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDataIntegrity parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDataIntegrity)getTypeLoader().parse(xmlInputStream, CTDataIntegrity.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDataIntegrity.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDataIntegrity.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
