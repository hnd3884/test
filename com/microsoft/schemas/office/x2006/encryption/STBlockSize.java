package com.microsoft.schemas.office.x2006.encryption;

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
import org.apache.xmlbeans.XmlUnsignedInt;

public interface STBlockSize extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STBlockSize.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("stblocksize2e10type");
    
    int getIntValue();
    
    void setIntValue(final int p0);
    
    @Deprecated
    int intValue();
    
    @Deprecated
    void set(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STBlockSize newValue(final Object o) {
            return (STBlockSize)STBlockSize.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STBlockSize.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STBlockSize newInstance() {
            return (STBlockSize)getTypeLoader().newInstance(STBlockSize.type, (XmlOptions)null);
        }
        
        public static STBlockSize newInstance(final XmlOptions xmlOptions) {
            return (STBlockSize)getTypeLoader().newInstance(STBlockSize.type, xmlOptions);
        }
        
        public static STBlockSize parse(final String s) throws XmlException {
            return (STBlockSize)getTypeLoader().parse(s, STBlockSize.type, (XmlOptions)null);
        }
        
        public static STBlockSize parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STBlockSize)getTypeLoader().parse(s, STBlockSize.type, xmlOptions);
        }
        
        public static STBlockSize parse(final File file) throws XmlException, IOException {
            return (STBlockSize)getTypeLoader().parse(file, STBlockSize.type, (XmlOptions)null);
        }
        
        public static STBlockSize parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBlockSize)getTypeLoader().parse(file, STBlockSize.type, xmlOptions);
        }
        
        public static STBlockSize parse(final URL url) throws XmlException, IOException {
            return (STBlockSize)getTypeLoader().parse(url, STBlockSize.type, (XmlOptions)null);
        }
        
        public static STBlockSize parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBlockSize)getTypeLoader().parse(url, STBlockSize.type, xmlOptions);
        }
        
        public static STBlockSize parse(final InputStream inputStream) throws XmlException, IOException {
            return (STBlockSize)getTypeLoader().parse(inputStream, STBlockSize.type, (XmlOptions)null);
        }
        
        public static STBlockSize parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBlockSize)getTypeLoader().parse(inputStream, STBlockSize.type, xmlOptions);
        }
        
        public static STBlockSize parse(final Reader reader) throws XmlException, IOException {
            return (STBlockSize)getTypeLoader().parse(reader, STBlockSize.type, (XmlOptions)null);
        }
        
        public static STBlockSize parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBlockSize)getTypeLoader().parse(reader, STBlockSize.type, xmlOptions);
        }
        
        public static STBlockSize parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STBlockSize)getTypeLoader().parse(xmlStreamReader, STBlockSize.type, (XmlOptions)null);
        }
        
        public static STBlockSize parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STBlockSize)getTypeLoader().parse(xmlStreamReader, STBlockSize.type, xmlOptions);
        }
        
        public static STBlockSize parse(final Node node) throws XmlException {
            return (STBlockSize)getTypeLoader().parse(node, STBlockSize.type, (XmlOptions)null);
        }
        
        public static STBlockSize parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STBlockSize)getTypeLoader().parse(node, STBlockSize.type, xmlOptions);
        }
        
        @Deprecated
        public static STBlockSize parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STBlockSize)getTypeLoader().parse(xmlInputStream, STBlockSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STBlockSize parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STBlockSize)getTypeLoader().parse(xmlInputStream, STBlockSize.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STBlockSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STBlockSize.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
