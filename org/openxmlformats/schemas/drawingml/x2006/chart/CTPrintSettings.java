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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPrintSettings extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPrintSettings.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctprintsettings61b6type");
    
    CTHeaderFooter getHeaderFooter();
    
    boolean isSetHeaderFooter();
    
    void setHeaderFooter(final CTHeaderFooter p0);
    
    CTHeaderFooter addNewHeaderFooter();
    
    void unsetHeaderFooter();
    
    CTPageMargins getPageMargins();
    
    boolean isSetPageMargins();
    
    void setPageMargins(final CTPageMargins p0);
    
    CTPageMargins addNewPageMargins();
    
    void unsetPageMargins();
    
    CTPageSetup getPageSetup();
    
    boolean isSetPageSetup();
    
    void setPageSetup(final CTPageSetup p0);
    
    CTPageSetup addNewPageSetup();
    
    void unsetPageSetup();
    
    CTRelId getLegacyDrawingHF();
    
    boolean isSetLegacyDrawingHF();
    
    void setLegacyDrawingHF(final CTRelId p0);
    
    CTRelId addNewLegacyDrawingHF();
    
    void unsetLegacyDrawingHF();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPrintSettings.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPrintSettings newInstance() {
            return (CTPrintSettings)getTypeLoader().newInstance(CTPrintSettings.type, (XmlOptions)null);
        }
        
        public static CTPrintSettings newInstance(final XmlOptions xmlOptions) {
            return (CTPrintSettings)getTypeLoader().newInstance(CTPrintSettings.type, xmlOptions);
        }
        
        public static CTPrintSettings parse(final String s) throws XmlException {
            return (CTPrintSettings)getTypeLoader().parse(s, CTPrintSettings.type, (XmlOptions)null);
        }
        
        public static CTPrintSettings parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPrintSettings)getTypeLoader().parse(s, CTPrintSettings.type, xmlOptions);
        }
        
        public static CTPrintSettings parse(final File file) throws XmlException, IOException {
            return (CTPrintSettings)getTypeLoader().parse(file, CTPrintSettings.type, (XmlOptions)null);
        }
        
        public static CTPrintSettings parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPrintSettings)getTypeLoader().parse(file, CTPrintSettings.type, xmlOptions);
        }
        
        public static CTPrintSettings parse(final URL url) throws XmlException, IOException {
            return (CTPrintSettings)getTypeLoader().parse(url, CTPrintSettings.type, (XmlOptions)null);
        }
        
        public static CTPrintSettings parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPrintSettings)getTypeLoader().parse(url, CTPrintSettings.type, xmlOptions);
        }
        
        public static CTPrintSettings parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPrintSettings)getTypeLoader().parse(inputStream, CTPrintSettings.type, (XmlOptions)null);
        }
        
        public static CTPrintSettings parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPrintSettings)getTypeLoader().parse(inputStream, CTPrintSettings.type, xmlOptions);
        }
        
        public static CTPrintSettings parse(final Reader reader) throws XmlException, IOException {
            return (CTPrintSettings)getTypeLoader().parse(reader, CTPrintSettings.type, (XmlOptions)null);
        }
        
        public static CTPrintSettings parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPrintSettings)getTypeLoader().parse(reader, CTPrintSettings.type, xmlOptions);
        }
        
        public static CTPrintSettings parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPrintSettings)getTypeLoader().parse(xmlStreamReader, CTPrintSettings.type, (XmlOptions)null);
        }
        
        public static CTPrintSettings parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPrintSettings)getTypeLoader().parse(xmlStreamReader, CTPrintSettings.type, xmlOptions);
        }
        
        public static CTPrintSettings parse(final Node node) throws XmlException {
            return (CTPrintSettings)getTypeLoader().parse(node, CTPrintSettings.type, (XmlOptions)null);
        }
        
        public static CTPrintSettings parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPrintSettings)getTypeLoader().parse(node, CTPrintSettings.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPrintSettings parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPrintSettings)getTypeLoader().parse(xmlInputStream, CTPrintSettings.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPrintSettings parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPrintSettings)getTypeLoader().parse(xmlInputStream, CTPrintSettings.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPrintSettings.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPrintSettings.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
