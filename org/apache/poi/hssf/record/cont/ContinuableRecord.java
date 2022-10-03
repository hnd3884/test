package org.apache.poi.hssf.record.cont;

import java.io.IOException;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.hssf.record.Record;

public abstract class ContinuableRecord extends Record
{
    protected ContinuableRecord() {
    }
    
    protected ContinuableRecord(final ContinuableRecord other) {
        super(other);
    }
    
    protected abstract void serialize(final ContinuableRecordOutput p0);
    
    @Override
    public final int getRecordSize() {
        final ContinuableRecordOutput out = ContinuableRecordOutput.createForCountingOnly();
        this.serialize(out);
        out.terminate();
        return out.getTotalSize();
    }
    
    @Override
    public final int serialize(final int offset, final byte[] data) {
        int totalSize = 0;
        try (final LittleEndianByteArrayOutputStream leo = new LittleEndianByteArrayOutputStream(data, offset)) {
            final ContinuableRecordOutput out = new ContinuableRecordOutput(leo, this.getSid());
            this.serialize(out);
            out.terminate();
            totalSize = out.getTotalSize();
        }
        catch (final IOException ioe) {
            throw new IllegalStateException(ioe);
        }
        return totalSize;
    }
}
