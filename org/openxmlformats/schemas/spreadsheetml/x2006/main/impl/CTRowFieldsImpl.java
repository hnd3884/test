package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTField;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRowFields;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRowFieldsImpl extends XmlComplexContentImpl implements CTRowFields
{
    private static final long serialVersionUID = 1L;
    private static final QName FIELD$0;
    private static final QName COUNT$2;
    
    public CTRowFieldsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTField> getFieldList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FieldList extends AbstractList<CTField>
            {
                @Override
                public CTField get(final int n) {
                    return CTRowFieldsImpl.this.getFieldArray(n);
                }
                
                @Override
                public CTField set(final int n, final CTField ctField) {
                    final CTField fieldArray = CTRowFieldsImpl.this.getFieldArray(n);
                    CTRowFieldsImpl.this.setFieldArray(n, ctField);
                    return fieldArray;
                }
                
                @Override
                public void add(final int n, final CTField ctField) {
                    CTRowFieldsImpl.this.insertNewField(n).set((XmlObject)ctField);
                }
                
                @Override
                public CTField remove(final int n) {
                    final CTField fieldArray = CTRowFieldsImpl.this.getFieldArray(n);
                    CTRowFieldsImpl.this.removeField(n);
                    return fieldArray;
                }
                
                @Override
                public int size() {
                    return CTRowFieldsImpl.this.sizeOfFieldArray();
                }
            }
            return new FieldList();
        }
    }
    
    @Deprecated
    public CTField[] getFieldArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRowFieldsImpl.FIELD$0, (List)list);
            final CTField[] array = new CTField[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTField getFieldArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTField ctField = (CTField)this.get_store().find_element_user(CTRowFieldsImpl.FIELD$0, n);
            if (ctField == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctField;
        }
    }
    
    public int sizeOfFieldArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowFieldsImpl.FIELD$0);
        }
    }
    
    public void setFieldArray(final CTField[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowFieldsImpl.FIELD$0);
    }
    
    public void setFieldArray(final int n, final CTField ctField) {
        this.generatedSetterHelperImpl((XmlObject)ctField, CTRowFieldsImpl.FIELD$0, n, (short)2);
    }
    
    public CTField insertNewField(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTField)this.get_store().insert_element_user(CTRowFieldsImpl.FIELD$0, n);
        }
    }
    
    public CTField addNewField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTField)this.get_store().add_element_user(CTRowFieldsImpl.FIELD$0);
        }
    }
    
    public void removeField(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowFieldsImpl.FIELD$0, n);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowFieldsImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRowFieldsImpl.COUNT$2);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTRowFieldsImpl.COUNT$2);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTRowFieldsImpl.COUNT$2);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowFieldsImpl.COUNT$2) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowFieldsImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowFieldsImpl.COUNT$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTRowFieldsImpl.COUNT$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTRowFieldsImpl.COUNT$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowFieldsImpl.COUNT$2);
        }
    }
    
    static {
        FIELD$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "field");
        COUNT$2 = new QName("", "count");
    }
}
