package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdListEntry;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSlideIdListImpl extends XmlComplexContentImpl implements CTSlideIdList
{
    private static final long serialVersionUID = 1L;
    private static final QName SLDID$0;
    
    public CTSlideIdListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTSlideIdListEntry> getSldIdList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SldIdList extends AbstractList<CTSlideIdListEntry>
            {
                @Override
                public CTSlideIdListEntry get(final int n) {
                    return CTSlideIdListImpl.this.getSldIdArray(n);
                }
                
                @Override
                public CTSlideIdListEntry set(final int n, final CTSlideIdListEntry ctSlideIdListEntry) {
                    final CTSlideIdListEntry sldIdArray = CTSlideIdListImpl.this.getSldIdArray(n);
                    CTSlideIdListImpl.this.setSldIdArray(n, ctSlideIdListEntry);
                    return sldIdArray;
                }
                
                @Override
                public void add(final int n, final CTSlideIdListEntry ctSlideIdListEntry) {
                    CTSlideIdListImpl.this.insertNewSldId(n).set((XmlObject)ctSlideIdListEntry);
                }
                
                @Override
                public CTSlideIdListEntry remove(final int n) {
                    final CTSlideIdListEntry sldIdArray = CTSlideIdListImpl.this.getSldIdArray(n);
                    CTSlideIdListImpl.this.removeSldId(n);
                    return sldIdArray;
                }
                
                @Override
                public int size() {
                    return CTSlideIdListImpl.this.sizeOfSldIdArray();
                }
            }
            return new SldIdList();
        }
    }
    
    @Deprecated
    public CTSlideIdListEntry[] getSldIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSlideIdListImpl.SLDID$0, (List)list);
            final CTSlideIdListEntry[] array = new CTSlideIdListEntry[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSlideIdListEntry getSldIdArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlideIdListEntry ctSlideIdListEntry = (CTSlideIdListEntry)this.get_store().find_element_user(CTSlideIdListImpl.SLDID$0, n);
            if (ctSlideIdListEntry == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSlideIdListEntry;
        }
    }
    
    public int sizeOfSldIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideIdListImpl.SLDID$0);
        }
    }
    
    public void setSldIdArray(final CTSlideIdListEntry[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSlideIdListImpl.SLDID$0);
    }
    
    public void setSldIdArray(final int n, final CTSlideIdListEntry ctSlideIdListEntry) {
        this.generatedSetterHelperImpl((XmlObject)ctSlideIdListEntry, CTSlideIdListImpl.SLDID$0, n, (short)2);
    }
    
    public CTSlideIdListEntry insertNewSldId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideIdListEntry)this.get_store().insert_element_user(CTSlideIdListImpl.SLDID$0, n);
        }
    }
    
    public CTSlideIdListEntry addNewSldId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideIdListEntry)this.get_store().add_element_user(CTSlideIdListImpl.SLDID$0);
        }
    }
    
    public void removeSldId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideIdListImpl.SLDID$0, n);
        }
    }
    
    static {
        SLDID$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "sldId");
    }
}
