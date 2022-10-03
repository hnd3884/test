package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.impl.xb.xsdschema.FormChoice;
import org.apache.xmlbeans.impl.xb.xsdschema.BlockSet;
import org.apache.xmlbeans.impl.xb.xsdschema.DerivationSet;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.xb.xsdschema.AllNNI;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.xb.xsdschema.KeyrefDocument;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xsdschema.Keybase;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalComplexType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.Element;

public class ElementImpl extends AnnotatedImpl implements Element
{
    private static final long serialVersionUID = 1L;
    private static final QName SIMPLETYPE$0;
    private static final QName COMPLEXTYPE$2;
    private static final QName UNIQUE$4;
    private static final QName KEY$6;
    private static final QName KEYREF$8;
    private static final QName NAME$10;
    private static final QName REF$12;
    private static final QName TYPE$14;
    private static final QName SUBSTITUTIONGROUP$16;
    private static final QName MINOCCURS$18;
    private static final QName MAXOCCURS$20;
    private static final QName DEFAULT$22;
    private static final QName FIXED$24;
    private static final QName NILLABLE$26;
    private static final QName ABSTRACT$28;
    private static final QName FINAL$30;
    private static final QName BLOCK$32;
    private static final QName FORM$34;
    
    public ElementImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public LocalSimpleType getSimpleType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            LocalSimpleType target = null;
            target = (LocalSimpleType)this.get_store().find_element_user(ElementImpl.SIMPLETYPE$0, 0);
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
            return this.get_store().count_elements(ElementImpl.SIMPLETYPE$0) != 0;
        }
    }
    
    @Override
    public void setSimpleType(final LocalSimpleType simpleType) {
        this.generatedSetterHelperImpl(simpleType, ElementImpl.SIMPLETYPE$0, 0, (short)1);
    }
    
    @Override
    public LocalSimpleType addNewSimpleType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            LocalSimpleType target = null;
            target = (LocalSimpleType)this.get_store().add_element_user(ElementImpl.SIMPLETYPE$0);
            return target;
        }
    }
    
    @Override
    public void unsetSimpleType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ElementImpl.SIMPLETYPE$0, 0);
        }
    }
    
    @Override
    public LocalComplexType getComplexType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            LocalComplexType target = null;
            target = (LocalComplexType)this.get_store().find_element_user(ElementImpl.COMPLEXTYPE$2, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetComplexType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ElementImpl.COMPLEXTYPE$2) != 0;
        }
    }
    
    @Override
    public void setComplexType(final LocalComplexType complexType) {
        this.generatedSetterHelperImpl(complexType, ElementImpl.COMPLEXTYPE$2, 0, (short)1);
    }
    
    @Override
    public LocalComplexType addNewComplexType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            LocalComplexType target = null;
            target = (LocalComplexType)this.get_store().add_element_user(ElementImpl.COMPLEXTYPE$2);
            return target;
        }
    }
    
    @Override
    public void unsetComplexType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ElementImpl.COMPLEXTYPE$2, 0);
        }
    }
    
    @Override
    public Keybase[] getUniqueArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(ElementImpl.UNIQUE$4, targetList);
            final Keybase[] result = new Keybase[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public Keybase getUniqueArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Keybase target = null;
            target = (Keybase)this.get_store().find_element_user(ElementImpl.UNIQUE$4, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfUniqueArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ElementImpl.UNIQUE$4);
        }
    }
    
    @Override
    public void setUniqueArray(final Keybase[] uniqueArray) {
        this.check_orphaned();
        this.arraySetterHelper(uniqueArray, ElementImpl.UNIQUE$4);
    }
    
    @Override
    public void setUniqueArray(final int i, final Keybase unique) {
        this.generatedSetterHelperImpl(unique, ElementImpl.UNIQUE$4, i, (short)2);
    }
    
    @Override
    public Keybase insertNewUnique(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Keybase target = null;
            target = (Keybase)this.get_store().insert_element_user(ElementImpl.UNIQUE$4, i);
            return target;
        }
    }
    
    @Override
    public Keybase addNewUnique() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Keybase target = null;
            target = (Keybase)this.get_store().add_element_user(ElementImpl.UNIQUE$4);
            return target;
        }
    }
    
    @Override
    public void removeUnique(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ElementImpl.UNIQUE$4, i);
        }
    }
    
    @Override
    public Keybase[] getKeyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(ElementImpl.KEY$6, targetList);
            final Keybase[] result = new Keybase[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public Keybase getKeyArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Keybase target = null;
            target = (Keybase)this.get_store().find_element_user(ElementImpl.KEY$6, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfKeyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ElementImpl.KEY$6);
        }
    }
    
    @Override
    public void setKeyArray(final Keybase[] keyArray) {
        this.check_orphaned();
        this.arraySetterHelper(keyArray, ElementImpl.KEY$6);
    }
    
    @Override
    public void setKeyArray(final int i, final Keybase key) {
        this.generatedSetterHelperImpl(key, ElementImpl.KEY$6, i, (short)2);
    }
    
    @Override
    public Keybase insertNewKey(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Keybase target = null;
            target = (Keybase)this.get_store().insert_element_user(ElementImpl.KEY$6, i);
            return target;
        }
    }
    
    @Override
    public Keybase addNewKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Keybase target = null;
            target = (Keybase)this.get_store().add_element_user(ElementImpl.KEY$6);
            return target;
        }
    }
    
    @Override
    public void removeKey(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ElementImpl.KEY$6, i);
        }
    }
    
    @Override
    public KeyrefDocument.Keyref[] getKeyrefArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(ElementImpl.KEYREF$8, targetList);
            final KeyrefDocument.Keyref[] result = new KeyrefDocument.Keyref[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public KeyrefDocument.Keyref getKeyrefArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            KeyrefDocument.Keyref target = null;
            target = (KeyrefDocument.Keyref)this.get_store().find_element_user(ElementImpl.KEYREF$8, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfKeyrefArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ElementImpl.KEYREF$8);
        }
    }
    
    @Override
    public void setKeyrefArray(final KeyrefDocument.Keyref[] keyrefArray) {
        this.check_orphaned();
        this.arraySetterHelper(keyrefArray, ElementImpl.KEYREF$8);
    }
    
    @Override
    public void setKeyrefArray(final int i, final KeyrefDocument.Keyref keyref) {
        this.generatedSetterHelperImpl(keyref, ElementImpl.KEYREF$8, i, (short)2);
    }
    
    @Override
    public KeyrefDocument.Keyref insertNewKeyref(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            KeyrefDocument.Keyref target = null;
            target = (KeyrefDocument.Keyref)this.get_store().insert_element_user(ElementImpl.KEYREF$8, i);
            return target;
        }
    }
    
    @Override
    public KeyrefDocument.Keyref addNewKeyref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            KeyrefDocument.Keyref target = null;
            target = (KeyrefDocument.Keyref)this.get_store().add_element_user(ElementImpl.KEYREF$8);
            return target;
        }
    }
    
    @Override
    public void removeKeyref(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ElementImpl.KEYREF$8, i);
        }
    }
    
    @Override
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.NAME$10);
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
            target = (XmlNCName)this.get_store().find_attribute_user(ElementImpl.NAME$10);
            return target;
        }
    }
    
    @Override
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ElementImpl.NAME$10) != null;
        }
    }
    
    @Override
    public void setName(final String name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.NAME$10);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ElementImpl.NAME$10);
            }
            target.setStringValue(name);
        }
    }
    
    @Override
    public void xsetName(final XmlNCName name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlNCName target = null;
            target = (XmlNCName)this.get_store().find_attribute_user(ElementImpl.NAME$10);
            if (target == null) {
                target = (XmlNCName)this.get_store().add_attribute_user(ElementImpl.NAME$10);
            }
            target.set(name);
        }
    }
    
    @Override
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ElementImpl.NAME$10);
        }
    }
    
    @Override
    public QName getRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.REF$12);
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
            target = (XmlQName)this.get_store().find_attribute_user(ElementImpl.REF$12);
            return target;
        }
    }
    
    @Override
    public boolean isSetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ElementImpl.REF$12) != null;
        }
    }
    
    @Override
    public void setRef(final QName ref) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.REF$12);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ElementImpl.REF$12);
            }
            target.setQNameValue(ref);
        }
    }
    
    @Override
    public void xsetRef(final XmlQName ref) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(ElementImpl.REF$12);
            if (target == null) {
                target = (XmlQName)this.get_store().add_attribute_user(ElementImpl.REF$12);
            }
            target.set(ref);
        }
    }
    
    @Override
    public void unsetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ElementImpl.REF$12);
        }
    }
    
    @Override
    public QName getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.TYPE$14);
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
            target = (XmlQName)this.get_store().find_attribute_user(ElementImpl.TYPE$14);
            return target;
        }
    }
    
    @Override
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ElementImpl.TYPE$14) != null;
        }
    }
    
    @Override
    public void setType(final QName type) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.TYPE$14);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ElementImpl.TYPE$14);
            }
            target.setQNameValue(type);
        }
    }
    
    @Override
    public void xsetType(final XmlQName type) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(ElementImpl.TYPE$14);
            if (target == null) {
                target = (XmlQName)this.get_store().add_attribute_user(ElementImpl.TYPE$14);
            }
            target.set(type);
        }
    }
    
    @Override
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ElementImpl.TYPE$14);
        }
    }
    
    @Override
    public QName getSubstitutionGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.SUBSTITUTIONGROUP$16);
            if (target == null) {
                return null;
            }
            return target.getQNameValue();
        }
    }
    
    @Override
    public XmlQName xgetSubstitutionGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(ElementImpl.SUBSTITUTIONGROUP$16);
            return target;
        }
    }
    
    @Override
    public boolean isSetSubstitutionGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ElementImpl.SUBSTITUTIONGROUP$16) != null;
        }
    }
    
    @Override
    public void setSubstitutionGroup(final QName substitutionGroup) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.SUBSTITUTIONGROUP$16);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ElementImpl.SUBSTITUTIONGROUP$16);
            }
            target.setQNameValue(substitutionGroup);
        }
    }
    
    @Override
    public void xsetSubstitutionGroup(final XmlQName substitutionGroup) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(ElementImpl.SUBSTITUTIONGROUP$16);
            if (target == null) {
                target = (XmlQName)this.get_store().add_attribute_user(ElementImpl.SUBSTITUTIONGROUP$16);
            }
            target.set(substitutionGroup);
        }
    }
    
    @Override
    public void unsetSubstitutionGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ElementImpl.SUBSTITUTIONGROUP$16);
        }
    }
    
    @Override
    public BigInteger getMinOccurs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.MINOCCURS$18);
            if (target == null) {
                target = (SimpleValue)this.get_default_attribute_value(ElementImpl.MINOCCURS$18);
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
            target = (XmlNonNegativeInteger)this.get_store().find_attribute_user(ElementImpl.MINOCCURS$18);
            if (target == null) {
                target = (XmlNonNegativeInteger)this.get_default_attribute_value(ElementImpl.MINOCCURS$18);
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetMinOccurs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ElementImpl.MINOCCURS$18) != null;
        }
    }
    
    @Override
    public void setMinOccurs(final BigInteger minOccurs) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.MINOCCURS$18);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ElementImpl.MINOCCURS$18);
            }
            target.setBigIntegerValue(minOccurs);
        }
    }
    
    @Override
    public void xsetMinOccurs(final XmlNonNegativeInteger minOccurs) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlNonNegativeInteger target = null;
            target = (XmlNonNegativeInteger)this.get_store().find_attribute_user(ElementImpl.MINOCCURS$18);
            if (target == null) {
                target = (XmlNonNegativeInteger)this.get_store().add_attribute_user(ElementImpl.MINOCCURS$18);
            }
            target.set(minOccurs);
        }
    }
    
    @Override
    public void unsetMinOccurs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ElementImpl.MINOCCURS$18);
        }
    }
    
    @Override
    public Object getMaxOccurs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.MAXOCCURS$20);
            if (target == null) {
                target = (SimpleValue)this.get_default_attribute_value(ElementImpl.MAXOCCURS$20);
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
            target = (AllNNI)this.get_store().find_attribute_user(ElementImpl.MAXOCCURS$20);
            if (target == null) {
                target = (AllNNI)this.get_default_attribute_value(ElementImpl.MAXOCCURS$20);
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetMaxOccurs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ElementImpl.MAXOCCURS$20) != null;
        }
    }
    
    @Override
    public void setMaxOccurs(final Object maxOccurs) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.MAXOCCURS$20);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ElementImpl.MAXOCCURS$20);
            }
            target.setObjectValue(maxOccurs);
        }
    }
    
    @Override
    public void xsetMaxOccurs(final AllNNI maxOccurs) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AllNNI target = null;
            target = (AllNNI)this.get_store().find_attribute_user(ElementImpl.MAXOCCURS$20);
            if (target == null) {
                target = (AllNNI)this.get_store().add_attribute_user(ElementImpl.MAXOCCURS$20);
            }
            target.set(maxOccurs);
        }
    }
    
    @Override
    public void unsetMaxOccurs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ElementImpl.MAXOCCURS$20);
        }
    }
    
    @Override
    public String getDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.DEFAULT$22);
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
            target = (XmlString)this.get_store().find_attribute_user(ElementImpl.DEFAULT$22);
            return target;
        }
    }
    
    @Override
    public boolean isSetDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ElementImpl.DEFAULT$22) != null;
        }
    }
    
    @Override
    public void setDefault(final String xdefault) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.DEFAULT$22);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ElementImpl.DEFAULT$22);
            }
            target.setStringValue(xdefault);
        }
    }
    
    @Override
    public void xsetDefault(final XmlString xdefault) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_attribute_user(ElementImpl.DEFAULT$22);
            if (target == null) {
                target = (XmlString)this.get_store().add_attribute_user(ElementImpl.DEFAULT$22);
            }
            target.set(xdefault);
        }
    }
    
    @Override
    public void unsetDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ElementImpl.DEFAULT$22);
        }
    }
    
    @Override
    public String getFixed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.FIXED$24);
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
            target = (XmlString)this.get_store().find_attribute_user(ElementImpl.FIXED$24);
            return target;
        }
    }
    
    @Override
    public boolean isSetFixed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ElementImpl.FIXED$24) != null;
        }
    }
    
    @Override
    public void setFixed(final String fixed) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.FIXED$24);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ElementImpl.FIXED$24);
            }
            target.setStringValue(fixed);
        }
    }
    
    @Override
    public void xsetFixed(final XmlString fixed) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)this.get_store().find_attribute_user(ElementImpl.FIXED$24);
            if (target == null) {
                target = (XmlString)this.get_store().add_attribute_user(ElementImpl.FIXED$24);
            }
            target.set(fixed);
        }
    }
    
    @Override
    public void unsetFixed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ElementImpl.FIXED$24);
        }
    }
    
    @Override
    public boolean getNillable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.NILLABLE$26);
            if (target == null) {
                target = (SimpleValue)this.get_default_attribute_value(ElementImpl.NILLABLE$26);
            }
            return target != null && target.getBooleanValue();
        }
    }
    
    @Override
    public XmlBoolean xgetNillable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)this.get_store().find_attribute_user(ElementImpl.NILLABLE$26);
            if (target == null) {
                target = (XmlBoolean)this.get_default_attribute_value(ElementImpl.NILLABLE$26);
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetNillable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ElementImpl.NILLABLE$26) != null;
        }
    }
    
    @Override
    public void setNillable(final boolean nillable) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.NILLABLE$26);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ElementImpl.NILLABLE$26);
            }
            target.setBooleanValue(nillable);
        }
    }
    
    @Override
    public void xsetNillable(final XmlBoolean nillable) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)this.get_store().find_attribute_user(ElementImpl.NILLABLE$26);
            if (target == null) {
                target = (XmlBoolean)this.get_store().add_attribute_user(ElementImpl.NILLABLE$26);
            }
            target.set(nillable);
        }
    }
    
    @Override
    public void unsetNillable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ElementImpl.NILLABLE$26);
        }
    }
    
    @Override
    public boolean getAbstract() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.ABSTRACT$28);
            if (target == null) {
                target = (SimpleValue)this.get_default_attribute_value(ElementImpl.ABSTRACT$28);
            }
            return target != null && target.getBooleanValue();
        }
    }
    
    @Override
    public XmlBoolean xgetAbstract() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)this.get_store().find_attribute_user(ElementImpl.ABSTRACT$28);
            if (target == null) {
                target = (XmlBoolean)this.get_default_attribute_value(ElementImpl.ABSTRACT$28);
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetAbstract() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ElementImpl.ABSTRACT$28) != null;
        }
    }
    
    @Override
    public void setAbstract(final boolean xabstract) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.ABSTRACT$28);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ElementImpl.ABSTRACT$28);
            }
            target.setBooleanValue(xabstract);
        }
    }
    
    @Override
    public void xsetAbstract(final XmlBoolean xabstract) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)this.get_store().find_attribute_user(ElementImpl.ABSTRACT$28);
            if (target == null) {
                target = (XmlBoolean)this.get_store().add_attribute_user(ElementImpl.ABSTRACT$28);
            }
            target.set(xabstract);
        }
    }
    
    @Override
    public void unsetAbstract() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ElementImpl.ABSTRACT$28);
        }
    }
    
    @Override
    public Object getFinal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.FINAL$30);
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
            target = (DerivationSet)this.get_store().find_attribute_user(ElementImpl.FINAL$30);
            return target;
        }
    }
    
    @Override
    public boolean isSetFinal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ElementImpl.FINAL$30) != null;
        }
    }
    
    @Override
    public void setFinal(final Object xfinal) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.FINAL$30);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ElementImpl.FINAL$30);
            }
            target.setObjectValue(xfinal);
        }
    }
    
    @Override
    public void xsetFinal(final DerivationSet xfinal) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            DerivationSet target = null;
            target = (DerivationSet)this.get_store().find_attribute_user(ElementImpl.FINAL$30);
            if (target == null) {
                target = (DerivationSet)this.get_store().add_attribute_user(ElementImpl.FINAL$30);
            }
            target.set(xfinal);
        }
    }
    
    @Override
    public void unsetFinal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ElementImpl.FINAL$30);
        }
    }
    
    @Override
    public Object getBlock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.BLOCK$32);
            if (target == null) {
                return null;
            }
            return target.getObjectValue();
        }
    }
    
    @Override
    public BlockSet xgetBlock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            BlockSet target = null;
            target = (BlockSet)this.get_store().find_attribute_user(ElementImpl.BLOCK$32);
            return target;
        }
    }
    
    @Override
    public boolean isSetBlock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ElementImpl.BLOCK$32) != null;
        }
    }
    
    @Override
    public void setBlock(final Object block) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.BLOCK$32);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ElementImpl.BLOCK$32);
            }
            target.setObjectValue(block);
        }
    }
    
    @Override
    public void xsetBlock(final BlockSet block) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            BlockSet target = null;
            target = (BlockSet)this.get_store().find_attribute_user(ElementImpl.BLOCK$32);
            if (target == null) {
                target = (BlockSet)this.get_store().add_attribute_user(ElementImpl.BLOCK$32);
            }
            target.set(block);
        }
    }
    
    @Override
    public void unsetBlock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ElementImpl.BLOCK$32);
        }
    }
    
    @Override
    public FormChoice.Enum getForm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.FORM$34);
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
            target = (FormChoice)this.get_store().find_attribute_user(ElementImpl.FORM$34);
            return target;
        }
    }
    
    @Override
    public boolean isSetForm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ElementImpl.FORM$34) != null;
        }
    }
    
    @Override
    public void setForm(final FormChoice.Enum form) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(ElementImpl.FORM$34);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(ElementImpl.FORM$34);
            }
            target.setEnumValue(form);
        }
    }
    
    @Override
    public void xsetForm(final FormChoice form) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            FormChoice target = null;
            target = (FormChoice)this.get_store().find_attribute_user(ElementImpl.FORM$34);
            if (target == null) {
                target = (FormChoice)this.get_store().add_attribute_user(ElementImpl.FORM$34);
            }
            target.set(form);
        }
    }
    
    @Override
    public void unsetForm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ElementImpl.FORM$34);
        }
    }
    
    static {
        SIMPLETYPE$0 = new QName("http://www.w3.org/2001/XMLSchema", "simpleType");
        COMPLEXTYPE$2 = new QName("http://www.w3.org/2001/XMLSchema", "complexType");
        UNIQUE$4 = new QName("http://www.w3.org/2001/XMLSchema", "unique");
        KEY$6 = new QName("http://www.w3.org/2001/XMLSchema", "key");
        KEYREF$8 = new QName("http://www.w3.org/2001/XMLSchema", "keyref");
        NAME$10 = new QName("", "name");
        REF$12 = new QName("", "ref");
        TYPE$14 = new QName("", "type");
        SUBSTITUTIONGROUP$16 = new QName("", "substitutionGroup");
        MINOCCURS$18 = new QName("", "minOccurs");
        MAXOCCURS$20 = new QName("", "maxOccurs");
        DEFAULT$22 = new QName("", "default");
        FIXED$24 = new QName("", "fixed");
        NILLABLE$26 = new QName("", "nillable");
        ABSTRACT$28 = new QName("", "abstract");
        FINAL$30 = new QName("", "final");
        BLOCK$32 = new QName("", "block");
        FORM$34 = new QName("", "form");
    }
}
