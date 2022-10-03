package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.BitField;

public final class ProtectionRev4Record extends StandardRecord
{
    public static final short sid = 431;
    private static final BitField protectedFlag;
    private int _options;
    
    private ProtectionRev4Record(final int options) {
        this._options = options;
    }
    
    private ProtectionRev4Record(final ProtectionRev4Record other) {
        super(other);
        this._options = other._options;
    }
    
    public ProtectionRev4Record(final boolean protect) {
        this(0);
        this.setProtect(protect);
    }
    
    public ProtectionRev4Record(final RecordInputStream in) {
        this(in.readUShort());
    }
    
    public void setProtect(final boolean protect) {
        this._options = ProtectionRev4Record.protectedFlag.setBoolean(this._options, protect);
    }
    
    public boolean getProtect() {
        return ProtectionRev4Record.protectedFlag.isSet(this._options);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[PROT4REV]\n");
        buffer.append("    .options = ").append(HexDump.shortToHex(this._options)).append("\n");
        buffer.append("[/PROT4REV]\n");
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
        return 431;
    }
    
    @Override
    public ProtectionRev4Record copy() {
        return new ProtectionRev4Record(this);
    }
    
    static {
        protectedFlag = BitFieldFactory.getInstance(1);
    }
}
