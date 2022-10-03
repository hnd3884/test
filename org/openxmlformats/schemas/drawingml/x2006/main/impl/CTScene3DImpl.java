package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBackdrop;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLightRig;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCamera;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScene3D;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTScene3DImpl extends XmlComplexContentImpl implements CTScene3D
{
    private static final long serialVersionUID = 1L;
    private static final QName CAMERA$0;
    private static final QName LIGHTRIG$2;
    private static final QName BACKDROP$4;
    private static final QName EXTLST$6;
    
    public CTScene3DImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTCamera getCamera() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCamera ctCamera = (CTCamera)this.get_store().find_element_user(CTScene3DImpl.CAMERA$0, 0);
            if (ctCamera == null) {
                return null;
            }
            return ctCamera;
        }
    }
    
    public void setCamera(final CTCamera ctCamera) {
        this.generatedSetterHelperImpl((XmlObject)ctCamera, CTScene3DImpl.CAMERA$0, 0, (short)1);
    }
    
    public CTCamera addNewCamera() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCamera)this.get_store().add_element_user(CTScene3DImpl.CAMERA$0);
        }
    }
    
    public CTLightRig getLightRig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLightRig ctLightRig = (CTLightRig)this.get_store().find_element_user(CTScene3DImpl.LIGHTRIG$2, 0);
            if (ctLightRig == null) {
                return null;
            }
            return ctLightRig;
        }
    }
    
    public void setLightRig(final CTLightRig ctLightRig) {
        this.generatedSetterHelperImpl((XmlObject)ctLightRig, CTScene3DImpl.LIGHTRIG$2, 0, (short)1);
    }
    
    public CTLightRig addNewLightRig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLightRig)this.get_store().add_element_user(CTScene3DImpl.LIGHTRIG$2);
        }
    }
    
    public CTBackdrop getBackdrop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBackdrop ctBackdrop = (CTBackdrop)this.get_store().find_element_user(CTScene3DImpl.BACKDROP$4, 0);
            if (ctBackdrop == null) {
                return null;
            }
            return ctBackdrop;
        }
    }
    
    public boolean isSetBackdrop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScene3DImpl.BACKDROP$4) != 0;
        }
    }
    
    public void setBackdrop(final CTBackdrop ctBackdrop) {
        this.generatedSetterHelperImpl((XmlObject)ctBackdrop, CTScene3DImpl.BACKDROP$4, 0, (short)1);
    }
    
    public CTBackdrop addNewBackdrop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBackdrop)this.get_store().add_element_user(CTScene3DImpl.BACKDROP$4);
        }
    }
    
    public void unsetBackdrop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScene3DImpl.BACKDROP$4, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTScene3DImpl.EXTLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScene3DImpl.EXTLST$6) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTScene3DImpl.EXTLST$6, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTScene3DImpl.EXTLST$6);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScene3DImpl.EXTLST$6, 0);
        }
    }
    
    static {
        CAMERA$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "camera");
        LIGHTRIG$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lightRig");
        BACKDROP$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "backdrop");
        EXTLST$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
    }
}
