package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTShape3D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScene3D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectContainer;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectStyleItem;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTEffectStyleItemImpl extends XmlComplexContentImpl implements CTEffectStyleItem
{
    private static final long serialVersionUID = 1L;
    private static final QName EFFECTLST$0;
    private static final QName EFFECTDAG$2;
    private static final QName SCENE3D$4;
    private static final QName SP3D$6;
    
    public CTEffectStyleItemImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTEffectList getEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectList list = (CTEffectList)this.get_store().find_element_user(CTEffectStyleItemImpl.EFFECTLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectStyleItemImpl.EFFECTLST$0) != 0;
        }
    }
    
    public void setEffectLst(final CTEffectList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTEffectStyleItemImpl.EFFECTLST$0, 0, (short)1);
    }
    
    public CTEffectList addNewEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectList)this.get_store().add_element_user(CTEffectStyleItemImpl.EFFECTLST$0);
        }
    }
    
    public void unsetEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectStyleItemImpl.EFFECTLST$0, 0);
        }
    }
    
    public CTEffectContainer getEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectContainer ctEffectContainer = (CTEffectContainer)this.get_store().find_element_user(CTEffectStyleItemImpl.EFFECTDAG$2, 0);
            if (ctEffectContainer == null) {
                return null;
            }
            return ctEffectContainer;
        }
    }
    
    public boolean isSetEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectStyleItemImpl.EFFECTDAG$2) != 0;
        }
    }
    
    public void setEffectDag(final CTEffectContainer ctEffectContainer) {
        this.generatedSetterHelperImpl((XmlObject)ctEffectContainer, CTEffectStyleItemImpl.EFFECTDAG$2, 0, (short)1);
    }
    
    public CTEffectContainer addNewEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectContainer)this.get_store().add_element_user(CTEffectStyleItemImpl.EFFECTDAG$2);
        }
    }
    
    public void unsetEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectStyleItemImpl.EFFECTDAG$2, 0);
        }
    }
    
    public CTScene3D getScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTScene3D ctScene3D = (CTScene3D)this.get_store().find_element_user(CTEffectStyleItemImpl.SCENE3D$4, 0);
            if (ctScene3D == null) {
                return null;
            }
            return ctScene3D;
        }
    }
    
    public boolean isSetScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectStyleItemImpl.SCENE3D$4) != 0;
        }
    }
    
    public void setScene3D(final CTScene3D ctScene3D) {
        this.generatedSetterHelperImpl((XmlObject)ctScene3D, CTEffectStyleItemImpl.SCENE3D$4, 0, (short)1);
    }
    
    public CTScene3D addNewScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScene3D)this.get_store().add_element_user(CTEffectStyleItemImpl.SCENE3D$4);
        }
    }
    
    public void unsetScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectStyleItemImpl.SCENE3D$4, 0);
        }
    }
    
    public CTShape3D getSp3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShape3D ctShape3D = (CTShape3D)this.get_store().find_element_user(CTEffectStyleItemImpl.SP3D$6, 0);
            if (ctShape3D == null) {
                return null;
            }
            return ctShape3D;
        }
    }
    
    public boolean isSetSp3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectStyleItemImpl.SP3D$6) != 0;
        }
    }
    
    public void setSp3D(final CTShape3D ctShape3D) {
        this.generatedSetterHelperImpl((XmlObject)ctShape3D, CTEffectStyleItemImpl.SP3D$6, 0, (short)1);
    }
    
    public CTShape3D addNewSp3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShape3D)this.get_store().add_element_user(CTEffectStyleItemImpl.SP3D$6);
        }
    }
    
    public void unsetSp3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectStyleItemImpl.SP3D$6, 0);
        }
    }
    
    static {
        EFFECTLST$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectLst");
        EFFECTDAG$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectDag");
        SCENE3D$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "scene3d");
        SP3D$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "sp3d");
    }
}
