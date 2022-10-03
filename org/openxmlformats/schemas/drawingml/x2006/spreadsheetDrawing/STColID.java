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

public interface STColID extends XmlInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STColID.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcolidb7f5type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STColID newValue(final Object o) {
            return (STColID)STColID.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STColID.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STColID newInstance() {
            return (STColID)getTypeLoader().newInstance(STColID.type, (XmlOptions)null);
        }
        
        public static STColID newInstance(final XmlOptions xmlOptions) {
            return (STColID)getTypeLoader().newInstance(STColID.type, xmlOptions);
        }
        
        public static STColID parse(final String s) throws XmlException {
            return (STColID)getTypeLoader().parse(s, STColID.type, (XmlOptions)null);
        }
        
        public static STColID parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STColID)getTypeLoader().parse(s, STColID.type, xmlOptions);
        }
        
        public static STColID parse(final File file) throws XmlException, IOException {
            return (STColID)getTypeLoader().parse(file, STColID.type, (XmlOptions)null);
        }
        
        public static STColID parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STColID)getTypeLoader().parse(file, STColID.type, xmlOptions);
        }
        
        public static STColID parse(final URL url) throws XmlException, IOException {
            return (STColID)getTypeLoader().parse(url, STColID.type, (XmlOptions)null);
        }
        
        public static STColID parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STColID)getTypeLoader().parse(url, STColID.type, xmlOptions);
        }
        
        public static STColID parse(final InputStream inputStream) throws XmlException, IOException {
            return (STColID)getTypeLoader().parse(inputStream, STColID.type, (XmlOptions)null);
        }
        
        public static STColID parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STColID)getTypeLoader().parse(inputStream, STColID.type, xmlOptions);
        }
        
        public static STColID parse(final Reader reader) throws XmlException, IOException {
            return (STColID)getTypeLoader().parse(reader, STColID.type, (XmlOptions)null);
        }
        
        public static STColID parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STColID)getTypeLoader().parse(reader, STColID.type, xmlOptions);
        }
        
        public static STColID parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STColID)getTypeLoader().parse(xmlStreamReader, STColID.type, (XmlOptions)null);
        }
        
        public static STColID parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STColID)getTypeLoader().parse(xmlStreamReader, STColID.type, xmlOptions);
        }
        
        public static STColID parse(final Node node) throws XmlException {
            return (STColID)getTypeLoader().parse(node, STColID.type, (XmlOptions)null);
        }
        
        public static STColID parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STColID)getTypeLoader().parse(node, STColID.type, xmlOptions);
        }
        
        @Deprecated
        public static STColID parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STColID)getTypeLoader().parse(xmlInputStream, STColID.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STColID parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STColID)getTypeLoader().parse(xmlInputStream, STColID.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STColID.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STColID.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
