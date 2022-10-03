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

public interface CTUnsignedInt extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTUnsignedInt.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctunsignedinte8ectype");
    
    long getVal();
    
    XmlUnsignedInt xgetVal();
    
    void setVal(final long p0);
    
    void xsetVal(final XmlUnsignedInt p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTUnsignedInt.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTUnsignedInt newInstance() {
            return (CTUnsignedInt)getTypeLoader().newInstance(CTUnsignedInt.type, (XmlOptions)null);
        }
        
        public static CTUnsignedInt newInstance(final XmlOptions xmlOptions) {
            return (CTUnsignedInt)getTypeLoader().newInstance(CTUnsignedInt.type, xmlOptions);
        }
        
        public static CTUnsignedInt parse(final String s) throws XmlException {
            return (CTUnsignedInt)getTypeLoader().parse(s, CTUnsignedInt.type, (XmlOptions)null);
        }
        
        public static CTUnsignedInt parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTUnsignedInt)getTypeLoader().parse(s, CTUnsignedInt.type, xmlOptions);
        }
        
        public static CTUnsignedInt parse(final File file) throws XmlException, IOException {
            return (CTUnsignedInt)getTypeLoader().parse(file, CTUnsignedInt.type, (XmlOptions)null);
        }
        
        public static CTUnsignedInt parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTUnsignedInt)getTypeLoader().parse(file, CTUnsignedInt.type, xmlOptions);
        }
        
        public static CTUnsignedInt parse(final URL url) throws XmlException, IOException {
            return (CTUnsignedInt)getTypeLoader().parse(url, CTUnsignedInt.type, (XmlOptions)null);
        }
        
        public static CTUnsignedInt parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTUnsignedInt)getTypeLoader().parse(url, CTUnsignedInt.type, xmlOptions);
        }
        
        public static CTUnsignedInt parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTUnsignedInt)getTypeLoader().parse(inputStream, CTUnsignedInt.type, (XmlOptions)null);
        }
        
        public static CTUnsignedInt parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTUnsignedInt)getTypeLoader().parse(inputStream, CTUnsignedInt.type, xmlOptions);
        }
        
        public static CTUnsignedInt parse(final Reader reader) throws XmlException, IOException {
            return (CTUnsignedInt)getTypeLoader().parse(reader, CTUnsignedInt.type, (XmlOptions)null);
        }
        
        public static CTUnsignedInt parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTUnsignedInt)getTypeLoader().parse(reader, CTUnsignedInt.type, xmlOptions);
        }
        
        public static CTUnsignedInt parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTUnsignedInt)getTypeLoader().parse(xmlStreamReader, CTUnsignedInt.type, (XmlOptions)null);
        }
        
        public static CTUnsignedInt parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTUnsignedInt)getTypeLoader().parse(xmlStreamReader, CTUnsignedInt.type, xmlOptions);
        }
        
        public static CTUnsignedInt parse(final Node node) throws XmlException {
            return (CTUnsignedInt)getTypeLoader().parse(node, CTUnsignedInt.type, (XmlOptions)null);
        }
        
        public static CTUnsignedInt parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTUnsignedInt)getTypeLoader().parse(node, CTUnsignedInt.type, xmlOptions);
        }
        
        @Deprecated
        public static CTUnsignedInt parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTUnsignedInt)getTypeLoader().parse(xmlInputStream, CTUnsignedInt.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTUnsignedInt parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTUnsignedInt)getTypeLoader().parse(xmlInputStream, CTUnsignedInt.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTUnsignedInt.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTUnsignedInt.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
