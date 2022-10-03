package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.ptg.ExpPtg;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Removal;
import java.util.Iterator;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.hssf.record.RecordBase;
import java.util.List;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Comment;
import java.text.SimpleDateFormat;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.RichTextString;
import java.util.Calendar;
import java.time.LocalDateTime;
import org.apache.poi.ss.usermodel.DateUtil;
import java.util.Date;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellBase;

public class HSSFCell extends CellBase
{
    private static final String FILE_FORMAT_NAME = "BIFF8";
    public static final int LAST_COLUMN_NUMBER;
    private static final String LAST_COLUMN_NAME;
    public static final short ENCODING_UNCHANGED = -1;
    public static final short ENCODING_COMPRESSED_UNICODE = 0;
    public static final short ENCODING_UTF_16 = 1;
    private final HSSFWorkbook _book;
    private final HSSFSheet _sheet;
    private CellType _cellType;
    private HSSFRichTextString _stringValue;
    private CellValueRecordInterface _record;
    private HSSFComment _comment;
    
    protected HSSFCell(final HSSFWorkbook book, final HSSFSheet sheet, final int row, final short col) {
        checkBounds(col);
        this._stringValue = null;
        this._book = book;
        this._sheet = sheet;
        final short xfindex = sheet.getSheet().getXFIndexForColAt(col);
        this.setCellType(CellType.BLANK, false, row, col, xfindex);
    }
    
    @Override
    protected SpreadsheetVersion getSpreadsheetVersion() {
        return SpreadsheetVersion.EXCEL97;
    }
    
    @Override
    public HSSFSheet getSheet() {
        return this._sheet;
    }
    
    @Override
    public HSSFRow getRow() {
        final int rowIndex = this.getRowIndex();
        return this._sheet.getRow(rowIndex);
    }
    
    protected HSSFCell(final HSSFWorkbook book, final HSSFSheet sheet, final int row, final short col, final CellType type) {
        checkBounds(col);
        this._cellType = CellType._NONE;
        this._stringValue = null;
        this._book = book;
        this._sheet = sheet;
        final short xfindex = sheet.getSheet().getXFIndexForColAt(col);
        this.setCellType(type, false, row, col, xfindex);
    }
    
    protected HSSFCell(final HSSFWorkbook book, final HSSFSheet sheet, final CellValueRecordInterface cval) {
        this._record = cval;
        this._cellType = determineType(cval);
        this._stringValue = null;
        this._book = book;
        this._sheet = sheet;
        switch (this._cellType) {
            case STRING: {
                this._stringValue = new HSSFRichTextString(book.getWorkbook(), (LabelSSTRecord)cval);
            }
            case FORMULA: {
                this._stringValue = new HSSFRichTextString(((FormulaRecordAggregate)cval).getStringValue());
                break;
            }
        }
    }
    
    private static CellType determineType(final CellValueRecordInterface cval) {
        if (cval instanceof FormulaRecordAggregate) {
            return CellType.FORMULA;
        }
        final Record record = (Record)cval;
        switch (record.getSid()) {
            case 515: {
                return CellType.NUMERIC;
            }
            case 513: {
                return CellType.BLANK;
            }
            case 253: {
                return CellType.STRING;
            }
            case 517: {
                final BoolErrRecord boolErrRecord = (BoolErrRecord)record;
                return boolErrRecord.isBoolean() ? CellType.BOOLEAN : CellType.ERROR;
            }
            default: {
                throw new RuntimeException("Bad cell value rec (" + cval.getClass().getName() + ")");
            }
        }
    }
    
    protected InternalWorkbook getBoundWorkbook() {
        return this._book.getWorkbook();
    }
    
    @Override
    public int getRowIndex() {
        return this._record.getRow();
    }
    
    protected void updateCellNum(final short num) {
        this._record.setColumn(num);
    }
    
    @Override
    public int getColumnIndex() {
        return this._record.getColumn() & 0xFFFF;
    }
    
    @Override
    protected void setCellTypeImpl(final CellType cellType) {
        this.notifyFormulaChanging();
        final int row = this._record.getRow();
        final short col = this._record.getColumn();
        final short styleIndex = this._record.getXFIndex();
        this.setCellType(cellType, true, row, col, styleIndex);
    }
    
    private void setCellType(final CellType cellType, final boolean setValue, final int row, final short col, final short styleIndex) {
        switch (cellType) {
            case FORMULA: {
                FormulaRecordAggregate frec;
                if (cellType != this._cellType) {
                    frec = this._sheet.getSheet().getRowsAggregate().createFormula(row, col);
                }
                else {
                    frec = (FormulaRecordAggregate)this._record;
                    frec.setRow(row);
                    frec.setColumn(col);
                }
                if (this.getCellType() == CellType.BLANK) {
                    frec.getFormulaRecord().setValue(0.0);
                }
                frec.setXFIndex(styleIndex);
                this._record = frec;
                break;
            }
            case NUMERIC: {
                NumberRecord nrec;
                if (cellType != this._cellType) {
                    nrec = new NumberRecord();
                }
                else {
                    nrec = (NumberRecord)this._record;
                }
                nrec.setColumn(col);
                if (setValue) {
                    nrec.setValue(this.getNumericCellValue());
                }
                nrec.setXFIndex(styleIndex);
                nrec.setRow(row);
                this._record = nrec;
                break;
            }
            case STRING: {
                LabelSSTRecord lrec;
                if (cellType == this._cellType) {
                    lrec = (LabelSSTRecord)this._record;
                }
                else {
                    lrec = new LabelSSTRecord();
                    lrec.setColumn(col);
                    lrec.setRow(row);
                    lrec.setXFIndex(styleIndex);
                }
                if (setValue) {
                    final String str = this.convertCellValueToString();
                    if (str == null) {
                        this.setCellType(CellType.BLANK, false, row, col, styleIndex);
                        return;
                    }
                    final int sstIndex = this._book.getWorkbook().addSSTString(new UnicodeString(str));
                    lrec.setSSTIndex(sstIndex);
                    final UnicodeString us = this._book.getWorkbook().getSSTString(sstIndex);
                    (this._stringValue = new HSSFRichTextString()).setUnicodeString(us);
                }
                this._record = lrec;
                break;
            }
            case BLANK: {
                BlankRecord brec;
                if (cellType != this._cellType) {
                    brec = new BlankRecord();
                }
                else {
                    brec = (BlankRecord)this._record;
                }
                brec.setColumn(col);
                brec.setXFIndex(styleIndex);
                brec.setRow(row);
                this._record = brec;
                break;
            }
            case BOOLEAN: {
                BoolErrRecord boolRec;
                if (cellType != this._cellType) {
                    boolRec = new BoolErrRecord();
                }
                else {
                    boolRec = (BoolErrRecord)this._record;
                }
                boolRec.setColumn(col);
                if (setValue) {
                    boolRec.setValue(this.convertCellValueToBoolean());
                }
                boolRec.setXFIndex(styleIndex);
                boolRec.setRow(row);
                this._record = boolRec;
                break;
            }
            case ERROR: {
                BoolErrRecord errRec;
                if (cellType != this._cellType) {
                    errRec = new BoolErrRecord();
                }
                else {
                    errRec = (BoolErrRecord)this._record;
                }
                errRec.setColumn(col);
                if (setValue) {
                    errRec.setValue(FormulaError.VALUE.getCode());
                }
                errRec.setXFIndex(styleIndex);
                errRec.setRow(row);
                this._record = errRec;
                break;
            }
            default: {
                throw new IllegalStateException("Invalid cell type: " + cellType);
            }
        }
        if (cellType != this._cellType && this._cellType != CellType._NONE) {
            this._sheet.getSheet().replaceValueRecord(this._record);
        }
        this._cellType = cellType;
    }
    
    @Override
    public CellType getCellType() {
        return this._cellType;
    }
    
    @Override
    protected void setCellValueImpl(final double value) {
        switch (this._cellType) {
            default: {
                this.setCellType(CellType.NUMERIC, false, this._record.getRow(), this._record.getColumn(), this._record.getXFIndex());
            }
            case NUMERIC: {
                ((NumberRecord)this._record).setValue(value);
                break;
            }
            case FORMULA: {
                ((FormulaRecordAggregate)this._record).setCachedDoubleResult(value);
                break;
            }
        }
    }
    
    @Override
    protected void setCellValueImpl(final Date value) {
        this.setCellValue(DateUtil.getExcelDate(value, this._book.getWorkbook().isUsing1904DateWindowing()));
    }
    
    @Override
    protected void setCellValueImpl(final LocalDateTime value) {
        this.setCellValue(DateUtil.getExcelDate(value, this._book.getWorkbook().isUsing1904DateWindowing()));
    }
    
    @Override
    protected void setCellValueImpl(final Calendar value) {
        this.setCellValue(DateUtil.getExcelDate(value, this._book.getWorkbook().isUsing1904DateWindowing()));
    }
    
    @Override
    protected void setCellValueImpl(final String value) {
        this.setCellValueImpl(new HSSFRichTextString(value));
    }
    
    @Override
    protected void setCellValueImpl(final RichTextString value) {
        if (this._cellType == CellType.FORMULA) {
            final FormulaRecordAggregate fr = (FormulaRecordAggregate)this._record;
            fr.setCachedStringResult(value.getString());
            this._stringValue = new HSSFRichTextString(value.getString());
            return;
        }
        if (this._cellType != CellType.STRING) {
            final int row = this._record.getRow();
            final short col = this._record.getColumn();
            final short styleIndex = this._record.getXFIndex();
            this.setCellType(CellType.STRING, false, row, col, styleIndex);
        }
        final HSSFRichTextString hvalue = (HSSFRichTextString)value;
        final UnicodeString str = hvalue.getUnicodeString();
        final int index = this._book.getWorkbook().addSSTString(str);
        ((LabelSSTRecord)this._record).setSSTIndex(index);
        (this._stringValue = hvalue).setWorkbookReferences(this._book.getWorkbook(), (LabelSSTRecord)this._record);
        this._stringValue.setUnicodeString(this._book.getWorkbook().getSSTString(index));
    }
    
    @Override
    protected void setCellFormulaImpl(final String formula) {
        assert formula != null;
        final int row = this._record.getRow();
        final short col = this._record.getColumn();
        final short styleIndex = this._record.getXFIndex();
        final CellValue savedValue = this.readValue();
        final int sheetIndex = this._book.getSheetIndex(this._sheet);
        final Ptg[] ptgs = HSSFFormulaParser.parse(formula, this._book, FormulaType.CELL, sheetIndex);
        this.setCellType(CellType.FORMULA, false, row, col, styleIndex);
        final FormulaRecordAggregate agg = (FormulaRecordAggregate)this._record;
        final FormulaRecord frec = agg.getFormulaRecord();
        frec.setOptions((short)2);
        if (agg.getXFIndex() == 0) {
            agg.setXFIndex((short)15);
        }
        agg.setParsedExpression(ptgs);
        this.restoreValue(savedValue);
    }
    
    private CellValue readValue() {
        final CellType valueType = (this.getCellType() == CellType.FORMULA) ? this.getCachedFormulaResultType() : this.getCellType();
        switch (valueType) {
            case NUMERIC: {
                return new CellValue(this.getNumericCellValue());
            }
            case STRING: {
                return new CellValue(this.getStringCellValue());
            }
            case BOOLEAN: {
                return CellValue.valueOf(this.getBooleanCellValue());
            }
            case ERROR: {
                return CellValue.getError(this.getErrorCellValue());
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    private void restoreValue(final CellValue value) {
        switch (value.getCellType()) {
            case NUMERIC: {
                this.setCellValue(value.getNumberValue());
                break;
            }
            case STRING: {
                this.setCellValue(value.getStringValue());
                break;
            }
            case BOOLEAN: {
                this.setCellValue(value.getBooleanValue());
                break;
            }
            case ERROR: {
                this.setCellErrorValue(FormulaError.forInt(value.getErrorValue()));
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    protected void removeFormulaImpl() {
        assert this.getCellType() == CellType.FORMULA;
        this.notifyFormulaChanging();
        switch (this.getCachedFormulaResultType()) {
            case NUMERIC: {
                final double numericValue = ((FormulaRecordAggregate)this._record).getFormulaRecord().getValue();
                this._record = new NumberRecord();
                ((NumberRecord)this._record).setValue(numericValue);
                this._cellType = CellType.NUMERIC;
                break;
            }
            case STRING: {
                this._record = new NumberRecord();
                ((NumberRecord)this._record).setValue(0.0);
                this._cellType = CellType.STRING;
                break;
            }
            case BOOLEAN: {
                final boolean booleanValue = ((FormulaRecordAggregate)this._record).getFormulaRecord().getCachedBooleanValue();
                this._record = new BoolErrRecord();
                ((BoolErrRecord)this._record).setValue(booleanValue);
                this._cellType = CellType.BOOLEAN;
                break;
            }
            case ERROR: {
                final byte errorValue = (byte)((FormulaRecordAggregate)this._record).getFormulaRecord().getCachedErrorValue();
                this._record = new BoolErrRecord();
                ((BoolErrRecord)this._record).setValue(errorValue);
                this._cellType = CellType.ERROR;
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    private void notifyFormulaChanging() {
        if (this._record instanceof FormulaRecordAggregate) {
            ((FormulaRecordAggregate)this._record).notifyFormulaChanging();
        }
    }
    
    @Override
    public String getCellFormula() {
        if (!(this._record instanceof FormulaRecordAggregate)) {
            throw typeMismatch(CellType.FORMULA, this._cellType, true);
        }
        return HSSFFormulaParser.toFormulaString(this._book, ((FormulaRecordAggregate)this._record).getFormulaTokens());
    }
    
    private static RuntimeException typeMismatch(final CellType expectedTypeCode, final CellType actualTypeCode, final boolean isFormulaCell) {
        final String msg = "Cannot get a " + expectedTypeCode + " value from a " + actualTypeCode + " " + (isFormulaCell ? "formula " : "") + "cell";
        return new IllegalStateException(msg);
    }
    
    private static void checkFormulaCachedValueType(final CellType expectedTypeCode, final FormulaRecord fr) {
        final CellType cachedValueType = CellType.forInt(fr.getCachedResultType());
        if (cachedValueType != expectedTypeCode) {
            throw typeMismatch(expectedTypeCode, cachedValueType, true);
        }
    }
    
    @Override
    public double getNumericCellValue() {
        switch (this._cellType) {
            case BLANK: {
                return 0.0;
            }
            case NUMERIC: {
                return ((NumberRecord)this._record).getValue();
            }
            default: {
                throw typeMismatch(CellType.NUMERIC, this._cellType, false);
            }
            case FORMULA: {
                final FormulaRecord fr = ((FormulaRecordAggregate)this._record).getFormulaRecord();
                checkFormulaCachedValueType(CellType.NUMERIC, fr);
                return fr.getValue();
            }
        }
    }
    
    @Override
    public Date getDateCellValue() {
        if (this._cellType == CellType.BLANK) {
            return null;
        }
        final double value = this.getNumericCellValue();
        if (this._book.getWorkbook().isUsing1904DateWindowing()) {
            return DateUtil.getJavaDate(value, true);
        }
        return DateUtil.getJavaDate(value, false);
    }
    
    @Override
    public LocalDateTime getLocalDateTimeCellValue() {
        if (this._cellType == CellType.BLANK) {
            return null;
        }
        final double value = this.getNumericCellValue();
        if (this._book.getWorkbook().isUsing1904DateWindowing()) {
            return DateUtil.getLocalDateTime(value, true);
        }
        return DateUtil.getLocalDateTime(value, false);
    }
    
    @Override
    public String getStringCellValue() {
        final HSSFRichTextString str = this.getRichStringCellValue();
        return str.getString();
    }
    
    @Override
    public HSSFRichTextString getRichStringCellValue() {
        switch (this._cellType) {
            case BLANK: {
                return new HSSFRichTextString("");
            }
            case STRING: {
                return this._stringValue;
            }
            default: {
                throw typeMismatch(CellType.STRING, this._cellType, false);
            }
            case FORMULA: {
                final FormulaRecordAggregate fra = (FormulaRecordAggregate)this._record;
                checkFormulaCachedValueType(CellType.STRING, fra.getFormulaRecord());
                final String strVal = fra.getStringValue();
                return new HSSFRichTextString((strVal == null) ? "" : strVal);
            }
        }
    }
    
    @Override
    public void setCellValue(final boolean value) {
        final int row = this._record.getRow();
        final short col = this._record.getColumn();
        final short styleIndex = this._record.getXFIndex();
        switch (this._cellType) {
            default: {
                this.setCellType(CellType.BOOLEAN, false, row, col, styleIndex);
            }
            case BOOLEAN: {
                ((BoolErrRecord)this._record).setValue(value);
                break;
            }
            case FORMULA: {
                ((FormulaRecordAggregate)this._record).setCachedBooleanResult(value);
                break;
            }
        }
    }
    
    @Override
    @Deprecated
    public void setCellErrorValue(final byte errorCode) {
        final FormulaError error = FormulaError.forInt(errorCode);
        this.setCellErrorValue(error);
    }
    
    public void setCellErrorValue(final FormulaError error) {
        final int row = this._record.getRow();
        final short col = this._record.getColumn();
        final short styleIndex = this._record.getXFIndex();
        switch (this._cellType) {
            default: {
                this.setCellType(CellType.ERROR, false, row, col, styleIndex);
            }
            case ERROR: {
                ((BoolErrRecord)this._record).setValue(error);
                break;
            }
            case FORMULA: {
                ((FormulaRecordAggregate)this._record).setCachedErrorResult(error.getCode());
                break;
            }
        }
    }
    
    private boolean convertCellValueToBoolean() {
        switch (this._cellType) {
            case BOOLEAN: {
                return ((BoolErrRecord)this._record).getBooleanValue();
            }
            case STRING: {
                final int sstIndex = ((LabelSSTRecord)this._record).getSSTIndex();
                final String text = this._book.getWorkbook().getSSTString(sstIndex).getString();
                return Boolean.valueOf(text);
            }
            case NUMERIC: {
                return ((NumberRecord)this._record).getValue() != 0.0;
            }
            case FORMULA: {
                final FormulaRecord fr = ((FormulaRecordAggregate)this._record).getFormulaRecord();
                checkFormulaCachedValueType(CellType.BOOLEAN, fr);
                return fr.getCachedBooleanValue();
            }
            case BLANK:
            case ERROR: {
                return false;
            }
            default: {
                throw new RuntimeException("Unexpected cell type (" + this._cellType + ")");
            }
        }
    }
    
    private String convertCellValueToString() {
        switch (this._cellType) {
            case BLANK: {
                return "";
            }
            case BOOLEAN: {
                return ((BoolErrRecord)this._record).getBooleanValue() ? "TRUE" : "FALSE";
            }
            case STRING: {
                final int sstIndex = ((LabelSSTRecord)this._record).getSSTIndex();
                return this._book.getWorkbook().getSSTString(sstIndex).getString();
            }
            case NUMERIC: {
                return NumberToTextConverter.toText(((NumberRecord)this._record).getValue());
            }
            case ERROR: {
                return FormulaError.forInt(((BoolErrRecord)this._record).getErrorValue()).getString();
            }
            case FORMULA: {
                final FormulaRecordAggregate fra = (FormulaRecordAggregate)this._record;
                final FormulaRecord fr = fra.getFormulaRecord();
                switch (CellType.forInt(fr.getCachedResultType())) {
                    case BOOLEAN: {
                        return fr.getCachedBooleanValue() ? "TRUE" : "FALSE";
                    }
                    case STRING: {
                        return fra.getStringValue();
                    }
                    case NUMERIC: {
                        return NumberToTextConverter.toText(fr.getValue());
                    }
                    case ERROR: {
                        return FormulaError.forInt(fr.getCachedErrorValue()).getString();
                    }
                    default: {
                        throw new IllegalStateException("Unexpected formula result type (" + this._cellType + ")");
                    }
                }
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected cell type (" + this._cellType + ")");
            }
        }
    }
    
    @Override
    public boolean getBooleanCellValue() {
        switch (this._cellType) {
            case BLANK: {
                return false;
            }
            case BOOLEAN: {
                return ((BoolErrRecord)this._record).getBooleanValue();
            }
            case FORMULA: {
                final FormulaRecord fr = ((FormulaRecordAggregate)this._record).getFormulaRecord();
                checkFormulaCachedValueType(CellType.BOOLEAN, fr);
                return fr.getCachedBooleanValue();
            }
            default: {
                throw typeMismatch(CellType.BOOLEAN, this._cellType, false);
            }
        }
    }
    
    @Override
    public byte getErrorCellValue() {
        switch (this._cellType) {
            case ERROR: {
                return ((BoolErrRecord)this._record).getErrorValue();
            }
            case FORMULA: {
                final FormulaRecord fr = ((FormulaRecordAggregate)this._record).getFormulaRecord();
                checkFormulaCachedValueType(CellType.ERROR, fr);
                return (byte)fr.getCachedErrorValue();
            }
            default: {
                throw typeMismatch(CellType.ERROR, this._cellType, false);
            }
        }
    }
    
    @Override
    public void setCellStyle(final CellStyle style) {
        this.setCellStyle((HSSFCellStyle)style);
    }
    
    public void setCellStyle(final HSSFCellStyle style) {
        if (style == null) {
            this._record.setXFIndex((short)15);
            return;
        }
        style.verifyBelongsToWorkbook(this._book);
        short styleIndex;
        if (style.getUserStyleName() != null) {
            styleIndex = this.applyUserCellStyle(style);
        }
        else {
            styleIndex = style.getIndex();
        }
        this._record.setXFIndex(styleIndex);
    }
    
    @Override
    public HSSFCellStyle getCellStyle() {
        final short styleIndex = this._record.getXFIndex();
        final ExtendedFormatRecord xf = this._book.getWorkbook().getExFormatAt(styleIndex);
        return new HSSFCellStyle(styleIndex, xf, this._book);
    }
    
    protected CellValueRecordInterface getCellValueRecord() {
        return this._record;
    }
    
    private static void checkBounds(final int cellIndex) {
        if (cellIndex < 0 || cellIndex > HSSFCell.LAST_COLUMN_NUMBER) {
            throw new IllegalArgumentException("Invalid column index (" + cellIndex + ").  Allowable column range for " + "BIFF8" + " is (0.." + HSSFCell.LAST_COLUMN_NUMBER + ") or ('A'..'" + HSSFCell.LAST_COLUMN_NAME + "')");
        }
    }
    
    @Override
    public void setAsActiveCell() {
        final int row = this._record.getRow();
        final short col = this._record.getColumn();
        this._sheet.getSheet().setActiveCellRow(row);
        this._sheet.getSheet().setActiveCellCol(col);
    }
    
    @Override
    public String toString() {
        switch (this.getCellTypeEnum()) {
            case BLANK: {
                return "";
            }
            case BOOLEAN: {
                return this.getBooleanCellValue() ? "TRUE" : "FALSE";
            }
            case ERROR: {
                return ErrorEval.getText(((BoolErrRecord)this._record).getErrorValue());
            }
            case FORMULA: {
                return this.getCellFormula();
            }
            case NUMERIC: {
                if (DateUtil.isCellDateFormatted(this)) {
                    final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", LocaleUtil.getUserLocale());
                    sdf.setTimeZone(LocaleUtil.getUserTimeZone());
                    return sdf.format(this.getDateCellValue());
                }
                return String.valueOf(this.getNumericCellValue());
            }
            case STRING: {
                return this.getStringCellValue();
            }
            default: {
                return "Unknown Cell Type: " + this.getCellType();
            }
        }
    }
    
    @Override
    public void setCellComment(final Comment comment) {
        if (comment == null) {
            this.removeCellComment();
            return;
        }
        comment.setRow(this._record.getRow());
        comment.setColumn(this._record.getColumn());
        this._comment = (HSSFComment)comment;
    }
    
    @Override
    public HSSFComment getCellComment() {
        if (this._comment == null) {
            this._comment = this._sheet.findCellComment(this._record.getRow(), this._record.getColumn());
        }
        return this._comment;
    }
    
    @Override
    public void removeCellComment() {
        final HSSFComment comment = this._sheet.findCellComment(this._record.getRow(), this._record.getColumn());
        this._comment = null;
        if (null == comment) {
            return;
        }
        this._sheet.getDrawingPatriarch().removeShape(comment);
    }
    
    @Override
    public HSSFHyperlink getHyperlink() {
        return this._sheet.getHyperlink(this._record.getRow(), (int)this._record.getColumn());
    }
    
    @Override
    public void setHyperlink(final Hyperlink hyperlink) {
        if (hyperlink == null) {
            this.removeHyperlink();
            return;
        }
        final HSSFHyperlink link = (HSSFHyperlink)hyperlink;
        link.setFirstRow(this._record.getRow());
        link.setLastRow(this._record.getRow());
        link.setFirstColumn(this._record.getColumn());
        link.setLastColumn(this._record.getColumn());
        switch (link.getTypeEnum()) {
            case EMAIL:
            case URL: {
                link.setLabel("url");
                break;
            }
            case FILE: {
                link.setLabel("file");
                break;
            }
            case DOCUMENT: {
                link.setLabel("place");
                break;
            }
        }
        final List<RecordBase> records = this._sheet.getSheet().getRecords();
        final int eofLoc = records.size() - 1;
        records.add(eofLoc, link.record);
    }
    
    @Override
    public void removeHyperlink() {
        final Iterator<RecordBase> it = this._sheet.getSheet().getRecords().iterator();
        while (it.hasNext()) {
            final RecordBase rec = it.next();
            if (rec instanceof HyperlinkRecord) {
                final HyperlinkRecord link = (HyperlinkRecord)rec;
                if (link.getFirstColumn() == this._record.getColumn() && link.getFirstRow() == this._record.getRow()) {
                    it.remove();
                    return;
                }
                continue;
            }
        }
    }
    
    @Override
    public CellType getCachedFormulaResultType() {
        if (this._cellType != CellType.FORMULA) {
            throw new IllegalStateException("Only formula cells have cached results");
        }
        final int code = ((FormulaRecordAggregate)this._record).getFormulaRecord().getCachedResultType();
        return CellType.forInt(code);
    }
    
    @Deprecated
    @Removal(version = "4.2")
    @Override
    public CellType getCachedFormulaResultTypeEnum() {
        return this.getCachedFormulaResultType();
    }
    
    void setCellArrayFormula(final CellRangeAddress range) {
        final int row = this._record.getRow();
        final short col = this._record.getColumn();
        final short styleIndex = this._record.getXFIndex();
        this.setCellType(CellType.FORMULA, false, row, col, styleIndex);
        final Ptg[] ptgsForCell = { new ExpPtg(range.getFirstRow(), range.getFirstColumn()) };
        final FormulaRecordAggregate agg = (FormulaRecordAggregate)this._record;
        agg.setParsedExpression(ptgsForCell);
    }
    
    @Override
    public CellRangeAddress getArrayFormulaRange() {
        if (this._cellType != CellType.FORMULA) {
            final String ref = new CellReference(this).formatAsString();
            throw new IllegalStateException("Cell " + ref + " is not part of an array formula.");
        }
        return ((FormulaRecordAggregate)this._record).getArrayFormulaRange();
    }
    
    @Override
    public boolean isPartOfArrayFormulaGroup() {
        return this._cellType == CellType.FORMULA && ((FormulaRecordAggregate)this._record).isPartOfArrayFormula();
    }
    
    private short applyUserCellStyle(final HSSFCellStyle style) {
        if (style.getUserStyleName() == null) {
            throw new IllegalArgumentException("Expected user-defined style");
        }
        final InternalWorkbook iwb = this._book.getWorkbook();
        short userXf = -1;
        final int numfmt = iwb.getNumExFormats();
        for (short i = 0; i < numfmt; ++i) {
            final ExtendedFormatRecord xf = iwb.getExFormatAt(i);
            if (xf.getXFType() == 0 && xf.getParentIndex() == style.getIndex()) {
                userXf = i;
                break;
            }
        }
        short styleIndex;
        if (userXf == -1) {
            final ExtendedFormatRecord xfr = iwb.createCellXF();
            xfr.cloneStyleFrom(iwb.getExFormatAt(style.getIndex()));
            xfr.setIndentionOptions((short)0);
            xfr.setXFType((short)0);
            xfr.setParentIndex(style.getIndex());
            styleIndex = (short)numfmt;
        }
        else {
            styleIndex = userXf;
        }
        return styleIndex;
    }
    
    static {
        LAST_COLUMN_NUMBER = SpreadsheetVersion.EXCEL97.getLastColumnIndex();
        LAST_COLUMN_NAME = SpreadsheetVersion.EXCEL97.getLastColumnName();
    }
}
