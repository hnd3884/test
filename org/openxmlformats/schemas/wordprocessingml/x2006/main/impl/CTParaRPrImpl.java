package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTParaRPrChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEastAsianLayout;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLanguage;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEm;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalAlignRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFitText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextEffect;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTUnderline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHighlight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextScale;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChange;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTParaRPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTParaRPrImpl extends XmlComplexContentImpl implements CTParaRPr
{
    private static final long serialVersionUID = 1L;
    private static final QName INS$0;
    private static final QName DEL$2;
    private static final QName MOVEFROM$4;
    private static final QName MOVETO$6;
    private static final QName RSTYLE$8;
    private static final QName RFONTS$10;
    private static final QName B$12;
    private static final QName BCS$14;
    private static final QName I$16;
    private static final QName ICS$18;
    private static final QName CAPS$20;
    private static final QName SMALLCAPS$22;
    private static final QName STRIKE$24;
    private static final QName DSTRIKE$26;
    private static final QName OUTLINE$28;
    private static final QName SHADOW$30;
    private static final QName EMBOSS$32;
    private static final QName IMPRINT$34;
    private static final QName NOPROOF$36;
    private static final QName SNAPTOGRID$38;
    private static final QName VANISH$40;
    private static final QName WEBHIDDEN$42;
    private static final QName COLOR$44;
    private static final QName SPACING$46;
    private static final QName W$48;
    private static final QName KERN$50;
    private static final QName POSITION$52;
    private static final QName SZ$54;
    private static final QName SZCS$56;
    private static final QName HIGHLIGHT$58;
    private static final QName U$60;
    private static final QName EFFECT$62;
    private static final QName BDR$64;
    private static final QName SHD$66;
    private static final QName FITTEXT$68;
    private static final QName VERTALIGN$70;
    private static final QName RTL$72;
    private static final QName CS$74;
    private static final QName EM$76;
    private static final QName LANG$78;
    private static final QName EASTASIANLAYOUT$80;
    private static final QName SPECVANISH$82;
    private static final QName OMATH$84;
    private static final QName RPRCHANGE$86;
    
    public CTParaRPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTrackChange getIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTParaRPrImpl.INS$0, 0);
            if (ctTrackChange == null) {
                return null;
            }
            return ctTrackChange;
        }
    }
    
    public boolean isSetIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.INS$0) != 0;
        }
    }
    
    public void setIns(final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTParaRPrImpl.INS$0, 0, (short)1);
    }
    
    public CTTrackChange addNewIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTParaRPrImpl.INS$0);
        }
    }
    
    public void unsetIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.INS$0, 0);
        }
    }
    
    public CTTrackChange getDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTParaRPrImpl.DEL$2, 0);
            if (ctTrackChange == null) {
                return null;
            }
            return ctTrackChange;
        }
    }
    
    public boolean isSetDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.DEL$2) != 0;
        }
    }
    
    public void setDel(final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTParaRPrImpl.DEL$2, 0, (short)1);
    }
    
    public CTTrackChange addNewDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTParaRPrImpl.DEL$2);
        }
    }
    
    public void unsetDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.DEL$2, 0);
        }
    }
    
    public CTTrackChange getMoveFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTParaRPrImpl.MOVEFROM$4, 0);
            if (ctTrackChange == null) {
                return null;
            }
            return ctTrackChange;
        }
    }
    
    public boolean isSetMoveFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.MOVEFROM$4) != 0;
        }
    }
    
    public void setMoveFrom(final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTParaRPrImpl.MOVEFROM$4, 0, (short)1);
    }
    
    public CTTrackChange addNewMoveFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTParaRPrImpl.MOVEFROM$4);
        }
    }
    
    public void unsetMoveFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.MOVEFROM$4, 0);
        }
    }
    
    public CTTrackChange getMoveTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTParaRPrImpl.MOVETO$6, 0);
            if (ctTrackChange == null) {
                return null;
            }
            return ctTrackChange;
        }
    }
    
    public boolean isSetMoveTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.MOVETO$6) != 0;
        }
    }
    
    public void setMoveTo(final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTParaRPrImpl.MOVETO$6, 0, (short)1);
    }
    
    public CTTrackChange addNewMoveTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTParaRPrImpl.MOVETO$6);
        }
    }
    
    public void unsetMoveTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.MOVETO$6, 0);
        }
    }
    
    public CTString getRStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTParaRPrImpl.RSTYLE$8, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetRStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.RSTYLE$8) != 0;
        }
    }
    
    public void setRStyle(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTParaRPrImpl.RSTYLE$8, 0, (short)1);
    }
    
    public CTString addNewRStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTParaRPrImpl.RSTYLE$8);
        }
    }
    
    public void unsetRStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.RSTYLE$8, 0);
        }
    }
    
    public CTFonts getRFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFonts ctFonts = (CTFonts)this.get_store().find_element_user(CTParaRPrImpl.RFONTS$10, 0);
            if (ctFonts == null) {
                return null;
            }
            return ctFonts;
        }
    }
    
    public boolean isSetRFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.RFONTS$10) != 0;
        }
    }
    
    public void setRFonts(final CTFonts ctFonts) {
        this.generatedSetterHelperImpl((XmlObject)ctFonts, CTParaRPrImpl.RFONTS$10, 0, (short)1);
    }
    
    public CTFonts addNewRFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFonts)this.get_store().add_element_user(CTParaRPrImpl.RFONTS$10);
        }
    }
    
    public void unsetRFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.RFONTS$10, 0);
        }
    }
    
    public CTOnOff getB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.B$12, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.B$12) != 0;
        }
    }
    
    public void setB(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.B$12, 0, (short)1);
    }
    
    public CTOnOff addNewB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.B$12);
        }
    }
    
    public void unsetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.B$12, 0);
        }
    }
    
    public CTOnOff getBCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.BCS$14, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetBCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.BCS$14) != 0;
        }
    }
    
    public void setBCs(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.BCS$14, 0, (short)1);
    }
    
    public CTOnOff addNewBCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.BCS$14);
        }
    }
    
    public void unsetBCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.BCS$14, 0);
        }
    }
    
    public CTOnOff getI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.I$16, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.I$16) != 0;
        }
    }
    
    public void setI(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.I$16, 0, (short)1);
    }
    
    public CTOnOff addNewI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.I$16);
        }
    }
    
    public void unsetI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.I$16, 0);
        }
    }
    
    public CTOnOff getICs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.ICS$18, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetICs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.ICS$18) != 0;
        }
    }
    
    public void setICs(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.ICS$18, 0, (short)1);
    }
    
    public CTOnOff addNewICs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.ICS$18);
        }
    }
    
    public void unsetICs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.ICS$18, 0);
        }
    }
    
    public CTOnOff getCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.CAPS$20, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.CAPS$20) != 0;
        }
    }
    
    public void setCaps(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.CAPS$20, 0, (short)1);
    }
    
    public CTOnOff addNewCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.CAPS$20);
        }
    }
    
    public void unsetCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.CAPS$20, 0);
        }
    }
    
    public CTOnOff getSmallCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.SMALLCAPS$22, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSmallCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.SMALLCAPS$22) != 0;
        }
    }
    
    public void setSmallCaps(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.SMALLCAPS$22, 0, (short)1);
    }
    
    public CTOnOff addNewSmallCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.SMALLCAPS$22);
        }
    }
    
    public void unsetSmallCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.SMALLCAPS$22, 0);
        }
    }
    
    public CTOnOff getStrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.STRIKE$24, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetStrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.STRIKE$24) != 0;
        }
    }
    
    public void setStrike(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.STRIKE$24, 0, (short)1);
    }
    
    public CTOnOff addNewStrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.STRIKE$24);
        }
    }
    
    public void unsetStrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.STRIKE$24, 0);
        }
    }
    
    public CTOnOff getDstrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.DSTRIKE$26, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDstrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.DSTRIKE$26) != 0;
        }
    }
    
    public void setDstrike(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.DSTRIKE$26, 0, (short)1);
    }
    
    public CTOnOff addNewDstrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.DSTRIKE$26);
        }
    }
    
    public void unsetDstrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.DSTRIKE$26, 0);
        }
    }
    
    public CTOnOff getOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.OUTLINE$28, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.OUTLINE$28) != 0;
        }
    }
    
    public void setOutline(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.OUTLINE$28, 0, (short)1);
    }
    
    public CTOnOff addNewOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.OUTLINE$28);
        }
    }
    
    public void unsetOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.OUTLINE$28, 0);
        }
    }
    
    public CTOnOff getShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.SHADOW$30, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.SHADOW$30) != 0;
        }
    }
    
    public void setShadow(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.SHADOW$30, 0, (short)1);
    }
    
    public CTOnOff addNewShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.SHADOW$30);
        }
    }
    
    public void unsetShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.SHADOW$30, 0);
        }
    }
    
    public CTOnOff getEmboss() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.EMBOSS$32, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetEmboss() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.EMBOSS$32) != 0;
        }
    }
    
    public void setEmboss(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.EMBOSS$32, 0, (short)1);
    }
    
    public CTOnOff addNewEmboss() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.EMBOSS$32);
        }
    }
    
    public void unsetEmboss() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.EMBOSS$32, 0);
        }
    }
    
    public CTOnOff getImprint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.IMPRINT$34, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetImprint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.IMPRINT$34) != 0;
        }
    }
    
    public void setImprint(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.IMPRINT$34, 0, (short)1);
    }
    
    public CTOnOff addNewImprint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.IMPRINT$34);
        }
    }
    
    public void unsetImprint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.IMPRINT$34, 0);
        }
    }
    
    public CTOnOff getNoProof() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.NOPROOF$36, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetNoProof() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.NOPROOF$36) != 0;
        }
    }
    
    public void setNoProof(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.NOPROOF$36, 0, (short)1);
    }
    
    public CTOnOff addNewNoProof() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.NOPROOF$36);
        }
    }
    
    public void unsetNoProof() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.NOPROOF$36, 0);
        }
    }
    
    public CTOnOff getSnapToGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.SNAPTOGRID$38, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSnapToGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.SNAPTOGRID$38) != 0;
        }
    }
    
    public void setSnapToGrid(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.SNAPTOGRID$38, 0, (short)1);
    }
    
    public CTOnOff addNewSnapToGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.SNAPTOGRID$38);
        }
    }
    
    public void unsetSnapToGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.SNAPTOGRID$38, 0);
        }
    }
    
    public CTOnOff getVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.VANISH$40, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.VANISH$40) != 0;
        }
    }
    
    public void setVanish(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.VANISH$40, 0, (short)1);
    }
    
    public CTOnOff addNewVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.VANISH$40);
        }
    }
    
    public void unsetVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.VANISH$40, 0);
        }
    }
    
    public CTOnOff getWebHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.WEBHIDDEN$42, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetWebHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.WEBHIDDEN$42) != 0;
        }
    }
    
    public void setWebHidden(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.WEBHIDDEN$42, 0, (short)1);
    }
    
    public CTOnOff addNewWebHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.WEBHIDDEN$42);
        }
    }
    
    public void unsetWebHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.WEBHIDDEN$42, 0);
        }
    }
    
    public CTColor getColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTParaRPrImpl.COLOR$44, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public boolean isSetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.COLOR$44) != 0;
        }
    }
    
    public void setColor(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTParaRPrImpl.COLOR$44, 0, (short)1);
    }
    
    public CTColor addNewColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTParaRPrImpl.COLOR$44);
        }
    }
    
    public void unsetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.COLOR$44, 0);
        }
    }
    
    public CTSignedTwipsMeasure getSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSignedTwipsMeasure ctSignedTwipsMeasure = (CTSignedTwipsMeasure)this.get_store().find_element_user(CTParaRPrImpl.SPACING$46, 0);
            if (ctSignedTwipsMeasure == null) {
                return null;
            }
            return ctSignedTwipsMeasure;
        }
    }
    
    public boolean isSetSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.SPACING$46) != 0;
        }
    }
    
    public void setSpacing(final CTSignedTwipsMeasure ctSignedTwipsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctSignedTwipsMeasure, CTParaRPrImpl.SPACING$46, 0, (short)1);
    }
    
    public CTSignedTwipsMeasure addNewSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSignedTwipsMeasure)this.get_store().add_element_user(CTParaRPrImpl.SPACING$46);
        }
    }
    
    public void unsetSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.SPACING$46, 0);
        }
    }
    
    public CTTextScale getW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextScale ctTextScale = (CTTextScale)this.get_store().find_element_user(CTParaRPrImpl.W$48, 0);
            if (ctTextScale == null) {
                return null;
            }
            return ctTextScale;
        }
    }
    
    public boolean isSetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.W$48) != 0;
        }
    }
    
    public void setW(final CTTextScale ctTextScale) {
        this.generatedSetterHelperImpl((XmlObject)ctTextScale, CTParaRPrImpl.W$48, 0, (short)1);
    }
    
    public CTTextScale addNewW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextScale)this.get_store().add_element_user(CTParaRPrImpl.W$48);
        }
    }
    
    public void unsetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.W$48, 0);
        }
    }
    
    public CTHpsMeasure getKern() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHpsMeasure ctHpsMeasure = (CTHpsMeasure)this.get_store().find_element_user(CTParaRPrImpl.KERN$50, 0);
            if (ctHpsMeasure == null) {
                return null;
            }
            return ctHpsMeasure;
        }
    }
    
    public boolean isSetKern() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.KERN$50) != 0;
        }
    }
    
    public void setKern(final CTHpsMeasure ctHpsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctHpsMeasure, CTParaRPrImpl.KERN$50, 0, (short)1);
    }
    
    public CTHpsMeasure addNewKern() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHpsMeasure)this.get_store().add_element_user(CTParaRPrImpl.KERN$50);
        }
    }
    
    public void unsetKern() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.KERN$50, 0);
        }
    }
    
    public CTSignedHpsMeasure getPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSignedHpsMeasure ctSignedHpsMeasure = (CTSignedHpsMeasure)this.get_store().find_element_user(CTParaRPrImpl.POSITION$52, 0);
            if (ctSignedHpsMeasure == null) {
                return null;
            }
            return ctSignedHpsMeasure;
        }
    }
    
    public boolean isSetPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.POSITION$52) != 0;
        }
    }
    
    public void setPosition(final CTSignedHpsMeasure ctSignedHpsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctSignedHpsMeasure, CTParaRPrImpl.POSITION$52, 0, (short)1);
    }
    
    public CTSignedHpsMeasure addNewPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSignedHpsMeasure)this.get_store().add_element_user(CTParaRPrImpl.POSITION$52);
        }
    }
    
    public void unsetPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.POSITION$52, 0);
        }
    }
    
    public CTHpsMeasure getSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHpsMeasure ctHpsMeasure = (CTHpsMeasure)this.get_store().find_element_user(CTParaRPrImpl.SZ$54, 0);
            if (ctHpsMeasure == null) {
                return null;
            }
            return ctHpsMeasure;
        }
    }
    
    public boolean isSetSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.SZ$54) != 0;
        }
    }
    
    public void setSz(final CTHpsMeasure ctHpsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctHpsMeasure, CTParaRPrImpl.SZ$54, 0, (short)1);
    }
    
    public CTHpsMeasure addNewSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHpsMeasure)this.get_store().add_element_user(CTParaRPrImpl.SZ$54);
        }
    }
    
    public void unsetSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.SZ$54, 0);
        }
    }
    
    public CTHpsMeasure getSzCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHpsMeasure ctHpsMeasure = (CTHpsMeasure)this.get_store().find_element_user(CTParaRPrImpl.SZCS$56, 0);
            if (ctHpsMeasure == null) {
                return null;
            }
            return ctHpsMeasure;
        }
    }
    
    public boolean isSetSzCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.SZCS$56) != 0;
        }
    }
    
    public void setSzCs(final CTHpsMeasure ctHpsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctHpsMeasure, CTParaRPrImpl.SZCS$56, 0, (short)1);
    }
    
    public CTHpsMeasure addNewSzCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHpsMeasure)this.get_store().add_element_user(CTParaRPrImpl.SZCS$56);
        }
    }
    
    public void unsetSzCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.SZCS$56, 0);
        }
    }
    
    public CTHighlight getHighlight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHighlight ctHighlight = (CTHighlight)this.get_store().find_element_user(CTParaRPrImpl.HIGHLIGHT$58, 0);
            if (ctHighlight == null) {
                return null;
            }
            return ctHighlight;
        }
    }
    
    public boolean isSetHighlight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.HIGHLIGHT$58) != 0;
        }
    }
    
    public void setHighlight(final CTHighlight ctHighlight) {
        this.generatedSetterHelperImpl((XmlObject)ctHighlight, CTParaRPrImpl.HIGHLIGHT$58, 0, (short)1);
    }
    
    public CTHighlight addNewHighlight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHighlight)this.get_store().add_element_user(CTParaRPrImpl.HIGHLIGHT$58);
        }
    }
    
    public void unsetHighlight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.HIGHLIGHT$58, 0);
        }
    }
    
    public CTUnderline getU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnderline ctUnderline = (CTUnderline)this.get_store().find_element_user(CTParaRPrImpl.U$60, 0);
            if (ctUnderline == null) {
                return null;
            }
            return ctUnderline;
        }
    }
    
    public boolean isSetU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.U$60) != 0;
        }
    }
    
    public void setU(final CTUnderline ctUnderline) {
        this.generatedSetterHelperImpl((XmlObject)ctUnderline, CTParaRPrImpl.U$60, 0, (short)1);
    }
    
    public CTUnderline addNewU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnderline)this.get_store().add_element_user(CTParaRPrImpl.U$60);
        }
    }
    
    public void unsetU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.U$60, 0);
        }
    }
    
    public CTTextEffect getEffect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextEffect ctTextEffect = (CTTextEffect)this.get_store().find_element_user(CTParaRPrImpl.EFFECT$62, 0);
            if (ctTextEffect == null) {
                return null;
            }
            return ctTextEffect;
        }
    }
    
    public boolean isSetEffect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.EFFECT$62) != 0;
        }
    }
    
    public void setEffect(final CTTextEffect ctTextEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctTextEffect, CTParaRPrImpl.EFFECT$62, 0, (short)1);
    }
    
    public CTTextEffect addNewEffect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextEffect)this.get_store().add_element_user(CTParaRPrImpl.EFFECT$62);
        }
    }
    
    public void unsetEffect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.EFFECT$62, 0);
        }
    }
    
    public CTBorder getBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTParaRPrImpl.BDR$64, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.BDR$64) != 0;
        }
    }
    
    public void setBdr(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTParaRPrImpl.BDR$64, 0, (short)1);
    }
    
    public CTBorder addNewBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTParaRPrImpl.BDR$64);
        }
    }
    
    public void unsetBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.BDR$64, 0);
        }
    }
    
    public CTShd getShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShd ctShd = (CTShd)this.get_store().find_element_user(CTParaRPrImpl.SHD$66, 0);
            if (ctShd == null) {
                return null;
            }
            return ctShd;
        }
    }
    
    public boolean isSetShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.SHD$66) != 0;
        }
    }
    
    public void setShd(final CTShd ctShd) {
        this.generatedSetterHelperImpl((XmlObject)ctShd, CTParaRPrImpl.SHD$66, 0, (short)1);
    }
    
    public CTShd addNewShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShd)this.get_store().add_element_user(CTParaRPrImpl.SHD$66);
        }
    }
    
    public void unsetShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.SHD$66, 0);
        }
    }
    
    public CTFitText getFitText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFitText ctFitText = (CTFitText)this.get_store().find_element_user(CTParaRPrImpl.FITTEXT$68, 0);
            if (ctFitText == null) {
                return null;
            }
            return ctFitText;
        }
    }
    
    public boolean isSetFitText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.FITTEXT$68) != 0;
        }
    }
    
    public void setFitText(final CTFitText ctFitText) {
        this.generatedSetterHelperImpl((XmlObject)ctFitText, CTParaRPrImpl.FITTEXT$68, 0, (short)1);
    }
    
    public CTFitText addNewFitText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFitText)this.get_store().add_element_user(CTParaRPrImpl.FITTEXT$68);
        }
    }
    
    public void unsetFitText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.FITTEXT$68, 0);
        }
    }
    
    public CTVerticalAlignRun getVertAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVerticalAlignRun ctVerticalAlignRun = (CTVerticalAlignRun)this.get_store().find_element_user(CTParaRPrImpl.VERTALIGN$70, 0);
            if (ctVerticalAlignRun == null) {
                return null;
            }
            return ctVerticalAlignRun;
        }
    }
    
    public boolean isSetVertAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.VERTALIGN$70) != 0;
        }
    }
    
    public void setVertAlign(final CTVerticalAlignRun ctVerticalAlignRun) {
        this.generatedSetterHelperImpl((XmlObject)ctVerticalAlignRun, CTParaRPrImpl.VERTALIGN$70, 0, (short)1);
    }
    
    public CTVerticalAlignRun addNewVertAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVerticalAlignRun)this.get_store().add_element_user(CTParaRPrImpl.VERTALIGN$70);
        }
    }
    
    public void unsetVertAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.VERTALIGN$70, 0);
        }
    }
    
    public CTOnOff getRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.RTL$72, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.RTL$72) != 0;
        }
    }
    
    public void setRtl(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.RTL$72, 0, (short)1);
    }
    
    public CTOnOff addNewRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.RTL$72);
        }
    }
    
    public void unsetRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.RTL$72, 0);
        }
    }
    
    public CTOnOff getCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.CS$74, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.CS$74) != 0;
        }
    }
    
    public void setCs(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.CS$74, 0, (short)1);
    }
    
    public CTOnOff addNewCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.CS$74);
        }
    }
    
    public void unsetCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.CS$74, 0);
        }
    }
    
    public CTEm getEm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEm ctEm = (CTEm)this.get_store().find_element_user(CTParaRPrImpl.EM$76, 0);
            if (ctEm == null) {
                return null;
            }
            return ctEm;
        }
    }
    
    public boolean isSetEm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.EM$76) != 0;
        }
    }
    
    public void setEm(final CTEm ctEm) {
        this.generatedSetterHelperImpl((XmlObject)ctEm, CTParaRPrImpl.EM$76, 0, (short)1);
    }
    
    public CTEm addNewEm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEm)this.get_store().add_element_user(CTParaRPrImpl.EM$76);
        }
    }
    
    public void unsetEm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.EM$76, 0);
        }
    }
    
    public CTLanguage getLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLanguage ctLanguage = (CTLanguage)this.get_store().find_element_user(CTParaRPrImpl.LANG$78, 0);
            if (ctLanguage == null) {
                return null;
            }
            return ctLanguage;
        }
    }
    
    public boolean isSetLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.LANG$78) != 0;
        }
    }
    
    public void setLang(final CTLanguage ctLanguage) {
        this.generatedSetterHelperImpl((XmlObject)ctLanguage, CTParaRPrImpl.LANG$78, 0, (short)1);
    }
    
    public CTLanguage addNewLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLanguage)this.get_store().add_element_user(CTParaRPrImpl.LANG$78);
        }
    }
    
    public void unsetLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.LANG$78, 0);
        }
    }
    
    public CTEastAsianLayout getEastAsianLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEastAsianLayout ctEastAsianLayout = (CTEastAsianLayout)this.get_store().find_element_user(CTParaRPrImpl.EASTASIANLAYOUT$80, 0);
            if (ctEastAsianLayout == null) {
                return null;
            }
            return ctEastAsianLayout;
        }
    }
    
    public boolean isSetEastAsianLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.EASTASIANLAYOUT$80) != 0;
        }
    }
    
    public void setEastAsianLayout(final CTEastAsianLayout ctEastAsianLayout) {
        this.generatedSetterHelperImpl((XmlObject)ctEastAsianLayout, CTParaRPrImpl.EASTASIANLAYOUT$80, 0, (short)1);
    }
    
    public CTEastAsianLayout addNewEastAsianLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEastAsianLayout)this.get_store().add_element_user(CTParaRPrImpl.EASTASIANLAYOUT$80);
        }
    }
    
    public void unsetEastAsianLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.EASTASIANLAYOUT$80, 0);
        }
    }
    
    public CTOnOff getSpecVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.SPECVANISH$82, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSpecVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.SPECVANISH$82) != 0;
        }
    }
    
    public void setSpecVanish(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.SPECVANISH$82, 0, (short)1);
    }
    
    public CTOnOff addNewSpecVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.SPECVANISH$82);
        }
    }
    
    public void unsetSpecVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.SPECVANISH$82, 0);
        }
    }
    
    public CTOnOff getOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTParaRPrImpl.OMATH$84, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.OMATH$84) != 0;
        }
    }
    
    public void setOMath(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTParaRPrImpl.OMATH$84, 0, (short)1);
    }
    
    public CTOnOff addNewOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTParaRPrImpl.OMATH$84);
        }
    }
    
    public void unsetOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.OMATH$84, 0);
        }
    }
    
    public CTParaRPrChange getRPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTParaRPrChange ctParaRPrChange = (CTParaRPrChange)this.get_store().find_element_user(CTParaRPrImpl.RPRCHANGE$86, 0);
            if (ctParaRPrChange == null) {
                return null;
            }
            return ctParaRPrChange;
        }
    }
    
    public boolean isSetRPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTParaRPrImpl.RPRCHANGE$86) != 0;
        }
    }
    
    public void setRPrChange(final CTParaRPrChange ctParaRPrChange) {
        this.generatedSetterHelperImpl((XmlObject)ctParaRPrChange, CTParaRPrImpl.RPRCHANGE$86, 0, (short)1);
    }
    
    public CTParaRPrChange addNewRPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTParaRPrChange)this.get_store().add_element_user(CTParaRPrImpl.RPRCHANGE$86);
        }
    }
    
    public void unsetRPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTParaRPrImpl.RPRCHANGE$86, 0);
        }
    }
    
    static {
        INS$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ins");
        DEL$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "del");
        MOVEFROM$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFrom");
        MOVETO$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveTo");
        RSTYLE$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rStyle");
        RFONTS$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rFonts");
        B$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "b");
        BCS$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bCs");
        I$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "i");
        ICS$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "iCs");
        CAPS$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "caps");
        SMALLCAPS$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "smallCaps");
        STRIKE$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "strike");
        DSTRIKE$26 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dstrike");
        OUTLINE$28 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "outline");
        SHADOW$30 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "shadow");
        EMBOSS$32 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "emboss");
        IMPRINT$34 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "imprint");
        NOPROOF$36 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "noProof");
        SNAPTOGRID$38 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "snapToGrid");
        VANISH$40 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "vanish");
        WEBHIDDEN$42 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "webHidden");
        COLOR$44 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "color");
        SPACING$46 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "spacing");
        W$48 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "w");
        KERN$50 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "kern");
        POSITION$52 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "position");
        SZ$54 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sz");
        SZCS$56 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "szCs");
        HIGHLIGHT$58 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "highlight");
        U$60 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "u");
        EFFECT$62 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "effect");
        BDR$64 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bdr");
        SHD$66 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "shd");
        FITTEXT$68 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "fitText");
        VERTALIGN$70 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "vertAlign");
        RTL$72 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rtl");
        CS$74 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cs");
        EM$76 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "em");
        LANG$78 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lang");
        EASTASIANLAYOUT$80 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "eastAsianLayout");
        SPECVANISH$82 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "specVanish");
        OMATH$84 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "oMath");
        RPRCHANGE$86 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rPrChange");
    }
}
