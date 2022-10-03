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
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMapping;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSlideMaster extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSlideMaster.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctslidemasterd8fctype");
    
    CTCommonSlideData getCSld();
    
    void setCSld(final CTCommonSlideData p0);
    
    CTCommonSlideData addNewCSld();
    
    CTColorMapping getClrMap();
    
    void setClrMap(final CTColorMapping p0);
    
    CTColorMapping addNewClrMap();
    
    CTSlideLayoutIdList getSldLayoutIdLst();
    
    boolean isSetSldLayoutIdLst();
    
    void setSldLayoutIdLst(final CTSlideLayoutIdList p0);
    
    CTSlideLayoutIdList addNewSldLayoutIdLst();
    
    void unsetSldLayoutIdLst();
    
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
    
    CTSlideMasterTextStyles getTxStyles();
    
    boolean isSetTxStyles();
    
    void setTxStyles(final CTSlideMasterTextStyles p0);
    
    CTSlideMasterTextStyles addNewTxStyles();
    
    void unsetTxStyles();
    
    CTExtensionListModify getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionListModify p0);
    
    CTExtensionListModify addNewExtLst();
    
    void unsetExtLst();
    
    boolean getPreserve();
    
    XmlBoolean xgetPreserve();
    
    boolean isSetPreserve();
    
    void setPreserve(final boolean p0);
    
    void xsetPreserve(final XmlBoolean p0);
    
    void unsetPreserve();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSlideMaster.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSlideMaster newInstance() {
            return (CTSlideMaster)getTypeLoader().newInstance(CTSlideMaster.type, (XmlOptions)null);
        }
        
        public static CTSlideMaster newInstance(final XmlOptions xmlOptions) {
            return (CTSlideMaster)getTypeLoader().newInstance(CTSlideMaster.type, xmlOptions);
        }
        
        public static CTSlideMaster parse(final String s) throws XmlException {
            return (CTSlideMaster)getTypeLoader().parse(s, CTSlideMaster.type, (XmlOptions)null);
        }
        
        public static CTSlideMaster parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideMaster)getTypeLoader().parse(s, CTSlideMaster.type, xmlOptions);
        }
        
        public static CTSlideMaster parse(final File file) throws XmlException, IOException {
            return (CTSlideMaster)getTypeLoader().parse(file, CTSlideMaster.type, (XmlOptions)null);
        }
        
        public static CTSlideMaster parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMaster)getTypeLoader().parse(file, CTSlideMaster.type, xmlOptions);
        }
        
        public static CTSlideMaster parse(final URL url) throws XmlException, IOException {
            return (CTSlideMaster)getTypeLoader().parse(url, CTSlideMaster.type, (XmlOptions)null);
        }
        
        public static CTSlideMaster parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMaster)getTypeLoader().parse(url, CTSlideMaster.type, xmlOptions);
        }
        
        public static CTSlideMaster parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSlideMaster)getTypeLoader().parse(inputStream, CTSlideMaster.type, (XmlOptions)null);
        }
        
        public static CTSlideMaster parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMaster)getTypeLoader().parse(inputStream, CTSlideMaster.type, xmlOptions);
        }
        
        public static CTSlideMaster parse(final Reader reader) throws XmlException, IOException {
            return (CTSlideMaster)getTypeLoader().parse(reader, CTSlideMaster.type, (XmlOptions)null);
        }
        
        public static CTSlideMaster parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMaster)getTypeLoader().parse(reader, CTSlideMaster.type, xmlOptions);
        }
        
        public static CTSlideMaster parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSlideMaster)getTypeLoader().parse(xmlStreamReader, CTSlideMaster.type, (XmlOptions)null);
        }
        
        public static CTSlideMaster parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideMaster)getTypeLoader().parse(xmlStreamReader, CTSlideMaster.type, xmlOptions);
        }
        
        public static CTSlideMaster parse(final Node node) throws XmlException {
            return (CTSlideMaster)getTypeLoader().parse(node, CTSlideMaster.type, (XmlOptions)null);
        }
        
        public static CTSlideMaster parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideMaster)getTypeLoader().parse(node, CTSlideMaster.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSlideMaster parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSlideMaster)getTypeLoader().parse(xmlInputStream, CTSlideMaster.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSlideMaster parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSlideMaster)getTypeLoader().parse(xmlInputStream, CTSlideMaster.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideMaster.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideMaster.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
