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
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTIntProperty extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTIntProperty.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctintproperty32c3type");
    
    int getVal();
    
    XmlInt xgetVal();
    
    void setVal(final int p0);
    
    void xsetVal(final XmlInt p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTIntProperty.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTIntProperty newInstance() {
            return (CTIntProperty)getTypeLoader().newInstance(CTIntProperty.type, (XmlOptions)null);
        }
        
        public static CTIntProperty newInstance(final XmlOptions xmlOptions) {
            return (CTIntProperty)getTypeLoader().newInstance(CTIntProperty.type, xmlOptions);
        }
        
        public static CTIntProperty parse(final String s) throws XmlException {
            return (CTIntProperty)getTypeLoader().parse(s, CTIntProperty.type, (XmlOptions)null);
        }
        
        public static CTIntProperty parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTIntProperty)getTypeLoader().parse(s, CTIntProperty.type, xmlOptions);
        }
        
        public static CTIntProperty parse(final File file) throws XmlException, IOException {
            return (CTIntProperty)getTypeLoader().parse(file, CTIntProperty.type, (XmlOptions)null);
        }
        
        public static CTIntProperty parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIntProperty)getTypeLoader().parse(file, CTIntProperty.type, xmlOptions);
        }
        
        public static CTIntProperty parse(final URL url) throws XmlException, IOException {
            return (CTIntProperty)getTypeLoader().parse(url, CTIntProperty.type, (XmlOptions)null);
        }
        
        public static CTIntProperty parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIntProperty)getTypeLoader().parse(url, CTIntProperty.type, xmlOptions);
        }
        
        public static CTIntProperty parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTIntProperty)getTypeLoader().parse(inputStream, CTIntProperty.type, (XmlOptions)null);
        }
        
        public static CTIntProperty parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIntProperty)getTypeLoader().parse(inputStream, CTIntProperty.type, xmlOptions);
        }
        
        public static CTIntProperty parse(final Reader reader) throws XmlException, IOException {
            return (CTIntProperty)getTypeLoader().parse(reader, CTIntProperty.type, (XmlOptions)null);
        }
        
        public static CTIntProperty parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIntProperty)getTypeLoader().parse(reader, CTIntProperty.type, xmlOptions);
        }
        
        public static CTIntProperty parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTIntProperty)getTypeLoader().parse(xmlStreamReader, CTIntProperty.type, (XmlOptions)null);
        }
        
        public static CTIntProperty parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTIntProperty)getTypeLoader().parse(xmlStreamReader, CTIntProperty.type, xmlOptions);
        }
        
        public static CTIntProperty parse(final Node node) throws XmlException {
            return (CTIntProperty)getTypeLoader().parse(node, CTIntProperty.type, (XmlOptions)null);
        }
        
        public static CTIntProperty parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTIntProperty)getTypeLoader().parse(node, CTIntProperty.type, xmlOptions);
        }
        
        @Deprecated
        public static CTIntProperty parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTIntProperty)getTypeLoader().parse(xmlInputStream, CTIntProperty.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTIntProperty parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTIntProperty)getTypeLoader().parse(xmlInputStream, CTIntProperty.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTIntProperty.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTIntProperty.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
