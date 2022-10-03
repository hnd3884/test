package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STTextHorzOverflowType;
import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAnchoringType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextVerticalType;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate32;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCell3D;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCellProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableCellPropertiesImpl extends XmlComplexContentImpl implements CTTableCellProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName LNL$0;
    private static final QName LNR$2;
    private static final QName LNT$4;
    private static final QName LNB$6;
    private static final QName LNTLTOBR$8;
    private static final QName LNBLTOTR$10;
    private static final QName CELL3D$12;
    private static final QName NOFILL$14;
    private static final QName SOLIDFILL$16;
    private static final QName GRADFILL$18;
    private static final QName BLIPFILL$20;
    private static final QName PATTFILL$22;
    private static final QName GRPFILL$24;
    private static final QName EXTLST$26;
    private static final QName MARL$28;
    private static final QName MARR$30;
    private static final QName MART$32;
    private static final QName MARB$34;
    private static final QName VERT$36;
    private static final QName ANCHOR$38;
    private static final QName ANCHORCTR$40;
    private static final QName HORZOVERFLOW$42;
    
    public CTTableCellPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTLineProperties getLnL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineProperties ctLineProperties = (CTLineProperties)this.get_store().find_element_user(CTTableCellPropertiesImpl.LNL$0, 0);
            if (ctLineProperties == null) {
                return null;
            }
            return ctLineProperties;
        }
    }
    
    public boolean isSetLnL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellPropertiesImpl.LNL$0) != 0;
        }
    }
    
    public void setLnL(final CTLineProperties ctLineProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctLineProperties, CTTableCellPropertiesImpl.LNL$0, 0, (short)1);
    }
    
    public CTLineProperties addNewLnL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineProperties)this.get_store().add_element_user(CTTableCellPropertiesImpl.LNL$0);
        }
    }
    
    public void unsetLnL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellPropertiesImpl.LNL$0, 0);
        }
    }
    
    public CTLineProperties getLnR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineProperties ctLineProperties = (CTLineProperties)this.get_store().find_element_user(CTTableCellPropertiesImpl.LNR$2, 0);
            if (ctLineProperties == null) {
                return null;
            }
            return ctLineProperties;
        }
    }
    
    public boolean isSetLnR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellPropertiesImpl.LNR$2) != 0;
        }
    }
    
    public void setLnR(final CTLineProperties ctLineProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctLineProperties, CTTableCellPropertiesImpl.LNR$2, 0, (short)1);
    }
    
    public CTLineProperties addNewLnR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineProperties)this.get_store().add_element_user(CTTableCellPropertiesImpl.LNR$2);
        }
    }
    
    public void unsetLnR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellPropertiesImpl.LNR$2, 0);
        }
    }
    
    public CTLineProperties getLnT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineProperties ctLineProperties = (CTLineProperties)this.get_store().find_element_user(CTTableCellPropertiesImpl.LNT$4, 0);
            if (ctLineProperties == null) {
                return null;
            }
            return ctLineProperties;
        }
    }
    
    public boolean isSetLnT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellPropertiesImpl.LNT$4) != 0;
        }
    }
    
    public void setLnT(final CTLineProperties ctLineProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctLineProperties, CTTableCellPropertiesImpl.LNT$4, 0, (short)1);
    }
    
    public CTLineProperties addNewLnT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineProperties)this.get_store().add_element_user(CTTableCellPropertiesImpl.LNT$4);
        }
    }
    
    public void unsetLnT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellPropertiesImpl.LNT$4, 0);
        }
    }
    
    public CTLineProperties getLnB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineProperties ctLineProperties = (CTLineProperties)this.get_store().find_element_user(CTTableCellPropertiesImpl.LNB$6, 0);
            if (ctLineProperties == null) {
                return null;
            }
            return ctLineProperties;
        }
    }
    
    public boolean isSetLnB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellPropertiesImpl.LNB$6) != 0;
        }
    }
    
    public void setLnB(final CTLineProperties ctLineProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctLineProperties, CTTableCellPropertiesImpl.LNB$6, 0, (short)1);
    }
    
    public CTLineProperties addNewLnB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineProperties)this.get_store().add_element_user(CTTableCellPropertiesImpl.LNB$6);
        }
    }
    
    public void unsetLnB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellPropertiesImpl.LNB$6, 0);
        }
    }
    
    public CTLineProperties getLnTlToBr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineProperties ctLineProperties = (CTLineProperties)this.get_store().find_element_user(CTTableCellPropertiesImpl.LNTLTOBR$8, 0);
            if (ctLineProperties == null) {
                return null;
            }
            return ctLineProperties;
        }
    }
    
    public boolean isSetLnTlToBr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellPropertiesImpl.LNTLTOBR$8) != 0;
        }
    }
    
    public void setLnTlToBr(final CTLineProperties ctLineProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctLineProperties, CTTableCellPropertiesImpl.LNTLTOBR$8, 0, (short)1);
    }
    
    public CTLineProperties addNewLnTlToBr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineProperties)this.get_store().add_element_user(CTTableCellPropertiesImpl.LNTLTOBR$8);
        }
    }
    
    public void unsetLnTlToBr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellPropertiesImpl.LNTLTOBR$8, 0);
        }
    }
    
    public CTLineProperties getLnBlToTr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineProperties ctLineProperties = (CTLineProperties)this.get_store().find_element_user(CTTableCellPropertiesImpl.LNBLTOTR$10, 0);
            if (ctLineProperties == null) {
                return null;
            }
            return ctLineProperties;
        }
    }
    
    public boolean isSetLnBlToTr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellPropertiesImpl.LNBLTOTR$10) != 0;
        }
    }
    
    public void setLnBlToTr(final CTLineProperties ctLineProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctLineProperties, CTTableCellPropertiesImpl.LNBLTOTR$10, 0, (short)1);
    }
    
    public CTLineProperties addNewLnBlToTr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineProperties)this.get_store().add_element_user(CTTableCellPropertiesImpl.LNBLTOTR$10);
        }
    }
    
    public void unsetLnBlToTr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellPropertiesImpl.LNBLTOTR$10, 0);
        }
    }
    
    public CTCell3D getCell3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCell3D ctCell3D = (CTCell3D)this.get_store().find_element_user(CTTableCellPropertiesImpl.CELL3D$12, 0);
            if (ctCell3D == null) {
                return null;
            }
            return ctCell3D;
        }
    }
    
    public boolean isSetCell3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellPropertiesImpl.CELL3D$12) != 0;
        }
    }
    
    public void setCell3D(final CTCell3D ctCell3D) {
        this.generatedSetterHelperImpl((XmlObject)ctCell3D, CTTableCellPropertiesImpl.CELL3D$12, 0, (short)1);
    }
    
    public CTCell3D addNewCell3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCell3D)this.get_store().add_element_user(CTTableCellPropertiesImpl.CELL3D$12);
        }
    }
    
    public void unsetCell3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellPropertiesImpl.CELL3D$12, 0);
        }
    }
    
    public CTNoFillProperties getNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNoFillProperties ctNoFillProperties = (CTNoFillProperties)this.get_store().find_element_user(CTTableCellPropertiesImpl.NOFILL$14, 0);
            if (ctNoFillProperties == null) {
                return null;
            }
            return ctNoFillProperties;
        }
    }
    
    public boolean isSetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellPropertiesImpl.NOFILL$14) != 0;
        }
    }
    
    public void setNoFill(final CTNoFillProperties ctNoFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctNoFillProperties, CTTableCellPropertiesImpl.NOFILL$14, 0, (short)1);
    }
    
    public CTNoFillProperties addNewNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNoFillProperties)this.get_store().add_element_user(CTTableCellPropertiesImpl.NOFILL$14);
        }
    }
    
    public void unsetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellPropertiesImpl.NOFILL$14, 0);
        }
    }
    
    public CTSolidColorFillProperties getSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSolidColorFillProperties ctSolidColorFillProperties = (CTSolidColorFillProperties)this.get_store().find_element_user(CTTableCellPropertiesImpl.SOLIDFILL$16, 0);
            if (ctSolidColorFillProperties == null) {
                return null;
            }
            return ctSolidColorFillProperties;
        }
    }
    
    public boolean isSetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellPropertiesImpl.SOLIDFILL$16) != 0;
        }
    }
    
    public void setSolidFill(final CTSolidColorFillProperties ctSolidColorFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctSolidColorFillProperties, CTTableCellPropertiesImpl.SOLIDFILL$16, 0, (short)1);
    }
    
    public CTSolidColorFillProperties addNewSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSolidColorFillProperties)this.get_store().add_element_user(CTTableCellPropertiesImpl.SOLIDFILL$16);
        }
    }
    
    public void unsetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellPropertiesImpl.SOLIDFILL$16, 0);
        }
    }
    
    public CTGradientFillProperties getGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGradientFillProperties ctGradientFillProperties = (CTGradientFillProperties)this.get_store().find_element_user(CTTableCellPropertiesImpl.GRADFILL$18, 0);
            if (ctGradientFillProperties == null) {
                return null;
            }
            return ctGradientFillProperties;
        }
    }
    
    public boolean isSetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellPropertiesImpl.GRADFILL$18) != 0;
        }
    }
    
    public void setGradFill(final CTGradientFillProperties ctGradientFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGradientFillProperties, CTTableCellPropertiesImpl.GRADFILL$18, 0, (short)1);
    }
    
    public CTGradientFillProperties addNewGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGradientFillProperties)this.get_store().add_element_user(CTTableCellPropertiesImpl.GRADFILL$18);
        }
    }
    
    public void unsetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellPropertiesImpl.GRADFILL$18, 0);
        }
    }
    
    public CTBlipFillProperties getBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlipFillProperties ctBlipFillProperties = (CTBlipFillProperties)this.get_store().find_element_user(CTTableCellPropertiesImpl.BLIPFILL$20, 0);
            if (ctBlipFillProperties == null) {
                return null;
            }
            return ctBlipFillProperties;
        }
    }
    
    public boolean isSetBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellPropertiesImpl.BLIPFILL$20) != 0;
        }
    }
    
    public void setBlipFill(final CTBlipFillProperties ctBlipFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctBlipFillProperties, CTTableCellPropertiesImpl.BLIPFILL$20, 0, (short)1);
    }
    
    public CTBlipFillProperties addNewBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlipFillProperties)this.get_store().add_element_user(CTTableCellPropertiesImpl.BLIPFILL$20);
        }
    }
    
    public void unsetBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellPropertiesImpl.BLIPFILL$20, 0);
        }
    }
    
    public CTPatternFillProperties getPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPatternFillProperties ctPatternFillProperties = (CTPatternFillProperties)this.get_store().find_element_user(CTTableCellPropertiesImpl.PATTFILL$22, 0);
            if (ctPatternFillProperties == null) {
                return null;
            }
            return ctPatternFillProperties;
        }
    }
    
    public boolean isSetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellPropertiesImpl.PATTFILL$22) != 0;
        }
    }
    
    public void setPattFill(final CTPatternFillProperties ctPatternFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctPatternFillProperties, CTTableCellPropertiesImpl.PATTFILL$22, 0, (short)1);
    }
    
    public CTPatternFillProperties addNewPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPatternFillProperties)this.get_store().add_element_user(CTTableCellPropertiesImpl.PATTFILL$22);
        }
    }
    
    public void unsetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellPropertiesImpl.PATTFILL$22, 0);
        }
    }
    
    public CTGroupFillProperties getGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupFillProperties ctGroupFillProperties = (CTGroupFillProperties)this.get_store().find_element_user(CTTableCellPropertiesImpl.GRPFILL$24, 0);
            if (ctGroupFillProperties == null) {
                return null;
            }
            return ctGroupFillProperties;
        }
    }
    
    public boolean isSetGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellPropertiesImpl.GRPFILL$24) != 0;
        }
    }
    
    public void setGrpFill(final CTGroupFillProperties ctGroupFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupFillProperties, CTTableCellPropertiesImpl.GRPFILL$24, 0, (short)1);
    }
    
    public CTGroupFillProperties addNewGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupFillProperties)this.get_store().add_element_user(CTTableCellPropertiesImpl.GRPFILL$24);
        }
    }
    
    public void unsetGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellPropertiesImpl.GRPFILL$24, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTTableCellPropertiesImpl.EXTLST$26, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellPropertiesImpl.EXTLST$26) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTableCellPropertiesImpl.EXTLST$26, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTTableCellPropertiesImpl.EXTLST$26);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellPropertiesImpl.EXTLST$26, 0);
        }
    }
    
    public int getMarL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MARL$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableCellPropertiesImpl.MARL$28);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STCoordinate32 xgetMarL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate32 stCoordinate32 = (STCoordinate32)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MARL$28);
            if (stCoordinate32 == null) {
                stCoordinate32 = (STCoordinate32)this.get_default_attribute_value(CTTableCellPropertiesImpl.MARL$28);
            }
            return stCoordinate32;
        }
    }
    
    public boolean isSetMarL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MARL$28) != null;
        }
    }
    
    public void setMarL(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MARL$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.MARL$28);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetMarL(final STCoordinate32 stCoordinate32) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate32 stCoordinate33 = (STCoordinate32)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MARL$28);
            if (stCoordinate33 == null) {
                stCoordinate33 = (STCoordinate32)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.MARL$28);
            }
            stCoordinate33.set((XmlObject)stCoordinate32);
        }
    }
    
    public void unsetMarL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableCellPropertiesImpl.MARL$28);
        }
    }
    
    public int getMarR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MARR$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableCellPropertiesImpl.MARR$30);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STCoordinate32 xgetMarR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate32 stCoordinate32 = (STCoordinate32)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MARR$30);
            if (stCoordinate32 == null) {
                stCoordinate32 = (STCoordinate32)this.get_default_attribute_value(CTTableCellPropertiesImpl.MARR$30);
            }
            return stCoordinate32;
        }
    }
    
    public boolean isSetMarR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MARR$30) != null;
        }
    }
    
    public void setMarR(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MARR$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.MARR$30);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetMarR(final STCoordinate32 stCoordinate32) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate32 stCoordinate33 = (STCoordinate32)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MARR$30);
            if (stCoordinate33 == null) {
                stCoordinate33 = (STCoordinate32)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.MARR$30);
            }
            stCoordinate33.set((XmlObject)stCoordinate32);
        }
    }
    
    public void unsetMarR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableCellPropertiesImpl.MARR$30);
        }
    }
    
    public int getMarT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MART$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableCellPropertiesImpl.MART$32);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STCoordinate32 xgetMarT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate32 stCoordinate32 = (STCoordinate32)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MART$32);
            if (stCoordinate32 == null) {
                stCoordinate32 = (STCoordinate32)this.get_default_attribute_value(CTTableCellPropertiesImpl.MART$32);
            }
            return stCoordinate32;
        }
    }
    
    public boolean isSetMarT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MART$32) != null;
        }
    }
    
    public void setMarT(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MART$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.MART$32);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetMarT(final STCoordinate32 stCoordinate32) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate32 stCoordinate33 = (STCoordinate32)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MART$32);
            if (stCoordinate33 == null) {
                stCoordinate33 = (STCoordinate32)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.MART$32);
            }
            stCoordinate33.set((XmlObject)stCoordinate32);
        }
    }
    
    public void unsetMarT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableCellPropertiesImpl.MART$32);
        }
    }
    
    public int getMarB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MARB$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableCellPropertiesImpl.MARB$34);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STCoordinate32 xgetMarB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate32 stCoordinate32 = (STCoordinate32)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MARB$34);
            if (stCoordinate32 == null) {
                stCoordinate32 = (STCoordinate32)this.get_default_attribute_value(CTTableCellPropertiesImpl.MARB$34);
            }
            return stCoordinate32;
        }
    }
    
    public boolean isSetMarB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MARB$34) != null;
        }
    }
    
    public void setMarB(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MARB$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.MARB$34);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetMarB(final STCoordinate32 stCoordinate32) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate32 stCoordinate33 = (STCoordinate32)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.MARB$34);
            if (stCoordinate33 == null) {
                stCoordinate33 = (STCoordinate32)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.MARB$34);
            }
            stCoordinate33.set((XmlObject)stCoordinate32);
        }
    }
    
    public void unsetMarB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableCellPropertiesImpl.MARB$34);
        }
    }
    
    public STTextVerticalType.Enum getVert() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.VERT$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableCellPropertiesImpl.VERT$36);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STTextVerticalType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextVerticalType xgetVert() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextVerticalType stTextVerticalType = (STTextVerticalType)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.VERT$36);
            if (stTextVerticalType == null) {
                stTextVerticalType = (STTextVerticalType)this.get_default_attribute_value(CTTableCellPropertiesImpl.VERT$36);
            }
            return stTextVerticalType;
        }
    }
    
    public boolean isSetVert() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableCellPropertiesImpl.VERT$36) != null;
        }
    }
    
    public void setVert(final STTextVerticalType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.VERT$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.VERT$36);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVert(final STTextVerticalType stTextVerticalType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextVerticalType stTextVerticalType2 = (STTextVerticalType)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.VERT$36);
            if (stTextVerticalType2 == null) {
                stTextVerticalType2 = (STTextVerticalType)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.VERT$36);
            }
            stTextVerticalType2.set((XmlObject)stTextVerticalType);
        }
    }
    
    public void unsetVert() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableCellPropertiesImpl.VERT$36);
        }
    }
    
    public STTextAnchoringType.Enum getAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.ANCHOR$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableCellPropertiesImpl.ANCHOR$38);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STTextAnchoringType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextAnchoringType xgetAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextAnchoringType stTextAnchoringType = (STTextAnchoringType)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.ANCHOR$38);
            if (stTextAnchoringType == null) {
                stTextAnchoringType = (STTextAnchoringType)this.get_default_attribute_value(CTTableCellPropertiesImpl.ANCHOR$38);
            }
            return stTextAnchoringType;
        }
    }
    
    public boolean isSetAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableCellPropertiesImpl.ANCHOR$38) != null;
        }
    }
    
    public void setAnchor(final STTextAnchoringType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.ANCHOR$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.ANCHOR$38);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAnchor(final STTextAnchoringType stTextAnchoringType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextAnchoringType stTextAnchoringType2 = (STTextAnchoringType)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.ANCHOR$38);
            if (stTextAnchoringType2 == null) {
                stTextAnchoringType2 = (STTextAnchoringType)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.ANCHOR$38);
            }
            stTextAnchoringType2.set((XmlObject)stTextAnchoringType);
        }
    }
    
    public void unsetAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableCellPropertiesImpl.ANCHOR$38);
        }
    }
    
    public boolean getAnchorCtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.ANCHORCTR$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableCellPropertiesImpl.ANCHORCTR$40);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAnchorCtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.ANCHORCTR$40);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTableCellPropertiesImpl.ANCHORCTR$40);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAnchorCtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableCellPropertiesImpl.ANCHORCTR$40) != null;
        }
    }
    
    public void setAnchorCtr(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.ANCHORCTR$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.ANCHORCTR$40);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAnchorCtr(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.ANCHORCTR$40);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.ANCHORCTR$40);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAnchorCtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableCellPropertiesImpl.ANCHORCTR$40);
        }
    }
    
    public STTextHorzOverflowType.Enum getHorzOverflow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.HORZOVERFLOW$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableCellPropertiesImpl.HORZOVERFLOW$42);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STTextHorzOverflowType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextHorzOverflowType xgetHorzOverflow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextHorzOverflowType stTextHorzOverflowType = (STTextHorzOverflowType)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.HORZOVERFLOW$42);
            if (stTextHorzOverflowType == null) {
                stTextHorzOverflowType = (STTextHorzOverflowType)this.get_default_attribute_value(CTTableCellPropertiesImpl.HORZOVERFLOW$42);
            }
            return stTextHorzOverflowType;
        }
    }
    
    public boolean isSetHorzOverflow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableCellPropertiesImpl.HORZOVERFLOW$42) != null;
        }
    }
    
    public void setHorzOverflow(final STTextHorzOverflowType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.HORZOVERFLOW$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.HORZOVERFLOW$42);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHorzOverflow(final STTextHorzOverflowType stTextHorzOverflowType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextHorzOverflowType stTextHorzOverflowType2 = (STTextHorzOverflowType)this.get_store().find_attribute_user(CTTableCellPropertiesImpl.HORZOVERFLOW$42);
            if (stTextHorzOverflowType2 == null) {
                stTextHorzOverflowType2 = (STTextHorzOverflowType)this.get_store().add_attribute_user(CTTableCellPropertiesImpl.HORZOVERFLOW$42);
            }
            stTextHorzOverflowType2.set((XmlObject)stTextHorzOverflowType);
        }
    }
    
    public void unsetHorzOverflow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableCellPropertiesImpl.HORZOVERFLOW$42);
        }
    }
    
    static {
        LNL$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lnL");
        LNR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lnR");
        LNT$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lnT");
        LNB$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lnB");
        LNTLTOBR$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lnTlToBr");
        LNBLTOTR$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lnBlToTr");
        CELL3D$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "cell3D");
        NOFILL$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "noFill");
        SOLIDFILL$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "solidFill");
        GRADFILL$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gradFill");
        BLIPFILL$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blipFill");
        PATTFILL$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pattFill");
        GRPFILL$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "grpFill");
        EXTLST$26 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        MARL$28 = new QName("", "marL");
        MARR$30 = new QName("", "marR");
        MART$32 = new QName("", "marT");
        MARB$34 = new QName("", "marB");
        VERT$36 = new QName("", "vert");
        ANCHOR$38 = new QName("", "anchor");
        ANCHORCTR$40 = new QName("", "anchorCtr");
        HORZOVERFLOW$42 = new QName("", "horzOverflow");
    }
}
