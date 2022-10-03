package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.BitField;

public final class RefreshAllRecord extends StandardRecord
{
    public static final short sid = 439;
    private static final BitField refreshFlag;
    private int _options;
    
    private RefreshAllRecord(final int options) {
        this._options = options;
    }
    
    private RefreshAllRecord(final RefreshAllRecord other) {
        super(other);
        this._options = other._options;
    }
    
    public RefreshAllRecord(final RecordInputStream in) {
        this(in.readUShort());
    }
    
    public RefreshAllRecord(final boolean refreshAll) {
        this(0);
        this.setRefreshAll(refreshAll);
    }
    
    public void setRefreshAll(final boolean refreshAll) {
        this._options = RefreshAllRecord.refreshFlag.setBoolean(this._options, refreshAll);
    }
    
    public boolean getRefreshAll() {
        return RefreshAllRecord.refreshFlag.isSet(this._options);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[REFRESHALL]\n");
        buffer.append("    .options      = ").append(HexDump.shortToHex(this._options)).append("\n");
        buffer.append("[/REFRESHALL]\n");
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
        return 439;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public RefreshAllRecord clone() {
        return this.copy();
    }
    
    @Override
    public RefreshAllRecord copy() {
        return new RefreshAllRecord(this);
    }
    
    static {
        refreshFlag = BitFieldFactory.getInstance(1);
    }
}
