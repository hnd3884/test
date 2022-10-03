package com.microsoft.schemas.office.x2006.encryption;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
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
import com.microsoft.schemas.office.x2006.keyEncryptor.certificate.CTCertificateKeyEncryptor;
import com.microsoft.schemas.office.x2006.keyEncryptor.password.CTPasswordKeyEncryptor;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTKeyEncryptor extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTKeyEncryptor.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("ctkeyencryptor1205type");
    
    CTPasswordKeyEncryptor getEncryptedPasswordKey();
    
    boolean isSetEncryptedPasswordKey();
    
    void setEncryptedPasswordKey(final CTPasswordKeyEncryptor p0);
    
    CTPasswordKeyEncryptor addNewEncryptedPasswordKey();
    
    void unsetEncryptedPasswordKey();
    
    CTCertificateKeyEncryptor getEncryptedCertificateKey();
    
    boolean isSetEncryptedCertificateKey();
    
    void setEncryptedCertificateKey(final CTCertificateKeyEncryptor p0);
    
    CTCertificateKeyEncryptor addNewEncryptedCertificateKey();
    
    void unsetEncryptedCertificateKey();
    
    Uri.Enum getUri();
    
    Uri xgetUri();
    
    boolean isSetUri();
    
    void setUri(final Uri.Enum p0);
    
    void xsetUri(final Uri p0);
    
    void unsetUri();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTKeyEncryptor.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTKeyEncryptor newInstance() {
            return (CTKeyEncryptor)getTypeLoader().newInstance(CTKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptor newInstance(final XmlOptions xmlOptions) {
            return (CTKeyEncryptor)getTypeLoader().newInstance(CTKeyEncryptor.type, xmlOptions);
        }
        
        public static CTKeyEncryptor parse(final String s) throws XmlException {
            return (CTKeyEncryptor)getTypeLoader().parse(s, CTKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptor parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTKeyEncryptor)getTypeLoader().parse(s, CTKeyEncryptor.type, xmlOptions);
        }
        
        public static CTKeyEncryptor parse(final File file) throws XmlException, IOException {
            return (CTKeyEncryptor)getTypeLoader().parse(file, CTKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptor parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTKeyEncryptor)getTypeLoader().parse(file, CTKeyEncryptor.type, xmlOptions);
        }
        
        public static CTKeyEncryptor parse(final URL url) throws XmlException, IOException {
            return (CTKeyEncryptor)getTypeLoader().parse(url, CTKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptor parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTKeyEncryptor)getTypeLoader().parse(url, CTKeyEncryptor.type, xmlOptions);
        }
        
        public static CTKeyEncryptor parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTKeyEncryptor)getTypeLoader().parse(inputStream, CTKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptor parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTKeyEncryptor)getTypeLoader().parse(inputStream, CTKeyEncryptor.type, xmlOptions);
        }
        
        public static CTKeyEncryptor parse(final Reader reader) throws XmlException, IOException {
            return (CTKeyEncryptor)getTypeLoader().parse(reader, CTKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptor parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTKeyEncryptor)getTypeLoader().parse(reader, CTKeyEncryptor.type, xmlOptions);
        }
        
        public static CTKeyEncryptor parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTKeyEncryptor)getTypeLoader().parse(xmlStreamReader, CTKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptor parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTKeyEncryptor)getTypeLoader().parse(xmlStreamReader, CTKeyEncryptor.type, xmlOptions);
        }
        
        public static CTKeyEncryptor parse(final Node node) throws XmlException {
            return (CTKeyEncryptor)getTypeLoader().parse(node, CTKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTKeyEncryptor parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTKeyEncryptor)getTypeLoader().parse(node, CTKeyEncryptor.type, xmlOptions);
        }
        
        @Deprecated
        public static CTKeyEncryptor parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTKeyEncryptor)getTypeLoader().parse(xmlInputStream, CTKeyEncryptor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTKeyEncryptor parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTKeyEncryptor)getTypeLoader().parse(xmlInputStream, CTKeyEncryptor.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTKeyEncryptor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTKeyEncryptor.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public interface Uri extends XmlToken
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Uri.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("uribad9attrtype");
        public static final Enum HTTP_SCHEMAS_MICROSOFT_COM_OFFICE_2006_KEY_ENCRYPTOR_PASSWORD = Enum.forString("http://schemas.microsoft.com/office/2006/keyEncryptor/password");
        public static final Enum HTTP_SCHEMAS_MICROSOFT_COM_OFFICE_2006_KEY_ENCRYPTOR_CERTIFICATE = Enum.forString("http://schemas.microsoft.com/office/2006/keyEncryptor/certificate");
        public static final int INT_HTTP_SCHEMAS_MICROSOFT_COM_OFFICE_2006_KEY_ENCRYPTOR_PASSWORD = 1;
        public static final int INT_HTTP_SCHEMAS_MICROSOFT_COM_OFFICE_2006_KEY_ENCRYPTOR_CERTIFICATE = 2;
        
        StringEnumAbstractBase enumValue();
        
        void set(final StringEnumAbstractBase p0);
        
        public static final class Factory
        {
            private static SoftReference<SchemaTypeLoader> typeLoader;
            
            public static Uri newValue(final Object o) {
                return (Uri)Uri.type.newValue(o);
            }
            
            private static synchronized SchemaTypeLoader getTypeLoader() {
                SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
                if (typeLoaderForClassLoader == null) {
                    typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(Uri.class.getClassLoader());
                    Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
                }
                return typeLoaderForClassLoader;
            }
            
            public static Uri newInstance() {
                return (Uri)getTypeLoader().newInstance(Uri.type, (XmlOptions)null);
            }
            
            public static Uri newInstance(final XmlOptions xmlOptions) {
                return (Uri)getTypeLoader().newInstance(Uri.type, xmlOptions);
            }
            
            private Factory() {
            }
        }
        
        public static final class Enum extends StringEnumAbstractBase
        {
            static final int INT_HTTP_SCHEMAS_MICROSOFT_COM_OFFICE_2006_KEY_ENCRYPTOR_PASSWORD = 1;
            static final int INT_HTTP_SCHEMAS_MICROSOFT_COM_OFFICE_2006_KEY_ENCRYPTOR_CERTIFICATE = 2;
            public static final StringEnumAbstractBase.Table table;
            private static final long serialVersionUID = 1L;
            
            public static Enum forString(final String s) {
                return (Enum)Enum.table.forString(s);
            }
            
            public static Enum forInt(final int n) {
                return (Enum)Enum.table.forInt(n);
            }
            
            private Enum(final String s, final int n) {
                super(s, n);
            }
            
            private Object readResolve() {
                return forInt(this.intValue());
            }
            
            static {
                table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("http://schemas.microsoft.com/office/2006/keyEncryptor/password", 1), new Enum("http://schemas.microsoft.com/office/2006/keyEncryptor/certificate", 2) });
            }
        }
    }
}
