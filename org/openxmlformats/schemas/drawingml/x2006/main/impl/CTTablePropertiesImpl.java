package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.STGuid;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectContainer;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTablePropertiesImpl extends XmlComplexContentImpl implements CTTableProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName NOFILL$0;
    private static final QName SOLIDFILL$2;
    private static final QName GRADFILL$4;
    private static final QName BLIPFILL$6;
    private static final QName PATTFILL$8;
    private static final QName GRPFILL$10;
    private static final QName EFFECTLST$12;
    private static final QName EFFECTDAG$14;
    private static final QName TABLESTYLE$16;
    private static final QName TABLESTYLEID$18;
    private static final QName EXTLST$20;
    private static final QName RTL$22;
    private static final QName FIRSTROW$24;
    private static final QName FIRSTCOL$26;
    private static final QName LASTROW$28;
    private static final QName LASTCOL$30;
    private static final QName BANDROW$32;
    private static final QName BANDCOL$34;
    
    public CTTablePropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTNoFillProperties getNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNoFillProperties ctNoFillProperties = (CTNoFillProperties)this.get_store().find_element_user(CTTablePropertiesImpl.NOFILL$0, 0);
            if (ctNoFillProperties == null) {
                return null;
            }
            return ctNoFillProperties;
        }
    }
    
    public boolean isSetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTablePropertiesImpl.NOFILL$0) != 0;
        }
    }
    
    public void setNoFill(final CTNoFillProperties ctNoFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctNoFillProperties, CTTablePropertiesImpl.NOFILL$0, 0, (short)1);
    }
    
    public CTNoFillProperties addNewNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNoFillProperties)this.get_store().add_element_user(CTTablePropertiesImpl.NOFILL$0);
        }
    }
    
    public void unsetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTablePropertiesImpl.NOFILL$0, 0);
        }
    }
    
    public CTSolidColorFillProperties getSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSolidColorFillProperties ctSolidColorFillProperties = (CTSolidColorFillProperties)this.get_store().find_element_user(CTTablePropertiesImpl.SOLIDFILL$2, 0);
            if (ctSolidColorFillProperties == null) {
                return null;
            }
            return ctSolidColorFillProperties;
        }
    }
    
    public boolean isSetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTablePropertiesImpl.SOLIDFILL$2) != 0;
        }
    }
    
    public void setSolidFill(final CTSolidColorFillProperties ctSolidColorFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctSolidColorFillProperties, CTTablePropertiesImpl.SOLIDFILL$2, 0, (short)1);
    }
    
    public CTSolidColorFillProperties addNewSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSolidColorFillProperties)this.get_store().add_element_user(CTTablePropertiesImpl.SOLIDFILL$2);
        }
    }
    
    public void unsetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTablePropertiesImpl.SOLIDFILL$2, 0);
        }
    }
    
    public CTGradientFillProperties getGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGradientFillProperties ctGradientFillProperties = (CTGradientFillProperties)this.get_store().find_element_user(CTTablePropertiesImpl.GRADFILL$4, 0);
            if (ctGradientFillProperties == null) {
                return null;
            }
            return ctGradientFillProperties;
        }
    }
    
    public boolean isSetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTablePropertiesImpl.GRADFILL$4) != 0;
        }
    }
    
    public void setGradFill(final CTGradientFillProperties ctGradientFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGradientFillProperties, CTTablePropertiesImpl.GRADFILL$4, 0, (short)1);
    }
    
    public CTGradientFillProperties addNewGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGradientFillProperties)this.get_store().add_element_user(CTTablePropertiesImpl.GRADFILL$4);
        }
    }
    
    public void unsetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTablePropertiesImpl.GRADFILL$4, 0);
        }
    }
    
    public CTBlipFillProperties getBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlipFillProperties ctBlipFillProperties = (CTBlipFillProperties)this.get_store().find_element_user(CTTablePropertiesImpl.BLIPFILL$6, 0);
            if (ctBlipFillProperties == null) {
                return null;
            }
            return ctBlipFillProperties;
        }
    }
    
    public boolean isSetBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTablePropertiesImpl.BLIPFILL$6) != 0;
        }
    }
    
    public void setBlipFill(final CTBlipFillProperties ctBlipFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctBlipFillProperties, CTTablePropertiesImpl.BLIPFILL$6, 0, (short)1);
    }
    
    public CTBlipFillProperties addNewBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlipFillProperties)this.get_store().add_element_user(CTTablePropertiesImpl.BLIPFILL$6);
        }
    }
    
    public void unsetBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTablePropertiesImpl.BLIPFILL$6, 0);
        }
    }
    
    public CTPatternFillProperties getPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPatternFillProperties ctPatternFillProperties = (CTPatternFillProperties)this.get_store().find_element_user(CTTablePropertiesImpl.PATTFILL$8, 0);
            if (ctPatternFillProperties == null) {
                return null;
            }
            return ctPatternFillProperties;
        }
    }
    
    public boolean isSetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTablePropertiesImpl.PATTFILL$8) != 0;
        }
    }
    
    public void setPattFill(final CTPatternFillProperties ctPatternFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctPatternFillProperties, CTTablePropertiesImpl.PATTFILL$8, 0, (short)1);
    }
    
    public CTPatternFillProperties addNewPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPatternFillProperties)this.get_store().add_element_user(CTTablePropertiesImpl.PATTFILL$8);
        }
    }
    
    public void unsetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTablePropertiesImpl.PATTFILL$8, 0);
        }
    }
    
    public CTGroupFillProperties getGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupFillProperties ctGroupFillProperties = (CTGroupFillProperties)this.get_store().find_element_user(CTTablePropertiesImpl.GRPFILL$10, 0);
            if (ctGroupFillProperties == null) {
                return null;
            }
            return ctGroupFillProperties;
        }
    }
    
    public boolean isSetGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTablePropertiesImpl.GRPFILL$10) != 0;
        }
    }
    
    public void setGrpFill(final CTGroupFillProperties ctGroupFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupFillProperties, CTTablePropertiesImpl.GRPFILL$10, 0, (short)1);
    }
    
    public CTGroupFillProperties addNewGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupFillProperties)this.get_store().add_element_user(CTTablePropertiesImpl.GRPFILL$10);
        }
    }
    
    public void unsetGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTablePropertiesImpl.GRPFILL$10, 0);
        }
    }
    
    public CTEffectList getEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectList list = (CTEffectList)this.get_store().find_element_user(CTTablePropertiesImpl.EFFECTLST$12, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTablePropertiesImpl.EFFECTLST$12) != 0;
        }
    }
    
    public void setEffectLst(final CTEffectList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTablePropertiesImpl.EFFECTLST$12, 0, (short)1);
    }
    
    public CTEffectList addNewEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectList)this.get_store().add_element_user(CTTablePropertiesImpl.EFFECTLST$12);
        }
    }
    
    public void unsetEffectLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTablePropertiesImpl.EFFECTLST$12, 0);
        }
    }
    
    public CTEffectContainer getEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectContainer ctEffectContainer = (CTEffectContainer)this.get_store().find_element_user(CTTablePropertiesImpl.EFFECTDAG$14, 0);
            if (ctEffectContainer == null) {
                return null;
            }
            return ctEffectContainer;
        }
    }
    
    public boolean isSetEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTablePropertiesImpl.EFFECTDAG$14) != 0;
        }
    }
    
    public void setEffectDag(final CTEffectContainer ctEffectContainer) {
        this.generatedSetterHelperImpl((XmlObject)ctEffectContainer, CTTablePropertiesImpl.EFFECTDAG$14, 0, (short)1);
    }
    
    public CTEffectContainer addNewEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectContainer)this.get_store().add_element_user(CTTablePropertiesImpl.EFFECTDAG$14);
        }
    }
    
    public void unsetEffectDag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTablePropertiesImpl.EFFECTDAG$14, 0);
        }
    }
    
    public CTTableStyle getTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableStyle ctTableStyle = (CTTableStyle)this.get_store().find_element_user(CTTablePropertiesImpl.TABLESTYLE$16, 0);
            if (ctTableStyle == null) {
                return null;
            }
            return ctTableStyle;
        }
    }
    
    public boolean isSetTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTablePropertiesImpl.TABLESTYLE$16) != 0;
        }
    }
    
    public void setTableStyle(final CTTableStyle ctTableStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTableStyle, CTTablePropertiesImpl.TABLESTYLE$16, 0, (short)1);
    }
    
    public CTTableStyle addNewTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableStyle)this.get_store().add_element_user(CTTablePropertiesImpl.TABLESTYLE$16);
        }
    }
    
    public void unsetTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTablePropertiesImpl.TABLESTYLE$16, 0);
        }
    }
    
    public String getTableStyleId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTTablePropertiesImpl.TABLESTYLEID$18, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STGuid xgetTableStyleId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STGuid)this.get_store().find_element_user(CTTablePropertiesImpl.TABLESTYLEID$18, 0);
        }
    }
    
    public boolean isSetTableStyleId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTablePropertiesImpl.TABLESTYLEID$18) != 0;
        }
    }
    
    public void setTableStyleId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTTablePropertiesImpl.TABLESTYLEID$18, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTTablePropertiesImpl.TABLESTYLEID$18);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTableStyleId(final STGuid stGuid) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STGuid stGuid2 = (STGuid)this.get_store().find_element_user(CTTablePropertiesImpl.TABLESTYLEID$18, 0);
            if (stGuid2 == null) {
                stGuid2 = (STGuid)this.get_store().add_element_user(CTTablePropertiesImpl.TABLESTYLEID$18);
            }
            stGuid2.set((XmlObject)stGuid);
        }
    }
    
    public void unsetTableStyleId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTablePropertiesImpl.TABLESTYLEID$18, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTTablePropertiesImpl.EXTLST$20, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTablePropertiesImpl.EXTLST$20) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTablePropertiesImpl.EXTLST$20, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTTablePropertiesImpl.EXTLST$20);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTablePropertiesImpl.EXTLST$20, 0);
        }
    }
    
    public boolean getRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePropertiesImpl.RTL$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTablePropertiesImpl.RTL$22);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTablePropertiesImpl.RTL$22);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTablePropertiesImpl.RTL$22);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTablePropertiesImpl.RTL$22) != null;
        }
    }
    
    public void setRtl(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePropertiesImpl.RTL$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTablePropertiesImpl.RTL$22);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetRtl(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTablePropertiesImpl.RTL$22);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTablePropertiesImpl.RTL$22);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTablePropertiesImpl.RTL$22);
        }
    }
    
    public boolean getFirstRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePropertiesImpl.FIRSTROW$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTablePropertiesImpl.FIRSTROW$24);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFirstRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTablePropertiesImpl.FIRSTROW$24);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTablePropertiesImpl.FIRSTROW$24);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFirstRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTablePropertiesImpl.FIRSTROW$24) != null;
        }
    }
    
    public void setFirstRow(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePropertiesImpl.FIRSTROW$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTablePropertiesImpl.FIRSTROW$24);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFirstRow(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTablePropertiesImpl.FIRSTROW$24);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTablePropertiesImpl.FIRSTROW$24);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFirstRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTablePropertiesImpl.FIRSTROW$24);
        }
    }
    
    public boolean getFirstCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePropertiesImpl.FIRSTCOL$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTablePropertiesImpl.FIRSTCOL$26);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFirstCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTablePropertiesImpl.FIRSTCOL$26);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTablePropertiesImpl.FIRSTCOL$26);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFirstCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTablePropertiesImpl.FIRSTCOL$26) != null;
        }
    }
    
    public void setFirstCol(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePropertiesImpl.FIRSTCOL$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTablePropertiesImpl.FIRSTCOL$26);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFirstCol(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTablePropertiesImpl.FIRSTCOL$26);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTablePropertiesImpl.FIRSTCOL$26);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFirstCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTablePropertiesImpl.FIRSTCOL$26);
        }
    }
    
    public boolean getLastRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePropertiesImpl.LASTROW$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTablePropertiesImpl.LASTROW$28);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetLastRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTablePropertiesImpl.LASTROW$28);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTablePropertiesImpl.LASTROW$28);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetLastRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTablePropertiesImpl.LASTROW$28) != null;
        }
    }
    
    public void setLastRow(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePropertiesImpl.LASTROW$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTablePropertiesImpl.LASTROW$28);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetLastRow(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTablePropertiesImpl.LASTROW$28);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTablePropertiesImpl.LASTROW$28);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetLastRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTablePropertiesImpl.LASTROW$28);
        }
    }
    
    public boolean getLastCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePropertiesImpl.LASTCOL$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTablePropertiesImpl.LASTCOL$30);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetLastCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTablePropertiesImpl.LASTCOL$30);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTablePropertiesImpl.LASTCOL$30);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetLastCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTablePropertiesImpl.LASTCOL$30) != null;
        }
    }
    
    public void setLastCol(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePropertiesImpl.LASTCOL$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTablePropertiesImpl.LASTCOL$30);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetLastCol(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTablePropertiesImpl.LASTCOL$30);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTablePropertiesImpl.LASTCOL$30);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetLastCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTablePropertiesImpl.LASTCOL$30);
        }
    }
    
    public boolean getBandRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePropertiesImpl.BANDROW$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTablePropertiesImpl.BANDROW$32);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetBandRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTablePropertiesImpl.BANDROW$32);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTablePropertiesImpl.BANDROW$32);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetBandRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTablePropertiesImpl.BANDROW$32) != null;
        }
    }
    
    public void setBandRow(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePropertiesImpl.BANDROW$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTablePropertiesImpl.BANDROW$32);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetBandRow(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTablePropertiesImpl.BANDROW$32);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTablePropertiesImpl.BANDROW$32);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetBandRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTablePropertiesImpl.BANDROW$32);
        }
    }
    
    public boolean getBandCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePropertiesImpl.BANDCOL$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTablePropertiesImpl.BANDCOL$34);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetBandCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTablePropertiesImpl.BANDCOL$34);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTablePropertiesImpl.BANDCOL$34);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetBandCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTablePropertiesImpl.BANDCOL$34) != null;
        }
    }
    
    public void setBandCol(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTablePropertiesImpl.BANDCOL$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTablePropertiesImpl.BANDCOL$34);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetBandCol(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTablePropertiesImpl.BANDCOL$34);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTablePropertiesImpl.BANDCOL$34);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetBandCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTablePropertiesImpl.BANDCOL$34);
        }
    }
    
    static {
        NOFILL$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "noFill");
        SOLIDFILL$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "solidFill");
        GRADFILL$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gradFill");
        BLIPFILL$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blipFill");
        PATTFILL$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pattFill");
        GRPFILL$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "grpFill");
        EFFECTLST$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectLst");
        EFFECTDAG$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectDag");
        TABLESTYLE$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tableStyle");
        TABLESTYLEID$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tableStyleId");
        EXTLST$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        RTL$22 = new QName("", "rtl");
        FIRSTROW$24 = new QName("", "firstRow");
        FIRSTCOL$26 = new QName("", "firstCol");
        LASTROW$28 = new QName("", "lastRow");
        LASTCOL$30 = new QName("", "lastCol");
        BANDROW$32 = new QName("", "bandRow");
        BANDCOL$34 = new QName("", "bandCol");
    }
}
