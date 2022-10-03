package org.apache.poi.xssf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheetSource;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.AutoFilter;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Hyperlink;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.XmlCursor;
import javax.xml.stream.XMLStreamException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObjects;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObject;
import org.apache.poi.ss.usermodel.CellType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIgnoredErrors;
import java.util.LinkedHashSet;
import org.apache.poi.xssf.usermodel.helpers.XSSFIgnoredErrorHelper;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIgnoredError;
import java.util.LinkedHashMap;
import java.util.Set;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.usermodel.Table;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.ss.usermodel.Name;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTablePart;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableParts;
import org.apache.poi.util.Removal;
import org.apache.poi.ss.util.AreaReference;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAutoFilter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidations;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.SSCellRange;
import org.apache.poi.ss.usermodel.CellRange;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import java.io.OutputStream;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellFormulaType;
import org.apache.poi.openxml4j.exceptions.PartAlreadyExistsException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCommentList;
import org.apache.poi.xssf.usermodel.helpers.XSSFColumnShifter;
import org.apache.poi.xssf.usermodel.helpers.XSSFRowShifter;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetCalcPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCalcMode;
import org.apache.poi.ss.usermodel.Row;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCols;
import java.util.Collection;
import org.apache.poi.xssf.usermodel.helpers.XSSFPasswordHelper;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetProtection;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.ss.util.PaneInformation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPrintOptions;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Footer;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageSetUpPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOutlinePr;
import org.apache.poi.ss.usermodel.CellStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBreak;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageBreak;
import java.util.Collections;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComment;
import org.apache.poi.ss.util.CellAddress;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.ss.usermodel.Cell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPane;
import org.apache.poi.ss.util.CellReference;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPaneState;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPane;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSelection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLegacyDrawing;
import org.apache.poi.ooxml.POIXMLFactory;
import org.apache.poi.ooxml.POIXMLRelation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawing;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.ss.util.CellRangeAddressBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMergeCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMergeCells;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageMargins;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetFormatPr;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHyperlink;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRow;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorksheetDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.TreeMap;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellFormula;
import java.util.Map;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.usermodel.helpers.ColumnHelper;
import java.util.List;
import java.util.SortedMap;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheet;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XSSFSheet extends POIXMLDocumentPart implements Sheet
{
    private static final POILogger logger;
    private static final double DEFAULT_ROW_HEIGHT = 15.0;
    private static final double DEFAULT_MARGIN_HEADER = 0.3;
    private static final double DEFAULT_MARGIN_FOOTER = 0.3;
    private static final double DEFAULT_MARGIN_TOP = 0.75;
    private static final double DEFAULT_MARGIN_BOTTOM = 0.75;
    private static final double DEFAULT_MARGIN_LEFT = 0.7;
    private static final double DEFAULT_MARGIN_RIGHT = 0.7;
    public static final int TWIPS_PER_POINT = 20;
    protected CTSheet sheet;
    protected CTWorksheet worksheet;
    private final SortedMap<Integer, XSSFRow> _rows;
    private List<XSSFHyperlink> hyperlinks;
    private ColumnHelper columnHelper;
    private CommentsTable sheetComments;
    private Map<Integer, CTCellFormula> sharedFormulas;
    private SortedMap<String, XSSFTable> tables;
    private List<CellRangeAddress> arrayFormulas;
    private XSSFDataValidationHelper dataValidationHelper;
    
    protected XSSFSheet() {
        this._rows = new TreeMap<Integer, XSSFRow>();
        this.dataValidationHelper = new XSSFDataValidationHelper(this);
        this.onDocumentCreate();
    }
    
    protected XSSFSheet(final PackagePart part) {
        super(part);
        this._rows = new TreeMap<Integer, XSSFRow>();
        this.dataValidationHelper = new XSSFDataValidationHelper(this);
    }
    
    public XSSFWorkbook getWorkbook() {
        return (XSSFWorkbook)this.getParent();
    }
    
    @Override
    protected void onDocumentRead() {
        try {
            this.read(this.getPackagePart().getInputStream());
        }
        catch (final IOException e) {
            throw new POIXMLException(e);
        }
    }
    
    protected void read(final InputStream is) throws IOException {
        try {
            this.worksheet = WorksheetDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS).getWorksheet();
        }
        catch (final XmlException e) {
            throw new POIXMLException((Throwable)e);
        }
        this.initRows(this.worksheet);
        this.columnHelper = new ColumnHelper(this.worksheet);
        for (final RelationPart rp : this.getRelationParts()) {
            final POIXMLDocumentPart p = rp.getDocumentPart();
            if (p instanceof CommentsTable) {
                this.sheetComments = (CommentsTable)p;
            }
            if (p instanceof XSSFTable) {
                this.tables.put(rp.getRelationship().getId(), (XSSFTable)p);
            }
            if (p instanceof XSSFPivotTable) {
                this.getWorkbook().getPivotTables().add((XSSFPivotTable)p);
            }
        }
        this.initHyperlinks();
    }
    
    @Override
    protected void onDocumentCreate() {
        this.initRows(this.worksheet = newSheet());
        this.columnHelper = new ColumnHelper(this.worksheet);
        this.hyperlinks = new ArrayList<XSSFHyperlink>();
    }
    
    private void initRows(final CTWorksheet worksheetParam) {
        this._rows.clear();
        this.tables = new TreeMap<String, XSSFTable>();
        this.sharedFormulas = new HashMap<Integer, CTCellFormula>();
        this.arrayFormulas = new ArrayList<CellRangeAddress>();
        for (final CTRow row : worksheetParam.getSheetData().getRowArray()) {
            final XSSFRow r = new XSSFRow(row, this);
            final Integer rownumI = r.getRowNum();
            this._rows.put(rownumI, r);
        }
    }
    
    private void initHyperlinks() {
        this.hyperlinks = new ArrayList<XSSFHyperlink>();
        if (!this.worksheet.isSetHyperlinks()) {
            return;
        }
        try {
            final PackageRelationshipCollection hyperRels = this.getPackagePart().getRelationshipsByType(XSSFRelation.SHEET_HYPERLINKS.getRelation());
            for (final CTHyperlink hyperlink : this.worksheet.getHyperlinks().getHyperlinkArray()) {
                PackageRelationship hyperRel = null;
                if (hyperlink.getId() != null) {
                    hyperRel = hyperRels.getRelationshipByID(hyperlink.getId());
                }
                this.hyperlinks.add(new XSSFHyperlink(hyperlink, hyperRel));
            }
        }
        catch (final InvalidFormatException e) {
            throw new POIXMLException(e);
        }
    }
    
    private static CTWorksheet newSheet() {
        final CTWorksheet worksheet = CTWorksheet.Factory.newInstance();
        final CTSheetFormatPr ctFormat = worksheet.addNewSheetFormatPr();
        ctFormat.setDefaultRowHeight(15.0);
        final CTSheetView ctView = worksheet.addNewSheetViews().addNewSheetView();
        ctView.setWorkbookViewId(0L);
        worksheet.addNewDimension().setRef("A1");
        worksheet.addNewSheetData();
        final CTPageMargins ctMargins = worksheet.addNewPageMargins();
        ctMargins.setBottom(0.75);
        ctMargins.setFooter(0.3);
        ctMargins.setHeader(0.3);
        ctMargins.setLeft(0.7);
        ctMargins.setRight(0.7);
        ctMargins.setTop(0.75);
        return worksheet;
    }
    
    @Internal
    public CTWorksheet getCTWorksheet() {
        return this.worksheet;
    }
    
    public ColumnHelper getColumnHelper() {
        return this.columnHelper;
    }
    
    public String getSheetName() {
        return this.sheet.getName();
    }
    
    public int addMergedRegion(final CellRangeAddress region) {
        return this.addMergedRegion(region, true);
    }
    
    public int addMergedRegionUnsafe(final CellRangeAddress region) {
        return this.addMergedRegion(region, false);
    }
    
    private int addMergedRegion(final CellRangeAddress region, final boolean validate) {
        if (region.getNumberOfCells() < 2) {
            throw new IllegalArgumentException("Merged region " + region.formatAsString() + " must contain 2 or more cells");
        }
        region.validate(SpreadsheetVersion.EXCEL2007);
        if (validate) {
            this.validateArrayFormulas(region);
            this.validateMergedRegions(region);
        }
        final CTMergeCells ctMergeCells = this.worksheet.isSetMergeCells() ? this.worksheet.getMergeCells() : this.worksheet.addNewMergeCells();
        final CTMergeCell ctMergeCell = ctMergeCells.addNewMergeCell();
        ctMergeCell.setRef(region.formatAsString());
        final int numMergeRegions = ctMergeCells.sizeOfMergeCellArray();
        ctMergeCells.setCount((long)numMergeRegions);
        return numMergeRegions - 1;
    }
    
    private void validateArrayFormulas(final CellRangeAddress region) {
        final int firstRow = region.getFirstRow();
        final int firstColumn = region.getFirstColumn();
        final int lastRow = region.getLastRow();
        final int lastColumn = region.getLastColumn();
        for (int rowIn = firstRow; rowIn <= lastRow; ++rowIn) {
            final XSSFRow row = this.getRow(rowIn);
            if (row != null) {
                for (int colIn = firstColumn; colIn <= lastColumn; ++colIn) {
                    final XSSFCell cell = row.getCell(colIn);
                    if (cell != null) {
                        if (cell.isPartOfArrayFormulaGroup()) {
                            final CellRangeAddress arrayRange = cell.getArrayFormulaRange();
                            if (arrayRange.getNumberOfCells() > 1 && region.intersects((CellRangeAddressBase)arrayRange)) {
                                final String msg = "The range " + region.formatAsString() + " intersects with a multi-cell array formula. You cannot merge cells of an array.";
                                throw new IllegalStateException(msg);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void checkForMergedRegionsIntersectingArrayFormulas() {
        for (final CellRangeAddress region : this.getMergedRegions()) {
            this.validateArrayFormulas(region);
        }
    }
    
    private void validateMergedRegions(final CellRangeAddress candidateRegion) {
        for (final CellRangeAddress existingRegion : this.getMergedRegions()) {
            if (existingRegion.intersects((CellRangeAddressBase)candidateRegion)) {
                throw new IllegalStateException("Cannot add merged region " + candidateRegion.formatAsString() + " to sheet because it overlaps with an existing merged region (" + existingRegion.formatAsString() + ").");
            }
        }
    }
    
    private void checkForIntersectingMergedRegions() {
        final List<CellRangeAddress> regions = this.getMergedRegions();
        for (int size = regions.size(), i = 0; i < size; ++i) {
            final CellRangeAddress region = regions.get(i);
            for (final CellRangeAddress other : regions.subList(i + 1, regions.size())) {
                if (region.intersects((CellRangeAddressBase)other)) {
                    final String msg = "The range " + region.formatAsString() + " intersects with another merged region " + other.formatAsString() + " in this sheet";
                    throw new IllegalStateException(msg);
                }
            }
        }
    }
    
    public void validateMergedRegions() {
        this.checkForMergedRegionsIntersectingArrayFormulas();
        this.checkForIntersectingMergedRegions();
    }
    
    public void autoSizeColumn(final int column) {
        this.autoSizeColumn(column, false);
    }
    
    public void autoSizeColumn(final int column, final boolean useMergedCells) {
        double width = SheetUtil.getColumnWidth((Sheet)this, column, useMergedCells);
        if (width != -1.0) {
            width *= 256.0;
            final int maxColumnWidth = 65280;
            if (width > maxColumnWidth) {
                width = maxColumnWidth;
            }
            this.setColumnWidth(column, Math.toIntExact(Math.round(width)));
            this.columnHelper.setColBestFit(column, true);
        }
    }
    
    public XSSFDrawing getDrawingPatriarch() {
        final CTDrawing ctDrawing = this.getCTDrawing();
        if (ctDrawing != null) {
            for (final RelationPart rp : this.getRelationParts()) {
                final POIXMLDocumentPart p = rp.getDocumentPart();
                if (p instanceof XSSFDrawing) {
                    final XSSFDrawing dr = (XSSFDrawing)p;
                    final String drId = rp.getRelationship().getId();
                    if (drId.equals(ctDrawing.getId())) {
                        return dr;
                    }
                    break;
                }
            }
            XSSFSheet.logger.log(7, new Object[] { "Can't find drawing with id=" + ctDrawing.getId() + " in the list of the sheet's relationships" });
        }
        return null;
    }
    
    public XSSFDrawing createDrawingPatriarch() {
        CTDrawing ctDrawing = this.getCTDrawing();
        if (ctDrawing != null) {
            return this.getDrawingPatriarch();
        }
        int drawingNumber = this.getPackagePart().getPackage().getPartsByContentType(XSSFRelation.DRAWINGS.getContentType()).size() + 1;
        drawingNumber = this.getNextPartNumber(XSSFRelation.DRAWINGS, drawingNumber);
        final RelationPart rp = this.createRelationship(XSSFRelation.DRAWINGS, XSSFFactory.getInstance(), drawingNumber, false);
        final XSSFDrawing drawing = rp.getDocumentPart();
        final String relId = rp.getRelationship().getId();
        ctDrawing = this.worksheet.addNewDrawing();
        ctDrawing.setId(relId);
        return drawing;
    }
    
    protected XSSFVMLDrawing getVMLDrawing(final boolean autoCreate) {
        XSSFVMLDrawing drawing = null;
        CTLegacyDrawing ctDrawing = this.getCTLegacyDrawing();
        if (ctDrawing == null) {
            if (autoCreate) {
                final int drawingNumber = this.getPackagePart().getPackage().getPartsByContentType(XSSFRelation.VML_DRAWINGS.getContentType()).size() + 1;
                final RelationPart rp = this.createRelationship(XSSFRelation.VML_DRAWINGS, XSSFFactory.getInstance(), drawingNumber, false);
                drawing = rp.getDocumentPart();
                final String relId = rp.getRelationship().getId();
                ctDrawing = this.worksheet.addNewLegacyDrawing();
                ctDrawing.setId(relId);
            }
        }
        else {
            final String id = ctDrawing.getId();
            for (final RelationPart rp2 : this.getRelationParts()) {
                final POIXMLDocumentPart p = rp2.getDocumentPart();
                if (p instanceof XSSFVMLDrawing) {
                    final XSSFVMLDrawing dr = (XSSFVMLDrawing)p;
                    final String drId = rp2.getRelationship().getId();
                    if (drId.equals(id)) {
                        drawing = dr;
                        break;
                    }
                    continue;
                }
            }
            if (drawing == null) {
                XSSFSheet.logger.log(7, new Object[] { "Can't find VML drawing with id=" + id + " in the list of the sheet's relationships" });
            }
        }
        return drawing;
    }
    
    protected CTDrawing getCTDrawing() {
        return this.worksheet.getDrawing();
    }
    
    protected CTLegacyDrawing getCTLegacyDrawing() {
        return this.worksheet.getLegacyDrawing();
    }
    
    public void createFreezePane(final int colSplit, final int rowSplit) {
        this.createFreezePane(colSplit, rowSplit, colSplit, rowSplit);
    }
    
    public void createFreezePane(final int colSplit, final int rowSplit, final int leftmostColumn, final int topRow) {
        final boolean removeSplit = colSplit == 0 && rowSplit == 0;
        final CTSheetView ctView = this.getDefaultSheetView(!removeSplit);
        if (ctView != null) {
            ctView.setSelectionArray((CTSelection[])null);
        }
        if (removeSplit) {
            if (ctView != null && ctView.isSetPane()) {
                ctView.unsetPane();
            }
            return;
        }
        assert ctView != null;
        final CTPane pane = ctView.isSetPane() ? ctView.getPane() : ctView.addNewPane();
        assert pane != null;
        if (colSplit > 0) {
            pane.setXSplit((double)colSplit);
        }
        else if (pane.isSetXSplit()) {
            pane.unsetXSplit();
        }
        if (rowSplit > 0) {
            pane.setYSplit((double)rowSplit);
        }
        else if (pane.isSetYSplit()) {
            pane.unsetYSplit();
        }
        STPane.Enum activePane = STPane.BOTTOM_RIGHT;
        int pRow = topRow;
        int pCol = leftmostColumn;
        if (rowSplit == 0) {
            pRow = 0;
            activePane = STPane.TOP_RIGHT;
        }
        else if (colSplit == 0) {
            pCol = 0;
            activePane = STPane.BOTTOM_LEFT;
        }
        pane.setState(STPaneState.FROZEN);
        pane.setTopLeftCell(new CellReference(pRow, pCol).formatAsString());
        pane.setActivePane(activePane);
        ctView.addNewSelection().setPane(activePane);
    }
    
    public XSSFRow createRow(final int rownum) {
        final Integer rownumI = rownum;
        final XSSFRow prev = this._rows.get(rownumI);
        CTRow ctRow;
        if (prev != null) {
            while (prev.getFirstCellNum() != -1) {
                prev.removeCell((Cell)prev.getCell(prev.getFirstCellNum()));
            }
            ctRow = prev.getCTRow();
            ctRow.set((XmlObject)CTRow.Factory.newInstance());
        }
        else if (this._rows.isEmpty() || rownum > this._rows.lastKey()) {
            ctRow = this.worksheet.getSheetData().addNewRow();
        }
        else {
            final int idx = this._rows.headMap(rownumI).size();
            ctRow = this.worksheet.getSheetData().insertNewRow(idx);
        }
        final XSSFRow r = new XSSFRow(ctRow, this);
        r.setRowNum(rownum);
        this._rows.put(rownumI, r);
        return r;
    }
    
    public void createSplitPane(final int xSplitPos, final int ySplitPos, final int leftmostColumn, final int topRow, final int activePane) {
        this.createFreezePane(xSplitPos, ySplitPos, leftmostColumn, topRow);
        if (xSplitPos > 0 || ySplitPos > 0) {
            final CTPane pane = this.getPane(true);
            pane.setState(STPaneState.SPLIT);
            pane.setActivePane(STPane.Enum.forInt(activePane));
        }
    }
    
    public XSSFComment getCellComment(final CellAddress address) {
        if (this.sheetComments == null) {
            return null;
        }
        final int row = address.getRow();
        final int column = address.getColumn();
        final CellAddress ref = new CellAddress(row, column);
        final CTComment ctComment = this.sheetComments.getCTComment(ref);
        if (ctComment == null) {
            return null;
        }
        final XSSFVMLDrawing vml = this.getVMLDrawing(false);
        return new XSSFComment(this.sheetComments, ctComment, (vml == null) ? null : vml.findCommentShape(row, column));
    }
    
    public Map<CellAddress, XSSFComment> getCellComments() {
        if (this.sheetComments == null) {
            return Collections.emptyMap();
        }
        final Map<CellAddress, XSSFComment> map = new HashMap<CellAddress, XSSFComment>();
        final Iterator<CellAddress> iter = this.sheetComments.getCellAddresses();
        while (iter.hasNext()) {
            final CellAddress address = iter.next();
            map.put(address, this.getCellComment(address));
        }
        return map;
    }
    
    public XSSFHyperlink getHyperlink(final int row, final int column) {
        return this.getHyperlink(new CellAddress(row, column));
    }
    
    public XSSFHyperlink getHyperlink(final CellAddress addr) {
        final String ref = addr.formatAsString();
        for (final XSSFHyperlink hyperlink : this.hyperlinks) {
            if (hyperlink.getCellRef().equals(ref)) {
                return hyperlink;
            }
        }
        return null;
    }
    
    public List<XSSFHyperlink> getHyperlinkList() {
        return Collections.unmodifiableList((List<? extends XSSFHyperlink>)this.hyperlinks);
    }
    
    private int[] getBreaks(final CTPageBreak ctPageBreak) {
        final CTBreak[] brkArray = ctPageBreak.getBrkArray();
        final int[] breaks = new int[brkArray.length];
        for (int i = 0; i < brkArray.length; ++i) {
            breaks[i] = Math.toIntExact(brkArray[i].getId() - 1L);
        }
        return breaks;
    }
    
    private void removeBreak(final int index, final CTPageBreak ctPageBreak) {
        final int index2 = index + 1;
        final CTBreak[] brkArray = ctPageBreak.getBrkArray();
        for (int i = 0; i < brkArray.length; ++i) {
            if (brkArray[i].getId() == index2) {
                ctPageBreak.removeBrk(i);
            }
        }
    }
    
    public int[] getColumnBreaks() {
        return this.worksheet.isSetColBreaks() ? this.getBreaks(this.worksheet.getColBreaks()) : new int[0];
    }
    
    public int getColumnWidth(final int columnIndex) {
        final CTCol col = this.columnHelper.getColumn(columnIndex, false);
        final double width = (col == null || !col.isSetWidth()) ? this.getDefaultColumnWidth() : col.getWidth();
        return Math.toIntExact(Math.round(width * 256.0));
    }
    
    public float getColumnWidthInPixels(final int columnIndex) {
        final float widthIn256 = (float)this.getColumnWidth(columnIndex);
        return (float)(widthIn256 / 256.0 * 7.001699924468994);
    }
    
    public int getDefaultColumnWidth() {
        final CTSheetFormatPr pr = this.worksheet.getSheetFormatPr();
        return (pr == null) ? 8 : Math.toIntExact(pr.getBaseColWidth());
    }
    
    public short getDefaultRowHeight() {
        return (short)(this.getDefaultRowHeightInPoints() * 20.0f);
    }
    
    public float getDefaultRowHeightInPoints() {
        final CTSheetFormatPr pr = this.worksheet.getSheetFormatPr();
        return (float)((pr == null) ? 0.0 : pr.getDefaultRowHeight());
    }
    
    private CTSheetFormatPr getSheetTypeSheetFormatPr() {
        return this.worksheet.isSetSheetFormatPr() ? this.worksheet.getSheetFormatPr() : this.worksheet.addNewSheetFormatPr();
    }
    
    public CellStyle getColumnStyle(final int column) {
        final int idx = this.columnHelper.getColDefaultStyle(column);
        return (CellStyle)this.getWorkbook().getCellStyleAt((short)((idx == -1) ? 0 : idx));
    }
    
    public void setRightToLeft(final boolean value) {
        final CTSheetView dsv = this.getDefaultSheetView(true);
        assert dsv != null;
        dsv.setRightToLeft(value);
    }
    
    public boolean isRightToLeft() {
        final CTSheetView dsv = this.getDefaultSheetView(false);
        return dsv != null && dsv.getRightToLeft();
    }
    
    public boolean getDisplayGuts() {
        final CTSheetPr sheetPr = this.getSheetTypeSheetPr();
        final CTOutlinePr outlinePr = (sheetPr.getOutlinePr() == null) ? CTOutlinePr.Factory.newInstance() : sheetPr.getOutlinePr();
        return outlinePr.getShowOutlineSymbols();
    }
    
    public void setDisplayGuts(final boolean value) {
        final CTSheetPr sheetPr = this.getSheetTypeSheetPr();
        final CTOutlinePr outlinePr = (sheetPr.getOutlinePr() == null) ? sheetPr.addNewOutlinePr() : sheetPr.getOutlinePr();
        outlinePr.setShowOutlineSymbols(value);
    }
    
    public boolean isDisplayZeros() {
        final CTSheetView dsv = this.getDefaultSheetView(false);
        return dsv == null || dsv.getShowZeros();
    }
    
    public void setDisplayZeros(final boolean value) {
        final CTSheetView view = this.getDefaultSheetView(true);
        assert view != null;
        view.setShowZeros(value);
    }
    
    public int getFirstRowNum() {
        return this._rows.isEmpty() ? -1 : this._rows.firstKey();
    }
    
    public boolean getFitToPage() {
        final CTSheetPr sheetPr = this.getSheetTypeSheetPr();
        final CTPageSetUpPr psSetup = (sheetPr == null || !sheetPr.isSetPageSetUpPr()) ? CTPageSetUpPr.Factory.newInstance() : sheetPr.getPageSetUpPr();
        return psSetup.getFitToPage();
    }
    
    private CTSheetPr getSheetTypeSheetPr() {
        if (this.worksheet.getSheetPr() == null) {
            this.worksheet.setSheetPr(CTSheetPr.Factory.newInstance());
        }
        return this.worksheet.getSheetPr();
    }
    
    private CTHeaderFooter getSheetTypeHeaderFooter() {
        if (this.worksheet.getHeaderFooter() == null) {
            this.worksheet.setHeaderFooter(CTHeaderFooter.Factory.newInstance());
        }
        return this.worksheet.getHeaderFooter();
    }
    
    public Footer getFooter() {
        return this.getOddFooter();
    }
    
    public Header getHeader() {
        return this.getOddHeader();
    }
    
    public Footer getOddFooter() {
        return (Footer)new XSSFOddFooter(this.getSheetTypeHeaderFooter());
    }
    
    public Footer getEvenFooter() {
        return (Footer)new XSSFEvenFooter(this.getSheetTypeHeaderFooter());
    }
    
    public Footer getFirstFooter() {
        return (Footer)new XSSFFirstFooter(this.getSheetTypeHeaderFooter());
    }
    
    public Header getOddHeader() {
        return (Header)new XSSFOddHeader(this.getSheetTypeHeaderFooter());
    }
    
    public Header getEvenHeader() {
        return (Header)new XSSFEvenHeader(this.getSheetTypeHeaderFooter());
    }
    
    public Header getFirstHeader() {
        return (Header)new XSSFFirstHeader(this.getSheetTypeHeaderFooter());
    }
    
    public boolean getHorizontallyCenter() {
        final CTPrintOptions opts = this.worksheet.getPrintOptions();
        return opts != null && opts.getHorizontalCentered();
    }
    
    public int getLastRowNum() {
        return this._rows.isEmpty() ? -1 : this._rows.lastKey();
    }
    
    public short getLeftCol() {
        final String cellRef = this.worksheet.getSheetViews().getSheetViewArray(0).getTopLeftCell();
        if (cellRef == null) {
            return 0;
        }
        final CellReference cellReference = new CellReference(cellRef);
        return cellReference.getCol();
    }
    
    public double getMargin(final short margin) {
        if (!this.worksheet.isSetPageMargins()) {
            return 0.0;
        }
        final CTPageMargins pageMargins = this.worksheet.getPageMargins();
        switch (margin) {
            case 0: {
                return pageMargins.getLeft();
            }
            case 1: {
                return pageMargins.getRight();
            }
            case 2: {
                return pageMargins.getTop();
            }
            case 3: {
                return pageMargins.getBottom();
            }
            case 4: {
                return pageMargins.getHeader();
            }
            case 5: {
                return pageMargins.getFooter();
            }
            default: {
                throw new IllegalArgumentException("Unknown margin constant:  " + margin);
            }
        }
    }
    
    public void setMargin(final short margin, final double size) {
        final CTPageMargins pageMargins = this.worksheet.isSetPageMargins() ? this.worksheet.getPageMargins() : this.worksheet.addNewPageMargins();
        switch (margin) {
            case 0: {
                pageMargins.setLeft(size);
                break;
            }
            case 1: {
                pageMargins.setRight(size);
                break;
            }
            case 2: {
                pageMargins.setTop(size);
                break;
            }
            case 3: {
                pageMargins.setBottom(size);
                break;
            }
            case 4: {
                pageMargins.setHeader(size);
                break;
            }
            case 5: {
                pageMargins.setFooter(size);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown margin constant:  " + margin);
            }
        }
    }
    
    public CellRangeAddress getMergedRegion(final int index) {
        final CTMergeCells ctMergeCells = this.worksheet.getMergeCells();
        if (ctMergeCells == null) {
            throw new IllegalStateException("This worksheet does not contain merged regions");
        }
        final CTMergeCell ctMergeCell = ctMergeCells.getMergeCellArray(index);
        final String ref = ctMergeCell.getRef();
        return CellRangeAddress.valueOf(ref);
    }
    
    public List<CellRangeAddress> getMergedRegions() {
        final List<CellRangeAddress> addresses = new ArrayList<CellRangeAddress>();
        final CTMergeCells ctMergeCells = this.worksheet.getMergeCells();
        if (ctMergeCells == null) {
            return addresses;
        }
        for (final CTMergeCell ctMergeCell : ctMergeCells.getMergeCellArray()) {
            final String ref = ctMergeCell.getRef();
            addresses.add(CellRangeAddress.valueOf(ref));
        }
        return addresses;
    }
    
    public int getNumMergedRegions() {
        final CTMergeCells ctMergeCells = this.worksheet.getMergeCells();
        return (ctMergeCells == null) ? 0 : ctMergeCells.sizeOfMergeCellArray();
    }
    
    public int getNumHyperlinks() {
        return this.hyperlinks.size();
    }
    
    public PaneInformation getPaneInformation() {
        final CTPane pane = this.getPane(false);
        if (pane == null) {
            return null;
        }
        short row = 0;
        short col = 0;
        if (pane.isSetTopLeftCell()) {
            final CellReference cellRef = new CellReference(pane.getTopLeftCell());
            row = (short)cellRef.getRow();
            col = cellRef.getCol();
        }
        final short x = (short)pane.getXSplit();
        final short y = (short)pane.getYSplit();
        final byte active = (byte)(pane.getActivePane().intValue() - 1);
        final boolean frozen = pane.getState() == STPaneState.FROZEN;
        return new PaneInformation(x, y, row, col, active, frozen);
    }
    
    public int getPhysicalNumberOfRows() {
        return this._rows.size();
    }
    
    public XSSFPrintSetup getPrintSetup() {
        return new XSSFPrintSetup(this.worksheet);
    }
    
    public boolean getProtect() {
        return this.isSheetLocked();
    }
    
    public void protectSheet(final String password) {
        if (password != null) {
            final CTSheetProtection sheetProtection = this.safeGetProtectionField();
            this.setSheetPassword(password, null);
            sheetProtection.setSheet(true);
            sheetProtection.setScenarios(true);
            sheetProtection.setObjects(true);
        }
        else {
            this.worksheet.unsetSheetProtection();
        }
    }
    
    public void setSheetPassword(final String password, final HashAlgorithm hashAlgo) {
        if (password == null && !this.isSheetProtectionEnabled()) {
            return;
        }
        XSSFPasswordHelper.setPassword((XmlObject)this.safeGetProtectionField(), password, hashAlgo, null);
    }
    
    public boolean validateSheetPassword(final String password) {
        if (!this.isSheetProtectionEnabled()) {
            return password == null;
        }
        return XSSFPasswordHelper.validatePassword((XmlObject)this.safeGetProtectionField(), password, null);
    }
    
    public XSSFRow getRow(final int rownum) {
        final Integer rownumI = rownum;
        return this._rows.get(rownumI);
    }
    
    private List<XSSFRow> getRows(final int startRowNum, final int endRowNum, final boolean createRowIfMissing) {
        if (startRowNum > endRowNum) {
            throw new IllegalArgumentException("getRows: startRowNum must be less than or equal to endRowNum");
        }
        final List<XSSFRow> rows = new ArrayList<XSSFRow>();
        if (createRowIfMissing) {
            for (int i = startRowNum; i <= endRowNum; ++i) {
                XSSFRow row = this.getRow(i);
                if (row == null) {
                    row = this.createRow(i);
                }
                rows.add(row);
            }
        }
        else {
            final Integer startI = startRowNum;
            final Integer endI = endRowNum + 1;
            final Collection<XSSFRow> inclusive = this._rows.subMap(startI, endI).values();
            rows.addAll(inclusive);
        }
        return rows;
    }
    
    public int[] getRowBreaks() {
        return this.worksheet.isSetRowBreaks() ? this.getBreaks(this.worksheet.getRowBreaks()) : new int[0];
    }
    
    public boolean getRowSumsBelow() {
        final CTSheetPr sheetPr = this.worksheet.getSheetPr();
        final CTOutlinePr outlinePr = (sheetPr != null && sheetPr.isSetOutlinePr()) ? sheetPr.getOutlinePr() : null;
        return outlinePr == null || outlinePr.getSummaryBelow();
    }
    
    public void setRowSumsBelow(final boolean value) {
        this.ensureOutlinePr().setSummaryBelow(value);
    }
    
    public boolean getRowSumsRight() {
        final CTSheetPr sheetPr = this.worksheet.getSheetPr();
        final CTOutlinePr outlinePr = (sheetPr != null && sheetPr.isSetOutlinePr()) ? sheetPr.getOutlinePr() : CTOutlinePr.Factory.newInstance();
        return outlinePr.getSummaryRight();
    }
    
    public void setRowSumsRight(final boolean value) {
        this.ensureOutlinePr().setSummaryRight(value);
    }
    
    private CTOutlinePr ensureOutlinePr() {
        final CTSheetPr sheetPr = this.worksheet.isSetSheetPr() ? this.worksheet.getSheetPr() : this.worksheet.addNewSheetPr();
        return sheetPr.isSetOutlinePr() ? sheetPr.getOutlinePr() : sheetPr.addNewOutlinePr();
    }
    
    public boolean getScenarioProtect() {
        return this.worksheet.isSetSheetProtection() && this.worksheet.getSheetProtection().getScenarios();
    }
    
    public short getTopRow() {
        final CTSheetView dsv = this.getDefaultSheetView(false);
        final String cellRef = (dsv == null) ? null : dsv.getTopLeftCell();
        if (cellRef == null) {
            return 0;
        }
        return (short)new CellReference(cellRef).getRow();
    }
    
    public boolean getVerticallyCenter() {
        final CTPrintOptions opts = this.worksheet.getPrintOptions();
        return opts != null && opts.getVerticalCentered();
    }
    
    public void groupColumn(final int fromColumn, final int toColumn) {
        this.groupColumn1Based(fromColumn + 1, toColumn + 1);
    }
    
    private void groupColumn1Based(final int fromColumn, final int toColumn) {
        final CTCols ctCols = this.worksheet.getColsArray(0);
        final CTCol ctCol = CTCol.Factory.newInstance();
        CTCol fixCol_before = this.columnHelper.getColumn1Based(toColumn, false);
        if (fixCol_before != null) {
            fixCol_before = (CTCol)fixCol_before.copy();
        }
        ctCol.setMin((long)fromColumn);
        ctCol.setMax((long)toColumn);
        this.columnHelper.addCleanColIntoCols(ctCols, ctCol);
        final CTCol fixCol_after = this.columnHelper.getColumn1Based(toColumn, false);
        if (fixCol_before != null && fixCol_after != null) {
            this.columnHelper.setColumnAttributes(fixCol_before, fixCol_after);
        }
        CTCol col;
        for (int index = fromColumn; index <= toColumn; index = Math.toIntExact(col.getMax()), ++index) {
            col = this.columnHelper.getColumn1Based(index, false);
            final short outlineLevel = col.getOutlineLevel();
            col.setOutlineLevel((short)(outlineLevel + 1));
        }
        this.worksheet.setColsArray(0, ctCols);
        this.setSheetFormatPrOutlineLevelCol();
    }
    
    private void setColWidthAttribute(final CTCols ctCols) {
        for (final CTCol col : ctCols.getColArray()) {
            if (!col.isSetWidth()) {
                col.setWidth((double)this.getDefaultColumnWidth());
                col.setCustomWidth(false);
            }
        }
    }
    
    public void groupRow(final int fromRow, final int toRow) {
        for (int i = fromRow; i <= toRow; ++i) {
            XSSFRow xrow = this.getRow(i);
            if (xrow == null) {
                xrow = this.createRow(i);
            }
            final CTRow ctrow = xrow.getCTRow();
            final short outlineLevel = ctrow.getOutlineLevel();
            ctrow.setOutlineLevel((short)(outlineLevel + 1));
        }
        this.setSheetFormatPrOutlineLevelRow();
    }
    
    private short getMaxOutlineLevelRows() {
        int outlineLevel = 0;
        for (final XSSFRow xrow : this._rows.values()) {
            outlineLevel = Math.max(outlineLevel, xrow.getCTRow().getOutlineLevel());
        }
        return (short)outlineLevel;
    }
    
    private short getMaxOutlineLevelCols() {
        final CTCols ctCols = this.worksheet.getColsArray(0);
        int outlineLevel = 0;
        for (final CTCol col : ctCols.getColArray()) {
            outlineLevel = Math.max(outlineLevel, col.getOutlineLevel());
        }
        return (short)outlineLevel;
    }
    
    public boolean isColumnBroken(final int column) {
        for (final int colBreak : this.getColumnBreaks()) {
            if (colBreak == column) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isColumnHidden(final int columnIndex) {
        final CTCol col = this.columnHelper.getColumn(columnIndex, false);
        return col != null && col.getHidden();
    }
    
    public boolean isDisplayFormulas() {
        final CTSheetView dsv = this.getDefaultSheetView(false);
        return dsv != null && dsv.getShowFormulas();
    }
    
    public boolean isDisplayGridlines() {
        final CTSheetView dsv = this.getDefaultSheetView(false);
        return dsv == null || dsv.getShowGridLines();
    }
    
    public void setDisplayGridlines(final boolean show) {
        final CTSheetView dsv = this.getDefaultSheetView(true);
        assert dsv != null;
        dsv.setShowGridLines(show);
    }
    
    public boolean isDisplayRowColHeadings() {
        final CTSheetView dsv = this.getDefaultSheetView(false);
        return dsv == null || dsv.getShowRowColHeaders();
    }
    
    public void setDisplayRowColHeadings(final boolean show) {
        final CTSheetView dsv = this.getDefaultSheetView(true);
        assert dsv != null;
        dsv.setShowRowColHeaders(show);
    }
    
    public boolean isPrintGridlines() {
        final CTPrintOptions opts = this.worksheet.getPrintOptions();
        return opts != null && opts.getGridLines();
    }
    
    public void setPrintGridlines(final boolean value) {
        final CTPrintOptions opts = this.worksheet.isSetPrintOptions() ? this.worksheet.getPrintOptions() : this.worksheet.addNewPrintOptions();
        opts.setGridLines(value);
    }
    
    public boolean isPrintRowAndColumnHeadings() {
        final CTPrintOptions opts = this.worksheet.getPrintOptions();
        return opts != null && opts.getHeadings();
    }
    
    public void setPrintRowAndColumnHeadings(final boolean value) {
        final CTPrintOptions opts = this.worksheet.isSetPrintOptions() ? this.worksheet.getPrintOptions() : this.worksheet.addNewPrintOptions();
        opts.setHeadings(value);
    }
    
    public boolean isRowBroken(final int row) {
        for (final int rowBreak : this.getRowBreaks()) {
            if (rowBreak == row) {
                return true;
            }
        }
        return false;
    }
    
    private void setBreak(final int id, final CTPageBreak ctPgBreak, final int lastIndex) {
        final CTBreak brk = ctPgBreak.addNewBrk();
        brk.setId((long)(id + 1));
        brk.setMan(true);
        brk.setMax((long)lastIndex);
        final int nPageBreaks = ctPgBreak.sizeOfBrkArray();
        ctPgBreak.setCount((long)nPageBreaks);
        ctPgBreak.setManualBreakCount((long)nPageBreaks);
    }
    
    public void setRowBreak(final int row) {
        if (!this.isRowBroken(row)) {
            final CTPageBreak pgBreak = this.worksheet.isSetRowBreaks() ? this.worksheet.getRowBreaks() : this.worksheet.addNewRowBreaks();
            this.setBreak(row, pgBreak, SpreadsheetVersion.EXCEL2007.getLastColumnIndex());
        }
    }
    
    public void removeColumnBreak(final int column) {
        if (this.worksheet.isSetColBreaks()) {
            this.removeBreak(column, this.worksheet.getColBreaks());
        }
    }
    
    public void removeMergedRegion(final int index) {
        if (!this.worksheet.isSetMergeCells()) {
            return;
        }
        final CTMergeCells ctMergeCells = this.worksheet.getMergeCells();
        final int size = ctMergeCells.sizeOfMergeCellArray();
        assert 0 <= index && index < size;
        if (size > 1) {
            ctMergeCells.removeMergeCell(index);
        }
        else {
            this.worksheet.unsetMergeCells();
        }
    }
    
    public void removeMergedRegions(final Collection<Integer> indices) {
        if (!this.worksheet.isSetMergeCells()) {
            return;
        }
        final CTMergeCells ctMergeCells = this.worksheet.getMergeCells();
        final List<CTMergeCell> newMergeCells = new ArrayList<CTMergeCell>(ctMergeCells.sizeOfMergeCellArray());
        int idx = 0;
        for (final CTMergeCell mc : ctMergeCells.getMergeCellArray()) {
            if (!indices.contains(idx++)) {
                newMergeCells.add(mc);
            }
        }
        if (newMergeCells.isEmpty()) {
            this.worksheet.unsetMergeCells();
        }
        else {
            final CTMergeCell[] newMergeCellsArray = new CTMergeCell[newMergeCells.size()];
            ctMergeCells.setMergeCellArray((CTMergeCell[])newMergeCells.toArray(newMergeCellsArray));
        }
    }
    
    public void removeRow(final Row row) {
        if (row.getSheet() != this) {
            throw new IllegalArgumentException("Specified row does not belong to this sheet");
        }
        final ArrayList<XSSFCell> cellsToDelete = new ArrayList<XSSFCell>();
        for (final Cell cell : row) {
            cellsToDelete.add((XSSFCell)cell);
        }
        for (final XSSFCell cell2 : cellsToDelete) {
            row.removeCell((Cell)cell2);
        }
        final int rowNum = row.getRowNum();
        final Integer rowNumI = rowNum;
        final int idx = this._rows.headMap(rowNumI).size();
        this._rows.remove(rowNumI);
        this.worksheet.getSheetData().removeRow(idx);
        if (this.sheetComments != null) {
            for (final CellAddress ref : this.getCellComments().keySet()) {
                if (ref.getRow() == rowNum) {
                    this.sheetComments.removeComment(ref);
                }
            }
        }
    }
    
    public void removeRowBreak(final int row) {
        if (this.worksheet.isSetRowBreaks()) {
            this.removeBreak(row, this.worksheet.getRowBreaks());
        }
    }
    
    public void setForceFormulaRecalculation(final boolean value) {
        final CTCalcPr calcPr = this.getWorkbook().getCTWorkbook().getCalcPr();
        if (this.worksheet.isSetSheetCalcPr()) {
            final CTSheetCalcPr calc = this.worksheet.getSheetCalcPr();
            calc.setFullCalcOnLoad(value);
        }
        else if (value) {
            final CTSheetCalcPr calc = this.worksheet.addNewSheetCalcPr();
            calc.setFullCalcOnLoad(value);
        }
        if (value && calcPr != null && calcPr.getCalcMode() == STCalcMode.MANUAL) {
            calcPr.setCalcMode(STCalcMode.AUTO);
        }
    }
    
    public boolean getForceFormulaRecalculation() {
        if (this.worksheet.isSetSheetCalcPr()) {
            final CTSheetCalcPr calc = this.worksheet.getSheetCalcPr();
            return calc.getFullCalcOnLoad();
        }
        return false;
    }
    
    public Iterator<Row> rowIterator() {
        return (Iterator<Row>)this._rows.values().iterator();
    }
    
    public Iterator<Row> iterator() {
        return this.rowIterator();
    }
    
    public boolean getAutobreaks() {
        final CTSheetPr sheetPr = this.getSheetTypeSheetPr();
        final CTPageSetUpPr psSetup = (sheetPr == null || !sheetPr.isSetPageSetUpPr()) ? CTPageSetUpPr.Factory.newInstance() : sheetPr.getPageSetUpPr();
        return psSetup.getAutoPageBreaks();
    }
    
    public void setAutobreaks(final boolean value) {
        final CTSheetPr sheetPr = this.getSheetTypeSheetPr();
        final CTPageSetUpPr psSetup = sheetPr.isSetPageSetUpPr() ? sheetPr.getPageSetUpPr() : sheetPr.addNewPageSetUpPr();
        psSetup.setAutoPageBreaks(value);
    }
    
    public void setColumnBreak(final int column) {
        if (!this.isColumnBroken(column)) {
            final CTPageBreak pgBreak = this.worksheet.isSetColBreaks() ? this.worksheet.getColBreaks() : this.worksheet.addNewColBreaks();
            this.setBreak(column, pgBreak, SpreadsheetVersion.EXCEL2007.getLastRowIndex());
        }
    }
    
    public void setColumnGroupCollapsed(final int columnNumber, final boolean collapsed) {
        if (collapsed) {
            this.collapseColumn(columnNumber);
        }
        else {
            this.expandColumn(columnNumber);
        }
    }
    
    private void collapseColumn(final int columnNumber) {
        final CTCols cols = this.worksheet.getColsArray(0);
        final CTCol col = this.columnHelper.getColumn(columnNumber, false);
        final int colInfoIx = this.columnHelper.getIndexOfColumn(cols, col);
        if (colInfoIx == -1) {
            return;
        }
        final int groupStartColInfoIx = this.findStartOfColumnOutlineGroup(colInfoIx);
        final CTCol columnInfo = cols.getColArray(groupStartColInfoIx);
        final int lastColMax = this.setGroupHidden(groupStartColInfoIx, columnInfo.getOutlineLevel(), true);
        this.setColumn(lastColMax + 1, 0, null, null, Boolean.TRUE);
    }
    
    private void setColumn(final int targetColumnIx, final Integer style, final Integer level, final Boolean hidden, final Boolean collapsed) {
        final CTCols cols = this.worksheet.getColsArray(0);
        CTCol ci = null;
        for (final CTCol tci : cols.getColArray()) {
            final long tciMin = tci.getMin();
            final long tciMax = tci.getMax();
            if (tciMin >= targetColumnIx && tciMax <= targetColumnIx) {
                ci = tci;
                break;
            }
            if (tciMin > targetColumnIx) {
                break;
            }
        }
        if (ci == null) {
            final CTCol nci = CTCol.Factory.newInstance();
            nci.setMin((long)targetColumnIx);
            nci.setMax((long)targetColumnIx);
            this.unsetCollapsed(collapsed, nci);
            this.columnHelper.addCleanColIntoCols(cols, nci);
            return;
        }
        final boolean styleChanged = style != null && ci.getStyle() != style;
        final boolean levelChanged = level != null && ci.getOutlineLevel() != level;
        final boolean hiddenChanged = hidden != null && ci.getHidden() != hidden;
        final boolean collapsedChanged = collapsed != null && ci.getCollapsed() != collapsed;
        final boolean columnChanged = levelChanged || hiddenChanged || collapsedChanged || styleChanged;
        if (!columnChanged) {
            return;
        }
        final long ciMin = ci.getMin();
        final long ciMax = ci.getMax();
        if (ciMin == targetColumnIx && ciMax == targetColumnIx) {
            this.unsetCollapsed(collapsed, ci);
            return;
        }
        if (ciMin == targetColumnIx || ciMax == targetColumnIx) {
            if (ciMin == targetColumnIx) {
                ci.setMin((long)(targetColumnIx + 1));
            }
            else {
                ci.setMax((long)(targetColumnIx - 1));
            }
            final CTCol nci2 = this.columnHelper.cloneCol(cols, ci);
            nci2.setMin((long)targetColumnIx);
            this.unsetCollapsed(collapsed, nci2);
            this.columnHelper.addCleanColIntoCols(cols, nci2);
        }
        else {
            final CTCol ciMid = this.columnHelper.cloneCol(cols, ci);
            final CTCol ciEnd = this.columnHelper.cloneCol(cols, ci);
            final int lastcolumn = Math.toIntExact(ciMax);
            ci.setMax((long)(targetColumnIx - 1));
            ciMid.setMin((long)targetColumnIx);
            ciMid.setMax((long)targetColumnIx);
            this.unsetCollapsed(collapsed, ciMid);
            this.columnHelper.addCleanColIntoCols(cols, ciMid);
            ciEnd.setMin((long)(targetColumnIx + 1));
            ciEnd.setMax((long)lastcolumn);
            this.columnHelper.addCleanColIntoCols(cols, ciEnd);
        }
    }
    
    private void unsetCollapsed(final Boolean collapsed, final CTCol ci) {
        if (collapsed != null && collapsed) {
            ci.setCollapsed(true);
        }
        else {
            ci.unsetCollapsed();
        }
    }
    
    private int setGroupHidden(final int pIdx, final int level, final boolean hidden) {
        final CTCols cols = this.worksheet.getColsArray(0);
        int idx = pIdx;
        final CTCol[] colArray = cols.getColArray();
        CTCol columnInfo = colArray[idx];
        while (idx < colArray.length) {
            columnInfo.setHidden(hidden);
            if (idx + 1 < colArray.length) {
                final CTCol nextColumnInfo = colArray[idx + 1];
                if (!this.isAdjacentBefore(columnInfo, nextColumnInfo)) {
                    break;
                }
                if (nextColumnInfo.getOutlineLevel() < level) {
                    break;
                }
                columnInfo = nextColumnInfo;
            }
            ++idx;
        }
        return Math.toIntExact(columnInfo.getMax());
    }
    
    private boolean isAdjacentBefore(final CTCol col, final CTCol otherCol) {
        return col.getMax() == otherCol.getMin() - 1L;
    }
    
    private int findStartOfColumnOutlineGroup(final int pIdx) {
        final CTCols cols = this.worksheet.getColsArray(0);
        final CTCol[] colArray = cols.getColArray();
        CTCol columnInfo = colArray[pIdx];
        final int level = columnInfo.getOutlineLevel();
        int idx;
        CTCol prevColumnInfo;
        for (idx = pIdx; idx != 0; --idx, columnInfo = prevColumnInfo) {
            prevColumnInfo = colArray[idx - 1];
            if (!this.isAdjacentBefore(prevColumnInfo, columnInfo)) {
                break;
            }
            if (prevColumnInfo.getOutlineLevel() < level) {
                break;
            }
        }
        return idx;
    }
    
    private int findEndOfColumnOutlineGroup(final int colInfoIndex) {
        final CTCols cols = this.worksheet.getColsArray(0);
        final CTCol[] colArray = cols.getColArray();
        CTCol columnInfo = colArray[colInfoIndex];
        final int level = columnInfo.getOutlineLevel();
        int idx = colInfoIndex;
        CTCol nextColumnInfo;
        for (int lastIdx = colArray.length - 1; idx < lastIdx; ++idx, columnInfo = nextColumnInfo) {
            nextColumnInfo = colArray[idx + 1];
            if (!this.isAdjacentBefore(columnInfo, nextColumnInfo)) {
                break;
            }
            if (nextColumnInfo.getOutlineLevel() < level) {
                break;
            }
        }
        return idx;
    }
    
    private void expandColumn(final int columnIndex) {
        final CTCols cols = this.worksheet.getColsArray(0);
        final CTCol col = this.columnHelper.getColumn(columnIndex, false);
        final int colInfoIx = this.columnHelper.getIndexOfColumn(cols, col);
        final int idx = this.findColInfoIdx(Math.toIntExact(col.getMax()), colInfoIx);
        if (idx == -1) {
            return;
        }
        if (!this.isColumnGroupCollapsed(idx)) {
            return;
        }
        final int startIdx = this.findStartOfColumnOutlineGroup(idx);
        final int endIdx = this.findEndOfColumnOutlineGroup(idx);
        final CTCol[] colArray = cols.getColArray();
        final CTCol columnInfo = colArray[endIdx];
        if (!this.isColumnGroupHiddenByParent(idx)) {
            final short outlineLevel = columnInfo.getOutlineLevel();
            boolean nestedGroup = false;
            for (int i = startIdx; i <= endIdx; ++i) {
                final CTCol ci = colArray[i];
                if (outlineLevel == ci.getOutlineLevel()) {
                    ci.unsetHidden();
                    if (nestedGroup) {
                        nestedGroup = false;
                        ci.setCollapsed(true);
                    }
                }
                else {
                    nestedGroup = true;
                }
            }
        }
        this.setColumn(Math.toIntExact(columnInfo.getMax() + 1L), null, null, Boolean.FALSE, Boolean.FALSE);
    }
    
    private boolean isColumnGroupHiddenByParent(final int idx) {
        final CTCols cols = this.worksheet.getColsArray(0);
        int endLevel = 0;
        boolean endHidden = false;
        final int endOfOutlineGroupIdx = this.findEndOfColumnOutlineGroup(idx);
        final CTCol[] colArray = cols.getColArray();
        if (endOfOutlineGroupIdx < colArray.length - 1) {
            final CTCol nextInfo = colArray[endOfOutlineGroupIdx + 1];
            if (this.isAdjacentBefore(colArray[endOfOutlineGroupIdx], nextInfo)) {
                endLevel = nextInfo.getOutlineLevel();
                endHidden = nextInfo.getHidden();
            }
        }
        int startLevel = 0;
        boolean startHidden = false;
        final int startOfOutlineGroupIdx = this.findStartOfColumnOutlineGroup(idx);
        if (startOfOutlineGroupIdx > 0) {
            final CTCol prevInfo = colArray[startOfOutlineGroupIdx - 1];
            if (this.isAdjacentBefore(prevInfo, colArray[startOfOutlineGroupIdx])) {
                startLevel = prevInfo.getOutlineLevel();
                startHidden = prevInfo.getHidden();
            }
        }
        if (endLevel > startLevel) {
            return endHidden;
        }
        return startHidden;
    }
    
    private int findColInfoIdx(final int columnValue, final int fromColInfoIdx) {
        final CTCols cols = this.worksheet.getColsArray(0);
        if (columnValue < 0) {
            throw new IllegalArgumentException("column parameter out of range: " + columnValue);
        }
        if (fromColInfoIdx < 0) {
            throw new IllegalArgumentException("fromIdx parameter out of range: " + fromColInfoIdx);
        }
        final CTCol[] colArray = cols.getColArray();
        for (int k = fromColInfoIdx; k < colArray.length; ++k) {
            final CTCol ci = colArray[k];
            if (this.containsColumn(ci, columnValue)) {
                return k;
            }
            if (ci.getMin() > fromColInfoIdx) {
                break;
            }
        }
        return -1;
    }
    
    private boolean containsColumn(final CTCol col, final int columnIndex) {
        return col.getMin() <= columnIndex && columnIndex <= col.getMax();
    }
    
    private boolean isColumnGroupCollapsed(final int idx) {
        final CTCols cols = this.worksheet.getColsArray(0);
        final CTCol[] colArray = cols.getColArray();
        final int endOfOutlineGroupIdx = this.findEndOfColumnOutlineGroup(idx);
        final int nextColInfoIx = endOfOutlineGroupIdx + 1;
        if (nextColInfoIx >= colArray.length) {
            return false;
        }
        final CTCol nextColInfo = colArray[nextColInfoIx];
        final CTCol col = colArray[endOfOutlineGroupIdx];
        return this.isAdjacentBefore(col, nextColInfo) && nextColInfo.getCollapsed();
    }
    
    public void setColumnHidden(final int columnIndex, final boolean hidden) {
        this.columnHelper.setColHidden(columnIndex, hidden);
    }
    
    public void setColumnWidth(final int columnIndex, final int width) {
        if (width > 65280) {
            throw new IllegalArgumentException("The maximum column width for an individual cell is 255 characters.");
        }
        this.columnHelper.setColWidth(columnIndex, width / 256.0);
        this.columnHelper.setCustomWidth(columnIndex, true);
    }
    
    public void setDefaultColumnStyle(final int column, final CellStyle style) {
        this.columnHelper.setColDefaultStyle(column, style);
    }
    
    public void setDefaultColumnWidth(final int width) {
        this.getSheetTypeSheetFormatPr().setBaseColWidth((long)width);
    }
    
    public void setDefaultRowHeight(final short height) {
        this.setDefaultRowHeightInPoints(height / 20.0f);
    }
    
    public void setDefaultRowHeightInPoints(final float height) {
        final CTSheetFormatPr pr = this.getSheetTypeSheetFormatPr();
        pr.setDefaultRowHeight((double)height);
        pr.setCustomHeight(true);
    }
    
    public void setDisplayFormulas(final boolean show) {
        final CTSheetView dsv = this.getDefaultSheetView(true);
        assert dsv != null;
        dsv.setShowFormulas(show);
    }
    
    public void setFitToPage(final boolean b) {
        this.getSheetTypePageSetUpPr().setFitToPage(b);
    }
    
    public void setHorizontallyCenter(final boolean value) {
        final CTPrintOptions opts = this.worksheet.isSetPrintOptions() ? this.worksheet.getPrintOptions() : this.worksheet.addNewPrintOptions();
        opts.setHorizontalCentered(value);
    }
    
    public void setVerticallyCenter(final boolean value) {
        final CTPrintOptions opts = this.worksheet.isSetPrintOptions() ? this.worksheet.getPrintOptions() : this.worksheet.addNewPrintOptions();
        opts.setVerticalCentered(value);
    }
    
    public void setRowGroupCollapsed(final int rowIndex, final boolean collapse) {
        if (collapse) {
            this.collapseRow(rowIndex);
        }
        else {
            this.expandRow(rowIndex);
        }
    }
    
    private void collapseRow(final int rowIndex) {
        final XSSFRow row = this.getRow(rowIndex);
        if (row != null) {
            final int startRow = this.findStartOfRowOutlineGroup(rowIndex);
            final int lastRow = this.writeHidden(row, startRow, true);
            if (this.getRow(lastRow) != null) {
                this.getRow(lastRow).getCTRow().setCollapsed(true);
            }
            else {
                final XSSFRow newRow = this.createRow(lastRow);
                newRow.getCTRow().setCollapsed(true);
            }
        }
    }
    
    private int findStartOfRowOutlineGroup(final int rowIndex) {
        final short level = this.getRow(rowIndex).getCTRow().getOutlineLevel();
        int currentRow;
        for (currentRow = rowIndex; this.getRow(currentRow) != null; --currentRow) {
            if (this.getRow(currentRow).getCTRow().getOutlineLevel() < level) {
                return currentRow + 1;
            }
        }
        return currentRow;
    }
    
    private int writeHidden(XSSFRow xRow, int rowIndex, final boolean hidden) {
        final short level = xRow.getCTRow().getOutlineLevel();
        final Iterator<Row> it = this.rowIterator();
        while (it.hasNext()) {
            xRow = (XSSFRow)it.next();
            if (xRow.getRowNum() < rowIndex) {
                continue;
            }
            if (xRow.getCTRow().getOutlineLevel() < level) {
                continue;
            }
            xRow.getCTRow().setHidden(hidden);
            ++rowIndex;
        }
        return rowIndex;
    }
    
    private void expandRow(final int rowNumber) {
        if (rowNumber == -1) {
            return;
        }
        final XSSFRow row = this.getRow(rowNumber);
        if (!row.getCTRow().isSetHidden()) {
            return;
        }
        final int startIdx = this.findStartOfRowOutlineGroup(rowNumber);
        final int endIdx = this.findEndOfRowOutlineGroup(rowNumber);
        final short level = row.getCTRow().getOutlineLevel();
        if (!this.isRowGroupHiddenByParent(rowNumber)) {
            for (int i = startIdx; i < endIdx; ++i) {
                if (level == this.getRow(i).getCTRow().getOutlineLevel()) {
                    this.getRow(i).getCTRow().unsetHidden();
                }
                else if (!this.isRowGroupCollapsed(i)) {
                    this.getRow(i).getCTRow().unsetHidden();
                }
            }
        }
        final CTRow ctRow = this.getRow(endIdx).getCTRow();
        if (ctRow.getCollapsed()) {
            ctRow.unsetCollapsed();
        }
    }
    
    public int findEndOfRowOutlineGroup(final int row) {
        short level;
        int lastRowNum;
        int currentRow;
        for (level = this.getRow(row).getCTRow().getOutlineLevel(), lastRowNum = this.getLastRowNum(), currentRow = row; currentRow < lastRowNum && this.getRow(currentRow) != null && this.getRow(currentRow).getCTRow().getOutlineLevel() >= level; ++currentRow) {}
        return currentRow;
    }
    
    private boolean isRowGroupHiddenByParent(final int row) {
        final int endOfOutlineGroupIdx = this.findEndOfRowOutlineGroup(row);
        int endLevel;
        boolean endHidden;
        if (this.getRow(endOfOutlineGroupIdx) == null) {
            endLevel = 0;
            endHidden = false;
        }
        else {
            endLevel = this.getRow(endOfOutlineGroupIdx).getCTRow().getOutlineLevel();
            endHidden = this.getRow(endOfOutlineGroupIdx).getCTRow().getHidden();
        }
        final int startOfOutlineGroupIdx = this.findStartOfRowOutlineGroup(row);
        int startLevel;
        boolean startHidden;
        if (startOfOutlineGroupIdx < 0 || this.getRow(startOfOutlineGroupIdx) == null) {
            startLevel = 0;
            startHidden = false;
        }
        else {
            startLevel = this.getRow(startOfOutlineGroupIdx).getCTRow().getOutlineLevel();
            startHidden = this.getRow(startOfOutlineGroupIdx).getCTRow().getHidden();
        }
        if (endLevel > startLevel) {
            return endHidden;
        }
        return startHidden;
    }
    
    private boolean isRowGroupCollapsed(final int row) {
        final int collapseRow = this.findEndOfRowOutlineGroup(row) + 1;
        return this.getRow(collapseRow) != null && this.getRow(collapseRow).getCTRow().getCollapsed();
    }
    
    public void setZoom(final int scale) {
        if (scale < 10 || scale > 400) {
            throw new IllegalArgumentException("Valid scale values range from 10 to 400");
        }
        final CTSheetView dsv = this.getDefaultSheetView(true);
        assert dsv != null;
        dsv.setZoomScale((long)scale);
    }
    
    public void copyRows(final List<? extends Row> srcRows, final int destStartRow, final CellCopyPolicy policy) {
        if (srcRows == null || srcRows.size() == 0) {
            throw new IllegalArgumentException("No rows to copy");
        }
        final Row srcStartRow = (Row)srcRows.get(0);
        final Row srcEndRow = (Row)srcRows.get(srcRows.size() - 1);
        if (srcStartRow == null) {
            throw new IllegalArgumentException("copyRows: First row cannot be null");
        }
        final int srcStartRowNum = srcStartRow.getRowNum();
        final int srcEndRowNum = srcEndRow.getRowNum();
        for (int size = srcRows.size(), index = 1; index < size; ++index) {
            final Row curRow = (Row)srcRows.get(index);
            if (curRow == null) {
                throw new IllegalArgumentException("srcRows may not contain null rows. Found null row at index " + index + ".");
            }
            if (srcStartRow.getSheet().getWorkbook() != curRow.getSheet().getWorkbook()) {
                throw new IllegalArgumentException("All rows in srcRows must belong to the same sheet in the same workbook. Expected all rows from same workbook (" + srcStartRow.getSheet().getWorkbook() + "). Got srcRows[" + index + "] from different workbook (" + curRow.getSheet().getWorkbook() + ").");
            }
            if (srcStartRow.getSheet() != curRow.getSheet()) {
                throw new IllegalArgumentException("All rows in srcRows must belong to the same sheet. Expected all rows from " + srcStartRow.getSheet().getSheetName() + ". Got srcRows[" + index + "] from " + curRow.getSheet().getSheetName());
            }
        }
        final CellCopyPolicy options = new CellCopyPolicy(policy);
        options.setCopyMergedRegions(false);
        int r = destStartRow;
        for (final Row srcRow : srcRows) {
            int destRowNum;
            if (policy.isCondenseRows()) {
                destRowNum = r++;
            }
            else {
                final int shift = srcRow.getRowNum() - srcStartRowNum;
                destRowNum = destStartRow + shift;
            }
            final XSSFRow destRow = this.createRow(destRowNum);
            destRow.copyRowFrom(srcRow, options);
        }
        if (policy.isCopyMergedRegions()) {
            final int shift2 = destStartRow - srcStartRowNum;
            for (final CellRangeAddress srcRegion : srcStartRow.getSheet().getMergedRegions()) {
                if (srcStartRowNum <= srcRegion.getFirstRow() && srcRegion.getLastRow() <= srcEndRowNum) {
                    final CellRangeAddress destRegion = srcRegion.copy();
                    destRegion.setFirstRow(destRegion.getFirstRow() + shift2);
                    destRegion.setLastRow(destRegion.getLastRow() + shift2);
                    this.addMergedRegion(destRegion);
                }
            }
        }
    }
    
    public void copyRows(final int srcStartRow, final int srcEndRow, final int destStartRow, final CellCopyPolicy cellCopyPolicy) {
        final List<XSSFRow> srcRows = this.getRows(srcStartRow, srcEndRow, false);
        this.copyRows((List<? extends Row>)srcRows, destStartRow, cellCopyPolicy);
    }
    
    public void shiftRows(final int startRow, final int endRow, final int n) {
        this.shiftRows(startRow, endRow, n, false, false);
    }
    
    public void shiftRows(final int startRow, final int endRow, final int n, final boolean copyRowHeight, final boolean resetOriginalRowHeight) {
        final XSSFVMLDrawing vml = this.getVMLDrawing(false);
        final int sheetIndex = this.getWorkbook().getSheetIndex((Sheet)this);
        final String sheetName = this.getWorkbook().getSheetName(sheetIndex);
        final FormulaShifter formulaShifter = FormulaShifter.createForRowShift(sheetIndex, sheetName, startRow, endRow, n, SpreadsheetVersion.EXCEL2007);
        this.removeOverwritten(vml, startRow, endRow, n);
        this.shiftCommentsAndRows(vml, startRow, endRow, n);
        final XSSFRowShifter rowShifter = new XSSFRowShifter(this);
        rowShifter.shiftMergedRegions(startRow, endRow, n);
        rowShifter.updateNamedRanges(formulaShifter);
        rowShifter.updateFormulas(formulaShifter);
        rowShifter.updateConditionalFormatting(formulaShifter);
        rowShifter.updateHyperlinks(formulaShifter);
        this.rebuildRows();
    }
    
    public void shiftColumns(final int startColumn, final int endColumn, final int n) {
        final XSSFVMLDrawing vml = this.getVMLDrawing(false);
        this.shiftCommentsForColumns(vml, startColumn, endColumn, n);
        final FormulaShifter formulaShifter = FormulaShifter.createForColumnShift(this.getWorkbook().getSheetIndex((Sheet)this), this.getSheetName(), startColumn, endColumn, n, SpreadsheetVersion.EXCEL2007);
        final XSSFColumnShifter columnShifter = new XSSFColumnShifter(this);
        columnShifter.shiftColumns(startColumn, endColumn, n);
        columnShifter.shiftMergedRegions(startColumn, startColumn, n);
        columnShifter.updateFormulas(formulaShifter);
        columnShifter.updateConditionalFormatting(formulaShifter);
        columnShifter.updateHyperlinks(formulaShifter);
        columnShifter.updateNamedRanges(formulaShifter);
        this.rebuildRows();
    }
    
    private void rebuildRows() {
        final List<XSSFRow> rowList = new ArrayList<XSSFRow>(this._rows.values());
        this._rows.clear();
        for (final XSSFRow r : rowList) {
            final Integer rownumI = new Integer(r.getRowNum());
            this._rows.put(rownumI, r);
        }
    }
    
    private void removeOverwritten(final XSSFVMLDrawing vml, final int startRow, final int endRow, final int n) {
        final Iterator<Row> it = this.rowIterator();
        while (it.hasNext()) {
            final XSSFRow row = (XSSFRow)it.next();
            final int rownum = row.getRowNum();
            if (shouldRemoveRow(startRow, endRow, n, rownum)) {
                final Integer rownumI = row.getRowNum();
                final int idx = this._rows.headMap(rownumI).size();
                this.worksheet.getSheetData().removeRow(idx);
                it.remove();
                if (this.sheetComments != null) {
                    final CTCommentList lst = this.sheetComments.getCTComments().getCommentList();
                    for (final CTComment comment : lst.getCommentArray()) {
                        final String strRef = comment.getRef();
                        final CellAddress ref = new CellAddress(strRef);
                        if (ref.getRow() == rownum) {
                            this.sheetComments.removeComment(ref);
                            vml.removeCommentShape(ref.getRow(), ref.getColumn());
                        }
                    }
                }
                if (this.hyperlinks == null) {
                    continue;
                }
                for (final XSSFHyperlink link : new ArrayList(this.hyperlinks)) {
                    final CellReference ref2 = new CellReference(link.getCellRef());
                    if (ref2.getRow() == rownum) {
                        this.hyperlinks.remove(link);
                    }
                }
            }
        }
    }
    
    private void shiftCommentsAndRows(final XSSFVMLDrawing vml, final int startRow, final int endRow, final int n) {
        final SortedMap<XSSFComment, Integer> commentsToShift = new TreeMap<XSSFComment, Integer>((o1, o2) -> {
            final int row2 = o1.getRow();
            final int row3 = o2.getRow();
            if (row2 == row3) {
                return o1.hashCode() - o2.hashCode();
            }
            else if (n > 0) {
                return (row2 < row3) ? 1 : -1;
            }
            else {
                return (row2 > row3) ? 1 : -1;
            }
        });
        final Iterator<Row> it = this.rowIterator();
        while (it.hasNext()) {
            final XSSFRow row = (XSSFRow)it.next();
            final int rownum = row.getRowNum();
            if (this.sheetComments != null) {
                final int newrownum = this.shiftedRowNum(startRow, endRow, n, rownum);
                if (newrownum != rownum) {
                    final CTCommentList lst = this.sheetComments.getCTComments().getCommentList();
                    for (final CTComment comment : lst.getCommentArray()) {
                        final String oldRef = comment.getRef();
                        final CellReference ref = new CellReference(oldRef);
                        if (ref.getRow() == rownum) {
                            final XSSFComment xssfComment = new XSSFComment(this.sheetComments, comment, (vml == null) ? null : vml.findCommentShape(rownum, ref.getCol()));
                            commentsToShift.put(xssfComment, newrownum);
                        }
                    }
                }
            }
            if (rownum >= startRow) {
                if (rownum > endRow) {
                    continue;
                }
                row.shift(n);
            }
        }
        for (final Map.Entry<XSSFComment, Integer> entry : commentsToShift.entrySet()) {
            entry.getKey().setRow(entry.getValue());
        }
        this.rebuildRows();
    }
    
    private int shiftedRowNum(final int startRow, final int endRow, final int n, final int rownum) {
        if (rownum < startRow && (n > 0 || startRow - rownum > n)) {
            return rownum;
        }
        if (rownum > endRow && (n < 0 || rownum - endRow > n)) {
            return rownum;
        }
        if (rownum < startRow) {
            return rownum + (endRow - startRow);
        }
        if (rownum > endRow) {
            return rownum - (endRow - startRow);
        }
        return rownum + n;
    }
    
    private void shiftCommentsForColumns(final XSSFVMLDrawing vml, final int startColumnIndex, final int endColumnIndex, final int n) {
        final SortedMap<XSSFComment, Integer> commentsToShift = new TreeMap<XSSFComment, Integer>((o1, o2) -> {
            final int column1 = o1.getColumn();
            final int column2 = o2.getColumn();
            if (column1 == column2) {
                return o1.hashCode() - o2.hashCode();
            }
            else if (n > 0) {
                return (column1 < column2) ? 1 : -1;
            }
            else {
                return (column1 > column2) ? 1 : -1;
            }
        });
        if (this.sheetComments != null) {
            final CTCommentList lst = this.sheetComments.getCTComments().getCommentList();
            for (final CTComment comment : lst.getCommentArray()) {
                final String oldRef = comment.getRef();
                final CellReference ref = new CellReference(oldRef);
                final int columnIndex = ref.getCol();
                final int newColumnIndex = this.shiftedRowNum(startColumnIndex, endColumnIndex, n, columnIndex);
                if (newColumnIndex != columnIndex) {
                    final XSSFComment xssfComment = new XSSFComment(this.sheetComments, comment, (vml == null) ? null : vml.findCommentShape(ref.getRow(), columnIndex));
                    commentsToShift.put(xssfComment, newColumnIndex);
                }
            }
        }
        for (final Map.Entry<XSSFComment, Integer> entry : commentsToShift.entrySet()) {
            entry.getKey().setColumn(entry.getValue());
        }
        this.rebuildRows();
    }
    
    public void showInPane(final int topRow, final int leftCol) {
        final CellReference cellReference = new CellReference(topRow, leftCol);
        final String cellRef = cellReference.formatAsString();
        final CTPane pane = this.getPane(true);
        assert pane != null;
        pane.setTopLeftCell(cellRef);
    }
    
    public void ungroupColumn(final int fromColumn, final int toColumn) {
        final CTCols cols = this.worksheet.getColsArray(0);
        for (int index = fromColumn; index <= toColumn; ++index) {
            final CTCol col = this.columnHelper.getColumn(index, false);
            if (col != null) {
                final short outlineLevel = col.getOutlineLevel();
                col.setOutlineLevel((short)(outlineLevel - 1));
                index = Math.toIntExact(col.getMax());
                if (col.getOutlineLevel() <= 0) {
                    final int colIndex = this.columnHelper.getIndexOfColumn(cols, col);
                    this.worksheet.getColsArray(0).removeCol(colIndex);
                }
            }
        }
        this.worksheet.setColsArray(0, cols);
        this.setSheetFormatPrOutlineLevelCol();
    }
    
    public void ungroupRow(final int fromRow, final int toRow) {
        for (int i = fromRow; i <= toRow; ++i) {
            final XSSFRow xrow = this.getRow(i);
            if (xrow != null) {
                final CTRow ctRow = xrow.getCTRow();
                final int outlineLevel = ctRow.getOutlineLevel();
                ctRow.setOutlineLevel((short)(outlineLevel - 1));
                if (outlineLevel == 1 && xrow.getFirstCellNum() == -1) {
                    this.removeRow((Row)xrow);
                }
            }
        }
        this.setSheetFormatPrOutlineLevelRow();
    }
    
    private void setSheetFormatPrOutlineLevelRow() {
        final short maxLevelRow = this.getMaxOutlineLevelRows();
        this.getSheetTypeSheetFormatPr().setOutlineLevelRow(maxLevelRow);
    }
    
    private void setSheetFormatPrOutlineLevelCol() {
        final short maxLevelCol = this.getMaxOutlineLevelCols();
        this.getSheetTypeSheetFormatPr().setOutlineLevelCol(maxLevelCol);
    }
    
    protected CTSheetViews getSheetTypeSheetViews(final boolean create) {
        final CTSheetViews views = (this.worksheet.isSetSheetViews() || !create) ? this.worksheet.getSheetViews() : this.worksheet.addNewSheetViews();
        assert !create;
        if (views == null) {
            return null;
        }
        if (views.sizeOfSheetViewArray() == 0 && create) {
            views.addNewSheetView();
        }
        return views;
    }
    
    public boolean isSelected() {
        final CTSheetView dsv = this.getDefaultSheetView(false);
        return dsv != null && dsv.getTabSelected();
    }
    
    public void setSelected(final boolean value) {
        final CTSheetViews views = this.getSheetTypeSheetViews(true);
        assert views != null;
        for (final CTSheetView view : views.getSheetViewArray()) {
            view.setTabSelected(value);
        }
    }
    
    @Internal
    public void addHyperlink(final XSSFHyperlink hyperlink) {
        this.hyperlinks.add(hyperlink);
    }
    
    @Internal
    public void removeHyperlink(final int row, final int column) {
        final String ref = new CellReference(row, column).formatAsString();
        final Iterator<XSSFHyperlink> it = this.hyperlinks.iterator();
        while (it.hasNext()) {
            final XSSFHyperlink hyperlink = it.next();
            if (hyperlink.getCellRef().equals(ref)) {
                it.remove();
            }
        }
    }
    
    public CellAddress getActiveCell() {
        final CTSelection sts = this.getSheetTypeSelection(false);
        final String address = (sts != null) ? sts.getActiveCell() : null;
        return (address != null) ? new CellAddress(address) : null;
    }
    
    public void setActiveCell(final CellAddress address) {
        final CTSelection ctsel = this.getSheetTypeSelection(true);
        assert ctsel != null;
        final String ref = address.formatAsString();
        ctsel.setActiveCell(ref);
        ctsel.setSqref((List)Collections.singletonList(ref));
    }
    
    public boolean hasComments() {
        return this.sheetComments != null && this.sheetComments.getNumberOfComments() > 0;
    }
    
    protected int getNumberOfComments() {
        return (this.sheetComments == null) ? 0 : this.sheetComments.getNumberOfComments();
    }
    
    private CTSelection getSheetTypeSelection(final boolean create) {
        final CTSheetView dsv = this.getDefaultSheetView(create);
        assert !create;
        if (dsv == null) {
            return null;
        }
        final int sz = dsv.sizeOfSelectionArray();
        if (sz == 0) {
            return create ? dsv.addNewSelection() : null;
        }
        return dsv.getSelectionArray(sz - 1);
    }
    
    private CTSheetView getDefaultSheetView(final boolean create) {
        final CTSheetViews views = this.getSheetTypeSheetViews(create);
        assert !create;
        if (views == null) {
            return null;
        }
        final int sz = views.sizeOfSheetViewArray();
        assert !create;
        return (sz == 0) ? null : views.getSheetViewArray(sz - 1);
    }
    
    protected CommentsTable getCommentsTable(final boolean create) {
        if (this.sheetComments == null && create) {
            try {
                this.sheetComments = (CommentsTable)this.createRelationship(XSSFRelation.SHEET_COMMENTS, XSSFFactory.getInstance(), Math.toIntExact(this.sheet.getSheetId()));
            }
            catch (final PartAlreadyExistsException e) {
                this.sheetComments = (CommentsTable)this.createRelationship(XSSFRelation.SHEET_COMMENTS, XSSFFactory.getInstance(), -1);
            }
        }
        return this.sheetComments;
    }
    
    private CTPageSetUpPr getSheetTypePageSetUpPr() {
        final CTSheetPr sheetPr = this.getSheetTypeSheetPr();
        return sheetPr.isSetPageSetUpPr() ? sheetPr.getPageSetUpPr() : sheetPr.addNewPageSetUpPr();
    }
    
    private static boolean shouldRemoveRow(final int startRow, final int endRow, final int n, final int rownum) {
        return rownum >= startRow + n && rownum <= endRow + n && ((n > 0 && rownum > endRow) || (n < 0 && rownum < startRow));
    }
    
    private CTPane getPane(final boolean create) {
        final CTSheetView dsv = this.getDefaultSheetView(create);
        assert !create;
        if (dsv == null) {
            return null;
        }
        return (dsv.isSetPane() || !create) ? dsv.getPane() : dsv.addNewPane();
    }
    
    @Internal
    public CTCellFormula getSharedFormula(final int sid) {
        return this.sharedFormulas.get(sid);
    }
    
    void onReadCell(final XSSFCell cell) {
        final CTCell ct = cell.getCTCell();
        final CTCellFormula f = ct.getF();
        if (f != null && f.getT() == STCellFormulaType.SHARED && f.isSetRef() && f.getStringValue() != null) {
            final CTCellFormula sf = (CTCellFormula)f.copy();
            final CellRangeAddress sfRef = CellRangeAddress.valueOf(sf.getRef());
            final CellReference cellRef = new CellReference((Cell)cell);
            if (cellRef.getCol() > sfRef.getFirstColumn() || cellRef.getRow() > sfRef.getFirstRow()) {
                final String effectiveRef = new CellRangeAddress(Math.max(cellRef.getRow(), sfRef.getFirstRow()), sfRef.getLastRow(), Math.max(cellRef.getCol(), sfRef.getFirstColumn()), sfRef.getLastColumn()).formatAsString();
                sf.setRef(effectiveRef);
            }
            this.sharedFormulas.put(Math.toIntExact(f.getSi()), sf);
        }
        if (f != null && f.getT() == STCellFormulaType.ARRAY && f.getRef() != null) {
            this.arrayFormulas.add(CellRangeAddress.valueOf(f.getRef()));
        }
    }
    
    @Override
    protected void commit() throws IOException {
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.write(out);
        out.close();
    }
    
    protected void write(final OutputStream out) throws IOException {
        boolean setToNull = false;
        if (this.worksheet.sizeOfColsArray() == 1) {
            final CTCols col = this.worksheet.getColsArray(0);
            if (col.sizeOfColArray() == 0) {
                setToNull = true;
                this.worksheet.setColsArray((CTCols[])null);
            }
            else {
                this.setColWidthAttribute(col);
            }
        }
        if (this.hyperlinks.size() > 0) {
            if (this.worksheet.getHyperlinks() == null) {
                this.worksheet.addNewHyperlinks();
            }
            final CTHyperlink[] ctHls = new CTHyperlink[this.hyperlinks.size()];
            for (int i = 0; i < ctHls.length; ++i) {
                final XSSFHyperlink hyperlink = this.hyperlinks.get(i);
                hyperlink.generateRelationIfNeeded(this.getPackagePart());
                ctHls[i] = hyperlink.getCTHyperlink();
            }
            this.worksheet.getHyperlinks().setHyperlinkArray(ctHls);
        }
        else if (this.worksheet.getHyperlinks() != null) {
            final int count = this.worksheet.getHyperlinks().sizeOfHyperlinkArray();
            for (int i = count - 1; i >= 0; --i) {
                this.worksheet.getHyperlinks().removeHyperlink(i);
            }
            this.worksheet.unsetHyperlinks();
        }
        int minCell = Integer.MAX_VALUE;
        int maxCell = Integer.MIN_VALUE;
        for (final Map.Entry<Integer, XSSFRow> entry : this._rows.entrySet()) {
            final XSSFRow row = entry.getValue();
            row.onDocumentWrite();
            if (row.getFirstCellNum() != -1) {
                minCell = Math.min(minCell, row.getFirstCellNum());
            }
            if (row.getLastCellNum() != -1) {
                maxCell = Math.max(maxCell, row.getLastCellNum() - 1);
            }
        }
        if (minCell != Integer.MAX_VALUE) {
            final String ref = new CellRangeAddress(this.getFirstRowNum(), this.getLastRowNum(), minCell, maxCell).formatAsString();
            if (this.worksheet.isSetDimension()) {
                this.worksheet.getDimension().setRef(ref);
            }
            else {
                this.worksheet.addNewDimension().setRef(ref);
            }
        }
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTWorksheet.type.getName().getNamespaceURI(), "worksheet"));
        this.worksheet.save(out, xmlOptions);
        if (setToNull) {
            this.worksheet.addNewCols();
        }
    }
    
    public boolean isAutoFilterLocked() {
        return this.isSheetLocked() && this.safeGetProtectionField().getAutoFilter();
    }
    
    public boolean isDeleteColumnsLocked() {
        return this.isSheetLocked() && this.safeGetProtectionField().getDeleteColumns();
    }
    
    public boolean isDeleteRowsLocked() {
        return this.isSheetLocked() && this.safeGetProtectionField().getDeleteRows();
    }
    
    public boolean isFormatCellsLocked() {
        return this.isSheetLocked() && this.safeGetProtectionField().getFormatCells();
    }
    
    public boolean isFormatColumnsLocked() {
        return this.isSheetLocked() && this.safeGetProtectionField().getFormatColumns();
    }
    
    public boolean isFormatRowsLocked() {
        return this.isSheetLocked() && this.safeGetProtectionField().getFormatRows();
    }
    
    public boolean isInsertColumnsLocked() {
        return this.isSheetLocked() && this.safeGetProtectionField().getInsertColumns();
    }
    
    public boolean isInsertHyperlinksLocked() {
        return this.isSheetLocked() && this.safeGetProtectionField().getInsertHyperlinks();
    }
    
    public boolean isInsertRowsLocked() {
        return this.isSheetLocked() && this.safeGetProtectionField().getInsertRows();
    }
    
    public boolean isPivotTablesLocked() {
        return this.isSheetLocked() && this.safeGetProtectionField().getPivotTables();
    }
    
    public boolean isSortLocked() {
        return this.isSheetLocked() && this.safeGetProtectionField().getSort();
    }
    
    public boolean isObjectsLocked() {
        return this.isSheetLocked() && this.safeGetProtectionField().getObjects();
    }
    
    public boolean isScenariosLocked() {
        return this.isSheetLocked() && this.safeGetProtectionField().getScenarios();
    }
    
    public boolean isSelectLockedCellsLocked() {
        return this.isSheetLocked() && this.safeGetProtectionField().getSelectLockedCells();
    }
    
    public boolean isSelectUnlockedCellsLocked() {
        return this.isSheetLocked() && this.safeGetProtectionField().getSelectUnlockedCells();
    }
    
    public boolean isSheetLocked() {
        return this.worksheet.isSetSheetProtection() && this.safeGetProtectionField().getSheet();
    }
    
    public void enableLocking() {
        this.safeGetProtectionField().setSheet(true);
    }
    
    public void disableLocking() {
        this.safeGetProtectionField().setSheet(false);
    }
    
    public void lockAutoFilter(final boolean enabled) {
        this.safeGetProtectionField().setAutoFilter(enabled);
    }
    
    public void lockDeleteColumns(final boolean enabled) {
        this.safeGetProtectionField().setDeleteColumns(enabled);
    }
    
    public void lockDeleteRows(final boolean enabled) {
        this.safeGetProtectionField().setDeleteRows(enabled);
    }
    
    public void lockFormatCells(final boolean enabled) {
        this.safeGetProtectionField().setFormatCells(enabled);
    }
    
    public void lockFormatColumns(final boolean enabled) {
        this.safeGetProtectionField().setFormatColumns(enabled);
    }
    
    public void lockFormatRows(final boolean enabled) {
        this.safeGetProtectionField().setFormatRows(enabled);
    }
    
    public void lockInsertColumns(final boolean enabled) {
        this.safeGetProtectionField().setInsertColumns(enabled);
    }
    
    public void lockInsertHyperlinks(final boolean enabled) {
        this.safeGetProtectionField().setInsertHyperlinks(enabled);
    }
    
    public void lockInsertRows(final boolean enabled) {
        this.safeGetProtectionField().setInsertRows(enabled);
    }
    
    public void lockPivotTables(final boolean enabled) {
        this.safeGetProtectionField().setPivotTables(enabled);
    }
    
    public void lockSort(final boolean enabled) {
        this.safeGetProtectionField().setSort(enabled);
    }
    
    public void lockObjects(final boolean enabled) {
        this.safeGetProtectionField().setObjects(enabled);
    }
    
    public void lockScenarios(final boolean enabled) {
        this.safeGetProtectionField().setScenarios(enabled);
    }
    
    public void lockSelectLockedCells(final boolean enabled) {
        this.safeGetProtectionField().setSelectLockedCells(enabled);
    }
    
    public void lockSelectUnlockedCells(final boolean enabled) {
        this.safeGetProtectionField().setSelectUnlockedCells(enabled);
    }
    
    private CTSheetProtection safeGetProtectionField() {
        if (!this.isSheetProtectionEnabled()) {
            return this.worksheet.addNewSheetProtection();
        }
        return this.worksheet.getSheetProtection();
    }
    
    boolean isSheetProtectionEnabled() {
        return this.worksheet.isSetSheetProtection();
    }
    
    boolean isCellInArrayFormulaContext(final XSSFCell cell) {
        for (final CellRangeAddress range : this.arrayFormulas) {
            if (range.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                return true;
            }
        }
        return false;
    }
    
    XSSFCell getFirstCellInArrayFormula(final XSSFCell cell) {
        for (final CellRangeAddress range : this.arrayFormulas) {
            if (range.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                return this.getRow(range.getFirstRow()).getCell(range.getFirstColumn());
            }
        }
        return null;
    }
    
    private CellRange<XSSFCell> getCellRange(final CellRangeAddress range) {
        final int firstRow = range.getFirstRow();
        final int firstColumn = range.getFirstColumn();
        final int lastRow = range.getLastRow();
        final int lastColumn = range.getLastColumn();
        final int height = lastRow - firstRow + 1;
        final int width = lastColumn - firstColumn + 1;
        final List<XSSFCell> temp = new ArrayList<XSSFCell>(height * width);
        for (int rowIn = firstRow; rowIn <= lastRow; ++rowIn) {
            for (int colIn = firstColumn; colIn <= lastColumn; ++colIn) {
                XSSFRow row = this.getRow(rowIn);
                if (row == null) {
                    row = this.createRow(rowIn);
                }
                XSSFCell cell = row.getCell(colIn);
                if (cell == null) {
                    cell = row.createCell(colIn);
                }
                temp.add(cell);
            }
        }
        return (CellRange<XSSFCell>)SSCellRange.create(firstRow, firstColumn, height, width, (List)temp, (Class)XSSFCell.class);
    }
    
    public CellRange<XSSFCell> setArrayFormula(final String formula, final CellRangeAddress range) {
        final CellRange<XSSFCell> cr = this.getCellRange(range);
        final XSSFCell mainArrayFormulaCell = (XSSFCell)cr.getTopLeftCell();
        mainArrayFormulaCell.setCellArrayFormula(formula, range);
        this.arrayFormulas.add(range);
        return cr;
    }
    
    public CellRange<XSSFCell> removeArrayFormula(final Cell cell) {
        if (cell.getSheet() != this) {
            throw new IllegalArgumentException("Specified cell does not belong to this sheet.");
        }
        for (final CellRangeAddress range : this.arrayFormulas) {
            if (range.isInRange(cell)) {
                this.arrayFormulas.remove(range);
                final CellRange<XSSFCell> cr = this.getCellRange(range);
                for (final XSSFCell c : cr) {
                    c.setBlank();
                }
                return cr;
            }
        }
        final String ref = new CellReference(cell).formatAsString();
        throw new IllegalArgumentException("Cell " + ref + " is not part of an array formula.");
    }
    
    public DataValidationHelper getDataValidationHelper() {
        return (DataValidationHelper)this.dataValidationHelper;
    }
    
    public List<XSSFDataValidation> getDataValidations() {
        final List<XSSFDataValidation> xssfValidations = new ArrayList<XSSFDataValidation>();
        final CTDataValidations dataValidations = this.worksheet.getDataValidations();
        if (dataValidations != null && dataValidations.getCount() > 0L) {
            for (final CTDataValidation ctDataValidation : dataValidations.getDataValidationArray()) {
                final CellRangeAddressList addressList = new CellRangeAddressList();
                final List<String> sqref = ctDataValidation.getSqref();
                for (final String stRef : sqref) {
                    final String[] split;
                    final String[] regions = split = stRef.split(" ");
                    for (final String region : split) {
                        final String[] parts = region.split(":");
                        final CellReference begin = new CellReference(parts[0]);
                        final CellReference end = (parts.length > 1) ? new CellReference(parts[1]) : begin;
                        final CellRangeAddress cellRangeAddress = new CellRangeAddress(begin.getRow(), end.getRow(), (int)begin.getCol(), (int)end.getCol());
                        addressList.addCellRangeAddress(cellRangeAddress);
                    }
                }
                final XSSFDataValidation xssfDataValidation = new XSSFDataValidation(addressList, ctDataValidation);
                xssfValidations.add(xssfDataValidation);
            }
        }
        return xssfValidations;
    }
    
    public void addValidationData(final DataValidation dataValidation) {
        final XSSFDataValidation xssfDataValidation = (XSSFDataValidation)dataValidation;
        CTDataValidations dataValidations = this.worksheet.getDataValidations();
        if (dataValidations == null) {
            dataValidations = this.worksheet.addNewDataValidations();
        }
        final int currentCount = dataValidations.sizeOfDataValidationArray();
        final CTDataValidation newval = dataValidations.addNewDataValidation();
        newval.set((XmlObject)xssfDataValidation.getCtDdataValidation());
        dataValidations.setCount((long)(currentCount + 1));
    }
    
    public XSSFAutoFilter setAutoFilter(final CellRangeAddress range) {
        CTAutoFilter af = this.worksheet.getAutoFilter();
        if (af == null) {
            af = this.worksheet.addNewAutoFilter();
        }
        final CellRangeAddress norm = new CellRangeAddress(range.getFirstRow(), range.getLastRow(), range.getFirstColumn(), range.getLastColumn());
        final String ref = norm.formatAsString();
        af.setRef(ref);
        final XSSFWorkbook wb = this.getWorkbook();
        final int sheetIndex = this.getWorkbook().getSheetIndex((Sheet)this);
        XSSFName name = wb.getBuiltInName("_xlnm._FilterDatabase", sheetIndex);
        if (name == null) {
            name = wb.createBuiltInName("_xlnm._FilterDatabase", sheetIndex);
        }
        name.getCTName().setHidden(true);
        final CellReference r1 = new CellReference(this.getSheetName(), range.getFirstRow(), range.getFirstColumn(), true, true);
        final CellReference r2 = new CellReference((String)null, range.getLastRow(), range.getLastColumn(), true, true);
        final String fmla = r1.formatAsString() + ":" + r2.formatAsString();
        name.setRefersToFormula(fmla);
        return new XSSFAutoFilter(this);
    }
    
    @Deprecated
    @Removal(version = "4.2.0")
    public XSSFTable createTable() {
        return this.createTable(null);
    }
    
    public XSSFTable createTable(final AreaReference tableArea) {
        if (!this.worksheet.isSetTableParts()) {
            this.worksheet.addNewTableParts();
        }
        final CTTableParts tblParts = this.worksheet.getTableParts();
        final CTTablePart tbl = tblParts.addNewTablePart();
        int tableNumber = this.getPackagePart().getPackage().getPartsByContentType(XSSFRelation.TABLE.getContentType()).size() + 1;
        boolean loop = true;
        while (loop) {
            loop = false;
            for (final PackagePart packagePart : this.getPackagePart().getPackage().getPartsByContentType(XSSFRelation.TABLE.getContentType())) {
                final String fileName = XSSFRelation.TABLE.getFileName(tableNumber);
                if (fileName.equals(packagePart.getPartName().getName())) {
                    ++tableNumber;
                    loop = true;
                }
            }
        }
        final RelationPart rp = this.createRelationship(XSSFRelation.TABLE, XSSFFactory.getInstance(), tableNumber, false);
        final XSSFTable table = rp.getDocumentPart();
        tbl.setId(rp.getRelationship().getId());
        table.getCTTable().setId((long)tableNumber);
        this.tables.put(tbl.getId(), table);
        if (tableArea != null) {
            table.setArea(tableArea);
        }
        while (tableNumber < Integer.MAX_VALUE) {
            final String displayName = "Table" + tableNumber;
            if (this.getWorkbook().getTable(displayName) == null && this.getWorkbook().getName(displayName) == null) {
                table.setDisplayName(displayName);
                table.setName(displayName);
                break;
            }
            ++tableNumber;
        }
        return table;
    }
    
    public List<XSSFTable> getTables() {
        return new ArrayList<XSSFTable>(this.tables.values());
    }
    
    public void removeTable(final XSSFTable t) {
        final long id = t.getCTTable().getId();
        Map.Entry<String, XSSFTable> toDelete = null;
        for (final Map.Entry<String, XSSFTable> entry : this.tables.entrySet()) {
            if (entry.getValue().getCTTable().getId() == id) {
                toDelete = entry;
            }
        }
        if (toDelete != null) {
            this.removeRelation(this.getRelationById(toDelete.getKey()), true);
            this.tables.remove(toDelete.getKey());
            toDelete.getValue().onTableDelete();
        }
    }
    
    public XSSFSheetConditionalFormatting getSheetConditionalFormatting() {
        return new XSSFSheetConditionalFormatting(this);
    }
    
    public XSSFColor getTabColor() {
        CTSheetPr pr = this.worksheet.getSheetPr();
        if (pr == null) {
            pr = this.worksheet.addNewSheetPr();
        }
        if (!pr.isSetTabColor()) {
            return null;
        }
        return XSSFColor.from(pr.getTabColor(), this.getWorkbook().getStylesSource().getIndexedColors());
    }
    
    public void setTabColor(final XSSFColor color) {
        CTSheetPr pr = this.worksheet.getSheetPr();
        if (pr == null) {
            pr = this.worksheet.addNewSheetPr();
        }
        pr.setTabColor(color.getCTColor());
    }
    
    public CellRangeAddress getRepeatingRows() {
        return this.getRepeatingRowsOrColums(true);
    }
    
    public CellRangeAddress getRepeatingColumns() {
        return this.getRepeatingRowsOrColums(false);
    }
    
    public void setRepeatingRows(final CellRangeAddress rowRangeRef) {
        final CellRangeAddress columnRangeRef = this.getRepeatingColumns();
        this.setRepeatingRowsAndColumns(rowRangeRef, columnRangeRef);
    }
    
    public void setRepeatingColumns(final CellRangeAddress columnRangeRef) {
        final CellRangeAddress rowRangeRef = this.getRepeatingRows();
        this.setRepeatingRowsAndColumns(rowRangeRef, columnRangeRef);
    }
    
    private void setRepeatingRowsAndColumns(final CellRangeAddress rowDef, final CellRangeAddress colDef) {
        int col1 = -1;
        int col2 = -1;
        int row1 = -1;
        int row2 = -1;
        if (rowDef != null) {
            row1 = rowDef.getFirstRow();
            row2 = rowDef.getLastRow();
            if ((row1 == -1 && row2 != -1) || row1 < -1 || row2 < -1 || row1 > row2) {
                throw new IllegalArgumentException("Invalid row range specification");
            }
        }
        if (colDef != null) {
            col1 = colDef.getFirstColumn();
            col2 = colDef.getLastColumn();
            if ((col1 == -1 && col2 != -1) || col1 < -1 || col2 < -1 || col1 > col2) {
                throw new IllegalArgumentException("Invalid column range specification");
            }
        }
        final int sheetIndex = this.getWorkbook().getSheetIndex((Sheet)this);
        final boolean removeAll = rowDef == null && colDef == null;
        XSSFName name = this.getWorkbook().getBuiltInName("_xlnm.Print_Titles", sheetIndex);
        if (removeAll) {
            if (name != null) {
                this.getWorkbook().removeName((Name)name);
            }
            return;
        }
        if (name == null) {
            name = this.getWorkbook().createBuiltInName("_xlnm.Print_Titles", sheetIndex);
        }
        final String reference = getReferenceBuiltInRecord(name.getSheetName(), col1, col2, row1, row2);
        name.setRefersToFormula(reference);
        if (!this.worksheet.isSetPageSetup() || !this.worksheet.isSetPageMargins()) {
            this.getPrintSetup().setValidSettings(false);
        }
    }
    
    private static String getReferenceBuiltInRecord(final String sheetName, final int startC, final int endC, final int startR, final int endR) {
        final CellReference colRef = new CellReference(sheetName, 0, startC, true, true);
        final CellReference colRef2 = new CellReference(sheetName, 0, endC, true, true);
        final CellReference rowRef = new CellReference(sheetName, startR, 0, true, true);
        final CellReference rowRef2 = new CellReference(sheetName, endR, 0, true, true);
        final String escapedName = SheetNameFormatter.format(sheetName);
        String c = "";
        String r = "";
        if (startC != -1 || endC != -1) {
            final String col1 = colRef.getCellRefParts()[2];
            final String col2 = colRef2.getCellRefParts()[2];
            c = escapedName + "!$" + col1 + ":$" + col2;
        }
        if (startR != -1 || endR != -1) {
            final String row1 = rowRef.getCellRefParts()[1];
            final String row2 = rowRef2.getCellRefParts()[1];
            if (!row1.equals("0") && !row2.equals("0")) {
                r = escapedName + "!$" + row1 + ":$" + row2;
            }
        }
        final StringBuilder rng = new StringBuilder();
        rng.append(c);
        if (rng.length() > 0 && r.length() > 0) {
            rng.append(',');
        }
        rng.append(r);
        return rng.toString();
    }
    
    private CellRangeAddress getRepeatingRowsOrColums(final boolean rows) {
        final int sheetIndex = this.getWorkbook().getSheetIndex((Sheet)this);
        final XSSFName name = this.getWorkbook().getBuiltInName("_xlnm.Print_Titles", sheetIndex);
        if (name == null) {
            return null;
        }
        final String refStr = name.getRefersToFormula();
        if (refStr == null) {
            return null;
        }
        final String[] parts = refStr.split(",");
        final int maxRowIndex = SpreadsheetVersion.EXCEL2007.getLastRowIndex();
        final int maxColIndex = SpreadsheetVersion.EXCEL2007.getLastColumnIndex();
        for (final String part : parts) {
            final CellRangeAddress range = CellRangeAddress.valueOf(part);
            if ((range.getFirstColumn() == 0 && range.getLastColumn() == maxColIndex) || (range.getFirstColumn() == -1 && range.getLastColumn() == -1)) {
                if (rows) {
                    return range;
                }
            }
            else if (((range.getFirstRow() == 0 && range.getLastRow() == maxRowIndex) || (range.getFirstRow() == -1 && range.getLastRow() == -1)) && !rows) {
                return range;
            }
        }
        return null;
    }
    
    private XSSFPivotTable createPivotTable() {
        final XSSFWorkbook wb = this.getWorkbook();
        final List<XSSFPivotTable> pivotTables = wb.getPivotTables();
        final int tableId = this.getWorkbook().getPivotTables().size() + 1;
        final XSSFPivotTable pivotTable = (XSSFPivotTable)this.createRelationship(XSSFRelation.PIVOT_TABLE, XSSFFactory.getInstance(), tableId);
        pivotTable.setParentSheet(this);
        pivotTables.add(pivotTable);
        final XSSFWorkbook workbook = this.getWorkbook();
        final XSSFPivotCacheDefinition pivotCacheDefinition = (XSSFPivotCacheDefinition)workbook.createRelationship(XSSFRelation.PIVOT_CACHE_DEFINITION, XSSFFactory.getInstance(), tableId);
        final String rId = workbook.getRelationId(pivotCacheDefinition);
        final PackagePart pivotPackagePart = pivotTable.getPackagePart();
        pivotPackagePart.addRelationship(pivotCacheDefinition.getPackagePart().getPartName(), TargetMode.INTERNAL, XSSFRelation.PIVOT_CACHE_DEFINITION.getRelation());
        pivotTable.setPivotCacheDefinition(pivotCacheDefinition);
        pivotTable.setPivotCache(new XSSFPivotCache(workbook.addPivotCache(rId)));
        final XSSFPivotCacheRecords pivotCacheRecords = (XSSFPivotCacheRecords)pivotCacheDefinition.createRelationship(XSSFRelation.PIVOT_CACHE_RECORDS, XSSFFactory.getInstance(), tableId);
        pivotTable.getPivotCacheDefinition().getCTPivotCacheDefinition().setId(pivotCacheDefinition.getRelationId(pivotCacheRecords));
        wb.setPivotTables(pivotTables);
        return pivotTable;
    }
    
    public XSSFPivotTable createPivotTable(final AreaReference source, final CellReference position, final Sheet sourceSheet) {
        final String sourceSheetName = source.getFirstCell().getSheetName();
        if (sourceSheetName != null && !sourceSheetName.equalsIgnoreCase(sourceSheet.getSheetName())) {
            throw new IllegalArgumentException("The area is referenced in another sheet than the defined source sheet " + sourceSheet.getSheetName() + ".");
        }
        return this.createPivotTable(position, sourceSheet, wsSource -> {
            final String[] firstCell = source.getFirstCell().getCellRefParts();
            final String firstRow = firstCell[1];
            final String firstCol = firstCell[2];
            final String[] lastCell = source.getLastCell().getCellRefParts();
            final String lastRow = lastCell[1];
            final String lastCol = lastCell[2];
            final String ref = firstCol + firstRow + ':' + lastCol + lastRow;
            wsSource.setRef(ref);
        });
    }
    
    private XSSFPivotTable createPivotTable(final CellReference position, final Sheet sourceSheet, final XSSFPivotTable.PivotTableReferenceConfigurator refConfig) {
        final XSSFPivotTable pivotTable = this.createPivotTable();
        pivotTable.setDefaultPivotTableDefinition();
        pivotTable.createSourceReferences(position, sourceSheet, refConfig);
        pivotTable.getPivotCacheDefinition().createCacheFields(sourceSheet);
        pivotTable.createDefaultDataColumns();
        return pivotTable;
    }
    
    public XSSFPivotTable createPivotTable(final AreaReference source, final CellReference position) {
        final String sourceSheetName = source.getFirstCell().getSheetName();
        if (sourceSheetName != null && !sourceSheetName.equalsIgnoreCase(this.getSheetName())) {
            final XSSFSheet sourceSheet = this.getWorkbook().getSheet(sourceSheetName);
            return this.createPivotTable(source, position, (Sheet)sourceSheet);
        }
        return this.createPivotTable(source, position, (Sheet)this);
    }
    
    public XSSFPivotTable createPivotTable(final Name source, final CellReference position, final Sheet sourceSheet) {
        if (source.getSheetName() != null && !source.getSheetName().equals(sourceSheet.getSheetName())) {
            throw new IllegalArgumentException("The named range references another sheet than the defined source sheet " + sourceSheet.getSheetName() + ".");
        }
        return this.createPivotTable(position, sourceSheet, wsSource -> wsSource.setName(source.getNameName()));
    }
    
    public XSSFPivotTable createPivotTable(final Name source, final CellReference position) {
        return this.createPivotTable(source, position, (Sheet)this.getWorkbook().getSheet(source.getSheetName()));
    }
    
    public XSSFPivotTable createPivotTable(final Table source, final CellReference position) {
        return this.createPivotTable(position, (Sheet)this.getWorkbook().getSheet(source.getSheetName()), wsSource -> wsSource.setName(source.getName()));
    }
    
    public List<XSSFPivotTable> getPivotTables() {
        final List<XSSFPivotTable> tables = new ArrayList<XSSFPivotTable>();
        for (final XSSFPivotTable table : this.getWorkbook().getPivotTables()) {
            if (table.getParent() == this) {
                tables.add(table);
            }
        }
        return tables;
    }
    
    public int getColumnOutlineLevel(final int columnIndex) {
        final CTCol col = this.columnHelper.getColumn(columnIndex, false);
        if (col == null) {
            return 0;
        }
        return col.getOutlineLevel();
    }
    
    public void addIgnoredErrors(final CellReference cell, final IgnoredErrorType... ignoredErrorTypes) {
        this.addIgnoredErrors(cell.formatAsString(false), ignoredErrorTypes);
    }
    
    public void addIgnoredErrors(final CellRangeAddress region, final IgnoredErrorType... ignoredErrorTypes) {
        region.validate(SpreadsheetVersion.EXCEL2007);
        this.addIgnoredErrors(region.formatAsString(), ignoredErrorTypes);
    }
    
    public Map<IgnoredErrorType, Set<CellRangeAddress>> getIgnoredErrors() {
        final Map<IgnoredErrorType, Set<CellRangeAddress>> result = new LinkedHashMap<IgnoredErrorType, Set<CellRangeAddress>>();
        if (this.worksheet.isSetIgnoredErrors()) {
            for (final CTIgnoredError err : this.worksheet.getIgnoredErrors().getIgnoredErrorList()) {
                for (final IgnoredErrorType errType : XSSFIgnoredErrorHelper.getErrorTypes(err)) {
                    if (!result.containsKey(errType)) {
                        result.put(errType, new LinkedHashSet<CellRangeAddress>());
                    }
                    for (final Object ref : err.getSqref()) {
                        result.get(errType).add(CellRangeAddress.valueOf(ref.toString()));
                    }
                }
            }
        }
        return result;
    }
    
    private void addIgnoredErrors(final String ref, final IgnoredErrorType... ignoredErrorTypes) {
        final CTIgnoredErrors ctIgnoredErrors = this.worksheet.isSetIgnoredErrors() ? this.worksheet.getIgnoredErrors() : this.worksheet.addNewIgnoredErrors();
        final CTIgnoredError ctIgnoredError = ctIgnoredErrors.addNewIgnoredError();
        XSSFIgnoredErrorHelper.addIgnoredErrors(ctIgnoredError, ref, ignoredErrorTypes);
    }
    
    protected void onSheetDelete() {
        for (final RelationPart part : this.getRelationParts()) {
            if (part.getDocumentPart() instanceof XSSFTable) {
                this.removeTable(part.getDocumentPart());
            }
            else {
                this.removeRelation(part.getDocumentPart(), true);
            }
        }
    }
    
    protected void onDeleteFormula(final XSSFCell cell, final BaseXSSFEvaluationWorkbook evalWb) {
        final CTCellFormula f = cell.getCTCell().getF();
        Label_0246: {
            if (f != null && f.getT() == STCellFormulaType.SHARED && f.isSetRef() && f.getStringValue() != null) {
                final CellRangeAddress ref = CellRangeAddress.valueOf(f.getRef());
                if (ref.getNumberOfCells() > 1) {
                    for (int i = cell.getRowIndex(); i <= ref.getLastRow(); ++i) {
                        final XSSFRow row = this.getRow(i);
                        if (row != null) {
                            for (int j = cell.getColumnIndex(); j <= ref.getLastColumn(); ++j) {
                                final XSSFCell nextCell = row.getCell(j);
                                if (nextCell != null && nextCell != cell && nextCell.getCellType() == CellType.FORMULA) {
                                    final CTCellFormula nextF = nextCell.getCTCell().getF();
                                    nextF.setStringValue(nextCell.getCellFormula(evalWb));
                                    final CellRangeAddress nextRef = new CellRangeAddress(nextCell.getRowIndex(), ref.getLastRow(), nextCell.getColumnIndex(), ref.getLastColumn());
                                    nextF.setRef(nextRef.formatAsString());
                                    this.sharedFormulas.put(Math.toIntExact(nextF.getSi()), nextF);
                                    break Label_0246;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected CTOleObject readOleObject(final long shapeId) {
        if (!this.getCTWorksheet().isSetOleObjects()) {
            return null;
        }
        final String xquery = "declare namespace p='http://schemas.openxmlformats.org/spreadsheetml/2006/main' .//p:oleObject";
        final XmlCursor cur = this.getCTWorksheet().getOleObjects().newCursor();
        try {
            cur.selectPath(xquery);
            CTOleObject coo = null;
            while (cur.toNextSelection()) {
                final String sId = cur.getAttributeText(new QName(null, "shapeId"));
                if (sId != null) {
                    if (Long.parseLong(sId) != shapeId) {
                        continue;
                    }
                    final XmlObject xObj = cur.getObject();
                    if (xObj instanceof CTOleObject) {
                        coo = (CTOleObject)xObj;
                    }
                    else {
                        final XMLStreamReader reader = cur.newXMLStreamReader();
                        try {
                            final CTOleObjects coos = CTOleObjects.Factory.parse(reader);
                            if (coos.sizeOfOleObjectArray() == 0) {}
                            coo = coos.getOleObjectArray(0);
                        }
                        catch (final XmlException e) {
                            XSSFSheet.logger.log(3, new Object[] { "can't parse CTOleObjects", e });
                            try {
                                reader.close();
                            }
                            catch (final XMLStreamException e2) {
                                XSSFSheet.logger.log(3, new Object[] { "can't close reader", e2 });
                            }
                        }
                        finally {
                            try {
                                reader.close();
                            }
                            catch (final XMLStreamException e3) {
                                XSSFSheet.logger.log(3, new Object[] { "can't close reader", e3 });
                            }
                        }
                    }
                    if (cur.toChild("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "objectPr")) {
                        break;
                    }
                    continue;
                }
            }
            return coo;
        }
        finally {
            cur.dispose();
        }
    }
    
    public XSSFHeaderFooterProperties getHeaderFooterProperties() {
        return new XSSFHeaderFooterProperties(this.getSheetTypeHeaderFooter());
    }
    
    static {
        logger = POILogFactory.getLogger((Class)XSSFSheet.class);
    }
}
