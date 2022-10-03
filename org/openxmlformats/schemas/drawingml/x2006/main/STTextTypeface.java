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
import org.apache.xmlbeans.XmlString;

public interface STTextTypeface extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextTypeface.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttexttypefacea80ftype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextTypeface newValue(final Object o) {
            return (STTextTypeface)STTextTypeface.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextTypeface.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextTypeface newInstance() {
            return (STTextTypeface)getTypeLoader().newInstance(STTextTypeface.type, (XmlOptions)null);
        }
        
        public static STTextTypeface newInstance(final XmlOptions xmlOptions) {
            return (STTextTypeface)getTypeLoader().newInstance(STTextTypeface.type, xmlOptions);
        }
        
        public static STTextTypeface parse(final String s) throws XmlException {
            return (STTextTypeface)getTypeLoader().parse(s, STTextTypeface.type, (XmlOptions)null);
        }
        
        public static STTextTypeface parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextTypeface)getTypeLoader().parse(s, STTextTypeface.type, xmlOptions);
        }
        
        public static STTextTypeface parse(final File file) throws XmlException, IOException {
            return (STTextTypeface)getTypeLoader().parse(file, STTextTypeface.type, (XmlOptions)null);
        }
        
        public static STTextTypeface parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextTypeface)getTypeLoader().parse(file, STTextTypeface.type, xmlOptions);
        }
        
        public static STTextTypeface parse(final URL url) throws XmlException, IOException {
            return (STTextTypeface)getTypeLoader().parse(url, STTextTypeface.type, (XmlOptions)null);
        }
        
        public static STTextTypeface parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextTypeface)getTypeLoader().parse(url, STTextTypeface.type, xmlOptions);
        }
        
        public static STTextTypeface parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextTypeface)getTypeLoader().parse(inputStream, STTextTypeface.type, (XmlOptions)null);
        }
        
        public static STTextTypeface parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextTypeface)getTypeLoader().parse(inputStream, STTextTypeface.type, xmlOptions);
        }
        
        public static STTextTypeface parse(final Reader reader) throws XmlException, IOException {
            return (STTextTypeface)getTypeLoader().parse(reader, STTextTypeface.type, (XmlOptions)null);
        }
        
        public static STTextTypeface parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextTypeface)getTypeLoader().parse(reader, STTextTypeface.type, xmlOptions);
        }
        
        public static STTextTypeface parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextTypeface)getTypeLoader().parse(xmlStreamReader, STTextTypeface.type, (XmlOptions)null);
        }
        
        public static STTextTypeface parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextTypeface)getTypeLoader().parse(xmlStreamReader, STTextTypeface.type, xmlOptions);
        }
        
        public static STTextTypeface parse(final Node node) throws XmlException {
            return (STTextTypeface)getTypeLoader().parse(node, STTextTypeface.type, (XmlOptions)null);
        }
        
        public static STTextTypeface parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextTypeface)getTypeLoader().parse(node, STTextTypeface.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextTypeface parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextTypeface)getTypeLoader().parse(xmlInputStream, STTextTypeface.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextTypeface parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextTypeface)getTypeLoader().parse(xmlInputStream, STTextTypeface.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextTypeface.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextTypeface.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
