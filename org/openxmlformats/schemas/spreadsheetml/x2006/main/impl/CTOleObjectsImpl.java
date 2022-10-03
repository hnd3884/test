package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObject;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObjects;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTOleObjectsImpl extends XmlComplexContentImpl implements CTOleObjects
{
    private static final long serialVersionUID = 1L;
    private static final QName OLEOBJECT$0;
    
    public CTOleObjectsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTOleObject> getOleObjectList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OleObjectList extends AbstractList<CTOleObject>
            {
                @Override
                public CTOleObject get(final int n) {
                    return CTOleObjectsImpl.this.getOleObjectArray(n);
                }
                
                @Override
                public CTOleObject set(final int n, final CTOleObject ctOleObject) {
                    final CTOleObject oleObjectArray = CTOleObjectsImpl.this.getOleObjectArray(n);
                    CTOleObjectsImpl.this.setOleObjectArray(n, ctOleObject);
                    return oleObjectArray;
                }
                
                @Override
                public void add(final int n, final CTOleObject ctOleObject) {
                    CTOleObjectsImpl.this.insertNewOleObject(n).set((XmlObject)ctOleObject);
                }
                
                @Override
                public CTOleObject remove(final int n) {
                    final CTOleObject oleObjectArray = CTOleObjectsImpl.this.getOleObjectArray(n);
                    CTOleObjectsImpl.this.removeOleObject(n);
                    return oleObjectArray;
                }
                
                @Override
                public int size() {
                    return CTOleObjectsImpl.this.sizeOfOleObjectArray();
                }
            }
            return new OleObjectList();
        }
    }
    
    @Deprecated
    public CTOleObject[] getOleObjectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTOleObjectsImpl.OLEOBJECT$0, (List)list);
            final CTOleObject[] array = new CTOleObject[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOleObject getOleObjectArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOleObject ctOleObject = (CTOleObject)this.get_store().find_element_user(CTOleObjectsImpl.OLEOBJECT$0, n);
            if (ctOleObject == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctOleObject;
        }
    }
    
    public int sizeOfOleObjectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOleObjectsImpl.OLEOBJECT$0);
        }
    }
    
    public void setOleObjectArray(final CTOleObject[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTOleObjectsImpl.OLEOBJECT$0);
    }
    
    public void setOleObjectArray(final int n, final CTOleObject ctOleObject) {
        this.generatedSetterHelperImpl((XmlObject)ctOleObject, CTOleObjectsImpl.OLEOBJECT$0, n, (short)2);
    }
    
    public CTOleObject insertNewOleObject(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOleObject)this.get_store().insert_element_user(CTOleObjectsImpl.OLEOBJECT$0, n);
        }
    }
    
    public CTOleObject addNewOleObject() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOleObject)this.get_store().add_element_user(CTOleObjectsImpl.OLEOBJECT$0);
        }
    }
    
    public void removeOleObject(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOleObjectsImpl.OLEOBJECT$0, n);
        }
    }
    
    static {
        OLEOBJECT$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "oleObject");
    }
}
