package org.apache.xmlbeans.impl.xb.xmlschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xmlschema.BaseAttribute;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class BaseAttributeImpl extends XmlComplexContentImpl implements BaseAttribute
{
    private static final long serialVersionUID = 1L;
    private static final QName BASE$0;
    
    public BaseAttributeImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public String getBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(BaseAttributeImpl.BASE$0);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlAnyURI xgetBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)this.get_store().find_attribute_user(BaseAttributeImpl.BASE$0);
            return target;
        }
    }
    
    @Override
    public boolean isSetBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(BaseAttributeImpl.BASE$0) != null;
        }
    }
    
    @Override
    public void setBase(final String base) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(BaseAttributeImpl.BASE$0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(BaseAttributeImpl.BASE$0);
            }
            target.setStringValue(base);
        }
    }
    
    @Override
    public void xsetBase(final XmlAnyURI base) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)this.get_store().find_attribute_user(BaseAttributeImpl.BASE$0);
            if (target == null) {
                target = (XmlAnyURI)this.get_store().add_attribute_user(BaseAttributeImpl.BASE$0);
            }
            target.set(base);
        }
    }
    
    @Override
    public void unsetBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(BaseAttributeImpl.BASE$0);
        }
    }
    
    static {
        BASE$0 = new QName("http://www.w3.org/XML/1998/namespace", "base");
    }
}
