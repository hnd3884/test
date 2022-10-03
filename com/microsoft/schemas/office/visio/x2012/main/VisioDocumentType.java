package com.microsoft.schemas.office.visio.x2012.main;

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

public interface VisioDocumentType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(VisioDocumentType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("visiodocumenttypebfcatype");
    
    DocumentSettingsType getDocumentSettings();
    
    boolean isSetDocumentSettings();
    
    void setDocumentSettings(final DocumentSettingsType p0);
    
    DocumentSettingsType addNewDocumentSettings();
    
    void unsetDocumentSettings();
    
    ColorsType getColors();
    
    boolean isSetColors();
    
    void setColors(final ColorsType p0);
    
    ColorsType addNewColors();
    
    void unsetColors();
    
    FaceNamesType getFaceNames();
    
    boolean isSetFaceNames();
    
    void setFaceNames(final FaceNamesType p0);
    
    FaceNamesType addNewFaceNames();
    
    void unsetFaceNames();
    
    StyleSheetsType getStyleSheets();
    
    boolean isSetStyleSheets();
    
    void setStyleSheets(final StyleSheetsType p0);
    
    StyleSheetsType addNewStyleSheets();
    
    void unsetStyleSheets();
    
    DocumentSheetType getDocumentSheet();
    
    boolean isSetDocumentSheet();
    
    void setDocumentSheet(final DocumentSheetType p0);
    
    DocumentSheetType addNewDocumentSheet();
    
    void unsetDocumentSheet();
    
    EventListType getEventList();
    
    boolean isSetEventList();
    
    void setEventList(final EventListType p0);
    
    EventListType addNewEventList();
    
    void unsetEventList();
    
    HeaderFooterType getHeaderFooter();
    
    boolean isSetHeaderFooter();
    
    void setHeaderFooter(final HeaderFooterType p0);
    
    HeaderFooterType addNewHeaderFooter();
    
    void unsetHeaderFooter();
    
    PublishSettingsType getPublishSettings();
    
    boolean isSetPublishSettings();
    
    void setPublishSettings(final PublishSettingsType p0);
    
    PublishSettingsType addNewPublishSettings();
    
    void unsetPublishSettings();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(VisioDocumentType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static VisioDocumentType newInstance() {
            return (VisioDocumentType)getTypeLoader().newInstance(VisioDocumentType.type, (XmlOptions)null);
        }
        
        public static VisioDocumentType newInstance(final XmlOptions xmlOptions) {
            return (VisioDocumentType)getTypeLoader().newInstance(VisioDocumentType.type, xmlOptions);
        }
        
        public static VisioDocumentType parse(final String s) throws XmlException {
            return (VisioDocumentType)getTypeLoader().parse(s, VisioDocumentType.type, (XmlOptions)null);
        }
        
        public static VisioDocumentType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (VisioDocumentType)getTypeLoader().parse(s, VisioDocumentType.type, xmlOptions);
        }
        
        public static VisioDocumentType parse(final File file) throws XmlException, IOException {
            return (VisioDocumentType)getTypeLoader().parse(file, VisioDocumentType.type, (XmlOptions)null);
        }
        
        public static VisioDocumentType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (VisioDocumentType)getTypeLoader().parse(file, VisioDocumentType.type, xmlOptions);
        }
        
        public static VisioDocumentType parse(final URL url) throws XmlException, IOException {
            return (VisioDocumentType)getTypeLoader().parse(url, VisioDocumentType.type, (XmlOptions)null);
        }
        
        public static VisioDocumentType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (VisioDocumentType)getTypeLoader().parse(url, VisioDocumentType.type, xmlOptions);
        }
        
        public static VisioDocumentType parse(final InputStream inputStream) throws XmlException, IOException {
            return (VisioDocumentType)getTypeLoader().parse(inputStream, VisioDocumentType.type, (XmlOptions)null);
        }
        
        public static VisioDocumentType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (VisioDocumentType)getTypeLoader().parse(inputStream, VisioDocumentType.type, xmlOptions);
        }
        
        public static VisioDocumentType parse(final Reader reader) throws XmlException, IOException {
            return (VisioDocumentType)getTypeLoader().parse(reader, VisioDocumentType.type, (XmlOptions)null);
        }
        
        public static VisioDocumentType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (VisioDocumentType)getTypeLoader().parse(reader, VisioDocumentType.type, xmlOptions);
        }
        
        public static VisioDocumentType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (VisioDocumentType)getTypeLoader().parse(xmlStreamReader, VisioDocumentType.type, (XmlOptions)null);
        }
        
        public static VisioDocumentType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (VisioDocumentType)getTypeLoader().parse(xmlStreamReader, VisioDocumentType.type, xmlOptions);
        }
        
        public static VisioDocumentType parse(final Node node) throws XmlException {
            return (VisioDocumentType)getTypeLoader().parse(node, VisioDocumentType.type, (XmlOptions)null);
        }
        
        public static VisioDocumentType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (VisioDocumentType)getTypeLoader().parse(node, VisioDocumentType.type, xmlOptions);
        }
        
        @Deprecated
        public static VisioDocumentType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (VisioDocumentType)getTypeLoader().parse(xmlInputStream, VisioDocumentType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static VisioDocumentType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (VisioDocumentType)getTypeLoader().parse(xmlInputStream, VisioDocumentType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, VisioDocumentType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, VisioDocumentType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
