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

public interface CTRotX extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRotX.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctrotx3c3btype");
    
    byte getVal();
    
    STRotX xgetVal();
    
    boolean isSetVal();
    
    void setVal(final byte p0);
    
    void xsetVal(final STRotX p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRotX.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRotX newInstance() {
            return (CTRotX)getTypeLoader().newInstance(CTRotX.type, (XmlOptions)null);
        }
        
        public static CTRotX newInstance(final XmlOptions xmlOptions) {
            return (CTRotX)getTypeLoader().newInstance(CTRotX.type, xmlOptions);
        }
        
        public static CTRotX parse(final String s) throws XmlException {
            return (CTRotX)getTypeLoader().parse(s, CTRotX.type, (XmlOptions)null);
        }
        
        public static CTRotX parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRotX)getTypeLoader().parse(s, CTRotX.type, xmlOptions);
        }
        
        public static CTRotX parse(final File file) throws XmlException, IOException {
            return (CTRotX)getTypeLoader().parse(file, CTRotX.type, (XmlOptions)null);
        }
        
        public static CTRotX parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRotX)getTypeLoader().parse(file, CTRotX.type, xmlOptions);
        }
        
        public static CTRotX parse(final URL url) throws XmlException, IOException {
            return (CTRotX)getTypeLoader().parse(url, CTRotX.type, (XmlOptions)null);
        }
        
        public static CTRotX parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRotX)getTypeLoader().parse(url, CTRotX.type, xmlOptions);
        }
        
        public static CTRotX parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRotX)getTypeLoader().parse(inputStream, CTRotX.type, (XmlOptions)null);
        }
        
        public static CTRotX parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRotX)getTypeLoader().parse(inputStream, CTRotX.type, xmlOptions);
        }
        
        public static CTRotX parse(final Reader reader) throws XmlException, IOException {
            return (CTRotX)getTypeLoader().parse(reader, CTRotX.type, (XmlOptions)null);
        }
        
        public static CTRotX parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRotX)getTypeLoader().parse(reader, CTRotX.type, xmlOptions);
        }
        
        public static CTRotX parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRotX)getTypeLoader().parse(xmlStreamReader, CTRotX.type, (XmlOptions)null);
        }
        
        public static CTRotX parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRotX)getTypeLoader().parse(xmlStreamReader, CTRotX.type, xmlOptions);
        }
        
        public static CTRotX parse(final Node node) throws XmlException {
            return (CTRotX)getTypeLoader().parse(node, CTRotX.type, (XmlOptions)null);
        }
        
        public static CTRotX parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRotX)getTypeLoader().parse(node, CTRotX.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRotX parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRotX)getTypeLoader().parse(xmlInputStream, CTRotX.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRotX parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRotX)getTypeLoader().parse(xmlInputStream, CTRotX.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRotX.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRotX.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
