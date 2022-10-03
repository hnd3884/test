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

public interface CTHeight extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTHeight.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctheighta2e1type");
    
    BigInteger getVal();
    
    STTwipsMeasure xgetVal();
    
    boolean isSetVal();
    
    void setVal(final BigInteger p0);
    
    void xsetVal(final STTwipsMeasure p0);
    
    void unsetVal();
    
    STHeightRule.Enum getHRule();
    
    STHeightRule xgetHRule();
    
    boolean isSetHRule();
    
    void setHRule(final STHeightRule.Enum p0);
    
    void xsetHRule(final STHeightRule p0);
    
    void unsetHRule();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTHeight.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTHeight newInstance() {
            return (CTHeight)getTypeLoader().newInstance(CTHeight.type, (XmlOptions)null);
        }
        
        public static CTHeight newInstance(final XmlOptions xmlOptions) {
            return (CTHeight)getTypeLoader().newInstance(CTHeight.type, xmlOptions);
        }
        
        public static CTHeight parse(final String s) throws XmlException {
            return (CTHeight)getTypeLoader().parse(s, CTHeight.type, (XmlOptions)null);
        }
        
        public static CTHeight parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTHeight)getTypeLoader().parse(s, CTHeight.type, xmlOptions);
        }
        
        public static CTHeight parse(final File file) throws XmlException, IOException {
            return (CTHeight)getTypeLoader().parse(file, CTHeight.type, (XmlOptions)null);
        }
        
        public static CTHeight parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHeight)getTypeLoader().parse(file, CTHeight.type, xmlOptions);
        }
        
        public static CTHeight parse(final URL url) throws XmlException, IOException {
            return (CTHeight)getTypeLoader().parse(url, CTHeight.type, (XmlOptions)null);
        }
        
        public static CTHeight parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHeight)getTypeLoader().parse(url, CTHeight.type, xmlOptions);
        }
        
        public static CTHeight parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTHeight)getTypeLoader().parse(inputStream, CTHeight.type, (XmlOptions)null);
        }
        
        public static CTHeight parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHeight)getTypeLoader().parse(inputStream, CTHeight.type, xmlOptions);
        }
        
        public static CTHeight parse(final Reader reader) throws XmlException, IOException {
            return (CTHeight)getTypeLoader().parse(reader, CTHeight.type, (XmlOptions)null);
        }
        
        public static CTHeight parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHeight)getTypeLoader().parse(reader, CTHeight.type, xmlOptions);
        }
        
        public static CTHeight parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTHeight)getTypeLoader().parse(xmlStreamReader, CTHeight.type, (XmlOptions)null);
        }
        
        public static CTHeight parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTHeight)getTypeLoader().parse(xmlStreamReader, CTHeight.type, xmlOptions);
        }
        
        public static CTHeight parse(final Node node) throws XmlException {
            return (CTHeight)getTypeLoader().parse(node, CTHeight.type, (XmlOptions)null);
        }
        
        public static CTHeight parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTHeight)getTypeLoader().parse(node, CTHeight.type, xmlOptions);
        }
        
        @Deprecated
        public static CTHeight parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTHeight)getTypeLoader().parse(xmlInputStream, CTHeight.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTHeight parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTHeight)getTypeLoader().parse(xmlInputStream, CTHeight.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHeight.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHeight.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
