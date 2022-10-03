package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetName;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetNames;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTExternalSheetNamesImpl extends XmlComplexContentImpl implements CTExternalSheetNames
{
    private static final long serialVersionUID = 1L;
    private static final QName SHEETNAME$0;
    
    public CTExternalSheetNamesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTExternalSheetName> getSheetNameList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SheetNameList extends AbstractList<CTExternalSheetName>
            {
                @Override
                public CTExternalSheetName get(final int n) {
                    return CTExternalSheetNamesImpl.this.getSheetNameArray(n);
                }
                
                @Override
                public CTExternalSheetName set(final int n, final CTExternalSheetName ctExternalSheetName) {
                    final CTExternalSheetName sheetNameArray = CTExternalSheetNamesImpl.this.getSheetNameArray(n);
                    CTExternalSheetNamesImpl.this.setSheetNameArray(n, ctExternalSheetName);
                    return sheetNameArray;
                }
                
                @Override
                public void add(final int n, final CTExternalSheetName ctExternalSheetName) {
                    CTExternalSheetNamesImpl.this.insertNewSheetName(n).set((XmlObject)ctExternalSheetName);
                }
                
                @Override
                public CTExternalSheetName remove(final int n) {
                    final CTExternalSheetName sheetNameArray = CTExternalSheetNamesImpl.this.getSheetNameArray(n);
                    CTExternalSheetNamesImpl.this.removeSheetName(n);
                    return sheetNameArray;
                }
                
                @Override
                public int size() {
                    return CTExternalSheetNamesImpl.this.sizeOfSheetNameArray();
                }
            }
            return new SheetNameList();
        }
    }
    
    @Deprecated
    public CTExternalSheetName[] getSheetNameArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTExternalSheetNamesImpl.SHEETNAME$0, (List)list);
            final CTExternalSheetName[] array = new CTExternalSheetName[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTExternalSheetName getSheetNameArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExternalSheetName ctExternalSheetName = (CTExternalSheetName)this.get_store().find_element_user(CTExternalSheetNamesImpl.SHEETNAME$0, n);
            if (ctExternalSheetName == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctExternalSheetName;
        }
    }
    
    public int sizeOfSheetNameArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTExternalSheetNamesImpl.SHEETNAME$0);
        }
    }
    
    public void setSheetNameArray(final CTExternalSheetName[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTExternalSheetNamesImpl.SHEETNAME$0);
    }
    
    public void setSheetNameArray(final int n, final CTExternalSheetName ctExternalSheetName) {
        this.generatedSetterHelperImpl((XmlObject)ctExternalSheetName, CTExternalSheetNamesImpl.SHEETNAME$0, n, (short)2);
    }
    
    public CTExternalSheetName insertNewSheetName(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExternalSheetName)this.get_store().insert_element_user(CTExternalSheetNamesImpl.SHEETNAME$0, n);
        }
    }
    
    public CTExternalSheetName addNewSheetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExternalSheetName)this.get_store().add_element_user(CTExternalSheetNamesImpl.SHEETNAME$0);
        }
    }
    
    public void removeSheetName(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTExternalSheetNamesImpl.SHEETNAME$0, n);
        }
    }
    
    static {
        SHEETNAME$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetName");
    }
}
