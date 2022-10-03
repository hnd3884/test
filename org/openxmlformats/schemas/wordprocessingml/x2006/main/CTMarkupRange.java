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

public interface CTMarkupRange extends CTMarkup
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTMarkupRange.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctmarkuprangeba3dtype");
    
    STDisplacedByCustomXml.Enum getDisplacedByCustomXml();
    
    STDisplacedByCustomXml xgetDisplacedByCustomXml();
    
    boolean isSetDisplacedByCustomXml();
    
    void setDisplacedByCustomXml(final STDisplacedByCustomXml.Enum p0);
    
    void xsetDisplacedByCustomXml(final STDisplacedByCustomXml p0);
    
    void unsetDisplacedByCustomXml();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTMarkupRange.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTMarkupRange newInstance() {
            return (CTMarkupRange)getTypeLoader().newInstance(CTMarkupRange.type, (XmlOptions)null);
        }
        
        public static CTMarkupRange newInstance(final XmlOptions xmlOptions) {
            return (CTMarkupRange)getTypeLoader().newInstance(CTMarkupRange.type, xmlOptions);
        }
        
        public static CTMarkupRange parse(final String s) throws XmlException {
            return (CTMarkupRange)getTypeLoader().parse(s, CTMarkupRange.type, (XmlOptions)null);
        }
        
        public static CTMarkupRange parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTMarkupRange)getTypeLoader().parse(s, CTMarkupRange.type, xmlOptions);
        }
        
        public static CTMarkupRange parse(final File file) throws XmlException, IOException {
            return (CTMarkupRange)getTypeLoader().parse(file, CTMarkupRange.type, (XmlOptions)null);
        }
        
        public static CTMarkupRange parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkupRange)getTypeLoader().parse(file, CTMarkupRange.type, xmlOptions);
        }
        
        public static CTMarkupRange parse(final URL url) throws XmlException, IOException {
            return (CTMarkupRange)getTypeLoader().parse(url, CTMarkupRange.type, (XmlOptions)null);
        }
        
        public static CTMarkupRange parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkupRange)getTypeLoader().parse(url, CTMarkupRange.type, xmlOptions);
        }
        
        public static CTMarkupRange parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTMarkupRange)getTypeLoader().parse(inputStream, CTMarkupRange.type, (XmlOptions)null);
        }
        
        public static CTMarkupRange parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkupRange)getTypeLoader().parse(inputStream, CTMarkupRange.type, xmlOptions);
        }
        
        public static CTMarkupRange parse(final Reader reader) throws XmlException, IOException {
            return (CTMarkupRange)getTypeLoader().parse(reader, CTMarkupRange.type, (XmlOptions)null);
        }
        
        public static CTMarkupRange parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkupRange)getTypeLoader().parse(reader, CTMarkupRange.type, xmlOptions);
        }
        
        public static CTMarkupRange parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTMarkupRange)getTypeLoader().parse(xmlStreamReader, CTMarkupRange.type, (XmlOptions)null);
        }
        
        public static CTMarkupRange parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTMarkupRange)getTypeLoader().parse(xmlStreamReader, CTMarkupRange.type, xmlOptions);
        }
        
        public static CTMarkupRange parse(final Node node) throws XmlException {
            return (CTMarkupRange)getTypeLoader().parse(node, CTMarkupRange.type, (XmlOptions)null);
        }
        
        public static CTMarkupRange parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTMarkupRange)getTypeLoader().parse(node, CTMarkupRange.type, xmlOptions);
        }
        
        @Deprecated
        public static CTMarkupRange parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTMarkupRange)getTypeLoader().parse(xmlInputStream, CTMarkupRange.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTMarkupRange parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTMarkupRange)getTypeLoader().parse(xmlInputStream, CTMarkupRange.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMarkupRange.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMarkupRange.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
