package org.apache.xmlbeans.impl.xb.xmlschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlLanguage;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xmlschema.LangAttribute;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class LangAttributeImpl extends XmlComplexContentImpl implements LangAttribute
{
    private static final long serialVersionUID = 1L;
    private static final QName LANG$0;
    
    public LangAttributeImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public String getLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(LangAttributeImpl.LANG$0);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlLanguage xgetLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlLanguage target = null;
            target = (XmlLanguage)this.get_store().find_attribute_user(LangAttributeImpl.LANG$0);
            return target;
        }
    }
    
    @Override
    public boolean isSetLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(LangAttributeImpl.LANG$0) != null;
        }
    }
    
    @Override
    public void setLang(final String lang) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(LangAttributeImpl.LANG$0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(LangAttributeImpl.LANG$0);
            }
            target.setStringValue(lang);
        }
    }
    
    @Override
    public void xsetLang(final XmlLanguage lang) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlLanguage target = null;
            target = (XmlLanguage)this.get_store().find_attribute_user(LangAttributeImpl.LANG$0);
            if (target == null) {
                target = (XmlLanguage)this.get_store().add_attribute_user(LangAttributeImpl.LANG$0);
            }
            target.set(lang);
        }
    }
    
    @Override
    public void unsetLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(LangAttributeImpl.LANG$0);
        }
    }
    
    static {
        LANG$0 = new QName("http://www.w3.org/XML/1998/namespace", "lang");
    }
}
