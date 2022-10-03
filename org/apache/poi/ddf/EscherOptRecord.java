package org.apache.poi.ddf;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Internal;

public class EscherOptRecord extends AbstractEscherOptRecord
{
    public static final short RECORD_ID;
    public static final String RECORD_DESCRIPTION;
    
    public EscherOptRecord() {
    }
    
    public EscherOptRecord(final EscherOptRecord other) {
        super(other);
    }
    
    @Override
    public short getInstance() {
        this.setInstance((short)this.getEscherProperties().size());
        return super.getInstance();
    }
    
    @Internal
    @Override
    public short getOptions() {
        this.getInstance();
        this.getVersion();
        return super.getOptions();
    }
    
    @Override
    public String getRecordName() {
        return EscherRecordTypes.OPT.recordName;
    }
    
    @Override
    public short getVersion() {
        this.setVersion((short)3);
        return super.getVersion();
    }
    
    @Override
    public void setVersion(final short value) {
        if (value != 3) {
            throw new IllegalArgumentException(EscherOptRecord.RECORD_DESCRIPTION + " can have only '0x3' version");
        }
        super.setVersion(value);
    }
    
    @Override
    public Enum getGenericRecordType() {
        return EscherRecordTypes.OPT;
    }
    
    @Override
    public EscherOptRecord copy() {
        return new EscherOptRecord(this);
    }
    
    static {
        RECORD_ID = EscherRecordTypes.OPT.typeID;
        RECORD_DESCRIPTION = EscherRecordTypes.OPT.description;
    }
}
