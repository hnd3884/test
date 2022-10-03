package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.BitField;

public final class UseSelFSRecord extends StandardRecord
{
    public static final short sid = 352;
    private static final BitField useNaturalLanguageFormulasFlag;
    private int _options;
    
    private UseSelFSRecord(final UseSelFSRecord other) {
        super(other);
        this._options = other._options;
    }
    
    private UseSelFSRecord(final int options) {
        this._options = options;
    }
    
    public UseSelFSRecord(final RecordInputStream in) {
        this(in.readUShort());
    }
    
    public UseSelFSRecord(final boolean b) {
        this(0);
        this._options = UseSelFSRecord.useNaturalLanguageFormulasFlag.setBoolean(this._options, b);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[USESELFS]\n");
        buffer.append("    .options = ").append(HexDump.shortToHex(this._options)).append("\n");
        buffer.append("[/USESELFS]\n");
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
        return 352;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public UseSelFSRecord clone() {
        return this.copy();
    }
    
    @Override
    public UseSelFSRecord copy() {
        return new UseSelFSRecord(this);
    }
    
    static {
        useNaturalLanguageFormulasFlag = BitFieldFactory.getInstance(1);
    }
}
