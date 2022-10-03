package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.drawingml.x2006.main.STRectAlignment;
import org.openxmlformats.schemas.drawingml.x2006.main.STFixedAngle;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveFixedAngle;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveCoordinate;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSystemColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHslColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScRgbColor;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOuterShadowEffect;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTOuterShadowEffectImpl extends XmlComplexContentImpl implements CTOuterShadowEffect
{
    private static final long serialVersionUID = 1L;
    private static final QName SCRGBCLR$0;
    private static final QName SRGBCLR$2;
    private static final QName HSLCLR$4;
    private static final QName SYSCLR$6;
    private static final QName SCHEMECLR$8;
    private static final QName PRSTCLR$10;
    private static final QName BLURRAD$12;
    private static final QName DIST$14;
    private static final QName DIR$16;
    private static final QName SX$18;
    private static final QName SY$20;
    private static final QName KX$22;
    private static final QName KY$24;
    private static final QName ALGN$26;
    private static final QName ROTWITHSHAPE$28;
    
    public CTOuterShadowEffectImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTScRgbColor getScrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTScRgbColor ctScRgbColor = (CTScRgbColor)this.get_store().find_element_user(CTOuterShadowEffectImpl.SCRGBCLR$0, 0);
            if (ctScRgbColor == null) {
                return null;
            }
            return ctScRgbColor;
        }
    }
    
    public boolean isSetScrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOuterShadowEffectImpl.SCRGBCLR$0) != 0;
        }
    }
    
    public void setScrgbClr(final CTScRgbColor ctScRgbColor) {
        this.generatedSetterHelperImpl((XmlObject)ctScRgbColor, CTOuterShadowEffectImpl.SCRGBCLR$0, 0, (short)1);
    }
    
    public CTScRgbColor addNewScrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScRgbColor)this.get_store().add_element_user(CTOuterShadowEffectImpl.SCRGBCLR$0);
        }
    }
    
    public void unsetScrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOuterShadowEffectImpl.SCRGBCLR$0, 0);
        }
    }
    
    public CTSRgbColor getSrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSRgbColor ctsRgbColor = (CTSRgbColor)this.get_store().find_element_user(CTOuterShadowEffectImpl.SRGBCLR$2, 0);
            if (ctsRgbColor == null) {
                return null;
            }
            return ctsRgbColor;
        }
    }
    
    public boolean isSetSrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOuterShadowEffectImpl.SRGBCLR$2) != 0;
        }
    }
    
    public void setSrgbClr(final CTSRgbColor ctsRgbColor) {
        this.generatedSetterHelperImpl((XmlObject)ctsRgbColor, CTOuterShadowEffectImpl.SRGBCLR$2, 0, (short)1);
    }
    
    public CTSRgbColor addNewSrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSRgbColor)this.get_store().add_element_user(CTOuterShadowEffectImpl.SRGBCLR$2);
        }
    }
    
    public void unsetSrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOuterShadowEffectImpl.SRGBCLR$2, 0);
        }
    }
    
    public CTHslColor getHslClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHslColor ctHslColor = (CTHslColor)this.get_store().find_element_user(CTOuterShadowEffectImpl.HSLCLR$4, 0);
            if (ctHslColor == null) {
                return null;
            }
            return ctHslColor;
        }
    }
    
    public boolean isSetHslClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOuterShadowEffectImpl.HSLCLR$4) != 0;
        }
    }
    
    public void setHslClr(final CTHslColor ctHslColor) {
        this.generatedSetterHelperImpl((XmlObject)ctHslColor, CTOuterShadowEffectImpl.HSLCLR$4, 0, (short)1);
    }
    
    public CTHslColor addNewHslClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHslColor)this.get_store().add_element_user(CTOuterShadowEffectImpl.HSLCLR$4);
        }
    }
    
    public void unsetHslClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOuterShadowEffectImpl.HSLCLR$4, 0);
        }
    }
    
    public CTSystemColor getSysClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSystemColor ctSystemColor = (CTSystemColor)this.get_store().find_element_user(CTOuterShadowEffectImpl.SYSCLR$6, 0);
            if (ctSystemColor == null) {
                return null;
            }
            return ctSystemColor;
        }
    }
    
    public boolean isSetSysClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOuterShadowEffectImpl.SYSCLR$6) != 0;
        }
    }
    
    public void setSysClr(final CTSystemColor ctSystemColor) {
        this.generatedSetterHelperImpl((XmlObject)ctSystemColor, CTOuterShadowEffectImpl.SYSCLR$6, 0, (short)1);
    }
    
    public CTSystemColor addNewSysClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSystemColor)this.get_store().add_element_user(CTOuterShadowEffectImpl.SYSCLR$6);
        }
    }
    
    public void unsetSysClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOuterShadowEffectImpl.SYSCLR$6, 0);
        }
    }
    
    public CTSchemeColor getSchemeClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSchemeColor ctSchemeColor = (CTSchemeColor)this.get_store().find_element_user(CTOuterShadowEffectImpl.SCHEMECLR$8, 0);
            if (ctSchemeColor == null) {
                return null;
            }
            return ctSchemeColor;
        }
    }
    
    public boolean isSetSchemeClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOuterShadowEffectImpl.SCHEMECLR$8) != 0;
        }
    }
    
    public void setSchemeClr(final CTSchemeColor ctSchemeColor) {
        this.generatedSetterHelperImpl((XmlObject)ctSchemeColor, CTOuterShadowEffectImpl.SCHEMECLR$8, 0, (short)1);
    }
    
    public CTSchemeColor addNewSchemeClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSchemeColor)this.get_store().add_element_user(CTOuterShadowEffectImpl.SCHEMECLR$8);
        }
    }
    
    public void unsetSchemeClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOuterShadowEffectImpl.SCHEMECLR$8, 0);
        }
    }
    
    public CTPresetColor getPrstClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPresetColor ctPresetColor = (CTPresetColor)this.get_store().find_element_user(CTOuterShadowEffectImpl.PRSTCLR$10, 0);
            if (ctPresetColor == null) {
                return null;
            }
            return ctPresetColor;
        }
    }
    
    public boolean isSetPrstClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOuterShadowEffectImpl.PRSTCLR$10) != 0;
        }
    }
    
    public void setPrstClr(final CTPresetColor ctPresetColor) {
        this.generatedSetterHelperImpl((XmlObject)ctPresetColor, CTOuterShadowEffectImpl.PRSTCLR$10, 0, (short)1);
    }
    
    public CTPresetColor addNewPrstClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPresetColor)this.get_store().add_element_user(CTOuterShadowEffectImpl.PRSTCLR$10);
        }
    }
    
    public void unsetPrstClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOuterShadowEffectImpl.PRSTCLR$10, 0);
        }
    }
    
    public long getBlurRad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.BLURRAD$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOuterShadowEffectImpl.BLURRAD$12);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STPositiveCoordinate xgetBlurRad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate stPositiveCoordinate = (STPositiveCoordinate)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.BLURRAD$12);
            if (stPositiveCoordinate == null) {
                stPositiveCoordinate = (STPositiveCoordinate)this.get_default_attribute_value(CTOuterShadowEffectImpl.BLURRAD$12);
            }
            return stPositiveCoordinate;
        }
    }
    
    public boolean isSetBlurRad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOuterShadowEffectImpl.BLURRAD$12) != null;
        }
    }
    
    public void setBlurRad(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.BLURRAD$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.BLURRAD$12);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetBlurRad(final STPositiveCoordinate stPositiveCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.BLURRAD$12);
            if (stPositiveCoordinate2 == null) {
                stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.BLURRAD$12);
            }
            stPositiveCoordinate2.set((XmlObject)stPositiveCoordinate);
        }
    }
    
    public void unsetBlurRad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOuterShadowEffectImpl.BLURRAD$12);
        }
    }
    
    public long getDist() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.DIST$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOuterShadowEffectImpl.DIST$14);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STPositiveCoordinate xgetDist() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate stPositiveCoordinate = (STPositiveCoordinate)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.DIST$14);
            if (stPositiveCoordinate == null) {
                stPositiveCoordinate = (STPositiveCoordinate)this.get_default_attribute_value(CTOuterShadowEffectImpl.DIST$14);
            }
            return stPositiveCoordinate;
        }
    }
    
    public boolean isSetDist() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOuterShadowEffectImpl.DIST$14) != null;
        }
    }
    
    public void setDist(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.DIST$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.DIST$14);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDist(final STPositiveCoordinate stPositiveCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.DIST$14);
            if (stPositiveCoordinate2 == null) {
                stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.DIST$14);
            }
            stPositiveCoordinate2.set((XmlObject)stPositiveCoordinate);
        }
    }
    
    public void unsetDist() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOuterShadowEffectImpl.DIST$14);
        }
    }
    
    public int getDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.DIR$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOuterShadowEffectImpl.DIR$16);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPositiveFixedAngle xgetDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveFixedAngle stPositiveFixedAngle = (STPositiveFixedAngle)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.DIR$16);
            if (stPositiveFixedAngle == null) {
                stPositiveFixedAngle = (STPositiveFixedAngle)this.get_default_attribute_value(CTOuterShadowEffectImpl.DIR$16);
            }
            return stPositiveFixedAngle;
        }
    }
    
    public boolean isSetDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOuterShadowEffectImpl.DIR$16) != null;
        }
    }
    
    public void setDir(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.DIR$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.DIR$16);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetDir(final STPositiveFixedAngle stPositiveFixedAngle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveFixedAngle stPositiveFixedAngle2 = (STPositiveFixedAngle)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.DIR$16);
            if (stPositiveFixedAngle2 == null) {
                stPositiveFixedAngle2 = (STPositiveFixedAngle)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.DIR$16);
            }
            stPositiveFixedAngle2.set((XmlObject)stPositiveFixedAngle);
        }
    }
    
    public void unsetDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOuterShadowEffectImpl.DIR$16);
        }
    }
    
    public int getSx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.SX$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOuterShadowEffectImpl.SX$18);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPercentage xgetSx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage = (STPercentage)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.SX$18);
            if (stPercentage == null) {
                stPercentage = (STPercentage)this.get_default_attribute_value(CTOuterShadowEffectImpl.SX$18);
            }
            return stPercentage;
        }
    }
    
    public boolean isSetSx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOuterShadowEffectImpl.SX$18) != null;
        }
    }
    
    public void setSx(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.SX$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.SX$18);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetSx(final STPercentage stPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage2 = (STPercentage)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.SX$18);
            if (stPercentage2 == null) {
                stPercentage2 = (STPercentage)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.SX$18);
            }
            stPercentage2.set((XmlObject)stPercentage);
        }
    }
    
    public void unsetSx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOuterShadowEffectImpl.SX$18);
        }
    }
    
    public int getSy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.SY$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOuterShadowEffectImpl.SY$20);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPercentage xgetSy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage = (STPercentage)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.SY$20);
            if (stPercentage == null) {
                stPercentage = (STPercentage)this.get_default_attribute_value(CTOuterShadowEffectImpl.SY$20);
            }
            return stPercentage;
        }
    }
    
    public boolean isSetSy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOuterShadowEffectImpl.SY$20) != null;
        }
    }
    
    public void setSy(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.SY$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.SY$20);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetSy(final STPercentage stPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage2 = (STPercentage)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.SY$20);
            if (stPercentage2 == null) {
                stPercentage2 = (STPercentage)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.SY$20);
            }
            stPercentage2.set((XmlObject)stPercentage);
        }
    }
    
    public void unsetSy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOuterShadowEffectImpl.SY$20);
        }
    }
    
    public int getKx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.KX$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOuterShadowEffectImpl.KX$22);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STFixedAngle xgetKx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFixedAngle stFixedAngle = (STFixedAngle)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.KX$22);
            if (stFixedAngle == null) {
                stFixedAngle = (STFixedAngle)this.get_default_attribute_value(CTOuterShadowEffectImpl.KX$22);
            }
            return stFixedAngle;
        }
    }
    
    public boolean isSetKx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOuterShadowEffectImpl.KX$22) != null;
        }
    }
    
    public void setKx(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.KX$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.KX$22);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetKx(final STFixedAngle stFixedAngle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFixedAngle stFixedAngle2 = (STFixedAngle)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.KX$22);
            if (stFixedAngle2 == null) {
                stFixedAngle2 = (STFixedAngle)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.KX$22);
            }
            stFixedAngle2.set((XmlObject)stFixedAngle);
        }
    }
    
    public void unsetKx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOuterShadowEffectImpl.KX$22);
        }
    }
    
    public int getKy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.KY$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOuterShadowEffectImpl.KY$24);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STFixedAngle xgetKy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFixedAngle stFixedAngle = (STFixedAngle)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.KY$24);
            if (stFixedAngle == null) {
                stFixedAngle = (STFixedAngle)this.get_default_attribute_value(CTOuterShadowEffectImpl.KY$24);
            }
            return stFixedAngle;
        }
    }
    
    public boolean isSetKy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOuterShadowEffectImpl.KY$24) != null;
        }
    }
    
    public void setKy(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.KY$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.KY$24);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetKy(final STFixedAngle stFixedAngle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFixedAngle stFixedAngle2 = (STFixedAngle)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.KY$24);
            if (stFixedAngle2 == null) {
                stFixedAngle2 = (STFixedAngle)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.KY$24);
            }
            stFixedAngle2.set((XmlObject)stFixedAngle);
        }
    }
    
    public void unsetKy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOuterShadowEffectImpl.KY$24);
        }
    }
    
    public STRectAlignment.Enum getAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.ALGN$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOuterShadowEffectImpl.ALGN$26);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STRectAlignment.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STRectAlignment xgetAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRectAlignment stRectAlignment = (STRectAlignment)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.ALGN$26);
            if (stRectAlignment == null) {
                stRectAlignment = (STRectAlignment)this.get_default_attribute_value(CTOuterShadowEffectImpl.ALGN$26);
            }
            return stRectAlignment;
        }
    }
    
    public boolean isSetAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOuterShadowEffectImpl.ALGN$26) != null;
        }
    }
    
    public void setAlgn(final STRectAlignment.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.ALGN$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.ALGN$26);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAlgn(final STRectAlignment stRectAlignment) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRectAlignment stRectAlignment2 = (STRectAlignment)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.ALGN$26);
            if (stRectAlignment2 == null) {
                stRectAlignment2 = (STRectAlignment)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.ALGN$26);
            }
            stRectAlignment2.set((XmlObject)stRectAlignment);
        }
    }
    
    public void unsetAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOuterShadowEffectImpl.ALGN$26);
        }
    }
    
    public boolean getRotWithShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.ROTWITHSHAPE$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOuterShadowEffectImpl.ROTWITHSHAPE$28);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetRotWithShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.ROTWITHSHAPE$28);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTOuterShadowEffectImpl.ROTWITHSHAPE$28);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetRotWithShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOuterShadowEffectImpl.ROTWITHSHAPE$28) != null;
        }
    }
    
    public void setRotWithShape(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.ROTWITHSHAPE$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.ROTWITHSHAPE$28);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetRotWithShape(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTOuterShadowEffectImpl.ROTWITHSHAPE$28);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTOuterShadowEffectImpl.ROTWITHSHAPE$28);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetRotWithShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOuterShadowEffectImpl.ROTWITHSHAPE$28);
        }
    }
    
    static {
        SCRGBCLR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "scrgbClr");
        SRGBCLR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "srgbClr");
        HSLCLR$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hslClr");
        SYSCLR$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "sysClr");
        SCHEMECLR$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "schemeClr");
        PRSTCLR$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "prstClr");
        BLURRAD$12 = new QName("", "blurRad");
        DIST$14 = new QName("", "dist");
        DIR$16 = new QName("", "dir");
        SX$18 = new QName("", "sx");
        SY$20 = new QName("", "sy");
        KX$22 = new QName("", "kx");
        KY$24 = new QName("", "ky");
        ALGN$26 = new QName("", "algn");
        ROTWITHSHAPE$28 = new QName("", "rotWithShape");
    }
}
