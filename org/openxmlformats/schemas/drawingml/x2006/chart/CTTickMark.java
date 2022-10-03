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

public interface CTTickMark extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTickMark.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttickmarke7f2type");
    
    STTickMark.Enum getVal();
    
    STTickMark xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STTickMark.Enum p0);
    
    void xsetVal(final STTickMark p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTickMark.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTickMark newInstance() {
            return (CTTickMark)getTypeLoader().newInstance(CTTickMark.type, (XmlOptions)null);
        }
        
        public static CTTickMark newInstance(final XmlOptions xmlOptions) {
            return (CTTickMark)getTypeLoader().newInstance(CTTickMark.type, xmlOptions);
        }
        
        public static CTTickMark parse(final String s) throws XmlException {
            return (CTTickMark)getTypeLoader().parse(s, CTTickMark.type, (XmlOptions)null);
        }
        
        public static CTTickMark parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTickMark)getTypeLoader().parse(s, CTTickMark.type, xmlOptions);
        }
        
        public static CTTickMark parse(final File file) throws XmlException, IOException {
            return (CTTickMark)getTypeLoader().parse(file, CTTickMark.type, (XmlOptions)null);
        }
        
        public static CTTickMark parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTickMark)getTypeLoader().parse(file, CTTickMark.type, xmlOptions);
        }
        
        public static CTTickMark parse(final URL url) throws XmlException, IOException {
            return (CTTickMark)getTypeLoader().parse(url, CTTickMark.type, (XmlOptions)null);
        }
        
        public static CTTickMark parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTickMark)getTypeLoader().parse(url, CTTickMark.type, xmlOptions);
        }
        
        public static CTTickMark parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTickMark)getTypeLoader().parse(inputStream, CTTickMark.type, (XmlOptions)null);
        }
        
        public static CTTickMark parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTickMark)getTypeLoader().parse(inputStream, CTTickMark.type, xmlOptions);
        }
        
        public static CTTickMark parse(final Reader reader) throws XmlException, IOException {
            return (CTTickMark)getTypeLoader().parse(reader, CTTickMark.type, (XmlOptions)null);
        }
        
        public static CTTickMark parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTickMark)getTypeLoader().parse(reader, CTTickMark.type, xmlOptions);
        }
        
        public static CTTickMark parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTickMark)getTypeLoader().parse(xmlStreamReader, CTTickMark.type, (XmlOptions)null);
        }
        
        public static CTTickMark parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTickMark)getTypeLoader().parse(xmlStreamReader, CTTickMark.type, xmlOptions);
        }
        
        public static CTTickMark parse(final Node node) throws XmlException {
            return (CTTickMark)getTypeLoader().parse(node, CTTickMark.type, (XmlOptions)null);
        }
        
        public static CTTickMark parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTickMark)getTypeLoader().parse(node, CTTickMark.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTickMark parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTickMark)getTypeLoader().parse(xmlInputStream, CTTickMark.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTickMark parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTickMark)getTypeLoader().parse(xmlInputStream, CTTickMark.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTickMark.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTickMark.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
