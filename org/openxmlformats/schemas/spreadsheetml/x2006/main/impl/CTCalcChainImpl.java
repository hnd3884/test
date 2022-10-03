package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcCell;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcChain;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCalcChainImpl extends XmlComplexContentImpl implements CTCalcChain
{
    private static final long serialVersionUID = 1L;
    private static final QName C$0;
    private static final QName EXTLST$2;
    
    public CTCalcChainImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTCalcCell> getCList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CList extends AbstractList<CTCalcCell>
            {
                @Override
                public CTCalcCell get(final int n) {
                    return CTCalcChainImpl.this.getCArray(n);
                }
                
                @Override
                public CTCalcCell set(final int n, final CTCalcCell ctCalcCell) {
                    final CTCalcCell cArray = CTCalcChainImpl.this.getCArray(n);
                    CTCalcChainImpl.this.setCArray(n, ctCalcCell);
                    return cArray;
                }
                
                @Override
                public void add(final int n, final CTCalcCell ctCalcCell) {
                    CTCalcChainImpl.this.insertNewC(n).set((XmlObject)ctCalcCell);
                }
                
                @Override
                public CTCalcCell remove(final int n) {
                    final CTCalcCell cArray = CTCalcChainImpl.this.getCArray(n);
                    CTCalcChainImpl.this.removeC(n);
                    return cArray;
                }
                
                @Override
                public int size() {
                    return CTCalcChainImpl.this.sizeOfCArray();
                }
            }
            return new CList();
        }
    }
    
    @Deprecated
    public CTCalcCell[] getCArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTCalcChainImpl.C$0, (List)list);
            final CTCalcCell[] array = new CTCalcCell[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCalcCell getCArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCalcCell ctCalcCell = (CTCalcCell)this.get_store().find_element_user(CTCalcChainImpl.C$0, n);
            if (ctCalcCell == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCalcCell;
        }
    }
    
    public int sizeOfCArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCalcChainImpl.C$0);
        }
    }
    
    public void setCArray(final CTCalcCell[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTCalcChainImpl.C$0);
    }
    
    public void setCArray(final int n, final CTCalcCell ctCalcCell) {
        this.generatedSetterHelperImpl((XmlObject)ctCalcCell, CTCalcChainImpl.C$0, n, (short)2);
    }
    
    public CTCalcCell insertNewC(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCalcCell)this.get_store().insert_element_user(CTCalcChainImpl.C$0, n);
        }
    }
    
    public CTCalcCell addNewC() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCalcCell)this.get_store().add_element_user(CTCalcChainImpl.C$0);
        }
    }
    
    public void removeC(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCalcChainImpl.C$0, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTCalcChainImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCalcChainImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCalcChainImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTCalcChainImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCalcChainImpl.EXTLST$2, 0);
        }
    }
    
    static {
        C$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "c");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
    }
}
