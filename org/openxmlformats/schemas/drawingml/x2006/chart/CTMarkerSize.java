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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTMarkerSize extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTMarkerSize.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctmarkersized8c1type");
    
    short getVal();
    
    STMarkerSize xgetVal();
    
    boolean isSetVal();
    
    void setVal(final short p0);
    
    void xsetVal(final STMarkerSize p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTMarkerSize.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTMarkerSize newInstance() {
            return (CTMarkerSize)getTypeLoader().newInstance(CTMarkerSize.type, (XmlOptions)null);
        }
        
        public static CTMarkerSize newInstance(final XmlOptions xmlOptions) {
            return (CTMarkerSize)getTypeLoader().newInstance(CTMarkerSize.type, xmlOptions);
        }
        
        public static CTMarkerSize parse(final String s) throws XmlException {
            return (CTMarkerSize)getTypeLoader().parse(s, CTMarkerSize.type, (XmlOptions)null);
        }
        
        public static CTMarkerSize parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTMarkerSize)getTypeLoader().parse(s, CTMarkerSize.type, xmlOptions);
        }
        
        public static CTMarkerSize parse(final File file) throws XmlException, IOException {
            return (CTMarkerSize)getTypeLoader().parse(file, CTMarkerSize.type, (XmlOptions)null);
        }
        
        public static CTMarkerSize parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkerSize)getTypeLoader().parse(file, CTMarkerSize.type, xmlOptions);
        }
        
        public static CTMarkerSize parse(final URL url) throws XmlException, IOException {
            return (CTMarkerSize)getTypeLoader().parse(url, CTMarkerSize.type, (XmlOptions)null);
        }
        
        public static CTMarkerSize parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkerSize)getTypeLoader().parse(url, CTMarkerSize.type, xmlOptions);
        }
        
        public static CTMarkerSize parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTMarkerSize)getTypeLoader().parse(inputStream, CTMarkerSize.type, (XmlOptions)null);
        }
        
        public static CTMarkerSize parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkerSize)getTypeLoader().parse(inputStream, CTMarkerSize.type, xmlOptions);
        }
        
        public static CTMarkerSize parse(final Reader reader) throws XmlException, IOException {
            return (CTMarkerSize)getTypeLoader().parse(reader, CTMarkerSize.type, (XmlOptions)null);
        }
        
        public static CTMarkerSize parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkerSize)getTypeLoader().parse(reader, CTMarkerSize.type, xmlOptions);
        }
        
        public static CTMarkerSize parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTMarkerSize)getTypeLoader().parse(xmlStreamReader, CTMarkerSize.type, (XmlOptions)null);
        }
        
        public static CTMarkerSize parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTMarkerSize)getTypeLoader().parse(xmlStreamReader, CTMarkerSize.type, xmlOptions);
        }
        
        public static CTMarkerSize parse(final Node node) throws XmlException {
            return (CTMarkerSize)getTypeLoader().parse(node, CTMarkerSize.type, (XmlOptions)null);
        }
        
        public static CTMarkerSize parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTMarkerSize)getTypeLoader().parse(node, CTMarkerSize.type, xmlOptions);
        }
        
        @Deprecated
        public static CTMarkerSize parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTMarkerSize)getTypeLoader().parse(xmlInputStream, CTMarkerSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTMarkerSize parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTMarkerSize)getTypeLoader().parse(xmlInputStream, CTMarkerSize.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMarkerSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMarkerSize.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
