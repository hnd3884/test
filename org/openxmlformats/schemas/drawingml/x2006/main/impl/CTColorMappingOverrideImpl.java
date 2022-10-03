package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMapping;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEmptyElement;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMappingOverride;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTColorMappingOverrideImpl extends XmlComplexContentImpl implements CTColorMappingOverride
{
    private static final long serialVersionUID = 1L;
    private static final QName MASTERCLRMAPPING$0;
    private static final QName OVERRIDECLRMAPPING$2;
    
    public CTColorMappingOverrideImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTEmptyElement getMasterClrMapping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmptyElement ctEmptyElement = (CTEmptyElement)this.get_store().find_element_user(CTColorMappingOverrideImpl.MASTERCLRMAPPING$0, 0);
            if (ctEmptyElement == null) {
                return null;
            }
            return ctEmptyElement;
        }
    }
    
    public boolean isSetMasterClrMapping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTColorMappingOverrideImpl.MASTERCLRMAPPING$0) != 0;
        }
    }
    
    public void setMasterClrMapping(final CTEmptyElement ctEmptyElement) {
        this.generatedSetterHelperImpl((XmlObject)ctEmptyElement, CTColorMappingOverrideImpl.MASTERCLRMAPPING$0, 0, (short)1);
    }
    
    public CTEmptyElement addNewMasterClrMapping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmptyElement)this.get_store().add_element_user(CTColorMappingOverrideImpl.MASTERCLRMAPPING$0);
        }
    }
    
    public void unsetMasterClrMapping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTColorMappingOverrideImpl.MASTERCLRMAPPING$0, 0);
        }
    }
    
    public CTColorMapping getOverrideClrMapping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColorMapping ctColorMapping = (CTColorMapping)this.get_store().find_element_user(CTColorMappingOverrideImpl.OVERRIDECLRMAPPING$2, 0);
            if (ctColorMapping == null) {
                return null;
            }
            return ctColorMapping;
        }
    }
    
    public boolean isSetOverrideClrMapping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTColorMappingOverrideImpl.OVERRIDECLRMAPPING$2) != 0;
        }
    }
    
    public void setOverrideClrMapping(final CTColorMapping ctColorMapping) {
        this.generatedSetterHelperImpl((XmlObject)ctColorMapping, CTColorMappingOverrideImpl.OVERRIDECLRMAPPING$2, 0, (short)1);
    }
    
    public CTColorMapping addNewOverrideClrMapping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorMapping)this.get_store().add_element_user(CTColorMappingOverrideImpl.OVERRIDECLRMAPPING$2);
        }
    }
    
    public void unsetOverrideClrMapping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTColorMappingOverrideImpl.OVERRIDECLRMAPPING$2, 0);
        }
    }
    
    static {
        MASTERCLRMAPPING$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "masterClrMapping");
        OVERRIDECLRMAPPING$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "overrideClrMapping");
    }
}
