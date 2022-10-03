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

public interface CTRElt extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRElt.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctrelt6464type");
    
    CTRPrElt getRPr();
    
    boolean isSetRPr();
    
    void setRPr(final CTRPrElt p0);
    
    CTRPrElt addNewRPr();
    
    void unsetRPr();
    
    String getT();
    
    STXstring xgetT();
    
    void setT(final String p0);
    
    void xsetT(final STXstring p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRElt.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRElt newInstance() {
            return (CTRElt)getTypeLoader().newInstance(CTRElt.type, (XmlOptions)null);
        }
        
        public static CTRElt newInstance(final XmlOptions xmlOptions) {
            return (CTRElt)getTypeLoader().newInstance(CTRElt.type, xmlOptions);
        }
        
        public static CTRElt parse(final String s) throws XmlException {
            return (CTRElt)getTypeLoader().parse(s, CTRElt.type, (XmlOptions)null);
        }
        
        public static CTRElt parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRElt)getTypeLoader().parse(s, CTRElt.type, xmlOptions);
        }
        
        public static CTRElt parse(final File file) throws XmlException, IOException {
            return (CTRElt)getTypeLoader().parse(file, CTRElt.type, (XmlOptions)null);
        }
        
        public static CTRElt parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRElt)getTypeLoader().parse(file, CTRElt.type, xmlOptions);
        }
        
        public static CTRElt parse(final URL url) throws XmlException, IOException {
            return (CTRElt)getTypeLoader().parse(url, CTRElt.type, (XmlOptions)null);
        }
        
        public static CTRElt parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRElt)getTypeLoader().parse(url, CTRElt.type, xmlOptions);
        }
        
        public static CTRElt parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRElt)getTypeLoader().parse(inputStream, CTRElt.type, (XmlOptions)null);
        }
        
        public static CTRElt parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRElt)getTypeLoader().parse(inputStream, CTRElt.type, xmlOptions);
        }
        
        public static CTRElt parse(final Reader reader) throws XmlException, IOException {
            return (CTRElt)getTypeLoader().parse(reader, CTRElt.type, (XmlOptions)null);
        }
        
        public static CTRElt parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRElt)getTypeLoader().parse(reader, CTRElt.type, xmlOptions);
        }
        
        public static CTRElt parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRElt)getTypeLoader().parse(xmlStreamReader, CTRElt.type, (XmlOptions)null);
        }
        
        public static CTRElt parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRElt)getTypeLoader().parse(xmlStreamReader, CTRElt.type, xmlOptions);
        }
        
        public static CTRElt parse(final Node node) throws XmlException {
            return (CTRElt)getTypeLoader().parse(node, CTRElt.type, (XmlOptions)null);
        }
        
        public static CTRElt parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRElt)getTypeLoader().parse(node, CTRElt.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRElt parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRElt)getTypeLoader().parse(xmlInputStream, CTRElt.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRElt parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRElt)getTypeLoader().parse(xmlInputStream, CTRElt.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRElt.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRElt.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
