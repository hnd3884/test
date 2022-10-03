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

public interface CTKeyData extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTKeyData.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("ctkeydata6bdbtype");
    
    int getSaltSize();
    
    STSaltSize xgetSaltSize();
    
    void setSaltSize(final int p0);
    
    void xsetSaltSize(final STSaltSize p0);
    
    int getBlockSize();
    
    STBlockSize xgetBlockSize();
    
    void setBlockSize(final int p0);
    
    void xsetBlockSize(final STBlockSize p0);
    
    long getKeyBits();
    
    STKeyBits xgetKeyBits();
    
    void setKeyBits(final long p0);
    
    void xsetKeyBits(final STKeyBits p0);
    
    int getHashSize();
    
    STHashSize xgetHashSize();
    
    void setHashSize(final int p0);
    
    void xsetHashSize(final STHashSize p0);
    
    STCipherAlgorithm.Enum getCipherAlgorithm();
    
    STCipherAlgorithm xgetCipherAlgorithm();
    
    void setCipherAlgorithm(final STCipherAlgorithm.Enum p0);
    
    void xsetCipherAlgorithm(final STCipherAlgorithm p0);
    
    STCipherChaining.Enum getCipherChaining();
    
    STCipherChaining xgetCipherChaining();
    
    void setCipherChaining(final STCipherChaining.Enum p0);
    
    void xsetCipherChaining(final STCipherChaining p0);
    
    STHashAlgorithm.Enum getHashAlgorithm();
    
    STHashAlgorithm xgetHashAlgorithm();
    
    void setHashAlgorithm(final STHashAlgorithm.Enum p0);
    
    void xsetHashAlgorithm(final STHashAlgorithm p0);
    
    byte[] getSaltValue();
    
    XmlBase64Binary xgetSaltValue();
    
    void setSaltValue(final byte[] p0);
    
    void xsetSaltValue(final XmlBase64Binary p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTKeyData.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTKeyData newInstance() {
            return (CTKeyData)getTypeLoader().newInstance(CTKeyData.type, (XmlOptions)null);
        }
        
        public static CTKeyData newInstance(final XmlOptions xmlOptions) {
            return (CTKeyData)getTypeLoader().newInstance(CTKeyData.type, xmlOptions);
        }
        
        public static CTKeyData parse(final String s) throws XmlException {
            return (CTKeyData)getTypeLoader().parse(s, CTKeyData.type, (XmlOptions)null);
        }
        
        public static CTKeyData parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTKeyData)getTypeLoader().parse(s, CTKeyData.type, xmlOptions);
        }
        
        public static CTKeyData parse(final File file) throws XmlException, IOException {
            return (CTKeyData)getTypeLoader().parse(file, CTKeyData.type, (XmlOptions)null);
        }
        
        public static CTKeyData parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTKeyData)getTypeLoader().parse(file, CTKeyData.type, xmlOptions);
        }
        
        public static CTKeyData parse(final URL url) throws XmlException, IOException {
            return (CTKeyData)getTypeLoader().parse(url, CTKeyData.type, (XmlOptions)null);
        }
        
        public static CTKeyData parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTKeyData)getTypeLoader().parse(url, CTKeyData.type, xmlOptions);
        }
        
        public static CTKeyData parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTKeyData)getTypeLoader().parse(inputStream, CTKeyData.type, (XmlOptions)null);
        }
        
        public static CTKeyData parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTKeyData)getTypeLoader().parse(inputStream, CTKeyData.type, xmlOptions);
        }
        
        public static CTKeyData parse(final Reader reader) throws XmlException, IOException {
            return (CTKeyData)getTypeLoader().parse(reader, CTKeyData.type, (XmlOptions)null);
        }
        
        public static CTKeyData parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTKeyData)getTypeLoader().parse(reader, CTKeyData.type, xmlOptions);
        }
        
        public static CTKeyData parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTKeyData)getTypeLoader().parse(xmlStreamReader, CTKeyData.type, (XmlOptions)null);
        }
        
        public static CTKeyData parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTKeyData)getTypeLoader().parse(xmlStreamReader, CTKeyData.type, xmlOptions);
        }
        
        public static CTKeyData parse(final Node node) throws XmlException {
            return (CTKeyData)getTypeLoader().parse(node, CTKeyData.type, (XmlOptions)null);
        }
        
        public static CTKeyData parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTKeyData)getTypeLoader().parse(node, CTKeyData.type, xmlOptions);
        }
        
        @Deprecated
        public static CTKeyData parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTKeyData)getTypeLoader().parse(xmlInputStream, CTKeyData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTKeyData parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTKeyData)getTypeLoader().parse(xmlInputStream, CTKeyData.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTKeyData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTKeyData.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
