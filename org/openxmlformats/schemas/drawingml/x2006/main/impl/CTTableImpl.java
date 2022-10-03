package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableRow;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableGrid;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTable;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableImpl extends XmlComplexContentImpl implements CTTable
{
    private static final long serialVersionUID = 1L;
    private static final QName TBLPR$0;
    private static final QName TBLGRID$2;
    private static final QName TR$4;
    
    public CTTableImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTableProperties getTblPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableProperties ctTableProperties = (CTTableProperties)this.get_store().find_element_user(CTTableImpl.TBLPR$0, 0);
            if (ctTableProperties == null) {
                return null;
            }
            return ctTableProperties;
        }
    }
    
    public boolean isSetTblPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableImpl.TBLPR$0) != 0;
        }
    }
    
    public void setTblPr(final CTTableProperties ctTableProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTableProperties, CTTableImpl.TBLPR$0, 0, (short)1);
    }
    
    public CTTableProperties addNewTblPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableProperties)this.get_store().add_element_user(CTTableImpl.TBLPR$0);
        }
    }
    
    public void unsetTblPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableImpl.TBLPR$0, 0);
        }
    }
    
    public CTTableGrid getTblGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableGrid ctTableGrid = (CTTableGrid)this.get_store().find_element_user(CTTableImpl.TBLGRID$2, 0);
            if (ctTableGrid == null) {
                return null;
            }
            return ctTableGrid;
        }
    }
    
    public void setTblGrid(final CTTableGrid ctTableGrid) {
        this.generatedSetterHelperImpl((XmlObject)ctTableGrid, CTTableImpl.TBLGRID$2, 0, (short)1);
    }
    
    public CTTableGrid addNewTblGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableGrid)this.get_store().add_element_user(CTTableImpl.TBLGRID$2);
        }
    }
    
    public List<CTTableRow> getTrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TrList extends AbstractList<CTTableRow>
            {
                @Override
                public CTTableRow get(final int n) {
                    return CTTableImpl.this.getTrArray(n);
                }
                
                @Override
                public CTTableRow set(final int n, final CTTableRow ctTableRow) {
                    final CTTableRow trArray = CTTableImpl.this.getTrArray(n);
                    CTTableImpl.this.setTrArray(n, ctTableRow);
                    return trArray;
                }
                
                @Override
                public void add(final int n, final CTTableRow ctTableRow) {
                    CTTableImpl.this.insertNewTr(n).set((XmlObject)ctTableRow);
                }
                
                @Override
                public CTTableRow remove(final int n) {
                    final CTTableRow trArray = CTTableImpl.this.getTrArray(n);
                    CTTableImpl.this.removeTr(n);
                    return trArray;
                }
                
                @Override
                public int size() {
                    return CTTableImpl.this.sizeOfTrArray();
                }
            }
            return new TrList();
        }
    }
    
    @Deprecated
    public CTTableRow[] getTrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTableImpl.TR$4, (List)list);
            final CTTableRow[] array = new CTTableRow[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTableRow getTrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableRow ctTableRow = (CTTableRow)this.get_store().find_element_user(CTTableImpl.TR$4, n);
            if (ctTableRow == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTableRow;
        }
    }
    
    public int sizeOfTrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableImpl.TR$4);
        }
    }
    
    public void setTrArray(final CTTableRow[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTableImpl.TR$4);
    }
    
    public void setTrArray(final int n, final CTTableRow ctTableRow) {
        this.generatedSetterHelperImpl((XmlObject)ctTableRow, CTTableImpl.TR$4, n, (short)2);
    }
    
    public CTTableRow insertNewTr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableRow)this.get_store().insert_element_user(CTTableImpl.TR$4, n);
        }
    }
    
    public CTTableRow addNewTr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableRow)this.get_store().add_element_user(CTTableImpl.TR$4);
        }
    }
    
    public void removeTr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableImpl.TR$4, n);
        }
    }
    
    static {
        TBLPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tblPr");
        TBLGRID$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tblGrid");
        TR$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tr");
    }
}
