package com.microsoft.schemas.vml;

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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTHandles extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTHandles.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cthandles5c1ftype");
    
    List<CTH> getHList();
    
    @Deprecated
    CTH[] getHArray();
    
    CTH getHArray(final int p0);
    
    int sizeOfHArray();
    
    void setHArray(final CTH[] p0);
    
    void setHArray(final int p0, final CTH p1);
    
    CTH insertNewH(final int p0);
    
    CTH addNewH();
    
    void removeH(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTHandles.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTHandles newInstance() {
            return (CTHandles)getTypeLoader().newInstance(CTHandles.type, (XmlOptions)null);
        }
        
        public static CTHandles newInstance(final XmlOptions xmlOptions) {
            return (CTHandles)getTypeLoader().newInstance(CTHandles.type, xmlOptions);
        }
        
        public static CTHandles parse(final String s) throws XmlException {
            return (CTHandles)getTypeLoader().parse(s, CTHandles.type, (XmlOptions)null);
        }
        
        public static CTHandles parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTHandles)getTypeLoader().parse(s, CTHandles.type, xmlOptions);
        }
        
        public static CTHandles parse(final File file) throws XmlException, IOException {
            return (CTHandles)getTypeLoader().parse(file, CTHandles.type, (XmlOptions)null);
        }
        
        public static CTHandles parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHandles)getTypeLoader().parse(file, CTHandles.type, xmlOptions);
        }
        
        public static CTHandles parse(final URL url) throws XmlException, IOException {
            return (CTHandles)getTypeLoader().parse(url, CTHandles.type, (XmlOptions)null);
        }
        
        public static CTHandles parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHandles)getTypeLoader().parse(url, CTHandles.type, xmlOptions);
        }
        
        public static CTHandles parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTHandles)getTypeLoader().parse(inputStream, CTHandles.type, (XmlOptions)null);
        }
        
        public static CTHandles parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHandles)getTypeLoader().parse(inputStream, CTHandles.type, xmlOptions);
        }
        
        public static CTHandles parse(final Reader reader) throws XmlException, IOException {
            return (CTHandles)getTypeLoader().parse(reader, CTHandles.type, (XmlOptions)null);
        }
        
        public static CTHandles parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHandles)getTypeLoader().parse(reader, CTHandles.type, xmlOptions);
        }
        
        public static CTHandles parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTHandles)getTypeLoader().parse(xmlStreamReader, CTHandles.type, (XmlOptions)null);
        }
        
        public static CTHandles parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTHandles)getTypeLoader().parse(xmlStreamReader, CTHandles.type, xmlOptions);
        }
        
        public static CTHandles parse(final Node node) throws XmlException {
            return (CTHandles)getTypeLoader().parse(node, CTHandles.type, (XmlOptions)null);
        }
        
        public static CTHandles parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTHandles)getTypeLoader().parse(node, CTHandles.type, xmlOptions);
        }
        
        @Deprecated
        public static CTHandles parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTHandles)getTypeLoader().parse(xmlInputStream, CTHandles.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTHandles parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTHandles)getTypeLoader().parse(xmlInputStream, CTHandles.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHandles.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHandles.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
