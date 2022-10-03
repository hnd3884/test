package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.xb.xsdschema.Wildcard;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroupRef;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.GroupRef;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.ExtensionType;

public class ExtensionTypeImpl extends AnnotatedImpl implements ExtensionType
{
    private static final long serialVersionUID = 1L;
    private static final QName GROUP$0;
    private static final QName ALL$2;
    private static final QName CHOICE$4;
    private static final QName SEQUENCE$6;
    private static final QName ATTRIBUTE$8;
    private static final QName ATTRIBUTEGROUP$10;
    private static final QName ANYATTRIBUTE$12;
    private static final QName BASE$14;
    
    public ExtensionTypeImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public GroupRef getGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            GroupRef target = null;
            target = (GroupRef)this.get_store().find_element_user(ExtensionTypeImpl.GROUP$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ExtensionTypeImpl.GROUP$0) != 0;
        }
    }
    
    @Override
    public void setGroup(final GroupRef group) {
        this.generatedSetterHelperImpl(group, ExtensionTypeImpl.GROUP$0, 0, (short)1);
    }
    
    @Override
    public GroupRef addNewGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            GroupRef target = null;
            target = (GroupRef)this.get_store().add_element_user(ExtensionTypeImpl.GROUP$0);
            return target;
        }
    }
    
    @Override
    public void unsetGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ExtensionTypeImpl.GROUP$0, 0);
        }
    }
    
    @Override
    public All getAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            All target = null;
            target = (All)this.get_store().find_element_user(ExtensionTypeImpl.ALL$2, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ExtensionTypeImpl.ALL$2) != 0;
        }
    }
    
    @Override
    public void setAll(final All all) {
        this.generatedSetterHelperImpl(all, ExtensionTypeImpl.ALL$2, 0, (short)1);
    }
    
    @Override
    public All addNewAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            All target = null;
            target = (All)this.get_store().add_element_user(ExtensionTypeImpl.ALL$2);
            return target;
        }
    }
    
    @Override
    public void unsetAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ExtensionTypeImpl.ALL$2, 0);
        }
    }
    
    @Override
    public ExplicitGroup getChoice() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().find_element_user(ExtensionTypeImpl.CHOICE$4, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetChoice() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ExtensionTypeImpl.CHOICE$4) != 0;
        }
    }
    
    @Override
    public void setChoice(final ExplicitGroup choice) {
        this.generatedSetterHelperImpl(choice, ExtensionTypeImpl.CHOICE$4, 0, (short)1);
    }
    
    @Override
    public ExplicitGroup addNewChoice() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().add_element_user(ExtensionTypeImpl.CHOICE$4);
            return target;
        }
    }
    
    @Override
    public void unsetChoice() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ExtensionTypeImpl.CHOICE$4, 0);
        }
    }
    
    @Override
    public ExplicitGroup getSequence() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().find_element_user(ExtensionTypeImpl.SEQUENCE$6, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetSequence() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ExtensionTypeImpl.SEQUENCE$6) != 0;
        }
    }
    
    @Override
    public void setSequence(final ExplicitGroup sequence) {
        this.generatedSetterHelperImpl(sequence, ExtensionTypeImpl.SEQUENCE$6, 0, (short)1);
    }
    
    @Override
    public ExplicitGroup addNewSequence() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().add_element_user(ExtensionTypeImpl.SEQUENCE$6);
            return target;
        }
    }
    
    @Override
    public void unsetSequence() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ExtensionTypeImpl.SEQUENCE$6, 0);
        }
    }
    
    @Override
    public Attribute[] getAttributeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(ExtensionTypeImpl.ATTRIBUTE$8, targetList);
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
            target = (Attribute)this.get_store().find_element_user(ExtensionTypeImpl.ATTRIBUTE$8, i);
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
            return this.get_store().count_elements(ExtensionTypeImpl.ATTRIBUTE$8);
        }
    }
    
    @Override
    public void setAttributeArray(final Attribute[] attributeArray) {
        this.check_orphaned();
        this.arraySetterHelper(attributeArray, ExtensionTypeImpl.ATTRIBUTE$8);
    }
    
    @Override
    public void setAttributeArray(final int i, final Attribute attribute) {
        this.generatedSetterHelperImpl(attribute, ExtensionTypeImpl.ATTRIBUTE$8, i, (short)2);
    }
    
    @Override
    public Attribute insertNewAttribute(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Attribute target = null;
            target = (Attribute)this.get_store().insert_element_user(ExtensionTypeImpl.ATTRIBUTE$8, i);
            return target;
        }
    }
    
    @Override
    public Attribute addNewAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Attribute target = null;
            target = (Attribute)this.get_store().add_element_user(ExtensionTypeImpl.ATTRIBUTE$8);
            return target;
        }
    }
    
    @Override
    public void removeAttribute(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ExtensionTypeImpl.ATTRIBUTE$8, i);
        }
    }
    
    @Override
    public AttributeGroupRef[] getAttributeGroupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(ExtensionTypeImpl.ATTRIBUTEGROUP$10, targetList);
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
            target = (AttributeGroupRef)this.get_store().find_element_user(ExtensionTypeImpl.ATTRIBUTEGROUP$10, i);
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
            return this.get_store().count_elements(ExtensionTypeImpl.ATTRIBUTEGROUP$10);
        }
    }
    
    @Override
    public void setAttributeGroupArray(final AttributeGroupRef[] attributeGroupArray) {
        this.check_orphaned();
        this.arraySetterHelper(attributeGroupArray, ExtensionTypeImpl.ATTRIBUTEGROUP$10);
    }
    
    @Override
    public void setAttributeGroupArray(final int i, final AttributeGroupRef attributeGroup) {
        this.generatedSetterHelperImpl(attributeGroup, ExtensionTypeImpl.ATTRIBUTEGROUP$10, i, (short)2);
    }
    
    @Override
    public AttributeGroupRef insertNewAttributeGroup(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AttributeGroupRef target = null;
            target = (AttributeGroupRef)this.get_store().insert_element_user(ExtensionTypeImpl.ATTRIBUTEGROUP$10, i);
            return target;
        }
    }
    
    @Override
    public AttributeGroupRef addNewAttributeGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AttributeGroupRef target = null;
            target = (AttributeGroupRef)this.get_store().add_element_user(ExtensionTypeImpl.ATTRIBUTEGROUP$10);
            return target;
        }
    }
    
    @Override
    public void removeAttributeGroup(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ExtensionTypeImpl.ATTRIBUTEGROUP$10, i);
        }
    }
    
    @Override
    public Wildcard getAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Wildcard target = null;
            target = (Wildcard)this.get_store().find_element_user(ExtensionTypeImpl.ANYATTRIBUTE$12, 0);
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
            return this.get_store().count_elements(ExtensionTypeImpl.ANYATTRIBUTE$12) != 0;
        }
    }
    
    @Override
    public void setAnyAttribute(final Wildcard anyAttribute) {
        this.generatedSetterHelperImpl(anyAttribute, ExtensionTypeImpl.ANYATTRIBUTE$12, 0, (short)1);
    }
    
    @Override
    public Wildcard addNewAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Wildcard target = null;
            target = (Wildcard)this.get_store().add_element_user(ExtensionTypeImpl.ANYATTRIBUTE$12);
            return target;
        }
    }
    
    @Override
    public void unsetAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ExtensionTypeImpl.ANYATTRIBUTE$12, 0);
        }
    }
    
    @Override
    public QName getBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ExtensionTypeImpl.BASE$14);
            if (target == null) {
                return null;
            }
            return target.getQNameValue();
        }
    }
    
    @Override
    public XmlQName xgetBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(ExtensionTypeImpl.BASE$14);
            return target;
        }
    }
    
    @Override
    public void setBase(final QName base) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ExtensionTypeImpl.BASE$14);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ExtensionTypeImpl.BASE$14);
            }
            target.setQNameValue(base);
        }
    }
    
    @Override
    public void xsetBase(final XmlQName base) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(ExtensionTypeImpl.BASE$14);
            if (target == null) {
                target = (XmlQName)this.get_store().add_attribute_user(ExtensionTypeImpl.BASE$14);
            }
            target.set(base);
        }
    }
    
    static {
        GROUP$0 = new QName("http://www.w3.org/2001/XMLSchema", "group");
        ALL$2 = new QName("http://www.w3.org/2001/XMLSchema", "all");
        CHOICE$4 = new QName("http://www.w3.org/2001/XMLSchema", "choice");
        SEQUENCE$6 = new QName("http://www.w3.org/2001/XMLSchema", "sequence");
        ATTRIBUTE$8 = new QName("http://www.w3.org/2001/XMLSchema", "attribute");
        ATTRIBUTEGROUP$10 = new QName("http://www.w3.org/2001/XMLSchema", "attributeGroup");
        ANYATTRIBUTE$12 = new QName("http://www.w3.org/2001/XMLSchema", "anyAttribute");
        BASE$14 = new QName("", "base");
    }
}
