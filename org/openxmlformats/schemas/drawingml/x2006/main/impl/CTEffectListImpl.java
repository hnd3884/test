package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTSoftEdgesEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTReflectionEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOuterShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTInnerShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGlowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFillOverlayEffect;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlurEffect;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTEffectListImpl extends XmlComplexContentImpl implements CTEffectList
{
    private static final long serialVersionUID = 1L;
    private static final QName BLUR$0;
    private static final QName FILLOVERLAY$2;
    private static final QName GLOW$4;
    private static final QName INNERSHDW$6;
    private static final QName OUTERSHDW$8;
    private static final QName PRSTSHDW$10;
    private static final QName REFLECTION$12;
    private static final QName SOFTEDGE$14;
    
    public CTEffectListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBlurEffect getBlur() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlurEffect ctBlurEffect = (CTBlurEffect)this.get_store().find_element_user(CTEffectListImpl.BLUR$0, 0);
            if (ctBlurEffect == null) {
                return null;
            }
            return ctBlurEffect;
        }
    }
    
    public boolean isSetBlur() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectListImpl.BLUR$0) != 0;
        }
    }
    
    public void setBlur(final CTBlurEffect ctBlurEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctBlurEffect, CTEffectListImpl.BLUR$0, 0, (short)1);
    }
    
    public CTBlurEffect addNewBlur() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlurEffect)this.get_store().add_element_user(CTEffectListImpl.BLUR$0);
        }
    }
    
    public void unsetBlur() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectListImpl.BLUR$0, 0);
        }
    }
    
    public CTFillOverlayEffect getFillOverlay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFillOverlayEffect ctFillOverlayEffect = (CTFillOverlayEffect)this.get_store().find_element_user(CTEffectListImpl.FILLOVERLAY$2, 0);
            if (ctFillOverlayEffect == null) {
                return null;
            }
            return ctFillOverlayEffect;
        }
    }
    
    public boolean isSetFillOverlay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectListImpl.FILLOVERLAY$2) != 0;
        }
    }
    
    public void setFillOverlay(final CTFillOverlayEffect ctFillOverlayEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctFillOverlayEffect, CTEffectListImpl.FILLOVERLAY$2, 0, (short)1);
    }
    
    public CTFillOverlayEffect addNewFillOverlay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFillOverlayEffect)this.get_store().add_element_user(CTEffectListImpl.FILLOVERLAY$2);
        }
    }
    
    public void unsetFillOverlay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectListImpl.FILLOVERLAY$2, 0);
        }
    }
    
    public CTGlowEffect getGlow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGlowEffect ctGlowEffect = (CTGlowEffect)this.get_store().find_element_user(CTEffectListImpl.GLOW$4, 0);
            if (ctGlowEffect == null) {
                return null;
            }
            return ctGlowEffect;
        }
    }
    
    public boolean isSetGlow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectListImpl.GLOW$4) != 0;
        }
    }
    
    public void setGlow(final CTGlowEffect ctGlowEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctGlowEffect, CTEffectListImpl.GLOW$4, 0, (short)1);
    }
    
    public CTGlowEffect addNewGlow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGlowEffect)this.get_store().add_element_user(CTEffectListImpl.GLOW$4);
        }
    }
    
    public void unsetGlow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectListImpl.GLOW$4, 0);
        }
    }
    
    public CTInnerShadowEffect getInnerShdw() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTInnerShadowEffect ctInnerShadowEffect = (CTInnerShadowEffect)this.get_store().find_element_user(CTEffectListImpl.INNERSHDW$6, 0);
            if (ctInnerShadowEffect == null) {
                return null;
            }
            return ctInnerShadowEffect;
        }
    }
    
    public boolean isSetInnerShdw() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectListImpl.INNERSHDW$6) != 0;
        }
    }
    
    public void setInnerShdw(final CTInnerShadowEffect ctInnerShadowEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctInnerShadowEffect, CTEffectListImpl.INNERSHDW$6, 0, (short)1);
    }
    
    public CTInnerShadowEffect addNewInnerShdw() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTInnerShadowEffect)this.get_store().add_element_user(CTEffectListImpl.INNERSHDW$6);
        }
    }
    
    public void unsetInnerShdw() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectListImpl.INNERSHDW$6, 0);
        }
    }
    
    public CTOuterShadowEffect getOuterShdw() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOuterShadowEffect ctOuterShadowEffect = (CTOuterShadowEffect)this.get_store().find_element_user(CTEffectListImpl.OUTERSHDW$8, 0);
            if (ctOuterShadowEffect == null) {
                return null;
            }
            return ctOuterShadowEffect;
        }
    }
    
    public boolean isSetOuterShdw() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectListImpl.OUTERSHDW$8) != 0;
        }
    }
    
    public void setOuterShdw(final CTOuterShadowEffect ctOuterShadowEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctOuterShadowEffect, CTEffectListImpl.OUTERSHDW$8, 0, (short)1);
    }
    
    public CTOuterShadowEffect addNewOuterShdw() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOuterShadowEffect)this.get_store().add_element_user(CTEffectListImpl.OUTERSHDW$8);
        }
    }
    
    public void unsetOuterShdw() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectListImpl.OUTERSHDW$8, 0);
        }
    }
    
    public CTPresetShadowEffect getPrstShdw() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPresetShadowEffect ctPresetShadowEffect = (CTPresetShadowEffect)this.get_store().find_element_user(CTEffectListImpl.PRSTSHDW$10, 0);
            if (ctPresetShadowEffect == null) {
                return null;
            }
            return ctPresetShadowEffect;
        }
    }
    
    public boolean isSetPrstShdw() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectListImpl.PRSTSHDW$10) != 0;
        }
    }
    
    public void setPrstShdw(final CTPresetShadowEffect ctPresetShadowEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctPresetShadowEffect, CTEffectListImpl.PRSTSHDW$10, 0, (short)1);
    }
    
    public CTPresetShadowEffect addNewPrstShdw() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPresetShadowEffect)this.get_store().add_element_user(CTEffectListImpl.PRSTSHDW$10);
        }
    }
    
    public void unsetPrstShdw() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectListImpl.PRSTSHDW$10, 0);
        }
    }
    
    public CTReflectionEffect getReflection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTReflectionEffect ctReflectionEffect = (CTReflectionEffect)this.get_store().find_element_user(CTEffectListImpl.REFLECTION$12, 0);
            if (ctReflectionEffect == null) {
                return null;
            }
            return ctReflectionEffect;
        }
    }
    
    public boolean isSetReflection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectListImpl.REFLECTION$12) != 0;
        }
    }
    
    public void setReflection(final CTReflectionEffect ctReflectionEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctReflectionEffect, CTEffectListImpl.REFLECTION$12, 0, (short)1);
    }
    
    public CTReflectionEffect addNewReflection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTReflectionEffect)this.get_store().add_element_user(CTEffectListImpl.REFLECTION$12);
        }
    }
    
    public void unsetReflection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectListImpl.REFLECTION$12, 0);
        }
    }
    
    public CTSoftEdgesEffect getSoftEdge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSoftEdgesEffect ctSoftEdgesEffect = (CTSoftEdgesEffect)this.get_store().find_element_user(CTEffectListImpl.SOFTEDGE$14, 0);
            if (ctSoftEdgesEffect == null) {
                return null;
            }
            return ctSoftEdgesEffect;
        }
    }
    
    public boolean isSetSoftEdge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectListImpl.SOFTEDGE$14) != 0;
        }
    }
    
    public void setSoftEdge(final CTSoftEdgesEffect ctSoftEdgesEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctSoftEdgesEffect, CTEffectListImpl.SOFTEDGE$14, 0, (short)1);
    }
    
    public CTSoftEdgesEffect addNewSoftEdge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSoftEdgesEffect)this.get_store().add_element_user(CTEffectListImpl.SOFTEDGE$14);
        }
    }
    
    public void unsetSoftEdge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectListImpl.SOFTEDGE$14, 0);
        }
    }
    
    static {
        BLUR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blur");
        FILLOVERLAY$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fillOverlay");
        GLOW$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "glow");
        INNERSHDW$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "innerShdw");
        OUTERSHDW$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "outerShdw");
        PRSTSHDW$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "prstShdw");
        REFLECTION$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "reflection");
        SOFTEDGE$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "softEdge");
    }
}
