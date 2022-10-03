package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetState;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheet;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSheetImpl extends XmlComplexContentImpl implements CTSheet
{
    private static final long serialVersionUID = 1L;
    private static final QName NAME$0;
    private static final QName SHEETID$2;
    private static final QName STATE$4;
    private static final QName ID$6;
    
    public CTSheetImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetImpl.NAME$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTSheetImpl.NAME$0);
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetImpl.NAME$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetImpl.NAME$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTSheetImpl.NAME$0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTSheetImpl.NAME$0);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public long getSheetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetImpl.SHEETID$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetSheetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetImpl.SHEETID$2);
        }
    }
    
    public void setSheetId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetImpl.SHEETID$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetImpl.SHEETID$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetSheetId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetImpl.SHEETID$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTSheetImpl.SHEETID$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public STSheetState.Enum getState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetImpl.STATE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetImpl.STATE$4);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STSheetState.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STSheetState xgetState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSheetState stSheetState = (STSheetState)this.get_store().find_attribute_user(CTSheetImpl.STATE$4);
            if (stSheetState == null) {
                stSheetState = (STSheetState)this.get_default_attribute_value(CTSheetImpl.STATE$4);
            }
            return stSheetState;
        }
    }
    
    public boolean isSetState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetImpl.STATE$4) != null;
        }
    }
    
    public void setState(final STSheetState.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetImpl.STATE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetImpl.STATE$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetState(final STSheetState stSheetState) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSheetState stSheetState2 = (STSheetState)this.get_store().find_attribute_user(CTSheetImpl.STATE$4);
            if (stSheetState2 == null) {
                stSheetState2 = (STSheetState)this.get_store().add_attribute_user(CTSheetImpl.STATE$4);
            }
            stSheetState2.set((XmlObject)stSheetState);
        }
    }
    
    public void unsetState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetImpl.STATE$4);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetImpl.ID$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRelationshipId)this.get_store().find_attribute_user(CTSheetImpl.ID$6);
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetImpl.ID$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetImpl.ID$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTSheetImpl.ID$6);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTSheetImpl.ID$6);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    static {
        NAME$0 = new QName("", "name");
        SHEETID$2 = new QName("", "sheetId");
        STATE$4 = new QName("", "state");
        ID$6 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
    }
}
