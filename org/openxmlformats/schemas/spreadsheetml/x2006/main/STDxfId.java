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

public interface STDxfId extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STDxfId.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stdxfid9fdctype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STDxfId newValue(final Object o) {
            return (STDxfId)STDxfId.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STDxfId.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STDxfId newInstance() {
            return (STDxfId)getTypeLoader().newInstance(STDxfId.type, (XmlOptions)null);
        }
        
        public static STDxfId newInstance(final XmlOptions xmlOptions) {
            return (STDxfId)getTypeLoader().newInstance(STDxfId.type, xmlOptions);
        }
        
        public static STDxfId parse(final String s) throws XmlException {
            return (STDxfId)getTypeLoader().parse(s, STDxfId.type, (XmlOptions)null);
        }
        
        public static STDxfId parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STDxfId)getTypeLoader().parse(s, STDxfId.type, xmlOptions);
        }
        
        public static STDxfId parse(final File file) throws XmlException, IOException {
            return (STDxfId)getTypeLoader().parse(file, STDxfId.type, (XmlOptions)null);
        }
        
        public static STDxfId parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDxfId)getTypeLoader().parse(file, STDxfId.type, xmlOptions);
        }
        
        public static STDxfId parse(final URL url) throws XmlException, IOException {
            return (STDxfId)getTypeLoader().parse(url, STDxfId.type, (XmlOptions)null);
        }
        
        public static STDxfId parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDxfId)getTypeLoader().parse(url, STDxfId.type, xmlOptions);
        }
        
        public static STDxfId parse(final InputStream inputStream) throws XmlException, IOException {
            return (STDxfId)getTypeLoader().parse(inputStream, STDxfId.type, (XmlOptions)null);
        }
        
        public static STDxfId parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDxfId)getTypeLoader().parse(inputStream, STDxfId.type, xmlOptions);
        }
        
        public static STDxfId parse(final Reader reader) throws XmlException, IOException {
            return (STDxfId)getTypeLoader().parse(reader, STDxfId.type, (XmlOptions)null);
        }
        
        public static STDxfId parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDxfId)getTypeLoader().parse(reader, STDxfId.type, xmlOptions);
        }
        
        public static STDxfId parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STDxfId)getTypeLoader().parse(xmlStreamReader, STDxfId.type, (XmlOptions)null);
        }
        
        public static STDxfId parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STDxfId)getTypeLoader().parse(xmlStreamReader, STDxfId.type, xmlOptions);
        }
        
        public static STDxfId parse(final Node node) throws XmlException {
            return (STDxfId)getTypeLoader().parse(node, STDxfId.type, (XmlOptions)null);
        }
        
        public static STDxfId parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STDxfId)getTypeLoader().parse(node, STDxfId.type, xmlOptions);
        }
        
        @Deprecated
        public static STDxfId parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STDxfId)getTypeLoader().parse(xmlInputStream, STDxfId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STDxfId parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STDxfId)getTypeLoader().parse(xmlInputStream, STDxfId.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDxfId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDxfId.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
