package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVerticalAlignRun;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTVerticalAlignFontProperty;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTVerticalAlignFontPropertyImpl extends XmlComplexContentImpl implements CTVerticalAlignFontProperty
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTVerticalAlignFontPropertyImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STVerticalAlignRun.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTVerticalAlignFontPropertyImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STVerticalAlignRun.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STVerticalAlignRun xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STVerticalAlignRun)this.get_store().find_attribute_user(CTVerticalAlignFontPropertyImpl.VAL$0);
        }
    }
    
    public void setVal(final STVerticalAlignRun.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTVerticalAlignFontPropertyImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTVerticalAlignFontPropertyImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STVerticalAlignRun stVerticalAlignRun) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STVerticalAlignRun stVerticalAlignRun2 = (STVerticalAlignRun)this.get_store().find_attribute_user(CTVerticalAlignFontPropertyImpl.VAL$0);
            if (stVerticalAlignRun2 == null) {
                stVerticalAlignRun2 = (STVerticalAlignRun)this.get_store().add_attribute_user(CTVerticalAlignFontPropertyImpl.VAL$0);
            }
            stVerticalAlignRun2.set((XmlObject)stVerticalAlignRun);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
