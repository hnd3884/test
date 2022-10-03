package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.BitField;

public final class ProtectRecord extends StandardRecord
{
    public static final short sid = 18;
    private static final BitField protectFlag;
    private int _options;
    
    private ProtectRecord(final int options) {
        this._options = options;
    }
    
    private ProtectRecord(final ProtectRecord other) {
        super(other);
        this._options = other._options;
    }
    
    public ProtectRecord(final boolean isProtected) {
        this(0);
        this.setProtect(isProtected);
    }
    
    public ProtectRecord(final RecordInputStream in) {
        this(in.readShort());
    }
    
    public void setProtect(final boolean protect) {
        this._options = ProtectRecord.protectFlag.setBoolean(this._options, protect);
    }
    
    public boolean getProtect() {
        return ProtectRecord.protectFlag.isSet(this._options);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[PROTECT]\n");
        buffer.append("    .options = ").append(HexDump.shortToHex(this._options)).append("\n");
        buffer.append("[/PROTECT]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this._options);
    }
    
    @Override
    protected int getDataSize() {
        return 2;
    }
    
    @Override
    public short getSid() {
        return 18;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public ProtectRecord clone() {
        return this.copy();
    }
    
    @Override
    public ProtectRecord copy() {
        return new ProtectRecord(this);
    }
    
    static {
        protectFlag = BitFieldFactory.getInstance(1);
    }
}
