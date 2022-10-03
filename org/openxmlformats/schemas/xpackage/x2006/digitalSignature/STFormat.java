package org.openxmlformats.schemas.xpackage.x2006.digitalSignature;

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
import org.apache.xmlbeans.XmlString;

public interface STFormat extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STFormat.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("stformat98d1type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STFormat newValue(final Object o) {
            return (STFormat)STFormat.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STFormat.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STFormat newInstance() {
            return (STFormat)getTypeLoader().newInstance(STFormat.type, (XmlOptions)null);
        }
        
        public static STFormat newInstance(final XmlOptions xmlOptions) {
            return (STFormat)getTypeLoader().newInstance(STFormat.type, xmlOptions);
        }
        
        public static STFormat parse(final String s) throws XmlException {
            return (STFormat)getTypeLoader().parse(s, STFormat.type, (XmlOptions)null);
        }
        
        public static STFormat parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STFormat)getTypeLoader().parse(s, STFormat.type, xmlOptions);
        }
        
        public static STFormat parse(final File file) throws XmlException, IOException {
            return (STFormat)getTypeLoader().parse(file, STFormat.type, (XmlOptions)null);
        }
        
        public static STFormat parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFormat)getTypeLoader().parse(file, STFormat.type, xmlOptions);
        }
        
        public static STFormat parse(final URL url) throws XmlException, IOException {
            return (STFormat)getTypeLoader().parse(url, STFormat.type, (XmlOptions)null);
        }
        
        public static STFormat parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFormat)getTypeLoader().parse(url, STFormat.type, xmlOptions);
        }
        
        public static STFormat parse(final InputStream inputStream) throws XmlException, IOException {
            return (STFormat)getTypeLoader().parse(inputStream, STFormat.type, (XmlOptions)null);
        }
        
        public static STFormat parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFormat)getTypeLoader().parse(inputStream, STFormat.type, xmlOptions);
        }
        
        public static STFormat parse(final Reader reader) throws XmlException, IOException {
            return (STFormat)getTypeLoader().parse(reader, STFormat.type, (XmlOptions)null);
        }
        
        public static STFormat parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFormat)getTypeLoader().parse(reader, STFormat.type, xmlOptions);
        }
        
        public static STFormat parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STFormat)getTypeLoader().parse(xmlStreamReader, STFormat.type, (XmlOptions)null);
        }
        
        public static STFormat parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STFormat)getTypeLoader().parse(xmlStreamReader, STFormat.type, xmlOptions);
        }
        
        public static STFormat parse(final Node node) throws XmlException {
            return (STFormat)getTypeLoader().parse(node, STFormat.type, (XmlOptions)null);
        }
        
        public static STFormat parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STFormat)getTypeLoader().parse(node, STFormat.type, xmlOptions);
        }
        
        @Deprecated
        public static STFormat parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STFormat)getTypeLoader().parse(xmlInputStream, STFormat.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STFormat parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STFormat)getTypeLoader().parse(xmlInputStream, STFormat.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFormat.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFormat.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
