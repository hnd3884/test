package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotField;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotFields;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPivotFieldsImpl extends XmlComplexContentImpl implements CTPivotFields
{
    private static final long serialVersionUID = 1L;
    private static final QName PIVOTFIELD$0;
    private static final QName COUNT$2;
    
    public CTPivotFieldsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTPivotField> getPivotFieldList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PivotFieldList extends AbstractList<CTPivotField>
            {
                @Override
                public CTPivotField get(final int n) {
                    return CTPivotFieldsImpl.this.getPivotFieldArray(n);
                }
                
                @Override
                public CTPivotField set(final int n, final CTPivotField ctPivotField) {
                    final CTPivotField pivotFieldArray = CTPivotFieldsImpl.this.getPivotFieldArray(n);
                    CTPivotFieldsImpl.this.setPivotFieldArray(n, ctPivotField);
                    return pivotFieldArray;
                }
                
                @Override
                public void add(final int n, final CTPivotField ctPivotField) {
                    CTPivotFieldsImpl.this.insertNewPivotField(n).set((XmlObject)ctPivotField);
                }
                
                @Override
                public CTPivotField remove(final int n) {
                    final CTPivotField pivotFieldArray = CTPivotFieldsImpl.this.getPivotFieldArray(n);
                    CTPivotFieldsImpl.this.removePivotField(n);
                    return pivotFieldArray;
                }
                
                @Override
                public int size() {
                    return CTPivotFieldsImpl.this.sizeOfPivotFieldArray();
                }
            }
            return new PivotFieldList();
        }
    }
    
    @Deprecated
    public CTPivotField[] getPivotFieldArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPivotFieldsImpl.PIVOTFIELD$0, (List)list);
            final CTPivotField[] array = new CTPivotField[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPivotField getPivotFieldArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPivotField ctPivotField = (CTPivotField)this.get_store().find_element_user(CTPivotFieldsImpl.PIVOTFIELD$0, n);
            if (ctPivotField == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPivotField;
        }
    }
    
    public int sizeOfPivotFieldArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotFieldsImpl.PIVOTFIELD$0);
        }
    }
    
    public void setPivotFieldArray(final CTPivotField[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPivotFieldsImpl.PIVOTFIELD$0);
    }
    
    public void setPivotFieldArray(final int n, final CTPivotField ctPivotField) {
        this.generatedSetterHelperImpl((XmlObject)ctPivotField, CTPivotFieldsImpl.PIVOTFIELD$0, n, (short)2);
    }
    
    public CTPivotField insertNewPivotField(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPivotField)this.get_store().insert_element_user(CTPivotFieldsImpl.PIVOTFIELD$0, n);
        }
    }
    
    public CTPivotField addNewPivotField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPivotField)this.get_store().add_element_user(CTPivotFieldsImpl.PIVOTFIELD$0);
        }
    }
    
    public void removePivotField(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotFieldsImpl.PIVOTFIELD$0, n);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldsImpl.COUNT$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotFieldsImpl.COUNT$2);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldsImpl.COUNT$2) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldsImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldsImpl.COUNT$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotFieldsImpl.COUNT$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPivotFieldsImpl.COUNT$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldsImpl.COUNT$2);
        }
    }
    
    static {
        PIVOTFIELD$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pivotField");
        COUNT$2 = new QName("", "count");
    }
}
