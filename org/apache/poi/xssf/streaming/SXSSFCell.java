package org.apache.poi.xssf.streaming;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.RichTextString;
import java.util.Calendar;
import java.time.LocalDateTime;
import org.apache.poi.ss.usermodel.DateUtil;
import java.util.Date;
import org.apache.poi.util.Removal;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellBase;

public class SXSSFCell extends CellBase
{
    private final SXSSFRow _row;
    private Value _value;
    private CellStyle _style;
    private Property _firstProperty;
    
    public SXSSFCell(final SXSSFRow row, final CellType cellType) {
        this._row = row;
        this._value = new BlankValue();
        this.setType(cellType);
    }
    
    protected SpreadsheetVersion getSpreadsheetVersion() {
        return SpreadsheetVersion.EXCEL2007;
    }
    
    public int getColumnIndex() {
        return this._row.getCellIndex(this);
    }
    
    public int getRowIndex() {
        return this._row.getRowNum();
    }
    
    public SXSSFSheet getSheet() {
        return this._row.getSheet();
    }
    
    public Row getRow() {
        return (Row)this._row;
    }
    
    protected void setCellTypeImpl(final CellType cellType) {
        this.ensureType(cellType);
    }
    
    private boolean isFormulaCell() {
        return this._value instanceof FormulaValue;
    }
    
    public CellType getCellType() {
        if (this.isFormulaCell()) {
            return CellType.FORMULA;
        }
        return this._value.getType();
    }
    
    public CellType getCachedFormulaResultType() {
        if (!this.isFormulaCell()) {
            throw new IllegalStateException("Only formula cells have cached results");
        }
        return ((FormulaValue)this._value).getFormulaType();
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public CellType getCachedFormulaResultTypeEnum() {
        return this.getCachedFormulaResultType();
    }
    
    public void setCellValueImpl(final double value) {
        this.ensureTypeOrFormulaType(CellType.NUMERIC);
        if (this._value.getType() == CellType.FORMULA) {
            ((NumericFormulaValue)this._value).setPreEvaluatedValue(value);
        }
        else {
            ((NumericValue)this._value).setValue(value);
        }
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
    
    protected void setCellValueImpl(final RichTextString value) {
        this.ensureRichTextStringType();
        if (this._value instanceof RichTextStringFormulaValue) {
            ((RichTextStringFormulaValue)this._value).setPreEvaluatedValue(value);
        }
        else {
            ((RichTextValue)this._value).setValue(value);
        }
    }
    
    protected void setCellValueImpl(final String value) {
        this.ensureTypeOrFormulaType(CellType.STRING);
        if (this._value.getType() == CellType.FORMULA) {
            ((StringFormulaValue)this._value).setPreEvaluatedValue(value);
        }
        else {
            ((PlainStringValue)this._value).setValue(value);
        }
    }
    
    public void setCellFormulaImpl(final String formula) throws FormulaParseException {
        assert formula != null;
        if (this.getCellType() == CellType.FORMULA) {
            ((FormulaValue)this._value).setValue(formula);
        }
        else {
            switch (this.getCellType()) {
                case NUMERIC: {
                    this._value = new NumericFormulaValue(formula, this.getNumericCellValue());
                    break;
                }
                case STRING: {
                    if (this._value instanceof PlainStringValue) {
                        this._value = new StringFormulaValue(formula, this.getStringCellValue());
                        break;
                    }
                    assert this._value instanceof RichTextValue;
                    this._value = new RichTextStringFormulaValue(formula, ((RichTextValue)this._value).getValue());
                    break;
                }
                case BOOLEAN: {
                    this._value = new BooleanFormulaValue(formula, this.getBooleanCellValue());
                    break;
                }
                case ERROR: {
                    this._value = new ErrorFormulaValue(formula, this.getErrorCellValue());
                    break;
                }
                case _NONE:
                case FORMULA:
                case BLANK: {
                    throw new AssertionError();
                }
            }
        }
    }
    
    protected void removeFormulaImpl() {
        assert this.getCellType() == CellType.FORMULA;
        switch (this.getCachedFormulaResultType()) {
            case NUMERIC: {
                final double numericValue = ((NumericFormulaValue)this._value).getPreEvaluatedValue();
                this._value = new NumericValue();
                ((NumericValue)this._value).setValue(numericValue);
                break;
            }
            case STRING: {
                final String stringValue = ((StringFormulaValue)this._value).getPreEvaluatedValue();
                this._value = new PlainStringValue();
                ((PlainStringValue)this._value).setValue(stringValue);
                break;
            }
            case BOOLEAN: {
                final boolean booleanValue = ((BooleanFormulaValue)this._value).getPreEvaluatedValue();
                this._value = new BooleanValue();
                ((BooleanValue)this._value).setValue(booleanValue);
                break;
            }
            case ERROR: {
                final byte errorValue = ((ErrorFormulaValue)this._value).getPreEvaluatedValue();
                this._value = new ErrorValue();
                ((ErrorValue)this._value).setValue(errorValue);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    public String getCellFormula() {
        if (this._value.getType() != CellType.FORMULA) {
            throw typeMismatch(CellType.FORMULA, this._value.getType(), false);
        }
        return ((FormulaValue)this._value).getValue();
    }
    
    public double getNumericCellValue() {
        final CellType cellType = this.getCellType();
        switch (cellType) {
            case BLANK: {
                return 0.0;
            }
            case FORMULA: {
                final FormulaValue fv = (FormulaValue)this._value;
                if (fv.getFormulaType() != CellType.NUMERIC) {
                    throw typeMismatch(CellType.NUMERIC, CellType.FORMULA, false);
                }
                return ((NumericFormulaValue)this._value).getPreEvaluatedValue();
            }
            case NUMERIC: {
                return ((NumericValue)this._value).getValue();
            }
            default: {
                throw typeMismatch(CellType.NUMERIC, cellType, false);
            }
        }
    }
    
    public Date getDateCellValue() {
        final CellType cellType = this.getCellType();
        if (cellType == CellType.BLANK) {
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
    
    public RichTextString getRichStringCellValue() {
        final CellType cellType = this.getCellType();
        if (this.getCellType() != CellType.STRING) {
            throw typeMismatch(CellType.STRING, cellType, false);
        }
        final StringValue sval = (StringValue)this._value;
        if (sval.isRichText()) {
            return ((RichTextValue)this._value).getValue();
        }
        final String plainText = this.getStringCellValue();
        return this.getSheet().getWorkbook().getCreationHelper().createRichTextString(plainText);
    }
    
    public String getStringCellValue() {
        final CellType cellType = this.getCellType();
        switch (cellType) {
            case BLANK: {
                return "";
            }
            case FORMULA: {
                final FormulaValue fv = (FormulaValue)this._value;
                if (fv.getFormulaType() != CellType.STRING) {
                    throw typeMismatch(CellType.STRING, CellType.FORMULA, false);
                }
                if (this._value instanceof RichTextStringFormulaValue) {
                    return ((RichTextStringFormulaValue)this._value).getPreEvaluatedValue().getString();
                }
                return ((StringFormulaValue)this._value).getPreEvaluatedValue();
            }
            case STRING: {
                if (((StringValue)this._value).isRichText()) {
                    return ((RichTextValue)this._value).getValue().getString();
                }
                return ((PlainStringValue)this._value).getValue();
            }
            default: {
                throw typeMismatch(CellType.STRING, cellType, false);
            }
        }
    }
    
    public void setCellValue(final boolean value) {
        this.ensureTypeOrFormulaType(CellType.BOOLEAN);
        if (this._value.getType() == CellType.FORMULA) {
            ((BooleanFormulaValue)this._value).setPreEvaluatedValue(value);
        }
        else {
            ((BooleanValue)this._value).setValue(value);
        }
    }
    
    public void setCellErrorValue(final byte value) {
        if (this._value.getType() == CellType.FORMULA) {
            this._value = new ErrorFormulaValue(this.getCellFormula(), value);
        }
        else {
            this._value = new ErrorValue(value);
        }
    }
    
    public boolean getBooleanCellValue() {
        final CellType cellType = this.getCellType();
        switch (cellType) {
            case BLANK: {
                return false;
            }
            case FORMULA: {
                final FormulaValue fv = (FormulaValue)this._value;
                if (fv.getFormulaType() != CellType.BOOLEAN) {
                    throw typeMismatch(CellType.BOOLEAN, CellType.FORMULA, false);
                }
                return ((BooleanFormulaValue)this._value).getPreEvaluatedValue();
            }
            case BOOLEAN: {
                return ((BooleanValue)this._value).getValue();
            }
            default: {
                throw typeMismatch(CellType.BOOLEAN, cellType, false);
            }
        }
    }
    
    public byte getErrorCellValue() {
        final CellType cellType = this.getCellType();
        switch (cellType) {
            case BLANK: {
                return 0;
            }
            case FORMULA: {
                final FormulaValue fv = (FormulaValue)this._value;
                if (fv.getFormulaType() != CellType.ERROR) {
                    throw typeMismatch(CellType.ERROR, CellType.FORMULA, false);
                }
                return ((ErrorFormulaValue)this._value).getPreEvaluatedValue();
            }
            case ERROR: {
                return ((ErrorValue)this._value).getValue();
            }
            default: {
                throw typeMismatch(CellType.ERROR, cellType, false);
            }
        }
    }
    
    public void setCellStyle(final CellStyle style) {
        this._style = style;
    }
    
    public CellStyle getCellStyle() {
        if (this._style == null) {
            final SXSSFWorkbook wb = (SXSSFWorkbook)this.getRow().getSheet().getWorkbook();
            return wb.getCellStyleAt(0);
        }
        return this._style;
    }
    
    public void setAsActiveCell() {
        this.getSheet().setActiveCell(this.getAddress());
    }
    
    public void setCellComment(final Comment comment) {
        this.setProperty(1, comment);
    }
    
    public Comment getCellComment() {
        return (Comment)this.getPropertyValue(1);
    }
    
    public void removeCellComment() {
        this.removeProperty(1);
    }
    
    public Hyperlink getHyperlink() {
        return (Hyperlink)this.getPropertyValue(2);
    }
    
    public void setHyperlink(final Hyperlink link) {
        if (link == null) {
            this.removeHyperlink();
            return;
        }
        this.setProperty(2, link);
        final XSSFHyperlink xssfobj = (XSSFHyperlink)link;
        final CellReference ref = new CellReference(this.getRowIndex(), this.getColumnIndex());
        xssfobj.setCellReference(ref);
        this.getSheet()._sh.addHyperlink(xssfobj);
    }
    
    public void removeHyperlink() {
        this.removeProperty(2);
        this.getSheet()._sh.removeHyperlink(this.getRowIndex(), this.getColumnIndex());
    }
    
    @NotImplemented
    public CellRangeAddress getArrayFormulaRange() {
        return null;
    }
    
    @NotImplemented
    public boolean isPartOfArrayFormulaGroup() {
        return false;
    }
    
    public String toString() {
        switch (this.getCellType()) {
            case BLANK: {
                return "";
            }
            case BOOLEAN: {
                return this.getBooleanCellValue() ? "TRUE" : "FALSE";
            }
            case ERROR: {
                return ErrorEval.getText((int)this.getErrorCellValue());
            }
            case FORMULA: {
                return this.getCellFormula();
            }
            case NUMERIC: {
                if (DateUtil.isCellDateFormatted((Cell)this)) {
                    final DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", LocaleUtil.getUserLocale());
                    sdf.setTimeZone(LocaleUtil.getUserTimeZone());
                    return sdf.format(this.getDateCellValue());
                }
                return this.getNumericCellValue() + "";
            }
            case STRING: {
                return this.getRichStringCellValue().toString();
            }
            default: {
                return "Unknown Cell Type: " + this.getCellType();
            }
        }
    }
    
    void removeProperty(final int type) {
        Property current = this._firstProperty;
        Property previous = null;
        while (current != null && current.getType() != type) {
            previous = current;
            current = current._next;
        }
        if (current != null) {
            if (previous != null) {
                previous._next = current._next;
            }
            else {
                this._firstProperty = current._next;
            }
        }
    }
    
    void setProperty(final int type, final Object value) {
        Property current = this._firstProperty;
        Property previous = null;
        while (current != null && current.getType() != type) {
            previous = current;
            current = current._next;
        }
        if (current != null) {
            current.setValue(value);
        }
        else {
            switch (type) {
                case 1: {
                    current = new CommentProperty(value);
                    break;
                }
                case 2: {
                    current = new HyperlinkProperty(value);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid type: " + type);
                }
            }
            if (previous != null) {
                previous._next = current;
            }
            else {
                this._firstProperty = current;
            }
        }
    }
    
    Object getPropertyValue(final int type) {
        return this.getPropertyValue(type, null);
    }
    
    Object getPropertyValue(final int type, final String defaultValue) {
        Property current;
        for (current = this._firstProperty; current != null && current.getType() != type; current = current._next) {}
        return (current == null) ? defaultValue : current.getValue();
    }
    
    void ensurePlainStringType() {
        if (this._value.getType() != CellType.STRING || ((StringValue)this._value).isRichText()) {
            this._value = new PlainStringValue();
        }
    }
    
    void ensureRichTextStringType() {
        if (this._value.getType() == CellType.FORMULA) {
            final String formula = ((FormulaValue)this._value).getValue();
            this._value = new RichTextStringFormulaValue(formula, (RichTextString)new XSSFRichTextString(""));
        }
        else if (this._value.getType() != CellType.STRING || !((StringValue)this._value).isRichText()) {
            this._value = new RichTextValue();
        }
    }
    
    void ensureType(final CellType type) {
        if (this._value.getType() != type) {
            this.setType(type);
        }
    }
    
    void ensureTypeOrFormulaType(final CellType type) {
        if (this._value.getType() == type) {
            if (type == CellType.STRING && ((StringValue)this._value).isRichText()) {
                this.setType(CellType.STRING);
            }
            return;
        }
        if (this._value.getType() != CellType.FORMULA) {
            this.setType(type);
            return;
        }
        if (((FormulaValue)this._value).getFormulaType() == type) {
            return;
        }
        switch (type) {
            case BOOLEAN: {
                this._value = new BooleanFormulaValue(this.getCellFormula(), false);
                break;
            }
            case NUMERIC: {
                this._value = new NumericFormulaValue(this.getCellFormula(), 0.0);
                break;
            }
            case STRING: {
                this._value = new StringFormulaValue(this.getCellFormula(), "");
                break;
            }
            case ERROR: {
                this._value = new ErrorFormulaValue(this.getCellFormula(), FormulaError._NO_ERROR.getCode());
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    void setType(final CellType type) {
        switch (type) {
            case NUMERIC: {
                this._value = new NumericValue();
                break;
            }
            case STRING: {
                final PlainStringValue sval = new PlainStringValue();
                if (this._value != null) {
                    final String str = this.convertCellValueToString();
                    sval.setValue(str);
                }
                this._value = sval;
                break;
            }
            case FORMULA: {
                if (this.getCellType() == CellType.BLANK) {
                    this._value = new NumericFormulaValue("", 0.0);
                    break;
                }
                break;
            }
            case BLANK: {
                this._value = new BlankValue();
                break;
            }
            case BOOLEAN: {
                final BooleanValue bval = new BooleanValue();
                if (this._value != null) {
                    final boolean val = this.convertCellValueToBoolean();
                    bval.setValue(val);
                }
                this._value = bval;
                break;
            }
            case ERROR: {
                this._value = new ErrorValue();
                break;
            }
            default: {
                throw new IllegalArgumentException("Illegal type " + type);
            }
        }
    }
    
    private static RuntimeException typeMismatch(final CellType expectedTypeCode, final CellType actualTypeCode, final boolean isFormulaCell) {
        final String msg = "Cannot get a " + expectedTypeCode + " value from a " + actualTypeCode + " " + (isFormulaCell ? "formula " : "") + "cell";
        return new IllegalStateException(msg);
    }
    
    private boolean convertCellValueToBoolean() {
        CellType cellType = this.getCellType();
        if (cellType == CellType.FORMULA) {
            cellType = this.getCachedFormulaResultType();
        }
        switch (cellType) {
            case BOOLEAN: {
                return this.getBooleanCellValue();
            }
            case STRING: {
                final String text = this.getStringCellValue();
                return Boolean.parseBoolean(text);
            }
            case NUMERIC: {
                return this.getNumericCellValue() != 0.0;
            }
            case ERROR:
            case BLANK: {
                return false;
            }
            default: {
                throw new RuntimeException("Unexpected cell type (" + cellType + ")");
            }
        }
    }
    
    private String convertCellValueToString() {
        final CellType cellType = this.getCellType();
        return this.convertCellValueToString(cellType);
    }
    
    private String convertCellValueToString(final CellType cellType) {
        switch (cellType) {
            case BLANK: {
                return "";
            }
            case BOOLEAN: {
                return this.getBooleanCellValue() ? "TRUE" : "FALSE";
            }
            case STRING: {
                return this.getStringCellValue();
            }
            case NUMERIC: {
                return Double.toString(this.getNumericCellValue());
            }
            case ERROR: {
                final byte errVal = this.getErrorCellValue();
                return FormulaError.forInt(errVal).getString();
            }
            case FORMULA: {
                if (this._value != null) {
                    final FormulaValue fv = (FormulaValue)this._value;
                    if (fv.getFormulaType() != CellType.FORMULA) {
                        return this.convertCellValueToString(fv.getFormulaType());
                    }
                }
                return "";
            }
            default: {
                throw new IllegalStateException("Unexpected cell type (" + cellType + ")");
            }
        }
    }
    
    abstract static class Property
    {
        static final int COMMENT = 1;
        static final int HYPERLINK = 2;
        Object _value;
        Property _next;
        
        public Property(final Object value) {
            this._value = value;
        }
        
        abstract int getType();
        
        void setValue(final Object value) {
            this._value = value;
        }
        
        Object getValue() {
            return this._value;
        }
    }
    
    static class CommentProperty extends Property
    {
        public CommentProperty(final Object value) {
            super(value);
        }
        
        public int getType() {
            return 1;
        }
    }
    
    static class HyperlinkProperty extends Property
    {
        public HyperlinkProperty(final Object value) {
            super(value);
        }
        
        public int getType() {
            return 2;
        }
    }
    
    static class NumericValue implements Value
    {
        double _value;
        
        public NumericValue() {
            this._value = 0.0;
        }
        
        public NumericValue(final double _value) {
            this._value = _value;
        }
        
        @Override
        public CellType getType() {
            return CellType.NUMERIC;
        }
        
        void setValue(final double value) {
            this._value = value;
        }
        
        double getValue() {
            return this._value;
        }
    }
    
    abstract static class StringValue implements Value
    {
        @Override
        public CellType getType() {
            return CellType.STRING;
        }
        
        abstract boolean isRichText();
    }
    
    static class PlainStringValue extends StringValue
    {
        String _value;
        
        void setValue(final String value) {
            this._value = value;
        }
        
        String getValue() {
            return this._value;
        }
        
        @Override
        boolean isRichText() {
            return false;
        }
    }
    
    static class RichTextValue extends StringValue
    {
        RichTextString _value;
        
        @Override
        public CellType getType() {
            return CellType.STRING;
        }
        
        void setValue(final RichTextString value) {
            this._value = value;
        }
        
        RichTextString getValue() {
            return this._value;
        }
        
        @Override
        boolean isRichText() {
            return true;
        }
    }
    
    abstract static class FormulaValue implements Value
    {
        String _value;
        
        public FormulaValue(final String _value) {
            this._value = _value;
        }
        
        @Override
        public CellType getType() {
            return CellType.FORMULA;
        }
        
        void setValue(final String value) {
            this._value = value;
        }
        
        String getValue() {
            return this._value;
        }
        
        abstract CellType getFormulaType();
    }
    
    static class NumericFormulaValue extends FormulaValue
    {
        double _preEvaluatedValue;
        
        public NumericFormulaValue(final String formula, final double _preEvaluatedValue) {
            super(formula);
            this._preEvaluatedValue = _preEvaluatedValue;
        }
        
        @Override
        CellType getFormulaType() {
            return CellType.NUMERIC;
        }
        
        void setPreEvaluatedValue(final double value) {
            this._preEvaluatedValue = value;
        }
        
        double getPreEvaluatedValue() {
            return this._preEvaluatedValue;
        }
    }
    
    static class StringFormulaValue extends FormulaValue
    {
        String _preEvaluatedValue;
        
        public StringFormulaValue(final String formula, final String value) {
            super(formula);
            this._preEvaluatedValue = value;
        }
        
        @Override
        CellType getFormulaType() {
            return CellType.STRING;
        }
        
        void setPreEvaluatedValue(final String value) {
            this._preEvaluatedValue = value;
        }
        
        String getPreEvaluatedValue() {
            return this._preEvaluatedValue;
        }
    }
    
    static class RichTextStringFormulaValue extends FormulaValue
    {
        RichTextString _preEvaluatedValue;
        
        public RichTextStringFormulaValue(final String formula, final RichTextString value) {
            super(formula);
            this._preEvaluatedValue = value;
        }
        
        @Override
        CellType getFormulaType() {
            return CellType.STRING;
        }
        
        void setPreEvaluatedValue(final RichTextString value) {
            this._preEvaluatedValue = value;
        }
        
        RichTextString getPreEvaluatedValue() {
            return this._preEvaluatedValue;
        }
    }
    
    static class BooleanFormulaValue extends FormulaValue
    {
        boolean _preEvaluatedValue;
        
        public BooleanFormulaValue(final String formula, final boolean value) {
            super(formula);
            this._preEvaluatedValue = value;
        }
        
        @Override
        CellType getFormulaType() {
            return CellType.BOOLEAN;
        }
        
        void setPreEvaluatedValue(final boolean value) {
            this._preEvaluatedValue = value;
        }
        
        boolean getPreEvaluatedValue() {
            return this._preEvaluatedValue;
        }
    }
    
    static class ErrorFormulaValue extends FormulaValue
    {
        byte _preEvaluatedValue;
        
        public ErrorFormulaValue(final String formula, final byte value) {
            super(formula);
            this._preEvaluatedValue = value;
        }
        
        @Override
        CellType getFormulaType() {
            return CellType.ERROR;
        }
        
        void setPreEvaluatedValue(final byte value) {
            this._preEvaluatedValue = value;
        }
        
        byte getPreEvaluatedValue() {
            return this._preEvaluatedValue;
        }
    }
    
    static class BlankValue implements Value
    {
        @Override
        public CellType getType() {
            return CellType.BLANK;
        }
    }
    
    static class BooleanValue implements Value
    {
        boolean _value;
        
        public BooleanValue() {
            this._value = false;
        }
        
        public BooleanValue(final boolean _value) {
            this._value = _value;
        }
        
        @Override
        public CellType getType() {
            return CellType.BOOLEAN;
        }
        
        void setValue(final boolean value) {
            this._value = value;
        }
        
        boolean getValue() {
            return this._value;
        }
    }
    
    static class ErrorValue implements Value
    {
        byte _value;
        
        public ErrorValue() {
            this._value = FormulaError._NO_ERROR.getCode();
        }
        
        public ErrorValue(final byte _value) {
            this._value = _value;
        }
        
        @Override
        public CellType getType() {
            return CellType.ERROR;
        }
        
        void setValue(final byte value) {
            this._value = value;
        }
        
        byte getValue() {
            return this._value;
        }
    }
    
    interface Value
    {
        CellType getType();
    }
}
