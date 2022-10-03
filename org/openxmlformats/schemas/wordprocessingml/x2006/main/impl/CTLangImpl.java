package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLang;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLang;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLangImpl extends XmlComplexContentImpl implements CTLang
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTLangImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public Object getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLangImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STLang xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLang)this.get_store().find_attribute_user(CTLangImpl.VAL$0);
        }
    }
    
    public void setVal(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLangImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLangImpl.VAL$0);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetVal(final STLang stLang) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLang stLang2 = (STLang)this.get_store().find_attribute_user(CTLangImpl.VAL$0);
            if (stLang2 == null) {
                stLang2 = (STLang)this.get_store().add_attribute_user(CTLangImpl.VAL$0);
            }
            stLang2.set((XmlObject)stLang);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
    }
}
