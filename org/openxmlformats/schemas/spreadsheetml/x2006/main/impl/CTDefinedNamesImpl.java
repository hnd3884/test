package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedName;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedNames;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDefinedNamesImpl extends XmlComplexContentImpl implements CTDefinedNames
{
    private static final long serialVersionUID = 1L;
    private static final QName DEFINEDNAME$0;
    
    public CTDefinedNamesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTDefinedName> getDefinedNameList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DefinedNameList extends AbstractList<CTDefinedName>
            {
                @Override
                public CTDefinedName get(final int n) {
                    return CTDefinedNamesImpl.this.getDefinedNameArray(n);
                }
                
                @Override
                public CTDefinedName set(final int n, final CTDefinedName ctDefinedName) {
                    final CTDefinedName definedNameArray = CTDefinedNamesImpl.this.getDefinedNameArray(n);
                    CTDefinedNamesImpl.this.setDefinedNameArray(n, ctDefinedName);
                    return definedNameArray;
                }
                
                @Override
                public void add(final int n, final CTDefinedName ctDefinedName) {
                    CTDefinedNamesImpl.this.insertNewDefinedName(n).set((XmlObject)ctDefinedName);
                }
                
                @Override
                public CTDefinedName remove(final int n) {
                    final CTDefinedName definedNameArray = CTDefinedNamesImpl.this.getDefinedNameArray(n);
                    CTDefinedNamesImpl.this.removeDefinedName(n);
                    return definedNameArray;
                }
                
                @Override
                public int size() {
                    return CTDefinedNamesImpl.this.sizeOfDefinedNameArray();
                }
            }
            return new DefinedNameList();
        }
    }
    
    @Deprecated
    public CTDefinedName[] getDefinedNameArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDefinedNamesImpl.DEFINEDNAME$0, (List)list);
            final CTDefinedName[] array = new CTDefinedName[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDefinedName getDefinedNameArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDefinedName ctDefinedName = (CTDefinedName)this.get_store().find_element_user(CTDefinedNamesImpl.DEFINEDNAME$0, n);
            if (ctDefinedName == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDefinedName;
        }
    }
    
    public int sizeOfDefinedNameArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDefinedNamesImpl.DEFINEDNAME$0);
        }
    }
    
    public void setDefinedNameArray(final CTDefinedName[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDefinedNamesImpl.DEFINEDNAME$0);
    }
    
    public void setDefinedNameArray(final int n, final CTDefinedName ctDefinedName) {
        this.generatedSetterHelperImpl((XmlObject)ctDefinedName, CTDefinedNamesImpl.DEFINEDNAME$0, n, (short)2);
    }
    
    public CTDefinedName insertNewDefinedName(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDefinedName)this.get_store().insert_element_user(CTDefinedNamesImpl.DEFINEDNAME$0, n);
        }
    }
    
    public CTDefinedName addNewDefinedName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDefinedName)this.get_store().add_element_user(CTDefinedNamesImpl.DEFINEDNAME$0);
        }
    }
    
    public void removeDefinedName(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDefinedNamesImpl.DEFINEDNAME$0, n);
        }
    }
    
    static {
        DEFINEDNAME$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "definedName");
    }
}
