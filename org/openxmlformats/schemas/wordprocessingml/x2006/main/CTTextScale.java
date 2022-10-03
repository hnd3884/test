package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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

public interface CTTextScale extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextScale.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextscale3455type");
    
    int getVal();
    
    STTextScale xgetVal();
    
    boolean isSetVal();
    
    void setVal(final int p0);
    
    void xsetVal(final STTextScale p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextScale.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextScale newInstance() {
            return (CTTextScale)getTypeLoader().newInstance(CTTextScale.type, (XmlOptions)null);
        }
        
        public static CTTextScale newInstance(final XmlOptions xmlOptions) {
            return (CTTextScale)getTypeLoader().newInstance(CTTextScale.type, xmlOptions);
        }
        
        public static CTTextScale parse(final String s) throws XmlException {
            return (CTTextScale)getTypeLoader().parse(s, CTTextScale.type, (XmlOptions)null);
        }
        
        public static CTTextScale parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextScale)getTypeLoader().parse(s, CTTextScale.type, xmlOptions);
        }
        
        public static CTTextScale parse(final File file) throws XmlException, IOException {
            return (CTTextScale)getTypeLoader().parse(file, CTTextScale.type, (XmlOptions)null);
        }
        
        public static CTTextScale parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextScale)getTypeLoader().parse(file, CTTextScale.type, xmlOptions);
        }
        
        public static CTTextScale parse(final URL url) throws XmlException, IOException {
            return (CTTextScale)getTypeLoader().parse(url, CTTextScale.type, (XmlOptions)null);
        }
        
        public static CTTextScale parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextScale)getTypeLoader().parse(url, CTTextScale.type, xmlOptions);
        }
        
        public static CTTextScale parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextScale)getTypeLoader().parse(inputStream, CTTextScale.type, (XmlOptions)null);
        }
        
        public static CTTextScale parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextScale)getTypeLoader().parse(inputStream, CTTextScale.type, xmlOptions);
        }
        
        public static CTTextScale parse(final Reader reader) throws XmlException, IOException {
            return (CTTextScale)getTypeLoader().parse(reader, CTTextScale.type, (XmlOptions)null);
        }
        
        public static CTTextScale parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextScale)getTypeLoader().parse(reader, CTTextScale.type, xmlOptions);
        }
        
        public static CTTextScale parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextScale)getTypeLoader().parse(xmlStreamReader, CTTextScale.type, (XmlOptions)null);
        }
        
        public static CTTextScale parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextScale)getTypeLoader().parse(xmlStreamReader, CTTextScale.type, xmlOptions);
        }
        
        public static CTTextScale parse(final Node node) throws XmlException {
            return (CTTextScale)getTypeLoader().parse(node, CTTextScale.type, (XmlOptions)null);
        }
        
        public static CTTextScale parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextScale)getTypeLoader().parse(node, CTTextScale.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextScale parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextScale)getTypeLoader().parse(xmlInputStream, CTTextScale.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextScale parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextScale)getTypeLoader().parse(xmlInputStream, CTTextScale.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextScale.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextScale.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
