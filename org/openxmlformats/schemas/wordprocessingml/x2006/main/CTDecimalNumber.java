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

public interface CTDecimalNumber extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDecimalNumber.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdecimalnumbera518type");
    
    BigInteger getVal();
    
    STDecimalNumber xgetVal();
    
    void setVal(final BigInteger p0);
    
    void xsetVal(final STDecimalNumber p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDecimalNumber.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDecimalNumber newInstance() {
            return (CTDecimalNumber)getTypeLoader().newInstance(CTDecimalNumber.type, (XmlOptions)null);
        }
        
        public static CTDecimalNumber newInstance(final XmlOptions xmlOptions) {
            return (CTDecimalNumber)getTypeLoader().newInstance(CTDecimalNumber.type, xmlOptions);
        }
        
        public static CTDecimalNumber parse(final String s) throws XmlException {
            return (CTDecimalNumber)getTypeLoader().parse(s, CTDecimalNumber.type, (XmlOptions)null);
        }
        
        public static CTDecimalNumber parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDecimalNumber)getTypeLoader().parse(s, CTDecimalNumber.type, xmlOptions);
        }
        
        public static CTDecimalNumber parse(final File file) throws XmlException, IOException {
            return (CTDecimalNumber)getTypeLoader().parse(file, CTDecimalNumber.type, (XmlOptions)null);
        }
        
        public static CTDecimalNumber parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDecimalNumber)getTypeLoader().parse(file, CTDecimalNumber.type, xmlOptions);
        }
        
        public static CTDecimalNumber parse(final URL url) throws XmlException, IOException {
            return (CTDecimalNumber)getTypeLoader().parse(url, CTDecimalNumber.type, (XmlOptions)null);
        }
        
        public static CTDecimalNumber parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDecimalNumber)getTypeLoader().parse(url, CTDecimalNumber.type, xmlOptions);
        }
        
        public static CTDecimalNumber parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDecimalNumber)getTypeLoader().parse(inputStream, CTDecimalNumber.type, (XmlOptions)null);
        }
        
        public static CTDecimalNumber parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDecimalNumber)getTypeLoader().parse(inputStream, CTDecimalNumber.type, xmlOptions);
        }
        
        public static CTDecimalNumber parse(final Reader reader) throws XmlException, IOException {
            return (CTDecimalNumber)getTypeLoader().parse(reader, CTDecimalNumber.type, (XmlOptions)null);
        }
        
        public static CTDecimalNumber parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDecimalNumber)getTypeLoader().parse(reader, CTDecimalNumber.type, xmlOptions);
        }
        
        public static CTDecimalNumber parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDecimalNumber)getTypeLoader().parse(xmlStreamReader, CTDecimalNumber.type, (XmlOptions)null);
        }
        
        public static CTDecimalNumber parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDecimalNumber)getTypeLoader().parse(xmlStreamReader, CTDecimalNumber.type, xmlOptions);
        }
        
        public static CTDecimalNumber parse(final Node node) throws XmlException {
            return (CTDecimalNumber)getTypeLoader().parse(node, CTDecimalNumber.type, (XmlOptions)null);
        }
        
        public static CTDecimalNumber parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDecimalNumber)getTypeLoader().parse(node, CTDecimalNumber.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDecimalNumber parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDecimalNumber)getTypeLoader().parse(xmlInputStream, CTDecimalNumber.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDecimalNumber parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDecimalNumber)getTypeLoader().parse(xmlInputStream, CTDecimalNumber.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDecimalNumber.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDecimalNumber.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
