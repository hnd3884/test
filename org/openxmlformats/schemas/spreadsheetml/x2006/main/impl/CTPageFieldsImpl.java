package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageField;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageFields;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPageFieldsImpl extends XmlComplexContentImpl implements CTPageFields
{
    private static final long serialVersionUID = 1L;
    private static final QName PAGEFIELD$0;
    private static final QName COUNT$2;
    
    public CTPageFieldsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTPageField> getPageFieldList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PageFieldList extends AbstractList<CTPageField>
            {
                @Override
                public CTPageField get(final int n) {
                    return CTPageFieldsImpl.this.getPageFieldArray(n);
                }
                
                @Override
                public CTPageField set(final int n, final CTPageField ctPageField) {
                    final CTPageField pageFieldArray = CTPageFieldsImpl.this.getPageFieldArray(n);
                    CTPageFieldsImpl.this.setPageFieldArray(n, ctPageField);
                    return pageFieldArray;
                }
                
                @Override
                public void add(final int n, final CTPageField ctPageField) {
                    CTPageFieldsImpl.this.insertNewPageField(n).set((XmlObject)ctPageField);
                }
                
                @Override
                public CTPageField remove(final int n) {
                    final CTPageField pageFieldArray = CTPageFieldsImpl.this.getPageFieldArray(n);
                    CTPageFieldsImpl.this.removePageField(n);
                    return pageFieldArray;
                }
                
                @Override
                public int size() {
                    return CTPageFieldsImpl.this.sizeOfPageFieldArray();
                }
            }
            return new PageFieldList();
        }
    }
    
    @Deprecated
    public CTPageField[] getPageFieldArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPageFieldsImpl.PAGEFIELD$0, (List)list);
            final CTPageField[] array = new CTPageField[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPageField getPageFieldArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageField ctPageField = (CTPageField)this.get_store().find_element_user(CTPageFieldsImpl.PAGEFIELD$0, n);
            if (ctPageField == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPageField;
        }
    }
    
    public int sizeOfPageFieldArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPageFieldsImpl.PAGEFIELD$0);
        }
    }
    
    public void setPageFieldArray(final CTPageField[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPageFieldsImpl.PAGEFIELD$0);
    }
    
    public void setPageFieldArray(final int n, final CTPageField ctPageField) {
        this.generatedSetterHelperImpl((XmlObject)ctPageField, CTPageFieldsImpl.PAGEFIELD$0, n, (short)2);
    }
    
    public CTPageField insertNewPageField(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageField)this.get_store().insert_element_user(CTPageFieldsImpl.PAGEFIELD$0, n);
        }
    }
    
    public CTPageField addNewPageField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageField)this.get_store().add_element_user(CTPageFieldsImpl.PAGEFIELD$0);
        }
    }
    
    public void removePageField(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPageFieldsImpl.PAGEFIELD$0, n);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageFieldsImpl.COUNT$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageFieldsImpl.COUNT$2);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageFieldsImpl.COUNT$2) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageFieldsImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageFieldsImpl.COUNT$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageFieldsImpl.COUNT$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPageFieldsImpl.COUNT$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageFieldsImpl.COUNT$2);
        }
    }
    
    static {
        PAGEFIELD$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pageField");
        COUNT$2 = new QName("", "count");
    }
}
