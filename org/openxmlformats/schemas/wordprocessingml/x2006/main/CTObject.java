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

public interface CTObject extends CTPictureBase
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTObject.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctobject47c9type");
    
    CTControl getControl();
    
    boolean isSetControl();
    
    void setControl(final CTControl p0);
    
    CTControl addNewControl();
    
    void unsetControl();
    
    BigInteger getDxaOrig();
    
    STTwipsMeasure xgetDxaOrig();
    
    boolean isSetDxaOrig();
    
    void setDxaOrig(final BigInteger p0);
    
    void xsetDxaOrig(final STTwipsMeasure p0);
    
    void unsetDxaOrig();
    
    BigInteger getDyaOrig();
    
    STTwipsMeasure xgetDyaOrig();
    
    boolean isSetDyaOrig();
    
    void setDyaOrig(final BigInteger p0);
    
    void xsetDyaOrig(final STTwipsMeasure p0);
    
    void unsetDyaOrig();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTObject.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTObject newInstance() {
            return (CTObject)getTypeLoader().newInstance(CTObject.type, (XmlOptions)null);
        }
        
        public static CTObject newInstance(final XmlOptions xmlOptions) {
            return (CTObject)getTypeLoader().newInstance(CTObject.type, xmlOptions);
        }
        
        public static CTObject parse(final String s) throws XmlException {
            return (CTObject)getTypeLoader().parse(s, CTObject.type, (XmlOptions)null);
        }
        
        public static CTObject parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTObject)getTypeLoader().parse(s, CTObject.type, xmlOptions);
        }
        
        public static CTObject parse(final File file) throws XmlException, IOException {
            return (CTObject)getTypeLoader().parse(file, CTObject.type, (XmlOptions)null);
        }
        
        public static CTObject parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTObject)getTypeLoader().parse(file, CTObject.type, xmlOptions);
        }
        
        public static CTObject parse(final URL url) throws XmlException, IOException {
            return (CTObject)getTypeLoader().parse(url, CTObject.type, (XmlOptions)null);
        }
        
        public static CTObject parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTObject)getTypeLoader().parse(url, CTObject.type, xmlOptions);
        }
        
        public static CTObject parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTObject)getTypeLoader().parse(inputStream, CTObject.type, (XmlOptions)null);
        }
        
        public static CTObject parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTObject)getTypeLoader().parse(inputStream, CTObject.type, xmlOptions);
        }
        
        public static CTObject parse(final Reader reader) throws XmlException, IOException {
            return (CTObject)getTypeLoader().parse(reader, CTObject.type, (XmlOptions)null);
        }
        
        public static CTObject parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTObject)getTypeLoader().parse(reader, CTObject.type, xmlOptions);
        }
        
        public static CTObject parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTObject)getTypeLoader().parse(xmlStreamReader, CTObject.type, (XmlOptions)null);
        }
        
        public static CTObject parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTObject)getTypeLoader().parse(xmlStreamReader, CTObject.type, xmlOptions);
        }
        
        public static CTObject parse(final Node node) throws XmlException {
            return (CTObject)getTypeLoader().parse(node, CTObject.type, (XmlOptions)null);
        }
        
        public static CTObject parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTObject)getTypeLoader().parse(node, CTObject.type, xmlOptions);
        }
        
        @Deprecated
        public static CTObject parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTObject)getTypeLoader().parse(xmlInputStream, CTObject.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTObject parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTObject)getTypeLoader().parse(xmlInputStream, CTObject.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTObject.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTObject.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
