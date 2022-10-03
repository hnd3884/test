package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleDerivationSet;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.xb.xsdschema.UnionDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.ListDocument;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.RestrictionDocument;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleType;

public class SimpleTypeImpl extends AnnotatedImpl implements SimpleType
{
    private static final long serialVersionUID = 1L;
    private static final QName RESTRICTION$0;
    private static final QName LIST$2;
    private static final QName UNION$4;
    private static final QName FINAL$6;
    private static final QName NAME$8;
    
    public SimpleTypeImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public RestrictionDocument.Restriction getRestriction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            RestrictionDocument.Restriction target = null;
            target = (RestrictionDocument.Restriction)this.get_store().find_element_user(SimpleTypeImpl.RESTRICTION$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetRestriction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SimpleTypeImpl.RESTRICTION$0) != 0;
        }
    }
    
    @Override
    public void setRestriction(final RestrictionDocument.Restriction restriction) {
        this.generatedSetterHelperImpl(restriction, SimpleTypeImpl.RESTRICTION$0, 0, (short)1);
    }
    
    @Override
    public RestrictionDocument.Restriction addNewRestriction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            RestrictionDocument.Restriction target = null;
            target = (RestrictionDocument.Restriction)this.get_store().add_element_user(SimpleTypeImpl.RESTRICTION$0);
            return target;
        }
    }
    
    @Override
    public void unsetRestriction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SimpleTypeImpl.RESTRICTION$0, 0);
        }
    }
    
    @Override
    public ListDocument.List getList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ListDocument.List target = null;
            target = (ListDocument.List)this.get_store().find_element_user(SimpleTypeImpl.LIST$2, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SimpleTypeImpl.LIST$2) != 0;
        }
    }
    
    @Override
    public void setList(final ListDocument.List list) {
        this.generatedSetterHelperImpl(list, SimpleTypeImpl.LIST$2, 0, (short)1);
    }
    
    @Override
    public ListDocument.List addNewList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ListDocument.List target = null;
            target = (ListDocument.List)this.get_store().add_element_user(SimpleTypeImpl.LIST$2);
            return target;
        }
    }
    
    @Override
    public void unsetList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SimpleTypeImpl.LIST$2, 0);
        }
    }
    
    @Override
    public UnionDocument.Union getUnion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            UnionDocument.Union target = null;
            target = (UnionDocument.Union)this.get_store().find_element_user(SimpleTypeImpl.UNION$4, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetUnion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SimpleTypeImpl.UNION$4) != 0;
        }
    }
    
    @Override
    public void setUnion(final UnionDocument.Union union) {
        this.generatedSetterHelperImpl(union, SimpleTypeImpl.UNION$4, 0, (short)1);
    }
    
    @Override
    public UnionDocument.Union addNewUnion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            UnionDocument.Union target = null;
            target = (UnionDocument.Union)this.get_store().add_element_user(SimpleTypeImpl.UNION$4);
            return target;
        }
    }
    
    @Override
    public void unsetUnion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SimpleTypeImpl.UNION$4, 0);
        }
    }
    
    @Override
    public Object getFinal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(SimpleTypeImpl.FINAL$6);
            if (target == null) {
                return null;
            }
            return target.getObjectValue();
        }
    }
    
    @Override
    public SimpleDerivationSet xgetFinal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleDerivationSet target = null;
            target = (SimpleDerivationSet)this.get_store().find_attribute_user(SimpleTypeImpl.FINAL$6);
            return target;
        }
    }
    
    @Override
    public boolean isSetFinal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(SimpleTypeImpl.FINAL$6) != null;
        }
    }
    
    @Override
    public void setFinal(final Object xfinal) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(SimpleTypeImpl.FINAL$6);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(SimpleTypeImpl.FINAL$6);
            }
            target.setObjectValue(xfinal);
        }
    }
    
    @Override
    public void xsetFinal(final SimpleDerivationSet xfinal) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleDerivationSet target = null;
            target = (SimpleDerivationSet)this.get_store().find_attribute_user(SimpleTypeImpl.FINAL$6);
            if (target == null) {
                target = (SimpleDerivationSet)this.get_store().add_attribute_user(SimpleTypeImpl.FINAL$6);
            }
            target.set(xfinal);
        }
    }
    
    @Override
    public void unsetFinal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(SimpleTypeImpl.FINAL$6);
        }
    }
    
    @Override
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(SimpleTypeImpl.NAME$8);
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
            target = (XmlNCName)this.get_store().find_attribute_user(SimpleTypeImpl.NAME$8);
            return target;
        }
    }
    
    @Override
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(SimpleTypeImpl.NAME$8) != null;
        }
    }
    
    @Override
    public void setName(final String name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(SimpleTypeImpl.NAME$8);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(SimpleTypeImpl.NAME$8);
            }
            target.setStringValue(name);
        }
    }
    
    @Override
    public void xsetName(final XmlNCName name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlNCName target = null;
            target = (XmlNCName)this.get_store().find_attribute_user(SimpleTypeImpl.NAME$8);
            if (target == null) {
                target = (XmlNCName)this.get_store().add_attribute_user(SimpleTypeImpl.NAME$8);
            }
            target.set(name);
        }
    }
    
    @Override
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(SimpleTypeImpl.NAME$8);
        }
    }
    
    static {
        RESTRICTION$0 = new QName("http://www.w3.org/2001/XMLSchema", "restriction");
        LIST$2 = new QName("http://www.w3.org/2001/XMLSchema", "list");
        UNION$4 = new QName("http://www.w3.org/2001/XMLSchema", "union");
        FINAL$6 = new QName("", "final");
        NAME$8 = new QName("", "name");
    }
}
