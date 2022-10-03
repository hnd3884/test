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
import org.apache.xmlbeans.XmlString;

public interface STCellRef extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCellRef.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcellrefe4e0type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCellRef newValue(final Object o) {
            return (STCellRef)STCellRef.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCellRef.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCellRef newInstance() {
            return (STCellRef)getTypeLoader().newInstance(STCellRef.type, (XmlOptions)null);
        }
        
        public static STCellRef newInstance(final XmlOptions xmlOptions) {
            return (STCellRef)getTypeLoader().newInstance(STCellRef.type, xmlOptions);
        }
        
        public static STCellRef parse(final String s) throws XmlException {
            return (STCellRef)getTypeLoader().parse(s, STCellRef.type, (XmlOptions)null);
        }
        
        public static STCellRef parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCellRef)getTypeLoader().parse(s, STCellRef.type, xmlOptions);
        }
        
        public static STCellRef parse(final File file) throws XmlException, IOException {
            return (STCellRef)getTypeLoader().parse(file, STCellRef.type, (XmlOptions)null);
        }
        
        public static STCellRef parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellRef)getTypeLoader().parse(file, STCellRef.type, xmlOptions);
        }
        
        public static STCellRef parse(final URL url) throws XmlException, IOException {
            return (STCellRef)getTypeLoader().parse(url, STCellRef.type, (XmlOptions)null);
        }
        
        public static STCellRef parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellRef)getTypeLoader().parse(url, STCellRef.type, xmlOptions);
        }
        
        public static STCellRef parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCellRef)getTypeLoader().parse(inputStream, STCellRef.type, (XmlOptions)null);
        }
        
        public static STCellRef parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellRef)getTypeLoader().parse(inputStream, STCellRef.type, xmlOptions);
        }
        
        public static STCellRef parse(final Reader reader) throws XmlException, IOException {
            return (STCellRef)getTypeLoader().parse(reader, STCellRef.type, (XmlOptions)null);
        }
        
        public static STCellRef parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellRef)getTypeLoader().parse(reader, STCellRef.type, xmlOptions);
        }
        
        public static STCellRef parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCellRef)getTypeLoader().parse(xmlStreamReader, STCellRef.type, (XmlOptions)null);
        }
        
        public static STCellRef parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCellRef)getTypeLoader().parse(xmlStreamReader, STCellRef.type, xmlOptions);
        }
        
        public static STCellRef parse(final Node node) throws XmlException {
            return (STCellRef)getTypeLoader().parse(node, STCellRef.type, (XmlOptions)null);
        }
        
        public static STCellRef parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCellRef)getTypeLoader().parse(node, STCellRef.type, xmlOptions);
        }
        
        @Deprecated
        public static STCellRef parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCellRef)getTypeLoader().parse(xmlInputStream, STCellRef.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCellRef parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCellRef)getTypeLoader().parse(xmlInputStream, STCellRef.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCellRef.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCellRef.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
