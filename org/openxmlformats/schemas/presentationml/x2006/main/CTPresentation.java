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
import org.apache.xmlbeans.XmlInt;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPresentation extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPresentation.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpresentation56cbtype");
    
    CTSlideMasterIdList getSldMasterIdLst();
    
    boolean isSetSldMasterIdLst();
    
    void setSldMasterIdLst(final CTSlideMasterIdList p0);
    
    CTSlideMasterIdList addNewSldMasterIdLst();
    
    void unsetSldMasterIdLst();
    
    CTNotesMasterIdList getNotesMasterIdLst();
    
    boolean isSetNotesMasterIdLst();
    
    void setNotesMasterIdLst(final CTNotesMasterIdList p0);
    
    CTNotesMasterIdList addNewNotesMasterIdLst();
    
    void unsetNotesMasterIdLst();
    
    CTHandoutMasterIdList getHandoutMasterIdLst();
    
    boolean isSetHandoutMasterIdLst();
    
    void setHandoutMasterIdLst(final CTHandoutMasterIdList p0);
    
    CTHandoutMasterIdList addNewHandoutMasterIdLst();
    
    void unsetHandoutMasterIdLst();
    
    CTSlideIdList getSldIdLst();
    
    boolean isSetSldIdLst();
    
    void setSldIdLst(final CTSlideIdList p0);
    
    CTSlideIdList addNewSldIdLst();
    
    void unsetSldIdLst();
    
    CTSlideSize getSldSz();
    
    boolean isSetSldSz();
    
    void setSldSz(final CTSlideSize p0);
    
    CTSlideSize addNewSldSz();
    
    void unsetSldSz();
    
    CTPositiveSize2D getNotesSz();
    
    void setNotesSz(final CTPositiveSize2D p0);
    
    CTPositiveSize2D addNewNotesSz();
    
    CTSmartTags getSmartTags();
    
    boolean isSetSmartTags();
    
    void setSmartTags(final CTSmartTags p0);
    
    CTSmartTags addNewSmartTags();
    
    void unsetSmartTags();
    
    CTEmbeddedFontList getEmbeddedFontLst();
    
    boolean isSetEmbeddedFontLst();
    
    void setEmbeddedFontLst(final CTEmbeddedFontList p0);
    
    CTEmbeddedFontList addNewEmbeddedFontLst();
    
    void unsetEmbeddedFontLst();
    
    CTCustomShowList getCustShowLst();
    
    boolean isSetCustShowLst();
    
    void setCustShowLst(final CTCustomShowList p0);
    
    CTCustomShowList addNewCustShowLst();
    
    void unsetCustShowLst();
    
    CTPhotoAlbum getPhotoAlbum();
    
    boolean isSetPhotoAlbum();
    
    void setPhotoAlbum(final CTPhotoAlbum p0);
    
    CTPhotoAlbum addNewPhotoAlbum();
    
    void unsetPhotoAlbum();
    
    CTCustomerDataList getCustDataLst();
    
    boolean isSetCustDataLst();
    
    void setCustDataLst(final CTCustomerDataList p0);
    
    CTCustomerDataList addNewCustDataLst();
    
    void unsetCustDataLst();
    
    CTKinsoku getKinsoku();
    
    boolean isSetKinsoku();
    
    void setKinsoku(final CTKinsoku p0);
    
    CTKinsoku addNewKinsoku();
    
    void unsetKinsoku();
    
    CTTextListStyle getDefaultTextStyle();
    
    boolean isSetDefaultTextStyle();
    
    void setDefaultTextStyle(final CTTextListStyle p0);
    
    CTTextListStyle addNewDefaultTextStyle();
    
    void unsetDefaultTextStyle();
    
    CTModifyVerifier getModifyVerifier();
    
    boolean isSetModifyVerifier();
    
    void setModifyVerifier(final CTModifyVerifier p0);
    
    CTModifyVerifier addNewModifyVerifier();
    
    void unsetModifyVerifier();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    int getServerZoom();
    
    STPercentage xgetServerZoom();
    
    boolean isSetServerZoom();
    
    void setServerZoom(final int p0);
    
    void xsetServerZoom(final STPercentage p0);
    
    void unsetServerZoom();
    
    int getFirstSlideNum();
    
    XmlInt xgetFirstSlideNum();
    
    boolean isSetFirstSlideNum();
    
    void setFirstSlideNum(final int p0);
    
    void xsetFirstSlideNum(final XmlInt p0);
    
    void unsetFirstSlideNum();
    
    boolean getShowSpecialPlsOnTitleSld();
    
    XmlBoolean xgetShowSpecialPlsOnTitleSld();
    
    boolean isSetShowSpecialPlsOnTitleSld();
    
    void setShowSpecialPlsOnTitleSld(final boolean p0);
    
    void xsetShowSpecialPlsOnTitleSld(final XmlBoolean p0);
    
    void unsetShowSpecialPlsOnTitleSld();
    
    boolean getRtl();
    
    XmlBoolean xgetRtl();
    
    boolean isSetRtl();
    
    void setRtl(final boolean p0);
    
    void xsetRtl(final XmlBoolean p0);
    
    void unsetRtl();
    
    boolean getRemovePersonalInfoOnSave();
    
    XmlBoolean xgetRemovePersonalInfoOnSave();
    
    boolean isSetRemovePersonalInfoOnSave();
    
    void setRemovePersonalInfoOnSave(final boolean p0);
    
    void xsetRemovePersonalInfoOnSave(final XmlBoolean p0);
    
    void unsetRemovePersonalInfoOnSave();
    
    boolean getCompatMode();
    
    XmlBoolean xgetCompatMode();
    
    boolean isSetCompatMode();
    
    void setCompatMode(final boolean p0);
    
    void xsetCompatMode(final XmlBoolean p0);
    
    void unsetCompatMode();
    
    boolean getStrictFirstAndLastChars();
    
    XmlBoolean xgetStrictFirstAndLastChars();
    
    boolean isSetStrictFirstAndLastChars();
    
    void setStrictFirstAndLastChars(final boolean p0);
    
    void xsetStrictFirstAndLastChars(final XmlBoolean p0);
    
    void unsetStrictFirstAndLastChars();
    
    boolean getEmbedTrueTypeFonts();
    
    XmlBoolean xgetEmbedTrueTypeFonts();
    
    boolean isSetEmbedTrueTypeFonts();
    
    void setEmbedTrueTypeFonts(final boolean p0);
    
    void xsetEmbedTrueTypeFonts(final XmlBoolean p0);
    
    void unsetEmbedTrueTypeFonts();
    
    boolean getSaveSubsetFonts();
    
    XmlBoolean xgetSaveSubsetFonts();
    
    boolean isSetSaveSubsetFonts();
    
    void setSaveSubsetFonts(final boolean p0);
    
    void xsetSaveSubsetFonts(final XmlBoolean p0);
    
    void unsetSaveSubsetFonts();
    
    boolean getAutoCompressPictures();
    
    XmlBoolean xgetAutoCompressPictures();
    
    boolean isSetAutoCompressPictures();
    
    void setAutoCompressPictures(final boolean p0);
    
    void xsetAutoCompressPictures(final XmlBoolean p0);
    
    void unsetAutoCompressPictures();
    
    long getBookmarkIdSeed();
    
    STBookmarkIdSeed xgetBookmarkIdSeed();
    
    boolean isSetBookmarkIdSeed();
    
    void setBookmarkIdSeed(final long p0);
    
    void xsetBookmarkIdSeed(final STBookmarkIdSeed p0);
    
    void unsetBookmarkIdSeed();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPresentation.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPresentation newInstance() {
            return (CTPresentation)getTypeLoader().newInstance(CTPresentation.type, (XmlOptions)null);
        }
        
        public static CTPresentation newInstance(final XmlOptions xmlOptions) {
            return (CTPresentation)getTypeLoader().newInstance(CTPresentation.type, xmlOptions);
        }
        
        public static CTPresentation parse(final String s) throws XmlException {
            return (CTPresentation)getTypeLoader().parse(s, CTPresentation.type, (XmlOptions)null);
        }
        
        public static CTPresentation parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPresentation)getTypeLoader().parse(s, CTPresentation.type, xmlOptions);
        }
        
        public static CTPresentation parse(final File file) throws XmlException, IOException {
            return (CTPresentation)getTypeLoader().parse(file, CTPresentation.type, (XmlOptions)null);
        }
        
        public static CTPresentation parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPresentation)getTypeLoader().parse(file, CTPresentation.type, xmlOptions);
        }
        
        public static CTPresentation parse(final URL url) throws XmlException, IOException {
            return (CTPresentation)getTypeLoader().parse(url, CTPresentation.type, (XmlOptions)null);
        }
        
        public static CTPresentation parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPresentation)getTypeLoader().parse(url, CTPresentation.type, xmlOptions);
        }
        
        public static CTPresentation parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPresentation)getTypeLoader().parse(inputStream, CTPresentation.type, (XmlOptions)null);
        }
        
        public static CTPresentation parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPresentation)getTypeLoader().parse(inputStream, CTPresentation.type, xmlOptions);
        }
        
        public static CTPresentation parse(final Reader reader) throws XmlException, IOException {
            return (CTPresentation)getTypeLoader().parse(reader, CTPresentation.type, (XmlOptions)null);
        }
        
        public static CTPresentation parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPresentation)getTypeLoader().parse(reader, CTPresentation.type, xmlOptions);
        }
        
        public static CTPresentation parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPresentation)getTypeLoader().parse(xmlStreamReader, CTPresentation.type, (XmlOptions)null);
        }
        
        public static CTPresentation parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPresentation)getTypeLoader().parse(xmlStreamReader, CTPresentation.type, xmlOptions);
        }
        
        public static CTPresentation parse(final Node node) throws XmlException {
            return (CTPresentation)getTypeLoader().parse(node, CTPresentation.type, (XmlOptions)null);
        }
        
        public static CTPresentation parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPresentation)getTypeLoader().parse(node, CTPresentation.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPresentation parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPresentation)getTypeLoader().parse(xmlInputStream, CTPresentation.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPresentation parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPresentation)getTypeLoader().parse(xmlInputStream, CTPresentation.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPresentation.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPresentation.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
