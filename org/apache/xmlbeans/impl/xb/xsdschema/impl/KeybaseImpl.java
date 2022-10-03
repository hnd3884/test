package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SimpleValue;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xsdschema.FieldDocument;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.SelectorDocument;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.Keybase;

public class KeybaseImpl extends AnnotatedImpl implements Keybase
{
    private static final long serialVersionUID = 1L;
    private static final QName SELECTOR$0;
    private static final QName FIELD$2;
    private static final QName NAME$4;
    
    public KeybaseImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public SelectorDocument.Selector getSelector() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SelectorDocument.Selector target = null;
            target = (SelectorDocument.Selector)this.get_store().find_element_user(KeybaseImpl.SELECTOR$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setSelector(final SelectorDocument.Selector selector) {
        this.generatedSetterHelperImpl(selector, KeybaseImpl.SELECTOR$0, 0, (short)1);
    }
    
    @Override
    public SelectorDocument.Selector addNewSelector() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SelectorDocument.Selector target = null;
            target = (SelectorDocument.Selector)this.get_store().add_element_user(KeybaseImpl.SELECTOR$0);
            return target;
        }
    }
    
    @Override
    public FieldDocument.Field[] getFieldArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(KeybaseImpl.FIELD$2, targetList);
            final FieldDocument.Field[] result = new FieldDocument.Field[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public FieldDocument.Field getFieldArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            FieldDocument.Field target = null;
            target = (FieldDocument.Field)this.get_store().find_element_user(KeybaseImpl.FIELD$2, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfFieldArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(KeybaseImpl.FIELD$2);
        }
    }
    
    @Override
    public void setFieldArray(final FieldDocument.Field[] fieldArray) {
        this.check_orphaned();
        this.arraySetterHelper(fieldArray, KeybaseImpl.FIELD$2);
    }
    
    @Override
    public void setFieldArray(final int i, final FieldDocument.Field field) {
        this.generatedSetterHelperImpl(field, KeybaseImpl.FIELD$2, i, (short)2);
    }
    
    @Override
    public FieldDocument.Field insertNewField(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            FieldDocument.Field target = null;
            target = (FieldDocument.Field)this.get_store().insert_element_user(KeybaseImpl.FIELD$2, i);
            return target;
        }
    }
    
    @Override
    public FieldDocument.Field addNewField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            FieldDocument.Field target = null;
            target = (FieldDocument.Field)this.get_store().add_element_user(KeybaseImpl.FIELD$2);
            return target;
        }
    }
    
    @Override
    public void removeField(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(KeybaseImpl.FIELD$2, i);
        }
    }
    
    @Override
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(KeybaseImpl.NAME$4);
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
            target = (XmlNCName)this.get_store().find_attribute_user(KeybaseImpl.NAME$4);
            return target;
        }
    }
    
    @Override
    public void setName(final String name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(KeybaseImpl.NAME$4);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(KeybaseImpl.NAME$4);
            }
            target.setStringValue(name);
        }
    }
    
    @Override
    public void xsetName(final XmlNCName name) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlNCName target = null;
            target = (XmlNCName)this.get_store().find_attribute_user(KeybaseImpl.NAME$4);
            if (target == null) {
                target = (XmlNCName)this.get_store().add_attribute_user(KeybaseImpl.NAME$4);
            }
            target.set(name);
        }
    }
    
    static {
        SELECTOR$0 = new QName("http://www.w3.org/2001/XMLSchema", "selector");
        FIELD$2 = new QName("http://www.w3.org/2001/XMLSchema", "field");
        NAME$4 = new QName("", "name");
    }
}
