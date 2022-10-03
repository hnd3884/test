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
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTDouble extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDouble.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdoublec10btype");
    
    double getVal();
    
    XmlDouble xgetVal();
    
    void setVal(final double p0);
    
    void xsetVal(final XmlDouble p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDouble.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDouble newInstance() {
            return (CTDouble)getTypeLoader().newInstance(CTDouble.type, (XmlOptions)null);
        }
        
        public static CTDouble newInstance(final XmlOptions xmlOptions) {
            return (CTDouble)getTypeLoader().newInstance(CTDouble.type, xmlOptions);
        }
        
        public static CTDouble parse(final String s) throws XmlException {
            return (CTDouble)getTypeLoader().parse(s, CTDouble.type, (XmlOptions)null);
        }
        
        public static CTDouble parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDouble)getTypeLoader().parse(s, CTDouble.type, xmlOptions);
        }
        
        public static CTDouble parse(final File file) throws XmlException, IOException {
            return (CTDouble)getTypeLoader().parse(file, CTDouble.type, (XmlOptions)null);
        }
        
        public static CTDouble parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDouble)getTypeLoader().parse(file, CTDouble.type, xmlOptions);
        }
        
        public static CTDouble parse(final URL url) throws XmlException, IOException {
            return (CTDouble)getTypeLoader().parse(url, CTDouble.type, (XmlOptions)null);
        }
        
        public static CTDouble parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDouble)getTypeLoader().parse(url, CTDouble.type, xmlOptions);
        }
        
        public static CTDouble parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDouble)getTypeLoader().parse(inputStream, CTDouble.type, (XmlOptions)null);
        }
        
        public static CTDouble parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDouble)getTypeLoader().parse(inputStream, CTDouble.type, xmlOptions);
        }
        
        public static CTDouble parse(final Reader reader) throws XmlException, IOException {
            return (CTDouble)getTypeLoader().parse(reader, CTDouble.type, (XmlOptions)null);
        }
        
        public static CTDouble parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDouble)getTypeLoader().parse(reader, CTDouble.type, xmlOptions);
        }
        
        public static CTDouble parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDouble)getTypeLoader().parse(xmlStreamReader, CTDouble.type, (XmlOptions)null);
        }
        
        public static CTDouble parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDouble)getTypeLoader().parse(xmlStreamReader, CTDouble.type, xmlOptions);
        }
        
        public static CTDouble parse(final Node node) throws XmlException {
            return (CTDouble)getTypeLoader().parse(node, CTDouble.type, (XmlOptions)null);
        }
        
        public static CTDouble parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDouble)getTypeLoader().parse(node, CTDouble.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDouble parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDouble)getTypeLoader().parse(xmlInputStream, CTDouble.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDouble parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDouble)getTypeLoader().parse(xmlInputStream, CTDouble.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDouble.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDouble.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
