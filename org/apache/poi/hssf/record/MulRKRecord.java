package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.HexDump;
import org.apache.poi.hssf.util.RKUtil;

public final class MulRKRecord extends StandardRecord
{
    public static final short sid = 189;
    private final int field_1_row;
    private final short field_2_first_col;
    private final RkRec[] field_3_rks;
    private final short field_4_last_col;
    
    public int getRow() {
        return this.field_1_row;
    }
    
    public short getFirstColumn() {
        return this.field_2_first_col;
    }
    
    public short getLastColumn() {
        return this.field_4_last_col;
    }
    
    public int getNumColumns() {
        return this.field_4_last_col - this.field_2_first_col + 1;
    }
    
    public short getXFAt(final int coffset) {
        return this.field_3_rks[coffset].xf;
    }
    
    public double getRKNumberAt(final int coffset) {
        return RKUtil.decodeNumber(this.field_3_rks[coffset].rk);
    }
    
    public MulRKRecord(final RecordInputStream in) {
        this.field_1_row = in.readUShort();
        this.field_2_first_col = in.readShort();
        this.field_3_rks = RkRec.parseRKs(in);
        this.field_4_last_col = in.readShort();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[MULRK]\n");
        buffer.append("\t.row\t = ").append(HexDump.shortToHex(this.getRow())).append("\n");
        buffer.append("\t.firstcol= ").append(HexDump.shortToHex(this.getFirstColumn())).append("\n");
        buffer.append("\t.lastcol = ").append(HexDump.shortToHex(this.getLastColumn())).append("\n");
        for (int k = 0; k < this.getNumColumns(); ++k) {
            buffer.append("\txf[").append(k).append("] = ").append(HexDump.shortToHex(this.getXFAt(k))).append("\n");
            buffer.append("\trk[").append(k).append("] = ").append(this.getRKNumberAt(k)).append("\n");
        }
        buffer.append("[/MULRK]\n");
        return buffer.toString();
    }
    
    @Override
    public short getSid() {
        return 189;
    }
    
    public void serialize(final LittleEndianOutput out) {
        throw new RecordFormatException("Sorry, you can't serialize MulRK in this release");
    }
    
    @Override
    protected int getDataSize() {
        throw new RecordFormatException("Sorry, you can't serialize MulRK in this release");
    }
    
    @Override
    public MulRKRecord copy() {
        return this;
    }
    
    private static final class RkRec
    {
        public static final int ENCODED_SIZE = 6;
        public final short xf;
        public final int rk;
        
        private RkRec(final RecordInputStream in) {
            this.xf = in.readShort();
            this.rk = in.readInt();
        }
        
        public static RkRec[] parseRKs(final RecordInputStream in) {
            final int nItems = (in.remaining() - 2) / 6;
            final RkRec[] retval = new RkRec[nItems];
            for (int i = 0; i < nItems; ++i) {
                retval[i] = new RkRec(in);
            }
            return retval;
        }
    }
}
