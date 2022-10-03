package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIgnoredError;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIgnoredErrors;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTIgnoredErrorsImpl extends XmlComplexContentImpl implements CTIgnoredErrors
{
    private static final long serialVersionUID = 1L;
    private static final QName IGNOREDERROR$0;
    private static final QName EXTLST$2;
    
    public CTIgnoredErrorsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTIgnoredError> getIgnoredErrorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class IgnoredErrorList extends AbstractList<CTIgnoredError>
            {
                @Override
                public CTIgnoredError get(final int n) {
                    return CTIgnoredErrorsImpl.this.getIgnoredErrorArray(n);
                }
                
                @Override
                public CTIgnoredError set(final int n, final CTIgnoredError ctIgnoredError) {
                    final CTIgnoredError ignoredErrorArray = CTIgnoredErrorsImpl.this.getIgnoredErrorArray(n);
                    CTIgnoredErrorsImpl.this.setIgnoredErrorArray(n, ctIgnoredError);
                    return ignoredErrorArray;
                }
                
                @Override
                public void add(final int n, final CTIgnoredError ctIgnoredError) {
                    CTIgnoredErrorsImpl.this.insertNewIgnoredError(n).set((XmlObject)ctIgnoredError);
                }
                
                @Override
                public CTIgnoredError remove(final int n) {
                    final CTIgnoredError ignoredErrorArray = CTIgnoredErrorsImpl.this.getIgnoredErrorArray(n);
                    CTIgnoredErrorsImpl.this.removeIgnoredError(n);
                    return ignoredErrorArray;
                }
                
                @Override
                public int size() {
                    return CTIgnoredErrorsImpl.this.sizeOfIgnoredErrorArray();
                }
            }
            return new IgnoredErrorList();
        }
    }
    
    @Deprecated
    public CTIgnoredError[] getIgnoredErrorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTIgnoredErrorsImpl.IGNOREDERROR$0, (List)list);
            final CTIgnoredError[] array = new CTIgnoredError[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTIgnoredError getIgnoredErrorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTIgnoredError ctIgnoredError = (CTIgnoredError)this.get_store().find_element_user(CTIgnoredErrorsImpl.IGNOREDERROR$0, n);
            if (ctIgnoredError == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctIgnoredError;
        }
    }
    
    public int sizeOfIgnoredErrorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTIgnoredErrorsImpl.IGNOREDERROR$0);
        }
    }
    
    public void setIgnoredErrorArray(final CTIgnoredError[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTIgnoredErrorsImpl.IGNOREDERROR$0);
    }
    
    public void setIgnoredErrorArray(final int n, final CTIgnoredError ctIgnoredError) {
        this.generatedSetterHelperImpl((XmlObject)ctIgnoredError, CTIgnoredErrorsImpl.IGNOREDERROR$0, n, (short)2);
    }
    
    public CTIgnoredError insertNewIgnoredError(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTIgnoredError)this.get_store().insert_element_user(CTIgnoredErrorsImpl.IGNOREDERROR$0, n);
        }
    }
    
    public CTIgnoredError addNewIgnoredError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTIgnoredError)this.get_store().add_element_user(CTIgnoredErrorsImpl.IGNOREDERROR$0);
        }
    }
    
    public void removeIgnoredError(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTIgnoredErrorsImpl.IGNOREDERROR$0, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTIgnoredErrorsImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTIgnoredErrorsImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTIgnoredErrorsImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTIgnoredErrorsImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTIgnoredErrorsImpl.EXTLST$2, 0);
        }
    }
    
    static {
        IGNOREDERROR$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "ignoredError");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
    }
}
