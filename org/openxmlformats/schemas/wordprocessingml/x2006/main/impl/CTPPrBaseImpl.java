package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCnf;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextboxTightWrap;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextAlignment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextDirection;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabs;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPBdr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFramePr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrBase;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPPrBaseImpl extends XmlComplexContentImpl implements CTPPrBase
{
    private static final long serialVersionUID = 1L;
    private static final QName PSTYLE$0;
    private static final QName KEEPNEXT$2;
    private static final QName KEEPLINES$4;
    private static final QName PAGEBREAKBEFORE$6;
    private static final QName FRAMEPR$8;
    private static final QName WIDOWCONTROL$10;
    private static final QName NUMPR$12;
    private static final QName SUPPRESSLINENUMBERS$14;
    private static final QName PBDR$16;
    private static final QName SHD$18;
    private static final QName TABS$20;
    private static final QName SUPPRESSAUTOHYPHENS$22;
    private static final QName KINSOKU$24;
    private static final QName WORDWRAP$26;
    private static final QName OVERFLOWPUNCT$28;
    private static final QName TOPLINEPUNCT$30;
    private static final QName AUTOSPACEDE$32;
    private static final QName AUTOSPACEDN$34;
    private static final QName BIDI$36;
    private static final QName ADJUSTRIGHTIND$38;
    private static final QName SNAPTOGRID$40;
    private static final QName SPACING$42;
    private static final QName IND$44;
    private static final QName CONTEXTUALSPACING$46;
    private static final QName MIRRORINDENTS$48;
    private static final QName SUPPRESSOVERLAP$50;
    private static final QName JC$52;
    private static final QName TEXTDIRECTION$54;
    private static final QName TEXTALIGNMENT$56;
    private static final QName TEXTBOXTIGHTWRAP$58;
    private static final QName OUTLINELVL$60;
    private static final QName DIVID$62;
    private static final QName CNFSTYLE$64;
    
    public CTPPrBaseImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTString getPStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTPPrBaseImpl.PSTYLE$0, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetPStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.PSTYLE$0) != 0;
        }
    }
    
    public void setPStyle(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTPPrBaseImpl.PSTYLE$0, 0, (short)1);
    }
    
    public CTString addNewPStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTPPrBaseImpl.PSTYLE$0);
        }
    }
    
    public void unsetPStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.PSTYLE$0, 0);
        }
    }
    
    public CTOnOff getKeepNext() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.KEEPNEXT$2, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetKeepNext() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.KEEPNEXT$2) != 0;
        }
    }
    
    public void setKeepNext(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.KEEPNEXT$2, 0, (short)1);
    }
    
    public CTOnOff addNewKeepNext() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.KEEPNEXT$2);
        }
    }
    
    public void unsetKeepNext() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.KEEPNEXT$2, 0);
        }
    }
    
    public CTOnOff getKeepLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.KEEPLINES$4, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetKeepLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.KEEPLINES$4) != 0;
        }
    }
    
    public void setKeepLines(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.KEEPLINES$4, 0, (short)1);
    }
    
    public CTOnOff addNewKeepLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.KEEPLINES$4);
        }
    }
    
    public void unsetKeepLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.KEEPLINES$4, 0);
        }
    }
    
    public CTOnOff getPageBreakBefore() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.PAGEBREAKBEFORE$6, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetPageBreakBefore() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.PAGEBREAKBEFORE$6) != 0;
        }
    }
    
    public void setPageBreakBefore(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.PAGEBREAKBEFORE$6, 0, (short)1);
    }
    
    public CTOnOff addNewPageBreakBefore() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.PAGEBREAKBEFORE$6);
        }
    }
    
    public void unsetPageBreakBefore() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.PAGEBREAKBEFORE$6, 0);
        }
    }
    
    public CTFramePr getFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFramePr ctFramePr = (CTFramePr)this.get_store().find_element_user(CTPPrBaseImpl.FRAMEPR$8, 0);
            if (ctFramePr == null) {
                return null;
            }
            return ctFramePr;
        }
    }
    
    public boolean isSetFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.FRAMEPR$8) != 0;
        }
    }
    
    public void setFramePr(final CTFramePr ctFramePr) {
        this.generatedSetterHelperImpl((XmlObject)ctFramePr, CTPPrBaseImpl.FRAMEPR$8, 0, (short)1);
    }
    
    public CTFramePr addNewFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFramePr)this.get_store().add_element_user(CTPPrBaseImpl.FRAMEPR$8);
        }
    }
    
    public void unsetFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.FRAMEPR$8, 0);
        }
    }
    
    public CTOnOff getWidowControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.WIDOWCONTROL$10, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetWidowControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.WIDOWCONTROL$10) != 0;
        }
    }
    
    public void setWidowControl(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.WIDOWCONTROL$10, 0, (short)1);
    }
    
    public CTOnOff addNewWidowControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.WIDOWCONTROL$10);
        }
    }
    
    public void unsetWidowControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.WIDOWCONTROL$10, 0);
        }
    }
    
    public CTNumPr getNumPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumPr ctNumPr = (CTNumPr)this.get_store().find_element_user(CTPPrBaseImpl.NUMPR$12, 0);
            if (ctNumPr == null) {
                return null;
            }
            return ctNumPr;
        }
    }
    
    public boolean isSetNumPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.NUMPR$12) != 0;
        }
    }
    
    public void setNumPr(final CTNumPr ctNumPr) {
        this.generatedSetterHelperImpl((XmlObject)ctNumPr, CTPPrBaseImpl.NUMPR$12, 0, (short)1);
    }
    
    public CTNumPr addNewNumPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumPr)this.get_store().add_element_user(CTPPrBaseImpl.NUMPR$12);
        }
    }
    
    public void unsetNumPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.NUMPR$12, 0);
        }
    }
    
    public CTOnOff getSuppressLineNumbers() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.SUPPRESSLINENUMBERS$14, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSuppressLineNumbers() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.SUPPRESSLINENUMBERS$14) != 0;
        }
    }
    
    public void setSuppressLineNumbers(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.SUPPRESSLINENUMBERS$14, 0, (short)1);
    }
    
    public CTOnOff addNewSuppressLineNumbers() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.SUPPRESSLINENUMBERS$14);
        }
    }
    
    public void unsetSuppressLineNumbers() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.SUPPRESSLINENUMBERS$14, 0);
        }
    }
    
    public CTPBdr getPBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPBdr ctpBdr = (CTPBdr)this.get_store().find_element_user(CTPPrBaseImpl.PBDR$16, 0);
            if (ctpBdr == null) {
                return null;
            }
            return ctpBdr;
        }
    }
    
    public boolean isSetPBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.PBDR$16) != 0;
        }
    }
    
    public void setPBdr(final CTPBdr ctpBdr) {
        this.generatedSetterHelperImpl((XmlObject)ctpBdr, CTPPrBaseImpl.PBDR$16, 0, (short)1);
    }
    
    public CTPBdr addNewPBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPBdr)this.get_store().add_element_user(CTPPrBaseImpl.PBDR$16);
        }
    }
    
    public void unsetPBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.PBDR$16, 0);
        }
    }
    
    public CTShd getShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShd ctShd = (CTShd)this.get_store().find_element_user(CTPPrBaseImpl.SHD$18, 0);
            if (ctShd == null) {
                return null;
            }
            return ctShd;
        }
    }
    
    public boolean isSetShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.SHD$18) != 0;
        }
    }
    
    public void setShd(final CTShd ctShd) {
        this.generatedSetterHelperImpl((XmlObject)ctShd, CTPPrBaseImpl.SHD$18, 0, (short)1);
    }
    
    public CTShd addNewShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShd)this.get_store().add_element_user(CTPPrBaseImpl.SHD$18);
        }
    }
    
    public void unsetShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.SHD$18, 0);
        }
    }
    
    public CTTabs getTabs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTabs ctTabs = (CTTabs)this.get_store().find_element_user(CTPPrBaseImpl.TABS$20, 0);
            if (ctTabs == null) {
                return null;
            }
            return ctTabs;
        }
    }
    
    public boolean isSetTabs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.TABS$20) != 0;
        }
    }
    
    public void setTabs(final CTTabs ctTabs) {
        this.generatedSetterHelperImpl((XmlObject)ctTabs, CTPPrBaseImpl.TABS$20, 0, (short)1);
    }
    
    public CTTabs addNewTabs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTabs)this.get_store().add_element_user(CTPPrBaseImpl.TABS$20);
        }
    }
    
    public void unsetTabs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.TABS$20, 0);
        }
    }
    
    public CTOnOff getSuppressAutoHyphens() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.SUPPRESSAUTOHYPHENS$22, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSuppressAutoHyphens() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.SUPPRESSAUTOHYPHENS$22) != 0;
        }
    }
    
    public void setSuppressAutoHyphens(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.SUPPRESSAUTOHYPHENS$22, 0, (short)1);
    }
    
    public CTOnOff addNewSuppressAutoHyphens() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.SUPPRESSAUTOHYPHENS$22);
        }
    }
    
    public void unsetSuppressAutoHyphens() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.SUPPRESSAUTOHYPHENS$22, 0);
        }
    }
    
    public CTOnOff getKinsoku() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.KINSOKU$24, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetKinsoku() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.KINSOKU$24) != 0;
        }
    }
    
    public void setKinsoku(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.KINSOKU$24, 0, (short)1);
    }
    
    public CTOnOff addNewKinsoku() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.KINSOKU$24);
        }
    }
    
    public void unsetKinsoku() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.KINSOKU$24, 0);
        }
    }
    
    public CTOnOff getWordWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.WORDWRAP$26, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetWordWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.WORDWRAP$26) != 0;
        }
    }
    
    public void setWordWrap(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.WORDWRAP$26, 0, (short)1);
    }
    
    public CTOnOff addNewWordWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.WORDWRAP$26);
        }
    }
    
    public void unsetWordWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.WORDWRAP$26, 0);
        }
    }
    
    public CTOnOff getOverflowPunct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.OVERFLOWPUNCT$28, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetOverflowPunct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.OVERFLOWPUNCT$28) != 0;
        }
    }
    
    public void setOverflowPunct(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.OVERFLOWPUNCT$28, 0, (short)1);
    }
    
    public CTOnOff addNewOverflowPunct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.OVERFLOWPUNCT$28);
        }
    }
    
    public void unsetOverflowPunct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.OVERFLOWPUNCT$28, 0);
        }
    }
    
    public CTOnOff getTopLinePunct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.TOPLINEPUNCT$30, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetTopLinePunct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.TOPLINEPUNCT$30) != 0;
        }
    }
    
    public void setTopLinePunct(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.TOPLINEPUNCT$30, 0, (short)1);
    }
    
    public CTOnOff addNewTopLinePunct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.TOPLINEPUNCT$30);
        }
    }
    
    public void unsetTopLinePunct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.TOPLINEPUNCT$30, 0);
        }
    }
    
    public CTOnOff getAutoSpaceDE() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.AUTOSPACEDE$32, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetAutoSpaceDE() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.AUTOSPACEDE$32) != 0;
        }
    }
    
    public void setAutoSpaceDE(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.AUTOSPACEDE$32, 0, (short)1);
    }
    
    public CTOnOff addNewAutoSpaceDE() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.AUTOSPACEDE$32);
        }
    }
    
    public void unsetAutoSpaceDE() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.AUTOSPACEDE$32, 0);
        }
    }
    
    public CTOnOff getAutoSpaceDN() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.AUTOSPACEDN$34, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetAutoSpaceDN() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.AUTOSPACEDN$34) != 0;
        }
    }
    
    public void setAutoSpaceDN(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.AUTOSPACEDN$34, 0, (short)1);
    }
    
    public CTOnOff addNewAutoSpaceDN() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.AUTOSPACEDN$34);
        }
    }
    
    public void unsetAutoSpaceDN() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.AUTOSPACEDN$34, 0);
        }
    }
    
    public CTOnOff getBidi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.BIDI$36, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetBidi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.BIDI$36) != 0;
        }
    }
    
    public void setBidi(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.BIDI$36, 0, (short)1);
    }
    
    public CTOnOff addNewBidi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.BIDI$36);
        }
    }
    
    public void unsetBidi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.BIDI$36, 0);
        }
    }
    
    public CTOnOff getAdjustRightInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.ADJUSTRIGHTIND$38, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetAdjustRightInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.ADJUSTRIGHTIND$38) != 0;
        }
    }
    
    public void setAdjustRightInd(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.ADJUSTRIGHTIND$38, 0, (short)1);
    }
    
    public CTOnOff addNewAdjustRightInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.ADJUSTRIGHTIND$38);
        }
    }
    
    public void unsetAdjustRightInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.ADJUSTRIGHTIND$38, 0);
        }
    }
    
    public CTOnOff getSnapToGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.SNAPTOGRID$40, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSnapToGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.SNAPTOGRID$40) != 0;
        }
    }
    
    public void setSnapToGrid(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.SNAPTOGRID$40, 0, (short)1);
    }
    
    public CTOnOff addNewSnapToGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.SNAPTOGRID$40);
        }
    }
    
    public void unsetSnapToGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.SNAPTOGRID$40, 0);
        }
    }
    
    public CTSpacing getSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSpacing ctSpacing = (CTSpacing)this.get_store().find_element_user(CTPPrBaseImpl.SPACING$42, 0);
            if (ctSpacing == null) {
                return null;
            }
            return ctSpacing;
        }
    }
    
    public boolean isSetSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.SPACING$42) != 0;
        }
    }
    
    public void setSpacing(final CTSpacing ctSpacing) {
        this.generatedSetterHelperImpl((XmlObject)ctSpacing, CTPPrBaseImpl.SPACING$42, 0, (short)1);
    }
    
    public CTSpacing addNewSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSpacing)this.get_store().add_element_user(CTPPrBaseImpl.SPACING$42);
        }
    }
    
    public void unsetSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.SPACING$42, 0);
        }
    }
    
    public CTInd getInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTInd ctInd = (CTInd)this.get_store().find_element_user(CTPPrBaseImpl.IND$44, 0);
            if (ctInd == null) {
                return null;
            }
            return ctInd;
        }
    }
    
    public boolean isSetInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.IND$44) != 0;
        }
    }
    
    public void setInd(final CTInd ctInd) {
        this.generatedSetterHelperImpl((XmlObject)ctInd, CTPPrBaseImpl.IND$44, 0, (short)1);
    }
    
    public CTInd addNewInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTInd)this.get_store().add_element_user(CTPPrBaseImpl.IND$44);
        }
    }
    
    public void unsetInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.IND$44, 0);
        }
    }
    
    public CTOnOff getContextualSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.CONTEXTUALSPACING$46, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetContextualSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.CONTEXTUALSPACING$46) != 0;
        }
    }
    
    public void setContextualSpacing(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.CONTEXTUALSPACING$46, 0, (short)1);
    }
    
    public CTOnOff addNewContextualSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.CONTEXTUALSPACING$46);
        }
    }
    
    public void unsetContextualSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.CONTEXTUALSPACING$46, 0);
        }
    }
    
    public CTOnOff getMirrorIndents() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.MIRRORINDENTS$48, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetMirrorIndents() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.MIRRORINDENTS$48) != 0;
        }
    }
    
    public void setMirrorIndents(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.MIRRORINDENTS$48, 0, (short)1);
    }
    
    public CTOnOff addNewMirrorIndents() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.MIRRORINDENTS$48);
        }
    }
    
    public void unsetMirrorIndents() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.MIRRORINDENTS$48, 0);
        }
    }
    
    public CTOnOff getSuppressOverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTPPrBaseImpl.SUPPRESSOVERLAP$50, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSuppressOverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.SUPPRESSOVERLAP$50) != 0;
        }
    }
    
    public void setSuppressOverlap(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTPPrBaseImpl.SUPPRESSOVERLAP$50, 0, (short)1);
    }
    
    public CTOnOff addNewSuppressOverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTPPrBaseImpl.SUPPRESSOVERLAP$50);
        }
    }
    
    public void unsetSuppressOverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.SUPPRESSOVERLAP$50, 0);
        }
    }
    
    public CTJc getJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTJc ctJc = (CTJc)this.get_store().find_element_user(CTPPrBaseImpl.JC$52, 0);
            if (ctJc == null) {
                return null;
            }
            return ctJc;
        }
    }
    
    public boolean isSetJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.JC$52) != 0;
        }
    }
    
    public void setJc(final CTJc ctJc) {
        this.generatedSetterHelperImpl((XmlObject)ctJc, CTPPrBaseImpl.JC$52, 0, (short)1);
    }
    
    public CTJc addNewJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTJc)this.get_store().add_element_user(CTPPrBaseImpl.JC$52);
        }
    }
    
    public void unsetJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.JC$52, 0);
        }
    }
    
    public CTTextDirection getTextDirection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextDirection ctTextDirection = (CTTextDirection)this.get_store().find_element_user(CTPPrBaseImpl.TEXTDIRECTION$54, 0);
            if (ctTextDirection == null) {
                return null;
            }
            return ctTextDirection;
        }
    }
    
    public boolean isSetTextDirection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.TEXTDIRECTION$54) != 0;
        }
    }
    
    public void setTextDirection(final CTTextDirection ctTextDirection) {
        this.generatedSetterHelperImpl((XmlObject)ctTextDirection, CTPPrBaseImpl.TEXTDIRECTION$54, 0, (short)1);
    }
    
    public CTTextDirection addNewTextDirection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextDirection)this.get_store().add_element_user(CTPPrBaseImpl.TEXTDIRECTION$54);
        }
    }
    
    public void unsetTextDirection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.TEXTDIRECTION$54, 0);
        }
    }
    
    public CTTextAlignment getTextAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextAlignment ctTextAlignment = (CTTextAlignment)this.get_store().find_element_user(CTPPrBaseImpl.TEXTALIGNMENT$56, 0);
            if (ctTextAlignment == null) {
                return null;
            }
            return ctTextAlignment;
        }
    }
    
    public boolean isSetTextAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.TEXTALIGNMENT$56) != 0;
        }
    }
    
    public void setTextAlignment(final CTTextAlignment ctTextAlignment) {
        this.generatedSetterHelperImpl((XmlObject)ctTextAlignment, CTPPrBaseImpl.TEXTALIGNMENT$56, 0, (short)1);
    }
    
    public CTTextAlignment addNewTextAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextAlignment)this.get_store().add_element_user(CTPPrBaseImpl.TEXTALIGNMENT$56);
        }
    }
    
    public void unsetTextAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.TEXTALIGNMENT$56, 0);
        }
    }
    
    public CTTextboxTightWrap getTextboxTightWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextboxTightWrap ctTextboxTightWrap = (CTTextboxTightWrap)this.get_store().find_element_user(CTPPrBaseImpl.TEXTBOXTIGHTWRAP$58, 0);
            if (ctTextboxTightWrap == null) {
                return null;
            }
            return ctTextboxTightWrap;
        }
    }
    
    public boolean isSetTextboxTightWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.TEXTBOXTIGHTWRAP$58) != 0;
        }
    }
    
    public void setTextboxTightWrap(final CTTextboxTightWrap ctTextboxTightWrap) {
        this.generatedSetterHelperImpl((XmlObject)ctTextboxTightWrap, CTPPrBaseImpl.TEXTBOXTIGHTWRAP$58, 0, (short)1);
    }
    
    public CTTextboxTightWrap addNewTextboxTightWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextboxTightWrap)this.get_store().add_element_user(CTPPrBaseImpl.TEXTBOXTIGHTWRAP$58);
        }
    }
    
    public void unsetTextboxTightWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.TEXTBOXTIGHTWRAP$58, 0);
        }
    }
    
    public CTDecimalNumber getOutlineLvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTPPrBaseImpl.OUTLINELVL$60, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetOutlineLvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.OUTLINELVL$60) != 0;
        }
    }
    
    public void setOutlineLvl(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTPPrBaseImpl.OUTLINELVL$60, 0, (short)1);
    }
    
    public CTDecimalNumber addNewOutlineLvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTPPrBaseImpl.OUTLINELVL$60);
        }
    }
    
    public void unsetOutlineLvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.OUTLINELVL$60, 0);
        }
    }
    
    public CTDecimalNumber getDivId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTPPrBaseImpl.DIVID$62, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetDivId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.DIVID$62) != 0;
        }
    }
    
    public void setDivId(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTPPrBaseImpl.DIVID$62, 0, (short)1);
    }
    
    public CTDecimalNumber addNewDivId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTPPrBaseImpl.DIVID$62);
        }
    }
    
    public void unsetDivId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.DIVID$62, 0);
        }
    }
    
    public CTCnf getCnfStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCnf ctCnf = (CTCnf)this.get_store().find_element_user(CTPPrBaseImpl.CNFSTYLE$64, 0);
            if (ctCnf == null) {
                return null;
            }
            return ctCnf;
        }
    }
    
    public boolean isSetCnfStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrBaseImpl.CNFSTYLE$64) != 0;
        }
    }
    
    public void setCnfStyle(final CTCnf ctCnf) {
        this.generatedSetterHelperImpl((XmlObject)ctCnf, CTPPrBaseImpl.CNFSTYLE$64, 0, (short)1);
    }
    
    public CTCnf addNewCnfStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCnf)this.get_store().add_element_user(CTPPrBaseImpl.CNFSTYLE$64);
        }
    }
    
    public void unsetCnfStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrBaseImpl.CNFSTYLE$64, 0);
        }
    }
    
    static {
        PSTYLE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pStyle");
        KEEPNEXT$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "keepNext");
        KEEPLINES$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "keepLines");
        PAGEBREAKBEFORE$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pageBreakBefore");
        FRAMEPR$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "framePr");
        WIDOWCONTROL$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "widowControl");
        NUMPR$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "numPr");
        SUPPRESSLINENUMBERS$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "suppressLineNumbers");
        PBDR$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pBdr");
        SHD$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "shd");
        TABS$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tabs");
        SUPPRESSAUTOHYPHENS$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "suppressAutoHyphens");
        KINSOKU$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "kinsoku");
        WORDWRAP$26 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "wordWrap");
        OVERFLOWPUNCT$28 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "overflowPunct");
        TOPLINEPUNCT$30 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "topLinePunct");
        AUTOSPACEDE$32 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "autoSpaceDE");
        AUTOSPACEDN$34 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "autoSpaceDN");
        BIDI$36 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bidi");
        ADJUSTRIGHTIND$38 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "adjustRightInd");
        SNAPTOGRID$40 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "snapToGrid");
        SPACING$42 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "spacing");
        IND$44 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ind");
        CONTEXTUALSPACING$46 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "contextualSpacing");
        MIRRORINDENTS$48 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "mirrorIndents");
        SUPPRESSOVERLAP$50 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "suppressOverlap");
        JC$52 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "jc");
        TEXTDIRECTION$54 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "textDirection");
        TEXTALIGNMENT$56 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "textAlignment");
        TEXTBOXTIGHTWRAP$58 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "textboxTightWrap");
        OUTLINELVL$60 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "outlineLvl");
        DIVID$62 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "divId");
        CNFSTYLE$64 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cnfStyle");
    }
}
