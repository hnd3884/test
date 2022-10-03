package org.apache.poi.xssf.usermodel;

import org.apache.poi.xssf.model.CalculationChain;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.util.CellAddress;
import java.text.DateFormat;
import org.apache.poi.ss.formula.eval.ErrorEval;
import java.text.SimpleDateFormat;
import org.apache.poi.util.LocaleUtil;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.ss.usermodel.FormulaError;
import java.util.Calendar;
import java.time.LocalDateTime;
import java.util.Date;
import org.apache.poi.util.Removal;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.SharedFormula;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellFormula;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellFormulaType;
import org.apache.poi.ss.usermodel.RichTextString;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType;
import org.apache.poi.util.Internal;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;
import org.apache.poi.ss.usermodel.CellBase;

public final class XSSFCell extends CellBase
{
    private static final String FALSE_AS_STRING = "0";
    private static final String TRUE_AS_STRING = "1";
    private static final String FALSE = "FALSE";
    private static final String TRUE = "TRUE";
    private CTCell _cell;
    private final XSSFRow _row;
    private int _cellNum;
    private SharedStringsTable _sharedStringSource;
    private StylesTable _stylesSource;
    
    protected XSSFCell(final XSSFRow row, final CTCell cell) {
        this._cell = cell;
        this._row = row;
        if (cell.getR() != null) {
            this._cellNum = new CellReference(cell.getR()).getCol();
        }
        else {
            final int prevNum = row.getLastCellNum();
            if (prevNum != -1) {
                this._cellNum = row.getCell(prevNum - 1, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK).getColumnIndex() + 1;
            }
        }
        this._sharedStringSource = row.getSheet().getWorkbook().getSharedStringSource();
        this._stylesSource = row.getSheet().getWorkbook().getStylesSource();
    }
    
    protected SpreadsheetVersion getSpreadsheetVersion() {
        return SpreadsheetVersion.EXCEL2007;
    }
    
    @Internal
    public void copyCellFrom(final Cell srcCell, final CellCopyPolicy policy) {
        if (policy.isCopyCellValue()) {
            if (srcCell != null) {
                CellType copyCellType = srcCell.getCellType();
                if (copyCellType == CellType.FORMULA && !policy.isCopyCellFormula()) {
                    copyCellType = srcCell.getCachedFormulaResultType();
                }
                switch (copyCellType) {
                    case NUMERIC: {
                        if (DateUtil.isCellDateFormatted(srcCell)) {
                            this.setCellValue(srcCell.getDateCellValue());
                            break;
                        }
                        this.setCellValue(srcCell.getNumericCellValue());
                        break;
                    }
                    case STRING: {
                        this.setCellValue(srcCell.getStringCellValue());
                        break;
                    }
                    case FORMULA: {
                        this.setCellFormula(srcCell.getCellFormula());
                        break;
                    }
                    case BLANK: {
                        this.setBlank();
                        break;
                    }
                    case BOOLEAN: {
                        this.setCellValue(srcCell.getBooleanCellValue());
                        break;
                    }
                    case ERROR: {
                        this.setCellErrorValue(srcCell.getErrorCellValue());
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Invalid cell type " + srcCell.getCellType());
                    }
                }
            }
            else {
                this.setBlank();
            }
        }
        if (policy.isCopyCellStyle()) {
            this.setCellStyle((srcCell == null) ? null : srcCell.getCellStyle());
        }
        final Hyperlink srcHyperlink = (srcCell == null) ? null : srcCell.getHyperlink();
        if (policy.isMergeHyperlink()) {
            if (srcHyperlink != null) {
                this.setHyperlink((Hyperlink)new XSSFHyperlink(srcHyperlink));
            }
        }
        else if (policy.isCopyHyperlink()) {
            this.setHyperlink((Hyperlink)((srcHyperlink == null) ? null : new XSSFHyperlink(srcHyperlink)));
        }
    }
    
    protected SharedStringsTable getSharedStringSource() {
        return this._sharedStringSource;
    }
    
    protected StylesTable getStylesSource() {
        return this._stylesSource;
    }
    
    public XSSFSheet getSheet() {
        return this.getRow().getSheet();
    }
    
    public XSSFRow getRow() {
        return this._row;
    }
    
    public boolean getBooleanCellValue() {
        final CellType cellType = this.getCellType();
        switch (cellType) {
            case BLANK: {
                return false;
            }
            case BOOLEAN: {
                return this._cell.isSetV() && "1".equals(this._cell.getV());
            }
            case FORMULA: {
                return this._cell.isSetV() && "1".equals(this._cell.getV());
            }
            default: {
                throw typeMismatch(CellType.BOOLEAN, cellType, false);
            }
        }
    }
    
    public void setCellValue(final boolean value) {
        this._cell.setT(STCellType.B);
        this._cell.setV(value ? "1" : "0");
    }
    
    public double getNumericCellValue() {
        final CellType valueType = this.isFormulaCell() ? this.getCachedFormulaResultType() : this.getCellType();
        switch (valueType) {
            case BLANK: {
                return 0.0;
            }
            case NUMERIC: {
                if (this._cell.isSetV()) {
                    final String v = this._cell.getV();
                    if (v.isEmpty()) {
                        return 0.0;
                    }
                    try {
                        return Double.parseDouble(v);
                    }
                    catch (final NumberFormatException e) {
                        throw typeMismatch(CellType.NUMERIC, CellType.STRING, false);
                    }
                }
                return 0.0;
            }
            case FORMULA: {
                throw new AssertionError();
            }
            default: {
                throw typeMismatch(CellType.NUMERIC, valueType, false);
            }
        }
    }
    
    public void setCellValueImpl(final double value) {
        this._cell.setT(STCellType.N);
        this._cell.setV(String.valueOf(value));
    }
    
    public String getStringCellValue() {
        return this.getRichStringCellValue().getString();
    }
    
    public XSSFRichTextString getRichStringCellValue() {
        final CellType cellType = this.getCellType();
        XSSFRichTextString rt = null;
        switch (cellType) {
            case BLANK: {
                rt = new XSSFRichTextString("");
                break;
            }
            case STRING: {
                if (this._cell.getT() == STCellType.INLINE_STR) {
                    if (this._cell.isSetIs()) {
                        rt = new XSSFRichTextString(this._cell.getIs());
                        break;
                    }
                    if (this._cell.isSetV()) {
                        rt = new XSSFRichTextString(this._cell.getV());
                        break;
                    }
                    rt = new XSSFRichTextString("");
                    break;
                }
                else {
                    if (this._cell.getT() == STCellType.STR) {
                        rt = new XSSFRichTextString(this._cell.isSetV() ? this._cell.getV() : "");
                        break;
                    }
                    if (this._cell.isSetV()) {
                        final int idx = Integer.parseInt(this._cell.getV());
                        rt = new XSSFRichTextString(this._sharedStringSource.getEntryAt(idx));
                        break;
                    }
                    rt = new XSSFRichTextString("");
                    break;
                }
                break;
            }
            case FORMULA: {
                checkFormulaCachedValueType(CellType.STRING, this.getBaseCellType(false));
                rt = new XSSFRichTextString(this._cell.isSetV() ? this._cell.getV() : "");
                break;
            }
            default: {
                throw typeMismatch(CellType.STRING, cellType, false);
            }
        }
        rt.setStylesTableReference(this._stylesSource);
        return rt;
    }
    
    private static void checkFormulaCachedValueType(final CellType expectedTypeCode, final CellType cachedValueType) {
        if (cachedValueType != expectedTypeCode) {
            throw typeMismatch(expectedTypeCode, cachedValueType, true);
        }
    }
    
    protected void setCellValueImpl(final String value) {
        this.setCellValueImpl((RichTextString)new XSSFRichTextString(value));
    }
    
    protected void setCellValueImpl(final RichTextString str) {
        final CellType cellType = this.getCellType();
        if (cellType == CellType.FORMULA) {
            this._cell.setV(str.getString());
            this._cell.setT(STCellType.STR);
        }
        else if (this._cell.getT() == STCellType.INLINE_STR) {
            this._cell.setV(str.getString());
        }
        else {
            this._cell.setT(STCellType.S);
            final XSSFRichTextString rt = (XSSFRichTextString)str;
            rt.setStylesTableReference(this._stylesSource);
            final int sRef = this._sharedStringSource.addSharedStringItem((RichTextString)rt);
            this._cell.setV(Integer.toString(sRef));
        }
    }
    
    public String getCellFormula() {
        return this.getCellFormula(null);
    }
    
    protected String getCellFormula(final BaseXSSFEvaluationWorkbook fpb) {
        final CellType cellType = this.getCellType();
        if (cellType != CellType.FORMULA) {
            throw typeMismatch(CellType.FORMULA, cellType, false);
        }
        final CTCellFormula f = this._cell.getF();
        if (this.isPartOfArrayFormulaGroup() && (f == null || f.getStringValue().isEmpty())) {
            final XSSFCell cell = this.getSheet().getFirstCellInArrayFormula(this);
            return cell.getCellFormula(fpb);
        }
        if (f == null) {
            return null;
        }
        if (f.getT() == STCellFormulaType.SHARED) {
            return this.convertSharedFormula(Math.toIntExact(f.getSi()), (fpb == null) ? XSSFEvaluationWorkbook.create(this.getSheet().getWorkbook()) : fpb);
        }
        return f.getStringValue();
    }
    
    private String convertSharedFormula(final int si, final BaseXSSFEvaluationWorkbook fpb) {
        final XSSFSheet sheet = this.getSheet();
        final CTCellFormula f = sheet.getSharedFormula(si);
        if (f == null) {
            throw new IllegalStateException("Master cell of a shared formula with sid=" + si + " was not found");
        }
        final String sharedFormula = f.getStringValue();
        final String sharedFormulaRange = f.getRef();
        final CellRangeAddress ref = CellRangeAddress.valueOf(sharedFormulaRange);
        final int sheetIndex = sheet.getWorkbook().getSheetIndex((Sheet)sheet);
        final SharedFormula sf = new SharedFormula(SpreadsheetVersion.EXCEL2007);
        final Ptg[] ptgs = FormulaParser.parse(sharedFormula, (FormulaParsingWorkbook)fpb, FormulaType.CELL, sheetIndex, this.getRowIndex());
        final Ptg[] fmla = sf.convertSharedFormulas(ptgs, this.getRowIndex() - ref.getFirstRow(), this.getColumnIndex() - ref.getFirstColumn());
        return FormulaRenderer.toFormulaString((FormulaRenderingWorkbook)fpb, fmla);
    }
    
    protected void setCellFormulaImpl(final String formula) {
        assert formula != null;
        this.setFormula(formula, FormulaType.CELL);
    }
    
    void setCellArrayFormula(final String formula, final CellRangeAddress range) {
        this.setFormula(formula, FormulaType.ARRAY);
        final CTCellFormula cellFormula = this._cell.getF();
        cellFormula.setT(STCellFormulaType.ARRAY);
        cellFormula.setRef(range.formatAsString());
    }
    
    private void setFormula(final String formula, final FormulaType formulaType) {
        final XSSFWorkbook wb = this._row.getSheet().getWorkbook();
        if (formulaType == FormulaType.ARRAY && formula == null) {
            wb.onDeleteFormula(this);
            if (this._cell.isSetF()) {
                this._row.getSheet().onDeleteFormula(this, null);
                this._cell.unsetF();
            }
            return;
        }
        if (wb.getCellFormulaValidation()) {
            final XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create(wb);
            FormulaParser.parse(formula, (FormulaParsingWorkbook)fpb, formulaType, wb.getSheetIndex((Sheet)this.getSheet()), this.getRowIndex());
        }
        if (this._cell.isSetF()) {
            final CTCellFormula f = this._cell.getF();
            f.setStringValue(formula);
            if (f.getT() == STCellFormulaType.SHARED) {
                this.getRow().getSheet().onReadCell(this);
            }
        }
        else {
            final CTCellFormula f = CTCellFormula.Factory.newInstance();
            f.setStringValue(formula);
            this._cell.setF(f);
        }
    }
    
    protected void removeFormulaImpl() {
        this._row.getSheet().getWorkbook().onDeleteFormula(this);
        if (this._cell.isSetF()) {
            this._row.getSheet().onDeleteFormula(this, null);
            this._cell.unsetF();
        }
    }
    
    public int getColumnIndex() {
        return this._cellNum;
    }
    
    public int getRowIndex() {
        return this._row.getRowNum();
    }
    
    public String getReference() {
        final String ref = this._cell.getR();
        if (ref == null) {
            return this.getAddress().formatAsString();
        }
        return ref;
    }
    
    public XSSFCellStyle getCellStyle() {
        XSSFCellStyle style = null;
        if (this._stylesSource.getNumCellStyles() > 0) {
            final long idx = this._cell.isSetS() ? this._cell.getS() : 0L;
            style = this._stylesSource.getStyleAt(Math.toIntExact(idx));
        }
        return style;
    }
    
    public void setCellStyle(final CellStyle style) {
        if (style == null) {
            if (this._cell.isSetS()) {
                this._cell.unsetS();
            }
        }
        else {
            final XSSFCellStyle xStyle = (XSSFCellStyle)style;
            xStyle.verifyBelongsToStylesSource(this._stylesSource);
            final long idx = this._stylesSource.putStyle(xStyle);
            this._cell.setS(idx);
        }
    }
    
    private boolean isFormulaCell() {
        return (this._cell.isSetF() && this._cell.getF().getT() != STCellFormulaType.DATA_TABLE) || this.getSheet().isCellInArrayFormulaContext(this);
    }
    
    public CellType getCellType() {
        if (this.isFormulaCell()) {
            return CellType.FORMULA;
        }
        return this.getBaseCellType(true);
    }
    
    public CellType getCachedFormulaResultType() {
        if (!this.isFormulaCell()) {
            throw new IllegalStateException("Only formula cells have cached results");
        }
        return this.getBaseCellType(false);
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public CellType getCachedFormulaResultTypeEnum() {
        return this.getCachedFormulaResultType();
    }
    
    private CellType getBaseCellType(final boolean blankCells) {
        switch (this._cell.getT().intValue()) {
            case 1: {
                return CellType.BOOLEAN;
            }
            case 2: {
                if (!this._cell.isSetV() && blankCells) {
                    return CellType.BLANK;
                }
                return CellType.NUMERIC;
            }
            case 3: {
                return CellType.ERROR;
            }
            case 4:
            case 5:
            case 6: {
                return CellType.STRING;
            }
            default: {
                throw new IllegalStateException("Illegal cell type: " + this._cell.getT());
            }
        }
    }
    
    public Date getDateCellValue() {
        if (this.getCellType() == CellType.BLANK) {
            return null;
        }
        final double value = this.getNumericCellValue();
        final boolean date1904 = this.getSheet().getWorkbook().isDate1904();
        return DateUtil.getJavaDate(value, date1904);
    }
    
    public LocalDateTime getLocalDateTimeCellValue() {
        if (this.getCellType() == CellType.BLANK) {
            return null;
        }
        final double value = this.getNumericCellValue();
        final boolean date1904 = this.getSheet().getWorkbook().isDate1904();
        return DateUtil.getLocalDateTime(value, date1904);
    }
    
    protected void setCellValueImpl(final Date value) {
        final boolean date1904 = this.getSheet().getWorkbook().isDate1904();
        this.setCellValue(DateUtil.getExcelDate(value, date1904));
    }
    
    protected void setCellValueImpl(final LocalDateTime value) {
        final boolean date1904 = this.getSheet().getWorkbook().isDate1904();
        this.setCellValue(DateUtil.getExcelDate(value, date1904));
    }
    
    protected void setCellValueImpl(final Calendar value) {
        final boolean date1904 = this.getSheet().getWorkbook().isDate1904();
        this.setCellValue(DateUtil.getExcelDate(value, date1904));
    }
    
    public String getErrorCellString() throws IllegalStateException {
        final CellType cellType = this.getBaseCellType(true);
        if (cellType != CellType.ERROR) {
            throw typeMismatch(CellType.ERROR, cellType, false);
        }
        return this._cell.getV();
    }
    
    public byte getErrorCellValue() throws IllegalStateException {
        final String code = this.getErrorCellString();
        if (code == null) {
            return 0;
        }
        try {
            return FormulaError.forString(code).getCode();
        }
        catch (final IllegalArgumentException e) {
            throw new IllegalStateException("Unexpected error code", e);
        }
    }
    
    public void setCellErrorValue(final byte errorCode) {
        final FormulaError error = FormulaError.forInt(errorCode);
        this.setCellErrorValue(error);
    }
    
    public void setCellErrorValue(final FormulaError error) {
        this._cell.setT(STCellType.E);
        this._cell.setV(error.getString());
    }
    
    public void setAsActiveCell() {
        this.getSheet().setActiveCell(this.getAddress());
    }
    
    private void setBlankPrivate() {
        final CTCell blank = CTCell.Factory.newInstance();
        blank.setR(this._cell.getR());
        if (this._cell.isSetS()) {
            blank.setS(this._cell.getS());
        }
        this._cell.set((XmlObject)blank);
    }
    
    protected void setCellNum(final int num) {
        checkBounds(num);
        this._cellNum = num;
        final String ref = new CellReference(this.getRowIndex(), this.getColumnIndex()).formatAsString();
        this._cell.setR(ref);
    }
    
    protected void setCellTypeImpl(final CellType cellType) {
        this.setCellType(cellType, null);
    }
    
    protected void setCellType(final CellType cellType, final BaseXSSFEvaluationWorkbook evalWb) {
        final CellType prevType = this.getCellType();
        if (prevType == CellType.FORMULA && cellType != CellType.FORMULA) {
            if (this._cell.isSetF()) {
                this._row.getSheet().onDeleteFormula(this, evalWb);
            }
            this.getSheet().getWorkbook().onDeleteFormula(this);
        }
        switch (cellType) {
            case NUMERIC: {
                this._cell.setT(STCellType.N);
                break;
            }
            case STRING: {
                if (prevType != CellType.STRING) {
                    final String str = this.convertCellValueToString();
                    final XSSFRichTextString rt = new XSSFRichTextString(str);
                    rt.setStylesTableReference(this._stylesSource);
                    final int sRef = this._sharedStringSource.addSharedStringItem((RichTextString)rt);
                    this._cell.setV(Integer.toString(sRef));
                }
                this._cell.setT(STCellType.S);
                break;
            }
            case FORMULA: {
                if (!this._cell.isSetF()) {
                    final CTCellFormula f = CTCellFormula.Factory.newInstance();
                    f.setStringValue("0");
                    this._cell.setF(f);
                    if (this._cell.isSetT()) {
                        this._cell.unsetT();
                    }
                    break;
                }
                break;
            }
            case BLANK: {
                this.setBlankPrivate();
                break;
            }
            case BOOLEAN: {
                final String newVal = this.convertCellValueToBoolean() ? "1" : "0";
                this._cell.setT(STCellType.B);
                this._cell.setV(newVal);
                break;
            }
            case ERROR: {
                this._cell.setT(STCellType.E);
                break;
            }
            default: {
                throw new IllegalArgumentException("Illegal cell type: " + cellType);
            }
        }
        if (cellType != CellType.FORMULA && this._cell.isSetF()) {
            this._cell.unsetF();
        }
    }
    
    public String toString() {
        switch (this.getCellType()) {
            case NUMERIC: {
                if (DateUtil.isCellDateFormatted((Cell)this)) {
                    final DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", LocaleUtil.getUserLocale());
                    sdf.setTimeZone(LocaleUtil.getUserTimeZone());
                    return sdf.format(this.getDateCellValue());
                }
                return Double.toString(this.getNumericCellValue());
            }
            case STRING: {
                return this.getRichStringCellValue().toString();
            }
            case FORMULA: {
                return this.getCellFormula();
            }
            case BLANK: {
                return "";
            }
            case BOOLEAN: {
                return this.getBooleanCellValue() ? "TRUE" : "FALSE";
            }
            case ERROR: {
                return ErrorEval.getText((int)this.getErrorCellValue());
            }
            default: {
                return "Unknown Cell Type: " + this.getCellType();
            }
        }
    }
    
    public String getRawValue() {
        return this._cell.getV();
    }
    
    private static RuntimeException typeMismatch(final CellType expectedType, final CellType actualType, final boolean isFormulaCell) {
        final String msg = "Cannot get a " + expectedType + " value from a " + actualType + " " + (isFormulaCell ? "formula " : "") + "cell";
        return new IllegalStateException(msg);
    }
    
    private static void checkBounds(final int cellIndex) {
        final SpreadsheetVersion v = SpreadsheetVersion.EXCEL2007;
        final int maxcol = SpreadsheetVersion.EXCEL2007.getLastColumnIndex();
        if (cellIndex < 0 || cellIndex > maxcol) {
            throw new IllegalArgumentException("Invalid column index (" + cellIndex + ").  Allowable column range for " + v.name() + " is (0.." + maxcol + ") or ('A'..'" + v.getLastColumnName() + "')");
        }
    }
    
    public XSSFComment getCellComment() {
        return this.getSheet().getCellComment(new CellAddress((Cell)this));
    }
    
    public void setCellComment(final Comment comment) {
        if (comment == null) {
            this.removeCellComment();
            return;
        }
        comment.setAddress(this.getRowIndex(), this.getColumnIndex());
    }
    
    public void removeCellComment() {
        final XSSFComment comment = this.getCellComment();
        if (comment != null) {
            final CellAddress ref = new CellAddress(this.getReference());
            final XSSFSheet sh = this.getSheet();
            sh.getCommentsTable(false).removeComment(ref);
            sh.getVMLDrawing(false).removeCommentShape(this.getRowIndex(), this.getColumnIndex());
        }
    }
    
    public XSSFHyperlink getHyperlink() {
        return this.getSheet().getHyperlink(this._row.getRowNum(), this._cellNum);
    }
    
    public void setHyperlink(final Hyperlink hyperlink) {
        if (hyperlink == null) {
            this.removeHyperlink();
            return;
        }
        final XSSFHyperlink link = (XSSFHyperlink)hyperlink;
        link.setCellReference(new CellReference(this._row.getRowNum(), this._cellNum).formatAsString());
        this.getSheet().addHyperlink(link);
    }
    
    public void removeHyperlink() {
        this.getSheet().removeHyperlink(this._row.getRowNum(), this._cellNum);
    }
    
    @Internal
    public CTCell getCTCell() {
        return this._cell;
    }
    
    @Internal
    public void setCTCell(final CTCell cell) {
        this._cell = cell;
    }
    
    private boolean convertCellValueToBoolean() {
        CellType cellType = this.getCellType();
        if (cellType == CellType.FORMULA) {
            cellType = this.getBaseCellType(false);
        }
        switch (cellType) {
            case BOOLEAN: {
                return "1".equals(this._cell.getV());
            }
            case STRING: {
                final int sstIndex = Integer.parseInt(this._cell.getV());
                final XSSFRichTextString rt = new XSSFRichTextString(this._sharedStringSource.getEntryAt(sstIndex));
                final String text = rt.getString();
                return Boolean.parseBoolean(text);
            }
            case NUMERIC: {
                return Double.parseDouble(this._cell.getV()) != 0.0;
            }
            case BLANK:
            case ERROR: {
                return false;
            }
            default: {
                throw new RuntimeException("Unexpected cell type (" + cellType + ")");
            }
        }
    }
    
    private String convertCellValueToString() {
        CellType cellType = this.getCellType();
        switch (cellType) {
            case BLANK: {
                return "";
            }
            case BOOLEAN: {
                return "1".equals(this._cell.getV()) ? "TRUE" : "FALSE";
            }
            case STRING: {
                final int sstIndex = Integer.parseInt(this._cell.getV());
                final XSSFRichTextString rt = new XSSFRichTextString(this._sharedStringSource.getEntryAt(sstIndex));
                return rt.getString();
            }
            case NUMERIC:
            case ERROR: {
                return this._cell.getV();
            }
            case FORMULA: {
                cellType = this.getBaseCellType(false);
                final String textValue = this._cell.getV();
                switch (cellType) {
                    case BOOLEAN: {
                        if ("1".equals(textValue)) {
                            return "TRUE";
                        }
                        if ("0".equals(textValue)) {
                            return "FALSE";
                        }
                        throw new IllegalStateException("Unexpected boolean cached formula value '" + textValue + "'.");
                    }
                    case NUMERIC:
                    case STRING:
                    case ERROR: {
                        return textValue;
                    }
                    default: {
                        throw new IllegalStateException("Unexpected formula result type (" + cellType + ")");
                    }
                }
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected cell type (" + cellType + ")");
            }
        }
    }
    
    public CellRangeAddress getArrayFormulaRange() {
        final XSSFCell cell = this.getSheet().getFirstCellInArrayFormula(this);
        if (cell == null) {
            throw new IllegalStateException("Cell " + new CellReference((Cell)this).formatAsString() + " is not part of an array formula.");
        }
        final String formulaRef = cell._cell.getF().getRef();
        return CellRangeAddress.valueOf(formulaRef);
    }
    
    public boolean isPartOfArrayFormulaGroup() {
        return this.getSheet().isCellInArrayFormulaContext(this);
    }
    
    public void updateCellReferencesForShifting(final String msg) {
        if (this.isPartOfArrayFormulaGroup()) {
            this.tryToDeleteArrayFormula(msg);
        }
        final CalculationChain calcChain = this.getSheet().getWorkbook().getCalculationChain();
        final int sheetId = Math.toIntExact(this.getSheet().sheet.getSheetId());
        if (calcChain != null) {
            calcChain.removeItem(sheetId, this.getReference());
        }
        final CTCell ctCell = this.getCTCell();
        final String r = new CellReference(this.getRowIndex(), this.getColumnIndex()).formatAsString();
        ctCell.setR(r);
    }
}
