package org.apache.poi.hssf.record.aggregates;

import org.apache.poi.hssf.record.HeaderFooterRecord;
import java.util.ArrayList;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.RecordBase;
import java.util.List;
import org.apache.poi.hssf.record.Record;

public final class CustomViewSettingsRecordAggregate extends RecordAggregate
{
    private final Record _begin;
    private final Record _end;
    private final List<RecordBase> _recs;
    private PageSettingsBlock _psBlock;
    
    public CustomViewSettingsRecordAggregate(final RecordStream rs) {
        this._begin = rs.getNext();
        if (this._begin.getSid() != 426) {
            throw new IllegalStateException("Bad begin record");
        }
        final List<RecordBase> temp = new ArrayList<RecordBase>();
        while (rs.peekNextSid() != 427) {
            if (PageSettingsBlock.isComponentRecord(rs.peekNextSid())) {
                if (this._psBlock != null) {
                    if (rs.peekNextSid() != 2204) {
                        throw new IllegalStateException("Found more than one PageSettingsBlock in chart sub-stream, had sid: " + rs.peekNextSid());
                    }
                    this._psBlock.addLateHeaderFooter((HeaderFooterRecord)rs.getNext());
                }
                else {
                    temp.add(this._psBlock = new PageSettingsBlock(rs));
                }
            }
            else {
                temp.add(rs.getNext());
            }
        }
        this._recs = temp;
        this._end = rs.getNext();
        if (this._end.getSid() != 427) {
            throw new IllegalStateException("Bad custom view settings end record");
        }
    }
    
    @Override
    public void visitContainedRecords(final RecordVisitor rv) {
        if (this._recs.isEmpty()) {
            return;
        }
        rv.visitRecord(this._begin);
        for (int i = 0; i < this._recs.size(); ++i) {
            final RecordBase rb = this._recs.get(i);
            if (rb instanceof RecordAggregate) {
                ((RecordAggregate)rb).visitContainedRecords(rv);
            }
            else {
                rv.visitRecord((Record)rb);
            }
        }
        rv.visitRecord(this._end);
    }
    
    public static boolean isBeginRecord(final int sid) {
        return sid == 426;
    }
    
    public void append(final RecordBase r) {
        this._recs.add(r);
    }
}
