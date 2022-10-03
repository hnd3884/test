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

public interface STHashSize extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STHashSize.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("sthashsize605btype");
    
    int getIntValue();
    
    void setIntValue(final int p0);
    
    @Deprecated
    int intValue();
    
    @Deprecated
    void set(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STHashSize newValue(final Object o) {
            return (STHashSize)STHashSize.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STHashSize.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STHashSize newInstance() {
            return (STHashSize)getTypeLoader().newInstance(STHashSize.type, (XmlOptions)null);
        }
        
        public static STHashSize newInstance(final XmlOptions xmlOptions) {
            return (STHashSize)getTypeLoader().newInstance(STHashSize.type, xmlOptions);
        }
        
        public static STHashSize parse(final String s) throws XmlException {
            return (STHashSize)getTypeLoader().parse(s, STHashSize.type, (XmlOptions)null);
        }
        
        public static STHashSize parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STHashSize)getTypeLoader().parse(s, STHashSize.type, xmlOptions);
        }
        
        public static STHashSize parse(final File file) throws XmlException, IOException {
            return (STHashSize)getTypeLoader().parse(file, STHashSize.type, (XmlOptions)null);
        }
        
        public static STHashSize parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHashSize)getTypeLoader().parse(file, STHashSize.type, xmlOptions);
        }
        
        public static STHashSize parse(final URL url) throws XmlException, IOException {
            return (STHashSize)getTypeLoader().parse(url, STHashSize.type, (XmlOptions)null);
        }
        
        public static STHashSize parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHashSize)getTypeLoader().parse(url, STHashSize.type, xmlOptions);
        }
        
        public static STHashSize parse(final InputStream inputStream) throws XmlException, IOException {
            return (STHashSize)getTypeLoader().parse(inputStream, STHashSize.type, (XmlOptions)null);
        }
        
        public static STHashSize parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHashSize)getTypeLoader().parse(inputStream, STHashSize.type, xmlOptions);
        }
        
        public static STHashSize parse(final Reader reader) throws XmlException, IOException {
            return (STHashSize)getTypeLoader().parse(reader, STHashSize.type, (XmlOptions)null);
        }
        
        public static STHashSize parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHashSize)getTypeLoader().parse(reader, STHashSize.type, xmlOptions);
        }
        
        public static STHashSize parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STHashSize)getTypeLoader().parse(xmlStreamReader, STHashSize.type, (XmlOptions)null);
        }
        
        public static STHashSize parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STHashSize)getTypeLoader().parse(xmlStreamReader, STHashSize.type, xmlOptions);
        }
        
        public static STHashSize parse(final Node node) throws XmlException {
            return (STHashSize)getTypeLoader().parse(node, STHashSize.type, (XmlOptions)null);
        }
        
        public static STHashSize parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STHashSize)getTypeLoader().parse(node, STHashSize.type, xmlOptions);
        }
        
        @Deprecated
        public static STHashSize parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STHashSize)getTypeLoader().parse(xmlInputStream, STHashSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STHashSize parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STHashSize)getTypeLoader().parse(xmlInputStream, STHashSize.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHashSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHashSize.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
