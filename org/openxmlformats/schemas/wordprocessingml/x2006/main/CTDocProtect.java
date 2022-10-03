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
import org.apache.xmlbeans.XmlBase64Binary;
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTDocProtect extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDocProtect.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdocprotectc611type");
    
    STDocProtect.Enum getEdit();
    
    STDocProtect xgetEdit();
    
    boolean isSetEdit();
    
    void setEdit(final STDocProtect.Enum p0);
    
    void xsetEdit(final STDocProtect p0);
    
    void unsetEdit();
    
    STOnOff.Enum getFormatting();
    
    STOnOff xgetFormatting();
    
    boolean isSetFormatting();
    
    void setFormatting(final STOnOff.Enum p0);
    
    void xsetFormatting(final STOnOff p0);
    
    void unsetFormatting();
    
    STOnOff.Enum getEnforcement();
    
    STOnOff xgetEnforcement();
    
    boolean isSetEnforcement();
    
    void setEnforcement(final STOnOff.Enum p0);
    
    void xsetEnforcement(final STOnOff p0);
    
    void unsetEnforcement();
    
    STCryptProv.Enum getCryptProviderType();
    
    STCryptProv xgetCryptProviderType();
    
    boolean isSetCryptProviderType();
    
    void setCryptProviderType(final STCryptProv.Enum p0);
    
    void xsetCryptProviderType(final STCryptProv p0);
    
    void unsetCryptProviderType();
    
    STAlgClass.Enum getCryptAlgorithmClass();
    
    STAlgClass xgetCryptAlgorithmClass();
    
    boolean isSetCryptAlgorithmClass();
    
    void setCryptAlgorithmClass(final STAlgClass.Enum p0);
    
    void xsetCryptAlgorithmClass(final STAlgClass p0);
    
    void unsetCryptAlgorithmClass();
    
    STAlgType.Enum getCryptAlgorithmType();
    
    STAlgType xgetCryptAlgorithmType();
    
    boolean isSetCryptAlgorithmType();
    
    void setCryptAlgorithmType(final STAlgType.Enum p0);
    
    void xsetCryptAlgorithmType(final STAlgType p0);
    
    void unsetCryptAlgorithmType();
    
    BigInteger getCryptAlgorithmSid();
    
    STDecimalNumber xgetCryptAlgorithmSid();
    
    boolean isSetCryptAlgorithmSid();
    
    void setCryptAlgorithmSid(final BigInteger p0);
    
    void xsetCryptAlgorithmSid(final STDecimalNumber p0);
    
    void unsetCryptAlgorithmSid();
    
    BigInteger getCryptSpinCount();
    
    STDecimalNumber xgetCryptSpinCount();
    
    boolean isSetCryptSpinCount();
    
    void setCryptSpinCount(final BigInteger p0);
    
    void xsetCryptSpinCount(final STDecimalNumber p0);
    
    void unsetCryptSpinCount();
    
    String getCryptProvider();
    
    STString xgetCryptProvider();
    
    boolean isSetCryptProvider();
    
    void setCryptProvider(final String p0);
    
    void xsetCryptProvider(final STString p0);
    
    void unsetCryptProvider();
    
    byte[] getAlgIdExt();
    
    STLongHexNumber xgetAlgIdExt();
    
    boolean isSetAlgIdExt();
    
    void setAlgIdExt(final byte[] p0);
    
    void xsetAlgIdExt(final STLongHexNumber p0);
    
    void unsetAlgIdExt();
    
    String getAlgIdExtSource();
    
    STString xgetAlgIdExtSource();
    
    boolean isSetAlgIdExtSource();
    
    void setAlgIdExtSource(final String p0);
    
    void xsetAlgIdExtSource(final STString p0);
    
    void unsetAlgIdExtSource();
    
    byte[] getCryptProviderTypeExt();
    
    STLongHexNumber xgetCryptProviderTypeExt();
    
    boolean isSetCryptProviderTypeExt();
    
    void setCryptProviderTypeExt(final byte[] p0);
    
    void xsetCryptProviderTypeExt(final STLongHexNumber p0);
    
    void unsetCryptProviderTypeExt();
    
    String getCryptProviderTypeExtSource();
    
    STString xgetCryptProviderTypeExtSource();
    
    boolean isSetCryptProviderTypeExtSource();
    
    void setCryptProviderTypeExtSource(final String p0);
    
    void xsetCryptProviderTypeExtSource(final STString p0);
    
    void unsetCryptProviderTypeExtSource();
    
    byte[] getHash();
    
    XmlBase64Binary xgetHash();
    
    boolean isSetHash();
    
    void setHash(final byte[] p0);
    
    void xsetHash(final XmlBase64Binary p0);
    
    void unsetHash();
    
    byte[] getSalt();
    
    XmlBase64Binary xgetSalt();
    
    boolean isSetSalt();
    
    void setSalt(final byte[] p0);
    
    void xsetSalt(final XmlBase64Binary p0);
    
    void unsetSalt();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDocProtect.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDocProtect newInstance() {
            return (CTDocProtect)getTypeLoader().newInstance(CTDocProtect.type, (XmlOptions)null);
        }
        
        public static CTDocProtect newInstance(final XmlOptions xmlOptions) {
            return (CTDocProtect)getTypeLoader().newInstance(CTDocProtect.type, xmlOptions);
        }
        
        public static CTDocProtect parse(final String s) throws XmlException {
            return (CTDocProtect)getTypeLoader().parse(s, CTDocProtect.type, (XmlOptions)null);
        }
        
        public static CTDocProtect parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDocProtect)getTypeLoader().parse(s, CTDocProtect.type, xmlOptions);
        }
        
        public static CTDocProtect parse(final File file) throws XmlException, IOException {
            return (CTDocProtect)getTypeLoader().parse(file, CTDocProtect.type, (XmlOptions)null);
        }
        
        public static CTDocProtect parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocProtect)getTypeLoader().parse(file, CTDocProtect.type, xmlOptions);
        }
        
        public static CTDocProtect parse(final URL url) throws XmlException, IOException {
            return (CTDocProtect)getTypeLoader().parse(url, CTDocProtect.type, (XmlOptions)null);
        }
        
        public static CTDocProtect parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocProtect)getTypeLoader().parse(url, CTDocProtect.type, xmlOptions);
        }
        
        public static CTDocProtect parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDocProtect)getTypeLoader().parse(inputStream, CTDocProtect.type, (XmlOptions)null);
        }
        
        public static CTDocProtect parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocProtect)getTypeLoader().parse(inputStream, CTDocProtect.type, xmlOptions);
        }
        
        public static CTDocProtect parse(final Reader reader) throws XmlException, IOException {
            return (CTDocProtect)getTypeLoader().parse(reader, CTDocProtect.type, (XmlOptions)null);
        }
        
        public static CTDocProtect parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocProtect)getTypeLoader().parse(reader, CTDocProtect.type, xmlOptions);
        }
        
        public static CTDocProtect parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDocProtect)getTypeLoader().parse(xmlStreamReader, CTDocProtect.type, (XmlOptions)null);
        }
        
        public static CTDocProtect parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDocProtect)getTypeLoader().parse(xmlStreamReader, CTDocProtect.type, xmlOptions);
        }
        
        public static CTDocProtect parse(final Node node) throws XmlException {
            return (CTDocProtect)getTypeLoader().parse(node, CTDocProtect.type, (XmlOptions)null);
        }
        
        public static CTDocProtect parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDocProtect)getTypeLoader().parse(node, CTDocProtect.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDocProtect parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDocProtect)getTypeLoader().parse(xmlInputStream, CTDocProtect.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDocProtect parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDocProtect)getTypeLoader().parse(xmlInputStream, CTDocProtect.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDocProtect.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDocProtect.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
