package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrameNonVisual;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGraphicalObjectFrameImpl extends XmlComplexContentImpl implements CTGraphicalObjectFrame
{
    private static final long serialVersionUID = 1L;
    private static final QName NVGRAPHICFRAMEPR$0;
    private static final QName XFRM$2;
    private static final QName GRAPHIC$4;
    private static final QName EXTLST$6;
    
    public CTGraphicalObjectFrameImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTGraphicalObjectFrameNonVisual getNvGraphicFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGraphicalObjectFrameNonVisual ctGraphicalObjectFrameNonVisual = (CTGraphicalObjectFrameNonVisual)this.get_store().find_element_user(CTGraphicalObjectFrameImpl.NVGRAPHICFRAMEPR$0, 0);
            if (ctGraphicalObjectFrameNonVisual == null) {
                return null;
            }
            return ctGraphicalObjectFrameNonVisual;
        }
    }
    
    public void setNvGraphicFramePr(final CTGraphicalObjectFrameNonVisual ctGraphicalObjectFrameNonVisual) {
        this.generatedSetterHelperImpl((XmlObject)ctGraphicalObjectFrameNonVisual, CTGraphicalObjectFrameImpl.NVGRAPHICFRAMEPR$0, 0, (short)1);
    }
    
    public CTGraphicalObjectFrameNonVisual addNewNvGraphicFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGraphicalObjectFrameNonVisual)this.get_store().add_element_user(CTGraphicalObjectFrameImpl.NVGRAPHICFRAMEPR$0);
        }
    }
    
    public CTTransform2D getXfrm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTransform2D ctTransform2D = (CTTransform2D)this.get_store().find_element_user(CTGraphicalObjectFrameImpl.XFRM$2, 0);
            if (ctTransform2D == null) {
                return null;
            }
            return ctTransform2D;
        }
    }
    
    public void setXfrm(final CTTransform2D ctTransform2D) {
        this.generatedSetterHelperImpl((XmlObject)ctTransform2D, CTGraphicalObjectFrameImpl.XFRM$2, 0, (short)1);
    }
    
    public CTTransform2D addNewXfrm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTransform2D)this.get_store().add_element_user(CTGraphicalObjectFrameImpl.XFRM$2);
        }
    }
    
    public CTGraphicalObject getGraphic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGraphicalObject ctGraphicalObject = (CTGraphicalObject)this.get_store().find_element_user(CTGraphicalObjectFrameImpl.GRAPHIC$4, 0);
            if (ctGraphicalObject == null) {
                return null;
            }
            return ctGraphicalObject;
        }
    }
    
    public void setGraphic(final CTGraphicalObject ctGraphicalObject) {
        this.generatedSetterHelperImpl((XmlObject)ctGraphicalObject, CTGraphicalObjectFrameImpl.GRAPHIC$4, 0, (short)1);
    }
    
    public CTGraphicalObject addNewGraphic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGraphicalObject)this.get_store().add_element_user(CTGraphicalObjectFrameImpl.GRAPHIC$4);
        }
    }
    
    public CTExtensionListModify getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionListModify ctExtensionListModify = (CTExtensionListModify)this.get_store().find_element_user(CTGraphicalObjectFrameImpl.EXTLST$6, 0);
            if (ctExtensionListModify == null) {
                return null;
            }
            return ctExtensionListModify;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGraphicalObjectFrameImpl.EXTLST$6) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionListModify ctExtensionListModify) {
        this.generatedSetterHelperImpl((XmlObject)ctExtensionListModify, CTGraphicalObjectFrameImpl.EXTLST$6, 0, (short)1);
    }
    
    public CTExtensionListModify addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionListModify)this.get_store().add_element_user(CTGraphicalObjectFrameImpl.EXTLST$6);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGraphicalObjectFrameImpl.EXTLST$6, 0);
        }
    }
    
    static {
        NVGRAPHICFRAMEPR$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvGraphicFramePr");
        XFRM$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "xfrm");
        GRAPHIC$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "graphic");
        EXTLST$6 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
    }
}
