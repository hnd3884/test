package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFills;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFillsImpl extends XmlComplexContentImpl implements CTFills
{
    private static final long serialVersionUID = 1L;
    private static final QName FILL$0;
    private static final QName COUNT$2;
    
    public CTFillsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTFill> getFillList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FillList extends AbstractList<CTFill>
            {
                @Override
                public CTFill get(final int n) {
                    return CTFillsImpl.this.getFillArray(n);
                }
                
                @Override
                public CTFill set(final int n, final CTFill ctFill) {
                    final CTFill fillArray = CTFillsImpl.this.getFillArray(n);
                    CTFillsImpl.this.setFillArray(n, ctFill);
                    return fillArray;
                }
                
                @Override
                public void add(final int n, final CTFill ctFill) {
                    CTFillsImpl.this.insertNewFill(n).set((XmlObject)ctFill);
                }
                
                @Override
                public CTFill remove(final int n) {
                    final CTFill fillArray = CTFillsImpl.this.getFillArray(n);
                    CTFillsImpl.this.removeFill(n);
                    return fillArray;
                }
                
                @Override
                public int size() {
                    return CTFillsImpl.this.sizeOfFillArray();
                }
            }
            return new FillList();
        }
    }
    
    @Deprecated
    public CTFill[] getFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFillsImpl.FILL$0, (List)list);
            final CTFill[] array = new CTFill[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFill getFillArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFill ctFill = (CTFill)this.get_store().find_element_user(CTFillsImpl.FILL$0, n);
            if (ctFill == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFill;
        }
    }
    
    public int sizeOfFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFillsImpl.FILL$0);
        }
    }
    
    public void setFillArray(final CTFill[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFillsImpl.FILL$0);
    }
    
    public void setFillArray(final int n, final CTFill ctFill) {
        this.generatedSetterHelperImpl((XmlObject)ctFill, CTFillsImpl.FILL$0, n, (short)2);
    }
    
    public CTFill insertNewFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFill)this.get_store().insert_element_user(CTFillsImpl.FILL$0, n);
        }
    }
    
    public CTFill addNewFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFill)this.get_store().add_element_user(CTFillsImpl.FILL$0);
        }
    }
    
    public void removeFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFillsImpl.FILL$0, n);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillsImpl.COUNT$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTFillsImpl.COUNT$2);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFillsImpl.COUNT$2) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFillsImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFillsImpl.COUNT$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTFillsImpl.COUNT$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTFillsImpl.COUNT$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFillsImpl.COUNT$2);
        }
    }
    
    static {
        FILL$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "fill");
        COUNT$2 = new QName("", "count");
    }
}
