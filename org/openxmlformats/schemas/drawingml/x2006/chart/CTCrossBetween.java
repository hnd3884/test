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

public interface CTCrossBetween extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCrossBetween.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcrossbetweeneb14type");
    
    STCrossBetween.Enum getVal();
    
    STCrossBetween xgetVal();
    
    void setVal(final STCrossBetween.Enum p0);
    
    void xsetVal(final STCrossBetween p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCrossBetween.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCrossBetween newInstance() {
            return (CTCrossBetween)getTypeLoader().newInstance(CTCrossBetween.type, (XmlOptions)null);
        }
        
        public static CTCrossBetween newInstance(final XmlOptions xmlOptions) {
            return (CTCrossBetween)getTypeLoader().newInstance(CTCrossBetween.type, xmlOptions);
        }
        
        public static CTCrossBetween parse(final String s) throws XmlException {
            return (CTCrossBetween)getTypeLoader().parse(s, CTCrossBetween.type, (XmlOptions)null);
        }
        
        public static CTCrossBetween parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCrossBetween)getTypeLoader().parse(s, CTCrossBetween.type, xmlOptions);
        }
        
        public static CTCrossBetween parse(final File file) throws XmlException, IOException {
            return (CTCrossBetween)getTypeLoader().parse(file, CTCrossBetween.type, (XmlOptions)null);
        }
        
        public static CTCrossBetween parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCrossBetween)getTypeLoader().parse(file, CTCrossBetween.type, xmlOptions);
        }
        
        public static CTCrossBetween parse(final URL url) throws XmlException, IOException {
            return (CTCrossBetween)getTypeLoader().parse(url, CTCrossBetween.type, (XmlOptions)null);
        }
        
        public static CTCrossBetween parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCrossBetween)getTypeLoader().parse(url, CTCrossBetween.type, xmlOptions);
        }
        
        public static CTCrossBetween parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCrossBetween)getTypeLoader().parse(inputStream, CTCrossBetween.type, (XmlOptions)null);
        }
        
        public static CTCrossBetween parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCrossBetween)getTypeLoader().parse(inputStream, CTCrossBetween.type, xmlOptions);
        }
        
        public static CTCrossBetween parse(final Reader reader) throws XmlException, IOException {
            return (CTCrossBetween)getTypeLoader().parse(reader, CTCrossBetween.type, (XmlOptions)null);
        }
        
        public static CTCrossBetween parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCrossBetween)getTypeLoader().parse(reader, CTCrossBetween.type, xmlOptions);
        }
        
        public static CTCrossBetween parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCrossBetween)getTypeLoader().parse(xmlStreamReader, CTCrossBetween.type, (XmlOptions)null);
        }
        
        public static CTCrossBetween parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCrossBetween)getTypeLoader().parse(xmlStreamReader, CTCrossBetween.type, xmlOptions);
        }
        
        public static CTCrossBetween parse(final Node node) throws XmlException {
            return (CTCrossBetween)getTypeLoader().parse(node, CTCrossBetween.type, (XmlOptions)null);
        }
        
        public static CTCrossBetween parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCrossBetween)getTypeLoader().parse(node, CTCrossBetween.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCrossBetween parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCrossBetween)getTypeLoader().parse(xmlInputStream, CTCrossBetween.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCrossBetween parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCrossBetween)getTypeLoader().parse(xmlInputStream, CTCrossBetween.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCrossBetween.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCrossBetween.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
