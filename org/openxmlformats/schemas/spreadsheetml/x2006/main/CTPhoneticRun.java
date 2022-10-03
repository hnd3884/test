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

public interface CTPhoneticRun extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPhoneticRun.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctphoneticrun2b2atype");
    
    String getT();
    
    STXstring xgetT();
    
    void setT(final String p0);
    
    void xsetT(final STXstring p0);
    
    long getSb();
    
    XmlUnsignedInt xgetSb();
    
    void setSb(final long p0);
    
    void xsetSb(final XmlUnsignedInt p0);
    
    long getEb();
    
    XmlUnsignedInt xgetEb();
    
    void setEb(final long p0);
    
    void xsetEb(final XmlUnsignedInt p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPhoneticRun.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPhoneticRun newInstance() {
            return (CTPhoneticRun)getTypeLoader().newInstance(CTPhoneticRun.type, (XmlOptions)null);
        }
        
        public static CTPhoneticRun newInstance(final XmlOptions xmlOptions) {
            return (CTPhoneticRun)getTypeLoader().newInstance(CTPhoneticRun.type, xmlOptions);
        }
        
        public static CTPhoneticRun parse(final String s) throws XmlException {
            return (CTPhoneticRun)getTypeLoader().parse(s, CTPhoneticRun.type, (XmlOptions)null);
        }
        
        public static CTPhoneticRun parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPhoneticRun)getTypeLoader().parse(s, CTPhoneticRun.type, xmlOptions);
        }
        
        public static CTPhoneticRun parse(final File file) throws XmlException, IOException {
            return (CTPhoneticRun)getTypeLoader().parse(file, CTPhoneticRun.type, (XmlOptions)null);
        }
        
        public static CTPhoneticRun parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPhoneticRun)getTypeLoader().parse(file, CTPhoneticRun.type, xmlOptions);
        }
        
        public static CTPhoneticRun parse(final URL url) throws XmlException, IOException {
            return (CTPhoneticRun)getTypeLoader().parse(url, CTPhoneticRun.type, (XmlOptions)null);
        }
        
        public static CTPhoneticRun parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPhoneticRun)getTypeLoader().parse(url, CTPhoneticRun.type, xmlOptions);
        }
        
        public static CTPhoneticRun parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPhoneticRun)getTypeLoader().parse(inputStream, CTPhoneticRun.type, (XmlOptions)null);
        }
        
        public static CTPhoneticRun parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPhoneticRun)getTypeLoader().parse(inputStream, CTPhoneticRun.type, xmlOptions);
        }
        
        public static CTPhoneticRun parse(final Reader reader) throws XmlException, IOException {
            return (CTPhoneticRun)getTypeLoader().parse(reader, CTPhoneticRun.type, (XmlOptions)null);
        }
        
        public static CTPhoneticRun parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPhoneticRun)getTypeLoader().parse(reader, CTPhoneticRun.type, xmlOptions);
        }
        
        public static CTPhoneticRun parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPhoneticRun)getTypeLoader().parse(xmlStreamReader, CTPhoneticRun.type, (XmlOptions)null);
        }
        
        public static CTPhoneticRun parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPhoneticRun)getTypeLoader().parse(xmlStreamReader, CTPhoneticRun.type, xmlOptions);
        }
        
        public static CTPhoneticRun parse(final Node node) throws XmlException {
            return (CTPhoneticRun)getTypeLoader().parse(node, CTPhoneticRun.type, (XmlOptions)null);
        }
        
        public static CTPhoneticRun parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPhoneticRun)getTypeLoader().parse(node, CTPhoneticRun.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPhoneticRun parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPhoneticRun)getTypeLoader().parse(xmlInputStream, CTPhoneticRun.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPhoneticRun parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPhoneticRun)getTypeLoader().parse(xmlInputStream, CTPhoneticRun.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPhoneticRun.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPhoneticRun.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
