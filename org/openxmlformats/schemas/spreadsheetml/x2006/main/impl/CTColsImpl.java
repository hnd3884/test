package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCols;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTColsImpl extends XmlComplexContentImpl implements CTCols
{
    private static final long serialVersionUID = 1L;
    private static final QName COL$0;
    
    public CTColsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTCol> getColList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ColList extends AbstractList<CTCol>
            {
                @Override
                public CTCol get(final int n) {
                    return CTColsImpl.this.getColArray(n);
                }
                
                @Override
                public CTCol set(final int n, final CTCol ctCol) {
                    final CTCol colArray = CTColsImpl.this.getColArray(n);
                    CTColsImpl.this.setColArray(n, ctCol);
                    return colArray;
                }
                
                @Override
                public void add(final int n, final CTCol ctCol) {
                    CTColsImpl.this.insertNewCol(n).set((XmlObject)ctCol);
                }
                
                @Override
                public CTCol remove(final int n) {
                    final CTCol colArray = CTColsImpl.this.getColArray(n);
                    CTColsImpl.this.removeCol(n);
                    return colArray;
                }
                
                @Override
                public int size() {
                    return CTColsImpl.this.sizeOfColArray();
                }
            }
            return new ColList();
        }
    }
    
    @Deprecated
    public CTCol[] getColArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTColsImpl.COL$0, (List)list);
            final CTCol[] array = new CTCol[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCol getColArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCol ctCol = (CTCol)this.get_store().find_element_user(CTColsImpl.COL$0, n);
            if (ctCol == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCol;
        }
    }
    
    public int sizeOfColArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTColsImpl.COL$0);
        }
    }
    
    public void setColArray(final CTCol[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTColsImpl.COL$0);
    }
    
    public void setColArray(final int n, final CTCol ctCol) {
        this.generatedSetterHelperImpl((XmlObject)ctCol, CTColsImpl.COL$0, n, (short)2);
    }
    
    public CTCol insertNewCol(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCol)this.get_store().insert_element_user(CTColsImpl.COL$0, n);
        }
    }
    
    public CTCol addNewCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCol)this.get_store().add_element_user(CTColsImpl.COL$0);
        }
    }
    
    public void removeCol(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTColsImpl.COL$0, n);
        }
    }
    
    static {
        COL$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "col");
    }
}
