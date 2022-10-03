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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTextCharacterProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextCharacterProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextcharacterproperties76c0type");
    
    CTLineProperties getLn();
    
    boolean isSetLn();
    
    void setLn(final CTLineProperties p0);
    
    CTLineProperties addNewLn();
    
    void unsetLn();
    
    CTNoFillProperties getNoFill();
    
    boolean isSetNoFill();
    
    void setNoFill(final CTNoFillProperties p0);
    
    CTNoFillProperties addNewNoFill();
    
    void unsetNoFill();
    
    CTSolidColorFillProperties getSolidFill();
    
    boolean isSetSolidFill();
    
    void setSolidFill(final CTSolidColorFillProperties p0);
    
    CTSolidColorFillProperties addNewSolidFill();
    
    void unsetSolidFill();
    
    CTGradientFillProperties getGradFill();
    
    boolean isSetGradFill();
    
    void setGradFill(final CTGradientFillProperties p0);
    
    CTGradientFillProperties addNewGradFill();
    
    void unsetGradFill();
    
    CTBlipFillProperties getBlipFill();
    
    boolean isSetBlipFill();
    
    void setBlipFill(final CTBlipFillProperties p0);
    
    CTBlipFillProperties addNewBlipFill();
    
    void unsetBlipFill();
    
    CTPatternFillProperties getPattFill();
    
    boolean isSetPattFill();
    
    void setPattFill(final CTPatternFillProperties p0);
    
    CTPatternFillProperties addNewPattFill();
    
    void unsetPattFill();
    
    CTGroupFillProperties getGrpFill();
    
    boolean isSetGrpFill();
    
    void setGrpFill(final CTGroupFillProperties p0);
    
    CTGroupFillProperties addNewGrpFill();
    
    void unsetGrpFill();
    
    CTEffectList getEffectLst();
    
    boolean isSetEffectLst();
    
    void setEffectLst(final CTEffectList p0);
    
    CTEffectList addNewEffectLst();
    
    void unsetEffectLst();
    
    CTEffectContainer getEffectDag();
    
    boolean isSetEffectDag();
    
    void setEffectDag(final CTEffectContainer p0);
    
    CTEffectContainer addNewEffectDag();
    
    void unsetEffectDag();
    
    CTColor getHighlight();
    
    boolean isSetHighlight();
    
    void setHighlight(final CTColor p0);
    
    CTColor addNewHighlight();
    
    void unsetHighlight();
    
    CTTextUnderlineLineFollowText getULnTx();
    
    boolean isSetULnTx();
    
    void setULnTx(final CTTextUnderlineLineFollowText p0);
    
    CTTextUnderlineLineFollowText addNewULnTx();
    
    void unsetULnTx();
    
    CTLineProperties getULn();
    
    boolean isSetULn();
    
    void setULn(final CTLineProperties p0);
    
    CTLineProperties addNewULn();
    
    void unsetULn();
    
    CTTextUnderlineFillFollowText getUFillTx();
    
    boolean isSetUFillTx();
    
    void setUFillTx(final CTTextUnderlineFillFollowText p0);
    
    CTTextUnderlineFillFollowText addNewUFillTx();
    
    void unsetUFillTx();
    
    CTTextUnderlineFillGroupWrapper getUFill();
    
    boolean isSetUFill();
    
    void setUFill(final CTTextUnderlineFillGroupWrapper p0);
    
    CTTextUnderlineFillGroupWrapper addNewUFill();
    
    void unsetUFill();
    
    CTTextFont getLatin();
    
    boolean isSetLatin();
    
    void setLatin(final CTTextFont p0);
    
    CTTextFont addNewLatin();
    
    void unsetLatin();
    
    CTTextFont getEa();
    
    boolean isSetEa();
    
    void setEa(final CTTextFont p0);
    
    CTTextFont addNewEa();
    
    void unsetEa();
    
    CTTextFont getCs();
    
    boolean isSetCs();
    
    void setCs(final CTTextFont p0);
    
    CTTextFont addNewCs();
    
    void unsetCs();
    
    CTTextFont getSym();
    
    boolean isSetSym();
    
    void setSym(final CTTextFont p0);
    
    CTTextFont addNewSym();
    
    void unsetSym();
    
    CTHyperlink getHlinkClick();
    
    boolean isSetHlinkClick();
    
    void setHlinkClick(final CTHyperlink p0);
    
    CTHyperlink addNewHlinkClick();
    
    void unsetHlinkClick();
    
    CTHyperlink getHlinkMouseOver();
    
    boolean isSetHlinkMouseOver();
    
    void setHlinkMouseOver(final CTHyperlink p0);
    
    CTHyperlink addNewHlinkMouseOver();
    
    void unsetHlinkMouseOver();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    boolean getKumimoji();
    
    XmlBoolean xgetKumimoji();
    
    boolean isSetKumimoji();
    
    void setKumimoji(final boolean p0);
    
    void xsetKumimoji(final XmlBoolean p0);
    
    void unsetKumimoji();
    
    String getLang();
    
    STTextLanguageID xgetLang();
    
    boolean isSetLang();
    
    void setLang(final String p0);
    
    void xsetLang(final STTextLanguageID p0);
    
    void unsetLang();
    
    String getAltLang();
    
    STTextLanguageID xgetAltLang();
    
    boolean isSetAltLang();
    
    void setAltLang(final String p0);
    
    void xsetAltLang(final STTextLanguageID p0);
    
    void unsetAltLang();
    
    int getSz();
    
    STTextFontSize xgetSz();
    
    boolean isSetSz();
    
    void setSz(final int p0);
    
    void xsetSz(final STTextFontSize p0);
    
    void unsetSz();
    
    boolean getB();
    
    XmlBoolean xgetB();
    
    boolean isSetB();
    
    void setB(final boolean p0);
    
    void xsetB(final XmlBoolean p0);
    
    void unsetB();
    
    boolean getI();
    
    XmlBoolean xgetI();
    
    boolean isSetI();
    
    void setI(final boolean p0);
    
    void xsetI(final XmlBoolean p0);
    
    void unsetI();
    
    STTextUnderlineType.Enum getU();
    
    STTextUnderlineType xgetU();
    
    boolean isSetU();
    
    void setU(final STTextUnderlineType.Enum p0);
    
    void xsetU(final STTextUnderlineType p0);
    
    void unsetU();
    
    STTextStrikeType.Enum getStrike();
    
    STTextStrikeType xgetStrike();
    
    boolean isSetStrike();
    
    void setStrike(final STTextStrikeType.Enum p0);
    
    void xsetStrike(final STTextStrikeType p0);
    
    void unsetStrike();
    
    int getKern();
    
    STTextNonNegativePoint xgetKern();
    
    boolean isSetKern();
    
    void setKern(final int p0);
    
    void xsetKern(final STTextNonNegativePoint p0);
    
    void unsetKern();
    
    STTextCapsType.Enum getCap();
    
    STTextCapsType xgetCap();
    
    boolean isSetCap();
    
    void setCap(final STTextCapsType.Enum p0);
    
    void xsetCap(final STTextCapsType p0);
    
    void unsetCap();
    
    int getSpc();
    
    STTextPoint xgetSpc();
    
    boolean isSetSpc();
    
    void setSpc(final int p0);
    
    void xsetSpc(final STTextPoint p0);
    
    void unsetSpc();
    
    boolean getNormalizeH();
    
    XmlBoolean xgetNormalizeH();
    
    boolean isSetNormalizeH();
    
    void setNormalizeH(final boolean p0);
    
    void xsetNormalizeH(final XmlBoolean p0);
    
    void unsetNormalizeH();
    
    int getBaseline();
    
    STPercentage xgetBaseline();
    
    boolean isSetBaseline();
    
    void setBaseline(final int p0);
    
    void xsetBaseline(final STPercentage p0);
    
    void unsetBaseline();
    
    boolean getNoProof();
    
    XmlBoolean xgetNoProof();
    
    boolean isSetNoProof();
    
    void setNoProof(final boolean p0);
    
    void xsetNoProof(final XmlBoolean p0);
    
    void unsetNoProof();
    
    boolean getDirty();
    
    XmlBoolean xgetDirty();
    
    boolean isSetDirty();
    
    void setDirty(final boolean p0);
    
    void xsetDirty(final XmlBoolean p0);
    
    void unsetDirty();
    
    boolean getErr();
    
    XmlBoolean xgetErr();
    
    boolean isSetErr();
    
    void setErr(final boolean p0);
    
    void xsetErr(final XmlBoolean p0);
    
    void unsetErr();
    
    boolean getSmtClean();
    
    XmlBoolean xgetSmtClean();
    
    boolean isSetSmtClean();
    
    void setSmtClean(final boolean p0);
    
    void xsetSmtClean(final XmlBoolean p0);
    
    void unsetSmtClean();
    
    long getSmtId();
    
    XmlUnsignedInt xgetSmtId();
    
    boolean isSetSmtId();
    
    void setSmtId(final long p0);
    
    void xsetSmtId(final XmlUnsignedInt p0);
    
    void unsetSmtId();
    
    String getBmk();
    
    XmlString xgetBmk();
    
    boolean isSetBmk();
    
    void setBmk(final String p0);
    
    void xsetBmk(final XmlString p0);
    
    void unsetBmk();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextCharacterProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextCharacterProperties newInstance() {
            return (CTTextCharacterProperties)getTypeLoader().newInstance(CTTextCharacterProperties.type, (XmlOptions)null);
        }
        
        public static CTTextCharacterProperties newInstance(final XmlOptions xmlOptions) {
            return (CTTextCharacterProperties)getTypeLoader().newInstance(CTTextCharacterProperties.type, xmlOptions);
        }
        
        public static CTTextCharacterProperties parse(final String s) throws XmlException {
            return (CTTextCharacterProperties)getTypeLoader().parse(s, CTTextCharacterProperties.type, (XmlOptions)null);
        }
        
        public static CTTextCharacterProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextCharacterProperties)getTypeLoader().parse(s, CTTextCharacterProperties.type, xmlOptions);
        }
        
        public static CTTextCharacterProperties parse(final File file) throws XmlException, IOException {
            return (CTTextCharacterProperties)getTypeLoader().parse(file, CTTextCharacterProperties.type, (XmlOptions)null);
        }
        
        public static CTTextCharacterProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextCharacterProperties)getTypeLoader().parse(file, CTTextCharacterProperties.type, xmlOptions);
        }
        
        public static CTTextCharacterProperties parse(final URL url) throws XmlException, IOException {
            return (CTTextCharacterProperties)getTypeLoader().parse(url, CTTextCharacterProperties.type, (XmlOptions)null);
        }
        
        public static CTTextCharacterProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextCharacterProperties)getTypeLoader().parse(url, CTTextCharacterProperties.type, xmlOptions);
        }
        
        public static CTTextCharacterProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextCharacterProperties)getTypeLoader().parse(inputStream, CTTextCharacterProperties.type, (XmlOptions)null);
        }
        
        public static CTTextCharacterProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextCharacterProperties)getTypeLoader().parse(inputStream, CTTextCharacterProperties.type, xmlOptions);
        }
        
        public static CTTextCharacterProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTTextCharacterProperties)getTypeLoader().parse(reader, CTTextCharacterProperties.type, (XmlOptions)null);
        }
        
        public static CTTextCharacterProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextCharacterProperties)getTypeLoader().parse(reader, CTTextCharacterProperties.type, xmlOptions);
        }
        
        public static CTTextCharacterProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextCharacterProperties)getTypeLoader().parse(xmlStreamReader, CTTextCharacterProperties.type, (XmlOptions)null);
        }
        
        public static CTTextCharacterProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextCharacterProperties)getTypeLoader().parse(xmlStreamReader, CTTextCharacterProperties.type, xmlOptions);
        }
        
        public static CTTextCharacterProperties parse(final Node node) throws XmlException {
            return (CTTextCharacterProperties)getTypeLoader().parse(node, CTTextCharacterProperties.type, (XmlOptions)null);
        }
        
        public static CTTextCharacterProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextCharacterProperties)getTypeLoader().parse(node, CTTextCharacterProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextCharacterProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextCharacterProperties)getTypeLoader().parse(xmlInputStream, CTTextCharacterProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextCharacterProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextCharacterProperties)getTypeLoader().parse(xmlInputStream, CTTextCharacterProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextCharacterProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextCharacterProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
