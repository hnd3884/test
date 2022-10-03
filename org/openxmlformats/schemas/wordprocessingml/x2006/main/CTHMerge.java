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

public interface CTHMerge extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTHMerge.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cthmerge1bf8type");
    
    STMerge.Enum getVal();
    
    STMerge xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STMerge.Enum p0);
    
    void xsetVal(final STMerge p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTHMerge.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTHMerge newInstance() {
            return (CTHMerge)getTypeLoader().newInstance(CTHMerge.type, (XmlOptions)null);
        }
        
        public static CTHMerge newInstance(final XmlOptions xmlOptions) {
            return (CTHMerge)getTypeLoader().newInstance(CTHMerge.type, xmlOptions);
        }
        
        public static CTHMerge parse(final String s) throws XmlException {
            return (CTHMerge)getTypeLoader().parse(s, CTHMerge.type, (XmlOptions)null);
        }
        
        public static CTHMerge parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTHMerge)getTypeLoader().parse(s, CTHMerge.type, xmlOptions);
        }
        
        public static CTHMerge parse(final File file) throws XmlException, IOException {
            return (CTHMerge)getTypeLoader().parse(file, CTHMerge.type, (XmlOptions)null);
        }
        
        public static CTHMerge parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHMerge)getTypeLoader().parse(file, CTHMerge.type, xmlOptions);
        }
        
        public static CTHMerge parse(final URL url) throws XmlException, IOException {
            return (CTHMerge)getTypeLoader().parse(url, CTHMerge.type, (XmlOptions)null);
        }
        
        public static CTHMerge parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHMerge)getTypeLoader().parse(url, CTHMerge.type, xmlOptions);
        }
        
        public static CTHMerge parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTHMerge)getTypeLoader().parse(inputStream, CTHMerge.type, (XmlOptions)null);
        }
        
        public static CTHMerge parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHMerge)getTypeLoader().parse(inputStream, CTHMerge.type, xmlOptions);
        }
        
        public static CTHMerge parse(final Reader reader) throws XmlException, IOException {
            return (CTHMerge)getTypeLoader().parse(reader, CTHMerge.type, (XmlOptions)null);
        }
        
        public static CTHMerge parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHMerge)getTypeLoader().parse(reader, CTHMerge.type, xmlOptions);
        }
        
        public static CTHMerge parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTHMerge)getTypeLoader().parse(xmlStreamReader, CTHMerge.type, (XmlOptions)null);
        }
        
        public static CTHMerge parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTHMerge)getTypeLoader().parse(xmlStreamReader, CTHMerge.type, xmlOptions);
        }
        
        public static CTHMerge parse(final Node node) throws XmlException {
            return (CTHMerge)getTypeLoader().parse(node, CTHMerge.type, (XmlOptions)null);
        }
        
        public static CTHMerge parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTHMerge)getTypeLoader().parse(node, CTHMerge.type, xmlOptions);
        }
        
        @Deprecated
        public static CTHMerge parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTHMerge)getTypeLoader().parse(xmlInputStream, CTHMerge.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTHMerge parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTHMerge)getTypeLoader().parse(xmlInputStream, CTHMerge.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHMerge.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHMerge.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
