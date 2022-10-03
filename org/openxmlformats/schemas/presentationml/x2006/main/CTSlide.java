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

public interface CTSlide extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSlide.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctslided7betype");
    
    CTCommonSlideData getCSld();
    
    void setCSld(final CTCommonSlideData p0);
    
    CTCommonSlideData addNewCSld();
    
    CTColorMappingOverride getClrMapOvr();
    
    boolean isSetClrMapOvr();
    
    void setClrMapOvr(final CTColorMappingOverride p0);
    
    CTColorMappingOverride addNewClrMapOvr();
    
    void unsetClrMapOvr();
    
    CTSlideTransition getTransition();
    
    boolean isSetTransition();
    
    void setTransition(final CTSlideTransition p0);
    
    CTSlideTransition addNewTransition();
    
    void unsetTransition();
    
    CTSlideTiming getTiming();
    
    boolean isSetTiming();
    
    void setTiming(final CTSlideTiming p0);
    
    CTSlideTiming addNewTiming();
    
    void unsetTiming();
    
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
    
    boolean getShow();
    
    XmlBoolean xgetShow();
    
    boolean isSetShow();
    
    void setShow(final boolean p0);
    
    void xsetShow(final XmlBoolean p0);
    
    void unsetShow();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSlide.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSlide newInstance() {
            return (CTSlide)getTypeLoader().newInstance(CTSlide.type, (XmlOptions)null);
        }
        
        public static CTSlide newInstance(final XmlOptions xmlOptions) {
            return (CTSlide)getTypeLoader().newInstance(CTSlide.type, xmlOptions);
        }
        
        public static CTSlide parse(final String s) throws XmlException {
            return (CTSlide)getTypeLoader().parse(s, CTSlide.type, (XmlOptions)null);
        }
        
        public static CTSlide parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlide)getTypeLoader().parse(s, CTSlide.type, xmlOptions);
        }
        
        public static CTSlide parse(final File file) throws XmlException, IOException {
            return (CTSlide)getTypeLoader().parse(file, CTSlide.type, (XmlOptions)null);
        }
        
        public static CTSlide parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlide)getTypeLoader().parse(file, CTSlide.type, xmlOptions);
        }
        
        public static CTSlide parse(final URL url) throws XmlException, IOException {
            return (CTSlide)getTypeLoader().parse(url, CTSlide.type, (XmlOptions)null);
        }
        
        public static CTSlide parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlide)getTypeLoader().parse(url, CTSlide.type, xmlOptions);
        }
        
        public static CTSlide parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSlide)getTypeLoader().parse(inputStream, CTSlide.type, (XmlOptions)null);
        }
        
        public static CTSlide parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlide)getTypeLoader().parse(inputStream, CTSlide.type, xmlOptions);
        }
        
        public static CTSlide parse(final Reader reader) throws XmlException, IOException {
            return (CTSlide)getTypeLoader().parse(reader, CTSlide.type, (XmlOptions)null);
        }
        
        public static CTSlide parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlide)getTypeLoader().parse(reader, CTSlide.type, xmlOptions);
        }
        
        public static CTSlide parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSlide)getTypeLoader().parse(xmlStreamReader, CTSlide.type, (XmlOptions)null);
        }
        
        public static CTSlide parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlide)getTypeLoader().parse(xmlStreamReader, CTSlide.type, xmlOptions);
        }
        
        public static CTSlide parse(final Node node) throws XmlException {
            return (CTSlide)getTypeLoader().parse(node, CTSlide.type, (XmlOptions)null);
        }
        
        public static CTSlide parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlide)getTypeLoader().parse(node, CTSlide.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSlide parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSlide)getTypeLoader().parse(xmlInputStream, CTSlide.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSlide parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSlide)getTypeLoader().parse(xmlInputStream, CTSlide.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlide.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlide.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
