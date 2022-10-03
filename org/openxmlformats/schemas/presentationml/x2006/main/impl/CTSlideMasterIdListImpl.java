package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterIdListEntry;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterIdList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSlideMasterIdListImpl extends XmlComplexContentImpl implements CTSlideMasterIdList
{
    private static final long serialVersionUID = 1L;
    private static final QName SLDMASTERID$0;
    
    public CTSlideMasterIdListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTSlideMasterIdListEntry> getSldMasterIdList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SldMasterIdList extends AbstractList<CTSlideMasterIdListEntry>
            {
                @Override
                public CTSlideMasterIdListEntry get(final int n) {
                    return CTSlideMasterIdListImpl.this.getSldMasterIdArray(n);
                }
                
                @Override
                public CTSlideMasterIdListEntry set(final int n, final CTSlideMasterIdListEntry ctSlideMasterIdListEntry) {
                    final CTSlideMasterIdListEntry sldMasterIdArray = CTSlideMasterIdListImpl.this.getSldMasterIdArray(n);
                    CTSlideMasterIdListImpl.this.setSldMasterIdArray(n, ctSlideMasterIdListEntry);
                    return sldMasterIdArray;
                }
                
                @Override
                public void add(final int n, final CTSlideMasterIdListEntry ctSlideMasterIdListEntry) {
                    CTSlideMasterIdListImpl.this.insertNewSldMasterId(n).set((XmlObject)ctSlideMasterIdListEntry);
                }
                
                @Override
                public CTSlideMasterIdListEntry remove(final int n) {
                    final CTSlideMasterIdListEntry sldMasterIdArray = CTSlideMasterIdListImpl.this.getSldMasterIdArray(n);
                    CTSlideMasterIdListImpl.this.removeSldMasterId(n);
                    return sldMasterIdArray;
                }
                
                @Override
                public int size() {
                    return CTSlideMasterIdListImpl.this.sizeOfSldMasterIdArray();
                }
            }
            return new SldMasterIdList();
        }
    }
    
    @Deprecated
    public CTSlideMasterIdListEntry[] getSldMasterIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSlideMasterIdListImpl.SLDMASTERID$0, (List)list);
            final CTSlideMasterIdListEntry[] array = new CTSlideMasterIdListEntry[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSlideMasterIdListEntry getSldMasterIdArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlideMasterIdListEntry ctSlideMasterIdListEntry = (CTSlideMasterIdListEntry)this.get_store().find_element_user(CTSlideMasterIdListImpl.SLDMASTERID$0, n);
            if (ctSlideMasterIdListEntry == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSlideMasterIdListEntry;
        }
    }
    
    public int sizeOfSldMasterIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideMasterIdListImpl.SLDMASTERID$0);
        }
    }
    
    public void setSldMasterIdArray(final CTSlideMasterIdListEntry[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSlideMasterIdListImpl.SLDMASTERID$0);
    }
    
    public void setSldMasterIdArray(final int n, final CTSlideMasterIdListEntry ctSlideMasterIdListEntry) {
        this.generatedSetterHelperImpl((XmlObject)ctSlideMasterIdListEntry, CTSlideMasterIdListImpl.SLDMASTERID$0, n, (short)2);
    }
    
    public CTSlideMasterIdListEntry insertNewSldMasterId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideMasterIdListEntry)this.get_store().insert_element_user(CTSlideMasterIdListImpl.SLDMASTERID$0, n);
        }
    }
    
    public CTSlideMasterIdListEntry addNewSldMasterId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideMasterIdListEntry)this.get_store().add_element_user(CTSlideMasterIdListImpl.SLDMASTERID$0);
        }
    }
    
    public void removeSldMasterId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideMasterIdListImpl.SLDMASTERID$0, n);
        }
    }
    
    static {
        SLDMASTERID$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "sldMasterId");
    }
}
