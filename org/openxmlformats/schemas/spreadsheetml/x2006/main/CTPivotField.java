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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPivotField extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPivotField.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpivotfieldf961type");
    
    CTItems getItems();
    
    boolean isSetItems();
    
    void setItems(final CTItems p0);
    
    CTItems addNewItems();
    
    void unsetItems();
    
    CTAutoSortScope getAutoSortScope();
    
    boolean isSetAutoSortScope();
    
    void setAutoSortScope(final CTAutoSortScope p0);
    
    CTAutoSortScope addNewAutoSortScope();
    
    void unsetAutoSortScope();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    String getName();
    
    STXstring xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final STXstring p0);
    
    void unsetName();
    
    STAxis.Enum getAxis();
    
    STAxis xgetAxis();
    
    boolean isSetAxis();
    
    void setAxis(final STAxis.Enum p0);
    
    void xsetAxis(final STAxis p0);
    
    void unsetAxis();
    
    boolean getDataField();
    
    XmlBoolean xgetDataField();
    
    boolean isSetDataField();
    
    void setDataField(final boolean p0);
    
    void xsetDataField(final XmlBoolean p0);
    
    void unsetDataField();
    
    String getSubtotalCaption();
    
    STXstring xgetSubtotalCaption();
    
    boolean isSetSubtotalCaption();
    
    void setSubtotalCaption(final String p0);
    
    void xsetSubtotalCaption(final STXstring p0);
    
    void unsetSubtotalCaption();
    
    boolean getShowDropDowns();
    
    XmlBoolean xgetShowDropDowns();
    
    boolean isSetShowDropDowns();
    
    void setShowDropDowns(final boolean p0);
    
    void xsetShowDropDowns(final XmlBoolean p0);
    
    void unsetShowDropDowns();
    
    boolean getHiddenLevel();
    
    XmlBoolean xgetHiddenLevel();
    
    boolean isSetHiddenLevel();
    
    void setHiddenLevel(final boolean p0);
    
    void xsetHiddenLevel(final XmlBoolean p0);
    
    void unsetHiddenLevel();
    
    String getUniqueMemberProperty();
    
    STXstring xgetUniqueMemberProperty();
    
    boolean isSetUniqueMemberProperty();
    
    void setUniqueMemberProperty(final String p0);
    
    void xsetUniqueMemberProperty(final STXstring p0);
    
    void unsetUniqueMemberProperty();
    
    boolean getCompact();
    
    XmlBoolean xgetCompact();
    
    boolean isSetCompact();
    
    void setCompact(final boolean p0);
    
    void xsetCompact(final XmlBoolean p0);
    
    void unsetCompact();
    
    boolean getAllDrilled();
    
    XmlBoolean xgetAllDrilled();
    
    boolean isSetAllDrilled();
    
    void setAllDrilled(final boolean p0);
    
    void xsetAllDrilled(final XmlBoolean p0);
    
    void unsetAllDrilled();
    
    long getNumFmtId();
    
    STNumFmtId xgetNumFmtId();
    
    boolean isSetNumFmtId();
    
    void setNumFmtId(final long p0);
    
    void xsetNumFmtId(final STNumFmtId p0);
    
    void unsetNumFmtId();
    
    boolean getOutline();
    
    XmlBoolean xgetOutline();
    
    boolean isSetOutline();
    
    void setOutline(final boolean p0);
    
    void xsetOutline(final XmlBoolean p0);
    
    void unsetOutline();
    
    boolean getSubtotalTop();
    
    XmlBoolean xgetSubtotalTop();
    
    boolean isSetSubtotalTop();
    
    void setSubtotalTop(final boolean p0);
    
    void xsetSubtotalTop(final XmlBoolean p0);
    
    void unsetSubtotalTop();
    
    boolean getDragToRow();
    
    XmlBoolean xgetDragToRow();
    
    boolean isSetDragToRow();
    
    void setDragToRow(final boolean p0);
    
    void xsetDragToRow(final XmlBoolean p0);
    
    void unsetDragToRow();
    
    boolean getDragToCol();
    
    XmlBoolean xgetDragToCol();
    
    boolean isSetDragToCol();
    
    void setDragToCol(final boolean p0);
    
    void xsetDragToCol(final XmlBoolean p0);
    
    void unsetDragToCol();
    
    boolean getMultipleItemSelectionAllowed();
    
    XmlBoolean xgetMultipleItemSelectionAllowed();
    
    boolean isSetMultipleItemSelectionAllowed();
    
    void setMultipleItemSelectionAllowed(final boolean p0);
    
    void xsetMultipleItemSelectionAllowed(final XmlBoolean p0);
    
    void unsetMultipleItemSelectionAllowed();
    
    boolean getDragToPage();
    
    XmlBoolean xgetDragToPage();
    
    boolean isSetDragToPage();
    
    void setDragToPage(final boolean p0);
    
    void xsetDragToPage(final XmlBoolean p0);
    
    void unsetDragToPage();
    
    boolean getDragToData();
    
    XmlBoolean xgetDragToData();
    
    boolean isSetDragToData();
    
    void setDragToData(final boolean p0);
    
    void xsetDragToData(final XmlBoolean p0);
    
    void unsetDragToData();
    
    boolean getDragOff();
    
    XmlBoolean xgetDragOff();
    
    boolean isSetDragOff();
    
    void setDragOff(final boolean p0);
    
    void xsetDragOff(final XmlBoolean p0);
    
    void unsetDragOff();
    
    boolean getShowAll();
    
    XmlBoolean xgetShowAll();
    
    boolean isSetShowAll();
    
    void setShowAll(final boolean p0);
    
    void xsetShowAll(final XmlBoolean p0);
    
    void unsetShowAll();
    
    boolean getInsertBlankRow();
    
    XmlBoolean xgetInsertBlankRow();
    
    boolean isSetInsertBlankRow();
    
    void setInsertBlankRow(final boolean p0);
    
    void xsetInsertBlankRow(final XmlBoolean p0);
    
    void unsetInsertBlankRow();
    
    boolean getServerField();
    
    XmlBoolean xgetServerField();
    
    boolean isSetServerField();
    
    void setServerField(final boolean p0);
    
    void xsetServerField(final XmlBoolean p0);
    
    void unsetServerField();
    
    boolean getInsertPageBreak();
    
    XmlBoolean xgetInsertPageBreak();
    
    boolean isSetInsertPageBreak();
    
    void setInsertPageBreak(final boolean p0);
    
    void xsetInsertPageBreak(final XmlBoolean p0);
    
    void unsetInsertPageBreak();
    
    boolean getAutoShow();
    
    XmlBoolean xgetAutoShow();
    
    boolean isSetAutoShow();
    
    void setAutoShow(final boolean p0);
    
    void xsetAutoShow(final XmlBoolean p0);
    
    void unsetAutoShow();
    
    boolean getTopAutoShow();
    
    XmlBoolean xgetTopAutoShow();
    
    boolean isSetTopAutoShow();
    
    void setTopAutoShow(final boolean p0);
    
    void xsetTopAutoShow(final XmlBoolean p0);
    
    void unsetTopAutoShow();
    
    boolean getHideNewItems();
    
    XmlBoolean xgetHideNewItems();
    
    boolean isSetHideNewItems();
    
    void setHideNewItems(final boolean p0);
    
    void xsetHideNewItems(final XmlBoolean p0);
    
    void unsetHideNewItems();
    
    boolean getMeasureFilter();
    
    XmlBoolean xgetMeasureFilter();
    
    boolean isSetMeasureFilter();
    
    void setMeasureFilter(final boolean p0);
    
    void xsetMeasureFilter(final XmlBoolean p0);
    
    void unsetMeasureFilter();
    
    boolean getIncludeNewItemsInFilter();
    
    XmlBoolean xgetIncludeNewItemsInFilter();
    
    boolean isSetIncludeNewItemsInFilter();
    
    void setIncludeNewItemsInFilter(final boolean p0);
    
    void xsetIncludeNewItemsInFilter(final XmlBoolean p0);
    
    void unsetIncludeNewItemsInFilter();
    
    long getItemPageCount();
    
    XmlUnsignedInt xgetItemPageCount();
    
    boolean isSetItemPageCount();
    
    void setItemPageCount(final long p0);
    
    void xsetItemPageCount(final XmlUnsignedInt p0);
    
    void unsetItemPageCount();
    
    STFieldSortType.Enum getSortType();
    
    STFieldSortType xgetSortType();
    
    boolean isSetSortType();
    
    void setSortType(final STFieldSortType.Enum p0);
    
    void xsetSortType(final STFieldSortType p0);
    
    void unsetSortType();
    
    boolean getDataSourceSort();
    
    XmlBoolean xgetDataSourceSort();
    
    boolean isSetDataSourceSort();
    
    void setDataSourceSort(final boolean p0);
    
    void xsetDataSourceSort(final XmlBoolean p0);
    
    void unsetDataSourceSort();
    
    boolean getNonAutoSortDefault();
    
    XmlBoolean xgetNonAutoSortDefault();
    
    boolean isSetNonAutoSortDefault();
    
    void setNonAutoSortDefault(final boolean p0);
    
    void xsetNonAutoSortDefault(final XmlBoolean p0);
    
    void unsetNonAutoSortDefault();
    
    long getRankBy();
    
    XmlUnsignedInt xgetRankBy();
    
    boolean isSetRankBy();
    
    void setRankBy(final long p0);
    
    void xsetRankBy(final XmlUnsignedInt p0);
    
    void unsetRankBy();
    
    boolean getDefaultSubtotal();
    
    XmlBoolean xgetDefaultSubtotal();
    
    boolean isSetDefaultSubtotal();
    
    void setDefaultSubtotal(final boolean p0);
    
    void xsetDefaultSubtotal(final XmlBoolean p0);
    
    void unsetDefaultSubtotal();
    
    boolean getSumSubtotal();
    
    XmlBoolean xgetSumSubtotal();
    
    boolean isSetSumSubtotal();
    
    void setSumSubtotal(final boolean p0);
    
    void xsetSumSubtotal(final XmlBoolean p0);
    
    void unsetSumSubtotal();
    
    boolean getCountASubtotal();
    
    XmlBoolean xgetCountASubtotal();
    
    boolean isSetCountASubtotal();
    
    void setCountASubtotal(final boolean p0);
    
    void xsetCountASubtotal(final XmlBoolean p0);
    
    void unsetCountASubtotal();
    
    boolean getAvgSubtotal();
    
    XmlBoolean xgetAvgSubtotal();
    
    boolean isSetAvgSubtotal();
    
    void setAvgSubtotal(final boolean p0);
    
    void xsetAvgSubtotal(final XmlBoolean p0);
    
    void unsetAvgSubtotal();
    
    boolean getMaxSubtotal();
    
    XmlBoolean xgetMaxSubtotal();
    
    boolean isSetMaxSubtotal();
    
    void setMaxSubtotal(final boolean p0);
    
    void xsetMaxSubtotal(final XmlBoolean p0);
    
    void unsetMaxSubtotal();
    
    boolean getMinSubtotal();
    
    XmlBoolean xgetMinSubtotal();
    
    boolean isSetMinSubtotal();
    
    void setMinSubtotal(final boolean p0);
    
    void xsetMinSubtotal(final XmlBoolean p0);
    
    void unsetMinSubtotal();
    
    boolean getProductSubtotal();
    
    XmlBoolean xgetProductSubtotal();
    
    boolean isSetProductSubtotal();
    
    void setProductSubtotal(final boolean p0);
    
    void xsetProductSubtotal(final XmlBoolean p0);
    
    void unsetProductSubtotal();
    
    boolean getCountSubtotal();
    
    XmlBoolean xgetCountSubtotal();
    
    boolean isSetCountSubtotal();
    
    void setCountSubtotal(final boolean p0);
    
    void xsetCountSubtotal(final XmlBoolean p0);
    
    void unsetCountSubtotal();
    
    boolean getStdDevSubtotal();
    
    XmlBoolean xgetStdDevSubtotal();
    
    boolean isSetStdDevSubtotal();
    
    void setStdDevSubtotal(final boolean p0);
    
    void xsetStdDevSubtotal(final XmlBoolean p0);
    
    void unsetStdDevSubtotal();
    
    boolean getStdDevPSubtotal();
    
    XmlBoolean xgetStdDevPSubtotal();
    
    boolean isSetStdDevPSubtotal();
    
    void setStdDevPSubtotal(final boolean p0);
    
    void xsetStdDevPSubtotal(final XmlBoolean p0);
    
    void unsetStdDevPSubtotal();
    
    boolean getVarSubtotal();
    
    XmlBoolean xgetVarSubtotal();
    
    boolean isSetVarSubtotal();
    
    void setVarSubtotal(final boolean p0);
    
    void xsetVarSubtotal(final XmlBoolean p0);
    
    void unsetVarSubtotal();
    
    boolean getVarPSubtotal();
    
    XmlBoolean xgetVarPSubtotal();
    
    boolean isSetVarPSubtotal();
    
    void setVarPSubtotal(final boolean p0);
    
    void xsetVarPSubtotal(final XmlBoolean p0);
    
    void unsetVarPSubtotal();
    
    boolean getShowPropCell();
    
    XmlBoolean xgetShowPropCell();
    
    boolean isSetShowPropCell();
    
    void setShowPropCell(final boolean p0);
    
    void xsetShowPropCell(final XmlBoolean p0);
    
    void unsetShowPropCell();
    
    boolean getShowPropTip();
    
    XmlBoolean xgetShowPropTip();
    
    boolean isSetShowPropTip();
    
    void setShowPropTip(final boolean p0);
    
    void xsetShowPropTip(final XmlBoolean p0);
    
    void unsetShowPropTip();
    
    boolean getShowPropAsCaption();
    
    XmlBoolean xgetShowPropAsCaption();
    
    boolean isSetShowPropAsCaption();
    
    void setShowPropAsCaption(final boolean p0);
    
    void xsetShowPropAsCaption(final XmlBoolean p0);
    
    void unsetShowPropAsCaption();
    
    boolean getDefaultAttributeDrillState();
    
    XmlBoolean xgetDefaultAttributeDrillState();
    
    boolean isSetDefaultAttributeDrillState();
    
    void setDefaultAttributeDrillState(final boolean p0);
    
    void xsetDefaultAttributeDrillState(final XmlBoolean p0);
    
    void unsetDefaultAttributeDrillState();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPivotField.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPivotField newInstance() {
            return (CTPivotField)getTypeLoader().newInstance(CTPivotField.type, (XmlOptions)null);
        }
        
        public static CTPivotField newInstance(final XmlOptions xmlOptions) {
            return (CTPivotField)getTypeLoader().newInstance(CTPivotField.type, xmlOptions);
        }
        
        public static CTPivotField parse(final String s) throws XmlException {
            return (CTPivotField)getTypeLoader().parse(s, CTPivotField.type, (XmlOptions)null);
        }
        
        public static CTPivotField parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotField)getTypeLoader().parse(s, CTPivotField.type, xmlOptions);
        }
        
        public static CTPivotField parse(final File file) throws XmlException, IOException {
            return (CTPivotField)getTypeLoader().parse(file, CTPivotField.type, (XmlOptions)null);
        }
        
        public static CTPivotField parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotField)getTypeLoader().parse(file, CTPivotField.type, xmlOptions);
        }
        
        public static CTPivotField parse(final URL url) throws XmlException, IOException {
            return (CTPivotField)getTypeLoader().parse(url, CTPivotField.type, (XmlOptions)null);
        }
        
        public static CTPivotField parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotField)getTypeLoader().parse(url, CTPivotField.type, xmlOptions);
        }
        
        public static CTPivotField parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPivotField)getTypeLoader().parse(inputStream, CTPivotField.type, (XmlOptions)null);
        }
        
        public static CTPivotField parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotField)getTypeLoader().parse(inputStream, CTPivotField.type, xmlOptions);
        }
        
        public static CTPivotField parse(final Reader reader) throws XmlException, IOException {
            return (CTPivotField)getTypeLoader().parse(reader, CTPivotField.type, (XmlOptions)null);
        }
        
        public static CTPivotField parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotField)getTypeLoader().parse(reader, CTPivotField.type, xmlOptions);
        }
        
        public static CTPivotField parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPivotField)getTypeLoader().parse(xmlStreamReader, CTPivotField.type, (XmlOptions)null);
        }
        
        public static CTPivotField parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotField)getTypeLoader().parse(xmlStreamReader, CTPivotField.type, xmlOptions);
        }
        
        public static CTPivotField parse(final Node node) throws XmlException {
            return (CTPivotField)getTypeLoader().parse(node, CTPivotField.type, (XmlOptions)null);
        }
        
        public static CTPivotField parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotField)getTypeLoader().parse(node, CTPivotField.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPivotField parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPivotField)getTypeLoader().parse(xmlInputStream, CTPivotField.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPivotField parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPivotField)getTypeLoader().parse(xmlInputStream, CTPivotField.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotField.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotField.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
