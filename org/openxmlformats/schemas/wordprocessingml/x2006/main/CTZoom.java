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

public interface CTZoom extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTZoom.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctzoomc275type");
    
    STZoom.Enum getVal();
    
    STZoom xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STZoom.Enum p0);
    
    void xsetVal(final STZoom p0);
    
    void unsetVal();
    
    BigInteger getPercent();
    
    STDecimalNumber xgetPercent();
    
    void setPercent(final BigInteger p0);
    
    void xsetPercent(final STDecimalNumber p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTZoom.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTZoom newInstance() {
            return (CTZoom)getTypeLoader().newInstance(CTZoom.type, (XmlOptions)null);
        }
        
        public static CTZoom newInstance(final XmlOptions xmlOptions) {
            return (CTZoom)getTypeLoader().newInstance(CTZoom.type, xmlOptions);
        }
        
        public static CTZoom parse(final String s) throws XmlException {
            return (CTZoom)getTypeLoader().parse(s, CTZoom.type, (XmlOptions)null);
        }
        
        public static CTZoom parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTZoom)getTypeLoader().parse(s, CTZoom.type, xmlOptions);
        }
        
        public static CTZoom parse(final File file) throws XmlException, IOException {
            return (CTZoom)getTypeLoader().parse(file, CTZoom.type, (XmlOptions)null);
        }
        
        public static CTZoom parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTZoom)getTypeLoader().parse(file, CTZoom.type, xmlOptions);
        }
        
        public static CTZoom parse(final URL url) throws XmlException, IOException {
            return (CTZoom)getTypeLoader().parse(url, CTZoom.type, (XmlOptions)null);
        }
        
        public static CTZoom parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTZoom)getTypeLoader().parse(url, CTZoom.type, xmlOptions);
        }
        
        public static CTZoom parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTZoom)getTypeLoader().parse(inputStream, CTZoom.type, (XmlOptions)null);
        }
        
        public static CTZoom parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTZoom)getTypeLoader().parse(inputStream, CTZoom.type, xmlOptions);
        }
        
        public static CTZoom parse(final Reader reader) throws XmlException, IOException {
            return (CTZoom)getTypeLoader().parse(reader, CTZoom.type, (XmlOptions)null);
        }
        
        public static CTZoom parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTZoom)getTypeLoader().parse(reader, CTZoom.type, xmlOptions);
        }
        
        public static CTZoom parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTZoom)getTypeLoader().parse(xmlStreamReader, CTZoom.type, (XmlOptions)null);
        }
        
        public static CTZoom parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTZoom)getTypeLoader().parse(xmlStreamReader, CTZoom.type, xmlOptions);
        }
        
        public static CTZoom parse(final Node node) throws XmlException {
            return (CTZoom)getTypeLoader().parse(node, CTZoom.type, (XmlOptions)null);
        }
        
        public static CTZoom parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTZoom)getTypeLoader().parse(node, CTZoom.type, xmlOptions);
        }
        
        @Deprecated
        public static CTZoom parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTZoom)getTypeLoader().parse(xmlInputStream, CTZoom.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTZoom parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTZoom)getTypeLoader().parse(xmlInputStream, CTZoom.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTZoom.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTZoom.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
