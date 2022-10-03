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
import org.apache.xmlbeans.XmlString;

public interface STTextLanguageID extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextLanguageID.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextlanguageid806btype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextLanguageID newValue(final Object o) {
            return (STTextLanguageID)STTextLanguageID.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextLanguageID.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextLanguageID newInstance() {
            return (STTextLanguageID)getTypeLoader().newInstance(STTextLanguageID.type, (XmlOptions)null);
        }
        
        public static STTextLanguageID newInstance(final XmlOptions xmlOptions) {
            return (STTextLanguageID)getTypeLoader().newInstance(STTextLanguageID.type, xmlOptions);
        }
        
        public static STTextLanguageID parse(final String s) throws XmlException {
            return (STTextLanguageID)getTypeLoader().parse(s, STTextLanguageID.type, (XmlOptions)null);
        }
        
        public static STTextLanguageID parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextLanguageID)getTypeLoader().parse(s, STTextLanguageID.type, xmlOptions);
        }
        
        public static STTextLanguageID parse(final File file) throws XmlException, IOException {
            return (STTextLanguageID)getTypeLoader().parse(file, STTextLanguageID.type, (XmlOptions)null);
        }
        
        public static STTextLanguageID parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextLanguageID)getTypeLoader().parse(file, STTextLanguageID.type, xmlOptions);
        }
        
        public static STTextLanguageID parse(final URL url) throws XmlException, IOException {
            return (STTextLanguageID)getTypeLoader().parse(url, STTextLanguageID.type, (XmlOptions)null);
        }
        
        public static STTextLanguageID parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextLanguageID)getTypeLoader().parse(url, STTextLanguageID.type, xmlOptions);
        }
        
        public static STTextLanguageID parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextLanguageID)getTypeLoader().parse(inputStream, STTextLanguageID.type, (XmlOptions)null);
        }
        
        public static STTextLanguageID parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextLanguageID)getTypeLoader().parse(inputStream, STTextLanguageID.type, xmlOptions);
        }
        
        public static STTextLanguageID parse(final Reader reader) throws XmlException, IOException {
            return (STTextLanguageID)getTypeLoader().parse(reader, STTextLanguageID.type, (XmlOptions)null);
        }
        
        public static STTextLanguageID parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextLanguageID)getTypeLoader().parse(reader, STTextLanguageID.type, xmlOptions);
        }
        
        public static STTextLanguageID parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextLanguageID)getTypeLoader().parse(xmlStreamReader, STTextLanguageID.type, (XmlOptions)null);
        }
        
        public static STTextLanguageID parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextLanguageID)getTypeLoader().parse(xmlStreamReader, STTextLanguageID.type, xmlOptions);
        }
        
        public static STTextLanguageID parse(final Node node) throws XmlException {
            return (STTextLanguageID)getTypeLoader().parse(node, STTextLanguageID.type, (XmlOptions)null);
        }
        
        public static STTextLanguageID parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextLanguageID)getTypeLoader().parse(node, STTextLanguageID.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextLanguageID parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextLanguageID)getTypeLoader().parse(xmlInputStream, STTextLanguageID.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextLanguageID parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextLanguageID)getTypeLoader().parse(xmlInputStream, STTextLanguageID.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextLanguageID.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextLanguageID.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
