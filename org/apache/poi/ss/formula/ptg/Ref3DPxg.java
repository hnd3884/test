package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.formula.SheetRangeAndWorkbookIndexFormatter;
import org.apache.poi.ss.formula.SheetRangeIdentifier;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.SheetIdentifier;

public final class Ref3DPxg extends RefPtgBase implements Pxg3D
{
    private int externalWorkbookNumber;
    private String firstSheetName;
    private String lastSheetName;
    
    public Ref3DPxg(final Ref3DPxg other) {
        super(other);
        this.externalWorkbookNumber = -1;
        this.externalWorkbookNumber = other.externalWorkbookNumber;
        this.firstSheetName = other.firstSheetName;
        this.lastSheetName = other.lastSheetName;
    }
    
    public Ref3DPxg(final int externalWorkbookNumber, final SheetIdentifier sheetName, final String cellref) {
        this(externalWorkbookNumber, sheetName, new CellReference(cellref));
    }
    
    public Ref3DPxg(final int externalWorkbookNumber, final SheetIdentifier sheetName, final CellReference c) {
        super(c);
        this.externalWorkbookNumber = -1;
        this.externalWorkbookNumber = externalWorkbookNumber;
        this.firstSheetName = sheetName.getSheetIdentifier().getName();
        if (sheetName instanceof SheetRangeIdentifier) {
            this.lastSheetName = ((SheetRangeIdentifier)sheetName).getLastSheetIdentifier().getName();
        }
        else {
            this.lastSheetName = null;
        }
    }
    
    public Ref3DPxg(final SheetIdentifier sheetName, final String cellref) {
        this(sheetName, new CellReference(cellref));
    }
    
    public Ref3DPxg(final SheetIdentifier sheetName, final CellReference c) {
        this(-1, sheetName, c);
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
        sb.append("sheet=").append(this.firstSheetName);
        if (this.lastSheetName != null) {
            sb.append(" : ");
            sb.append("sheet=").append(this.lastSheetName);
        }
        sb.append(" ! ");
        sb.append(this.formatReferenceAsString());
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public int getExternalWorkbookNumber() {
        return this.externalWorkbookNumber;
    }
    
    @Override
    public String getSheetName() {
        return this.firstSheetName;
    }
    
    @Override
    public String getLastSheetName() {
        return this.lastSheetName;
    }
    
    @Override
    public void setSheetName(final String sheetName) {
        this.firstSheetName = sheetName;
    }
    
    @Override
    public void setLastSheetName(final String sheetName) {
        this.lastSheetName = sheetName;
    }
    
    public String format2DRefAsString() {
        return this.formatReferenceAsString();
    }
    
    @Override
    public String toFormulaString() {
        final StringBuilder sb = new StringBuilder(64);
        SheetRangeAndWorkbookIndexFormatter.format(sb, this.externalWorkbookNumber, this.firstSheetName, this.lastSheetName);
        sb.append('!');
        sb.append(this.formatReferenceAsString());
        return sb.toString();
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
    public Ref3DPxg copy() {
        return new Ref3DPxg(this);
    }
}
