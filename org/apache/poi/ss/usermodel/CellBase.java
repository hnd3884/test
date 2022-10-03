package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.SpreadsheetVersion;
import java.util.Locale;
import java.util.Calendar;
import java.time.LocalDateTime;
import java.util.Date;
import org.apache.poi.util.Removal;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellAddress;

public abstract class CellBase implements Cell
{
    @Override
    public final void setCellType(final CellType cellType) {
        if (cellType == null || cellType == CellType._NONE) {
            throw new IllegalArgumentException("cellType shall not be null nor _NONE");
        }
        if (cellType != CellType.FORMULA) {
            this.tryToDeleteArrayFormulaIfSet();
            this.setCellTypeImpl(cellType);
            return;
        }
        if (this.getCellType() != CellType.FORMULA) {
            throw new IllegalArgumentException("Calling Cell.setCellType(CellType.FORMULA) is illegal. Use setCellFormula(String) directly.");
        }
    }
    
    @Override
    public void setBlank() {
        this.setCellType(CellType.BLANK);
    }
    
    @Override
    public CellAddress getAddress() {
        return new CellAddress(this);
    }
    
    protected abstract void setCellTypeImpl(final CellType p0);
    
    public final void tryToDeleteArrayFormula(String message) {
        assert this.isPartOfArrayFormulaGroup();
        final CellRangeAddress arrayFormulaRange = this.getArrayFormulaRange();
        if (arrayFormulaRange.getNumberOfCells() > 1) {
            if (message == null) {
                message = "Cell " + new CellReference(this).formatAsString() + " is part of a multi-cell array formula. You cannot change part of an array.";
            }
            throw new IllegalStateException(message);
        }
        this.getRow().getSheet().removeArrayFormula(this);
    }
    
    @Override
    public final void setCellFormula(final String formula) throws FormulaParseException, IllegalStateException {
        this.tryToDeleteArrayFormulaIfSet();
        if (formula == null) {
            this.removeFormula();
            return;
        }
        if (this.getValueType() == CellType.BLANK) {
            this.setCellValue(0.0);
        }
        this.setCellFormulaImpl(formula);
    }
    
    @Deprecated
    @Removal(version = "4.2")
    @Override
    public final CellType getCellTypeEnum() {
        return this.getCellType();
    }
    
    protected abstract void setCellFormulaImpl(final String p0);
    
    protected final CellType getValueType() {
        final CellType type = this.getCellType();
        if (type != CellType.FORMULA) {
            return type;
        }
        return this.getCachedFormulaResultType();
    }
    
    @Override
    public final void removeFormula() {
        if (this.getCellType() == CellType.BLANK) {
            return;
        }
        if (this.isPartOfArrayFormulaGroup()) {
            this.tryToDeleteArrayFormula(null);
            return;
        }
        this.removeFormulaImpl();
    }
    
    protected abstract void removeFormulaImpl();
    
    private void tryToDeleteArrayFormulaIfSet() {
        if (this.isPartOfArrayFormulaGroup()) {
            this.tryToDeleteArrayFormula(null);
        }
    }
    
    @Override
    public void setCellValue(final double value) {
        if (Double.isInfinite(value)) {
            this.setCellErrorValue(FormulaError.DIV0.getCode());
        }
        else if (Double.isNaN(value)) {
            this.setCellErrorValue(FormulaError.NUM.getCode());
        }
        else {
            this.setCellValueImpl(value);
        }
    }
    
    protected abstract void setCellValueImpl(final double p0);
    
    @Override
    public void setCellValue(final Date value) {
        if (value == null) {
            this.setBlank();
            return;
        }
        this.setCellValueImpl(value);
    }
    
    @Override
    public void setCellValue(final LocalDateTime value) {
        if (value == null) {
            this.setBlank();
            return;
        }
        this.setCellValueImpl(value);
    }
    
    protected abstract void setCellValueImpl(final Date p0);
    
    protected abstract void setCellValueImpl(final LocalDateTime p0);
    
    @Override
    public void setCellValue(final Calendar value) {
        if (value == null) {
            this.setBlank();
            return;
        }
        this.setCellValueImpl(value);
    }
    
    protected abstract void setCellValueImpl(final Calendar p0);
    
    @Override
    public void setCellValue(final String value) {
        if (value == null) {
            this.setBlank();
            return;
        }
        this.checkLength(value);
        this.setCellValueImpl(value);
    }
    
    protected abstract void setCellValueImpl(final String p0);
    
    private void checkLength(final String value) {
        if (value.length() > this.getSpreadsheetVersion().getMaxTextLength()) {
            final String message = String.format(Locale.ROOT, "The maximum length of cell contents (text) is %d characters", this.getSpreadsheetVersion().getMaxTextLength());
            throw new IllegalArgumentException(message);
        }
    }
    
    @Override
    public void setCellValue(final RichTextString value) {
        if (value == null || value.getString() == null) {
            this.setBlank();
            return;
        }
        this.checkLength(value.getString());
        this.setCellValueImpl(value);
    }
    
    protected abstract void setCellValueImpl(final RichTextString p0);
    
    protected abstract SpreadsheetVersion getSpreadsheetVersion();
}
