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

public interface ThemeDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ThemeDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("themefd26doctype");
    
    CTOfficeStyleSheet getTheme();
    
    void setTheme(final CTOfficeStyleSheet p0);
    
    CTOfficeStyleSheet addNewTheme();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(ThemeDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static ThemeDocument newInstance() {
            return (ThemeDocument)getTypeLoader().newInstance(ThemeDocument.type, (XmlOptions)null);
        }
        
        public static ThemeDocument newInstance(final XmlOptions xmlOptions) {
            return (ThemeDocument)getTypeLoader().newInstance(ThemeDocument.type, xmlOptions);
        }
        
        public static ThemeDocument parse(final String s) throws XmlException {
            return (ThemeDocument)getTypeLoader().parse(s, ThemeDocument.type, (XmlOptions)null);
        }
        
        public static ThemeDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (ThemeDocument)getTypeLoader().parse(s, ThemeDocument.type, xmlOptions);
        }
        
        public static ThemeDocument parse(final File file) throws XmlException, IOException {
            return (ThemeDocument)getTypeLoader().parse(file, ThemeDocument.type, (XmlOptions)null);
        }
        
        public static ThemeDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ThemeDocument)getTypeLoader().parse(file, ThemeDocument.type, xmlOptions);
        }
        
        public static ThemeDocument parse(final URL url) throws XmlException, IOException {
            return (ThemeDocument)getTypeLoader().parse(url, ThemeDocument.type, (XmlOptions)null);
        }
        
        public static ThemeDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ThemeDocument)getTypeLoader().parse(url, ThemeDocument.type, xmlOptions);
        }
        
        public static ThemeDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (ThemeDocument)getTypeLoader().parse(inputStream, ThemeDocument.type, (XmlOptions)null);
        }
        
        public static ThemeDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ThemeDocument)getTypeLoader().parse(inputStream, ThemeDocument.type, xmlOptions);
        }
        
        public static ThemeDocument parse(final Reader reader) throws XmlException, IOException {
            return (ThemeDocument)getTypeLoader().parse(reader, ThemeDocument.type, (XmlOptions)null);
        }
        
        public static ThemeDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ThemeDocument)getTypeLoader().parse(reader, ThemeDocument.type, xmlOptions);
        }
        
        public static ThemeDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (ThemeDocument)getTypeLoader().parse(xmlStreamReader, ThemeDocument.type, (XmlOptions)null);
        }
        
        public static ThemeDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (ThemeDocument)getTypeLoader().parse(xmlStreamReader, ThemeDocument.type, xmlOptions);
        }
        
        public static ThemeDocument parse(final Node node) throws XmlException {
            return (ThemeDocument)getTypeLoader().parse(node, ThemeDocument.type, (XmlOptions)null);
        }
        
        public static ThemeDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (ThemeDocument)getTypeLoader().parse(node, ThemeDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static ThemeDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (ThemeDocument)getTypeLoader().parse(xmlInputStream, ThemeDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static ThemeDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (ThemeDocument)getTypeLoader().parse(xmlInputStream, ThemeDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ThemeDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ThemeDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
