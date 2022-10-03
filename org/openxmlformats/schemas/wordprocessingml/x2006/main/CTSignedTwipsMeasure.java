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

public interface CTSignedTwipsMeasure extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSignedTwipsMeasure.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsignedtwipsmeasure1037type");
    
    BigInteger getVal();
    
    STSignedTwipsMeasure xgetVal();
    
    void setVal(final BigInteger p0);
    
    void xsetVal(final STSignedTwipsMeasure p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSignedTwipsMeasure.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSignedTwipsMeasure newInstance() {
            return (CTSignedTwipsMeasure)getTypeLoader().newInstance(CTSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedTwipsMeasure newInstance(final XmlOptions xmlOptions) {
            return (CTSignedTwipsMeasure)getTypeLoader().newInstance(CTSignedTwipsMeasure.type, xmlOptions);
        }
        
        public static CTSignedTwipsMeasure parse(final String s) throws XmlException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(s, CTSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedTwipsMeasure parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(s, CTSignedTwipsMeasure.type, xmlOptions);
        }
        
        public static CTSignedTwipsMeasure parse(final File file) throws XmlException, IOException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(file, CTSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedTwipsMeasure parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(file, CTSignedTwipsMeasure.type, xmlOptions);
        }
        
        public static CTSignedTwipsMeasure parse(final URL url) throws XmlException, IOException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(url, CTSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedTwipsMeasure parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(url, CTSignedTwipsMeasure.type, xmlOptions);
        }
        
        public static CTSignedTwipsMeasure parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(inputStream, CTSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedTwipsMeasure parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(inputStream, CTSignedTwipsMeasure.type, xmlOptions);
        }
        
        public static CTSignedTwipsMeasure parse(final Reader reader) throws XmlException, IOException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(reader, CTSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedTwipsMeasure parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(reader, CTSignedTwipsMeasure.type, xmlOptions);
        }
        
        public static CTSignedTwipsMeasure parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(xmlStreamReader, CTSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedTwipsMeasure parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(xmlStreamReader, CTSignedTwipsMeasure.type, xmlOptions);
        }
        
        public static CTSignedTwipsMeasure parse(final Node node) throws XmlException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(node, CTSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static CTSignedTwipsMeasure parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(node, CTSignedTwipsMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSignedTwipsMeasure parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(xmlInputStream, CTSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSignedTwipsMeasure parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSignedTwipsMeasure)getTypeLoader().parse(xmlInputStream, CTSignedTwipsMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSignedTwipsMeasure.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
