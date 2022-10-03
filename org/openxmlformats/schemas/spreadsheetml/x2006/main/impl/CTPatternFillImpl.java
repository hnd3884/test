package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPatternType;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPatternFill;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPatternFillImpl extends XmlComplexContentImpl implements CTPatternFill
{
    private static final long serialVersionUID = 1L;
    private static final QName FGCOLOR$0;
    private static final QName BGCOLOR$2;
    private static final QName PATTERNTYPE$4;
    
    public CTPatternFillImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTColor getFgColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTPatternFillImpl.FGCOLOR$0, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public boolean isSetFgColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPatternFillImpl.FGCOLOR$0) != 0;
        }
    }
    
    public void setFgColor(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTPatternFillImpl.FGCOLOR$0, 0, (short)1);
    }
    
    public CTColor addNewFgColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTPatternFillImpl.FGCOLOR$0);
        }
    }
    
    public void unsetFgColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPatternFillImpl.FGCOLOR$0, 0);
        }
    }
    
    public CTColor getBgColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTPatternFillImpl.BGCOLOR$2, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public boolean isSetBgColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPatternFillImpl.BGCOLOR$2) != 0;
        }
    }
    
    public void setBgColor(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTPatternFillImpl.BGCOLOR$2, 0, (short)1);
    }
    
    public CTColor addNewBgColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTPatternFillImpl.BGCOLOR$2);
        }
    }
    
    public void unsetBgColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPatternFillImpl.BGCOLOR$2, 0);
        }
    }
    
    public STPatternType.Enum getPatternType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPatternFillImpl.PATTERNTYPE$4);
            if (simpleValue == null) {
                return null;
            }
            return (STPatternType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPatternType xgetPatternType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPatternType)this.get_store().find_attribute_user(CTPatternFillImpl.PATTERNTYPE$4);
        }
    }
    
    public boolean isSetPatternType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPatternFillImpl.PATTERNTYPE$4) != null;
        }
    }
    
    public void setPatternType(final STPatternType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPatternFillImpl.PATTERNTYPE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPatternFillImpl.PATTERNTYPE$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetPatternType(final STPatternType stPatternType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPatternType stPatternType2 = (STPatternType)this.get_store().find_attribute_user(CTPatternFillImpl.PATTERNTYPE$4);
            if (stPatternType2 == null) {
                stPatternType2 = (STPatternType)this.get_store().add_attribute_user(CTPatternFillImpl.PATTERNTYPE$4);
            }
            stPatternType2.set((XmlObject)stPatternType);
        }
    }
    
    public void unsetPatternType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPatternFillImpl.PATTERNTYPE$4);
        }
    }
    
    static {
        FGCOLOR$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "fgColor");
        BGCOLOR$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "bgColor");
        PATTERNTYPE$4 = new QName("", "patternType");
    }
}
