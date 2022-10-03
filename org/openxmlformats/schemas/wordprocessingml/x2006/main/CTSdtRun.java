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

public interface CTSdtRun extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSdtRun.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsdtrun5c60type");
    
    CTSdtPr getSdtPr();
    
    boolean isSetSdtPr();
    
    void setSdtPr(final CTSdtPr p0);
    
    CTSdtPr addNewSdtPr();
    
    void unsetSdtPr();
    
    CTSdtEndPr getSdtEndPr();
    
    boolean isSetSdtEndPr();
    
    void setSdtEndPr(final CTSdtEndPr p0);
    
    CTSdtEndPr addNewSdtEndPr();
    
    void unsetSdtEndPr();
    
    CTSdtContentRun getSdtContent();
    
    boolean isSetSdtContent();
    
    void setSdtContent(final CTSdtContentRun p0);
    
    CTSdtContentRun addNewSdtContent();
    
    void unsetSdtContent();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSdtRun.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSdtRun newInstance() {
            return (CTSdtRun)getTypeLoader().newInstance(CTSdtRun.type, (XmlOptions)null);
        }
        
        public static CTSdtRun newInstance(final XmlOptions xmlOptions) {
            return (CTSdtRun)getTypeLoader().newInstance(CTSdtRun.type, xmlOptions);
        }
        
        public static CTSdtRun parse(final String s) throws XmlException {
            return (CTSdtRun)getTypeLoader().parse(s, CTSdtRun.type, (XmlOptions)null);
        }
        
        public static CTSdtRun parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtRun)getTypeLoader().parse(s, CTSdtRun.type, xmlOptions);
        }
        
        public static CTSdtRun parse(final File file) throws XmlException, IOException {
            return (CTSdtRun)getTypeLoader().parse(file, CTSdtRun.type, (XmlOptions)null);
        }
        
        public static CTSdtRun parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtRun)getTypeLoader().parse(file, CTSdtRun.type, xmlOptions);
        }
        
        public static CTSdtRun parse(final URL url) throws XmlException, IOException {
            return (CTSdtRun)getTypeLoader().parse(url, CTSdtRun.type, (XmlOptions)null);
        }
        
        public static CTSdtRun parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtRun)getTypeLoader().parse(url, CTSdtRun.type, xmlOptions);
        }
        
        public static CTSdtRun parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSdtRun)getTypeLoader().parse(inputStream, CTSdtRun.type, (XmlOptions)null);
        }
        
        public static CTSdtRun parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtRun)getTypeLoader().parse(inputStream, CTSdtRun.type, xmlOptions);
        }
        
        public static CTSdtRun parse(final Reader reader) throws XmlException, IOException {
            return (CTSdtRun)getTypeLoader().parse(reader, CTSdtRun.type, (XmlOptions)null);
        }
        
        public static CTSdtRun parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtRun)getTypeLoader().parse(reader, CTSdtRun.type, xmlOptions);
        }
        
        public static CTSdtRun parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSdtRun)getTypeLoader().parse(xmlStreamReader, CTSdtRun.type, (XmlOptions)null);
        }
        
        public static CTSdtRun parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtRun)getTypeLoader().parse(xmlStreamReader, CTSdtRun.type, xmlOptions);
        }
        
        public static CTSdtRun parse(final Node node) throws XmlException {
            return (CTSdtRun)getTypeLoader().parse(node, CTSdtRun.type, (XmlOptions)null);
        }
        
        public static CTSdtRun parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtRun)getTypeLoader().parse(node, CTSdtRun.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSdtRun parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSdtRun)getTypeLoader().parse(xmlInputStream, CTSdtRun.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSdtRun parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSdtRun)getTypeLoader().parse(xmlInputStream, CTSdtRun.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSdtRun.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSdtRun.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
