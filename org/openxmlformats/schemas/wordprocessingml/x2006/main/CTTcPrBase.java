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
import org.apache.xmlbeans.XmlObject;

public interface CTTcPrBase extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTcPrBase.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttcprbase93e6type");
    
    CTCnf getCnfStyle();
    
    boolean isSetCnfStyle();
    
    void setCnfStyle(final CTCnf p0);
    
    CTCnf addNewCnfStyle();
    
    void unsetCnfStyle();
    
    CTTblWidth getTcW();
    
    boolean isSetTcW();
    
    void setTcW(final CTTblWidth p0);
    
    CTTblWidth addNewTcW();
    
    void unsetTcW();
    
    CTDecimalNumber getGridSpan();
    
    boolean isSetGridSpan();
    
    void setGridSpan(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewGridSpan();
    
    void unsetGridSpan();
    
    CTHMerge getHMerge();
    
    boolean isSetHMerge();
    
    void setHMerge(final CTHMerge p0);
    
    CTHMerge addNewHMerge();
    
    void unsetHMerge();
    
    CTVMerge getVMerge();
    
    boolean isSetVMerge();
    
    void setVMerge(final CTVMerge p0);
    
    CTVMerge addNewVMerge();
    
    void unsetVMerge();
    
    CTTcBorders getTcBorders();
    
    boolean isSetTcBorders();
    
    void setTcBorders(final CTTcBorders p0);
    
    CTTcBorders addNewTcBorders();
    
    void unsetTcBorders();
    
    CTShd getShd();
    
    boolean isSetShd();
    
    void setShd(final CTShd p0);
    
    CTShd addNewShd();
    
    void unsetShd();
    
    CTOnOff getNoWrap();
    
    boolean isSetNoWrap();
    
    void setNoWrap(final CTOnOff p0);
    
    CTOnOff addNewNoWrap();
    
    void unsetNoWrap();
    
    CTTcMar getTcMar();
    
    boolean isSetTcMar();
    
    void setTcMar(final CTTcMar p0);
    
    CTTcMar addNewTcMar();
    
    void unsetTcMar();
    
    CTTextDirection getTextDirection();
    
    boolean isSetTextDirection();
    
    void setTextDirection(final CTTextDirection p0);
    
    CTTextDirection addNewTextDirection();
    
    void unsetTextDirection();
    
    CTOnOff getTcFitText();
    
    boolean isSetTcFitText();
    
    void setTcFitText(final CTOnOff p0);
    
    CTOnOff addNewTcFitText();
    
    void unsetTcFitText();
    
    CTVerticalJc getVAlign();
    
    boolean isSetVAlign();
    
    void setVAlign(final CTVerticalJc p0);
    
    CTVerticalJc addNewVAlign();
    
    void unsetVAlign();
    
    CTOnOff getHideMark();
    
    boolean isSetHideMark();
    
    void setHideMark(final CTOnOff p0);
    
    CTOnOff addNewHideMark();
    
    void unsetHideMark();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTcPrBase.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTcPrBase newInstance() {
            return (CTTcPrBase)getTypeLoader().newInstance(CTTcPrBase.type, (XmlOptions)null);
        }
        
        public static CTTcPrBase newInstance(final XmlOptions xmlOptions) {
            return (CTTcPrBase)getTypeLoader().newInstance(CTTcPrBase.type, xmlOptions);
        }
        
        public static CTTcPrBase parse(final String s) throws XmlException {
            return (CTTcPrBase)getTypeLoader().parse(s, CTTcPrBase.type, (XmlOptions)null);
        }
        
        public static CTTcPrBase parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTcPrBase)getTypeLoader().parse(s, CTTcPrBase.type, xmlOptions);
        }
        
        public static CTTcPrBase parse(final File file) throws XmlException, IOException {
            return (CTTcPrBase)getTypeLoader().parse(file, CTTcPrBase.type, (XmlOptions)null);
        }
        
        public static CTTcPrBase parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcPrBase)getTypeLoader().parse(file, CTTcPrBase.type, xmlOptions);
        }
        
        public static CTTcPrBase parse(final URL url) throws XmlException, IOException {
            return (CTTcPrBase)getTypeLoader().parse(url, CTTcPrBase.type, (XmlOptions)null);
        }
        
        public static CTTcPrBase parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcPrBase)getTypeLoader().parse(url, CTTcPrBase.type, xmlOptions);
        }
        
        public static CTTcPrBase parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTcPrBase)getTypeLoader().parse(inputStream, CTTcPrBase.type, (XmlOptions)null);
        }
        
        public static CTTcPrBase parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcPrBase)getTypeLoader().parse(inputStream, CTTcPrBase.type, xmlOptions);
        }
        
        public static CTTcPrBase parse(final Reader reader) throws XmlException, IOException {
            return (CTTcPrBase)getTypeLoader().parse(reader, CTTcPrBase.type, (XmlOptions)null);
        }
        
        public static CTTcPrBase parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcPrBase)getTypeLoader().parse(reader, CTTcPrBase.type, xmlOptions);
        }
        
        public static CTTcPrBase parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTcPrBase)getTypeLoader().parse(xmlStreamReader, CTTcPrBase.type, (XmlOptions)null);
        }
        
        public static CTTcPrBase parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTcPrBase)getTypeLoader().parse(xmlStreamReader, CTTcPrBase.type, xmlOptions);
        }
        
        public static CTTcPrBase parse(final Node node) throws XmlException {
            return (CTTcPrBase)getTypeLoader().parse(node, CTTcPrBase.type, (XmlOptions)null);
        }
        
        public static CTTcPrBase parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTcPrBase)getTypeLoader().parse(node, CTTcPrBase.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTcPrBase parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTcPrBase)getTypeLoader().parse(xmlInputStream, CTTcPrBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTcPrBase parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTcPrBase)getTypeLoader().parse(xmlInputStream, CTTcPrBase.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTcPrBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTcPrBase.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
