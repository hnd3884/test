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

public interface CTPPrBase extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPPrBase.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpprbasebaeftype");
    
    CTString getPStyle();
    
    boolean isSetPStyle();
    
    void setPStyle(final CTString p0);
    
    CTString addNewPStyle();
    
    void unsetPStyle();
    
    CTOnOff getKeepNext();
    
    boolean isSetKeepNext();
    
    void setKeepNext(final CTOnOff p0);
    
    CTOnOff addNewKeepNext();
    
    void unsetKeepNext();
    
    CTOnOff getKeepLines();
    
    boolean isSetKeepLines();
    
    void setKeepLines(final CTOnOff p0);
    
    CTOnOff addNewKeepLines();
    
    void unsetKeepLines();
    
    CTOnOff getPageBreakBefore();
    
    boolean isSetPageBreakBefore();
    
    void setPageBreakBefore(final CTOnOff p0);
    
    CTOnOff addNewPageBreakBefore();
    
    void unsetPageBreakBefore();
    
    CTFramePr getFramePr();
    
    boolean isSetFramePr();
    
    void setFramePr(final CTFramePr p0);
    
    CTFramePr addNewFramePr();
    
    void unsetFramePr();
    
    CTOnOff getWidowControl();
    
    boolean isSetWidowControl();
    
    void setWidowControl(final CTOnOff p0);
    
    CTOnOff addNewWidowControl();
    
    void unsetWidowControl();
    
    CTNumPr getNumPr();
    
    boolean isSetNumPr();
    
    void setNumPr(final CTNumPr p0);
    
    CTNumPr addNewNumPr();
    
    void unsetNumPr();
    
    CTOnOff getSuppressLineNumbers();
    
    boolean isSetSuppressLineNumbers();
    
    void setSuppressLineNumbers(final CTOnOff p0);
    
    CTOnOff addNewSuppressLineNumbers();
    
    void unsetSuppressLineNumbers();
    
    CTPBdr getPBdr();
    
    boolean isSetPBdr();
    
    void setPBdr(final CTPBdr p0);
    
    CTPBdr addNewPBdr();
    
    void unsetPBdr();
    
    CTShd getShd();
    
    boolean isSetShd();
    
    void setShd(final CTShd p0);
    
    CTShd addNewShd();
    
    void unsetShd();
    
    CTTabs getTabs();
    
    boolean isSetTabs();
    
    void setTabs(final CTTabs p0);
    
    CTTabs addNewTabs();
    
    void unsetTabs();
    
    CTOnOff getSuppressAutoHyphens();
    
    boolean isSetSuppressAutoHyphens();
    
    void setSuppressAutoHyphens(final CTOnOff p0);
    
    CTOnOff addNewSuppressAutoHyphens();
    
    void unsetSuppressAutoHyphens();
    
    CTOnOff getKinsoku();
    
    boolean isSetKinsoku();
    
    void setKinsoku(final CTOnOff p0);
    
    CTOnOff addNewKinsoku();
    
    void unsetKinsoku();
    
    CTOnOff getWordWrap();
    
    boolean isSetWordWrap();
    
    void setWordWrap(final CTOnOff p0);
    
    CTOnOff addNewWordWrap();
    
    void unsetWordWrap();
    
    CTOnOff getOverflowPunct();
    
    boolean isSetOverflowPunct();
    
    void setOverflowPunct(final CTOnOff p0);
    
    CTOnOff addNewOverflowPunct();
    
    void unsetOverflowPunct();
    
    CTOnOff getTopLinePunct();
    
    boolean isSetTopLinePunct();
    
    void setTopLinePunct(final CTOnOff p0);
    
    CTOnOff addNewTopLinePunct();
    
    void unsetTopLinePunct();
    
    CTOnOff getAutoSpaceDE();
    
    boolean isSetAutoSpaceDE();
    
    void setAutoSpaceDE(final CTOnOff p0);
    
    CTOnOff addNewAutoSpaceDE();
    
    void unsetAutoSpaceDE();
    
    CTOnOff getAutoSpaceDN();
    
    boolean isSetAutoSpaceDN();
    
    void setAutoSpaceDN(final CTOnOff p0);
    
    CTOnOff addNewAutoSpaceDN();
    
    void unsetAutoSpaceDN();
    
    CTOnOff getBidi();
    
    boolean isSetBidi();
    
    void setBidi(final CTOnOff p0);
    
    CTOnOff addNewBidi();
    
    void unsetBidi();
    
    CTOnOff getAdjustRightInd();
    
    boolean isSetAdjustRightInd();
    
    void setAdjustRightInd(final CTOnOff p0);
    
    CTOnOff addNewAdjustRightInd();
    
    void unsetAdjustRightInd();
    
    CTOnOff getSnapToGrid();
    
    boolean isSetSnapToGrid();
    
    void setSnapToGrid(final CTOnOff p0);
    
    CTOnOff addNewSnapToGrid();
    
    void unsetSnapToGrid();
    
    CTSpacing getSpacing();
    
    boolean isSetSpacing();
    
    void setSpacing(final CTSpacing p0);
    
    CTSpacing addNewSpacing();
    
    void unsetSpacing();
    
    CTInd getInd();
    
    boolean isSetInd();
    
    void setInd(final CTInd p0);
    
    CTInd addNewInd();
    
    void unsetInd();
    
    CTOnOff getContextualSpacing();
    
    boolean isSetContextualSpacing();
    
    void setContextualSpacing(final CTOnOff p0);
    
    CTOnOff addNewContextualSpacing();
    
    void unsetContextualSpacing();
    
    CTOnOff getMirrorIndents();
    
    boolean isSetMirrorIndents();
    
    void setMirrorIndents(final CTOnOff p0);
    
    CTOnOff addNewMirrorIndents();
    
    void unsetMirrorIndents();
    
    CTOnOff getSuppressOverlap();
    
    boolean isSetSuppressOverlap();
    
    void setSuppressOverlap(final CTOnOff p0);
    
    CTOnOff addNewSuppressOverlap();
    
    void unsetSuppressOverlap();
    
    CTJc getJc();
    
    boolean isSetJc();
    
    void setJc(final CTJc p0);
    
    CTJc addNewJc();
    
    void unsetJc();
    
    CTTextDirection getTextDirection();
    
    boolean isSetTextDirection();
    
    void setTextDirection(final CTTextDirection p0);
    
    CTTextDirection addNewTextDirection();
    
    void unsetTextDirection();
    
    CTTextAlignment getTextAlignment();
    
    boolean isSetTextAlignment();
    
    void setTextAlignment(final CTTextAlignment p0);
    
    CTTextAlignment addNewTextAlignment();
    
    void unsetTextAlignment();
    
    CTTextboxTightWrap getTextboxTightWrap();
    
    boolean isSetTextboxTightWrap();
    
    void setTextboxTightWrap(final CTTextboxTightWrap p0);
    
    CTTextboxTightWrap addNewTextboxTightWrap();
    
    void unsetTextboxTightWrap();
    
    CTDecimalNumber getOutlineLvl();
    
    boolean isSetOutlineLvl();
    
    void setOutlineLvl(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewOutlineLvl();
    
    void unsetOutlineLvl();
    
    CTDecimalNumber getDivId();
    
    boolean isSetDivId();
    
    void setDivId(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewDivId();
    
    void unsetDivId();
    
    CTCnf getCnfStyle();
    
    boolean isSetCnfStyle();
    
    void setCnfStyle(final CTCnf p0);
    
    CTCnf addNewCnfStyle();
    
    void unsetCnfStyle();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPPrBase.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPPrBase newInstance() {
            return (CTPPrBase)getTypeLoader().newInstance(CTPPrBase.type, (XmlOptions)null);
        }
        
        public static CTPPrBase newInstance(final XmlOptions xmlOptions) {
            return (CTPPrBase)getTypeLoader().newInstance(CTPPrBase.type, xmlOptions);
        }
        
        public static CTPPrBase parse(final String s) throws XmlException {
            return (CTPPrBase)getTypeLoader().parse(s, CTPPrBase.type, (XmlOptions)null);
        }
        
        public static CTPPrBase parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPPrBase)getTypeLoader().parse(s, CTPPrBase.type, xmlOptions);
        }
        
        public static CTPPrBase parse(final File file) throws XmlException, IOException {
            return (CTPPrBase)getTypeLoader().parse(file, CTPPrBase.type, (XmlOptions)null);
        }
        
        public static CTPPrBase parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPPrBase)getTypeLoader().parse(file, CTPPrBase.type, xmlOptions);
        }
        
        public static CTPPrBase parse(final URL url) throws XmlException, IOException {
            return (CTPPrBase)getTypeLoader().parse(url, CTPPrBase.type, (XmlOptions)null);
        }
        
        public static CTPPrBase parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPPrBase)getTypeLoader().parse(url, CTPPrBase.type, xmlOptions);
        }
        
        public static CTPPrBase parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPPrBase)getTypeLoader().parse(inputStream, CTPPrBase.type, (XmlOptions)null);
        }
        
        public static CTPPrBase parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPPrBase)getTypeLoader().parse(inputStream, CTPPrBase.type, xmlOptions);
        }
        
        public static CTPPrBase parse(final Reader reader) throws XmlException, IOException {
            return (CTPPrBase)getTypeLoader().parse(reader, CTPPrBase.type, (XmlOptions)null);
        }
        
        public static CTPPrBase parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPPrBase)getTypeLoader().parse(reader, CTPPrBase.type, xmlOptions);
        }
        
        public static CTPPrBase parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPPrBase)getTypeLoader().parse(xmlStreamReader, CTPPrBase.type, (XmlOptions)null);
        }
        
        public static CTPPrBase parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPPrBase)getTypeLoader().parse(xmlStreamReader, CTPPrBase.type, xmlOptions);
        }
        
        public static CTPPrBase parse(final Node node) throws XmlException {
            return (CTPPrBase)getTypeLoader().parse(node, CTPPrBase.type, (XmlOptions)null);
        }
        
        public static CTPPrBase parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPPrBase)getTypeLoader().parse(node, CTPPrBase.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPPrBase parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPPrBase)getTypeLoader().parse(xmlInputStream, CTPPrBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPPrBase parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPPrBase)getTypeLoader().parse(xmlInputStream, CTPPrBase.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPPrBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPPrBase.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
