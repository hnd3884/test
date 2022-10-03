package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;
import org.apache.xmlbeans.impl.xb.xsdschema.FormChoice;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;

public class AttributeImpl extends AnnotatedImpl implements Attribute
{
    private static final long serialVersionUID = 1L;
    private static final QName SIMPLETYPE$0;
    private static final QName NAME$2;
    private static final QName REF$4;
    private static final QName TYPE$6;
    private static final QName USE$8;
    private static final QName DEFAULT$10;
    private static final QName FIXED$12;
    private static final QName FORM$14;
    
    public AttributeImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public LocalSimpleType getSimpleType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            LocalSimpleType target = null;
            target = (LocalSimpleType)this.get_store().find_element_user(AttributeImpl.SIMPLETYPE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetSimpleType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(AttributeImpl.SIMPLETYPE$0) != 0;
        }
    }
    
    @Override
    public void setSimpleType(final LocalSimpleType simpleType) {
        this.generatedSetterHelperImpl(simpleType, AttributeImpl.SIMPLETYPE$0, 0, (short)1);
    }
    
    @Override
    public LocalSimpleType addNewSimpleType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            LocalSimpleType target = null;
            target = (LocalSimpleType)this.get_store().add_element_user(AttributeImpl.SIMPLETYPE$0);
            return target;
        }
    }
    
    @Override
    public void unsetSimpleType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(AttributeImpl.SIMPLETYPE$0, 0);
        }
    }
    
    @Override
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeImpl.NAME$2);
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
            target = (XmlNCName)this.get_store().find_attribute_user(AttributeImpl.NAME$2);
            return target;
        }
    }
    
    @Override
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(AttributeImpl.NAME$2) != null;
        }
    }
    
    @Override
    public void setName(final String name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeImpl.NAME$2);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(AttributeImpl.NAME$2);
            }
            target.setStringValue(name);
        }
    }
    
    @Override
    public void xsetName(final XmlNCName name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlNCName target = null;
            target = (XmlNCName)this.get_store().find_attribute_user(AttributeImpl.NAME$2);
            if (target == null) {
                target = (XmlNCName)this.get_store().add_attribute_user(AttributeImpl.NAME$2);
            }
            target.set(name);
        }
    }
    
    @Override
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(AttributeImpl.NAME$2);
        }
    }
    
    @Override
    public QName getRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeImpl.REF$4);
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
            target = (XmlQName)this.get_store().find_attribute_user(AttributeImpl.REF$4);
            return target;
        }
    }
    
    @Override
    public boolean isSetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(AttributeImpl.REF$4) != null;
        }
    }
    
    @Override
    public void setRef(final QName ref) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeImpl.REF$4);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(AttributeImpl.REF$4);
            }
            target.setQNameValue(ref);
        }
    }
    
    @Override
    public void xsetRef(final XmlQName ref) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(AttributeImpl.REF$4);
            if (target == null) {
                target = (XmlQName)this.get_store().add_attribute_user(AttributeImpl.REF$4);
            }
            target.set(ref);
        }
    }
    
    @Override
    public void unsetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(AttributeImpl.REF$4);
        }
    }
    
    @Override
    public QName getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeImpl.TYPE$6);
            if (target == null) {
                return null;
            }
            return target.getQNameValue();
        }
    }
    
    @Override
    public XmlQName xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(AttributeImpl.TYPE$6);
            return target;
        }
    }
    
    @Override
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(AttributeImpl.TYPE$6) != null;
        }
    }
    
    @Override
    public void setType(final QName type) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeImpl.TYPE$6);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(AttributeImpl.TYPE$6);
            }
            target.setQNameValue(type);
        }
    }
    
    @Override
    public void xsetType(final XmlQName type) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(AttributeImpl.TYPE$6);
            if (target == null) {
                target = (XmlQName)this.get_store().add_attribute_user(AttributeImpl.TYPE$6);
            }
            target.set(type);
        }
    }
    
    @Override
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(AttributeImpl.TYPE$6);
        }
    }
    
    @Override
    public Use.Enum getUse() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeImpl.USE$8);
            if (target == null) {
                target = (SimpleValue)this.get_default_attribute_value(AttributeImpl.USE$8);
            }
            if (target == null) {
                return null;
            }
            return (Use.Enum)target.getEnumValue();
        }
    }
    
    @Override
    public Use xgetUse() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Use target = null;
            target = (Use)this.get_store().find_attribute_user(AttributeImpl.USE$8);
            if (target == null) {
                target = (Use)this.get_default_attribute_value(AttributeImpl.USE$8);
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetUse() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(AttributeImpl.USE$8) != null;
        }
    }
    
    @Override
    public void setUse(final Use.Enum use) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeImpl.USE$8);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(AttributeImpl.USE$8);
            }
            target.setEnumValue(use);
        }
    }
    
    @Override
    public void xsetUse(final Use use) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Use target = null;
            target = (Use)this.get_store().find_attribute_user(AttributeImpl.USE$8);
            if (target == null) {
                target = (Use)this.get_store().add_attribute_user(AttributeImpl.USE$8);
            }
            target.set(use);
        }
    }
    
    @Override
    public void unsetUse() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(AttributeImpl.USE$8);
        }
    }
    
    @Override
    public String getDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeImpl.DEFAULT$10);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlString xgetDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_attribute_user(AttributeImpl.DEFAULT$10);
            return target;
        }
    }
    
    @Override
    public boolean isSetDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(AttributeImpl.DEFAULT$10) != null;
        }
    }
    
    @Override
    public void setDefault(final String xdefault) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeImpl.DEFAULT$10);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(AttributeImpl.DEFAULT$10);
            }
            target.setStringValue(xdefault);
        }
    }
    
    @Override
    public void xsetDefault(final XmlString xdefault) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_attribute_user(AttributeImpl.DEFAULT$10);
            if (target == null) {
                target = (XmlString)this.get_store().add_attribute_user(AttributeImpl.DEFAULT$10);
            }
            target.set(xdefault);
        }
    }
    
    @Override
    public void unsetDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(AttributeImpl.DEFAULT$10);
        }
    }
    
    @Override
    public String getFixed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeImpl.FIXED$12);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlString xgetFixed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_attribute_user(AttributeImpl.FIXED$12);
            return target;
        }
    }
    
    @Override
    public boolean isSetFixed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(AttributeImpl.FIXED$12) != null;
        }
    }
    
    @Override
    public void setFixed(final String fixed) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeImpl.FIXED$12);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(AttributeImpl.FIXED$12);
            }
            target.setStringValue(fixed);
        }
    }
    
    @Override
    public void xsetFixed(final XmlString fixed) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_attribute_user(AttributeImpl.FIXED$12);
            if (target == null) {
                target = (XmlString)this.get_store().add_attribute_user(AttributeImpl.FIXED$12);
            }
            target.set(fixed);
        }
    }
    
    @Override
    public void unsetFixed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(AttributeImpl.FIXED$12);
        }
    }
    
    @Override
    public FormChoice.Enum getForm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeImpl.FORM$14);
            if (target == null) {
                return null;
            }
            return (FormChoice.Enum)target.getEnumValue();
        }
    }
    
    @Override
    public FormChoice xgetForm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            FormChoice target = null;
            target = (FormChoice)this.get_store().find_attribute_user(AttributeImpl.FORM$14);
            return target;
        }
    }
    
    @Override
    public boolean isSetForm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(AttributeImpl.FORM$14) != null;
        }
    }
    
    @Override
    public void setForm(final FormChoice.Enum form) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeImpl.FORM$14);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(AttributeImpl.FORM$14);
            }
            target.setEnumValue(form);
        }
    }
    
    @Override
    public void xsetForm(final FormChoice form) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            FormChoice target = null;
            target = (FormChoice)this.get_store().find_attribute_user(AttributeImpl.FORM$14);
            if (target == null) {
                target = (FormChoice)this.get_store().add_attribute_user(AttributeImpl.FORM$14);
            }
            target.set(form);
        }
    }
    
    @Override
    public void unsetForm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(AttributeImpl.FORM$14);
        }
    }
    
    static {
        SIMPLETYPE$0 = new QName("http://www.w3.org/2001/XMLSchema", "simpleType");
        NAME$2 = new QName("", "name");
        REF$4 = new QName("", "ref");
        TYPE$6 = new QName("", "type");
        USE$8 = new QName("", "use");
        DEFAULT$10 = new QName("", "default");
        FIXED$12 = new QName("", "fixed");
        FORM$14 = new QName("", "form");
    }
    
    public static class UseImpl extends JavaStringEnumerationHolderEx implements Use
    {
        private static final long serialVersionUID = 1L;
        
        public UseImpl(final SchemaType sType) {
            super(sType, false);
        }
        
        protected UseImpl(final SchemaType sType, final boolean b) {
            super(sType, b);
        }
    }
}
