package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShortHexNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblCellMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblOverlap;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPPr;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPrBase;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTblPrBaseImpl extends XmlComplexContentImpl implements CTTblPrBase
{
    private static final long serialVersionUID = 1L;
    private static final QName TBLSTYLE$0;
    private static final QName TBLPPR$2;
    private static final QName TBLOVERLAP$4;
    private static final QName BIDIVISUAL$6;
    private static final QName TBLSTYLEROWBANDSIZE$8;
    private static final QName TBLSTYLECOLBANDSIZE$10;
    private static final QName TBLW$12;
    private static final QName JC$14;
    private static final QName TBLCELLSPACING$16;
    private static final QName TBLIND$18;
    private static final QName TBLBORDERS$20;
    private static final QName SHD$22;
    private static final QName TBLLAYOUT$24;
    private static final QName TBLCELLMAR$26;
    private static final QName TBLLOOK$28;
    
    public CTTblPrBaseImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTString getTblStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTTblPrBaseImpl.TBLSTYLE$0, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetTblStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrBaseImpl.TBLSTYLE$0) != 0;
        }
    }
    
    public void setTblStyle(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTTblPrBaseImpl.TBLSTYLE$0, 0, (short)1);
    }
    
    public CTString addNewTblStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTTblPrBaseImpl.TBLSTYLE$0);
        }
    }
    
    public void unsetTblStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrBaseImpl.TBLSTYLE$0, 0);
        }
    }
    
    public CTTblPPr getTblpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblPPr ctTblPPr = (CTTblPPr)this.get_store().find_element_user(CTTblPrBaseImpl.TBLPPR$2, 0);
            if (ctTblPPr == null) {
                return null;
            }
            return ctTblPPr;
        }
    }
    
    public boolean isSetTblpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrBaseImpl.TBLPPR$2) != 0;
        }
    }
    
    public void setTblpPr(final CTTblPPr ctTblPPr) {
        this.generatedSetterHelperImpl((XmlObject)ctTblPPr, CTTblPrBaseImpl.TBLPPR$2, 0, (short)1);
    }
    
    public CTTblPPr addNewTblpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblPPr)this.get_store().add_element_user(CTTblPrBaseImpl.TBLPPR$2);
        }
    }
    
    public void unsetTblpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrBaseImpl.TBLPPR$2, 0);
        }
    }
    
    public CTTblOverlap getTblOverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblOverlap ctTblOverlap = (CTTblOverlap)this.get_store().find_element_user(CTTblPrBaseImpl.TBLOVERLAP$4, 0);
            if (ctTblOverlap == null) {
                return null;
            }
            return ctTblOverlap;
        }
    }
    
    public boolean isSetTblOverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrBaseImpl.TBLOVERLAP$4) != 0;
        }
    }
    
    public void setTblOverlap(final CTTblOverlap ctTblOverlap) {
        this.generatedSetterHelperImpl((XmlObject)ctTblOverlap, CTTblPrBaseImpl.TBLOVERLAP$4, 0, (short)1);
    }
    
    public CTTblOverlap addNewTblOverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblOverlap)this.get_store().add_element_user(CTTblPrBaseImpl.TBLOVERLAP$4);
        }
    }
    
    public void unsetTblOverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrBaseImpl.TBLOVERLAP$4, 0);
        }
    }
    
    public CTOnOff getBidiVisual() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTTblPrBaseImpl.BIDIVISUAL$6, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetBidiVisual() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrBaseImpl.BIDIVISUAL$6) != 0;
        }
    }
    
    public void setBidiVisual(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTTblPrBaseImpl.BIDIVISUAL$6, 0, (short)1);
    }
    
    public CTOnOff addNewBidiVisual() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTTblPrBaseImpl.BIDIVISUAL$6);
        }
    }
    
    public void unsetBidiVisual() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrBaseImpl.BIDIVISUAL$6, 0);
        }
    }
    
    public CTDecimalNumber getTblStyleRowBandSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTTblPrBaseImpl.TBLSTYLEROWBANDSIZE$8, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetTblStyleRowBandSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrBaseImpl.TBLSTYLEROWBANDSIZE$8) != 0;
        }
    }
    
    public void setTblStyleRowBandSize(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTTblPrBaseImpl.TBLSTYLEROWBANDSIZE$8, 0, (short)1);
    }
    
    public CTDecimalNumber addNewTblStyleRowBandSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTTblPrBaseImpl.TBLSTYLEROWBANDSIZE$8);
        }
    }
    
    public void unsetTblStyleRowBandSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrBaseImpl.TBLSTYLEROWBANDSIZE$8, 0);
        }
    }
    
    public CTDecimalNumber getTblStyleColBandSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTTblPrBaseImpl.TBLSTYLECOLBANDSIZE$10, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetTblStyleColBandSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrBaseImpl.TBLSTYLECOLBANDSIZE$10) != 0;
        }
    }
    
    public void setTblStyleColBandSize(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTTblPrBaseImpl.TBLSTYLECOLBANDSIZE$10, 0, (short)1);
    }
    
    public CTDecimalNumber addNewTblStyleColBandSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTTblPrBaseImpl.TBLSTYLECOLBANDSIZE$10);
        }
    }
    
    public void unsetTblStyleColBandSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrBaseImpl.TBLSTYLECOLBANDSIZE$10, 0);
        }
    }
    
    public CTTblWidth getTblW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblWidth ctTblWidth = (CTTblWidth)this.get_store().find_element_user(CTTblPrBaseImpl.TBLW$12, 0);
            if (ctTblWidth == null) {
                return null;
            }
            return ctTblWidth;
        }
    }
    
    public boolean isSetTblW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrBaseImpl.TBLW$12) != 0;
        }
    }
    
    public void setTblW(final CTTblWidth ctTblWidth) {
        this.generatedSetterHelperImpl((XmlObject)ctTblWidth, CTTblPrBaseImpl.TBLW$12, 0, (short)1);
    }
    
    public CTTblWidth addNewTblW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().add_element_user(CTTblPrBaseImpl.TBLW$12);
        }
    }
    
    public void unsetTblW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrBaseImpl.TBLW$12, 0);
        }
    }
    
    public CTJc getJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTJc ctJc = (CTJc)this.get_store().find_element_user(CTTblPrBaseImpl.JC$14, 0);
            if (ctJc == null) {
                return null;
            }
            return ctJc;
        }
    }
    
    public boolean isSetJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrBaseImpl.JC$14) != 0;
        }
    }
    
    public void setJc(final CTJc ctJc) {
        this.generatedSetterHelperImpl((XmlObject)ctJc, CTTblPrBaseImpl.JC$14, 0, (short)1);
    }
    
    public CTJc addNewJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTJc)this.get_store().add_element_user(CTTblPrBaseImpl.JC$14);
        }
    }
    
    public void unsetJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrBaseImpl.JC$14, 0);
        }
    }
    
    public CTTblWidth getTblCellSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblWidth ctTblWidth = (CTTblWidth)this.get_store().find_element_user(CTTblPrBaseImpl.TBLCELLSPACING$16, 0);
            if (ctTblWidth == null) {
                return null;
            }
            return ctTblWidth;
        }
    }
    
    public boolean isSetTblCellSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrBaseImpl.TBLCELLSPACING$16) != 0;
        }
    }
    
    public void setTblCellSpacing(final CTTblWidth ctTblWidth) {
        this.generatedSetterHelperImpl((XmlObject)ctTblWidth, CTTblPrBaseImpl.TBLCELLSPACING$16, 0, (short)1);
    }
    
    public CTTblWidth addNewTblCellSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().add_element_user(CTTblPrBaseImpl.TBLCELLSPACING$16);
        }
    }
    
    public void unsetTblCellSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrBaseImpl.TBLCELLSPACING$16, 0);
        }
    }
    
    public CTTblWidth getTblInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblWidth ctTblWidth = (CTTblWidth)this.get_store().find_element_user(CTTblPrBaseImpl.TBLIND$18, 0);
            if (ctTblWidth == null) {
                return null;
            }
            return ctTblWidth;
        }
    }
    
    public boolean isSetTblInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrBaseImpl.TBLIND$18) != 0;
        }
    }
    
    public void setTblInd(final CTTblWidth ctTblWidth) {
        this.generatedSetterHelperImpl((XmlObject)ctTblWidth, CTTblPrBaseImpl.TBLIND$18, 0, (short)1);
    }
    
    public CTTblWidth addNewTblInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().add_element_user(CTTblPrBaseImpl.TBLIND$18);
        }
    }
    
    public void unsetTblInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrBaseImpl.TBLIND$18, 0);
        }
    }
    
    public CTTblBorders getTblBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblBorders ctTblBorders = (CTTblBorders)this.get_store().find_element_user(CTTblPrBaseImpl.TBLBORDERS$20, 0);
            if (ctTblBorders == null) {
                return null;
            }
            return ctTblBorders;
        }
    }
    
    public boolean isSetTblBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrBaseImpl.TBLBORDERS$20) != 0;
        }
    }
    
    public void setTblBorders(final CTTblBorders ctTblBorders) {
        this.generatedSetterHelperImpl((XmlObject)ctTblBorders, CTTblPrBaseImpl.TBLBORDERS$20, 0, (short)1);
    }
    
    public CTTblBorders addNewTblBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblBorders)this.get_store().add_element_user(CTTblPrBaseImpl.TBLBORDERS$20);
        }
    }
    
    public void unsetTblBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrBaseImpl.TBLBORDERS$20, 0);
        }
    }
    
    public CTShd getShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShd ctShd = (CTShd)this.get_store().find_element_user(CTTblPrBaseImpl.SHD$22, 0);
            if (ctShd == null) {
                return null;
            }
            return ctShd;
        }
    }
    
    public boolean isSetShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrBaseImpl.SHD$22) != 0;
        }
    }
    
    public void setShd(final CTShd ctShd) {
        this.generatedSetterHelperImpl((XmlObject)ctShd, CTTblPrBaseImpl.SHD$22, 0, (short)1);
    }
    
    public CTShd addNewShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShd)this.get_store().add_element_user(CTTblPrBaseImpl.SHD$22);
        }
    }
    
    public void unsetShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrBaseImpl.SHD$22, 0);
        }
    }
    
    public CTTblLayoutType getTblLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblLayoutType ctTblLayoutType = (CTTblLayoutType)this.get_store().find_element_user(CTTblPrBaseImpl.TBLLAYOUT$24, 0);
            if (ctTblLayoutType == null) {
                return null;
            }
            return ctTblLayoutType;
        }
    }
    
    public boolean isSetTblLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrBaseImpl.TBLLAYOUT$24) != 0;
        }
    }
    
    public void setTblLayout(final CTTblLayoutType ctTblLayoutType) {
        this.generatedSetterHelperImpl((XmlObject)ctTblLayoutType, CTTblPrBaseImpl.TBLLAYOUT$24, 0, (short)1);
    }
    
    public CTTblLayoutType addNewTblLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblLayoutType)this.get_store().add_element_user(CTTblPrBaseImpl.TBLLAYOUT$24);
        }
    }
    
    public void unsetTblLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrBaseImpl.TBLLAYOUT$24, 0);
        }
    }
    
    public CTTblCellMar getTblCellMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblCellMar ctTblCellMar = (CTTblCellMar)this.get_store().find_element_user(CTTblPrBaseImpl.TBLCELLMAR$26, 0);
            if (ctTblCellMar == null) {
                return null;
            }
            return ctTblCellMar;
        }
    }
    
    public boolean isSetTblCellMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrBaseImpl.TBLCELLMAR$26) != 0;
        }
    }
    
    public void setTblCellMar(final CTTblCellMar ctTblCellMar) {
        this.generatedSetterHelperImpl((XmlObject)ctTblCellMar, CTTblPrBaseImpl.TBLCELLMAR$26, 0, (short)1);
    }
    
    public CTTblCellMar addNewTblCellMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblCellMar)this.get_store().add_element_user(CTTblPrBaseImpl.TBLCELLMAR$26);
        }
    }
    
    public void unsetTblCellMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrBaseImpl.TBLCELLMAR$26, 0);
        }
    }
    
    public CTShortHexNumber getTblLook() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShortHexNumber ctShortHexNumber = (CTShortHexNumber)this.get_store().find_element_user(CTTblPrBaseImpl.TBLLOOK$28, 0);
            if (ctShortHexNumber == null) {
                return null;
            }
            return ctShortHexNumber;
        }
    }
    
    public boolean isSetTblLook() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrBaseImpl.TBLLOOK$28) != 0;
        }
    }
    
    public void setTblLook(final CTShortHexNumber ctShortHexNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctShortHexNumber, CTTblPrBaseImpl.TBLLOOK$28, 0, (short)1);
    }
    
    public CTShortHexNumber addNewTblLook() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShortHexNumber)this.get_store().add_element_user(CTTblPrBaseImpl.TBLLOOK$28);
        }
    }
    
    public void unsetTblLook() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrBaseImpl.TBLLOOK$28, 0);
        }
    }
    
    static {
        TBLSTYLE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblStyle");
        TBLPPR$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblpPr");
        TBLOVERLAP$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblOverlap");
        BIDIVISUAL$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bidiVisual");
        TBLSTYLEROWBANDSIZE$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblStyleRowBandSize");
        TBLSTYLECOLBANDSIZE$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblStyleColBandSize");
        TBLW$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblW");
        JC$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "jc");
        TBLCELLSPACING$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblCellSpacing");
        TBLIND$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblInd");
        TBLBORDERS$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblBorders");
        SHD$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "shd");
        TBLLAYOUT$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblLayout");
        TBLCELLMAR$26 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblCellMar");
        TBLLOOK$28 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblLook");
    }
}
