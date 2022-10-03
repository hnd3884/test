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

public interface CTHpsMeasure extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTHpsMeasure.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cthpsmeasure3795type");
    
    BigInteger getVal();
    
    STHpsMeasure xgetVal();
    
    void setVal(final BigInteger p0);
    
    void xsetVal(final STHpsMeasure p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTHpsMeasure.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTHpsMeasure newInstance() {
            return (CTHpsMeasure)getTypeLoader().newInstance(CTHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTHpsMeasure newInstance(final XmlOptions xmlOptions) {
            return (CTHpsMeasure)getTypeLoader().newInstance(CTHpsMeasure.type, xmlOptions);
        }
        
        public static CTHpsMeasure parse(final String s) throws XmlException {
            return (CTHpsMeasure)getTypeLoader().parse(s, CTHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTHpsMeasure parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTHpsMeasure)getTypeLoader().parse(s, CTHpsMeasure.type, xmlOptions);
        }
        
        public static CTHpsMeasure parse(final File file) throws XmlException, IOException {
            return (CTHpsMeasure)getTypeLoader().parse(file, CTHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTHpsMeasure parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHpsMeasure)getTypeLoader().parse(file, CTHpsMeasure.type, xmlOptions);
        }
        
        public static CTHpsMeasure parse(final URL url) throws XmlException, IOException {
            return (CTHpsMeasure)getTypeLoader().parse(url, CTHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTHpsMeasure parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHpsMeasure)getTypeLoader().parse(url, CTHpsMeasure.type, xmlOptions);
        }
        
        public static CTHpsMeasure parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTHpsMeasure)getTypeLoader().parse(inputStream, CTHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTHpsMeasure parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHpsMeasure)getTypeLoader().parse(inputStream, CTHpsMeasure.type, xmlOptions);
        }
        
        public static CTHpsMeasure parse(final Reader reader) throws XmlException, IOException {
            return (CTHpsMeasure)getTypeLoader().parse(reader, CTHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTHpsMeasure parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHpsMeasure)getTypeLoader().parse(reader, CTHpsMeasure.type, xmlOptions);
        }
        
        public static CTHpsMeasure parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTHpsMeasure)getTypeLoader().parse(xmlStreamReader, CTHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTHpsMeasure parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTHpsMeasure)getTypeLoader().parse(xmlStreamReader, CTHpsMeasure.type, xmlOptions);
        }
        
        public static CTHpsMeasure parse(final Node node) throws XmlException {
            return (CTHpsMeasure)getTypeLoader().parse(node, CTHpsMeasure.type, (XmlOptions)null);
        }
        
        public static CTHpsMeasure parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTHpsMeasure)getTypeLoader().parse(node, CTHpsMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static CTHpsMeasure parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTHpsMeasure)getTypeLoader().parse(xmlInputStream, CTHpsMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTHpsMeasure parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTHpsMeasure)getTypeLoader().parse(xmlInputStream, CTHpsMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHpsMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHpsMeasure.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
