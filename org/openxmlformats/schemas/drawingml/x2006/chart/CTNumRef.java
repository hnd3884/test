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

public interface CTNumRef extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNumRef.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnumref062ftype");
    
    String getF();
    
    XmlString xgetF();
    
    void setF(final String p0);
    
    void xsetF(final XmlString p0);
    
    CTNumData getNumCache();
    
    boolean isSetNumCache();
    
    void setNumCache(final CTNumData p0);
    
    CTNumData addNewNumCache();
    
    void unsetNumCache();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNumRef.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNumRef newInstance() {
            return (CTNumRef)getTypeLoader().newInstance(CTNumRef.type, (XmlOptions)null);
        }
        
        public static CTNumRef newInstance(final XmlOptions xmlOptions) {
            return (CTNumRef)getTypeLoader().newInstance(CTNumRef.type, xmlOptions);
        }
        
        public static CTNumRef parse(final String s) throws XmlException {
            return (CTNumRef)getTypeLoader().parse(s, CTNumRef.type, (XmlOptions)null);
        }
        
        public static CTNumRef parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumRef)getTypeLoader().parse(s, CTNumRef.type, xmlOptions);
        }
        
        public static CTNumRef parse(final File file) throws XmlException, IOException {
            return (CTNumRef)getTypeLoader().parse(file, CTNumRef.type, (XmlOptions)null);
        }
        
        public static CTNumRef parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumRef)getTypeLoader().parse(file, CTNumRef.type, xmlOptions);
        }
        
        public static CTNumRef parse(final URL url) throws XmlException, IOException {
            return (CTNumRef)getTypeLoader().parse(url, CTNumRef.type, (XmlOptions)null);
        }
        
        public static CTNumRef parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumRef)getTypeLoader().parse(url, CTNumRef.type, xmlOptions);
        }
        
        public static CTNumRef parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNumRef)getTypeLoader().parse(inputStream, CTNumRef.type, (XmlOptions)null);
        }
        
        public static CTNumRef parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumRef)getTypeLoader().parse(inputStream, CTNumRef.type, xmlOptions);
        }
        
        public static CTNumRef parse(final Reader reader) throws XmlException, IOException {
            return (CTNumRef)getTypeLoader().parse(reader, CTNumRef.type, (XmlOptions)null);
        }
        
        public static CTNumRef parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumRef)getTypeLoader().parse(reader, CTNumRef.type, xmlOptions);
        }
        
        public static CTNumRef parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNumRef)getTypeLoader().parse(xmlStreamReader, CTNumRef.type, (XmlOptions)null);
        }
        
        public static CTNumRef parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumRef)getTypeLoader().parse(xmlStreamReader, CTNumRef.type, xmlOptions);
        }
        
        public static CTNumRef parse(final Node node) throws XmlException {
            return (CTNumRef)getTypeLoader().parse(node, CTNumRef.type, (XmlOptions)null);
        }
        
        public static CTNumRef parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumRef)getTypeLoader().parse(node, CTNumRef.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNumRef parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNumRef)getTypeLoader().parse(xmlInputStream, CTNumRef.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNumRef parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNumRef)getTypeLoader().parse(xmlInputStream, CTNumRef.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumRef.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumRef.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
