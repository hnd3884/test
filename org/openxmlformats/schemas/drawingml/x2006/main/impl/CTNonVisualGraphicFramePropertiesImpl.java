package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectFrameLocking;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGraphicFrameProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNonVisualGraphicFramePropertiesImpl extends XmlComplexContentImpl implements CTNonVisualGraphicFrameProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName GRAPHICFRAMELOCKS$0;
    private static final QName EXTLST$2;
    
    public CTNonVisualGraphicFramePropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTGraphicalObjectFrameLocking getGraphicFrameLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGraphicalObjectFrameLocking ctGraphicalObjectFrameLocking = (CTGraphicalObjectFrameLocking)this.get_store().find_element_user(CTNonVisualGraphicFramePropertiesImpl.GRAPHICFRAMELOCKS$0, 0);
            if (ctGraphicalObjectFrameLocking == null) {
                return null;
            }
            return ctGraphicalObjectFrameLocking;
        }
    }
    
    public boolean isSetGraphicFrameLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNonVisualGraphicFramePropertiesImpl.GRAPHICFRAMELOCKS$0) != 0;
        }
    }
    
    public void setGraphicFrameLocks(final CTGraphicalObjectFrameLocking ctGraphicalObjectFrameLocking) {
        this.generatedSetterHelperImpl((XmlObject)ctGraphicalObjectFrameLocking, CTNonVisualGraphicFramePropertiesImpl.GRAPHICFRAMELOCKS$0, 0, (short)1);
    }
    
    public CTGraphicalObjectFrameLocking addNewGraphicFrameLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGraphicalObjectFrameLocking)this.get_store().add_element_user(CTNonVisualGraphicFramePropertiesImpl.GRAPHICFRAMELOCKS$0);
        }
    }
    
    public void unsetGraphicFrameLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNonVisualGraphicFramePropertiesImpl.GRAPHICFRAMELOCKS$0, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTNonVisualGraphicFramePropertiesImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNonVisualGraphicFramePropertiesImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTNonVisualGraphicFramePropertiesImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTNonVisualGraphicFramePropertiesImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNonVisualGraphicFramePropertiesImpl.EXTLST$2, 0);
        }
    }
    
    static {
        GRAPHICFRAMELOCKS$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "graphicFrameLocks");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
    }
}
