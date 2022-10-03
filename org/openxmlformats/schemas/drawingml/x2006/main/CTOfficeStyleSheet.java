package org.openxmlformats.schemas.drawingml.x2006.main;

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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTOfficeStyleSheet extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTOfficeStyleSheet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctofficestylesheetce25type");
    
    CTBaseStyles getThemeElements();
    
    void setThemeElements(final CTBaseStyles p0);
    
    CTBaseStyles addNewThemeElements();
    
    CTObjectStyleDefaults getObjectDefaults();
    
    boolean isSetObjectDefaults();
    
    void setObjectDefaults(final CTObjectStyleDefaults p0);
    
    CTObjectStyleDefaults addNewObjectDefaults();
    
    void unsetObjectDefaults();
    
    CTColorSchemeList getExtraClrSchemeLst();
    
    boolean isSetExtraClrSchemeLst();
    
    void setExtraClrSchemeLst(final CTColorSchemeList p0);
    
    CTColorSchemeList addNewExtraClrSchemeLst();
    
    void unsetExtraClrSchemeLst();
    
    CTCustomColorList getCustClrLst();
    
    boolean isSetCustClrLst();
    
    void setCustClrLst(final CTCustomColorList p0);
    
    CTCustomColorList addNewCustClrLst();
    
    void unsetCustClrLst();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    String getName();
    
    XmlString xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlString p0);
    
    void unsetName();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTOfficeStyleSheet.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTOfficeStyleSheet newInstance() {
            return (CTOfficeStyleSheet)getTypeLoader().newInstance(CTOfficeStyleSheet.type, (XmlOptions)null);
        }
        
        public static CTOfficeStyleSheet newInstance(final XmlOptions xmlOptions) {
            return (CTOfficeStyleSheet)getTypeLoader().newInstance(CTOfficeStyleSheet.type, xmlOptions);
        }
        
        public static CTOfficeStyleSheet parse(final String s) throws XmlException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(s, CTOfficeStyleSheet.type, (XmlOptions)null);
        }
        
        public static CTOfficeStyleSheet parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(s, CTOfficeStyleSheet.type, xmlOptions);
        }
        
        public static CTOfficeStyleSheet parse(final File file) throws XmlException, IOException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(file, CTOfficeStyleSheet.type, (XmlOptions)null);
        }
        
        public static CTOfficeStyleSheet parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(file, CTOfficeStyleSheet.type, xmlOptions);
        }
        
        public static CTOfficeStyleSheet parse(final URL url) throws XmlException, IOException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(url, CTOfficeStyleSheet.type, (XmlOptions)null);
        }
        
        public static CTOfficeStyleSheet parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(url, CTOfficeStyleSheet.type, xmlOptions);
        }
        
        public static CTOfficeStyleSheet parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(inputStream, CTOfficeStyleSheet.type, (XmlOptions)null);
        }
        
        public static CTOfficeStyleSheet parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(inputStream, CTOfficeStyleSheet.type, xmlOptions);
        }
        
        public static CTOfficeStyleSheet parse(final Reader reader) throws XmlException, IOException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(reader, CTOfficeStyleSheet.type, (XmlOptions)null);
        }
        
        public static CTOfficeStyleSheet parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(reader, CTOfficeStyleSheet.type, xmlOptions);
        }
        
        public static CTOfficeStyleSheet parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(xmlStreamReader, CTOfficeStyleSheet.type, (XmlOptions)null);
        }
        
        public static CTOfficeStyleSheet parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(xmlStreamReader, CTOfficeStyleSheet.type, xmlOptions);
        }
        
        public static CTOfficeStyleSheet parse(final Node node) throws XmlException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(node, CTOfficeStyleSheet.type, (XmlOptions)null);
        }
        
        public static CTOfficeStyleSheet parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(node, CTOfficeStyleSheet.type, xmlOptions);
        }
        
        @Deprecated
        public static CTOfficeStyleSheet parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(xmlInputStream, CTOfficeStyleSheet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTOfficeStyleSheet parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTOfficeStyleSheet)getTypeLoader().parse(xmlInputStream, CTOfficeStyleSheet.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOfficeStyleSheet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOfficeStyleSheet.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
