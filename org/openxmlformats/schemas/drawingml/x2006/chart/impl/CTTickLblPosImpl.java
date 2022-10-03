package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STTickLblPos;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickLblPos;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTickLblPosImpl extends XmlComplexContentImpl implements CTTickLblPos
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTTickLblPosImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STTickLblPos.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTickLblPosImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTickLblPosImpl.VAL$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STTickLblPos.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTickLblPos xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTickLblPos stTickLblPos = (STTickLblPos)this.get_store().find_attribute_user(CTTickLblPosImpl.VAL$0);
            if (stTickLblPos == null) {
                stTickLblPos = (STTickLblPos)this.get_default_attribute_value(CTTickLblPosImpl.VAL$0);
            }
            return stTickLblPos;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTickLblPosImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STTickLblPos.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTickLblPosImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTickLblPosImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STTickLblPos stTickLblPos) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTickLblPos stTickLblPos2 = (STTickLblPos)this.get_store().find_attribute_user(CTTickLblPosImpl.VAL$0);
            if (stTickLblPos2 == null) {
                stTickLblPos2 = (STTickLblPos)this.get_store().add_attribute_user(CTTickLblPosImpl.VAL$0);
            }
            stTickLblPos2.set((XmlObject)stTickLblPos);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTickLblPosImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
