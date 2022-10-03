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
import org.apache.xmlbeans.XmlUnsignedInt;

public interface STSlideMasterId extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STSlideMasterId.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stslidemasteridfe71type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STSlideMasterId newValue(final Object o) {
            return (STSlideMasterId)STSlideMasterId.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STSlideMasterId.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STSlideMasterId newInstance() {
            return (STSlideMasterId)getTypeLoader().newInstance(STSlideMasterId.type, (XmlOptions)null);
        }
        
        public static STSlideMasterId newInstance(final XmlOptions xmlOptions) {
            return (STSlideMasterId)getTypeLoader().newInstance(STSlideMasterId.type, xmlOptions);
        }
        
        public static STSlideMasterId parse(final String s) throws XmlException {
            return (STSlideMasterId)getTypeLoader().parse(s, STSlideMasterId.type, (XmlOptions)null);
        }
        
        public static STSlideMasterId parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STSlideMasterId)getTypeLoader().parse(s, STSlideMasterId.type, xmlOptions);
        }
        
        public static STSlideMasterId parse(final File file) throws XmlException, IOException {
            return (STSlideMasterId)getTypeLoader().parse(file, STSlideMasterId.type, (XmlOptions)null);
        }
        
        public static STSlideMasterId parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSlideMasterId)getTypeLoader().parse(file, STSlideMasterId.type, xmlOptions);
        }
        
        public static STSlideMasterId parse(final URL url) throws XmlException, IOException {
            return (STSlideMasterId)getTypeLoader().parse(url, STSlideMasterId.type, (XmlOptions)null);
        }
        
        public static STSlideMasterId parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSlideMasterId)getTypeLoader().parse(url, STSlideMasterId.type, xmlOptions);
        }
        
        public static STSlideMasterId parse(final InputStream inputStream) throws XmlException, IOException {
            return (STSlideMasterId)getTypeLoader().parse(inputStream, STSlideMasterId.type, (XmlOptions)null);
        }
        
        public static STSlideMasterId parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSlideMasterId)getTypeLoader().parse(inputStream, STSlideMasterId.type, xmlOptions);
        }
        
        public static STSlideMasterId parse(final Reader reader) throws XmlException, IOException {
            return (STSlideMasterId)getTypeLoader().parse(reader, STSlideMasterId.type, (XmlOptions)null);
        }
        
        public static STSlideMasterId parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSlideMasterId)getTypeLoader().parse(reader, STSlideMasterId.type, xmlOptions);
        }
        
        public static STSlideMasterId parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STSlideMasterId)getTypeLoader().parse(xmlStreamReader, STSlideMasterId.type, (XmlOptions)null);
        }
        
        public static STSlideMasterId parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STSlideMasterId)getTypeLoader().parse(xmlStreamReader, STSlideMasterId.type, xmlOptions);
        }
        
        public static STSlideMasterId parse(final Node node) throws XmlException {
            return (STSlideMasterId)getTypeLoader().parse(node, STSlideMasterId.type, (XmlOptions)null);
        }
        
        public static STSlideMasterId parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STSlideMasterId)getTypeLoader().parse(node, STSlideMasterId.type, xmlOptions);
        }
        
        @Deprecated
        public static STSlideMasterId parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STSlideMasterId)getTypeLoader().parse(xmlInputStream, STSlideMasterId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STSlideMasterId parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STSlideMasterId)getTypeLoader().parse(xmlInputStream, STSlideMasterId.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSlideMasterId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSlideMasterId.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
