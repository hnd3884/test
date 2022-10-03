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

public interface CTEmpty extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTEmpty.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctempty3fa5type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTEmpty.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTEmpty newInstance() {
            return (CTEmpty)getTypeLoader().newInstance(CTEmpty.type, (XmlOptions)null);
        }
        
        public static CTEmpty newInstance(final XmlOptions xmlOptions) {
            return (CTEmpty)getTypeLoader().newInstance(CTEmpty.type, xmlOptions);
        }
        
        public static CTEmpty parse(final String s) throws XmlException {
            return (CTEmpty)getTypeLoader().parse(s, CTEmpty.type, (XmlOptions)null);
        }
        
        public static CTEmpty parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTEmpty)getTypeLoader().parse(s, CTEmpty.type, xmlOptions);
        }
        
        public static CTEmpty parse(final File file) throws XmlException, IOException {
            return (CTEmpty)getTypeLoader().parse(file, CTEmpty.type, (XmlOptions)null);
        }
        
        public static CTEmpty parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmpty)getTypeLoader().parse(file, CTEmpty.type, xmlOptions);
        }
        
        public static CTEmpty parse(final URL url) throws XmlException, IOException {
            return (CTEmpty)getTypeLoader().parse(url, CTEmpty.type, (XmlOptions)null);
        }
        
        public static CTEmpty parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmpty)getTypeLoader().parse(url, CTEmpty.type, xmlOptions);
        }
        
        public static CTEmpty parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTEmpty)getTypeLoader().parse(inputStream, CTEmpty.type, (XmlOptions)null);
        }
        
        public static CTEmpty parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmpty)getTypeLoader().parse(inputStream, CTEmpty.type, xmlOptions);
        }
        
        public static CTEmpty parse(final Reader reader) throws XmlException, IOException {
            return (CTEmpty)getTypeLoader().parse(reader, CTEmpty.type, (XmlOptions)null);
        }
        
        public static CTEmpty parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmpty)getTypeLoader().parse(reader, CTEmpty.type, xmlOptions);
        }
        
        public static CTEmpty parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTEmpty)getTypeLoader().parse(xmlStreamReader, CTEmpty.type, (XmlOptions)null);
        }
        
        public static CTEmpty parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTEmpty)getTypeLoader().parse(xmlStreamReader, CTEmpty.type, xmlOptions);
        }
        
        public static CTEmpty parse(final Node node) throws XmlException {
            return (CTEmpty)getTypeLoader().parse(node, CTEmpty.type, (XmlOptions)null);
        }
        
        public static CTEmpty parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTEmpty)getTypeLoader().parse(node, CTEmpty.type, xmlOptions);
        }
        
        @Deprecated
        public static CTEmpty parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTEmpty)getTypeLoader().parse(xmlInputStream, CTEmpty.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTEmpty parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTEmpty)getTypeLoader().parse(xmlInputStream, CTEmpty.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEmpty.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEmpty.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
