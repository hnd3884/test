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

public interface CTEm extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTEm.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctemdc80type");
    
    STEm.Enum getVal();
    
    STEm xgetVal();
    
    void setVal(final STEm.Enum p0);
    
    void xsetVal(final STEm p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTEm.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTEm newInstance() {
            return (CTEm)getTypeLoader().newInstance(CTEm.type, (XmlOptions)null);
        }
        
        public static CTEm newInstance(final XmlOptions xmlOptions) {
            return (CTEm)getTypeLoader().newInstance(CTEm.type, xmlOptions);
        }
        
        public static CTEm parse(final String s) throws XmlException {
            return (CTEm)getTypeLoader().parse(s, CTEm.type, (XmlOptions)null);
        }
        
        public static CTEm parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTEm)getTypeLoader().parse(s, CTEm.type, xmlOptions);
        }
        
        public static CTEm parse(final File file) throws XmlException, IOException {
            return (CTEm)getTypeLoader().parse(file, CTEm.type, (XmlOptions)null);
        }
        
        public static CTEm parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEm)getTypeLoader().parse(file, CTEm.type, xmlOptions);
        }
        
        public static CTEm parse(final URL url) throws XmlException, IOException {
            return (CTEm)getTypeLoader().parse(url, CTEm.type, (XmlOptions)null);
        }
        
        public static CTEm parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEm)getTypeLoader().parse(url, CTEm.type, xmlOptions);
        }
        
        public static CTEm parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTEm)getTypeLoader().parse(inputStream, CTEm.type, (XmlOptions)null);
        }
        
        public static CTEm parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEm)getTypeLoader().parse(inputStream, CTEm.type, xmlOptions);
        }
        
        public static CTEm parse(final Reader reader) throws XmlException, IOException {
            return (CTEm)getTypeLoader().parse(reader, CTEm.type, (XmlOptions)null);
        }
        
        public static CTEm parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEm)getTypeLoader().parse(reader, CTEm.type, xmlOptions);
        }
        
        public static CTEm parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTEm)getTypeLoader().parse(xmlStreamReader, CTEm.type, (XmlOptions)null);
        }
        
        public static CTEm parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTEm)getTypeLoader().parse(xmlStreamReader, CTEm.type, xmlOptions);
        }
        
        public static CTEm parse(final Node node) throws XmlException {
            return (CTEm)getTypeLoader().parse(node, CTEm.type, (XmlOptions)null);
        }
        
        public static CTEm parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTEm)getTypeLoader().parse(node, CTEm.type, xmlOptions);
        }
        
        @Deprecated
        public static CTEm parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTEm)getTypeLoader().parse(xmlInputStream, CTEm.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTEm parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTEm)getTypeLoader().parse(xmlInputStream, CTEm.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEm.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEm.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
