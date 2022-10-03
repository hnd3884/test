package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCol;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableGrid;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableGridImpl extends XmlComplexContentImpl implements CTTableGrid
{
    private static final long serialVersionUID = 1L;
    private static final QName GRIDCOL$0;
    
    public CTTableGridImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTTableCol> getGridColList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GridColList extends AbstractList<CTTableCol>
            {
                @Override
                public CTTableCol get(final int n) {
                    return CTTableGridImpl.this.getGridColArray(n);
                }
                
                @Override
                public CTTableCol set(final int n, final CTTableCol ctTableCol) {
                    final CTTableCol gridColArray = CTTableGridImpl.this.getGridColArray(n);
                    CTTableGridImpl.this.setGridColArray(n, ctTableCol);
                    return gridColArray;
                }
                
                @Override
                public void add(final int n, final CTTableCol ctTableCol) {
                    CTTableGridImpl.this.insertNewGridCol(n).set((XmlObject)ctTableCol);
                }
                
                @Override
                public CTTableCol remove(final int n) {
                    final CTTableCol gridColArray = CTTableGridImpl.this.getGridColArray(n);
                    CTTableGridImpl.this.removeGridCol(n);
                    return gridColArray;
                }
                
                @Override
                public int size() {
                    return CTTableGridImpl.this.sizeOfGridColArray();
                }
            }
            return new GridColList();
        }
    }
    
    @Deprecated
    public CTTableCol[] getGridColArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTableGridImpl.GRIDCOL$0, (List)list);
            final CTTableCol[] array = new CTTableCol[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTableCol getGridColArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableCol ctTableCol = (CTTableCol)this.get_store().find_element_user(CTTableGridImpl.GRIDCOL$0, n);
            if (ctTableCol == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTableCol;
        }
    }
    
    public int sizeOfGridColArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableGridImpl.GRIDCOL$0);
        }
    }
    
    public void setGridColArray(final CTTableCol[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTableGridImpl.GRIDCOL$0);
    }
    
    public void setGridColArray(final int n, final CTTableCol ctTableCol) {
        this.generatedSetterHelperImpl((XmlObject)ctTableCol, CTTableGridImpl.GRIDCOL$0, n, (short)2);
    }
    
    public CTTableCol insertNewGridCol(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableCol)this.get_store().insert_element_user(CTTableGridImpl.GRIDCOL$0, n);
        }
    }
    
    public CTTableCol addNewGridCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableCol)this.get_store().add_element_user(CTTableGridImpl.GRIDCOL$0);
        }
    }
    
    public void removeGridCol(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableGridImpl.GRIDCOL$0, n);
        }
    }
    
    static {
        GRIDCOL$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gridCol");
    }
}
