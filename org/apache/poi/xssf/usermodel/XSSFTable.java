package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.TableStyleInfo;
import java.util.Locale;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.util.Removal;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import java.util.Collections;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import java.util.ArrayList;
import org.apache.poi.util.StringUtil;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.poi.util.Internal;
import java.io.OutputStream;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.TableDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.util.CellReference;
import java.util.HashMap;
import org.apache.poi.xssf.usermodel.helpers.XSSFXmlColumnPr;
import java.util.List;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.apache.poi.ss.usermodel.Table;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XSSFTable extends POIXMLDocumentPart implements Table
{
    private CTTable ctTable;
    private transient List<XSSFXmlColumnPr> xmlColumnPrs;
    private transient List<XSSFTableColumn> tableColumns;
    private transient HashMap<String, Integer> columnMap;
    private transient CellReference startCellReference;
    private transient CellReference endCellReference;
    private transient String commonXPath;
    private transient String name;
    private transient String styleName;
    
    public XSSFTable() {
        this.ctTable = CTTable.Factory.newInstance();
    }
    
    public XSSFTable(final PackagePart part) throws IOException {
        super(part);
        this.readFrom(part.getInputStream());
    }
    
    public void readFrom(final InputStream is) throws IOException {
        try {
            final TableDocument doc = TableDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.ctTable = doc.getTable();
        }
        catch (final XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }
    
    public XSSFSheet getXSSFSheet() {
        return (XSSFSheet)this.getParent();
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        this.updateHeaders();
        final TableDocument doc = TableDocument.Factory.newInstance();
        doc.setTable(this.ctTable);
        doc.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }
    
    @Override
    protected void commit() throws IOException {
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.writeTo(out);
        out.close();
    }
    
    @Internal(since = "POI 3.15 beta 3")
    public CTTable getCTTable() {
        return this.ctTable;
    }
    
    public boolean mapsTo(final long id) {
        final List<XSSFXmlColumnPr> pointers = this.getXmlColumnPrs();
        for (final XSSFXmlColumnPr pointer : pointers) {
            if (pointer.getMapId() == id) {
                return true;
            }
        }
        return false;
    }
    
    public String getCommonXpath() {
        if (this.commonXPath == null) {
            String[] commonTokens = new String[0];
            for (final XSSFTableColumn column : this.getColumns()) {
                if (column.getXmlColumnPr() != null) {
                    final String xpath = column.getXmlColumnPr().getXPath();
                    final String[] tokens = xpath.split("/");
                    if (commonTokens.length == 0) {
                        commonTokens = tokens;
                    }
                    else {
                        for (int maxLength = Math.min(commonTokens.length, tokens.length), i = 0; i < maxLength; ++i) {
                            if (!commonTokens[i].equals(tokens[i])) {
                                final List<String> subCommonTokens = Arrays.asList(commonTokens).subList(0, i);
                                final String[] container = new String[0];
                                commonTokens = subCommonTokens.toArray(container);
                                break;
                            }
                        }
                    }
                }
            }
            commonTokens[0] = "";
            this.commonXPath = StringUtil.join((Object[])commonTokens, "/");
        }
        return this.commonXPath;
    }
    
    public List<XSSFTableColumn> getColumns() {
        if (this.tableColumns == null) {
            final List<XSSFTableColumn> columns = new ArrayList<XSSFTableColumn>();
            final CTTableColumns ctTableColumns = this.ctTable.getTableColumns();
            if (ctTableColumns != null) {
                for (final CTTableColumn column : ctTableColumns.getTableColumnList()) {
                    final XSSFTableColumn tableColumn = new XSSFTableColumn(this, column);
                    columns.add(tableColumn);
                }
            }
            this.tableColumns = Collections.unmodifiableList((List<? extends XSSFTableColumn>)columns);
        }
        return this.tableColumns;
    }
    
    @Deprecated
    @Removal(version = "4.2.0")
    public List<XSSFXmlColumnPr> getXmlColumnPrs() {
        if (this.xmlColumnPrs == null) {
            this.xmlColumnPrs = new ArrayList<XSSFXmlColumnPr>();
            for (final XSSFTableColumn column : this.getColumns()) {
                final XSSFXmlColumnPr xmlColumnPr = column.getXmlColumnPr();
                if (xmlColumnPr != null) {
                    this.xmlColumnPrs.add(xmlColumnPr);
                }
            }
        }
        return this.xmlColumnPrs;
    }
    
    public XSSFTableColumn createColumn(final String columnName) {
        return this.createColumn(columnName, this.getColumnCount());
    }
    
    public XSSFTableColumn createColumn(final String columnName, final int columnIndex) {
        final int columnCount = this.getColumnCount();
        if (columnIndex < 0 || columnIndex > columnCount) {
            throw new IllegalArgumentException("Column index out of bounds");
        }
        CTTableColumns columns = this.ctTable.getTableColumns();
        if (columns == null) {
            columns = this.ctTable.addNewTableColumns();
        }
        long nextColumnId = 0L;
        for (final XSSFTableColumn tableColumn : this.getColumns()) {
            if (columnName != null && columnName.equalsIgnoreCase(tableColumn.getName())) {
                throw new IllegalArgumentException("Column '" + columnName + "' already exists. Column names must be unique per table.");
            }
            nextColumnId = Math.max(nextColumnId, tableColumn.getId());
        }
        ++nextColumnId;
        final CTTableColumn column = columns.insertNewTableColumn(columnIndex);
        columns.setCount((long)columns.sizeOfTableColumnArray());
        column.setId(nextColumnId);
        if (columnName != null) {
            column.setName(columnName);
        }
        else {
            column.setName("Column " + nextColumnId);
        }
        if (this.ctTable.getRef() != null) {
            final int newColumnCount = columnCount + 1;
            final CellReference tableStart = this.getStartCellReference();
            final CellReference tableEnd = this.getEndCellReference();
            final SpreadsheetVersion version = this.getXSSFSheet().getWorkbook().getSpreadsheetVersion();
            final CellReference newTableEnd = new CellReference(tableEnd.getRow(), tableStart.getCol() + newColumnCount - 1);
            final AreaReference newTableArea = new AreaReference(tableStart, newTableEnd, version);
            this.setCellRef(newTableArea);
        }
        this.updateHeaders();
        return this.getColumns().get(columnIndex);
    }
    
    public void removeColumn(final XSSFTableColumn column) {
        final int columnIndex = this.getColumns().indexOf(column);
        if (columnIndex >= 0) {
            this.ctTable.getTableColumns().removeTableColumn(columnIndex);
            this.updateReferences();
            this.updateHeaders();
        }
    }
    
    public void removeColumn(final int columnIndex) {
        if (columnIndex < 0 || columnIndex > this.getColumnCount() - 1) {
            throw new IllegalArgumentException("Column index out of bounds");
        }
        if (this.getColumnCount() == 1) {
            throw new IllegalArgumentException("Table must have at least one column");
        }
        final CTTableColumns tableColumns = this.ctTable.getTableColumns();
        tableColumns.removeTableColumn(columnIndex);
        tableColumns.setCount((long)tableColumns.getTableColumnList().size());
        this.updateReferences();
        this.updateHeaders();
    }
    
    public String getName() {
        if (this.name == null && this.ctTable.getName() != null) {
            this.setName(this.ctTable.getName());
        }
        return this.name;
    }
    
    public void setName(final String newName) {
        if (newName == null) {
            this.ctTable.unsetName();
            this.name = null;
            return;
        }
        this.ctTable.setName(newName);
        this.name = newName;
    }
    
    public String getStyleName() {
        if (this.styleName == null && this.ctTable.isSetTableStyleInfo()) {
            this.setStyleName(this.ctTable.getTableStyleInfo().getName());
        }
        return this.styleName;
    }
    
    public void setStyleName(final String newStyleName) {
        if (newStyleName == null) {
            if (this.ctTable.isSetTableStyleInfo()) {
                this.ctTable.getTableStyleInfo().unsetName();
            }
            this.styleName = null;
            return;
        }
        if (!this.ctTable.isSetTableStyleInfo()) {
            this.ctTable.addNewTableStyleInfo();
        }
        this.ctTable.getTableStyleInfo().setName(newStyleName);
        this.styleName = newStyleName;
    }
    
    public String getDisplayName() {
        return this.ctTable.getDisplayName();
    }
    
    public void setDisplayName(final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Display name must not be null or empty");
        }
        this.ctTable.setDisplayName(name);
    }
    
    @Deprecated
    @Removal(version = "4.2.0")
    public long getNumberOfMappedColumns() {
        return this.ctTable.getTableColumns().getCount();
    }
    
    public AreaReference getCellReferences() {
        return new AreaReference(this.getStartCellReference(), this.getEndCellReference(), SpreadsheetVersion.EXCEL2007);
    }
    
    public void setCellReferences(final AreaReference refs) {
        this.setCellRef(refs);
    }
    
    @Internal
    protected void setCellRef(final AreaReference refs) {
        String ref = refs.formatAsString();
        if (ref.indexOf(33) != -1) {
            ref = ref.substring(ref.indexOf(33) + 1);
        }
        this.ctTable.setRef(ref);
        if (this.ctTable.isSetAutoFilter()) {
            final int totalsRowCount = this.getTotalsRowCount();
            String filterRef;
            if (totalsRowCount == 0) {
                filterRef = ref;
            }
            else {
                final CellReference start = new CellReference(refs.getFirstCell().getRow(), refs.getFirstCell().getCol());
                final CellReference end = new CellReference(refs.getLastCell().getRow() - totalsRowCount, refs.getLastCell().getCol());
                filterRef = new AreaReference(start, end, SpreadsheetVersion.EXCEL2007).formatAsString();
            }
            this.ctTable.getAutoFilter().setRef(filterRef);
        }
        this.updateReferences();
        this.updateHeaders();
    }
    
    public void setArea(final AreaReference tableArea) {
        if (tableArea == null) {
            throw new IllegalArgumentException("AreaReference must not be null");
        }
        final String areaSheetName = tableArea.getFirstCell().getSheetName();
        if (areaSheetName != null && !areaSheetName.equals(this.getXSSFSheet().getSheetName())) {
            throw new IllegalArgumentException("The AreaReference must not reference a different sheet");
        }
        final int rowCount = tableArea.getLastCell().getRow() - tableArea.getFirstCell().getRow() + 1;
        final int minimumRowCount = 1 + this.getHeaderRowCount() + this.getTotalsRowCount();
        if (rowCount < minimumRowCount) {
            throw new IllegalArgumentException("AreaReference needs at least " + minimumRowCount + " rows, to cover at least one data row and all header rows and totals rows");
        }
        String ref = tableArea.formatAsString();
        if (ref.indexOf(33) != -1) {
            ref = ref.substring(ref.indexOf(33) + 1);
        }
        this.ctTable.setRef(ref);
        if (this.ctTable.isSetAutoFilter()) {
            this.ctTable.getAutoFilter().setRef(ref);
        }
        this.updateReferences();
        final int columnCount = this.getColumnCount();
        final int newColumnCount = tableArea.getLastCell().getCol() - tableArea.getFirstCell().getCol() + 1;
        if (newColumnCount > columnCount) {
            for (int i = columnCount; i < newColumnCount; ++i) {
                this.createColumn(null, i);
            }
        }
        else if (newColumnCount < columnCount) {
            for (int i = columnCount; i > newColumnCount; --i) {
                this.removeColumn(i - 1);
            }
        }
        this.updateHeaders();
    }
    
    public AreaReference getArea() {
        final String ref = this.ctTable.getRef();
        if (ref != null) {
            final SpreadsheetVersion version = this.getXSSFSheet().getWorkbook().getSpreadsheetVersion();
            return new AreaReference(this.ctTable.getRef(), version);
        }
        return null;
    }
    
    public CellReference getStartCellReference() {
        if (this.startCellReference == null) {
            this.setCellReferences();
        }
        return this.startCellReference;
    }
    
    public CellReference getEndCellReference() {
        if (this.endCellReference == null) {
            this.setCellReferences();
        }
        return this.endCellReference;
    }
    
    private void setCellReferences() {
        final String ref = this.ctTable.getRef();
        if (ref != null) {
            final String[] boundaries = ref.split(":", 2);
            final String from = boundaries[0];
            final String to = (boundaries.length == 2) ? boundaries[1] : boundaries[0];
            this.startCellReference = new CellReference(from);
            this.endCellReference = new CellReference(to);
        }
    }
    
    public void updateReferences() {
        this.startCellReference = null;
        this.endCellReference = null;
    }
    
    public int getRowCount() {
        final CellReference from = this.getStartCellReference();
        final CellReference to = this.getEndCellReference();
        int rowCount = 0;
        if (from != null && to != null) {
            rowCount = to.getRow() - from.getRow() + 1;
        }
        return rowCount;
    }
    
    public int getDataRowCount() {
        final CellReference from = this.getStartCellReference();
        final CellReference to = this.getEndCellReference();
        int rowCount = 0;
        if (from != null && to != null) {
            rowCount = to.getRow() - from.getRow() + 1 - this.getHeaderRowCount() - this.getTotalsRowCount();
        }
        return rowCount;
    }
    
    public void setDataRowCount(final int newDataRowCount) {
        if (newDataRowCount < 1) {
            throw new IllegalArgumentException("Table must have at least one data row");
        }
        this.updateReferences();
        final int dataRowCount = this.getDataRowCount();
        if (dataRowCount == newDataRowCount) {
            return;
        }
        final CellReference tableStart = this.getStartCellReference();
        final CellReference tableEnd = this.getEndCellReference();
        final SpreadsheetVersion version = this.getXSSFSheet().getWorkbook().getSpreadsheetVersion();
        final int newTotalRowCount = this.getHeaderRowCount() + newDataRowCount + this.getTotalsRowCount();
        final CellReference newTableEnd = new CellReference(tableStart.getRow() + newTotalRowCount - 1, tableEnd.getCol());
        final AreaReference newTableArea = new AreaReference(tableStart, newTableEnd, version);
        CellReference clearAreaStart;
        CellReference clearAreaEnd;
        if (newDataRowCount < dataRowCount) {
            clearAreaStart = new CellReference(newTableArea.getLastCell().getRow() + 1, newTableArea.getFirstCell().getCol());
            clearAreaEnd = tableEnd;
        }
        else {
            clearAreaStart = new CellReference(tableEnd.getRow() + 1, newTableArea.getFirstCell().getCol());
            clearAreaEnd = newTableEnd;
        }
        final AreaReference areaToClear = new AreaReference(clearAreaStart, clearAreaEnd, version);
        for (final CellReference cellRef : areaToClear.getAllReferencedCells()) {
            final XSSFRow row = this.getXSSFSheet().getRow(cellRef.getRow());
            if (row != null) {
                final XSSFCell cell = row.getCell(cellRef.getCol());
                if (cell != null) {
                    cell.setBlank();
                    cell.setCellStyle(null);
                }
            }
        }
        this.setCellRef(newTableArea);
    }
    
    public int getColumnCount() {
        final CTTableColumns tableColumns = this.ctTable.getTableColumns();
        if (tableColumns == null) {
            return 0;
        }
        return (int)tableColumns.getCount();
    }
    
    public void updateHeaders() {
        final XSSFSheet sheet = (XSSFSheet)this.getParent();
        final CellReference ref = this.getStartCellReference();
        if (ref == null) {
            return;
        }
        final int headerRow = ref.getRow();
        final int firstHeaderColumn = ref.getCol();
        final XSSFRow row = sheet.getRow(headerRow);
        final DataFormatter formatter = new DataFormatter();
        if (row != null && row.getCTRow().validate()) {
            int cellnum = firstHeaderColumn;
            final CTTableColumns ctTableColumns = this.getCTTable().getTableColumns();
            if (ctTableColumns != null) {
                for (final CTTableColumn col : ctTableColumns.getTableColumnList()) {
                    final XSSFCell cell = row.getCell(cellnum);
                    if (cell != null) {
                        col.setName(formatter.formatCellValue((Cell)cell));
                    }
                    ++cellnum;
                }
            }
        }
        this.tableColumns = null;
        this.columnMap = null;
        this.xmlColumnPrs = null;
        this.commonXPath = null;
    }
    
    private static String caseInsensitive(final String s) {
        return s.toUpperCase(Locale.ROOT);
    }
    
    public int findColumnIndex(final String columnHeader) {
        if (columnHeader == null) {
            return -1;
        }
        if (this.columnMap == null) {
            final int count = this.getColumnCount();
            this.columnMap = new HashMap<String, Integer>(count * 3 / 2);
            int i = 0;
            for (final XSSFTableColumn column : this.getColumns()) {
                final String columnName = column.getName();
                this.columnMap.put(caseInsensitive(columnName), i);
                ++i;
            }
        }
        final Integer idx = this.columnMap.get(caseInsensitive(columnHeader.replace("'", "")));
        return (idx == null) ? -1 : idx;
    }
    
    public String getSheetName() {
        return this.getXSSFSheet().getSheetName();
    }
    
    public boolean isHasTotalsRow() {
        return this.ctTable.getTotalsRowShown();
    }
    
    public int getTotalsRowCount() {
        return (int)this.ctTable.getTotalsRowCount();
    }
    
    public int getHeaderRowCount() {
        return (int)this.ctTable.getHeaderRowCount();
    }
    
    public int getStartColIndex() {
        return this.getStartCellReference().getCol();
    }
    
    public int getStartRowIndex() {
        return this.getStartCellReference().getRow();
    }
    
    public int getEndColIndex() {
        return this.getEndCellReference().getCol();
    }
    
    public int getEndRowIndex() {
        return this.getEndCellReference().getRow();
    }
    
    public TableStyleInfo getStyle() {
        if (!this.ctTable.isSetTableStyleInfo()) {
            return null;
        }
        return (TableStyleInfo)new XSSFTableStyleInfo(((XSSFSheet)this.getParent()).getWorkbook().getStylesSource(), this.ctTable.getTableStyleInfo());
    }
    
    public boolean contains(final CellReference cell) {
        return cell != null && this.getSheetName().equals(cell.getSheetName()) && (cell.getRow() >= this.getStartRowIndex() && cell.getRow() <= this.getEndRowIndex() && cell.getCol() >= this.getStartColIndex() && cell.getCol() <= this.getEndColIndex());
    }
    
    protected void onTableDelete() {
        for (final RelationPart part : this.getRelationParts()) {
            this.removeRelation(part.getDocumentPart(), true);
        }
    }
}
