package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChangeNumbering;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFData;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFldChar;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFldCharImpl extends XmlComplexContentImpl implements CTFldChar
{
    private static final long serialVersionUID = 1L;
    private static final QName FLDDATA$0;
    private static final QName FFDATA$2;
    private static final QName NUMBERINGCHANGE$4;
    private static final QName FLDCHARTYPE$6;
    private static final QName FLDLOCK$8;
    private static final QName DIRTY$10;
    
    public CTFldCharImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTText getFldData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTText ctText = (CTText)this.get_store().find_element_user(CTFldCharImpl.FLDDATA$0, 0);
            if (ctText == null) {
                return null;
            }
            return ctText;
        }
    }
    
    public boolean isSetFldData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFldCharImpl.FLDDATA$0) != 0;
        }
    }
    
    public void setFldData(final CTText ctText) {
        this.generatedSetterHelperImpl((XmlObject)ctText, CTFldCharImpl.FLDDATA$0, 0, (short)1);
    }
    
    public CTText addNewFldData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTText)this.get_store().add_element_user(CTFldCharImpl.FLDDATA$0);
        }
    }
    
    public void unsetFldData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFldCharImpl.FLDDATA$0, 0);
        }
    }
    
    public CTFFData getFfData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFFData ctffData = (CTFFData)this.get_store().find_element_user(CTFldCharImpl.FFDATA$2, 0);
            if (ctffData == null) {
                return null;
            }
            return ctffData;
        }
    }
    
    public boolean isSetFfData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFldCharImpl.FFDATA$2) != 0;
        }
    }
    
    public void setFfData(final CTFFData ctffData) {
        this.generatedSetterHelperImpl((XmlObject)ctffData, CTFldCharImpl.FFDATA$2, 0, (short)1);
    }
    
    public CTFFData addNewFfData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFFData)this.get_store().add_element_user(CTFldCharImpl.FFDATA$2);
        }
    }
    
    public void unsetFfData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFldCharImpl.FFDATA$2, 0);
        }
    }
    
    public CTTrackChangeNumbering getNumberingChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChangeNumbering ctTrackChangeNumbering = (CTTrackChangeNumbering)this.get_store().find_element_user(CTFldCharImpl.NUMBERINGCHANGE$4, 0);
            if (ctTrackChangeNumbering == null) {
                return null;
            }
            return ctTrackChangeNumbering;
        }
    }
    
    public boolean isSetNumberingChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFldCharImpl.NUMBERINGCHANGE$4) != 0;
        }
    }
    
    public void setNumberingChange(final CTTrackChangeNumbering ctTrackChangeNumbering) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChangeNumbering, CTFldCharImpl.NUMBERINGCHANGE$4, 0, (short)1);
    }
    
    public CTTrackChangeNumbering addNewNumberingChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChangeNumbering)this.get_store().add_element_user(CTFldCharImpl.NUMBERINGCHANGE$4);
        }
    }
    
    public void unsetNumberingChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFldCharImpl.NUMBERINGCHANGE$4, 0);
        }
    }
    
    public STFldCharType.Enum getFldCharType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFldCharImpl.FLDCHARTYPE$6);
            if (simpleValue == null) {
                return null;
            }
            return (STFldCharType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STFldCharType xgetFldCharType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STFldCharType)this.get_store().find_attribute_user(CTFldCharImpl.FLDCHARTYPE$6);
        }
    }
    
    public void setFldCharType(final STFldCharType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFldCharImpl.FLDCHARTYPE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFldCharImpl.FLDCHARTYPE$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFldCharType(final STFldCharType stFldCharType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFldCharType stFldCharType2 = (STFldCharType)this.get_store().find_attribute_user(CTFldCharImpl.FLDCHARTYPE$6);
            if (stFldCharType2 == null) {
                stFldCharType2 = (STFldCharType)this.get_store().add_attribute_user(CTFldCharImpl.FLDCHARTYPE$6);
            }
            stFldCharType2.set((XmlObject)stFldCharType);
        }
    }
    
    public STOnOff.Enum getFldLock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFldCharImpl.FLDLOCK$8);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetFldLock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTFldCharImpl.FLDLOCK$8);
        }
    }
    
    public boolean isSetFldLock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFldCharImpl.FLDLOCK$8) != null;
        }
    }
    
    public void setFldLock(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFldCharImpl.FLDLOCK$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFldCharImpl.FLDLOCK$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFldLock(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTFldCharImpl.FLDLOCK$8);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTFldCharImpl.FLDLOCK$8);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetFldLock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFldCharImpl.FLDLOCK$8);
        }
    }
    
    public STOnOff.Enum getDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFldCharImpl.DIRTY$10);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTFldCharImpl.DIRTY$10);
        }
    }
    
    public boolean isSetDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFldCharImpl.DIRTY$10) != null;
        }
    }
    
    public void setDirty(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFldCharImpl.DIRTY$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFldCharImpl.DIRTY$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDirty(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTFldCharImpl.DIRTY$10);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTFldCharImpl.DIRTY$10);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFldCharImpl.DIRTY$10);
        }
    }
    
    static {
        FLDDATA$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "fldData");
        FFDATA$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ffData");
        NUMBERINGCHANGE$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "numberingChange");
        FLDCHARTYPE$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "fldCharType");
        FLDLOCK$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "fldLock");
        DIRTY$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dirty");
    }
}
