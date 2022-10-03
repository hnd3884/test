package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnderlineValues;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTUnderlineProperty;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTUnderlinePropertyImpl extends XmlComplexContentImpl implements CTUnderlineProperty
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTUnderlinePropertyImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STUnderlineValues.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTUnderlinePropertyImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTUnderlinePropertyImpl.VAL$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STUnderlineValues.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STUnderlineValues xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUnderlineValues stUnderlineValues = (STUnderlineValues)this.get_store().find_attribute_user(CTUnderlinePropertyImpl.VAL$0);
            if (stUnderlineValues == null) {
                stUnderlineValues = (STUnderlineValues)this.get_default_attribute_value(CTUnderlinePropertyImpl.VAL$0);
            }
            return stUnderlineValues;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTUnderlinePropertyImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STUnderlineValues.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTUnderlinePropertyImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTUnderlinePropertyImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STUnderlineValues stUnderlineValues) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUnderlineValues stUnderlineValues2 = (STUnderlineValues)this.get_store().find_attribute_user(CTUnderlinePropertyImpl.VAL$0);
            if (stUnderlineValues2 == null) {
                stUnderlineValues2 = (STUnderlineValues)this.get_store().add_attribute_user(CTUnderlinePropertyImpl.VAL$0);
            }
            stUnderlineValues2.set((XmlObject)stUnderlineValues);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTUnderlinePropertyImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
