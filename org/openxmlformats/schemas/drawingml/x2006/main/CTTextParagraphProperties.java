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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTextParagraphProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextParagraphProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextparagraphpropertiesdd05type");
    
    CTTextSpacing getLnSpc();
    
    boolean isSetLnSpc();
    
    void setLnSpc(final CTTextSpacing p0);
    
    CTTextSpacing addNewLnSpc();
    
    void unsetLnSpc();
    
    CTTextSpacing getSpcBef();
    
    boolean isSetSpcBef();
    
    void setSpcBef(final CTTextSpacing p0);
    
    CTTextSpacing addNewSpcBef();
    
    void unsetSpcBef();
    
    CTTextSpacing getSpcAft();
    
    boolean isSetSpcAft();
    
    void setSpcAft(final CTTextSpacing p0);
    
    CTTextSpacing addNewSpcAft();
    
    void unsetSpcAft();
    
    CTTextBulletColorFollowText getBuClrTx();
    
    boolean isSetBuClrTx();
    
    void setBuClrTx(final CTTextBulletColorFollowText p0);
    
    CTTextBulletColorFollowText addNewBuClrTx();
    
    void unsetBuClrTx();
    
    CTColor getBuClr();
    
    boolean isSetBuClr();
    
    void setBuClr(final CTColor p0);
    
    CTColor addNewBuClr();
    
    void unsetBuClr();
    
    CTTextBulletSizeFollowText getBuSzTx();
    
    boolean isSetBuSzTx();
    
    void setBuSzTx(final CTTextBulletSizeFollowText p0);
    
    CTTextBulletSizeFollowText addNewBuSzTx();
    
    void unsetBuSzTx();
    
    CTTextBulletSizePercent getBuSzPct();
    
    boolean isSetBuSzPct();
    
    void setBuSzPct(final CTTextBulletSizePercent p0);
    
    CTTextBulletSizePercent addNewBuSzPct();
    
    void unsetBuSzPct();
    
    CTTextBulletSizePoint getBuSzPts();
    
    boolean isSetBuSzPts();
    
    void setBuSzPts(final CTTextBulletSizePoint p0);
    
    CTTextBulletSizePoint addNewBuSzPts();
    
    void unsetBuSzPts();
    
    CTTextBulletTypefaceFollowText getBuFontTx();
    
    boolean isSetBuFontTx();
    
    void setBuFontTx(final CTTextBulletTypefaceFollowText p0);
    
    CTTextBulletTypefaceFollowText addNewBuFontTx();
    
    void unsetBuFontTx();
    
    CTTextFont getBuFont();
    
    boolean isSetBuFont();
    
    void setBuFont(final CTTextFont p0);
    
    CTTextFont addNewBuFont();
    
    void unsetBuFont();
    
    CTTextNoBullet getBuNone();
    
    boolean isSetBuNone();
    
    void setBuNone(final CTTextNoBullet p0);
    
    CTTextNoBullet addNewBuNone();
    
    void unsetBuNone();
    
    CTTextAutonumberBullet getBuAutoNum();
    
    boolean isSetBuAutoNum();
    
    void setBuAutoNum(final CTTextAutonumberBullet p0);
    
    CTTextAutonumberBullet addNewBuAutoNum();
    
    void unsetBuAutoNum();
    
    CTTextCharBullet getBuChar();
    
    boolean isSetBuChar();
    
    void setBuChar(final CTTextCharBullet p0);
    
    CTTextCharBullet addNewBuChar();
    
    void unsetBuChar();
    
    CTTextBlipBullet getBuBlip();
    
    boolean isSetBuBlip();
    
    void setBuBlip(final CTTextBlipBullet p0);
    
    CTTextBlipBullet addNewBuBlip();
    
    void unsetBuBlip();
    
    CTTextTabStopList getTabLst();
    
    boolean isSetTabLst();
    
    void setTabLst(final CTTextTabStopList p0);
    
    CTTextTabStopList addNewTabLst();
    
    void unsetTabLst();
    
    CTTextCharacterProperties getDefRPr();
    
    boolean isSetDefRPr();
    
    void setDefRPr(final CTTextCharacterProperties p0);
    
    CTTextCharacterProperties addNewDefRPr();
    
    void unsetDefRPr();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    int getMarL();
    
    STTextMargin xgetMarL();
    
    boolean isSetMarL();
    
    void setMarL(final int p0);
    
    void xsetMarL(final STTextMargin p0);
    
    void unsetMarL();
    
    int getMarR();
    
    STTextMargin xgetMarR();
    
    boolean isSetMarR();
    
    void setMarR(final int p0);
    
    void xsetMarR(final STTextMargin p0);
    
    void unsetMarR();
    
    int getLvl();
    
    STTextIndentLevelType xgetLvl();
    
    boolean isSetLvl();
    
    void setLvl(final int p0);
    
    void xsetLvl(final STTextIndentLevelType p0);
    
    void unsetLvl();
    
    int getIndent();
    
    STTextIndent xgetIndent();
    
    boolean isSetIndent();
    
    void setIndent(final int p0);
    
    void xsetIndent(final STTextIndent p0);
    
    void unsetIndent();
    
    STTextAlignType.Enum getAlgn();
    
    STTextAlignType xgetAlgn();
    
    boolean isSetAlgn();
    
    void setAlgn(final STTextAlignType.Enum p0);
    
    void xsetAlgn(final STTextAlignType p0);
    
    void unsetAlgn();
    
    int getDefTabSz();
    
    STCoordinate32 xgetDefTabSz();
    
    boolean isSetDefTabSz();
    
    void setDefTabSz(final int p0);
    
    void xsetDefTabSz(final STCoordinate32 p0);
    
    void unsetDefTabSz();
    
    boolean getRtl();
    
    XmlBoolean xgetRtl();
    
    boolean isSetRtl();
    
    void setRtl(final boolean p0);
    
    void xsetRtl(final XmlBoolean p0);
    
    void unsetRtl();
    
    boolean getEaLnBrk();
    
    XmlBoolean xgetEaLnBrk();
    
    boolean isSetEaLnBrk();
    
    void setEaLnBrk(final boolean p0);
    
    void xsetEaLnBrk(final XmlBoolean p0);
    
    void unsetEaLnBrk();
    
    STTextFontAlignType.Enum getFontAlgn();
    
    STTextFontAlignType xgetFontAlgn();
    
    boolean isSetFontAlgn();
    
    void setFontAlgn(final STTextFontAlignType.Enum p0);
    
    void xsetFontAlgn(final STTextFontAlignType p0);
    
    void unsetFontAlgn();
    
    boolean getLatinLnBrk();
    
    XmlBoolean xgetLatinLnBrk();
    
    boolean isSetLatinLnBrk();
    
    void setLatinLnBrk(final boolean p0);
    
    void xsetLatinLnBrk(final XmlBoolean p0);
    
    void unsetLatinLnBrk();
    
    boolean getHangingPunct();
    
    XmlBoolean xgetHangingPunct();
    
    boolean isSetHangingPunct();
    
    void setHangingPunct(final boolean p0);
    
    void xsetHangingPunct(final XmlBoolean p0);
    
    void unsetHangingPunct();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextParagraphProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextParagraphProperties newInstance() {
            return (CTTextParagraphProperties)getTypeLoader().newInstance(CTTextParagraphProperties.type, (XmlOptions)null);
        }
        
        public static CTTextParagraphProperties newInstance(final XmlOptions xmlOptions) {
            return (CTTextParagraphProperties)getTypeLoader().newInstance(CTTextParagraphProperties.type, xmlOptions);
        }
        
        public static CTTextParagraphProperties parse(final String s) throws XmlException {
            return (CTTextParagraphProperties)getTypeLoader().parse(s, CTTextParagraphProperties.type, (XmlOptions)null);
        }
        
        public static CTTextParagraphProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextParagraphProperties)getTypeLoader().parse(s, CTTextParagraphProperties.type, xmlOptions);
        }
        
        public static CTTextParagraphProperties parse(final File file) throws XmlException, IOException {
            return (CTTextParagraphProperties)getTypeLoader().parse(file, CTTextParagraphProperties.type, (XmlOptions)null);
        }
        
        public static CTTextParagraphProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextParagraphProperties)getTypeLoader().parse(file, CTTextParagraphProperties.type, xmlOptions);
        }
        
        public static CTTextParagraphProperties parse(final URL url) throws XmlException, IOException {
            return (CTTextParagraphProperties)getTypeLoader().parse(url, CTTextParagraphProperties.type, (XmlOptions)null);
        }
        
        public static CTTextParagraphProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextParagraphProperties)getTypeLoader().parse(url, CTTextParagraphProperties.type, xmlOptions);
        }
        
        public static CTTextParagraphProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextParagraphProperties)getTypeLoader().parse(inputStream, CTTextParagraphProperties.type, (XmlOptions)null);
        }
        
        public static CTTextParagraphProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextParagraphProperties)getTypeLoader().parse(inputStream, CTTextParagraphProperties.type, xmlOptions);
        }
        
        public static CTTextParagraphProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTTextParagraphProperties)getTypeLoader().parse(reader, CTTextParagraphProperties.type, (XmlOptions)null);
        }
        
        public static CTTextParagraphProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextParagraphProperties)getTypeLoader().parse(reader, CTTextParagraphProperties.type, xmlOptions);
        }
        
        public static CTTextParagraphProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextParagraphProperties)getTypeLoader().parse(xmlStreamReader, CTTextParagraphProperties.type, (XmlOptions)null);
        }
        
        public static CTTextParagraphProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextParagraphProperties)getTypeLoader().parse(xmlStreamReader, CTTextParagraphProperties.type, xmlOptions);
        }
        
        public static CTTextParagraphProperties parse(final Node node) throws XmlException {
            return (CTTextParagraphProperties)getTypeLoader().parse(node, CTTextParagraphProperties.type, (XmlOptions)null);
        }
        
        public static CTTextParagraphProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextParagraphProperties)getTypeLoader().parse(node, CTTextParagraphProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextParagraphProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextParagraphProperties)getTypeLoader().parse(xmlInputStream, CTTextParagraphProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextParagraphProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextParagraphProperties)getTypeLoader().parse(xmlInputStream, CTTextParagraphProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextParagraphProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextParagraphProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
