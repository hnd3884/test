package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STTextFontAlignType;
import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate32;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAlignType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextIndent;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextIndentLevelType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextMargin;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStopList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBlipBullet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharBullet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextAutonumberBullet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNoBullet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletTypefaceFollowText;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletSizePoint;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletSizePercent;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletSizeFollowText;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletColorFollowText;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacing;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextParagraphPropertiesImpl extends XmlComplexContentImpl implements CTTextParagraphProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName LNSPC$0;
    private static final QName SPCBEF$2;
    private static final QName SPCAFT$4;
    private static final QName BUCLRTX$6;
    private static final QName BUCLR$8;
    private static final QName BUSZTX$10;
    private static final QName BUSZPCT$12;
    private static final QName BUSZPTS$14;
    private static final QName BUFONTTX$16;
    private static final QName BUFONT$18;
    private static final QName BUNONE$20;
    private static final QName BUAUTONUM$22;
    private static final QName BUCHAR$24;
    private static final QName BUBLIP$26;
    private static final QName TABLST$28;
    private static final QName DEFRPR$30;
    private static final QName EXTLST$32;
    private static final QName MARL$34;
    private static final QName MARR$36;
    private static final QName LVL$38;
    private static final QName INDENT$40;
    private static final QName ALGN$42;
    private static final QName DEFTABSZ$44;
    private static final QName RTL$46;
    private static final QName EALNBRK$48;
    private static final QName FONTALGN$50;
    private static final QName LATINLNBRK$52;
    private static final QName HANGINGPUNCT$54;
    
    public CTTextParagraphPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTextSpacing getLnSpc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextSpacing ctTextSpacing = (CTTextSpacing)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.LNSPC$0, 0);
            if (ctTextSpacing == null) {
                return null;
            }
            return ctTextSpacing;
        }
    }
    
    public boolean isSetLnSpc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.LNSPC$0) != 0;
        }
    }
    
    public void setLnSpc(final CTTextSpacing ctTextSpacing) {
        this.generatedSetterHelperImpl((XmlObject)ctTextSpacing, CTTextParagraphPropertiesImpl.LNSPC$0, 0, (short)1);
    }
    
    public CTTextSpacing addNewLnSpc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextSpacing)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.LNSPC$0);
        }
    }
    
    public void unsetLnSpc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.LNSPC$0, 0);
        }
    }
    
    public CTTextSpacing getSpcBef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextSpacing ctTextSpacing = (CTTextSpacing)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.SPCBEF$2, 0);
            if (ctTextSpacing == null) {
                return null;
            }
            return ctTextSpacing;
        }
    }
    
    public boolean isSetSpcBef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.SPCBEF$2) != 0;
        }
    }
    
    public void setSpcBef(final CTTextSpacing ctTextSpacing) {
        this.generatedSetterHelperImpl((XmlObject)ctTextSpacing, CTTextParagraphPropertiesImpl.SPCBEF$2, 0, (short)1);
    }
    
    public CTTextSpacing addNewSpcBef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextSpacing)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.SPCBEF$2);
        }
    }
    
    public void unsetSpcBef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.SPCBEF$2, 0);
        }
    }
    
    public CTTextSpacing getSpcAft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextSpacing ctTextSpacing = (CTTextSpacing)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.SPCAFT$4, 0);
            if (ctTextSpacing == null) {
                return null;
            }
            return ctTextSpacing;
        }
    }
    
    public boolean isSetSpcAft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.SPCAFT$4) != 0;
        }
    }
    
    public void setSpcAft(final CTTextSpacing ctTextSpacing) {
        this.generatedSetterHelperImpl((XmlObject)ctTextSpacing, CTTextParagraphPropertiesImpl.SPCAFT$4, 0, (short)1);
    }
    
    public CTTextSpacing addNewSpcAft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextSpacing)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.SPCAFT$4);
        }
    }
    
    public void unsetSpcAft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.SPCAFT$4, 0);
        }
    }
    
    public CTTextBulletColorFollowText getBuClrTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextBulletColorFollowText ctTextBulletColorFollowText = (CTTextBulletColorFollowText)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.BUCLRTX$6, 0);
            if (ctTextBulletColorFollowText == null) {
                return null;
            }
            return ctTextBulletColorFollowText;
        }
    }
    
    public boolean isSetBuClrTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.BUCLRTX$6) != 0;
        }
    }
    
    public void setBuClrTx(final CTTextBulletColorFollowText ctTextBulletColorFollowText) {
        this.generatedSetterHelperImpl((XmlObject)ctTextBulletColorFollowText, CTTextParagraphPropertiesImpl.BUCLRTX$6, 0, (short)1);
    }
    
    public CTTextBulletColorFollowText addNewBuClrTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextBulletColorFollowText)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.BUCLRTX$6);
        }
    }
    
    public void unsetBuClrTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.BUCLRTX$6, 0);
        }
    }
    
    public CTColor getBuClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.BUCLR$8, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public boolean isSetBuClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.BUCLR$8) != 0;
        }
    }
    
    public void setBuClr(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTTextParagraphPropertiesImpl.BUCLR$8, 0, (short)1);
    }
    
    public CTColor addNewBuClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.BUCLR$8);
        }
    }
    
    public void unsetBuClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.BUCLR$8, 0);
        }
    }
    
    public CTTextBulletSizeFollowText getBuSzTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextBulletSizeFollowText ctTextBulletSizeFollowText = (CTTextBulletSizeFollowText)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.BUSZTX$10, 0);
            if (ctTextBulletSizeFollowText == null) {
                return null;
            }
            return ctTextBulletSizeFollowText;
        }
    }
    
    public boolean isSetBuSzTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.BUSZTX$10) != 0;
        }
    }
    
    public void setBuSzTx(final CTTextBulletSizeFollowText ctTextBulletSizeFollowText) {
        this.generatedSetterHelperImpl((XmlObject)ctTextBulletSizeFollowText, CTTextParagraphPropertiesImpl.BUSZTX$10, 0, (short)1);
    }
    
    public CTTextBulletSizeFollowText addNewBuSzTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextBulletSizeFollowText)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.BUSZTX$10);
        }
    }
    
    public void unsetBuSzTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.BUSZTX$10, 0);
        }
    }
    
    public CTTextBulletSizePercent getBuSzPct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextBulletSizePercent ctTextBulletSizePercent = (CTTextBulletSizePercent)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.BUSZPCT$12, 0);
            if (ctTextBulletSizePercent == null) {
                return null;
            }
            return ctTextBulletSizePercent;
        }
    }
    
    public boolean isSetBuSzPct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.BUSZPCT$12) != 0;
        }
    }
    
    public void setBuSzPct(final CTTextBulletSizePercent ctTextBulletSizePercent) {
        this.generatedSetterHelperImpl((XmlObject)ctTextBulletSizePercent, CTTextParagraphPropertiesImpl.BUSZPCT$12, 0, (short)1);
    }
    
    public CTTextBulletSizePercent addNewBuSzPct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextBulletSizePercent)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.BUSZPCT$12);
        }
    }
    
    public void unsetBuSzPct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.BUSZPCT$12, 0);
        }
    }
    
    public CTTextBulletSizePoint getBuSzPts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextBulletSizePoint ctTextBulletSizePoint = (CTTextBulletSizePoint)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.BUSZPTS$14, 0);
            if (ctTextBulletSizePoint == null) {
                return null;
            }
            return ctTextBulletSizePoint;
        }
    }
    
    public boolean isSetBuSzPts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.BUSZPTS$14) != 0;
        }
    }
    
    public void setBuSzPts(final CTTextBulletSizePoint ctTextBulletSizePoint) {
        this.generatedSetterHelperImpl((XmlObject)ctTextBulletSizePoint, CTTextParagraphPropertiesImpl.BUSZPTS$14, 0, (short)1);
    }
    
    public CTTextBulletSizePoint addNewBuSzPts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextBulletSizePoint)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.BUSZPTS$14);
        }
    }
    
    public void unsetBuSzPts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.BUSZPTS$14, 0);
        }
    }
    
    public CTTextBulletTypefaceFollowText getBuFontTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextBulletTypefaceFollowText ctTextBulletTypefaceFollowText = (CTTextBulletTypefaceFollowText)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.BUFONTTX$16, 0);
            if (ctTextBulletTypefaceFollowText == null) {
                return null;
            }
            return ctTextBulletTypefaceFollowText;
        }
    }
    
    public boolean isSetBuFontTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.BUFONTTX$16) != 0;
        }
    }
    
    public void setBuFontTx(final CTTextBulletTypefaceFollowText ctTextBulletTypefaceFollowText) {
        this.generatedSetterHelperImpl((XmlObject)ctTextBulletTypefaceFollowText, CTTextParagraphPropertiesImpl.BUFONTTX$16, 0, (short)1);
    }
    
    public CTTextBulletTypefaceFollowText addNewBuFontTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextBulletTypefaceFollowText)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.BUFONTTX$16);
        }
    }
    
    public void unsetBuFontTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.BUFONTTX$16, 0);
        }
    }
    
    public CTTextFont getBuFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextFont ctTextFont = (CTTextFont)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.BUFONT$18, 0);
            if (ctTextFont == null) {
                return null;
            }
            return ctTextFont;
        }
    }
    
    public boolean isSetBuFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.BUFONT$18) != 0;
        }
    }
    
    public void setBuFont(final CTTextFont ctTextFont) {
        this.generatedSetterHelperImpl((XmlObject)ctTextFont, CTTextParagraphPropertiesImpl.BUFONT$18, 0, (short)1);
    }
    
    public CTTextFont addNewBuFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextFont)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.BUFONT$18);
        }
    }
    
    public void unsetBuFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.BUFONT$18, 0);
        }
    }
    
    public CTTextNoBullet getBuNone() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextNoBullet ctTextNoBullet = (CTTextNoBullet)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.BUNONE$20, 0);
            if (ctTextNoBullet == null) {
                return null;
            }
            return ctTextNoBullet;
        }
    }
    
    public boolean isSetBuNone() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.BUNONE$20) != 0;
        }
    }
    
    public void setBuNone(final CTTextNoBullet ctTextNoBullet) {
        this.generatedSetterHelperImpl((XmlObject)ctTextNoBullet, CTTextParagraphPropertiesImpl.BUNONE$20, 0, (short)1);
    }
    
    public CTTextNoBullet addNewBuNone() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextNoBullet)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.BUNONE$20);
        }
    }
    
    public void unsetBuNone() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.BUNONE$20, 0);
        }
    }
    
    public CTTextAutonumberBullet getBuAutoNum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextAutonumberBullet ctTextAutonumberBullet = (CTTextAutonumberBullet)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.BUAUTONUM$22, 0);
            if (ctTextAutonumberBullet == null) {
                return null;
            }
            return ctTextAutonumberBullet;
        }
    }
    
    public boolean isSetBuAutoNum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.BUAUTONUM$22) != 0;
        }
    }
    
    public void setBuAutoNum(final CTTextAutonumberBullet ctTextAutonumberBullet) {
        this.generatedSetterHelperImpl((XmlObject)ctTextAutonumberBullet, CTTextParagraphPropertiesImpl.BUAUTONUM$22, 0, (short)1);
    }
    
    public CTTextAutonumberBullet addNewBuAutoNum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextAutonumberBullet)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.BUAUTONUM$22);
        }
    }
    
    public void unsetBuAutoNum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.BUAUTONUM$22, 0);
        }
    }
    
    public CTTextCharBullet getBuChar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextCharBullet ctTextCharBullet = (CTTextCharBullet)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.BUCHAR$24, 0);
            if (ctTextCharBullet == null) {
                return null;
            }
            return ctTextCharBullet;
        }
    }
    
    public boolean isSetBuChar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.BUCHAR$24) != 0;
        }
    }
    
    public void setBuChar(final CTTextCharBullet ctTextCharBullet) {
        this.generatedSetterHelperImpl((XmlObject)ctTextCharBullet, CTTextParagraphPropertiesImpl.BUCHAR$24, 0, (short)1);
    }
    
    public CTTextCharBullet addNewBuChar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextCharBullet)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.BUCHAR$24);
        }
    }
    
    public void unsetBuChar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.BUCHAR$24, 0);
        }
    }
    
    public CTTextBlipBullet getBuBlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextBlipBullet ctTextBlipBullet = (CTTextBlipBullet)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.BUBLIP$26, 0);
            if (ctTextBlipBullet == null) {
                return null;
            }
            return ctTextBlipBullet;
        }
    }
    
    public boolean isSetBuBlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.BUBLIP$26) != 0;
        }
    }
    
    public void setBuBlip(final CTTextBlipBullet ctTextBlipBullet) {
        this.generatedSetterHelperImpl((XmlObject)ctTextBlipBullet, CTTextParagraphPropertiesImpl.BUBLIP$26, 0, (short)1);
    }
    
    public CTTextBlipBullet addNewBuBlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextBlipBullet)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.BUBLIP$26);
        }
    }
    
    public void unsetBuBlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.BUBLIP$26, 0);
        }
    }
    
    public CTTextTabStopList getTabLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextTabStopList list = (CTTextTabStopList)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.TABLST$28, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetTabLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.TABLST$28) != 0;
        }
    }
    
    public void setTabLst(final CTTextTabStopList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTextParagraphPropertiesImpl.TABLST$28, 0, (short)1);
    }
    
    public CTTextTabStopList addNewTabLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextTabStopList)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.TABLST$28);
        }
    }
    
    public void unsetTabLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.TABLST$28, 0);
        }
    }
    
    public CTTextCharacterProperties getDefRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextCharacterProperties ctTextCharacterProperties = (CTTextCharacterProperties)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.DEFRPR$30, 0);
            if (ctTextCharacterProperties == null) {
                return null;
            }
            return ctTextCharacterProperties;
        }
    }
    
    public boolean isSetDefRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.DEFRPR$30) != 0;
        }
    }
    
    public void setDefRPr(final CTTextCharacterProperties ctTextCharacterProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextCharacterProperties, CTTextParagraphPropertiesImpl.DEFRPR$30, 0, (short)1);
    }
    
    public CTTextCharacterProperties addNewDefRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextCharacterProperties)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.DEFRPR$30);
        }
    }
    
    public void unsetDefRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.DEFRPR$30, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTTextParagraphPropertiesImpl.EXTLST$32, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphPropertiesImpl.EXTLST$32) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTextParagraphPropertiesImpl.EXTLST$32, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTTextParagraphPropertiesImpl.EXTLST$32);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphPropertiesImpl.EXTLST$32, 0);
        }
    }
    
    public int getMarL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.MARL$34);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextMargin xgetMarL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextMargin)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.MARL$34);
        }
    }
    
    public boolean isSetMarL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.MARL$34) != null;
        }
    }
    
    public void setMarL(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.MARL$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.MARL$34);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetMarL(final STTextMargin stTextMargin) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextMargin stTextMargin2 = (STTextMargin)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.MARL$34);
            if (stTextMargin2 == null) {
                stTextMargin2 = (STTextMargin)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.MARL$34);
            }
            stTextMargin2.set((XmlObject)stTextMargin);
        }
    }
    
    public void unsetMarL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextParagraphPropertiesImpl.MARL$34);
        }
    }
    
    public int getMarR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.MARR$36);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextMargin xgetMarR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextMargin)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.MARR$36);
        }
    }
    
    public boolean isSetMarR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.MARR$36) != null;
        }
    }
    
    public void setMarR(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.MARR$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.MARR$36);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetMarR(final STTextMargin stTextMargin) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextMargin stTextMargin2 = (STTextMargin)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.MARR$36);
            if (stTextMargin2 == null) {
                stTextMargin2 = (STTextMargin)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.MARR$36);
            }
            stTextMargin2.set((XmlObject)stTextMargin);
        }
    }
    
    public void unsetMarR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextParagraphPropertiesImpl.MARR$36);
        }
    }
    
    public int getLvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.LVL$38);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextIndentLevelType xgetLvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextIndentLevelType)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.LVL$38);
        }
    }
    
    public boolean isSetLvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.LVL$38) != null;
        }
    }
    
    public void setLvl(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.LVL$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.LVL$38);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetLvl(final STTextIndentLevelType stTextIndentLevelType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextIndentLevelType stTextIndentLevelType2 = (STTextIndentLevelType)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.LVL$38);
            if (stTextIndentLevelType2 == null) {
                stTextIndentLevelType2 = (STTextIndentLevelType)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.LVL$38);
            }
            stTextIndentLevelType2.set((XmlObject)stTextIndentLevelType);
        }
    }
    
    public void unsetLvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextParagraphPropertiesImpl.LVL$38);
        }
    }
    
    public int getIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.INDENT$40);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextIndent xgetIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextIndent)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.INDENT$40);
        }
    }
    
    public boolean isSetIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.INDENT$40) != null;
        }
    }
    
    public void setIndent(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.INDENT$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.INDENT$40);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetIndent(final STTextIndent stTextIndent) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextIndent stTextIndent2 = (STTextIndent)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.INDENT$40);
            if (stTextIndent2 == null) {
                stTextIndent2 = (STTextIndent)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.INDENT$40);
            }
            stTextIndent2.set((XmlObject)stTextIndent);
        }
    }
    
    public void unsetIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextParagraphPropertiesImpl.INDENT$40);
        }
    }
    
    public STTextAlignType.Enum getAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.ALGN$42);
            if (simpleValue == null) {
                return null;
            }
            return (STTextAlignType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextAlignType xgetAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextAlignType)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.ALGN$42);
        }
    }
    
    public boolean isSetAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.ALGN$42) != null;
        }
    }
    
    public void setAlgn(final STTextAlignType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.ALGN$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.ALGN$42);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAlgn(final STTextAlignType stTextAlignType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextAlignType stTextAlignType2 = (STTextAlignType)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.ALGN$42);
            if (stTextAlignType2 == null) {
                stTextAlignType2 = (STTextAlignType)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.ALGN$42);
            }
            stTextAlignType2.set((XmlObject)stTextAlignType);
        }
    }
    
    public void unsetAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextParagraphPropertiesImpl.ALGN$42);
        }
    }
    
    public int getDefTabSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.DEFTABSZ$44);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STCoordinate32 xgetDefTabSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCoordinate32)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.DEFTABSZ$44);
        }
    }
    
    public boolean isSetDefTabSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.DEFTABSZ$44) != null;
        }
    }
    
    public void setDefTabSz(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.DEFTABSZ$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.DEFTABSZ$44);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetDefTabSz(final STCoordinate32 stCoordinate32) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate32 stCoordinate33 = (STCoordinate32)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.DEFTABSZ$44);
            if (stCoordinate33 == null) {
                stCoordinate33 = (STCoordinate32)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.DEFTABSZ$44);
            }
            stCoordinate33.set((XmlObject)stCoordinate32);
        }
    }
    
    public void unsetDefTabSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextParagraphPropertiesImpl.DEFTABSZ$44);
        }
    }
    
    public boolean getRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.RTL$46);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.RTL$46);
        }
    }
    
    public boolean isSetRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.RTL$46) != null;
        }
    }
    
    public void setRtl(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.RTL$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.RTL$46);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetRtl(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.RTL$46);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.RTL$46);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextParagraphPropertiesImpl.RTL$46);
        }
    }
    
    public boolean getEaLnBrk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.EALNBRK$48);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetEaLnBrk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.EALNBRK$48);
        }
    }
    
    public boolean isSetEaLnBrk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.EALNBRK$48) != null;
        }
    }
    
    public void setEaLnBrk(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.EALNBRK$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.EALNBRK$48);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetEaLnBrk(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.EALNBRK$48);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.EALNBRK$48);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetEaLnBrk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextParagraphPropertiesImpl.EALNBRK$48);
        }
    }
    
    public STTextFontAlignType.Enum getFontAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.FONTALGN$50);
            if (simpleValue == null) {
                return null;
            }
            return (STTextFontAlignType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextFontAlignType xgetFontAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextFontAlignType)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.FONTALGN$50);
        }
    }
    
    public boolean isSetFontAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.FONTALGN$50) != null;
        }
    }
    
    public void setFontAlgn(final STTextFontAlignType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.FONTALGN$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.FONTALGN$50);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFontAlgn(final STTextFontAlignType stTextFontAlignType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextFontAlignType stTextFontAlignType2 = (STTextFontAlignType)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.FONTALGN$50);
            if (stTextFontAlignType2 == null) {
                stTextFontAlignType2 = (STTextFontAlignType)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.FONTALGN$50);
            }
            stTextFontAlignType2.set((XmlObject)stTextFontAlignType);
        }
    }
    
    public void unsetFontAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextParagraphPropertiesImpl.FONTALGN$50);
        }
    }
    
    public boolean getLatinLnBrk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.LATINLNBRK$52);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetLatinLnBrk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.LATINLNBRK$52);
        }
    }
    
    public boolean isSetLatinLnBrk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.LATINLNBRK$52) != null;
        }
    }
    
    public void setLatinLnBrk(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.LATINLNBRK$52);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.LATINLNBRK$52);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetLatinLnBrk(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.LATINLNBRK$52);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.LATINLNBRK$52);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetLatinLnBrk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextParagraphPropertiesImpl.LATINLNBRK$52);
        }
    }
    
    public boolean getHangingPunct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.HANGINGPUNCT$54);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHangingPunct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.HANGINGPUNCT$54);
        }
    }
    
    public boolean isSetHangingPunct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.HANGINGPUNCT$54) != null;
        }
    }
    
    public void setHangingPunct(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.HANGINGPUNCT$54);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.HANGINGPUNCT$54);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHangingPunct(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextParagraphPropertiesImpl.HANGINGPUNCT$54);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextParagraphPropertiesImpl.HANGINGPUNCT$54);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHangingPunct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextParagraphPropertiesImpl.HANGINGPUNCT$54);
        }
    }
    
    static {
        LNSPC$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lnSpc");
        SPCBEF$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "spcBef");
        SPCAFT$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "spcAft");
        BUCLRTX$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "buClrTx");
        BUCLR$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "buClr");
        BUSZTX$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "buSzTx");
        BUSZPCT$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "buSzPct");
        BUSZPTS$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "buSzPts");
        BUFONTTX$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "buFontTx");
        BUFONT$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "buFont");
        BUNONE$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "buNone");
        BUAUTONUM$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "buAutoNum");
        BUCHAR$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "buChar");
        BUBLIP$26 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "buBlip");
        TABLST$28 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tabLst");
        DEFRPR$30 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "defRPr");
        EXTLST$32 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        MARL$34 = new QName("", "marL");
        MARR$36 = new QName("", "marR");
        LVL$38 = new QName("", "lvl");
        INDENT$40 = new QName("", "indent");
        ALGN$42 = new QName("", "algn");
        DEFTABSZ$44 = new QName("", "defTabSz");
        RTL$46 = new QName("", "rtl");
        EALNBRK$48 = new QName("", "eaLnBrk");
        FONTALGN$50 = new QName("", "fontAlgn");
        LATINLNBRK$52 = new QName("", "latinLnBrk");
        HANGINGPUNCT$54 = new QName("", "hangingPunct");
    }
}
