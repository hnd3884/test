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
import org.openxmlformats.schemas.schemaLibrary.x2006.main.CTSchemaLibrary;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTMathPr;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSettings extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSettings.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsettingsd6a5type");
    
    CTWriteProtection getWriteProtection();
    
    boolean isSetWriteProtection();
    
    void setWriteProtection(final CTWriteProtection p0);
    
    CTWriteProtection addNewWriteProtection();
    
    void unsetWriteProtection();
    
    CTView getView();
    
    boolean isSetView();
    
    void setView(final CTView p0);
    
    CTView addNewView();
    
    void unsetView();
    
    CTZoom getZoom();
    
    boolean isSetZoom();
    
    void setZoom(final CTZoom p0);
    
    CTZoom addNewZoom();
    
    void unsetZoom();
    
    CTOnOff getRemovePersonalInformation();
    
    boolean isSetRemovePersonalInformation();
    
    void setRemovePersonalInformation(final CTOnOff p0);
    
    CTOnOff addNewRemovePersonalInformation();
    
    void unsetRemovePersonalInformation();
    
    CTOnOff getRemoveDateAndTime();
    
    boolean isSetRemoveDateAndTime();
    
    void setRemoveDateAndTime(final CTOnOff p0);
    
    CTOnOff addNewRemoveDateAndTime();
    
    void unsetRemoveDateAndTime();
    
    CTOnOff getDoNotDisplayPageBoundaries();
    
    boolean isSetDoNotDisplayPageBoundaries();
    
    void setDoNotDisplayPageBoundaries(final CTOnOff p0);
    
    CTOnOff addNewDoNotDisplayPageBoundaries();
    
    void unsetDoNotDisplayPageBoundaries();
    
    CTOnOff getDisplayBackgroundShape();
    
    boolean isSetDisplayBackgroundShape();
    
    void setDisplayBackgroundShape(final CTOnOff p0);
    
    CTOnOff addNewDisplayBackgroundShape();
    
    void unsetDisplayBackgroundShape();
    
    CTOnOff getPrintPostScriptOverText();
    
    boolean isSetPrintPostScriptOverText();
    
    void setPrintPostScriptOverText(final CTOnOff p0);
    
    CTOnOff addNewPrintPostScriptOverText();
    
    void unsetPrintPostScriptOverText();
    
    CTOnOff getPrintFractionalCharacterWidth();
    
    boolean isSetPrintFractionalCharacterWidth();
    
    void setPrintFractionalCharacterWidth(final CTOnOff p0);
    
    CTOnOff addNewPrintFractionalCharacterWidth();
    
    void unsetPrintFractionalCharacterWidth();
    
    CTOnOff getPrintFormsData();
    
    boolean isSetPrintFormsData();
    
    void setPrintFormsData(final CTOnOff p0);
    
    CTOnOff addNewPrintFormsData();
    
    void unsetPrintFormsData();
    
    CTOnOff getEmbedTrueTypeFonts();
    
    boolean isSetEmbedTrueTypeFonts();
    
    void setEmbedTrueTypeFonts(final CTOnOff p0);
    
    CTOnOff addNewEmbedTrueTypeFonts();
    
    void unsetEmbedTrueTypeFonts();
    
    CTOnOff getEmbedSystemFonts();
    
    boolean isSetEmbedSystemFonts();
    
    void setEmbedSystemFonts(final CTOnOff p0);
    
    CTOnOff addNewEmbedSystemFonts();
    
    void unsetEmbedSystemFonts();
    
    CTOnOff getSaveSubsetFonts();
    
    boolean isSetSaveSubsetFonts();
    
    void setSaveSubsetFonts(final CTOnOff p0);
    
    CTOnOff addNewSaveSubsetFonts();
    
    void unsetSaveSubsetFonts();
    
    CTOnOff getSaveFormsData();
    
    boolean isSetSaveFormsData();
    
    void setSaveFormsData(final CTOnOff p0);
    
    CTOnOff addNewSaveFormsData();
    
    void unsetSaveFormsData();
    
    CTOnOff getMirrorMargins();
    
    boolean isSetMirrorMargins();
    
    void setMirrorMargins(final CTOnOff p0);
    
    CTOnOff addNewMirrorMargins();
    
    void unsetMirrorMargins();
    
    CTOnOff getAlignBordersAndEdges();
    
    boolean isSetAlignBordersAndEdges();
    
    void setAlignBordersAndEdges(final CTOnOff p0);
    
    CTOnOff addNewAlignBordersAndEdges();
    
    void unsetAlignBordersAndEdges();
    
    CTOnOff getBordersDoNotSurroundHeader();
    
    boolean isSetBordersDoNotSurroundHeader();
    
    void setBordersDoNotSurroundHeader(final CTOnOff p0);
    
    CTOnOff addNewBordersDoNotSurroundHeader();
    
    void unsetBordersDoNotSurroundHeader();
    
    CTOnOff getBordersDoNotSurroundFooter();
    
    boolean isSetBordersDoNotSurroundFooter();
    
    void setBordersDoNotSurroundFooter(final CTOnOff p0);
    
    CTOnOff addNewBordersDoNotSurroundFooter();
    
    void unsetBordersDoNotSurroundFooter();
    
    CTOnOff getGutterAtTop();
    
    boolean isSetGutterAtTop();
    
    void setGutterAtTop(final CTOnOff p0);
    
    CTOnOff addNewGutterAtTop();
    
    void unsetGutterAtTop();
    
    CTOnOff getHideSpellingErrors();
    
    boolean isSetHideSpellingErrors();
    
    void setHideSpellingErrors(final CTOnOff p0);
    
    CTOnOff addNewHideSpellingErrors();
    
    void unsetHideSpellingErrors();
    
    CTOnOff getHideGrammaticalErrors();
    
    boolean isSetHideGrammaticalErrors();
    
    void setHideGrammaticalErrors(final CTOnOff p0);
    
    CTOnOff addNewHideGrammaticalErrors();
    
    void unsetHideGrammaticalErrors();
    
    List<CTWritingStyle> getActiveWritingStyleList();
    
    @Deprecated
    CTWritingStyle[] getActiveWritingStyleArray();
    
    CTWritingStyle getActiveWritingStyleArray(final int p0);
    
    int sizeOfActiveWritingStyleArray();
    
    void setActiveWritingStyleArray(final CTWritingStyle[] p0);
    
    void setActiveWritingStyleArray(final int p0, final CTWritingStyle p1);
    
    CTWritingStyle insertNewActiveWritingStyle(final int p0);
    
    CTWritingStyle addNewActiveWritingStyle();
    
    void removeActiveWritingStyle(final int p0);
    
    CTProof getProofState();
    
    boolean isSetProofState();
    
    void setProofState(final CTProof p0);
    
    CTProof addNewProofState();
    
    void unsetProofState();
    
    CTOnOff getFormsDesign();
    
    boolean isSetFormsDesign();
    
    void setFormsDesign(final CTOnOff p0);
    
    CTOnOff addNewFormsDesign();
    
    void unsetFormsDesign();
    
    CTRel getAttachedTemplate();
    
    boolean isSetAttachedTemplate();
    
    void setAttachedTemplate(final CTRel p0);
    
    CTRel addNewAttachedTemplate();
    
    void unsetAttachedTemplate();
    
    CTOnOff getLinkStyles();
    
    boolean isSetLinkStyles();
    
    void setLinkStyles(final CTOnOff p0);
    
    CTOnOff addNewLinkStyles();
    
    void unsetLinkStyles();
    
    CTShortHexNumber getStylePaneFormatFilter();
    
    boolean isSetStylePaneFormatFilter();
    
    void setStylePaneFormatFilter(final CTShortHexNumber p0);
    
    CTShortHexNumber addNewStylePaneFormatFilter();
    
    void unsetStylePaneFormatFilter();
    
    CTShortHexNumber getStylePaneSortMethod();
    
    boolean isSetStylePaneSortMethod();
    
    void setStylePaneSortMethod(final CTShortHexNumber p0);
    
    CTShortHexNumber addNewStylePaneSortMethod();
    
    void unsetStylePaneSortMethod();
    
    CTDocType getDocumentType();
    
    boolean isSetDocumentType();
    
    void setDocumentType(final CTDocType p0);
    
    CTDocType addNewDocumentType();
    
    void unsetDocumentType();
    
    CTMailMerge getMailMerge();
    
    boolean isSetMailMerge();
    
    void setMailMerge(final CTMailMerge p0);
    
    CTMailMerge addNewMailMerge();
    
    void unsetMailMerge();
    
    CTTrackChangesView getRevisionView();
    
    boolean isSetRevisionView();
    
    void setRevisionView(final CTTrackChangesView p0);
    
    CTTrackChangesView addNewRevisionView();
    
    void unsetRevisionView();
    
    CTOnOff getTrackRevisions();
    
    boolean isSetTrackRevisions();
    
    void setTrackRevisions(final CTOnOff p0);
    
    CTOnOff addNewTrackRevisions();
    
    void unsetTrackRevisions();
    
    CTOnOff getDoNotTrackMoves();
    
    boolean isSetDoNotTrackMoves();
    
    void setDoNotTrackMoves(final CTOnOff p0);
    
    CTOnOff addNewDoNotTrackMoves();
    
    void unsetDoNotTrackMoves();
    
    CTOnOff getDoNotTrackFormatting();
    
    boolean isSetDoNotTrackFormatting();
    
    void setDoNotTrackFormatting(final CTOnOff p0);
    
    CTOnOff addNewDoNotTrackFormatting();
    
    void unsetDoNotTrackFormatting();
    
    CTDocProtect getDocumentProtection();
    
    boolean isSetDocumentProtection();
    
    void setDocumentProtection(final CTDocProtect p0);
    
    CTDocProtect addNewDocumentProtection();
    
    void unsetDocumentProtection();
    
    CTOnOff getAutoFormatOverride();
    
    boolean isSetAutoFormatOverride();
    
    void setAutoFormatOverride(final CTOnOff p0);
    
    CTOnOff addNewAutoFormatOverride();
    
    void unsetAutoFormatOverride();
    
    CTOnOff getStyleLockTheme();
    
    boolean isSetStyleLockTheme();
    
    void setStyleLockTheme(final CTOnOff p0);
    
    CTOnOff addNewStyleLockTheme();
    
    void unsetStyleLockTheme();
    
    CTOnOff getStyleLockQFSet();
    
    boolean isSetStyleLockQFSet();
    
    void setStyleLockQFSet(final CTOnOff p0);
    
    CTOnOff addNewStyleLockQFSet();
    
    void unsetStyleLockQFSet();
    
    CTTwipsMeasure getDefaultTabStop();
    
    boolean isSetDefaultTabStop();
    
    void setDefaultTabStop(final CTTwipsMeasure p0);
    
    CTTwipsMeasure addNewDefaultTabStop();
    
    void unsetDefaultTabStop();
    
    CTOnOff getAutoHyphenation();
    
    boolean isSetAutoHyphenation();
    
    void setAutoHyphenation(final CTOnOff p0);
    
    CTOnOff addNewAutoHyphenation();
    
    void unsetAutoHyphenation();
    
    CTDecimalNumber getConsecutiveHyphenLimit();
    
    boolean isSetConsecutiveHyphenLimit();
    
    void setConsecutiveHyphenLimit(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewConsecutiveHyphenLimit();
    
    void unsetConsecutiveHyphenLimit();
    
    CTTwipsMeasure getHyphenationZone();
    
    boolean isSetHyphenationZone();
    
    void setHyphenationZone(final CTTwipsMeasure p0);
    
    CTTwipsMeasure addNewHyphenationZone();
    
    void unsetHyphenationZone();
    
    CTOnOff getDoNotHyphenateCaps();
    
    boolean isSetDoNotHyphenateCaps();
    
    void setDoNotHyphenateCaps(final CTOnOff p0);
    
    CTOnOff addNewDoNotHyphenateCaps();
    
    void unsetDoNotHyphenateCaps();
    
    CTOnOff getShowEnvelope();
    
    boolean isSetShowEnvelope();
    
    void setShowEnvelope(final CTOnOff p0);
    
    CTOnOff addNewShowEnvelope();
    
    void unsetShowEnvelope();
    
    CTDecimalNumber getSummaryLength();
    
    boolean isSetSummaryLength();
    
    void setSummaryLength(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewSummaryLength();
    
    void unsetSummaryLength();
    
    CTString getClickAndTypeStyle();
    
    boolean isSetClickAndTypeStyle();
    
    void setClickAndTypeStyle(final CTString p0);
    
    CTString addNewClickAndTypeStyle();
    
    void unsetClickAndTypeStyle();
    
    CTString getDefaultTableStyle();
    
    boolean isSetDefaultTableStyle();
    
    void setDefaultTableStyle(final CTString p0);
    
    CTString addNewDefaultTableStyle();
    
    void unsetDefaultTableStyle();
    
    CTOnOff getEvenAndOddHeaders();
    
    boolean isSetEvenAndOddHeaders();
    
    void setEvenAndOddHeaders(final CTOnOff p0);
    
    CTOnOff addNewEvenAndOddHeaders();
    
    void unsetEvenAndOddHeaders();
    
    CTOnOff getBookFoldRevPrinting();
    
    boolean isSetBookFoldRevPrinting();
    
    void setBookFoldRevPrinting(final CTOnOff p0);
    
    CTOnOff addNewBookFoldRevPrinting();
    
    void unsetBookFoldRevPrinting();
    
    CTOnOff getBookFoldPrinting();
    
    boolean isSetBookFoldPrinting();
    
    void setBookFoldPrinting(final CTOnOff p0);
    
    CTOnOff addNewBookFoldPrinting();
    
    void unsetBookFoldPrinting();
    
    CTDecimalNumber getBookFoldPrintingSheets();
    
    boolean isSetBookFoldPrintingSheets();
    
    void setBookFoldPrintingSheets(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewBookFoldPrintingSheets();
    
    void unsetBookFoldPrintingSheets();
    
    CTTwipsMeasure getDrawingGridHorizontalSpacing();
    
    boolean isSetDrawingGridHorizontalSpacing();
    
    void setDrawingGridHorizontalSpacing(final CTTwipsMeasure p0);
    
    CTTwipsMeasure addNewDrawingGridHorizontalSpacing();
    
    void unsetDrawingGridHorizontalSpacing();
    
    CTTwipsMeasure getDrawingGridVerticalSpacing();
    
    boolean isSetDrawingGridVerticalSpacing();
    
    void setDrawingGridVerticalSpacing(final CTTwipsMeasure p0);
    
    CTTwipsMeasure addNewDrawingGridVerticalSpacing();
    
    void unsetDrawingGridVerticalSpacing();
    
    CTDecimalNumber getDisplayHorizontalDrawingGridEvery();
    
    boolean isSetDisplayHorizontalDrawingGridEvery();
    
    void setDisplayHorizontalDrawingGridEvery(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewDisplayHorizontalDrawingGridEvery();
    
    void unsetDisplayHorizontalDrawingGridEvery();
    
    CTDecimalNumber getDisplayVerticalDrawingGridEvery();
    
    boolean isSetDisplayVerticalDrawingGridEvery();
    
    void setDisplayVerticalDrawingGridEvery(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewDisplayVerticalDrawingGridEvery();
    
    void unsetDisplayVerticalDrawingGridEvery();
    
    CTOnOff getDoNotUseMarginsForDrawingGridOrigin();
    
    boolean isSetDoNotUseMarginsForDrawingGridOrigin();
    
    void setDoNotUseMarginsForDrawingGridOrigin(final CTOnOff p0);
    
    CTOnOff addNewDoNotUseMarginsForDrawingGridOrigin();
    
    void unsetDoNotUseMarginsForDrawingGridOrigin();
    
    CTTwipsMeasure getDrawingGridHorizontalOrigin();
    
    boolean isSetDrawingGridHorizontalOrigin();
    
    void setDrawingGridHorizontalOrigin(final CTTwipsMeasure p0);
    
    CTTwipsMeasure addNewDrawingGridHorizontalOrigin();
    
    void unsetDrawingGridHorizontalOrigin();
    
    CTTwipsMeasure getDrawingGridVerticalOrigin();
    
    boolean isSetDrawingGridVerticalOrigin();
    
    void setDrawingGridVerticalOrigin(final CTTwipsMeasure p0);
    
    CTTwipsMeasure addNewDrawingGridVerticalOrigin();
    
    void unsetDrawingGridVerticalOrigin();
    
    CTOnOff getDoNotShadeFormData();
    
    boolean isSetDoNotShadeFormData();
    
    void setDoNotShadeFormData(final CTOnOff p0);
    
    CTOnOff addNewDoNotShadeFormData();
    
    void unsetDoNotShadeFormData();
    
    CTOnOff getNoPunctuationKerning();
    
    boolean isSetNoPunctuationKerning();
    
    void setNoPunctuationKerning(final CTOnOff p0);
    
    CTOnOff addNewNoPunctuationKerning();
    
    void unsetNoPunctuationKerning();
    
    CTCharacterSpacing getCharacterSpacingControl();
    
    boolean isSetCharacterSpacingControl();
    
    void setCharacterSpacingControl(final CTCharacterSpacing p0);
    
    CTCharacterSpacing addNewCharacterSpacingControl();
    
    void unsetCharacterSpacingControl();
    
    CTOnOff getPrintTwoOnOne();
    
    boolean isSetPrintTwoOnOne();
    
    void setPrintTwoOnOne(final CTOnOff p0);
    
    CTOnOff addNewPrintTwoOnOne();
    
    void unsetPrintTwoOnOne();
    
    CTOnOff getStrictFirstAndLastChars();
    
    boolean isSetStrictFirstAndLastChars();
    
    void setStrictFirstAndLastChars(final CTOnOff p0);
    
    CTOnOff addNewStrictFirstAndLastChars();
    
    void unsetStrictFirstAndLastChars();
    
    CTKinsoku getNoLineBreaksAfter();
    
    boolean isSetNoLineBreaksAfter();
    
    void setNoLineBreaksAfter(final CTKinsoku p0);
    
    CTKinsoku addNewNoLineBreaksAfter();
    
    void unsetNoLineBreaksAfter();
    
    CTKinsoku getNoLineBreaksBefore();
    
    boolean isSetNoLineBreaksBefore();
    
    void setNoLineBreaksBefore(final CTKinsoku p0);
    
    CTKinsoku addNewNoLineBreaksBefore();
    
    void unsetNoLineBreaksBefore();
    
    CTOnOff getSavePreviewPicture();
    
    boolean isSetSavePreviewPicture();
    
    void setSavePreviewPicture(final CTOnOff p0);
    
    CTOnOff addNewSavePreviewPicture();
    
    void unsetSavePreviewPicture();
    
    CTOnOff getDoNotValidateAgainstSchema();
    
    boolean isSetDoNotValidateAgainstSchema();
    
    void setDoNotValidateAgainstSchema(final CTOnOff p0);
    
    CTOnOff addNewDoNotValidateAgainstSchema();
    
    void unsetDoNotValidateAgainstSchema();
    
    CTOnOff getSaveInvalidXml();
    
    boolean isSetSaveInvalidXml();
    
    void setSaveInvalidXml(final CTOnOff p0);
    
    CTOnOff addNewSaveInvalidXml();
    
    void unsetSaveInvalidXml();
    
    CTOnOff getIgnoreMixedContent();
    
    boolean isSetIgnoreMixedContent();
    
    void setIgnoreMixedContent(final CTOnOff p0);
    
    CTOnOff addNewIgnoreMixedContent();
    
    void unsetIgnoreMixedContent();
    
    CTOnOff getAlwaysShowPlaceholderText();
    
    boolean isSetAlwaysShowPlaceholderText();
    
    void setAlwaysShowPlaceholderText(final CTOnOff p0);
    
    CTOnOff addNewAlwaysShowPlaceholderText();
    
    void unsetAlwaysShowPlaceholderText();
    
    CTOnOff getDoNotDemarcateInvalidXml();
    
    boolean isSetDoNotDemarcateInvalidXml();
    
    void setDoNotDemarcateInvalidXml(final CTOnOff p0);
    
    CTOnOff addNewDoNotDemarcateInvalidXml();
    
    void unsetDoNotDemarcateInvalidXml();
    
    CTOnOff getSaveXmlDataOnly();
    
    boolean isSetSaveXmlDataOnly();
    
    void setSaveXmlDataOnly(final CTOnOff p0);
    
    CTOnOff addNewSaveXmlDataOnly();
    
    void unsetSaveXmlDataOnly();
    
    CTOnOff getUseXSLTWhenSaving();
    
    boolean isSetUseXSLTWhenSaving();
    
    void setUseXSLTWhenSaving(final CTOnOff p0);
    
    CTOnOff addNewUseXSLTWhenSaving();
    
    void unsetUseXSLTWhenSaving();
    
    CTSaveThroughXslt getSaveThroughXslt();
    
    boolean isSetSaveThroughXslt();
    
    void setSaveThroughXslt(final CTSaveThroughXslt p0);
    
    CTSaveThroughXslt addNewSaveThroughXslt();
    
    void unsetSaveThroughXslt();
    
    CTOnOff getShowXMLTags();
    
    boolean isSetShowXMLTags();
    
    void setShowXMLTags(final CTOnOff p0);
    
    CTOnOff addNewShowXMLTags();
    
    void unsetShowXMLTags();
    
    CTOnOff getAlwaysMergeEmptyNamespace();
    
    boolean isSetAlwaysMergeEmptyNamespace();
    
    void setAlwaysMergeEmptyNamespace(final CTOnOff p0);
    
    CTOnOff addNewAlwaysMergeEmptyNamespace();
    
    void unsetAlwaysMergeEmptyNamespace();
    
    CTOnOff getUpdateFields();
    
    boolean isSetUpdateFields();
    
    void setUpdateFields(final CTOnOff p0);
    
    CTOnOff addNewUpdateFields();
    
    void unsetUpdateFields();
    
    CTShapeDefaults getHdrShapeDefaults();
    
    boolean isSetHdrShapeDefaults();
    
    void setHdrShapeDefaults(final CTShapeDefaults p0);
    
    CTShapeDefaults addNewHdrShapeDefaults();
    
    void unsetHdrShapeDefaults();
    
    CTFtnDocProps getFootnotePr();
    
    boolean isSetFootnotePr();
    
    void setFootnotePr(final CTFtnDocProps p0);
    
    CTFtnDocProps addNewFootnotePr();
    
    void unsetFootnotePr();
    
    CTEdnDocProps getEndnotePr();
    
    boolean isSetEndnotePr();
    
    void setEndnotePr(final CTEdnDocProps p0);
    
    CTEdnDocProps addNewEndnotePr();
    
    void unsetEndnotePr();
    
    CTCompat getCompat();
    
    boolean isSetCompat();
    
    void setCompat(final CTCompat p0);
    
    CTCompat addNewCompat();
    
    void unsetCompat();
    
    CTDocVars getDocVars();
    
    boolean isSetDocVars();
    
    void setDocVars(final CTDocVars p0);
    
    CTDocVars addNewDocVars();
    
    void unsetDocVars();
    
    CTDocRsids getRsids();
    
    boolean isSetRsids();
    
    void setRsids(final CTDocRsids p0);
    
    CTDocRsids addNewRsids();
    
    void unsetRsids();
    
    CTMathPr getMathPr();
    
    boolean isSetMathPr();
    
    void setMathPr(final CTMathPr p0);
    
    CTMathPr addNewMathPr();
    
    void unsetMathPr();
    
    CTOnOff getUiCompat97To2003();
    
    boolean isSetUiCompat97To2003();
    
    void setUiCompat97To2003(final CTOnOff p0);
    
    CTOnOff addNewUiCompat97To2003();
    
    void unsetUiCompat97To2003();
    
    List<CTString> getAttachedSchemaList();
    
    @Deprecated
    CTString[] getAttachedSchemaArray();
    
    CTString getAttachedSchemaArray(final int p0);
    
    int sizeOfAttachedSchemaArray();
    
    void setAttachedSchemaArray(final CTString[] p0);
    
    void setAttachedSchemaArray(final int p0, final CTString p1);
    
    CTString insertNewAttachedSchema(final int p0);
    
    CTString addNewAttachedSchema();
    
    void removeAttachedSchema(final int p0);
    
    CTLanguage getThemeFontLang();
    
    boolean isSetThemeFontLang();
    
    void setThemeFontLang(final CTLanguage p0);
    
    CTLanguage addNewThemeFontLang();
    
    void unsetThemeFontLang();
    
    CTColorSchemeMapping getClrSchemeMapping();
    
    boolean isSetClrSchemeMapping();
    
    void setClrSchemeMapping(final CTColorSchemeMapping p0);
    
    CTColorSchemeMapping addNewClrSchemeMapping();
    
    void unsetClrSchemeMapping();
    
    CTOnOff getDoNotIncludeSubdocsInStats();
    
    boolean isSetDoNotIncludeSubdocsInStats();
    
    void setDoNotIncludeSubdocsInStats(final CTOnOff p0);
    
    CTOnOff addNewDoNotIncludeSubdocsInStats();
    
    void unsetDoNotIncludeSubdocsInStats();
    
    CTOnOff getDoNotAutoCompressPictures();
    
    boolean isSetDoNotAutoCompressPictures();
    
    void setDoNotAutoCompressPictures(final CTOnOff p0);
    
    CTOnOff addNewDoNotAutoCompressPictures();
    
    void unsetDoNotAutoCompressPictures();
    
    CTEmpty getForceUpgrade();
    
    boolean isSetForceUpgrade();
    
    void setForceUpgrade(final CTEmpty p0);
    
    CTEmpty addNewForceUpgrade();
    
    void unsetForceUpgrade();
    
    CTCaptions getCaptions();
    
    boolean isSetCaptions();
    
    void setCaptions(final CTCaptions p0);
    
    CTCaptions addNewCaptions();
    
    void unsetCaptions();
    
    CTReadingModeInkLockDown getReadModeInkLockDown();
    
    boolean isSetReadModeInkLockDown();
    
    void setReadModeInkLockDown(final CTReadingModeInkLockDown p0);
    
    CTReadingModeInkLockDown addNewReadModeInkLockDown();
    
    void unsetReadModeInkLockDown();
    
    List<CTSmartTagType> getSmartTagTypeList();
    
    @Deprecated
    CTSmartTagType[] getSmartTagTypeArray();
    
    CTSmartTagType getSmartTagTypeArray(final int p0);
    
    int sizeOfSmartTagTypeArray();
    
    void setSmartTagTypeArray(final CTSmartTagType[] p0);
    
    void setSmartTagTypeArray(final int p0, final CTSmartTagType p1);
    
    CTSmartTagType insertNewSmartTagType(final int p0);
    
    CTSmartTagType addNewSmartTagType();
    
    void removeSmartTagType(final int p0);
    
    CTSchemaLibrary getSchemaLibrary();
    
    boolean isSetSchemaLibrary();
    
    void setSchemaLibrary(final CTSchemaLibrary p0);
    
    CTSchemaLibrary addNewSchemaLibrary();
    
    void unsetSchemaLibrary();
    
    CTShapeDefaults getShapeDefaults();
    
    boolean isSetShapeDefaults();
    
    void setShapeDefaults(final CTShapeDefaults p0);
    
    CTShapeDefaults addNewShapeDefaults();
    
    void unsetShapeDefaults();
    
    CTOnOff getDoNotEmbedSmartTags();
    
    boolean isSetDoNotEmbedSmartTags();
    
    void setDoNotEmbedSmartTags(final CTOnOff p0);
    
    CTOnOff addNewDoNotEmbedSmartTags();
    
    void unsetDoNotEmbedSmartTags();
    
    CTString getDecimalSymbol();
    
    boolean isSetDecimalSymbol();
    
    void setDecimalSymbol(final CTString p0);
    
    CTString addNewDecimalSymbol();
    
    void unsetDecimalSymbol();
    
    CTString getListSeparator();
    
    boolean isSetListSeparator();
    
    void setListSeparator(final CTString p0);
    
    CTString addNewListSeparator();
    
    void unsetListSeparator();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSettings.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSettings newInstance() {
            return (CTSettings)getTypeLoader().newInstance(CTSettings.type, (XmlOptions)null);
        }
        
        public static CTSettings newInstance(final XmlOptions xmlOptions) {
            return (CTSettings)getTypeLoader().newInstance(CTSettings.type, xmlOptions);
        }
        
        public static CTSettings parse(final String s) throws XmlException {
            return (CTSettings)getTypeLoader().parse(s, CTSettings.type, (XmlOptions)null);
        }
        
        public static CTSettings parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSettings)getTypeLoader().parse(s, CTSettings.type, xmlOptions);
        }
        
        public static CTSettings parse(final File file) throws XmlException, IOException {
            return (CTSettings)getTypeLoader().parse(file, CTSettings.type, (XmlOptions)null);
        }
        
        public static CTSettings parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSettings)getTypeLoader().parse(file, CTSettings.type, xmlOptions);
        }
        
        public static CTSettings parse(final URL url) throws XmlException, IOException {
            return (CTSettings)getTypeLoader().parse(url, CTSettings.type, (XmlOptions)null);
        }
        
        public static CTSettings parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSettings)getTypeLoader().parse(url, CTSettings.type, xmlOptions);
        }
        
        public static CTSettings parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSettings)getTypeLoader().parse(inputStream, CTSettings.type, (XmlOptions)null);
        }
        
        public static CTSettings parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSettings)getTypeLoader().parse(inputStream, CTSettings.type, xmlOptions);
        }
        
        public static CTSettings parse(final Reader reader) throws XmlException, IOException {
            return (CTSettings)getTypeLoader().parse(reader, CTSettings.type, (XmlOptions)null);
        }
        
        public static CTSettings parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSettings)getTypeLoader().parse(reader, CTSettings.type, xmlOptions);
        }
        
        public static CTSettings parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSettings)getTypeLoader().parse(xmlStreamReader, CTSettings.type, (XmlOptions)null);
        }
        
        public static CTSettings parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSettings)getTypeLoader().parse(xmlStreamReader, CTSettings.type, xmlOptions);
        }
        
        public static CTSettings parse(final Node node) throws XmlException {
            return (CTSettings)getTypeLoader().parse(node, CTSettings.type, (XmlOptions)null);
        }
        
        public static CTSettings parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSettings)getTypeLoader().parse(node, CTSettings.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSettings parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSettings)getTypeLoader().parse(xmlInputStream, CTSettings.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSettings parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSettings)getTypeLoader().parse(xmlInputStream, CTSettings.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSettings.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSettings.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
