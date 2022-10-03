package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.util.CellRangeUtil;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellRangeAddress;

public abstract class CFHeaderBase extends StandardRecord
{
    private int field_1_numcf;
    private int field_2_need_recalculation_and_id;
    private CellRangeAddress field_3_enclosing_cell_range;
    private CellRangeAddressList field_4_cell_ranges;
    
    protected CFHeaderBase() {
    }
    
    protected CFHeaderBase(final CFHeaderBase other) {
        super(other);
        this.field_1_numcf = other.field_1_numcf;
        this.field_2_need_recalculation_and_id = other.field_2_need_recalculation_and_id;
        this.field_3_enclosing_cell_range = other.field_3_enclosing_cell_range.copy();
        this.field_4_cell_ranges = other.field_4_cell_ranges.copy();
    }
    
    protected CFHeaderBase(final CellRangeAddress[] regions, final int nRules) {
        final CellRangeAddress[] mergeCellRanges = CellRangeUtil.mergeCellRanges(regions);
        this.setCellRanges(mergeCellRanges);
        this.field_1_numcf = nRules;
    }
    
    protected void createEmpty() {
        this.field_3_enclosing_cell_range = new CellRangeAddress(0, 0, 0, 0);
        this.field_4_cell_ranges = new CellRangeAddressList();
    }
    
    protected void read(final RecordInputStream in) {
        this.field_1_numcf = in.readShort();
        this.field_2_need_recalculation_and_id = in.readShort();
        this.field_3_enclosing_cell_range = new CellRangeAddress(in);
        this.field_4_cell_ranges = new CellRangeAddressList(in);
    }
    
    public int getNumberOfConditionalFormats() {
        return this.field_1_numcf;
    }
    
    public void setNumberOfConditionalFormats(final int n) {
        this.field_1_numcf = n;
    }
    
    public boolean getNeedRecalculation() {
        return (this.field_2_need_recalculation_and_id & 0x1) == 0x1;
    }
    
    public void setNeedRecalculation(final boolean b) {
        if (b == this.getNeedRecalculation()) {
            return;
        }
        if (b) {
            ++this.field_2_need_recalculation_and_id;
        }
        else {
            --this.field_2_need_recalculation_and_id;
        }
    }
    
    public int getID() {
        return this.field_2_need_recalculation_and_id >> 1;
    }
    
    public void setID(final int id) {
        final boolean needsRecalc = this.getNeedRecalculation();
        this.field_2_need_recalculation_and_id = id << 1;
        if (needsRecalc) {
            ++this.field_2_need_recalculation_and_id;
        }
    }
    
    public CellRangeAddress getEnclosingCellRange() {
        return this.field_3_enclosing_cell_range;
    }
    
    public void setEnclosingCellRange(final CellRangeAddress cr) {
        this.field_3_enclosing_cell_range = cr;
    }
    
    public void setCellRanges(final CellRangeAddress[] cellRanges) {
        if (cellRanges == null) {
            throw new IllegalArgumentException("cellRanges must not be null");
        }
        final CellRangeAddressList cral = new CellRangeAddressList();
        CellRangeAddress enclosingRange = null;
        for (final CellRangeAddress cr : cellRanges) {
            enclosingRange = CellRangeUtil.createEnclosingCellRange(cr, enclosingRange);
            cral.addCellRangeAddress(cr);
        }
        this.field_3_enclosing_cell_range = enclosingRange;
        this.field_4_cell_ranges = cral;
    }
    
    public CellRangeAddress[] getCellRanges() {
        return this.field_4_cell_ranges.getCellRangeAddresses();
    }
    
    protected abstract String getRecordName();
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[").append(this.getRecordName()).append("]\n");
        buffer.append("\t.numCF             = ").append(this.getNumberOfConditionalFormats()).append("\n");
        buffer.append("\t.needRecalc        = ").append(this.getNeedRecalculation()).append("\n");
        buffer.append("\t.id                = ").append(this.getID()).append("\n");
        buffer.append("\t.enclosingCellRange= ").append(this.getEnclosingCellRange()).append("\n");
        buffer.append("\t.cfranges=[");
        for (int i = 0; i < this.field_4_cell_ranges.countRanges(); ++i) {
            buffer.append((i == 0) ? "" : ",").append(this.field_4_cell_ranges.getCellRangeAddress(i));
        }
        buffer.append("]\n");
        buffer.append("[/").append(this.getRecordName()).append("]\n");
        return buffer.toString();
    }
    
    @Override
    protected int getDataSize() {
        return 12 + this.field_4_cell_ranges.getSize();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.field_1_numcf);
        out.writeShort(this.field_2_need_recalculation_and_id);
        this.field_3_enclosing_cell_range.serialize(out);
        this.field_4_cell_ranges.serialize(out);
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public abstract CFHeaderBase clone();
    
    @Override
    public abstract CFHeaderBase copy();
}
