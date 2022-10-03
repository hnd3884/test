package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.schemaLibrary.x2006.main.CTSchemaLibrary;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSmartTagType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTReadingModeInkLockDown;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCaptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEmpty;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColorSchemeMapping;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLanguage;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTMathPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocRsids;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocVars;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCompat;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEdnDocProps;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnDocProps;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShapeDefaults;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSaveThroughXslt;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTKinsoku;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCharacterSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocProtect;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChangesView;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMailMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShortHexNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRel;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTProof;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTWritingStyle;
import java.util.List;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTZoom;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTView;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTWriteProtection;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSettings;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSettingsImpl extends XmlComplexContentImpl implements CTSettings
{
    private static final long serialVersionUID = 1L;
    private static final QName WRITEPROTECTION$0;
    private static final QName VIEW$2;
    private static final QName ZOOM$4;
    private static final QName REMOVEPERSONALINFORMATION$6;
    private static final QName REMOVEDATEANDTIME$8;
    private static final QName DONOTDISPLAYPAGEBOUNDARIES$10;
    private static final QName DISPLAYBACKGROUNDSHAPE$12;
    private static final QName PRINTPOSTSCRIPTOVERTEXT$14;
    private static final QName PRINTFRACTIONALCHARACTERWIDTH$16;
    private static final QName PRINTFORMSDATA$18;
    private static final QName EMBEDTRUETYPEFONTS$20;
    private static final QName EMBEDSYSTEMFONTS$22;
    private static final QName SAVESUBSETFONTS$24;
    private static final QName SAVEFORMSDATA$26;
    private static final QName MIRRORMARGINS$28;
    private static final QName ALIGNBORDERSANDEDGES$30;
    private static final QName BORDERSDONOTSURROUNDHEADER$32;
    private static final QName BORDERSDONOTSURROUNDFOOTER$34;
    private static final QName GUTTERATTOP$36;
    private static final QName HIDESPELLINGERRORS$38;
    private static final QName HIDEGRAMMATICALERRORS$40;
    private static final QName ACTIVEWRITINGSTYLE$42;
    private static final QName PROOFSTATE$44;
    private static final QName FORMSDESIGN$46;
    private static final QName ATTACHEDTEMPLATE$48;
    private static final QName LINKSTYLES$50;
    private static final QName STYLEPANEFORMATFILTER$52;
    private static final QName STYLEPANESORTMETHOD$54;
    private static final QName DOCUMENTTYPE$56;
    private static final QName MAILMERGE$58;
    private static final QName REVISIONVIEW$60;
    private static final QName TRACKREVISIONS$62;
    private static final QName DONOTTRACKMOVES$64;
    private static final QName DONOTTRACKFORMATTING$66;
    private static final QName DOCUMENTPROTECTION$68;
    private static final QName AUTOFORMATOVERRIDE$70;
    private static final QName STYLELOCKTHEME$72;
    private static final QName STYLELOCKQFSET$74;
    private static final QName DEFAULTTABSTOP$76;
    private static final QName AUTOHYPHENATION$78;
    private static final QName CONSECUTIVEHYPHENLIMIT$80;
    private static final QName HYPHENATIONZONE$82;
    private static final QName DONOTHYPHENATECAPS$84;
    private static final QName SHOWENVELOPE$86;
    private static final QName SUMMARYLENGTH$88;
    private static final QName CLICKANDTYPESTYLE$90;
    private static final QName DEFAULTTABLESTYLE$92;
    private static final QName EVENANDODDHEADERS$94;
    private static final QName BOOKFOLDREVPRINTING$96;
    private static final QName BOOKFOLDPRINTING$98;
    private static final QName BOOKFOLDPRINTINGSHEETS$100;
    private static final QName DRAWINGGRIDHORIZONTALSPACING$102;
    private static final QName DRAWINGGRIDVERTICALSPACING$104;
    private static final QName DISPLAYHORIZONTALDRAWINGGRIDEVERY$106;
    private static final QName DISPLAYVERTICALDRAWINGGRIDEVERY$108;
    private static final QName DONOTUSEMARGINSFORDRAWINGGRIDORIGIN$110;
    private static final QName DRAWINGGRIDHORIZONTALORIGIN$112;
    private static final QName DRAWINGGRIDVERTICALORIGIN$114;
    private static final QName DONOTSHADEFORMDATA$116;
    private static final QName NOPUNCTUATIONKERNING$118;
    private static final QName CHARACTERSPACINGCONTROL$120;
    private static final QName PRINTTWOONONE$122;
    private static final QName STRICTFIRSTANDLASTCHARS$124;
    private static final QName NOLINEBREAKSAFTER$126;
    private static final QName NOLINEBREAKSBEFORE$128;
    private static final QName SAVEPREVIEWPICTURE$130;
    private static final QName DONOTVALIDATEAGAINSTSCHEMA$132;
    private static final QName SAVEINVALIDXML$134;
    private static final QName IGNOREMIXEDCONTENT$136;
    private static final QName ALWAYSSHOWPLACEHOLDERTEXT$138;
    private static final QName DONOTDEMARCATEINVALIDXML$140;
    private static final QName SAVEXMLDATAONLY$142;
    private static final QName USEXSLTWHENSAVING$144;
    private static final QName SAVETHROUGHXSLT$146;
    private static final QName SHOWXMLTAGS$148;
    private static final QName ALWAYSMERGEEMPTYNAMESPACE$150;
    private static final QName UPDATEFIELDS$152;
    private static final QName HDRSHAPEDEFAULTS$154;
    private static final QName FOOTNOTEPR$156;
    private static final QName ENDNOTEPR$158;
    private static final QName COMPAT$160;
    private static final QName DOCVARS$162;
    private static final QName RSIDS$164;
    private static final QName MATHPR$166;
    private static final QName UICOMPAT97TO2003$168;
    private static final QName ATTACHEDSCHEMA$170;
    private static final QName THEMEFONTLANG$172;
    private static final QName CLRSCHEMEMAPPING$174;
    private static final QName DONOTINCLUDESUBDOCSINSTATS$176;
    private static final QName DONOTAUTOCOMPRESSPICTURES$178;
    private static final QName FORCEUPGRADE$180;
    private static final QName CAPTIONS$182;
    private static final QName READMODEINKLOCKDOWN$184;
    private static final QName SMARTTAGTYPE$186;
    private static final QName SCHEMALIBRARY$188;
    private static final QName SHAPEDEFAULTS$190;
    private static final QName DONOTEMBEDSMARTTAGS$192;
    private static final QName DECIMALSYMBOL$194;
    private static final QName LISTSEPARATOR$196;
    
    public CTSettingsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTWriteProtection getWriteProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWriteProtection ctWriteProtection = (CTWriteProtection)this.get_store().find_element_user(CTSettingsImpl.WRITEPROTECTION$0, 0);
            if (ctWriteProtection == null) {
                return null;
            }
            return ctWriteProtection;
        }
    }
    
    public boolean isSetWriteProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.WRITEPROTECTION$0) != 0;
        }
    }
    
    public void setWriteProtection(final CTWriteProtection ctWriteProtection) {
        this.generatedSetterHelperImpl((XmlObject)ctWriteProtection, CTSettingsImpl.WRITEPROTECTION$0, 0, (short)1);
    }
    
    public CTWriteProtection addNewWriteProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWriteProtection)this.get_store().add_element_user(CTSettingsImpl.WRITEPROTECTION$0);
        }
    }
    
    public void unsetWriteProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.WRITEPROTECTION$0, 0);
        }
    }
    
    public CTView getView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTView ctView = (CTView)this.get_store().find_element_user(CTSettingsImpl.VIEW$2, 0);
            if (ctView == null) {
                return null;
            }
            return ctView;
        }
    }
    
    public boolean isSetView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.VIEW$2) != 0;
        }
    }
    
    public void setView(final CTView ctView) {
        this.generatedSetterHelperImpl((XmlObject)ctView, CTSettingsImpl.VIEW$2, 0, (short)1);
    }
    
    public CTView addNewView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTView)this.get_store().add_element_user(CTSettingsImpl.VIEW$2);
        }
    }
    
    public void unsetView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.VIEW$2, 0);
        }
    }
    
    public CTZoom getZoom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTZoom ctZoom = (CTZoom)this.get_store().find_element_user(CTSettingsImpl.ZOOM$4, 0);
            if (ctZoom == null) {
                return null;
            }
            return ctZoom;
        }
    }
    
    public boolean isSetZoom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.ZOOM$4) != 0;
        }
    }
    
    public void setZoom(final CTZoom ctZoom) {
        this.generatedSetterHelperImpl((XmlObject)ctZoom, CTSettingsImpl.ZOOM$4, 0, (short)1);
    }
    
    public CTZoom addNewZoom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTZoom)this.get_store().add_element_user(CTSettingsImpl.ZOOM$4);
        }
    }
    
    public void unsetZoom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.ZOOM$4, 0);
        }
    }
    
    public CTOnOff getRemovePersonalInformation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.REMOVEPERSONALINFORMATION$6, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetRemovePersonalInformation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.REMOVEPERSONALINFORMATION$6) != 0;
        }
    }
    
    public void setRemovePersonalInformation(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.REMOVEPERSONALINFORMATION$6, 0, (short)1);
    }
    
    public CTOnOff addNewRemovePersonalInformation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.REMOVEPERSONALINFORMATION$6);
        }
    }
    
    public void unsetRemovePersonalInformation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.REMOVEPERSONALINFORMATION$6, 0);
        }
    }
    
    public CTOnOff getRemoveDateAndTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.REMOVEDATEANDTIME$8, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetRemoveDateAndTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.REMOVEDATEANDTIME$8) != 0;
        }
    }
    
    public void setRemoveDateAndTime(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.REMOVEDATEANDTIME$8, 0, (short)1);
    }
    
    public CTOnOff addNewRemoveDateAndTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.REMOVEDATEANDTIME$8);
        }
    }
    
    public void unsetRemoveDateAndTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.REMOVEDATEANDTIME$8, 0);
        }
    }
    
    public CTOnOff getDoNotDisplayPageBoundaries() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.DONOTDISPLAYPAGEBOUNDARIES$10, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDoNotDisplayPageBoundaries() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DONOTDISPLAYPAGEBOUNDARIES$10) != 0;
        }
    }
    
    public void setDoNotDisplayPageBoundaries(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.DONOTDISPLAYPAGEBOUNDARIES$10, 0, (short)1);
    }
    
    public CTOnOff addNewDoNotDisplayPageBoundaries() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.DONOTDISPLAYPAGEBOUNDARIES$10);
        }
    }
    
    public void unsetDoNotDisplayPageBoundaries() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DONOTDISPLAYPAGEBOUNDARIES$10, 0);
        }
    }
    
    public CTOnOff getDisplayBackgroundShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.DISPLAYBACKGROUNDSHAPE$12, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDisplayBackgroundShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DISPLAYBACKGROUNDSHAPE$12) != 0;
        }
    }
    
    public void setDisplayBackgroundShape(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.DISPLAYBACKGROUNDSHAPE$12, 0, (short)1);
    }
    
    public CTOnOff addNewDisplayBackgroundShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.DISPLAYBACKGROUNDSHAPE$12);
        }
    }
    
    public void unsetDisplayBackgroundShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DISPLAYBACKGROUNDSHAPE$12, 0);
        }
    }
    
    public CTOnOff getPrintPostScriptOverText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.PRINTPOSTSCRIPTOVERTEXT$14, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetPrintPostScriptOverText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.PRINTPOSTSCRIPTOVERTEXT$14) != 0;
        }
    }
    
    public void setPrintPostScriptOverText(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.PRINTPOSTSCRIPTOVERTEXT$14, 0, (short)1);
    }
    
    public CTOnOff addNewPrintPostScriptOverText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.PRINTPOSTSCRIPTOVERTEXT$14);
        }
    }
    
    public void unsetPrintPostScriptOverText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.PRINTPOSTSCRIPTOVERTEXT$14, 0);
        }
    }
    
    public CTOnOff getPrintFractionalCharacterWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.PRINTFRACTIONALCHARACTERWIDTH$16, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetPrintFractionalCharacterWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.PRINTFRACTIONALCHARACTERWIDTH$16) != 0;
        }
    }
    
    public void setPrintFractionalCharacterWidth(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.PRINTFRACTIONALCHARACTERWIDTH$16, 0, (short)1);
    }
    
    public CTOnOff addNewPrintFractionalCharacterWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.PRINTFRACTIONALCHARACTERWIDTH$16);
        }
    }
    
    public void unsetPrintFractionalCharacterWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.PRINTFRACTIONALCHARACTERWIDTH$16, 0);
        }
    }
    
    public CTOnOff getPrintFormsData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.PRINTFORMSDATA$18, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetPrintFormsData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.PRINTFORMSDATA$18) != 0;
        }
    }
    
    public void setPrintFormsData(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.PRINTFORMSDATA$18, 0, (short)1);
    }
    
    public CTOnOff addNewPrintFormsData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.PRINTFORMSDATA$18);
        }
    }
    
    public void unsetPrintFormsData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.PRINTFORMSDATA$18, 0);
        }
    }
    
    public CTOnOff getEmbedTrueTypeFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.EMBEDTRUETYPEFONTS$20, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetEmbedTrueTypeFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.EMBEDTRUETYPEFONTS$20) != 0;
        }
    }
    
    public void setEmbedTrueTypeFonts(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.EMBEDTRUETYPEFONTS$20, 0, (short)1);
    }
    
    public CTOnOff addNewEmbedTrueTypeFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.EMBEDTRUETYPEFONTS$20);
        }
    }
    
    public void unsetEmbedTrueTypeFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.EMBEDTRUETYPEFONTS$20, 0);
        }
    }
    
    public CTOnOff getEmbedSystemFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.EMBEDSYSTEMFONTS$22, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetEmbedSystemFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.EMBEDSYSTEMFONTS$22) != 0;
        }
    }
    
    public void setEmbedSystemFonts(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.EMBEDSYSTEMFONTS$22, 0, (short)1);
    }
    
    public CTOnOff addNewEmbedSystemFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.EMBEDSYSTEMFONTS$22);
        }
    }
    
    public void unsetEmbedSystemFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.EMBEDSYSTEMFONTS$22, 0);
        }
    }
    
    public CTOnOff getSaveSubsetFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.SAVESUBSETFONTS$24, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSaveSubsetFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.SAVESUBSETFONTS$24) != 0;
        }
    }
    
    public void setSaveSubsetFonts(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.SAVESUBSETFONTS$24, 0, (short)1);
    }
    
    public CTOnOff addNewSaveSubsetFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.SAVESUBSETFONTS$24);
        }
    }
    
    public void unsetSaveSubsetFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.SAVESUBSETFONTS$24, 0);
        }
    }
    
    public CTOnOff getSaveFormsData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.SAVEFORMSDATA$26, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSaveFormsData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.SAVEFORMSDATA$26) != 0;
        }
    }
    
    public void setSaveFormsData(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.SAVEFORMSDATA$26, 0, (short)1);
    }
    
    public CTOnOff addNewSaveFormsData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.SAVEFORMSDATA$26);
        }
    }
    
    public void unsetSaveFormsData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.SAVEFORMSDATA$26, 0);
        }
    }
    
    public CTOnOff getMirrorMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.MIRRORMARGINS$28, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetMirrorMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.MIRRORMARGINS$28) != 0;
        }
    }
    
    public void setMirrorMargins(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.MIRRORMARGINS$28, 0, (short)1);
    }
    
    public CTOnOff addNewMirrorMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.MIRRORMARGINS$28);
        }
    }
    
    public void unsetMirrorMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.MIRRORMARGINS$28, 0);
        }
    }
    
    public CTOnOff getAlignBordersAndEdges() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.ALIGNBORDERSANDEDGES$30, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetAlignBordersAndEdges() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.ALIGNBORDERSANDEDGES$30) != 0;
        }
    }
    
    public void setAlignBordersAndEdges(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.ALIGNBORDERSANDEDGES$30, 0, (short)1);
    }
    
    public CTOnOff addNewAlignBordersAndEdges() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.ALIGNBORDERSANDEDGES$30);
        }
    }
    
    public void unsetAlignBordersAndEdges() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.ALIGNBORDERSANDEDGES$30, 0);
        }
    }
    
    public CTOnOff getBordersDoNotSurroundHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.BORDERSDONOTSURROUNDHEADER$32, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetBordersDoNotSurroundHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.BORDERSDONOTSURROUNDHEADER$32) != 0;
        }
    }
    
    public void setBordersDoNotSurroundHeader(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.BORDERSDONOTSURROUNDHEADER$32, 0, (short)1);
    }
    
    public CTOnOff addNewBordersDoNotSurroundHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.BORDERSDONOTSURROUNDHEADER$32);
        }
    }
    
    public void unsetBordersDoNotSurroundHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.BORDERSDONOTSURROUNDHEADER$32, 0);
        }
    }
    
    public CTOnOff getBordersDoNotSurroundFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.BORDERSDONOTSURROUNDFOOTER$34, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetBordersDoNotSurroundFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.BORDERSDONOTSURROUNDFOOTER$34) != 0;
        }
    }
    
    public void setBordersDoNotSurroundFooter(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.BORDERSDONOTSURROUNDFOOTER$34, 0, (short)1);
    }
    
    public CTOnOff addNewBordersDoNotSurroundFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.BORDERSDONOTSURROUNDFOOTER$34);
        }
    }
    
    public void unsetBordersDoNotSurroundFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.BORDERSDONOTSURROUNDFOOTER$34, 0);
        }
    }
    
    public CTOnOff getGutterAtTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.GUTTERATTOP$36, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetGutterAtTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.GUTTERATTOP$36) != 0;
        }
    }
    
    public void setGutterAtTop(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.GUTTERATTOP$36, 0, (short)1);
    }
    
    public CTOnOff addNewGutterAtTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.GUTTERATTOP$36);
        }
    }
    
    public void unsetGutterAtTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.GUTTERATTOP$36, 0);
        }
    }
    
    public CTOnOff getHideSpellingErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.HIDESPELLINGERRORS$38, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetHideSpellingErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.HIDESPELLINGERRORS$38) != 0;
        }
    }
    
    public void setHideSpellingErrors(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.HIDESPELLINGERRORS$38, 0, (short)1);
    }
    
    public CTOnOff addNewHideSpellingErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.HIDESPELLINGERRORS$38);
        }
    }
    
    public void unsetHideSpellingErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.HIDESPELLINGERRORS$38, 0);
        }
    }
    
    public CTOnOff getHideGrammaticalErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.HIDEGRAMMATICALERRORS$40, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetHideGrammaticalErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.HIDEGRAMMATICALERRORS$40) != 0;
        }
    }
    
    public void setHideGrammaticalErrors(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.HIDEGRAMMATICALERRORS$40, 0, (short)1);
    }
    
    public CTOnOff addNewHideGrammaticalErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.HIDEGRAMMATICALERRORS$40);
        }
    }
    
    public void unsetHideGrammaticalErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.HIDEGRAMMATICALERRORS$40, 0);
        }
    }
    
    public List<CTWritingStyle> getActiveWritingStyleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ActiveWritingStyleList extends AbstractList<CTWritingStyle>
            {
                @Override
                public CTWritingStyle get(final int n) {
                    return CTSettingsImpl.this.getActiveWritingStyleArray(n);
                }
                
                @Override
                public CTWritingStyle set(final int n, final CTWritingStyle ctWritingStyle) {
                    final CTWritingStyle activeWritingStyleArray = CTSettingsImpl.this.getActiveWritingStyleArray(n);
                    CTSettingsImpl.this.setActiveWritingStyleArray(n, ctWritingStyle);
                    return activeWritingStyleArray;
                }
                
                @Override
                public void add(final int n, final CTWritingStyle ctWritingStyle) {
                    CTSettingsImpl.this.insertNewActiveWritingStyle(n).set((XmlObject)ctWritingStyle);
                }
                
                @Override
                public CTWritingStyle remove(final int n) {
                    final CTWritingStyle activeWritingStyleArray = CTSettingsImpl.this.getActiveWritingStyleArray(n);
                    CTSettingsImpl.this.removeActiveWritingStyle(n);
                    return activeWritingStyleArray;
                }
                
                @Override
                public int size() {
                    return CTSettingsImpl.this.sizeOfActiveWritingStyleArray();
                }
            }
            return new ActiveWritingStyleList();
        }
    }
    
    @Deprecated
    public CTWritingStyle[] getActiveWritingStyleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSettingsImpl.ACTIVEWRITINGSTYLE$42, (List)list);
            final CTWritingStyle[] array = new CTWritingStyle[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTWritingStyle getActiveWritingStyleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWritingStyle ctWritingStyle = (CTWritingStyle)this.get_store().find_element_user(CTSettingsImpl.ACTIVEWRITINGSTYLE$42, n);
            if (ctWritingStyle == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctWritingStyle;
        }
    }
    
    public int sizeOfActiveWritingStyleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.ACTIVEWRITINGSTYLE$42);
        }
    }
    
    public void setActiveWritingStyleArray(final CTWritingStyle[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSettingsImpl.ACTIVEWRITINGSTYLE$42);
    }
    
    public void setActiveWritingStyleArray(final int n, final CTWritingStyle ctWritingStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctWritingStyle, CTSettingsImpl.ACTIVEWRITINGSTYLE$42, n, (short)2);
    }
    
    public CTWritingStyle insertNewActiveWritingStyle(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWritingStyle)this.get_store().insert_element_user(CTSettingsImpl.ACTIVEWRITINGSTYLE$42, n);
        }
    }
    
    public CTWritingStyle addNewActiveWritingStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWritingStyle)this.get_store().add_element_user(CTSettingsImpl.ACTIVEWRITINGSTYLE$42);
        }
    }
    
    public void removeActiveWritingStyle(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.ACTIVEWRITINGSTYLE$42, n);
        }
    }
    
    public CTProof getProofState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTProof ctProof = (CTProof)this.get_store().find_element_user(CTSettingsImpl.PROOFSTATE$44, 0);
            if (ctProof == null) {
                return null;
            }
            return ctProof;
        }
    }
    
    public boolean isSetProofState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.PROOFSTATE$44) != 0;
        }
    }
    
    public void setProofState(final CTProof ctProof) {
        this.generatedSetterHelperImpl((XmlObject)ctProof, CTSettingsImpl.PROOFSTATE$44, 0, (short)1);
    }
    
    public CTProof addNewProofState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProof)this.get_store().add_element_user(CTSettingsImpl.PROOFSTATE$44);
        }
    }
    
    public void unsetProofState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.PROOFSTATE$44, 0);
        }
    }
    
    public CTOnOff getFormsDesign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.FORMSDESIGN$46, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetFormsDesign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.FORMSDESIGN$46) != 0;
        }
    }
    
    public void setFormsDesign(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.FORMSDESIGN$46, 0, (short)1);
    }
    
    public CTOnOff addNewFormsDesign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.FORMSDESIGN$46);
        }
    }
    
    public void unsetFormsDesign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.FORMSDESIGN$46, 0);
        }
    }
    
    public CTRel getAttachedTemplate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRel ctRel = (CTRel)this.get_store().find_element_user(CTSettingsImpl.ATTACHEDTEMPLATE$48, 0);
            if (ctRel == null) {
                return null;
            }
            return ctRel;
        }
    }
    
    public boolean isSetAttachedTemplate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.ATTACHEDTEMPLATE$48) != 0;
        }
    }
    
    public void setAttachedTemplate(final CTRel ctRel) {
        this.generatedSetterHelperImpl((XmlObject)ctRel, CTSettingsImpl.ATTACHEDTEMPLATE$48, 0, (short)1);
    }
    
    public CTRel addNewAttachedTemplate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRel)this.get_store().add_element_user(CTSettingsImpl.ATTACHEDTEMPLATE$48);
        }
    }
    
    public void unsetAttachedTemplate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.ATTACHEDTEMPLATE$48, 0);
        }
    }
    
    public CTOnOff getLinkStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.LINKSTYLES$50, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetLinkStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.LINKSTYLES$50) != 0;
        }
    }
    
    public void setLinkStyles(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.LINKSTYLES$50, 0, (short)1);
    }
    
    public CTOnOff addNewLinkStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.LINKSTYLES$50);
        }
    }
    
    public void unsetLinkStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.LINKSTYLES$50, 0);
        }
    }
    
    public CTShortHexNumber getStylePaneFormatFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShortHexNumber ctShortHexNumber = (CTShortHexNumber)this.get_store().find_element_user(CTSettingsImpl.STYLEPANEFORMATFILTER$52, 0);
            if (ctShortHexNumber == null) {
                return null;
            }
            return ctShortHexNumber;
        }
    }
    
    public boolean isSetStylePaneFormatFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.STYLEPANEFORMATFILTER$52) != 0;
        }
    }
    
    public void setStylePaneFormatFilter(final CTShortHexNumber ctShortHexNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctShortHexNumber, CTSettingsImpl.STYLEPANEFORMATFILTER$52, 0, (short)1);
    }
    
    public CTShortHexNumber addNewStylePaneFormatFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShortHexNumber)this.get_store().add_element_user(CTSettingsImpl.STYLEPANEFORMATFILTER$52);
        }
    }
    
    public void unsetStylePaneFormatFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.STYLEPANEFORMATFILTER$52, 0);
        }
    }
    
    public CTShortHexNumber getStylePaneSortMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShortHexNumber ctShortHexNumber = (CTShortHexNumber)this.get_store().find_element_user(CTSettingsImpl.STYLEPANESORTMETHOD$54, 0);
            if (ctShortHexNumber == null) {
                return null;
            }
            return ctShortHexNumber;
        }
    }
    
    public boolean isSetStylePaneSortMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.STYLEPANESORTMETHOD$54) != 0;
        }
    }
    
    public void setStylePaneSortMethod(final CTShortHexNumber ctShortHexNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctShortHexNumber, CTSettingsImpl.STYLEPANESORTMETHOD$54, 0, (short)1);
    }
    
    public CTShortHexNumber addNewStylePaneSortMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShortHexNumber)this.get_store().add_element_user(CTSettingsImpl.STYLEPANESORTMETHOD$54);
        }
    }
    
    public void unsetStylePaneSortMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.STYLEPANESORTMETHOD$54, 0);
        }
    }
    
    public CTDocType getDocumentType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDocType ctDocType = (CTDocType)this.get_store().find_element_user(CTSettingsImpl.DOCUMENTTYPE$56, 0);
            if (ctDocType == null) {
                return null;
            }
            return ctDocType;
        }
    }
    
    public boolean isSetDocumentType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DOCUMENTTYPE$56) != 0;
        }
    }
    
    public void setDocumentType(final CTDocType ctDocType) {
        this.generatedSetterHelperImpl((XmlObject)ctDocType, CTSettingsImpl.DOCUMENTTYPE$56, 0, (short)1);
    }
    
    public CTDocType addNewDocumentType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDocType)this.get_store().add_element_user(CTSettingsImpl.DOCUMENTTYPE$56);
        }
    }
    
    public void unsetDocumentType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DOCUMENTTYPE$56, 0);
        }
    }
    
    public CTMailMerge getMailMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMailMerge ctMailMerge = (CTMailMerge)this.get_store().find_element_user(CTSettingsImpl.MAILMERGE$58, 0);
            if (ctMailMerge == null) {
                return null;
            }
            return ctMailMerge;
        }
    }
    
    public boolean isSetMailMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.MAILMERGE$58) != 0;
        }
    }
    
    public void setMailMerge(final CTMailMerge ctMailMerge) {
        this.generatedSetterHelperImpl((XmlObject)ctMailMerge, CTSettingsImpl.MAILMERGE$58, 0, (short)1);
    }
    
    public CTMailMerge addNewMailMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMailMerge)this.get_store().add_element_user(CTSettingsImpl.MAILMERGE$58);
        }
    }
    
    public void unsetMailMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.MAILMERGE$58, 0);
        }
    }
    
    public CTTrackChangesView getRevisionView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChangesView ctTrackChangesView = (CTTrackChangesView)this.get_store().find_element_user(CTSettingsImpl.REVISIONVIEW$60, 0);
            if (ctTrackChangesView == null) {
                return null;
            }
            return ctTrackChangesView;
        }
    }
    
    public boolean isSetRevisionView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.REVISIONVIEW$60) != 0;
        }
    }
    
    public void setRevisionView(final CTTrackChangesView ctTrackChangesView) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChangesView, CTSettingsImpl.REVISIONVIEW$60, 0, (short)1);
    }
    
    public CTTrackChangesView addNewRevisionView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChangesView)this.get_store().add_element_user(CTSettingsImpl.REVISIONVIEW$60);
        }
    }
    
    public void unsetRevisionView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.REVISIONVIEW$60, 0);
        }
    }
    
    public CTOnOff getTrackRevisions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.TRACKREVISIONS$62, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetTrackRevisions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.TRACKREVISIONS$62) != 0;
        }
    }
    
    public void setTrackRevisions(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.TRACKREVISIONS$62, 0, (short)1);
    }
    
    public CTOnOff addNewTrackRevisions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.TRACKREVISIONS$62);
        }
    }
    
    public void unsetTrackRevisions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.TRACKREVISIONS$62, 0);
        }
    }
    
    public CTOnOff getDoNotTrackMoves() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.DONOTTRACKMOVES$64, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDoNotTrackMoves() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DONOTTRACKMOVES$64) != 0;
        }
    }
    
    public void setDoNotTrackMoves(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.DONOTTRACKMOVES$64, 0, (short)1);
    }
    
    public CTOnOff addNewDoNotTrackMoves() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.DONOTTRACKMOVES$64);
        }
    }
    
    public void unsetDoNotTrackMoves() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DONOTTRACKMOVES$64, 0);
        }
    }
    
    public CTOnOff getDoNotTrackFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.DONOTTRACKFORMATTING$66, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDoNotTrackFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DONOTTRACKFORMATTING$66) != 0;
        }
    }
    
    public void setDoNotTrackFormatting(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.DONOTTRACKFORMATTING$66, 0, (short)1);
    }
    
    public CTOnOff addNewDoNotTrackFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.DONOTTRACKFORMATTING$66);
        }
    }
    
    public void unsetDoNotTrackFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DONOTTRACKFORMATTING$66, 0);
        }
    }
    
    public CTDocProtect getDocumentProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDocProtect ctDocProtect = (CTDocProtect)this.get_store().find_element_user(CTSettingsImpl.DOCUMENTPROTECTION$68, 0);
            if (ctDocProtect == null) {
                return null;
            }
            return ctDocProtect;
        }
    }
    
    public boolean isSetDocumentProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DOCUMENTPROTECTION$68) != 0;
        }
    }
    
    public void setDocumentProtection(final CTDocProtect ctDocProtect) {
        this.generatedSetterHelperImpl((XmlObject)ctDocProtect, CTSettingsImpl.DOCUMENTPROTECTION$68, 0, (short)1);
    }
    
    public CTDocProtect addNewDocumentProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDocProtect)this.get_store().add_element_user(CTSettingsImpl.DOCUMENTPROTECTION$68);
        }
    }
    
    public void unsetDocumentProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DOCUMENTPROTECTION$68, 0);
        }
    }
    
    public CTOnOff getAutoFormatOverride() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.AUTOFORMATOVERRIDE$70, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetAutoFormatOverride() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.AUTOFORMATOVERRIDE$70) != 0;
        }
    }
    
    public void setAutoFormatOverride(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.AUTOFORMATOVERRIDE$70, 0, (short)1);
    }
    
    public CTOnOff addNewAutoFormatOverride() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.AUTOFORMATOVERRIDE$70);
        }
    }
    
    public void unsetAutoFormatOverride() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.AUTOFORMATOVERRIDE$70, 0);
        }
    }
    
    public CTOnOff getStyleLockTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.STYLELOCKTHEME$72, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetStyleLockTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.STYLELOCKTHEME$72) != 0;
        }
    }
    
    public void setStyleLockTheme(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.STYLELOCKTHEME$72, 0, (short)1);
    }
    
    public CTOnOff addNewStyleLockTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.STYLELOCKTHEME$72);
        }
    }
    
    public void unsetStyleLockTheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.STYLELOCKTHEME$72, 0);
        }
    }
    
    public CTOnOff getStyleLockQFSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.STYLELOCKQFSET$74, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetStyleLockQFSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.STYLELOCKQFSET$74) != 0;
        }
    }
    
    public void setStyleLockQFSet(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.STYLELOCKQFSET$74, 0, (short)1);
    }
    
    public CTOnOff addNewStyleLockQFSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.STYLELOCKQFSET$74);
        }
    }
    
    public void unsetStyleLockQFSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.STYLELOCKQFSET$74, 0);
        }
    }
    
    public CTTwipsMeasure getDefaultTabStop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTwipsMeasure ctTwipsMeasure = (CTTwipsMeasure)this.get_store().find_element_user(CTSettingsImpl.DEFAULTTABSTOP$76, 0);
            if (ctTwipsMeasure == null) {
                return null;
            }
            return ctTwipsMeasure;
        }
    }
    
    public boolean isSetDefaultTabStop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DEFAULTTABSTOP$76) != 0;
        }
    }
    
    public void setDefaultTabStop(final CTTwipsMeasure ctTwipsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctTwipsMeasure, CTSettingsImpl.DEFAULTTABSTOP$76, 0, (short)1);
    }
    
    public CTTwipsMeasure addNewDefaultTabStop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTwipsMeasure)this.get_store().add_element_user(CTSettingsImpl.DEFAULTTABSTOP$76);
        }
    }
    
    public void unsetDefaultTabStop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DEFAULTTABSTOP$76, 0);
        }
    }
    
    public CTOnOff getAutoHyphenation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.AUTOHYPHENATION$78, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetAutoHyphenation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.AUTOHYPHENATION$78) != 0;
        }
    }
    
    public void setAutoHyphenation(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.AUTOHYPHENATION$78, 0, (short)1);
    }
    
    public CTOnOff addNewAutoHyphenation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.AUTOHYPHENATION$78);
        }
    }
    
    public void unsetAutoHyphenation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.AUTOHYPHENATION$78, 0);
        }
    }
    
    public CTDecimalNumber getConsecutiveHyphenLimit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTSettingsImpl.CONSECUTIVEHYPHENLIMIT$80, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetConsecutiveHyphenLimit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.CONSECUTIVEHYPHENLIMIT$80) != 0;
        }
    }
    
    public void setConsecutiveHyphenLimit(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTSettingsImpl.CONSECUTIVEHYPHENLIMIT$80, 0, (short)1);
    }
    
    public CTDecimalNumber addNewConsecutiveHyphenLimit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTSettingsImpl.CONSECUTIVEHYPHENLIMIT$80);
        }
    }
    
    public void unsetConsecutiveHyphenLimit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.CONSECUTIVEHYPHENLIMIT$80, 0);
        }
    }
    
    public CTTwipsMeasure getHyphenationZone() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTwipsMeasure ctTwipsMeasure = (CTTwipsMeasure)this.get_store().find_element_user(CTSettingsImpl.HYPHENATIONZONE$82, 0);
            if (ctTwipsMeasure == null) {
                return null;
            }
            return ctTwipsMeasure;
        }
    }
    
    public boolean isSetHyphenationZone() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.HYPHENATIONZONE$82) != 0;
        }
    }
    
    public void setHyphenationZone(final CTTwipsMeasure ctTwipsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctTwipsMeasure, CTSettingsImpl.HYPHENATIONZONE$82, 0, (short)1);
    }
    
    public CTTwipsMeasure addNewHyphenationZone() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTwipsMeasure)this.get_store().add_element_user(CTSettingsImpl.HYPHENATIONZONE$82);
        }
    }
    
    public void unsetHyphenationZone() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.HYPHENATIONZONE$82, 0);
        }
    }
    
    public CTOnOff getDoNotHyphenateCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.DONOTHYPHENATECAPS$84, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDoNotHyphenateCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DONOTHYPHENATECAPS$84) != 0;
        }
    }
    
    public void setDoNotHyphenateCaps(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.DONOTHYPHENATECAPS$84, 0, (short)1);
    }
    
    public CTOnOff addNewDoNotHyphenateCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.DONOTHYPHENATECAPS$84);
        }
    }
    
    public void unsetDoNotHyphenateCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DONOTHYPHENATECAPS$84, 0);
        }
    }
    
    public CTOnOff getShowEnvelope() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.SHOWENVELOPE$86, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetShowEnvelope() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.SHOWENVELOPE$86) != 0;
        }
    }
    
    public void setShowEnvelope(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.SHOWENVELOPE$86, 0, (short)1);
    }
    
    public CTOnOff addNewShowEnvelope() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.SHOWENVELOPE$86);
        }
    }
    
    public void unsetShowEnvelope() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.SHOWENVELOPE$86, 0);
        }
    }
    
    public CTDecimalNumber getSummaryLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTSettingsImpl.SUMMARYLENGTH$88, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetSummaryLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.SUMMARYLENGTH$88) != 0;
        }
    }
    
    public void setSummaryLength(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTSettingsImpl.SUMMARYLENGTH$88, 0, (short)1);
    }
    
    public CTDecimalNumber addNewSummaryLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTSettingsImpl.SUMMARYLENGTH$88);
        }
    }
    
    public void unsetSummaryLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.SUMMARYLENGTH$88, 0);
        }
    }
    
    public CTString getClickAndTypeStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTSettingsImpl.CLICKANDTYPESTYLE$90, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetClickAndTypeStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.CLICKANDTYPESTYLE$90) != 0;
        }
    }
    
    public void setClickAndTypeStyle(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTSettingsImpl.CLICKANDTYPESTYLE$90, 0, (short)1);
    }
    
    public CTString addNewClickAndTypeStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTSettingsImpl.CLICKANDTYPESTYLE$90);
        }
    }
    
    public void unsetClickAndTypeStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.CLICKANDTYPESTYLE$90, 0);
        }
    }
    
    public CTString getDefaultTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTSettingsImpl.DEFAULTTABLESTYLE$92, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetDefaultTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DEFAULTTABLESTYLE$92) != 0;
        }
    }
    
    public void setDefaultTableStyle(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTSettingsImpl.DEFAULTTABLESTYLE$92, 0, (short)1);
    }
    
    public CTString addNewDefaultTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTSettingsImpl.DEFAULTTABLESTYLE$92);
        }
    }
    
    public void unsetDefaultTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DEFAULTTABLESTYLE$92, 0);
        }
    }
    
    public CTOnOff getEvenAndOddHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.EVENANDODDHEADERS$94, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetEvenAndOddHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.EVENANDODDHEADERS$94) != 0;
        }
    }
    
    public void setEvenAndOddHeaders(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.EVENANDODDHEADERS$94, 0, (short)1);
    }
    
    public CTOnOff addNewEvenAndOddHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.EVENANDODDHEADERS$94);
        }
    }
    
    public void unsetEvenAndOddHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.EVENANDODDHEADERS$94, 0);
        }
    }
    
    public CTOnOff getBookFoldRevPrinting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.BOOKFOLDREVPRINTING$96, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetBookFoldRevPrinting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.BOOKFOLDREVPRINTING$96) != 0;
        }
    }
    
    public void setBookFoldRevPrinting(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.BOOKFOLDREVPRINTING$96, 0, (short)1);
    }
    
    public CTOnOff addNewBookFoldRevPrinting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.BOOKFOLDREVPRINTING$96);
        }
    }
    
    public void unsetBookFoldRevPrinting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.BOOKFOLDREVPRINTING$96, 0);
        }
    }
    
    public CTOnOff getBookFoldPrinting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.BOOKFOLDPRINTING$98, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetBookFoldPrinting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.BOOKFOLDPRINTING$98) != 0;
        }
    }
    
    public void setBookFoldPrinting(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.BOOKFOLDPRINTING$98, 0, (short)1);
    }
    
    public CTOnOff addNewBookFoldPrinting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.BOOKFOLDPRINTING$98);
        }
    }
    
    public void unsetBookFoldPrinting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.BOOKFOLDPRINTING$98, 0);
        }
    }
    
    public CTDecimalNumber getBookFoldPrintingSheets() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTSettingsImpl.BOOKFOLDPRINTINGSHEETS$100, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetBookFoldPrintingSheets() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.BOOKFOLDPRINTINGSHEETS$100) != 0;
        }
    }
    
    public void setBookFoldPrintingSheets(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTSettingsImpl.BOOKFOLDPRINTINGSHEETS$100, 0, (short)1);
    }
    
    public CTDecimalNumber addNewBookFoldPrintingSheets() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTSettingsImpl.BOOKFOLDPRINTINGSHEETS$100);
        }
    }
    
    public void unsetBookFoldPrintingSheets() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.BOOKFOLDPRINTINGSHEETS$100, 0);
        }
    }
    
    public CTTwipsMeasure getDrawingGridHorizontalSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTwipsMeasure ctTwipsMeasure = (CTTwipsMeasure)this.get_store().find_element_user(CTSettingsImpl.DRAWINGGRIDHORIZONTALSPACING$102, 0);
            if (ctTwipsMeasure == null) {
                return null;
            }
            return ctTwipsMeasure;
        }
    }
    
    public boolean isSetDrawingGridHorizontalSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DRAWINGGRIDHORIZONTALSPACING$102) != 0;
        }
    }
    
    public void setDrawingGridHorizontalSpacing(final CTTwipsMeasure ctTwipsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctTwipsMeasure, CTSettingsImpl.DRAWINGGRIDHORIZONTALSPACING$102, 0, (short)1);
    }
    
    public CTTwipsMeasure addNewDrawingGridHorizontalSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTwipsMeasure)this.get_store().add_element_user(CTSettingsImpl.DRAWINGGRIDHORIZONTALSPACING$102);
        }
    }
    
    public void unsetDrawingGridHorizontalSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DRAWINGGRIDHORIZONTALSPACING$102, 0);
        }
    }
    
    public CTTwipsMeasure getDrawingGridVerticalSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTwipsMeasure ctTwipsMeasure = (CTTwipsMeasure)this.get_store().find_element_user(CTSettingsImpl.DRAWINGGRIDVERTICALSPACING$104, 0);
            if (ctTwipsMeasure == null) {
                return null;
            }
            return ctTwipsMeasure;
        }
    }
    
    public boolean isSetDrawingGridVerticalSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DRAWINGGRIDVERTICALSPACING$104) != 0;
        }
    }
    
    public void setDrawingGridVerticalSpacing(final CTTwipsMeasure ctTwipsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctTwipsMeasure, CTSettingsImpl.DRAWINGGRIDVERTICALSPACING$104, 0, (short)1);
    }
    
    public CTTwipsMeasure addNewDrawingGridVerticalSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTwipsMeasure)this.get_store().add_element_user(CTSettingsImpl.DRAWINGGRIDVERTICALSPACING$104);
        }
    }
    
    public void unsetDrawingGridVerticalSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DRAWINGGRIDVERTICALSPACING$104, 0);
        }
    }
    
    public CTDecimalNumber getDisplayHorizontalDrawingGridEvery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTSettingsImpl.DISPLAYHORIZONTALDRAWINGGRIDEVERY$106, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetDisplayHorizontalDrawingGridEvery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DISPLAYHORIZONTALDRAWINGGRIDEVERY$106) != 0;
        }
    }
    
    public void setDisplayHorizontalDrawingGridEvery(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTSettingsImpl.DISPLAYHORIZONTALDRAWINGGRIDEVERY$106, 0, (short)1);
    }
    
    public CTDecimalNumber addNewDisplayHorizontalDrawingGridEvery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTSettingsImpl.DISPLAYHORIZONTALDRAWINGGRIDEVERY$106);
        }
    }
    
    public void unsetDisplayHorizontalDrawingGridEvery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DISPLAYHORIZONTALDRAWINGGRIDEVERY$106, 0);
        }
    }
    
    public CTDecimalNumber getDisplayVerticalDrawingGridEvery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTSettingsImpl.DISPLAYVERTICALDRAWINGGRIDEVERY$108, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetDisplayVerticalDrawingGridEvery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DISPLAYVERTICALDRAWINGGRIDEVERY$108) != 0;
        }
    }
    
    public void setDisplayVerticalDrawingGridEvery(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTSettingsImpl.DISPLAYVERTICALDRAWINGGRIDEVERY$108, 0, (short)1);
    }
    
    public CTDecimalNumber addNewDisplayVerticalDrawingGridEvery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTSettingsImpl.DISPLAYVERTICALDRAWINGGRIDEVERY$108);
        }
    }
    
    public void unsetDisplayVerticalDrawingGridEvery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DISPLAYVERTICALDRAWINGGRIDEVERY$108, 0);
        }
    }
    
    public CTOnOff getDoNotUseMarginsForDrawingGridOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.DONOTUSEMARGINSFORDRAWINGGRIDORIGIN$110, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDoNotUseMarginsForDrawingGridOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DONOTUSEMARGINSFORDRAWINGGRIDORIGIN$110) != 0;
        }
    }
    
    public void setDoNotUseMarginsForDrawingGridOrigin(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.DONOTUSEMARGINSFORDRAWINGGRIDORIGIN$110, 0, (short)1);
    }
    
    public CTOnOff addNewDoNotUseMarginsForDrawingGridOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.DONOTUSEMARGINSFORDRAWINGGRIDORIGIN$110);
        }
    }
    
    public void unsetDoNotUseMarginsForDrawingGridOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DONOTUSEMARGINSFORDRAWINGGRIDORIGIN$110, 0);
        }
    }
    
    public CTTwipsMeasure getDrawingGridHorizontalOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTwipsMeasure ctTwipsMeasure = (CTTwipsMeasure)this.get_store().find_element_user(CTSettingsImpl.DRAWINGGRIDHORIZONTALORIGIN$112, 0);
            if (ctTwipsMeasure == null) {
                return null;
            }
            return ctTwipsMeasure;
        }
    }
    
    public boolean isSetDrawingGridHorizontalOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DRAWINGGRIDHORIZONTALORIGIN$112) != 0;
        }
    }
    
    public void setDrawingGridHorizontalOrigin(final CTTwipsMeasure ctTwipsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctTwipsMeasure, CTSettingsImpl.DRAWINGGRIDHORIZONTALORIGIN$112, 0, (short)1);
    }
    
    public CTTwipsMeasure addNewDrawingGridHorizontalOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTwipsMeasure)this.get_store().add_element_user(CTSettingsImpl.DRAWINGGRIDHORIZONTALORIGIN$112);
        }
    }
    
    public void unsetDrawingGridHorizontalOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DRAWINGGRIDHORIZONTALORIGIN$112, 0);
        }
    }
    
    public CTTwipsMeasure getDrawingGridVerticalOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTwipsMeasure ctTwipsMeasure = (CTTwipsMeasure)this.get_store().find_element_user(CTSettingsImpl.DRAWINGGRIDVERTICALORIGIN$114, 0);
            if (ctTwipsMeasure == null) {
                return null;
            }
            return ctTwipsMeasure;
        }
    }
    
    public boolean isSetDrawingGridVerticalOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DRAWINGGRIDVERTICALORIGIN$114) != 0;
        }
    }
    
    public void setDrawingGridVerticalOrigin(final CTTwipsMeasure ctTwipsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctTwipsMeasure, CTSettingsImpl.DRAWINGGRIDVERTICALORIGIN$114, 0, (short)1);
    }
    
    public CTTwipsMeasure addNewDrawingGridVerticalOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTwipsMeasure)this.get_store().add_element_user(CTSettingsImpl.DRAWINGGRIDVERTICALORIGIN$114);
        }
    }
    
    public void unsetDrawingGridVerticalOrigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DRAWINGGRIDVERTICALORIGIN$114, 0);
        }
    }
    
    public CTOnOff getDoNotShadeFormData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.DONOTSHADEFORMDATA$116, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDoNotShadeFormData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DONOTSHADEFORMDATA$116) != 0;
        }
    }
    
    public void setDoNotShadeFormData(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.DONOTSHADEFORMDATA$116, 0, (short)1);
    }
    
    public CTOnOff addNewDoNotShadeFormData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.DONOTSHADEFORMDATA$116);
        }
    }
    
    public void unsetDoNotShadeFormData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DONOTSHADEFORMDATA$116, 0);
        }
    }
    
    public CTOnOff getNoPunctuationKerning() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.NOPUNCTUATIONKERNING$118, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetNoPunctuationKerning() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.NOPUNCTUATIONKERNING$118) != 0;
        }
    }
    
    public void setNoPunctuationKerning(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.NOPUNCTUATIONKERNING$118, 0, (short)1);
    }
    
    public CTOnOff addNewNoPunctuationKerning() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.NOPUNCTUATIONKERNING$118);
        }
    }
    
    public void unsetNoPunctuationKerning() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.NOPUNCTUATIONKERNING$118, 0);
        }
    }
    
    public CTCharacterSpacing getCharacterSpacingControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCharacterSpacing ctCharacterSpacing = (CTCharacterSpacing)this.get_store().find_element_user(CTSettingsImpl.CHARACTERSPACINGCONTROL$120, 0);
            if (ctCharacterSpacing == null) {
                return null;
            }
            return ctCharacterSpacing;
        }
    }
    
    public boolean isSetCharacterSpacingControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.CHARACTERSPACINGCONTROL$120) != 0;
        }
    }
    
    public void setCharacterSpacingControl(final CTCharacterSpacing ctCharacterSpacing) {
        this.generatedSetterHelperImpl((XmlObject)ctCharacterSpacing, CTSettingsImpl.CHARACTERSPACINGCONTROL$120, 0, (short)1);
    }
    
    public CTCharacterSpacing addNewCharacterSpacingControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCharacterSpacing)this.get_store().add_element_user(CTSettingsImpl.CHARACTERSPACINGCONTROL$120);
        }
    }
    
    public void unsetCharacterSpacingControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.CHARACTERSPACINGCONTROL$120, 0);
        }
    }
    
    public CTOnOff getPrintTwoOnOne() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.PRINTTWOONONE$122, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetPrintTwoOnOne() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.PRINTTWOONONE$122) != 0;
        }
    }
    
    public void setPrintTwoOnOne(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.PRINTTWOONONE$122, 0, (short)1);
    }
    
    public CTOnOff addNewPrintTwoOnOne() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.PRINTTWOONONE$122);
        }
    }
    
    public void unsetPrintTwoOnOne() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.PRINTTWOONONE$122, 0);
        }
    }
    
    public CTOnOff getStrictFirstAndLastChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.STRICTFIRSTANDLASTCHARS$124, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetStrictFirstAndLastChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.STRICTFIRSTANDLASTCHARS$124) != 0;
        }
    }
    
    public void setStrictFirstAndLastChars(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.STRICTFIRSTANDLASTCHARS$124, 0, (short)1);
    }
    
    public CTOnOff addNewStrictFirstAndLastChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.STRICTFIRSTANDLASTCHARS$124);
        }
    }
    
    public void unsetStrictFirstAndLastChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.STRICTFIRSTANDLASTCHARS$124, 0);
        }
    }
    
    public CTKinsoku getNoLineBreaksAfter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTKinsoku ctKinsoku = (CTKinsoku)this.get_store().find_element_user(CTSettingsImpl.NOLINEBREAKSAFTER$126, 0);
            if (ctKinsoku == null) {
                return null;
            }
            return ctKinsoku;
        }
    }
    
    public boolean isSetNoLineBreaksAfter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.NOLINEBREAKSAFTER$126) != 0;
        }
    }
    
    public void setNoLineBreaksAfter(final CTKinsoku ctKinsoku) {
        this.generatedSetterHelperImpl((XmlObject)ctKinsoku, CTSettingsImpl.NOLINEBREAKSAFTER$126, 0, (short)1);
    }
    
    public CTKinsoku addNewNoLineBreaksAfter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTKinsoku)this.get_store().add_element_user(CTSettingsImpl.NOLINEBREAKSAFTER$126);
        }
    }
    
    public void unsetNoLineBreaksAfter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.NOLINEBREAKSAFTER$126, 0);
        }
    }
    
    public CTKinsoku getNoLineBreaksBefore() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTKinsoku ctKinsoku = (CTKinsoku)this.get_store().find_element_user(CTSettingsImpl.NOLINEBREAKSBEFORE$128, 0);
            if (ctKinsoku == null) {
                return null;
            }
            return ctKinsoku;
        }
    }
    
    public boolean isSetNoLineBreaksBefore() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.NOLINEBREAKSBEFORE$128) != 0;
        }
    }
    
    public void setNoLineBreaksBefore(final CTKinsoku ctKinsoku) {
        this.generatedSetterHelperImpl((XmlObject)ctKinsoku, CTSettingsImpl.NOLINEBREAKSBEFORE$128, 0, (short)1);
    }
    
    public CTKinsoku addNewNoLineBreaksBefore() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTKinsoku)this.get_store().add_element_user(CTSettingsImpl.NOLINEBREAKSBEFORE$128);
        }
    }
    
    public void unsetNoLineBreaksBefore() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.NOLINEBREAKSBEFORE$128, 0);
        }
    }
    
    public CTOnOff getSavePreviewPicture() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.SAVEPREVIEWPICTURE$130, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSavePreviewPicture() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.SAVEPREVIEWPICTURE$130) != 0;
        }
    }
    
    public void setSavePreviewPicture(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.SAVEPREVIEWPICTURE$130, 0, (short)1);
    }
    
    public CTOnOff addNewSavePreviewPicture() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.SAVEPREVIEWPICTURE$130);
        }
    }
    
    public void unsetSavePreviewPicture() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.SAVEPREVIEWPICTURE$130, 0);
        }
    }
    
    public CTOnOff getDoNotValidateAgainstSchema() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.DONOTVALIDATEAGAINSTSCHEMA$132, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDoNotValidateAgainstSchema() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DONOTVALIDATEAGAINSTSCHEMA$132) != 0;
        }
    }
    
    public void setDoNotValidateAgainstSchema(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.DONOTVALIDATEAGAINSTSCHEMA$132, 0, (short)1);
    }
    
    public CTOnOff addNewDoNotValidateAgainstSchema() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.DONOTVALIDATEAGAINSTSCHEMA$132);
        }
    }
    
    public void unsetDoNotValidateAgainstSchema() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DONOTVALIDATEAGAINSTSCHEMA$132, 0);
        }
    }
    
    public CTOnOff getSaveInvalidXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.SAVEINVALIDXML$134, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSaveInvalidXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.SAVEINVALIDXML$134) != 0;
        }
    }
    
    public void setSaveInvalidXml(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.SAVEINVALIDXML$134, 0, (short)1);
    }
    
    public CTOnOff addNewSaveInvalidXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.SAVEINVALIDXML$134);
        }
    }
    
    public void unsetSaveInvalidXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.SAVEINVALIDXML$134, 0);
        }
    }
    
    public CTOnOff getIgnoreMixedContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.IGNOREMIXEDCONTENT$136, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetIgnoreMixedContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.IGNOREMIXEDCONTENT$136) != 0;
        }
    }
    
    public void setIgnoreMixedContent(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.IGNOREMIXEDCONTENT$136, 0, (short)1);
    }
    
    public CTOnOff addNewIgnoreMixedContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.IGNOREMIXEDCONTENT$136);
        }
    }
    
    public void unsetIgnoreMixedContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.IGNOREMIXEDCONTENT$136, 0);
        }
    }
    
    public CTOnOff getAlwaysShowPlaceholderText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.ALWAYSSHOWPLACEHOLDERTEXT$138, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetAlwaysShowPlaceholderText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.ALWAYSSHOWPLACEHOLDERTEXT$138) != 0;
        }
    }
    
    public void setAlwaysShowPlaceholderText(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.ALWAYSSHOWPLACEHOLDERTEXT$138, 0, (short)1);
    }
    
    public CTOnOff addNewAlwaysShowPlaceholderText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.ALWAYSSHOWPLACEHOLDERTEXT$138);
        }
    }
    
    public void unsetAlwaysShowPlaceholderText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.ALWAYSSHOWPLACEHOLDERTEXT$138, 0);
        }
    }
    
    public CTOnOff getDoNotDemarcateInvalidXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.DONOTDEMARCATEINVALIDXML$140, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDoNotDemarcateInvalidXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DONOTDEMARCATEINVALIDXML$140) != 0;
        }
    }
    
    public void setDoNotDemarcateInvalidXml(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.DONOTDEMARCATEINVALIDXML$140, 0, (short)1);
    }
    
    public CTOnOff addNewDoNotDemarcateInvalidXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.DONOTDEMARCATEINVALIDXML$140);
        }
    }
    
    public void unsetDoNotDemarcateInvalidXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DONOTDEMARCATEINVALIDXML$140, 0);
        }
    }
    
    public CTOnOff getSaveXmlDataOnly() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.SAVEXMLDATAONLY$142, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSaveXmlDataOnly() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.SAVEXMLDATAONLY$142) != 0;
        }
    }
    
    public void setSaveXmlDataOnly(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.SAVEXMLDATAONLY$142, 0, (short)1);
    }
    
    public CTOnOff addNewSaveXmlDataOnly() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.SAVEXMLDATAONLY$142);
        }
    }
    
    public void unsetSaveXmlDataOnly() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.SAVEXMLDATAONLY$142, 0);
        }
    }
    
    public CTOnOff getUseXSLTWhenSaving() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.USEXSLTWHENSAVING$144, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetUseXSLTWhenSaving() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.USEXSLTWHENSAVING$144) != 0;
        }
    }
    
    public void setUseXSLTWhenSaving(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.USEXSLTWHENSAVING$144, 0, (short)1);
    }
    
    public CTOnOff addNewUseXSLTWhenSaving() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.USEXSLTWHENSAVING$144);
        }
    }
    
    public void unsetUseXSLTWhenSaving() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.USEXSLTWHENSAVING$144, 0);
        }
    }
    
    public CTSaveThroughXslt getSaveThroughXslt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSaveThroughXslt ctSaveThroughXslt = (CTSaveThroughXslt)this.get_store().find_element_user(CTSettingsImpl.SAVETHROUGHXSLT$146, 0);
            if (ctSaveThroughXslt == null) {
                return null;
            }
            return ctSaveThroughXslt;
        }
    }
    
    public boolean isSetSaveThroughXslt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.SAVETHROUGHXSLT$146) != 0;
        }
    }
    
    public void setSaveThroughXslt(final CTSaveThroughXslt ctSaveThroughXslt) {
        this.generatedSetterHelperImpl((XmlObject)ctSaveThroughXslt, CTSettingsImpl.SAVETHROUGHXSLT$146, 0, (short)1);
    }
    
    public CTSaveThroughXslt addNewSaveThroughXslt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSaveThroughXslt)this.get_store().add_element_user(CTSettingsImpl.SAVETHROUGHXSLT$146);
        }
    }
    
    public void unsetSaveThroughXslt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.SAVETHROUGHXSLT$146, 0);
        }
    }
    
    public CTOnOff getShowXMLTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.SHOWXMLTAGS$148, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetShowXMLTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.SHOWXMLTAGS$148) != 0;
        }
    }
    
    public void setShowXMLTags(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.SHOWXMLTAGS$148, 0, (short)1);
    }
    
    public CTOnOff addNewShowXMLTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.SHOWXMLTAGS$148);
        }
    }
    
    public void unsetShowXMLTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.SHOWXMLTAGS$148, 0);
        }
    }
    
    public CTOnOff getAlwaysMergeEmptyNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.ALWAYSMERGEEMPTYNAMESPACE$150, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetAlwaysMergeEmptyNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.ALWAYSMERGEEMPTYNAMESPACE$150) != 0;
        }
    }
    
    public void setAlwaysMergeEmptyNamespace(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.ALWAYSMERGEEMPTYNAMESPACE$150, 0, (short)1);
    }
    
    public CTOnOff addNewAlwaysMergeEmptyNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.ALWAYSMERGEEMPTYNAMESPACE$150);
        }
    }
    
    public void unsetAlwaysMergeEmptyNamespace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.ALWAYSMERGEEMPTYNAMESPACE$150, 0);
        }
    }
    
    public CTOnOff getUpdateFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.UPDATEFIELDS$152, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetUpdateFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.UPDATEFIELDS$152) != 0;
        }
    }
    
    public void setUpdateFields(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.UPDATEFIELDS$152, 0, (short)1);
    }
    
    public CTOnOff addNewUpdateFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.UPDATEFIELDS$152);
        }
    }
    
    public void unsetUpdateFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.UPDATEFIELDS$152, 0);
        }
    }
    
    public CTShapeDefaults getHdrShapeDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeDefaults ctShapeDefaults = (CTShapeDefaults)this.get_store().find_element_user(CTSettingsImpl.HDRSHAPEDEFAULTS$154, 0);
            if (ctShapeDefaults == null) {
                return null;
            }
            return ctShapeDefaults;
        }
    }
    
    public boolean isSetHdrShapeDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.HDRSHAPEDEFAULTS$154) != 0;
        }
    }
    
    public void setHdrShapeDefaults(final CTShapeDefaults ctShapeDefaults) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeDefaults, CTSettingsImpl.HDRSHAPEDEFAULTS$154, 0, (short)1);
    }
    
    public CTShapeDefaults addNewHdrShapeDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeDefaults)this.get_store().add_element_user(CTSettingsImpl.HDRSHAPEDEFAULTS$154);
        }
    }
    
    public void unsetHdrShapeDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.HDRSHAPEDEFAULTS$154, 0);
        }
    }
    
    public CTFtnDocProps getFootnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFtnDocProps ctFtnDocProps = (CTFtnDocProps)this.get_store().find_element_user(CTSettingsImpl.FOOTNOTEPR$156, 0);
            if (ctFtnDocProps == null) {
                return null;
            }
            return ctFtnDocProps;
        }
    }
    
    public boolean isSetFootnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.FOOTNOTEPR$156) != 0;
        }
    }
    
    public void setFootnotePr(final CTFtnDocProps ctFtnDocProps) {
        this.generatedSetterHelperImpl((XmlObject)ctFtnDocProps, CTSettingsImpl.FOOTNOTEPR$156, 0, (short)1);
    }
    
    public CTFtnDocProps addNewFootnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFtnDocProps)this.get_store().add_element_user(CTSettingsImpl.FOOTNOTEPR$156);
        }
    }
    
    public void unsetFootnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.FOOTNOTEPR$156, 0);
        }
    }
    
    public CTEdnDocProps getEndnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEdnDocProps ctEdnDocProps = (CTEdnDocProps)this.get_store().find_element_user(CTSettingsImpl.ENDNOTEPR$158, 0);
            if (ctEdnDocProps == null) {
                return null;
            }
            return ctEdnDocProps;
        }
    }
    
    public boolean isSetEndnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.ENDNOTEPR$158) != 0;
        }
    }
    
    public void setEndnotePr(final CTEdnDocProps ctEdnDocProps) {
        this.generatedSetterHelperImpl((XmlObject)ctEdnDocProps, CTSettingsImpl.ENDNOTEPR$158, 0, (short)1);
    }
    
    public CTEdnDocProps addNewEndnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEdnDocProps)this.get_store().add_element_user(CTSettingsImpl.ENDNOTEPR$158);
        }
    }
    
    public void unsetEndnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.ENDNOTEPR$158, 0);
        }
    }
    
    public CTCompat getCompat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCompat ctCompat = (CTCompat)this.get_store().find_element_user(CTSettingsImpl.COMPAT$160, 0);
            if (ctCompat == null) {
                return null;
            }
            return ctCompat;
        }
    }
    
    public boolean isSetCompat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.COMPAT$160) != 0;
        }
    }
    
    public void setCompat(final CTCompat ctCompat) {
        this.generatedSetterHelperImpl((XmlObject)ctCompat, CTSettingsImpl.COMPAT$160, 0, (short)1);
    }
    
    public CTCompat addNewCompat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCompat)this.get_store().add_element_user(CTSettingsImpl.COMPAT$160);
        }
    }
    
    public void unsetCompat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.COMPAT$160, 0);
        }
    }
    
    public CTDocVars getDocVars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDocVars ctDocVars = (CTDocVars)this.get_store().find_element_user(CTSettingsImpl.DOCVARS$162, 0);
            if (ctDocVars == null) {
                return null;
            }
            return ctDocVars;
        }
    }
    
    public boolean isSetDocVars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DOCVARS$162) != 0;
        }
    }
    
    public void setDocVars(final CTDocVars ctDocVars) {
        this.generatedSetterHelperImpl((XmlObject)ctDocVars, CTSettingsImpl.DOCVARS$162, 0, (short)1);
    }
    
    public CTDocVars addNewDocVars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDocVars)this.get_store().add_element_user(CTSettingsImpl.DOCVARS$162);
        }
    }
    
    public void unsetDocVars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DOCVARS$162, 0);
        }
    }
    
    public CTDocRsids getRsids() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDocRsids ctDocRsids = (CTDocRsids)this.get_store().find_element_user(CTSettingsImpl.RSIDS$164, 0);
            if (ctDocRsids == null) {
                return null;
            }
            return ctDocRsids;
        }
    }
    
    public boolean isSetRsids() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.RSIDS$164) != 0;
        }
    }
    
    public void setRsids(final CTDocRsids ctDocRsids) {
        this.generatedSetterHelperImpl((XmlObject)ctDocRsids, CTSettingsImpl.RSIDS$164, 0, (short)1);
    }
    
    public CTDocRsids addNewRsids() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDocRsids)this.get_store().add_element_user(CTSettingsImpl.RSIDS$164);
        }
    }
    
    public void unsetRsids() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.RSIDS$164, 0);
        }
    }
    
    public CTMathPr getMathPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMathPr ctMathPr = (CTMathPr)this.get_store().find_element_user(CTSettingsImpl.MATHPR$166, 0);
            if (ctMathPr == null) {
                return null;
            }
            return ctMathPr;
        }
    }
    
    public boolean isSetMathPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.MATHPR$166) != 0;
        }
    }
    
    public void setMathPr(final CTMathPr ctMathPr) {
        this.generatedSetterHelperImpl((XmlObject)ctMathPr, CTSettingsImpl.MATHPR$166, 0, (short)1);
    }
    
    public CTMathPr addNewMathPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMathPr)this.get_store().add_element_user(CTSettingsImpl.MATHPR$166);
        }
    }
    
    public void unsetMathPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.MATHPR$166, 0);
        }
    }
    
    public CTOnOff getUiCompat97To2003() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.UICOMPAT97TO2003$168, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetUiCompat97To2003() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.UICOMPAT97TO2003$168) != 0;
        }
    }
    
    public void setUiCompat97To2003(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.UICOMPAT97TO2003$168, 0, (short)1);
    }
    
    public CTOnOff addNewUiCompat97To2003() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.UICOMPAT97TO2003$168);
        }
    }
    
    public void unsetUiCompat97To2003() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.UICOMPAT97TO2003$168, 0);
        }
    }
    
    public List<CTString> getAttachedSchemaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AttachedSchemaList extends AbstractList<CTString>
            {
                @Override
                public CTString get(final int n) {
                    return CTSettingsImpl.this.getAttachedSchemaArray(n);
                }
                
                @Override
                public CTString set(final int n, final CTString ctString) {
                    final CTString attachedSchemaArray = CTSettingsImpl.this.getAttachedSchemaArray(n);
                    CTSettingsImpl.this.setAttachedSchemaArray(n, ctString);
                    return attachedSchemaArray;
                }
                
                @Override
                public void add(final int n, final CTString ctString) {
                    CTSettingsImpl.this.insertNewAttachedSchema(n).set((XmlObject)ctString);
                }
                
                @Override
                public CTString remove(final int n) {
                    final CTString attachedSchemaArray = CTSettingsImpl.this.getAttachedSchemaArray(n);
                    CTSettingsImpl.this.removeAttachedSchema(n);
                    return attachedSchemaArray;
                }
                
                @Override
                public int size() {
                    return CTSettingsImpl.this.sizeOfAttachedSchemaArray();
                }
            }
            return new AttachedSchemaList();
        }
    }
    
    @Deprecated
    public CTString[] getAttachedSchemaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSettingsImpl.ATTACHEDSCHEMA$170, (List)list);
            final CTString[] array = new CTString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTString getAttachedSchemaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTSettingsImpl.ATTACHEDSCHEMA$170, n);
            if (ctString == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctString;
        }
    }
    
    public int sizeOfAttachedSchemaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.ATTACHEDSCHEMA$170);
        }
    }
    
    public void setAttachedSchemaArray(final CTString[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSettingsImpl.ATTACHEDSCHEMA$170);
    }
    
    public void setAttachedSchemaArray(final int n, final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTSettingsImpl.ATTACHEDSCHEMA$170, n, (short)2);
    }
    
    public CTString insertNewAttachedSchema(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().insert_element_user(CTSettingsImpl.ATTACHEDSCHEMA$170, n);
        }
    }
    
    public CTString addNewAttachedSchema() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTSettingsImpl.ATTACHEDSCHEMA$170);
        }
    }
    
    public void removeAttachedSchema(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.ATTACHEDSCHEMA$170, n);
        }
    }
    
    public CTLanguage getThemeFontLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLanguage ctLanguage = (CTLanguage)this.get_store().find_element_user(CTSettingsImpl.THEMEFONTLANG$172, 0);
            if (ctLanguage == null) {
                return null;
            }
            return ctLanguage;
        }
    }
    
    public boolean isSetThemeFontLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.THEMEFONTLANG$172) != 0;
        }
    }
    
    public void setThemeFontLang(final CTLanguage ctLanguage) {
        this.generatedSetterHelperImpl((XmlObject)ctLanguage, CTSettingsImpl.THEMEFONTLANG$172, 0, (short)1);
    }
    
    public CTLanguage addNewThemeFontLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLanguage)this.get_store().add_element_user(CTSettingsImpl.THEMEFONTLANG$172);
        }
    }
    
    public void unsetThemeFontLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.THEMEFONTLANG$172, 0);
        }
    }
    
    public CTColorSchemeMapping getClrSchemeMapping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColorSchemeMapping ctColorSchemeMapping = (CTColorSchemeMapping)this.get_store().find_element_user(CTSettingsImpl.CLRSCHEMEMAPPING$174, 0);
            if (ctColorSchemeMapping == null) {
                return null;
            }
            return ctColorSchemeMapping;
        }
    }
    
    public boolean isSetClrSchemeMapping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.CLRSCHEMEMAPPING$174) != 0;
        }
    }
    
    public void setClrSchemeMapping(final CTColorSchemeMapping ctColorSchemeMapping) {
        this.generatedSetterHelperImpl((XmlObject)ctColorSchemeMapping, CTSettingsImpl.CLRSCHEMEMAPPING$174, 0, (short)1);
    }
    
    public CTColorSchemeMapping addNewClrSchemeMapping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorSchemeMapping)this.get_store().add_element_user(CTSettingsImpl.CLRSCHEMEMAPPING$174);
        }
    }
    
    public void unsetClrSchemeMapping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.CLRSCHEMEMAPPING$174, 0);
        }
    }
    
    public CTOnOff getDoNotIncludeSubdocsInStats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.DONOTINCLUDESUBDOCSINSTATS$176, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDoNotIncludeSubdocsInStats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DONOTINCLUDESUBDOCSINSTATS$176) != 0;
        }
    }
    
    public void setDoNotIncludeSubdocsInStats(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.DONOTINCLUDESUBDOCSINSTATS$176, 0, (short)1);
    }
    
    public CTOnOff addNewDoNotIncludeSubdocsInStats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.DONOTINCLUDESUBDOCSINSTATS$176);
        }
    }
    
    public void unsetDoNotIncludeSubdocsInStats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DONOTINCLUDESUBDOCSINSTATS$176, 0);
        }
    }
    
    public CTOnOff getDoNotAutoCompressPictures() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.DONOTAUTOCOMPRESSPICTURES$178, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDoNotAutoCompressPictures() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DONOTAUTOCOMPRESSPICTURES$178) != 0;
        }
    }
    
    public void setDoNotAutoCompressPictures(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.DONOTAUTOCOMPRESSPICTURES$178, 0, (short)1);
    }
    
    public CTOnOff addNewDoNotAutoCompressPictures() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.DONOTAUTOCOMPRESSPICTURES$178);
        }
    }
    
    public void unsetDoNotAutoCompressPictures() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DONOTAUTOCOMPRESSPICTURES$178, 0);
        }
    }
    
    public CTEmpty getForceUpgrade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTSettingsImpl.FORCEUPGRADE$180, 0);
            if (ctEmpty == null) {
                return null;
            }
            return ctEmpty;
        }
    }
    
    public boolean isSetForceUpgrade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.FORCEUPGRADE$180) != 0;
        }
    }
    
    public void setForceUpgrade(final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTSettingsImpl.FORCEUPGRADE$180, 0, (short)1);
    }
    
    public CTEmpty addNewForceUpgrade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTSettingsImpl.FORCEUPGRADE$180);
        }
    }
    
    public void unsetForceUpgrade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.FORCEUPGRADE$180, 0);
        }
    }
    
    public CTCaptions getCaptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCaptions ctCaptions = (CTCaptions)this.get_store().find_element_user(CTSettingsImpl.CAPTIONS$182, 0);
            if (ctCaptions == null) {
                return null;
            }
            return ctCaptions;
        }
    }
    
    public boolean isSetCaptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.CAPTIONS$182) != 0;
        }
    }
    
    public void setCaptions(final CTCaptions ctCaptions) {
        this.generatedSetterHelperImpl((XmlObject)ctCaptions, CTSettingsImpl.CAPTIONS$182, 0, (short)1);
    }
    
    public CTCaptions addNewCaptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCaptions)this.get_store().add_element_user(CTSettingsImpl.CAPTIONS$182);
        }
    }
    
    public void unsetCaptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.CAPTIONS$182, 0);
        }
    }
    
    public CTReadingModeInkLockDown getReadModeInkLockDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTReadingModeInkLockDown ctReadingModeInkLockDown = (CTReadingModeInkLockDown)this.get_store().find_element_user(CTSettingsImpl.READMODEINKLOCKDOWN$184, 0);
            if (ctReadingModeInkLockDown == null) {
                return null;
            }
            return ctReadingModeInkLockDown;
        }
    }
    
    public boolean isSetReadModeInkLockDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.READMODEINKLOCKDOWN$184) != 0;
        }
    }
    
    public void setReadModeInkLockDown(final CTReadingModeInkLockDown ctReadingModeInkLockDown) {
        this.generatedSetterHelperImpl((XmlObject)ctReadingModeInkLockDown, CTSettingsImpl.READMODEINKLOCKDOWN$184, 0, (short)1);
    }
    
    public CTReadingModeInkLockDown addNewReadModeInkLockDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTReadingModeInkLockDown)this.get_store().add_element_user(CTSettingsImpl.READMODEINKLOCKDOWN$184);
        }
    }
    
    public void unsetReadModeInkLockDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.READMODEINKLOCKDOWN$184, 0);
        }
    }
    
    public List<CTSmartTagType> getSmartTagTypeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SmartTagTypeList extends AbstractList<CTSmartTagType>
            {
                @Override
                public CTSmartTagType get(final int n) {
                    return CTSettingsImpl.this.getSmartTagTypeArray(n);
                }
                
                @Override
                public CTSmartTagType set(final int n, final CTSmartTagType ctSmartTagType) {
                    final CTSmartTagType smartTagTypeArray = CTSettingsImpl.this.getSmartTagTypeArray(n);
                    CTSettingsImpl.this.setSmartTagTypeArray(n, ctSmartTagType);
                    return smartTagTypeArray;
                }
                
                @Override
                public void add(final int n, final CTSmartTagType ctSmartTagType) {
                    CTSettingsImpl.this.insertNewSmartTagType(n).set((XmlObject)ctSmartTagType);
                }
                
                @Override
                public CTSmartTagType remove(final int n) {
                    final CTSmartTagType smartTagTypeArray = CTSettingsImpl.this.getSmartTagTypeArray(n);
                    CTSettingsImpl.this.removeSmartTagType(n);
                    return smartTagTypeArray;
                }
                
                @Override
                public int size() {
                    return CTSettingsImpl.this.sizeOfSmartTagTypeArray();
                }
            }
            return new SmartTagTypeList();
        }
    }
    
    @Deprecated
    public CTSmartTagType[] getSmartTagTypeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSettingsImpl.SMARTTAGTYPE$186, (List)list);
            final CTSmartTagType[] array = new CTSmartTagType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSmartTagType getSmartTagTypeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSmartTagType ctSmartTagType = (CTSmartTagType)this.get_store().find_element_user(CTSettingsImpl.SMARTTAGTYPE$186, n);
            if (ctSmartTagType == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSmartTagType;
        }
    }
    
    public int sizeOfSmartTagTypeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.SMARTTAGTYPE$186);
        }
    }
    
    public void setSmartTagTypeArray(final CTSmartTagType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSettingsImpl.SMARTTAGTYPE$186);
    }
    
    public void setSmartTagTypeArray(final int n, final CTSmartTagType ctSmartTagType) {
        this.generatedSetterHelperImpl((XmlObject)ctSmartTagType, CTSettingsImpl.SMARTTAGTYPE$186, n, (short)2);
    }
    
    public CTSmartTagType insertNewSmartTagType(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSmartTagType)this.get_store().insert_element_user(CTSettingsImpl.SMARTTAGTYPE$186, n);
        }
    }
    
    public CTSmartTagType addNewSmartTagType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSmartTagType)this.get_store().add_element_user(CTSettingsImpl.SMARTTAGTYPE$186);
        }
    }
    
    public void removeSmartTagType(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.SMARTTAGTYPE$186, n);
        }
    }
    
    public CTSchemaLibrary getSchemaLibrary() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSchemaLibrary ctSchemaLibrary = (CTSchemaLibrary)this.get_store().find_element_user(CTSettingsImpl.SCHEMALIBRARY$188, 0);
            if (ctSchemaLibrary == null) {
                return null;
            }
            return ctSchemaLibrary;
        }
    }
    
    public boolean isSetSchemaLibrary() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.SCHEMALIBRARY$188) != 0;
        }
    }
    
    public void setSchemaLibrary(final CTSchemaLibrary ctSchemaLibrary) {
        this.generatedSetterHelperImpl((XmlObject)ctSchemaLibrary, CTSettingsImpl.SCHEMALIBRARY$188, 0, (short)1);
    }
    
    public CTSchemaLibrary addNewSchemaLibrary() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSchemaLibrary)this.get_store().add_element_user(CTSettingsImpl.SCHEMALIBRARY$188);
        }
    }
    
    public void unsetSchemaLibrary() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.SCHEMALIBRARY$188, 0);
        }
    }
    
    public CTShapeDefaults getShapeDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeDefaults ctShapeDefaults = (CTShapeDefaults)this.get_store().find_element_user(CTSettingsImpl.SHAPEDEFAULTS$190, 0);
            if (ctShapeDefaults == null) {
                return null;
            }
            return ctShapeDefaults;
        }
    }
    
    public boolean isSetShapeDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.SHAPEDEFAULTS$190) != 0;
        }
    }
    
    public void setShapeDefaults(final CTShapeDefaults ctShapeDefaults) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeDefaults, CTSettingsImpl.SHAPEDEFAULTS$190, 0, (short)1);
    }
    
    public CTShapeDefaults addNewShapeDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeDefaults)this.get_store().add_element_user(CTSettingsImpl.SHAPEDEFAULTS$190);
        }
    }
    
    public void unsetShapeDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.SHAPEDEFAULTS$190, 0);
        }
    }
    
    public CTOnOff getDoNotEmbedSmartTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSettingsImpl.DONOTEMBEDSMARTTAGS$192, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDoNotEmbedSmartTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DONOTEMBEDSMARTTAGS$192) != 0;
        }
    }
    
    public void setDoNotEmbedSmartTags(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSettingsImpl.DONOTEMBEDSMARTTAGS$192, 0, (short)1);
    }
    
    public CTOnOff addNewDoNotEmbedSmartTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSettingsImpl.DONOTEMBEDSMARTTAGS$192);
        }
    }
    
    public void unsetDoNotEmbedSmartTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DONOTEMBEDSMARTTAGS$192, 0);
        }
    }
    
    public CTString getDecimalSymbol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTSettingsImpl.DECIMALSYMBOL$194, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetDecimalSymbol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.DECIMALSYMBOL$194) != 0;
        }
    }
    
    public void setDecimalSymbol(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTSettingsImpl.DECIMALSYMBOL$194, 0, (short)1);
    }
    
    public CTString addNewDecimalSymbol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTSettingsImpl.DECIMALSYMBOL$194);
        }
    }
    
    public void unsetDecimalSymbol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.DECIMALSYMBOL$194, 0);
        }
    }
    
    public CTString getListSeparator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTSettingsImpl.LISTSEPARATOR$196, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetListSeparator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSettingsImpl.LISTSEPARATOR$196) != 0;
        }
    }
    
    public void setListSeparator(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTSettingsImpl.LISTSEPARATOR$196, 0, (short)1);
    }
    
    public CTString addNewListSeparator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTSettingsImpl.LISTSEPARATOR$196);
        }
    }
    
    public void unsetListSeparator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSettingsImpl.LISTSEPARATOR$196, 0);
        }
    }
    
    static {
        WRITEPROTECTION$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "writeProtection");
        VIEW$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "view");
        ZOOM$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "zoom");
        REMOVEPERSONALINFORMATION$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "removePersonalInformation");
        REMOVEDATEANDTIME$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "removeDateAndTime");
        DONOTDISPLAYPAGEBOUNDARIES$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotDisplayPageBoundaries");
        DISPLAYBACKGROUNDSHAPE$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "displayBackgroundShape");
        PRINTPOSTSCRIPTOVERTEXT$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "printPostScriptOverText");
        PRINTFRACTIONALCHARACTERWIDTH$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "printFractionalCharacterWidth");
        PRINTFORMSDATA$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "printFormsData");
        EMBEDTRUETYPEFONTS$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "embedTrueTypeFonts");
        EMBEDSYSTEMFONTS$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "embedSystemFonts");
        SAVESUBSETFONTS$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "saveSubsetFonts");
        SAVEFORMSDATA$26 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "saveFormsData");
        MIRRORMARGINS$28 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "mirrorMargins");
        ALIGNBORDERSANDEDGES$30 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "alignBordersAndEdges");
        BORDERSDONOTSURROUNDHEADER$32 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bordersDoNotSurroundHeader");
        BORDERSDONOTSURROUNDFOOTER$34 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bordersDoNotSurroundFooter");
        GUTTERATTOP$36 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "gutterAtTop");
        HIDESPELLINGERRORS$38 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hideSpellingErrors");
        HIDEGRAMMATICALERRORS$40 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hideGrammaticalErrors");
        ACTIVEWRITINGSTYLE$42 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "activeWritingStyle");
        PROOFSTATE$44 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "proofState");
        FORMSDESIGN$46 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "formsDesign");
        ATTACHEDTEMPLATE$48 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "attachedTemplate");
        LINKSTYLES$50 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "linkStyles");
        STYLEPANEFORMATFILTER$52 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "stylePaneFormatFilter");
        STYLEPANESORTMETHOD$54 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "stylePaneSortMethod");
        DOCUMENTTYPE$56 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "documentType");
        MAILMERGE$58 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "mailMerge");
        REVISIONVIEW$60 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "revisionView");
        TRACKREVISIONS$62 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "trackRevisions");
        DONOTTRACKMOVES$64 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotTrackMoves");
        DONOTTRACKFORMATTING$66 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotTrackFormatting");
        DOCUMENTPROTECTION$68 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "documentProtection");
        AUTOFORMATOVERRIDE$70 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "autoFormatOverride");
        STYLELOCKTHEME$72 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "styleLockTheme");
        STYLELOCKQFSET$74 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "styleLockQFSet");
        DEFAULTTABSTOP$76 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "defaultTabStop");
        AUTOHYPHENATION$78 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "autoHyphenation");
        CONSECUTIVEHYPHENLIMIT$80 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "consecutiveHyphenLimit");
        HYPHENATIONZONE$82 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hyphenationZone");
        DONOTHYPHENATECAPS$84 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotHyphenateCaps");
        SHOWENVELOPE$86 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "showEnvelope");
        SUMMARYLENGTH$88 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "summaryLength");
        CLICKANDTYPESTYLE$90 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "clickAndTypeStyle");
        DEFAULTTABLESTYLE$92 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "defaultTableStyle");
        EVENANDODDHEADERS$94 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "evenAndOddHeaders");
        BOOKFOLDREVPRINTING$96 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookFoldRevPrinting");
        BOOKFOLDPRINTING$98 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookFoldPrinting");
        BOOKFOLDPRINTINGSHEETS$100 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookFoldPrintingSheets");
        DRAWINGGRIDHORIZONTALSPACING$102 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "drawingGridHorizontalSpacing");
        DRAWINGGRIDVERTICALSPACING$104 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "drawingGridVerticalSpacing");
        DISPLAYHORIZONTALDRAWINGGRIDEVERY$106 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "displayHorizontalDrawingGridEvery");
        DISPLAYVERTICALDRAWINGGRIDEVERY$108 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "displayVerticalDrawingGridEvery");
        DONOTUSEMARGINSFORDRAWINGGRIDORIGIN$110 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotUseMarginsForDrawingGridOrigin");
        DRAWINGGRIDHORIZONTALORIGIN$112 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "drawingGridHorizontalOrigin");
        DRAWINGGRIDVERTICALORIGIN$114 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "drawingGridVerticalOrigin");
        DONOTSHADEFORMDATA$116 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotShadeFormData");
        NOPUNCTUATIONKERNING$118 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "noPunctuationKerning");
        CHARACTERSPACINGCONTROL$120 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "characterSpacingControl");
        PRINTTWOONONE$122 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "printTwoOnOne");
        STRICTFIRSTANDLASTCHARS$124 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "strictFirstAndLastChars");
        NOLINEBREAKSAFTER$126 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "noLineBreaksAfter");
        NOLINEBREAKSBEFORE$128 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "noLineBreaksBefore");
        SAVEPREVIEWPICTURE$130 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "savePreviewPicture");
        DONOTVALIDATEAGAINSTSCHEMA$132 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotValidateAgainstSchema");
        SAVEINVALIDXML$134 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "saveInvalidXml");
        IGNOREMIXEDCONTENT$136 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ignoreMixedContent");
        ALWAYSSHOWPLACEHOLDERTEXT$138 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "alwaysShowPlaceholderText");
        DONOTDEMARCATEINVALIDXML$140 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotDemarcateInvalidXml");
        SAVEXMLDATAONLY$142 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "saveXmlDataOnly");
        USEXSLTWHENSAVING$144 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "useXSLTWhenSaving");
        SAVETHROUGHXSLT$146 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "saveThroughXslt");
        SHOWXMLTAGS$148 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "showXMLTags");
        ALWAYSMERGEEMPTYNAMESPACE$150 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "alwaysMergeEmptyNamespace");
        UPDATEFIELDS$152 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "updateFields");
        HDRSHAPEDEFAULTS$154 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hdrShapeDefaults");
        FOOTNOTEPR$156 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "footnotePr");
        ENDNOTEPR$158 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "endnotePr");
        COMPAT$160 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "compat");
        DOCVARS$162 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "docVars");
        RSIDS$164 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsids");
        MATHPR$166 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "mathPr");
        UICOMPAT97TO2003$168 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "uiCompat97To2003");
        ATTACHEDSCHEMA$170 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "attachedSchema");
        THEMEFONTLANG$172 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeFontLang");
        CLRSCHEMEMAPPING$174 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "clrSchemeMapping");
        DONOTINCLUDESUBDOCSINSTATS$176 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotIncludeSubdocsInStats");
        DONOTAUTOCOMPRESSPICTURES$178 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotAutoCompressPictures");
        FORCEUPGRADE$180 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "forceUpgrade");
        CAPTIONS$182 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "captions");
        READMODEINKLOCKDOWN$184 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "readModeInkLockDown");
        SMARTTAGTYPE$186 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "smartTagType");
        SCHEMALIBRARY$188 = new QName("http://schemas.openxmlformats.org/schemaLibrary/2006/main", "schemaLibrary");
        SHAPEDEFAULTS$190 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "shapeDefaults");
        DONOTEMBEDSMARTTAGS$192 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotEmbedSmartTags");
        DECIMALSYMBOL$194 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "decimalSymbol");
        LISTSEPARATOR$196 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "listSeparator");
    }
}
