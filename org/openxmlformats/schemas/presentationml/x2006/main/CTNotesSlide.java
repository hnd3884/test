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
import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMappingOverride;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTNotesSlide extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNotesSlide.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnotesslideab75type");
    
    CTCommonSlideData getCSld();
    
    void setCSld(final CTCommonSlideData p0);
    
    CTCommonSlideData addNewCSld();
    
    CTColorMappingOverride getClrMapOvr();
    
    boolean isSetClrMapOvr();
    
    void setClrMapOvr(final CTColorMappingOverride p0);
    
    CTColorMappingOverride addNewClrMapOvr();
    
    void unsetClrMapOvr();
    
    CTExtensionListModify getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionListModify p0);
    
    CTExtensionListModify addNewExtLst();
    
    void unsetExtLst();
    
    boolean getShowMasterSp();
    
    XmlBoolean xgetShowMasterSp();
    
    boolean isSetShowMasterSp();
    
    void setShowMasterSp(final boolean p0);
    
    void xsetShowMasterSp(final XmlBoolean p0);
    
    void unsetShowMasterSp();
    
    boolean getShowMasterPhAnim();
    
    XmlBoolean xgetShowMasterPhAnim();
    
    boolean isSetShowMasterPhAnim();
    
    void setShowMasterPhAnim(final boolean p0);
    
    void xsetShowMasterPhAnim(final XmlBoolean p0);
    
    void unsetShowMasterPhAnim();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNotesSlide.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNotesSlide newInstance() {
            return (CTNotesSlide)getTypeLoader().newInstance(CTNotesSlide.type, (XmlOptions)null);
        }
        
        public static CTNotesSlide newInstance(final XmlOptions xmlOptions) {
            return (CTNotesSlide)getTypeLoader().newInstance(CTNotesSlide.type, xmlOptions);
        }
        
        public static CTNotesSlide parse(final String s) throws XmlException {
            return (CTNotesSlide)getTypeLoader().parse(s, CTNotesSlide.type, (XmlOptions)null);
        }
        
        public static CTNotesSlide parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNotesSlide)getTypeLoader().parse(s, CTNotesSlide.type, xmlOptions);
        }
        
        public static CTNotesSlide parse(final File file) throws XmlException, IOException {
            return (CTNotesSlide)getTypeLoader().parse(file, CTNotesSlide.type, (XmlOptions)null);
        }
        
        public static CTNotesSlide parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesSlide)getTypeLoader().parse(file, CTNotesSlide.type, xmlOptions);
        }
        
        public static CTNotesSlide parse(final URL url) throws XmlException, IOException {
            return (CTNotesSlide)getTypeLoader().parse(url, CTNotesSlide.type, (XmlOptions)null);
        }
        
        public static CTNotesSlide parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesSlide)getTypeLoader().parse(url, CTNotesSlide.type, xmlOptions);
        }
        
        public static CTNotesSlide parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNotesSlide)getTypeLoader().parse(inputStream, CTNotesSlide.type, (XmlOptions)null);
        }
        
        public static CTNotesSlide parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesSlide)getTypeLoader().parse(inputStream, CTNotesSlide.type, xmlOptions);
        }
        
        public static CTNotesSlide parse(final Reader reader) throws XmlException, IOException {
            return (CTNotesSlide)getTypeLoader().parse(reader, CTNotesSlide.type, (XmlOptions)null);
        }
        
        public static CTNotesSlide parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesSlide)getTypeLoader().parse(reader, CTNotesSlide.type, xmlOptions);
        }
        
        public static CTNotesSlide parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNotesSlide)getTypeLoader().parse(xmlStreamReader, CTNotesSlide.type, (XmlOptions)null);
        }
        
        public static CTNotesSlide parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNotesSlide)getTypeLoader().parse(xmlStreamReader, CTNotesSlide.type, xmlOptions);
        }
        
        public static CTNotesSlide parse(final Node node) throws XmlException {
            return (CTNotesSlide)getTypeLoader().parse(node, CTNotesSlide.type, (XmlOptions)null);
        }
        
        public static CTNotesSlide parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNotesSlide)getTypeLoader().parse(node, CTNotesSlide.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNotesSlide parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNotesSlide)getTypeLoader().parse(xmlInputStream, CTNotesSlide.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNotesSlide parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNotesSlide)getTypeLoader().parse(xmlInputStream, CTNotesSlide.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNotesSlide.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNotesSlide.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
