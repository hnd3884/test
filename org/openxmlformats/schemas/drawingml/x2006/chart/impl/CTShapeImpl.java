package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STShape;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTShape;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTShapeImpl extends XmlComplexContentImpl implements CTShape
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTShapeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STShape.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapeImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTShapeImpl.VAL$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STShape.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STShape xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STShape stShape = (STShape)this.get_store().find_attribute_user(CTShapeImpl.VAL$0);
            if (stShape == null) {
                stShape = (STShape)this.get_default_attribute_value(CTShapeImpl.VAL$0);
            }
            return stShape;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapeImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STShape.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapeImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapeImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STShape stShape) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STShape stShape2 = (STShape)this.get_store().find_attribute_user(CTShapeImpl.VAL$0);
            if (stShape2 == null) {
                stShape2 = (STShape)this.get_store().add_attribute_user(CTShapeImpl.VAL$0);
            }
            stShape2.set((XmlObject)stShape);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapeImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
