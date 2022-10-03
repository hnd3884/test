package org.apache.poi.hssf.record.aggregates;

import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordBase;

public abstract class RecordAggregate extends RecordBase
{
    public abstract void visitContainedRecords(final RecordVisitor p0);
    
    @Override
    public final int serialize(final int offset, final byte[] data) {
        final SerializingRecordVisitor srv = new SerializingRecordVisitor(data, offset);
        this.visitContainedRecords(srv);
        return srv.countBytesWritten();
    }
    
    @Override
    public int getRecordSize() {
        final RecordSizingVisitor rsv = new RecordSizingVisitor();
        this.visitContainedRecords(rsv);
        return rsv.getTotalSize();
    }
    
    private static final class SerializingRecordVisitor implements RecordVisitor
    {
        private final byte[] _data;
        private final int _startOffset;
        private int _countBytesWritten;
        
        public SerializingRecordVisitor(final byte[] data, final int startOffset) {
            this._data = data;
            this._startOffset = startOffset;
            this._countBytesWritten = 0;
        }
        
        public int countBytesWritten() {
            return this._countBytesWritten;
        }
        
        @Override
        public void visitRecord(final Record r) {
            final int currentOffset = this._startOffset + this._countBytesWritten;
            this._countBytesWritten += r.serialize(currentOffset, this._data);
        }
    }
    
    private static final class RecordSizingVisitor implements RecordVisitor
    {
        private int _totalSize;
        
        public RecordSizingVisitor() {
            this._totalSize = 0;
        }
        
        public int getTotalSize() {
            return this._totalSize;
        }
        
        @Override
        public void visitRecord(final Record r) {
            this._totalSize += r.getRecordSize();
        }
    }
    
    public static final class PositionTrackingVisitor implements RecordVisitor
    {
        private final RecordVisitor _rv;
        private int _position;
        
        public PositionTrackingVisitor(final RecordVisitor rv, final int initialPosition) {
            this._rv = rv;
            this._position = initialPosition;
        }
        
        @Override
        public void visitRecord(final Record r) {
            this._position += r.getRecordSize();
            this._rv.visitRecord(r);
        }
        
        public void setPosition(final int position) {
            this._position = position;
        }
        
        public int getPosition() {
            return this._position;
        }
    }
    
    public interface RecordVisitor
    {
        void visitRecord(final Record p0);
    }
}
