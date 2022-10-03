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
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.apache.xmlbeans.SchemaType;

public interface CTText extends STString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTText.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttext7f5btype");
    
    SpaceAttribute.Space.Enum getSpace();
    
    SpaceAttribute.Space xgetSpace();
    
    boolean isSetSpace();
    
    void setSpace(final SpaceAttribute.Space.Enum p0);
    
    void xsetSpace(final SpaceAttribute.Space p0);
    
    void unsetSpace();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTText.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTText newInstance() {
            return (CTText)getTypeLoader().newInstance(CTText.type, (XmlOptions)null);
        }
        
        public static CTText newInstance(final XmlOptions xmlOptions) {
            return (CTText)getTypeLoader().newInstance(CTText.type, xmlOptions);
        }
        
        public static CTText parse(final String s) throws XmlException {
            return (CTText)getTypeLoader().parse(s, CTText.type, (XmlOptions)null);
        }
        
        public static CTText parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTText)getTypeLoader().parse(s, CTText.type, xmlOptions);
        }
        
        public static CTText parse(final File file) throws XmlException, IOException {
            return (CTText)getTypeLoader().parse(file, CTText.type, (XmlOptions)null);
        }
        
        public static CTText parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTText)getTypeLoader().parse(file, CTText.type, xmlOptions);
        }
        
        public static CTText parse(final URL url) throws XmlException, IOException {
            return (CTText)getTypeLoader().parse(url, CTText.type, (XmlOptions)null);
        }
        
        public static CTText parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTText)getTypeLoader().parse(url, CTText.type, xmlOptions);
        }
        
        public static CTText parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTText)getTypeLoader().parse(inputStream, CTText.type, (XmlOptions)null);
        }
        
        public static CTText parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTText)getTypeLoader().parse(inputStream, CTText.type, xmlOptions);
        }
        
        public static CTText parse(final Reader reader) throws XmlException, IOException {
            return (CTText)getTypeLoader().parse(reader, CTText.type, (XmlOptions)null);
        }
        
        public static CTText parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTText)getTypeLoader().parse(reader, CTText.type, xmlOptions);
        }
        
        public static CTText parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTText)getTypeLoader().parse(xmlStreamReader, CTText.type, (XmlOptions)null);
        }
        
        public static CTText parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTText)getTypeLoader().parse(xmlStreamReader, CTText.type, xmlOptions);
        }
        
        public static CTText parse(final Node node) throws XmlException {
            return (CTText)getTypeLoader().parse(node, CTText.type, (XmlOptions)null);
        }
        
        public static CTText parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTText)getTypeLoader().parse(node, CTText.type, xmlOptions);
        }
        
        @Deprecated
        public static CTText parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTText)getTypeLoader().parse(xmlInputStream, CTText.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTText parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTText)getTypeLoader().parse(xmlInputStream, CTText.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTText.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTText.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
