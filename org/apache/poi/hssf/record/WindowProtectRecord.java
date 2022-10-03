package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.BitField;

public final class WindowProtectRecord extends StandardRecord
{
    public static final short sid = 25;
    private static final BitField settingsProtectedFlag;
    private int _options;
    
    public WindowProtectRecord(final int options) {
        this._options = options;
    }
    
    public WindowProtectRecord(final WindowProtectRecord other) {
        super(other);
        this._options = other._options;
    }
    
    public WindowProtectRecord(final RecordInputStream in) {
        this(in.readUShort());
    }
    
    public WindowProtectRecord(final boolean protect) {
        this(0);
        this.setProtect(protect);
    }
    
    public void setProtect(final boolean protect) {
        this._options = WindowProtectRecord.settingsProtectedFlag.setBoolean(this._options, protect);
    }
    
    public boolean getProtect() {
        return WindowProtectRecord.settingsProtectedFlag.isSet(this._options);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[WINDOWPROTECT]\n");
        buffer.append("    .options = ").append(HexDump.shortToHex(this._options)).append("\n");
        buffer.append("[/WINDOWPROTECT]\n");
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
        return 25;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public WindowProtectRecord clone() {
        return this.copy();
    }
    
    @Override
    public WindowProtectRecord copy() {
        return new WindowProtectRecord(this);
    }
    
    static {
        settingsProtectedFlag = BitFieldFactory.getInstance(1);
    }
}
