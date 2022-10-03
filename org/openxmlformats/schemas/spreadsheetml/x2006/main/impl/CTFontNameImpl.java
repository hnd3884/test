package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontName;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFontNameImpl extends XmlComplexContentImpl implements CTFontName
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTFontNameImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontNameImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTFontNameImpl.VAL$0);
        }
    }
    
    public void setVal(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontNameImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFontNameImpl.VAL$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetVal(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTFontNameImpl.VAL$0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTFontNameImpl.VAL$0);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
