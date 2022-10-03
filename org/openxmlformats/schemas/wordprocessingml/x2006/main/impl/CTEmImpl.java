package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STEm;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEm;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTEmImpl extends XmlComplexContentImpl implements CTEm
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTEmImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STEm.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTEmImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STEm.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STEm xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STEm)this.get_store().find_attribute_user(CTEmImpl.VAL$0);
        }
    }
    
    public void setVal(final STEm.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTEmImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTEmImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STEm stEm) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STEm stEm2 = (STEm)this.get_store().find_attribute_user(CTEmImpl.VAL$0);
            if (stEm2 == null) {
                stEm2 = (STEm)this.get_store().add_attribute_user(CTEmImpl.VAL$0);
            }
            stEm2.set((XmlObject)stEm);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
    }
}
