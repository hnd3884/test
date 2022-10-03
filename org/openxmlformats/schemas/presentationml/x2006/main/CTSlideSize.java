package org.openxmlformats.schemas.presentationml.x2006.main;

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

public interface CTSlideSize extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSlideSize.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctslidesizeb0fdtype");
    
    int getCx();
    
    STSlideSizeCoordinate xgetCx();
    
    void setCx(final int p0);
    
    void xsetCx(final STSlideSizeCoordinate p0);
    
    int getCy();
    
    STSlideSizeCoordinate xgetCy();
    
    void setCy(final int p0);
    
    void xsetCy(final STSlideSizeCoordinate p0);
    
    STSlideSizeType.Enum getType();
    
    STSlideSizeType xgetType();
    
    boolean isSetType();
    
    void setType(final STSlideSizeType.Enum p0);
    
    void xsetType(final STSlideSizeType p0);
    
    void unsetType();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSlideSize.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSlideSize newInstance() {
            return (CTSlideSize)getTypeLoader().newInstance(CTSlideSize.type, (XmlOptions)null);
        }
        
        public static CTSlideSize newInstance(final XmlOptions xmlOptions) {
            return (CTSlideSize)getTypeLoader().newInstance(CTSlideSize.type, xmlOptions);
        }
        
        public static CTSlideSize parse(final String s) throws XmlException {
            return (CTSlideSize)getTypeLoader().parse(s, CTSlideSize.type, (XmlOptions)null);
        }
        
        public static CTSlideSize parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideSize)getTypeLoader().parse(s, CTSlideSize.type, xmlOptions);
        }
        
        public static CTSlideSize parse(final File file) throws XmlException, IOException {
            return (CTSlideSize)getTypeLoader().parse(file, CTSlideSize.type, (XmlOptions)null);
        }
        
        public static CTSlideSize parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideSize)getTypeLoader().parse(file, CTSlideSize.type, xmlOptions);
        }
        
        public static CTSlideSize parse(final URL url) throws XmlException, IOException {
            return (CTSlideSize)getTypeLoader().parse(url, CTSlideSize.type, (XmlOptions)null);
        }
        
        public static CTSlideSize parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideSize)getTypeLoader().parse(url, CTSlideSize.type, xmlOptions);
        }
        
        public static CTSlideSize parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSlideSize)getTypeLoader().parse(inputStream, CTSlideSize.type, (XmlOptions)null);
        }
        
        public static CTSlideSize parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideSize)getTypeLoader().parse(inputStream, CTSlideSize.type, xmlOptions);
        }
        
        public static CTSlideSize parse(final Reader reader) throws XmlException, IOException {
            return (CTSlideSize)getTypeLoader().parse(reader, CTSlideSize.type, (XmlOptions)null);
        }
        
        public static CTSlideSize parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideSize)getTypeLoader().parse(reader, CTSlideSize.type, xmlOptions);
        }
        
        public static CTSlideSize parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSlideSize)getTypeLoader().parse(xmlStreamReader, CTSlideSize.type, (XmlOptions)null);
        }
        
        public static CTSlideSize parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideSize)getTypeLoader().parse(xmlStreamReader, CTSlideSize.type, xmlOptions);
        }
        
        public static CTSlideSize parse(final Node node) throws XmlException {
            return (CTSlideSize)getTypeLoader().parse(node, CTSlideSize.type, (XmlOptions)null);
        }
        
        public static CTSlideSize parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideSize)getTypeLoader().parse(node, CTSlideSize.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSlideSize parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSlideSize)getTypeLoader().parse(xmlInputStream, CTSlideSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSlideSize parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSlideSize)getTypeLoader().parse(xmlInputStream, CTSlideSize.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideSize.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
