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
import org.apache.xmlbeans.XmlObject;

public interface SettingsDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SettingsDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("settings9dd1doctype");
    
    CTSettings getSettings();
    
    void setSettings(final CTSettings p0);
    
    CTSettings addNewSettings();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(SettingsDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static SettingsDocument newInstance() {
            return (SettingsDocument)getTypeLoader().newInstance(SettingsDocument.type, (XmlOptions)null);
        }
        
        public static SettingsDocument newInstance(final XmlOptions xmlOptions) {
            return (SettingsDocument)getTypeLoader().newInstance(SettingsDocument.type, xmlOptions);
        }
        
        public static SettingsDocument parse(final String s) throws XmlException {
            return (SettingsDocument)getTypeLoader().parse(s, SettingsDocument.type, (XmlOptions)null);
        }
        
        public static SettingsDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (SettingsDocument)getTypeLoader().parse(s, SettingsDocument.type, xmlOptions);
        }
        
        public static SettingsDocument parse(final File file) throws XmlException, IOException {
            return (SettingsDocument)getTypeLoader().parse(file, SettingsDocument.type, (XmlOptions)null);
        }
        
        public static SettingsDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SettingsDocument)getTypeLoader().parse(file, SettingsDocument.type, xmlOptions);
        }
        
        public static SettingsDocument parse(final URL url) throws XmlException, IOException {
            return (SettingsDocument)getTypeLoader().parse(url, SettingsDocument.type, (XmlOptions)null);
        }
        
        public static SettingsDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SettingsDocument)getTypeLoader().parse(url, SettingsDocument.type, xmlOptions);
        }
        
        public static SettingsDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (SettingsDocument)getTypeLoader().parse(inputStream, SettingsDocument.type, (XmlOptions)null);
        }
        
        public static SettingsDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SettingsDocument)getTypeLoader().parse(inputStream, SettingsDocument.type, xmlOptions);
        }
        
        public static SettingsDocument parse(final Reader reader) throws XmlException, IOException {
            return (SettingsDocument)getTypeLoader().parse(reader, SettingsDocument.type, (XmlOptions)null);
        }
        
        public static SettingsDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SettingsDocument)getTypeLoader().parse(reader, SettingsDocument.type, xmlOptions);
        }
        
        public static SettingsDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (SettingsDocument)getTypeLoader().parse(xmlStreamReader, SettingsDocument.type, (XmlOptions)null);
        }
        
        public static SettingsDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (SettingsDocument)getTypeLoader().parse(xmlStreamReader, SettingsDocument.type, xmlOptions);
        }
        
        public static SettingsDocument parse(final Node node) throws XmlException {
            return (SettingsDocument)getTypeLoader().parse(node, SettingsDocument.type, (XmlOptions)null);
        }
        
        public static SettingsDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (SettingsDocument)getTypeLoader().parse(node, SettingsDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static SettingsDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (SettingsDocument)getTypeLoader().parse(xmlInputStream, SettingsDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SettingsDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (SettingsDocument)getTypeLoader().parse(xmlInputStream, SettingsDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SettingsDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SettingsDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
