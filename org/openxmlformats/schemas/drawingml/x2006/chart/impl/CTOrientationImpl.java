package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STOrientation;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTOrientation;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTOrientationImpl extends XmlComplexContentImpl implements CTOrientation
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTOrientationImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STOrientation.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOrientationImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOrientationImpl.VAL$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STOrientation.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOrientation xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOrientation stOrientation = (STOrientation)this.get_store().find_attribute_user(CTOrientationImpl.VAL$0);
            if (stOrientation == null) {
                stOrientation = (STOrientation)this.get_default_attribute_value(CTOrientationImpl.VAL$0);
            }
            return stOrientation;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOrientationImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STOrientation.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOrientationImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOrientationImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STOrientation stOrientation) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOrientation stOrientation2 = (STOrientation)this.get_store().find_attribute_user(CTOrientationImpl.VAL$0);
            if (stOrientation2 == null) {
                stOrientation2 = (STOrientation)this.get_store().add_attribute_user(CTOrientationImpl.VAL$0);
            }
            stOrientation2.set((XmlObject)stOrientation);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOrientationImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
