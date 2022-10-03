package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetDataSet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalDefinedNames;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetNames;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalBook;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTExternalBookImpl extends XmlComplexContentImpl implements CTExternalBook
{
    private static final long serialVersionUID = 1L;
    private static final QName SHEETNAMES$0;
    private static final QName DEFINEDNAMES$2;
    private static final QName SHEETDATASET$4;
    private static final QName ID$6;
    
    public CTExternalBookImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTExternalSheetNames getSheetNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExternalSheetNames ctExternalSheetNames = (CTExternalSheetNames)this.get_store().find_element_user(CTExternalBookImpl.SHEETNAMES$0, 0);
            if (ctExternalSheetNames == null) {
                return null;
            }
            return ctExternalSheetNames;
        }
    }
    
    public boolean isSetSheetNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTExternalBookImpl.SHEETNAMES$0) != 0;
        }
    }
    
    public void setSheetNames(final CTExternalSheetNames ctExternalSheetNames) {
        this.generatedSetterHelperImpl((XmlObject)ctExternalSheetNames, CTExternalBookImpl.SHEETNAMES$0, 0, (short)1);
    }
    
    public CTExternalSheetNames addNewSheetNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExternalSheetNames)this.get_store().add_element_user(CTExternalBookImpl.SHEETNAMES$0);
        }
    }
    
    public void unsetSheetNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTExternalBookImpl.SHEETNAMES$0, 0);
        }
    }
    
    public CTExternalDefinedNames getDefinedNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExternalDefinedNames ctExternalDefinedNames = (CTExternalDefinedNames)this.get_store().find_element_user(CTExternalBookImpl.DEFINEDNAMES$2, 0);
            if (ctExternalDefinedNames == null) {
                return null;
            }
            return ctExternalDefinedNames;
        }
    }
    
    public boolean isSetDefinedNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTExternalBookImpl.DEFINEDNAMES$2) != 0;
        }
    }
    
    public void setDefinedNames(final CTExternalDefinedNames ctExternalDefinedNames) {
        this.generatedSetterHelperImpl((XmlObject)ctExternalDefinedNames, CTExternalBookImpl.DEFINEDNAMES$2, 0, (short)1);
    }
    
    public CTExternalDefinedNames addNewDefinedNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExternalDefinedNames)this.get_store().add_element_user(CTExternalBookImpl.DEFINEDNAMES$2);
        }
    }
    
    public void unsetDefinedNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTExternalBookImpl.DEFINEDNAMES$2, 0);
        }
    }
    
    public CTExternalSheetDataSet getSheetDataSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExternalSheetDataSet set = (CTExternalSheetDataSet)this.get_store().find_element_user(CTExternalBookImpl.SHEETDATASET$4, 0);
            if (set == null) {
                return null;
            }
            return set;
        }
    }
    
    public boolean isSetSheetDataSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTExternalBookImpl.SHEETDATASET$4) != 0;
        }
    }
    
    public void setSheetDataSet(final CTExternalSheetDataSet set) {
        this.generatedSetterHelperImpl((XmlObject)set, CTExternalBookImpl.SHEETDATASET$4, 0, (short)1);
    }
    
    public CTExternalSheetDataSet addNewSheetDataSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExternalSheetDataSet)this.get_store().add_element_user(CTExternalBookImpl.SHEETDATASET$4);
        }
    }
    
    public void unsetSheetDataSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTExternalBookImpl.SHEETDATASET$4, 0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTExternalBookImpl.ID$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRelationshipId)this.get_store().find_attribute_user(CTExternalBookImpl.ID$6);
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTExternalBookImpl.ID$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTExternalBookImpl.ID$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTExternalBookImpl.ID$6);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTExternalBookImpl.ID$6);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    static {
        SHEETNAMES$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetNames");
        DEFINEDNAMES$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "definedNames");
        SHEETDATASET$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetDataSet");
        ID$6 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
    }
}
