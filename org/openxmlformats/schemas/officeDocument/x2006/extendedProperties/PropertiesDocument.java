package org.openxmlformats.schemas.officeDocument.x2006.extendedProperties;

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

public interface PropertiesDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(PropertiesDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("propertiesee84doctype");
    
    CTProperties getProperties();
    
    void setProperties(final CTProperties p0);
    
    CTProperties addNewProperties();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(PropertiesDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static PropertiesDocument newInstance() {
            return (PropertiesDocument)getTypeLoader().newInstance(PropertiesDocument.type, (XmlOptions)null);
        }
        
        public static PropertiesDocument newInstance(final XmlOptions xmlOptions) {
            return (PropertiesDocument)getTypeLoader().newInstance(PropertiesDocument.type, xmlOptions);
        }
        
        public static PropertiesDocument parse(final String s) throws XmlException {
            return (PropertiesDocument)getTypeLoader().parse(s, PropertiesDocument.type, (XmlOptions)null);
        }
        
        public static PropertiesDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (PropertiesDocument)getTypeLoader().parse(s, PropertiesDocument.type, xmlOptions);
        }
        
        public static PropertiesDocument parse(final File file) throws XmlException, IOException {
            return (PropertiesDocument)getTypeLoader().parse(file, PropertiesDocument.type, (XmlOptions)null);
        }
        
        public static PropertiesDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PropertiesDocument)getTypeLoader().parse(file, PropertiesDocument.type, xmlOptions);
        }
        
        public static PropertiesDocument parse(final URL url) throws XmlException, IOException {
            return (PropertiesDocument)getTypeLoader().parse(url, PropertiesDocument.type, (XmlOptions)null);
        }
        
        public static PropertiesDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PropertiesDocument)getTypeLoader().parse(url, PropertiesDocument.type, xmlOptions);
        }
        
        public static PropertiesDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (PropertiesDocument)getTypeLoader().parse(inputStream, PropertiesDocument.type, (XmlOptions)null);
        }
        
        public static PropertiesDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PropertiesDocument)getTypeLoader().parse(inputStream, PropertiesDocument.type, xmlOptions);
        }
        
        public static PropertiesDocument parse(final Reader reader) throws XmlException, IOException {
            return (PropertiesDocument)getTypeLoader().parse(reader, PropertiesDocument.type, (XmlOptions)null);
        }
        
        public static PropertiesDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PropertiesDocument)getTypeLoader().parse(reader, PropertiesDocument.type, xmlOptions);
        }
        
        public static PropertiesDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (PropertiesDocument)getTypeLoader().parse(xmlStreamReader, PropertiesDocument.type, (XmlOptions)null);
        }
        
        public static PropertiesDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (PropertiesDocument)getTypeLoader().parse(xmlStreamReader, PropertiesDocument.type, xmlOptions);
        }
        
        public static PropertiesDocument parse(final Node node) throws XmlException {
            return (PropertiesDocument)getTypeLoader().parse(node, PropertiesDocument.type, (XmlOptions)null);
        }
        
        public static PropertiesDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (PropertiesDocument)getTypeLoader().parse(node, PropertiesDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static PropertiesDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (PropertiesDocument)getTypeLoader().parse(xmlInputStream, PropertiesDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static PropertiesDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (PropertiesDocument)getTypeLoader().parse(xmlInputStream, PropertiesDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PropertiesDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PropertiesDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
