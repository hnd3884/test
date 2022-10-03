package org.apache.poi.ddf;

import org.apache.poi.common.Duplicatable;

public class EscherTertiaryOptRecord extends AbstractEscherOptRecord
{
    public static final short RECORD_ID;
    
    public EscherTertiaryOptRecord() {
    }
    
    public EscherTertiaryOptRecord(final EscherTertiaryOptRecord other) {
        super(other);
    }
    
    @Override
    public String getRecordName() {
        return EscherRecordTypes.USER_DEFINED.recordName;
    }
    
    @Override
    public Enum getGenericRecordType() {
        return EscherRecordTypes.USER_DEFINED;
    }
    
    @Override
    public EscherTertiaryOptRecord copy() {
        return new EscherTertiaryOptRecord(this);
    }
    
    static {
        RECORD_ID = EscherRecordTypes.USER_DEFINED.typeID;
    }
}
