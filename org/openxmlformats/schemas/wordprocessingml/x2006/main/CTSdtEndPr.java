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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSdtEndPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSdtEndPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsdtendprbc6etype");
    
    List<CTRPr> getRPrList();
    
    @Deprecated
    CTRPr[] getRPrArray();
    
    CTRPr getRPrArray(final int p0);
    
    int sizeOfRPrArray();
    
    void setRPrArray(final CTRPr[] p0);
    
    void setRPrArray(final int p0, final CTRPr p1);
    
    CTRPr insertNewRPr(final int p0);
    
    CTRPr addNewRPr();
    
    void removeRPr(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSdtEndPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSdtEndPr newInstance() {
            return (CTSdtEndPr)getTypeLoader().newInstance(CTSdtEndPr.type, (XmlOptions)null);
        }
        
        public static CTSdtEndPr newInstance(final XmlOptions xmlOptions) {
            return (CTSdtEndPr)getTypeLoader().newInstance(CTSdtEndPr.type, xmlOptions);
        }
        
        public static CTSdtEndPr parse(final String s) throws XmlException {
            return (CTSdtEndPr)getTypeLoader().parse(s, CTSdtEndPr.type, (XmlOptions)null);
        }
        
        public static CTSdtEndPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtEndPr)getTypeLoader().parse(s, CTSdtEndPr.type, xmlOptions);
        }
        
        public static CTSdtEndPr parse(final File file) throws XmlException, IOException {
            return (CTSdtEndPr)getTypeLoader().parse(file, CTSdtEndPr.type, (XmlOptions)null);
        }
        
        public static CTSdtEndPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtEndPr)getTypeLoader().parse(file, CTSdtEndPr.type, xmlOptions);
        }
        
        public static CTSdtEndPr parse(final URL url) throws XmlException, IOException {
            return (CTSdtEndPr)getTypeLoader().parse(url, CTSdtEndPr.type, (XmlOptions)null);
        }
        
        public static CTSdtEndPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtEndPr)getTypeLoader().parse(url, CTSdtEndPr.type, xmlOptions);
        }
        
        public static CTSdtEndPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSdtEndPr)getTypeLoader().parse(inputStream, CTSdtEndPr.type, (XmlOptions)null);
        }
        
        public static CTSdtEndPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtEndPr)getTypeLoader().parse(inputStream, CTSdtEndPr.type, xmlOptions);
        }
        
        public static CTSdtEndPr parse(final Reader reader) throws XmlException, IOException {
            return (CTSdtEndPr)getTypeLoader().parse(reader, CTSdtEndPr.type, (XmlOptions)null);
        }
        
        public static CTSdtEndPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSdtEndPr)getTypeLoader().parse(reader, CTSdtEndPr.type, xmlOptions);
        }
        
        public static CTSdtEndPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSdtEndPr)getTypeLoader().parse(xmlStreamReader, CTSdtEndPr.type, (XmlOptions)null);
        }
        
        public static CTSdtEndPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtEndPr)getTypeLoader().parse(xmlStreamReader, CTSdtEndPr.type, xmlOptions);
        }
        
        public static CTSdtEndPr parse(final Node node) throws XmlException {
            return (CTSdtEndPr)getTypeLoader().parse(node, CTSdtEndPr.type, (XmlOptions)null);
        }
        
        public static CTSdtEndPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSdtEndPr)getTypeLoader().parse(node, CTSdtEndPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSdtEndPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSdtEndPr)getTypeLoader().parse(xmlInputStream, CTSdtEndPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSdtEndPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSdtEndPr)getTypeLoader().parse(xmlInputStream, CTSdtEndPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSdtEndPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSdtEndPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
