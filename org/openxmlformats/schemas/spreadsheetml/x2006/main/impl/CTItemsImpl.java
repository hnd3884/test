package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTItem;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTItems;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTItemsImpl extends XmlComplexContentImpl implements CTItems
{
    private static final long serialVersionUID = 1L;
    private static final QName ITEM$0;
    private static final QName COUNT$2;
    
    public CTItemsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTItem> getItemList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ItemList extends AbstractList<CTItem>
            {
                @Override
                public CTItem get(final int n) {
                    return CTItemsImpl.this.getItemArray(n);
                }
                
                @Override
                public CTItem set(final int n, final CTItem ctItem) {
                    final CTItem itemArray = CTItemsImpl.this.getItemArray(n);
                    CTItemsImpl.this.setItemArray(n, ctItem);
                    return itemArray;
                }
                
                @Override
                public void add(final int n, final CTItem ctItem) {
                    CTItemsImpl.this.insertNewItem(n).set((XmlObject)ctItem);
                }
                
                @Override
                public CTItem remove(final int n) {
                    final CTItem itemArray = CTItemsImpl.this.getItemArray(n);
                    CTItemsImpl.this.removeItem(n);
                    return itemArray;
                }
                
                @Override
                public int size() {
                    return CTItemsImpl.this.sizeOfItemArray();
                }
            }
            return new ItemList();
        }
    }
    
    @Deprecated
    public CTItem[] getItemArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTItemsImpl.ITEM$0, (List)list);
            final CTItem[] array = new CTItem[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTItem getItemArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTItem ctItem = (CTItem)this.get_store().find_element_user(CTItemsImpl.ITEM$0, n);
            if (ctItem == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctItem;
        }
    }
    
    public int sizeOfItemArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTItemsImpl.ITEM$0);
        }
    }
    
    public void setItemArray(final CTItem[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTItemsImpl.ITEM$0);
    }
    
    public void setItemArray(final int n, final CTItem ctItem) {
        this.generatedSetterHelperImpl((XmlObject)ctItem, CTItemsImpl.ITEM$0, n, (short)2);
    }
    
    public CTItem insertNewItem(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTItem)this.get_store().insert_element_user(CTItemsImpl.ITEM$0, n);
        }
    }
    
    public CTItem addNewItem() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTItem)this.get_store().add_element_user(CTItemsImpl.ITEM$0);
        }
    }
    
    public void removeItem(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTItemsImpl.ITEM$0, n);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTItemsImpl.COUNT$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTItemsImpl.COUNT$2);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTItemsImpl.COUNT$2) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTItemsImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTItemsImpl.COUNT$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTItemsImpl.COUNT$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTItemsImpl.COUNT$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTItemsImpl.COUNT$2);
        }
    }
    
    static {
        ITEM$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "item");
        COUNT$2 = new QName("", "count");
    }
}
