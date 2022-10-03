package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridCol;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridBase;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTblGridBaseImpl extends XmlComplexContentImpl implements CTTblGridBase
{
    private static final long serialVersionUID = 1L;
    private static final QName GRIDCOL$0;
    
    public CTTblGridBaseImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTTblGridCol> getGridColList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GridColList extends AbstractList<CTTblGridCol>
            {
                @Override
                public CTTblGridCol get(final int n) {
                    return CTTblGridBaseImpl.this.getGridColArray(n);
                }
                
                @Override
                public CTTblGridCol set(final int n, final CTTblGridCol ctTblGridCol) {
                    final CTTblGridCol gridColArray = CTTblGridBaseImpl.this.getGridColArray(n);
                    CTTblGridBaseImpl.this.setGridColArray(n, ctTblGridCol);
                    return gridColArray;
                }
                
                @Override
                public void add(final int n, final CTTblGridCol ctTblGridCol) {
                    CTTblGridBaseImpl.this.insertNewGridCol(n).set((XmlObject)ctTblGridCol);
                }
                
                @Override
                public CTTblGridCol remove(final int n) {
                    final CTTblGridCol gridColArray = CTTblGridBaseImpl.this.getGridColArray(n);
                    CTTblGridBaseImpl.this.removeGridCol(n);
                    return gridColArray;
                }
                
                @Override
                public int size() {
                    return CTTblGridBaseImpl.this.sizeOfGridColArray();
                }
            }
            return new GridColList();
        }
    }
    
    @Deprecated
    public CTTblGridCol[] getGridColArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTblGridBaseImpl.GRIDCOL$0, (List)list);
            final CTTblGridCol[] array = new CTTblGridCol[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTblGridCol getGridColArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblGridCol ctTblGridCol = (CTTblGridCol)this.get_store().find_element_user(CTTblGridBaseImpl.GRIDCOL$0, n);
            if (ctTblGridCol == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTblGridCol;
        }
    }
    
    public int sizeOfGridColArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblGridBaseImpl.GRIDCOL$0);
        }
    }
    
    public void setGridColArray(final CTTblGridCol[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblGridBaseImpl.GRIDCOL$0);
    }
    
    public void setGridColArray(final int n, final CTTblGridCol ctTblGridCol) {
        this.generatedSetterHelperImpl((XmlObject)ctTblGridCol, CTTblGridBaseImpl.GRIDCOL$0, n, (short)2);
    }
    
    public CTTblGridCol insertNewGridCol(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblGridCol)this.get_store().insert_element_user(CTTblGridBaseImpl.GRIDCOL$0, n);
        }
    }
    
    public CTTblGridCol addNewGridCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblGridCol)this.get_store().add_element_user(CTTblGridBaseImpl.GRIDCOL$0);
        }
    }
    
    public void removeGridCol(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblGridBaseImpl.GRIDCOL$0, n);
        }
    }
    
    static {
        GRIDCOL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "gridCol");
    }
}
