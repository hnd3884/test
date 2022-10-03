package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.impl.xb.xsdschema.AllNNI;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.xb.xsdschema.AnyDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.apache.xmlbeans.impl.xb.xsdschema.GroupRef;
import org.apache.xmlbeans.XmlObject;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalElement;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.Group;

public class GroupImpl extends AnnotatedImpl implements Group
{
    private static final long serialVersionUID = 1L;
    private static final QName ELEMENT$0;
    private static final QName GROUP$2;
    private static final QName ALL$4;
    private static final QName CHOICE$6;
    private static final QName SEQUENCE$8;
    private static final QName ANY$10;
    private static final QName NAME$12;
    private static final QName REF$14;
    private static final QName MINOCCURS$16;
    private static final QName MAXOCCURS$18;
    
    public GroupImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public LocalElement[] getElementArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(GroupImpl.ELEMENT$0, targetList);
            final LocalElement[] result = new LocalElement[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public LocalElement getElementArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            LocalElement target = null;
            target = (LocalElement)this.get_store().find_element_user(GroupImpl.ELEMENT$0, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfElementArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(GroupImpl.ELEMENT$0);
        }
    }
    
    @Override
    public void setElementArray(final LocalElement[] elementArray) {
        this.check_orphaned();
        this.arraySetterHelper(elementArray, GroupImpl.ELEMENT$0);
    }
    
    @Override
    public void setElementArray(final int i, final LocalElement element) {
        this.generatedSetterHelperImpl(element, GroupImpl.ELEMENT$0, i, (short)2);
    }
    
    @Override
    public LocalElement insertNewElement(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            LocalElement target = null;
            target = (LocalElement)this.get_store().insert_element_user(GroupImpl.ELEMENT$0, i);
            return target;
        }
    }
    
    @Override
    public LocalElement addNewElement() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            LocalElement target = null;
            target = (LocalElement)this.get_store().add_element_user(GroupImpl.ELEMENT$0);
            return target;
        }
    }
    
    @Override
    public void removeElement(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(GroupImpl.ELEMENT$0, i);
        }
    }
    
    @Override
    public GroupRef[] getGroupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(GroupImpl.GROUP$2, targetList);
            final GroupRef[] result = new GroupRef[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public GroupRef getGroupArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            GroupRef target = null;
            target = (GroupRef)this.get_store().find_element_user(GroupImpl.GROUP$2, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfGroupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(GroupImpl.GROUP$2);
        }
    }
    
    @Override
    public void setGroupArray(final GroupRef[] groupArray) {
        this.check_orphaned();
        this.arraySetterHelper(groupArray, GroupImpl.GROUP$2);
    }
    
    @Override
    public void setGroupArray(final int i, final GroupRef group) {
        this.generatedSetterHelperImpl(group, GroupImpl.GROUP$2, i, (short)2);
    }
    
    @Override
    public GroupRef insertNewGroup(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            GroupRef target = null;
            target = (GroupRef)this.get_store().insert_element_user(GroupImpl.GROUP$2, i);
            return target;
        }
    }
    
    @Override
    public GroupRef addNewGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            GroupRef target = null;
            target = (GroupRef)this.get_store().add_element_user(GroupImpl.GROUP$2);
            return target;
        }
    }
    
    @Override
    public void removeGroup(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(GroupImpl.GROUP$2, i);
        }
    }
    
    @Override
    public All[] getAllArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(GroupImpl.ALL$4, targetList);
            final All[] result = new All[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public All getAllArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            All target = null;
            target = (All)this.get_store().find_element_user(GroupImpl.ALL$4, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfAllArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(GroupImpl.ALL$4);
        }
    }
    
    @Override
    public void setAllArray(final All[] allArray) {
        this.check_orphaned();
        this.arraySetterHelper(allArray, GroupImpl.ALL$4);
    }
    
    @Override
    public void setAllArray(final int i, final All all) {
        this.generatedSetterHelperImpl(all, GroupImpl.ALL$4, i, (short)2);
    }
    
    @Override
    public All insertNewAll(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            All target = null;
            target = (All)this.get_store().insert_element_user(GroupImpl.ALL$4, i);
            return target;
        }
    }
    
    @Override
    public All addNewAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            All target = null;
            target = (All)this.get_store().add_element_user(GroupImpl.ALL$4);
            return target;
        }
    }
    
    @Override
    public void removeAll(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(GroupImpl.ALL$4, i);
        }
    }
    
    @Override
    public ExplicitGroup[] getChoiceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(GroupImpl.CHOICE$6, targetList);
            final ExplicitGroup[] result = new ExplicitGroup[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public ExplicitGroup getChoiceArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().find_element_user(GroupImpl.CHOICE$6, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfChoiceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(GroupImpl.CHOICE$6);
        }
    }
    
    @Override
    public void setChoiceArray(final ExplicitGroup[] choiceArray) {
        this.check_orphaned();
        this.arraySetterHelper(choiceArray, GroupImpl.CHOICE$6);
    }
    
    @Override
    public void setChoiceArray(final int i, final ExplicitGroup choice) {
        this.generatedSetterHelperImpl(choice, GroupImpl.CHOICE$6, i, (short)2);
    }
    
    @Override
    public ExplicitGroup insertNewChoice(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().insert_element_user(GroupImpl.CHOICE$6, i);
            return target;
        }
    }
    
    @Override
    public ExplicitGroup addNewChoice() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().add_element_user(GroupImpl.CHOICE$6);
            return target;
        }
    }
    
    @Override
    public void removeChoice(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(GroupImpl.CHOICE$6, i);
        }
    }
    
    @Override
    public ExplicitGroup[] getSequenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(GroupImpl.SEQUENCE$8, targetList);
            final ExplicitGroup[] result = new ExplicitGroup[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public ExplicitGroup getSequenceArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().find_element_user(GroupImpl.SEQUENCE$8, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfSequenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(GroupImpl.SEQUENCE$8);
        }
    }
    
    @Override
    public void setSequenceArray(final ExplicitGroup[] sequenceArray) {
        this.check_orphaned();
        this.arraySetterHelper(sequenceArray, GroupImpl.SEQUENCE$8);
    }
    
    @Override
    public void setSequenceArray(final int i, final ExplicitGroup sequence) {
        this.generatedSetterHelperImpl(sequence, GroupImpl.SEQUENCE$8, i, (short)2);
    }
    
    @Override
    public ExplicitGroup insertNewSequence(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().insert_element_user(GroupImpl.SEQUENCE$8, i);
            return target;
        }
    }
    
    @Override
    public ExplicitGroup addNewSequence() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().add_element_user(GroupImpl.SEQUENCE$8);
            return target;
        }
    }
    
    @Override
    public void removeSequence(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(GroupImpl.SEQUENCE$8, i);
        }
    }
    
    @Override
    public AnyDocument.Any[] getAnyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(GroupImpl.ANY$10, targetList);
            final AnyDocument.Any[] result = new AnyDocument.Any[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public AnyDocument.Any getAnyArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AnyDocument.Any target = null;
            target = (AnyDocument.Any)this.get_store().find_element_user(GroupImpl.ANY$10, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfAnyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(GroupImpl.ANY$10);
        }
    }
    
    @Override
    public void setAnyArray(final AnyDocument.Any[] anyArray) {
        this.check_orphaned();
        this.arraySetterHelper(anyArray, GroupImpl.ANY$10);
    }
    
    @Override
    public void setAnyArray(final int i, final AnyDocument.Any any) {
        this.generatedSetterHelperImpl(any, GroupImpl.ANY$10, i, (short)2);
    }
    
    @Override
    public AnyDocument.Any insertNewAny(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AnyDocument.Any target = null;
            target = (AnyDocument.Any)this.get_store().insert_element_user(GroupImpl.ANY$10, i);
            return target;
        }
    }
    
    @Override
    public AnyDocument.Any addNewAny() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AnyDocument.Any target = null;
            target = (AnyDocument.Any)this.get_store().add_element_user(GroupImpl.ANY$10);
            return target;
        }
    }
    
    @Override
    public void removeAny(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(GroupImpl.ANY$10, i);
        }
    }
    
    @Override
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(GroupImpl.NAME$12);
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
            target = (XmlNCName)this.get_store().find_attribute_user(GroupImpl.NAME$12);
            return target;
        }
    }
    
    @Override
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(GroupImpl.NAME$12) != null;
        }
    }
    
    @Override
    public void setName(final String name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(GroupImpl.NAME$12);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(GroupImpl.NAME$12);
            }
            target.setStringValue(name);
        }
    }
    
    @Override
    public void xsetName(final XmlNCName name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlNCName target = null;
            target = (XmlNCName)this.get_store().find_attribute_user(GroupImpl.NAME$12);
            if (target == null) {
                target = (XmlNCName)this.get_store().add_attribute_user(GroupImpl.NAME$12);
            }
            target.set(name);
        }
    }
    
    @Override
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(GroupImpl.NAME$12);
        }
    }
    
    @Override
    public QName getRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(GroupImpl.REF$14);
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
            target = (XmlQName)this.get_store().find_attribute_user(GroupImpl.REF$14);
            return target;
        }
    }
    
    @Override
    public boolean isSetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(GroupImpl.REF$14) != null;
        }
    }
    
    @Override
    public void setRef(final QName ref) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(GroupImpl.REF$14);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(GroupImpl.REF$14);
            }
            target.setQNameValue(ref);
        }
    }
    
    @Override
    public void xsetRef(final XmlQName ref) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(GroupImpl.REF$14);
            if (target == null) {
                target = (XmlQName)this.get_store().add_attribute_user(GroupImpl.REF$14);
            }
            target.set(ref);
        }
    }
    
    @Override
    public void unsetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(GroupImpl.REF$14);
        }
    }
    
    @Override
    public BigInteger getMinOccurs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(GroupImpl.MINOCCURS$16);
            if (target == null) {
                target = (SimpleValue)this.get_default_attribute_value(GroupImpl.MINOCCURS$16);
            }
            if (target == null) {
                return null;
            }
            return target.getBigIntegerValue();
        }
    }
    
    @Override
    public XmlNonNegativeInteger xgetMinOccurs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlNonNegativeInteger target = null;
            target = (XmlNonNegativeInteger)this.get_store().find_attribute_user(GroupImpl.MINOCCURS$16);
            if (target == null) {
                target = (XmlNonNegativeInteger)this.get_default_attribute_value(GroupImpl.MINOCCURS$16);
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetMinOccurs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(GroupImpl.MINOCCURS$16) != null;
        }
    }
    
    @Override
    public void setMinOccurs(final BigInteger minOccurs) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(GroupImpl.MINOCCURS$16);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(GroupImpl.MINOCCURS$16);
            }
            target.setBigIntegerValue(minOccurs);
        }
    }
    
    @Override
    public void xsetMinOccurs(final XmlNonNegativeInteger minOccurs) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlNonNegativeInteger target = null;
            target = (XmlNonNegativeInteger)this.get_store().find_attribute_user(GroupImpl.MINOCCURS$16);
            if (target == null) {
                target = (XmlNonNegativeInteger)this.get_store().add_attribute_user(GroupImpl.MINOCCURS$16);
            }
            target.set(minOccurs);
        }
    }
    
    @Override
    public void unsetMinOccurs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(GroupImpl.MINOCCURS$16);
        }
    }
    
    @Override
    public Object getMaxOccurs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(GroupImpl.MAXOCCURS$18);
            if (target == null) {
                target = (SimpleValue)this.get_default_attribute_value(GroupImpl.MAXOCCURS$18);
            }
            if (target == null) {
                return null;
            }
            return target.getObjectValue();
        }
    }
    
    @Override
    public AllNNI xgetMaxOccurs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AllNNI target = null;
            target = (AllNNI)this.get_store().find_attribute_user(GroupImpl.MAXOCCURS$18);
            if (target == null) {
                target = (AllNNI)this.get_default_attribute_value(GroupImpl.MAXOCCURS$18);
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetMaxOccurs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(GroupImpl.MAXOCCURS$18) != null;
        }
    }
    
    @Override
    public void setMaxOccurs(final Object maxOccurs) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(GroupImpl.MAXOCCURS$18);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(GroupImpl.MAXOCCURS$18);
            }
            target.setObjectValue(maxOccurs);
        }
    }
    
    @Override
    public void xsetMaxOccurs(final AllNNI maxOccurs) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AllNNI target = null;
            target = (AllNNI)this.get_store().find_attribute_user(GroupImpl.MAXOCCURS$18);
            if (target == null) {
                target = (AllNNI)this.get_store().add_attribute_user(GroupImpl.MAXOCCURS$18);
            }
            target.set(maxOccurs);
        }
    }
    
    @Override
    public void unsetMaxOccurs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(GroupImpl.MAXOCCURS$18);
        }
    }
    
    static {
        ELEMENT$0 = new QName("http://www.w3.org/2001/XMLSchema", "element");
        GROUP$2 = new QName("http://www.w3.org/2001/XMLSchema", "group");
        ALL$4 = new QName("http://www.w3.org/2001/XMLSchema", "all");
        CHOICE$6 = new QName("http://www.w3.org/2001/XMLSchema", "choice");
        SEQUENCE$8 = new QName("http://www.w3.org/2001/XMLSchema", "sequence");
        ANY$10 = new QName("http://www.w3.org/2001/XMLSchema", "any");
        NAME$12 = new QName("", "name");
        REF$14 = new QName("", "ref");
        MINOCCURS$16 = new QName("", "minOccurs");
        MAXOCCURS$18 = new QName("", "maxOccurs");
    }
}
