package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.GroupRef;

public class GroupRefImpl extends RealGroupImpl implements GroupRef
{
    private static final long serialVersionUID = 1L;
    private static final QName REF$0;
    
    public GroupRefImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public QName getRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(GroupRefImpl.REF$0);
            if (target == null) {
                return null;
            }
            return target.getQNameValue();
        }
    }
    
    @Override
    public XmlQName xgetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(GroupRefImpl.REF$0);
            return target;
        }
    }
    
    @Override
    public boolean isSetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(GroupRefImpl.REF$0) != null;
        }
    }
    
    @Override
    public void setRef(final QName ref) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(GroupRefImpl.REF$0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(GroupRefImpl.REF$0);
            }
            target.setQNameValue(ref);
        }
    }
    
    @Override
    public void xsetRef(final XmlQName ref) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(GroupRefImpl.REF$0);
            if (target == null) {
                target = (XmlQName)this.get_store().add_attribute_user(GroupRefImpl.REF$0);
            }
            target.set(ref);
        }
    }
    
    @Override
    public void unsetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(GroupRefImpl.REF$0);
        }
    }
    
    static {
        REF$0 = new QName("", "ref");
    }
}
