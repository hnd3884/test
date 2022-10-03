package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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

public interface STBorderId extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STBorderId.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stborderid1a80type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STBorderId newValue(final Object o) {
            return (STBorderId)STBorderId.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STBorderId.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STBorderId newInstance() {
            return (STBorderId)getTypeLoader().newInstance(STBorderId.type, (XmlOptions)null);
        }
        
        public static STBorderId newInstance(final XmlOptions xmlOptions) {
            return (STBorderId)getTypeLoader().newInstance(STBorderId.type, xmlOptions);
        }
        
        public static STBorderId parse(final String s) throws XmlException {
            return (STBorderId)getTypeLoader().parse(s, STBorderId.type, (XmlOptions)null);
        }
        
        public static STBorderId parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STBorderId)getTypeLoader().parse(s, STBorderId.type, xmlOptions);
        }
        
        public static STBorderId parse(final File file) throws XmlException, IOException {
            return (STBorderId)getTypeLoader().parse(file, STBorderId.type, (XmlOptions)null);
        }
        
        public static STBorderId parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBorderId)getTypeLoader().parse(file, STBorderId.type, xmlOptions);
        }
        
        public static STBorderId parse(final URL url) throws XmlException, IOException {
            return (STBorderId)getTypeLoader().parse(url, STBorderId.type, (XmlOptions)null);
        }
        
        public static STBorderId parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBorderId)getTypeLoader().parse(url, STBorderId.type, xmlOptions);
        }
        
        public static STBorderId parse(final InputStream inputStream) throws XmlException, IOException {
            return (STBorderId)getTypeLoader().parse(inputStream, STBorderId.type, (XmlOptions)null);
        }
        
        public static STBorderId parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBorderId)getTypeLoader().parse(inputStream, STBorderId.type, xmlOptions);
        }
        
        public static STBorderId parse(final Reader reader) throws XmlException, IOException {
            return (STBorderId)getTypeLoader().parse(reader, STBorderId.type, (XmlOptions)null);
        }
        
        public static STBorderId parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBorderId)getTypeLoader().parse(reader, STBorderId.type, xmlOptions);
        }
        
        public static STBorderId parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STBorderId)getTypeLoader().parse(xmlStreamReader, STBorderId.type, (XmlOptions)null);
        }
        
        public static STBorderId parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STBorderId)getTypeLoader().parse(xmlStreamReader, STBorderId.type, xmlOptions);
        }
        
        public static STBorderId parse(final Node node) throws XmlException {
            return (STBorderId)getTypeLoader().parse(node, STBorderId.type, (XmlOptions)null);
        }
        
        public static STBorderId parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STBorderId)getTypeLoader().parse(node, STBorderId.type, xmlOptions);
        }
        
        @Deprecated
        public static STBorderId parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STBorderId)getTypeLoader().parse(xmlInputStream, STBorderId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STBorderId parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STBorderId)getTypeLoader().parse(xmlInputStream, STBorderId.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STBorderId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STBorderId.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
