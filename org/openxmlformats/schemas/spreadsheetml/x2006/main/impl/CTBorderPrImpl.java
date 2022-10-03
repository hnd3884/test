package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STBorderStyle;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorderPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBorderPrImpl extends XmlComplexContentImpl implements CTBorderPr
{
    private static final long serialVersionUID = 1L;
    private static final QName COLOR$0;
    private static final QName STYLE$2;
    
    public CTBorderPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTColor getColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTBorderPrImpl.COLOR$0, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public boolean isSetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBorderPrImpl.COLOR$0) != 0;
        }
    }
    
    public void setColor(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTBorderPrImpl.COLOR$0, 0, (short)1);
    }
    
    public CTColor addNewColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTBorderPrImpl.COLOR$0);
        }
    }
    
    public void unsetColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBorderPrImpl.COLOR$0, 0);
        }
    }
    
    public STBorderStyle.Enum getStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderPrImpl.STYLE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBorderPrImpl.STYLE$2);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STBorderStyle.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBorderStyle xgetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBorderStyle stBorderStyle = (STBorderStyle)this.get_store().find_attribute_user(CTBorderPrImpl.STYLE$2);
            if (stBorderStyle == null) {
                stBorderStyle = (STBorderStyle)this.get_default_attribute_value(CTBorderPrImpl.STYLE$2);
            }
            return stBorderStyle;
        }
    }
    
    public boolean isSetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBorderPrImpl.STYLE$2) != null;
        }
    }
    
    public void setStyle(final STBorderStyle.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderPrImpl.STYLE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBorderPrImpl.STYLE$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetStyle(final STBorderStyle stBorderStyle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBorderStyle stBorderStyle2 = (STBorderStyle)this.get_store().find_attribute_user(CTBorderPrImpl.STYLE$2);
            if (stBorderStyle2 == null) {
                stBorderStyle2 = (STBorderStyle)this.get_store().add_attribute_user(CTBorderPrImpl.STYLE$2);
            }
            stBorderStyle2.set((XmlObject)stBorderStyle);
        }
    }
    
    public void unsetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBorderPrImpl.STYLE$2);
        }
    }
    
    static {
        COLOR$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "color");
        STYLE$2 = new QName("", "style");
    }
}
