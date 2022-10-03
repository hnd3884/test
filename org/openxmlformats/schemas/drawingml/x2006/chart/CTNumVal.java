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

public interface CTNumVal extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNumVal.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnumval2fe1type");
    
    String getV();
    
    STXstring xgetV();
    
    void setV(final String p0);
    
    void xsetV(final STXstring p0);
    
    long getIdx();
    
    XmlUnsignedInt xgetIdx();
    
    void setIdx(final long p0);
    
    void xsetIdx(final XmlUnsignedInt p0);
    
    String getFormatCode();
    
    STXstring xgetFormatCode();
    
    boolean isSetFormatCode();
    
    void setFormatCode(final String p0);
    
    void xsetFormatCode(final STXstring p0);
    
    void unsetFormatCode();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNumVal.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNumVal newInstance() {
            return (CTNumVal)getTypeLoader().newInstance(CTNumVal.type, (XmlOptions)null);
        }
        
        public static CTNumVal newInstance(final XmlOptions xmlOptions) {
            return (CTNumVal)getTypeLoader().newInstance(CTNumVal.type, xmlOptions);
        }
        
        public static CTNumVal parse(final String s) throws XmlException {
            return (CTNumVal)getTypeLoader().parse(s, CTNumVal.type, (XmlOptions)null);
        }
        
        public static CTNumVal parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumVal)getTypeLoader().parse(s, CTNumVal.type, xmlOptions);
        }
        
        public static CTNumVal parse(final File file) throws XmlException, IOException {
            return (CTNumVal)getTypeLoader().parse(file, CTNumVal.type, (XmlOptions)null);
        }
        
        public static CTNumVal parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumVal)getTypeLoader().parse(file, CTNumVal.type, xmlOptions);
        }
        
        public static CTNumVal parse(final URL url) throws XmlException, IOException {
            return (CTNumVal)getTypeLoader().parse(url, CTNumVal.type, (XmlOptions)null);
        }
        
        public static CTNumVal parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumVal)getTypeLoader().parse(url, CTNumVal.type, xmlOptions);
        }
        
        public static CTNumVal parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNumVal)getTypeLoader().parse(inputStream, CTNumVal.type, (XmlOptions)null);
        }
        
        public static CTNumVal parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumVal)getTypeLoader().parse(inputStream, CTNumVal.type, xmlOptions);
        }
        
        public static CTNumVal parse(final Reader reader) throws XmlException, IOException {
            return (CTNumVal)getTypeLoader().parse(reader, CTNumVal.type, (XmlOptions)null);
        }
        
        public static CTNumVal parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumVal)getTypeLoader().parse(reader, CTNumVal.type, xmlOptions);
        }
        
        public static CTNumVal parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNumVal)getTypeLoader().parse(xmlStreamReader, CTNumVal.type, (XmlOptions)null);
        }
        
        public static CTNumVal parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumVal)getTypeLoader().parse(xmlStreamReader, CTNumVal.type, xmlOptions);
        }
        
        public static CTNumVal parse(final Node node) throws XmlException {
            return (CTNumVal)getTypeLoader().parse(node, CTNumVal.type, (XmlOptions)null);
        }
        
        public static CTNumVal parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumVal)getTypeLoader().parse(node, CTNumVal.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNumVal parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNumVal)getTypeLoader().parse(xmlInputStream, CTNumVal.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNumVal parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNumVal)getTypeLoader().parse(xmlInputStream, CTNumVal.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumVal.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumVal.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
