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

public interface STFillId extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STFillId.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stfillida097type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STFillId newValue(final Object o) {
            return (STFillId)STFillId.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STFillId.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STFillId newInstance() {
            return (STFillId)getTypeLoader().newInstance(STFillId.type, (XmlOptions)null);
        }
        
        public static STFillId newInstance(final XmlOptions xmlOptions) {
            return (STFillId)getTypeLoader().newInstance(STFillId.type, xmlOptions);
        }
        
        public static STFillId parse(final String s) throws XmlException {
            return (STFillId)getTypeLoader().parse(s, STFillId.type, (XmlOptions)null);
        }
        
        public static STFillId parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STFillId)getTypeLoader().parse(s, STFillId.type, xmlOptions);
        }
        
        public static STFillId parse(final File file) throws XmlException, IOException {
            return (STFillId)getTypeLoader().parse(file, STFillId.type, (XmlOptions)null);
        }
        
        public static STFillId parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFillId)getTypeLoader().parse(file, STFillId.type, xmlOptions);
        }
        
        public static STFillId parse(final URL url) throws XmlException, IOException {
            return (STFillId)getTypeLoader().parse(url, STFillId.type, (XmlOptions)null);
        }
        
        public static STFillId parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFillId)getTypeLoader().parse(url, STFillId.type, xmlOptions);
        }
        
        public static STFillId parse(final InputStream inputStream) throws XmlException, IOException {
            return (STFillId)getTypeLoader().parse(inputStream, STFillId.type, (XmlOptions)null);
        }
        
        public static STFillId parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFillId)getTypeLoader().parse(inputStream, STFillId.type, xmlOptions);
        }
        
        public static STFillId parse(final Reader reader) throws XmlException, IOException {
            return (STFillId)getTypeLoader().parse(reader, STFillId.type, (XmlOptions)null);
        }
        
        public static STFillId parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFillId)getTypeLoader().parse(reader, STFillId.type, xmlOptions);
        }
        
        public static STFillId parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STFillId)getTypeLoader().parse(xmlStreamReader, STFillId.type, (XmlOptions)null);
        }
        
        public static STFillId parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STFillId)getTypeLoader().parse(xmlStreamReader, STFillId.type, xmlOptions);
        }
        
        public static STFillId parse(final Node node) throws XmlException {
            return (STFillId)getTypeLoader().parse(node, STFillId.type, (XmlOptions)null);
        }
        
        public static STFillId parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STFillId)getTypeLoader().parse(node, STFillId.type, xmlOptions);
        }
        
        @Deprecated
        public static STFillId parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STFillId)getTypeLoader().parse(xmlInputStream, STFillId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STFillId parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STFillId)getTypeLoader().parse(xmlInputStream, STFillId.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFillId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFillId.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
