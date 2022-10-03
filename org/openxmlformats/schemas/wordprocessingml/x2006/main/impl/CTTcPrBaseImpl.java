package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextDirection;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCnf;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPrBase;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTcPrBaseImpl extends XmlComplexContentImpl implements CTTcPrBase
{
    private static final long serialVersionUID = 1L;
    private static final QName CNFSTYLE$0;
    private static final QName TCW$2;
    private static final QName GRIDSPAN$4;
    private static final QName HMERGE$6;
    private static final QName VMERGE$8;
    private static final QName TCBORDERS$10;
    private static final QName SHD$12;
    private static final QName NOWRAP$14;
    private static final QName TCMAR$16;
    private static final QName TEXTDIRECTION$18;
    private static final QName TCFITTEXT$20;
    private static final QName VALIGN$22;
    private static final QName HIDEMARK$24;
    
    public CTTcPrBaseImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTCnf getCnfStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCnf ctCnf = (CTCnf)this.get_store().find_element_user(CTTcPrBaseImpl.CNFSTYLE$0, 0);
            if (ctCnf == null) {
                return null;
            }
            return ctCnf;
        }
    }
    
    public boolean isSetCnfStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrBaseImpl.CNFSTYLE$0) != 0;
        }
    }
    
    public void setCnfStyle(final CTCnf ctCnf) {
        this.generatedSetterHelperImpl((XmlObject)ctCnf, CTTcPrBaseImpl.CNFSTYLE$0, 0, (short)1);
    }
    
    public CTCnf addNewCnfStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCnf)this.get_store().add_element_user(CTTcPrBaseImpl.CNFSTYLE$0);
        }
    }
    
    public void unsetCnfStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrBaseImpl.CNFSTYLE$0, 0);
        }
    }
    
    public CTTblWidth getTcW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblWidth ctTblWidth = (CTTblWidth)this.get_store().find_element_user(CTTcPrBaseImpl.TCW$2, 0);
            if (ctTblWidth == null) {
                return null;
            }
            return ctTblWidth;
        }
    }
    
    public boolean isSetTcW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrBaseImpl.TCW$2) != 0;
        }
    }
    
    public void setTcW(final CTTblWidth ctTblWidth) {
        this.generatedSetterHelperImpl((XmlObject)ctTblWidth, CTTcPrBaseImpl.TCW$2, 0, (short)1);
    }
    
    public CTTblWidth addNewTcW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().add_element_user(CTTcPrBaseImpl.TCW$2);
        }
    }
    
    public void unsetTcW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrBaseImpl.TCW$2, 0);
        }
    }
    
    public CTDecimalNumber getGridSpan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTTcPrBaseImpl.GRIDSPAN$4, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetGridSpan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrBaseImpl.GRIDSPAN$4) != 0;
        }
    }
    
    public void setGridSpan(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTTcPrBaseImpl.GRIDSPAN$4, 0, (short)1);
    }
    
    public CTDecimalNumber addNewGridSpan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTTcPrBaseImpl.GRIDSPAN$4);
        }
    }
    
    public void unsetGridSpan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrBaseImpl.GRIDSPAN$4, 0);
        }
    }
    
    public CTHMerge getHMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHMerge cthMerge = (CTHMerge)this.get_store().find_element_user(CTTcPrBaseImpl.HMERGE$6, 0);
            if (cthMerge == null) {
                return null;
            }
            return cthMerge;
        }
    }
    
    public boolean isSetHMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrBaseImpl.HMERGE$6) != 0;
        }
    }
    
    public void setHMerge(final CTHMerge cthMerge) {
        this.generatedSetterHelperImpl((XmlObject)cthMerge, CTTcPrBaseImpl.HMERGE$6, 0, (short)1);
    }
    
    public CTHMerge addNewHMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHMerge)this.get_store().add_element_user(CTTcPrBaseImpl.HMERGE$6);
        }
    }
    
    public void unsetHMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrBaseImpl.HMERGE$6, 0);
        }
    }
    
    public CTVMerge getVMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVMerge ctvMerge = (CTVMerge)this.get_store().find_element_user(CTTcPrBaseImpl.VMERGE$8, 0);
            if (ctvMerge == null) {
                return null;
            }
            return ctvMerge;
        }
    }
    
    public boolean isSetVMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrBaseImpl.VMERGE$8) != 0;
        }
    }
    
    public void setVMerge(final CTVMerge ctvMerge) {
        this.generatedSetterHelperImpl((XmlObject)ctvMerge, CTTcPrBaseImpl.VMERGE$8, 0, (short)1);
    }
    
    public CTVMerge addNewVMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVMerge)this.get_store().add_element_user(CTTcPrBaseImpl.VMERGE$8);
        }
    }
    
    public void unsetVMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrBaseImpl.VMERGE$8, 0);
        }
    }
    
    public CTTcBorders getTcBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTcBorders ctTcBorders = (CTTcBorders)this.get_store().find_element_user(CTTcPrBaseImpl.TCBORDERS$10, 0);
            if (ctTcBorders == null) {
                return null;
            }
            return ctTcBorders;
        }
    }
    
    public boolean isSetTcBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrBaseImpl.TCBORDERS$10) != 0;
        }
    }
    
    public void setTcBorders(final CTTcBorders ctTcBorders) {
        this.generatedSetterHelperImpl((XmlObject)ctTcBorders, CTTcPrBaseImpl.TCBORDERS$10, 0, (short)1);
    }
    
    public CTTcBorders addNewTcBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTcBorders)this.get_store().add_element_user(CTTcPrBaseImpl.TCBORDERS$10);
        }
    }
    
    public void unsetTcBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrBaseImpl.TCBORDERS$10, 0);
        }
    }
    
    public CTShd getShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShd ctShd = (CTShd)this.get_store().find_element_user(CTTcPrBaseImpl.SHD$12, 0);
            if (ctShd == null) {
                return null;
            }
            return ctShd;
        }
    }
    
    public boolean isSetShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrBaseImpl.SHD$12) != 0;
        }
    }
    
    public void setShd(final CTShd ctShd) {
        this.generatedSetterHelperImpl((XmlObject)ctShd, CTTcPrBaseImpl.SHD$12, 0, (short)1);
    }
    
    public CTShd addNewShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShd)this.get_store().add_element_user(CTTcPrBaseImpl.SHD$12);
        }
    }
    
    public void unsetShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrBaseImpl.SHD$12, 0);
        }
    }
    
    public CTOnOff getNoWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTTcPrBaseImpl.NOWRAP$14, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetNoWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrBaseImpl.NOWRAP$14) != 0;
        }
    }
    
    public void setNoWrap(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTTcPrBaseImpl.NOWRAP$14, 0, (short)1);
    }
    
    public CTOnOff addNewNoWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTTcPrBaseImpl.NOWRAP$14);
        }
    }
    
    public void unsetNoWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrBaseImpl.NOWRAP$14, 0);
        }
    }
    
    public CTTcMar getTcMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTcMar ctTcMar = (CTTcMar)this.get_store().find_element_user(CTTcPrBaseImpl.TCMAR$16, 0);
            if (ctTcMar == null) {
                return null;
            }
            return ctTcMar;
        }
    }
    
    public boolean isSetTcMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrBaseImpl.TCMAR$16) != 0;
        }
    }
    
    public void setTcMar(final CTTcMar ctTcMar) {
        this.generatedSetterHelperImpl((XmlObject)ctTcMar, CTTcPrBaseImpl.TCMAR$16, 0, (short)1);
    }
    
    public CTTcMar addNewTcMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTcMar)this.get_store().add_element_user(CTTcPrBaseImpl.TCMAR$16);
        }
    }
    
    public void unsetTcMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrBaseImpl.TCMAR$16, 0);
        }
    }
    
    public CTTextDirection getTextDirection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextDirection ctTextDirection = (CTTextDirection)this.get_store().find_element_user(CTTcPrBaseImpl.TEXTDIRECTION$18, 0);
            if (ctTextDirection == null) {
                return null;
            }
            return ctTextDirection;
        }
    }
    
    public boolean isSetTextDirection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrBaseImpl.TEXTDIRECTION$18) != 0;
        }
    }
    
    public void setTextDirection(final CTTextDirection ctTextDirection) {
        this.generatedSetterHelperImpl((XmlObject)ctTextDirection, CTTcPrBaseImpl.TEXTDIRECTION$18, 0, (short)1);
    }
    
    public CTTextDirection addNewTextDirection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextDirection)this.get_store().add_element_user(CTTcPrBaseImpl.TEXTDIRECTION$18);
        }
    }
    
    public void unsetTextDirection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrBaseImpl.TEXTDIRECTION$18, 0);
        }
    }
    
    public CTOnOff getTcFitText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTTcPrBaseImpl.TCFITTEXT$20, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetTcFitText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrBaseImpl.TCFITTEXT$20) != 0;
        }
    }
    
    public void setTcFitText(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTTcPrBaseImpl.TCFITTEXT$20, 0, (short)1);
    }
    
    public CTOnOff addNewTcFitText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTTcPrBaseImpl.TCFITTEXT$20);
        }
    }
    
    public void unsetTcFitText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrBaseImpl.TCFITTEXT$20, 0);
        }
    }
    
    public CTVerticalJc getVAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVerticalJc ctVerticalJc = (CTVerticalJc)this.get_store().find_element_user(CTTcPrBaseImpl.VALIGN$22, 0);
            if (ctVerticalJc == null) {
                return null;
            }
            return ctVerticalJc;
        }
    }
    
    public boolean isSetVAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrBaseImpl.VALIGN$22) != 0;
        }
    }
    
    public void setVAlign(final CTVerticalJc ctVerticalJc) {
        this.generatedSetterHelperImpl((XmlObject)ctVerticalJc, CTTcPrBaseImpl.VALIGN$22, 0, (short)1);
    }
    
    public CTVerticalJc addNewVAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVerticalJc)this.get_store().add_element_user(CTTcPrBaseImpl.VALIGN$22);
        }
    }
    
    public void unsetVAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrBaseImpl.VALIGN$22, 0);
        }
    }
    
    public CTOnOff getHideMark() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTTcPrBaseImpl.HIDEMARK$24, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetHideMark() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrBaseImpl.HIDEMARK$24) != 0;
        }
    }
    
    public void setHideMark(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTTcPrBaseImpl.HIDEMARK$24, 0, (short)1);
    }
    
    public CTOnOff addNewHideMark() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTTcPrBaseImpl.HIDEMARK$24);
        }
    }
    
    public void unsetHideMark() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrBaseImpl.HIDEMARK$24, 0);
        }
    }
    
    static {
        CNFSTYLE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cnfStyle");
        TCW$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tcW");
        GRIDSPAN$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "gridSpan");
        HMERGE$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hMerge");
        VMERGE$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "vMerge");
        TCBORDERS$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tcBorders");
        SHD$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "shd");
        NOWRAP$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "noWrap");
        TCMAR$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tcMar");
        TEXTDIRECTION$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "textDirection");
        TCFITTEXT$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tcFitText");
        VALIGN$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "vAlign");
        HIDEMARK$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hideMark");
    }
}
