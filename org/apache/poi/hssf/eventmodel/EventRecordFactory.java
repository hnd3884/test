package org.apache.poi.hssf.eventmodel;

import org.apache.poi.util.RecordFormatException;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.hssf.record.RecordInputStream;
import java.io.InputStream;
import org.apache.poi.hssf.record.Record;
import java.util.Arrays;

public final class EventRecordFactory
{
    private final ERFListener _listener;
    private final short[] _sids;
    
    public EventRecordFactory(final ERFListener listener, final short[] sids) {
        this._listener = listener;
        if (sids == null) {
            this._sids = null;
        }
        else {
            Arrays.sort(this._sids = sids.clone());
        }
    }
    
    private boolean isSidIncluded(final short sid) {
        return this._sids == null || Arrays.binarySearch(this._sids, sid) >= 0;
    }
    
    private boolean processRecord(final Record record) {
        return !this.isSidIncluded(record.getSid()) || this._listener.processRecord(record);
    }
    
    public void processRecords(final InputStream in) throws RecordFormatException {
        Record last_record = null;
        final RecordInputStream recStream = new RecordInputStream(in);
        while (recStream.hasNextRecord()) {
            recStream.nextRecord();
            final Record[] recs = RecordFactory.createRecord(recStream);
            if (recs.length > 1) {
                for (final Record rec : recs) {
                    if (last_record != null && !this.processRecord(last_record)) {
                        return;
                    }
                    last_record = rec;
                }
            }
            else {
                final Record record = recs[0];
                if (record == null) {
                    continue;
                }
                if (last_record != null && !this.processRecord(last_record)) {
                    return;
                }
                last_record = record;
            }
        }
        if (last_record != null) {
            this.processRecord(last_record);
        }
    }
}
