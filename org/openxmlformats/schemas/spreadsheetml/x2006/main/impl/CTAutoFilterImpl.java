package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSortState;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFilterColumn;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAutoFilter;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTAutoFilterImpl extends XmlComplexContentImpl implements CTAutoFilter
{
    private static final long serialVersionUID = 1L;
    private static final QName FILTERCOLUMN$0;
    private static final QName SORTSTATE$2;
    private static final QName EXTLST$4;
    private static final QName REF$6;
    
    public CTAutoFilterImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTFilterColumn> getFilterColumnList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FilterColumnList extends AbstractList<CTFilterColumn>
            {
                @Override
                public CTFilterColumn get(final int n) {
                    return CTAutoFilterImpl.this.getFilterColumnArray(n);
                }
                
                @Override
                public CTFilterColumn set(final int n, final CTFilterColumn ctFilterColumn) {
                    final CTFilterColumn filterColumnArray = CTAutoFilterImpl.this.getFilterColumnArray(n);
                    CTAutoFilterImpl.this.setFilterColumnArray(n, ctFilterColumn);
                    return filterColumnArray;
                }
                
                @Override
                public void add(final int n, final CTFilterColumn ctFilterColumn) {
                    CTAutoFilterImpl.this.insertNewFilterColumn(n).set((XmlObject)ctFilterColumn);
                }
                
                @Override
                public CTFilterColumn remove(final int n) {
                    final CTFilterColumn filterColumnArray = CTAutoFilterImpl.this.getFilterColumnArray(n);
                    CTAutoFilterImpl.this.removeFilterColumn(n);
                    return filterColumnArray;
                }
                
                @Override
                public int size() {
                    return CTAutoFilterImpl.this.sizeOfFilterColumnArray();
                }
            }
            return new FilterColumnList();
        }
    }
    
    @Deprecated
    public CTFilterColumn[] getFilterColumnArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTAutoFilterImpl.FILTERCOLUMN$0, (List)list);
            final CTFilterColumn[] array = new CTFilterColumn[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFilterColumn getFilterColumnArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFilterColumn ctFilterColumn = (CTFilterColumn)this.get_store().find_element_user(CTAutoFilterImpl.FILTERCOLUMN$0, n);
            if (ctFilterColumn == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFilterColumn;
        }
    }
    
    public int sizeOfFilterColumnArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAutoFilterImpl.FILTERCOLUMN$0);
        }
    }
    
    public void setFilterColumnArray(final CTFilterColumn[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTAutoFilterImpl.FILTERCOLUMN$0);
    }
    
    public void setFilterColumnArray(final int n, final CTFilterColumn ctFilterColumn) {
        this.generatedSetterHelperImpl((XmlObject)ctFilterColumn, CTAutoFilterImpl.FILTERCOLUMN$0, n, (short)2);
    }
    
    public CTFilterColumn insertNewFilterColumn(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFilterColumn)this.get_store().insert_element_user(CTAutoFilterImpl.FILTERCOLUMN$0, n);
        }
    }
    
    public CTFilterColumn addNewFilterColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFilterColumn)this.get_store().add_element_user(CTAutoFilterImpl.FILTERCOLUMN$0);
        }
    }
    
    public void removeFilterColumn(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAutoFilterImpl.FILTERCOLUMN$0, n);
        }
    }
    
    public CTSortState getSortState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSortState ctSortState = (CTSortState)this.get_store().find_element_user(CTAutoFilterImpl.SORTSTATE$2, 0);
            if (ctSortState == null) {
                return null;
            }
            return ctSortState;
        }
    }
    
    public boolean isSetSortState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAutoFilterImpl.SORTSTATE$2) != 0;
        }
    }
    
    public void setSortState(final CTSortState ctSortState) {
        this.generatedSetterHelperImpl((XmlObject)ctSortState, CTAutoFilterImpl.SORTSTATE$2, 0, (short)1);
    }
    
    public CTSortState addNewSortState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSortState)this.get_store().add_element_user(CTAutoFilterImpl.SORTSTATE$2);
        }
    }
    
    public void unsetSortState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAutoFilterImpl.SORTSTATE$2, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTAutoFilterImpl.EXTLST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAutoFilterImpl.EXTLST$4) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTAutoFilterImpl.EXTLST$4, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTAutoFilterImpl.EXTLST$4);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAutoFilterImpl.EXTLST$4, 0);
        }
    }
    
    public String getRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAutoFilterImpl.REF$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRef xgetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRef)this.get_store().find_attribute_user(CTAutoFilterImpl.REF$6);
        }
    }
    
    public boolean isSetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTAutoFilterImpl.REF$6) != null;
        }
    }
    
    public void setRef(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAutoFilterImpl.REF$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAutoFilterImpl.REF$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetRef(final STRef stRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRef stRef2 = (STRef)this.get_store().find_attribute_user(CTAutoFilterImpl.REF$6);
            if (stRef2 == null) {
                stRef2 = (STRef)this.get_store().add_attribute_user(CTAutoFilterImpl.REF$6);
            }
            stRef2.set((XmlObject)stRef);
        }
    }
    
    public void unsetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTAutoFilterImpl.REF$6);
        }
    }
    
    static {
        FILTERCOLUMN$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "filterColumn");
        SORTSTATE$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sortState");
        EXTLST$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        REF$6 = new QName("", "ref");
    }
}
