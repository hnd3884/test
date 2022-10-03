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

public interface CTRotY extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRotY.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctroty8f1atype");
    
    int getVal();
    
    STRotY xgetVal();
    
    boolean isSetVal();
    
    void setVal(final int p0);
    
    void xsetVal(final STRotY p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRotY.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRotY newInstance() {
            return (CTRotY)getTypeLoader().newInstance(CTRotY.type, (XmlOptions)null);
        }
        
        public static CTRotY newInstance(final XmlOptions xmlOptions) {
            return (CTRotY)getTypeLoader().newInstance(CTRotY.type, xmlOptions);
        }
        
        public static CTRotY parse(final String s) throws XmlException {
            return (CTRotY)getTypeLoader().parse(s, CTRotY.type, (XmlOptions)null);
        }
        
        public static CTRotY parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRotY)getTypeLoader().parse(s, CTRotY.type, xmlOptions);
        }
        
        public static CTRotY parse(final File file) throws XmlException, IOException {
            return (CTRotY)getTypeLoader().parse(file, CTRotY.type, (XmlOptions)null);
        }
        
        public static CTRotY parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRotY)getTypeLoader().parse(file, CTRotY.type, xmlOptions);
        }
        
        public static CTRotY parse(final URL url) throws XmlException, IOException {
            return (CTRotY)getTypeLoader().parse(url, CTRotY.type, (XmlOptions)null);
        }
        
        public static CTRotY parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRotY)getTypeLoader().parse(url, CTRotY.type, xmlOptions);
        }
        
        public static CTRotY parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRotY)getTypeLoader().parse(inputStream, CTRotY.type, (XmlOptions)null);
        }
        
        public static CTRotY parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRotY)getTypeLoader().parse(inputStream, CTRotY.type, xmlOptions);
        }
        
        public static CTRotY parse(final Reader reader) throws XmlException, IOException {
            return (CTRotY)getTypeLoader().parse(reader, CTRotY.type, (XmlOptions)null);
        }
        
        public static CTRotY parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRotY)getTypeLoader().parse(reader, CTRotY.type, xmlOptions);
        }
        
        public static CTRotY parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRotY)getTypeLoader().parse(xmlStreamReader, CTRotY.type, (XmlOptions)null);
        }
        
        public static CTRotY parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRotY)getTypeLoader().parse(xmlStreamReader, CTRotY.type, xmlOptions);
        }
        
        public static CTRotY parse(final Node node) throws XmlException {
            return (CTRotY)getTypeLoader().parse(node, CTRotY.type, (XmlOptions)null);
        }
        
        public static CTRotY parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRotY)getTypeLoader().parse(node, CTRotY.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRotY parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRotY)getTypeLoader().parse(xmlInputStream, CTRotY.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRotY parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRotY)getTypeLoader().parse(xmlInputStream, CTRotY.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRotY.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRotY.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
