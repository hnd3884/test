package org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes;

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

public interface STClsid extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STClsid.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stclsida7datype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STClsid newValue(final Object o) {
            return (STClsid)STClsid.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STClsid.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STClsid newInstance() {
            return (STClsid)getTypeLoader().newInstance(STClsid.type, (XmlOptions)null);
        }
        
        public static STClsid newInstance(final XmlOptions xmlOptions) {
            return (STClsid)getTypeLoader().newInstance(STClsid.type, xmlOptions);
        }
        
        public static STClsid parse(final String s) throws XmlException {
            return (STClsid)getTypeLoader().parse(s, STClsid.type, (XmlOptions)null);
        }
        
        public static STClsid parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STClsid)getTypeLoader().parse(s, STClsid.type, xmlOptions);
        }
        
        public static STClsid parse(final File file) throws XmlException, IOException {
            return (STClsid)getTypeLoader().parse(file, STClsid.type, (XmlOptions)null);
        }
        
        public static STClsid parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STClsid)getTypeLoader().parse(file, STClsid.type, xmlOptions);
        }
        
        public static STClsid parse(final URL url) throws XmlException, IOException {
            return (STClsid)getTypeLoader().parse(url, STClsid.type, (XmlOptions)null);
        }
        
        public static STClsid parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STClsid)getTypeLoader().parse(url, STClsid.type, xmlOptions);
        }
        
        public static STClsid parse(final InputStream inputStream) throws XmlException, IOException {
            return (STClsid)getTypeLoader().parse(inputStream, STClsid.type, (XmlOptions)null);
        }
        
        public static STClsid parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STClsid)getTypeLoader().parse(inputStream, STClsid.type, xmlOptions);
        }
        
        public static STClsid parse(final Reader reader) throws XmlException, IOException {
            return (STClsid)getTypeLoader().parse(reader, STClsid.type, (XmlOptions)null);
        }
        
        public static STClsid parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STClsid)getTypeLoader().parse(reader, STClsid.type, xmlOptions);
        }
        
        public static STClsid parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STClsid)getTypeLoader().parse(xmlStreamReader, STClsid.type, (XmlOptions)null);
        }
        
        public static STClsid parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STClsid)getTypeLoader().parse(xmlStreamReader, STClsid.type, xmlOptions);
        }
        
        public static STClsid parse(final Node node) throws XmlException {
            return (STClsid)getTypeLoader().parse(node, STClsid.type, (XmlOptions)null);
        }
        
        public static STClsid parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STClsid)getTypeLoader().parse(node, STClsid.type, xmlOptions);
        }
        
        @Deprecated
        public static STClsid parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STClsid)getTypeLoader().parse(xmlInputStream, STClsid.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STClsid parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STClsid)getTypeLoader().parse(xmlInputStream, STClsid.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STClsid.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STClsid.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
