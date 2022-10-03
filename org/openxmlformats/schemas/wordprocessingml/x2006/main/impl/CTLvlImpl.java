package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLongHexNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.apache.xmlbeans.SimpleValue;
import java.math.BigInteger;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvlLegacy;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLevelText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLevelSuffix;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumFmt;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLvlImpl extends XmlComplexContentImpl implements CTLvl
{
    private static final long serialVersionUID = 1L;
    private static final QName START$0;
    private static final QName NUMFMT$2;
    private static final QName LVLRESTART$4;
    private static final QName PSTYLE$6;
    private static final QName ISLGL$8;
    private static final QName SUFF$10;
    private static final QName LVLTEXT$12;
    private static final QName LVLPICBULLETID$14;
    private static final QName LEGACY$16;
    private static final QName LVLJC$18;
    private static final QName PPR$20;
    private static final QName RPR$22;
    private static final QName ILVL$24;
    private static final QName TPLC$26;
    private static final QName TENTATIVE$28;
    
    public CTLvlImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTDecimalNumber getStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTLvlImpl.START$0, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLvlImpl.START$0) != 0;
        }
    }
    
    public void setStart(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTLvlImpl.START$0, 0, (short)1);
    }
    
    public CTDecimalNumber addNewStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTLvlImpl.START$0);
        }
    }
    
    public void unsetStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLvlImpl.START$0, 0);
        }
    }
    
    public CTNumFmt getNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumFmt ctNumFmt = (CTNumFmt)this.get_store().find_element_user(CTLvlImpl.NUMFMT$2, 0);
            if (ctNumFmt == null) {
                return null;
            }
            return ctNumFmt;
        }
    }
    
    public boolean isSetNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLvlImpl.NUMFMT$2) != 0;
        }
    }
    
    public void setNumFmt(final CTNumFmt ctNumFmt) {
        this.generatedSetterHelperImpl((XmlObject)ctNumFmt, CTLvlImpl.NUMFMT$2, 0, (short)1);
    }
    
    public CTNumFmt addNewNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumFmt)this.get_store().add_element_user(CTLvlImpl.NUMFMT$2);
        }
    }
    
    public void unsetNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLvlImpl.NUMFMT$2, 0);
        }
    }
    
    public CTDecimalNumber getLvlRestart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTLvlImpl.LVLRESTART$4, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetLvlRestart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLvlImpl.LVLRESTART$4) != 0;
        }
    }
    
    public void setLvlRestart(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTLvlImpl.LVLRESTART$4, 0, (short)1);
    }
    
    public CTDecimalNumber addNewLvlRestart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTLvlImpl.LVLRESTART$4);
        }
    }
    
    public void unsetLvlRestart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLvlImpl.LVLRESTART$4, 0);
        }
    }
    
    public CTString getPStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTLvlImpl.PSTYLE$6, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetPStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLvlImpl.PSTYLE$6) != 0;
        }
    }
    
    public void setPStyle(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTLvlImpl.PSTYLE$6, 0, (short)1);
    }
    
    public CTString addNewPStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTLvlImpl.PSTYLE$6);
        }
    }
    
    public void unsetPStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLvlImpl.PSTYLE$6, 0);
        }
    }
    
    public CTOnOff getIsLgl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTLvlImpl.ISLGL$8, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetIsLgl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLvlImpl.ISLGL$8) != 0;
        }
    }
    
    public void setIsLgl(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTLvlImpl.ISLGL$8, 0, (short)1);
    }
    
    public CTOnOff addNewIsLgl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTLvlImpl.ISLGL$8);
        }
    }
    
    public void unsetIsLgl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLvlImpl.ISLGL$8, 0);
        }
    }
    
    public CTLevelSuffix getSuff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLevelSuffix ctLevelSuffix = (CTLevelSuffix)this.get_store().find_element_user(CTLvlImpl.SUFF$10, 0);
            if (ctLevelSuffix == null) {
                return null;
            }
            return ctLevelSuffix;
        }
    }
    
    public boolean isSetSuff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLvlImpl.SUFF$10) != 0;
        }
    }
    
    public void setSuff(final CTLevelSuffix ctLevelSuffix) {
        this.generatedSetterHelperImpl((XmlObject)ctLevelSuffix, CTLvlImpl.SUFF$10, 0, (short)1);
    }
    
    public CTLevelSuffix addNewSuff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLevelSuffix)this.get_store().add_element_user(CTLvlImpl.SUFF$10);
        }
    }
    
    public void unsetSuff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLvlImpl.SUFF$10, 0);
        }
    }
    
    public CTLevelText getLvlText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLevelText ctLevelText = (CTLevelText)this.get_store().find_element_user(CTLvlImpl.LVLTEXT$12, 0);
            if (ctLevelText == null) {
                return null;
            }
            return ctLevelText;
        }
    }
    
    public boolean isSetLvlText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLvlImpl.LVLTEXT$12) != 0;
        }
    }
    
    public void setLvlText(final CTLevelText ctLevelText) {
        this.generatedSetterHelperImpl((XmlObject)ctLevelText, CTLvlImpl.LVLTEXT$12, 0, (short)1);
    }
    
    public CTLevelText addNewLvlText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLevelText)this.get_store().add_element_user(CTLvlImpl.LVLTEXT$12);
        }
    }
    
    public void unsetLvlText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLvlImpl.LVLTEXT$12, 0);
        }
    }
    
    public CTDecimalNumber getLvlPicBulletId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTLvlImpl.LVLPICBULLETID$14, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetLvlPicBulletId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLvlImpl.LVLPICBULLETID$14) != 0;
        }
    }
    
    public void setLvlPicBulletId(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTLvlImpl.LVLPICBULLETID$14, 0, (short)1);
    }
    
    public CTDecimalNumber addNewLvlPicBulletId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTLvlImpl.LVLPICBULLETID$14);
        }
    }
    
    public void unsetLvlPicBulletId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLvlImpl.LVLPICBULLETID$14, 0);
        }
    }
    
    public CTLvlLegacy getLegacy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLvlLegacy ctLvlLegacy = (CTLvlLegacy)this.get_store().find_element_user(CTLvlImpl.LEGACY$16, 0);
            if (ctLvlLegacy == null) {
                return null;
            }
            return ctLvlLegacy;
        }
    }
    
    public boolean isSetLegacy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLvlImpl.LEGACY$16) != 0;
        }
    }
    
    public void setLegacy(final CTLvlLegacy ctLvlLegacy) {
        this.generatedSetterHelperImpl((XmlObject)ctLvlLegacy, CTLvlImpl.LEGACY$16, 0, (short)1);
    }
    
    public CTLvlLegacy addNewLegacy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLvlLegacy)this.get_store().add_element_user(CTLvlImpl.LEGACY$16);
        }
    }
    
    public void unsetLegacy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLvlImpl.LEGACY$16, 0);
        }
    }
    
    public CTJc getLvlJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTJc ctJc = (CTJc)this.get_store().find_element_user(CTLvlImpl.LVLJC$18, 0);
            if (ctJc == null) {
                return null;
            }
            return ctJc;
        }
    }
    
    public boolean isSetLvlJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLvlImpl.LVLJC$18) != 0;
        }
    }
    
    public void setLvlJc(final CTJc ctJc) {
        this.generatedSetterHelperImpl((XmlObject)ctJc, CTLvlImpl.LVLJC$18, 0, (short)1);
    }
    
    public CTJc addNewLvlJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTJc)this.get_store().add_element_user(CTLvlImpl.LVLJC$18);
        }
    }
    
    public void unsetLvlJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLvlImpl.LVLJC$18, 0);
        }
    }
    
    public CTPPr getPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPPr ctpPr = (CTPPr)this.get_store().find_element_user(CTLvlImpl.PPR$20, 0);
            if (ctpPr == null) {
                return null;
            }
            return ctpPr;
        }
    }
    
    public boolean isSetPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLvlImpl.PPR$20) != 0;
        }
    }
    
    public void setPPr(final CTPPr ctpPr) {
        this.generatedSetterHelperImpl((XmlObject)ctpPr, CTLvlImpl.PPR$20, 0, (short)1);
    }
    
    public CTPPr addNewPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPPr)this.get_store().add_element_user(CTLvlImpl.PPR$20);
        }
    }
    
    public void unsetPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLvlImpl.PPR$20, 0);
        }
    }
    
    public CTRPr getRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRPr ctrPr = (CTRPr)this.get_store().find_element_user(CTLvlImpl.RPR$22, 0);
            if (ctrPr == null) {
                return null;
            }
            return ctrPr;
        }
    }
    
    public boolean isSetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLvlImpl.RPR$22) != 0;
        }
    }
    
    public void setRPr(final CTRPr ctrPr) {
        this.generatedSetterHelperImpl((XmlObject)ctrPr, CTLvlImpl.RPR$22, 0, (short)1);
    }
    
    public CTRPr addNewRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRPr)this.get_store().add_element_user(CTLvlImpl.RPR$22);
        }
    }
    
    public void unsetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLvlImpl.RPR$22, 0);
        }
    }
    
    public BigInteger getIlvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLvlImpl.ILVL$24);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetIlvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTLvlImpl.ILVL$24);
        }
    }
    
    public void setIlvl(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLvlImpl.ILVL$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLvlImpl.ILVL$24);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetIlvl(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTLvlImpl.ILVL$24);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTLvlImpl.ILVL$24);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    public byte[] getTplc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLvlImpl.TPLC$26);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STLongHexNumber xgetTplc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLongHexNumber)this.get_store().find_attribute_user(CTLvlImpl.TPLC$26);
        }
    }
    
    public boolean isSetTplc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLvlImpl.TPLC$26) != null;
        }
    }
    
    public void setTplc(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLvlImpl.TPLC$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLvlImpl.TPLC$26);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetTplc(final STLongHexNumber stLongHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLongHexNumber stLongHexNumber2 = (STLongHexNumber)this.get_store().find_attribute_user(CTLvlImpl.TPLC$26);
            if (stLongHexNumber2 == null) {
                stLongHexNumber2 = (STLongHexNumber)this.get_store().add_attribute_user(CTLvlImpl.TPLC$26);
            }
            stLongHexNumber2.set((XmlObject)stLongHexNumber);
        }
    }
    
    public void unsetTplc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLvlImpl.TPLC$26);
        }
    }
    
    public STOnOff.Enum getTentative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLvlImpl.TENTATIVE$28);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetTentative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTLvlImpl.TENTATIVE$28);
        }
    }
    
    public boolean isSetTentative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLvlImpl.TENTATIVE$28) != null;
        }
    }
    
    public void setTentative(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLvlImpl.TENTATIVE$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLvlImpl.TENTATIVE$28);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetTentative(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTLvlImpl.TENTATIVE$28);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTLvlImpl.TENTATIVE$28);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetTentative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLvlImpl.TENTATIVE$28);
        }
    }
    
    static {
        START$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "start");
        NUMFMT$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "numFmt");
        LVLRESTART$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lvlRestart");
        PSTYLE$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pStyle");
        ISLGL$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "isLgl");
        SUFF$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "suff");
        LVLTEXT$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lvlText");
        LVLPICBULLETID$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lvlPicBulletId");
        LEGACY$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "legacy");
        LVLJC$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lvlJc");
        PPR$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pPr");
        RPR$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rPr");
        ILVL$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ilvl");
        TPLC$26 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tplc");
        TENTATIVE$28 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tentative");
    }
}
