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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTBooleanProperty extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBooleanProperty.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbooleanproperty1f3ctype");
    
    boolean getVal();
    
    XmlBoolean xgetVal();
    
    boolean isSetVal();
    
    void setVal(final boolean p0);
    
    void xsetVal(final XmlBoolean p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBooleanProperty.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBooleanProperty newInstance() {
            return (CTBooleanProperty)getTypeLoader().newInstance(CTBooleanProperty.type, (XmlOptions)null);
        }
        
        public static CTBooleanProperty newInstance(final XmlOptions xmlOptions) {
            return (CTBooleanProperty)getTypeLoader().newInstance(CTBooleanProperty.type, xmlOptions);
        }
        
        public static CTBooleanProperty parse(final String s) throws XmlException {
            return (CTBooleanProperty)getTypeLoader().parse(s, CTBooleanProperty.type, (XmlOptions)null);
        }
        
        public static CTBooleanProperty parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBooleanProperty)getTypeLoader().parse(s, CTBooleanProperty.type, xmlOptions);
        }
        
        public static CTBooleanProperty parse(final File file) throws XmlException, IOException {
            return (CTBooleanProperty)getTypeLoader().parse(file, CTBooleanProperty.type, (XmlOptions)null);
        }
        
        public static CTBooleanProperty parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBooleanProperty)getTypeLoader().parse(file, CTBooleanProperty.type, xmlOptions);
        }
        
        public static CTBooleanProperty parse(final URL url) throws XmlException, IOException {
            return (CTBooleanProperty)getTypeLoader().parse(url, CTBooleanProperty.type, (XmlOptions)null);
        }
        
        public static CTBooleanProperty parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBooleanProperty)getTypeLoader().parse(url, CTBooleanProperty.type, xmlOptions);
        }
        
        public static CTBooleanProperty parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBooleanProperty)getTypeLoader().parse(inputStream, CTBooleanProperty.type, (XmlOptions)null);
        }
        
        public static CTBooleanProperty parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBooleanProperty)getTypeLoader().parse(inputStream, CTBooleanProperty.type, xmlOptions);
        }
        
        public static CTBooleanProperty parse(final Reader reader) throws XmlException, IOException {
            return (CTBooleanProperty)getTypeLoader().parse(reader, CTBooleanProperty.type, (XmlOptions)null);
        }
        
        public static CTBooleanProperty parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBooleanProperty)getTypeLoader().parse(reader, CTBooleanProperty.type, xmlOptions);
        }
        
        public static CTBooleanProperty parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBooleanProperty)getTypeLoader().parse(xmlStreamReader, CTBooleanProperty.type, (XmlOptions)null);
        }
        
        public static CTBooleanProperty parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBooleanProperty)getTypeLoader().parse(xmlStreamReader, CTBooleanProperty.type, xmlOptions);
        }
        
        public static CTBooleanProperty parse(final Node node) throws XmlException {
            return (CTBooleanProperty)getTypeLoader().parse(node, CTBooleanProperty.type, (XmlOptions)null);
        }
        
        public static CTBooleanProperty parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBooleanProperty)getTypeLoader().parse(node, CTBooleanProperty.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBooleanProperty parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBooleanProperty)getTypeLoader().parse(xmlInputStream, CTBooleanProperty.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBooleanProperty parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBooleanProperty)getTypeLoader().parse(xmlInputStream, CTBooleanProperty.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBooleanProperty.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBooleanProperty.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
