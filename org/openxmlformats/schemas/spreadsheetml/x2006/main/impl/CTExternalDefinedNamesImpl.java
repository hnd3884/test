package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalDefinedName;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalDefinedNames;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTExternalDefinedNamesImpl extends XmlComplexContentImpl implements CTExternalDefinedNames
{
    private static final long serialVersionUID = 1L;
    private static final QName DEFINEDNAME$0;
    
    public CTExternalDefinedNamesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTExternalDefinedName> getDefinedNameList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DefinedNameList extends AbstractList<CTExternalDefinedName>
            {
                @Override
                public CTExternalDefinedName get(final int n) {
                    return CTExternalDefinedNamesImpl.this.getDefinedNameArray(n);
                }
                
                @Override
                public CTExternalDefinedName set(final int n, final CTExternalDefinedName ctExternalDefinedName) {
                    final CTExternalDefinedName definedNameArray = CTExternalDefinedNamesImpl.this.getDefinedNameArray(n);
                    CTExternalDefinedNamesImpl.this.setDefinedNameArray(n, ctExternalDefinedName);
                    return definedNameArray;
                }
                
                @Override
                public void add(final int n, final CTExternalDefinedName ctExternalDefinedName) {
                    CTExternalDefinedNamesImpl.this.insertNewDefinedName(n).set((XmlObject)ctExternalDefinedName);
                }
                
                @Override
                public CTExternalDefinedName remove(final int n) {
                    final CTExternalDefinedName definedNameArray = CTExternalDefinedNamesImpl.this.getDefinedNameArray(n);
                    CTExternalDefinedNamesImpl.this.removeDefinedName(n);
                    return definedNameArray;
                }
                
                @Override
                public int size() {
                    return CTExternalDefinedNamesImpl.this.sizeOfDefinedNameArray();
                }
            }
            return new DefinedNameList();
        }
    }
    
    @Deprecated
    public CTExternalDefinedName[] getDefinedNameArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTExternalDefinedNamesImpl.DEFINEDNAME$0, (List)list);
            final CTExternalDefinedName[] array = new CTExternalDefinedName[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTExternalDefinedName getDefinedNameArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExternalDefinedName ctExternalDefinedName = (CTExternalDefinedName)this.get_store().find_element_user(CTExternalDefinedNamesImpl.DEFINEDNAME$0, n);
            if (ctExternalDefinedName == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctExternalDefinedName;
        }
    }
    
    public int sizeOfDefinedNameArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTExternalDefinedNamesImpl.DEFINEDNAME$0);
        }
    }
    
    public void setDefinedNameArray(final CTExternalDefinedName[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTExternalDefinedNamesImpl.DEFINEDNAME$0);
    }
    
    public void setDefinedNameArray(final int n, final CTExternalDefinedName ctExternalDefinedName) {
        this.generatedSetterHelperImpl((XmlObject)ctExternalDefinedName, CTExternalDefinedNamesImpl.DEFINEDNAME$0, n, (short)2);
    }
    
    public CTExternalDefinedName insertNewDefinedName(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExternalDefinedName)this.get_store().insert_element_user(CTExternalDefinedNamesImpl.DEFINEDNAME$0, n);
        }
    }
    
    public CTExternalDefinedName addNewDefinedName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExternalDefinedName)this.get_store().add_element_user(CTExternalDefinedNamesImpl.DEFINEDNAME$0);
        }
    }
    
    public void removeDefinedName(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTExternalDefinedNamesImpl.DEFINEDNAME$0, n);
        }
    }
    
    static {
        DEFINEDNAME$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "definedName");
    }
}
