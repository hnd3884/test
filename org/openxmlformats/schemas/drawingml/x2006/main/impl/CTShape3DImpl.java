package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.drawingml.x2006.main.STPresetMaterialType;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBevel;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShape3D;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTShape3DImpl extends XmlComplexContentImpl implements CTShape3D
{
    private static final long serialVersionUID = 1L;
    private static final QName BEVELT$0;
    private static final QName BEVELB$2;
    private static final QName EXTRUSIONCLR$4;
    private static final QName CONTOURCLR$6;
    private static final QName EXTLST$8;
    private static final QName Z$10;
    private static final QName EXTRUSIONH$12;
    private static final QName CONTOURW$14;
    private static final QName PRSTMATERIAL$16;
    
    public CTShape3DImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBevel getBevelT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBevel ctBevel = (CTBevel)this.get_store().find_element_user(CTShape3DImpl.BEVELT$0, 0);
            if (ctBevel == null) {
                return null;
            }
            return ctBevel;
        }
    }
    
    public boolean isSetBevelT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShape3DImpl.BEVELT$0) != 0;
        }
    }
    
    public void setBevelT(final CTBevel ctBevel) {
        this.generatedSetterHelperImpl((XmlObject)ctBevel, CTShape3DImpl.BEVELT$0, 0, (short)1);
    }
    
    public CTBevel addNewBevelT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBevel)this.get_store().add_element_user(CTShape3DImpl.BEVELT$0);
        }
    }
    
    public void unsetBevelT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShape3DImpl.BEVELT$0, 0);
        }
    }
    
    public CTBevel getBevelB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBevel ctBevel = (CTBevel)this.get_store().find_element_user(CTShape3DImpl.BEVELB$2, 0);
            if (ctBevel == null) {
                return null;
            }
            return ctBevel;
        }
    }
    
    public boolean isSetBevelB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShape3DImpl.BEVELB$2) != 0;
        }
    }
    
    public void setBevelB(final CTBevel ctBevel) {
        this.generatedSetterHelperImpl((XmlObject)ctBevel, CTShape3DImpl.BEVELB$2, 0, (short)1);
    }
    
    public CTBevel addNewBevelB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBevel)this.get_store().add_element_user(CTShape3DImpl.BEVELB$2);
        }
    }
    
    public void unsetBevelB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShape3DImpl.BEVELB$2, 0);
        }
    }
    
    public CTColor getExtrusionClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTShape3DImpl.EXTRUSIONCLR$4, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public boolean isSetExtrusionClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShape3DImpl.EXTRUSIONCLR$4) != 0;
        }
    }
    
    public void setExtrusionClr(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTShape3DImpl.EXTRUSIONCLR$4, 0, (short)1);
    }
    
    public CTColor addNewExtrusionClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTShape3DImpl.EXTRUSIONCLR$4);
        }
    }
    
    public void unsetExtrusionClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShape3DImpl.EXTRUSIONCLR$4, 0);
        }
    }
    
    public CTColor getContourClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTShape3DImpl.CONTOURCLR$6, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public boolean isSetContourClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShape3DImpl.CONTOURCLR$6) != 0;
        }
    }
    
    public void setContourClr(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTShape3DImpl.CONTOURCLR$6, 0, (short)1);
    }
    
    public CTColor addNewContourClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTShape3DImpl.CONTOURCLR$6);
        }
    }
    
    public void unsetContourClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShape3DImpl.CONTOURCLR$6, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTShape3DImpl.EXTLST$8, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShape3DImpl.EXTLST$8) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTShape3DImpl.EXTLST$8, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTShape3DImpl.EXTLST$8);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShape3DImpl.EXTLST$8, 0);
        }
    }
    
    public long getZ() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShape3DImpl.Z$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTShape3DImpl.Z$10);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STCoordinate xgetZ() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate stCoordinate = (STCoordinate)this.get_store().find_attribute_user(CTShape3DImpl.Z$10);
            if (stCoordinate == null) {
                stCoordinate = (STCoordinate)this.get_default_attribute_value(CTShape3DImpl.Z$10);
            }
            return stCoordinate;
        }
    }
    
    public boolean isSetZ() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShape3DImpl.Z$10) != null;
        }
    }
    
    public void setZ(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShape3DImpl.Z$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShape3DImpl.Z$10);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetZ(final STCoordinate stCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate stCoordinate2 = (STCoordinate)this.get_store().find_attribute_user(CTShape3DImpl.Z$10);
            if (stCoordinate2 == null) {
                stCoordinate2 = (STCoordinate)this.get_store().add_attribute_user(CTShape3DImpl.Z$10);
            }
            stCoordinate2.set((XmlObject)stCoordinate);
        }
    }
    
    public void unsetZ() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShape3DImpl.Z$10);
        }
    }
    
    public long getExtrusionH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShape3DImpl.EXTRUSIONH$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTShape3DImpl.EXTRUSIONH$12);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STPositiveCoordinate xgetExtrusionH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate stPositiveCoordinate = (STPositiveCoordinate)this.get_store().find_attribute_user(CTShape3DImpl.EXTRUSIONH$12);
            if (stPositiveCoordinate == null) {
                stPositiveCoordinate = (STPositiveCoordinate)this.get_default_attribute_value(CTShape3DImpl.EXTRUSIONH$12);
            }
            return stPositiveCoordinate;
        }
    }
    
    public boolean isSetExtrusionH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShape3DImpl.EXTRUSIONH$12) != null;
        }
    }
    
    public void setExtrusionH(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShape3DImpl.EXTRUSIONH$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShape3DImpl.EXTRUSIONH$12);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetExtrusionH(final STPositiveCoordinate stPositiveCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().find_attribute_user(CTShape3DImpl.EXTRUSIONH$12);
            if (stPositiveCoordinate2 == null) {
                stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().add_attribute_user(CTShape3DImpl.EXTRUSIONH$12);
            }
            stPositiveCoordinate2.set((XmlObject)stPositiveCoordinate);
        }
    }
    
    public void unsetExtrusionH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShape3DImpl.EXTRUSIONH$12);
        }
    }
    
    public long getContourW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShape3DImpl.CONTOURW$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTShape3DImpl.CONTOURW$14);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STPositiveCoordinate xgetContourW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate stPositiveCoordinate = (STPositiveCoordinate)this.get_store().find_attribute_user(CTShape3DImpl.CONTOURW$14);
            if (stPositiveCoordinate == null) {
                stPositiveCoordinate = (STPositiveCoordinate)this.get_default_attribute_value(CTShape3DImpl.CONTOURW$14);
            }
            return stPositiveCoordinate;
        }
    }
    
    public boolean isSetContourW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShape3DImpl.CONTOURW$14) != null;
        }
    }
    
    public void setContourW(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShape3DImpl.CONTOURW$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShape3DImpl.CONTOURW$14);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetContourW(final STPositiveCoordinate stPositiveCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().find_attribute_user(CTShape3DImpl.CONTOURW$14);
            if (stPositiveCoordinate2 == null) {
                stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().add_attribute_user(CTShape3DImpl.CONTOURW$14);
            }
            stPositiveCoordinate2.set((XmlObject)stPositiveCoordinate);
        }
    }
    
    public void unsetContourW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShape3DImpl.CONTOURW$14);
        }
    }
    
    public STPresetMaterialType.Enum getPrstMaterial() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShape3DImpl.PRSTMATERIAL$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTShape3DImpl.PRSTMATERIAL$16);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STPresetMaterialType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPresetMaterialType xgetPrstMaterial() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPresetMaterialType stPresetMaterialType = (STPresetMaterialType)this.get_store().find_attribute_user(CTShape3DImpl.PRSTMATERIAL$16);
            if (stPresetMaterialType == null) {
                stPresetMaterialType = (STPresetMaterialType)this.get_default_attribute_value(CTShape3DImpl.PRSTMATERIAL$16);
            }
            return stPresetMaterialType;
        }
    }
    
    public boolean isSetPrstMaterial() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShape3DImpl.PRSTMATERIAL$16) != null;
        }
    }
    
    public void setPrstMaterial(final STPresetMaterialType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShape3DImpl.PRSTMATERIAL$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShape3DImpl.PRSTMATERIAL$16);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetPrstMaterial(final STPresetMaterialType stPresetMaterialType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPresetMaterialType stPresetMaterialType2 = (STPresetMaterialType)this.get_store().find_attribute_user(CTShape3DImpl.PRSTMATERIAL$16);
            if (stPresetMaterialType2 == null) {
                stPresetMaterialType2 = (STPresetMaterialType)this.get_store().add_attribute_user(CTShape3DImpl.PRSTMATERIAL$16);
            }
            stPresetMaterialType2.set((XmlObject)stPresetMaterialType);
        }
    }
    
    public void unsetPrstMaterial() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShape3DImpl.PRSTMATERIAL$16);
        }
    }
    
    static {
        BEVELT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "bevelT");
        BEVELB$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "bevelB");
        EXTRUSIONCLR$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extrusionClr");
        CONTOURCLR$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "contourClr");
        EXTLST$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        Z$10 = new QName("", "z");
        EXTRUSIONH$12 = new QName("", "extrusionH");
        CONTOURW$14 = new QName("", "contourW");
        PRSTMATERIAL$16 = new QName("", "prstMaterial");
    }
}
