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

public interface CTLevelText extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLevelText.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctleveltext0621type");
    
    String getVal();
    
    STString xgetVal();
    
    boolean isSetVal();
    
    void setVal(final String p0);
    
    void xsetVal(final STString p0);
    
    void unsetVal();
    
    STOnOff.Enum getNull();
    
    STOnOff xgetNull();
    
    boolean isSetNull();
    
    void setNull(final STOnOff.Enum p0);
    
    void xsetNull(final STOnOff p0);
    
    void unsetNull();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLevelText.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLevelText newInstance() {
            return (CTLevelText)getTypeLoader().newInstance(CTLevelText.type, (XmlOptions)null);
        }
        
        public static CTLevelText newInstance(final XmlOptions xmlOptions) {
            return (CTLevelText)getTypeLoader().newInstance(CTLevelText.type, xmlOptions);
        }
        
        public static CTLevelText parse(final String s) throws XmlException {
            return (CTLevelText)getTypeLoader().parse(s, CTLevelText.type, (XmlOptions)null);
        }
        
        public static CTLevelText parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLevelText)getTypeLoader().parse(s, CTLevelText.type, xmlOptions);
        }
        
        public static CTLevelText parse(final File file) throws XmlException, IOException {
            return (CTLevelText)getTypeLoader().parse(file, CTLevelText.type, (XmlOptions)null);
        }
        
        public static CTLevelText parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLevelText)getTypeLoader().parse(file, CTLevelText.type, xmlOptions);
        }
        
        public static CTLevelText parse(final URL url) throws XmlException, IOException {
            return (CTLevelText)getTypeLoader().parse(url, CTLevelText.type, (XmlOptions)null);
        }
        
        public static CTLevelText parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLevelText)getTypeLoader().parse(url, CTLevelText.type, xmlOptions);
        }
        
        public static CTLevelText parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLevelText)getTypeLoader().parse(inputStream, CTLevelText.type, (XmlOptions)null);
        }
        
        public static CTLevelText parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLevelText)getTypeLoader().parse(inputStream, CTLevelText.type, xmlOptions);
        }
        
        public static CTLevelText parse(final Reader reader) throws XmlException, IOException {
            return (CTLevelText)getTypeLoader().parse(reader, CTLevelText.type, (XmlOptions)null);
        }
        
        public static CTLevelText parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLevelText)getTypeLoader().parse(reader, CTLevelText.type, xmlOptions);
        }
        
        public static CTLevelText parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLevelText)getTypeLoader().parse(xmlStreamReader, CTLevelText.type, (XmlOptions)null);
        }
        
        public static CTLevelText parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLevelText)getTypeLoader().parse(xmlStreamReader, CTLevelText.type, xmlOptions);
        }
        
        public static CTLevelText parse(final Node node) throws XmlException {
            return (CTLevelText)getTypeLoader().parse(node, CTLevelText.type, (XmlOptions)null);
        }
        
        public static CTLevelText parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLevelText)getTypeLoader().parse(node, CTLevelText.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLevelText parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLevelText)getTypeLoader().parse(xmlInputStream, CTLevelText.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLevelText parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLevelText)getTypeLoader().parse(xmlInputStream, CTLevelText.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLevelText.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLevelText.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
