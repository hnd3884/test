package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelAttribute;

public class TopLevelAttributeImpl extends AttributeImpl implements TopLevelAttribute
{
    private static final long serialVersionUID = 1L;
    private static final QName NAME$0;
    
    public TopLevelAttributeImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(TopLevelAttributeImpl.NAME$0);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlNCName xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlNCName target = null;
            target = (XmlNCName)this.get_store().find_attribute_user(TopLevelAttributeImpl.NAME$0);
            return target;
        }
    }
    
    @Override
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(TopLevelAttributeImpl.NAME$0) != null;
        }
    }
    
    @Override
    public void setName(final String name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(TopLevelAttributeImpl.NAME$0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(TopLevelAttributeImpl.NAME$0);
            }
            target.setStringValue(name);
        }
    }
    
    @Override
    public void xsetName(final XmlNCName name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlNCName target = null;
            target = (XmlNCName)this.get_store().find_attribute_user(TopLevelAttributeImpl.NAME$0);
            if (target == null) {
                target = (XmlNCName)this.get_store().add_attribute_user(TopLevelAttributeImpl.NAME$0);
            }
            target.set(name);
        }
    }
    
    @Override
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(TopLevelAttributeImpl.NAME$0);
        }
    }
    
    static {
        NAME$0 = new QName("", "name");
    }
}
