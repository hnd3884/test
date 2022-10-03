package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlString;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageSetUpPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOutlinePr;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSheetPrImpl extends XmlComplexContentImpl implements CTSheetPr
{
    private static final long serialVersionUID = 1L;
    private static final QName TABCOLOR$0;
    private static final QName OUTLINEPR$2;
    private static final QName PAGESETUPPR$4;
    private static final QName SYNCHORIZONTAL$6;
    private static final QName SYNCVERTICAL$8;
    private static final QName SYNCREF$10;
    private static final QName TRANSITIONEVALUATION$12;
    private static final QName TRANSITIONENTRY$14;
    private static final QName PUBLISHED$16;
    private static final QName CODENAME$18;
    private static final QName FILTERMODE$20;
    private static final QName ENABLEFORMATCONDITIONSCALCULATION$22;
    
    public CTSheetPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTColor getTabColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTSheetPrImpl.TABCOLOR$0, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public boolean isSetTabColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSheetPrImpl.TABCOLOR$0) != 0;
        }
    }
    
    public void setTabColor(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTSheetPrImpl.TABCOLOR$0, 0, (short)1);
    }
    
    public CTColor addNewTabColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTSheetPrImpl.TABCOLOR$0);
        }
    }
    
    public void unsetTabColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSheetPrImpl.TABCOLOR$0, 0);
        }
    }
    
    public CTOutlinePr getOutlinePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOutlinePr ctOutlinePr = (CTOutlinePr)this.get_store().find_element_user(CTSheetPrImpl.OUTLINEPR$2, 0);
            if (ctOutlinePr == null) {
                return null;
            }
            return ctOutlinePr;
        }
    }
    
    public boolean isSetOutlinePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSheetPrImpl.OUTLINEPR$2) != 0;
        }
    }
    
    public void setOutlinePr(final CTOutlinePr ctOutlinePr) {
        this.generatedSetterHelperImpl((XmlObject)ctOutlinePr, CTSheetPrImpl.OUTLINEPR$2, 0, (short)1);
    }
    
    public CTOutlinePr addNewOutlinePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOutlinePr)this.get_store().add_element_user(CTSheetPrImpl.OUTLINEPR$2);
        }
    }
    
    public void unsetOutlinePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSheetPrImpl.OUTLINEPR$2, 0);
        }
    }
    
    public CTPageSetUpPr getPageSetUpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageSetUpPr ctPageSetUpPr = (CTPageSetUpPr)this.get_store().find_element_user(CTSheetPrImpl.PAGESETUPPR$4, 0);
            if (ctPageSetUpPr == null) {
                return null;
            }
            return ctPageSetUpPr;
        }
    }
    
    public boolean isSetPageSetUpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSheetPrImpl.PAGESETUPPR$4) != 0;
        }
    }
    
    public void setPageSetUpPr(final CTPageSetUpPr ctPageSetUpPr) {
        this.generatedSetterHelperImpl((XmlObject)ctPageSetUpPr, CTSheetPrImpl.PAGESETUPPR$4, 0, (short)1);
    }
    
    public CTPageSetUpPr addNewPageSetUpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageSetUpPr)this.get_store().add_element_user(CTSheetPrImpl.PAGESETUPPR$4);
        }
    }
    
    public void unsetPageSetUpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSheetPrImpl.PAGESETUPPR$4, 0);
        }
    }
    
    public boolean getSyncHorizontal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.SYNCHORIZONTAL$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetPrImpl.SYNCHORIZONTAL$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSyncHorizontal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetPrImpl.SYNCHORIZONTAL$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetPrImpl.SYNCHORIZONTAL$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSyncHorizontal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetPrImpl.SYNCHORIZONTAL$6) != null;
        }
    }
    
    public void setSyncHorizontal(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.SYNCHORIZONTAL$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetPrImpl.SYNCHORIZONTAL$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSyncHorizontal(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetPrImpl.SYNCHORIZONTAL$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetPrImpl.SYNCHORIZONTAL$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSyncHorizontal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetPrImpl.SYNCHORIZONTAL$6);
        }
    }
    
    public boolean getSyncVertical() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.SYNCVERTICAL$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetPrImpl.SYNCVERTICAL$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSyncVertical() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetPrImpl.SYNCVERTICAL$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetPrImpl.SYNCVERTICAL$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSyncVertical() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetPrImpl.SYNCVERTICAL$8) != null;
        }
    }
    
    public void setSyncVertical(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.SYNCVERTICAL$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetPrImpl.SYNCVERTICAL$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSyncVertical(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetPrImpl.SYNCVERTICAL$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetPrImpl.SYNCVERTICAL$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSyncVertical() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetPrImpl.SYNCVERTICAL$8);
        }
    }
    
    public String getSyncRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.SYNCREF$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRef xgetSyncRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRef)this.get_store().find_attribute_user(CTSheetPrImpl.SYNCREF$10);
        }
    }
    
    public boolean isSetSyncRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetPrImpl.SYNCREF$10) != null;
        }
    }
    
    public void setSyncRef(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.SYNCREF$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetPrImpl.SYNCREF$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSyncRef(final STRef stRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRef stRef2 = (STRef)this.get_store().find_attribute_user(CTSheetPrImpl.SYNCREF$10);
            if (stRef2 == null) {
                stRef2 = (STRef)this.get_store().add_attribute_user(CTSheetPrImpl.SYNCREF$10);
            }
            stRef2.set((XmlObject)stRef);
        }
    }
    
    public void unsetSyncRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetPrImpl.SYNCREF$10);
        }
    }
    
    public boolean getTransitionEvaluation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.TRANSITIONEVALUATION$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetPrImpl.TRANSITIONEVALUATION$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetTransitionEvaluation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetPrImpl.TRANSITIONEVALUATION$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetPrImpl.TRANSITIONEVALUATION$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetTransitionEvaluation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetPrImpl.TRANSITIONEVALUATION$12) != null;
        }
    }
    
    public void setTransitionEvaluation(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.TRANSITIONEVALUATION$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetPrImpl.TRANSITIONEVALUATION$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetTransitionEvaluation(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetPrImpl.TRANSITIONEVALUATION$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetPrImpl.TRANSITIONEVALUATION$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetTransitionEvaluation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetPrImpl.TRANSITIONEVALUATION$12);
        }
    }
    
    public boolean getTransitionEntry() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.TRANSITIONENTRY$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetPrImpl.TRANSITIONENTRY$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetTransitionEntry() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetPrImpl.TRANSITIONENTRY$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetPrImpl.TRANSITIONENTRY$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetTransitionEntry() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetPrImpl.TRANSITIONENTRY$14) != null;
        }
    }
    
    public void setTransitionEntry(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.TRANSITIONENTRY$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetPrImpl.TRANSITIONENTRY$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetTransitionEntry(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetPrImpl.TRANSITIONENTRY$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetPrImpl.TRANSITIONENTRY$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetTransitionEntry() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetPrImpl.TRANSITIONENTRY$14);
        }
    }
    
    public boolean getPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.PUBLISHED$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetPrImpl.PUBLISHED$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetPrImpl.PUBLISHED$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetPrImpl.PUBLISHED$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetPrImpl.PUBLISHED$16) != null;
        }
    }
    
    public void setPublished(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.PUBLISHED$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetPrImpl.PUBLISHED$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPublished(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetPrImpl.PUBLISHED$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetPrImpl.PUBLISHED$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetPrImpl.PUBLISHED$16);
        }
    }
    
    public String getCodeName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.CODENAME$18);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetCodeName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTSheetPrImpl.CODENAME$18);
        }
    }
    
    public boolean isSetCodeName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetPrImpl.CODENAME$18) != null;
        }
    }
    
    public void setCodeName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.CODENAME$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetPrImpl.CODENAME$18);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCodeName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTSheetPrImpl.CODENAME$18);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTSheetPrImpl.CODENAME$18);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetCodeName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetPrImpl.CODENAME$18);
        }
    }
    
    public boolean getFilterMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.FILTERMODE$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetPrImpl.FILTERMODE$20);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFilterMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetPrImpl.FILTERMODE$20);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetPrImpl.FILTERMODE$20);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFilterMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetPrImpl.FILTERMODE$20) != null;
        }
    }
    
    public void setFilterMode(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.FILTERMODE$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetPrImpl.FILTERMODE$20);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFilterMode(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetPrImpl.FILTERMODE$20);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetPrImpl.FILTERMODE$20);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFilterMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetPrImpl.FILTERMODE$20);
        }
    }
    
    public boolean getEnableFormatConditionsCalculation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.ENABLEFORMATCONDITIONSCALCULATION$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetPrImpl.ENABLEFORMATCONDITIONSCALCULATION$22);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetEnableFormatConditionsCalculation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetPrImpl.ENABLEFORMATCONDITIONSCALCULATION$22);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetPrImpl.ENABLEFORMATCONDITIONSCALCULATION$22);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetEnableFormatConditionsCalculation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetPrImpl.ENABLEFORMATCONDITIONSCALCULATION$22) != null;
        }
    }
    
    public void setEnableFormatConditionsCalculation(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetPrImpl.ENABLEFORMATCONDITIONSCALCULATION$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetPrImpl.ENABLEFORMATCONDITIONSCALCULATION$22);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetEnableFormatConditionsCalculation(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetPrImpl.ENABLEFORMATCONDITIONSCALCULATION$22);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetPrImpl.ENABLEFORMATCONDITIONSCALCULATION$22);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetEnableFormatConditionsCalculation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetPrImpl.ENABLEFORMATCONDITIONSCALCULATION$22);
        }
    }
    
    static {
        TABCOLOR$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "tabColor");
        OUTLINEPR$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "outlinePr");
        PAGESETUPPR$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pageSetUpPr");
        SYNCHORIZONTAL$6 = new QName("", "syncHorizontal");
        SYNCVERTICAL$8 = new QName("", "syncVertical");
        SYNCREF$10 = new QName("", "syncRef");
        TRANSITIONEVALUATION$12 = new QName("", "transitionEvaluation");
        TRANSITIONENTRY$14 = new QName("", "transitionEntry");
        PUBLISHED$16 = new QName("", "published");
        CODENAME$18 = new QName("", "codeName");
        FILTERMODE$20 = new QName("", "filterMode");
        ENABLEFORMATCONDITIONSCALCULATION$22 = new QName("", "enableFormatConditionsCalculation");
    }
}
