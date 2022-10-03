package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMasterIdListEntry;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMasterIdList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNotesMasterIdListImpl extends XmlComplexContentImpl implements CTNotesMasterIdList
{
    private static final long serialVersionUID = 1L;
    private static final QName NOTESMASTERID$0;
    
    public CTNotesMasterIdListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTNotesMasterIdListEntry getNotesMasterId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNotesMasterIdListEntry ctNotesMasterIdListEntry = (CTNotesMasterIdListEntry)this.get_store().find_element_user(CTNotesMasterIdListImpl.NOTESMASTERID$0, 0);
            if (ctNotesMasterIdListEntry == null) {
                return null;
            }
            return ctNotesMasterIdListEntry;
        }
    }
    
    public boolean isSetNotesMasterId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNotesMasterIdListImpl.NOTESMASTERID$0) != 0;
        }
    }
    
    public void setNotesMasterId(final CTNotesMasterIdListEntry ctNotesMasterIdListEntry) {
        this.generatedSetterHelperImpl((XmlObject)ctNotesMasterIdListEntry, CTNotesMasterIdListImpl.NOTESMASTERID$0, 0, (short)1);
    }
    
    public CTNotesMasterIdListEntry addNewNotesMasterId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNotesMasterIdListEntry)this.get_store().add_element_user(CTNotesMasterIdListImpl.NOTESMASTERID$0);
        }
    }
    
    public void unsetNotesMasterId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNotesMasterIdListImpl.NOTESMASTERID$0, 0);
        }
    }
    
    static {
        NOTESMASTERID$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "notesMasterId");
    }
}
