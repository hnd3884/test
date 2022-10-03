package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheet;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheets;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSheetsImpl extends XmlComplexContentImpl implements CTSheets
{
    private static final long serialVersionUID = 1L;
    private static final QName SHEET$0;
    
    public CTSheetsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTSheet> getSheetList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SheetList extends AbstractList<CTSheet>
            {
                @Override
                public CTSheet get(final int n) {
                    return CTSheetsImpl.this.getSheetArray(n);
                }
                
                @Override
                public CTSheet set(final int n, final CTSheet ctSheet) {
                    final CTSheet sheetArray = CTSheetsImpl.this.getSheetArray(n);
                    CTSheetsImpl.this.setSheetArray(n, ctSheet);
                    return sheetArray;
                }
                
                @Override
                public void add(final int n, final CTSheet ctSheet) {
                    CTSheetsImpl.this.insertNewSheet(n).set((XmlObject)ctSheet);
                }
                
                @Override
                public CTSheet remove(final int n) {
                    final CTSheet sheetArray = CTSheetsImpl.this.getSheetArray(n);
                    CTSheetsImpl.this.removeSheet(n);
                    return sheetArray;
                }
                
                @Override
                public int size() {
                    return CTSheetsImpl.this.sizeOfSheetArray();
                }
            }
            return new SheetList();
        }
    }
    
    @Deprecated
    public CTSheet[] getSheetArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSheetsImpl.SHEET$0, (List)list);
            final CTSheet[] array = new CTSheet[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSheet getSheetArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheet ctSheet = (CTSheet)this.get_store().find_element_user(CTSheetsImpl.SHEET$0, n);
            if (ctSheet == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSheet;
        }
    }
    
    public int sizeOfSheetArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSheetsImpl.SHEET$0);
        }
    }
    
    public void setSheetArray(final CTSheet[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSheetsImpl.SHEET$0);
    }
    
    public void setSheetArray(final int n, final CTSheet ctSheet) {
        this.generatedSetterHelperImpl((XmlObject)ctSheet, CTSheetsImpl.SHEET$0, n, (short)2);
    }
    
    public CTSheet insertNewSheet(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheet)this.get_store().insert_element_user(CTSheetsImpl.SHEET$0, n);
        }
    }
    
    public CTSheet addNewSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheet)this.get_store().add_element_user(CTSheetsImpl.SHEET$0);
        }
    }
    
    public void removeSheet(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSheetsImpl.SHEET$0, n);
        }
    }
    
    static {
        SHEET$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheet");
    }
}
