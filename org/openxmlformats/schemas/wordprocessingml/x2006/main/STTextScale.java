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
import org.apache.xmlbeans.XmlInteger;

public interface STTextScale extends XmlInteger
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextScale.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextscalea465type");
    
    int getIntValue();
    
    void setIntValue(final int p0);
    
    @Deprecated
    int intValue();
    
    @Deprecated
    void set(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextScale newValue(final Object o) {
            return (STTextScale)STTextScale.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextScale.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextScale newInstance() {
            return (STTextScale)getTypeLoader().newInstance(STTextScale.type, (XmlOptions)null);
        }
        
        public static STTextScale newInstance(final XmlOptions xmlOptions) {
            return (STTextScale)getTypeLoader().newInstance(STTextScale.type, xmlOptions);
        }
        
        public static STTextScale parse(final String s) throws XmlException {
            return (STTextScale)getTypeLoader().parse(s, STTextScale.type, (XmlOptions)null);
        }
        
        public static STTextScale parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextScale)getTypeLoader().parse(s, STTextScale.type, xmlOptions);
        }
        
        public static STTextScale parse(final File file) throws XmlException, IOException {
            return (STTextScale)getTypeLoader().parse(file, STTextScale.type, (XmlOptions)null);
        }
        
        public static STTextScale parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextScale)getTypeLoader().parse(file, STTextScale.type, xmlOptions);
        }
        
        public static STTextScale parse(final URL url) throws XmlException, IOException {
            return (STTextScale)getTypeLoader().parse(url, STTextScale.type, (XmlOptions)null);
        }
        
        public static STTextScale parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextScale)getTypeLoader().parse(url, STTextScale.type, xmlOptions);
        }
        
        public static STTextScale parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextScale)getTypeLoader().parse(inputStream, STTextScale.type, (XmlOptions)null);
        }
        
        public static STTextScale parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextScale)getTypeLoader().parse(inputStream, STTextScale.type, xmlOptions);
        }
        
        public static STTextScale parse(final Reader reader) throws XmlException, IOException {
            return (STTextScale)getTypeLoader().parse(reader, STTextScale.type, (XmlOptions)null);
        }
        
        public static STTextScale parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextScale)getTypeLoader().parse(reader, STTextScale.type, xmlOptions);
        }
        
        public static STTextScale parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextScale)getTypeLoader().parse(xmlStreamReader, STTextScale.type, (XmlOptions)null);
        }
        
        public static STTextScale parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextScale)getTypeLoader().parse(xmlStreamReader, STTextScale.type, xmlOptions);
        }
        
        public static STTextScale parse(final Node node) throws XmlException {
            return (STTextScale)getTypeLoader().parse(node, STTextScale.type, (XmlOptions)null);
        }
        
        public static STTextScale parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextScale)getTypeLoader().parse(node, STTextScale.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextScale parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextScale)getTypeLoader().parse(xmlInputStream, STTextScale.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextScale parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextScale)getTypeLoader().parse(xmlInputStream, STTextScale.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextScale.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextScale.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
