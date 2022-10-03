package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMasterIdListEntry;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNotesMasterIdListEntryImpl extends XmlComplexContentImpl implements CTNotesMasterIdListEntry
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTLST$0;
    private static final QName ID$2;
    
    public CTNotesMasterIdListEntryImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTNotesMasterIdListEntryImpl.EXTLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNotesMasterIdListEntryImpl.EXTLST$0) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTNotesMasterIdListEntryImpl.EXTLST$0, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTNotesMasterIdListEntryImpl.EXTLST$0);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNotesMasterIdListEntryImpl.EXTLST$0, 0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNotesMasterIdListEntryImpl.ID$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRelationshipId)this.get_store().find_attribute_user(CTNotesMasterIdListEntryImpl.ID$2);
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNotesMasterIdListEntryImpl.ID$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNotesMasterIdListEntryImpl.ID$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTNotesMasterIdListEntryImpl.ID$2);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTNotesMasterIdListEntryImpl.ID$2);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    static {
        EXTLST$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
        ID$2 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
    }
}
