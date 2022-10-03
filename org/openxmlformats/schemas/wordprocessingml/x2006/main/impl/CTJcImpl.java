package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTJcImpl extends XmlComplexContentImpl implements CTJc
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTJcImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STJc.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTJcImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STJc.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STJc xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STJc)this.get_store().find_attribute_user(CTJcImpl.VAL$0);
        }
    }
    
    public void setVal(final STJc.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTJcImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTJcImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STJc stJc) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STJc stJc2 = (STJc)this.get_store().find_attribute_user(CTJcImpl.VAL$0);
            if (stJc2 == null) {
                stJc2 = (STJc)this.get_store().add_attribute_user(CTJcImpl.VAL$0);
            }
            stJc2.set((XmlObject)stJc);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
    }
}
