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
import org.apache.xmlbeans.XmlUnsignedInt;

public interface STStyleMatrixColumnIndex extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STStyleMatrixColumnIndex.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ststylematrixcolumnindex1755type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STStyleMatrixColumnIndex newValue(final Object o) {
            return (STStyleMatrixColumnIndex)STStyleMatrixColumnIndex.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STStyleMatrixColumnIndex.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STStyleMatrixColumnIndex newInstance() {
            return (STStyleMatrixColumnIndex)getTypeLoader().newInstance(STStyleMatrixColumnIndex.type, (XmlOptions)null);
        }
        
        public static STStyleMatrixColumnIndex newInstance(final XmlOptions xmlOptions) {
            return (STStyleMatrixColumnIndex)getTypeLoader().newInstance(STStyleMatrixColumnIndex.type, xmlOptions);
        }
        
        public static STStyleMatrixColumnIndex parse(final String s) throws XmlException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(s, STStyleMatrixColumnIndex.type, (XmlOptions)null);
        }
        
        public static STStyleMatrixColumnIndex parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(s, STStyleMatrixColumnIndex.type, xmlOptions);
        }
        
        public static STStyleMatrixColumnIndex parse(final File file) throws XmlException, IOException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(file, STStyleMatrixColumnIndex.type, (XmlOptions)null);
        }
        
        public static STStyleMatrixColumnIndex parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(file, STStyleMatrixColumnIndex.type, xmlOptions);
        }
        
        public static STStyleMatrixColumnIndex parse(final URL url) throws XmlException, IOException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(url, STStyleMatrixColumnIndex.type, (XmlOptions)null);
        }
        
        public static STStyleMatrixColumnIndex parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(url, STStyleMatrixColumnIndex.type, xmlOptions);
        }
        
        public static STStyleMatrixColumnIndex parse(final InputStream inputStream) throws XmlException, IOException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(inputStream, STStyleMatrixColumnIndex.type, (XmlOptions)null);
        }
        
        public static STStyleMatrixColumnIndex parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(inputStream, STStyleMatrixColumnIndex.type, xmlOptions);
        }
        
        public static STStyleMatrixColumnIndex parse(final Reader reader) throws XmlException, IOException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(reader, STStyleMatrixColumnIndex.type, (XmlOptions)null);
        }
        
        public static STStyleMatrixColumnIndex parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(reader, STStyleMatrixColumnIndex.type, xmlOptions);
        }
        
        public static STStyleMatrixColumnIndex parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(xmlStreamReader, STStyleMatrixColumnIndex.type, (XmlOptions)null);
        }
        
        public static STStyleMatrixColumnIndex parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(xmlStreamReader, STStyleMatrixColumnIndex.type, xmlOptions);
        }
        
        public static STStyleMatrixColumnIndex parse(final Node node) throws XmlException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(node, STStyleMatrixColumnIndex.type, (XmlOptions)null);
        }
        
        public static STStyleMatrixColumnIndex parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(node, STStyleMatrixColumnIndex.type, xmlOptions);
        }
        
        @Deprecated
        public static STStyleMatrixColumnIndex parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(xmlInputStream, STStyleMatrixColumnIndex.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STStyleMatrixColumnIndex parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STStyleMatrixColumnIndex)getTypeLoader().parse(xmlInputStream, STStyleMatrixColumnIndex.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STStyleMatrixColumnIndex.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STStyleMatrixColumnIndex.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
