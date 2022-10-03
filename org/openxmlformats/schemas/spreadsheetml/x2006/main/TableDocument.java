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
import org.apache.xmlbeans.XmlObject;

public interface TableDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(TableDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("table0b99doctype");
    
    CTTable getTable();
    
    void setTable(final CTTable p0);
    
    CTTable addNewTable();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(TableDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static TableDocument newInstance() {
            return (TableDocument)getTypeLoader().newInstance(TableDocument.type, (XmlOptions)null);
        }
        
        public static TableDocument newInstance(final XmlOptions xmlOptions) {
            return (TableDocument)getTypeLoader().newInstance(TableDocument.type, xmlOptions);
        }
        
        public static TableDocument parse(final String s) throws XmlException {
            return (TableDocument)getTypeLoader().parse(s, TableDocument.type, (XmlOptions)null);
        }
        
        public static TableDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (TableDocument)getTypeLoader().parse(s, TableDocument.type, xmlOptions);
        }
        
        public static TableDocument parse(final File file) throws XmlException, IOException {
            return (TableDocument)getTypeLoader().parse(file, TableDocument.type, (XmlOptions)null);
        }
        
        public static TableDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TableDocument)getTypeLoader().parse(file, TableDocument.type, xmlOptions);
        }
        
        public static TableDocument parse(final URL url) throws XmlException, IOException {
            return (TableDocument)getTypeLoader().parse(url, TableDocument.type, (XmlOptions)null);
        }
        
        public static TableDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TableDocument)getTypeLoader().parse(url, TableDocument.type, xmlOptions);
        }
        
        public static TableDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (TableDocument)getTypeLoader().parse(inputStream, TableDocument.type, (XmlOptions)null);
        }
        
        public static TableDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TableDocument)getTypeLoader().parse(inputStream, TableDocument.type, xmlOptions);
        }
        
        public static TableDocument parse(final Reader reader) throws XmlException, IOException {
            return (TableDocument)getTypeLoader().parse(reader, TableDocument.type, (XmlOptions)null);
        }
        
        public static TableDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TableDocument)getTypeLoader().parse(reader, TableDocument.type, xmlOptions);
        }
        
        public static TableDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (TableDocument)getTypeLoader().parse(xmlStreamReader, TableDocument.type, (XmlOptions)null);
        }
        
        public static TableDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (TableDocument)getTypeLoader().parse(xmlStreamReader, TableDocument.type, xmlOptions);
        }
        
        public static TableDocument parse(final Node node) throws XmlException {
            return (TableDocument)getTypeLoader().parse(node, TableDocument.type, (XmlOptions)null);
        }
        
        public static TableDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (TableDocument)getTypeLoader().parse(node, TableDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static TableDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (TableDocument)getTypeLoader().parse(xmlInputStream, TableDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static TableDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (TableDocument)getTypeLoader().parse(xmlInputStream, TableDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, TableDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, TableDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
