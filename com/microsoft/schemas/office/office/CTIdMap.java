package com.microsoft.schemas.office.office;

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
import org.apache.xmlbeans.XmlString;
import com.microsoft.schemas.vml.STExt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTIdMap extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTIdMap.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctidmap63fatype");
    
    STExt.Enum getExt();
    
    STExt xgetExt();
    
    boolean isSetExt();
    
    void setExt(final STExt.Enum p0);
    
    void xsetExt(final STExt p0);
    
    void unsetExt();
    
    String getData();
    
    XmlString xgetData();
    
    boolean isSetData();
    
    void setData(final String p0);
    
    void xsetData(final XmlString p0);
    
    void unsetData();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTIdMap.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTIdMap newInstance() {
            return (CTIdMap)getTypeLoader().newInstance(CTIdMap.type, (XmlOptions)null);
        }
        
        public static CTIdMap newInstance(final XmlOptions xmlOptions) {
            return (CTIdMap)getTypeLoader().newInstance(CTIdMap.type, xmlOptions);
        }
        
        public static CTIdMap parse(final String s) throws XmlException {
            return (CTIdMap)getTypeLoader().parse(s, CTIdMap.type, (XmlOptions)null);
        }
        
        public static CTIdMap parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTIdMap)getTypeLoader().parse(s, CTIdMap.type, xmlOptions);
        }
        
        public static CTIdMap parse(final File file) throws XmlException, IOException {
            return (CTIdMap)getTypeLoader().parse(file, CTIdMap.type, (XmlOptions)null);
        }
        
        public static CTIdMap parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIdMap)getTypeLoader().parse(file, CTIdMap.type, xmlOptions);
        }
        
        public static CTIdMap parse(final URL url) throws XmlException, IOException {
            return (CTIdMap)getTypeLoader().parse(url, CTIdMap.type, (XmlOptions)null);
        }
        
        public static CTIdMap parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIdMap)getTypeLoader().parse(url, CTIdMap.type, xmlOptions);
        }
        
        public static CTIdMap parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTIdMap)getTypeLoader().parse(inputStream, CTIdMap.type, (XmlOptions)null);
        }
        
        public static CTIdMap parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIdMap)getTypeLoader().parse(inputStream, CTIdMap.type, xmlOptions);
        }
        
        public static CTIdMap parse(final Reader reader) throws XmlException, IOException {
            return (CTIdMap)getTypeLoader().parse(reader, CTIdMap.type, (XmlOptions)null);
        }
        
        public static CTIdMap parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIdMap)getTypeLoader().parse(reader, CTIdMap.type, xmlOptions);
        }
        
        public static CTIdMap parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTIdMap)getTypeLoader().parse(xmlStreamReader, CTIdMap.type, (XmlOptions)null);
        }
        
        public static CTIdMap parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTIdMap)getTypeLoader().parse(xmlStreamReader, CTIdMap.type, xmlOptions);
        }
        
        public static CTIdMap parse(final Node node) throws XmlException {
            return (CTIdMap)getTypeLoader().parse(node, CTIdMap.type, (XmlOptions)null);
        }
        
        public static CTIdMap parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTIdMap)getTypeLoader().parse(node, CTIdMap.type, xmlOptions);
        }
        
        @Deprecated
        public static CTIdMap parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTIdMap)getTypeLoader().parse(xmlInputStream, CTIdMap.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTIdMap parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTIdMap)getTypeLoader().parse(xmlInputStream, CTIdMap.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTIdMap.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTIdMap.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
