package com.microsoft.schemas.office.x2006.keyEncryptor.password;

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
import com.microsoft.schemas.office.x2006.encryption.STSpinCount;
import org.apache.xmlbeans.XmlBase64Binary;
import com.microsoft.schemas.office.x2006.encryption.STHashAlgorithm;
import com.microsoft.schemas.office.x2006.encryption.STCipherChaining;
import com.microsoft.schemas.office.x2006.encryption.STCipherAlgorithm;
import com.microsoft.schemas.office.x2006.encryption.STHashSize;
import com.microsoft.schemas.office.x2006.encryption.STKeyBits;
import com.microsoft.schemas.office.x2006.encryption.STBlockSize;
import com.microsoft.schemas.office.x2006.encryption.STSaltSize;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPasswordKeyEncryptor extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPasswordKeyEncryptor.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("ctpasswordkeyencryptorde24type");
    
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
    
    int getSpinCount();
    
    STSpinCount xgetSpinCount();
    
    void setSpinCount(final int p0);
    
    void xsetSpinCount(final STSpinCount p0);
    
    byte[] getEncryptedVerifierHashInput();
    
    XmlBase64Binary xgetEncryptedVerifierHashInput();
    
    void setEncryptedVerifierHashInput(final byte[] p0);
    
    void xsetEncryptedVerifierHashInput(final XmlBase64Binary p0);
    
    byte[] getEncryptedVerifierHashValue();
    
    XmlBase64Binary xgetEncryptedVerifierHashValue();
    
    void setEncryptedVerifierHashValue(final byte[] p0);
    
    void xsetEncryptedVerifierHashValue(final XmlBase64Binary p0);
    
    byte[] getEncryptedKeyValue();
    
    XmlBase64Binary xgetEncryptedKeyValue();
    
    void setEncryptedKeyValue(final byte[] p0);
    
    void xsetEncryptedKeyValue(final XmlBase64Binary p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPasswordKeyEncryptor.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPasswordKeyEncryptor newInstance() {
            return (CTPasswordKeyEncryptor)getTypeLoader().newInstance(CTPasswordKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTPasswordKeyEncryptor newInstance(final XmlOptions xmlOptions) {
            return (CTPasswordKeyEncryptor)getTypeLoader().newInstance(CTPasswordKeyEncryptor.type, xmlOptions);
        }
        
        public static CTPasswordKeyEncryptor parse(final String s) throws XmlException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(s, CTPasswordKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTPasswordKeyEncryptor parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(s, CTPasswordKeyEncryptor.type, xmlOptions);
        }
        
        public static CTPasswordKeyEncryptor parse(final File file) throws XmlException, IOException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(file, CTPasswordKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTPasswordKeyEncryptor parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(file, CTPasswordKeyEncryptor.type, xmlOptions);
        }
        
        public static CTPasswordKeyEncryptor parse(final URL url) throws XmlException, IOException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(url, CTPasswordKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTPasswordKeyEncryptor parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(url, CTPasswordKeyEncryptor.type, xmlOptions);
        }
        
        public static CTPasswordKeyEncryptor parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(inputStream, CTPasswordKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTPasswordKeyEncryptor parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(inputStream, CTPasswordKeyEncryptor.type, xmlOptions);
        }
        
        public static CTPasswordKeyEncryptor parse(final Reader reader) throws XmlException, IOException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(reader, CTPasswordKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTPasswordKeyEncryptor parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(reader, CTPasswordKeyEncryptor.type, xmlOptions);
        }
        
        public static CTPasswordKeyEncryptor parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(xmlStreamReader, CTPasswordKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTPasswordKeyEncryptor parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(xmlStreamReader, CTPasswordKeyEncryptor.type, xmlOptions);
        }
        
        public static CTPasswordKeyEncryptor parse(final Node node) throws XmlException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(node, CTPasswordKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTPasswordKeyEncryptor parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(node, CTPasswordKeyEncryptor.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPasswordKeyEncryptor parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(xmlInputStream, CTPasswordKeyEncryptor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPasswordKeyEncryptor parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPasswordKeyEncryptor)getTypeLoader().parse(xmlInputStream, CTPasswordKeyEncryptor.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPasswordKeyEncryptor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPasswordKeyEncryptor.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
