package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheField;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheFields;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCacheFieldsImpl extends XmlComplexContentImpl implements CTCacheFields
{
    private static final long serialVersionUID = 1L;
    private static final QName CACHEFIELD$0;
    private static final QName COUNT$2;
    
    public CTCacheFieldsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTCacheField> getCacheFieldList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CacheFieldList extends AbstractList<CTCacheField>
            {
                @Override
                public CTCacheField get(final int n) {
                    return CTCacheFieldsImpl.this.getCacheFieldArray(n);
                }
                
                @Override
                public CTCacheField set(final int n, final CTCacheField ctCacheField) {
                    final CTCacheField cacheFieldArray = CTCacheFieldsImpl.this.getCacheFieldArray(n);
                    CTCacheFieldsImpl.this.setCacheFieldArray(n, ctCacheField);
                    return cacheFieldArray;
                }
                
                @Override
                public void add(final int n, final CTCacheField ctCacheField) {
                    CTCacheFieldsImpl.this.insertNewCacheField(n).set((XmlObject)ctCacheField);
                }
                
                @Override
                public CTCacheField remove(final int n) {
                    final CTCacheField cacheFieldArray = CTCacheFieldsImpl.this.getCacheFieldArray(n);
                    CTCacheFieldsImpl.this.removeCacheField(n);
                    return cacheFieldArray;
                }
                
                @Override
                public int size() {
                    return CTCacheFieldsImpl.this.sizeOfCacheFieldArray();
                }
            }
            return new CacheFieldList();
        }
    }
    
    @Deprecated
    public CTCacheField[] getCacheFieldArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTCacheFieldsImpl.CACHEFIELD$0, (List)list);
            final CTCacheField[] array = new CTCacheField[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCacheField getCacheFieldArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCacheField ctCacheField = (CTCacheField)this.get_store().find_element_user(CTCacheFieldsImpl.CACHEFIELD$0, n);
            if (ctCacheField == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCacheField;
        }
    }
    
    public int sizeOfCacheFieldArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCacheFieldsImpl.CACHEFIELD$0);
        }
    }
    
    public void setCacheFieldArray(final CTCacheField[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTCacheFieldsImpl.CACHEFIELD$0);
    }
    
    public void setCacheFieldArray(final int n, final CTCacheField ctCacheField) {
        this.generatedSetterHelperImpl((XmlObject)ctCacheField, CTCacheFieldsImpl.CACHEFIELD$0, n, (short)2);
    }
    
    public CTCacheField insertNewCacheField(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCacheField)this.get_store().insert_element_user(CTCacheFieldsImpl.CACHEFIELD$0, n);
        }
    }
    
    public CTCacheField addNewCacheField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCacheField)this.get_store().add_element_user(CTCacheFieldsImpl.CACHEFIELD$0);
        }
    }
    
    public void removeCacheField(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCacheFieldsImpl.CACHEFIELD$0, n);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldsImpl.COUNT$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTCacheFieldsImpl.COUNT$2);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCacheFieldsImpl.COUNT$2) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheFieldsImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheFieldsImpl.COUNT$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCacheFieldsImpl.COUNT$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCacheFieldsImpl.COUNT$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCacheFieldsImpl.COUNT$2);
        }
    }
    
    static {
        CACHEFIELD$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cacheField");
        COUNT$2 = new QName("", "count");
    }
}
