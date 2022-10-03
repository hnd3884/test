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

public interface CTEmptyElement extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTEmptyElement.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctemptyelement05catype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTEmptyElement.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTEmptyElement newInstance() {
            return (CTEmptyElement)getTypeLoader().newInstance(CTEmptyElement.type, (XmlOptions)null);
        }
        
        public static CTEmptyElement newInstance(final XmlOptions xmlOptions) {
            return (CTEmptyElement)getTypeLoader().newInstance(CTEmptyElement.type, xmlOptions);
        }
        
        public static CTEmptyElement parse(final String s) throws XmlException {
            return (CTEmptyElement)getTypeLoader().parse(s, CTEmptyElement.type, (XmlOptions)null);
        }
        
        public static CTEmptyElement parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTEmptyElement)getTypeLoader().parse(s, CTEmptyElement.type, xmlOptions);
        }
        
        public static CTEmptyElement parse(final File file) throws XmlException, IOException {
            return (CTEmptyElement)getTypeLoader().parse(file, CTEmptyElement.type, (XmlOptions)null);
        }
        
        public static CTEmptyElement parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmptyElement)getTypeLoader().parse(file, CTEmptyElement.type, xmlOptions);
        }
        
        public static CTEmptyElement parse(final URL url) throws XmlException, IOException {
            return (CTEmptyElement)getTypeLoader().parse(url, CTEmptyElement.type, (XmlOptions)null);
        }
        
        public static CTEmptyElement parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmptyElement)getTypeLoader().parse(url, CTEmptyElement.type, xmlOptions);
        }
        
        public static CTEmptyElement parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTEmptyElement)getTypeLoader().parse(inputStream, CTEmptyElement.type, (XmlOptions)null);
        }
        
        public static CTEmptyElement parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmptyElement)getTypeLoader().parse(inputStream, CTEmptyElement.type, xmlOptions);
        }
        
        public static CTEmptyElement parse(final Reader reader) throws XmlException, IOException {
            return (CTEmptyElement)getTypeLoader().parse(reader, CTEmptyElement.type, (XmlOptions)null);
        }
        
        public static CTEmptyElement parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmptyElement)getTypeLoader().parse(reader, CTEmptyElement.type, xmlOptions);
        }
        
        public static CTEmptyElement parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTEmptyElement)getTypeLoader().parse(xmlStreamReader, CTEmptyElement.type, (XmlOptions)null);
        }
        
        public static CTEmptyElement parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTEmptyElement)getTypeLoader().parse(xmlStreamReader, CTEmptyElement.type, xmlOptions);
        }
        
        public static CTEmptyElement parse(final Node node) throws XmlException {
            return (CTEmptyElement)getTypeLoader().parse(node, CTEmptyElement.type, (XmlOptions)null);
        }
        
        public static CTEmptyElement parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTEmptyElement)getTypeLoader().parse(node, CTEmptyElement.type, xmlOptions);
        }
        
        @Deprecated
        public static CTEmptyElement parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTEmptyElement)getTypeLoader().parse(xmlInputStream, CTEmptyElement.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTEmptyElement parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTEmptyElement)getTypeLoader().parse(xmlInputStream, CTEmptyElement.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEmptyElement.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEmptyElement.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
