package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLang;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRubyAlign;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRubyPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRubyPrImpl extends XmlComplexContentImpl implements CTRubyPr
{
    private static final long serialVersionUID = 1L;
    private static final QName RUBYALIGN$0;
    private static final QName HPS$2;
    private static final QName HPSRAISE$4;
    private static final QName HPSBASETEXT$6;
    private static final QName LID$8;
    private static final QName DIRTY$10;
    
    public CTRubyPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTRubyAlign getRubyAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRubyAlign ctRubyAlign = (CTRubyAlign)this.get_store().find_element_user(CTRubyPrImpl.RUBYALIGN$0, 0);
            if (ctRubyAlign == null) {
                return null;
            }
            return ctRubyAlign;
        }
    }
    
    public void setRubyAlign(final CTRubyAlign ctRubyAlign) {
        this.generatedSetterHelperImpl((XmlObject)ctRubyAlign, CTRubyPrImpl.RUBYALIGN$0, 0, (short)1);
    }
    
    public CTRubyAlign addNewRubyAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRubyAlign)this.get_store().add_element_user(CTRubyPrImpl.RUBYALIGN$0);
        }
    }
    
    public CTHpsMeasure getHps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHpsMeasure ctHpsMeasure = (CTHpsMeasure)this.get_store().find_element_user(CTRubyPrImpl.HPS$2, 0);
            if (ctHpsMeasure == null) {
                return null;
            }
            return ctHpsMeasure;
        }
    }
    
    public void setHps(final CTHpsMeasure ctHpsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctHpsMeasure, CTRubyPrImpl.HPS$2, 0, (short)1);
    }
    
    public CTHpsMeasure addNewHps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHpsMeasure)this.get_store().add_element_user(CTRubyPrImpl.HPS$2);
        }
    }
    
    public CTHpsMeasure getHpsRaise() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHpsMeasure ctHpsMeasure = (CTHpsMeasure)this.get_store().find_element_user(CTRubyPrImpl.HPSRAISE$4, 0);
            if (ctHpsMeasure == null) {
                return null;
            }
            return ctHpsMeasure;
        }
    }
    
    public void setHpsRaise(final CTHpsMeasure ctHpsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctHpsMeasure, CTRubyPrImpl.HPSRAISE$4, 0, (short)1);
    }
    
    public CTHpsMeasure addNewHpsRaise() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHpsMeasure)this.get_store().add_element_user(CTRubyPrImpl.HPSRAISE$4);
        }
    }
    
    public CTHpsMeasure getHpsBaseText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHpsMeasure ctHpsMeasure = (CTHpsMeasure)this.get_store().find_element_user(CTRubyPrImpl.HPSBASETEXT$6, 0);
            if (ctHpsMeasure == null) {
                return null;
            }
            return ctHpsMeasure;
        }
    }
    
    public void setHpsBaseText(final CTHpsMeasure ctHpsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctHpsMeasure, CTRubyPrImpl.HPSBASETEXT$6, 0, (short)1);
    }
    
    public CTHpsMeasure addNewHpsBaseText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHpsMeasure)this.get_store().add_element_user(CTRubyPrImpl.HPSBASETEXT$6);
        }
    }
    
    public CTLang getLid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLang ctLang = (CTLang)this.get_store().find_element_user(CTRubyPrImpl.LID$8, 0);
            if (ctLang == null) {
                return null;
            }
            return ctLang;
        }
    }
    
    public void setLid(final CTLang ctLang) {
        this.generatedSetterHelperImpl((XmlObject)ctLang, CTRubyPrImpl.LID$8, 0, (short)1);
    }
    
    public CTLang addNewLid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLang)this.get_store().add_element_user(CTRubyPrImpl.LID$8);
        }
    }
    
    public CTOnOff getDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTRubyPrImpl.DIRTY$10, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyPrImpl.DIRTY$10) != 0;
        }
    }
    
    public void setDirty(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTRubyPrImpl.DIRTY$10, 0, (short)1);
    }
    
    public CTOnOff addNewDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTRubyPrImpl.DIRTY$10);
        }
    }
    
    public void unsetDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyPrImpl.DIRTY$10, 0);
        }
    }
    
    static {
        RUBYALIGN$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rubyAlign");
        HPS$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hps");
        HPSRAISE$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hpsRaise");
        HPSBASETEXT$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hpsBaseText");
        LID$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lid");
        DIRTY$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dirty");
    }
}
