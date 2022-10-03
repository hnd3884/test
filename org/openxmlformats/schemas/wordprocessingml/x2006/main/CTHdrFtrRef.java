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

public interface CTHdrFtrRef extends CTRel
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTHdrFtrRef.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cthdrftrref224dtype");
    
    STHdrFtr.Enum getType();
    
    STHdrFtr xgetType();
    
    void setType(final STHdrFtr.Enum p0);
    
    void xsetType(final STHdrFtr p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTHdrFtrRef.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTHdrFtrRef newInstance() {
            return (CTHdrFtrRef)getTypeLoader().newInstance(CTHdrFtrRef.type, (XmlOptions)null);
        }
        
        public static CTHdrFtrRef newInstance(final XmlOptions xmlOptions) {
            return (CTHdrFtrRef)getTypeLoader().newInstance(CTHdrFtrRef.type, xmlOptions);
        }
        
        public static CTHdrFtrRef parse(final String s) throws XmlException {
            return (CTHdrFtrRef)getTypeLoader().parse(s, CTHdrFtrRef.type, (XmlOptions)null);
        }
        
        public static CTHdrFtrRef parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTHdrFtrRef)getTypeLoader().parse(s, CTHdrFtrRef.type, xmlOptions);
        }
        
        public static CTHdrFtrRef parse(final File file) throws XmlException, IOException {
            return (CTHdrFtrRef)getTypeLoader().parse(file, CTHdrFtrRef.type, (XmlOptions)null);
        }
        
        public static CTHdrFtrRef parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHdrFtrRef)getTypeLoader().parse(file, CTHdrFtrRef.type, xmlOptions);
        }
        
        public static CTHdrFtrRef parse(final URL url) throws XmlException, IOException {
            return (CTHdrFtrRef)getTypeLoader().parse(url, CTHdrFtrRef.type, (XmlOptions)null);
        }
        
        public static CTHdrFtrRef parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHdrFtrRef)getTypeLoader().parse(url, CTHdrFtrRef.type, xmlOptions);
        }
        
        public static CTHdrFtrRef parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTHdrFtrRef)getTypeLoader().parse(inputStream, CTHdrFtrRef.type, (XmlOptions)null);
        }
        
        public static CTHdrFtrRef parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHdrFtrRef)getTypeLoader().parse(inputStream, CTHdrFtrRef.type, xmlOptions);
        }
        
        public static CTHdrFtrRef parse(final Reader reader) throws XmlException, IOException {
            return (CTHdrFtrRef)getTypeLoader().parse(reader, CTHdrFtrRef.type, (XmlOptions)null);
        }
        
        public static CTHdrFtrRef parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHdrFtrRef)getTypeLoader().parse(reader, CTHdrFtrRef.type, xmlOptions);
        }
        
        public static CTHdrFtrRef parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTHdrFtrRef)getTypeLoader().parse(xmlStreamReader, CTHdrFtrRef.type, (XmlOptions)null);
        }
        
        public static CTHdrFtrRef parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTHdrFtrRef)getTypeLoader().parse(xmlStreamReader, CTHdrFtrRef.type, xmlOptions);
        }
        
        public static CTHdrFtrRef parse(final Node node) throws XmlException {
            return (CTHdrFtrRef)getTypeLoader().parse(node, CTHdrFtrRef.type, (XmlOptions)null);
        }
        
        public static CTHdrFtrRef parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTHdrFtrRef)getTypeLoader().parse(node, CTHdrFtrRef.type, xmlOptions);
        }
        
        @Deprecated
        public static CTHdrFtrRef parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTHdrFtrRef)getTypeLoader().parse(xmlInputStream, CTHdrFtrRef.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTHdrFtrRef parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTHdrFtrRef)getTypeLoader().parse(xmlInputStream, CTHdrFtrRef.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHdrFtrRef.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHdrFtrRef.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
