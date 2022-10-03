package org.apache.poi.hssf.record.cf;

import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.common.Duplicatable;

public final class DataBarThreshold extends Threshold implements Duplicatable
{
    public DataBarThreshold() {
    }
    
    public DataBarThreshold(final DataBarThreshold other) {
        super(other);
    }
    
    public DataBarThreshold(final LittleEndianInput in) {
        super(in);
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public DataBarThreshold clone() {
        return this.copy();
    }
    
    @Override
    public DataBarThreshold copy() {
        return new DataBarThreshold(this);
    }
}
