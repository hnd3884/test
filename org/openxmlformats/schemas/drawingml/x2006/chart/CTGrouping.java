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

public interface CTGrouping extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGrouping.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgroupingdcd9type");
    
    STGrouping.Enum getVal();
    
    STGrouping xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STGrouping.Enum p0);
    
    void xsetVal(final STGrouping p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGrouping.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGrouping newInstance() {
            return (CTGrouping)getTypeLoader().newInstance(CTGrouping.type, (XmlOptions)null);
        }
        
        public static CTGrouping newInstance(final XmlOptions xmlOptions) {
            return (CTGrouping)getTypeLoader().newInstance(CTGrouping.type, xmlOptions);
        }
        
        public static CTGrouping parse(final String s) throws XmlException {
            return (CTGrouping)getTypeLoader().parse(s, CTGrouping.type, (XmlOptions)null);
        }
        
        public static CTGrouping parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGrouping)getTypeLoader().parse(s, CTGrouping.type, xmlOptions);
        }
        
        public static CTGrouping parse(final File file) throws XmlException, IOException {
            return (CTGrouping)getTypeLoader().parse(file, CTGrouping.type, (XmlOptions)null);
        }
        
        public static CTGrouping parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGrouping)getTypeLoader().parse(file, CTGrouping.type, xmlOptions);
        }
        
        public static CTGrouping parse(final URL url) throws XmlException, IOException {
            return (CTGrouping)getTypeLoader().parse(url, CTGrouping.type, (XmlOptions)null);
        }
        
        public static CTGrouping parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGrouping)getTypeLoader().parse(url, CTGrouping.type, xmlOptions);
        }
        
        public static CTGrouping parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGrouping)getTypeLoader().parse(inputStream, CTGrouping.type, (XmlOptions)null);
        }
        
        public static CTGrouping parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGrouping)getTypeLoader().parse(inputStream, CTGrouping.type, xmlOptions);
        }
        
        public static CTGrouping parse(final Reader reader) throws XmlException, IOException {
            return (CTGrouping)getTypeLoader().parse(reader, CTGrouping.type, (XmlOptions)null);
        }
        
        public static CTGrouping parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGrouping)getTypeLoader().parse(reader, CTGrouping.type, xmlOptions);
        }
        
        public static CTGrouping parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGrouping)getTypeLoader().parse(xmlStreamReader, CTGrouping.type, (XmlOptions)null);
        }
        
        public static CTGrouping parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGrouping)getTypeLoader().parse(xmlStreamReader, CTGrouping.type, xmlOptions);
        }
        
        public static CTGrouping parse(final Node node) throws XmlException {
            return (CTGrouping)getTypeLoader().parse(node, CTGrouping.type, (XmlOptions)null);
        }
        
        public static CTGrouping parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGrouping)getTypeLoader().parse(node, CTGrouping.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGrouping parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGrouping)getTypeLoader().parse(xmlInputStream, CTGrouping.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGrouping parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGrouping)getTypeLoader().parse(xmlInputStream, CTGrouping.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGrouping.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGrouping.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
