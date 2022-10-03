package org.openxmlformats.schemas.drawingml.x2006.chart;

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
import org.apache.xmlbeans.XmlUnsignedShort;

public interface STGapAmount extends XmlUnsignedShort
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STGapAmount.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stgapamount99a8type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STGapAmount newValue(final Object o) {
            return (STGapAmount)STGapAmount.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STGapAmount.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STGapAmount newInstance() {
            return (STGapAmount)getTypeLoader().newInstance(STGapAmount.type, (XmlOptions)null);
        }
        
        public static STGapAmount newInstance(final XmlOptions xmlOptions) {
            return (STGapAmount)getTypeLoader().newInstance(STGapAmount.type, xmlOptions);
        }
        
        public static STGapAmount parse(final String s) throws XmlException {
            return (STGapAmount)getTypeLoader().parse(s, STGapAmount.type, (XmlOptions)null);
        }
        
        public static STGapAmount parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STGapAmount)getTypeLoader().parse(s, STGapAmount.type, xmlOptions);
        }
        
        public static STGapAmount parse(final File file) throws XmlException, IOException {
            return (STGapAmount)getTypeLoader().parse(file, STGapAmount.type, (XmlOptions)null);
        }
        
        public static STGapAmount parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STGapAmount)getTypeLoader().parse(file, STGapAmount.type, xmlOptions);
        }
        
        public static STGapAmount parse(final URL url) throws XmlException, IOException {
            return (STGapAmount)getTypeLoader().parse(url, STGapAmount.type, (XmlOptions)null);
        }
        
        public static STGapAmount parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STGapAmount)getTypeLoader().parse(url, STGapAmount.type, xmlOptions);
        }
        
        public static STGapAmount parse(final InputStream inputStream) throws XmlException, IOException {
            return (STGapAmount)getTypeLoader().parse(inputStream, STGapAmount.type, (XmlOptions)null);
        }
        
        public static STGapAmount parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STGapAmount)getTypeLoader().parse(inputStream, STGapAmount.type, xmlOptions);
        }
        
        public static STGapAmount parse(final Reader reader) throws XmlException, IOException {
            return (STGapAmount)getTypeLoader().parse(reader, STGapAmount.type, (XmlOptions)null);
        }
        
        public static STGapAmount parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STGapAmount)getTypeLoader().parse(reader, STGapAmount.type, xmlOptions);
        }
        
        public static STGapAmount parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STGapAmount)getTypeLoader().parse(xmlStreamReader, STGapAmount.type, (XmlOptions)null);
        }
        
        public static STGapAmount parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STGapAmount)getTypeLoader().parse(xmlStreamReader, STGapAmount.type, xmlOptions);
        }
        
        public static STGapAmount parse(final Node node) throws XmlException {
            return (STGapAmount)getTypeLoader().parse(node, STGapAmount.type, (XmlOptions)null);
        }
        
        public static STGapAmount parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STGapAmount)getTypeLoader().parse(node, STGapAmount.type, xmlOptions);
        }
        
        @Deprecated
        public static STGapAmount parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STGapAmount)getTypeLoader().parse(xmlInputStream, STGapAmount.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STGapAmount parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STGapAmount)getTypeLoader().parse(xmlInputStream, STGapAmount.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STGapAmount.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STGapAmount.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
