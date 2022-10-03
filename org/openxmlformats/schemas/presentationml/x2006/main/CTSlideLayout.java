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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMappingOverride;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSlideLayout extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSlideLayout.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctslidelayouteb34type");
    
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
    
    CTHeaderFooter getHf();
    
    boolean isSetHf();
    
    void setHf(final CTHeaderFooter p0);
    
    CTHeaderFooter addNewHf();
    
    void unsetHf();
    
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
    
    String getMatchingName();
    
    XmlString xgetMatchingName();
    
    boolean isSetMatchingName();
    
    void setMatchingName(final String p0);
    
    void xsetMatchingName(final XmlString p0);
    
    void unsetMatchingName();
    
    STSlideLayoutType.Enum getType();
    
    STSlideLayoutType xgetType();
    
    boolean isSetType();
    
    void setType(final STSlideLayoutType.Enum p0);
    
    void xsetType(final STSlideLayoutType p0);
    
    void unsetType();
    
    boolean getPreserve();
    
    XmlBoolean xgetPreserve();
    
    boolean isSetPreserve();
    
    void setPreserve(final boolean p0);
    
    void xsetPreserve(final XmlBoolean p0);
    
    void unsetPreserve();
    
    boolean getUserDrawn();
    
    XmlBoolean xgetUserDrawn();
    
    boolean isSetUserDrawn();
    
    void setUserDrawn(final boolean p0);
    
    void xsetUserDrawn(final XmlBoolean p0);
    
    void unsetUserDrawn();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSlideLayout.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSlideLayout newInstance() {
            return (CTSlideLayout)getTypeLoader().newInstance(CTSlideLayout.type, (XmlOptions)null);
        }
        
        public static CTSlideLayout newInstance(final XmlOptions xmlOptions) {
            return (CTSlideLayout)getTypeLoader().newInstance(CTSlideLayout.type, xmlOptions);
        }
        
        public static CTSlideLayout parse(final String s) throws XmlException {
            return (CTSlideLayout)getTypeLoader().parse(s, CTSlideLayout.type, (XmlOptions)null);
        }
        
        public static CTSlideLayout parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideLayout)getTypeLoader().parse(s, CTSlideLayout.type, xmlOptions);
        }
        
        public static CTSlideLayout parse(final File file) throws XmlException, IOException {
            return (CTSlideLayout)getTypeLoader().parse(file, CTSlideLayout.type, (XmlOptions)null);
        }
        
        public static CTSlideLayout parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideLayout)getTypeLoader().parse(file, CTSlideLayout.type, xmlOptions);
        }
        
        public static CTSlideLayout parse(final URL url) throws XmlException, IOException {
            return (CTSlideLayout)getTypeLoader().parse(url, CTSlideLayout.type, (XmlOptions)null);
        }
        
        public static CTSlideLayout parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideLayout)getTypeLoader().parse(url, CTSlideLayout.type, xmlOptions);
        }
        
        public static CTSlideLayout parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSlideLayout)getTypeLoader().parse(inputStream, CTSlideLayout.type, (XmlOptions)null);
        }
        
        public static CTSlideLayout parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideLayout)getTypeLoader().parse(inputStream, CTSlideLayout.type, xmlOptions);
        }
        
        public static CTSlideLayout parse(final Reader reader) throws XmlException, IOException {
            return (CTSlideLayout)getTypeLoader().parse(reader, CTSlideLayout.type, (XmlOptions)null);
        }
        
        public static CTSlideLayout parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideLayout)getTypeLoader().parse(reader, CTSlideLayout.type, xmlOptions);
        }
        
        public static CTSlideLayout parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSlideLayout)getTypeLoader().parse(xmlStreamReader, CTSlideLayout.type, (XmlOptions)null);
        }
        
        public static CTSlideLayout parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideLayout)getTypeLoader().parse(xmlStreamReader, CTSlideLayout.type, xmlOptions);
        }
        
        public static CTSlideLayout parse(final Node node) throws XmlException {
            return (CTSlideLayout)getTypeLoader().parse(node, CTSlideLayout.type, (XmlOptions)null);
        }
        
        public static CTSlideLayout parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideLayout)getTypeLoader().parse(node, CTSlideLayout.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSlideLayout parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSlideLayout)getTypeLoader().parse(xmlInputStream, CTSlideLayout.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSlideLayout parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSlideLayout)getTypeLoader().parse(xmlInputStream, CTSlideLayout.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideLayout.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideLayout.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
