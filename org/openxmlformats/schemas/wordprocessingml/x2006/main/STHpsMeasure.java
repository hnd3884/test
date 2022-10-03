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

public interface STHpsMeasure extends STUnsignedDecimalNumber
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STHpsMeasure.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sthpsmeasurec985type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STHpsMeasure newValue(final Object o) {
            return (STHpsMeasure)STHpsMeasure.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STHpsMeasure.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STHpsMeasure newInstance() {
            return (STHpsMeasure)getTypeLoader().newInstance(STHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STHpsMeasure newInstance(final XmlOptions xmlOptions) {
            return (STHpsMeasure)getTypeLoader().newInstance(STHpsMeasure.type, xmlOptions);
        }
        
        public static STHpsMeasure parse(final String s) throws XmlException {
            return (STHpsMeasure)getTypeLoader().parse(s, STHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STHpsMeasure parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STHpsMeasure)getTypeLoader().parse(s, STHpsMeasure.type, xmlOptions);
        }
        
        public static STHpsMeasure parse(final File file) throws XmlException, IOException {
            return (STHpsMeasure)getTypeLoader().parse(file, STHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STHpsMeasure parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHpsMeasure)getTypeLoader().parse(file, STHpsMeasure.type, xmlOptions);
        }
        
        public static STHpsMeasure parse(final URL url) throws XmlException, IOException {
            return (STHpsMeasure)getTypeLoader().parse(url, STHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STHpsMeasure parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHpsMeasure)getTypeLoader().parse(url, STHpsMeasure.type, xmlOptions);
        }
        
        public static STHpsMeasure parse(final InputStream inputStream) throws XmlException, IOException {
            return (STHpsMeasure)getTypeLoader().parse(inputStream, STHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STHpsMeasure parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHpsMeasure)getTypeLoader().parse(inputStream, STHpsMeasure.type, xmlOptions);
        }
        
        public static STHpsMeasure parse(final Reader reader) throws XmlException, IOException {
            return (STHpsMeasure)getTypeLoader().parse(reader, STHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STHpsMeasure parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHpsMeasure)getTypeLoader().parse(reader, STHpsMeasure.type, xmlOptions);
        }
        
        public static STHpsMeasure parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STHpsMeasure)getTypeLoader().parse(xmlStreamReader, STHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STHpsMeasure parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STHpsMeasure)getTypeLoader().parse(xmlStreamReader, STHpsMeasure.type, xmlOptions);
        }
        
        public static STHpsMeasure parse(final Node node) throws XmlException {
            return (STHpsMeasure)getTypeLoader().parse(node, STHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STHpsMeasure parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STHpsMeasure)getTypeLoader().parse(node, STHpsMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static STHpsMeasure parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STHpsMeasure)getTypeLoader().parse(xmlInputStream, STHpsMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STHpsMeasure parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STHpsMeasure)getTypeLoader().parse(xmlInputStream, STHpsMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHpsMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHpsMeasure.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
