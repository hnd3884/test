package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextPoint;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextCapsType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextNonNegativePoint;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextStrikeType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextUnderlineType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextFontSize;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextLanguageID;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextUnderlineFillGroupWrapper;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextUnderlineFillFollowText;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextUnderlineLineFollowText;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectContainer;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextCharacterPropertiesImpl extends XmlComplexContentImpl implements CTTextCharacterProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName LN$0;
    private static final QName NOFILL$2;
    private static final QName SOLIDFILL$4;
    private static final QName GRADFILL$6;
    private static final QName BLIPFILL$8;
    private static final QName PATTFILL$10;
    private static final QName GRPFILL$12;
    private static final QName EFFECTLST$14;
    private static final QName EFFECTDAG$16;
    private static final QName HIGHLIGHT$18;
    private static final QName ULNTX$20;
    private static final QName ULN$22;
    private static final QName UFILLTX$24;
    private static final QName UFILL$26;
    private static final QName LATIN$28;
    private static final QName EA$30;
    private static final QName CS$32;
    private static final QName SYM$34;
    private static final QName HLINKCLICK$36;
    private static final QName HLINKMOUSEOVER$38;
    private static final QName EXTLST$40;
    private static final QName KUMIMOJI$42;
    private static final QName LANG$44;
    private static final QName ALTLANG$46;
    private static final QName SZ$48;
    private static final QName B$50;
    private static final QName I$52;
    private static final QName U$54;
    private static final QName STRIKE$56;
    private static final QName KERN$58;
    private static final QName CAP$60;
    private static final QName SPC$62;
    private static final QName NORMALIZEH$64;
    private static final QName BASELINE$66;
    private static final QName NOPROOF$68;
    private static final QName DIRTY$70;
    private static final QName ERR$72;
    private static final QName SMTCLEAN$74;
    private static final QName SMTID$76;
    private static final QName BMK$78;
    
    public CTTextCharacterPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTLineProperties getLn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineProperties ctLineProperties = (CTLineProperties)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.LN$0, 0);
            if (ctLineProperties == null) {
                return null;
            }
            return ctLineProperties;
        }
    }
    
    public boolean isSetLn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.LN$0) != 0;
        }
    }
    
    public void setLn(final CTLineProperties ctLineProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctLineProperties, CTTextCharacterPropertiesImpl.LN$0, 0, (short)1);
    }
    
    public CTLineProperties addNewLn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineProperties)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.LN$0);
        }
    }
    
    public void unsetLn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.LN$0, 0);
        }
    }
    
    public CTNoFillProperties getNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNoFillProperties ctNoFillProperties = (CTNoFillProperties)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.NOFILL$2, 0);
            if (ctNoFillProperties == null) {
                return null;
            }
            return ctNoFillProperties;
        }
    }
    
    public boolean isSetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.NOFILL$2) != 0;
        }
    }
    
    public void setNoFill(final CTNoFillProperties ctNoFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctNoFillProperties, CTTextCharacterPropertiesImpl.NOFILL$2, 0, (short)1);
    }
    
    public CTNoFillProperties addNewNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNoFillProperties)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.NOFILL$2);
        }
    }
    
    public void unsetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.NOFILL$2, 0);
        }
    }
    
    public CTSolidColorFillProperties getSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSolidColorFillProperties ctSolidColorFillProperties = (CTSolidColorFillProperties)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.SOLIDFILL$4, 0);
            if (ctSolidColorFillProperties == null) {
                return null;
            }
            return ctSolidColorFillProperties;
        }
    }
    
    public boolean isSetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.SOLIDFILL$4) != 0;
        }
    }
    
    public void setSolidFill(final CTSolidColorFillProperties ctSolidColorFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctSolidColorFillProperties, CTTextCharacterPropertiesImpl.SOLIDFILL$4, 0, (short)1);
    }
    
    public CTSolidColorFillProperties addNewSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSolidColorFillProperties)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.SOLIDFILL$4);
        }
    }
    
    public void unsetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.SOLIDFILL$4, 0);
        }
    }
    
    public CTGradientFillProperties getGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGradientFillProperties ctGradientFillProperties = (CTGradientFillProperties)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.GRADFILL$6, 0);
            if (ctGradientFillProperties == null) {
                return null;
            }
            return ctGradientFillProperties;
        }
    }
    
    public boolean isSetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.GRADFILL$6) != 0;
        }
    }
    
    public void setGradFill(final CTGradientFillProperties ctGradientFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGradientFillProperties, CTTextCharacterPropertiesImpl.GRADFILL$6, 0, (short)1);
    }
    
    public CTGradientFillProperties addNewGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGradientFillProperties)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.GRADFILL$6);
        }
    }
    
    public void unsetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.GRADFILL$6, 0);
        }
    }
    
    public CTBlipFillProperties getBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlipFillProperties ctBlipFillProperties = (CTBlipFillProperties)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.BLIPFILL$8, 0);
            if (ctBlipFillProperties == null) {
                return null;
            }
            return ctBlipFillProperties;
        }
    }
    
    public boolean isSetBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.BLIPFILL$8) != 0;
        }
    }
    
    public void setBlipFill(final CTBlipFillProperties ctBlipFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctBlipFillProperties, CTTextCharacterPropertiesImpl.BLIPFILL$8, 0, (short)1);
    }
    
    public CTBlipFillProperties addNewBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlipFillProperties)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.BLIPFILL$8);
        }
    }
    
    public void unsetBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.BLIPFILL$8, 0);
        }
    }
    
    public CTPatternFillProperties getPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPatternFillProperties ctPatternFillProperties = (CTPatternFillProperties)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.PATTFILL$10, 0);
            if (ctPatternFillProperties == null) {
                return null;
            }
            return ctPatternFillProperties;
        }
    }
    
    public boolean isSetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.PATTFILL$10) != 0;
        }
    }
    
    public void setPattFill(final CTPatternFillProperties ctPatternFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctPatternFillProperties, CTTextCharacterPropertiesImpl.PATTFILL$10, 0, (short)1);
    }
    
    public CTPatternFillProperties addNewPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPatternFillProperties)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.PATTFILL$10);
        }
    }
    
    public void unsetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.PATTFILL$10, 0);
        }
    }
    
    public CTGroupFillProperties getGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupFillProperties ctGroupFillProperties = (CTGroupFillProperties)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.GRPFILL$12, 0);
            if (ctGroupFillProperties == null) {
                return null;
            }
            return ctGroupFillProperties;
        }
    }
    
    public boolean isSetGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.GRPFILL$12) != 0;
        }
    }
    
    public void setGrpFill(final CTGroupFillProperties ctGroupFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupFillProperties, CTTextCharacterPropertiesImpl.GRPFILL$12, 0, (short)1);
    }
    
    public CTGroupFillProperties addNewGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupFillProperties)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.GRPFILL$12);
        }
    }
    
    public void unsetGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.GRPFILL$12, 0);
        }
    }
    
    public CTEffectList getEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectList list = (CTEffectList)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.EFFECTLST$14, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.EFFECTLST$14) != 0;
        }
    }
    
    public void setEffectLst(final CTEffectList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTextCharacterPropertiesImpl.EFFECTLST$14, 0, (short)1);
    }
    
    public CTEffectList addNewEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectList)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.EFFECTLST$14);
        }
    }
    
    public void unsetEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.EFFECTLST$14, 0);
        }
    }
    
    public CTEffectContainer getEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectContainer ctEffectContainer = (CTEffectContainer)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.EFFECTDAG$16, 0);
            if (ctEffectContainer == null) {
                return null;
            }
            return ctEffectContainer;
        }
    }
    
    public boolean isSetEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.EFFECTDAG$16) != 0;
        }
    }
    
    public void setEffectDag(final CTEffectContainer ctEffectContainer) {
        this.generatedSetterHelperImpl((XmlObject)ctEffectContainer, CTTextCharacterPropertiesImpl.EFFECTDAG$16, 0, (short)1);
    }
    
    public CTEffectContainer addNewEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectContainer)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.EFFECTDAG$16);
        }
    }
    
    public void unsetEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.EFFECTDAG$16, 0);
        }
    }
    
    public CTColor getHighlight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.HIGHLIGHT$18, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public boolean isSetHighlight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.HIGHLIGHT$18) != 0;
        }
    }
    
    public void setHighlight(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTTextCharacterPropertiesImpl.HIGHLIGHT$18, 0, (short)1);
    }
    
    public CTColor addNewHighlight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.HIGHLIGHT$18);
        }
    }
    
    public void unsetHighlight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.HIGHLIGHT$18, 0);
        }
    }
    
    public CTTextUnderlineLineFollowText getULnTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextUnderlineLineFollowText ctTextUnderlineLineFollowText = (CTTextUnderlineLineFollowText)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.ULNTX$20, 0);
            if (ctTextUnderlineLineFollowText == null) {
                return null;
            }
            return ctTextUnderlineLineFollowText;
        }
    }
    
    public boolean isSetULnTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.ULNTX$20) != 0;
        }
    }
    
    public void setULnTx(final CTTextUnderlineLineFollowText ctTextUnderlineLineFollowText) {
        this.generatedSetterHelperImpl((XmlObject)ctTextUnderlineLineFollowText, CTTextCharacterPropertiesImpl.ULNTX$20, 0, (short)1);
    }
    
    public CTTextUnderlineLineFollowText addNewULnTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextUnderlineLineFollowText)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.ULNTX$20);
        }
    }
    
    public void unsetULnTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.ULNTX$20, 0);
        }
    }
    
    public CTLineProperties getULn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineProperties ctLineProperties = (CTLineProperties)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.ULN$22, 0);
            if (ctLineProperties == null) {
                return null;
            }
            return ctLineProperties;
        }
    }
    
    public boolean isSetULn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.ULN$22) != 0;
        }
    }
    
    public void setULn(final CTLineProperties ctLineProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctLineProperties, CTTextCharacterPropertiesImpl.ULN$22, 0, (short)1);
    }
    
    public CTLineProperties addNewULn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineProperties)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.ULN$22);
        }
    }
    
    public void unsetULn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.ULN$22, 0);
        }
    }
    
    public CTTextUnderlineFillFollowText getUFillTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextUnderlineFillFollowText ctTextUnderlineFillFollowText = (CTTextUnderlineFillFollowText)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.UFILLTX$24, 0);
            if (ctTextUnderlineFillFollowText == null) {
                return null;
            }
            return ctTextUnderlineFillFollowText;
        }
    }
    
    public boolean isSetUFillTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.UFILLTX$24) != 0;
        }
    }
    
    public void setUFillTx(final CTTextUnderlineFillFollowText ctTextUnderlineFillFollowText) {
        this.generatedSetterHelperImpl((XmlObject)ctTextUnderlineFillFollowText, CTTextCharacterPropertiesImpl.UFILLTX$24, 0, (short)1);
    }
    
    public CTTextUnderlineFillFollowText addNewUFillTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextUnderlineFillFollowText)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.UFILLTX$24);
        }
    }
    
    public void unsetUFillTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.UFILLTX$24, 0);
        }
    }
    
    public CTTextUnderlineFillGroupWrapper getUFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextUnderlineFillGroupWrapper ctTextUnderlineFillGroupWrapper = (CTTextUnderlineFillGroupWrapper)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.UFILL$26, 0);
            if (ctTextUnderlineFillGroupWrapper == null) {
                return null;
            }
            return ctTextUnderlineFillGroupWrapper;
        }
    }
    
    public boolean isSetUFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.UFILL$26) != 0;
        }
    }
    
    public void setUFill(final CTTextUnderlineFillGroupWrapper ctTextUnderlineFillGroupWrapper) {
        this.generatedSetterHelperImpl((XmlObject)ctTextUnderlineFillGroupWrapper, CTTextCharacterPropertiesImpl.UFILL$26, 0, (short)1);
    }
    
    public CTTextUnderlineFillGroupWrapper addNewUFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextUnderlineFillGroupWrapper)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.UFILL$26);
        }
    }
    
    public void unsetUFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.UFILL$26, 0);
        }
    }
    
    public CTTextFont getLatin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextFont ctTextFont = (CTTextFont)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.LATIN$28, 0);
            if (ctTextFont == null) {
                return null;
            }
            return ctTextFont;
        }
    }
    
    public boolean isSetLatin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.LATIN$28) != 0;
        }
    }
    
    public void setLatin(final CTTextFont ctTextFont) {
        this.generatedSetterHelperImpl((XmlObject)ctTextFont, CTTextCharacterPropertiesImpl.LATIN$28, 0, (short)1);
    }
    
    public CTTextFont addNewLatin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextFont)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.LATIN$28);
        }
    }
    
    public void unsetLatin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.LATIN$28, 0);
        }
    }
    
    public CTTextFont getEa() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextFont ctTextFont = (CTTextFont)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.EA$30, 0);
            if (ctTextFont == null) {
                return null;
            }
            return ctTextFont;
        }
    }
    
    public boolean isSetEa() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.EA$30) != 0;
        }
    }
    
    public void setEa(final CTTextFont ctTextFont) {
        this.generatedSetterHelperImpl((XmlObject)ctTextFont, CTTextCharacterPropertiesImpl.EA$30, 0, (short)1);
    }
    
    public CTTextFont addNewEa() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextFont)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.EA$30);
        }
    }
    
    public void unsetEa() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.EA$30, 0);
        }
    }
    
    public CTTextFont getCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextFont ctTextFont = (CTTextFont)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.CS$32, 0);
            if (ctTextFont == null) {
                return null;
            }
            return ctTextFont;
        }
    }
    
    public boolean isSetCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.CS$32) != 0;
        }
    }
    
    public void setCs(final CTTextFont ctTextFont) {
        this.generatedSetterHelperImpl((XmlObject)ctTextFont, CTTextCharacterPropertiesImpl.CS$32, 0, (short)1);
    }
    
    public CTTextFont addNewCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextFont)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.CS$32);
        }
    }
    
    public void unsetCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.CS$32, 0);
        }
    }
    
    public CTTextFont getSym() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextFont ctTextFont = (CTTextFont)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.SYM$34, 0);
            if (ctTextFont == null) {
                return null;
            }
            return ctTextFont;
        }
    }
    
    public boolean isSetSym() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.SYM$34) != 0;
        }
    }
    
    public void setSym(final CTTextFont ctTextFont) {
        this.generatedSetterHelperImpl((XmlObject)ctTextFont, CTTextCharacterPropertiesImpl.SYM$34, 0, (short)1);
    }
    
    public CTTextFont addNewSym() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextFont)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.SYM$34);
        }
    }
    
    public void unsetSym() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.SYM$34, 0);
        }
    }
    
    public CTHyperlink getHlinkClick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHyperlink ctHyperlink = (CTHyperlink)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.HLINKCLICK$36, 0);
            if (ctHyperlink == null) {
                return null;
            }
            return ctHyperlink;
        }
    }
    
    public boolean isSetHlinkClick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.HLINKCLICK$36) != 0;
        }
    }
    
    public void setHlinkClick(final CTHyperlink ctHyperlink) {
        this.generatedSetterHelperImpl((XmlObject)ctHyperlink, CTTextCharacterPropertiesImpl.HLINKCLICK$36, 0, (short)1);
    }
    
    public CTHyperlink addNewHlinkClick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHyperlink)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.HLINKCLICK$36);
        }
    }
    
    public void unsetHlinkClick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.HLINKCLICK$36, 0);
        }
    }
    
    public CTHyperlink getHlinkMouseOver() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHyperlink ctHyperlink = (CTHyperlink)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.HLINKMOUSEOVER$38, 0);
            if (ctHyperlink == null) {
                return null;
            }
            return ctHyperlink;
        }
    }
    
    public boolean isSetHlinkMouseOver() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.HLINKMOUSEOVER$38) != 0;
        }
    }
    
    public void setHlinkMouseOver(final CTHyperlink ctHyperlink) {
        this.generatedSetterHelperImpl((XmlObject)ctHyperlink, CTTextCharacterPropertiesImpl.HLINKMOUSEOVER$38, 0, (short)1);
    }
    
    public CTHyperlink addNewHlinkMouseOver() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHyperlink)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.HLINKMOUSEOVER$38);
        }
    }
    
    public void unsetHlinkMouseOver() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.HLINKMOUSEOVER$38, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTTextCharacterPropertiesImpl.EXTLST$40, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextCharacterPropertiesImpl.EXTLST$40) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTextCharacterPropertiesImpl.EXTLST$40, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTTextCharacterPropertiesImpl.EXTLST$40);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextCharacterPropertiesImpl.EXTLST$40, 0);
        }
    }
    
    public boolean getKumimoji() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.KUMIMOJI$42);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetKumimoji() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.KUMIMOJI$42);
        }
    }
    
    public boolean isSetKumimoji() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.KUMIMOJI$42) != null;
        }
    }
    
    public void setKumimoji(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.KUMIMOJI$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.KUMIMOJI$42);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetKumimoji(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.KUMIMOJI$42);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.KUMIMOJI$42);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetKumimoji() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.KUMIMOJI$42);
        }
    }
    
    public String getLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.LANG$44);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STTextLanguageID xgetLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextLanguageID)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.LANG$44);
        }
    }
    
    public boolean isSetLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.LANG$44) != null;
        }
    }
    
    public void setLang(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.LANG$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.LANG$44);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetLang(final STTextLanguageID stTextLanguageID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextLanguageID stTextLanguageID2 = (STTextLanguageID)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.LANG$44);
            if (stTextLanguageID2 == null) {
                stTextLanguageID2 = (STTextLanguageID)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.LANG$44);
            }
            stTextLanguageID2.set((XmlObject)stTextLanguageID);
        }
    }
    
    public void unsetLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.LANG$44);
        }
    }
    
    public String getAltLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.ALTLANG$46);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STTextLanguageID xgetAltLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextLanguageID)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.ALTLANG$46);
        }
    }
    
    public boolean isSetAltLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.ALTLANG$46) != null;
        }
    }
    
    public void setAltLang(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.ALTLANG$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.ALTLANG$46);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAltLang(final STTextLanguageID stTextLanguageID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextLanguageID stTextLanguageID2 = (STTextLanguageID)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.ALTLANG$46);
            if (stTextLanguageID2 == null) {
                stTextLanguageID2 = (STTextLanguageID)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.ALTLANG$46);
            }
            stTextLanguageID2.set((XmlObject)stTextLanguageID);
        }
    }
    
    public void unsetAltLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.ALTLANG$46);
        }
    }
    
    public int getSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SZ$48);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextFontSize xgetSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextFontSize)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SZ$48);
        }
    }
    
    public boolean isSetSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SZ$48) != null;
        }
    }
    
    public void setSz(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SZ$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.SZ$48);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetSz(final STTextFontSize stTextFontSize) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextFontSize stTextFontSize2 = (STTextFontSize)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SZ$48);
            if (stTextFontSize2 == null) {
                stTextFontSize2 = (STTextFontSize)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.SZ$48);
            }
            stTextFontSize2.set((XmlObject)stTextFontSize);
        }
    }
    
    public void unsetSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.SZ$48);
        }
    }
    
    public boolean getB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.B$50);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.B$50);
        }
    }
    
    public boolean isSetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.B$50) != null;
        }
    }
    
    public void setB(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.B$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.B$50);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetB(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.B$50);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.B$50);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.B$50);
        }
    }
    
    public boolean getI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.I$52);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.I$52);
        }
    }
    
    public boolean isSetI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.I$52) != null;
        }
    }
    
    public void setI(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.I$52);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.I$52);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetI(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.I$52);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.I$52);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.I$52);
        }
    }
    
    public STTextUnderlineType.Enum getU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.U$54);
            if (simpleValue == null) {
                return null;
            }
            return (STTextUnderlineType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextUnderlineType xgetU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextUnderlineType)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.U$54);
        }
    }
    
    public boolean isSetU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.U$54) != null;
        }
    }
    
    public void setU(final STTextUnderlineType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.U$54);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.U$54);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetU(final STTextUnderlineType stTextUnderlineType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextUnderlineType stTextUnderlineType2 = (STTextUnderlineType)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.U$54);
            if (stTextUnderlineType2 == null) {
                stTextUnderlineType2 = (STTextUnderlineType)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.U$54);
            }
            stTextUnderlineType2.set((XmlObject)stTextUnderlineType);
        }
    }
    
    public void unsetU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.U$54);
        }
    }
    
    public STTextStrikeType.Enum getStrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.STRIKE$56);
            if (simpleValue == null) {
                return null;
            }
            return (STTextStrikeType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextStrikeType xgetStrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextStrikeType)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.STRIKE$56);
        }
    }
    
    public boolean isSetStrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.STRIKE$56) != null;
        }
    }
    
    public void setStrike(final STTextStrikeType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.STRIKE$56);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.STRIKE$56);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetStrike(final STTextStrikeType stTextStrikeType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextStrikeType stTextStrikeType2 = (STTextStrikeType)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.STRIKE$56);
            if (stTextStrikeType2 == null) {
                stTextStrikeType2 = (STTextStrikeType)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.STRIKE$56);
            }
            stTextStrikeType2.set((XmlObject)stTextStrikeType);
        }
    }
    
    public void unsetStrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.STRIKE$56);
        }
    }
    
    public int getKern() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.KERN$58);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextNonNegativePoint xgetKern() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextNonNegativePoint)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.KERN$58);
        }
    }
    
    public boolean isSetKern() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.KERN$58) != null;
        }
    }
    
    public void setKern(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.KERN$58);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.KERN$58);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetKern(final STTextNonNegativePoint stTextNonNegativePoint) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextNonNegativePoint stTextNonNegativePoint2 = (STTextNonNegativePoint)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.KERN$58);
            if (stTextNonNegativePoint2 == null) {
                stTextNonNegativePoint2 = (STTextNonNegativePoint)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.KERN$58);
            }
            stTextNonNegativePoint2.set((XmlObject)stTextNonNegativePoint);
        }
    }
    
    public void unsetKern() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.KERN$58);
        }
    }
    
    public STTextCapsType.Enum getCap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.CAP$60);
            if (simpleValue == null) {
                return null;
            }
            return (STTextCapsType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextCapsType xgetCap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextCapsType)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.CAP$60);
        }
    }
    
    public boolean isSetCap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.CAP$60) != null;
        }
    }
    
    public void setCap(final STTextCapsType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.CAP$60);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.CAP$60);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCap(final STTextCapsType stTextCapsType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextCapsType stTextCapsType2 = (STTextCapsType)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.CAP$60);
            if (stTextCapsType2 == null) {
                stTextCapsType2 = (STTextCapsType)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.CAP$60);
            }
            stTextCapsType2.set((XmlObject)stTextCapsType);
        }
    }
    
    public void unsetCap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.CAP$60);
        }
    }
    
    public int getSpc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SPC$62);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextPoint xgetSpc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextPoint)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SPC$62);
        }
    }
    
    public boolean isSetSpc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SPC$62) != null;
        }
    }
    
    public void setSpc(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SPC$62);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.SPC$62);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetSpc(final STTextPoint stTextPoint) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextPoint stTextPoint2 = (STTextPoint)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SPC$62);
            if (stTextPoint2 == null) {
                stTextPoint2 = (STTextPoint)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.SPC$62);
            }
            stTextPoint2.set((XmlObject)stTextPoint);
        }
    }
    
    public void unsetSpc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.SPC$62);
        }
    }
    
    public boolean getNormalizeH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.NORMALIZEH$64);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNormalizeH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.NORMALIZEH$64);
        }
    }
    
    public boolean isSetNormalizeH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.NORMALIZEH$64) != null;
        }
    }
    
    public void setNormalizeH(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.NORMALIZEH$64);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.NORMALIZEH$64);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNormalizeH(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.NORMALIZEH$64);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.NORMALIZEH$64);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNormalizeH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.NORMALIZEH$64);
        }
    }
    
    public int getBaseline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.BASELINE$66);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPercentage xgetBaseline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPercentage)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.BASELINE$66);
        }
    }
    
    public boolean isSetBaseline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.BASELINE$66) != null;
        }
    }
    
    public void setBaseline(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.BASELINE$66);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.BASELINE$66);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetBaseline(final STPercentage stPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage2 = (STPercentage)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.BASELINE$66);
            if (stPercentage2 == null) {
                stPercentage2 = (STPercentage)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.BASELINE$66);
            }
            stPercentage2.set((XmlObject)stPercentage);
        }
    }
    
    public void unsetBaseline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.BASELINE$66);
        }
    }
    
    public boolean getNoProof() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.NOPROOF$68);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNoProof() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.NOPROOF$68);
        }
    }
    
    public boolean isSetNoProof() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.NOPROOF$68) != null;
        }
    }
    
    public void setNoProof(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.NOPROOF$68);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.NOPROOF$68);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNoProof(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.NOPROOF$68);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.NOPROOF$68);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNoProof() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.NOPROOF$68);
        }
    }
    
    public boolean getDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.DIRTY$70);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTextCharacterPropertiesImpl.DIRTY$70);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.DIRTY$70);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTextCharacterPropertiesImpl.DIRTY$70);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.DIRTY$70) != null;
        }
    }
    
    public void setDirty(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.DIRTY$70);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.DIRTY$70);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDirty(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.DIRTY$70);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.DIRTY$70);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.DIRTY$70);
        }
    }
    
    public boolean getErr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.ERR$72);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTextCharacterPropertiesImpl.ERR$72);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetErr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.ERR$72);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTextCharacterPropertiesImpl.ERR$72);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetErr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.ERR$72) != null;
        }
    }
    
    public void setErr(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.ERR$72);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.ERR$72);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetErr(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.ERR$72);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.ERR$72);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetErr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.ERR$72);
        }
    }
    
    public boolean getSmtClean() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SMTCLEAN$74);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTextCharacterPropertiesImpl.SMTCLEAN$74);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSmtClean() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SMTCLEAN$74);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTextCharacterPropertiesImpl.SMTCLEAN$74);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSmtClean() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SMTCLEAN$74) != null;
        }
    }
    
    public void setSmtClean(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SMTCLEAN$74);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.SMTCLEAN$74);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSmtClean(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SMTCLEAN$74);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.SMTCLEAN$74);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSmtClean() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.SMTCLEAN$74);
        }
    }
    
    public long getSmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SMTID$76);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTextCharacterPropertiesImpl.SMTID$76);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetSmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SMTID$76);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTTextCharacterPropertiesImpl.SMTID$76);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetSmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SMTID$76) != null;
        }
    }
    
    public void setSmtId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SMTID$76);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.SMTID$76);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetSmtId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.SMTID$76);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.SMTID$76);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetSmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.SMTID$76);
        }
    }
    
    public String getBmk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.BMK$78);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetBmk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.BMK$78);
        }
    }
    
    public boolean isSetBmk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.BMK$78) != null;
        }
    }
    
    public void setBmk(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.BMK$78);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.BMK$78);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBmk(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTTextCharacterPropertiesImpl.BMK$78);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTTextCharacterPropertiesImpl.BMK$78);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetBmk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextCharacterPropertiesImpl.BMK$78);
        }
    }
    
    static {
        LN$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ln");
        NOFILL$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "noFill");
        SOLIDFILL$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "solidFill");
        GRADFILL$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gradFill");
        BLIPFILL$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blipFill");
        PATTFILL$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pattFill");
        GRPFILL$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "grpFill");
        EFFECTLST$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectLst");
        EFFECTDAG$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectDag");
        HIGHLIGHT$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "highlight");
        ULNTX$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "uLnTx");
        ULN$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "uLn");
        UFILLTX$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "uFillTx");
        UFILL$26 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "uFill");
        LATIN$28 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "latin");
        EA$30 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ea");
        CS$32 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "cs");
        SYM$34 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "sym");
        HLINKCLICK$36 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hlinkClick");
        HLINKMOUSEOVER$38 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hlinkMouseOver");
        EXTLST$40 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        KUMIMOJI$42 = new QName("", "kumimoji");
        LANG$44 = new QName("", "lang");
        ALTLANG$46 = new QName("", "altLang");
        SZ$48 = new QName("", "sz");
        B$50 = new QName("", "b");
        I$52 = new QName("", "i");
        U$54 = new QName("", "u");
        STRIKE$56 = new QName("", "strike");
        KERN$58 = new QName("", "kern");
        CAP$60 = new QName("", "cap");
        SPC$62 = new QName("", "spc");
        NORMALIZEH$64 = new QName("", "normalizeH");
        BASELINE$66 = new QName("", "baseline");
        NOPROOF$68 = new QName("", "noProof");
        DIRTY$70 = new QName("", "dirty");
        ERR$72 = new QName("", "err");
        SMTCLEAN$74 = new QName("", "smtClean");
        SMTID$76 = new QName("", "smtId");
        BMK$78 = new QName("", "bmk");
    }
}
