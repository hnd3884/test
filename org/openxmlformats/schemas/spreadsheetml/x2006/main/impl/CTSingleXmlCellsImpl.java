package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSingleXmlCell;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSingleXmlCells;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSingleXmlCellsImpl extends XmlComplexContentImpl implements CTSingleXmlCells
{
    private static final long serialVersionUID = 1L;
    private static final QName SINGLEXMLCELL$0;
    
    public CTSingleXmlCellsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTSingleXmlCell> getSingleXmlCellList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SingleXmlCellList extends AbstractList<CTSingleXmlCell>
            {
                @Override
                public CTSingleXmlCell get(final int n) {
                    return CTSingleXmlCellsImpl.this.getSingleXmlCellArray(n);
                }
                
                @Override
                public CTSingleXmlCell set(final int n, final CTSingleXmlCell ctSingleXmlCell) {
                    final CTSingleXmlCell singleXmlCellArray = CTSingleXmlCellsImpl.this.getSingleXmlCellArray(n);
                    CTSingleXmlCellsImpl.this.setSingleXmlCellArray(n, ctSingleXmlCell);
                    return singleXmlCellArray;
                }
                
                @Override
                public void add(final int n, final CTSingleXmlCell ctSingleXmlCell) {
                    CTSingleXmlCellsImpl.this.insertNewSingleXmlCell(n).set((XmlObject)ctSingleXmlCell);
                }
                
                @Override
                public CTSingleXmlCell remove(final int n) {
                    final CTSingleXmlCell singleXmlCellArray = CTSingleXmlCellsImpl.this.getSingleXmlCellArray(n);
                    CTSingleXmlCellsImpl.this.removeSingleXmlCell(n);
                    return singleXmlCellArray;
                }
                
                @Override
                public int size() {
                    return CTSingleXmlCellsImpl.this.sizeOfSingleXmlCellArray();
                }
            }
            return new SingleXmlCellList();
        }
    }
    
    @Deprecated
    public CTSingleXmlCell[] getSingleXmlCellArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSingleXmlCellsImpl.SINGLEXMLCELL$0, (List)list);
            final CTSingleXmlCell[] array = new CTSingleXmlCell[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSingleXmlCell getSingleXmlCellArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSingleXmlCell ctSingleXmlCell = (CTSingleXmlCell)this.get_store().find_element_user(CTSingleXmlCellsImpl.SINGLEXMLCELL$0, n);
            if (ctSingleXmlCell == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSingleXmlCell;
        }
    }
    
    public int sizeOfSingleXmlCellArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSingleXmlCellsImpl.SINGLEXMLCELL$0);
        }
    }
    
    public void setSingleXmlCellArray(final CTSingleXmlCell[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSingleXmlCellsImpl.SINGLEXMLCELL$0);
    }
    
    public void setSingleXmlCellArray(final int n, final CTSingleXmlCell ctSingleXmlCell) {
        this.generatedSetterHelperImpl((XmlObject)ctSingleXmlCell, CTSingleXmlCellsImpl.SINGLEXMLCELL$0, n, (short)2);
    }
    
    public CTSingleXmlCell insertNewSingleXmlCell(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSingleXmlCell)this.get_store().insert_element_user(CTSingleXmlCellsImpl.SINGLEXMLCELL$0, n);
        }
    }
    
    public CTSingleXmlCell addNewSingleXmlCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSingleXmlCell)this.get_store().add_element_user(CTSingleXmlCellsImpl.SINGLEXMLCELL$0);
        }
    }
    
    public void removeSingleXmlCell(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSingleXmlCellsImpl.SINGLEXMLCELL$0, n);
        }
    }
    
    static {
        SINGLEXMLCELL$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "singleXmlCell");
    }
}
