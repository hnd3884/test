package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColHierarchiesUsage;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRowHierarchiesUsage;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotFilters;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotTableStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotHierarchies;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartFormats;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConditionalFormats;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFormats;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColItems;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRowItems;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRowFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotFields;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLocation;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotTableDefinition;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPivotTableDefinitionImpl extends XmlComplexContentImpl implements CTPivotTableDefinition
{
    private static final long serialVersionUID = 1L;
    private static final QName LOCATION$0;
    private static final QName PIVOTFIELDS$2;
    private static final QName ROWFIELDS$4;
    private static final QName ROWITEMS$6;
    private static final QName COLFIELDS$8;
    private static final QName COLITEMS$10;
    private static final QName PAGEFIELDS$12;
    private static final QName DATAFIELDS$14;
    private static final QName FORMATS$16;
    private static final QName CONDITIONALFORMATS$18;
    private static final QName CHARTFORMATS$20;
    private static final QName PIVOTHIERARCHIES$22;
    private static final QName PIVOTTABLESTYLEINFO$24;
    private static final QName FILTERS$26;
    private static final QName ROWHIERARCHIESUSAGE$28;
    private static final QName COLHIERARCHIESUSAGE$30;
    private static final QName EXTLST$32;
    private static final QName NAME$34;
    private static final QName CACHEID$36;
    private static final QName DATAONROWS$38;
    private static final QName DATAPOSITION$40;
    private static final QName AUTOFORMATID$42;
    private static final QName APPLYNUMBERFORMATS$44;
    private static final QName APPLYBORDERFORMATS$46;
    private static final QName APPLYFONTFORMATS$48;
    private static final QName APPLYPATTERNFORMATS$50;
    private static final QName APPLYALIGNMENTFORMATS$52;
    private static final QName APPLYWIDTHHEIGHTFORMATS$54;
    private static final QName DATACAPTION$56;
    private static final QName GRANDTOTALCAPTION$58;
    private static final QName ERRORCAPTION$60;
    private static final QName SHOWERROR$62;
    private static final QName MISSINGCAPTION$64;
    private static final QName SHOWMISSING$66;
    private static final QName PAGESTYLE$68;
    private static final QName PIVOTTABLESTYLE$70;
    private static final QName VACATEDSTYLE$72;
    private static final QName TAG$74;
    private static final QName UPDATEDVERSION$76;
    private static final QName MINREFRESHABLEVERSION$78;
    private static final QName ASTERISKTOTALS$80;
    private static final QName SHOWITEMS$82;
    private static final QName EDITDATA$84;
    private static final QName DISABLEFIELDLIST$86;
    private static final QName SHOWCALCMBRS$88;
    private static final QName VISUALTOTALS$90;
    private static final QName SHOWMULTIPLELABEL$92;
    private static final QName SHOWDATADROPDOWN$94;
    private static final QName SHOWDRILL$96;
    private static final QName PRINTDRILL$98;
    private static final QName SHOWMEMBERPROPERTYTIPS$100;
    private static final QName SHOWDATATIPS$102;
    private static final QName ENABLEWIZARD$104;
    private static final QName ENABLEDRILL$106;
    private static final QName ENABLEFIELDPROPERTIES$108;
    private static final QName PRESERVEFORMATTING$110;
    private static final QName USEAUTOFORMATTING$112;
    private static final QName PAGEWRAP$114;
    private static final QName PAGEOVERTHENDOWN$116;
    private static final QName SUBTOTALHIDDENITEMS$118;
    private static final QName ROWGRANDTOTALS$120;
    private static final QName COLGRANDTOTALS$122;
    private static final QName FIELDPRINTTITLES$124;
    private static final QName ITEMPRINTTITLES$126;
    private static final QName MERGEITEM$128;
    private static final QName SHOWDROPZONES$130;
    private static final QName CREATEDVERSION$132;
    private static final QName INDENT$134;
    private static final QName SHOWEMPTYROW$136;
    private static final QName SHOWEMPTYCOL$138;
    private static final QName SHOWHEADERS$140;
    private static final QName COMPACT$142;
    private static final QName OUTLINE$144;
    private static final QName OUTLINEDATA$146;
    private static final QName COMPACTDATA$148;
    private static final QName PUBLISHED$150;
    private static final QName GRIDDROPZONES$152;
    private static final QName IMMERSIVE$154;
    private static final QName MULTIPLEFIELDFILTERS$156;
    private static final QName CHARTFORMAT$158;
    private static final QName ROWHEADERCAPTION$160;
    private static final QName COLHEADERCAPTION$162;
    private static final QName FIELDLISTSORTASCENDING$164;
    private static final QName MDXSUBQUERIES$166;
    private static final QName CUSTOMLISTSORT$168;
    
    public CTPivotTableDefinitionImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTLocation getLocation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLocation ctLocation = (CTLocation)this.get_store().find_element_user(CTPivotTableDefinitionImpl.LOCATION$0, 0);
            if (ctLocation == null) {
                return null;
            }
            return ctLocation;
        }
    }
    
    public void setLocation(final CTLocation ctLocation) {
        this.generatedSetterHelperImpl((XmlObject)ctLocation, CTPivotTableDefinitionImpl.LOCATION$0, 0, (short)1);
    }
    
    public CTLocation addNewLocation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLocation)this.get_store().add_element_user(CTPivotTableDefinitionImpl.LOCATION$0);
        }
    }
    
    public CTPivotFields getPivotFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPivotFields ctPivotFields = (CTPivotFields)this.get_store().find_element_user(CTPivotTableDefinitionImpl.PIVOTFIELDS$2, 0);
            if (ctPivotFields == null) {
                return null;
            }
            return ctPivotFields;
        }
    }
    
    public boolean isSetPivotFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.PIVOTFIELDS$2) != 0;
        }
    }
    
    public void setPivotFields(final CTPivotFields ctPivotFields) {
        this.generatedSetterHelperImpl((XmlObject)ctPivotFields, CTPivotTableDefinitionImpl.PIVOTFIELDS$2, 0, (short)1);
    }
    
    public CTPivotFields addNewPivotFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPivotFields)this.get_store().add_element_user(CTPivotTableDefinitionImpl.PIVOTFIELDS$2);
        }
    }
    
    public void unsetPivotFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.PIVOTFIELDS$2, 0);
        }
    }
    
    public CTRowFields getRowFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRowFields ctRowFields = (CTRowFields)this.get_store().find_element_user(CTPivotTableDefinitionImpl.ROWFIELDS$4, 0);
            if (ctRowFields == null) {
                return null;
            }
            return ctRowFields;
        }
    }
    
    public boolean isSetRowFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.ROWFIELDS$4) != 0;
        }
    }
    
    public void setRowFields(final CTRowFields ctRowFields) {
        this.generatedSetterHelperImpl((XmlObject)ctRowFields, CTPivotTableDefinitionImpl.ROWFIELDS$4, 0, (short)1);
    }
    
    public CTRowFields addNewRowFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRowFields)this.get_store().add_element_user(CTPivotTableDefinitionImpl.ROWFIELDS$4);
        }
    }
    
    public void unsetRowFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.ROWFIELDS$4, 0);
        }
    }
    
    public CTRowItems getRowItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRowItems ctRowItems = (CTRowItems)this.get_store().find_element_user(CTPivotTableDefinitionImpl.ROWITEMS$6, 0);
            if (ctRowItems == null) {
                return null;
            }
            return ctRowItems;
        }
    }
    
    public boolean isSetRowItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.ROWITEMS$6) != 0;
        }
    }
    
    public void setRowItems(final CTRowItems ctRowItems) {
        this.generatedSetterHelperImpl((XmlObject)ctRowItems, CTPivotTableDefinitionImpl.ROWITEMS$6, 0, (short)1);
    }
    
    public CTRowItems addNewRowItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRowItems)this.get_store().add_element_user(CTPivotTableDefinitionImpl.ROWITEMS$6);
        }
    }
    
    public void unsetRowItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.ROWITEMS$6, 0);
        }
    }
    
    public CTColFields getColFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColFields ctColFields = (CTColFields)this.get_store().find_element_user(CTPivotTableDefinitionImpl.COLFIELDS$8, 0);
            if (ctColFields == null) {
                return null;
            }
            return ctColFields;
        }
    }
    
    public boolean isSetColFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.COLFIELDS$8) != 0;
        }
    }
    
    public void setColFields(final CTColFields ctColFields) {
        this.generatedSetterHelperImpl((XmlObject)ctColFields, CTPivotTableDefinitionImpl.COLFIELDS$8, 0, (short)1);
    }
    
    public CTColFields addNewColFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColFields)this.get_store().add_element_user(CTPivotTableDefinitionImpl.COLFIELDS$8);
        }
    }
    
    public void unsetColFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.COLFIELDS$8, 0);
        }
    }
    
    public CTColItems getColItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColItems ctColItems = (CTColItems)this.get_store().find_element_user(CTPivotTableDefinitionImpl.COLITEMS$10, 0);
            if (ctColItems == null) {
                return null;
            }
            return ctColItems;
        }
    }
    
    public boolean isSetColItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.COLITEMS$10) != 0;
        }
    }
    
    public void setColItems(final CTColItems ctColItems) {
        this.generatedSetterHelperImpl((XmlObject)ctColItems, CTPivotTableDefinitionImpl.COLITEMS$10, 0, (short)1);
    }
    
    public CTColItems addNewColItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColItems)this.get_store().add_element_user(CTPivotTableDefinitionImpl.COLITEMS$10);
        }
    }
    
    public void unsetColItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.COLITEMS$10, 0);
        }
    }
    
    public CTPageFields getPageFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageFields ctPageFields = (CTPageFields)this.get_store().find_element_user(CTPivotTableDefinitionImpl.PAGEFIELDS$12, 0);
            if (ctPageFields == null) {
                return null;
            }
            return ctPageFields;
        }
    }
    
    public boolean isSetPageFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.PAGEFIELDS$12) != 0;
        }
    }
    
    public void setPageFields(final CTPageFields ctPageFields) {
        this.generatedSetterHelperImpl((XmlObject)ctPageFields, CTPivotTableDefinitionImpl.PAGEFIELDS$12, 0, (short)1);
    }
    
    public CTPageFields addNewPageFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageFields)this.get_store().add_element_user(CTPivotTableDefinitionImpl.PAGEFIELDS$12);
        }
    }
    
    public void unsetPageFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.PAGEFIELDS$12, 0);
        }
    }
    
    public CTDataFields getDataFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDataFields ctDataFields = (CTDataFields)this.get_store().find_element_user(CTPivotTableDefinitionImpl.DATAFIELDS$14, 0);
            if (ctDataFields == null) {
                return null;
            }
            return ctDataFields;
        }
    }
    
    public boolean isSetDataFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.DATAFIELDS$14) != 0;
        }
    }
    
    public void setDataFields(final CTDataFields ctDataFields) {
        this.generatedSetterHelperImpl((XmlObject)ctDataFields, CTPivotTableDefinitionImpl.DATAFIELDS$14, 0, (short)1);
    }
    
    public CTDataFields addNewDataFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDataFields)this.get_store().add_element_user(CTPivotTableDefinitionImpl.DATAFIELDS$14);
        }
    }
    
    public void unsetDataFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.DATAFIELDS$14, 0);
        }
    }
    
    public CTFormats getFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFormats ctFormats = (CTFormats)this.get_store().find_element_user(CTPivotTableDefinitionImpl.FORMATS$16, 0);
            if (ctFormats == null) {
                return null;
            }
            return ctFormats;
        }
    }
    
    public boolean isSetFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.FORMATS$16) != 0;
        }
    }
    
    public void setFormats(final CTFormats ctFormats) {
        this.generatedSetterHelperImpl((XmlObject)ctFormats, CTPivotTableDefinitionImpl.FORMATS$16, 0, (short)1);
    }
    
    public CTFormats addNewFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFormats)this.get_store().add_element_user(CTPivotTableDefinitionImpl.FORMATS$16);
        }
    }
    
    public void unsetFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.FORMATS$16, 0);
        }
    }
    
    public CTConditionalFormats getConditionalFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTConditionalFormats ctConditionalFormats = (CTConditionalFormats)this.get_store().find_element_user(CTPivotTableDefinitionImpl.CONDITIONALFORMATS$18, 0);
            if (ctConditionalFormats == null) {
                return null;
            }
            return ctConditionalFormats;
        }
    }
    
    public boolean isSetConditionalFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.CONDITIONALFORMATS$18) != 0;
        }
    }
    
    public void setConditionalFormats(final CTConditionalFormats ctConditionalFormats) {
        this.generatedSetterHelperImpl((XmlObject)ctConditionalFormats, CTPivotTableDefinitionImpl.CONDITIONALFORMATS$18, 0, (short)1);
    }
    
    public CTConditionalFormats addNewConditionalFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTConditionalFormats)this.get_store().add_element_user(CTPivotTableDefinitionImpl.CONDITIONALFORMATS$18);
        }
    }
    
    public void unsetConditionalFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.CONDITIONALFORMATS$18, 0);
        }
    }
    
    public CTChartFormats getChartFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTChartFormats ctChartFormats = (CTChartFormats)this.get_store().find_element_user(CTPivotTableDefinitionImpl.CHARTFORMATS$20, 0);
            if (ctChartFormats == null) {
                return null;
            }
            return ctChartFormats;
        }
    }
    
    public boolean isSetChartFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.CHARTFORMATS$20) != 0;
        }
    }
    
    public void setChartFormats(final CTChartFormats ctChartFormats) {
        this.generatedSetterHelperImpl((XmlObject)ctChartFormats, CTPivotTableDefinitionImpl.CHARTFORMATS$20, 0, (short)1);
    }
    
    public CTChartFormats addNewChartFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChartFormats)this.get_store().add_element_user(CTPivotTableDefinitionImpl.CHARTFORMATS$20);
        }
    }
    
    public void unsetChartFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.CHARTFORMATS$20, 0);
        }
    }
    
    public CTPivotHierarchies getPivotHierarchies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPivotHierarchies ctPivotHierarchies = (CTPivotHierarchies)this.get_store().find_element_user(CTPivotTableDefinitionImpl.PIVOTHIERARCHIES$22, 0);
            if (ctPivotHierarchies == null) {
                return null;
            }
            return ctPivotHierarchies;
        }
    }
    
    public boolean isSetPivotHierarchies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.PIVOTHIERARCHIES$22) != 0;
        }
    }
    
    public void setPivotHierarchies(final CTPivotHierarchies ctPivotHierarchies) {
        this.generatedSetterHelperImpl((XmlObject)ctPivotHierarchies, CTPivotTableDefinitionImpl.PIVOTHIERARCHIES$22, 0, (short)1);
    }
    
    public CTPivotHierarchies addNewPivotHierarchies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPivotHierarchies)this.get_store().add_element_user(CTPivotTableDefinitionImpl.PIVOTHIERARCHIES$22);
        }
    }
    
    public void unsetPivotHierarchies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.PIVOTHIERARCHIES$22, 0);
        }
    }
    
    public CTPivotTableStyle getPivotTableStyleInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPivotTableStyle ctPivotTableStyle = (CTPivotTableStyle)this.get_store().find_element_user(CTPivotTableDefinitionImpl.PIVOTTABLESTYLEINFO$24, 0);
            if (ctPivotTableStyle == null) {
                return null;
            }
            return ctPivotTableStyle;
        }
    }
    
    public boolean isSetPivotTableStyleInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.PIVOTTABLESTYLEINFO$24) != 0;
        }
    }
    
    public void setPivotTableStyleInfo(final CTPivotTableStyle ctPivotTableStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctPivotTableStyle, CTPivotTableDefinitionImpl.PIVOTTABLESTYLEINFO$24, 0, (short)1);
    }
    
    public CTPivotTableStyle addNewPivotTableStyleInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPivotTableStyle)this.get_store().add_element_user(CTPivotTableDefinitionImpl.PIVOTTABLESTYLEINFO$24);
        }
    }
    
    public void unsetPivotTableStyleInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.PIVOTTABLESTYLEINFO$24, 0);
        }
    }
    
    public CTPivotFilters getFilters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPivotFilters ctPivotFilters = (CTPivotFilters)this.get_store().find_element_user(CTPivotTableDefinitionImpl.FILTERS$26, 0);
            if (ctPivotFilters == null) {
                return null;
            }
            return ctPivotFilters;
        }
    }
    
    public boolean isSetFilters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.FILTERS$26) != 0;
        }
    }
    
    public void setFilters(final CTPivotFilters ctPivotFilters) {
        this.generatedSetterHelperImpl((XmlObject)ctPivotFilters, CTPivotTableDefinitionImpl.FILTERS$26, 0, (short)1);
    }
    
    public CTPivotFilters addNewFilters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPivotFilters)this.get_store().add_element_user(CTPivotTableDefinitionImpl.FILTERS$26);
        }
    }
    
    public void unsetFilters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.FILTERS$26, 0);
        }
    }
    
    public CTRowHierarchiesUsage getRowHierarchiesUsage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRowHierarchiesUsage ctRowHierarchiesUsage = (CTRowHierarchiesUsage)this.get_store().find_element_user(CTPivotTableDefinitionImpl.ROWHIERARCHIESUSAGE$28, 0);
            if (ctRowHierarchiesUsage == null) {
                return null;
            }
            return ctRowHierarchiesUsage;
        }
    }
    
    public boolean isSetRowHierarchiesUsage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.ROWHIERARCHIESUSAGE$28) != 0;
        }
    }
    
    public void setRowHierarchiesUsage(final CTRowHierarchiesUsage ctRowHierarchiesUsage) {
        this.generatedSetterHelperImpl((XmlObject)ctRowHierarchiesUsage, CTPivotTableDefinitionImpl.ROWHIERARCHIESUSAGE$28, 0, (short)1);
    }
    
    public CTRowHierarchiesUsage addNewRowHierarchiesUsage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRowHierarchiesUsage)this.get_store().add_element_user(CTPivotTableDefinitionImpl.ROWHIERARCHIESUSAGE$28);
        }
    }
    
    public void unsetRowHierarchiesUsage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.ROWHIERARCHIESUSAGE$28, 0);
        }
    }
    
    public CTColHierarchiesUsage getColHierarchiesUsage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColHierarchiesUsage ctColHierarchiesUsage = (CTColHierarchiesUsage)this.get_store().find_element_user(CTPivotTableDefinitionImpl.COLHIERARCHIESUSAGE$30, 0);
            if (ctColHierarchiesUsage == null) {
                return null;
            }
            return ctColHierarchiesUsage;
        }
    }
    
    public boolean isSetColHierarchiesUsage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.COLHIERARCHIESUSAGE$30) != 0;
        }
    }
    
    public void setColHierarchiesUsage(final CTColHierarchiesUsage ctColHierarchiesUsage) {
        this.generatedSetterHelperImpl((XmlObject)ctColHierarchiesUsage, CTPivotTableDefinitionImpl.COLHIERARCHIESUSAGE$30, 0, (short)1);
    }
    
    public CTColHierarchiesUsage addNewColHierarchiesUsage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColHierarchiesUsage)this.get_store().add_element_user(CTPivotTableDefinitionImpl.COLHIERARCHIESUSAGE$30);
        }
    }
    
    public void unsetColHierarchiesUsage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.COLHIERARCHIESUSAGE$30, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTPivotTableDefinitionImpl.EXTLST$32, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotTableDefinitionImpl.EXTLST$32) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPivotTableDefinitionImpl.EXTLST$32, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTPivotTableDefinitionImpl.EXTLST$32);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotTableDefinitionImpl.EXTLST$32, 0);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.NAME$34);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.NAME$34);
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.NAME$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.NAME$34);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.NAME$34);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.NAME$34);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public long getCacheId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CACHEID$36);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCacheId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CACHEID$36);
        }
    }
    
    public void setCacheId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CACHEID$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.CACHEID$36);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCacheId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CACHEID$36);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.CACHEID$36);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public boolean getDataOnRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DATAONROWS$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.DATAONROWS$38);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDataOnRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DATAONROWS$38);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.DATAONROWS$38);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDataOnRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DATAONROWS$38) != null;
        }
    }
    
    public void setDataOnRows(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DATAONROWS$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.DATAONROWS$38);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDataOnRows(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DATAONROWS$38);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.DATAONROWS$38);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDataOnRows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.DATAONROWS$38);
        }
    }
    
    public long getDataPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DATAPOSITION$40);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetDataPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DATAPOSITION$40);
        }
    }
    
    public boolean isSetDataPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DATAPOSITION$40) != null;
        }
    }
    
    public void setDataPosition(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DATAPOSITION$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.DATAPOSITION$40);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDataPosition(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DATAPOSITION$40);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.DATAPOSITION$40);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetDataPosition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.DATAPOSITION$40);
        }
    }
    
    public long getAutoFormatId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.AUTOFORMATID$42);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetAutoFormatId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.AUTOFORMATID$42);
        }
    }
    
    public boolean isSetAutoFormatId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.AUTOFORMATID$42) != null;
        }
    }
    
    public void setAutoFormatId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.AUTOFORMATID$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.AUTOFORMATID$42);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetAutoFormatId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.AUTOFORMATID$42);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.AUTOFORMATID$42);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetAutoFormatId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.AUTOFORMATID$42);
        }
    }
    
    public boolean getApplyNumberFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYNUMBERFORMATS$44);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetApplyNumberFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYNUMBERFORMATS$44);
        }
    }
    
    public boolean isSetApplyNumberFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYNUMBERFORMATS$44) != null;
        }
    }
    
    public void setApplyNumberFormats(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYNUMBERFORMATS$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.APPLYNUMBERFORMATS$44);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetApplyNumberFormats(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYNUMBERFORMATS$44);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.APPLYNUMBERFORMATS$44);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetApplyNumberFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.APPLYNUMBERFORMATS$44);
        }
    }
    
    public boolean getApplyBorderFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYBORDERFORMATS$46);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetApplyBorderFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYBORDERFORMATS$46);
        }
    }
    
    public boolean isSetApplyBorderFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYBORDERFORMATS$46) != null;
        }
    }
    
    public void setApplyBorderFormats(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYBORDERFORMATS$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.APPLYBORDERFORMATS$46);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetApplyBorderFormats(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYBORDERFORMATS$46);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.APPLYBORDERFORMATS$46);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetApplyBorderFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.APPLYBORDERFORMATS$46);
        }
    }
    
    public boolean getApplyFontFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYFONTFORMATS$48);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetApplyFontFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYFONTFORMATS$48);
        }
    }
    
    public boolean isSetApplyFontFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYFONTFORMATS$48) != null;
        }
    }
    
    public void setApplyFontFormats(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYFONTFORMATS$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.APPLYFONTFORMATS$48);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetApplyFontFormats(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYFONTFORMATS$48);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.APPLYFONTFORMATS$48);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetApplyFontFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.APPLYFONTFORMATS$48);
        }
    }
    
    public boolean getApplyPatternFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYPATTERNFORMATS$50);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetApplyPatternFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYPATTERNFORMATS$50);
        }
    }
    
    public boolean isSetApplyPatternFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYPATTERNFORMATS$50) != null;
        }
    }
    
    public void setApplyPatternFormats(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYPATTERNFORMATS$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.APPLYPATTERNFORMATS$50);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetApplyPatternFormats(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYPATTERNFORMATS$50);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.APPLYPATTERNFORMATS$50);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetApplyPatternFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.APPLYPATTERNFORMATS$50);
        }
    }
    
    public boolean getApplyAlignmentFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYALIGNMENTFORMATS$52);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetApplyAlignmentFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYALIGNMENTFORMATS$52);
        }
    }
    
    public boolean isSetApplyAlignmentFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYALIGNMENTFORMATS$52) != null;
        }
    }
    
    public void setApplyAlignmentFormats(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYALIGNMENTFORMATS$52);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.APPLYALIGNMENTFORMATS$52);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetApplyAlignmentFormats(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYALIGNMENTFORMATS$52);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.APPLYALIGNMENTFORMATS$52);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetApplyAlignmentFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.APPLYALIGNMENTFORMATS$52);
        }
    }
    
    public boolean getApplyWidthHeightFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYWIDTHHEIGHTFORMATS$54);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetApplyWidthHeightFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYWIDTHHEIGHTFORMATS$54);
        }
    }
    
    public boolean isSetApplyWidthHeightFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYWIDTHHEIGHTFORMATS$54) != null;
        }
    }
    
    public void setApplyWidthHeightFormats(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYWIDTHHEIGHTFORMATS$54);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.APPLYWIDTHHEIGHTFORMATS$54);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetApplyWidthHeightFormats(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.APPLYWIDTHHEIGHTFORMATS$54);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.APPLYWIDTHHEIGHTFORMATS$54);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetApplyWidthHeightFormats() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.APPLYWIDTHHEIGHTFORMATS$54);
        }
    }
    
    public String getDataCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DATACAPTION$56);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetDataCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DATACAPTION$56);
        }
    }
    
    public void setDataCaption(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DATACAPTION$56);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.DATACAPTION$56);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDataCaption(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DATACAPTION$56);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.DATACAPTION$56);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public String getGrandTotalCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.GRANDTOTALCAPTION$58);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetGrandTotalCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.GRANDTOTALCAPTION$58);
        }
    }
    
    public boolean isSetGrandTotalCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.GRANDTOTALCAPTION$58) != null;
        }
    }
    
    public void setGrandTotalCaption(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.GRANDTOTALCAPTION$58);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.GRANDTOTALCAPTION$58);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetGrandTotalCaption(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.GRANDTOTALCAPTION$58);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.GRANDTOTALCAPTION$58);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetGrandTotalCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.GRANDTOTALCAPTION$58);
        }
    }
    
    public String getErrorCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ERRORCAPTION$60);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetErrorCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ERRORCAPTION$60);
        }
    }
    
    public boolean isSetErrorCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ERRORCAPTION$60) != null;
        }
    }
    
    public void setErrorCaption(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ERRORCAPTION$60);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ERRORCAPTION$60);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetErrorCaption(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ERRORCAPTION$60);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ERRORCAPTION$60);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetErrorCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.ERRORCAPTION$60);
        }
    }
    
    public boolean getShowError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWERROR$62);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWERROR$62);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWERROR$62);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWERROR$62);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWERROR$62) != null;
        }
    }
    
    public void setShowError(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWERROR$62);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWERROR$62);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowError(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWERROR$62);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWERROR$62);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.SHOWERROR$62);
        }
    }
    
    public String getMissingCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MISSINGCAPTION$64);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetMissingCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MISSINGCAPTION$64);
        }
    }
    
    public boolean isSetMissingCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MISSINGCAPTION$64) != null;
        }
    }
    
    public void setMissingCaption(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MISSINGCAPTION$64);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.MISSINGCAPTION$64);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetMissingCaption(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MISSINGCAPTION$64);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.MISSINGCAPTION$64);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetMissingCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.MISSINGCAPTION$64);
        }
    }
    
    public boolean getShowMissing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWMISSING$66);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWMISSING$66);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowMissing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWMISSING$66);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWMISSING$66);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowMissing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWMISSING$66) != null;
        }
    }
    
    public void setShowMissing(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWMISSING$66);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWMISSING$66);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowMissing(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWMISSING$66);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWMISSING$66);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowMissing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.SHOWMISSING$66);
        }
    }
    
    public String getPageStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PAGESTYLE$68);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetPageStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PAGESTYLE$68);
        }
    }
    
    public boolean isSetPageStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PAGESTYLE$68) != null;
        }
    }
    
    public void setPageStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PAGESTYLE$68);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.PAGESTYLE$68);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetPageStyle(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PAGESTYLE$68);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.PAGESTYLE$68);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetPageStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.PAGESTYLE$68);
        }
    }
    
    public String getPivotTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PIVOTTABLESTYLE$70);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetPivotTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PIVOTTABLESTYLE$70);
        }
    }
    
    public boolean isSetPivotTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PIVOTTABLESTYLE$70) != null;
        }
    }
    
    public void setPivotTableStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PIVOTTABLESTYLE$70);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.PIVOTTABLESTYLE$70);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetPivotTableStyle(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PIVOTTABLESTYLE$70);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.PIVOTTABLESTYLE$70);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetPivotTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.PIVOTTABLESTYLE$70);
        }
    }
    
    public String getVacatedStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.VACATEDSTYLE$72);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetVacatedStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.VACATEDSTYLE$72);
        }
    }
    
    public boolean isSetVacatedStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.VACATEDSTYLE$72) != null;
        }
    }
    
    public void setVacatedStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.VACATEDSTYLE$72);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.VACATEDSTYLE$72);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetVacatedStyle(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.VACATEDSTYLE$72);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.VACATEDSTYLE$72);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetVacatedStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.VACATEDSTYLE$72);
        }
    }
    
    public String getTag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.TAG$74);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetTag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.TAG$74);
        }
    }
    
    public boolean isSetTag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.TAG$74) != null;
        }
    }
    
    public void setTag(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.TAG$74);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.TAG$74);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTag(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.TAG$74);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.TAG$74);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetTag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.TAG$74);
        }
    }
    
    public short getUpdatedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.UPDATEDVERSION$76);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.UPDATEDVERSION$76);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getShortValue();
        }
    }
    
    public XmlUnsignedByte xgetUpdatedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte = (XmlUnsignedByte)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.UPDATEDVERSION$76);
            if (xmlUnsignedByte == null) {
                xmlUnsignedByte = (XmlUnsignedByte)this.get_default_attribute_value(CTPivotTableDefinitionImpl.UPDATEDVERSION$76);
            }
            return xmlUnsignedByte;
        }
    }
    
    public boolean isSetUpdatedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.UPDATEDVERSION$76) != null;
        }
    }
    
    public void setUpdatedVersion(final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.UPDATEDVERSION$76);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.UPDATEDVERSION$76);
            }
            simpleValue.setShortValue(shortValue);
        }
    }
    
    public void xsetUpdatedVersion(final XmlUnsignedByte xmlUnsignedByte) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.UPDATEDVERSION$76);
            if (xmlUnsignedByte2 == null) {
                xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.UPDATEDVERSION$76);
            }
            xmlUnsignedByte2.set((XmlObject)xmlUnsignedByte);
        }
    }
    
    public void unsetUpdatedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.UPDATEDVERSION$76);
        }
    }
    
    public short getMinRefreshableVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MINREFRESHABLEVERSION$78);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.MINREFRESHABLEVERSION$78);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getShortValue();
        }
    }
    
    public XmlUnsignedByte xgetMinRefreshableVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte = (XmlUnsignedByte)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MINREFRESHABLEVERSION$78);
            if (xmlUnsignedByte == null) {
                xmlUnsignedByte = (XmlUnsignedByte)this.get_default_attribute_value(CTPivotTableDefinitionImpl.MINREFRESHABLEVERSION$78);
            }
            return xmlUnsignedByte;
        }
    }
    
    public boolean isSetMinRefreshableVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MINREFRESHABLEVERSION$78) != null;
        }
    }
    
    public void setMinRefreshableVersion(final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MINREFRESHABLEVERSION$78);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.MINREFRESHABLEVERSION$78);
            }
            simpleValue.setShortValue(shortValue);
        }
    }
    
    public void xsetMinRefreshableVersion(final XmlUnsignedByte xmlUnsignedByte) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MINREFRESHABLEVERSION$78);
            if (xmlUnsignedByte2 == null) {
                xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.MINREFRESHABLEVERSION$78);
            }
            xmlUnsignedByte2.set((XmlObject)xmlUnsignedByte);
        }
    }
    
    public void unsetMinRefreshableVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.MINREFRESHABLEVERSION$78);
        }
    }
    
    public boolean getAsteriskTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ASTERISKTOTALS$80);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.ASTERISKTOTALS$80);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAsteriskTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ASTERISKTOTALS$80);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.ASTERISKTOTALS$80);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAsteriskTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ASTERISKTOTALS$80) != null;
        }
    }
    
    public void setAsteriskTotals(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ASTERISKTOTALS$80);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ASTERISKTOTALS$80);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAsteriskTotals(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ASTERISKTOTALS$80);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ASTERISKTOTALS$80);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAsteriskTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.ASTERISKTOTALS$80);
        }
    }
    
    public boolean getShowItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWITEMS$82);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWITEMS$82);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWITEMS$82);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWITEMS$82);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWITEMS$82) != null;
        }
    }
    
    public void setShowItems(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWITEMS$82);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWITEMS$82);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowItems(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWITEMS$82);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWITEMS$82);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.SHOWITEMS$82);
        }
    }
    
    public boolean getEditData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.EDITDATA$84);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.EDITDATA$84);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetEditData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.EDITDATA$84);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.EDITDATA$84);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetEditData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.EDITDATA$84) != null;
        }
    }
    
    public void setEditData(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.EDITDATA$84);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.EDITDATA$84);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetEditData(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.EDITDATA$84);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.EDITDATA$84);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetEditData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.EDITDATA$84);
        }
    }
    
    public boolean getDisableFieldList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DISABLEFIELDLIST$86);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.DISABLEFIELDLIST$86);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDisableFieldList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DISABLEFIELDLIST$86);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.DISABLEFIELDLIST$86);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDisableFieldList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DISABLEFIELDLIST$86) != null;
        }
    }
    
    public void setDisableFieldList(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DISABLEFIELDLIST$86);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.DISABLEFIELDLIST$86);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDisableFieldList(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.DISABLEFIELDLIST$86);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.DISABLEFIELDLIST$86);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDisableFieldList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.DISABLEFIELDLIST$86);
        }
    }
    
    public boolean getShowCalcMbrs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWCALCMBRS$88);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWCALCMBRS$88);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowCalcMbrs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWCALCMBRS$88);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWCALCMBRS$88);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowCalcMbrs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWCALCMBRS$88) != null;
        }
    }
    
    public void setShowCalcMbrs(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWCALCMBRS$88);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWCALCMBRS$88);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowCalcMbrs(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWCALCMBRS$88);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWCALCMBRS$88);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowCalcMbrs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.SHOWCALCMBRS$88);
        }
    }
    
    public boolean getVisualTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.VISUALTOTALS$90);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.VISUALTOTALS$90);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetVisualTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.VISUALTOTALS$90);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.VISUALTOTALS$90);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetVisualTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.VISUALTOTALS$90) != null;
        }
    }
    
    public void setVisualTotals(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.VISUALTOTALS$90);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.VISUALTOTALS$90);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetVisualTotals(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.VISUALTOTALS$90);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.VISUALTOTALS$90);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetVisualTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.VISUALTOTALS$90);
        }
    }
    
    public boolean getShowMultipleLabel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWMULTIPLELABEL$92);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWMULTIPLELABEL$92);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowMultipleLabel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWMULTIPLELABEL$92);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWMULTIPLELABEL$92);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowMultipleLabel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWMULTIPLELABEL$92) != null;
        }
    }
    
    public void setShowMultipleLabel(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWMULTIPLELABEL$92);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWMULTIPLELABEL$92);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowMultipleLabel(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWMULTIPLELABEL$92);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWMULTIPLELABEL$92);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowMultipleLabel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.SHOWMULTIPLELABEL$92);
        }
    }
    
    public boolean getShowDataDropDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDATADROPDOWN$94);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWDATADROPDOWN$94);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowDataDropDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDATADROPDOWN$94);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWDATADROPDOWN$94);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowDataDropDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDATADROPDOWN$94) != null;
        }
    }
    
    public void setShowDataDropDown(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDATADROPDOWN$94);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWDATADROPDOWN$94);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowDataDropDown(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDATADROPDOWN$94);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWDATADROPDOWN$94);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowDataDropDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.SHOWDATADROPDOWN$94);
        }
    }
    
    public boolean getShowDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDRILL$96);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWDRILL$96);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDRILL$96);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWDRILL$96);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDRILL$96) != null;
        }
    }
    
    public void setShowDrill(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDRILL$96);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWDRILL$96);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowDrill(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDRILL$96);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWDRILL$96);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.SHOWDRILL$96);
        }
    }
    
    public boolean getPrintDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PRINTDRILL$98);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.PRINTDRILL$98);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPrintDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PRINTDRILL$98);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.PRINTDRILL$98);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPrintDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PRINTDRILL$98) != null;
        }
    }
    
    public void setPrintDrill(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PRINTDRILL$98);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.PRINTDRILL$98);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPrintDrill(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PRINTDRILL$98);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.PRINTDRILL$98);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPrintDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.PRINTDRILL$98);
        }
    }
    
    public boolean getShowMemberPropertyTips() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWMEMBERPROPERTYTIPS$100);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWMEMBERPROPERTYTIPS$100);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowMemberPropertyTips() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWMEMBERPROPERTYTIPS$100);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWMEMBERPROPERTYTIPS$100);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowMemberPropertyTips() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWMEMBERPROPERTYTIPS$100) != null;
        }
    }
    
    public void setShowMemberPropertyTips(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWMEMBERPROPERTYTIPS$100);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWMEMBERPROPERTYTIPS$100);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowMemberPropertyTips(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWMEMBERPROPERTYTIPS$100);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWMEMBERPROPERTYTIPS$100);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowMemberPropertyTips() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.SHOWMEMBERPROPERTYTIPS$100);
        }
    }
    
    public boolean getShowDataTips() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDATATIPS$102);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWDATATIPS$102);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowDataTips() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDATATIPS$102);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWDATATIPS$102);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowDataTips() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDATATIPS$102) != null;
        }
    }
    
    public void setShowDataTips(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDATATIPS$102);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWDATATIPS$102);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowDataTips(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDATATIPS$102);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWDATATIPS$102);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowDataTips() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.SHOWDATATIPS$102);
        }
    }
    
    public boolean getEnableWizard() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ENABLEWIZARD$104);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.ENABLEWIZARD$104);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetEnableWizard() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ENABLEWIZARD$104);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.ENABLEWIZARD$104);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetEnableWizard() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ENABLEWIZARD$104) != null;
        }
    }
    
    public void setEnableWizard(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ENABLEWIZARD$104);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ENABLEWIZARD$104);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetEnableWizard(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ENABLEWIZARD$104);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ENABLEWIZARD$104);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetEnableWizard() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.ENABLEWIZARD$104);
        }
    }
    
    public boolean getEnableDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ENABLEDRILL$106);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.ENABLEDRILL$106);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetEnableDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ENABLEDRILL$106);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.ENABLEDRILL$106);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetEnableDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ENABLEDRILL$106) != null;
        }
    }
    
    public void setEnableDrill(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ENABLEDRILL$106);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ENABLEDRILL$106);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetEnableDrill(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ENABLEDRILL$106);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ENABLEDRILL$106);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetEnableDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.ENABLEDRILL$106);
        }
    }
    
    public boolean getEnableFieldProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ENABLEFIELDPROPERTIES$108);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.ENABLEFIELDPROPERTIES$108);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetEnableFieldProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ENABLEFIELDPROPERTIES$108);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.ENABLEFIELDPROPERTIES$108);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetEnableFieldProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ENABLEFIELDPROPERTIES$108) != null;
        }
    }
    
    public void setEnableFieldProperties(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ENABLEFIELDPROPERTIES$108);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ENABLEFIELDPROPERTIES$108);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetEnableFieldProperties(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ENABLEFIELDPROPERTIES$108);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ENABLEFIELDPROPERTIES$108);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetEnableFieldProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.ENABLEFIELDPROPERTIES$108);
        }
    }
    
    public boolean getPreserveFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PRESERVEFORMATTING$110);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.PRESERVEFORMATTING$110);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPreserveFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PRESERVEFORMATTING$110);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.PRESERVEFORMATTING$110);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPreserveFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PRESERVEFORMATTING$110) != null;
        }
    }
    
    public void setPreserveFormatting(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PRESERVEFORMATTING$110);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.PRESERVEFORMATTING$110);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPreserveFormatting(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PRESERVEFORMATTING$110);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.PRESERVEFORMATTING$110);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPreserveFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.PRESERVEFORMATTING$110);
        }
    }
    
    public boolean getUseAutoFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.USEAUTOFORMATTING$112);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.USEAUTOFORMATTING$112);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetUseAutoFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.USEAUTOFORMATTING$112);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.USEAUTOFORMATTING$112);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetUseAutoFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.USEAUTOFORMATTING$112) != null;
        }
    }
    
    public void setUseAutoFormatting(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.USEAUTOFORMATTING$112);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.USEAUTOFORMATTING$112);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetUseAutoFormatting(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.USEAUTOFORMATTING$112);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.USEAUTOFORMATTING$112);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetUseAutoFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.USEAUTOFORMATTING$112);
        }
    }
    
    public long getPageWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PAGEWRAP$114);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.PAGEWRAP$114);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetPageWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PAGEWRAP$114);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPivotTableDefinitionImpl.PAGEWRAP$114);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetPageWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PAGEWRAP$114) != null;
        }
    }
    
    public void setPageWrap(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PAGEWRAP$114);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.PAGEWRAP$114);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetPageWrap(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PAGEWRAP$114);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.PAGEWRAP$114);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetPageWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.PAGEWRAP$114);
        }
    }
    
    public boolean getPageOverThenDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PAGEOVERTHENDOWN$116);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.PAGEOVERTHENDOWN$116);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPageOverThenDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PAGEOVERTHENDOWN$116);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.PAGEOVERTHENDOWN$116);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPageOverThenDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PAGEOVERTHENDOWN$116) != null;
        }
    }
    
    public void setPageOverThenDown(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PAGEOVERTHENDOWN$116);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.PAGEOVERTHENDOWN$116);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPageOverThenDown(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PAGEOVERTHENDOWN$116);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.PAGEOVERTHENDOWN$116);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPageOverThenDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.PAGEOVERTHENDOWN$116);
        }
    }
    
    public boolean getSubtotalHiddenItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SUBTOTALHIDDENITEMS$118);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SUBTOTALHIDDENITEMS$118);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSubtotalHiddenItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SUBTOTALHIDDENITEMS$118);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SUBTOTALHIDDENITEMS$118);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSubtotalHiddenItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SUBTOTALHIDDENITEMS$118) != null;
        }
    }
    
    public void setSubtotalHiddenItems(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SUBTOTALHIDDENITEMS$118);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SUBTOTALHIDDENITEMS$118);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSubtotalHiddenItems(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SUBTOTALHIDDENITEMS$118);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SUBTOTALHIDDENITEMS$118);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSubtotalHiddenItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.SUBTOTALHIDDENITEMS$118);
        }
    }
    
    public boolean getRowGrandTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ROWGRANDTOTALS$120);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.ROWGRANDTOTALS$120);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetRowGrandTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ROWGRANDTOTALS$120);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.ROWGRANDTOTALS$120);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetRowGrandTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ROWGRANDTOTALS$120) != null;
        }
    }
    
    public void setRowGrandTotals(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ROWGRANDTOTALS$120);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ROWGRANDTOTALS$120);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetRowGrandTotals(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ROWGRANDTOTALS$120);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ROWGRANDTOTALS$120);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetRowGrandTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.ROWGRANDTOTALS$120);
        }
    }
    
    public boolean getColGrandTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COLGRANDTOTALS$122);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.COLGRANDTOTALS$122);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetColGrandTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COLGRANDTOTALS$122);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.COLGRANDTOTALS$122);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetColGrandTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COLGRANDTOTALS$122) != null;
        }
    }
    
    public void setColGrandTotals(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COLGRANDTOTALS$122);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.COLGRANDTOTALS$122);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetColGrandTotals(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COLGRANDTOTALS$122);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.COLGRANDTOTALS$122);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetColGrandTotals() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.COLGRANDTOTALS$122);
        }
    }
    
    public boolean getFieldPrintTitles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.FIELDPRINTTITLES$124);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.FIELDPRINTTITLES$124);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFieldPrintTitles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.FIELDPRINTTITLES$124);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.FIELDPRINTTITLES$124);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFieldPrintTitles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.FIELDPRINTTITLES$124) != null;
        }
    }
    
    public void setFieldPrintTitles(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.FIELDPRINTTITLES$124);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.FIELDPRINTTITLES$124);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFieldPrintTitles(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.FIELDPRINTTITLES$124);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.FIELDPRINTTITLES$124);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFieldPrintTitles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.FIELDPRINTTITLES$124);
        }
    }
    
    public boolean getItemPrintTitles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ITEMPRINTTITLES$126);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.ITEMPRINTTITLES$126);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetItemPrintTitles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ITEMPRINTTITLES$126);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.ITEMPRINTTITLES$126);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetItemPrintTitles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ITEMPRINTTITLES$126) != null;
        }
    }
    
    public void setItemPrintTitles(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ITEMPRINTTITLES$126);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ITEMPRINTTITLES$126);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetItemPrintTitles(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ITEMPRINTTITLES$126);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ITEMPRINTTITLES$126);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetItemPrintTitles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.ITEMPRINTTITLES$126);
        }
    }
    
    public boolean getMergeItem() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MERGEITEM$128);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.MERGEITEM$128);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetMergeItem() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MERGEITEM$128);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.MERGEITEM$128);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetMergeItem() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MERGEITEM$128) != null;
        }
    }
    
    public void setMergeItem(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MERGEITEM$128);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.MERGEITEM$128);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetMergeItem(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MERGEITEM$128);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.MERGEITEM$128);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetMergeItem() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.MERGEITEM$128);
        }
    }
    
    public boolean getShowDropZones() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDROPZONES$130);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWDROPZONES$130);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowDropZones() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDROPZONES$130);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWDROPZONES$130);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowDropZones() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDROPZONES$130) != null;
        }
    }
    
    public void setShowDropZones(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDROPZONES$130);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWDROPZONES$130);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowDropZones(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWDROPZONES$130);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWDROPZONES$130);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowDropZones() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.SHOWDROPZONES$130);
        }
    }
    
    public short getCreatedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CREATEDVERSION$132);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.CREATEDVERSION$132);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getShortValue();
        }
    }
    
    public XmlUnsignedByte xgetCreatedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte = (XmlUnsignedByte)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CREATEDVERSION$132);
            if (xmlUnsignedByte == null) {
                xmlUnsignedByte = (XmlUnsignedByte)this.get_default_attribute_value(CTPivotTableDefinitionImpl.CREATEDVERSION$132);
            }
            return xmlUnsignedByte;
        }
    }
    
    public boolean isSetCreatedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CREATEDVERSION$132) != null;
        }
    }
    
    public void setCreatedVersion(final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CREATEDVERSION$132);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.CREATEDVERSION$132);
            }
            simpleValue.setShortValue(shortValue);
        }
    }
    
    public void xsetCreatedVersion(final XmlUnsignedByte xmlUnsignedByte) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CREATEDVERSION$132);
            if (xmlUnsignedByte2 == null) {
                xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.CREATEDVERSION$132);
            }
            xmlUnsignedByte2.set((XmlObject)xmlUnsignedByte);
        }
    }
    
    public void unsetCreatedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.CREATEDVERSION$132);
        }
    }
    
    public long getIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.INDENT$134);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.INDENT$134);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.INDENT$134);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPivotTableDefinitionImpl.INDENT$134);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.INDENT$134) != null;
        }
    }
    
    public void setIndent(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.INDENT$134);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.INDENT$134);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetIndent(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.INDENT$134);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.INDENT$134);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.INDENT$134);
        }
    }
    
    public boolean getShowEmptyRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWEMPTYROW$136);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWEMPTYROW$136);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowEmptyRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWEMPTYROW$136);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWEMPTYROW$136);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowEmptyRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWEMPTYROW$136) != null;
        }
    }
    
    public void setShowEmptyRow(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWEMPTYROW$136);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWEMPTYROW$136);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowEmptyRow(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWEMPTYROW$136);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWEMPTYROW$136);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowEmptyRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.SHOWEMPTYROW$136);
        }
    }
    
    public boolean getShowEmptyCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWEMPTYCOL$138);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWEMPTYCOL$138);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowEmptyCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWEMPTYCOL$138);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWEMPTYCOL$138);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowEmptyCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWEMPTYCOL$138) != null;
        }
    }
    
    public void setShowEmptyCol(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWEMPTYCOL$138);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWEMPTYCOL$138);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowEmptyCol(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWEMPTYCOL$138);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWEMPTYCOL$138);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowEmptyCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.SHOWEMPTYCOL$138);
        }
    }
    
    public boolean getShowHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWHEADERS$140);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWHEADERS$140);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWHEADERS$140);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.SHOWHEADERS$140);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWHEADERS$140) != null;
        }
    }
    
    public void setShowHeaders(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWHEADERS$140);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWHEADERS$140);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowHeaders(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.SHOWHEADERS$140);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.SHOWHEADERS$140);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.SHOWHEADERS$140);
        }
    }
    
    public boolean getCompact() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COMPACT$142);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.COMPACT$142);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCompact() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COMPACT$142);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.COMPACT$142);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCompact() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COMPACT$142) != null;
        }
    }
    
    public void setCompact(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COMPACT$142);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.COMPACT$142);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCompact(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COMPACT$142);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.COMPACT$142);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCompact() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.COMPACT$142);
        }
    }
    
    public boolean getOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.OUTLINE$144);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.OUTLINE$144);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.OUTLINE$144);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.OUTLINE$144);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.OUTLINE$144) != null;
        }
    }
    
    public void setOutline(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.OUTLINE$144);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.OUTLINE$144);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetOutline(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.OUTLINE$144);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.OUTLINE$144);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.OUTLINE$144);
        }
    }
    
    public boolean getOutlineData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.OUTLINEDATA$146);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.OUTLINEDATA$146);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetOutlineData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.OUTLINEDATA$146);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.OUTLINEDATA$146);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetOutlineData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.OUTLINEDATA$146) != null;
        }
    }
    
    public void setOutlineData(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.OUTLINEDATA$146);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.OUTLINEDATA$146);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetOutlineData(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.OUTLINEDATA$146);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.OUTLINEDATA$146);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetOutlineData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.OUTLINEDATA$146);
        }
    }
    
    public boolean getCompactData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COMPACTDATA$148);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.COMPACTDATA$148);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCompactData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COMPACTDATA$148);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.COMPACTDATA$148);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCompactData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COMPACTDATA$148) != null;
        }
    }
    
    public void setCompactData(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COMPACTDATA$148);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.COMPACTDATA$148);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCompactData(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COMPACTDATA$148);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.COMPACTDATA$148);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCompactData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.COMPACTDATA$148);
        }
    }
    
    public boolean getPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PUBLISHED$150);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.PUBLISHED$150);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PUBLISHED$150);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.PUBLISHED$150);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PUBLISHED$150) != null;
        }
    }
    
    public void setPublished(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PUBLISHED$150);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.PUBLISHED$150);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPublished(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.PUBLISHED$150);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.PUBLISHED$150);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.PUBLISHED$150);
        }
    }
    
    public boolean getGridDropZones() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.GRIDDROPZONES$152);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.GRIDDROPZONES$152);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetGridDropZones() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.GRIDDROPZONES$152);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.GRIDDROPZONES$152);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetGridDropZones() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.GRIDDROPZONES$152) != null;
        }
    }
    
    public void setGridDropZones(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.GRIDDROPZONES$152);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.GRIDDROPZONES$152);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetGridDropZones(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.GRIDDROPZONES$152);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.GRIDDROPZONES$152);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetGridDropZones() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.GRIDDROPZONES$152);
        }
    }
    
    public boolean getImmersive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.IMMERSIVE$154);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.IMMERSIVE$154);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetImmersive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.IMMERSIVE$154);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.IMMERSIVE$154);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetImmersive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.IMMERSIVE$154) != null;
        }
    }
    
    public void setImmersive(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.IMMERSIVE$154);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.IMMERSIVE$154);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetImmersive(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.IMMERSIVE$154);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.IMMERSIVE$154);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetImmersive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.IMMERSIVE$154);
        }
    }
    
    public boolean getMultipleFieldFilters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MULTIPLEFIELDFILTERS$156);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.MULTIPLEFIELDFILTERS$156);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetMultipleFieldFilters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MULTIPLEFIELDFILTERS$156);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.MULTIPLEFIELDFILTERS$156);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetMultipleFieldFilters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MULTIPLEFIELDFILTERS$156) != null;
        }
    }
    
    public void setMultipleFieldFilters(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MULTIPLEFIELDFILTERS$156);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.MULTIPLEFIELDFILTERS$156);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetMultipleFieldFilters(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MULTIPLEFIELDFILTERS$156);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.MULTIPLEFIELDFILTERS$156);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetMultipleFieldFilters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.MULTIPLEFIELDFILTERS$156);
        }
    }
    
    public long getChartFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CHARTFORMAT$158);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.CHARTFORMAT$158);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetChartFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CHARTFORMAT$158);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPivotTableDefinitionImpl.CHARTFORMAT$158);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetChartFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CHARTFORMAT$158) != null;
        }
    }
    
    public void setChartFormat(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CHARTFORMAT$158);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.CHARTFORMAT$158);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetChartFormat(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CHARTFORMAT$158);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.CHARTFORMAT$158);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetChartFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.CHARTFORMAT$158);
        }
    }
    
    public String getRowHeaderCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ROWHEADERCAPTION$160);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetRowHeaderCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ROWHEADERCAPTION$160);
        }
    }
    
    public boolean isSetRowHeaderCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ROWHEADERCAPTION$160) != null;
        }
    }
    
    public void setRowHeaderCaption(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ROWHEADERCAPTION$160);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ROWHEADERCAPTION$160);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetRowHeaderCaption(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.ROWHEADERCAPTION$160);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.ROWHEADERCAPTION$160);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetRowHeaderCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.ROWHEADERCAPTION$160);
        }
    }
    
    public String getColHeaderCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COLHEADERCAPTION$162);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetColHeaderCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COLHEADERCAPTION$162);
        }
    }
    
    public boolean isSetColHeaderCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COLHEADERCAPTION$162) != null;
        }
    }
    
    public void setColHeaderCaption(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COLHEADERCAPTION$162);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.COLHEADERCAPTION$162);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetColHeaderCaption(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.COLHEADERCAPTION$162);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.COLHEADERCAPTION$162);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetColHeaderCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.COLHEADERCAPTION$162);
        }
    }
    
    public boolean getFieldListSortAscending() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.FIELDLISTSORTASCENDING$164);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.FIELDLISTSORTASCENDING$164);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFieldListSortAscending() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.FIELDLISTSORTASCENDING$164);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.FIELDLISTSORTASCENDING$164);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFieldListSortAscending() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.FIELDLISTSORTASCENDING$164) != null;
        }
    }
    
    public void setFieldListSortAscending(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.FIELDLISTSORTASCENDING$164);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.FIELDLISTSORTASCENDING$164);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFieldListSortAscending(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.FIELDLISTSORTASCENDING$164);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.FIELDLISTSORTASCENDING$164);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFieldListSortAscending() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.FIELDLISTSORTASCENDING$164);
        }
    }
    
    public boolean getMdxSubqueries() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MDXSUBQUERIES$166);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.MDXSUBQUERIES$166);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetMdxSubqueries() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MDXSUBQUERIES$166);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.MDXSUBQUERIES$166);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetMdxSubqueries() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MDXSUBQUERIES$166) != null;
        }
    }
    
    public void setMdxSubqueries(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MDXSUBQUERIES$166);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.MDXSUBQUERIES$166);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetMdxSubqueries(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.MDXSUBQUERIES$166);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.MDXSUBQUERIES$166);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetMdxSubqueries() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.MDXSUBQUERIES$166);
        }
    }
    
    public boolean getCustomListSort() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CUSTOMLISTSORT$168);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotTableDefinitionImpl.CUSTOMLISTSORT$168);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCustomListSort() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CUSTOMLISTSORT$168);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotTableDefinitionImpl.CUSTOMLISTSORT$168);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCustomListSort() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CUSTOMLISTSORT$168) != null;
        }
    }
    
    public void setCustomListSort(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CUSTOMLISTSORT$168);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.CUSTOMLISTSORT$168);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCustomListSort(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotTableDefinitionImpl.CUSTOMLISTSORT$168);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotTableDefinitionImpl.CUSTOMLISTSORT$168);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCustomListSort() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotTableDefinitionImpl.CUSTOMLISTSORT$168);
        }
    }
    
    static {
        LOCATION$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "location");
        PIVOTFIELDS$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pivotFields");
        ROWFIELDS$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "rowFields");
        ROWITEMS$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "rowItems");
        COLFIELDS$8 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "colFields");
        COLITEMS$10 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "colItems");
        PAGEFIELDS$12 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pageFields");
        DATAFIELDS$14 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "dataFields");
        FORMATS$16 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "formats");
        CONDITIONALFORMATS$18 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "conditionalFormats");
        CHARTFORMATS$20 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "chartFormats");
        PIVOTHIERARCHIES$22 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pivotHierarchies");
        PIVOTTABLESTYLEINFO$24 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pivotTableStyleInfo");
        FILTERS$26 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "filters");
        ROWHIERARCHIESUSAGE$28 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "rowHierarchiesUsage");
        COLHIERARCHIESUSAGE$30 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "colHierarchiesUsage");
        EXTLST$32 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        NAME$34 = new QName("", "name");
        CACHEID$36 = new QName("", "cacheId");
        DATAONROWS$38 = new QName("", "dataOnRows");
        DATAPOSITION$40 = new QName("", "dataPosition");
        AUTOFORMATID$42 = new QName("", "autoFormatId");
        APPLYNUMBERFORMATS$44 = new QName("", "applyNumberFormats");
        APPLYBORDERFORMATS$46 = new QName("", "applyBorderFormats");
        APPLYFONTFORMATS$48 = new QName("", "applyFontFormats");
        APPLYPATTERNFORMATS$50 = new QName("", "applyPatternFormats");
        APPLYALIGNMENTFORMATS$52 = new QName("", "applyAlignmentFormats");
        APPLYWIDTHHEIGHTFORMATS$54 = new QName("", "applyWidthHeightFormats");
        DATACAPTION$56 = new QName("", "dataCaption");
        GRANDTOTALCAPTION$58 = new QName("", "grandTotalCaption");
        ERRORCAPTION$60 = new QName("", "errorCaption");
        SHOWERROR$62 = new QName("", "showError");
        MISSINGCAPTION$64 = new QName("", "missingCaption");
        SHOWMISSING$66 = new QName("", "showMissing");
        PAGESTYLE$68 = new QName("", "pageStyle");
        PIVOTTABLESTYLE$70 = new QName("", "pivotTableStyle");
        VACATEDSTYLE$72 = new QName("", "vacatedStyle");
        TAG$74 = new QName("", "tag");
        UPDATEDVERSION$76 = new QName("", "updatedVersion");
        MINREFRESHABLEVERSION$78 = new QName("", "minRefreshableVersion");
        ASTERISKTOTALS$80 = new QName("", "asteriskTotals");
        SHOWITEMS$82 = new QName("", "showItems");
        EDITDATA$84 = new QName("", "editData");
        DISABLEFIELDLIST$86 = new QName("", "disableFieldList");
        SHOWCALCMBRS$88 = new QName("", "showCalcMbrs");
        VISUALTOTALS$90 = new QName("", "visualTotals");
        SHOWMULTIPLELABEL$92 = new QName("", "showMultipleLabel");
        SHOWDATADROPDOWN$94 = new QName("", "showDataDropDown");
        SHOWDRILL$96 = new QName("", "showDrill");
        PRINTDRILL$98 = new QName("", "printDrill");
        SHOWMEMBERPROPERTYTIPS$100 = new QName("", "showMemberPropertyTips");
        SHOWDATATIPS$102 = new QName("", "showDataTips");
        ENABLEWIZARD$104 = new QName("", "enableWizard");
        ENABLEDRILL$106 = new QName("", "enableDrill");
        ENABLEFIELDPROPERTIES$108 = new QName("", "enableFieldProperties");
        PRESERVEFORMATTING$110 = new QName("", "preserveFormatting");
        USEAUTOFORMATTING$112 = new QName("", "useAutoFormatting");
        PAGEWRAP$114 = new QName("", "pageWrap");
        PAGEOVERTHENDOWN$116 = new QName("", "pageOverThenDown");
        SUBTOTALHIDDENITEMS$118 = new QName("", "subtotalHiddenItems");
        ROWGRANDTOTALS$120 = new QName("", "rowGrandTotals");
        COLGRANDTOTALS$122 = new QName("", "colGrandTotals");
        FIELDPRINTTITLES$124 = new QName("", "fieldPrintTitles");
        ITEMPRINTTITLES$126 = new QName("", "itemPrintTitles");
        MERGEITEM$128 = new QName("", "mergeItem");
        SHOWDROPZONES$130 = new QName("", "showDropZones");
        CREATEDVERSION$132 = new QName("", "createdVersion");
        INDENT$134 = new QName("", "indent");
        SHOWEMPTYROW$136 = new QName("", "showEmptyRow");
        SHOWEMPTYCOL$138 = new QName("", "showEmptyCol");
        SHOWHEADERS$140 = new QName("", "showHeaders");
        COMPACT$142 = new QName("", "compact");
        OUTLINE$144 = new QName("", "outline");
        OUTLINEDATA$146 = new QName("", "outlineData");
        COMPACTDATA$148 = new QName("", "compactData");
        PUBLISHED$150 = new QName("", "published");
        GRIDDROPZONES$152 = new QName("", "gridDropZones");
        IMMERSIVE$154 = new QName("", "immersive");
        MULTIPLEFIELDFILTERS$156 = new QName("", "multipleFieldFilters");
        CHARTFORMAT$158 = new QName("", "chartFormat");
        ROWHEADERCAPTION$160 = new QName("", "rowHeaderCaption");
        COLHEADERCAPTION$162 = new QName("", "colHeaderCaption");
        FIELDLISTSORTASCENDING$164 = new QName("", "fieldListSortAscending");
        MDXSUBQUERIES$166 = new QName("", "mdxSubqueries");
        CUSTOMLISTSORT$168 = new QName("", "customListSort");
    }
}
