package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;

public final class InterfaceEndRecord extends StandardRecord
{
    public static final short sid = 226;
    public static final InterfaceEndRecord instance;
    
    private InterfaceEndRecord() {
    }
    
    public static Record create(final RecordInputStream in) {
        switch (in.remaining()) {
            case 0: {
                return InterfaceEndRecord.instance;
            }
            case 2: {
                return new InterfaceHdrRecord(in);
            }
            default: {
                throw new RecordFormatException("Invalid record data size: " + in.remaining());
            }
        }
    }
    
    @Override
    public String toString() {
        return "[INTERFACEEND/]\n";
    }
    
    public void serialize(final LittleEndianOutput out) {
    }
    
    @Override
    protected int getDataSize() {
        return 0;
    }
    
    @Override
    public short getSid() {
        return 226;
    }
    
    @Override
    public InterfaceEndRecord copy() {
        return InterfaceEndRecord.instance;
    }
    
    static {
        instance = new InterfaceEndRecord();
    }
}
