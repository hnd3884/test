package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLayoutTarget;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayoutTarget;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLayoutTargetImpl extends XmlComplexContentImpl implements CTLayoutTarget
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTLayoutTargetImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STLayoutTarget.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLayoutTargetImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTLayoutTargetImpl.VAL$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STLayoutTarget.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STLayoutTarget xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLayoutTarget stLayoutTarget = (STLayoutTarget)this.get_store().find_attribute_user(CTLayoutTargetImpl.VAL$0);
            if (stLayoutTarget == null) {
                stLayoutTarget = (STLayoutTarget)this.get_default_attribute_value(CTLayoutTargetImpl.VAL$0);
            }
            return stLayoutTarget;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLayoutTargetImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STLayoutTarget.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLayoutTargetImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLayoutTargetImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STLayoutTarget stLayoutTarget) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLayoutTarget stLayoutTarget2 = (STLayoutTarget)this.get_store().find_attribute_user(CTLayoutTargetImpl.VAL$0);
            if (stLayoutTarget2 == null) {
                stLayoutTarget2 = (STLayoutTarget)this.get_store().add_attribute_user(CTLayoutTargetImpl.VAL$0);
            }
            stLayoutTarget2.set((XmlObject)stLayoutTarget);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLayoutTargetImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
