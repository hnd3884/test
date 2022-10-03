package org.openxmlformats.schemas.presentationml.x2006.main;

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

public interface SldDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SldDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sld1b98doctype");
    
    CTSlide getSld();
    
    void setSld(final CTSlide p0);
    
    CTSlide addNewSld();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(SldDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static SldDocument newInstance() {
            return (SldDocument)getTypeLoader().newInstance(SldDocument.type, (XmlOptions)null);
        }
        
        public static SldDocument newInstance(final XmlOptions xmlOptions) {
            return (SldDocument)getTypeLoader().newInstance(SldDocument.type, xmlOptions);
        }
        
        public static SldDocument parse(final String s) throws XmlException {
            return (SldDocument)getTypeLoader().parse(s, SldDocument.type, (XmlOptions)null);
        }
        
        public static SldDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (SldDocument)getTypeLoader().parse(s, SldDocument.type, xmlOptions);
        }
        
        public static SldDocument parse(final File file) throws XmlException, IOException {
            return (SldDocument)getTypeLoader().parse(file, SldDocument.type, (XmlOptions)null);
        }
        
        public static SldDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SldDocument)getTypeLoader().parse(file, SldDocument.type, xmlOptions);
        }
        
        public static SldDocument parse(final URL url) throws XmlException, IOException {
            return (SldDocument)getTypeLoader().parse(url, SldDocument.type, (XmlOptions)null);
        }
        
        public static SldDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SldDocument)getTypeLoader().parse(url, SldDocument.type, xmlOptions);
        }
        
        public static SldDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (SldDocument)getTypeLoader().parse(inputStream, SldDocument.type, (XmlOptions)null);
        }
        
        public static SldDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SldDocument)getTypeLoader().parse(inputStream, SldDocument.type, xmlOptions);
        }
        
        public static SldDocument parse(final Reader reader) throws XmlException, IOException {
            return (SldDocument)getTypeLoader().parse(reader, SldDocument.type, (XmlOptions)null);
        }
        
        public static SldDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SldDocument)getTypeLoader().parse(reader, SldDocument.type, xmlOptions);
        }
        
        public static SldDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (SldDocument)getTypeLoader().parse(xmlStreamReader, SldDocument.type, (XmlOptions)null);
        }
        
        public static SldDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (SldDocument)getTypeLoader().parse(xmlStreamReader, SldDocument.type, xmlOptions);
        }
        
        public static SldDocument parse(final Node node) throws XmlException {
            return (SldDocument)getTypeLoader().parse(node, SldDocument.type, (XmlOptions)null);
        }
        
        public static SldDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (SldDocument)getTypeLoader().parse(node, SldDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static SldDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (SldDocument)getTypeLoader().parse(xmlInputStream, SldDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SldDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (SldDocument)getTypeLoader().parse(xmlInputStream, SldDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SldDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SldDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
