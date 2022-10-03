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

public interface CTOrientation extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTOrientation.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctorientationcb16type");
    
    STOrientation.Enum getVal();
    
    STOrientation xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STOrientation.Enum p0);
    
    void xsetVal(final STOrientation p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTOrientation.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTOrientation newInstance() {
            return (CTOrientation)getTypeLoader().newInstance(CTOrientation.type, (XmlOptions)null);
        }
        
        public static CTOrientation newInstance(final XmlOptions xmlOptions) {
            return (CTOrientation)getTypeLoader().newInstance(CTOrientation.type, xmlOptions);
        }
        
        public static CTOrientation parse(final String s) throws XmlException {
            return (CTOrientation)getTypeLoader().parse(s, CTOrientation.type, (XmlOptions)null);
        }
        
        public static CTOrientation parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTOrientation)getTypeLoader().parse(s, CTOrientation.type, xmlOptions);
        }
        
        public static CTOrientation parse(final File file) throws XmlException, IOException {
            return (CTOrientation)getTypeLoader().parse(file, CTOrientation.type, (XmlOptions)null);
        }
        
        public static CTOrientation parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOrientation)getTypeLoader().parse(file, CTOrientation.type, xmlOptions);
        }
        
        public static CTOrientation parse(final URL url) throws XmlException, IOException {
            return (CTOrientation)getTypeLoader().parse(url, CTOrientation.type, (XmlOptions)null);
        }
        
        public static CTOrientation parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOrientation)getTypeLoader().parse(url, CTOrientation.type, xmlOptions);
        }
        
        public static CTOrientation parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTOrientation)getTypeLoader().parse(inputStream, CTOrientation.type, (XmlOptions)null);
        }
        
        public static CTOrientation parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOrientation)getTypeLoader().parse(inputStream, CTOrientation.type, xmlOptions);
        }
        
        public static CTOrientation parse(final Reader reader) throws XmlException, IOException {
            return (CTOrientation)getTypeLoader().parse(reader, CTOrientation.type, (XmlOptions)null);
        }
        
        public static CTOrientation parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOrientation)getTypeLoader().parse(reader, CTOrientation.type, xmlOptions);
        }
        
        public static CTOrientation parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTOrientation)getTypeLoader().parse(xmlStreamReader, CTOrientation.type, (XmlOptions)null);
        }
        
        public static CTOrientation parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTOrientation)getTypeLoader().parse(xmlStreamReader, CTOrientation.type, xmlOptions);
        }
        
        public static CTOrientation parse(final Node node) throws XmlException {
            return (CTOrientation)getTypeLoader().parse(node, CTOrientation.type, (XmlOptions)null);
        }
        
        public static CTOrientation parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTOrientation)getTypeLoader().parse(node, CTOrientation.type, xmlOptions);
        }
        
        @Deprecated
        public static CTOrientation parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTOrientation)getTypeLoader().parse(xmlInputStream, CTOrientation.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTOrientation parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTOrientation)getTypeLoader().parse(xmlInputStream, CTOrientation.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOrientation.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOrientation.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
