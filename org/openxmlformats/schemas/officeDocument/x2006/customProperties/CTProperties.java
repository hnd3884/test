package org.openxmlformats.schemas.officeDocument.x2006.customProperties;

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

public interface CTProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctproperties2c18type");
    
    List<CTProperty> getPropertyList();
    
    @Deprecated
    CTProperty[] getPropertyArray();
    
    CTProperty getPropertyArray(final int p0);
    
    int sizeOfPropertyArray();
    
    void setPropertyArray(final CTProperty[] p0);
    
    void setPropertyArray(final int p0, final CTProperty p1);
    
    CTProperty insertNewProperty(final int p0);
    
    CTProperty addNewProperty();
    
    void removeProperty(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTProperties newInstance() {
            return (CTProperties)getTypeLoader().newInstance(CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties newInstance(final XmlOptions xmlOptions) {
            return (CTProperties)getTypeLoader().newInstance(CTProperties.type, xmlOptions);
        }
        
        public static CTProperties parse(final String s) throws XmlException {
            return (CTProperties)getTypeLoader().parse(s, CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTProperties)getTypeLoader().parse(s, CTProperties.type, xmlOptions);
        }
        
        public static CTProperties parse(final File file) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(file, CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(file, CTProperties.type, xmlOptions);
        }
        
        public static CTProperties parse(final URL url) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(url, CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(url, CTProperties.type, xmlOptions);
        }
        
        public static CTProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(inputStream, CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(inputStream, CTProperties.type, xmlOptions);
        }
        
        public static CTProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(reader, CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(reader, CTProperties.type, xmlOptions);
        }
        
        public static CTProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTProperties)getTypeLoader().parse(xmlStreamReader, CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTProperties)getTypeLoader().parse(xmlStreamReader, CTProperties.type, xmlOptions);
        }
        
        public static CTProperties parse(final Node node) throws XmlException {
            return (CTProperties)getTypeLoader().parse(node, CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTProperties)getTypeLoader().parse(node, CTProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTProperties)getTypeLoader().parse(xmlInputStream, CTProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTProperties)getTypeLoader().parse(xmlInputStream, CTProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
