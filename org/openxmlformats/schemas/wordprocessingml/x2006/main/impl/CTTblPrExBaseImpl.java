package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShortHexNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblCellMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPrExBase;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTblPrExBaseImpl extends XmlComplexContentImpl implements CTTblPrExBase
{
    private static final long serialVersionUID = 1L;
    private static final QName TBLW$0;
    private static final QName JC$2;
    private static final QName TBLCELLSPACING$4;
    private static final QName TBLIND$6;
    private static final QName TBLBORDERS$8;
    private static final QName SHD$10;
    private static final QName TBLLAYOUT$12;
    private static final QName TBLCELLMAR$14;
    private static final QName TBLLOOK$16;
    
    public CTTblPrExBaseImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTblWidth getTblW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblWidth ctTblWidth = (CTTblWidth)this.get_store().find_element_user(CTTblPrExBaseImpl.TBLW$0, 0);
            if (ctTblWidth == null) {
                return null;
            }
            return ctTblWidth;
        }
    }
    
    public boolean isSetTblW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrExBaseImpl.TBLW$0) != 0;
        }
    }
    
    public void setTblW(final CTTblWidth ctTblWidth) {
        this.generatedSetterHelperImpl((XmlObject)ctTblWidth, CTTblPrExBaseImpl.TBLW$0, 0, (short)1);
    }
    
    public CTTblWidth addNewTblW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().add_element_user(CTTblPrExBaseImpl.TBLW$0);
        }
    }
    
    public void unsetTblW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrExBaseImpl.TBLW$0, 0);
        }
    }
    
    public CTJc getJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTJc ctJc = (CTJc)this.get_store().find_element_user(CTTblPrExBaseImpl.JC$2, 0);
            if (ctJc == null) {
                return null;
            }
            return ctJc;
        }
    }
    
    public boolean isSetJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrExBaseImpl.JC$2) != 0;
        }
    }
    
    public void setJc(final CTJc ctJc) {
        this.generatedSetterHelperImpl((XmlObject)ctJc, CTTblPrExBaseImpl.JC$2, 0, (short)1);
    }
    
    public CTJc addNewJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTJc)this.get_store().add_element_user(CTTblPrExBaseImpl.JC$2);
        }
    }
    
    public void unsetJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrExBaseImpl.JC$2, 0);
        }
    }
    
    public CTTblWidth getTblCellSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblWidth ctTblWidth = (CTTblWidth)this.get_store().find_element_user(CTTblPrExBaseImpl.TBLCELLSPACING$4, 0);
            if (ctTblWidth == null) {
                return null;
            }
            return ctTblWidth;
        }
    }
    
    public boolean isSetTblCellSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrExBaseImpl.TBLCELLSPACING$4) != 0;
        }
    }
    
    public void setTblCellSpacing(final CTTblWidth ctTblWidth) {
        this.generatedSetterHelperImpl((XmlObject)ctTblWidth, CTTblPrExBaseImpl.TBLCELLSPACING$4, 0, (short)1);
    }
    
    public CTTblWidth addNewTblCellSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().add_element_user(CTTblPrExBaseImpl.TBLCELLSPACING$4);
        }
    }
    
    public void unsetTblCellSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrExBaseImpl.TBLCELLSPACING$4, 0);
        }
    }
    
    public CTTblWidth getTblInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblWidth ctTblWidth = (CTTblWidth)this.get_store().find_element_user(CTTblPrExBaseImpl.TBLIND$6, 0);
            if (ctTblWidth == null) {
                return null;
            }
            return ctTblWidth;
        }
    }
    
    public boolean isSetTblInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrExBaseImpl.TBLIND$6) != 0;
        }
    }
    
    public void setTblInd(final CTTblWidth ctTblWidth) {
        this.generatedSetterHelperImpl((XmlObject)ctTblWidth, CTTblPrExBaseImpl.TBLIND$6, 0, (short)1);
    }
    
    public CTTblWidth addNewTblInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().add_element_user(CTTblPrExBaseImpl.TBLIND$6);
        }
    }
    
    public void unsetTblInd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrExBaseImpl.TBLIND$6, 0);
        }
    }
    
    public CTTblBorders getTblBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblBorders ctTblBorders = (CTTblBorders)this.get_store().find_element_user(CTTblPrExBaseImpl.TBLBORDERS$8, 0);
            if (ctTblBorders == null) {
                return null;
            }
            return ctTblBorders;
        }
    }
    
    public boolean isSetTblBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrExBaseImpl.TBLBORDERS$8) != 0;
        }
    }
    
    public void setTblBorders(final CTTblBorders ctTblBorders) {
        this.generatedSetterHelperImpl((XmlObject)ctTblBorders, CTTblPrExBaseImpl.TBLBORDERS$8, 0, (short)1);
    }
    
    public CTTblBorders addNewTblBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblBorders)this.get_store().add_element_user(CTTblPrExBaseImpl.TBLBORDERS$8);
        }
    }
    
    public void unsetTblBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrExBaseImpl.TBLBORDERS$8, 0);
        }
    }
    
    public CTShd getShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShd ctShd = (CTShd)this.get_store().find_element_user(CTTblPrExBaseImpl.SHD$10, 0);
            if (ctShd == null) {
                return null;
            }
            return ctShd;
        }
    }
    
    public boolean isSetShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrExBaseImpl.SHD$10) != 0;
        }
    }
    
    public void setShd(final CTShd ctShd) {
        this.generatedSetterHelperImpl((XmlObject)ctShd, CTTblPrExBaseImpl.SHD$10, 0, (short)1);
    }
    
    public CTShd addNewShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShd)this.get_store().add_element_user(CTTblPrExBaseImpl.SHD$10);
        }
    }
    
    public void unsetShd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrExBaseImpl.SHD$10, 0);
        }
    }
    
    public CTTblLayoutType getTblLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblLayoutType ctTblLayoutType = (CTTblLayoutType)this.get_store().find_element_user(CTTblPrExBaseImpl.TBLLAYOUT$12, 0);
            if (ctTblLayoutType == null) {
                return null;
            }
            return ctTblLayoutType;
        }
    }
    
    public boolean isSetTblLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrExBaseImpl.TBLLAYOUT$12) != 0;
        }
    }
    
    public void setTblLayout(final CTTblLayoutType ctTblLayoutType) {
        this.generatedSetterHelperImpl((XmlObject)ctTblLayoutType, CTTblPrExBaseImpl.TBLLAYOUT$12, 0, (short)1);
    }
    
    public CTTblLayoutType addNewTblLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblLayoutType)this.get_store().add_element_user(CTTblPrExBaseImpl.TBLLAYOUT$12);
        }
    }
    
    public void unsetTblLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrExBaseImpl.TBLLAYOUT$12, 0);
        }
    }
    
    public CTTblCellMar getTblCellMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblCellMar ctTblCellMar = (CTTblCellMar)this.get_store().find_element_user(CTTblPrExBaseImpl.TBLCELLMAR$14, 0);
            if (ctTblCellMar == null) {
                return null;
            }
            return ctTblCellMar;
        }
    }
    
    public boolean isSetTblCellMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrExBaseImpl.TBLCELLMAR$14) != 0;
        }
    }
    
    public void setTblCellMar(final CTTblCellMar ctTblCellMar) {
        this.generatedSetterHelperImpl((XmlObject)ctTblCellMar, CTTblPrExBaseImpl.TBLCELLMAR$14, 0, (short)1);
    }
    
    public CTTblCellMar addNewTblCellMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblCellMar)this.get_store().add_element_user(CTTblPrExBaseImpl.TBLCELLMAR$14);
        }
    }
    
    public void unsetTblCellMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrExBaseImpl.TBLCELLMAR$14, 0);
        }
    }
    
    public CTShortHexNumber getTblLook() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShortHexNumber ctShortHexNumber = (CTShortHexNumber)this.get_store().find_element_user(CTTblPrExBaseImpl.TBLLOOK$16, 0);
            if (ctShortHexNumber == null) {
                return null;
            }
            return ctShortHexNumber;
        }
    }
    
    public boolean isSetTblLook() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblPrExBaseImpl.TBLLOOK$16) != 0;
        }
    }
    
    public void setTblLook(final CTShortHexNumber ctShortHexNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctShortHexNumber, CTTblPrExBaseImpl.TBLLOOK$16, 0, (short)1);
    }
    
    public CTShortHexNumber addNewTblLook() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShortHexNumber)this.get_store().add_element_user(CTTblPrExBaseImpl.TBLLOOK$16);
        }
    }
    
    public void unsetTblLook() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblPrExBaseImpl.TBLLOOK$16, 0);
        }
    }
    
    static {
        TBLW$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblW");
        JC$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "jc");
        TBLCELLSPACING$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblCellSpacing");
        TBLIND$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblInd");
        TBLBORDERS$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblBorders");
        SHD$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "shd");
        TBLLAYOUT$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblLayout");
        TBLCELLMAR$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblCellMar");
        TBLLOOK$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblLook");
    }
}
