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

public interface CTOverlap extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTOverlap.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctoverlapfd3ftype");
    
    byte getVal();
    
    STOverlap xgetVal();
    
    boolean isSetVal();
    
    void setVal(final byte p0);
    
    void xsetVal(final STOverlap p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTOverlap.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTOverlap newInstance() {
            return (CTOverlap)getTypeLoader().newInstance(CTOverlap.type, (XmlOptions)null);
        }
        
        public static CTOverlap newInstance(final XmlOptions xmlOptions) {
            return (CTOverlap)getTypeLoader().newInstance(CTOverlap.type, xmlOptions);
        }
        
        public static CTOverlap parse(final String s) throws XmlException {
            return (CTOverlap)getTypeLoader().parse(s, CTOverlap.type, (XmlOptions)null);
        }
        
        public static CTOverlap parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTOverlap)getTypeLoader().parse(s, CTOverlap.type, xmlOptions);
        }
        
        public static CTOverlap parse(final File file) throws XmlException, IOException {
            return (CTOverlap)getTypeLoader().parse(file, CTOverlap.type, (XmlOptions)null);
        }
        
        public static CTOverlap parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOverlap)getTypeLoader().parse(file, CTOverlap.type, xmlOptions);
        }
        
        public static CTOverlap parse(final URL url) throws XmlException, IOException {
            return (CTOverlap)getTypeLoader().parse(url, CTOverlap.type, (XmlOptions)null);
        }
        
        public static CTOverlap parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOverlap)getTypeLoader().parse(url, CTOverlap.type, xmlOptions);
        }
        
        public static CTOverlap parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTOverlap)getTypeLoader().parse(inputStream, CTOverlap.type, (XmlOptions)null);
        }
        
        public static CTOverlap parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOverlap)getTypeLoader().parse(inputStream, CTOverlap.type, xmlOptions);
        }
        
        public static CTOverlap parse(final Reader reader) throws XmlException, IOException {
            return (CTOverlap)getTypeLoader().parse(reader, CTOverlap.type, (XmlOptions)null);
        }
        
        public static CTOverlap parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOverlap)getTypeLoader().parse(reader, CTOverlap.type, xmlOptions);
        }
        
        public static CTOverlap parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTOverlap)getTypeLoader().parse(xmlStreamReader, CTOverlap.type, (XmlOptions)null);
        }
        
        public static CTOverlap parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTOverlap)getTypeLoader().parse(xmlStreamReader, CTOverlap.type, xmlOptions);
        }
        
        public static CTOverlap parse(final Node node) throws XmlException {
            return (CTOverlap)getTypeLoader().parse(node, CTOverlap.type, (XmlOptions)null);
        }
        
        public static CTOverlap parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTOverlap)getTypeLoader().parse(node, CTOverlap.type, xmlOptions);
        }
        
        @Deprecated
        public static CTOverlap parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTOverlap)getTypeLoader().parse(xmlInputStream, CTOverlap.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTOverlap parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTOverlap)getTypeLoader().parse(xmlInputStream, CTOverlap.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOverlap.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOverlap.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
