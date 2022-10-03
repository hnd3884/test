package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTManualLayout;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayout;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLayoutImpl extends XmlComplexContentImpl implements CTLayout
{
    private static final long serialVersionUID = 1L;
    private static final QName MANUALLAYOUT$0;
    private static final QName EXTLST$2;
    
    public CTLayoutImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTManualLayout getManualLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTManualLayout ctManualLayout = (CTManualLayout)this.get_store().find_element_user(CTLayoutImpl.MANUALLAYOUT$0, 0);
            if (ctManualLayout == null) {
                return null;
            }
            return ctManualLayout;
        }
    }
    
    public boolean isSetManualLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLayoutImpl.MANUALLAYOUT$0) != 0;
        }
    }
    
    public void setManualLayout(final CTManualLayout ctManualLayout) {
        this.generatedSetterHelperImpl((XmlObject)ctManualLayout, CTLayoutImpl.MANUALLAYOUT$0, 0, (short)1);
    }
    
    public CTManualLayout addNewManualLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTManualLayout)this.get_store().add_element_user(CTLayoutImpl.MANUALLAYOUT$0);
        }
    }
    
    public void unsetManualLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLayoutImpl.MANUALLAYOUT$0, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTLayoutImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLayoutImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTLayoutImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTLayoutImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLayoutImpl.EXTLST$2, 0);
        }
    }
    
    static {
        MANUALLAYOUT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "manualLayout");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
