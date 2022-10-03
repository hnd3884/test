package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLegendPos;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLegendPos;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLegendPosImpl extends XmlComplexContentImpl implements CTLegendPos
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTLegendPosImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STLegendPos.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLegendPosImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTLegendPosImpl.VAL$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STLegendPos.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STLegendPos xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLegendPos stLegendPos = (STLegendPos)this.get_store().find_attribute_user(CTLegendPosImpl.VAL$0);
            if (stLegendPos == null) {
                stLegendPos = (STLegendPos)this.get_default_attribute_value(CTLegendPosImpl.VAL$0);
            }
            return stLegendPos;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLegendPosImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STLegendPos.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLegendPosImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLegendPosImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STLegendPos stLegendPos) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLegendPos stLegendPos2 = (STLegendPos)this.get_store().find_attribute_user(CTLegendPosImpl.VAL$0);
            if (stLegendPos2 == null) {
                stLegendPos2 = (STLegendPos)this.get_store().add_attribute_user(CTLegendPosImpl.VAL$0);
            }
            stLegendPos2.set((XmlObject)stLegendPos);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLegendPosImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
