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
import org.apache.xmlbeans.XmlObject;

public interface NumberingDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(NumberingDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("numbering1c4ddoctype");
    
    CTNumbering getNumbering();
    
    void setNumbering(final CTNumbering p0);
    
    CTNumbering addNewNumbering();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(NumberingDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static NumberingDocument newInstance() {
            return (NumberingDocument)getTypeLoader().newInstance(NumberingDocument.type, (XmlOptions)null);
        }
        
        public static NumberingDocument newInstance(final XmlOptions xmlOptions) {
            return (NumberingDocument)getTypeLoader().newInstance(NumberingDocument.type, xmlOptions);
        }
        
        public static NumberingDocument parse(final String s) throws XmlException {
            return (NumberingDocument)getTypeLoader().parse(s, NumberingDocument.type, (XmlOptions)null);
        }
        
        public static NumberingDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (NumberingDocument)getTypeLoader().parse(s, NumberingDocument.type, xmlOptions);
        }
        
        public static NumberingDocument parse(final File file) throws XmlException, IOException {
            return (NumberingDocument)getTypeLoader().parse(file, NumberingDocument.type, (XmlOptions)null);
        }
        
        public static NumberingDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (NumberingDocument)getTypeLoader().parse(file, NumberingDocument.type, xmlOptions);
        }
        
        public static NumberingDocument parse(final URL url) throws XmlException, IOException {
            return (NumberingDocument)getTypeLoader().parse(url, NumberingDocument.type, (XmlOptions)null);
        }
        
        public static NumberingDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (NumberingDocument)getTypeLoader().parse(url, NumberingDocument.type, xmlOptions);
        }
        
        public static NumberingDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (NumberingDocument)getTypeLoader().parse(inputStream, NumberingDocument.type, (XmlOptions)null);
        }
        
        public static NumberingDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (NumberingDocument)getTypeLoader().parse(inputStream, NumberingDocument.type, xmlOptions);
        }
        
        public static NumberingDocument parse(final Reader reader) throws XmlException, IOException {
            return (NumberingDocument)getTypeLoader().parse(reader, NumberingDocument.type, (XmlOptions)null);
        }
        
        public static NumberingDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (NumberingDocument)getTypeLoader().parse(reader, NumberingDocument.type, xmlOptions);
        }
        
        public static NumberingDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (NumberingDocument)getTypeLoader().parse(xmlStreamReader, NumberingDocument.type, (XmlOptions)null);
        }
        
        public static NumberingDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (NumberingDocument)getTypeLoader().parse(xmlStreamReader, NumberingDocument.type, xmlOptions);
        }
        
        public static NumberingDocument parse(final Node node) throws XmlException {
            return (NumberingDocument)getTypeLoader().parse(node, NumberingDocument.type, (XmlOptions)null);
        }
        
        public static NumberingDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (NumberingDocument)getTypeLoader().parse(node, NumberingDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static NumberingDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (NumberingDocument)getTypeLoader().parse(xmlInputStream, NumberingDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static NumberingDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (NumberingDocument)getTypeLoader().parse(xmlInputStream, NumberingDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, NumberingDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, NumberingDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
