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

public interface STFontId extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STFontId.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stfontid9d63type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STFontId newValue(final Object o) {
            return (STFontId)STFontId.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STFontId.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STFontId newInstance() {
            return (STFontId)getTypeLoader().newInstance(STFontId.type, (XmlOptions)null);
        }
        
        public static STFontId newInstance(final XmlOptions xmlOptions) {
            return (STFontId)getTypeLoader().newInstance(STFontId.type, xmlOptions);
        }
        
        public static STFontId parse(final String s) throws XmlException {
            return (STFontId)getTypeLoader().parse(s, STFontId.type, (XmlOptions)null);
        }
        
        public static STFontId parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STFontId)getTypeLoader().parse(s, STFontId.type, xmlOptions);
        }
        
        public static STFontId parse(final File file) throws XmlException, IOException {
            return (STFontId)getTypeLoader().parse(file, STFontId.type, (XmlOptions)null);
        }
        
        public static STFontId parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFontId)getTypeLoader().parse(file, STFontId.type, xmlOptions);
        }
        
        public static STFontId parse(final URL url) throws XmlException, IOException {
            return (STFontId)getTypeLoader().parse(url, STFontId.type, (XmlOptions)null);
        }
        
        public static STFontId parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFontId)getTypeLoader().parse(url, STFontId.type, xmlOptions);
        }
        
        public static STFontId parse(final InputStream inputStream) throws XmlException, IOException {
            return (STFontId)getTypeLoader().parse(inputStream, STFontId.type, (XmlOptions)null);
        }
        
        public static STFontId parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFontId)getTypeLoader().parse(inputStream, STFontId.type, xmlOptions);
        }
        
        public static STFontId parse(final Reader reader) throws XmlException, IOException {
            return (STFontId)getTypeLoader().parse(reader, STFontId.type, (XmlOptions)null);
        }
        
        public static STFontId parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFontId)getTypeLoader().parse(reader, STFontId.type, xmlOptions);
        }
        
        public static STFontId parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STFontId)getTypeLoader().parse(xmlStreamReader, STFontId.type, (XmlOptions)null);
        }
        
        public static STFontId parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STFontId)getTypeLoader().parse(xmlStreamReader, STFontId.type, xmlOptions);
        }
        
        public static STFontId parse(final Node node) throws XmlException {
            return (STFontId)getTypeLoader().parse(node, STFontId.type, (XmlOptions)null);
        }
        
        public static STFontId parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STFontId)getTypeLoader().parse(node, STFontId.type, xmlOptions);
        }
        
        @Deprecated
        public static STFontId parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STFontId)getTypeLoader().parse(xmlInputStream, STFontId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STFontId parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STFontId)getTypeLoader().parse(xmlInputStream, STFontId.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFontId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFontId.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
