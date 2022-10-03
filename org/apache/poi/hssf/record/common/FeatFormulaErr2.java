package org.apache.poi.hssf.record.common;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.BitField;

public final class FeatFormulaErr2 implements SharedFeature
{
    private static final BitField CHECK_CALCULATION_ERRORS;
    private static final BitField CHECK_EMPTY_CELL_REF;
    private static final BitField CHECK_NUMBERS_AS_TEXT;
    private static final BitField CHECK_INCONSISTENT_RANGES;
    private static final BitField CHECK_INCONSISTENT_FORMULAS;
    private static final BitField CHECK_DATETIME_FORMATS;
    private static final BitField CHECK_UNPROTECTED_FORMULAS;
    private static final BitField PERFORM_DATA_VALIDATION;
    private int errorCheck;
    
    public FeatFormulaErr2() {
    }
    
    public FeatFormulaErr2(final FeatFormulaErr2 other) {
        this.errorCheck = other.errorCheck;
    }
    
    public FeatFormulaErr2(final RecordInputStream in) {
        this.errorCheck = in.readInt();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(" [FEATURE FORMULA ERRORS]\n");
        buffer.append("  checkCalculationErrors    = ");
        buffer.append("  checkEmptyCellRef         = ");
        buffer.append("  checkNumbersAsText        = ");
        buffer.append("  checkInconsistentRanges   = ");
        buffer.append("  checkInconsistentFormulas = ");
        buffer.append("  checkDateTimeFormats      = ");
        buffer.append("  checkUnprotectedFormulas  = ");
        buffer.append("  performDataValidation     = ");
        buffer.append(" [/FEATURE FORMULA ERRORS]\n");
        return buffer.toString();
    }
    
    @Override
    public void serialize(final LittleEndianOutput out) {
        out.writeInt(this.errorCheck);
    }
    
    @Override
    public int getDataSize() {
        return 4;
    }
    
    public int _getRawErrorCheckValue() {
        return this.errorCheck;
    }
    
    public boolean getCheckCalculationErrors() {
        return FeatFormulaErr2.CHECK_CALCULATION_ERRORS.isSet(this.errorCheck);
    }
    
    public void setCheckCalculationErrors(final boolean checkCalculationErrors) {
        this.errorCheck = FeatFormulaErr2.CHECK_CALCULATION_ERRORS.setBoolean(this.errorCheck, checkCalculationErrors);
    }
    
    public boolean getCheckEmptyCellRef() {
        return FeatFormulaErr2.CHECK_EMPTY_CELL_REF.isSet(this.errorCheck);
    }
    
    public void setCheckEmptyCellRef(final boolean checkEmptyCellRef) {
        this.errorCheck = FeatFormulaErr2.CHECK_EMPTY_CELL_REF.setBoolean(this.errorCheck, checkEmptyCellRef);
    }
    
    public boolean getCheckNumbersAsText() {
        return FeatFormulaErr2.CHECK_NUMBERS_AS_TEXT.isSet(this.errorCheck);
    }
    
    public void setCheckNumbersAsText(final boolean checkNumbersAsText) {
        this.errorCheck = FeatFormulaErr2.CHECK_NUMBERS_AS_TEXT.setBoolean(this.errorCheck, checkNumbersAsText);
    }
    
    public boolean getCheckInconsistentRanges() {
        return FeatFormulaErr2.CHECK_INCONSISTENT_RANGES.isSet(this.errorCheck);
    }
    
    public void setCheckInconsistentRanges(final boolean checkInconsistentRanges) {
        this.errorCheck = FeatFormulaErr2.CHECK_INCONSISTENT_RANGES.setBoolean(this.errorCheck, checkInconsistentRanges);
    }
    
    public boolean getCheckInconsistentFormulas() {
        return FeatFormulaErr2.CHECK_INCONSISTENT_FORMULAS.isSet(this.errorCheck);
    }
    
    public void setCheckInconsistentFormulas(final boolean checkInconsistentFormulas) {
        this.errorCheck = FeatFormulaErr2.CHECK_INCONSISTENT_FORMULAS.setBoolean(this.errorCheck, checkInconsistentFormulas);
    }
    
    public boolean getCheckDateTimeFormats() {
        return FeatFormulaErr2.CHECK_DATETIME_FORMATS.isSet(this.errorCheck);
    }
    
    public void setCheckDateTimeFormats(final boolean checkDateTimeFormats) {
        this.errorCheck = FeatFormulaErr2.CHECK_DATETIME_FORMATS.setBoolean(this.errorCheck, checkDateTimeFormats);
    }
    
    public boolean getCheckUnprotectedFormulas() {
        return FeatFormulaErr2.CHECK_UNPROTECTED_FORMULAS.isSet(this.errorCheck);
    }
    
    public void setCheckUnprotectedFormulas(final boolean checkUnprotectedFormulas) {
        this.errorCheck = FeatFormulaErr2.CHECK_UNPROTECTED_FORMULAS.setBoolean(this.errorCheck, checkUnprotectedFormulas);
    }
    
    public boolean getPerformDataValidation() {
        return FeatFormulaErr2.PERFORM_DATA_VALIDATION.isSet(this.errorCheck);
    }
    
    public void setPerformDataValidation(final boolean performDataValidation) {
        this.errorCheck = FeatFormulaErr2.PERFORM_DATA_VALIDATION.setBoolean(this.errorCheck, performDataValidation);
    }
    
    @Override
    public FeatFormulaErr2 copy() {
        return new FeatFormulaErr2(this);
    }
    
    static {
        CHECK_CALCULATION_ERRORS = BitFieldFactory.getInstance(1);
        CHECK_EMPTY_CELL_REF = BitFieldFactory.getInstance(2);
        CHECK_NUMBERS_AS_TEXT = BitFieldFactory.getInstance(4);
        CHECK_INCONSISTENT_RANGES = BitFieldFactory.getInstance(8);
        CHECK_INCONSISTENT_FORMULAS = BitFieldFactory.getInstance(16);
        CHECK_DATETIME_FORMATS = BitFieldFactory.getInstance(32);
        CHECK_UNPROTECTED_FORMULAS = BitFieldFactory.getInstance(64);
        PERFORM_DATA_VALIDATION = BitFieldFactory.getInstance(128);
    }
}
