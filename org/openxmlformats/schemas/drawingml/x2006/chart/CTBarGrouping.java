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

public interface CTBarGrouping extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBarGrouping.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbargrouping8bf0type");
    
    STBarGrouping.Enum getVal();
    
    STBarGrouping xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STBarGrouping.Enum p0);
    
    void xsetVal(final STBarGrouping p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBarGrouping.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBarGrouping newInstance() {
            return (CTBarGrouping)getTypeLoader().newInstance(CTBarGrouping.type, (XmlOptions)null);
        }
        
        public static CTBarGrouping newInstance(final XmlOptions xmlOptions) {
            return (CTBarGrouping)getTypeLoader().newInstance(CTBarGrouping.type, xmlOptions);
        }
        
        public static CTBarGrouping parse(final String s) throws XmlException {
            return (CTBarGrouping)getTypeLoader().parse(s, CTBarGrouping.type, (XmlOptions)null);
        }
        
        public static CTBarGrouping parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBarGrouping)getTypeLoader().parse(s, CTBarGrouping.type, xmlOptions);
        }
        
        public static CTBarGrouping parse(final File file) throws XmlException, IOException {
            return (CTBarGrouping)getTypeLoader().parse(file, CTBarGrouping.type, (XmlOptions)null);
        }
        
        public static CTBarGrouping parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBarGrouping)getTypeLoader().parse(file, CTBarGrouping.type, xmlOptions);
        }
        
        public static CTBarGrouping parse(final URL url) throws XmlException, IOException {
            return (CTBarGrouping)getTypeLoader().parse(url, CTBarGrouping.type, (XmlOptions)null);
        }
        
        public static CTBarGrouping parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBarGrouping)getTypeLoader().parse(url, CTBarGrouping.type, xmlOptions);
        }
        
        public static CTBarGrouping parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBarGrouping)getTypeLoader().parse(inputStream, CTBarGrouping.type, (XmlOptions)null);
        }
        
        public static CTBarGrouping parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBarGrouping)getTypeLoader().parse(inputStream, CTBarGrouping.type, xmlOptions);
        }
        
        public static CTBarGrouping parse(final Reader reader) throws XmlException, IOException {
            return (CTBarGrouping)getTypeLoader().parse(reader, CTBarGrouping.type, (XmlOptions)null);
        }
        
        public static CTBarGrouping parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBarGrouping)getTypeLoader().parse(reader, CTBarGrouping.type, xmlOptions);
        }
        
        public static CTBarGrouping parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBarGrouping)getTypeLoader().parse(xmlStreamReader, CTBarGrouping.type, (XmlOptions)null);
        }
        
        public static CTBarGrouping parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBarGrouping)getTypeLoader().parse(xmlStreamReader, CTBarGrouping.type, xmlOptions);
        }
        
        public static CTBarGrouping parse(final Node node) throws XmlException {
            return (CTBarGrouping)getTypeLoader().parse(node, CTBarGrouping.type, (XmlOptions)null);
        }
        
        public static CTBarGrouping parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBarGrouping)getTypeLoader().parse(node, CTBarGrouping.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBarGrouping parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBarGrouping)getTypeLoader().parse(xmlInputStream, CTBarGrouping.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBarGrouping parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBarGrouping)getTypeLoader().parse(xmlInputStream, CTBarGrouping.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBarGrouping.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBarGrouping.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
