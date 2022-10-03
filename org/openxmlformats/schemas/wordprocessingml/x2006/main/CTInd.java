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
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTInd extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTInd.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctind4b93type");
    
    BigInteger getLeft();
    
    STSignedTwipsMeasure xgetLeft();
    
    boolean isSetLeft();
    
    void setLeft(final BigInteger p0);
    
    void xsetLeft(final STSignedTwipsMeasure p0);
    
    void unsetLeft();
    
    BigInteger getLeftChars();
    
    STDecimalNumber xgetLeftChars();
    
    boolean isSetLeftChars();
    
    void setLeftChars(final BigInteger p0);
    
    void xsetLeftChars(final STDecimalNumber p0);
    
    void unsetLeftChars();
    
    BigInteger getRight();
    
    STSignedTwipsMeasure xgetRight();
    
    boolean isSetRight();
    
    void setRight(final BigInteger p0);
    
    void xsetRight(final STSignedTwipsMeasure p0);
    
    void unsetRight();
    
    BigInteger getRightChars();
    
    STDecimalNumber xgetRightChars();
    
    boolean isSetRightChars();
    
    void setRightChars(final BigInteger p0);
    
    void xsetRightChars(final STDecimalNumber p0);
    
    void unsetRightChars();
    
    BigInteger getHanging();
    
    STTwipsMeasure xgetHanging();
    
    boolean isSetHanging();
    
    void setHanging(final BigInteger p0);
    
    void xsetHanging(final STTwipsMeasure p0);
    
    void unsetHanging();
    
    BigInteger getHangingChars();
    
    STDecimalNumber xgetHangingChars();
    
    boolean isSetHangingChars();
    
    void setHangingChars(final BigInteger p0);
    
    void xsetHangingChars(final STDecimalNumber p0);
    
    void unsetHangingChars();
    
    BigInteger getFirstLine();
    
    STTwipsMeasure xgetFirstLine();
    
    boolean isSetFirstLine();
    
    void setFirstLine(final BigInteger p0);
    
    void xsetFirstLine(final STTwipsMeasure p0);
    
    void unsetFirstLine();
    
    BigInteger getFirstLineChars();
    
    STDecimalNumber xgetFirstLineChars();
    
    boolean isSetFirstLineChars();
    
    void setFirstLineChars(final BigInteger p0);
    
    void xsetFirstLineChars(final STDecimalNumber p0);
    
    void unsetFirstLineChars();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTInd.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTInd newInstance() {
            return (CTInd)getTypeLoader().newInstance(CTInd.type, (XmlOptions)null);
        }
        
        public static CTInd newInstance(final XmlOptions xmlOptions) {
            return (CTInd)getTypeLoader().newInstance(CTInd.type, xmlOptions);
        }
        
        public static CTInd parse(final String s) throws XmlException {
            return (CTInd)getTypeLoader().parse(s, CTInd.type, (XmlOptions)null);
        }
        
        public static CTInd parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTInd)getTypeLoader().parse(s, CTInd.type, xmlOptions);
        }
        
        public static CTInd parse(final File file) throws XmlException, IOException {
            return (CTInd)getTypeLoader().parse(file, CTInd.type, (XmlOptions)null);
        }
        
        public static CTInd parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTInd)getTypeLoader().parse(file, CTInd.type, xmlOptions);
        }
        
        public static CTInd parse(final URL url) throws XmlException, IOException {
            return (CTInd)getTypeLoader().parse(url, CTInd.type, (XmlOptions)null);
        }
        
        public static CTInd parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTInd)getTypeLoader().parse(url, CTInd.type, xmlOptions);
        }
        
        public static CTInd parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTInd)getTypeLoader().parse(inputStream, CTInd.type, (XmlOptions)null);
        }
        
        public static CTInd parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTInd)getTypeLoader().parse(inputStream, CTInd.type, xmlOptions);
        }
        
        public static CTInd parse(final Reader reader) throws XmlException, IOException {
            return (CTInd)getTypeLoader().parse(reader, CTInd.type, (XmlOptions)null);
        }
        
        public static CTInd parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTInd)getTypeLoader().parse(reader, CTInd.type, xmlOptions);
        }
        
        public static CTInd parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTInd)getTypeLoader().parse(xmlStreamReader, CTInd.type, (XmlOptions)null);
        }
        
        public static CTInd parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTInd)getTypeLoader().parse(xmlStreamReader, CTInd.type, xmlOptions);
        }
        
        public static CTInd parse(final Node node) throws XmlException {
            return (CTInd)getTypeLoader().parse(node, CTInd.type, (XmlOptions)null);
        }
        
        public static CTInd parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTInd)getTypeLoader().parse(node, CTInd.type, xmlOptions);
        }
        
        @Deprecated
        public static CTInd parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTInd)getTypeLoader().parse(xmlInputStream, CTInd.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTInd parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTInd)getTypeLoader().parse(xmlInputStream, CTInd.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTInd.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTInd.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
