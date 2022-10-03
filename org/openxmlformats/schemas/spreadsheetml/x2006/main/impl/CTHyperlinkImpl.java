package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHyperlink;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTHyperlinkImpl extends XmlComplexContentImpl implements CTHyperlink
{
    private static final long serialVersionUID = 1L;
    private static final QName REF$0;
    private static final QName ID$2;
    private static final QName LOCATION$4;
    private static final QName TOOLTIP$6;
    private static final QName DISPLAY$8;
    
    public CTHyperlinkImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.REF$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRef xgetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRef)this.get_store().find_attribute_user(CTHyperlinkImpl.REF$0);
        }
    }
    
    public void setRef(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.REF$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.REF$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetRef(final STRef stRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRef stRef2 = (STRef)this.get_store().find_attribute_user(CTHyperlinkImpl.REF$0);
            if (stRef2 == null) {
                stRef2 = (STRef)this.get_store().add_attribute_user(CTHyperlinkImpl.REF$0);
            }
            stRef2.set((XmlObject)stRef);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.ID$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRelationshipId)this.get_store().find_attribute_user(CTHyperlinkImpl.ID$2);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.ID$2) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.ID$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.ID$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTHyperlinkImpl.ID$2);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTHyperlinkImpl.ID$2);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.ID$2);
        }
    }
    
    public String getLocation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.LOCATION$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetLocation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTHyperlinkImpl.LOCATION$4);
        }
    }
    
    public boolean isSetLocation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.LOCATION$4) != null;
        }
    }
    
    public void setLocation(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.LOCATION$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.LOCATION$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetLocation(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTHyperlinkImpl.LOCATION$4);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTHyperlinkImpl.LOCATION$4);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetLocation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.LOCATION$4);
        }
    }
    
    public String getTooltip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.TOOLTIP$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetTooltip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTHyperlinkImpl.TOOLTIP$6);
        }
    }
    
    public boolean isSetTooltip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.TOOLTIP$6) != null;
        }
    }
    
    public void setTooltip(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.TOOLTIP$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.TOOLTIP$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTooltip(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTHyperlinkImpl.TOOLTIP$6);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTHyperlinkImpl.TOOLTIP$6);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetTooltip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.TOOLTIP$6);
        }
    }
    
    public String getDisplay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.DISPLAY$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetDisplay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTHyperlinkImpl.DISPLAY$8);
        }
    }
    
    public boolean isSetDisplay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.DISPLAY$8) != null;
        }
    }
    
    public void setDisplay(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.DISPLAY$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.DISPLAY$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDisplay(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTHyperlinkImpl.DISPLAY$8);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTHyperlinkImpl.DISPLAY$8);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetDisplay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.DISPLAY$8);
        }
    }
    
    static {
        REF$0 = new QName("", "ref");
        ID$2 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
        LOCATION$4 = new QName("", "location");
        TOOLTIP$6 = new QName("", "tooltip");
        DISPLAY$8 = new QName("", "display");
    }
}
