package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheetSource;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheSource;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCacheDefinition;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSourceType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageField;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLocation;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.SpreadsheetVersion;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataField;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataConsolidateFunction;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColFields;
import org.apache.poi.ss.usermodel.DataFormat;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTField;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRowFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTItems;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STItemType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STAxis;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotField;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotTableStyle;
import java.io.OutputStream;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlException;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.Sheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotTableDefinition;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XSSFPivotTable extends POIXMLDocumentPart
{
    protected static final short CREATED_VERSION = 3;
    protected static final short MIN_REFRESHABLE_VERSION = 3;
    protected static final short UPDATED_VERSION = 3;
    private CTPivotTableDefinition pivotTableDefinition;
    private XSSFPivotCacheDefinition pivotCacheDefinition;
    private XSSFPivotCache pivotCache;
    private XSSFPivotCacheRecords pivotCacheRecords;
    private Sheet parentSheet;
    private Sheet dataSheet;
    
    protected XSSFPivotTable() {
        this.pivotTableDefinition = CTPivotTableDefinition.Factory.newInstance();
        this.pivotCache = new XSSFPivotCache();
        this.pivotCacheDefinition = new XSSFPivotCacheDefinition();
        this.pivotCacheRecords = new XSSFPivotCacheRecords();
    }
    
    protected XSSFPivotTable(final PackagePart part) throws IOException {
        super(part);
        this.readFrom(part.getInputStream());
    }
    
    public void readFrom(final InputStream is) throws IOException {
        try {
            final XmlOptions options = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            options.setLoadReplaceDocumentElement((QName)null);
            this.pivotTableDefinition = CTPivotTableDefinition.Factory.parse(is, options);
        }
        catch (final XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }
    
    public void setPivotCache(final XSSFPivotCache pivotCache) {
        this.pivotCache = pivotCache;
    }
    
    public XSSFPivotCache getPivotCache() {
        return this.pivotCache;
    }
    
    public Sheet getParentSheet() {
        return this.parentSheet;
    }
    
    public void setParentSheet(final XSSFSheet parentSheet) {
        this.parentSheet = (Sheet)parentSheet;
    }
    
    @Internal
    public CTPivotTableDefinition getCTPivotTableDefinition() {
        return this.pivotTableDefinition;
    }
    
    @Internal
    public void setCTPivotTableDefinition(final CTPivotTableDefinition pivotTableDefinition) {
        this.pivotTableDefinition = pivotTableDefinition;
    }
    
    public XSSFPivotCacheDefinition getPivotCacheDefinition() {
        return this.pivotCacheDefinition;
    }
    
    public void setPivotCacheDefinition(final XSSFPivotCacheDefinition pivotCacheDefinition) {
        this.pivotCacheDefinition = pivotCacheDefinition;
    }
    
    public XSSFPivotCacheRecords getPivotCacheRecords() {
        return this.pivotCacheRecords;
    }
    
    public void setPivotCacheRecords(final XSSFPivotCacheRecords pivotCacheRecords) {
        this.pivotCacheRecords = pivotCacheRecords;
    }
    
    public Sheet getDataSheet() {
        return this.dataSheet;
    }
    
    private void setDataSheet(final Sheet dataSheet) {
        this.dataSheet = dataSheet;
    }
    
    @Override
    protected void commit() throws IOException {
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTPivotTableDefinition.type.getName().getNamespaceURI(), "pivotTableDefinition"));
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.pivotTableDefinition.save(out, xmlOptions);
        out.close();
    }
    
    protected void setDefaultPivotTableDefinition() {
        this.pivotTableDefinition.setMultipleFieldFilters(false);
        this.pivotTableDefinition.setIndent(0L);
        this.pivotTableDefinition.setCreatedVersion((short)3);
        this.pivotTableDefinition.setMinRefreshableVersion((short)3);
        this.pivotTableDefinition.setUpdatedVersion((short)3);
        this.pivotTableDefinition.setItemPrintTitles(true);
        this.pivotTableDefinition.setUseAutoFormatting(true);
        this.pivotTableDefinition.setApplyNumberFormats(false);
        this.pivotTableDefinition.setApplyWidthHeightFormats(true);
        this.pivotTableDefinition.setApplyAlignmentFormats(false);
        this.pivotTableDefinition.setApplyPatternFormats(false);
        this.pivotTableDefinition.setApplyFontFormats(false);
        this.pivotTableDefinition.setApplyBorderFormats(false);
        this.pivotTableDefinition.setCacheId(this.pivotCache.getCTPivotCache().getCacheId());
        this.pivotTableDefinition.setName("PivotTable" + this.pivotTableDefinition.getCacheId());
        this.pivotTableDefinition.setDataCaption("Values");
        final CTPivotTableStyle style = this.pivotTableDefinition.addNewPivotTableStyleInfo();
        style.setName("PivotStyleLight16");
        style.setShowLastColumn(true);
        style.setShowColStripes(false);
        style.setShowRowStripes(false);
        style.setShowColHeaders(true);
        style.setShowRowHeaders(true);
    }
    
    protected AreaReference getPivotArea() {
        final Workbook wb = this.getDataSheet().getWorkbook();
        return this.getPivotCacheDefinition().getPivotArea(wb);
    }
    
    private void checkColumnIndex(final int columnIndex) throws IndexOutOfBoundsException {
        final AreaReference pivotArea = this.getPivotArea();
        final int size = pivotArea.getLastCell().getCol() - pivotArea.getFirstCell().getCol() + 1;
        if (columnIndex < 0 || columnIndex >= size) {
            throw new IndexOutOfBoundsException("Column Index: " + columnIndex + ", Size: " + size);
        }
    }
    
    public void addRowLabel(final int columnIndex) {
        this.checkColumnIndex(columnIndex);
        final AreaReference pivotArea = this.getPivotArea();
        final int lastRowIndex = pivotArea.getLastCell().getRow() - pivotArea.getFirstCell().getRow();
        final CTPivotFields pivotFields = this.pivotTableDefinition.getPivotFields();
        final CTPivotField pivotField = CTPivotField.Factory.newInstance();
        final CTItems items = pivotField.addNewItems();
        pivotField.setAxis(STAxis.AXIS_ROW);
        pivotField.setShowAll(false);
        for (int i = 0; i <= lastRowIndex; ++i) {
            items.addNewItem().setT(STItemType.DEFAULT);
        }
        items.setCount((long)items.sizeOfItemArray());
        pivotFields.setPivotFieldArray(columnIndex, pivotField);
        CTRowFields rowFields;
        if (this.pivotTableDefinition.getRowFields() != null) {
            rowFields = this.pivotTableDefinition.getRowFields();
        }
        else {
            rowFields = this.pivotTableDefinition.addNewRowFields();
        }
        rowFields.addNewField().setX(columnIndex);
        rowFields.setCount((long)rowFields.sizeOfFieldArray());
    }
    
    public List<Integer> getRowLabelColumns() {
        if (this.pivotTableDefinition.getRowFields() != null) {
            final List<Integer> columnIndexes = new ArrayList<Integer>();
            for (final CTField f : this.pivotTableDefinition.getRowFields().getFieldArray()) {
                columnIndexes.add(f.getX());
            }
            return columnIndexes;
        }
        return Collections.emptyList();
    }
    
    public void addColLabel(final int columnIndex, final String valueFormat) {
        this.checkColumnIndex(columnIndex);
        final AreaReference pivotArea = this.getPivotArea();
        final int lastRowIndex = pivotArea.getLastCell().getRow() - pivotArea.getFirstCell().getRow();
        final CTPivotFields pivotFields = this.pivotTableDefinition.getPivotFields();
        final CTPivotField pivotField = CTPivotField.Factory.newInstance();
        final CTItems items = pivotField.addNewItems();
        pivotField.setAxis(STAxis.AXIS_COL);
        pivotField.setShowAll(false);
        if (valueFormat != null && !valueFormat.trim().isEmpty()) {
            final DataFormat df = this.parentSheet.getWorkbook().createDataFormat();
            pivotField.setNumFmtId((long)df.getFormat(valueFormat));
        }
        for (int i = 0; i <= lastRowIndex; ++i) {
            items.addNewItem().setT(STItemType.DEFAULT);
        }
        items.setCount((long)items.sizeOfItemArray());
        pivotFields.setPivotFieldArray(columnIndex, pivotField);
        CTColFields colFields;
        if (this.pivotTableDefinition.getColFields() != null) {
            colFields = this.pivotTableDefinition.getColFields();
        }
        else {
            colFields = this.pivotTableDefinition.addNewColFields();
        }
        colFields.addNewField().setX(columnIndex);
        colFields.setCount((long)colFields.sizeOfFieldArray());
    }
    
    public void addColLabel(final int columnIndex) {
        this.addColLabel(columnIndex, null);
    }
    
    public List<Integer> getColLabelColumns() {
        if (this.pivotTableDefinition.getColFields() != null) {
            final List<Integer> columnIndexes = new ArrayList<Integer>();
            for (final CTField f : this.pivotTableDefinition.getColFields().getFieldArray()) {
                columnIndexes.add(f.getX());
            }
            return columnIndexes;
        }
        return Collections.emptyList();
    }
    
    public void addColumnLabel(final DataConsolidateFunction function, final int columnIndex, final String valueFieldName, final String valueFormat) {
        this.checkColumnIndex(columnIndex);
        this.addDataColumn(columnIndex, true);
        this.addDataField(function, columnIndex, valueFieldName, valueFormat);
        if (this.pivotTableDefinition.getDataFields().getCount() == 2L) {
            CTColFields colFields;
            if (this.pivotTableDefinition.getColFields() != null) {
                colFields = this.pivotTableDefinition.getColFields();
            }
            else {
                colFields = this.pivotTableDefinition.addNewColFields();
            }
            colFields.addNewField().setX(-2);
            colFields.setCount((long)colFields.sizeOfFieldArray());
        }
    }
    
    public void addColumnLabel(final DataConsolidateFunction function, final int columnIndex, final String valueFieldName) {
        this.addColumnLabel(function, columnIndex, valueFieldName, null);
    }
    
    public void addColumnLabel(final DataConsolidateFunction function, final int columnIndex) {
        this.addColumnLabel(function, columnIndex, function.getName(), null);
    }
    
    private void addDataField(final DataConsolidateFunction function, final int columnIndex, final String valueFieldName, final String valueFormat) {
        this.checkColumnIndex(columnIndex);
        CTDataFields dataFields;
        if (this.pivotTableDefinition.getDataFields() != null) {
            dataFields = this.pivotTableDefinition.getDataFields();
        }
        else {
            dataFields = this.pivotTableDefinition.addNewDataFields();
        }
        final CTDataField dataField = dataFields.addNewDataField();
        dataField.setSubtotal(STDataConsolidateFunction.Enum.forInt(function.getValue()));
        dataField.setName(valueFieldName);
        dataField.setFld((long)columnIndex);
        if (valueFormat != null && !valueFormat.trim().isEmpty()) {
            final DataFormat df = this.parentSheet.getWorkbook().createDataFormat();
            dataField.setNumFmtId((long)df.getFormat(valueFormat));
        }
        dataFields.setCount((long)dataFields.sizeOfDataFieldArray());
    }
    
    public void addDataColumn(final int columnIndex, final boolean isDataField) {
        this.checkColumnIndex(columnIndex);
        final CTPivotFields pivotFields = this.pivotTableDefinition.getPivotFields();
        final CTPivotField pivotField = CTPivotField.Factory.newInstance();
        pivotField.setDataField(isDataField);
        pivotField.setShowAll(false);
        pivotFields.setPivotFieldArray(columnIndex, pivotField);
    }
    
    public void addReportFilter(final int columnIndex) {
        this.checkColumnIndex(columnIndex);
        final AreaReference pivotArea = this.getPivotArea();
        final int lastRowIndex = pivotArea.getLastCell().getRow() - pivotArea.getFirstCell().getRow();
        final CTLocation location = this.pivotTableDefinition.getLocation();
        final AreaReference destination = new AreaReference(location.getRef(), SpreadsheetVersion.EXCEL2007);
        if (destination.getFirstCell().getRow() < 2) {
            final AreaReference newDestination = new AreaReference(new CellReference(2, destination.getFirstCell().getCol()), new CellReference(3, destination.getFirstCell().getCol() + 1), SpreadsheetVersion.EXCEL2007);
            location.setRef(newDestination.formatAsString());
        }
        final CTPivotFields pivotFields = this.pivotTableDefinition.getPivotFields();
        final CTPivotField pivotField = CTPivotField.Factory.newInstance();
        final CTItems items = pivotField.addNewItems();
        pivotField.setAxis(STAxis.AXIS_PAGE);
        pivotField.setShowAll(false);
        for (int i = 0; i <= lastRowIndex; ++i) {
            items.addNewItem().setT(STItemType.DEFAULT);
        }
        items.setCount((long)items.sizeOfItemArray());
        pivotFields.setPivotFieldArray(columnIndex, pivotField);
        CTPageFields pageFields;
        if (this.pivotTableDefinition.getPageFields() != null) {
            pageFields = this.pivotTableDefinition.getPageFields();
            this.pivotTableDefinition.setMultipleFieldFilters(true);
        }
        else {
            pageFields = this.pivotTableDefinition.addNewPageFields();
        }
        final CTPageField pageField = pageFields.addNewPageField();
        pageField.setHier(-1);
        pageField.setFld(columnIndex);
        pageFields.setCount((long)pageFields.sizeOfPageFieldArray());
        this.pivotTableDefinition.getLocation().setColPageCount(pageFields.getCount());
    }
    
    protected void createSourceReferences(final CellReference position, final Sheet sourceSheet, final PivotTableReferenceConfigurator refConfig) {
        final AreaReference destination = new AreaReference(position, new CellReference(position.getRow() + 1, position.getCol() + 1), SpreadsheetVersion.EXCEL2007);
        CTLocation location;
        if (this.pivotTableDefinition.getLocation() == null) {
            location = this.pivotTableDefinition.addNewLocation();
            location.setFirstDataCol(1L);
            location.setFirstDataRow(1L);
            location.setFirstHeaderRow(1L);
        }
        else {
            location = this.pivotTableDefinition.getLocation();
        }
        location.setRef(destination.formatAsString());
        this.pivotTableDefinition.setLocation(location);
        final CTPivotCacheDefinition cacheDef = this.getPivotCacheDefinition().getCTPivotCacheDefinition();
        final CTCacheSource cacheSource = cacheDef.addNewCacheSource();
        cacheSource.setType(STSourceType.WORKSHEET);
        final CTWorksheetSource worksheetSource = cacheSource.addNewWorksheetSource();
        worksheetSource.setSheet(sourceSheet.getSheetName());
        this.setDataSheet(sourceSheet);
        refConfig.configureReference(worksheetSource);
        if (worksheetSource.getName() == null && worksheetSource.getRef() == null) {
            throw new IllegalArgumentException("Pivot table source area reference or name must be specified.");
        }
    }
    
    protected void createDefaultDataColumns() {
        CTPivotFields pivotFields;
        if (this.pivotTableDefinition.getPivotFields() != null) {
            pivotFields = this.pivotTableDefinition.getPivotFields();
        }
        else {
            pivotFields = this.pivotTableDefinition.addNewPivotFields();
        }
        final AreaReference sourceArea = this.getPivotArea();
        final int firstColumn = sourceArea.getFirstCell().getCol();
        for (int lastColumn = sourceArea.getLastCell().getCol(), i = firstColumn; i <= lastColumn; ++i) {
            final CTPivotField pivotField = pivotFields.addNewPivotField();
            pivotField.setDataField(false);
            pivotField.setShowAll(false);
        }
        pivotFields.setCount((long)pivotFields.sizeOfPivotFieldArray());
    }
    
    protected interface PivotTableReferenceConfigurator
    {
        void configureReference(final CTWorksheetSource p0);
    }
}
