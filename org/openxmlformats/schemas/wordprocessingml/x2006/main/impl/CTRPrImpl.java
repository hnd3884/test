package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPrChange;
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
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRPrImpl extends XmlComplexContentImpl implements CTRPr
{
    private static final long serialVersionUID = 1L;
    private static final QName RSTYLE$0;
    private static final QName RFONTS$2;
    private static final QName B$4;
    private static final QName BCS$6;
    private static final QName I$8;
    private static final QName ICS$10;
    private static final QName CAPS$12;
    private static final QName SMALLCAPS$14;
    private static final QName STRIKE$16;
    private static final QName DSTRIKE$18;
    private static final QName OUTLINE$20;
    private static final QName SHADOW$22;
    private static final QName EMBOSS$24;
    private static final QName IMPRINT$26;
    private static final QName NOPROOF$28;
    private static final QName SNAPTOGRID$30;
    private static final QName VANISH$32;
    private static final QName WEBHIDDEN$34;
    private static final QName COLOR$36;
    private static final QName SPACING$38;
    private static final QName W$40;
    private static final QName KERN$42;
    private static final QName POSITION$44;
    private static final QName SZ$46;
    private static final QName SZCS$48;
    private static final QName HIGHLIGHT$50;
    private static final QName U$52;
    private static final QName EFFECT$54;
    private static final QName BDR$56;
    private static final QName SHD$58;
    private static final QName FITTEXT$60;
    private static final QName VERTALIGN$62;
    private static final QName RTL$64;
    private static final QName CS$66;
    private static final QName EM$68;
    private static final QName LANG$70;
    private static final QName EASTASIANLAYOUT$72;
    private static final QName SPECVANISH$74;
    private static final QName OMATH$76;
    private static final QName RPRCHANGE$78;
    
    public CTRPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTString getRStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTRPrImpl.RSTYLE$0, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetRStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.RSTYLE$0) != 0;
        }
    }
    
    public void setRStyle(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTRPrImpl.RSTYLE$0, 0, (short)1);
    }
    
    public CTString addNewRStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTRPrImpl.RSTYLE$0);
        }
    }
    
    public void unsetRStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.RSTYLE$0, 0);
        }
    }
    
    public CTFonts getRFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFonts ctFonts = (CTFonts)this.get_store().find_element_user(CTRPrImpl.RFONTS$2, 0);
            if (ctFonts == null) {
                return null;
            }
            return ctFonts;
        }
    }
    
    public boolean isSetRFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.RFONTS$2) != 0;
        }
    }
    
    public void setRFonts(final CTFonts ctFonts) {
        this.generatedSetterHelperImpl((XmlObject)ctFonts, CTRPrImpl.RFONTS$2, 0, (short)1);
    }
    
    public CTFonts addNewRFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFonts)this.get_store().add_element_user(CTRPrImpl.RFONTS$2);
        }
    }
    
    public void unsetRFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.RFONTS$2, 0);
        }
    }
    
    public CTOnOff getB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.B$4, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.B$4) != 0;
        }
    }
    
    public void setB(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.B$4, 0, (short)1);
    }
    
    public CTOnOff addNewB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.B$4);
        }
    }
    
    public void unsetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.B$4, 0);
        }
    }
    
    public CTOnOff getBCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.BCS$6, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetBCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.BCS$6) != 0;
        }
    }
    
    public void setBCs(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.BCS$6, 0, (short)1);
    }
    
    public CTOnOff addNewBCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.BCS$6);
        }
    }
    
    public void unsetBCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.BCS$6, 0);
        }
    }
    
    public CTOnOff getI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.I$8, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.I$8) != 0;
        }
    }
    
    public void setI(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.I$8, 0, (short)1);
    }
    
    public CTOnOff addNewI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.I$8);
        }
    }
    
    public void unsetI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.I$8, 0);
        }
    }
    
    public CTOnOff getICs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.ICS$10, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetICs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.ICS$10) != 0;
        }
    }
    
    public void setICs(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.ICS$10, 0, (short)1);
    }
    
    public CTOnOff addNewICs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.ICS$10);
        }
    }
    
    public void unsetICs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.ICS$10, 0);
        }
    }
    
    public CTOnOff getCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.CAPS$12, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.CAPS$12) != 0;
        }
    }
    
    public void setCaps(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.CAPS$12, 0, (short)1);
    }
    
    public CTOnOff addNewCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.CAPS$12);
        }
    }
    
    public void unsetCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.CAPS$12, 0);
        }
    }
    
    public CTOnOff getSmallCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.SMALLCAPS$14, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSmallCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.SMALLCAPS$14) != 0;
        }
    }
    
    public void setSmallCaps(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.SMALLCAPS$14, 0, (short)1);
    }
    
    public CTOnOff addNewSmallCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.SMALLCAPS$14);
        }
    }
    
    public void unsetSmallCaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.SMALLCAPS$14, 0);
        }
    }
    
    public CTOnOff getStrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.STRIKE$16, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetStrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.STRIKE$16) != 0;
        }
    }
    
    public void setStrike(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.STRIKE$16, 0, (short)1);
    }
    
    public CTOnOff addNewStrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.STRIKE$16);
        }
    }
    
    public void unsetStrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.STRIKE$16, 0);
        }
    }
    
    public CTOnOff getDstrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.DSTRIKE$18, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDstrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.DSTRIKE$18) != 0;
        }
    }
    
    public void setDstrike(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.DSTRIKE$18, 0, (short)1);
    }
    
    public CTOnOff addNewDstrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.DSTRIKE$18);
        }
    }
    
    public void unsetDstrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.DSTRIKE$18, 0);
        }
    }
    
    public CTOnOff getOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.OUTLINE$20, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.OUTLINE$20) != 0;
        }
    }
    
    public void setOutline(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.OUTLINE$20, 0, (short)1);
    }
    
    public CTOnOff addNewOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.OUTLINE$20);
        }
    }
    
    public void unsetOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.OUTLINE$20, 0);
        }
    }
    
    public CTOnOff getShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.SHADOW$22, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.SHADOW$22) != 0;
        }
    }
    
    public void setShadow(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.SHADOW$22, 0, (short)1);
    }
    
    public CTOnOff addNewShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.SHADOW$22);
        }
    }
    
    public void unsetShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.SHADOW$22, 0);
        }
    }
    
    public CTOnOff getEmboss() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.EMBOSS$24, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetEmboss() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.EMBOSS$24) != 0;
        }
    }
    
    public void setEmboss(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.EMBOSS$24, 0, (short)1);
    }
    
    public CTOnOff addNewEmboss() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.EMBOSS$24);
        }
    }
    
    public void unsetEmboss() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.EMBOSS$24, 0);
        }
    }
    
    public CTOnOff getImprint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.IMPRINT$26, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetImprint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.IMPRINT$26) != 0;
        }
    }
    
    public void setImprint(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.IMPRINT$26, 0, (short)1);
    }
    
    public CTOnOff addNewImprint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.IMPRINT$26);
        }
    }
    
    public void unsetImprint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.IMPRINT$26, 0);
        }
    }
    
    public CTOnOff getNoProof() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.NOPROOF$28, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetNoProof() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.NOPROOF$28) != 0;
        }
    }
    
    public void setNoProof(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.NOPROOF$28, 0, (short)1);
    }
    
    public CTOnOff addNewNoProof() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.NOPROOF$28);
        }
    }
    
    public void unsetNoProof() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.NOPROOF$28, 0);
        }
    }
    
    public CTOnOff getSnapToGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.SNAPTOGRID$30, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSnapToGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.SNAPTOGRID$30) != 0;
        }
    }
    
    public void setSnapToGrid(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.SNAPTOGRID$30, 0, (short)1);
    }
    
    public CTOnOff addNewSnapToGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.SNAPTOGRID$30);
        }
    }
    
    public void unsetSnapToGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.SNAPTOGRID$30, 0);
        }
    }
    
    public CTOnOff getVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.VANISH$32, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.VANISH$32) != 0;
        }
    }
    
    public void setVanish(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.VANISH$32, 0, (short)1);
    }
    
    public CTOnOff addNewVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.VANISH$32);
        }
    }
    
    public void unsetVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.VANISH$32, 0);
        }
    }
    
    public CTOnOff getWebHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.WEBHIDDEN$34, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetWebHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.WEBHIDDEN$34) != 0;
        }
    }
    
    public void setWebHidden(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.WEBHIDDEN$34, 0, (short)1);
    }
    
    public CTOnOff addNewWebHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.WEBHIDDEN$34);
        }
    }
    
    public void unsetWebHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.WEBHIDDEN$34, 0);
        }
    }
    
    public CTColor getColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTRPrImpl.COLOR$36, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public boolean isSetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.COLOR$36) != 0;
        }
    }
    
    public void setColor(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTRPrImpl.COLOR$36, 0, (short)1);
    }
    
    public CTColor addNewColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTRPrImpl.COLOR$36);
        }
    }
    
    public void unsetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.COLOR$36, 0);
        }
    }
    
    public CTSignedTwipsMeasure getSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSignedTwipsMeasure ctSignedTwipsMeasure = (CTSignedTwipsMeasure)this.get_store().find_element_user(CTRPrImpl.SPACING$38, 0);
            if (ctSignedTwipsMeasure == null) {
                return null;
            }
            return ctSignedTwipsMeasure;
        }
    }
    
    public boolean isSetSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.SPACING$38) != 0;
        }
    }
    
    public void setSpacing(final CTSignedTwipsMeasure ctSignedTwipsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctSignedTwipsMeasure, CTRPrImpl.SPACING$38, 0, (short)1);
    }
    
    public CTSignedTwipsMeasure addNewSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSignedTwipsMeasure)this.get_store().add_element_user(CTRPrImpl.SPACING$38);
        }
    }
    
    public void unsetSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.SPACING$38, 0);
        }
    }
    
    public CTTextScale getW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextScale ctTextScale = (CTTextScale)this.get_store().find_element_user(CTRPrImpl.W$40, 0);
            if (ctTextScale == null) {
                return null;
            }
            return ctTextScale;
        }
    }
    
    public boolean isSetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.W$40) != 0;
        }
    }
    
    public void setW(final CTTextScale ctTextScale) {
        this.generatedSetterHelperImpl((XmlObject)ctTextScale, CTRPrImpl.W$40, 0, (short)1);
    }
    
    public CTTextScale addNewW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextScale)this.get_store().add_element_user(CTRPrImpl.W$40);
        }
    }
    
    public void unsetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.W$40, 0);
        }
    }
    
    public CTHpsMeasure getKern() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHpsMeasure ctHpsMeasure = (CTHpsMeasure)this.get_store().find_element_user(CTRPrImpl.KERN$42, 0);
            if (ctHpsMeasure == null) {
                return null;
            }
            return ctHpsMeasure;
        }
    }
    
    public boolean isSetKern() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.KERN$42) != 0;
        }
    }
    
    public void setKern(final CTHpsMeasure ctHpsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctHpsMeasure, CTRPrImpl.KERN$42, 0, (short)1);
    }
    
    public CTHpsMeasure addNewKern() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHpsMeasure)this.get_store().add_element_user(CTRPrImpl.KERN$42);
        }
    }
    
    public void unsetKern() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.KERN$42, 0);
        }
    }
    
    public CTSignedHpsMeasure getPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSignedHpsMeasure ctSignedHpsMeasure = (CTSignedHpsMeasure)this.get_store().find_element_user(CTRPrImpl.POSITION$44, 0);
            if (ctSignedHpsMeasure == null) {
                return null;
            }
            return ctSignedHpsMeasure;
        }
    }
    
    public boolean isSetPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.POSITION$44) != 0;
        }
    }
    
    public void setPosition(final CTSignedHpsMeasure ctSignedHpsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctSignedHpsMeasure, CTRPrImpl.POSITION$44, 0, (short)1);
    }
    
    public CTSignedHpsMeasure addNewPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSignedHpsMeasure)this.get_store().add_element_user(CTRPrImpl.POSITION$44);
        }
    }
    
    public void unsetPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.POSITION$44, 0);
        }
    }
    
    public CTHpsMeasure getSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHpsMeasure ctHpsMeasure = (CTHpsMeasure)this.get_store().find_element_user(CTRPrImpl.SZ$46, 0);
            if (ctHpsMeasure == null) {
                return null;
            }
            return ctHpsMeasure;
        }
    }
    
    public boolean isSetSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.SZ$46) != 0;
        }
    }
    
    public void setSz(final CTHpsMeasure ctHpsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctHpsMeasure, CTRPrImpl.SZ$46, 0, (short)1);
    }
    
    public CTHpsMeasure addNewSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHpsMeasure)this.get_store().add_element_user(CTRPrImpl.SZ$46);
        }
    }
    
    public void unsetSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.SZ$46, 0);
        }
    }
    
    public CTHpsMeasure getSzCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHpsMeasure ctHpsMeasure = (CTHpsMeasure)this.get_store().find_element_user(CTRPrImpl.SZCS$48, 0);
            if (ctHpsMeasure == null) {
                return null;
            }
            return ctHpsMeasure;
        }
    }
    
    public boolean isSetSzCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.SZCS$48) != 0;
        }
    }
    
    public void setSzCs(final CTHpsMeasure ctHpsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctHpsMeasure, CTRPrImpl.SZCS$48, 0, (short)1);
    }
    
    public CTHpsMeasure addNewSzCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHpsMeasure)this.get_store().add_element_user(CTRPrImpl.SZCS$48);
        }
    }
    
    public void unsetSzCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.SZCS$48, 0);
        }
    }
    
    public CTHighlight getHighlight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHighlight ctHighlight = (CTHighlight)this.get_store().find_element_user(CTRPrImpl.HIGHLIGHT$50, 0);
            if (ctHighlight == null) {
                return null;
            }
            return ctHighlight;
        }
    }
    
    public boolean isSetHighlight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.HIGHLIGHT$50) != 0;
        }
    }
    
    public void setHighlight(final CTHighlight ctHighlight) {
        this.generatedSetterHelperImpl((XmlObject)ctHighlight, CTRPrImpl.HIGHLIGHT$50, 0, (short)1);
    }
    
    public CTHighlight addNewHighlight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHighlight)this.get_store().add_element_user(CTRPrImpl.HIGHLIGHT$50);
        }
    }
    
    public void unsetHighlight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.HIGHLIGHT$50, 0);
        }
    }
    
    public CTUnderline getU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnderline ctUnderline = (CTUnderline)this.get_store().find_element_user(CTRPrImpl.U$52, 0);
            if (ctUnderline == null) {
                return null;
            }
            return ctUnderline;
        }
    }
    
    public boolean isSetU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.U$52) != 0;
        }
    }
    
    public void setU(final CTUnderline ctUnderline) {
        this.generatedSetterHelperImpl((XmlObject)ctUnderline, CTRPrImpl.U$52, 0, (short)1);
    }
    
    public CTUnderline addNewU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnderline)this.get_store().add_element_user(CTRPrImpl.U$52);
        }
    }
    
    public void unsetU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.U$52, 0);
        }
    }
    
    public CTTextEffect getEffect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextEffect ctTextEffect = (CTTextEffect)this.get_store().find_element_user(CTRPrImpl.EFFECT$54, 0);
            if (ctTextEffect == null) {
                return null;
            }
            return ctTextEffect;
        }
    }
    
    public boolean isSetEffect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.EFFECT$54) != 0;
        }
    }
    
    public void setEffect(final CTTextEffect ctTextEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctTextEffect, CTRPrImpl.EFFECT$54, 0, (short)1);
    }
    
    public CTTextEffect addNewEffect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextEffect)this.get_store().add_element_user(CTRPrImpl.EFFECT$54);
        }
    }
    
    public void unsetEffect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.EFFECT$54, 0);
        }
    }
    
    public CTBorder getBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTRPrImpl.BDR$56, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.BDR$56) != 0;
        }
    }
    
    public void setBdr(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTRPrImpl.BDR$56, 0, (short)1);
    }
    
    public CTBorder addNewBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTRPrImpl.BDR$56);
        }
    }
    
    public void unsetBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.BDR$56, 0);
        }
    }
    
    public CTShd getShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShd ctShd = (CTShd)this.get_store().find_element_user(CTRPrImpl.SHD$58, 0);
            if (ctShd == null) {
                return null;
            }
            return ctShd;
        }
    }
    
    public boolean isSetShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.SHD$58) != 0;
        }
    }
    
    public void setShd(final CTShd ctShd) {
        this.generatedSetterHelperImpl((XmlObject)ctShd, CTRPrImpl.SHD$58, 0, (short)1);
    }
    
    public CTShd addNewShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShd)this.get_store().add_element_user(CTRPrImpl.SHD$58);
        }
    }
    
    public void unsetShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.SHD$58, 0);
        }
    }
    
    public CTFitText getFitText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFitText ctFitText = (CTFitText)this.get_store().find_element_user(CTRPrImpl.FITTEXT$60, 0);
            if (ctFitText == null) {
                return null;
            }
            return ctFitText;
        }
    }
    
    public boolean isSetFitText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.FITTEXT$60) != 0;
        }
    }
    
    public void setFitText(final CTFitText ctFitText) {
        this.generatedSetterHelperImpl((XmlObject)ctFitText, CTRPrImpl.FITTEXT$60, 0, (short)1);
    }
    
    public CTFitText addNewFitText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFitText)this.get_store().add_element_user(CTRPrImpl.FITTEXT$60);
        }
    }
    
    public void unsetFitText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.FITTEXT$60, 0);
        }
    }
    
    public CTVerticalAlignRun getVertAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVerticalAlignRun ctVerticalAlignRun = (CTVerticalAlignRun)this.get_store().find_element_user(CTRPrImpl.VERTALIGN$62, 0);
            if (ctVerticalAlignRun == null) {
                return null;
            }
            return ctVerticalAlignRun;
        }
    }
    
    public boolean isSetVertAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.VERTALIGN$62) != 0;
        }
    }
    
    public void setVertAlign(final CTVerticalAlignRun ctVerticalAlignRun) {
        this.generatedSetterHelperImpl((XmlObject)ctVerticalAlignRun, CTRPrImpl.VERTALIGN$62, 0, (short)1);
    }
    
    public CTVerticalAlignRun addNewVertAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVerticalAlignRun)this.get_store().add_element_user(CTRPrImpl.VERTALIGN$62);
        }
    }
    
    public void unsetVertAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.VERTALIGN$62, 0);
        }
    }
    
    public CTOnOff getRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.RTL$64, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.RTL$64) != 0;
        }
    }
    
    public void setRtl(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.RTL$64, 0, (short)1);
    }
    
    public CTOnOff addNewRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.RTL$64);
        }
    }
    
    public void unsetRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.RTL$64, 0);
        }
    }
    
    public CTOnOff getCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.CS$66, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.CS$66) != 0;
        }
    }
    
    public void setCs(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.CS$66, 0, (short)1);
    }
    
    public CTOnOff addNewCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.CS$66);
        }
    }
    
    public void unsetCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.CS$66, 0);
        }
    }
    
    public CTEm getEm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEm ctEm = (CTEm)this.get_store().find_element_user(CTRPrImpl.EM$68, 0);
            if (ctEm == null) {
                return null;
            }
            return ctEm;
        }
    }
    
    public boolean isSetEm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.EM$68) != 0;
        }
    }
    
    public void setEm(final CTEm ctEm) {
        this.generatedSetterHelperImpl((XmlObject)ctEm, CTRPrImpl.EM$68, 0, (short)1);
    }
    
    public CTEm addNewEm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEm)this.get_store().add_element_user(CTRPrImpl.EM$68);
        }
    }
    
    public void unsetEm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.EM$68, 0);
        }
    }
    
    public CTLanguage getLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLanguage ctLanguage = (CTLanguage)this.get_store().find_element_user(CTRPrImpl.LANG$70, 0);
            if (ctLanguage == null) {
                return null;
            }
            return ctLanguage;
        }
    }
    
    public boolean isSetLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.LANG$70) != 0;
        }
    }
    
    public void setLang(final CTLanguage ctLanguage) {
        this.generatedSetterHelperImpl((XmlObject)ctLanguage, CTRPrImpl.LANG$70, 0, (short)1);
    }
    
    public CTLanguage addNewLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLanguage)this.get_store().add_element_user(CTRPrImpl.LANG$70);
        }
    }
    
    public void unsetLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.LANG$70, 0);
        }
    }
    
    public CTEastAsianLayout getEastAsianLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEastAsianLayout ctEastAsianLayout = (CTEastAsianLayout)this.get_store().find_element_user(CTRPrImpl.EASTASIANLAYOUT$72, 0);
            if (ctEastAsianLayout == null) {
                return null;
            }
            return ctEastAsianLayout;
        }
    }
    
    public boolean isSetEastAsianLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.EASTASIANLAYOUT$72) != 0;
        }
    }
    
    public void setEastAsianLayout(final CTEastAsianLayout ctEastAsianLayout) {
        this.generatedSetterHelperImpl((XmlObject)ctEastAsianLayout, CTRPrImpl.EASTASIANLAYOUT$72, 0, (short)1);
    }
    
    public CTEastAsianLayout addNewEastAsianLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEastAsianLayout)this.get_store().add_element_user(CTRPrImpl.EASTASIANLAYOUT$72);
        }
    }
    
    public void unsetEastAsianLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.EASTASIANLAYOUT$72, 0);
        }
    }
    
    public CTOnOff getSpecVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.SPECVANISH$74, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSpecVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.SPECVANISH$74) != 0;
        }
    }
    
    public void setSpecVanish(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.SPECVANISH$74, 0, (short)1);
    }
    
    public CTOnOff addNewSpecVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.SPECVANISH$74);
        }
    }
    
    public void unsetSpecVanish() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.SPECVANISH$74, 0);
        }
    }
    
    public CTOnOff getOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRPrImpl.OMATH$76, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.OMATH$76) != 0;
        }
    }
    
    public void setOMath(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRPrImpl.OMATH$76, 0, (short)1);
    }
    
    public CTOnOff addNewOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRPrImpl.OMATH$76);
        }
    }
    
    public void unsetOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.OMATH$76, 0);
        }
    }
    
    public CTRPrChange getRPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRPrChange ctrPrChange = (CTRPrChange)this.get_store().find_element_user(CTRPrImpl.RPRCHANGE$78, 0);
            if (ctrPrChange == null) {
                return null;
            }
            return ctrPrChange;
        }
    }
    
    public boolean isSetRPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrImpl.RPRCHANGE$78) != 0;
        }
    }
    
    public void setRPrChange(final CTRPrChange ctrPrChange) {
        this.generatedSetterHelperImpl((XmlObject)ctrPrChange, CTRPrImpl.RPRCHANGE$78, 0, (short)1);
    }
    
    public CTRPrChange addNewRPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRPrChange)this.get_store().add_element_user(CTRPrImpl.RPRCHANGE$78);
        }
    }
    
    public void unsetRPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrImpl.RPRCHANGE$78, 0);
        }
    }
    
    static {
        RSTYLE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rStyle");
        RFONTS$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rFonts");
        B$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "b");
        BCS$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bCs");
        I$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "i");
        ICS$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "iCs");
        CAPS$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "caps");
        SMALLCAPS$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "smallCaps");
        STRIKE$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "strike");
        DSTRIKE$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dstrike");
        OUTLINE$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "outline");
        SHADOW$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "shadow");
        EMBOSS$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "emboss");
        IMPRINT$26 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "imprint");
        NOPROOF$28 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "noProof");
        SNAPTOGRID$30 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "snapToGrid");
        VANISH$32 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "vanish");
        WEBHIDDEN$34 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "webHidden");
        COLOR$36 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "color");
        SPACING$38 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "spacing");
        W$40 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "w");
        KERN$42 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "kern");
        POSITION$44 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "position");
        SZ$46 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sz");
        SZCS$48 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "szCs");
        HIGHLIGHT$50 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "highlight");
        U$52 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "u");
        EFFECT$54 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "effect");
        BDR$56 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bdr");
        SHD$58 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "shd");
        FITTEXT$60 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "fitText");
        VERTALIGN$62 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "vertAlign");
        RTL$64 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rtl");
        CS$66 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cs");
        EM$68 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "em");
        LANG$70 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lang");
        EASTASIANLAYOUT$72 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "eastAsianLayout");
        SPECVANISH$74 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "specVanish");
        OMATH$76 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "oMath");
        RPRCHANGE$78 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rPrChange");
    }
}
