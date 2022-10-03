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

public interface STSaltSize extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STSaltSize.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("stsaltsizee7a3type");
    
    int getIntValue();
    
    void setIntValue(final int p0);
    
    @Deprecated
    int intValue();
    
    @Deprecated
    void set(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STSaltSize newValue(final Object o) {
            return (STSaltSize)STSaltSize.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STSaltSize.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STSaltSize newInstance() {
            return (STSaltSize)getTypeLoader().newInstance(STSaltSize.type, (XmlOptions)null);
        }
        
        public static STSaltSize newInstance(final XmlOptions xmlOptions) {
            return (STSaltSize)getTypeLoader().newInstance(STSaltSize.type, xmlOptions);
        }
        
        public static STSaltSize parse(final String s) throws XmlException {
            return (STSaltSize)getTypeLoader().parse(s, STSaltSize.type, (XmlOptions)null);
        }
        
        public static STSaltSize parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STSaltSize)getTypeLoader().parse(s, STSaltSize.type, xmlOptions);
        }
        
        public static STSaltSize parse(final File file) throws XmlException, IOException {
            return (STSaltSize)getTypeLoader().parse(file, STSaltSize.type, (XmlOptions)null);
        }
        
        public static STSaltSize parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSaltSize)getTypeLoader().parse(file, STSaltSize.type, xmlOptions);
        }
        
        public static STSaltSize parse(final URL url) throws XmlException, IOException {
            return (STSaltSize)getTypeLoader().parse(url, STSaltSize.type, (XmlOptions)null);
        }
        
        public static STSaltSize parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSaltSize)getTypeLoader().parse(url, STSaltSize.type, xmlOptions);
        }
        
        public static STSaltSize parse(final InputStream inputStream) throws XmlException, IOException {
            return (STSaltSize)getTypeLoader().parse(inputStream, STSaltSize.type, (XmlOptions)null);
        }
        
        public static STSaltSize parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSaltSize)getTypeLoader().parse(inputStream, STSaltSize.type, xmlOptions);
        }
        
        public static STSaltSize parse(final Reader reader) throws XmlException, IOException {
            return (STSaltSize)getTypeLoader().parse(reader, STSaltSize.type, (XmlOptions)null);
        }
        
        public static STSaltSize parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSaltSize)getTypeLoader().parse(reader, STSaltSize.type, xmlOptions);
        }
        
        public static STSaltSize parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STSaltSize)getTypeLoader().parse(xmlStreamReader, STSaltSize.type, (XmlOptions)null);
        }
        
        public static STSaltSize parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STSaltSize)getTypeLoader().parse(xmlStreamReader, STSaltSize.type, xmlOptions);
        }
        
        public static STSaltSize parse(final Node node) throws XmlException {
            return (STSaltSize)getTypeLoader().parse(node, STSaltSize.type, (XmlOptions)null);
        }
        
        public static STSaltSize parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STSaltSize)getTypeLoader().parse(node, STSaltSize.type, xmlOptions);
        }
        
        @Deprecated
        public static STSaltSize parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STSaltSize)getTypeLoader().parse(xmlInputStream, STSaltSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STSaltSize parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STSaltSize)getTypeLoader().parse(xmlInputStream, STSaltSize.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSaltSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSaltSize.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
