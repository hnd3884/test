package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExternalData;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTExternalDataImpl extends XmlComplexContentImpl implements CTExternalData
{
    private static final long serialVersionUID = 1L;
    private static final QName AUTOUPDATE$0;
    private static final QName ID$2;
    
    public CTExternalDataImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBoolean getAutoUpdate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTExternalDataImpl.AUTOUPDATE$0, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetAutoUpdate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTExternalDataImpl.AUTOUPDATE$0) != 0;
        }
    }
    
    public void setAutoUpdate(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTExternalDataImpl.AUTOUPDATE$0, 0, (short)1);
    }
    
    public CTBoolean addNewAutoUpdate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTExternalDataImpl.AUTOUPDATE$0);
        }
    }
    
    public void unsetAutoUpdate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTExternalDataImpl.AUTOUPDATE$0, 0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTExternalDataImpl.ID$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRelationshipId)this.get_store().find_attribute_user(CTExternalDataImpl.ID$2);
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTExternalDataImpl.ID$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTExternalDataImpl.ID$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTExternalDataImpl.ID$2);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTExternalDataImpl.ID$2);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    static {
        AUTOUPDATE$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "autoUpdate");
        ID$2 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
    }
}
