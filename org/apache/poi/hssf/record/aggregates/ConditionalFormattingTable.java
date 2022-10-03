package org.apache.poi.hssf.record.aggregates;

import org.apache.poi.ss.formula.FormulaShifter;
import java.util.Iterator;
import org.apache.poi.hssf.record.CFHeaderBase;
import org.apache.poi.hssf.model.RecordStream;
import java.util.ArrayList;
import java.util.List;

public final class ConditionalFormattingTable extends RecordAggregate
{
    private final List<CFRecordsAggregate> _cfHeaders;
    
    public ConditionalFormattingTable() {
        this._cfHeaders = new ArrayList<CFRecordsAggregate>();
    }
    
    public ConditionalFormattingTable(final RecordStream rs) {
        this._cfHeaders = new ArrayList<CFRecordsAggregate>();
        while (rs.peekNextRecord() instanceof CFHeaderBase) {
            this._cfHeaders.add(CFRecordsAggregate.createCFAggregate(rs));
        }
    }
    
    @Override
    public void visitContainedRecords(final RecordVisitor rv) {
        for (final CFRecordsAggregate subAgg : this._cfHeaders) {
            subAgg.visitContainedRecords(rv);
        }
    }
    
    public int add(final CFRecordsAggregate cfAggregate) {
        cfAggregate.getHeader().setID(this._cfHeaders.size());
        this._cfHeaders.add(cfAggregate);
        return this._cfHeaders.size() - 1;
    }
    
    public int size() {
        return this._cfHeaders.size();
    }
    
    public CFRecordsAggregate get(final int index) {
        this.checkIndex(index);
        return this._cfHeaders.get(index);
    }
    
    public void remove(final int index) {
        this.checkIndex(index);
        this._cfHeaders.remove(index);
    }
    
    private void checkIndex(final int index) {
        if (index < 0 || index >= this._cfHeaders.size()) {
            throw new IllegalArgumentException("Specified CF index " + index + " is outside the allowable range (0.." + (this._cfHeaders.size() - 1) + ")");
        }
    }
    
    public void updateFormulasAfterCellShift(final FormulaShifter shifter, final int externSheetIndex) {
        for (int i = 0; i < this._cfHeaders.size(); ++i) {
            final CFRecordsAggregate subAgg = this._cfHeaders.get(i);
            final boolean shouldKeep = subAgg.updateFormulasAfterCellShift(shifter, externSheetIndex);
            if (!shouldKeep) {
                this._cfHeaders.remove(i);
                --i;
            }
        }
    }
}
