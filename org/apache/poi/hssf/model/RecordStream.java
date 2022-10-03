package org.apache.poi.hssf.model;

import org.apache.poi.hssf.record.Record;
import java.util.List;

public final class RecordStream
{
    private final List<Record> _list;
    private int _nextIndex;
    private int _countRead;
    private final int _endIx;
    
    public RecordStream(final List<Record> inputList, final int startIndex, final int endIx) {
        this._list = inputList;
        this._nextIndex = startIndex;
        this._endIx = endIx;
        this._countRead = 0;
    }
    
    public RecordStream(final List<Record> records, final int startIx) {
        this(records, startIx, records.size());
    }
    
    public boolean hasNext() {
        return this._nextIndex < this._endIx;
    }
    
    public Record getNext() {
        if (!this.hasNext()) {
            throw new RuntimeException("Attempt to read past end of record stream");
        }
        ++this._countRead;
        return this._list.get(this._nextIndex++);
    }
    
    public Class<? extends Record> peekNextClass() {
        if (!this.hasNext()) {
            return null;
        }
        return this._list.get(this._nextIndex).getClass();
    }
    
    public Record peekNextRecord() {
        return this.hasNext() ? this._list.get(this._nextIndex) : null;
    }
    
    public int peekNextSid() {
        if (!this.hasNext()) {
            return -1;
        }
        return this._list.get(this._nextIndex).getSid();
    }
    
    public int getCountRead() {
        return this._countRead;
    }
}
