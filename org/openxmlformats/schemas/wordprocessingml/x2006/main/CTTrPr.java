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

public interface CTTrPr extends CTTrPrBase
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTrPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttrpr2848type");
    
    CTTrackChange getIns();
    
    boolean isSetIns();
    
    void setIns(final CTTrackChange p0);
    
    CTTrackChange addNewIns();
    
    void unsetIns();
    
    CTTrackChange getDel();
    
    boolean isSetDel();
    
    void setDel(final CTTrackChange p0);
    
    CTTrackChange addNewDel();
    
    void unsetDel();
    
    CTTrPrChange getTrPrChange();
    
    boolean isSetTrPrChange();
    
    void setTrPrChange(final CTTrPrChange p0);
    
    CTTrPrChange addNewTrPrChange();
    
    void unsetTrPrChange();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTrPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTrPr newInstance() {
            return (CTTrPr)getTypeLoader().newInstance(CTTrPr.type, (XmlOptions)null);
        }
        
        public static CTTrPr newInstance(final XmlOptions xmlOptions) {
            return (CTTrPr)getTypeLoader().newInstance(CTTrPr.type, xmlOptions);
        }
        
        public static CTTrPr parse(final String s) throws XmlException {
            return (CTTrPr)getTypeLoader().parse(s, CTTrPr.type, (XmlOptions)null);
        }
        
        public static CTTrPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTrPr)getTypeLoader().parse(s, CTTrPr.type, xmlOptions);
        }
        
        public static CTTrPr parse(final File file) throws XmlException, IOException {
            return (CTTrPr)getTypeLoader().parse(file, CTTrPr.type, (XmlOptions)null);
        }
        
        public static CTTrPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTrPr)getTypeLoader().parse(file, CTTrPr.type, xmlOptions);
        }
        
        public static CTTrPr parse(final URL url) throws XmlException, IOException {
            return (CTTrPr)getTypeLoader().parse(url, CTTrPr.type, (XmlOptions)null);
        }
        
        public static CTTrPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTrPr)getTypeLoader().parse(url, CTTrPr.type, xmlOptions);
        }
        
        public static CTTrPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTrPr)getTypeLoader().parse(inputStream, CTTrPr.type, (XmlOptions)null);
        }
        
        public static CTTrPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTrPr)getTypeLoader().parse(inputStream, CTTrPr.type, xmlOptions);
        }
        
        public static CTTrPr parse(final Reader reader) throws XmlException, IOException {
            return (CTTrPr)getTypeLoader().parse(reader, CTTrPr.type, (XmlOptions)null);
        }
        
        public static CTTrPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTrPr)getTypeLoader().parse(reader, CTTrPr.type, xmlOptions);
        }
        
        public static CTTrPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTrPr)getTypeLoader().parse(xmlStreamReader, CTTrPr.type, (XmlOptions)null);
        }
        
        public static CTTrPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTrPr)getTypeLoader().parse(xmlStreamReader, CTTrPr.type, xmlOptions);
        }
        
        public static CTTrPr parse(final Node node) throws XmlException {
            return (CTTrPr)getTypeLoader().parse(node, CTTrPr.type, (XmlOptions)null);
        }
        
        public static CTTrPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTrPr)getTypeLoader().parse(node, CTTrPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTrPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTrPr)getTypeLoader().parse(xmlInputStream, CTTrPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTrPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTrPr)getTypeLoader().parse(xmlInputStream, CTTrPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTrPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTrPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
