package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLang;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLanguage;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLanguageImpl extends XmlComplexContentImpl implements CTLanguage
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    private static final QName EASTASIA$2;
    private static final QName BIDI$4;
    
    public CTLanguageImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public Object getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLanguageImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STLang xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLang)this.get_store().find_attribute_user(CTLanguageImpl.VAL$0);
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLanguageImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLanguageImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLanguageImpl.VAL$0);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetVal(final STLang stLang) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLang stLang2 = (STLang)this.get_store().find_attribute_user(CTLanguageImpl.VAL$0);
            if (stLang2 == null) {
                stLang2 = (STLang)this.get_store().add_attribute_user(CTLanguageImpl.VAL$0);
            }
            stLang2.set((XmlObject)stLang);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLanguageImpl.VAL$0);
        }
    }
    
    public Object getEastAsia() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLanguageImpl.EASTASIA$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STLang xgetEastAsia() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLang)this.get_store().find_attribute_user(CTLanguageImpl.EASTASIA$2);
        }
    }
    
    public boolean isSetEastAsia() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLanguageImpl.EASTASIA$2) != null;
        }
    }
    
    public void setEastAsia(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLanguageImpl.EASTASIA$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLanguageImpl.EASTASIA$2);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetEastAsia(final STLang stLang) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLang stLang2 = (STLang)this.get_store().find_attribute_user(CTLanguageImpl.EASTASIA$2);
            if (stLang2 == null) {
                stLang2 = (STLang)this.get_store().add_attribute_user(CTLanguageImpl.EASTASIA$2);
            }
            stLang2.set((XmlObject)stLang);
        }
    }
    
    public void unsetEastAsia() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLanguageImpl.EASTASIA$2);
        }
    }
    
    public Object getBidi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLanguageImpl.BIDI$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getObjectValue();
        }
    }
    
    public STLang xgetBidi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLang)this.get_store().find_attribute_user(CTLanguageImpl.BIDI$4);
        }
    }
    
    public boolean isSetBidi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLanguageImpl.BIDI$4) != null;
        }
    }
    
    public void setBidi(final Object objectValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLanguageImpl.BIDI$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLanguageImpl.BIDI$4);
            }
            simpleValue.setObjectValue(objectValue);
        }
    }
    
    public void xsetBidi(final STLang stLang) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLang stLang2 = (STLang)this.get_store().find_attribute_user(CTLanguageImpl.BIDI$4);
            if (stLang2 == null) {
                stLang2 = (STLang)this.get_store().add_attribute_user(CTLanguageImpl.BIDI$4);
            }
            stLang2.set((XmlObject)stLang);
        }
    }
    
    public void unsetBidi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLanguageImpl.BIDI$4);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
        EASTASIA$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "eastAsia");
        BIDI$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bidi");
    }
}
