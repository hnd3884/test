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

public interface CTVMerge extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTVMerge.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctvmergee086type");
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTVMerge.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTVMerge newInstance() {
            return (CTVMerge)getTypeLoader().newInstance(CTVMerge.type, (XmlOptions)null);
        }
        
        public static CTVMerge newInstance(final XmlOptions xmlOptions) {
            return (CTVMerge)getTypeLoader().newInstance(CTVMerge.type, xmlOptions);
        }
        
        public static CTVMerge parse(final String s) throws XmlException {
            return (CTVMerge)getTypeLoader().parse(s, CTVMerge.type, (XmlOptions)null);
        }
        
        public static CTVMerge parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTVMerge)getTypeLoader().parse(s, CTVMerge.type, xmlOptions);
        }
        
        public static CTVMerge parse(final File file) throws XmlException, IOException {
            return (CTVMerge)getTypeLoader().parse(file, CTVMerge.type, (XmlOptions)null);
        }
        
        public static CTVMerge parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVMerge)getTypeLoader().parse(file, CTVMerge.type, xmlOptions);
        }
        
        public static CTVMerge parse(final URL url) throws XmlException, IOException {
            return (CTVMerge)getTypeLoader().parse(url, CTVMerge.type, (XmlOptions)null);
        }
        
        public static CTVMerge parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVMerge)getTypeLoader().parse(url, CTVMerge.type, xmlOptions);
        }
        
        public static CTVMerge parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTVMerge)getTypeLoader().parse(inputStream, CTVMerge.type, (XmlOptions)null);
        }
        
        public static CTVMerge parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVMerge)getTypeLoader().parse(inputStream, CTVMerge.type, xmlOptions);
        }
        
        public static CTVMerge parse(final Reader reader) throws XmlException, IOException {
            return (CTVMerge)getTypeLoader().parse(reader, CTVMerge.type, (XmlOptions)null);
        }
        
        public static CTVMerge parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVMerge)getTypeLoader().parse(reader, CTVMerge.type, xmlOptions);
        }
        
        public static CTVMerge parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTVMerge)getTypeLoader().parse(xmlStreamReader, CTVMerge.type, (XmlOptions)null);
        }
        
        public static CTVMerge parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTVMerge)getTypeLoader().parse(xmlStreamReader, CTVMerge.type, xmlOptions);
        }
        
        public static CTVMerge parse(final Node node) throws XmlException {
            return (CTVMerge)getTypeLoader().parse(node, CTVMerge.type, (XmlOptions)null);
        }
        
        public static CTVMerge parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTVMerge)getTypeLoader().parse(node, CTVMerge.type, xmlOptions);
        }
        
        @Deprecated
        public static CTVMerge parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTVMerge)getTypeLoader().parse(xmlInputStream, CTVMerge.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTVMerge parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTVMerge)getTypeLoader().parse(xmlInputStream, CTVMerge.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVMerge.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVMerge.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
