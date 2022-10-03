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

public interface CTShape extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTShape.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctshape89e5type");
    
    STShape.Enum getVal();
    
    STShape xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STShape.Enum p0);
    
    void xsetVal(final STShape p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTShape.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTShape newInstance() {
            return (CTShape)getTypeLoader().newInstance(CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape newInstance(final XmlOptions xmlOptions) {
            return (CTShape)getTypeLoader().newInstance(CTShape.type, xmlOptions);
        }
        
        public static CTShape parse(final String s) throws XmlException {
            return (CTShape)getTypeLoader().parse(s, CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTShape)getTypeLoader().parse(s, CTShape.type, xmlOptions);
        }
        
        public static CTShape parse(final File file) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(file, CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(file, CTShape.type, xmlOptions);
        }
        
        public static CTShape parse(final URL url) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(url, CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(url, CTShape.type, xmlOptions);
        }
        
        public static CTShape parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(inputStream, CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(inputStream, CTShape.type, xmlOptions);
        }
        
        public static CTShape parse(final Reader reader) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(reader, CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(reader, CTShape.type, xmlOptions);
        }
        
        public static CTShape parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTShape)getTypeLoader().parse(xmlStreamReader, CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTShape)getTypeLoader().parse(xmlStreamReader, CTShape.type, xmlOptions);
        }
        
        public static CTShape parse(final Node node) throws XmlException {
            return (CTShape)getTypeLoader().parse(node, CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTShape)getTypeLoader().parse(node, CTShape.type, xmlOptions);
        }
        
        @Deprecated
        public static CTShape parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTShape)getTypeLoader().parse(xmlInputStream, CTShape.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTShape parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTShape)getTypeLoader().parse(xmlInputStream, CTShape.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShape.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShape.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
