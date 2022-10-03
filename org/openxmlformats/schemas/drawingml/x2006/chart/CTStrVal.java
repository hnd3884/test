package org.openxmlformats.schemas.drawingml.x2006.chart;

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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTStrVal extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTStrVal.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctstrval86cctype");
    
    String getV();
    
    STXstring xgetV();
    
    void setV(final String p0);
    
    void xsetV(final STXstring p0);
    
    long getIdx();
    
    XmlUnsignedInt xgetIdx();
    
    void setIdx(final long p0);
    
    void xsetIdx(final XmlUnsignedInt p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTStrVal.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTStrVal newInstance() {
            return (CTStrVal)getTypeLoader().newInstance(CTStrVal.type, (XmlOptions)null);
        }
        
        public static CTStrVal newInstance(final XmlOptions xmlOptions) {
            return (CTStrVal)getTypeLoader().newInstance(CTStrVal.type, xmlOptions);
        }
        
        public static CTStrVal parse(final String s) throws XmlException {
            return (CTStrVal)getTypeLoader().parse(s, CTStrVal.type, (XmlOptions)null);
        }
        
        public static CTStrVal parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTStrVal)getTypeLoader().parse(s, CTStrVal.type, xmlOptions);
        }
        
        public static CTStrVal parse(final File file) throws XmlException, IOException {
            return (CTStrVal)getTypeLoader().parse(file, CTStrVal.type, (XmlOptions)null);
        }
        
        public static CTStrVal parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStrVal)getTypeLoader().parse(file, CTStrVal.type, xmlOptions);
        }
        
        public static CTStrVal parse(final URL url) throws XmlException, IOException {
            return (CTStrVal)getTypeLoader().parse(url, CTStrVal.type, (XmlOptions)null);
        }
        
        public static CTStrVal parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStrVal)getTypeLoader().parse(url, CTStrVal.type, xmlOptions);
        }
        
        public static CTStrVal parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTStrVal)getTypeLoader().parse(inputStream, CTStrVal.type, (XmlOptions)null);
        }
        
        public static CTStrVal parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStrVal)getTypeLoader().parse(inputStream, CTStrVal.type, xmlOptions);
        }
        
        public static CTStrVal parse(final Reader reader) throws XmlException, IOException {
            return (CTStrVal)getTypeLoader().parse(reader, CTStrVal.type, (XmlOptions)null);
        }
        
        public static CTStrVal parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStrVal)getTypeLoader().parse(reader, CTStrVal.type, xmlOptions);
        }
        
        public static CTStrVal parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTStrVal)getTypeLoader().parse(xmlStreamReader, CTStrVal.type, (XmlOptions)null);
        }
        
        public static CTStrVal parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTStrVal)getTypeLoader().parse(xmlStreamReader, CTStrVal.type, xmlOptions);
        }
        
        public static CTStrVal parse(final Node node) throws XmlException {
            return (CTStrVal)getTypeLoader().parse(node, CTStrVal.type, (XmlOptions)null);
        }
        
        public static CTStrVal parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTStrVal)getTypeLoader().parse(node, CTStrVal.type, xmlOptions);
        }
        
        @Deprecated
        public static CTStrVal parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTStrVal)getTypeLoader().parse(xmlInputStream, CTStrVal.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTStrVal parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTStrVal)getTypeLoader().parse(xmlInputStream, CTStrVal.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStrVal.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStrVal.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
