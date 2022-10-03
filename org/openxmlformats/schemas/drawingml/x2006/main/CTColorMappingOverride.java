package org.openxmlformats.schemas.drawingml.x2006.main;

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

public interface CTColorMappingOverride extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTColorMappingOverride.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcolormappingoverridea2b2type");
    
    CTEmptyElement getMasterClrMapping();
    
    boolean isSetMasterClrMapping();
    
    void setMasterClrMapping(final CTEmptyElement p0);
    
    CTEmptyElement addNewMasterClrMapping();
    
    void unsetMasterClrMapping();
    
    CTColorMapping getOverrideClrMapping();
    
    boolean isSetOverrideClrMapping();
    
    void setOverrideClrMapping(final CTColorMapping p0);
    
    CTColorMapping addNewOverrideClrMapping();
    
    void unsetOverrideClrMapping();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTColorMappingOverride.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTColorMappingOverride newInstance() {
            return (CTColorMappingOverride)getTypeLoader().newInstance(CTColorMappingOverride.type, (XmlOptions)null);
        }
        
        public static CTColorMappingOverride newInstance(final XmlOptions xmlOptions) {
            return (CTColorMappingOverride)getTypeLoader().newInstance(CTColorMappingOverride.type, xmlOptions);
        }
        
        public static CTColorMappingOverride parse(final String s) throws XmlException {
            return (CTColorMappingOverride)getTypeLoader().parse(s, CTColorMappingOverride.type, (XmlOptions)null);
        }
        
        public static CTColorMappingOverride parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTColorMappingOverride)getTypeLoader().parse(s, CTColorMappingOverride.type, xmlOptions);
        }
        
        public static CTColorMappingOverride parse(final File file) throws XmlException, IOException {
            return (CTColorMappingOverride)getTypeLoader().parse(file, CTColorMappingOverride.type, (XmlOptions)null);
        }
        
        public static CTColorMappingOverride parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorMappingOverride)getTypeLoader().parse(file, CTColorMappingOverride.type, xmlOptions);
        }
        
        public static CTColorMappingOverride parse(final URL url) throws XmlException, IOException {
            return (CTColorMappingOverride)getTypeLoader().parse(url, CTColorMappingOverride.type, (XmlOptions)null);
        }
        
        public static CTColorMappingOverride parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorMappingOverride)getTypeLoader().parse(url, CTColorMappingOverride.type, xmlOptions);
        }
        
        public static CTColorMappingOverride parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTColorMappingOverride)getTypeLoader().parse(inputStream, CTColorMappingOverride.type, (XmlOptions)null);
        }
        
        public static CTColorMappingOverride parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorMappingOverride)getTypeLoader().parse(inputStream, CTColorMappingOverride.type, xmlOptions);
        }
        
        public static CTColorMappingOverride parse(final Reader reader) throws XmlException, IOException {
            return (CTColorMappingOverride)getTypeLoader().parse(reader, CTColorMappingOverride.type, (XmlOptions)null);
        }
        
        public static CTColorMappingOverride parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorMappingOverride)getTypeLoader().parse(reader, CTColorMappingOverride.type, xmlOptions);
        }
        
        public static CTColorMappingOverride parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTColorMappingOverride)getTypeLoader().parse(xmlStreamReader, CTColorMappingOverride.type, (XmlOptions)null);
        }
        
        public static CTColorMappingOverride parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTColorMappingOverride)getTypeLoader().parse(xmlStreamReader, CTColorMappingOverride.type, xmlOptions);
        }
        
        public static CTColorMappingOverride parse(final Node node) throws XmlException {
            return (CTColorMappingOverride)getTypeLoader().parse(node, CTColorMappingOverride.type, (XmlOptions)null);
        }
        
        public static CTColorMappingOverride parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTColorMappingOverride)getTypeLoader().parse(node, CTColorMappingOverride.type, xmlOptions);
        }
        
        @Deprecated
        public static CTColorMappingOverride parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTColorMappingOverride)getTypeLoader().parse(xmlInputStream, CTColorMappingOverride.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTColorMappingOverride parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTColorMappingOverride)getTypeLoader().parse(xmlInputStream, CTColorMappingOverride.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTColorMappingOverride.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTColorMappingOverride.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
