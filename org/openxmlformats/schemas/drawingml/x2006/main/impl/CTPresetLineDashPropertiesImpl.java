package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.STPresetLineDashVal;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetLineDashProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPresetLineDashPropertiesImpl extends XmlComplexContentImpl implements CTPresetLineDashProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTPresetLineDashPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STPresetLineDashVal.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresetLineDashPropertiesImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STPresetLineDashVal.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPresetLineDashVal xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPresetLineDashVal)this.get_store().find_attribute_user(CTPresetLineDashPropertiesImpl.VAL$0);
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPresetLineDashPropertiesImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STPresetLineDashVal.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresetLineDashPropertiesImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPresetLineDashPropertiesImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STPresetLineDashVal stPresetLineDashVal) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPresetLineDashVal stPresetLineDashVal2 = (STPresetLineDashVal)this.get_store().find_attribute_user(CTPresetLineDashPropertiesImpl.VAL$0);
            if (stPresetLineDashVal2 == null) {
                stPresetLineDashVal2 = (STPresetLineDashVal)this.get_store().add_attribute_user(CTPresetLineDashPropertiesImpl.VAL$0);
            }
            stPresetLineDashVal2.set((XmlObject)stPresetLineDashVal);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPresetLineDashPropertiesImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
