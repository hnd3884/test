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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTableStyleElement extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableStyleElement.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablestyleelementa658type");
    
    STTableStyleType.Enum getType();
    
    STTableStyleType xgetType();
    
    void setType(final STTableStyleType.Enum p0);
    
    void xsetType(final STTableStyleType p0);
    
    long getSize();
    
    XmlUnsignedInt xgetSize();
    
    boolean isSetSize();
    
    void setSize(final long p0);
    
    void xsetSize(final XmlUnsignedInt p0);
    
    void unsetSize();
    
    long getDxfId();
    
    STDxfId xgetDxfId();
    
    boolean isSetDxfId();
    
    void setDxfId(final long p0);
    
    void xsetDxfId(final STDxfId p0);
    
    void unsetDxfId();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableStyleElement.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableStyleElement newInstance() {
            return (CTTableStyleElement)getTypeLoader().newInstance(CTTableStyleElement.type, (XmlOptions)null);
        }
        
        public static CTTableStyleElement newInstance(final XmlOptions xmlOptions) {
            return (CTTableStyleElement)getTypeLoader().newInstance(CTTableStyleElement.type, xmlOptions);
        }
        
        public static CTTableStyleElement parse(final String s) throws XmlException {
            return (CTTableStyleElement)getTypeLoader().parse(s, CTTableStyleElement.type, (XmlOptions)null);
        }
        
        public static CTTableStyleElement parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyleElement)getTypeLoader().parse(s, CTTableStyleElement.type, xmlOptions);
        }
        
        public static CTTableStyleElement parse(final File file) throws XmlException, IOException {
            return (CTTableStyleElement)getTypeLoader().parse(file, CTTableStyleElement.type, (XmlOptions)null);
        }
        
        public static CTTableStyleElement parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleElement)getTypeLoader().parse(file, CTTableStyleElement.type, xmlOptions);
        }
        
        public static CTTableStyleElement parse(final URL url) throws XmlException, IOException {
            return (CTTableStyleElement)getTypeLoader().parse(url, CTTableStyleElement.type, (XmlOptions)null);
        }
        
        public static CTTableStyleElement parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleElement)getTypeLoader().parse(url, CTTableStyleElement.type, xmlOptions);
        }
        
        public static CTTableStyleElement parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableStyleElement)getTypeLoader().parse(inputStream, CTTableStyleElement.type, (XmlOptions)null);
        }
        
        public static CTTableStyleElement parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleElement)getTypeLoader().parse(inputStream, CTTableStyleElement.type, xmlOptions);
        }
        
        public static CTTableStyleElement parse(final Reader reader) throws XmlException, IOException {
            return (CTTableStyleElement)getTypeLoader().parse(reader, CTTableStyleElement.type, (XmlOptions)null);
        }
        
        public static CTTableStyleElement parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleElement)getTypeLoader().parse(reader, CTTableStyleElement.type, xmlOptions);
        }
        
        public static CTTableStyleElement parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableStyleElement)getTypeLoader().parse(xmlStreamReader, CTTableStyleElement.type, (XmlOptions)null);
        }
        
        public static CTTableStyleElement parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyleElement)getTypeLoader().parse(xmlStreamReader, CTTableStyleElement.type, xmlOptions);
        }
        
        public static CTTableStyleElement parse(final Node node) throws XmlException {
            return (CTTableStyleElement)getTypeLoader().parse(node, CTTableStyleElement.type, (XmlOptions)null);
        }
        
        public static CTTableStyleElement parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyleElement)getTypeLoader().parse(node, CTTableStyleElement.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableStyleElement parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableStyleElement)getTypeLoader().parse(xmlInputStream, CTTableStyleElement.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableStyleElement parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableStyleElement)getTypeLoader().parse(xmlInputStream, CTTableStyleElement.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyleElement.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyleElement.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
