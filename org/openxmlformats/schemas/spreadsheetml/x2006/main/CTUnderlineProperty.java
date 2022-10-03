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

public interface CTUnderlineProperty extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTUnderlineProperty.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctunderlineproperty8e20type");
    
    STUnderlineValues.Enum getVal();
    
    STUnderlineValues xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STUnderlineValues.Enum p0);
    
    void xsetVal(final STUnderlineValues p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTUnderlineProperty.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTUnderlineProperty newInstance() {
            return (CTUnderlineProperty)getTypeLoader().newInstance(CTUnderlineProperty.type, (XmlOptions)null);
        }
        
        public static CTUnderlineProperty newInstance(final XmlOptions xmlOptions) {
            return (CTUnderlineProperty)getTypeLoader().newInstance(CTUnderlineProperty.type, xmlOptions);
        }
        
        public static CTUnderlineProperty parse(final String s) throws XmlException {
            return (CTUnderlineProperty)getTypeLoader().parse(s, CTUnderlineProperty.type, (XmlOptions)null);
        }
        
        public static CTUnderlineProperty parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTUnderlineProperty)getTypeLoader().parse(s, CTUnderlineProperty.type, xmlOptions);
        }
        
        public static CTUnderlineProperty parse(final File file) throws XmlException, IOException {
            return (CTUnderlineProperty)getTypeLoader().parse(file, CTUnderlineProperty.type, (XmlOptions)null);
        }
        
        public static CTUnderlineProperty parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTUnderlineProperty)getTypeLoader().parse(file, CTUnderlineProperty.type, xmlOptions);
        }
        
        public static CTUnderlineProperty parse(final URL url) throws XmlException, IOException {
            return (CTUnderlineProperty)getTypeLoader().parse(url, CTUnderlineProperty.type, (XmlOptions)null);
        }
        
        public static CTUnderlineProperty parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTUnderlineProperty)getTypeLoader().parse(url, CTUnderlineProperty.type, xmlOptions);
        }
        
        public static CTUnderlineProperty parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTUnderlineProperty)getTypeLoader().parse(inputStream, CTUnderlineProperty.type, (XmlOptions)null);
        }
        
        public static CTUnderlineProperty parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTUnderlineProperty)getTypeLoader().parse(inputStream, CTUnderlineProperty.type, xmlOptions);
        }
        
        public static CTUnderlineProperty parse(final Reader reader) throws XmlException, IOException {
            return (CTUnderlineProperty)getTypeLoader().parse(reader, CTUnderlineProperty.type, (XmlOptions)null);
        }
        
        public static CTUnderlineProperty parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTUnderlineProperty)getTypeLoader().parse(reader, CTUnderlineProperty.type, xmlOptions);
        }
        
        public static CTUnderlineProperty parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTUnderlineProperty)getTypeLoader().parse(xmlStreamReader, CTUnderlineProperty.type, (XmlOptions)null);
        }
        
        public static CTUnderlineProperty parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTUnderlineProperty)getTypeLoader().parse(xmlStreamReader, CTUnderlineProperty.type, xmlOptions);
        }
        
        public static CTUnderlineProperty parse(final Node node) throws XmlException {
            return (CTUnderlineProperty)getTypeLoader().parse(node, CTUnderlineProperty.type, (XmlOptions)null);
        }
        
        public static CTUnderlineProperty parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTUnderlineProperty)getTypeLoader().parse(node, CTUnderlineProperty.type, xmlOptions);
        }
        
        @Deprecated
        public static CTUnderlineProperty parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTUnderlineProperty)getTypeLoader().parse(xmlInputStream, CTUnderlineProperty.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTUnderlineProperty parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTUnderlineProperty)getTypeLoader().parse(xmlInputStream, CTUnderlineProperty.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTUnderlineProperty.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTUnderlineProperty.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
