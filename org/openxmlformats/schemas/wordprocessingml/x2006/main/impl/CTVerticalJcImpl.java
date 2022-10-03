package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTVerticalJcImpl extends XmlComplexContentImpl implements CTVerticalJc
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTVerticalJcImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STVerticalJc.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTVerticalJcImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STVerticalJc.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STVerticalJc xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STVerticalJc)this.get_store().find_attribute_user(CTVerticalJcImpl.VAL$0);
        }
    }
    
    public void setVal(final STVerticalJc.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTVerticalJcImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTVerticalJcImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STVerticalJc stVerticalJc) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STVerticalJc stVerticalJc2 = (STVerticalJc)this.get_store().find_attribute_user(CTVerticalJcImpl.VAL$0);
            if (stVerticalJc2 == null) {
                stVerticalJc2 = (STVerticalJc)this.get_store().add_attribute_user(CTVerticalJcImpl.VAL$0);
            }
            stVerticalJc2.set((XmlObject)stVerticalJc);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
    }
}
