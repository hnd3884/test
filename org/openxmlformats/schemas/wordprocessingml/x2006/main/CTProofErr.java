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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTProofErr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTProofErr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctprooferr1e07type");
    
    STProofErr.Enum getType();
    
    STProofErr xgetType();
    
    void setType(final STProofErr.Enum p0);
    
    void xsetType(final STProofErr p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTProofErr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTProofErr newInstance() {
            return (CTProofErr)getTypeLoader().newInstance(CTProofErr.type, (XmlOptions)null);
        }
        
        public static CTProofErr newInstance(final XmlOptions xmlOptions) {
            return (CTProofErr)getTypeLoader().newInstance(CTProofErr.type, xmlOptions);
        }
        
        public static CTProofErr parse(final String s) throws XmlException {
            return (CTProofErr)getTypeLoader().parse(s, CTProofErr.type, (XmlOptions)null);
        }
        
        public static CTProofErr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTProofErr)getTypeLoader().parse(s, CTProofErr.type, xmlOptions);
        }
        
        public static CTProofErr parse(final File file) throws XmlException, IOException {
            return (CTProofErr)getTypeLoader().parse(file, CTProofErr.type, (XmlOptions)null);
        }
        
        public static CTProofErr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTProofErr)getTypeLoader().parse(file, CTProofErr.type, xmlOptions);
        }
        
        public static CTProofErr parse(final URL url) throws XmlException, IOException {
            return (CTProofErr)getTypeLoader().parse(url, CTProofErr.type, (XmlOptions)null);
        }
        
        public static CTProofErr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTProofErr)getTypeLoader().parse(url, CTProofErr.type, xmlOptions);
        }
        
        public static CTProofErr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTProofErr)getTypeLoader().parse(inputStream, CTProofErr.type, (XmlOptions)null);
        }
        
        public static CTProofErr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTProofErr)getTypeLoader().parse(inputStream, CTProofErr.type, xmlOptions);
        }
        
        public static CTProofErr parse(final Reader reader) throws XmlException, IOException {
            return (CTProofErr)getTypeLoader().parse(reader, CTProofErr.type, (XmlOptions)null);
        }
        
        public static CTProofErr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTProofErr)getTypeLoader().parse(reader, CTProofErr.type, xmlOptions);
        }
        
        public static CTProofErr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTProofErr)getTypeLoader().parse(xmlStreamReader, CTProofErr.type, (XmlOptions)null);
        }
        
        public static CTProofErr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTProofErr)getTypeLoader().parse(xmlStreamReader, CTProofErr.type, xmlOptions);
        }
        
        public static CTProofErr parse(final Node node) throws XmlException {
            return (CTProofErr)getTypeLoader().parse(node, CTProofErr.type, (XmlOptions)null);
        }
        
        public static CTProofErr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTProofErr)getTypeLoader().parse(node, CTProofErr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTProofErr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTProofErr)getTypeLoader().parse(xmlInputStream, CTProofErr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTProofErr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTProofErr)getTypeLoader().parse(xmlInputStream, CTProofErr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTProofErr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTProofErr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
