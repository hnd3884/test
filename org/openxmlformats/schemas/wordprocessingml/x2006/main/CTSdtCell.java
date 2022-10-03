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

public interface CTSdtCell extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSdtCell.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsdtcell626dtype");
    
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
    
    CTSdtContentCell getSdtContent();
    
    boolean isSetSdtContent();
    
    void setSdtContent(final CTSdtContentCell p0);
    
    CTSdtContentCell addNewSdtContent();
    
    void unsetSdtContent();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSdtCell.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSdtCell newInstance() {
            return (CTSdtCell)getTypeLoader().newInstance(CTSdtCell.type, (XmlOptions)null);
        }
        
        public static CTSdtCell newInstance(final XmlOptions xmlOptions) {
            return (CTSdtCell)getTypeLoader().newInstance(CTSdtCell.type, xmlOptions);
        }
        
        public static CTSdtCell parse(final String s) throws XmlException {
            return (CTSdtCell)getTypeLoader().parse(s, CTSdtCell.type, (XmlOptions)null);
        }
        
        public static CTSdtCell parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtCell)getTypeLoader().parse(s, CTSdtCell.type, xmlOptions);
        }
        
        public static CTSdtCell parse(final File file) throws XmlException, IOException {
            return (CTSdtCell)getTypeLoader().parse(file, CTSdtCell.type, (XmlOptions)null);
        }
        
        public static CTSdtCell parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtCell)getTypeLoader().parse(file, CTSdtCell.type, xmlOptions);
        }
        
        public static CTSdtCell parse(final URL url) throws XmlException, IOException {
            return (CTSdtCell)getTypeLoader().parse(url, CTSdtCell.type, (XmlOptions)null);
        }
        
        public static CTSdtCell parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtCell)getTypeLoader().parse(url, CTSdtCell.type, xmlOptions);
        }
        
        public static CTSdtCell parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSdtCell)getTypeLoader().parse(inputStream, CTSdtCell.type, (XmlOptions)null);
        }
        
        public static CTSdtCell parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtCell)getTypeLoader().parse(inputStream, CTSdtCell.type, xmlOptions);
        }
        
        public static CTSdtCell parse(final Reader reader) throws XmlException, IOException {
            return (CTSdtCell)getTypeLoader().parse(reader, CTSdtCell.type, (XmlOptions)null);
        }
        
        public static CTSdtCell parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtCell)getTypeLoader().parse(reader, CTSdtCell.type, xmlOptions);
        }
        
        public static CTSdtCell parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSdtCell)getTypeLoader().parse(xmlStreamReader, CTSdtCell.type, (XmlOptions)null);
        }
        
        public static CTSdtCell parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtCell)getTypeLoader().parse(xmlStreamReader, CTSdtCell.type, xmlOptions);
        }
        
        public static CTSdtCell parse(final Node node) throws XmlException {
            return (CTSdtCell)getTypeLoader().parse(node, CTSdtCell.type, (XmlOptions)null);
        }
        
        public static CTSdtCell parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtCell)getTypeLoader().parse(node, CTSdtCell.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSdtCell parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSdtCell)getTypeLoader().parse(xmlInputStream, CTSdtCell.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSdtCell parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSdtCell)getTypeLoader().parse(xmlInputStream, CTSdtCell.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSdtCell.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSdtCell.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
