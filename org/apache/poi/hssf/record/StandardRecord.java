package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import java.io.IOException;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;

public abstract class StandardRecord extends Record
{
    protected abstract int getDataSize();
    
    protected StandardRecord() {
    }
    
    protected StandardRecord(final StandardRecord other) {
    }
    
    @Override
    public final int getRecordSize() {
        return 4 + this.getDataSize();
    }
    
    @Override
    public final int serialize(final int offset, final byte[] data) {
        final int dataSize = this.getDataSize();
        final int recSize = 4 + dataSize;
        try (final LittleEndianByteArrayOutputStream out = new LittleEndianByteArrayOutputStream(data, offset, recSize)) {
            out.writeShort(this.getSid());
            out.writeShort(dataSize);
            this.serialize(out);
            if (out.getWriteIndex() - offset != recSize) {
                throw new IllegalStateException("Error in serialization of (" + this.getClass().getName() + "): Incorrect number of bytes written - expected " + recSize + " but got " + (out.getWriteIndex() - offset));
            }
        }
        catch (final IOException ioe) {
            throw new IllegalStateException(ioe);
        }
        return recSize;
    }
    
    protected abstract void serialize(final LittleEndianOutput p0);
    
    @Override
    public abstract StandardRecord copy();
}
