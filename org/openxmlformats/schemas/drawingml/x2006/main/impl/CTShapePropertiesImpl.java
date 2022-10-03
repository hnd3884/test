package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.STBlackWhiteMode;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShape3D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScene3D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectContainer;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCustomGeometry2D;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTShapePropertiesImpl extends XmlComplexContentImpl implements CTShapeProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName XFRM$0;
    private static final QName CUSTGEOM$2;
    private static final QName PRSTGEOM$4;
    private static final QName NOFILL$6;
    private static final QName SOLIDFILL$8;
    private static final QName GRADFILL$10;
    private static final QName BLIPFILL$12;
    private static final QName PATTFILL$14;
    private static final QName GRPFILL$16;
    private static final QName LN$18;
    private static final QName EFFECTLST$20;
    private static final QName EFFECTDAG$22;
    private static final QName SCENE3D$24;
    private static final QName SP3D$26;
    private static final QName EXTLST$28;
    private static final QName BWMODE$30;
    
    public CTShapePropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTransform2D getXfrm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTransform2D ctTransform2D = (CTTransform2D)this.get_store().find_element_user(CTShapePropertiesImpl.XFRM$0, 0);
            if (ctTransform2D == null) {
                return null;
            }
            return ctTransform2D;
        }
    }
    
    public boolean isSetXfrm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapePropertiesImpl.XFRM$0) != 0;
        }
    }
    
    public void setXfrm(final CTTransform2D ctTransform2D) {
        this.generatedSetterHelperImpl((XmlObject)ctTransform2D, CTShapePropertiesImpl.XFRM$0, 0, (short)1);
    }
    
    public CTTransform2D addNewXfrm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTransform2D)this.get_store().add_element_user(CTShapePropertiesImpl.XFRM$0);
        }
    }
    
    public void unsetXfrm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapePropertiesImpl.XFRM$0, 0);
        }
    }
    
    public CTCustomGeometry2D getCustGeom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomGeometry2D ctCustomGeometry2D = (CTCustomGeometry2D)this.get_store().find_element_user(CTShapePropertiesImpl.CUSTGEOM$2, 0);
            if (ctCustomGeometry2D == null) {
                return null;
            }
            return ctCustomGeometry2D;
        }
    }
    
    public boolean isSetCustGeom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapePropertiesImpl.CUSTGEOM$2) != 0;
        }
    }
    
    public void setCustGeom(final CTCustomGeometry2D ctCustomGeometry2D) {
        this.generatedSetterHelperImpl((XmlObject)ctCustomGeometry2D, CTShapePropertiesImpl.CUSTGEOM$2, 0, (short)1);
    }
    
    public CTCustomGeometry2D addNewCustGeom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomGeometry2D)this.get_store().add_element_user(CTShapePropertiesImpl.CUSTGEOM$2);
        }
    }
    
    public void unsetCustGeom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapePropertiesImpl.CUSTGEOM$2, 0);
        }
    }
    
    public CTPresetGeometry2D getPrstGeom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPresetGeometry2D ctPresetGeometry2D = (CTPresetGeometry2D)this.get_store().find_element_user(CTShapePropertiesImpl.PRSTGEOM$4, 0);
            if (ctPresetGeometry2D == null) {
                return null;
            }
            return ctPresetGeometry2D;
        }
    }
    
    public boolean isSetPrstGeom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapePropertiesImpl.PRSTGEOM$4) != 0;
        }
    }
    
    public void setPrstGeom(final CTPresetGeometry2D ctPresetGeometry2D) {
        this.generatedSetterHelperImpl((XmlObject)ctPresetGeometry2D, CTShapePropertiesImpl.PRSTGEOM$4, 0, (short)1);
    }
    
    public CTPresetGeometry2D addNewPrstGeom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPresetGeometry2D)this.get_store().add_element_user(CTShapePropertiesImpl.PRSTGEOM$4);
        }
    }
    
    public void unsetPrstGeom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapePropertiesImpl.PRSTGEOM$4, 0);
        }
    }
    
    public CTNoFillProperties getNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNoFillProperties ctNoFillProperties = (CTNoFillProperties)this.get_store().find_element_user(CTShapePropertiesImpl.NOFILL$6, 0);
            if (ctNoFillProperties == null) {
                return null;
            }
            return ctNoFillProperties;
        }
    }
    
    public boolean isSetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapePropertiesImpl.NOFILL$6) != 0;
        }
    }
    
    public void setNoFill(final CTNoFillProperties ctNoFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctNoFillProperties, CTShapePropertiesImpl.NOFILL$6, 0, (short)1);
    }
    
    public CTNoFillProperties addNewNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNoFillProperties)this.get_store().add_element_user(CTShapePropertiesImpl.NOFILL$6);
        }
    }
    
    public void unsetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapePropertiesImpl.NOFILL$6, 0);
        }
    }
    
    public CTSolidColorFillProperties getSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSolidColorFillProperties ctSolidColorFillProperties = (CTSolidColorFillProperties)this.get_store().find_element_user(CTShapePropertiesImpl.SOLIDFILL$8, 0);
            if (ctSolidColorFillProperties == null) {
                return null;
            }
            return ctSolidColorFillProperties;
        }
    }
    
    public boolean isSetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapePropertiesImpl.SOLIDFILL$8) != 0;
        }
    }
    
    public void setSolidFill(final CTSolidColorFillProperties ctSolidColorFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctSolidColorFillProperties, CTShapePropertiesImpl.SOLIDFILL$8, 0, (short)1);
    }
    
    public CTSolidColorFillProperties addNewSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSolidColorFillProperties)this.get_store().add_element_user(CTShapePropertiesImpl.SOLIDFILL$8);
        }
    }
    
    public void unsetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapePropertiesImpl.SOLIDFILL$8, 0);
        }
    }
    
    public CTGradientFillProperties getGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGradientFillProperties ctGradientFillProperties = (CTGradientFillProperties)this.get_store().find_element_user(CTShapePropertiesImpl.GRADFILL$10, 0);
            if (ctGradientFillProperties == null) {
                return null;
            }
            return ctGradientFillProperties;
        }
    }
    
    public boolean isSetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapePropertiesImpl.GRADFILL$10) != 0;
        }
    }
    
    public void setGradFill(final CTGradientFillProperties ctGradientFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGradientFillProperties, CTShapePropertiesImpl.GRADFILL$10, 0, (short)1);
    }
    
    public CTGradientFillProperties addNewGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGradientFillProperties)this.get_store().add_element_user(CTShapePropertiesImpl.GRADFILL$10);
        }
    }
    
    public void unsetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapePropertiesImpl.GRADFILL$10, 0);
        }
    }
    
    public CTBlipFillProperties getBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlipFillProperties ctBlipFillProperties = (CTBlipFillProperties)this.get_store().find_element_user(CTShapePropertiesImpl.BLIPFILL$12, 0);
            if (ctBlipFillProperties == null) {
                return null;
            }
            return ctBlipFillProperties;
        }
    }
    
    public boolean isSetBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapePropertiesImpl.BLIPFILL$12) != 0;
        }
    }
    
    public void setBlipFill(final CTBlipFillProperties ctBlipFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctBlipFillProperties, CTShapePropertiesImpl.BLIPFILL$12, 0, (short)1);
    }
    
    public CTBlipFillProperties addNewBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlipFillProperties)this.get_store().add_element_user(CTShapePropertiesImpl.BLIPFILL$12);
        }
    }
    
    public void unsetBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapePropertiesImpl.BLIPFILL$12, 0);
        }
    }
    
    public CTPatternFillProperties getPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPatternFillProperties ctPatternFillProperties = (CTPatternFillProperties)this.get_store().find_element_user(CTShapePropertiesImpl.PATTFILL$14, 0);
            if (ctPatternFillProperties == null) {
                return null;
            }
            return ctPatternFillProperties;
        }
    }
    
    public boolean isSetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapePropertiesImpl.PATTFILL$14) != 0;
        }
    }
    
    public void setPattFill(final CTPatternFillProperties ctPatternFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctPatternFillProperties, CTShapePropertiesImpl.PATTFILL$14, 0, (short)1);
    }
    
    public CTPatternFillProperties addNewPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPatternFillProperties)this.get_store().add_element_user(CTShapePropertiesImpl.PATTFILL$14);
        }
    }
    
    public void unsetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapePropertiesImpl.PATTFILL$14, 0);
        }
    }
    
    public CTGroupFillProperties getGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupFillProperties ctGroupFillProperties = (CTGroupFillProperties)this.get_store().find_element_user(CTShapePropertiesImpl.GRPFILL$16, 0);
            if (ctGroupFillProperties == null) {
                return null;
            }
            return ctGroupFillProperties;
        }
    }
    
    public boolean isSetGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapePropertiesImpl.GRPFILL$16) != 0;
        }
    }
    
    public void setGrpFill(final CTGroupFillProperties ctGroupFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupFillProperties, CTShapePropertiesImpl.GRPFILL$16, 0, (short)1);
    }
    
    public CTGroupFillProperties addNewGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupFillProperties)this.get_store().add_element_user(CTShapePropertiesImpl.GRPFILL$16);
        }
    }
    
    public void unsetGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapePropertiesImpl.GRPFILL$16, 0);
        }
    }
    
    public CTLineProperties getLn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineProperties ctLineProperties = (CTLineProperties)this.get_store().find_element_user(CTShapePropertiesImpl.LN$18, 0);
            if (ctLineProperties == null) {
                return null;
            }
            return ctLineProperties;
        }
    }
    
    public boolean isSetLn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapePropertiesImpl.LN$18) != 0;
        }
    }
    
    public void setLn(final CTLineProperties ctLineProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctLineProperties, CTShapePropertiesImpl.LN$18, 0, (short)1);
    }
    
    public CTLineProperties addNewLn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineProperties)this.get_store().add_element_user(CTShapePropertiesImpl.LN$18);
        }
    }
    
    public void unsetLn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapePropertiesImpl.LN$18, 0);
        }
    }
    
    public CTEffectList getEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectList list = (CTEffectList)this.get_store().find_element_user(CTShapePropertiesImpl.EFFECTLST$20, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapePropertiesImpl.EFFECTLST$20) != 0;
        }
    }
    
    public void setEffectLst(final CTEffectList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTShapePropertiesImpl.EFFECTLST$20, 0, (short)1);
    }
    
    public CTEffectList addNewEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectList)this.get_store().add_element_user(CTShapePropertiesImpl.EFFECTLST$20);
        }
    }
    
    public void unsetEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapePropertiesImpl.EFFECTLST$20, 0);
        }
    }
    
    public CTEffectContainer getEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectContainer ctEffectContainer = (CTEffectContainer)this.get_store().find_element_user(CTShapePropertiesImpl.EFFECTDAG$22, 0);
            if (ctEffectContainer == null) {
                return null;
            }
            return ctEffectContainer;
        }
    }
    
    public boolean isSetEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapePropertiesImpl.EFFECTDAG$22) != 0;
        }
    }
    
    public void setEffectDag(final CTEffectContainer ctEffectContainer) {
        this.generatedSetterHelperImpl((XmlObject)ctEffectContainer, CTShapePropertiesImpl.EFFECTDAG$22, 0, (short)1);
    }
    
    public CTEffectContainer addNewEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectContainer)this.get_store().add_element_user(CTShapePropertiesImpl.EFFECTDAG$22);
        }
    }
    
    public void unsetEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapePropertiesImpl.EFFECTDAG$22, 0);
        }
    }
    
    public CTScene3D getScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTScene3D ctScene3D = (CTScene3D)this.get_store().find_element_user(CTShapePropertiesImpl.SCENE3D$24, 0);
            if (ctScene3D == null) {
                return null;
            }
            return ctScene3D;
        }
    }
    
    public boolean isSetScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapePropertiesImpl.SCENE3D$24) != 0;
        }
    }
    
    public void setScene3D(final CTScene3D ctScene3D) {
        this.generatedSetterHelperImpl((XmlObject)ctScene3D, CTShapePropertiesImpl.SCENE3D$24, 0, (short)1);
    }
    
    public CTScene3D addNewScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScene3D)this.get_store().add_element_user(CTShapePropertiesImpl.SCENE3D$24);
        }
    }
    
    public void unsetScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapePropertiesImpl.SCENE3D$24, 0);
        }
    }
    
    public CTShape3D getSp3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShape3D ctShape3D = (CTShape3D)this.get_store().find_element_user(CTShapePropertiesImpl.SP3D$26, 0);
            if (ctShape3D == null) {
                return null;
            }
            return ctShape3D;
        }
    }
    
    public boolean isSetSp3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapePropertiesImpl.SP3D$26) != 0;
        }
    }
    
    public void setSp3D(final CTShape3D ctShape3D) {
        this.generatedSetterHelperImpl((XmlObject)ctShape3D, CTShapePropertiesImpl.SP3D$26, 0, (short)1);
    }
    
    public CTShape3D addNewSp3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShape3D)this.get_store().add_element_user(CTShapePropertiesImpl.SP3D$26);
        }
    }
    
    public void unsetSp3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapePropertiesImpl.SP3D$26, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTShapePropertiesImpl.EXTLST$28, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapePropertiesImpl.EXTLST$28) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTShapePropertiesImpl.EXTLST$28, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTShapePropertiesImpl.EXTLST$28);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapePropertiesImpl.EXTLST$28, 0);
        }
    }
    
    public STBlackWhiteMode.Enum getBwMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapePropertiesImpl.BWMODE$30);
            if (simpleValue == null) {
                return null;
            }
            return (STBlackWhiteMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBlackWhiteMode xgetBwMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STBlackWhiteMode)this.get_store().find_attribute_user(CTShapePropertiesImpl.BWMODE$30);
        }
    }
    
    public boolean isSetBwMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapePropertiesImpl.BWMODE$30) != null;
        }
    }
    
    public void setBwMode(final STBlackWhiteMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapePropertiesImpl.BWMODE$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapePropertiesImpl.BWMODE$30);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBwMode(final STBlackWhiteMode stBlackWhiteMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBlackWhiteMode stBlackWhiteMode2 = (STBlackWhiteMode)this.get_store().find_attribute_user(CTShapePropertiesImpl.BWMODE$30);
            if (stBlackWhiteMode2 == null) {
                stBlackWhiteMode2 = (STBlackWhiteMode)this.get_store().add_attribute_user(CTShapePropertiesImpl.BWMODE$30);
            }
            stBlackWhiteMode2.set((XmlObject)stBlackWhiteMode);
        }
    }
    
    public void unsetBwMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapePropertiesImpl.BWMODE$30);
        }
    }
    
    static {
        XFRM$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "xfrm");
        CUSTGEOM$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "custGeom");
        PRSTGEOM$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "prstGeom");
        NOFILL$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "noFill");
        SOLIDFILL$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "solidFill");
        GRADFILL$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gradFill");
        BLIPFILL$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blipFill");
        PATTFILL$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pattFill");
        GRPFILL$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "grpFill");
        LN$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ln");
        EFFECTLST$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectLst");
        EFFECTDAG$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectDag");
        SCENE3D$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "scene3d");
        SP3D$26 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "sp3d");
        EXTLST$28 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        BWMODE$30 = new QName("", "bwMode");
    }
}
