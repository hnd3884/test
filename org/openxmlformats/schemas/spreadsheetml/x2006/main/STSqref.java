package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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
import org.apache.xmlbeans.XmlAnySimpleType;

public interface STSqref extends XmlAnySimpleType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STSqref.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stsqrefb044type");
    
    List getListValue();
    
    List xgetListValue();
    
    void setListValue(final List p0);
    
    @Deprecated
    List listValue();
    
    @Deprecated
    List xlistValue();
    
    @Deprecated
    void set(final List p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STSqref newValue(final Object o) {
            return (STSqref)STSqref.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STSqref.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STSqref newInstance() {
            return (STSqref)getTypeLoader().newInstance(STSqref.type, (XmlOptions)null);
        }
        
        public static STSqref newInstance(final XmlOptions xmlOptions) {
            return (STSqref)getTypeLoader().newInstance(STSqref.type, xmlOptions);
        }
        
        public static STSqref parse(final String s) throws XmlException {
            return (STSqref)getTypeLoader().parse(s, STSqref.type, (XmlOptions)null);
        }
        
        public static STSqref parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STSqref)getTypeLoader().parse(s, STSqref.type, xmlOptions);
        }
        
        public static STSqref parse(final File file) throws XmlException, IOException {
            return (STSqref)getTypeLoader().parse(file, STSqref.type, (XmlOptions)null);
        }
        
        public static STSqref parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSqref)getTypeLoader().parse(file, STSqref.type, xmlOptions);
        }
        
        public static STSqref parse(final URL url) throws XmlException, IOException {
            return (STSqref)getTypeLoader().parse(url, STSqref.type, (XmlOptions)null);
        }
        
        public static STSqref parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSqref)getTypeLoader().parse(url, STSqref.type, xmlOptions);
        }
        
        public static STSqref parse(final InputStream inputStream) throws XmlException, IOException {
            return (STSqref)getTypeLoader().parse(inputStream, STSqref.type, (XmlOptions)null);
        }
        
        public static STSqref parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSqref)getTypeLoader().parse(inputStream, STSqref.type, xmlOptions);
        }
        
        public static STSqref parse(final Reader reader) throws XmlException, IOException {
            return (STSqref)getTypeLoader().parse(reader, STSqref.type, (XmlOptions)null);
        }
        
        public static STSqref parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSqref)getTypeLoader().parse(reader, STSqref.type, xmlOptions);
        }
        
        public static STSqref parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STSqref)getTypeLoader().parse(xmlStreamReader, STSqref.type, (XmlOptions)null);
        }
        
        public static STSqref parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STSqref)getTypeLoader().parse(xmlStreamReader, STSqref.type, xmlOptions);
        }
        
        public static STSqref parse(final Node node) throws XmlException {
            return (STSqref)getTypeLoader().parse(node, STSqref.type, (XmlOptions)null);
        }
        
        public static STSqref parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STSqref)getTypeLoader().parse(node, STSqref.type, xmlOptions);
        }
        
        @Deprecated
        public static STSqref parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STSqref)getTypeLoader().parse(xmlInputStream, STSqref.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STSqref parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STSqref)getTypeLoader().parse(xmlInputStream, STSqref.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSqref.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSqref.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
