package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STRadarStyle;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRadarStyle;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRadarStyleImpl extends XmlComplexContentImpl implements CTRadarStyle
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTRadarStyleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STRadarStyle.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRadarStyleImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRadarStyleImpl.VAL$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STRadarStyle.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STRadarStyle xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRadarStyle stRadarStyle = (STRadarStyle)this.get_store().find_attribute_user(CTRadarStyleImpl.VAL$0);
            if (stRadarStyle == null) {
                stRadarStyle = (STRadarStyle)this.get_default_attribute_value(CTRadarStyleImpl.VAL$0);
            }
            return stRadarStyle;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRadarStyleImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STRadarStyle.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRadarStyleImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRadarStyleImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STRadarStyle stRadarStyle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRadarStyle stRadarStyle2 = (STRadarStyle)this.get_store().find_attribute_user(CTRadarStyleImpl.VAL$0);
            if (stRadarStyle2 == null) {
                stRadarStyle2 = (STRadarStyle)this.get_store().add_attribute_user(CTRadarStyleImpl.VAL$0);
            }
            stRadarStyle2.set((XmlObject)stRadarStyle);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRadarStyleImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
