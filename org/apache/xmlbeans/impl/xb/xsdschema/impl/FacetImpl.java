package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.Facet;

public class FacetImpl extends AnnotatedImpl implements Facet
{
    private static final long serialVersionUID = 1L;
    private static final QName VALUE$0;
    private static final QName FIXED$2;
    
    public FacetImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public XmlAnySimpleType getValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnySimpleType target = null;
            target = (XmlAnySimpleType)this.get_store().find_attribute_user(FacetImpl.VALUE$0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setValue(final XmlAnySimpleType value) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnySimpleType target = null;
            target = (XmlAnySimpleType)this.get_store().find_attribute_user(FacetImpl.VALUE$0);
            if (target == null) {
                target = (XmlAnySimpleType)this.get_store().add_attribute_user(FacetImpl.VALUE$0);
            }
            target.set(value);
        }
    }
    
    @Override
    public XmlAnySimpleType addNewValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnySimpleType target = null;
            target = (XmlAnySimpleType)this.get_store().add_attribute_user(FacetImpl.VALUE$0);
            return target;
        }
    }
    
    @Override
    public boolean getFixed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(FacetImpl.FIXED$2);
            if (target == null) {
                target = (SimpleValue)this.get_default_attribute_value(FacetImpl.FIXED$2);
            }
            return target != null && target.getBooleanValue();
        }
    }
    
    @Override
    public XmlBoolean xgetFixed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)this.get_store().find_attribute_user(FacetImpl.FIXED$2);
            if (target == null) {
                target = (XmlBoolean)this.get_default_attribute_value(FacetImpl.FIXED$2);
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetFixed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(FacetImpl.FIXED$2) != null;
        }
    }
    
    @Override
    public void setFixed(final boolean fixed) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(FacetImpl.FIXED$2);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(FacetImpl.FIXED$2);
            }
            target.setBooleanValue(fixed);
        }
    }
    
    @Override
    public void xsetFixed(final XmlBoolean fixed) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)this.get_store().find_attribute_user(FacetImpl.FIXED$2);
            if (target == null) {
                target = (XmlBoolean)this.get_store().add_attribute_user(FacetImpl.FIXED$2);
            }
            target.set(fixed);
        }
    }
    
    @Override
    public void unsetFixed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(FacetImpl.FIXED$2);
        }
    }
    
    static {
        VALUE$0 = new QName("", "value");
        FIXED$2 = new QName("", "fixed");
    }
}
