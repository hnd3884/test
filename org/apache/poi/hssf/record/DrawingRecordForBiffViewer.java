package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

public final class DrawingRecordForBiffViewer extends AbstractEscherHolderRecord
{
    public static final short sid = 236;
    
    public DrawingRecordForBiffViewer() {
    }
    
    public DrawingRecordForBiffViewer(final DrawingRecordForBiffViewer other) {
        super(other);
    }
    
    public DrawingRecordForBiffViewer(final RecordInputStream in) {
        super(in);
    }
    
    public DrawingRecordForBiffViewer(final DrawingRecord r) {
        super(convertToInputStream(r));
        this.convertRawBytesToEscherRecords();
    }
    
    private static RecordInputStream convertToInputStream(final DrawingRecord r) {
        final byte[] data = r.serialize();
        final RecordInputStream rinp = new RecordInputStream(new ByteArrayInputStream(data));
        rinp.nextRecord();
        return rinp;
    }
    
    @Override
    protected String getRecordName() {
        return "MSODRAWING";
    }
    
    @Override
    public short getSid() {
        return 236;
    }
    
    @Override
    public DrawingRecordForBiffViewer copy() {
        return new DrawingRecordForBiffViewer(this);
    }
}
