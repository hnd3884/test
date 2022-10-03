package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing;

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
import org.apache.xmlbeans.XmlInt;

public interface STRowID extends XmlInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STRowID.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("strowidf4cftype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STRowID newValue(final Object o) {
            return (STRowID)STRowID.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STRowID.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STRowID newInstance() {
            return (STRowID)getTypeLoader().newInstance(STRowID.type, (XmlOptions)null);
        }
        
        public static STRowID newInstance(final XmlOptions xmlOptions) {
            return (STRowID)getTypeLoader().newInstance(STRowID.type, xmlOptions);
        }
        
        public static STRowID parse(final String s) throws XmlException {
            return (STRowID)getTypeLoader().parse(s, STRowID.type, (XmlOptions)null);
        }
        
        public static STRowID parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STRowID)getTypeLoader().parse(s, STRowID.type, xmlOptions);
        }
        
        public static STRowID parse(final File file) throws XmlException, IOException {
            return (STRowID)getTypeLoader().parse(file, STRowID.type, (XmlOptions)null);
        }
        
        public static STRowID parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRowID)getTypeLoader().parse(file, STRowID.type, xmlOptions);
        }
        
        public static STRowID parse(final URL url) throws XmlException, IOException {
            return (STRowID)getTypeLoader().parse(url, STRowID.type, (XmlOptions)null);
        }
        
        public static STRowID parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRowID)getTypeLoader().parse(url, STRowID.type, xmlOptions);
        }
        
        public static STRowID parse(final InputStream inputStream) throws XmlException, IOException {
            return (STRowID)getTypeLoader().parse(inputStream, STRowID.type, (XmlOptions)null);
        }
        
        public static STRowID parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRowID)getTypeLoader().parse(inputStream, STRowID.type, xmlOptions);
        }
        
        public static STRowID parse(final Reader reader) throws XmlException, IOException {
            return (STRowID)getTypeLoader().parse(reader, STRowID.type, (XmlOptions)null);
        }
        
        public static STRowID parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRowID)getTypeLoader().parse(reader, STRowID.type, xmlOptions);
        }
        
        public static STRowID parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STRowID)getTypeLoader().parse(xmlStreamReader, STRowID.type, (XmlOptions)null);
        }
        
        public static STRowID parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STRowID)getTypeLoader().parse(xmlStreamReader, STRowID.type, xmlOptions);
        }
        
        public static STRowID parse(final Node node) throws XmlException {
            return (STRowID)getTypeLoader().parse(node, STRowID.type, (XmlOptions)null);
        }
        
        public static STRowID parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STRowID)getTypeLoader().parse(node, STRowID.type, xmlOptions);
        }
        
        @Deprecated
        public static STRowID parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STRowID)getTypeLoader().parse(xmlInputStream, STRowID.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STRowID parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STRowID)getTypeLoader().parse(xmlInputStream, STRowID.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STRowID.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STRowID.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
