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
import org.apache.xmlbeans.XmlInteger;

public interface STSignedTwipsMeasure extends XmlInteger
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STSignedTwipsMeasure.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stsignedtwipsmeasureb227type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STSignedTwipsMeasure newValue(final Object o) {
            return (STSignedTwipsMeasure)STSignedTwipsMeasure.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STSignedTwipsMeasure.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STSignedTwipsMeasure newInstance() {
            return (STSignedTwipsMeasure)getTypeLoader().newInstance(STSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedTwipsMeasure newInstance(final XmlOptions xmlOptions) {
            return (STSignedTwipsMeasure)getTypeLoader().newInstance(STSignedTwipsMeasure.type, xmlOptions);
        }
        
        public static STSignedTwipsMeasure parse(final String s) throws XmlException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(s, STSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedTwipsMeasure parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(s, STSignedTwipsMeasure.type, xmlOptions);
        }
        
        public static STSignedTwipsMeasure parse(final File file) throws XmlException, IOException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(file, STSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedTwipsMeasure parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(file, STSignedTwipsMeasure.type, xmlOptions);
        }
        
        public static STSignedTwipsMeasure parse(final URL url) throws XmlException, IOException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(url, STSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedTwipsMeasure parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(url, STSignedTwipsMeasure.type, xmlOptions);
        }
        
        public static STSignedTwipsMeasure parse(final InputStream inputStream) throws XmlException, IOException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(inputStream, STSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedTwipsMeasure parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(inputStream, STSignedTwipsMeasure.type, xmlOptions);
        }
        
        public static STSignedTwipsMeasure parse(final Reader reader) throws XmlException, IOException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(reader, STSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedTwipsMeasure parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(reader, STSignedTwipsMeasure.type, xmlOptions);
        }
        
        public static STSignedTwipsMeasure parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(xmlStreamReader, STSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedTwipsMeasure parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(xmlStreamReader, STSignedTwipsMeasure.type, xmlOptions);
        }
        
        public static STSignedTwipsMeasure parse(final Node node) throws XmlException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(node, STSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedTwipsMeasure parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(node, STSignedTwipsMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static STSignedTwipsMeasure parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(xmlInputStream, STSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STSignedTwipsMeasure parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STSignedTwipsMeasure)getTypeLoader().parse(xmlInputStream, STSignedTwipsMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSignedTwipsMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSignedTwipsMeasure.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
