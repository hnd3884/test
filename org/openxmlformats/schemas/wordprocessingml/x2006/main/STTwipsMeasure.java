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

public interface STTwipsMeasure extends STUnsignedDecimalNumber
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTwipsMeasure.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttwipsmeasure1e23type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTwipsMeasure newValue(final Object o) {
            return (STTwipsMeasure)STTwipsMeasure.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTwipsMeasure.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTwipsMeasure newInstance() {
            return (STTwipsMeasure)getTypeLoader().newInstance(STTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STTwipsMeasure newInstance(final XmlOptions xmlOptions) {
            return (STTwipsMeasure)getTypeLoader().newInstance(STTwipsMeasure.type, xmlOptions);
        }
        
        public static STTwipsMeasure parse(final String s) throws XmlException {
            return (STTwipsMeasure)getTypeLoader().parse(s, STTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STTwipsMeasure parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTwipsMeasure)getTypeLoader().parse(s, STTwipsMeasure.type, xmlOptions);
        }
        
        public static STTwipsMeasure parse(final File file) throws XmlException, IOException {
            return (STTwipsMeasure)getTypeLoader().parse(file, STTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STTwipsMeasure parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTwipsMeasure)getTypeLoader().parse(file, STTwipsMeasure.type, xmlOptions);
        }
        
        public static STTwipsMeasure parse(final URL url) throws XmlException, IOException {
            return (STTwipsMeasure)getTypeLoader().parse(url, STTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STTwipsMeasure parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTwipsMeasure)getTypeLoader().parse(url, STTwipsMeasure.type, xmlOptions);
        }
        
        public static STTwipsMeasure parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTwipsMeasure)getTypeLoader().parse(inputStream, STTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STTwipsMeasure parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTwipsMeasure)getTypeLoader().parse(inputStream, STTwipsMeasure.type, xmlOptions);
        }
        
        public static STTwipsMeasure parse(final Reader reader) throws XmlException, IOException {
            return (STTwipsMeasure)getTypeLoader().parse(reader, STTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STTwipsMeasure parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTwipsMeasure)getTypeLoader().parse(reader, STTwipsMeasure.type, xmlOptions);
        }
        
        public static STTwipsMeasure parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTwipsMeasure)getTypeLoader().parse(xmlStreamReader, STTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STTwipsMeasure parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTwipsMeasure)getTypeLoader().parse(xmlStreamReader, STTwipsMeasure.type, xmlOptions);
        }
        
        public static STTwipsMeasure parse(final Node node) throws XmlException {
            return (STTwipsMeasure)getTypeLoader().parse(node, STTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STTwipsMeasure parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTwipsMeasure)getTypeLoader().parse(node, STTwipsMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static STTwipsMeasure parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTwipsMeasure)getTypeLoader().parse(xmlInputStream, STTwipsMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTwipsMeasure parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTwipsMeasure)getTypeLoader().parse(xmlInputStream, STTwipsMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTwipsMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTwipsMeasure.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
