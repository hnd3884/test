package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupLocking;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGroupDrawingShapeProps;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNonVisualGroupDrawingShapePropsImpl extends XmlComplexContentImpl implements CTNonVisualGroupDrawingShapeProps
{
    private static final long serialVersionUID = 1L;
    private static final QName GRPSPLOCKS$0;
    private static final QName EXTLST$2;
    
    public CTNonVisualGroupDrawingShapePropsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTGroupLocking getGrpSpLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupLocking ctGroupLocking = (CTGroupLocking)this.get_store().find_element_user(CTNonVisualGroupDrawingShapePropsImpl.GRPSPLOCKS$0, 0);
            if (ctGroupLocking == null) {
                return null;
            }
            return ctGroupLocking;
        }
    }
    
    public boolean isSetGrpSpLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNonVisualGroupDrawingShapePropsImpl.GRPSPLOCKS$0) != 0;
        }
    }
    
    public void setGrpSpLocks(final CTGroupLocking ctGroupLocking) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupLocking, CTNonVisualGroupDrawingShapePropsImpl.GRPSPLOCKS$0, 0, (short)1);
    }
    
    public CTGroupLocking addNewGrpSpLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupLocking)this.get_store().add_element_user(CTNonVisualGroupDrawingShapePropsImpl.GRPSPLOCKS$0);
        }
    }
    
    public void unsetGrpSpLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNonVisualGroupDrawingShapePropsImpl.GRPSPLOCKS$0, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTNonVisualGroupDrawingShapePropsImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNonVisualGroupDrawingShapePropsImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTNonVisualGroupDrawingShapePropsImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTNonVisualGroupDrawingShapePropsImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNonVisualGroupDrawingShapePropsImpl.EXTLST$2, 0);
        }
    }
    
    static {
        GRPSPLOCKS$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "grpSpLocks");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
    }
}
