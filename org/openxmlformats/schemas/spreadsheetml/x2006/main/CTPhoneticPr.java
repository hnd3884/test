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

public interface CTPhoneticPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPhoneticPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctphoneticpr898btype");
    
    long getFontId();
    
    STFontId xgetFontId();
    
    void setFontId(final long p0);
    
    void xsetFontId(final STFontId p0);
    
    STPhoneticType.Enum getType();
    
    STPhoneticType xgetType();
    
    boolean isSetType();
    
    void setType(final STPhoneticType.Enum p0);
    
    void xsetType(final STPhoneticType p0);
    
    void unsetType();
    
    STPhoneticAlignment.Enum getAlignment();
    
    STPhoneticAlignment xgetAlignment();
    
    boolean isSetAlignment();
    
    void setAlignment(final STPhoneticAlignment.Enum p0);
    
    void xsetAlignment(final STPhoneticAlignment p0);
    
    void unsetAlignment();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPhoneticPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPhoneticPr newInstance() {
            return (CTPhoneticPr)getTypeLoader().newInstance(CTPhoneticPr.type, (XmlOptions)null);
        }
        
        public static CTPhoneticPr newInstance(final XmlOptions xmlOptions) {
            return (CTPhoneticPr)getTypeLoader().newInstance(CTPhoneticPr.type, xmlOptions);
        }
        
        public static CTPhoneticPr parse(final String s) throws XmlException {
            return (CTPhoneticPr)getTypeLoader().parse(s, CTPhoneticPr.type, (XmlOptions)null);
        }
        
        public static CTPhoneticPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPhoneticPr)getTypeLoader().parse(s, CTPhoneticPr.type, xmlOptions);
        }
        
        public static CTPhoneticPr parse(final File file) throws XmlException, IOException {
            return (CTPhoneticPr)getTypeLoader().parse(file, CTPhoneticPr.type, (XmlOptions)null);
        }
        
        public static CTPhoneticPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPhoneticPr)getTypeLoader().parse(file, CTPhoneticPr.type, xmlOptions);
        }
        
        public static CTPhoneticPr parse(final URL url) throws XmlException, IOException {
            return (CTPhoneticPr)getTypeLoader().parse(url, CTPhoneticPr.type, (XmlOptions)null);
        }
        
        public static CTPhoneticPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPhoneticPr)getTypeLoader().parse(url, CTPhoneticPr.type, xmlOptions);
        }
        
        public static CTPhoneticPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPhoneticPr)getTypeLoader().parse(inputStream, CTPhoneticPr.type, (XmlOptions)null);
        }
        
        public static CTPhoneticPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPhoneticPr)getTypeLoader().parse(inputStream, CTPhoneticPr.type, xmlOptions);
        }
        
        public static CTPhoneticPr parse(final Reader reader) throws XmlException, IOException {
            return (CTPhoneticPr)getTypeLoader().parse(reader, CTPhoneticPr.type, (XmlOptions)null);
        }
        
        public static CTPhoneticPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPhoneticPr)getTypeLoader().parse(reader, CTPhoneticPr.type, xmlOptions);
        }
        
        public static CTPhoneticPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPhoneticPr)getTypeLoader().parse(xmlStreamReader, CTPhoneticPr.type, (XmlOptions)null);
        }
        
        public static CTPhoneticPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPhoneticPr)getTypeLoader().parse(xmlStreamReader, CTPhoneticPr.type, xmlOptions);
        }
        
        public static CTPhoneticPr parse(final Node node) throws XmlException {
            return (CTPhoneticPr)getTypeLoader().parse(node, CTPhoneticPr.type, (XmlOptions)null);
        }
        
        public static CTPhoneticPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPhoneticPr)getTypeLoader().parse(node, CTPhoneticPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPhoneticPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPhoneticPr)getTypeLoader().parse(xmlInputStream, CTPhoneticPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPhoneticPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPhoneticPr)getTypeLoader().parse(xmlInputStream, CTPhoneticPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPhoneticPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPhoneticPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
