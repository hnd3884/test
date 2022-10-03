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

public interface CTBarDir extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBarDir.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbardir5f42type");
    
    STBarDir.Enum getVal();
    
    STBarDir xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STBarDir.Enum p0);
    
    void xsetVal(final STBarDir p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBarDir.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBarDir newInstance() {
            return (CTBarDir)getTypeLoader().newInstance(CTBarDir.type, (XmlOptions)null);
        }
        
        public static CTBarDir newInstance(final XmlOptions xmlOptions) {
            return (CTBarDir)getTypeLoader().newInstance(CTBarDir.type, xmlOptions);
        }
        
        public static CTBarDir parse(final String s) throws XmlException {
            return (CTBarDir)getTypeLoader().parse(s, CTBarDir.type, (XmlOptions)null);
        }
        
        public static CTBarDir parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBarDir)getTypeLoader().parse(s, CTBarDir.type, xmlOptions);
        }
        
        public static CTBarDir parse(final File file) throws XmlException, IOException {
            return (CTBarDir)getTypeLoader().parse(file, CTBarDir.type, (XmlOptions)null);
        }
        
        public static CTBarDir parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBarDir)getTypeLoader().parse(file, CTBarDir.type, xmlOptions);
        }
        
        public static CTBarDir parse(final URL url) throws XmlException, IOException {
            return (CTBarDir)getTypeLoader().parse(url, CTBarDir.type, (XmlOptions)null);
        }
        
        public static CTBarDir parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBarDir)getTypeLoader().parse(url, CTBarDir.type, xmlOptions);
        }
        
        public static CTBarDir parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBarDir)getTypeLoader().parse(inputStream, CTBarDir.type, (XmlOptions)null);
        }
        
        public static CTBarDir parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBarDir)getTypeLoader().parse(inputStream, CTBarDir.type, xmlOptions);
        }
        
        public static CTBarDir parse(final Reader reader) throws XmlException, IOException {
            return (CTBarDir)getTypeLoader().parse(reader, CTBarDir.type, (XmlOptions)null);
        }
        
        public static CTBarDir parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBarDir)getTypeLoader().parse(reader, CTBarDir.type, xmlOptions);
        }
        
        public static CTBarDir parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBarDir)getTypeLoader().parse(xmlStreamReader, CTBarDir.type, (XmlOptions)null);
        }
        
        public static CTBarDir parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBarDir)getTypeLoader().parse(xmlStreamReader, CTBarDir.type, xmlOptions);
        }
        
        public static CTBarDir parse(final Node node) throws XmlException {
            return (CTBarDir)getTypeLoader().parse(node, CTBarDir.type, (XmlOptions)null);
        }
        
        public static CTBarDir parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBarDir)getTypeLoader().parse(node, CTBarDir.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBarDir parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBarDir)getTypeLoader().parse(xmlInputStream, CTBarDir.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBarDir parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBarDir)getTypeLoader().parse(xmlInputStream, CTBarDir.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBarDir.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBarDir.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
