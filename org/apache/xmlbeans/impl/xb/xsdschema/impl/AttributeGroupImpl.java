package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.xb.xsdschema.Wildcard;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroupRef;
import org.apache.xmlbeans.XmlObject;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroup;

public class AttributeGroupImpl extends AnnotatedImpl implements AttributeGroup
{
    private static final long serialVersionUID = 1L;
    private static final QName ATTRIBUTE$0;
    private static final QName ATTRIBUTEGROUP$2;
    private static final QName ANYATTRIBUTE$4;
    private static final QName NAME$6;
    private static final QName REF$8;
    
    public AttributeGroupImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Attribute[] getAttributeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(AttributeGroupImpl.ATTRIBUTE$0, targetList);
            final Attribute[] result = new Attribute[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public Attribute getAttributeArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Attribute target = null;
            target = (Attribute)this.get_store().find_element_user(AttributeGroupImpl.ATTRIBUTE$0, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfAttributeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(AttributeGroupImpl.ATTRIBUTE$0);
        }
    }
    
    @Override
    public void setAttributeArray(final Attribute[] attributeArray) {
        this.check_orphaned();
        this.arraySetterHelper(attributeArray, AttributeGroupImpl.ATTRIBUTE$0);
    }
    
    @Override
    public void setAttributeArray(final int i, final Attribute attribute) {
        this.generatedSetterHelperImpl(attribute, AttributeGroupImpl.ATTRIBUTE$0, i, (short)2);
    }
    
    @Override
    public Attribute insertNewAttribute(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Attribute target = null;
            target = (Attribute)this.get_store().insert_element_user(AttributeGroupImpl.ATTRIBUTE$0, i);
            return target;
        }
    }
    
    @Override
    public Attribute addNewAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Attribute target = null;
            target = (Attribute)this.get_store().add_element_user(AttributeGroupImpl.ATTRIBUTE$0);
            return target;
        }
    }
    
    @Override
    public void removeAttribute(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(AttributeGroupImpl.ATTRIBUTE$0, i);
        }
    }
    
    @Override
    public AttributeGroupRef[] getAttributeGroupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(AttributeGroupImpl.ATTRIBUTEGROUP$2, targetList);
            final AttributeGroupRef[] result = new AttributeGroupRef[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public AttributeGroupRef getAttributeGroupArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AttributeGroupRef target = null;
            target = (AttributeGroupRef)this.get_store().find_element_user(AttributeGroupImpl.ATTRIBUTEGROUP$2, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfAttributeGroupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(AttributeGroupImpl.ATTRIBUTEGROUP$2);
        }
    }
    
    @Override
    public void setAttributeGroupArray(final AttributeGroupRef[] attributeGroupArray) {
        this.check_orphaned();
        this.arraySetterHelper(attributeGroupArray, AttributeGroupImpl.ATTRIBUTEGROUP$2);
    }
    
    @Override
    public void setAttributeGroupArray(final int i, final AttributeGroupRef attributeGroup) {
        this.generatedSetterHelperImpl(attributeGroup, AttributeGroupImpl.ATTRIBUTEGROUP$2, i, (short)2);
    }
    
    @Override
    public AttributeGroupRef insertNewAttributeGroup(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AttributeGroupRef target = null;
            target = (AttributeGroupRef)this.get_store().insert_element_user(AttributeGroupImpl.ATTRIBUTEGROUP$2, i);
            return target;
        }
    }
    
    @Override
    public AttributeGroupRef addNewAttributeGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AttributeGroupRef target = null;
            target = (AttributeGroupRef)this.get_store().add_element_user(AttributeGroupImpl.ATTRIBUTEGROUP$2);
            return target;
        }
    }
    
    @Override
    public void removeAttributeGroup(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(AttributeGroupImpl.ATTRIBUTEGROUP$2, i);
        }
    }
    
    @Override
    public Wildcard getAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Wildcard target = null;
            target = (Wildcard)this.get_store().find_element_user(AttributeGroupImpl.ANYATTRIBUTE$4, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(AttributeGroupImpl.ANYATTRIBUTE$4) != 0;
        }
    }
    
    @Override
    public void setAnyAttribute(final Wildcard anyAttribute) {
        this.generatedSetterHelperImpl(anyAttribute, AttributeGroupImpl.ANYATTRIBUTE$4, 0, (short)1);
    }
    
    @Override
    public Wildcard addNewAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Wildcard target = null;
            target = (Wildcard)this.get_store().add_element_user(AttributeGroupImpl.ANYATTRIBUTE$4);
            return target;
        }
    }
    
    @Override
    public void unsetAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(AttributeGroupImpl.ANYATTRIBUTE$4, 0);
        }
    }
    
    @Override
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeGroupImpl.NAME$6);
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
            target = (XmlNCName)this.get_store().find_attribute_user(AttributeGroupImpl.NAME$6);
            return target;
        }
    }
    
    @Override
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(AttributeGroupImpl.NAME$6) != null;
        }
    }
    
    @Override
    public void setName(final String name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeGroupImpl.NAME$6);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(AttributeGroupImpl.NAME$6);
            }
            target.setStringValue(name);
        }
    }
    
    @Override
    public void xsetName(final XmlNCName name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlNCName target = null;
            target = (XmlNCName)this.get_store().find_attribute_user(AttributeGroupImpl.NAME$6);
            if (target == null) {
                target = (XmlNCName)this.get_store().add_attribute_user(AttributeGroupImpl.NAME$6);
            }
            target.set(name);
        }
    }
    
    @Override
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(AttributeGroupImpl.NAME$6);
        }
    }
    
    @Override
    public QName getRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeGroupImpl.REF$8);
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
            target = (XmlQName)this.get_store().find_attribute_user(AttributeGroupImpl.REF$8);
            return target;
        }
    }
    
    @Override
    public boolean isSetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(AttributeGroupImpl.REF$8) != null;
        }
    }
    
    @Override
    public void setRef(final QName ref) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(AttributeGroupImpl.REF$8);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(AttributeGroupImpl.REF$8);
            }
            target.setQNameValue(ref);
        }
    }
    
    @Override
    public void xsetRef(final XmlQName ref) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(AttributeGroupImpl.REF$8);
            if (target == null) {
                target = (XmlQName)this.get_store().add_attribute_user(AttributeGroupImpl.REF$8);
            }
            target.set(ref);
        }
    }
    
    @Override
    public void unsetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(AttributeGroupImpl.REF$8);
        }
    }
    
    static {
        ATTRIBUTE$0 = new QName("http://www.w3.org/2001/XMLSchema", "attribute");
        ATTRIBUTEGROUP$2 = new QName("http://www.w3.org/2001/XMLSchema", "attributeGroup");
        ANYATTRIBUTE$4 = new QName("http://www.w3.org/2001/XMLSchema", "anyAttribute");
        NAME$6 = new QName("", "name");
        REF$8 = new QName("", "ref");
    }
}
