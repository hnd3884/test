package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableColumnsImpl extends XmlComplexContentImpl implements CTTableColumns
{
    private static final long serialVersionUID = 1L;
    private static final QName TABLECOLUMN$0;
    private static final QName COUNT$2;
    
    public CTTableColumnsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTTableColumn> getTableColumnList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TableColumnList extends AbstractList<CTTableColumn>
            {
                @Override
                public CTTableColumn get(final int n) {
                    return CTTableColumnsImpl.this.getTableColumnArray(n);
                }
                
                @Override
                public CTTableColumn set(final int n, final CTTableColumn ctTableColumn) {
                    final CTTableColumn tableColumnArray = CTTableColumnsImpl.this.getTableColumnArray(n);
                    CTTableColumnsImpl.this.setTableColumnArray(n, ctTableColumn);
                    return tableColumnArray;
                }
                
                @Override
                public void add(final int n, final CTTableColumn ctTableColumn) {
                    CTTableColumnsImpl.this.insertNewTableColumn(n).set((XmlObject)ctTableColumn);
                }
                
                @Override
                public CTTableColumn remove(final int n) {
                    final CTTableColumn tableColumnArray = CTTableColumnsImpl.this.getTableColumnArray(n);
                    CTTableColumnsImpl.this.removeTableColumn(n);
                    return tableColumnArray;
                }
                
                @Override
                public int size() {
                    return CTTableColumnsImpl.this.sizeOfTableColumnArray();
                }
            }
            return new TableColumnList();
        }
    }
    
    @Deprecated
    public CTTableColumn[] getTableColumnArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTableColumnsImpl.TABLECOLUMN$0, (List)list);
            final CTTableColumn[] array = new CTTableColumn[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTableColumn getTableColumnArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableColumn ctTableColumn = (CTTableColumn)this.get_store().find_element_user(CTTableColumnsImpl.TABLECOLUMN$0, n);
            if (ctTableColumn == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTableColumn;
        }
    }
    
    public int sizeOfTableColumnArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableColumnsImpl.TABLECOLUMN$0);
        }
    }
    
    public void setTableColumnArray(final CTTableColumn[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTableColumnsImpl.TABLECOLUMN$0);
    }
    
    public void setTableColumnArray(final int n, final CTTableColumn ctTableColumn) {
        this.generatedSetterHelperImpl((XmlObject)ctTableColumn, CTTableColumnsImpl.TABLECOLUMN$0, n, (short)2);
    }
    
    public CTTableColumn insertNewTableColumn(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableColumn)this.get_store().insert_element_user(CTTableColumnsImpl.TABLECOLUMN$0, n);
        }
    }
    
    public CTTableColumn addNewTableColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableColumn)this.get_store().add_element_user(CTTableColumnsImpl.TABLECOLUMN$0);
        }
    }
    
    public void removeTableColumn(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableColumnsImpl.TABLECOLUMN$0, n);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnsImpl.COUNT$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableColumnsImpl.COUNT$2);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableColumnsImpl.COUNT$2) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnsImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableColumnsImpl.COUNT$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableColumnsImpl.COUNT$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTTableColumnsImpl.COUNT$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableColumnsImpl.COUNT$2);
        }
    }
    
    static {
        TABLECOLUMN$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "tableColumn");
        COUNT$2 = new QName("", "count");
    }
}
