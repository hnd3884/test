package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STPenAlignment;
import org.openxmlformats.schemas.drawingml.x2006.main.STCompoundLine;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineCap;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineWidth;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineEndProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineJoinMiterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineJoinBevel;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineJoinRound;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDashStopList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetLineDashProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLinePropertiesImpl extends XmlComplexContentImpl implements CTLineProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName NOFILL$0;
    private static final QName SOLIDFILL$2;
    private static final QName GRADFILL$4;
    private static final QName PATTFILL$6;
    private static final QName PRSTDASH$8;
    private static final QName CUSTDASH$10;
    private static final QName ROUND$12;
    private static final QName BEVEL$14;
    private static final QName MITER$16;
    private static final QName HEADEND$18;
    private static final QName TAILEND$20;
    private static final QName EXTLST$22;
    private static final QName W$24;
    private static final QName CAP$26;
    private static final QName CMPD$28;
    private static final QName ALGN$30;
    
    public CTLinePropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTNoFillProperties getNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNoFillProperties ctNoFillProperties = (CTNoFillProperties)this.get_store().find_element_user(CTLinePropertiesImpl.NOFILL$0, 0);
            if (ctNoFillProperties == null) {
                return null;
            }
            return ctNoFillProperties;
        }
    }
    
    public boolean isSetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLinePropertiesImpl.NOFILL$0) != 0;
        }
    }
    
    public void setNoFill(final CTNoFillProperties ctNoFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctNoFillProperties, CTLinePropertiesImpl.NOFILL$0, 0, (short)1);
    }
    
    public CTNoFillProperties addNewNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNoFillProperties)this.get_store().add_element_user(CTLinePropertiesImpl.NOFILL$0);
        }
    }
    
    public void unsetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLinePropertiesImpl.NOFILL$0, 0);
        }
    }
    
    public CTSolidColorFillProperties getSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSolidColorFillProperties ctSolidColorFillProperties = (CTSolidColorFillProperties)this.get_store().find_element_user(CTLinePropertiesImpl.SOLIDFILL$2, 0);
            if (ctSolidColorFillProperties == null) {
                return null;
            }
            return ctSolidColorFillProperties;
        }
    }
    
    public boolean isSetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLinePropertiesImpl.SOLIDFILL$2) != 0;
        }
    }
    
    public void setSolidFill(final CTSolidColorFillProperties ctSolidColorFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctSolidColorFillProperties, CTLinePropertiesImpl.SOLIDFILL$2, 0, (short)1);
    }
    
    public CTSolidColorFillProperties addNewSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSolidColorFillProperties)this.get_store().add_element_user(CTLinePropertiesImpl.SOLIDFILL$2);
        }
    }
    
    public void unsetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLinePropertiesImpl.SOLIDFILL$2, 0);
        }
    }
    
    public CTGradientFillProperties getGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGradientFillProperties ctGradientFillProperties = (CTGradientFillProperties)this.get_store().find_element_user(CTLinePropertiesImpl.GRADFILL$4, 0);
            if (ctGradientFillProperties == null) {
                return null;
            }
            return ctGradientFillProperties;
        }
    }
    
    public boolean isSetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLinePropertiesImpl.GRADFILL$4) != 0;
        }
    }
    
    public void setGradFill(final CTGradientFillProperties ctGradientFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGradientFillProperties, CTLinePropertiesImpl.GRADFILL$4, 0, (short)1);
    }
    
    public CTGradientFillProperties addNewGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGradientFillProperties)this.get_store().add_element_user(CTLinePropertiesImpl.GRADFILL$4);
        }
    }
    
    public void unsetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLinePropertiesImpl.GRADFILL$4, 0);
        }
    }
    
    public CTPatternFillProperties getPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPatternFillProperties ctPatternFillProperties = (CTPatternFillProperties)this.get_store().find_element_user(CTLinePropertiesImpl.PATTFILL$6, 0);
            if (ctPatternFillProperties == null) {
                return null;
            }
            return ctPatternFillProperties;
        }
    }
    
    public boolean isSetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLinePropertiesImpl.PATTFILL$6) != 0;
        }
    }
    
    public void setPattFill(final CTPatternFillProperties ctPatternFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctPatternFillProperties, CTLinePropertiesImpl.PATTFILL$6, 0, (short)1);
    }
    
    public CTPatternFillProperties addNewPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPatternFillProperties)this.get_store().add_element_user(CTLinePropertiesImpl.PATTFILL$6);
        }
    }
    
    public void unsetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLinePropertiesImpl.PATTFILL$6, 0);
        }
    }
    
    public CTPresetLineDashProperties getPrstDash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPresetLineDashProperties ctPresetLineDashProperties = (CTPresetLineDashProperties)this.get_store().find_element_user(CTLinePropertiesImpl.PRSTDASH$8, 0);
            if (ctPresetLineDashProperties == null) {
                return null;
            }
            return ctPresetLineDashProperties;
        }
    }
    
    public boolean isSetPrstDash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLinePropertiesImpl.PRSTDASH$8) != 0;
        }
    }
    
    public void setPrstDash(final CTPresetLineDashProperties ctPresetLineDashProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctPresetLineDashProperties, CTLinePropertiesImpl.PRSTDASH$8, 0, (short)1);
    }
    
    public CTPresetLineDashProperties addNewPrstDash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPresetLineDashProperties)this.get_store().add_element_user(CTLinePropertiesImpl.PRSTDASH$8);
        }
    }
    
    public void unsetPrstDash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLinePropertiesImpl.PRSTDASH$8, 0);
        }
    }
    
    public CTDashStopList getCustDash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDashStopList list = (CTDashStopList)this.get_store().find_element_user(CTLinePropertiesImpl.CUSTDASH$10, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetCustDash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLinePropertiesImpl.CUSTDASH$10) != 0;
        }
    }
    
    public void setCustDash(final CTDashStopList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTLinePropertiesImpl.CUSTDASH$10, 0, (short)1);
    }
    
    public CTDashStopList addNewCustDash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDashStopList)this.get_store().add_element_user(CTLinePropertiesImpl.CUSTDASH$10);
        }
    }
    
    public void unsetCustDash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLinePropertiesImpl.CUSTDASH$10, 0);
        }
    }
    
    public CTLineJoinRound getRound() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineJoinRound ctLineJoinRound = (CTLineJoinRound)this.get_store().find_element_user(CTLinePropertiesImpl.ROUND$12, 0);
            if (ctLineJoinRound == null) {
                return null;
            }
            return ctLineJoinRound;
        }
    }
    
    public boolean isSetRound() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLinePropertiesImpl.ROUND$12) != 0;
        }
    }
    
    public void setRound(final CTLineJoinRound ctLineJoinRound) {
        this.generatedSetterHelperImpl((XmlObject)ctLineJoinRound, CTLinePropertiesImpl.ROUND$12, 0, (short)1);
    }
    
    public CTLineJoinRound addNewRound() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineJoinRound)this.get_store().add_element_user(CTLinePropertiesImpl.ROUND$12);
        }
    }
    
    public void unsetRound() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLinePropertiesImpl.ROUND$12, 0);
        }
    }
    
    public CTLineJoinBevel getBevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineJoinBevel ctLineJoinBevel = (CTLineJoinBevel)this.get_store().find_element_user(CTLinePropertiesImpl.BEVEL$14, 0);
            if (ctLineJoinBevel == null) {
                return null;
            }
            return ctLineJoinBevel;
        }
    }
    
    public boolean isSetBevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLinePropertiesImpl.BEVEL$14) != 0;
        }
    }
    
    public void setBevel(final CTLineJoinBevel ctLineJoinBevel) {
        this.generatedSetterHelperImpl((XmlObject)ctLineJoinBevel, CTLinePropertiesImpl.BEVEL$14, 0, (short)1);
    }
    
    public CTLineJoinBevel addNewBevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineJoinBevel)this.get_store().add_element_user(CTLinePropertiesImpl.BEVEL$14);
        }
    }
    
    public void unsetBevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLinePropertiesImpl.BEVEL$14, 0);
        }
    }
    
    public CTLineJoinMiterProperties getMiter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineJoinMiterProperties ctLineJoinMiterProperties = (CTLineJoinMiterProperties)this.get_store().find_element_user(CTLinePropertiesImpl.MITER$16, 0);
            if (ctLineJoinMiterProperties == null) {
                return null;
            }
            return ctLineJoinMiterProperties;
        }
    }
    
    public boolean isSetMiter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLinePropertiesImpl.MITER$16) != 0;
        }
    }
    
    public void setMiter(final CTLineJoinMiterProperties ctLineJoinMiterProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctLineJoinMiterProperties, CTLinePropertiesImpl.MITER$16, 0, (short)1);
    }
    
    public CTLineJoinMiterProperties addNewMiter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineJoinMiterProperties)this.get_store().add_element_user(CTLinePropertiesImpl.MITER$16);
        }
    }
    
    public void unsetMiter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLinePropertiesImpl.MITER$16, 0);
        }
    }
    
    public CTLineEndProperties getHeadEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineEndProperties ctLineEndProperties = (CTLineEndProperties)this.get_store().find_element_user(CTLinePropertiesImpl.HEADEND$18, 0);
            if (ctLineEndProperties == null) {
                return null;
            }
            return ctLineEndProperties;
        }
    }
    
    public boolean isSetHeadEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLinePropertiesImpl.HEADEND$18) != 0;
        }
    }
    
    public void setHeadEnd(final CTLineEndProperties ctLineEndProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctLineEndProperties, CTLinePropertiesImpl.HEADEND$18, 0, (short)1);
    }
    
    public CTLineEndProperties addNewHeadEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineEndProperties)this.get_store().add_element_user(CTLinePropertiesImpl.HEADEND$18);
        }
    }
    
    public void unsetHeadEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLinePropertiesImpl.HEADEND$18, 0);
        }
    }
    
    public CTLineEndProperties getTailEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineEndProperties ctLineEndProperties = (CTLineEndProperties)this.get_store().find_element_user(CTLinePropertiesImpl.TAILEND$20, 0);
            if (ctLineEndProperties == null) {
                return null;
            }
            return ctLineEndProperties;
        }
    }
    
    public boolean isSetTailEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLinePropertiesImpl.TAILEND$20) != 0;
        }
    }
    
    public void setTailEnd(final CTLineEndProperties ctLineEndProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctLineEndProperties, CTLinePropertiesImpl.TAILEND$20, 0, (short)1);
    }
    
    public CTLineEndProperties addNewTailEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineEndProperties)this.get_store().add_element_user(CTLinePropertiesImpl.TAILEND$20);
        }
    }
    
    public void unsetTailEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLinePropertiesImpl.TAILEND$20, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTLinePropertiesImpl.EXTLST$22, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLinePropertiesImpl.EXTLST$22) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTLinePropertiesImpl.EXTLST$22, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTLinePropertiesImpl.EXTLST$22);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLinePropertiesImpl.EXTLST$22, 0);
        }
    }
    
    public int getW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLinePropertiesImpl.W$24);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STLineWidth xgetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLineWidth)this.get_store().find_attribute_user(CTLinePropertiesImpl.W$24);
        }
    }
    
    public boolean isSetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLinePropertiesImpl.W$24) != null;
        }
    }
    
    public void setW(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLinePropertiesImpl.W$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLinePropertiesImpl.W$24);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetW(final STLineWidth stLineWidth) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLineWidth stLineWidth2 = (STLineWidth)this.get_store().find_attribute_user(CTLinePropertiesImpl.W$24);
            if (stLineWidth2 == null) {
                stLineWidth2 = (STLineWidth)this.get_store().add_attribute_user(CTLinePropertiesImpl.W$24);
            }
            stLineWidth2.set((XmlObject)stLineWidth);
        }
    }
    
    public void unsetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLinePropertiesImpl.W$24);
        }
    }
    
    public STLineCap.Enum getCap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLinePropertiesImpl.CAP$26);
            if (simpleValue == null) {
                return null;
            }
            return (STLineCap.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STLineCap xgetCap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLineCap)this.get_store().find_attribute_user(CTLinePropertiesImpl.CAP$26);
        }
    }
    
    public boolean isSetCap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLinePropertiesImpl.CAP$26) != null;
        }
    }
    
    public void setCap(final STLineCap.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLinePropertiesImpl.CAP$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLinePropertiesImpl.CAP$26);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCap(final STLineCap stLineCap) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLineCap stLineCap2 = (STLineCap)this.get_store().find_attribute_user(CTLinePropertiesImpl.CAP$26);
            if (stLineCap2 == null) {
                stLineCap2 = (STLineCap)this.get_store().add_attribute_user(CTLinePropertiesImpl.CAP$26);
            }
            stLineCap2.set((XmlObject)stLineCap);
        }
    }
    
    public void unsetCap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLinePropertiesImpl.CAP$26);
        }
    }
    
    public STCompoundLine.Enum getCmpd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLinePropertiesImpl.CMPD$28);
            if (simpleValue == null) {
                return null;
            }
            return (STCompoundLine.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STCompoundLine xgetCmpd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCompoundLine)this.get_store().find_attribute_user(CTLinePropertiesImpl.CMPD$28);
        }
    }
    
    public boolean isSetCmpd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLinePropertiesImpl.CMPD$28) != null;
        }
    }
    
    public void setCmpd(final STCompoundLine.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLinePropertiesImpl.CMPD$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLinePropertiesImpl.CMPD$28);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCmpd(final STCompoundLine stCompoundLine) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCompoundLine stCompoundLine2 = (STCompoundLine)this.get_store().find_attribute_user(CTLinePropertiesImpl.CMPD$28);
            if (stCompoundLine2 == null) {
                stCompoundLine2 = (STCompoundLine)this.get_store().add_attribute_user(CTLinePropertiesImpl.CMPD$28);
            }
            stCompoundLine2.set((XmlObject)stCompoundLine);
        }
    }
    
    public void unsetCmpd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLinePropertiesImpl.CMPD$28);
        }
    }
    
    public STPenAlignment.Enum getAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLinePropertiesImpl.ALGN$30);
            if (simpleValue == null) {
                return null;
            }
            return (STPenAlignment.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPenAlignment xgetAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPenAlignment)this.get_store().find_attribute_user(CTLinePropertiesImpl.ALGN$30);
        }
    }
    
    public boolean isSetAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLinePropertiesImpl.ALGN$30) != null;
        }
    }
    
    public void setAlgn(final STPenAlignment.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLinePropertiesImpl.ALGN$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLinePropertiesImpl.ALGN$30);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAlgn(final STPenAlignment stPenAlignment) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPenAlignment stPenAlignment2 = (STPenAlignment)this.get_store().find_attribute_user(CTLinePropertiesImpl.ALGN$30);
            if (stPenAlignment2 == null) {
                stPenAlignment2 = (STPenAlignment)this.get_store().add_attribute_user(CTLinePropertiesImpl.ALGN$30);
            }
            stPenAlignment2.set((XmlObject)stPenAlignment);
        }
    }
    
    public void unsetAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLinePropertiesImpl.ALGN$30);
        }
    }
    
    static {
        NOFILL$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "noFill");
        SOLIDFILL$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "solidFill");
        GRADFILL$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gradFill");
        PATTFILL$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pattFill");
        PRSTDASH$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "prstDash");
        CUSTDASH$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "custDash");
        ROUND$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "round");
        BEVEL$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "bevel");
        MITER$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "miter");
        HEADEND$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "headEnd");
        TAILEND$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tailEnd");
        EXTLST$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        W$24 = new QName("", "w");
        CAP$26 = new QName("", "cap");
        CMPD$28 = new QName("", "cmpd");
        ALGN$30 = new QName("", "algn");
    }
}
