package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalReference;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalReferences;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTExternalReferencesImpl extends XmlComplexContentImpl implements CTExternalReferences
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTERNALREFERENCE$0;
    
    public CTExternalReferencesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTExternalReference> getExternalReferenceList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ExternalReferenceList extends AbstractList<CTExternalReference>
            {
                @Override
                public CTExternalReference get(final int n) {
                    return CTExternalReferencesImpl.this.getExternalReferenceArray(n);
                }
                
                @Override
                public CTExternalReference set(final int n, final CTExternalReference ctExternalReference) {
                    final CTExternalReference externalReferenceArray = CTExternalReferencesImpl.this.getExternalReferenceArray(n);
                    CTExternalReferencesImpl.this.setExternalReferenceArray(n, ctExternalReference);
                    return externalReferenceArray;
                }
                
                @Override
                public void add(final int n, final CTExternalReference ctExternalReference) {
                    CTExternalReferencesImpl.this.insertNewExternalReference(n).set((XmlObject)ctExternalReference);
                }
                
                @Override
                public CTExternalReference remove(final int n) {
                    final CTExternalReference externalReferenceArray = CTExternalReferencesImpl.this.getExternalReferenceArray(n);
                    CTExternalReferencesImpl.this.removeExternalReference(n);
                    return externalReferenceArray;
                }
                
                @Override
                public int size() {
                    return CTExternalReferencesImpl.this.sizeOfExternalReferenceArray();
                }
            }
            return new ExternalReferenceList();
        }
    }
    
    @Deprecated
    public CTExternalReference[] getExternalReferenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTExternalReferencesImpl.EXTERNALREFERENCE$0, (List)list);
            final CTExternalReference[] array = new CTExternalReference[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTExternalReference getExternalReferenceArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExternalReference ctExternalReference = (CTExternalReference)this.get_store().find_element_user(CTExternalReferencesImpl.EXTERNALREFERENCE$0, n);
            if (ctExternalReference == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctExternalReference;
        }
    }
    
    public int sizeOfExternalReferenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTExternalReferencesImpl.EXTERNALREFERENCE$0);
        }
    }
    
    public void setExternalReferenceArray(final CTExternalReference[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTExternalReferencesImpl.EXTERNALREFERENCE$0);
    }
    
    public void setExternalReferenceArray(final int n, final CTExternalReference ctExternalReference) {
        this.generatedSetterHelperImpl((XmlObject)ctExternalReference, CTExternalReferencesImpl.EXTERNALREFERENCE$0, n, (short)2);
    }
    
    public CTExternalReference insertNewExternalReference(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExternalReference)this.get_store().insert_element_user(CTExternalReferencesImpl.EXTERNALREFERENCE$0, n);
        }
    }
    
    public CTExternalReference addNewExternalReference() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExternalReference)this.get_store().add_element_user(CTExternalReferencesImpl.EXTERNALREFERENCE$0);
        }
    }
    
    public void removeExternalReference(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTExternalReferencesImpl.EXTERNALREFERENCE$0, n);
        }
    }
    
    static {
        EXTERNALREFERENCE$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "externalReference");
    }
}
