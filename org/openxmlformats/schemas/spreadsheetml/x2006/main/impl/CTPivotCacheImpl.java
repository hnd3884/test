package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCache;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPivotCacheImpl extends XmlComplexContentImpl implements CTPivotCache
{
    private static final long serialVersionUID = 1L;
    private static final QName CACHEID$0;
    private static final QName ID$2;
    
    public CTPivotCacheImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public long getCacheId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheImpl.CACHEID$0);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCacheId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotCacheImpl.CACHEID$0);
        }
    }
    
    public void setCacheId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheImpl.CACHEID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheImpl.CACHEID$0);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCacheId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotCacheImpl.CACHEID$0);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPivotCacheImpl.CACHEID$0);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheImpl.ID$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRelationshipId)this.get_store().find_attribute_user(CTPivotCacheImpl.ID$2);
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheImpl.ID$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheImpl.ID$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTPivotCacheImpl.ID$2);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTPivotCacheImpl.ID$2);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    static {
        CACHEID$0 = new QName("", "cacheId");
        ID$2 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
    }
}
