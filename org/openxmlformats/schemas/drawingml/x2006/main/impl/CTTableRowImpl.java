package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCell;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableRow;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableRowImpl extends XmlComplexContentImpl implements CTTableRow
{
    private static final long serialVersionUID = 1L;
    private static final QName TC$0;
    private static final QName EXTLST$2;
    private static final QName H$4;
    
    public CTTableRowImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTTableCell> getTcList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TcList extends AbstractList<CTTableCell>
            {
                @Override
                public CTTableCell get(final int n) {
                    return CTTableRowImpl.this.getTcArray(n);
                }
                
                @Override
                public CTTableCell set(final int n, final CTTableCell ctTableCell) {
                    final CTTableCell tcArray = CTTableRowImpl.this.getTcArray(n);
                    CTTableRowImpl.this.setTcArray(n, ctTableCell);
                    return tcArray;
                }
                
                @Override
                public void add(final int n, final CTTableCell ctTableCell) {
                    CTTableRowImpl.this.insertNewTc(n).set((XmlObject)ctTableCell);
                }
                
                @Override
                public CTTableCell remove(final int n) {
                    final CTTableCell tcArray = CTTableRowImpl.this.getTcArray(n);
                    CTTableRowImpl.this.removeTc(n);
                    return tcArray;
                }
                
                @Override
                public int size() {
                    return CTTableRowImpl.this.sizeOfTcArray();
                }
            }
            return new TcList();
        }
    }
    
    @Deprecated
    public CTTableCell[] getTcArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTableRowImpl.TC$0, (List)list);
            final CTTableCell[] array = new CTTableCell[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTableCell getTcArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableCell ctTableCell = (CTTableCell)this.get_store().find_element_user(CTTableRowImpl.TC$0, n);
            if (ctTableCell == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTableCell;
        }
    }
    
    public int sizeOfTcArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableRowImpl.TC$0);
        }
    }
    
    public void setTcArray(final CTTableCell[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTableRowImpl.TC$0);
    }
    
    public void setTcArray(final int n, final CTTableCell ctTableCell) {
        this.generatedSetterHelperImpl((XmlObject)ctTableCell, CTTableRowImpl.TC$0, n, (short)2);
    }
    
    public CTTableCell insertNewTc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableCell)this.get_store().insert_element_user(CTTableRowImpl.TC$0, n);
        }
    }
    
    public CTTableCell addNewTc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableCell)this.get_store().add_element_user(CTTableRowImpl.TC$0);
        }
    }
    
    public void removeTc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableRowImpl.TC$0, n);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTTableRowImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableRowImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTableRowImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTTableRowImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableRowImpl.EXTLST$2, 0);
        }
    }
    
    public long getH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableRowImpl.H$4);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STCoordinate xgetH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCoordinate)this.get_store().find_attribute_user(CTTableRowImpl.H$4);
        }
    }
    
    public void setH(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableRowImpl.H$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableRowImpl.H$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetH(final STCoordinate stCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate stCoordinate2 = (STCoordinate)this.get_store().find_attribute_user(CTTableRowImpl.H$4);
            if (stCoordinate2 == null) {
                stCoordinate2 = (STCoordinate)this.get_store().add_attribute_user(CTTableRowImpl.H$4);
            }
            stCoordinate2.set((XmlObject)stCoordinate);
        }
    }
    
    static {
        TC$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tc");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        H$4 = new QName("", "h");
    }
}
