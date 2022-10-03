package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.STBlackWhiteMode;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScene3D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectContainer;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupTransform2D;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGroupShapePropertiesImpl extends XmlComplexContentImpl implements CTGroupShapeProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName XFRM$0;
    private static final QName NOFILL$2;
    private static final QName SOLIDFILL$4;
    private static final QName GRADFILL$6;
    private static final QName BLIPFILL$8;
    private static final QName PATTFILL$10;
    private static final QName GRPFILL$12;
    private static final QName EFFECTLST$14;
    private static final QName EFFECTDAG$16;
    private static final QName SCENE3D$18;
    private static final QName EXTLST$20;
    private static final QName BWMODE$22;
    
    public CTGroupShapePropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTGroupTransform2D getXfrm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupTransform2D ctGroupTransform2D = (CTGroupTransform2D)this.get_store().find_element_user(CTGroupShapePropertiesImpl.XFRM$0, 0);
            if (ctGroupTransform2D == null) {
                return null;
            }
            return ctGroupTransform2D;
        }
    }
    
    public boolean isSetXfrm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapePropertiesImpl.XFRM$0) != 0;
        }
    }
    
    public void setXfrm(final CTGroupTransform2D ctGroupTransform2D) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupTransform2D, CTGroupShapePropertiesImpl.XFRM$0, 0, (short)1);
    }
    
    public CTGroupTransform2D addNewXfrm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupTransform2D)this.get_store().add_element_user(CTGroupShapePropertiesImpl.XFRM$0);
        }
    }
    
    public void unsetXfrm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapePropertiesImpl.XFRM$0, 0);
        }
    }
    
    public CTNoFillProperties getNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNoFillProperties ctNoFillProperties = (CTNoFillProperties)this.get_store().find_element_user(CTGroupShapePropertiesImpl.NOFILL$2, 0);
            if (ctNoFillProperties == null) {
                return null;
            }
            return ctNoFillProperties;
        }
    }
    
    public boolean isSetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapePropertiesImpl.NOFILL$2) != 0;
        }
    }
    
    public void setNoFill(final CTNoFillProperties ctNoFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctNoFillProperties, CTGroupShapePropertiesImpl.NOFILL$2, 0, (short)1);
    }
    
    public CTNoFillProperties addNewNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNoFillProperties)this.get_store().add_element_user(CTGroupShapePropertiesImpl.NOFILL$2);
        }
    }
    
    public void unsetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapePropertiesImpl.NOFILL$2, 0);
        }
    }
    
    public CTSolidColorFillProperties getSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSolidColorFillProperties ctSolidColorFillProperties = (CTSolidColorFillProperties)this.get_store().find_element_user(CTGroupShapePropertiesImpl.SOLIDFILL$4, 0);
            if (ctSolidColorFillProperties == null) {
                return null;
            }
            return ctSolidColorFillProperties;
        }
    }
    
    public boolean isSetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapePropertiesImpl.SOLIDFILL$4) != 0;
        }
    }
    
    public void setSolidFill(final CTSolidColorFillProperties ctSolidColorFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctSolidColorFillProperties, CTGroupShapePropertiesImpl.SOLIDFILL$4, 0, (short)1);
    }
    
    public CTSolidColorFillProperties addNewSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSolidColorFillProperties)this.get_store().add_element_user(CTGroupShapePropertiesImpl.SOLIDFILL$4);
        }
    }
    
    public void unsetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapePropertiesImpl.SOLIDFILL$4, 0);
        }
    }
    
    public CTGradientFillProperties getGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGradientFillProperties ctGradientFillProperties = (CTGradientFillProperties)this.get_store().find_element_user(CTGroupShapePropertiesImpl.GRADFILL$6, 0);
            if (ctGradientFillProperties == null) {
                return null;
            }
            return ctGradientFillProperties;
        }
    }
    
    public boolean isSetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapePropertiesImpl.GRADFILL$6) != 0;
        }
    }
    
    public void setGradFill(final CTGradientFillProperties ctGradientFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGradientFillProperties, CTGroupShapePropertiesImpl.GRADFILL$6, 0, (short)1);
    }
    
    public CTGradientFillProperties addNewGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGradientFillProperties)this.get_store().add_element_user(CTGroupShapePropertiesImpl.GRADFILL$6);
        }
    }
    
    public void unsetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapePropertiesImpl.GRADFILL$6, 0);
        }
    }
    
    public CTBlipFillProperties getBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlipFillProperties ctBlipFillProperties = (CTBlipFillProperties)this.get_store().find_element_user(CTGroupShapePropertiesImpl.BLIPFILL$8, 0);
            if (ctBlipFillProperties == null) {
                return null;
            }
            return ctBlipFillProperties;
        }
    }
    
    public boolean isSetBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapePropertiesImpl.BLIPFILL$8) != 0;
        }
    }
    
    public void setBlipFill(final CTBlipFillProperties ctBlipFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctBlipFillProperties, CTGroupShapePropertiesImpl.BLIPFILL$8, 0, (short)1);
    }
    
    public CTBlipFillProperties addNewBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlipFillProperties)this.get_store().add_element_user(CTGroupShapePropertiesImpl.BLIPFILL$8);
        }
    }
    
    public void unsetBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapePropertiesImpl.BLIPFILL$8, 0);
        }
    }
    
    public CTPatternFillProperties getPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPatternFillProperties ctPatternFillProperties = (CTPatternFillProperties)this.get_store().find_element_user(CTGroupShapePropertiesImpl.PATTFILL$10, 0);
            if (ctPatternFillProperties == null) {
                return null;
            }
            return ctPatternFillProperties;
        }
    }
    
    public boolean isSetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapePropertiesImpl.PATTFILL$10) != 0;
        }
    }
    
    public void setPattFill(final CTPatternFillProperties ctPatternFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctPatternFillProperties, CTGroupShapePropertiesImpl.PATTFILL$10, 0, (short)1);
    }
    
    public CTPatternFillProperties addNewPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPatternFillProperties)this.get_store().add_element_user(CTGroupShapePropertiesImpl.PATTFILL$10);
        }
    }
    
    public void unsetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapePropertiesImpl.PATTFILL$10, 0);
        }
    }
    
    public CTGroupFillProperties getGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupFillProperties ctGroupFillProperties = (CTGroupFillProperties)this.get_store().find_element_user(CTGroupShapePropertiesImpl.GRPFILL$12, 0);
            if (ctGroupFillProperties == null) {
                return null;
            }
            return ctGroupFillProperties;
        }
    }
    
    public boolean isSetGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapePropertiesImpl.GRPFILL$12) != 0;
        }
    }
    
    public void setGrpFill(final CTGroupFillProperties ctGroupFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupFillProperties, CTGroupShapePropertiesImpl.GRPFILL$12, 0, (short)1);
    }
    
    public CTGroupFillProperties addNewGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupFillProperties)this.get_store().add_element_user(CTGroupShapePropertiesImpl.GRPFILL$12);
        }
    }
    
    public void unsetGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapePropertiesImpl.GRPFILL$12, 0);
        }
    }
    
    public CTEffectList getEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectList list = (CTEffectList)this.get_store().find_element_user(CTGroupShapePropertiesImpl.EFFECTLST$14, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapePropertiesImpl.EFFECTLST$14) != 0;
        }
    }
    
    public void setEffectLst(final CTEffectList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTGroupShapePropertiesImpl.EFFECTLST$14, 0, (short)1);
    }
    
    public CTEffectList addNewEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectList)this.get_store().add_element_user(CTGroupShapePropertiesImpl.EFFECTLST$14);
        }
    }
    
    public void unsetEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapePropertiesImpl.EFFECTLST$14, 0);
        }
    }
    
    public CTEffectContainer getEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectContainer ctEffectContainer = (CTEffectContainer)this.get_store().find_element_user(CTGroupShapePropertiesImpl.EFFECTDAG$16, 0);
            if (ctEffectContainer == null) {
                return null;
            }
            return ctEffectContainer;
        }
    }
    
    public boolean isSetEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapePropertiesImpl.EFFECTDAG$16) != 0;
        }
    }
    
    public void setEffectDag(final CTEffectContainer ctEffectContainer) {
        this.generatedSetterHelperImpl((XmlObject)ctEffectContainer, CTGroupShapePropertiesImpl.EFFECTDAG$16, 0, (short)1);
    }
    
    public CTEffectContainer addNewEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectContainer)this.get_store().add_element_user(CTGroupShapePropertiesImpl.EFFECTDAG$16);
        }
    }
    
    public void unsetEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapePropertiesImpl.EFFECTDAG$16, 0);
        }
    }
    
    public CTScene3D getScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTScene3D ctScene3D = (CTScene3D)this.get_store().find_element_user(CTGroupShapePropertiesImpl.SCENE3D$18, 0);
            if (ctScene3D == null) {
                return null;
            }
            return ctScene3D;
        }
    }
    
    public boolean isSetScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapePropertiesImpl.SCENE3D$18) != 0;
        }
    }
    
    public void setScene3D(final CTScene3D ctScene3D) {
        this.generatedSetterHelperImpl((XmlObject)ctScene3D, CTGroupShapePropertiesImpl.SCENE3D$18, 0, (short)1);
    }
    
    public CTScene3D addNewScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScene3D)this.get_store().add_element_user(CTGroupShapePropertiesImpl.SCENE3D$18);
        }
    }
    
    public void unsetScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapePropertiesImpl.SCENE3D$18, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTGroupShapePropertiesImpl.EXTLST$20, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapePropertiesImpl.EXTLST$20) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTGroupShapePropertiesImpl.EXTLST$20, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTGroupShapePropertiesImpl.EXTLST$20);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapePropertiesImpl.EXTLST$20, 0);
        }
    }
    
    public STBlackWhiteMode.Enum getBwMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupShapePropertiesImpl.BWMODE$22);
            if (simpleValue == null) {
                return null;
            }
            return (STBlackWhiteMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBlackWhiteMode xgetBwMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STBlackWhiteMode)this.get_store().find_attribute_user(CTGroupShapePropertiesImpl.BWMODE$22);
        }
    }
    
    public boolean isSetBwMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupShapePropertiesImpl.BWMODE$22) != null;
        }
    }
    
    public void setBwMode(final STBlackWhiteMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupShapePropertiesImpl.BWMODE$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupShapePropertiesImpl.BWMODE$22);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBwMode(final STBlackWhiteMode stBlackWhiteMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBlackWhiteMode stBlackWhiteMode2 = (STBlackWhiteMode)this.get_store().find_attribute_user(CTGroupShapePropertiesImpl.BWMODE$22);
            if (stBlackWhiteMode2 == null) {
                stBlackWhiteMode2 = (STBlackWhiteMode)this.get_store().add_attribute_user(CTGroupShapePropertiesImpl.BWMODE$22);
            }
            stBlackWhiteMode2.set((XmlObject)stBlackWhiteMode);
        }
    }
    
    public void unsetBwMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupShapePropertiesImpl.BWMODE$22);
        }
    }
    
    static {
        XFRM$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "xfrm");
        NOFILL$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "noFill");
        SOLIDFILL$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "solidFill");
        GRADFILL$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gradFill");
        BLIPFILL$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blipFill");
        PATTFILL$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pattFill");
        GRPFILL$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "grpFill");
        EFFECTLST$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectLst");
        EFFECTDAG$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectDag");
        SCENE3D$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "scene3d");
        EXTLST$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        BWMODE$22 = new QName("", "bwMode");
    }
}
