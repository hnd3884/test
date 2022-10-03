package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.presentationml.x2006.main.STSlideMasterId;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterIdListEntry;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSlideMasterIdListEntryImpl extends XmlComplexContentImpl implements CTSlideMasterIdListEntry
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTLST$0;
    private static final QName ID$2;
    private static final QName ID2$4;
    
    public CTSlideMasterIdListEntryImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTSlideMasterIdListEntryImpl.EXTLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideMasterIdListEntryImpl.EXTLST$0) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTSlideMasterIdListEntryImpl.EXTLST$0, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTSlideMasterIdListEntryImpl.EXTLST$0);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideMasterIdListEntryImpl.EXTLST$0, 0);
        }
    }
    
    public long getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideMasterIdListEntryImpl.ID$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STSlideMasterId xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSlideMasterId)this.get_store().find_attribute_user(CTSlideMasterIdListEntryImpl.ID$2);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSlideMasterIdListEntryImpl.ID$2) != null;
        }
    }
    
    public void setId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideMasterIdListEntryImpl.ID$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSlideMasterIdListEntryImpl.ID$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetId(final STSlideMasterId stSlideMasterId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSlideMasterId stSlideMasterId2 = (STSlideMasterId)this.get_store().find_attribute_user(CTSlideMasterIdListEntryImpl.ID$2);
            if (stSlideMasterId2 == null) {
                stSlideMasterId2 = (STSlideMasterId)this.get_store().add_attribute_user(CTSlideMasterIdListEntryImpl.ID$2);
            }
            stSlideMasterId2.set((XmlObject)stSlideMasterId);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSlideMasterIdListEntryImpl.ID$2);
        }
    }
    
    public String getId2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideMasterIdListEntryImpl.ID2$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetId2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRelationshipId)this.get_store().find_attribute_user(CTSlideMasterIdListEntryImpl.ID2$4);
        }
    }
    
    public void setId2(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideMasterIdListEntryImpl.ID2$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSlideMasterIdListEntryImpl.ID2$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId2(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTSlideMasterIdListEntryImpl.ID2$4);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTSlideMasterIdListEntryImpl.ID2$4);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    static {
        EXTLST$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
        ID$2 = new QName("", "id");
        ID2$4 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
    }
}
