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

public interface CTRPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctrpr097etype");
    
    CTString getRStyle();
    
    boolean isSetRStyle();
    
    void setRStyle(final CTString p0);
    
    CTString addNewRStyle();
    
    void unsetRStyle();
    
    CTFonts getRFonts();
    
    boolean isSetRFonts();
    
    void setRFonts(final CTFonts p0);
    
    CTFonts addNewRFonts();
    
    void unsetRFonts();
    
    CTOnOff getB();
    
    boolean isSetB();
    
    void setB(final CTOnOff p0);
    
    CTOnOff addNewB();
    
    void unsetB();
    
    CTOnOff getBCs();
    
    boolean isSetBCs();
    
    void setBCs(final CTOnOff p0);
    
    CTOnOff addNewBCs();
    
    void unsetBCs();
    
    CTOnOff getI();
    
    boolean isSetI();
    
    void setI(final CTOnOff p0);
    
    CTOnOff addNewI();
    
    void unsetI();
    
    CTOnOff getICs();
    
    boolean isSetICs();
    
    void setICs(final CTOnOff p0);
    
    CTOnOff addNewICs();
    
    void unsetICs();
    
    CTOnOff getCaps();
    
    boolean isSetCaps();
    
    void setCaps(final CTOnOff p0);
    
    CTOnOff addNewCaps();
    
    void unsetCaps();
    
    CTOnOff getSmallCaps();
    
    boolean isSetSmallCaps();
    
    void setSmallCaps(final CTOnOff p0);
    
    CTOnOff addNewSmallCaps();
    
    void unsetSmallCaps();
    
    CTOnOff getStrike();
    
    boolean isSetStrike();
    
    void setStrike(final CTOnOff p0);
    
    CTOnOff addNewStrike();
    
    void unsetStrike();
    
    CTOnOff getDstrike();
    
    boolean isSetDstrike();
    
    void setDstrike(final CTOnOff p0);
    
    CTOnOff addNewDstrike();
    
    void unsetDstrike();
    
    CTOnOff getOutline();
    
    boolean isSetOutline();
    
    void setOutline(final CTOnOff p0);
    
    CTOnOff addNewOutline();
    
    void unsetOutline();
    
    CTOnOff getShadow();
    
    boolean isSetShadow();
    
    void setShadow(final CTOnOff p0);
    
    CTOnOff addNewShadow();
    
    void unsetShadow();
    
    CTOnOff getEmboss();
    
    boolean isSetEmboss();
    
    void setEmboss(final CTOnOff p0);
    
    CTOnOff addNewEmboss();
    
    void unsetEmboss();
    
    CTOnOff getImprint();
    
    boolean isSetImprint();
    
    void setImprint(final CTOnOff p0);
    
    CTOnOff addNewImprint();
    
    void unsetImprint();
    
    CTOnOff getNoProof();
    
    boolean isSetNoProof();
    
    void setNoProof(final CTOnOff p0);
    
    CTOnOff addNewNoProof();
    
    void unsetNoProof();
    
    CTOnOff getSnapToGrid();
    
    boolean isSetSnapToGrid();
    
    void setSnapToGrid(final CTOnOff p0);
    
    CTOnOff addNewSnapToGrid();
    
    void unsetSnapToGrid();
    
    CTOnOff getVanish();
    
    boolean isSetVanish();
    
    void setVanish(final CTOnOff p0);
    
    CTOnOff addNewVanish();
    
    void unsetVanish();
    
    CTOnOff getWebHidden();
    
    boolean isSetWebHidden();
    
    void setWebHidden(final CTOnOff p0);
    
    CTOnOff addNewWebHidden();
    
    void unsetWebHidden();
    
    CTColor getColor();
    
    boolean isSetColor();
    
    void setColor(final CTColor p0);
    
    CTColor addNewColor();
    
    void unsetColor();
    
    CTSignedTwipsMeasure getSpacing();
    
    boolean isSetSpacing();
    
    void setSpacing(final CTSignedTwipsMeasure p0);
    
    CTSignedTwipsMeasure addNewSpacing();
    
    void unsetSpacing();
    
    CTTextScale getW();
    
    boolean isSetW();
    
    void setW(final CTTextScale p0);
    
    CTTextScale addNewW();
    
    void unsetW();
    
    CTHpsMeasure getKern();
    
    boolean isSetKern();
    
    void setKern(final CTHpsMeasure p0);
    
    CTHpsMeasure addNewKern();
    
    void unsetKern();
    
    CTSignedHpsMeasure getPosition();
    
    boolean isSetPosition();
    
    void setPosition(final CTSignedHpsMeasure p0);
    
    CTSignedHpsMeasure addNewPosition();
    
    void unsetPosition();
    
    CTHpsMeasure getSz();
    
    boolean isSetSz();
    
    void setSz(final CTHpsMeasure p0);
    
    CTHpsMeasure addNewSz();
    
    void unsetSz();
    
    CTHpsMeasure getSzCs();
    
    boolean isSetSzCs();
    
    void setSzCs(final CTHpsMeasure p0);
    
    CTHpsMeasure addNewSzCs();
    
    void unsetSzCs();
    
    CTHighlight getHighlight();
    
    boolean isSetHighlight();
    
    void setHighlight(final CTHighlight p0);
    
    CTHighlight addNewHighlight();
    
    void unsetHighlight();
    
    CTUnderline getU();
    
    boolean isSetU();
    
    void setU(final CTUnderline p0);
    
    CTUnderline addNewU();
    
    void unsetU();
    
    CTTextEffect getEffect();
    
    boolean isSetEffect();
    
    void setEffect(final CTTextEffect p0);
    
    CTTextEffect addNewEffect();
    
    void unsetEffect();
    
    CTBorder getBdr();
    
    boolean isSetBdr();
    
    void setBdr(final CTBorder p0);
    
    CTBorder addNewBdr();
    
    void unsetBdr();
    
    CTShd getShd();
    
    boolean isSetShd();
    
    void setShd(final CTShd p0);
    
    CTShd addNewShd();
    
    void unsetShd();
    
    CTFitText getFitText();
    
    boolean isSetFitText();
    
    void setFitText(final CTFitText p0);
    
    CTFitText addNewFitText();
    
    void unsetFitText();
    
    CTVerticalAlignRun getVertAlign();
    
    boolean isSetVertAlign();
    
    void setVertAlign(final CTVerticalAlignRun p0);
    
    CTVerticalAlignRun addNewVertAlign();
    
    void unsetVertAlign();
    
    CTOnOff getRtl();
    
    boolean isSetRtl();
    
    void setRtl(final CTOnOff p0);
    
    CTOnOff addNewRtl();
    
    void unsetRtl();
    
    CTOnOff getCs();
    
    boolean isSetCs();
    
    void setCs(final CTOnOff p0);
    
    CTOnOff addNewCs();
    
    void unsetCs();
    
    CTEm getEm();
    
    boolean isSetEm();
    
    void setEm(final CTEm p0);
    
    CTEm addNewEm();
    
    void unsetEm();
    
    CTLanguage getLang();
    
    boolean isSetLang();
    
    void setLang(final CTLanguage p0);
    
    CTLanguage addNewLang();
    
    void unsetLang();
    
    CTEastAsianLayout getEastAsianLayout();
    
    boolean isSetEastAsianLayout();
    
    void setEastAsianLayout(final CTEastAsianLayout p0);
    
    CTEastAsianLayout addNewEastAsianLayout();
    
    void unsetEastAsianLayout();
    
    CTOnOff getSpecVanish();
    
    boolean isSetSpecVanish();
    
    void setSpecVanish(final CTOnOff p0);
    
    CTOnOff addNewSpecVanish();
    
    void unsetSpecVanish();
    
    CTOnOff getOMath();
    
    boolean isSetOMath();
    
    void setOMath(final CTOnOff p0);
    
    CTOnOff addNewOMath();
    
    void unsetOMath();
    
    CTRPrChange getRPrChange();
    
    boolean isSetRPrChange();
    
    void setRPrChange(final CTRPrChange p0);
    
    CTRPrChange addNewRPrChange();
    
    void unsetRPrChange();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRPr newInstance() {
            return (CTRPr)getTypeLoader().newInstance(CTRPr.type, (XmlOptions)null);
        }
        
        public static CTRPr newInstance(final XmlOptions xmlOptions) {
            return (CTRPr)getTypeLoader().newInstance(CTRPr.type, xmlOptions);
        }
        
        public static CTRPr parse(final String s) throws XmlException {
            return (CTRPr)getTypeLoader().parse(s, CTRPr.type, (XmlOptions)null);
        }
        
        public static CTRPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRPr)getTypeLoader().parse(s, CTRPr.type, xmlOptions);
        }
        
        public static CTRPr parse(final File file) throws XmlException, IOException {
            return (CTRPr)getTypeLoader().parse(file, CTRPr.type, (XmlOptions)null);
        }
        
        public static CTRPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRPr)getTypeLoader().parse(file, CTRPr.type, xmlOptions);
        }
        
        public static CTRPr parse(final URL url) throws XmlException, IOException {
            return (CTRPr)getTypeLoader().parse(url, CTRPr.type, (XmlOptions)null);
        }
        
        public static CTRPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRPr)getTypeLoader().parse(url, CTRPr.type, xmlOptions);
        }
        
        public static CTRPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRPr)getTypeLoader().parse(inputStream, CTRPr.type, (XmlOptions)null);
        }
        
        public static CTRPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRPr)getTypeLoader().parse(inputStream, CTRPr.type, xmlOptions);
        }
        
        public static CTRPr parse(final Reader reader) throws XmlException, IOException {
            return (CTRPr)getTypeLoader().parse(reader, CTRPr.type, (XmlOptions)null);
        }
        
        public static CTRPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRPr)getTypeLoader().parse(reader, CTRPr.type, xmlOptions);
        }
        
        public static CTRPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRPr)getTypeLoader().parse(xmlStreamReader, CTRPr.type, (XmlOptions)null);
        }
        
        public static CTRPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRPr)getTypeLoader().parse(xmlStreamReader, CTRPr.type, xmlOptions);
        }
        
        public static CTRPr parse(final Node node) throws XmlException {
            return (CTRPr)getTypeLoader().parse(node, CTRPr.type, (XmlOptions)null);
        }
        
        public static CTRPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRPr)getTypeLoader().parse(node, CTRPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRPr)getTypeLoader().parse(xmlInputStream, CTRPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRPr)getTypeLoader().parse(xmlInputStream, CTRPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
