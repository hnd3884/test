package org.openxmlformats.schemas.drawingml.x2006.chart;

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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTStrRef extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTStrRef.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctstrref5d1atype");
    
    String getF();
    
    XmlString xgetF();
    
    void setF(final String p0);
    
    void xsetF(final XmlString p0);
    
    CTStrData getStrCache();
    
    boolean isSetStrCache();
    
    void setStrCache(final CTStrData p0);
    
    CTStrData addNewStrCache();
    
    void unsetStrCache();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTStrRef.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTStrRef newInstance() {
            return (CTStrRef)getTypeLoader().newInstance(CTStrRef.type, (XmlOptions)null);
        }
        
        public static CTStrRef newInstance(final XmlOptions xmlOptions) {
            return (CTStrRef)getTypeLoader().newInstance(CTStrRef.type, xmlOptions);
        }
        
        public static CTStrRef parse(final String s) throws XmlException {
            return (CTStrRef)getTypeLoader().parse(s, CTStrRef.type, (XmlOptions)null);
        }
        
        public static CTStrRef parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTStrRef)getTypeLoader().parse(s, CTStrRef.type, xmlOptions);
        }
        
        public static CTStrRef parse(final File file) throws XmlException, IOException {
            return (CTStrRef)getTypeLoader().parse(file, CTStrRef.type, (XmlOptions)null);
        }
        
        public static CTStrRef parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStrRef)getTypeLoader().parse(file, CTStrRef.type, xmlOptions);
        }
        
        public static CTStrRef parse(final URL url) throws XmlException, IOException {
            return (CTStrRef)getTypeLoader().parse(url, CTStrRef.type, (XmlOptions)null);
        }
        
        public static CTStrRef parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStrRef)getTypeLoader().parse(url, CTStrRef.type, xmlOptions);
        }
        
        public static CTStrRef parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTStrRef)getTypeLoader().parse(inputStream, CTStrRef.type, (XmlOptions)null);
        }
        
        public static CTStrRef parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStrRef)getTypeLoader().parse(inputStream, CTStrRef.type, xmlOptions);
        }
        
        public static CTStrRef parse(final Reader reader) throws XmlException, IOException {
            return (CTStrRef)getTypeLoader().parse(reader, CTStrRef.type, (XmlOptions)null);
        }
        
        public static CTStrRef parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStrRef)getTypeLoader().parse(reader, CTStrRef.type, xmlOptions);
        }
        
        public static CTStrRef parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTStrRef)getTypeLoader().parse(xmlStreamReader, CTStrRef.type, (XmlOptions)null);
        }
        
        public static CTStrRef parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTStrRef)getTypeLoader().parse(xmlStreamReader, CTStrRef.type, xmlOptions);
        }
        
        public static CTStrRef parse(final Node node) throws XmlException {
            return (CTStrRef)getTypeLoader().parse(node, CTStrRef.type, (XmlOptions)null);
        }
        
        public static CTStrRef parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTStrRef)getTypeLoader().parse(node, CTStrRef.type, xmlOptions);
        }
        
        @Deprecated
        public static CTStrRef parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTStrRef)getTypeLoader().parse(xmlInputStream, CTStrRef.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTStrRef parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTStrRef)getTypeLoader().parse(xmlInputStream, CTStrRef.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStrRef.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStrRef.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
