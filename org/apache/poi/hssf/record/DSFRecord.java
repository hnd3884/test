package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.BitField;

public final class DSFRecord extends StandardRecord
{
    public static final short sid = 353;
    private static final BitField biff5BookStreamFlag;
    private int _options;
    
    private DSFRecord(final DSFRecord other) {
        super(other);
        this._options = other._options;
    }
    
    private DSFRecord(final int options) {
        this._options = options;
    }
    
    public DSFRecord(final boolean isBiff5BookStreamPresent) {
        this(0);
        this._options = DSFRecord.biff5BookStreamFlag.setBoolean(0, isBiff5BookStreamPresent);
    }
    
    public DSFRecord(final RecordInputStream in) {
        this(in.readShort());
    }
    
    public boolean isBiff5BookStreamPresent() {
        return DSFRecord.biff5BookStreamFlag.isSet(this._options);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[DSF]\n");
        buffer.append("    .options = ").append(HexDump.shortToHex(this._options)).append("\n");
        buffer.append("[/DSF]\n");
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
        return 353;
    }
    
    @Override
    public DSFRecord copy() {
        return new DSFRecord(this);
    }
    
    static {
        biff5BookStreamFlag = BitFieldFactory.getInstance(1);
    }
}
