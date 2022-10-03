package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.SchemaTypeLoader;
import java.lang.ref.SoftReference;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPivotTableDefinition extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPivotTableDefinition.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpivottabledefinitionb188type");
    
    CTLocation getLocation();
    
    void setLocation(final CTLocation p0);
    
    CTLocation addNewLocation();
    
    CTPivotFields getPivotFields();
    
    boolean isSetPivotFields();
    
    void setPivotFields(final CTPivotFields p0);
    
    CTPivotFields addNewPivotFields();
    
    void unsetPivotFields();
    
    CTRowFields getRowFields();
    
    boolean isSetRowFields();
    
    void setRowFields(final CTRowFields p0);
    
    CTRowFields addNewRowFields();
    
    void unsetRowFields();
    
    CTRowItems getRowItems();
    
    boolean isSetRowItems();
    
    void setRowItems(final CTRowItems p0);
    
    CTRowItems addNewRowItems();
    
    void unsetRowItems();
    
    CTColFields getColFields();
    
    boolean isSetColFields();
    
    void setColFields(final CTColFields p0);
    
    CTColFields addNewColFields();
    
    void unsetColFields();
    
    CTColItems getColItems();
    
    boolean isSetColItems();
    
    void setColItems(final CTColItems p0);
    
    CTColItems addNewColItems();
    
    void unsetColItems();
    
    CTPageFields getPageFields();
    
    boolean isSetPageFields();
    
    void setPageFields(final CTPageFields p0);
    
    CTPageFields addNewPageFields();
    
    void unsetPageFields();
    
    CTDataFields getDataFields();
    
    boolean isSetDataFields();
    
    void setDataFields(final CTDataFields p0);
    
    CTDataFields addNewDataFields();
    
    void unsetDataFields();
    
    CTFormats getFormats();
    
    boolean isSetFormats();
    
    void setFormats(final CTFormats p0);
    
    CTFormats addNewFormats();
    
    void unsetFormats();
    
    CTConditionalFormats getConditionalFormats();
    
    boolean isSetConditionalFormats();
    
    void setConditionalFormats(final CTConditionalFormats p0);
    
    CTConditionalFormats addNewConditionalFormats();
    
    void unsetConditionalFormats();
    
    CTChartFormats getChartFormats();
    
    boolean isSetChartFormats();
    
    void setChartFormats(final CTChartFormats p0);
    
    CTChartFormats addNewChartFormats();
    
    void unsetChartFormats();
    
    CTPivotHierarchies getPivotHierarchies();
    
    boolean isSetPivotHierarchies();
    
    void setPivotHierarchies(final CTPivotHierarchies p0);
    
    CTPivotHierarchies addNewPivotHierarchies();
    
    void unsetPivotHierarchies();
    
    CTPivotTableStyle getPivotTableStyleInfo();
    
    boolean isSetPivotTableStyleInfo();
    
    void setPivotTableStyleInfo(final CTPivotTableStyle p0);
    
    CTPivotTableStyle addNewPivotTableStyleInfo();
    
    void unsetPivotTableStyleInfo();
    
    CTPivotFilters getFilters();
    
    boolean isSetFilters();
    
    void setFilters(final CTPivotFilters p0);
    
    CTPivotFilters addNewFilters();
    
    void unsetFilters();
    
    CTRowHierarchiesUsage getRowHierarchiesUsage();
    
    boolean isSetRowHierarchiesUsage();
    
    void setRowHierarchiesUsage(final CTRowHierarchiesUsage p0);
    
    CTRowHierarchiesUsage addNewRowHierarchiesUsage();
    
    void unsetRowHierarchiesUsage();
    
    CTColHierarchiesUsage getColHierarchiesUsage();
    
    boolean isSetColHierarchiesUsage();
    
    void setColHierarchiesUsage(final CTColHierarchiesUsage p0);
    
    CTColHierarchiesUsage addNewColHierarchiesUsage();
    
    void unsetColHierarchiesUsage();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    String getName();
    
    STXstring xgetName();
    
    void setName(final String p0);
    
    void xsetName(final STXstring p0);
    
    long getCacheId();
    
    XmlUnsignedInt xgetCacheId();
    
    void setCacheId(final long p0);
    
    void xsetCacheId(final XmlUnsignedInt p0);
    
    boolean getDataOnRows();
    
    XmlBoolean xgetDataOnRows();
    
    boolean isSetDataOnRows();
    
    void setDataOnRows(final boolean p0);
    
    void xsetDataOnRows(final XmlBoolean p0);
    
    void unsetDataOnRows();
    
    long getDataPosition();
    
    XmlUnsignedInt xgetDataPosition();
    
    boolean isSetDataPosition();
    
    void setDataPosition(final long p0);
    
    void xsetDataPosition(final XmlUnsignedInt p0);
    
    void unsetDataPosition();
    
    long getAutoFormatId();
    
    XmlUnsignedInt xgetAutoFormatId();
    
    boolean isSetAutoFormatId();
    
    void setAutoFormatId(final long p0);
    
    void xsetAutoFormatId(final XmlUnsignedInt p0);
    
    void unsetAutoFormatId();
    
    boolean getApplyNumberFormats();
    
    XmlBoolean xgetApplyNumberFormats();
    
    boolean isSetApplyNumberFormats();
    
    void setApplyNumberFormats(final boolean p0);
    
    void xsetApplyNumberFormats(final XmlBoolean p0);
    
    void unsetApplyNumberFormats();
    
    boolean getApplyBorderFormats();
    
    XmlBoolean xgetApplyBorderFormats();
    
    boolean isSetApplyBorderFormats();
    
    void setApplyBorderFormats(final boolean p0);
    
    void xsetApplyBorderFormats(final XmlBoolean p0);
    
    void unsetApplyBorderFormats();
    
    boolean getApplyFontFormats();
    
    XmlBoolean xgetApplyFontFormats();
    
    boolean isSetApplyFontFormats();
    
    void setApplyFontFormats(final boolean p0);
    
    void xsetApplyFontFormats(final XmlBoolean p0);
    
    void unsetApplyFontFormats();
    
    boolean getApplyPatternFormats();
    
    XmlBoolean xgetApplyPatternFormats();
    
    boolean isSetApplyPatternFormats();
    
    void setApplyPatternFormats(final boolean p0);
    
    void xsetApplyPatternFormats(final XmlBoolean p0);
    
    void unsetApplyPatternFormats();
    
    boolean getApplyAlignmentFormats();
    
    XmlBoolean xgetApplyAlignmentFormats();
    
    boolean isSetApplyAlignmentFormats();
    
    void setApplyAlignmentFormats(final boolean p0);
    
    void xsetApplyAlignmentFormats(final XmlBoolean p0);
    
    void unsetApplyAlignmentFormats();
    
    boolean getApplyWidthHeightFormats();
    
    XmlBoolean xgetApplyWidthHeightFormats();
    
    boolean isSetApplyWidthHeightFormats();
    
    void setApplyWidthHeightFormats(final boolean p0);
    
    void xsetApplyWidthHeightFormats(final XmlBoolean p0);
    
    void unsetApplyWidthHeightFormats();
    
    String getDataCaption();
    
    STXstring xgetDataCaption();
    
    void setDataCaption(final String p0);
    
    void xsetDataCaption(final STXstring p0);
    
    String getGrandTotalCaption();
    
    STXstring xgetGrandTotalCaption();
    
    boolean isSetGrandTotalCaption();
    
    void setGrandTotalCaption(final String p0);
    
    void xsetGrandTotalCaption(final STXstring p0);
    
    void unsetGrandTotalCaption();
    
    String getErrorCaption();
    
    STXstring xgetErrorCaption();
    
    boolean isSetErrorCaption();
    
    void setErrorCaption(final String p0);
    
    void xsetErrorCaption(final STXstring p0);
    
    void unsetErrorCaption();
    
    boolean getShowError();
    
    XmlBoolean xgetShowError();
    
    boolean isSetShowError();
    
    void setShowError(final boolean p0);
    
    void xsetShowError(final XmlBoolean p0);
    
    void unsetShowError();
    
    String getMissingCaption();
    
    STXstring xgetMissingCaption();
    
    boolean isSetMissingCaption();
    
    void setMissingCaption(final String p0);
    
    void xsetMissingCaption(final STXstring p0);
    
    void unsetMissingCaption();
    
    boolean getShowMissing();
    
    XmlBoolean xgetShowMissing();
    
    boolean isSetShowMissing();
    
    void setShowMissing(final boolean p0);
    
    void xsetShowMissing(final XmlBoolean p0);
    
    void unsetShowMissing();
    
    String getPageStyle();
    
    STXstring xgetPageStyle();
    
    boolean isSetPageStyle();
    
    void setPageStyle(final String p0);
    
    void xsetPageStyle(final STXstring p0);
    
    void unsetPageStyle();
    
    String getPivotTableStyle();
    
    STXstring xgetPivotTableStyle();
    
    boolean isSetPivotTableStyle();
    
    void setPivotTableStyle(final String p0);
    
    void xsetPivotTableStyle(final STXstring p0);
    
    void unsetPivotTableStyle();
    
    String getVacatedStyle();
    
    STXstring xgetVacatedStyle();
    
    boolean isSetVacatedStyle();
    
    void setVacatedStyle(final String p0);
    
    void xsetVacatedStyle(final STXstring p0);
    
    void unsetVacatedStyle();
    
    String getTag();
    
    STXstring xgetTag();
    
    boolean isSetTag();
    
    void setTag(final String p0);
    
    void xsetTag(final STXstring p0);
    
    void unsetTag();
    
    short getUpdatedVersion();
    
    XmlUnsignedByte xgetUpdatedVersion();
    
    boolean isSetUpdatedVersion();
    
    void setUpdatedVersion(final short p0);
    
    void xsetUpdatedVersion(final XmlUnsignedByte p0);
    
    void unsetUpdatedVersion();
    
    short getMinRefreshableVersion();
    
    XmlUnsignedByte xgetMinRefreshableVersion();
    
    boolean isSetMinRefreshableVersion();
    
    void setMinRefreshableVersion(final short p0);
    
    void xsetMinRefreshableVersion(final XmlUnsignedByte p0);
    
    void unsetMinRefreshableVersion();
    
    boolean getAsteriskTotals();
    
    XmlBoolean xgetAsteriskTotals();
    
    boolean isSetAsteriskTotals();
    
    void setAsteriskTotals(final boolean p0);
    
    void xsetAsteriskTotals(final XmlBoolean p0);
    
    void unsetAsteriskTotals();
    
    boolean getShowItems();
    
    XmlBoolean xgetShowItems();
    
    boolean isSetShowItems();
    
    void setShowItems(final boolean p0);
    
    void xsetShowItems(final XmlBoolean p0);
    
    void unsetShowItems();
    
    boolean getEditData();
    
    XmlBoolean xgetEditData();
    
    boolean isSetEditData();
    
    void setEditData(final boolean p0);
    
    void xsetEditData(final XmlBoolean p0);
    
    void unsetEditData();
    
    boolean getDisableFieldList();
    
    XmlBoolean xgetDisableFieldList();
    
    boolean isSetDisableFieldList();
    
    void setDisableFieldList(final boolean p0);
    
    void xsetDisableFieldList(final XmlBoolean p0);
    
    void unsetDisableFieldList();
    
    boolean getShowCalcMbrs();
    
    XmlBoolean xgetShowCalcMbrs();
    
    boolean isSetShowCalcMbrs();
    
    void setShowCalcMbrs(final boolean p0);
    
    void xsetShowCalcMbrs(final XmlBoolean p0);
    
    void unsetShowCalcMbrs();
    
    boolean getVisualTotals();
    
    XmlBoolean xgetVisualTotals();
    
    boolean isSetVisualTotals();
    
    void setVisualTotals(final boolean p0);
    
    void xsetVisualTotals(final XmlBoolean p0);
    
    void unsetVisualTotals();
    
    boolean getShowMultipleLabel();
    
    XmlBoolean xgetShowMultipleLabel();
    
    boolean isSetShowMultipleLabel();
    
    void setShowMultipleLabel(final boolean p0);
    
    void xsetShowMultipleLabel(final XmlBoolean p0);
    
    void unsetShowMultipleLabel();
    
    boolean getShowDataDropDown();
    
    XmlBoolean xgetShowDataDropDown();
    
    boolean isSetShowDataDropDown();
    
    void setShowDataDropDown(final boolean p0);
    
    void xsetShowDataDropDown(final XmlBoolean p0);
    
    void unsetShowDataDropDown();
    
    boolean getShowDrill();
    
    XmlBoolean xgetShowDrill();
    
    boolean isSetShowDrill();
    
    void setShowDrill(final boolean p0);
    
    void xsetShowDrill(final XmlBoolean p0);
    
    void unsetShowDrill();
    
    boolean getPrintDrill();
    
    XmlBoolean xgetPrintDrill();
    
    boolean isSetPrintDrill();
    
    void setPrintDrill(final boolean p0);
    
    void xsetPrintDrill(final XmlBoolean p0);
    
    void unsetPrintDrill();
    
    boolean getShowMemberPropertyTips();
    
    XmlBoolean xgetShowMemberPropertyTips();
    
    boolean isSetShowMemberPropertyTips();
    
    void setShowMemberPropertyTips(final boolean p0);
    
    void xsetShowMemberPropertyTips(final XmlBoolean p0);
    
    void unsetShowMemberPropertyTips();
    
    boolean getShowDataTips();
    
    XmlBoolean xgetShowDataTips();
    
    boolean isSetShowDataTips();
    
    void setShowDataTips(final boolean p0);
    
    void xsetShowDataTips(final XmlBoolean p0);
    
    void unsetShowDataTips();
    
    boolean getEnableWizard();
    
    XmlBoolean xgetEnableWizard();
    
    boolean isSetEnableWizard();
    
    void setEnableWizard(final boolean p0);
    
    void xsetEnableWizard(final XmlBoolean p0);
    
    void unsetEnableWizard();
    
    boolean getEnableDrill();
    
    XmlBoolean xgetEnableDrill();
    
    boolean isSetEnableDrill();
    
    void setEnableDrill(final boolean p0);
    
    void xsetEnableDrill(final XmlBoolean p0);
    
    void unsetEnableDrill();
    
    boolean getEnableFieldProperties();
    
    XmlBoolean xgetEnableFieldProperties();
    
    boolean isSetEnableFieldProperties();
    
    void setEnableFieldProperties(final boolean p0);
    
    void xsetEnableFieldProperties(final XmlBoolean p0);
    
    void unsetEnableFieldProperties();
    
    boolean getPreserveFormatting();
    
    XmlBoolean xgetPreserveFormatting();
    
    boolean isSetPreserveFormatting();
    
    void setPreserveFormatting(final boolean p0);
    
    void xsetPreserveFormatting(final XmlBoolean p0);
    
    void unsetPreserveFormatting();
    
    boolean getUseAutoFormatting();
    
    XmlBoolean xgetUseAutoFormatting();
    
    boolean isSetUseAutoFormatting();
    
    void setUseAutoFormatting(final boolean p0);
    
    void xsetUseAutoFormatting(final XmlBoolean p0);
    
    void unsetUseAutoFormatting();
    
    long getPageWrap();
    
    XmlUnsignedInt xgetPageWrap();
    
    boolean isSetPageWrap();
    
    void setPageWrap(final long p0);
    
    void xsetPageWrap(final XmlUnsignedInt p0);
    
    void unsetPageWrap();
    
    boolean getPageOverThenDown();
    
    XmlBoolean xgetPageOverThenDown();
    
    boolean isSetPageOverThenDown();
    
    void setPageOverThenDown(final boolean p0);
    
    void xsetPageOverThenDown(final XmlBoolean p0);
    
    void unsetPageOverThenDown();
    
    boolean getSubtotalHiddenItems();
    
    XmlBoolean xgetSubtotalHiddenItems();
    
    boolean isSetSubtotalHiddenItems();
    
    void setSubtotalHiddenItems(final boolean p0);
    
    void xsetSubtotalHiddenItems(final XmlBoolean p0);
    
    void unsetSubtotalHiddenItems();
    
    boolean getRowGrandTotals();
    
    XmlBoolean xgetRowGrandTotals();
    
    boolean isSetRowGrandTotals();
    
    void setRowGrandTotals(final boolean p0);
    
    void xsetRowGrandTotals(final XmlBoolean p0);
    
    void unsetRowGrandTotals();
    
    boolean getColGrandTotals();
    
    XmlBoolean xgetColGrandTotals();
    
    boolean isSetColGrandTotals();
    
    void setColGrandTotals(final boolean p0);
    
    void xsetColGrandTotals(final XmlBoolean p0);
    
    void unsetColGrandTotals();
    
    boolean getFieldPrintTitles();
    
    XmlBoolean xgetFieldPrintTitles();
    
    boolean isSetFieldPrintTitles();
    
    void setFieldPrintTitles(final boolean p0);
    
    void xsetFieldPrintTitles(final XmlBoolean p0);
    
    void unsetFieldPrintTitles();
    
    boolean getItemPrintTitles();
    
    XmlBoolean xgetItemPrintTitles();
    
    boolean isSetItemPrintTitles();
    
    void setItemPrintTitles(final boolean p0);
    
    void xsetItemPrintTitles(final XmlBoolean p0);
    
    void unsetItemPrintTitles();
    
    boolean getMergeItem();
    
    XmlBoolean xgetMergeItem();
    
    boolean isSetMergeItem();
    
    void setMergeItem(final boolean p0);
    
    void xsetMergeItem(final XmlBoolean p0);
    
    void unsetMergeItem();
    
    boolean getShowDropZones();
    
    XmlBoolean xgetShowDropZones();
    
    boolean isSetShowDropZones();
    
    void setShowDropZones(final boolean p0);
    
    void xsetShowDropZones(final XmlBoolean p0);
    
    void unsetShowDropZones();
    
    short getCreatedVersion();
    
    XmlUnsignedByte xgetCreatedVersion();
    
    boolean isSetCreatedVersion();
    
    void setCreatedVersion(final short p0);
    
    void xsetCreatedVersion(final XmlUnsignedByte p0);
    
    void unsetCreatedVersion();
    
    long getIndent();
    
    XmlUnsignedInt xgetIndent();
    
    boolean isSetIndent();
    
    void setIndent(final long p0);
    
    void xsetIndent(final XmlUnsignedInt p0);
    
    void unsetIndent();
    
    boolean getShowEmptyRow();
    
    XmlBoolean xgetShowEmptyRow();
    
    boolean isSetShowEmptyRow();
    
    void setShowEmptyRow(final boolean p0);
    
    void xsetShowEmptyRow(final XmlBoolean p0);
    
    void unsetShowEmptyRow();
    
    boolean getShowEmptyCol();
    
    XmlBoolean xgetShowEmptyCol();
    
    boolean isSetShowEmptyCol();
    
    void setShowEmptyCol(final boolean p0);
    
    void xsetShowEmptyCol(final XmlBoolean p0);
    
    void unsetShowEmptyCol();
    
    boolean getShowHeaders();
    
    XmlBoolean xgetShowHeaders();
    
    boolean isSetShowHeaders();
    
    void setShowHeaders(final boolean p0);
    
    void xsetShowHeaders(final XmlBoolean p0);
    
    void unsetShowHeaders();
    
    boolean getCompact();
    
    XmlBoolean xgetCompact();
    
    boolean isSetCompact();
    
    void setCompact(final boolean p0);
    
    void xsetCompact(final XmlBoolean p0);
    
    void unsetCompact();
    
    boolean getOutline();
    
    XmlBoolean xgetOutline();
    
    boolean isSetOutline();
    
    void setOutline(final boolean p0);
    
    void xsetOutline(final XmlBoolean p0);
    
    void unsetOutline();
    
    boolean getOutlineData();
    
    XmlBoolean xgetOutlineData();
    
    boolean isSetOutlineData();
    
    void setOutlineData(final boolean p0);
    
    void xsetOutlineData(final XmlBoolean p0);
    
    void unsetOutlineData();
    
    boolean getCompactData();
    
    XmlBoolean xgetCompactData();
    
    boolean isSetCompactData();
    
    void setCompactData(final boolean p0);
    
    void xsetCompactData(final XmlBoolean p0);
    
    void unsetCompactData();
    
    boolean getPublished();
    
    XmlBoolean xgetPublished();
    
    boolean isSetPublished();
    
    void setPublished(final boolean p0);
    
    void xsetPublished(final XmlBoolean p0);
    
    void unsetPublished();
    
    boolean getGridDropZones();
    
    XmlBoolean xgetGridDropZones();
    
    boolean isSetGridDropZones();
    
    void setGridDropZones(final boolean p0);
    
    void xsetGridDropZones(final XmlBoolean p0);
    
    void unsetGridDropZones();
    
    boolean getImmersive();
    
    XmlBoolean xgetImmersive();
    
    boolean isSetImmersive();
    
    void setImmersive(final boolean p0);
    
    void xsetImmersive(final XmlBoolean p0);
    
    void unsetImmersive();
    
    boolean getMultipleFieldFilters();
    
    XmlBoolean xgetMultipleFieldFilters();
    
    boolean isSetMultipleFieldFilters();
    
    void setMultipleFieldFilters(final boolean p0);
    
    void xsetMultipleFieldFilters(final XmlBoolean p0);
    
    void unsetMultipleFieldFilters();
    
    long getChartFormat();
    
    XmlUnsignedInt xgetChartFormat();
    
    boolean isSetChartFormat();
    
    void setChartFormat(final long p0);
    
    void xsetChartFormat(final XmlUnsignedInt p0);
    
    void unsetChartFormat();
    
    String getRowHeaderCaption();
    
    STXstring xgetRowHeaderCaption();
    
    boolean isSetRowHeaderCaption();
    
    void setRowHeaderCaption(final String p0);
    
    void xsetRowHeaderCaption(final STXstring p0);
    
    void unsetRowHeaderCaption();
    
    String getColHeaderCaption();
    
    STXstring xgetColHeaderCaption();
    
    boolean isSetColHeaderCaption();
    
    void setColHeaderCaption(final String p0);
    
    void xsetColHeaderCaption(final STXstring p0);
    
    void unsetColHeaderCaption();
    
    boolean getFieldListSortAscending();
    
    XmlBoolean xgetFieldListSortAscending();
    
    boolean isSetFieldListSortAscending();
    
    void setFieldListSortAscending(final boolean p0);
    
    void xsetFieldListSortAscending(final XmlBoolean p0);
    
    void unsetFieldListSortAscending();
    
    boolean getMdxSubqueries();
    
    XmlBoolean xgetMdxSubqueries();
    
    boolean isSetMdxSubqueries();
    
    void setMdxSubqueries(final boolean p0);
    
    void xsetMdxSubqueries(final XmlBoolean p0);
    
    void unsetMdxSubqueries();
    
    boolean getCustomListSort();
    
    XmlBoolean xgetCustomListSort();
    
    boolean isSetCustomListSort();
    
    void setCustomListSort(final boolean p0);
    
    void xsetCustomListSort(final XmlBoolean p0);
    
    void unsetCustomListSort();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPivotTableDefinition.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPivotTableDefinition newInstance() {
            return (CTPivotTableDefinition)getTypeLoader().newInstance(CTPivotTableDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotTableDefinition newInstance(final XmlOptions xmlOptions) {
            return (CTPivotTableDefinition)getTypeLoader().newInstance(CTPivotTableDefinition.type, xmlOptions);
        }
        
        public static CTPivotTableDefinition parse(final String s) throws XmlException {
            return (CTPivotTableDefinition)getTypeLoader().parse(s, CTPivotTableDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotTableDefinition parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotTableDefinition)getTypeLoader().parse(s, CTPivotTableDefinition.type, xmlOptions);
        }
        
        public static CTPivotTableDefinition parse(final File file) throws XmlException, IOException {
            return (CTPivotTableDefinition)getTypeLoader().parse(file, CTPivotTableDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotTableDefinition parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotTableDefinition)getTypeLoader().parse(file, CTPivotTableDefinition.type, xmlOptions);
        }
        
        public static CTPivotTableDefinition parse(final URL url) throws XmlException, IOException {
            return (CTPivotTableDefinition)getTypeLoader().parse(url, CTPivotTableDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotTableDefinition parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotTableDefinition)getTypeLoader().parse(url, CTPivotTableDefinition.type, xmlOptions);
        }
        
        public static CTPivotTableDefinition parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPivotTableDefinition)getTypeLoader().parse(inputStream, CTPivotTableDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotTableDefinition parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotTableDefinition)getTypeLoader().parse(inputStream, CTPivotTableDefinition.type, xmlOptions);
        }
        
        public static CTPivotTableDefinition parse(final Reader reader) throws XmlException, IOException {
            return (CTPivotTableDefinition)getTypeLoader().parse(reader, CTPivotTableDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotTableDefinition parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotTableDefinition)getTypeLoader().parse(reader, CTPivotTableDefinition.type, xmlOptions);
        }
        
        public static CTPivotTableDefinition parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPivotTableDefinition)getTypeLoader().parse(xmlStreamReader, CTPivotTableDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotTableDefinition parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotTableDefinition)getTypeLoader().parse(xmlStreamReader, CTPivotTableDefinition.type, xmlOptions);
        }
        
        public static CTPivotTableDefinition parse(final Node node) throws XmlException {
            return (CTPivotTableDefinition)getTypeLoader().parse(node, CTPivotTableDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotTableDefinition parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotTableDefinition)getTypeLoader().parse(node, CTPivotTableDefinition.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPivotTableDefinition parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPivotTableDefinition)getTypeLoader().parse(xmlInputStream, CTPivotTableDefinition.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPivotTableDefinition parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPivotTableDefinition)getTypeLoader().parse(xmlInputStream, CTPivotTableDefinition.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotTableDefinition.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotTableDefinition.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
