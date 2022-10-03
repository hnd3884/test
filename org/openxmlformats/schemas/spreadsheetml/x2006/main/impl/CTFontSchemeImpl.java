package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFontScheme;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontScheme;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFontSchemeImpl extends XmlComplexContentImpl implements CTFontScheme
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTFontSchemeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STFontScheme.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontSchemeImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STFontScheme.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STFontScheme xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STFontScheme)this.get_store().find_attribute_user(CTFontSchemeImpl.VAL$0);
        }
    }
    
    public void setVal(final STFontScheme.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontSchemeImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFontSchemeImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STFontScheme stFontScheme) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFontScheme stFontScheme2 = (STFontScheme)this.get_store().find_attribute_user(CTFontSchemeImpl.VAL$0);
            if (stFontScheme2 == null) {
                stFontScheme2 = (STFontScheme)this.get_store().add_attribute_user(CTFontSchemeImpl.VAL$0);
            }
            stFontScheme2.set((XmlObject)stFontScheme);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
