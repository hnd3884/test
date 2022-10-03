package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnection;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnectorLocking;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualConnectorProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNonVisualConnectorPropertiesImpl extends XmlComplexContentImpl implements CTNonVisualConnectorProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName CXNSPLOCKS$0;
    private static final QName STCXN$2;
    private static final QName ENDCXN$4;
    private static final QName EXTLST$6;
    
    public CTNonVisualConnectorPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTConnectorLocking getCxnSpLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTConnectorLocking ctConnectorLocking = (CTConnectorLocking)this.get_store().find_element_user(CTNonVisualConnectorPropertiesImpl.CXNSPLOCKS$0, 0);
            if (ctConnectorLocking == null) {
                return null;
            }
            return ctConnectorLocking;
        }
    }
    
    public boolean isSetCxnSpLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNonVisualConnectorPropertiesImpl.CXNSPLOCKS$0) != 0;
        }
    }
    
    public void setCxnSpLocks(final CTConnectorLocking ctConnectorLocking) {
        this.generatedSetterHelperImpl((XmlObject)ctConnectorLocking, CTNonVisualConnectorPropertiesImpl.CXNSPLOCKS$0, 0, (short)1);
    }
    
    public CTConnectorLocking addNewCxnSpLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTConnectorLocking)this.get_store().add_element_user(CTNonVisualConnectorPropertiesImpl.CXNSPLOCKS$0);
        }
    }
    
    public void unsetCxnSpLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNonVisualConnectorPropertiesImpl.CXNSPLOCKS$0, 0);
        }
    }
    
    public CTConnection getStCxn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTConnection ctConnection = (CTConnection)this.get_store().find_element_user(CTNonVisualConnectorPropertiesImpl.STCXN$2, 0);
            if (ctConnection == null) {
                return null;
            }
            return ctConnection;
        }
    }
    
    public boolean isSetStCxn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNonVisualConnectorPropertiesImpl.STCXN$2) != 0;
        }
    }
    
    public void setStCxn(final CTConnection ctConnection) {
        this.generatedSetterHelperImpl((XmlObject)ctConnection, CTNonVisualConnectorPropertiesImpl.STCXN$2, 0, (short)1);
    }
    
    public CTConnection addNewStCxn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTConnection)this.get_store().add_element_user(CTNonVisualConnectorPropertiesImpl.STCXN$2);
        }
    }
    
    public void unsetStCxn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNonVisualConnectorPropertiesImpl.STCXN$2, 0);
        }
    }
    
    public CTConnection getEndCxn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTConnection ctConnection = (CTConnection)this.get_store().find_element_user(CTNonVisualConnectorPropertiesImpl.ENDCXN$4, 0);
            if (ctConnection == null) {
                return null;
            }
            return ctConnection;
        }
    }
    
    public boolean isSetEndCxn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNonVisualConnectorPropertiesImpl.ENDCXN$4) != 0;
        }
    }
    
    public void setEndCxn(final CTConnection ctConnection) {
        this.generatedSetterHelperImpl((XmlObject)ctConnection, CTNonVisualConnectorPropertiesImpl.ENDCXN$4, 0, (short)1);
    }
    
    public CTConnection addNewEndCxn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTConnection)this.get_store().add_element_user(CTNonVisualConnectorPropertiesImpl.ENDCXN$4);
        }
    }
    
    public void unsetEndCxn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNonVisualConnectorPropertiesImpl.ENDCXN$4, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTNonVisualConnectorPropertiesImpl.EXTLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNonVisualConnectorPropertiesImpl.EXTLST$6) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTNonVisualConnectorPropertiesImpl.EXTLST$6, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTNonVisualConnectorPropertiesImpl.EXTLST$6);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNonVisualConnectorPropertiesImpl.EXTLST$6, 0);
        }
    }
    
    static {
        CXNSPLOCKS$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "cxnSpLocks");
        STCXN$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "stCxn");
        ENDCXN$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "endCxn");
        EXTLST$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
    }
}
