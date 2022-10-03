package org.openxmlformats.schemas.presentationml.x2006.main;

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
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSlideMasterTextStyles extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSlideMasterTextStyles.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctslidemastertextstylesb48dtype");
    
    CTTextListStyle getTitleStyle();
    
    boolean isSetTitleStyle();
    
    void setTitleStyle(final CTTextListStyle p0);
    
    CTTextListStyle addNewTitleStyle();
    
    void unsetTitleStyle();
    
    CTTextListStyle getBodyStyle();
    
    boolean isSetBodyStyle();
    
    void setBodyStyle(final CTTextListStyle p0);
    
    CTTextListStyle addNewBodyStyle();
    
    void unsetBodyStyle();
    
    CTTextListStyle getOtherStyle();
    
    boolean isSetOtherStyle();
    
    void setOtherStyle(final CTTextListStyle p0);
    
    CTTextListStyle addNewOtherStyle();
    
    void unsetOtherStyle();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSlideMasterTextStyles.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSlideMasterTextStyles newInstance() {
            return (CTSlideMasterTextStyles)getTypeLoader().newInstance(CTSlideMasterTextStyles.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterTextStyles newInstance(final XmlOptions xmlOptions) {
            return (CTSlideMasterTextStyles)getTypeLoader().newInstance(CTSlideMasterTextStyles.type, xmlOptions);
        }
        
        public static CTSlideMasterTextStyles parse(final String s) throws XmlException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(s, CTSlideMasterTextStyles.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterTextStyles parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(s, CTSlideMasterTextStyles.type, xmlOptions);
        }
        
        public static CTSlideMasterTextStyles parse(final File file) throws XmlException, IOException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(file, CTSlideMasterTextStyles.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterTextStyles parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(file, CTSlideMasterTextStyles.type, xmlOptions);
        }
        
        public static CTSlideMasterTextStyles parse(final URL url) throws XmlException, IOException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(url, CTSlideMasterTextStyles.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterTextStyles parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(url, CTSlideMasterTextStyles.type, xmlOptions);
        }
        
        public static CTSlideMasterTextStyles parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(inputStream, CTSlideMasterTextStyles.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterTextStyles parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(inputStream, CTSlideMasterTextStyles.type, xmlOptions);
        }
        
        public static CTSlideMasterTextStyles parse(final Reader reader) throws XmlException, IOException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(reader, CTSlideMasterTextStyles.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterTextStyles parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(reader, CTSlideMasterTextStyles.type, xmlOptions);
        }
        
        public static CTSlideMasterTextStyles parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(xmlStreamReader, CTSlideMasterTextStyles.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterTextStyles parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(xmlStreamReader, CTSlideMasterTextStyles.type, xmlOptions);
        }
        
        public static CTSlideMasterTextStyles parse(final Node node) throws XmlException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(node, CTSlideMasterTextStyles.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterTextStyles parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(node, CTSlideMasterTextStyles.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSlideMasterTextStyles parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(xmlInputStream, CTSlideMasterTextStyles.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSlideMasterTextStyles parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSlideMasterTextStyles)getTypeLoader().parse(xmlInputStream, CTSlideMasterTextStyles.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideMasterTextStyles.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideMasterTextStyles.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
