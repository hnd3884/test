package com.microsoft.schemas.vml.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import com.microsoft.schemas.vml.CTF;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.vml.CTFormulas;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFormulasImpl extends XmlComplexContentImpl implements CTFormulas
{
    private static final long serialVersionUID = 1L;
    private static final QName F$0;
    
    public CTFormulasImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTF> getFList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FList extends AbstractList<CTF>
            {
                @Override
                public CTF get(final int n) {
                    return CTFormulasImpl.this.getFArray(n);
                }
                
                @Override
                public CTF set(final int n, final CTF ctf) {
                    final CTF fArray = CTFormulasImpl.this.getFArray(n);
                    CTFormulasImpl.this.setFArray(n, ctf);
                    return fArray;
                }
                
                @Override
                public void add(final int n, final CTF ctf) {
                    CTFormulasImpl.this.insertNewF(n).set((XmlObject)ctf);
                }
                
                @Override
                public CTF remove(final int n) {
                    final CTF fArray = CTFormulasImpl.this.getFArray(n);
                    CTFormulasImpl.this.removeF(n);
                    return fArray;
                }
                
                @Override
                public int size() {
                    return CTFormulasImpl.this.sizeOfFArray();
                }
            }
            return new FList();
        }
    }
    
    @Deprecated
    public CTF[] getFArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFormulasImpl.F$0, (List)list);
            final CTF[] array = new CTF[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTF getFArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTF ctf = (CTF)this.get_store().find_element_user(CTFormulasImpl.F$0, n);
            if (ctf == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctf;
        }
    }
    
    public int sizeOfFArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFormulasImpl.F$0);
        }
    }
    
    public void setFArray(final CTF[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFormulasImpl.F$0);
    }
    
    public void setFArray(final int n, final CTF ctf) {
        this.generatedSetterHelperImpl((XmlObject)ctf, CTFormulasImpl.F$0, n, (short)2);
    }
    
    public CTF insertNewF(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTF)this.get_store().insert_element_user(CTFormulasImpl.F$0, n);
        }
    }
    
    public CTF addNewF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTF)this.get_store().add_element_user(CTFormulasImpl.F$0);
        }
    }
    
    public void removeF(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFormulasImpl.F$0, n);
        }
    }
    
    static {
        F$0 = new QName("urn:schemas-microsoft-com:vml", "f");
    }
}
