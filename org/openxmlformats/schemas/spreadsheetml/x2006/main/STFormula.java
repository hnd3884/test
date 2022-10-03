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

public interface STFormula extends STXstring
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STFormula.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stformula7e35type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STFormula newValue(final Object o) {
            return (STFormula)STFormula.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STFormula.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STFormula newInstance() {
            return (STFormula)getTypeLoader().newInstance(STFormula.type, (XmlOptions)null);
        }
        
        public static STFormula newInstance(final XmlOptions xmlOptions) {
            return (STFormula)getTypeLoader().newInstance(STFormula.type, xmlOptions);
        }
        
        public static STFormula parse(final String s) throws XmlException {
            return (STFormula)getTypeLoader().parse(s, STFormula.type, (XmlOptions)null);
        }
        
        public static STFormula parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STFormula)getTypeLoader().parse(s, STFormula.type, xmlOptions);
        }
        
        public static STFormula parse(final File file) throws XmlException, IOException {
            return (STFormula)getTypeLoader().parse(file, STFormula.type, (XmlOptions)null);
        }
        
        public static STFormula parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFormula)getTypeLoader().parse(file, STFormula.type, xmlOptions);
        }
        
        public static STFormula parse(final URL url) throws XmlException, IOException {
            return (STFormula)getTypeLoader().parse(url, STFormula.type, (XmlOptions)null);
        }
        
        public static STFormula parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFormula)getTypeLoader().parse(url, STFormula.type, xmlOptions);
        }
        
        public static STFormula parse(final InputStream inputStream) throws XmlException, IOException {
            return (STFormula)getTypeLoader().parse(inputStream, STFormula.type, (XmlOptions)null);
        }
        
        public static STFormula parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFormula)getTypeLoader().parse(inputStream, STFormula.type, xmlOptions);
        }
        
        public static STFormula parse(final Reader reader) throws XmlException, IOException {
            return (STFormula)getTypeLoader().parse(reader, STFormula.type, (XmlOptions)null);
        }
        
        public static STFormula parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFormula)getTypeLoader().parse(reader, STFormula.type, xmlOptions);
        }
        
        public static STFormula parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STFormula)getTypeLoader().parse(xmlStreamReader, STFormula.type, (XmlOptions)null);
        }
        
        public static STFormula parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STFormula)getTypeLoader().parse(xmlStreamReader, STFormula.type, xmlOptions);
        }
        
        public static STFormula parse(final Node node) throws XmlException {
            return (STFormula)getTypeLoader().parse(node, STFormula.type, (XmlOptions)null);
        }
        
        public static STFormula parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STFormula)getTypeLoader().parse(node, STFormula.type, xmlOptions);
        }
        
        @Deprecated
        public static STFormula parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STFormula)getTypeLoader().parse(xmlInputStream, STFormula.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STFormula parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STFormula)getTypeLoader().parse(xmlInputStream, STFormula.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFormula.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFormula.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
