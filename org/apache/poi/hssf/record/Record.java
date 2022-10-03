package org.apache.poi.hssf.record;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.apache.poi.common.Duplicatable;

public abstract class Record extends RecordBase implements Duplicatable
{
    protected Record() {
    }
    
    protected Record(final Record other) {
    }
    
    public final byte[] serialize() {
        final byte[] retval = new byte[this.getRecordSize()];
        this.serialize(0, retval);
        return retval;
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
    
    public abstract short getSid();
    
    public Record cloneViaReserialise() {
        final byte[] b = this.serialize();
        final RecordInputStream rinp = new RecordInputStream(new ByteArrayInputStream(b));
        rinp.nextRecord();
        final Record[] r = RecordFactory.createRecord(rinp);
        if (r.length != 1) {
            throw new IllegalStateException("Re-serialised a record to clone it, but got " + r.length + " records back!");
        }
        return r[0];
    }
    
    @Override
    public abstract Record copy();
}
