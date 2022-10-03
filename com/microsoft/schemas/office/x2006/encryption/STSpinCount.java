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

public interface STSpinCount extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STSpinCount.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("stspincount544ftype");
    
    int getIntValue();
    
    void setIntValue(final int p0);
    
    @Deprecated
    int intValue();
    
    @Deprecated
    void set(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STSpinCount newValue(final Object o) {
            return (STSpinCount)STSpinCount.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STSpinCount.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STSpinCount newInstance() {
            return (STSpinCount)getTypeLoader().newInstance(STSpinCount.type, (XmlOptions)null);
        }
        
        public static STSpinCount newInstance(final XmlOptions xmlOptions) {
            return (STSpinCount)getTypeLoader().newInstance(STSpinCount.type, xmlOptions);
        }
        
        public static STSpinCount parse(final String s) throws XmlException {
            return (STSpinCount)getTypeLoader().parse(s, STSpinCount.type, (XmlOptions)null);
        }
        
        public static STSpinCount parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STSpinCount)getTypeLoader().parse(s, STSpinCount.type, xmlOptions);
        }
        
        public static STSpinCount parse(final File file) throws XmlException, IOException {
            return (STSpinCount)getTypeLoader().parse(file, STSpinCount.type, (XmlOptions)null);
        }
        
        public static STSpinCount parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSpinCount)getTypeLoader().parse(file, STSpinCount.type, xmlOptions);
        }
        
        public static STSpinCount parse(final URL url) throws XmlException, IOException {
            return (STSpinCount)getTypeLoader().parse(url, STSpinCount.type, (XmlOptions)null);
        }
        
        public static STSpinCount parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSpinCount)getTypeLoader().parse(url, STSpinCount.type, xmlOptions);
        }
        
        public static STSpinCount parse(final InputStream inputStream) throws XmlException, IOException {
            return (STSpinCount)getTypeLoader().parse(inputStream, STSpinCount.type, (XmlOptions)null);
        }
        
        public static STSpinCount parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSpinCount)getTypeLoader().parse(inputStream, STSpinCount.type, xmlOptions);
        }
        
        public static STSpinCount parse(final Reader reader) throws XmlException, IOException {
            return (STSpinCount)getTypeLoader().parse(reader, STSpinCount.type, (XmlOptions)null);
        }
        
        public static STSpinCount parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSpinCount)getTypeLoader().parse(reader, STSpinCount.type, xmlOptions);
        }
        
        public static STSpinCount parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STSpinCount)getTypeLoader().parse(xmlStreamReader, STSpinCount.type, (XmlOptions)null);
        }
        
        public static STSpinCount parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STSpinCount)getTypeLoader().parse(xmlStreamReader, STSpinCount.type, xmlOptions);
        }
        
        public static STSpinCount parse(final Node node) throws XmlException {
            return (STSpinCount)getTypeLoader().parse(node, STSpinCount.type, (XmlOptions)null);
        }
        
        public static STSpinCount parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STSpinCount)getTypeLoader().parse(node, STSpinCount.type, xmlOptions);
        }
        
        @Deprecated
        public static STSpinCount parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STSpinCount)getTypeLoader().parse(xmlInputStream, STSpinCount.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STSpinCount parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STSpinCount)getTypeLoader().parse(xmlInputStream, STSpinCount.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSpinCount.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSpinCount.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
