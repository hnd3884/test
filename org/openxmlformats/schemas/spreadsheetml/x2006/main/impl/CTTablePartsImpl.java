package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTablePart;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableParts;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTablePartsImpl extends XmlComplexContentImpl implements CTTableParts
{
    private static final long serialVersionUID = 1L;
    private static final QName TABLEPART$0;
    private static final QName COUNT$2;
    
    public CTTablePartsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTTablePart> getTablePartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TablePartList extends AbstractList<CTTablePart>
            {
                @Override
                public CTTablePart get(final int n) {
                    return CTTablePartsImpl.this.getTablePartArray(n);
                }
                
                @Override
                public CTTablePart set(final int n, final CTTablePart ctTablePart) {
                    final CTTablePart tablePartArray = CTTablePartsImpl.this.getTablePartArray(n);
                    CTTablePartsImpl.this.setTablePartArray(n, ctTablePart);
                    return tablePartArray;
                }
                
                @Override
                public void add(final int n, final CTTablePart ctTablePart) {
                    CTTablePartsImpl.this.insertNewTablePart(n).set((XmlObject)ctTablePart);
                }
                
                @Override
                public CTTablePart remove(final int n) {
                    final CTTablePart tablePartArray = CTTablePartsImpl.this.getTablePartArray(n);
                    CTTablePartsImpl.this.removeTablePart(n);
                    return tablePartArray;
                }
                
                @Override
                public int size() {
                    return CTTablePartsImpl.this.sizeOfTablePartArray();
                }
            }
            return new TablePartList();
        }
    }
    
    @Deprecated
    public CTTablePart[] getTablePartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTablePartsImpl.TABLEPART$0, (List)list);
            final CTTablePart[] array = new CTTablePart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTablePart getTablePartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTablePart ctTablePart = (CTTablePart)this.get_store().find_element_user(CTTablePartsImpl.TABLEPART$0, n);
            if (ctTablePart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTablePart;
        }
    }
    
    public int sizeOfTablePartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTablePartsImpl.TABLEPART$0);
        }
    }
    
    public void setTablePartArray(final CTTablePart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTablePartsImpl.TABLEPART$0);
    }
    
    public void setTablePartArray(final int n, final CTTablePart ctTablePart) {
        this.generatedSetterHelperImpl((XmlObject)ctTablePart, CTTablePartsImpl.TABLEPART$0, n, (short)2);
    }
    
    public CTTablePart insertNewTablePart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTablePart)this.get_store().insert_element_user(CTTablePartsImpl.TABLEPART$0, n);
        }
    }
    
    public CTTablePart addNewTablePart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTablePart)this.get_store().add_element_user(CTTablePartsImpl.TABLEPART$0);
        }
    }
    
    public void removeTablePart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTablePartsImpl.TABLEPART$0, n);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePartsImpl.COUNT$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTTablePartsImpl.COUNT$2);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTablePartsImpl.COUNT$2) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePartsImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTablePartsImpl.COUNT$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTablePartsImpl.COUNT$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTTablePartsImpl.COUNT$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTablePartsImpl.COUNT$2);
        }
    }
    
    static {
        TABLEPART$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "tablePart");
        COUNT$2 = new QName("", "count");
    }
}
