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

public interface CTSdtBlock extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSdtBlock.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsdtblock221etype");
    
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
    
    CTSdtContentBlock getSdtContent();
    
    boolean isSetSdtContent();
    
    void setSdtContent(final CTSdtContentBlock p0);
    
    CTSdtContentBlock addNewSdtContent();
    
    void unsetSdtContent();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSdtBlock.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSdtBlock newInstance() {
            return (CTSdtBlock)getTypeLoader().newInstance(CTSdtBlock.type, (XmlOptions)null);
        }
        
        public static CTSdtBlock newInstance(final XmlOptions xmlOptions) {
            return (CTSdtBlock)getTypeLoader().newInstance(CTSdtBlock.type, xmlOptions);
        }
        
        public static CTSdtBlock parse(final String s) throws XmlException {
            return (CTSdtBlock)getTypeLoader().parse(s, CTSdtBlock.type, (XmlOptions)null);
        }
        
        public static CTSdtBlock parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtBlock)getTypeLoader().parse(s, CTSdtBlock.type, xmlOptions);
        }
        
        public static CTSdtBlock parse(final File file) throws XmlException, IOException {
            return (CTSdtBlock)getTypeLoader().parse(file, CTSdtBlock.type, (XmlOptions)null);
        }
        
        public static CTSdtBlock parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtBlock)getTypeLoader().parse(file, CTSdtBlock.type, xmlOptions);
        }
        
        public static CTSdtBlock parse(final URL url) throws XmlException, IOException {
            return (CTSdtBlock)getTypeLoader().parse(url, CTSdtBlock.type, (XmlOptions)null);
        }
        
        public static CTSdtBlock parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtBlock)getTypeLoader().parse(url, CTSdtBlock.type, xmlOptions);
        }
        
        public static CTSdtBlock parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSdtBlock)getTypeLoader().parse(inputStream, CTSdtBlock.type, (XmlOptions)null);
        }
        
        public static CTSdtBlock parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtBlock)getTypeLoader().parse(inputStream, CTSdtBlock.type, xmlOptions);
        }
        
        public static CTSdtBlock parse(final Reader reader) throws XmlException, IOException {
            return (CTSdtBlock)getTypeLoader().parse(reader, CTSdtBlock.type, (XmlOptions)null);
        }
        
        public static CTSdtBlock parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtBlock)getTypeLoader().parse(reader, CTSdtBlock.type, xmlOptions);
        }
        
        public static CTSdtBlock parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSdtBlock)getTypeLoader().parse(xmlStreamReader, CTSdtBlock.type, (XmlOptions)null);
        }
        
        public static CTSdtBlock parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtBlock)getTypeLoader().parse(xmlStreamReader, CTSdtBlock.type, xmlOptions);
        }
        
        public static CTSdtBlock parse(final Node node) throws XmlException {
            return (CTSdtBlock)getTypeLoader().parse(node, CTSdtBlock.type, (XmlOptions)null);
        }
        
        public static CTSdtBlock parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtBlock)getTypeLoader().parse(node, CTSdtBlock.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSdtBlock parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSdtBlock)getTypeLoader().parse(xmlInputStream, CTSdtBlock.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSdtBlock parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSdtBlock)getTypeLoader().parse(xmlInputStream, CTSdtBlock.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSdtBlock.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSdtBlock.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
