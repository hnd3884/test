package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.impl.xb.xsdschema.DerivationSet;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.xb.xsdschema.Wildcard;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroupRef;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.apache.xmlbeans.impl.xb.xsdschema.GroupRef;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexContentDocument;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleContentDocument;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexType;

public class ComplexTypeImpl extends AnnotatedImpl implements ComplexType
{
    private static final long serialVersionUID = 1L;
    private static final QName SIMPLECONTENT$0;
    private static final QName COMPLEXCONTENT$2;
    private static final QName GROUP$4;
    private static final QName ALL$6;
    private static final QName CHOICE$8;
    private static final QName SEQUENCE$10;
    private static final QName ATTRIBUTE$12;
    private static final QName ATTRIBUTEGROUP$14;
    private static final QName ANYATTRIBUTE$16;
    private static final QName NAME$18;
    private static final QName MIXED$20;
    private static final QName ABSTRACT$22;
    private static final QName FINAL$24;
    private static final QName BLOCK$26;
    
    public ComplexTypeImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public SimpleContentDocument.SimpleContent getSimpleContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleContentDocument.SimpleContent target = null;
            target = (SimpleContentDocument.SimpleContent)this.get_store().find_element_user(ComplexTypeImpl.SIMPLECONTENT$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetSimpleContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ComplexTypeImpl.SIMPLECONTENT$0) != 0;
        }
    }
    
    @Override
    public void setSimpleContent(final SimpleContentDocument.SimpleContent simpleContent) {
        this.generatedSetterHelperImpl(simpleContent, ComplexTypeImpl.SIMPLECONTENT$0, 0, (short)1);
    }
    
    @Override
    public SimpleContentDocument.SimpleContent addNewSimpleContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleContentDocument.SimpleContent target = null;
            target = (SimpleContentDocument.SimpleContent)this.get_store().add_element_user(ComplexTypeImpl.SIMPLECONTENT$0);
            return target;
        }
    }
    
    @Override
    public void unsetSimpleContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ComplexTypeImpl.SIMPLECONTENT$0, 0);
        }
    }
    
    @Override
    public ComplexContentDocument.ComplexContent getComplexContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ComplexContentDocument.ComplexContent target = null;
            target = (ComplexContentDocument.ComplexContent)this.get_store().find_element_user(ComplexTypeImpl.COMPLEXCONTENT$2, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetComplexContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ComplexTypeImpl.COMPLEXCONTENT$2) != 0;
        }
    }
    
    @Override
    public void setComplexContent(final ComplexContentDocument.ComplexContent complexContent) {
        this.generatedSetterHelperImpl(complexContent, ComplexTypeImpl.COMPLEXCONTENT$2, 0, (short)1);
    }
    
    @Override
    public ComplexContentDocument.ComplexContent addNewComplexContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ComplexContentDocument.ComplexContent target = null;
            target = (ComplexContentDocument.ComplexContent)this.get_store().add_element_user(ComplexTypeImpl.COMPLEXCONTENT$2);
            return target;
        }
    }
    
    @Override
    public void unsetComplexContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ComplexTypeImpl.COMPLEXCONTENT$2, 0);
        }
    }
    
    @Override
    public GroupRef getGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            GroupRef target = null;
            target = (GroupRef)this.get_store().find_element_user(ComplexTypeImpl.GROUP$4, 0);
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
            return this.get_store().count_elements(ComplexTypeImpl.GROUP$4) != 0;
        }
    }
    
    @Override
    public void setGroup(final GroupRef group) {
        this.generatedSetterHelperImpl(group, ComplexTypeImpl.GROUP$4, 0, (short)1);
    }
    
    @Override
    public GroupRef addNewGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            GroupRef target = null;
            target = (GroupRef)this.get_store().add_element_user(ComplexTypeImpl.GROUP$4);
            return target;
        }
    }
    
    @Override
    public void unsetGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ComplexTypeImpl.GROUP$4, 0);
        }
    }
    
    @Override
    public All getAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            All target = null;
            target = (All)this.get_store().find_element_user(ComplexTypeImpl.ALL$6, 0);
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
            return this.get_store().count_elements(ComplexTypeImpl.ALL$6) != 0;
        }
    }
    
    @Override
    public void setAll(final All all) {
        this.generatedSetterHelperImpl(all, ComplexTypeImpl.ALL$6, 0, (short)1);
    }
    
    @Override
    public All addNewAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            All target = null;
            target = (All)this.get_store().add_element_user(ComplexTypeImpl.ALL$6);
            return target;
        }
    }
    
    @Override
    public void unsetAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ComplexTypeImpl.ALL$6, 0);
        }
    }
    
    @Override
    public ExplicitGroup getChoice() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().find_element_user(ComplexTypeImpl.CHOICE$8, 0);
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
            return this.get_store().count_elements(ComplexTypeImpl.CHOICE$8) != 0;
        }
    }
    
    @Override
    public void setChoice(final ExplicitGroup choice) {
        this.generatedSetterHelperImpl(choice, ComplexTypeImpl.CHOICE$8, 0, (short)1);
    }
    
    @Override
    public ExplicitGroup addNewChoice() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().add_element_user(ComplexTypeImpl.CHOICE$8);
            return target;
        }
    }
    
    @Override
    public void unsetChoice() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ComplexTypeImpl.CHOICE$8, 0);
        }
    }
    
    @Override
    public ExplicitGroup getSequence() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().find_element_user(ComplexTypeImpl.SEQUENCE$10, 0);
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
            return this.get_store().count_elements(ComplexTypeImpl.SEQUENCE$10) != 0;
        }
    }
    
    @Override
    public void setSequence(final ExplicitGroup sequence) {
        this.generatedSetterHelperImpl(sequence, ComplexTypeImpl.SEQUENCE$10, 0, (short)1);
    }
    
    @Override
    public ExplicitGroup addNewSequence() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().add_element_user(ComplexTypeImpl.SEQUENCE$10);
            return target;
        }
    }
    
    @Override
    public void unsetSequence() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ComplexTypeImpl.SEQUENCE$10, 0);
        }
    }
    
    @Override
    public Attribute[] getAttributeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(ComplexTypeImpl.ATTRIBUTE$12, targetList);
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
            target = (Attribute)this.get_store().find_element_user(ComplexTypeImpl.ATTRIBUTE$12, i);
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
            return this.get_store().count_elements(ComplexTypeImpl.ATTRIBUTE$12);
        }
    }
    
    @Override
    public void setAttributeArray(final Attribute[] attributeArray) {
        this.check_orphaned();
        this.arraySetterHelper(attributeArray, ComplexTypeImpl.ATTRIBUTE$12);
    }
    
    @Override
    public void setAttributeArray(final int i, final Attribute attribute) {
        this.generatedSetterHelperImpl(attribute, ComplexTypeImpl.ATTRIBUTE$12, i, (short)2);
    }
    
    @Override
    public Attribute insertNewAttribute(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Attribute target = null;
            target = (Attribute)this.get_store().insert_element_user(ComplexTypeImpl.ATTRIBUTE$12, i);
            return target;
        }
    }
    
    @Override
    public Attribute addNewAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Attribute target = null;
            target = (Attribute)this.get_store().add_element_user(ComplexTypeImpl.ATTRIBUTE$12);
            return target;
        }
    }
    
    @Override
    public void removeAttribute(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ComplexTypeImpl.ATTRIBUTE$12, i);
        }
    }
    
    @Override
    public AttributeGroupRef[] getAttributeGroupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(ComplexTypeImpl.ATTRIBUTEGROUP$14, targetList);
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
            target = (AttributeGroupRef)this.get_store().find_element_user(ComplexTypeImpl.ATTRIBUTEGROUP$14, i);
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
            return this.get_store().count_elements(ComplexTypeImpl.ATTRIBUTEGROUP$14);
        }
    }
    
    @Override
    public void setAttributeGroupArray(final AttributeGroupRef[] attributeGroupArray) {
        this.check_orphaned();
        this.arraySetterHelper(attributeGroupArray, ComplexTypeImpl.ATTRIBUTEGROUP$14);
    }
    
    @Override
    public void setAttributeGroupArray(final int i, final AttributeGroupRef attributeGroup) {
        this.generatedSetterHelperImpl(attributeGroup, ComplexTypeImpl.ATTRIBUTEGROUP$14, i, (short)2);
    }
    
    @Override
    public AttributeGroupRef insertNewAttributeGroup(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AttributeGroupRef target = null;
            target = (AttributeGroupRef)this.get_store().insert_element_user(ComplexTypeImpl.ATTRIBUTEGROUP$14, i);
            return target;
        }
    }
    
    @Override
    public AttributeGroupRef addNewAttributeGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AttributeGroupRef target = null;
            target = (AttributeGroupRef)this.get_store().add_element_user(ComplexTypeImpl.ATTRIBUTEGROUP$14);
            return target;
        }
    }
    
    @Override
    public void removeAttributeGroup(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ComplexTypeImpl.ATTRIBUTEGROUP$14, i);
        }
    }
    
    @Override
    public Wildcard getAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Wildcard target = null;
            target = (Wildcard)this.get_store().find_element_user(ComplexTypeImpl.ANYATTRIBUTE$16, 0);
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
            return this.get_store().count_elements(ComplexTypeImpl.ANYATTRIBUTE$16) != 0;
        }
    }
    
    @Override
    public void setAnyAttribute(final Wildcard anyAttribute) {
        this.generatedSetterHelperImpl(anyAttribute, ComplexTypeImpl.ANYATTRIBUTE$16, 0, (short)1);
    }
    
    @Override
    public Wildcard addNewAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Wildcard target = null;
            target = (Wildcard)this.get_store().add_element_user(ComplexTypeImpl.ANYATTRIBUTE$16);
            return target;
        }
    }
    
    @Override
    public void unsetAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ComplexTypeImpl.ANYATTRIBUTE$16, 0);
        }
    }
    
    @Override
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ComplexTypeImpl.NAME$18);
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
            target = (XmlNCName)this.get_store().find_attribute_user(ComplexTypeImpl.NAME$18);
            return target;
        }
    }
    
    @Override
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ComplexTypeImpl.NAME$18) != null;
        }
    }
    
    @Override
    public void setName(final String name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ComplexTypeImpl.NAME$18);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ComplexTypeImpl.NAME$18);
            }
            target.setStringValue(name);
        }
    }
    
    @Override
    public void xsetName(final XmlNCName name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlNCName target = null;
            target = (XmlNCName)this.get_store().find_attribute_user(ComplexTypeImpl.NAME$18);
            if (target == null) {
                target = (XmlNCName)this.get_store().add_attribute_user(ComplexTypeImpl.NAME$18);
            }
            target.set(name);
        }
    }
    
    @Override
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ComplexTypeImpl.NAME$18);
        }
    }
    
    @Override
    public boolean getMixed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ComplexTypeImpl.MIXED$20);
            if (target == null) {
                target = (SimpleValue)this.get_default_attribute_value(ComplexTypeImpl.MIXED$20);
            }
            return target != null && target.getBooleanValue();
        }
    }
    
    @Override
    public XmlBoolean xgetMixed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)this.get_store().find_attribute_user(ComplexTypeImpl.MIXED$20);
            if (target == null) {
                target = (XmlBoolean)this.get_default_attribute_value(ComplexTypeImpl.MIXED$20);
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetMixed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ComplexTypeImpl.MIXED$20) != null;
        }
    }
    
    @Override
    public void setMixed(final boolean mixed) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ComplexTypeImpl.MIXED$20);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ComplexTypeImpl.MIXED$20);
            }
            target.setBooleanValue(mixed);
        }
    }
    
    @Override
    public void xsetMixed(final XmlBoolean mixed) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)this.get_store().find_attribute_user(ComplexTypeImpl.MIXED$20);
            if (target == null) {
                target = (XmlBoolean)this.get_store().add_attribute_user(ComplexTypeImpl.MIXED$20);
            }
            target.set(mixed);
        }
    }
    
    @Override
    public void unsetMixed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ComplexTypeImpl.MIXED$20);
        }
    }
    
    @Override
    public boolean getAbstract() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ComplexTypeImpl.ABSTRACT$22);
            if (target == null) {
                target = (SimpleValue)this.get_default_attribute_value(ComplexTypeImpl.ABSTRACT$22);
            }
            return target != null && target.getBooleanValue();
        }
    }
    
    @Override
    public XmlBoolean xgetAbstract() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)this.get_store().find_attribute_user(ComplexTypeImpl.ABSTRACT$22);
            if (target == null) {
                target = (XmlBoolean)this.get_default_attribute_value(ComplexTypeImpl.ABSTRACT$22);
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetAbstract() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ComplexTypeImpl.ABSTRACT$22) != null;
        }
    }
    
    @Override
    public void setAbstract(final boolean xabstract) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ComplexTypeImpl.ABSTRACT$22);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ComplexTypeImpl.ABSTRACT$22);
            }
            target.setBooleanValue(xabstract);
        }
    }
    
    @Override
    public void xsetAbstract(final XmlBoolean xabstract) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)this.get_store().find_attribute_user(ComplexTypeImpl.ABSTRACT$22);
            if (target == null) {
                target = (XmlBoolean)this.get_store().add_attribute_user(ComplexTypeImpl.ABSTRACT$22);
            }
            target.set(xabstract);
        }
    }
    
    @Override
    public void unsetAbstract() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ComplexTypeImpl.ABSTRACT$22);
        }
    }
    
    @Override
    public Object getFinal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ComplexTypeImpl.FINAL$24);
            if (target == null) {
                return null;
            }
            return target.getObjectValue();
        }
    }
    
    @Override
    public DerivationSet xgetFinal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            DerivationSet target = null;
            target = (DerivationSet)this.get_store().find_attribute_user(ComplexTypeImpl.FINAL$24);
            return target;
        }
    }
    
    @Override
    public boolean isSetFinal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ComplexTypeImpl.FINAL$24) != null;
        }
    }
    
    @Override
    public void setFinal(final Object xfinal) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ComplexTypeImpl.FINAL$24);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ComplexTypeImpl.FINAL$24);
            }
            target.setObjectValue(xfinal);
        }
    }
    
    @Override
    public void xsetFinal(final DerivationSet xfinal) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            DerivationSet target = null;
            target = (DerivationSet)this.get_store().find_attribute_user(ComplexTypeImpl.FINAL$24);
            if (target == null) {
                target = (DerivationSet)this.get_store().add_attribute_user(ComplexTypeImpl.FINAL$24);
            }
            target.set(xfinal);
        }
    }
    
    @Override
    public void unsetFinal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ComplexTypeImpl.FINAL$24);
        }
    }
    
    @Override
    public Object getBlock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ComplexTypeImpl.BLOCK$26);
            if (target == null) {
                return null;
            }
            return target.getObjectValue();
        }
    }
    
    @Override
    public DerivationSet xgetBlock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            DerivationSet target = null;
            target = (DerivationSet)this.get_store().find_attribute_user(ComplexTypeImpl.BLOCK$26);
            return target;
        }
    }
    
    @Override
    public boolean isSetBlock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ComplexTypeImpl.BLOCK$26) != null;
        }
    }
    
    @Override
    public void setBlock(final Object block) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ComplexTypeImpl.BLOCK$26);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ComplexTypeImpl.BLOCK$26);
            }
            target.setObjectValue(block);
        }
    }
    
    @Override
    public void xsetBlock(final DerivationSet block) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            DerivationSet target = null;
            target = (DerivationSet)this.get_store().find_attribute_user(ComplexTypeImpl.BLOCK$26);
            if (target == null) {
                target = (DerivationSet)this.get_store().add_attribute_user(ComplexTypeImpl.BLOCK$26);
            }
            target.set(block);
        }
    }
    
    @Override
    public void unsetBlock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ComplexTypeImpl.BLOCK$26);
        }
    }
    
    static {
        SIMPLECONTENT$0 = new QName("http://www.w3.org/2001/XMLSchema", "simpleContent");
        COMPLEXCONTENT$2 = new QName("http://www.w3.org/2001/XMLSchema", "complexContent");
        GROUP$4 = new QName("http://www.w3.org/2001/XMLSchema", "group");
        ALL$6 = new QName("http://www.w3.org/2001/XMLSchema", "all");
        CHOICE$8 = new QName("http://www.w3.org/2001/XMLSchema", "choice");
        SEQUENCE$10 = new QName("http://www.w3.org/2001/XMLSchema", "sequence");
        ATTRIBUTE$12 = new QName("http://www.w3.org/2001/XMLSchema", "attribute");
        ATTRIBUTEGROUP$14 = new QName("http://www.w3.org/2001/XMLSchema", "attributeGroup");
        ANYATTRIBUTE$16 = new QName("http://www.w3.org/2001/XMLSchema", "anyAttribute");
        NAME$18 = new QName("", "name");
        MIXED$20 = new QName("", "mixed");
        ABSTRACT$22 = new QName("", "abstract");
        FINAL$24 = new QName("", "final");
        BLOCK$26 = new QName("", "block");
    }
}
