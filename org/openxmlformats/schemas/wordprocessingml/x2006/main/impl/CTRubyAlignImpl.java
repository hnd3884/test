package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STRubyAlign;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRubyAlign;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRubyAlignImpl extends XmlComplexContentImpl implements CTRubyAlign
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTRubyAlignImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STRubyAlign.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRubyAlignImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STRubyAlign.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STRubyAlign xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRubyAlign)this.get_store().find_attribute_user(CTRubyAlignImpl.VAL$0);
        }
    }
    
    public void setVal(final STRubyAlign.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRubyAlignImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRubyAlignImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STRubyAlign stRubyAlign) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRubyAlign stRubyAlign2 = (STRubyAlign)this.get_store().find_attribute_user(CTRubyAlignImpl.VAL$0);
            if (stRubyAlign2 == null) {
                stRubyAlign2 = (STRubyAlign)this.get_store().add_attribute_user(CTRubyAlignImpl.VAL$0);
            }
            stRubyAlign2.set((XmlObject)stRubyAlign);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
    }
}
