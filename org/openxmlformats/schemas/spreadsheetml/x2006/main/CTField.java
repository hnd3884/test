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

public interface CTField extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTField.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfieldc999type");
    
    int getX();
    
    XmlInt xgetX();
    
    void setX(final int p0);
    
    void xsetX(final XmlInt p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTField.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTField newInstance() {
            return (CTField)getTypeLoader().newInstance(CTField.type, (XmlOptions)null);
        }
        
        public static CTField newInstance(final XmlOptions xmlOptions) {
            return (CTField)getTypeLoader().newInstance(CTField.type, xmlOptions);
        }
        
        public static CTField parse(final String s) throws XmlException {
            return (CTField)getTypeLoader().parse(s, CTField.type, (XmlOptions)null);
        }
        
        public static CTField parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTField)getTypeLoader().parse(s, CTField.type, xmlOptions);
        }
        
        public static CTField parse(final File file) throws XmlException, IOException {
            return (CTField)getTypeLoader().parse(file, CTField.type, (XmlOptions)null);
        }
        
        public static CTField parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTField)getTypeLoader().parse(file, CTField.type, xmlOptions);
        }
        
        public static CTField parse(final URL url) throws XmlException, IOException {
            return (CTField)getTypeLoader().parse(url, CTField.type, (XmlOptions)null);
        }
        
        public static CTField parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTField)getTypeLoader().parse(url, CTField.type, xmlOptions);
        }
        
        public static CTField parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTField)getTypeLoader().parse(inputStream, CTField.type, (XmlOptions)null);
        }
        
        public static CTField parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTField)getTypeLoader().parse(inputStream, CTField.type, xmlOptions);
        }
        
        public static CTField parse(final Reader reader) throws XmlException, IOException {
            return (CTField)getTypeLoader().parse(reader, CTField.type, (XmlOptions)null);
        }
        
        public static CTField parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTField)getTypeLoader().parse(reader, CTField.type, xmlOptions);
        }
        
        public static CTField parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTField)getTypeLoader().parse(xmlStreamReader, CTField.type, (XmlOptions)null);
        }
        
        public static CTField parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTField)getTypeLoader().parse(xmlStreamReader, CTField.type, xmlOptions);
        }
        
        public static CTField parse(final Node node) throws XmlException {
            return (CTField)getTypeLoader().parse(node, CTField.type, (XmlOptions)null);
        }
        
        public static CTField parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTField)getTypeLoader().parse(node, CTField.type, xmlOptions);
        }
        
        @Deprecated
        public static CTField parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTField)getTypeLoader().parse(xmlInputStream, CTField.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTField parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTField)getTypeLoader().parse(xmlInputStream, CTField.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTField.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTField.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
