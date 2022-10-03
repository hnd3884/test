package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRow;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetData;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSheetDataImpl extends XmlComplexContentImpl implements CTSheetData
{
    private static final long serialVersionUID = 1L;
    private static final QName ROW$0;
    
    public CTSheetDataImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTRow> getRowList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RowList extends AbstractList<CTRow>
            {
                @Override
                public CTRow get(final int n) {
                    return CTSheetDataImpl.this.getRowArray(n);
                }
                
                @Override
                public CTRow set(final int n, final CTRow ctRow) {
                    final CTRow rowArray = CTSheetDataImpl.this.getRowArray(n);
                    CTSheetDataImpl.this.setRowArray(n, ctRow);
                    return rowArray;
                }
                
                @Override
                public void add(final int n, final CTRow ctRow) {
                    CTSheetDataImpl.this.insertNewRow(n).set((XmlObject)ctRow);
                }
                
                @Override
                public CTRow remove(final int n) {
                    final CTRow rowArray = CTSheetDataImpl.this.getRowArray(n);
                    CTSheetDataImpl.this.removeRow(n);
                    return rowArray;
                }
                
                @Override
                public int size() {
                    return CTSheetDataImpl.this.sizeOfRowArray();
                }
            }
            return new RowList();
        }
    }
    
    @Deprecated
    public CTRow[] getRowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSheetDataImpl.ROW$0, (List)list);
            final CTRow[] array = new CTRow[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRow getRowArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRow ctRow = (CTRow)this.get_store().find_element_user(CTSheetDataImpl.ROW$0, n);
            if (ctRow == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRow;
        }
    }
    
    public int sizeOfRowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSheetDataImpl.ROW$0);
        }
    }
    
    public void setRowArray(final CTRow[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSheetDataImpl.ROW$0);
    }
    
    public void setRowArray(final int n, final CTRow ctRow) {
        this.generatedSetterHelperImpl((XmlObject)ctRow, CTSheetDataImpl.ROW$0, n, (short)2);
    }
    
    public CTRow insertNewRow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRow)this.get_store().insert_element_user(CTSheetDataImpl.ROW$0, n);
        }
    }
    
    public CTRow addNewRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRow)this.get_store().add_element_user(CTSheetDataImpl.ROW$0);
        }
    }
    
    public void removeRow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSheetDataImpl.ROW$0, n);
        }
    }
    
    static {
        ROW$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "row");
    }
}
