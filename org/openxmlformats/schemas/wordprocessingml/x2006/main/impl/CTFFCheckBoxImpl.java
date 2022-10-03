package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFCheckBox;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFFCheckBoxImpl extends XmlComplexContentImpl implements CTFFCheckBox
{
    private static final long serialVersionUID = 1L;
    private static final QName SIZE$0;
    private static final QName SIZEAUTO$2;
    private static final QName DEFAULT$4;
    private static final QName CHECKED$6;
    
    public CTFFCheckBoxImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTHpsMeasure getSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHpsMeasure ctHpsMeasure = (CTHpsMeasure)this.get_store().find_element_user(CTFFCheckBoxImpl.SIZE$0, 0);
            if (ctHpsMeasure == null) {
                return null;
            }
            return ctHpsMeasure;
        }
    }
    
    public boolean isSetSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFFCheckBoxImpl.SIZE$0) != 0;
        }
    }
    
    public void setSize(final CTHpsMeasure ctHpsMeasure) {
        this.generatedSetterHelperImpl((XmlObject)ctHpsMeasure, CTFFCheckBoxImpl.SIZE$0, 0, (short)1);
    }
    
    public CTHpsMeasure addNewSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHpsMeasure)this.get_store().add_element_user(CTFFCheckBoxImpl.SIZE$0);
        }
    }
    
    public void unsetSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFFCheckBoxImpl.SIZE$0, 0);
        }
    }
    
    public CTOnOff getSizeAuto() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTFFCheckBoxImpl.SIZEAUTO$2, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSizeAuto() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFFCheckBoxImpl.SIZEAUTO$2) != 0;
        }
    }
    
    public void setSizeAuto(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTFFCheckBoxImpl.SIZEAUTO$2, 0, (short)1);
    }
    
    public CTOnOff addNewSizeAuto() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTFFCheckBoxImpl.SIZEAUTO$2);
        }
    }
    
    public void unsetSizeAuto() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFFCheckBoxImpl.SIZEAUTO$2, 0);
        }
    }
    
    public CTOnOff getDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTFFCheckBoxImpl.DEFAULT$4, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFFCheckBoxImpl.DEFAULT$4) != 0;
        }
    }
    
    public void setDefault(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTFFCheckBoxImpl.DEFAULT$4, 0, (short)1);
    }
    
    public CTOnOff addNewDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTFFCheckBoxImpl.DEFAULT$4);
        }
    }
    
    public void unsetDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFFCheckBoxImpl.DEFAULT$4, 0);
        }
    }
    
    public CTOnOff getChecked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTFFCheckBoxImpl.CHECKED$6, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetChecked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFFCheckBoxImpl.CHECKED$6) != 0;
        }
    }
    
    public void setChecked(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTFFCheckBoxImpl.CHECKED$6, 0, (short)1);
    }
    
    public CTOnOff addNewChecked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTFFCheckBoxImpl.CHECKED$6);
        }
    }
    
    public void unsetChecked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFFCheckBoxImpl.CHECKED$6, 0);
        }
    }
    
    static {
        SIZE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "size");
        SIZEAUTO$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sizeAuto");
        DEFAULT$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "default");
        CHECKED$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "checked");
    }
}
