package org.openxmlformats.schemas.officeDocument.x2006.extendedProperties;

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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTDigSigBlob extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDigSigBlob.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdigsigblob73c9type");
    
    byte[] getBlob();
    
    XmlBase64Binary xgetBlob();
    
    void setBlob(final byte[] p0);
    
    void xsetBlob(final XmlBase64Binary p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDigSigBlob.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDigSigBlob newInstance() {
            return (CTDigSigBlob)getTypeLoader().newInstance(CTDigSigBlob.type, (XmlOptions)null);
        }
        
        public static CTDigSigBlob newInstance(final XmlOptions xmlOptions) {
            return (CTDigSigBlob)getTypeLoader().newInstance(CTDigSigBlob.type, xmlOptions);
        }
        
        public static CTDigSigBlob parse(final String s) throws XmlException {
            return (CTDigSigBlob)getTypeLoader().parse(s, CTDigSigBlob.type, (XmlOptions)null);
        }
        
        public static CTDigSigBlob parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDigSigBlob)getTypeLoader().parse(s, CTDigSigBlob.type, xmlOptions);
        }
        
        public static CTDigSigBlob parse(final File file) throws XmlException, IOException {
            return (CTDigSigBlob)getTypeLoader().parse(file, CTDigSigBlob.type, (XmlOptions)null);
        }
        
        public static CTDigSigBlob parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDigSigBlob)getTypeLoader().parse(file, CTDigSigBlob.type, xmlOptions);
        }
        
        public static CTDigSigBlob parse(final URL url) throws XmlException, IOException {
            return (CTDigSigBlob)getTypeLoader().parse(url, CTDigSigBlob.type, (XmlOptions)null);
        }
        
        public static CTDigSigBlob parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDigSigBlob)getTypeLoader().parse(url, CTDigSigBlob.type, xmlOptions);
        }
        
        public static CTDigSigBlob parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDigSigBlob)getTypeLoader().parse(inputStream, CTDigSigBlob.type, (XmlOptions)null);
        }
        
        public static CTDigSigBlob parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDigSigBlob)getTypeLoader().parse(inputStream, CTDigSigBlob.type, xmlOptions);
        }
        
        public static CTDigSigBlob parse(final Reader reader) throws XmlException, IOException {
            return (CTDigSigBlob)getTypeLoader().parse(reader, CTDigSigBlob.type, (XmlOptions)null);
        }
        
        public static CTDigSigBlob parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDigSigBlob)getTypeLoader().parse(reader, CTDigSigBlob.type, xmlOptions);
        }
        
        public static CTDigSigBlob parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDigSigBlob)getTypeLoader().parse(xmlStreamReader, CTDigSigBlob.type, (XmlOptions)null);
        }
        
        public static CTDigSigBlob parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDigSigBlob)getTypeLoader().parse(xmlStreamReader, CTDigSigBlob.type, xmlOptions);
        }
        
        public static CTDigSigBlob parse(final Node node) throws XmlException {
            return (CTDigSigBlob)getTypeLoader().parse(node, CTDigSigBlob.type, (XmlOptions)null);
        }
        
        public static CTDigSigBlob parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDigSigBlob)getTypeLoader().parse(node, CTDigSigBlob.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDigSigBlob parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDigSigBlob)getTypeLoader().parse(xmlInputStream, CTDigSigBlob.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDigSigBlob parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDigSigBlob)getTypeLoader().parse(xmlInputStream, CTDigSigBlob.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDigSigBlob.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDigSigBlob.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
