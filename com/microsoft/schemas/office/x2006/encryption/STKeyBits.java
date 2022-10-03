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

public interface STKeyBits extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STKeyBits.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("stkeybitse527type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STKeyBits newValue(final Object o) {
            return (STKeyBits)STKeyBits.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STKeyBits.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STKeyBits newInstance() {
            return (STKeyBits)getTypeLoader().newInstance(STKeyBits.type, (XmlOptions)null);
        }
        
        public static STKeyBits newInstance(final XmlOptions xmlOptions) {
            return (STKeyBits)getTypeLoader().newInstance(STKeyBits.type, xmlOptions);
        }
        
        public static STKeyBits parse(final String s) throws XmlException {
            return (STKeyBits)getTypeLoader().parse(s, STKeyBits.type, (XmlOptions)null);
        }
        
        public static STKeyBits parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STKeyBits)getTypeLoader().parse(s, STKeyBits.type, xmlOptions);
        }
        
        public static STKeyBits parse(final File file) throws XmlException, IOException {
            return (STKeyBits)getTypeLoader().parse(file, STKeyBits.type, (XmlOptions)null);
        }
        
        public static STKeyBits parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STKeyBits)getTypeLoader().parse(file, STKeyBits.type, xmlOptions);
        }
        
        public static STKeyBits parse(final URL url) throws XmlException, IOException {
            return (STKeyBits)getTypeLoader().parse(url, STKeyBits.type, (XmlOptions)null);
        }
        
        public static STKeyBits parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STKeyBits)getTypeLoader().parse(url, STKeyBits.type, xmlOptions);
        }
        
        public static STKeyBits parse(final InputStream inputStream) throws XmlException, IOException {
            return (STKeyBits)getTypeLoader().parse(inputStream, STKeyBits.type, (XmlOptions)null);
        }
        
        public static STKeyBits parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STKeyBits)getTypeLoader().parse(inputStream, STKeyBits.type, xmlOptions);
        }
        
        public static STKeyBits parse(final Reader reader) throws XmlException, IOException {
            return (STKeyBits)getTypeLoader().parse(reader, STKeyBits.type, (XmlOptions)null);
        }
        
        public static STKeyBits parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STKeyBits)getTypeLoader().parse(reader, STKeyBits.type, xmlOptions);
        }
        
        public static STKeyBits parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STKeyBits)getTypeLoader().parse(xmlStreamReader, STKeyBits.type, (XmlOptions)null);
        }
        
        public static STKeyBits parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STKeyBits)getTypeLoader().parse(xmlStreamReader, STKeyBits.type, xmlOptions);
        }
        
        public static STKeyBits parse(final Node node) throws XmlException {
            return (STKeyBits)getTypeLoader().parse(node, STKeyBits.type, (XmlOptions)null);
        }
        
        public static STKeyBits parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STKeyBits)getTypeLoader().parse(node, STKeyBits.type, xmlOptions);
        }
        
        @Deprecated
        public static STKeyBits parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STKeyBits)getTypeLoader().parse(xmlInputStream, STKeyBits.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STKeyBits parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STKeyBits)getTypeLoader().parse(xmlInputStream, STKeyBits.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STKeyBits.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STKeyBits.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
