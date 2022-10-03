package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.formula.SheetNameFormatter;

public final class NameXPxg extends OperandPtg implements Pxg
{
    private int externalWorkbookNumber;
    private String sheetName;
    private String nameName;
    
    public NameXPxg(final int externalWorkbookNumber, final String sheetName, final String nameName) {
        this.externalWorkbookNumber = -1;
        this.externalWorkbookNumber = externalWorkbookNumber;
        this.sheetName = sheetName;
        this.nameName = nameName;
    }
    
    public NameXPxg(final NameXPxg other) {
        super(other);
        this.externalWorkbookNumber = -1;
        this.externalWorkbookNumber = other.externalWorkbookNumber;
        this.sheetName = other.sheetName;
        this.nameName = other.nameName;
    }
    
    public NameXPxg(final String sheetName, final String nameName) {
        this(-1, sheetName, nameName);
    }
    
    public NameXPxg(final String nameName) {
        this(-1, null, nameName);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append(" [");
        if (this.externalWorkbookNumber >= 0) {
            sb.append(" [");
            sb.append("workbook=").append(this.getExternalWorkbookNumber());
            sb.append("] ");
        }
        sb.append("sheet=").append(this.getSheetName());
        sb.append(" ! ");
        sb.append("name=");
        sb.append(this.nameName);
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public int getExternalWorkbookNumber() {
        return this.externalWorkbookNumber;
    }
    
    @Override
    public String getSheetName() {
        return this.sheetName;
    }
    
    public String getNameName() {
        return this.nameName;
    }
    
    @Override
    public void setSheetName(final String sheetName) {
        this.sheetName = sheetName;
    }
    
    @Override
    public String toFormulaString() {
        final StringBuilder sb = new StringBuilder(64);
        boolean needsExclamation = false;
        if (this.externalWorkbookNumber >= 0) {
            sb.append('[');
            sb.append(this.externalWorkbookNumber);
            sb.append(']');
            needsExclamation = true;
        }
        if (this.sheetName != null) {
            SheetNameFormatter.appendFormat(sb, this.sheetName);
            needsExclamation = true;
        }
        if (needsExclamation) {
            sb.append('!');
        }
        sb.append(this.nameName);
        return sb.toString();
    }
    
    @Override
    public byte getDefaultOperandClass() {
        return 32;
    }
    
    @Override
    public int getSize() {
        return 1;
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        throw new IllegalStateException("XSSF-only Ptg, should not be serialised");
    }
    
    @Override
    public NameXPxg copy() {
        return new NameXPxg(this);
    }
}
