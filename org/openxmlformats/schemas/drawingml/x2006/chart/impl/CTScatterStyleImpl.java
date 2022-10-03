package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STScatterStyle;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterStyle;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTScatterStyleImpl extends XmlComplexContentImpl implements CTScatterStyle
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTScatterStyleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STScatterStyle.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTScatterStyleImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTScatterStyleImpl.VAL$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STScatterStyle.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STScatterStyle xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STScatterStyle stScatterStyle = (STScatterStyle)this.get_store().find_attribute_user(CTScatterStyleImpl.VAL$0);
            if (stScatterStyle == null) {
                stScatterStyle = (STScatterStyle)this.get_default_attribute_value(CTScatterStyleImpl.VAL$0);
            }
            return stScatterStyle;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTScatterStyleImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STScatterStyle.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTScatterStyleImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTScatterStyleImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STScatterStyle stScatterStyle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STScatterStyle stScatterStyle2 = (STScatterStyle)this.get_store().find_attribute_user(CTScatterStyleImpl.VAL$0);
            if (stScatterStyle2 == null) {
                stScatterStyle2 = (STScatterStyle)this.get_store().add_attribute_user(CTScatterStyleImpl.VAL$0);
            }
            stScatterStyle2.set((XmlObject)stScatterStyle);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTScatterStyleImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
