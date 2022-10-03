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

public interface CTSignedHpsMeasure extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSignedHpsMeasure.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsignedhpsmeasure3099type");
    
    BigInteger getVal();
    
    STSignedHpsMeasure xgetVal();
    
    void setVal(final BigInteger p0);
    
    void xsetVal(final STSignedHpsMeasure p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSignedHpsMeasure.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSignedHpsMeasure newInstance() {
            return (CTSignedHpsMeasure)getTypeLoader().newInstance(CTSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedHpsMeasure newInstance(final XmlOptions xmlOptions) {
            return (CTSignedHpsMeasure)getTypeLoader().newInstance(CTSignedHpsMeasure.type, xmlOptions);
        }
        
        public static CTSignedHpsMeasure parse(final String s) throws XmlException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(s, CTSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedHpsMeasure parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(s, CTSignedHpsMeasure.type, xmlOptions);
        }
        
        public static CTSignedHpsMeasure parse(final File file) throws XmlException, IOException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(file, CTSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedHpsMeasure parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(file, CTSignedHpsMeasure.type, xmlOptions);
        }
        
        public static CTSignedHpsMeasure parse(final URL url) throws XmlException, IOException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(url, CTSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedHpsMeasure parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(url, CTSignedHpsMeasure.type, xmlOptions);
        }
        
        public static CTSignedHpsMeasure parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(inputStream, CTSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedHpsMeasure parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(inputStream, CTSignedHpsMeasure.type, xmlOptions);
        }
        
        public static CTSignedHpsMeasure parse(final Reader reader) throws XmlException, IOException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(reader, CTSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedHpsMeasure parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(reader, CTSignedHpsMeasure.type, xmlOptions);
        }
        
        public static CTSignedHpsMeasure parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(xmlStreamReader, CTSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedHpsMeasure parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(xmlStreamReader, CTSignedHpsMeasure.type, xmlOptions);
        }
        
        public static CTSignedHpsMeasure parse(final Node node) throws XmlException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(node, CTSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedHpsMeasure parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(node, CTSignedHpsMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSignedHpsMeasure parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(xmlInputStream, CTSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSignedHpsMeasure parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSignedHpsMeasure)getTypeLoader().parse(xmlInputStream, CTSignedHpsMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSignedHpsMeasure.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
